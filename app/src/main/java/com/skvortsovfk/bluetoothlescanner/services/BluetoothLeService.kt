package com.skvortsovfk.bluetoothlescanner.services

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class BluetoothLeService : Service() {

    private var connectionState = STATE_DISCONNECTED
    private var binder = LocalBinder()

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothDeviceAddress: String? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(intentAction)
                    Log.i(TAG, "Connected to GATT server.")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    connectionState = STATE_DISCONNECTED

                    Log.i(TAG, "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }
    }

    private fun broadcastUpdate(intentAction: String) {
        val intent = Intent(intentAction)
        sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }

    override fun onBind(intent: Intent?) = binder

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    fun initialize(): Boolean {
        if (bluetoothManager == null) {
            bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.")
                return false
            }
        }

        bluetoothAdapter = bluetoothManager!!.adapter
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }

    fun connect(address: String): Boolean {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        if (bluetoothDeviceAddress != null
            && address == bluetoothDeviceAddress
            && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            return if (bluetoothGatt!!.connect()) {
                connectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }

        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        bluetoothDeviceAddress = address
        connectionState = STATE_CONNECTING
        return true
    }

    fun disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bluetoothGatt?.disconnect()
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    companion object {
        private val TAG = BluetoothLeService::class.java.simpleName

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2

        private val BLUETOOTH_LE_CC254X_SERVICE =
            UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

        const val ACTION_GATT_CONNECTED =
            "com.skvortsovfk.bluetoothlescanner.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.skvortsovfk.bluetoothlescanner.ACTION_GATT_DISCONNECTED"
    }
}