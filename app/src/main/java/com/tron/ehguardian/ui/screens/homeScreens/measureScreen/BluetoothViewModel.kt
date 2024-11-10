package com.trontech.ehguardian.ui.screens.homeScreens.measureScreen

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
import android.os.VibrationEffect
import android.os.Vibrator
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

    private var bluetoothGatt: BluetoothGatt? = null
    private val bleHandler = Handler(Looper.getMainLooper())
    private lateinit var scanCallback: ScanCallback

    @SuppressLint("MissingPermission")
    fun startBluetoothDiscovery(context: Context, foundDevices: SnapshotStateList<BluetoothDevice>) {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (adapter == null || !adapter.isEnabled) {
            showToast(context, "Bluetooth not available or not enabled")
            return
        }

        if (!hasPermissions(context)) {
            showToast(context, "Please grant Bluetooth permissions")
            return
        }

        val scanner = adapter.bluetoothLeScanner ?: run {
            showToast(context, "Bluetooth scanner not available")
            return
        }

        val filters = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(BLOOD_PRESSURE_SERVICE_UUID)).build())
        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

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
                showToast(context, "Scan failed: $errorCode")
            }
        }

        scanner.startScan(filters, scanSettings, scanCallback)
        showToast(context, "Scanning for devices...")

        // Stop scanning after 2 minutes
        bleHandler.postDelayed({
            scanner.stopScan(scanCallback)
            showToast(context, "Scanning Stopped")
        }, SCAN_PERIOD)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice, context: Context) {
        showToast(context, "Connecting to device...")
        try {
            connectToHealthDevice(device, context)
        } catch (e: Exception) {
            showToast(context, "Failed to connect to device. ${e.message}")
            Log.e(TAG, "Failed to connect: ${e.message}", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun unPairDevice(device: BluetoothDevice, context: Context) {
        if (device.bondState == BluetoothDevice.BOND_BONDED) {
            try {
                device::class.java.getMethod("removeBond").invoke(device)
                showToast(context, "Bond has been removed successfully.")
            } catch (e: Exception) {
                showToast(context, "Removing bond has been failed. ${e.message}")
                Log.e(TAG, "Failed to unpair: ${e.message}", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScanning(context: Context) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = adapter.bluetoothLeScanner ?: run {
            showToast(context, "Bluetooth scanner not available")
            return
        }

        // Stop scanning if the scan callback is active
        scanner.stopScan(scanCallback)
        showToast(context, "Stopped scanning")
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
                            try {gatt.discoverServices()
                            stopScanning(context)
                            }
                            catch (e: Exception){
                                showToast(context, "Failed to connect device. ${e.message}")
                            }
                        } else {
                            try{
                            device.createBond()
                            gatt.discoverServices()
                            stopScanning(context)}
                            catch (e: Exception){
                                showToast(context, "Failed to connect device. ${e.message}")
                            }
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

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                val value = characteristic.value.clone()
                try {
                    bleHandler.post {
                        parseBloodPressureMeasurement(value, context)
                    }
                } catch (e: Exception) {
                    showToast(context, "Error Syncing Data, Please try again later ${e.message}")
                    Log.e(TAG, "Error parsing blood pressure data: ${e.message}", e)
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val value = characteristic.value.clone()
                try {
                    bleHandler.post {
                        parseBloodPressureMeasurement(value, context)
                    }
                } catch (e: Exception) {
                    showToast(context, "Error Syncing Data, Please try again later ${e.message}")
                    Log.e(TAG, "Error parsing blood pressure data: ${e.message}", e)
                }
            }
        }

        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0 ||
            characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {

            // Enable notifications on the client side
            bluetoothGatt?.setCharacteristicNotification(characteristic, true)

            // Write the descriptor to enable notifications or indications
            val descriptor = characteristic.getDescriptor(UUID.fromString(CCCD_DESCRIPTOR_UUID))
            descriptor?.let {
                val enableValue = if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0)
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                else
                    BluetoothGattDescriptor.ENABLE_INDICATION_VALUE

                it.value = enableValue
                bluetoothGatt?.writeDescriptor(it)
            } ?: run {
                Log.e(TAG, "Failed to get CCC descriptor for characteristic ${characteristic.uuid}")
            }
        }
    }

    fun parseBloodPressureMeasurement(rawData: ByteArray, context: Context) {
        if (rawData.size < 16) { // Ensure there are enough bytes for the pulse data
            Log.e(TAG, "Invalid data length")
            return
        }

        Log.d(TAG, "Raw Data: ${rawData.joinToString(", ")}")

        // The first byte usually contains flags, but we can skip it if not needed
        var index = 1

        // Extract systolic (2 bytes)
        val systolic = (rawData[index].toInt() and 0xFF) or ((rawData[index + 1].toInt() and 0xFF) shl 8)
        index += 2

        // Extract diastolic (2 bytes)
        val diastolic = (rawData[index].toInt() and 0xFF) or ((rawData[index + 1].toInt() and 0xFF) shl 8)
        index += 2

//        // Extract MAP (Mean Arterial Pressure, if needed)
//        val map = (rawData[index].toInt() and 0xFF) or ((rawData[index + 1].toInt() and 0xFF) shl 8)
//        index += 2

        // Move to pulse offset
        index = 14 // Adjust index directly to the pulse position

        // Extract pulse rate (2 bytes)
        val pulseRate = (rawData[index].toInt() and 0xFF) or ((rawData[index + 1].toInt() and 0xFF) shl 8)

        // Update LiveData values
        _systolic.value = systolic
        _diastolic.value = diastolic
        _pulse.value = pulseRate

        // Log the values
        Log.i(TAG, "Systolic: ${_systolic.value} mmHg, Diastolic: ${_diastolic.value} mmHg, Pulse Rate: ${_pulse.value} bpm")
        showToast(context, "Data Synced Successfully")
        vibratePhone(context)
    }


    private fun vibratePhone(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }


    private fun hasPermissions(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private companion object {
        private const val SCAN_PERIOD: Long = 120000L // 2 minutes
        private const val TAG = "BluetoothViewModel"
        private const val CCCD_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"
        private val BLOOD_PRESSURE_SERVICE_UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")
        private val BLOOD_PRESSURE_MEASUREMENT_CHAR_UUID = UUID.fromString("00002A35-0000-1000-8000-00805f9b34fb")
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    @SuppressLint("MissingPermission")
    private fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
