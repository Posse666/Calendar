package com.posse.kotlin1.calendar.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import javax.inject.Inject

class NetworkStatusImpl @Inject constructor(private val context: Context) : NetworkStatus {

    override fun isNetworkOnline(): Boolean {
        var isOnline = false
        try {
            val manager = context.getSystemService<ConnectivityManager>()
            val capabilities = manager?.getNetworkCapabilities(manager.activeNetwork)
            isOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isOnline
    }
}