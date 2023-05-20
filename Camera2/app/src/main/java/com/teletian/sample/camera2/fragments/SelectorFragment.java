package com.teletian.sample.camera2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teletian.sample.camera2.R;

public class SelectorFragment extends Fragment {

    private OnNavigateToCameraBasicListener onNavigateToCameraBasicListener;

    public void setOnNavigateToCameraBasicListener(OnNavigateToCameraBasicListener listener) {
        this.onNavigateToCameraBasicListener = listener;
    }

    public static SelectorFragment newInstance() {
        return new SelectorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_selector, container, false);
        view.findViewById(R.id.basic).setOnClickListener(v -> navigateToCameraBasic());
        return view;
    }

    private void navigateToCameraBasic() {
        if (onNavigateToCameraBasicListener != null) {
            onNavigateToCameraBasicListener.onNavigateToCameraBasic();
        }
    }

    public interface OnNavigateToCameraBasicListener {
        void onNavigateToCameraBasic();
    }
}
