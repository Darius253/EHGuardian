@file:Suppress("DEPRECATION")

package com.example.ehguardian.ui.screens.homeScreens.measureScreen


import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

// Create a formatter to extract Year, Day of Year, and Time
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

// Format the createdDate to the required format
    val formattedDate = createdDate.format(formatter)


    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!bluetoothEnabled) {
            ViewBluetoothDevicesButton(
                onClick = { setBluetoothEnabled(true) }
            )
        } else {
            BluetoothAnimation(composition = composition, progress = progress)
            ViewBluetoothDevicesSheet(onDismiss = { setBluetoothEnabled(false) }, sheetState = sheetState)
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
                        context = context, // Pass the current context
                        measurementData = MeasurementData(
                            systolic = systolic,
                            diastolic = diastolic,
                            pulse = heartRate,
                            timestamp = formattedDate,
                            bmi =   String.format("%.2f", bmi),
                        )
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("InlinedApi")
@Composable
fun ViewBluetoothDevicesSheet(onDismiss: () -> Unit, sheetState: SheetState) {
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }


    LaunchedEffect(Unit) {
        val hasPermissionScan = ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        val hasPermissionBluetooth = ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermissionScan || hasPermissionBluetooth) {
            bluetoothAdapter?.startDiscovery()
            Toast.makeText(context, "Searching for devices", Toast.LENGTH_SHORT).show()
        } else {

            Toast.makeText(context, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

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

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
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
                        BluetoothDeviceItem(device = device) {
                            connectToDevice(context, device)
                        }
                    }
                }
            } else {
                Text("No devices found.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun BluetoothDeviceItem(device: BluetoothDevice, onConnect: (BluetoothDevice) -> Unit) {
    val context = LocalContext.current
    var deviceName by remember { mutableStateOf("Unnamed Device") }

    // Check if BLUETOOTH_CONNECT permission is granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
        == PackageManager.PERMISSION_GRANTED ||
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
            Column {
                Text(
                    text = deviceName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = device.address, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { onConnect(device) }) {
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

private fun connectToDevice(context: Context, device: BluetoothDevice) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
        == PackageManager.PERMISSION_GRANTED
    ) {
        device.createBond() // Trigger Bluetooth device pairing
        Toast.makeText(context, "Attempting to pair with ${device.name}", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Bluetooth connect permission not granted", Toast.LENGTH_SHORT).show()
    }
}
