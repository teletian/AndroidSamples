package com.teletian.databindingradiobutton.enums;

import com.teletian.databindingradiobutton.utils.IntegerUtil;

public enum Hobby {

    EATING(1), SLEEPING(2), ATTACKING_DOUDOU(3), NOT_SET(null);

    private Integer value;

    Hobby(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static Hobby fromValue(Integer value) {
        for (Hobby val : values()) {
            if (IntegerUtil.isSame(val.getValue(), value)) {
                return val;
            }
        }
        return NOT_SET;
    }
}
