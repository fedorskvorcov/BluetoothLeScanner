package com.skvortsovfk.bluetoothlescanner.viewmodels

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceListViewModel(context: Context) : ViewModel() {
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private val handler = Handler()
    private val _devices by lazy {
        MutableLiveData<MutableList<BluetoothDevice>>()
    }

    val devices: LiveData<MutableList<BluetoothDevice>>
        get() = _devices

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val scannedDevices = devices.value ?: mutableListOf()
            if (!scannedDevices.contains(result?.device)) {
                result?.device?.let { scannedDevices.add(it) }
                _devices.value = scannedDevices
            }
        }
    }

    fun scanLeDevice(enable: Boolean) {
        when(enable) {
            true -> {
                handler.postDelayed({
                    bluetoothLeScanner?.stopScan(scanCallback)
                }, SCAN_PERIOD)
                bluetoothLeScanner?.startScan(scanCallback)
            }
            else -> {
                bluetoothLeScanner?.stopScan(scanCallback)
            }
        }
    }

    companion object {
        private const val SCAN_PERIOD = 10_000L
    }
}