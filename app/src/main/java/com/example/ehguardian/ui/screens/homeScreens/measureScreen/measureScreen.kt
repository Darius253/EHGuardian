package com.example.ehguardian.ui.screens.homeScreens.measureScreen

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.example.ehguardian.R
import com.example.ehguardian.ui.screens.homeScreens.profile.InputField
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureScreen(modifier: Modifier = Modifier) {

    val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }
    val (systolic, setSystolic) = rememberSaveable { mutableStateOf("") }
    val (diastolic, setDiastolic) = rememberSaveable { mutableStateOf("") }
    val (heartRate, setHeartRate) = rememberSaveable { mutableStateOf("") }
    var (bluetoothEnabled, setBluetoothEnabled) = rememberSaveable { mutableStateOf(false) }




    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bluetooth))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!bluetoothEnabled ) {
            ViewBluetoothDevices(
                onClick = {

                    setBluetoothEnabled(true)

            }
            )
        } else {
            BluetoothAnimation(composition = composition, progress = progress)
//            coroutineScope.launch { sheetState.show() }
            ViewBluetoothDevices(onDismiss = { setBluetoothEnabled(false) }, sheetState = sheetState)
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
                onHeartRateChange = setHeartRate
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBluetoothDevices(onDismiss: () -> Unit, sheetState: SheetState) {
    val context = LocalContext.current
    val bluetoothManager = getSystemService(context, BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            bluetoothAdapter?.startDiscovery()
        } else {
            Toast.makeText(context, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
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
        )
        {
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
                        BluetoothDeviceItem(device = device)
                    }
                }
            } else {
                Text("No devices found.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}






@Composable
fun BluetoothDeviceItem(device: BluetoothDevice) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request
            return
        }
        Text(
            text = device.name ?: "Unnamed Device",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = device.address,
            style = MaterialTheme.typography.bodySmall
        )
        HorizontalDivider(color = Color.Gray)
    }
}

@Composable
fun ViewBluetoothDevices(onClick:  () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "View Available Bluetooth Devices")
    }
}

@Composable
fun BluetoothAnimation(composition: LottieComposition?, progress: Float) {
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun AddDetailsFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(top = 500.dp, start = 300.dp)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Details Manually")
    }
}

@Composable
fun OverlayBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    )
}

@Composable
fun ManuallyAddDetails(
    onDismiss: () -> Unit,
    systolic: String,
    diastolic: String,
    heartRate: String,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onHeartRateChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            DialogHeader(onDismiss = onDismiss)
            Spacer(modifier = Modifier.height(16.dp))
            ExpandableSection(
                title = "Blood Pressure",
                iconResId = R.drawable.arm
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                BloodPressureInputs(
                    systolic = systolic,
                    diastolic = diastolic,
                    onSystolicChange = onSystolicChange,
                    onDiastolicChange = onDiastolicChange
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            ExpandableSection(
                title = "Heart Rate",
                iconResId = R.drawable.heart_rate_monitor
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                HeartRateInput(
                    heartRate = heartRate,
                    onHeartRateChange = onHeartRateChange
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            UploadButton(
                onUpload = { onDismiss() },
                systolic = systolic,
                diastolic = diastolic,
                heartRate = heartRate
            )
        }
    }
}

@Composable
fun DialogHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Manually Input Data",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    iconResId: Int,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = iconResId),
                contentDescription = "$title Image"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        content()
    }
}

@Composable
fun BloodPressureInputs(
    systolic: String,
    diastolic: String,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit
) {
    Row {
        InputField(
            label = "Systolic",
            value = systolic,
            onValueChange = onSystolicChange,
            suffix = "mmHg",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.width(10.dp))
        InputField(
            label = "Diastolic",
            value = diastolic,
            onValueChange = onDiastolicChange,
            suffix = "mmHg",
            modifier = Modifier.weight(1f),
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
fun HeartRateInput(
    heartRate: String,
    onHeartRateChange: (String) -> Unit
) {
    InputField(
        label = "Pulse",
        value = heartRate,
        onValueChange = onHeartRateChange,
        suffix = "bpm",
        modifier = Modifier.fillMaxWidth(),
        keyboardType = KeyboardType.Number
    )
}

@Composable
fun UploadButton(
    onUpload: () -> Unit,
    systolic: String,
    diastolic: String,
    heartRate: String
) {
    val context = LocalContext.current

    TextButton(
        modifier = Modifier.padding(start = 200.dp),
        onClick = {
            onUpload()
            Toast.makeText(context, "$systolic $diastolic $heartRate", Toast.LENGTH_SHORT).show()
        }
    ) {
        Text(
            text = "Upload",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}