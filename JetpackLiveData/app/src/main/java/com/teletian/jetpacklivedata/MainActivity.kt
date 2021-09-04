package com.teletian.jetpacklivedata

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    var viewModel: NameViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.text)

        viewModel = ViewModelProvider(this)[NameViewModel::class.java]
        // observe 方法有两个参数。
        // 第一个指定 LifecycleOwner
        // 第二个指定 Observer。当 LiveData 更新时，Observer 的 onChanged 方法会被调用
        viewModel?.currentName?.observe(this, { name -> textView.text = name })

        // 自定义 LiveData 监听网络变化
        NetworkLiveData.getInstance(this).observe(this, { networkInfo ->
            Log.d("tianjf", "onChanged: networkInfo=${networkInfo.toString()}")
        })
    }

    fun onButtonPressed(view: View) {
        // 从主线程更新 LiveData 使用 setValue
        // 从子线程更新 LiveData 使用 postValue。其实内部就是使用了 Handler 把值 post 到主线程去了
        //     如果在主线程执行之前调用了多次 postValue，只有最后一次有效
        viewModel?.currentName?.value = "更新 LiveData"
    }
}