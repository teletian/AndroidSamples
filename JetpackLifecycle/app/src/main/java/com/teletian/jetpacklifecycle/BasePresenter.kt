package com.teletian.jetpacklifecycle

interface BasePresenter {
    fun onCreate()
    fun onStart()
    fun onResume()
    fun onStop()
    fun onDestroy()
}