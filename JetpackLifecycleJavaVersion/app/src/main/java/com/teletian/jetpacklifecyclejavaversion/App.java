package com.teletian.jetpacklifecyclejavaversion;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationLifecycleObserver());
    }

    static class ApplicationLifecycleObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void onAppForeground() {
            Log.i("tianjf", "App foreground");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onAppBackground() {
            Log.i("tianjf", "App background");
        }
    }
}