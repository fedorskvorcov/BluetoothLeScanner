package com.skvortsovfk.bluetoothlescanner.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skvortsovfk.bluetoothlescanner.databinding.FragmentDeviceControlBinding
import com.skvortsovfk.bluetoothlescanner.services.BluetoothLeService
import com.skvortsovfk.bluetoothlescanner.services.BluetoothLeService.Companion.ACTION_GATT_CONNECTED
import com.skvortsovfk.bluetoothlescanner.services.BluetoothLeService.Companion.ACTION_GATT_DISCONNECTED

class DeviceControlFragment : Fragment() {

    private var _binding: FragmentDeviceControlBinding? = null
    private val binding
        get() = _binding!!

    val args: DeviceControlFragmentArgs by navArgs()

    private var bluetoothLeService: BluetoothLeService? = null

    private var bound = false
    private var connected = false

    // Code to manage Service lifecycle.
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as BluetoothLeService.LocalBinder
            bluetoothLeService = binder.getService()
            if (!bluetoothLeService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth"
                )
                activity?.finish()
            }
            bound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bound = false
        }
    }

    private val gattUpdateReceiver = object : BroadcastReceiver() {

        private lateinit var bluetoothLeService: BluetoothLeService

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_GATT_CONNECTED -> {
                    connected = true
                }
                ACTION_GATT_DISCONNECTED -> {
                    connected = false
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = args.bluetoothDevice.name
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.connectButton.setOnClickListener {
            bluetoothLeService?.connect(args.bluetoothDevice.address)
        }
        binding.disconnectButton.setOnClickListener {
            bluetoothLeService?.disconnect()
        }
        // Bind to local service
        Intent(requireContext(), BluetoothLeService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(ACTION_GATT_CONNECTED)
            addAction(ACTION_GATT_DISCONNECTED)
        }
        activity?.registerReceiver(gattUpdateReceiver, filter)
        if (bluetoothLeService != null) {
            val result: Boolean = bluetoothLeService!!.connect(args.bluetoothDevice.address)
            Log.d(TAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(gattUpdateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.unbindService(connection)
        bound = false
    }

    companion object {
        private val TAG = DeviceListFragment::class.simpleName!!
    }
}