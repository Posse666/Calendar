package com.posse.kotlin1.calendar.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.posse.kotlin1.calendar.app.App

fun isNetworkOnline(): Boolean {
    var isOnline = false
    try {
        val manager =
            App.appInstance?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        isOnline =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return isOnline
}