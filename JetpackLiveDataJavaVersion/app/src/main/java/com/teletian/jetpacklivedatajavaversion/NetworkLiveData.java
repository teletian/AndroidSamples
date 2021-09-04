package com.teletian.jetpacklivedatajavaversion;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;

public class NetworkLiveData extends LiveData<NetworkInfo> {

    private Context context;
    private IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private NetworkReceiver networkReceiver = new NetworkReceiver();

    @SuppressLint("StaticFieldLeak")
    private static NetworkLiveData instance;

    private NetworkLiveData(Context context) {
        this.context = context;
    }

    public static NetworkLiveData getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkLiveData(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    protected void onActive() {
        super.onActive();
        context.registerReceiver(networkReceiver, intentFilter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        context.unregisterReceiver(networkReceiver);
    }

    static class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            NetworkLiveData.getInstance(context).setValue(networkInfo);
        }
    }
}
