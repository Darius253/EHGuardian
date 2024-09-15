@file:Suppress("DEPRECATION")

package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.example.ehguardian.R
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.ui.AppViewModelProvider
import com.example.ehguardian.ui.screens.homeScreens.HomeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }
    val (systolic, setSystolic) = rememberSaveable { mutableStateOf("") }
    val (diastolic, setDiastolic) = rememberSaveable { mutableStateOf("") }
    val (heartRate, setHeartRate) = rememberSaveable { mutableStateOf("") }
    val (bluetoothEnabled, setBluetoothEnabled) = rememberSaveable { mutableStateOf(false) }
    val (isConnected, setIsConnected) = rememberSaveable { mutableStateOf(false) } // New state variable

    val focusManager: FocusManager = LocalFocusManager.current
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bluetooth))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val weight = (homeViewModel.userDetails.collectAsState().value?.userWeight ?: "").toDoubleOrNull() ?: 0.0
    val height = (homeViewModel.userDetails.collectAsState().value?.userHeight ?: "").toDoubleOrNull() ?: 0.0

    val bmi = if (height > 0) weight / (height * height) else 0.0

    val sheetState = rememberModalBottomSheetState()
    val createdDate = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    val formattedDate = createdDate.format(formatter)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!bluetoothEnabled) {
            ViewBluetoothDevicesButton(onClick = { setBluetoothEnabled(true) })
        }
        else {
            BluetoothAnimation(composition = composition, progress = progress)
            ViewBluetoothDevicesSheet(
                onDismiss = { setBluetoothEnabled(false) },
                sheetState = sheetState,
            )
        }

        AddDetailsFab { setShowDialog(true) }

        if (showDialog) {
            OverlayBackground()
            ManuallyAddDetails(
                onDismiss = { setShowDialog(false) },
                systolic = systolic,
                diastolic = diastolic,
                heartRate = heartRate,
                onSystolicChange = setSystolic,
                onDiastolicChange = setDiastolic,
                onHeartRateChange = setHeartRate,
                onDone = { focusManager.clearFocus() },
                onUpload = {
                    setShowDialog(false)
                    homeViewModel.uploadUserMeasurement(
                        context = context,
                        measurementData = MeasurementData(
                            systolic = systolic,
                            diastolic = diastolic,
                            pulse = heartRate,
                            timestamp = formattedDate,
                            bmi = String.format("%.2f", bmi)
                        )
                    )
                }
            )
        }

        // Display connection status

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("InlinedApi")
@Composable
fun ViewBluetoothDevicesSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,

) {
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }

    // Check and request Bluetooth permissions
    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH
            )
        }

        val hasPermissions = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            ActivityCompat.requestPermissions(context as Activity, permissions, 1)
        } else {
            startBluetoothDiscovery(bluetoothAdapter, foundDevices, context)
        }
    }

    // Unregister the receiver when done
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (device != null && !foundDevices.contains(device)) {
                            foundDevices.add(device)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
            bluetoothAdapter?.cancelDiscovery()
        }
    }

    // UI for displaying found devices in a modal sheet
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
        ) {
            Text(
                text = "Available Bluetooth Devices",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (foundDevices.isNotEmpty()) {
                LazyColumn {
                    items(foundDevices) { device ->
                        BluetoothDeviceItem(device = device, onConnect = {
                            connectToDevice(
                                context,
                                device,
                            )
                        },
                            onDismiss = { onDismiss() }
                        )
                    }
                }



            } else {
                Text("No devices found.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}


private fun startBluetoothDiscovery(
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

private fun connectToDevice(
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

    val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d("Bluetooth", "Connected to GATT server.")
                        gatt?.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d("Bluetooth", "Disconnected from GATT server.")
                    }
                }
            } else {
                Log.e("Bluetooth", "Connection failed with status: $status")
                gatt?.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val customService = gatt?.getService(UUID.fromString("0000fe4a-0000-1000-8000-00805f9b34fb"))
                val customCharacteristic = customService?.getCharacteristic(UUID.fromString("b305b680-aee7-11e1-a730-0002a5d5c51b"))

                if (customCharacteristic != null) {
                    enableNotification(gatt, customCharacteristic)
                } else {
                    Log.e("Bluetooth", "Characteristic not found!")
                }
            } else {
                Log.e("Bluetooth", "Service discovery failed with status: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
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
        val gatt = device.connectGatt(context, false, gattCallback)
        if (gatt == null) {
            Log.e("Bluetooth", "Failed to connect to GATT server")
            Toast.makeText(context, "Failed to connect to device", Toast.LENGTH_SHORT).show()
        } else {
            if (device.bondState == BluetoothDevice.BOND_NONE) {
                gatt.device.createBond()
            } else {
                gatt.connect()
            }
            Log.d("Bluetooth", "Connection Status = ${gatt.device.bondState}")
        }
    } catch (e: Exception) {
        Log.e("Bluetooth", "Error connecting to device: ${e.message}")
        Toast.makeText(context, "Error connecting to device: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// Function to enable notifications
@SuppressLint("MissingPermission")
private fun enableNotification(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
    gatt?.setCharacteristicNotification(characteristic, true)
    val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
    descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
    gatt?.writeDescriptor(descriptor)
    Log.d("Bluetooth", "Notification enabled for characteristic ${characteristic.uuid}")
}

// Function to parse blood pressure data
fun parseBloodPressureData(value: ByteArray): Triple<Int, Int, Int>? {
    if (value.size < 7) return null

    val flags = value[0].toInt() and 0xFF
    val systolic = ((value[1].toInt() and 0xFF) or ((value[2].toInt() and 0xFF) shl 8)) * 0.1f
    val diastolic = ((value[3].toInt() and 0xFF) or ((value[4].toInt() and 0xFF) shl 8)) * 0.1f
    val pulseRate = ((value[5].toInt() and 0xFF) or ((value[6].toInt() and 0xFF) shl 8))

    return Triple(systolic.toInt(), diastolic.toInt(), pulseRate)
}





@Composable
fun BluetoothDeviceItem(device: BluetoothDevice, onConnect: (BluetoothDevice) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var deviceName by remember { mutableStateOf("Unnamed Device") }

    // Check if BLUETOOTH_CONNECT permission is granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
    ) {
        deviceName = device.name ?: "Unnamed Device"
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            )
            {
                Text(
                    text = deviceName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = device.address, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = {
                onConnect(device)
                onDismiss()
            }
            ) {
                Text("Connect")
            }
        }
        Divider(color = Color.Gray)
    }
}

@Composable
fun ViewBluetoothDevicesButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "View Available Bluetooth Devices")
    }
}





