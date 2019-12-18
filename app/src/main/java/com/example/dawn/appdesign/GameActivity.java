package com.example.dawn.appdesign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dawn.appdesign.Service.BluetoothLeService1;
import com.example.dawn.appdesign.Service.BluetoothLeService2;
import com.example.dawn.appdesign.Service.BluetoothLeService3;
import com.example.dawn.appdesign.Service.BluetoothLeService4;
import com.example.dawn.appdesign.sample.BluetoothLeService;
import com.example.dawn.appdesign.util.ApplicationRecorder;
import com.example.dawn.appdesign.util.InfoCenter;
import com.example.dawn.appdesign.util.TimerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class GameActivity extends Activity {

    private final static String TAG = "GameActivity";
    private ApplicationRecorder app ;

    private Button settings_button;
    private Button refresh_button;
    private Button info_button;
    private Button p1_1;
    private Button p1_3;
    private Button p1_5;
    private Button p2_1;
    private Button p2_3;
    private Button p2_5;
    private TextView p1_score_view;
    private TextView p2_score_view;
    private TextView p1_head_connect;
    private TextView p1_body_connect;
    private TextView p2_head_connect;
    private TextView p2_body_connect;
    private TextView p1_head_heart;
    private TextView p1_body_heart;
    private TextView p2_head_heart;
    private TextView p2_body_heart;
    private TextView time_left_view;
    private Button gameStart_button;
    private Button backHome_button;
    private TextView diff_score_view;
    private TextView time_view;
    private TextView mode_view;


    private int diff_score=20;
    private int time_all=60;
    private int p1_score=0;
    private int p2_score=0;

    private StringBuilder sb = new StringBuilder();
    private String p1_info;
    private String p2_info;
    private boolean mode = true;
    private boolean timeIsOut = false;

    CountDownTimer timer;

    private boolean mConnected1 = false;
    private boolean mConnected2 = false;
    private boolean mConnected3 = false;
    private boolean mConnected4 = false;
    private BluetoothLeService1 mBluetoothLeService1;
    private BluetoothLeService2 mBluetoothLeService2;
    private BluetoothLeService3 mBluetoothLeService3;
    private BluetoothLeService4 mBluetoothLeService4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        settings_button = findViewById(R.id.settings_button);
        refresh_button = findViewById(R.id.refresh_button);
        p1_1 = findViewById(R.id.player1_1);
        p1_3 = findViewById(R.id.player1_3);
        p1_5 = findViewById(R.id.player1_5);
        p2_1 = findViewById(R.id.player2_1);
        p2_3 = findViewById(R.id.player2_3);
        p2_5 = findViewById(R.id.player2_5);
        p1_score_view = findViewById(R.id.p1_score);
        p2_score_view = findViewById(R.id.p2_score);
        p1_head_connect = findViewById(R.id.p1_head_connect);
        p1_body_connect = findViewById(R.id.p1_body_connect);
        p2_head_connect = findViewById(R.id.p2_head_connect);
        p2_body_connect = findViewById(R.id.p2_body_connect);
        p1_head_heart = findViewById(R.id.p1_head_heart);
        p1_body_heart = findViewById(R.id.p1_body_heart);
        p2_head_heart = findViewById(R.id.p2_head_heart);
        p2_body_heart = findViewById(R.id.p2_body_heart);
        time_left_view = findViewById(R.id.time);
        gameStart_button = findViewById(R.id.gameStart_button);
        backHome_button = findViewById(R.id.backHome_button);
        diff_score_view = findViewById(R.id.diff_view);
        time_view = findViewById(R.id.time_view);
        mode_view = findViewById(R.id.mode_view);
        refresh_button = findViewById(R.id.refresh_button);
        info_button = findViewById(R.id.info_button);

        app = (ApplicationRecorder)getApplication();

        //首先加载配置

        loadSettings();

        settings_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameActivity.this,SettingActivity.class);
                startActivity(it);
            }
        });
        refresh_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                p1_score=0;
                p2_score=0;
                sb.delete(0,sb.length());
                mode = true;
                refresh(0);
            }
        });
        info_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameActivity.this,InfoActivity.class);
                startActivity(it);
            }
        });

        gameStart_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mode=false;//进入计时模式
                int timeParam = 1000*time_all;
                p1_score=0;
                p2_score=0;
                sb.delete(0,sb.length());
                refresh(0);
                timer = new CountDownTimer(timeParam, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        refresh((int)millisUntilFinished/1000);
                    }

                    @Override
                    public void onFinish() {
                        timeIsOut=true;
                        judge_win();
                    }
                };

                timer.start();
            }
        });
        backHome_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GameActivity.this,MainActivity.class);
                startActivity(it);
            }
        });

        p1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1_score++;
                sb.append("p1+1,");
                judge_win();
            }
        });
        p1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1_score+=3;
                sb.append("p1+3,");
                judge_win();
            }
        });
        p1_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1_score+=5;
                sb.append("p1+5,");
                judge_win();
            }
        });
        p2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p2_score++;
                sb.append("p2+1,");
                judge_win();
            }
        });
        p2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p2_score+=3;
                sb.append("p2+3,");
                judge_win();
            }
        });
        p2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p2_score+=5;
                sb.append("p2+5,");
                judge_win();
            }
        });
    }

    @Override
    protected void onResume() {  //重新注册recever 重新连接蓝牙服务
        super.onResume();
        loadSettings();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService1 != null) {
            final boolean result = mBluetoothLeService1.connect(app.getP1_head());
            Log.d(TAG, "Connect p1_head request result=" + result);
        }
        if (mBluetoothLeService2 != null) {
            final boolean result = mBluetoothLeService2.connect(app.getP1_body());
            Log.d(TAG, "Connect p1_body request result=" + result);
        }
        if (mBluetoothLeService3 != null) {
            final boolean result = mBluetoothLeService3.connect(app.getP2_head());
            Log.d(TAG, "Connect p2_head request result=" + result);
        }
        if (mBluetoothLeService4 != null) {
            final boolean result = mBluetoothLeService4.connect(app.getP2_body());
            Log.d(TAG, "Connect p2_body request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    public void onBackPressed(){
        Intent it = new Intent(GameActivity.this,MainActivity.class);
        startActivity(it);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection1);
        unbindService(mServiceConnection2);
        unbindService(mServiceConnection3);
        unbindService(mServiceConnection4);
        mBluetoothLeService1 = null;
        mBluetoothLeService2 = null;
        mBluetoothLeService3 = null;
        mBluetoothLeService4 = null;
    }


    private void judge_win(){
        refresh(0);
        if(Math.abs(p1_score-p2_score)>=diff_score||(!mode&&timeIsOut)){
            timeIsOut=false;  //首先要重置 时间耗尽标志位
            if(timer!=null) {
                timer.cancel();
            }
            String result=p1_score>p2_score?"p1获胜":"p2获胜";
            AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                    .setTitle("游戏结束")
                    .setMessage(result)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(GameActivity.this, "已重置比赛信息", Toast.LENGTH_SHORT).show();
                            p1_score=0;
                            p2_score=0;
                            mode=true;
                            refresh(0);
                        }
                    })

                    .setNegativeButton("详细记录", new DialogInterface.OnClickListener() {//添加取消
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(GameActivity.this, "此处应展示详细记录", Toast.LENGTH_SHORT).show();
                            String date = getDateString();
                            save(date);
                            Intent toDetail = new Intent(GameActivity.this,RecordDetailActivity.class);
                            toDetail.putExtra("date",date);
                            startActivity(toDetail);
                        }
                    })
                    .setNeutralButton("保存本场记录", new DialogInterface.OnClickListener() {//添加普通按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(GameActivity.this, "此处进行保存", Toast.LENGTH_SHORT).show();
                            save(getDateString());
                            p1_score=0;
                            p2_score=0;
                            mode=true;
                            refresh(0);
                        }
                    })
                    .create();
            alertDialog2.show();
        }
    }

    private void save(String name){//保存当前比赛信息并重置记录stringBuilder
        SharedPreferences sharedPreferences = getSharedPreferences("GameData",MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        sb.deleteCharAt(sb.length()-1);
        editor.putString(name,sb.toString());
        String p1Info = p1_info==null?"无信息记录":p1_info;
        String p2Info = p2_info==null?"无信息记录":p2_info;
        String name1 = name+"p1";
        String name2 = name+"p2";
        editor.putString(name1,p1Info);
        editor.putString(name2,p2Info);
        //步骤4：提交
        editor.commit();
        sb.delete(0,sb.length());
    }

    private void refresh(int stillTime){//更新当前界面  注意refresh函数是不会重置记录字符串的
        p1_score_view.setText(String.valueOf(p1_score));
        p2_score_view.setText(String.valueOf(p2_score));
        diff_score_view.setText("分差:"+diff_score+"分");
        time_view.setText("时间:"+time_all+"s");
        if(!mode) {
            mode_view.setText("计时模式");
            if(stillTime!=0)
                time_left_view.setText(String.valueOf(stillTime)+"s");
        }else{
            mode_view.setText("不计时模式");
            time_left_view.setText("inf");
        }
    }

    private String getDateString(){//获取当前系统时间(用做记录保存的键值)
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
        String str = formatter.format(curDate);
        return str;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String name = intent.getStringExtra("name");
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                switch(name){
                    case "p1_head":
                        mConnected1=false;
                        p1_head_connect.setBackgroundColor(Color.parseColor("#FF0000"));
                        mBluetoothLeService1.connect(app.getP1_head());
                        break;
                    case "p1_body":
                        mConnected2=false;
                        p1_body_connect.setBackgroundColor(Color.parseColor("#FF0000"));
                        mBluetoothLeService2.connect(app.getP1_body());
                        break;
                    case "p2_head":
                        mConnected3=false;
                        p2_head_connect.setBackgroundColor(Color.parseColor("#FF0000"));
                        mBluetoothLeService3.connect(app.getP2_head());
                        break;
                    case "p2_body":
                        mConnected4=false;
                        p2_body_connect.setBackgroundColor(Color.parseColor("#FF0000"));
                        mBluetoothLeService4.connect(app.getP2_body());
                        break;
                }
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //特征值找到才代表连接成功
                switch(name){
                    case "p1_head":
                        p1_head_connect.setBackgroundColor(Color.parseColor("#00FF00"));
                        mConnected1=true;
                        break;
                    case "p1_body":
                        p1_body_connect.setBackgroundColor(Color.parseColor("#00FF00"));
                        mConnected2=true;
                        break;
                    case "p2_head":
                        p2_head_connect.setBackgroundColor(Color.parseColor("#00FF00"));
                        mConnected3=true;
                        break;
                    case "p2_body":
                        p2_body_connect.setBackgroundColor(Color.parseColor("#00FF00"));
                        mConnected4=true;
                        break;
                }
//                mConnected = true;
//                updateConnectionState(R.string.connected);
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
                switch(name){
                    case "p1_head":
                        mBluetoothLeService1.connect(app.getP1_head());
                        break;
                    case "p1_body":
                        mBluetoothLeService2.connect(app.getP1_body());
                        break;
                    case "p2_head":
                        mBluetoothLeService3.connect(app.getP2_head());
                        break;
                    case "p2_body":
                        mBluetoothLeService4.connect(app.getP2_body());
                        break;
                }
//                mBluetoothLeService.connect(mDeviceAddress);
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//                final StringBuilder stringBuilder = new StringBuilder();
//                 for(byte byteChar : data)
//                      stringBuilder.append(String.format("%02X ", byteChar));
//                Log.v("log",stringBuilder.toString());
                HashMap<String,String> map = InfoCenter.dealMessage(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA),app);
                dealMessage(map);
            }
//            }else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
//                mSendBytes.setText(sendBytes + " ");
//                if (sendDataLen>0)
//                {
//                    Log.v("log","Write OK,Send again");
//                    onSendBtnClicked();
//                }
//                else {
//                    Log.v("log","Write Finish");
//                }
//            }

        }
    };

    private void dealMessage(HashMap<String,String> map){
        TimerUtil TU1 = new TimerUtil(); //以防万一多搞几个
        TimerUtil TU2 = new TimerUtil();
        TimerUtil TU3 = new TimerUtil();
        TimerUtil TU4 = new TimerUtil();
        String result = map.get("result");
        if(result==null||result.equals("不处理")){
            return;
        }
        String name = map.get("name");
        String action = map.get("action");
//        if("通信测试设备".equals(name)){
//            if("心跳码".equals(action)){
//                TU.dealColorView(p1_head_heart,1000);
//            }else if("打击码".equals(action)){
//                p1_score+=5; //等同于调用回调函数
//                sb.append("p1+5,");
//                judge_win();
//            }
//        }
        switch(name){
            case "竞赛用蓝牙": //讲道理 已经对完码了这里不应该出现8000
                Log.v(TAG,"GameActivity出现了不该出现的对码");
                break;
            case "p1_head":
                if(action.equals("受击码")) {
                    p2_score += 5;
                    sb.append("p1+5,");
                    judge_win();
                }else{
                    TU1.dealColorView(p1_head_heart,1000);
                }
                break;
            case "p1_body":
                if (action.equals("受击码")) {
                    p2_score+=3;
                    sb.append("p2+5,");
                    judge_win();
                }else{
                    TU2.dealColorView(p1_body_heart,1000);
                }
                break;
            case "p2_head":
                if (action.equals("受击码")) {
                    p1_score+=5;
                    sb.append("p1+5,");
                    judge_win();
                }else{
                    TU3.dealColorView(p2_head_heart,1000);
                }
            case "p2_body":
                if (action.equals("受击码")) {
                    p1_score+=3;
                    sb.append("p1+3,");
                    judge_win();
                }else{
                    TU4.dealColorView(p2_body_connect,1000);
                }
                break;
            default:
                Log.v(TAG,"受击部位信息异常?");
                break;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                connectConfirm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectConfirm(){

        int setDeviceMacNumber = 0;
        if(app.getP1_head()!=null) setDeviceMacNumber++;
        if(app.getP1_body()!=null) setDeviceMacNumber++;
        if(app.getP2_head()!=null) setDeviceMacNumber++;
        if(app.getP2_body()!=null) setDeviceMacNumber++;

        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("提示");
        builder.setMessage("检测到已设置"+setDeviceMacNumber+"个下位机地址"+"\n"+"确认连接?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(app.getP1_head()!=null) {
                    Intent gattServiceIntent1 = new Intent(GameActivity.this, BluetoothLeService1.class);
                    bindService(gattServiceIntent1, mServiceConnection1, BIND_AUTO_CREATE);
                }
                if (app.getP1_body()!=null) {
                    Intent gattServiceIntent2 = new Intent(GameActivity.this, BluetoothLeService2.class);
                    bindService(gattServiceIntent2, mServiceConnection2, BIND_AUTO_CREATE);
                }
                if (app.getP2_head()!=null) {
                    Intent gattServiceIntent3 = new Intent(GameActivity.this, BluetoothLeService3.class);
                    bindService(gattServiceIntent3, mServiceConnection3, BIND_AUTO_CREATE);
                }
                if (app.getP2_body()!=null) {
                    Intent gattServiceIntent4 = new Intent(GameActivity.this, BluetoothLeService4.class);
                    bindService(gattServiceIntent4, mServiceConnection4, BIND_AUTO_CREATE);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void loadSettings(){
        app=(ApplicationRecorder)getApplication();
        if(!app.isSet()){
            ToastUtil("请先进行登记设置!");
        }else{
            p1_info = "p1姓名:"+app.getP1_name()+" 体重:"+app.getP1_weight();
            p2_info = "p2姓名:"+app.getP2_name()+" 体重:"+app.getP2_weight();
            diff_score = app.getDiff_score();
            time_all = app.getTime_all();
            diff_score_view.setText("分差:"+String.valueOf(diff_score));
            time_view.setText("时间:"+String.valueOf(time_all));
        }
    }

    private void ToastUtil(String para){
        Toast toast=Toast.makeText(GameActivity.this,para,Toast.LENGTH_SHORT);
        toast.show();
    }

    //严重的代码重复  应该写一个获取 ServiceConnection的工具类
    private final ServiceConnection mServiceConnection1 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService1 = ((BluetoothLeService1.LocalBinder) service).getService();
            if (!mBluetoothLeService1.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            String p1HeadAddress = app.getP1_head();
            mBluetoothLeService1.connect(p1HeadAddress);
        }

        @Override
            public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService1 = null;
        }
    };

    private final ServiceConnection mServiceConnection2 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService2 = ((BluetoothLeService2.LocalBinder) service).getService();
            if (!mBluetoothLeService2.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            String p1BodyAddress = app.getP1_body();
            mBluetoothLeService2.connect(p1BodyAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService2 = null;
        }
    };

    private final ServiceConnection mServiceConnection3 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService3 = ((BluetoothLeService3.LocalBinder) service).getService();
            if (!mBluetoothLeService3.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            String p2HeadAddress = app.getP2_head();
            mBluetoothLeService3.connect(p2HeadAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService3 = null;
        }
    };

    private final ServiceConnection mServiceConnection4 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService4 = ((BluetoothLeService4.LocalBinder) service).getService();
            if (!mBluetoothLeService4.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            String p2BodyAddress = app.getP2_body();
            mBluetoothLeService4.connect(p2BodyAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService4 = null;
        }
    };
}
