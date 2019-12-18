package com.example.dawn.appdesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dawn.appdesign.sample.DeviceScanActivity;

public class MainActivity extends Activity {


    private final static String TAG = "MainActivity";

    private Button gameStart_button;
    private Button record_button;
    private Button bt_button;
    private Button test_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG,"进入LoginActivity");

//        getActionBar().setTitle("测试");

        gameStart_button=findViewById(R.id.start_button);
        record_button=findViewById(R.id.record_button);
        bt_button = findViewById(R.id.bt_button);
        test_button = findViewById(R.id.test_button);



        gameStart_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,GameActivity.class);
                startActivity(it);
            }
        });

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,RecordActivity.class);
                startActivity(it);
            }
        });

        bt_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,BtBridgeActivity.class);
                startActivity(it);
            }
        });
        test_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,DeviceScanActivity.class);
                startActivity(it);
            }
        });

    }
}
