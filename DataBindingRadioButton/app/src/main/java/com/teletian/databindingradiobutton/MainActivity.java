package com.teletian.databindingradiobutton;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.teletian.databindingradiobutton.databinding.ActivityMainBinding;
import com.teletian.databindingradiobutton.enums.Hobby;
import com.teletian.databindingradiobutton.viewmodel.ViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewModel vm = new ViewModel(Hobby.SLEEPING);
        binding.setVm(vm);

        findViewById(R.id.button).setOnClickListener(
                v -> ((TextView) findViewById(R.id.text_view)).setText(String.valueOf(vm.getHobby())));
    }
}
