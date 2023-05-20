package com.teletian.sample.camera2.utils;

import android.graphics.Point;
import android.util.Size;
import android.view.Display;

public class SmartSize {

    private Size size;
    private int longSize;
    private int shortSize;

    public static SmartSize SIZE_1080P = new SmartSize(1920, 1080);

    public SmartSize(int width, int height) {
        this.size = new Size(width, height);
        this.longSize = Math.max(width, height);
        this.shortSize = Math.min(width, height);
    }

    public static SmartSize getDisplaySmartSize(Display display) {
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        return new SmartSize(outPoint.x, outPoint.y);
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getLongSize() {
        return longSize;
    }

    public void setLongSize(int longSize) {
        this.longSize = longSize;
    }

    public int getShortSize() {
        return shortSize;
    }

    public void setShortSize(int shortSize) {
        this.shortSize = shortSize;
    }
}
