package com.example.dawn.appdesign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordActivity extends Activity {

    static final String TAG = "RecordList";
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        listView = findViewById(R.id.listView);
        dealList();

    }

    @Override
    protected void onResume() {
        dealList();
        super.onResume();
    }

    private void dealList(){
        final String[] params = getRecord(); //获取最新记录
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,params);
        listView.setAdapter(adapter);  //显示记录

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"选择"+params[position]);
                Intent toDetail = new Intent(RecordActivity.this,RecordDetailActivity.class);
                toDetail.putExtra("date",params[position]);
                startActivity(toDetail);
            }
        });

        if(params.length==0){
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前无记录")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent it = new Intent(RecordActivity.this,MainActivity.class);
                            startActivity(it);
                        }
                    })
                    .create();
            alertDialog2.show();
        }
    }

    private String[] getRecord(){
        SharedPreferences sharedPreferences = getSharedPreferences("GameData",MODE_PRIVATE);
//        String record=sharedPreferences.getString("name","");
        Map<String,?> map;
        map = sharedPreferences.getAll();
        List<String> tmp = new ArrayList<String>();
        for(Map.Entry<String,?> cur:map.entrySet()){
            String name = cur.getKey();
            if(name.length()<=21)
                tmp.add(name);
        }
        String[] result = new String[tmp.size()];
        tmp.toArray(result);
        return result;
    }
}
