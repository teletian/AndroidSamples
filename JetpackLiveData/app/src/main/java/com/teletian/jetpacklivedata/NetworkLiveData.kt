package com.teletian.jetpacklivedata

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData

class NetworkLiveData(private val context: Context?) : LiveData<NetworkInfo?>() {

    private var networkReceiver: NetworkReceiver
    private var intentFilter: IntentFilter

    init {
        networkReceiver = NetworkReceiver()
        intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    companion object {
        private lateinit var instance: NetworkLiveData

        @MainThread
        fun getInstance(context: Context?): NetworkLiveData {
            instance = if (::instance.isInitialized) instance else NetworkLiveData(context)
            return instance
        }
    }

    override fun onActive() {
        super.onActive()
        Log.d("tianjf", "onActive")
        context!!.applicationContext.registerReceiver(networkReceiver, intentFilter)
    }

    override fun onInactive() {
        super.onInactive()
        Log.d("tianjf", "onInactive")
        context!!.applicationContext.unregisterReceiver(networkReceiver)
    }

    inner class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo = connectivityManager?.activeNetworkInfo
            getInstance(context).value = networkInfo
        }

    }
}