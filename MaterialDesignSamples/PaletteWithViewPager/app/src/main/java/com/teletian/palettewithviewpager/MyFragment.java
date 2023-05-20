package com.teletian.palettewithviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyFragment extends Fragment {

    public static MyFragment newInstance(int imageResId) {
        Bundle args = new Bundle();
        args.putInt("image", imageResId);
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.image);
        imageView.setBackgroundResource(getArguments().getInt("image"));
        return view;
    }
}
