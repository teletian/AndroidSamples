package com.teletian.sample.servicecommunicatewithactivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MyConn conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = new MyConn();

        findViewById(R.id.button).setOnClickListener(view -> {
            Intent bindIntent = new Intent(this, MyService.class);
            // 传入的 ServiceConnection 对象监听 Service 的回调
            // 第三个参数 flags。
            // 传 0 表示只绑定，不启动 Service，需要调用 startService 启动。
            // 传 1（BIND_AUTO_CREATE）表示绑定并启动，不需要调用 startService 启动。
            bindService(bindIntent, conn, BIND_AUTO_CREATE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private static class MyConn implements ServiceConnection {
        // 当服务连接时，如果绑定的服务 onBind 没有返回值，则不执行该方法
        // iBinder 就是 Service 的 onBind 方法返回的 Binder 对象
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            binder.run();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}