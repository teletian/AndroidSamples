package com.teletian.sample.camera2.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionsFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final String[] PERMISSIONS_REQUIRED = new String[]{Manifest.permission.CAMERA};
    private OnNavigateToSelectorListener onNavigateToSelectorListener;

    public void setOnNavigateToSelectorListener(OnNavigateToSelectorListener listener) {
        this.onNavigateToSelectorListener = listener;
    }

    public static PermissionsFragment newInstance() {
        return new PermissionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasPermissions(requireContext())) {
            navigateToSelector();
        } else {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToSelector();
            } else {
                Toast.makeText(requireContext(),
                        "Permission request denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void navigateToSelector() {
        if (onNavigateToSelectorListener != null) {
            onNavigateToSelectorListener.OnNavigateToSelector();
        }
    }

    private boolean hasPermissions(Context context) {
        for (String permission : PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface OnNavigateToSelectorListener {
        void OnNavigateToSelector();
    }
}
