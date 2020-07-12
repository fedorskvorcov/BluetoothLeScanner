package com.skvortsovfk.bluetoothlescanner.adapters

import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.RecyclerView
import com.skvortsovfk.bluetoothlescanner.databinding.ListItemDeviceBinding

class DeviceViewHolder(
    private val binding: ListItemDeviceBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(device: BluetoothDevice) {
        binding.nameTextView.text = device.name
        binding.addressTextView.text = device.address
    }
}