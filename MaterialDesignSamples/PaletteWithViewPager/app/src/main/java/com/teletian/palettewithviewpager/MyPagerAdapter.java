package com.teletian.palettewithviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

class MyPagerAdapter extends FragmentStatePagerAdapter {

    private List<Bean> dataList;

    MyPagerAdapter(FragmentManager fm, List<Bean> dataList) {
        super(fm);
        this.dataList = dataList;
    }

    @Override
    public Fragment getItem(int position) {
        return MyFragment.newInstance(dataList.get(position).getImageResId());
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dataList.get(position).getTitle();
    }

    public int getImageResId(int position) {
        return dataList.get(position).getImageResId();
    }
}
