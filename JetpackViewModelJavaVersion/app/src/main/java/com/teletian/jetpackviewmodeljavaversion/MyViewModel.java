package com.teletian.jetpackviewmodeljavaversion;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private MutableLiveData<String> news;

    public MyViewModel() {
        this.news = new MutableLiveData<>();
        fetchNews();
    }

    public MutableLiveData<String> getNews() {
        return news;
    }

    private void fetchNews() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            news.postValue("Completed!");
        }).start();
    }
}
