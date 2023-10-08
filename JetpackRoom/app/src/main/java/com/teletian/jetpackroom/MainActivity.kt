package com.teletian.jetpackroom

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tianjf.db")
                .allowMainThreadQueries()
                .build()
        val userDao = db.userDao()
        userDao.deleteAll()
        for (i in 1..10) {
            userDao.insertAll(User(i, firstName = "Jack", lastName = "West"))
        }
        userDao.getAll().forEach { Log.i("tianjf", it.toString()) }
    }
}