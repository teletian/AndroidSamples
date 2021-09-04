package com.teletian.jetpacklifecyclejavaversion;

interface BasePresenter {
    void onCreate();

    void onStart();

    void onResume();

    void onStop();

    void onDestroy();
}
