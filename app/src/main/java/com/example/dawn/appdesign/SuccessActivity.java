package com.example.dawn.appdesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SuccessActivity extends AppCompatActivity {
    private final static String TAG = "SuccessActivity";
    private EditText tip;
    private Button but;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        tip = (EditText)findViewById(R.id.SuccessTip);
        but = (Button)findViewById(R.id.SuccessBut);
        Intent it = this.getIntent();
        Bundle bd = it.getExtras();
        tip.setText(bd.get("userid").toString()+"登陆成功");
        but.setOnClickListener(new View.OnClickListener(){
                                   @Override
                                   public void onClick(View v) {
                                       Intent back = new Intent(SuccessActivity.this,MainActivity.class);
                                       startActivity(back);
                                   }
                               }
        );
    }

}
