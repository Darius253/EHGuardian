@file:Suppress("DEPRECATION")

package com.trontech.ehguardian

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.trontech.ehguardian.ui.screens.MyApp
import com.trontech.ehguardian.ui.screens.homeScreens.HomeScreen
import com.trontech.ehguardian.ui.theme.EHGuardianTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.trontech.ehguardian.data.repositories.workManagers.PostNotificationRepository

@Suppress("OVERRIDE_DEPRECATION")
class MainActivity : ComponentActivity() {

    private lateinit var enableBtLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var discoverableLauncher: ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    private val requestCode = 1
    private val discoverableIntent: Intent =
        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1000)
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)

        setContent {
            EHGuardianTheme {
                MyApp()
            }
        }

        // Initialize the Activity Result Launchers
        enableBtLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    showToast("Bluetooth enabled")
                } else {
                    showToast("Bluetooth not enabled")
                }
            }

        requestPermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allPermissionsGranted = permissions.values.all { it }
                if (allPermissionsGranted) {
                    checkBluetooth()
                } else {
                    showToast("Bluetooth is required to use Bluetooth features")
                    startActivityForResult(discoverableIntent, requestCode)
                    checkBluetooth()
                }
            }
        discoverableLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    showToast("Device is discoverable")
                } else {
                    showToast("Device is not discoverable")
                }
            }

        checkPermissionsAndBluetooth()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission()
            } else {
                // Permission already granted, proceed with showing notifications
                triggerNotification()
            }
        } else {

            triggerNotification()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. You can now show notifications
                triggerNotification()
            }
//            else {
//                WorkManager.getInstance(this).cancelUniqueWork("TwiceDailyNotificationWork")
//            }
        }
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun triggerNotification() {
        val postNotificationRepository = PostNotificationRepository(this)
       postNotificationRepository.scheduleDailyNotification()
    }


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            setContent {
                EHGuardianTheme {
                    HomeScreen()
                }
            }
        } else {
            setContent {
                EHGuardianTheme {
                    MyApp()
                }
            }
        }


    }






    private fun checkPermissionsAndBluetooth() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_ADVERTISE,
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



        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            checkBluetooth()
        }
    }

    private fun checkBluetooth() {
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not supported on this device")
        } else if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBtLauncher.launch(enableBtIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_BT_CONNECT) {
            if (grantResults.isEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                checkBluetooth()
            } else {
                showToast("Bluetooth permission is required to enable Bluetooth")
            }
        }
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_PERMISSION_BT_CONNECT = 1
    }
}
