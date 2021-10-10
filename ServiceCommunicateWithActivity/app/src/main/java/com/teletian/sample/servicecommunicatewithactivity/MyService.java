package com.teletian.sample.servicecommunicatewithactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyService extends Service {

    private MyBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new MyBinder();
    }

    /**
     * onBind 方法是 Service 与 Activity 之间建立通信的桥梁（中间人）
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 此处返回 Binder 对象供 Activity 使用
        return binder;
    }

    public class MyBinder extends Binder {
        public void run() {
            Toast.makeText(MyService.this, "成功", Toast.LENGTH_SHORT).show();
        }
    }
}
