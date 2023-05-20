package com.teletian.databindingradiobutton.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.teletian.databindingradiobutton.BR;
import com.teletian.databindingradiobutton.enums.Hobby;

public class ViewModel extends BaseObservable {

    private Hobby hobby;

    public ViewModel(Hobby hobby) {
        this.hobby = hobby;
    }

    @Bindable
    public Integer getHobby() {
        return hobby.getValue();
    }

    public void setHobby(Integer value) {
        this.hobby = Hobby.fromValue(value);
        notifyPropertyChanged(BR.hobby);
    }
}
