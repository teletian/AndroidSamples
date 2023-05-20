### 前言
自定义 View 的时候如果用到非系统定义的属性的时候，如果要实现双向绑定，不是用了 @= 就行的，自定义 View 中还需要一些设置。

下面通过一个例子来说明自定义 View 的双向绑定的实现。

例子要求：
1. 通过 RadioButton 来选择爱好（爱好的选项是：吃饭 / 睡觉 / 打豆豆）
2. 画面加载的时候显示初始的爱好值（将 ViewModel 里设好的值传到 RadioButton 上）
3. RadioButton 选择的时候把值传到 ViewModel 中去
4. 可以将 RadioButton 的值清空，也就是说可以没有爱好

### 首先自定义 RadioButton 和 RadioGroup
由于爱好是需要定义成 enum 类型的，而 RadioGroup 选择 RadioButton 的时候是通过 id 来的，所以必须先把 enum 转换成 id 才能够实现绑定。但是我们可以通过自定义 RadioButton 和 RadioGroup 来让他们支持 enum 绑定！

先来看自定义 RadioButton 的代码
```java
public class DataBindingRadioButton extends AppCompatRadioButton {

    private Integer value;

    public DataBindingRadioButton(Context context) {
        super(context);
    }

    public DataBindingRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataBindingRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void toggle() {
        if (isChecked()) {
            if (getParent() instanceof RadioGroup) {
                // 点击选中的 RadioButton，可以取消选择
                ((RadioGroup) getParent()).clearCheck();
            }
        } else {
            setChecked(true);
        }
    }

    @BindingAdapter(value = {"value"})
    public static void setValue(DataBindingRadioButton radioButton, Integer value) {
        radioButton.setValue(value);
        ViewParent parent = radioButton.getParent();
        if (parent instanceof DataBindingRadioGroup) {
            Integer checkedValue = ((DataBindingRadioGroup) parent).getCheckedValue();
            radioButton.setChecked(IntegerUtil.isSame(checkedValue, value));
        }
    }
}
```
我们給 DataBindingRadioButton 定义了一个属性 value，value 的值就是 enum 对应的 Integer 值。

enum 的值是通过 DataBinding 绑定进来的，所以需要对应的 set 方法。

我们没有直接用 setValue(Integer value)，而是通过 @BindingAdapter
 用了另外一个带有参数  DataBindingRadioButton 的 set 方法。

原因是不仅需要把值传进来，还需要让 RadioGroup 知道选中的 RadioButton 是哪一个。RadioGroup 如果设置 OnCheckedChange 监听的话，radioButton.setChecked 就会通知 RadioGroup 了。

RadioButton 默认是必须选择一个，toggle() 部分代码是让 RadioButton 支持什么都不选。因为我们的要求是也可以没有爱好。

代码中 IntegerUtil 是为了比较两个 Integer 写的一个 Util 类。问题来了，为什么 value 的值是 Integer 类型的而不是 int 类型的？因为支持不选择爱好，所以爱好的值可以为 null，所以需要定义成 Integer 类型的。

下面是自定义 RadioGroup 的代码
```java
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

    @BindingAdapter("checkedValueAttrChanged")
    public static void setValueChangedListener(DataBindingRadioGroup view, final InverseBindingListener bindingListener) {
        if (bindingListener == null) {
            view.setListener(null);
        } else {
            // 通知 ViewModel
            view.setListener(bindingListener::onChange);
        }
    }
}
```
要支持逆向绑定，首先要在类名上定义 @InverseBindingMethods。
attribute = "checkedValue" 是指定支持逆向绑定的属性。
event = "checkedValueAttrChanged" 是指定 valueChanged 监听事件。
method = "getCheckedValue" 是指定逆向绑定的时候的数据来源方法。

event 和 method 都不是必须的，如果不指定，默认会以以下规则自动生成
event = "xxxAttrChanged"
method = "getXxx"

method 的定义还可以直接在方法上面
```java
@InverseBindingAdapter(attribute = "checkedValue", event = "checkedValueAttrChanged")
public Integer getCheckedValue() {
    return checkedValue;
}
```

@BindingAdapter("checkedValueAttrChanged") 是用来指定监听方法的，重点在 InverseBindingListener，它的 onChange 方法是最后通知 ViewModel 值变更的地方（InverseBindingListener 的实现在生成的类里面，以本例子的话，就是 ActivityMainBinding，下面贴上 InverseBindingListener 的实现）。
```java
    private android.databinding.InverseBindingListener mboundView1checkedValueAttrChanged = new android.databinding.InverseBindingListener() {
        @Override
        public void onChange() {
            // Inverse of vm.hobby
            //         is vm.setHobby((java.lang.Integer) callbackArg_0)
            // 这里就是 method = "getCheckedValue" 指定的方法
            java.lang.Integer callbackArg_0 = mboundView1.getCheckedValue();
            // localize variables for thread safety
            // vm != null
            boolean vmJavaLangObjectNull = false;
            // vm
            com.teletian.databindingradiobutton.viewmodel.ViewModel vm = mVm;
            // vm.hobby
            java.lang.Integer vmHobby = null;

            vmJavaLangObjectNull = (vm) != (null);
            if (vmJavaLangObjectNull) {
                // 这里就是修改 ViewModel 的值
                vm.setHobby(((java.lang.Integer) (callbackArg_0)));
            }
        }
    };
```

setValueChangedListener 所做的事情就是将 onChange 方法做的事情设置到 OnValueChangedListener 里面去。

也许你会问，为什么要这么麻烦，我直接定义一个 InverseBindingListener 的属性直接赋值给它不就 OK 了！

是的，确实是这样，上面的代码确实可以简单的这样做！但是如果 RadioGroup 真的需要设置 OnValueChangedListener，那么就不能这样了！代码需要改成下面这样
```java
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
```

setCheckedValue 方法里面做的事情就是，控制 RadioButton 的 Check 状态以及执行监听的内容。
由于会调用 RadioButton 的 setChecked 方法，然后 init 方法里面又设置了 setOnCheckedChangeListener，所以 setCheckedValue 方法会再次被调用，为了防止循环调用，以下代码是必不可少的
```java
if (IntegerUtil.isSame(this.checkedValue, checkedValue)) {
    return;
}
```

### RadioGroup 和 RadioButton 都自定义完了，下面来看看 Layout 文件
```xml
         <com.teletian.databindingradiobutton.customview.DataBindingRadioGroup
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:checkedValue="@={vm.hobby}">

            <com.teletian.databindingradiobutton.customview.DataBindingRadioButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="吃饭"
                app:value="@{Hobby.EATING.value}" />

            <com.teletian.databindingradiobutton.customview.DataBindingRadioButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="睡觉"
                app:value="@{Hobby.SLEEPING.value}" />

            <com.teletian.databindingradiobutton.customview.DataBindingRadioButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="打豆豆"
                app:value="@{Hobby.ATTACKING_DOUDOU.value}" />

        </com.teletian.databindingradiobutton.customview.DataBindingRadioGroup>
```
首先 RadioButton 的值是通过 app:value="@{Hobby.EATING.value}" 指定的，这样就把 enum 的值 和 RadioButton 联系起来了。

然后在 RadioGroup 中设置 app:checkedValue="@={vm.hobby}" 来设置双向绑定。
