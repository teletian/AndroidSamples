package com.teletian.inputfromenddecimaledittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.text.DecimalFormat;

/**
 * 从右边开始输入的 EditText
 */
public class InputFromEndDecimalEditText extends AppCompatEditText {

    // 整数位位数
    private int integerLength;
    // 小数位位数
    private int decimalLength;

    public InputFromEndDecimalEditText(Context context) {
        super(context);
        init(context, null);
    }

    public InputFromEndDecimalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InputFromEndDecimalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputFromEndDecimalEditText);
        integerLength = a.getInteger(R.styleable.InputFromEndDecimalEditText_integerLength, 0);
        decimalLength = a.getInteger(R.styleable.InputFromEndDecimalEditText_decimalLength, 0);
        a.recycle();

        // 限制 EditText 的输入长度
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerLength + decimalLength + 1)});
        // 光标不可见（因为要控制光标始终在最后，所以不让用户看见光标）
        setCursorVisible(false);
        setTextChangeListener();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setMaximumIntegerDigits(integerLength);
        decimalFormat.setMinimumFractionDigits(decimalLength);
        decimalFormat.setMaximumFractionDigits(decimalLength);

        super.setText(TextUtils.isEmpty(text) ?
                text : decimalFormat.format(Double.valueOf(text.toString())), type);
    }

    private void setTextChangeListener() {
        addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 利用正则表达式来判断数字的格式是否达到要求
                if (!TextUtils.isEmpty(s) &&
                        !s.toString().matches("^(\\d{1," + integerLength + "}.\\d{" + decimalLength + "})$")) {
                    // 去掉非数字，也就是小数点
                    Integer userInputWithoutDecimalPoint = Integer.valueOf(s.toString().replaceAll("[^\\d]", ""));

                    if (count == 0 && userInputWithoutDecimalPoint == 0) {
                        // 删除之后为 0 的话，全部删除
                        setText(null);
                    } else {
                        StringBuilder userInputBuilder = new StringBuilder(userInputWithoutDecimalPoint.toString());

                        // 补 0
                        while (userInputBuilder.length() <= decimalLength) {
                            userInputBuilder.insert(0, '0');
                        }
                        // 插入小数点
                        userInputBuilder.insert(userInputBuilder.length() - decimalLength, '.');

                        setText(userInputBuilder.toString());
                        moveCursorToEnd();
                    }
                }
            }
        });
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (getText() != null && (selStart != getText().length() || selEnd != getText().length())) {
            moveCursorToEnd();
            return;
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    private void moveCursorToEnd() {
        setSelection(getText().length());
    }
}
