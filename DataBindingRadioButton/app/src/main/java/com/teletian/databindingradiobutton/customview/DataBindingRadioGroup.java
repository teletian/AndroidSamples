package com.teletian.databindingradiobutton.customview;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import com.teletian.databindingradiobutton.utils.IntegerUtil;

@InverseBindingMethods({
        @InverseBindingMethod(
                type = DataBindingRadioGroup.class,
                attribute = "checkedValue",
                event = "checkedValueAttrChanged",
                method = "getCheckedValue")
})
public class DataBindingRadioGroup extends RadioGroup {

    private Integer checkedValue;
    private OnValueChangedListener listener;

    public DataBindingRadioGroup(Context context) {
        super(context);
        init();
    }

    public DataBindingRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId > 0) {
                DataBindingRadioButton radioButton = (DataBindingRadioButton) findViewById(checkedId);
                setCheckedValue(radioButton.isChecked() ? radioButton.getValue() : null);
            } else {
                setCheckedValue(null);
            }
        });
    }

    public Integer getCheckedValue() {
        return checkedValue;
    }

    public void setCheckedValue(Integer checkedValue) {

        if (IntegerUtil.isSame(this.checkedValue, checkedValue)) {
            return;
        }

        this.checkedValue = checkedValue;

        if (this.checkedValue == null) {
            clearCheck();
        } else {
            DataBindingRadioButton customRadioButton = (DataBindingRadioButton) findViewById(getCheckedRadioButtonId());
            if (customRadioButton == null || !IntegerUtil.isSame(this.checkedValue, customRadioButton.getValue())) {

                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof DataBindingRadioButton) {
                        Integer value = ((DataBindingRadioButton) child).getValue();
                        if (IntegerUtil.isSame(this.checkedValue, value)) {
                            ((DataBindingRadioButton) child).setChecked(true);
                        }
                    }
                }
            }
        }

        if (listener != null) {
            listener.onValueChanged();
        }
    }

    public void setListener(OnValueChangedListener listener) {
        this.listener = listener;
    }

    public interface OnValueChangedListener {
        void onValueChanged();
    }

    @BindingAdapter(value = {"onCheckedValueChanged", "checkedValueAttrChanged"}, requireAll = false)
    public static void setValueChangedListener(DataBindingRadioGroup view,
                                               final OnValueChangedListener valueChangedListener,
                                               final InverseBindingListener bindingListener) {
        if (bindingListener == null) {
            view.setListener(valueChangedListener);
        } else {
            view.setListener(() -> {
                if (valueChangedListener != null) {
                    valueChangedListener.onValueChanged();
                }
                // 通知 ViewModel
                bindingListener.onChange();
            });
        }
    }
}
