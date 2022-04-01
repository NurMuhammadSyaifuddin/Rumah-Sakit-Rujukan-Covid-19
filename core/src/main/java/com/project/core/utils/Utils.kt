package com.project.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

const val EXTRA_DATA_FOR_REGISTRATION = "extra_data_for_registration"

fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        return connectivityManager.activeNetworkInfo != null
    } else {
        for (network in connectivityManager.allNetworks) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return (
                    networkCapabilities!!.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                            && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                    )
        }
    }

    return false
}

fun ImageView.loadImage(url: String){
    Glide.with(this.context)
        .load(Uri.parse(url))
        .into(this)
}