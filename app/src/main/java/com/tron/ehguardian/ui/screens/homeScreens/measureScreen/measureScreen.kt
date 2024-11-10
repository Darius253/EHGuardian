@file:Suppress("DEPRECATION")

package com.trontech.ehguardian.ui.screens.homeScreens.measureScreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.trontech.ehguardian.R
import com.trontech.ehguardian.data.models.MeasurementData
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.authenticationScreens.ToggleScreenButton
import com.trontech.ehguardian.ui.screens.homeScreens.HomeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothViewModel: BluetoothViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }
    val (systolic, setSystolic) = rememberSaveable { mutableStateOf("") }
    val (diastolic, setDiastolic) = rememberSaveable { mutableStateOf("") }
    val (heartRate, setHeartRate) = rememberSaveable { mutableStateOf("") }

// Observe the LiveData and update the state
    val systolicValue by bluetoothViewModel.systolic.observeAsState()
    val diastolicValue by bluetoothViewModel.diastolic.observeAsState()
    val heartRateValue by bluetoothViewModel.pulse.observeAsState()
    val (bluetoothEnabled, setBluetoothEnabled) = rememberSaveable { mutableStateOf(false) }// New state variable

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



    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH
        )
    }

    val hasPermissions = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // Update state whenever LiveData values change
    LaunchedEffect(systolicValue) {
        systolicValue?.let { setSystolic(it.toString()) }
    }
    LaunchedEffect(diastolicValue) {
        diastolicValue?.let { setDiastolic(it.toString()) }
    }
    LaunchedEffect(heartRateValue) {
        heartRateValue?.let { setHeartRate(it.toString()) }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if (!bluetoothEnabled) {

            if (!hasPermissions) {
                ActivityCompat.requestPermissions(context as Activity, permissions, 1)
                Toast.makeText(context, "Bluetooth Permissions not granted", Toast.LENGTH_SHORT).show()
            } else {
                ViewBluetoothDevicesButton(
                    onClick = {
                        setBluetoothEnabled(true)
                    }
                )

            }

        }
        else {
            BluetoothAnimation(composition = composition, progress = progress)
            ViewBluetoothDevicesModalSheet(
                onDismiss = { setBluetoothEnabled(false) },
                sheetState = sheetState,
            )
        }

        AddDetailsFab { setShowDialog(true) }

        if (showDialog || systolic!="" || diastolic!=""  || heartRate!="" ) {
            OverlayBackground()
            ManuallyAddDetails(
                onDismiss = {
                    setShowDialog(false)
                    setSystolic("")
                    setDiastolic("")
                    setHeartRate("") },
                systolic = systolic,
                diastolic = diastolic,
                heartRate = heartRate,
                onSystolicChange = { if (it.length <= 3) setSystolic(it) else setSystolic(it.substring(0, 3)) },
                onDiastolicChange = { if (it.length <= 3) setDiastolic (it)else setSystolic(it.substring(0, 3))  },
                onHeartRateChange = { if (it.length <= 3) setHeartRate(it) else setSystolic(it.substring(0, 3)) },
                onDone = { focusManager.clearFocus() },
                onUpload = {
                    homeViewModel.uploadUserMeasurement(
                        context = context,
                        measurementData = MeasurementData(
                            systolic = systolic,
                            diastolic = diastolic,
                            pulse = heartRate,
                            timestamp = formattedDate,
                            bmi = String.format("%.2f", bmi)
                        ),
                        onSuccess = {
                            setShowDialog(false)
                            homeViewModel.fetchUserDetails()
                            homeViewModel.fetchUserMeasurements()
                            setSystolic("")
                            setDiastolic("")
                            setHeartRate("")
                        }
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
fun ViewBluetoothDevicesModalSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    bluetoothViewModel: BluetoothViewModel = viewModel(factory = AppViewModelProvider.Factory)

) {
    var isFirstPage by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }

    // Check and request Bluetooth permissions
    LaunchedEffect(Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.INTERNET
            )
        } else {
            arrayOf(
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
            bluetoothViewModel.startBluetoothDiscovery(context, foundDevices)
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
        }
    }

    // UI for displaying found devices in a modal sheet
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        dragHandle = {
            ToggleScreenButton(
                isFirstPage = isFirstPage,
                onButtonClick = { isFirstPage = !isFirstPage },
                firstText = "Available Devices",
                secondText = "Paired Devices",
                color = Color.White
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
        ) {



            if (foundDevices.isNotEmpty()) {
                for (device in foundDevices) {
                if (isFirstPage) {
                        if (device.bondState == BluetoothDevice.BOND_NONE) {
                            LazyColumn {
                                items(foundDevices) { device ->
                                    BluetoothDeviceItem(device = device, onConnect = {
                                        bluetoothViewModel.connectToDevice(
                                            device,
                                            context
                                        )

                                    },
                                        onDismiss = { onDismiss() }
                                    )
                                }
                            }

                        }
                    else{
                            Text(
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                ),
                                text = "No Bluetooth Devices Found")
                        }


                    }
                    else{
                        if (device.bondState == BluetoothDevice.BOND_BONDED) {
                            LazyColumn {
                                items(foundDevices) { device ->
                                    BluetoothDeviceItem(device = device, onConnect = {
                                        bluetoothViewModel.connectToDevice(
                                            device,
                                            context,

                                        )

                                    },
                                        onDismiss = { onDismiss() },
                                        pairedDevices = true,
                                        onUnpair = {
                                            bluetoothViewModel.unPairDevice(device, context)
                                        }
                                    )
                                }
                            }

                        }
                    else{
                        Text(
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            ),
                            text = "No Paired Devices Found")

                        }
                }



                }

            }
            else{
                Text(text = "No Devices Found")
            }

        }






    }
}



@Composable
fun BluetoothDeviceItem(device: BluetoothDevice,
                        onConnect: (BluetoothDevice) -> Unit, onDismiss: () -> Unit,
                        pairedDevices: Boolean = false,
                        onUnpair: () -> Unit = {}) {
    val context = LocalContext.current
    var deviceName by remember { mutableStateOf("Unnamed Device") }

    // Check if BLUETOOTH_CONNECT permission is granted
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        deviceName = device.name ?: "Unnamed Device"
    }

    if (pairedDevices)Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = deviceName,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

         Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        )
        {
            Button(
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    onConnect(device)
                    onDismiss()
                }
            ) {
                Icon(imageVector = Icons.Filled.Sync, contentDescription = "Sync Icon",
                    )

                Text("Sync Data",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold)
            }
            Button(
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                onClick = {
                    onUnpair()
                    onDismiss()
                }
            ) {
                Icon(imageVector = Icons.Filled.BluetoothDisabled, contentDescription = "Unpair Icon")

                Text("Unpair",
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Gray)
    }
    else{
       Column(
           modifier = Modifier
               .fillMaxWidth()
       ){
           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically
           ) {
               Text(
                   modifier = Modifier.weight(1f),
                   text = deviceName,
                   fontWeight = FontWeight.Bold,
                   style = MaterialTheme.typography.titleMedium,
                   maxLines = 1,
                   overflow = TextOverflow.Ellipsis
               )
               Button(

                   shape = RoundedCornerShape(4.dp),
                   onClick = {
                       onConnect(device)
                       onDismiss()
                   }
               ) {
                   Icon(imageVector = Icons.Filled.BluetoothConnected, contentDescription = "Bluetooth Icon",
                   )

                   Text("Connect",
                       style = MaterialTheme.typography.bodyMedium,
                       fontWeight = FontWeight.Bold)
               }

           }

           Spacer(modifier = Modifier.height(8.dp))
           Divider(color = Color.Gray)
       }

    }

    }


@Composable
fun ViewBluetoothDevicesButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "View Available Bluetooth Devices")
    }
}








