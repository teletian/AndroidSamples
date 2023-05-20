package com.teletian.sample.customradiogroupradiobutton.customview

import android.view.View
import android.widget.Checkable

interface RadioCheckable : Checkable {

    fun addOnCheckChangeListener(onCheckedChangeListener: OnCheckedChangeListener)
    fun removeOnCheckChangeListener(onCheckedChangeListener: OnCheckedChangeListener)

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: View, isChecked: Boolean)
    }
}