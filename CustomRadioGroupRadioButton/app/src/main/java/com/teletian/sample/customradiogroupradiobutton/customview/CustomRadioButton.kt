package com.teletian.sample.customradiogroupradiobutton.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class CustomRadioButton(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), RadioCheckable {

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

    }

    override fun addOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener) {
        TODO("Not yet implemented")
    }

    override fun removeOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener) {
        TODO("Not yet implemented")
    }

    override fun setChecked(checked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isChecked(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toggle() {
        TODO("Not yet implemented")
    }
}