package com.skvortsovfk.bluetoothlescanner.ui

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skvortsovfk.bluetoothlescanner.R
import com.skvortsovfk.bluetoothlescanner.adapters.DeviceListAdapter
import com.skvortsovfk.bluetoothlescanner.databinding.FragmentDeviceListBinding
import com.skvortsovfk.bluetoothlescanner.viewmodels.DeviceListViewModel
import com.skvortsovfk.bluetoothlescanner.viewmodels.DeviceListViewModelFactory

class DeviceListFragment : Fragment() {

    private var _binding: FragmentDeviceListBinding? = null
    val binding
        get() = _binding!!

    private lateinit var viewModel: DeviceListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = DeviceListViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DeviceListViewModel::class.java)

        val adapter = DeviceListAdapter()
        binding.deviceListRecyclerView.adapter = adapter

        adapter.onItemClick = { bluetoothDevice ->
            Toast.makeText(
                context,
                "Clicked ${bluetoothDevice.name} ${bluetoothDevice.address}",
                Toast.LENGTH_SHORT
            ).show()
        }

        adapter.onNameClick = { bluetoothDevice ->
            Toast.makeText(
                context,
                "${bluetoothDevice.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.devices.observe(viewLifecycleOwner, Observer {  devices ->
            devices?.let { adapter.values = devices }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.scanLeDevice(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.scanLeDevice(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}