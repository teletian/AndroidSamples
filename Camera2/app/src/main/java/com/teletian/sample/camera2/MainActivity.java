package com.teletian.sample.camera2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teletian.sample.camera2.fragments.CameraBasicFragment;
import com.teletian.sample.camera2.fragments.PermissionsFragment;
import com.teletian.sample.camera2.fragments.SelectorFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigateToPermission();
    }

    private void navigateToPermission() {
        PermissionsFragment permissionsFragment = PermissionsFragment.newInstance();
        permissionsFragment.setOnNavigateToSelectorListener(this::navigateToSelector);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, permissionsFragment)
                .commit();
    }

    private void navigateToSelector() {
        SelectorFragment selectorFragment = SelectorFragment.newInstance();
        selectorFragment.setOnNavigateToCameraBasicListener(this::navigateToCameraBasic);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, selectorFragment)
                .commit();
    }

    private void navigateToCameraBasic() {
        CameraBasicFragment cameraBasicFragment = CameraBasicFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, cameraBasicFragment)
                .commit();
    }
}
