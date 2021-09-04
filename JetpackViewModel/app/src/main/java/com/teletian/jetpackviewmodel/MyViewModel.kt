package com.teletian.jetpackviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {

    private val news: MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            fetchNews()
        }
    }

    fun getNews(): LiveData<String> {
        return news
    }

    private fun fetchNews() {
        Thread {
            Thread.sleep(3000)
            news.postValue("Completed!")
        }.start()
    }
}