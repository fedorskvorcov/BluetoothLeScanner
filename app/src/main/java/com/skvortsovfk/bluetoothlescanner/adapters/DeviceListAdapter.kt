package com.skvortsovfk.bluetoothlescanner.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skvortsovfk.bluetoothlescanner.databinding.ListItemDeviceBinding
import kotlinx.android.synthetic.main.list_item_device.view.*

class DeviceListAdapter : RecyclerView.Adapter<DeviceViewHolder>() {

    var onCardClick: ((BluetoothDevice) -> Unit)? = null

    var values = emptyList<BluetoothDevice>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DeviceViewHolder(
            ListItemDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = values[position]
        holder.bind(device)

        holder.itemView.cardView.setOnClickListener {
            onCardClick?.invoke(values[position])
        }
    }
}