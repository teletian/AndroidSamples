package com.example.tianjf.porterduffxfermode;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context context;
    private List<PorterDuff.Mode> modeList = Arrays.asList
            (
                    PorterDuff.Mode.CLEAR, //清除
                    PorterDuff.Mode.SRC, //只绘制源图
                    PorterDuff.Mode.DST, //只绘制目标图像
                    PorterDuff.Mode.SRC_OVER, //在目标图像的上方绘制源图像
                    PorterDuff.Mode.DST_OVER, //在源图像的上方绘制目标图像
                    PorterDuff.Mode.SRC_IN, //只在源图像和目标图像相交的地方绘制源图像
                    PorterDuff.Mode.DST_IN, //只在源图像和目标图像相交的地方绘制目标图像
                    PorterDuff.Mode.SRC_OUT, //只在源图像和目标图像不相交的地方绘制源图像
                    PorterDuff.Mode.DST_OUT, //只在源图像和目标图像不相交的地方绘制目标图像
                    PorterDuff.Mode.SRC_ATOP, //在源图像和目标图像相交的地方绘制源图像，在不相交的地方绘制目标图像
                    PorterDuff.Mode.DST_ATOP, //在源图像和目标图像相交的地方绘制目标图像，在不相交的地方绘制源图像
                    PorterDuff.Mode.XOR, //在源图像和目标图像重叠之外的任何地方绘制他们，而在重叠的地方不绘制任何内容
                    PorterDuff.Mode.DARKEN, //变暗
                    PorterDuff.Mode.LIGHTEN, //变亮
                    PorterDuff.Mode.MULTIPLY, //正片叠底
                    PorterDuff.Mode.SCREEN, //滤色
                    PorterDuff.Mode.ADD, //饱和相加
                    PorterDuff.Mode.OVERLAY //叠加
            );

    CustomAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PorterDuff.Mode mode = modeList.get(position);
        holder.textView.setText(mode.toString());
        holder.customView.setXfermode(mode);
    }

    @Override
    public int getItemCount() {
        return modeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        CustomView customView;

        ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.text_view);
            customView = (CustomView) v.findViewById(R.id.custom_view);
        }
    }
}
