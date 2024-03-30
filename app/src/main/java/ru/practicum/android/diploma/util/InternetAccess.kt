package ru.practicum.android.diploma.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun isConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    var connected = false

    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> connected = true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> connected = true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> connected = true
        }
    }
    return connected
}
