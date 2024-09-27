package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class BluetoothViewModel : ViewModel() {

    private val _systolic = MutableLiveData<Int>()
    val systolic: LiveData<Int> get() = _systolic

    private val _diastolic = MutableLiveData<Int>()
    val diastolic: LiveData<Int> get() = _diastolic

    private val _pulse = MutableLiveData<Int>()
    val pulse: LiveData<Int> get() = _pulse



    private lateinit var bluetoothGatt: BluetoothGatt
    private val bleHandler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingPermission")
    fun startBluetoothDiscovery(context: Context, foundDevices: SnapshotStateList<BluetoothDevice>) {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (adapter == null || !adapter.isEnabled) {
            Toast.makeText(context, "Bluetooth not available or not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        // Check permissions for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Please grant Bluetooth scan permissions", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val scanner = adapter.bluetoothLeScanner ?: run {
            Toast.makeText(context, "Bluetooth scanner not available", Toast.LENGTH_SHORT).show()
            return
        }

        val filters = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(BLOOD_PRESSURE_SERVICE_UUID)).build())
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                if (!foundDevices.contains(device)) {
                    foundDevices.add(device)
                    Log.d(TAG, "Found device: ${device.name ?: "Unknown"} - ${device.address}")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "BLE Scan Failed with code $errorCode")
                Toast.makeText(context, "Scan failed: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }

        scanner.startScan(filters, scanSettings, scanCallback)
        Toast.makeText(context, "Scanning for devices...", Toast.LENGTH_LONG).show()

        // Stop scanning after 2 minutes
        bleHandler.postDelayed({
            scanner.stopScan(scanCallback)
            Toast.makeText(context, "Stopped scanning after 2 minutes", Toast.LENGTH_SHORT).show()
        }, SCAN_PERIOD)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice, context: Context) {
            Toast.makeText(context, "Connecting to device..", Toast.LENGTH_SHORT).show()
            connectToHealthDevice(device, context)

    }

    @SuppressLint("MissingPermission")
    fun unPairDevice(device: BluetoothDevice, context: Context) {
        if (device.bondState == BluetoothDevice.BOND_BONDED) {
            try {
                device::class.java.getMethod("removeBond").invoke(device)
                showToast(context, "Bond has been removed successfully.")
            } catch (e: Exception) {
                showToast(context, "Removing bond has been failed. ${e.message}")
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToHealthDevice(device: BluetoothDevice, context: Context) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    gatt.close()
                    return
                }

                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        // If the device is already bonded, discover services immediately
                        if (device.bondState == BluetoothDevice.BOND_BONDED) {
                            gatt.discoverServices()

                        } else {
                            device.createBond()
                            gatt.discoverServices()

                        }


                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from ${device.name ?: "Unknown"}")
                        gatt.close()
                    }
                    else -> Log.d(TAG, "Connection state changed: $newState")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "Services discovered: ${gatt.services.size} for '${device.name}'")
                    for (service in gatt.services) {
                        Log.i(TAG, "Service UUID: ${service.uuid}")
                    }


                    // Look for the specific service and characteristic
                    gatt.services.forEach { service ->
                        if (service.uuid == BLOOD_PRESSURE_SERVICE_UUID) {
                            service.characteristics.forEach { characteristic ->
                                Log.i(TAG, "Found characteristic: ${characteristic.uuid}")
                                if (characteristic.uuid == BLOOD_PRESSURE_MEASUREMENT_CHAR_UUID) {
                                    enableNotifications(characteristic)
                                }
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Service discovery failed with status $status")

                    gatt.disconnect()
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val value = characteristic.value.clone()
               try{ bleHandler.post {
                    processCharacteristicUpdate(value, characteristic, context)
                }
            }
                catch (e: Exception){
                  showToast(context, "Error Syncing Data, Please try again later ${e.message}")

                }        }


        }

        // Use connectGatt with autoConnect set to false
        bluetoothGatt = device.connectGatt(context, true, gattCallback)
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0 ||
            characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {

            // Enable notifications on the client side
            bluetoothGatt.setCharacteristicNotification(characteristic, true)

            // Write the descriptor to enable notifications or indications
            val descriptor = characteristic.getDescriptor(UUID.fromString(CCCD_DESCRIPTOR_UUID))
            descriptor?.let {
                val enableValue = if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0)
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                else
                    BluetoothGattDescriptor.ENABLE_INDICATION_VALUE

                it.value = enableValue
                bluetoothGatt.writeDescriptor(it)
            } ?: run {
                Log.e(TAG, "Failed to get CCC descriptor for characteristic ${characteristic.uuid}")
            }
        }
    }

    private fun processCharacteristicUpdate(value: ByteArray, characteristic: BluetoothGattCharacteristic, context: Context) {
        if (characteristic.uuid == BLOOD_PRESSURE_MEASUREMENT_CHAR_UUID) {
            val flags = value[0].toInt()
            var index = 1

            // Verify units based on flags (bit 0)
            val units = if (flags and 0x01 == 0) "mmHg" else "kPa"

            // Read systolic, diastolic, and pulse rate values
            val systolic = parseIEEE11073Float(value, index)
            index += 4  // Move index by 4 bytes (3 bytes for mantissa and exponent + 1 reserved)

            val diastolic = parseIEEE11073Float(value, index)
            index += 4  // Move index by 4 bytes (3 bytes for mantissa and exponent + 1 reserved)

            var pulseRate: Float? = null
            if (flags and 0x04 != 0) {  // Check if pulse rate is present
                pulseRate = parseIEEE11073Float(value, index)
                index += 4  // Move index by 4 bytes for pulse rate
            }

            // Update LiveData with correct values
            _systolic.value = systolic?.toInt() ?: 0
            _diastolic.value = diastolic?.toInt() ?: 0
            pulseRate?.let { _pulse.value = it.toInt() }

            // Log the values to ensure they're correct
            Log.d(TAG, "Flags: $flags")
            Log.d(TAG, "Systolic: $systolic $units, Diastolic: $diastolic $units, Pulse Rate: $pulseRate $units")
            Toast.makeText(context, "Systolic: $systolic $units, Diastolic: $diastolic $units, Pulse Rate: $pulseRate $units", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Raw Data: ${value.joinToString(separator = " ") { String.format("%02X", it) }}")
        }
    }

    private fun parseIEEE11073Float(data: ByteArray, offset: Int): Float? {
        if (data.size < offset + 4) return null  // Check if there are enough bytes to parse

        val mantissa = (data[offset].toInt() and 0xFF) or
                ((data[offset + 1].toInt() and 0xFF) shl 8) or
                ((data[offset + 2].toInt() and 0xFF) shl 16)

        val exponent = data[offset + 3].toInt()  // Last byte is the exponent

        return (mantissa * Math.pow(10.0, exponent.toDouble())).toFloat()
    }


    private companion object {
        private const val SCAN_PERIOD: Long = 120000L // 2 minutes
        private const val TAG = "BluetoothViewModel"
        private const val CCCD_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"
        private val BLOOD_PRESSURE_SERVICE_UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")
        private val BLOOD_PRESSURE_MEASUREMENT_CHAR_UUID = UUID.fromString("00002A35-0000-1000-8000-00805f9b34fb")
        private val Heart_RATE_MEASUREMENT_CHAR_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
    }



    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
