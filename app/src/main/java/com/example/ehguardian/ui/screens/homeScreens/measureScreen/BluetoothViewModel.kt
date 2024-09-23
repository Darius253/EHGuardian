package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.BOND_BONDED
import android.bluetooth.BluetoothDevice.TRANSPORT_LE
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGatt.GATT_FAILURE
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale
import java.util.UUID


class BluetoothViewModel : ViewModel() {

    private val _systolic: LiveData<String> = MutableLiveData()
    val systolic: LiveData<String> = _systolic


    private val _diastolic: LiveData<String> = MutableLiveData()
    val diastolic: LiveData<String> = _diastolic


    private val _pulse: LiveData<String> = MutableLiveData()
    val pulse: LiveData<String> = _pulse




     @SuppressLint("MissingPermission")
     fun startBluetoothDiscovery(
         foundDevices: SnapshotStateList<BluetoothDevice>,
         context: Context
     ) {
         val bleServiceUUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")
         val serviceUUIDs = arrayOf(bleServiceUUID)
         val adapter = BluetoothAdapter.getDefaultAdapter()

         if (adapter == null || !adapter.isEnabled) {
             Toast.makeText(context, "Bluetooth not available or not enabled", Toast.LENGTH_SHORT).show()
             return
         }

         val scanner = adapter.bluetoothLeScanner
         if (scanner == null) {
             Toast.makeText(context, "Bluetooth scanner not available", Toast.LENGTH_SHORT).show()
             return
         }

         val filters = serviceUUIDs.map { serviceUUID ->
             ScanFilter.Builder()
                 .setServiceUuid(ParcelUuid(serviceUUID))
                 .build()
         }

         val scanSettings = ScanSettings.Builder()
             .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
             .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
             .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
             .setNumOfMatches(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT)
             .setReportDelay(0L)
             .build()

         // Create the ScanCallback as an inner object to access foundDevices
         val scanCallback: ScanCallback = object : ScanCallback() {
             override fun onScanResult(callbackType: Int, result: ScanResult) {
                 val device: BluetoothDevice = result.device
                 if (!foundDevices.contains(device))
                 foundDevices.add(device)

             }

             override fun onBatchScanResults(results: List<ScanResult?>?) {
                 results?.forEach { result ->
                     val device: BluetoothDevice = result?.device ?: return
                     if (!foundDevices.contains(device))
                     foundDevices.add(device)

                 }
             }

             override fun onScanFailed(errorCode: Int) {
                 Log.e("Bluetooth", "BLE Scan Failed with code $errorCode")
             }
         }

         // Start scanning
         scanner.startScan(filters, scanSettings, scanCallback)

         // Provide user feedback
         Toast.makeText(context, "Scanning for devices...", Toast.LENGTH_SHORT).show()
         val handler = Handler(Looper.getMainLooper())
         handler.postDelayed({
             scanner.stopScan(scanCallback)
             Toast.makeText(context, "Stopped scanning after 2 minutes", Toast.LENGTH_SHORT).show()
         }, 120000)
     }






    fun connectToDevice(
        context: Context,
        device: BluetoothDevice,
    ) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN)== PackageManager.PERMISSION_GRANTED ) {
                Toast.makeText(context, "Connecting ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()

                connectToHealthDevice(context, device)// Trigger Bluetooth device pairing


            } else {
                Toast.makeText(context, "Bluetooth connect permission not granted", Toast.LENGTH_SHORT).show()
            }}














    private fun connectToHealthDevice(
        context: Context,
        device: BluetoothDevice,
    ) {
        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
        { Toast.makeText(context, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()}



        val gattCallback = object : BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        when (newState) {
                            BluetoothProfile.STATE_CONNECTED -> {
                                val bondState = device.bondState
                                var delayWhenBonded = 0
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                                    delayWhenBonded = 1000
                                }


                                val delay = if (bondState == BOND_BONDED) delayWhenBonded else 0

                                // Use Handler to delay bonding if needed and then discover services
                                Handler(Looper.getMainLooper()).postDelayed({
                                    // Create bond if necessary
                                    if (bondState == BluetoothDevice.BOND_NONE) {
                                        device.createBond()
                                    }

                                    // Proceed with service discovery
                                    Log.d("BluetoothGatttt", "Discovering services of ${device.name} with delay of $delay ms")
                                    val result = gatt.discoverServices()
                                    if (!result) {
                                        Log.e("BluetoothGatttt", "Service discovery failed to start")
                                    }
                                }, delay.toLong())
                            }

                            BluetoothProfile.STATE_DISCONNECTED -> {
                                // Successfully disconnected, close the GATT connection
                                Log.d(TAG, "Disconnected from ${device.name}")
                                gatt.close()
                            }

                            else -> {
                                // CONNECTING or DISCONNECTING states - ignore for now
                            }
                        }
                    }

                }

                else {
                    gatt.close()
                }
            }

            @SuppressLint("MissingPermission")
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                if (status == GATT_FAILURE) {
                    Log.e(TAG, "Service discovery failed");
                    gatt?.disconnect()
                    return;
                }
                if (status == GATT_SUCCESS) {
                    val services = gatt!!.services
                    Log.i(
                        "Discovered Services",
                        java.lang.String.format(
                            Locale.ENGLISH,
                            "discovered %d services for '%s'",
                            services.size,
                            device.name
                        )
                    )
                }

            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                super.onCharacteristicChanged(gatt, characteristic)




            }
            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                super.onCharacteristicRead(gatt, characteristic, status)

                if (VDBG) {
                    Log.d(TAG, "onCharacteristicRead() - Device=" + address
                            + " handle=" + handle + " Status=" + status);
                }

                if (!address.equals(mDevice.getAddress())) {
                    return;
                }

                synchronized (mDeviceBusy) {
                    mDeviceBusy = false;
                }
                }


            }


        }

        if(device.bondState == BOND_BONDED){
            val gatt = device.connectGatt(context, true, gattCallback, TRANSPORT_LE)
        }


        val gatt = device.connectGatt(context, false, gattCallback, TRANSPORT_LE)














    }




    // Enable notifications for the given characteristic
    @SuppressLint("MissingPermission")
    private fun enableNotification(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
        gatt?.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))

        if (descriptor != null) {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            val success = gatt?.writeDescriptor(descriptor)

            if (success == true) {
                Log.d("Bluetooth", "FUUUCCKKK: $descriptor")
                Log.d("Bluetooth", "Notification enabled successfully.")

            } else {
                Log.e("Bluetooth", "Failed to enable notifications.")
            }
        } else {
            Log.e("Bluetooth", "Descriptor not found for characteristic.")
        }
    }

    // Parse the blood pressure data from the byte array
    fun parseBloodPressureData(value: ByteArray): Triple<Int, Int, Int>? {
        if (value.size < 7) return null

        val systolic = ((value[1].toInt() and 0xFF) or ((value[2].toInt() and 0xFF) shl 8)) * 0.1f
        val diastolic = ((value[3].toInt() and 0xFF) or ((value[4].toInt() and 0xFF) shl 8)) * 0.1f
        val pulseRate = ((value[5].toInt() and 0xFF) or ((value[6].toInt() and 0xFF) shl 8))

        return Triple(systolic.toInt(), diastolic.toInt(), pulseRate)
    }


    // Auto connect to paired devices
//    @SuppressLint("MissingPermission")
//    fun autoConnectToDevice(context: Context) {
//        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//
//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
//            Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Get paired devices
//        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
//
//        if (pairedDevices.isNotEmpty()) {
//            // Connect to the first available paired device
//            val device = pairedDevices.first()
//            connectToHealthDevice(context, device)
//            Toast.makeText(context, "Connecting to ${device.name}", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show()
//        }
//    }




}