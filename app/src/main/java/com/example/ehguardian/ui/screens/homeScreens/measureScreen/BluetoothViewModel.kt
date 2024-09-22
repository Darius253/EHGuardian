package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.internal.notify
import java.util.UUID

class BluetoothViewModel : ViewModel() {

    private val _systolic: LiveData<String> = MutableLiveData()
    val systolic: LiveData<String> = _systolic


    private val _diastolic: LiveData<String> = MutableLiveData()
    val diastolic: LiveData<String> = _diastolic


    private val _pulse: LiveData<String> = MutableLiveData()
    val pulse: LiveData<String> = _pulse




     fun startBluetoothDiscovery(
        bluetoothAdapter: BluetoothAdapter?,
        foundDevices: SnapshotStateList<BluetoothDevice>,
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter?.takeIf { it.isEnabled }?.let {
                    if (it.isDiscovering) {
                        it.cancelDiscovery() // Stop any ongoing discovery before starting a new one
                    }
                    foundDevices.clear() // Clear previously found devices
                    it.startDiscovery() // Start discovery for new devices
                    Toast.makeText(context, "Searching for devices...", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(context, "Some Bluetooth Permissions are not granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Bluetooth permissions are required to discover devices",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        else{
            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter?.takeIf { it.isEnabled }?.let {
                    if (it.isDiscovering) {
                        it.cancelDiscovery() // Stop any ongoing discovery before starting a new one
                    }
                    foundDevices.clear() // Clear previously found devices
                    it.startDiscovery() // Start discovery for new devices
                    Toast.makeText(context, "Searching for devices...", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Bluetooth permissions are required to discover devices",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

     fun connectToDevice(
        context: Context,
        device: BluetoothDevice,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Attempting to pair with ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()

                connectToHealthDevice(context, device)// Trigger Bluetooth device pairing


            } else {
                Toast.makeText(context, "Bluetooth connect permission not granted", Toast.LENGTH_SHORT).show()
            }}
        else{
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN)== PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Attempting to pair with ${device.name ?: "Unknown"}", Toast.LENGTH_SHORT).show()

                connectToHealthDevice(context, device)// Trigger Bluetooth device pairing


            } else {
                Toast.makeText(context, "Bluetooth connect permission not granted", Toast.LENGTH_SHORT).show()
            }

        }

    }




    private fun connectToHealthDevice(
        context: Context,
        device: BluetoothDevice,
    ) {
        // Check Bluetooth permissions based on SDK level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Bluetooth connect permission not granted", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Bluetooth GATT callback for handling connection, services, and characteristics
        val gattCallback = object : BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("Bluetooth", "Connected to GATT server.")
                        gatt?.discoverServices()
                   gatt?.notify()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d("Bluetooth", "Disconnected from GATT server.")
                        gatt?.close() // Close connection on disconnect
                    }
                } else {
                    Log.e("Bluetooth", "Connection failed with status: $status")
                    gatt?.close()

                    // Retry connection if status is GATT_CONN_TIMEOUT (status = 14)
                    if (status == 14) {
                        Log.d("Bluetooth", "Retrying connection due to GATT_CONN_TIMEOUT...")
                        val newGatt = gatt?.device?.connectGatt(context, false, this)
                        newGatt?.connect()
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val customService = gatt?.getService(UUID.fromString("0000fe4a-0000-1000-8000-00805f9b34fb"))
                    val customCharacteristic = customService?.getCharacteristic(UUID.fromString("b305b680-aee7-11e1-a730-0002a5d5c51b"))

                    if (customCharacteristic != null) {
                        // Check if notifications are supported for the characteristic
                        if (customCharacteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) {
                            enableNotification(gatt, customCharacteristic)
                        } else {
                            Log.e("Bluetooth", "Characteristic does not support notifications.")
                        }
                    } else {
                        Log.e("Bluetooth", "Characteristic not found!")
                    }
                } else {
                    Log.e("Bluetooth", "Service discovery failed with status: $status")
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                Log.d("Bluetooth", "Characteristic changed: ${characteristic?.uuid}")
                characteristic?.let {
                    val value = it.value
                    val (systolic, diastolic, pulse) = parseBloodPressureData(value) ?: return@let
                    Log.i("Bluetooth", "Blood Pressure: $systolic/$diastolic, Pulse: $pulse")
                }
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("Bluetooth", "Characteristic read successfully.")
                } else {
                    Log.e("Bluetooth", "Failed to read characteristic with status: $status")
                }
            }
        }

        try {
            // Connect to the GATT server on the device
            val gatt = device.connectGatt(context, false, gattCallback)

            // Ensure connection is properly established
            Handler(Looper.getMainLooper()).postDelayed({
                if (gatt?.device?.bondState != BluetoothDevice.BOND_BONDED) {
                    Log.e("Bluetooth", "Connection timed out, closing GATT")
                    gatt?.disconnect()
                    gatt?.close()
                }
            }, 10000) // 10 seconds timeout for bonding

            if (gatt == null) {
                Log.e("Bluetooth", "Failed to connect to GATT server")
                Toast.makeText(context, "Failed to connect to device", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("Bluetooth", "Connected to ${gatt.device.name}")

                // If the device is not bonded, initiate bonding
                if (gatt.device.bondState != BluetoothDevice.BOND_BONDED) {
                    gatt.device.createBond()
                }

                // Start connection process
                gatt.connect()
            }
        } catch (e: Exception) {
            Log.e("Bluetooth", "Error connecting to device: ${e.message}")
            Toast.makeText(context, "Error connecting to device: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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
    @SuppressLint("MissingPermission")
    fun autoConnectToDevice(context: Context) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        // Get paired devices
        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices

        if (pairedDevices.isNotEmpty()) {
            // Connect to the first available paired device
            val device = pairedDevices.first()
            connectToHealthDevice(context, device)
            Toast.makeText(context, "Connecting to ${device.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show()
        }
    }



}