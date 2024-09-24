package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
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

    private val TAG = "BluetoothViewModel"
    private val CCCDESCRIPTORUUID = "00002902-0000-1000-8000-00805f9b34fb"
    private val BloodPressureServiceUUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")

    private val _systolic: MutableLiveData<String> = MutableLiveData()
    val systolic: LiveData<String> get() = _systolic

    private val _diastolic: MutableLiveData<String> = MutableLiveData()
    val diastolic: LiveData<String> get() = _diastolic

    private val _pulse: MutableLiveData<String> = MutableLiveData()
    val pulse: LiveData<String> get() = _pulse

    private val commandQueue: Queue<Runnable> = LinkedList()
    private var commandQueueBusy = false
    private val notifyingCharacteristics = mutableSetOf<UUID>()
    private lateinit var bluetoothGatt: BluetoothGatt

    private val bleHandler = Handler(Looper.getMainLooper()) // Handler for BLE callbacks

    @SuppressLint("MissingPermission")
    fun startBluetoothDiscovery(foundDevices: SnapshotStateList<BluetoothDevice>, context: Context) {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (adapter == null || !adapter.isEnabled) {
            Toast.makeText(context, "Bluetooth not available or not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        val scanner = adapter.bluetoothLeScanner ?: run {
            Toast.makeText(context, "Bluetooth scanner not available", Toast.LENGTH_SHORT).show()
            return
        }

        val filters = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(BloodPressureServiceUUID)).build())
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT)
            .setReportDelay(0L)
            .build()

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device: BluetoothDevice = result.device
                if (!foundDevices.contains(device)) {
                    foundDevices.add(device)
                }
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                results.forEach { result ->
                    val device: BluetoothDevice = result.device
                    if (!foundDevices.contains(device)) {
                        foundDevices.add(device)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "BLE Scan Failed with code $errorCode")
                Toast.makeText(context, "Scan failed: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }

        scanner.startScan(filters, scanSettings, scanCallback)
        Toast.makeText(context, "Scanning for devices...", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            scanner.stopScan(scanCallback)
            Toast.makeText(context, "Stopped scanning after 2 minutes", Toast.LENGTH_SHORT).show()
        }, 120000)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(context: Context, device: BluetoothDevice) {
        Toast.makeText(context, "Connecting to ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()
        connectToHealthDevice(context, device)
    }

    @SuppressLint("MissingPermission")
    private fun connectToHealthDevice(context: Context, device: BluetoothDevice) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(TAG, "Connected to ${device.name}")
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (device.bondState == BluetoothDevice.BOND_NONE) {
                                device.createBond() // Initiate pairing
                            }
                            if (!gatt.discoverServices()) {
                                Log.e(TAG, "Service discovery failed to start")
                            }
                        }, if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) 1000 else 0)
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from ${device.name}")
                        gatt.close()
                    }
                    else -> Log.d(TAG, "Connection state changed: $newState")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "Services discovered: ${gatt.services.size} for '${device.name}'")

                    // Log all services and their characteristics
                    gatt.services.forEach { service ->
                        Log.i(TAG, "Service UUID: ${service.uuid}")
                        service.characteristics.forEach { characteristic ->
                            Log.i(TAG, "Characteristic UUID: ${characteristic.uuid} Properties: ${characteristic.properties}")
                            enableNotifications(characteristic) // Enable notifications and indications
                        }
                    }
                } else {
                    Log.e(TAG, "Service discovery failed with status $status")
                    gatt.disconnect()
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                // Copy the byte array to have a thread-safe copy
                val value = ByteArray(characteristic.value.size)
                System.arraycopy(characteristic.value, 0, value, 0, characteristic.value.size)

                // Characteristic has new value, pass it on for processing
                bleHandler.post {
                    // Call your processor here, update accordingly
                    processCharacteristicUpdate(value, characteristic)
                }
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "Characteristic read: ${characteristic.uuid}")
                    // Handle the read value here
                } else {
                    Log.e(TAG, "Failed to read characteristic: ${characteristic.uuid}, status: $status")
                }
                completedCommand()
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "Failed to write characteristic: ${characteristic.uuid}, status: $status")
                } else {
                    Log.d(TAG, "Successfully wrote to characteristic: ${characteristic.uuid}")
                }
                completedCommand()
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                // Do some checks first
                val parentCharacteristic = descriptor.characteristic
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "Failed to write descriptor: ${descriptor.uuid}, status: $status")
                    return
                }

                // Check if this was the Client Configuration Descriptor
                if (descriptor.uuid == UUID.fromString(CCCDESCRIPTORUUID)) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        // Check if we were turning notify on or off
                        val value = descriptor.value
                        value?.let {
                            if (it[0] != 0.toByte()) {
                                // Notify set to on, add it to the set of notifying characteristics
                                notifyingCharacteristics.add(parentCharacteristic.uuid)
                            } else {
                                // Notify was turned off, so remove it from the set of notifying characteristics
                                notifyingCharacteristics.remove(parentCharacteristic.uuid)
                            }
                        }
                    }
                } else {
                    // This was a normal descriptor write....
                }
                completedCommand()
            }
        }

        // Establish the connection
        bluetoothGatt = device.connectGatt(context, device.bondState == BluetoothDevice.BOND_BONDED, gattCallback)
    }

    private fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
            setNotify(characteristic, true)
        }
        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
            setNotify(characteristic, true)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setNotify(characteristic: BluetoothGattCharacteristic, enable: Boolean) {
        // Check if characteristic is valid
        if (characteristic == null) {
            Log.e(TAG, "ERROR: Characteristic is 'null', ignoring setNotify request")
            return
        }

        // Get the CCC Descriptor for the characteristic
        val descriptor = characteristic.getDescriptor(UUID.fromString(CCCDESCRIPTORUUID))
        if (descriptor == null) {
            Log.e(TAG, "ERROR: Could not get CCC descriptor for characteristic ${characteristic.uuid}")
            return
        }

        // Set the appropriate byte value to enable/disable notification/indication
        val value = if (enable) {
            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        } else {
            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
        }

        descriptor.value = value

        // Write the descriptor
        bluetoothGatt.writeDescriptor(descriptor).also { success ->
            if (!success) {
                Log.e(TAG, "ERROR: writeDescriptor failed for descriptor: ${descriptor.uuid}")
            }
        }
    }

    private fun completedCommand() {
        if (commandQueueBusy) {
            return // Prevent running multiple commands at once
        }
        commandQueueBusy = true

        val command = commandQueue.poll()
        if (command != null) {
            bleHandler.post(command) // Run the command
        } else {
            commandQueueBusy = false // No more commands to run
        }
    }

    private fun processCharacteristicUpdate(value: ByteArray, characteristic: BluetoothGattCharacteristic) {
        // Here you can parse the characteristic value and update LiveData accordingly
        // For demonstration, we'll assume the characteristic value is in a known format

        if (characteristic.uuid == BloodPressureServiceUUID) {
            // Example parsing logic (replace with your actual logic)
            val systolicValue = value[0].toString() // Replace with actual parsing logic
            val diastolicValue = value[1].toString() // Replace with actual parsing logic
            val pulseValue = value[2].toString() // Replace with actual parsing logic

            _systolic.value = systolicValue
            _diastolic.value = diastolicValue
            _pulse.value = pulseValue

            Log.d(TAG, "Systolic: $systolicValue, Diastolic: $diastolicValue, Pulse: $pulseValue")
        }
    }

    private fun bytes2String(bytes: ByteArray): String {
        return bytes.joinToString(" ") { String.format("%02X", it) }
    }
}
