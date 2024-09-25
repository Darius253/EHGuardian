import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.SharedPreferences
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class BluetoothViewModel(private val context: Context) : ViewModel() {

    private val TAG = "BluetoothViewModel"
    private val CCCDESCRIPTORUUID = "00002902-0000-1000-8000-00805f9b34fb"
    private val BloodPressureServiceUUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb")
    private val BloodPressureMeasurementCharUUID = UUID.fromString("00002A35-0000-1000-8000-00805f9b34fb")

    private val _systolic: MutableLiveData<Int> = MutableLiveData()
    val systolic: LiveData<Int> get() = _systolic

    private val _diastolic: MutableLiveData<Int> = MutableLiveData()
    val diastolic: LiveData<Int> get() = _diastolic

    private val _pulse: MutableLiveData<Int> = MutableLiveData()
    val pulse: LiveData<Int> get() = _pulse

    private val _connectionStatus: MutableLiveData<String> = MutableLiveData()
    val connectionStatus: LiveData<String> get() = _connectionStatus

    private val commandQueue: Queue<Runnable> = LinkedList()
    private var commandQueueBusy = false
    private val notifyingCharacteristics = mutableSetOf<UUID>()
    private lateinit var bluetoothGatt: BluetoothGatt

    private val bleHandler = Handler(Looper.getMainLooper())
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("BluetoothPrefs", Context.MODE_PRIVATE)

    init {
        reconnectToStoredDevices()
    }

    @SuppressLint("MissingPermission")
    fun startBluetoothDiscovery(foundDevices: SnapshotStateList<BluetoothDevice>) {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (adapter == null || !adapter.isEnabled) {
            updateConnectionStatus("Bluetooth not available or not enabled")
            return
        }

        val scanner = adapter.bluetoothLeScanner ?: run {
            updateConnectionStatus("Bluetooth scanner not available")
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

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "BLE Scan Failed with code $errorCode")
                updateConnectionStatus("Scan failed: $errorCode")
            }
        }

        scanner.startScan(filters, scanSettings, scanCallback)
        updateConnectionStatus("Scanning for devices...")

        Handler(Looper.getMainLooper()).postDelayed({
            scanner.stopScan(scanCallback)
            updateConnectionStatus("Stopped scanning after 2 minutes")
        }, 120000)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        updateConnectionStatus("Connecting to ${device.name ?: "Unknown"}")
        connectToHealthDevice(device)
    }

    @SuppressLint("MissingPermission")
    private fun connectToHealthDevice(device: BluetoothDevice) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(TAG, "Connected to ${device.name}")
                        updateConnectionStatus("Connected to ${device.name}")
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (device.bondState == BluetoothDevice.BOND_NONE) {
                                device.createBond()
                            } else {
                                if (!gatt.discoverServices()) {
                                    Log.e(TAG, "Service discovery failed to start")
                                    updateConnectionStatus("Service discovery failed to start")
                                }
                            }
                        }, if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) 1000 else 0)
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from ${device.name}")
                        updateConnectionStatus("Disconnected from ${device.name}")
                        gatt.close()
                        // Attempt to reconnect
                        connectToHealthDevice(device)
                    }
                    else -> updateConnectionStatus("Connection state changed: $newState")
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "Services discovered: ${gatt.services.size} for '${device.name}'")
                    updateConnectionStatus("Services discovered for ${device.name}")

                    gatt.services.forEach { service ->
                        if (service.uuid == BloodPressureServiceUUID) {
                            service.characteristics.forEach { characteristic ->
                                if (characteristic.uuid == BloodPressureMeasurementCharUUID) {
                                    enableNotifications(characteristic)
                                }
                            }
                        }
                    }

                    // Store the device as paired
                    storepairedDevice(device)
                } else {
                    Log.e(TAG, "Service discovery failed with status $status")
                    updateConnectionStatus("Service discovery failed")
                    gatt.disconnect()
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val value = characteristic.value.clone()
                bleHandler.post {
                    processCharacteristicUpdate(value, characteristic)
                }
            }

            // Implement other callback methods as needed...
        }

        bluetoothGatt = device.connectGatt(context, true, gattCallback)
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
        val descriptor = characteristic.getDescriptor(UUID.fromString(CCCDESCRIPTORUUID)) ?: run {
            Log.e(TAG, "Could not get CCC descriptor for characteristic ${characteristic.uuid}")
            return
        }

        val value = if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE

        descriptor.value = value

        bluetoothGatt.writeDescriptor(descriptor).also { success ->
            if (!success) {
                Log.e(TAG, "Failed to write descriptor: ${descriptor.uuid}")
            }
        }
    }

    private fun processCharacteristicUpdate(value: ByteArray, characteristic: BluetoothGattCharacteristic) {
        if (characteristic.uuid == BloodPressureMeasurementCharUUID) {
            // Parse blood pressure measurement as per Bluetooth SIG specifications
            val flags = value[0].toInt()
            var index = 1

            // Check if blood pressure is in mmHg or kPa
            val units = if (flags and 0x01 == 0) "mmHg" else "kPa"

            // Parse systolic, diastolic, and mean arterial pressure
            val systolic = IEEE11073Float.readFloatLE(value, index)
            index += 2
            val diastolic = IEEE11073Float.readFloatLE(value, index)
            index += 2
            val meanArterialPressure = IEEE11073Float.readFloatLE(value, index)
            index += 2

            // Parse timestamp if present
            if (flags and 0x02 != 0) {
                // Parse timestamp (year, month, day, hours, minutes, seconds)
                index += 7
            }

            // Parse pulse rate if present
            var pulseRate: Float? = null
            if (flags and 0x04 != 0) {
                pulseRate = IEEE11073Float.readFloatLE(value, index)
                index += 2
            }

            // Update LiveData
            _systolic.value = systolic.toInt()
            _diastolic.value = diastolic.toInt()
            pulseRate?.let { _pulse.value = it.toInt() }

            updateConnectionStatus("Blood Pressure: $systolic/$diastolic $units, Pulse: ${pulseRate ?: "N/A"}")
        }
    }

    private fun storepairedDevice(device: BluetoothDevice) {
        val pairedDevices = getPairedDevices().toMutableSet()
        pairedDevices.add(device.address)
        val json = Gson().toJson(pairedDevices)
        sharedPreferences.edit().putString("paired_devices", json).apply()
    }

    private fun getPairedDevices(): Set<String> {
        val json = sharedPreferences.getString("paired_devices", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<Set<String>>() {}.type)
        } else {
            emptySet()
        }
    }

    private fun reconnectToStoredDevices() {
        val pairedDevices = getPairedDevices()
        val adapter = BluetoothAdapter.getDefaultAdapter()
        pairedDevices.forEach { address ->
            adapter.getRemoteDevice(address)?.let { device ->
                connectToDevice(device)
            }
        }
    }

    private fun updateConnectionStatus(status: String) {
        _connectionStatus.postValue(status)
        Log.d(TAG, status)
    }

    // IEEE 11073 20601 floating point structure to float conversion
    private object IEEE11073Float {
        fun readFloatLE(data: ByteArray, offset: Int): Float {
            val mantissa = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8) or
                    ((data[offset + 2].toInt() and 0xFF) shl 16)
            val exponent = data[offset + 3].toInt() and 0xFF
            return (mantissa * Math.pow(10.0, exponent.toDouble() - 3)).toFloat()
        }
    }
}