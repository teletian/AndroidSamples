### 背景
最近项目要求可以输入体重，而且小数点是自动输入的，不需要用户输入。

这样就带来一个问题，体重的整数位可以是两位，比如 60.5，也可以是三位，比如 100.5，如果单位是 g，也可以是四位，比如 1000.5。

那么小数点自动输入的话，是在第二位整数后面输入？还是在第三第四位整数后面输入？

这个根本没有办法判断！

所以 UX 提出了一个方案：小数点固定为两位，输入时从右边开始输入。

举个例子：如果想输入 60.55 的体重的时候
输入6 → 0.06
输入0 → 0.60
输入5 → 6.05
输入5 → 60.55

这样的话我要输入 66，岂不是要输入 6600 ？这样 Usability 真的好么？

后来在中石化圈存加油卡的时候，发现金额的输入规则也是从右边开始输入的，我要圈存 500 元，输入了 50000。看来是我孤陋寡闻了(=ﾟДﾟ=)

### 自定义 EditText
为了达到从右边开始输入的效果，我们来自定义 EditText。
直接上代码吧（讲解请看代码中的注释）
```java
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
```

XML 中的用法如下
```xml
   <com.teletian.inputfromenddecimaledittext.InputFromEndDecimalEditText
        android:id="@+id/edit_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@null"
        android:gravity="right"
        android:hint="000.00"
        android:inputType="numberDecimal"
        android:textColor="@android:color/holo_blue_dark"
        android:textColorHint="@android:color/darker_gray"
        app:decimalLength="2"
        app:integerLength="3" />
```
没有输入的时候显示 hint，提示用户有几位整数，有几位小数。
为了把 hint 和正常的数值区分开，我们设置了 textColor 和 textColorHint 为不同的颜色。

以上代码还有个小小的问题：
如果数值是以 double 类型存在数据库中的，那么 66.00 会变成 66.0。那么下次显示到 EditText 的时候就会变成 6.60。

为了解决这个问题，我们让传入到 EditText 的值变换成指定的小数位。
只要复写 EditText 的 setText 方法就行了
```java
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
```
