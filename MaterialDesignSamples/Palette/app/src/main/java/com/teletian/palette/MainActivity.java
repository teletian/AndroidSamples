package com.teletian.palette;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image = findViewById(R.id.image);
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                //充满活力的样本
                Palette.Swatch swath1 = palette.getVibrantSwatch();
                if (swath1 != null) {
                    findViewById(R.id.color1).setBackgroundColor(swath1.getRgb());
                }

                //充满活力的暗色样本
                Palette.Swatch swath2 = palette.getDarkVibrantSwatch();
                if (swath2 != null) {
                    findViewById(R.id.color2).setBackgroundColor(swath2.getRgb());
                }

                //充满活力的亮色样本
                Palette.Swatch swath3 = palette.getLightVibrantSwatch();
                if (swath3 != null) {
                    findViewById(R.id.color3).setBackgroundColor(swath3.getRgb());
                }

                //柔和的样本
                Palette.Swatch swath4 = palette.getMutedSwatch();
                if (swath4 != null) {
                    findViewById(R.id.color4).setBackgroundColor(swath4.getRgb());
                }

                //柔和的暗色样本
                Palette.Swatch swath5 = palette.getDarkMutedSwatch();
                if (swath5 != null) {
                    findViewById(R.id.color5).setBackgroundColor(swath5.getRgb());
                }

                //柔和的亮色样本
                Palette.Swatch swath6 = palette.getLightMutedSwatch();
                if (swath6 != null) {
                    findViewById(R.id.color6).setBackgroundColor(swath6.getRgb());
                }
            }
        });
    }
}
