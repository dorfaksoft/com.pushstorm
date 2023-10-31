package com.pushstormsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pushstorm.PushStorm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushStorm.init(this);
    }
}