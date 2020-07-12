package com.skvortsovfk.bluetoothlescanner.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class DeviceListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceListViewModel::class.java)) {
            return DeviceListViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}