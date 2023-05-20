package com.teletian.sample.camera2.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class AutoFitSurfaceView extends SurfaceView {

    private static final String TAG = AutoFitSurfaceView.class.getSimpleName();
    private float aspectRatio = 0f;

    public AutoFitSurfaceView(Context context) {
        super(context);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width, int height) {
        aspectRatio = width * 1.0f / height;
        getHolder().setFixedSize(width, height);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (aspectRatio == 0f) {
            setMeasuredDimension(width, height);
        } else {
            int newWidth;
            int newHeight;
            float actualRatio = width > height ? aspectRatio : 1f / aspectRatio;
            if (width < height * actualRatio) {
                newHeight = height;
                newWidth = (int) (height * actualRatio);
            } else {
                newWidth = width;
                newHeight = (int) (width / actualRatio);
            }

            Log.d(TAG, "Measured dimensions set: $newWidth x $newHeight");
            setMeasuredDimension(newWidth, newHeight);
        }
    }
}
