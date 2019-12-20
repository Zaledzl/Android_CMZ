package com.example.dawn.appdesign;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dawn.appdesign.util.ApplicationRecorder;

public class BtBridgeActivity extends Activity {

    private final String TAG = "BtBridge";
    private Button find_new_button;
    private ListView bond_devices;
    private TextView recent_mac;

    ListAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_bridge);

        find_new_button=findViewById(R.id.find_new_button);
        bond_devices=findViewById(R.id.bond_devices);
        recent_mac=findViewById(R.id.recent_mac);

        dealList();

        find_new_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(BtBridgeActivity.this,BlueToothActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dealList();
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(BtBridgeActivity.this,MainActivity.class);
        startActivity(it);
    }

    private void dealList(){
        SharedPreferences sharedPreferences = getSharedPreferences("GameData",MODE_PRIVATE);
        String recentMac = sharedPreferences.getString("recentMac","暂无记录");
        recent_mac.setText("最近连接的蓝牙地址:"+recentMac);

        ApplicationRecorder app = (ApplicationRecorder)getApplication();
        String p1_head = app.getP1_2_head()==null?"尚未配对":"配对完成";
        String p1_body = app.getP1_2_body()==null?"尚未配对":"配对完成";
        String p2_head = app.getP2_2_head()==null?"尚未配对":"配对完成";
        String p2_body = app.getP2_2_body()==null?"尚未配对":"配对完成";
        String [] params = new String[4];
        params[0] = "玩家1头部下位机"+"\n"+p1_head;
        params[1] = "玩家1身体下位机"+"\n"+p1_body;
        params[2] = "玩家2头部下位机"+"\n"+p2_head;
        params[3] = "玩家2身体下位机"+"\n"+p2_body;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,params);
        bond_devices.setAdapter(adapter);
    }

}
