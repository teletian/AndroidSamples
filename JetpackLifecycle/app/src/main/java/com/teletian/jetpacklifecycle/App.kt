package com.teletian.jetpacklifecycle

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())
    }

    inner class ApplicationLifecycleObserver : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onAppForeground() {
            Log.i("tianjf", "App foreground")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onAppBackground() {
            Log.i("tianjf", "App background")
        }
    }
}