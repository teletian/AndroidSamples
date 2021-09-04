package com.teletian.jetpackviewmodeljavaversion;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelProvider.AndroidViewModelFactory viewModelFactory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        MyViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(MyViewModel.class);
        viewModel.getNews().observe(this, msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
}