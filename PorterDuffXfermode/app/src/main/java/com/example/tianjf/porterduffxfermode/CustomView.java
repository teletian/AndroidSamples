package com.example.tianjf.porterduffxfermode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class CustomView extends View {

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    PorterDuff.Mode mode;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setXfermode(PorterDuff.Mode mode) {
        this.mode = mode;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int r = canvas.getWidth() / 3;

        // 设置背景色
        canvas.drawARGB(255, 139, 197, 186);

        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);

        //绘制黄色的圆形
        paint.setColor(0xFFFFCC44);
        canvas.drawCircle(r, r, r, paint);

        //绘制蓝色的矩形
        paint.setColor(0xFF66AAFF);
        if (mode != null) {
            paint.setXfermode(new PorterDuffXfermode(mode));
        }
        canvas.drawRect(r, r, r * 2.7f, r * 2.7f, paint);

        canvas.restoreToCount(layerId);

        paint.setXfermode(null);
    }
}
