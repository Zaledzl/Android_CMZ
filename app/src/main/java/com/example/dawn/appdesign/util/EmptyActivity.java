package com.example.dawn.appdesign.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dawn.appdesign.R;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
    }
}
