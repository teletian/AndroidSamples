package com.teletian.palettewithviewpager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyPagerAdapter adapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.view_pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager(), getData());
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        changeColor(0); // 初次加载的时候如果正好 position 是 0，则不调用 onPageSelected
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeColor(position);
            }
        });
    }

    private List<Bean> getData() {
        List<Bean> dataList = new ArrayList<>();
        dataList.add(new Bean("One", R.drawable.one));
        dataList.add(new Bean("Two", R.drawable.two));
        dataList.add(new Bean("Three", R.drawable.three));
        dataList.add(new Bean("Four", R.drawable.four));
        return dataList;
    }

    private void changeColor(int position) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), adapter.getImageResId(position));
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();

                if (vibrant == null) {
                    return;
                }

                tabLayout.setBackgroundColor(vibrant.getRgb());
                tabLayout.setTabTextColors(vibrant.getTitleTextColor(), vibrant.getTitleTextColor());
                tabLayout.setSelectedTabIndicatorColor(darkColorOf(vibrant.getRgb()));

                toolbar.setBackgroundColor(vibrant.getRgb());

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    window.setStatusBarColor(darkColorOf(vibrant.getRgb()));
                    window.setNavigationBarColor(darkColorOf(vibrant.getRgb()));
                }
            }
        });
    }

    /**
     * StatusBar，NavigationBar，以及 Tab 的 Indicator 的颜色做加深处理
     */
    private int darkColorOf(int RGBValues) {
        // 通过向右位移和与操作得到 R G B 的值，每个颜色值占 1 个字节（8 位）
        // 0x 开头的表示 16 进制，0xFF 换成二进制是 1111 1111
        // & 0xFF 进行与操作，和 1111 1111 进行与操作得到的是自身，所以相当于取出颜色值的末 8 位
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        // R G B 的值都减少一点，然后再合成，看起来会深一点
        red = (int) Math.floor(red * (1 - 0.3));
        green = (int) Math.floor(green * (1 - 0.3));
        blue = (int) Math.floor(blue * (1 - 0.3));
        return Color.rgb(red, green, blue);
    }
}
