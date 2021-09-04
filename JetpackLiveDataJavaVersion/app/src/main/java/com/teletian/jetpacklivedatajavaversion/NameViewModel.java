package com.teletian.jetpacklivedatajavaversion;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NameViewModel extends ViewModel {

    private MutableLiveData<String> currentName = new MutableLiveData<>();

    public MutableLiveData<String> getCurrentName() {
        return currentName;
    }
}
