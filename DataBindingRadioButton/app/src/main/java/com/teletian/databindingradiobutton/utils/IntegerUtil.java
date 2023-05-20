package com.teletian.databindingradiobutton.utils;

public class IntegerUtil {

    public static boolean isSame(Integer value1, Integer value2) {
        return (value1 == null && value2 == null) || (value1 != null && value1.equals(value2));
    }
}
