package com.example.dawn.appdesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dawn.appdesign.util.ApplicationRecorder;

public class SettingActivity extends Activity {

    private EditText diff_score_edit;
    private EditText time_all_edit;
    private EditText p1_name_edit;
    private EditText p1_weight_edit;
    private EditText p2_name_edit;
    private EditText p2_weight_edit;
    private Button back_button;

    ApplicationRecorder app = (ApplicationRecorder)getApplication();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        diff_score_edit=findViewById(R.id.diff_setting);
        time_all_edit=findViewById(R.id.time_setting);
        p1_name_edit=findViewById(R.id.p1_name_setting);
        p1_weight_edit=findViewById(R.id.p1_weight_setting);
        p2_name_edit=findViewById(R.id.p2_name_setting);
        p2_weight_edit=findViewById(R.id.p2_weight_setting);
        back_button=findViewById(R.id.confirm_setting);

        if(app.getP1_name()!=null)p1_name_edit.setText(app.getP1_name());
        if(app.getP1_weight()!=null)p1_weight_edit.setText(app.getP1_weight());
        if(app.getP2_name()!=null)p2_name_edit.setText(app.getP2_name());
        if(app.getP2_weight()!=null)p2_weight_edit.setText(app.getP2_weight());
        diff_score_edit.setText(String.valueOf(app.getDiff_score()));
        time_all_edit.setText(String.valueOf(app.getTime_all()));

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int diff_score = Integer.valueOf(diff_score_edit.getText().toString());
                int time_all = Integer.valueOf(time_all_edit.getText().toString());
                String p1_name = p1_name_edit.getText().toString();
                String p2_name = p2_name_edit.getText().toString();
                String p1_weight = p1_weight_edit.getText().toString();
                String p2_weight = p2_weight_edit.getText().toString();

                app.setDiff_score(diff_score);
                app.setTime_all(time_all);
                app.setP1_name(p1_name);
                app.setP1_weight(p1_weight);
                app.setP2_name(p2_name);
                app.setP2_weight(p2_weight);
                app.setSet(true);

                Intent it = new Intent(SettingActivity.this,GameActivity.class);
                startActivity(it);
            }
        });
    }
}
