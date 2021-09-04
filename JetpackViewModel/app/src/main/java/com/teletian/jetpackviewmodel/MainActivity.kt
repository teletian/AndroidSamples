package com.teletian.jetpackviewmodel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProvider(this)[MyViewModel::class.java]
        // Use the 'by viewModels()' Kotlin property delegate
        // from the activity-ktx artifact
//        val model: MyViewModel by viewModels()

        model.getNews().observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}