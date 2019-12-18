package com.example.dawn.appdesign;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dawn.appdesign.Service.BluetoothLeTestService;
import com.example.dawn.appdesign.sample.BluetoothLeService;
import com.example.dawn.appdesign.util.ApplicationRecorder;
import com.example.dawn.appdesign.util.InfoCenter;
import com.example.dawn.appdesign.util.TimerUtil;

import java.util.HashMap;

public class ConnectActivity extends Activity {

    private final String TAG = "connect";

    private TextView connect_name;
    private TextView connect_mac;
    private TextView connect_result;
    private TextView connect_heart;
    private TextView connect_beat;
    private Button connect_back;
    private Button connect_test;
    private Button p1_head_button;
    private Button p1_body_button;
    private Button p2_head_button;
    private Button p2_body_button;
    private Button connect_confirm;


    String deviceName;
    String deviceMac;
    String device8Mac;
    String flag;  //连接测试部位
    String flagCode;    //该部位的对码

    private BluetoothLeTestService mBluetoothLeTestService;
    private boolean mConnected = false;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeTestService = ((BluetoothLeTestService.LocalBinder) service).getService();
            if (!mBluetoothLeTestService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeTestService.connect(deviceMac);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeTestService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        FindView();//寻找组件对应实现
        ButtonSet();//按钮监听设置

//        p1_head_button.setEnabled(false);
//        p1_body_button.setEnabled(false);
//        p2_head_button.setEnabled(false);
//        p2_body_button.setEnabled(false);

        Intent it = this.getIntent();
        Bundle bd = it.getExtras();
        deviceName = (String)bd.get("name");
        deviceMac = (String)bd.get("mac");
        connect_name.setText(deviceName);
        connect_mac.setText(deviceMac);

        Intent gattServiceIntent = new Intent(this, BluetoothLeTestService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
//                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();  //刷新菜单栏
                mBluetoothLeTestService.connect(deviceMac);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //特征值找到才代表连接成功
                mConnected = true;
                invalidateOptionsMenu();
//                updateConnectionState(R.string.connected);
            }else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)){
                mBluetoothLeTestService.connect(deviceMac);
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//                final StringBuilder stringBuilder = new StringBuilder();
//                 for(byte byteChar : data)
//                      stringBuilder.append(String.format("%02X ", byteChar));
//                Log.v("log",stringBuilder.toString());

//                displayData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if(flag!=null) {
                    HashMap<String, String> map = InfoCenter.messageBuffConnect(data, flag);
                    dealMessage(map);
                }
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

    @Override
    protected void onResume() {  //重新注册recever 重新连接蓝牙服务
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeTestService != null) {
            final boolean result = mBluetoothLeTestService.connect(deviceMac);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothLeTestService.disconnect();
        mBluetoothLeTestService = null;
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeTestService.disconnect();
        unbindService(mServiceConnection);
        mBluetoothLeTestService = null;
    }
    @Override
    public void onBackPressed(){
        Intent it = new Intent(ConnectActivity.this,BtBridgeActivity.class);
        startActivity(it);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeTestService.connect(deviceMac);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeTestService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeTestService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeTestService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeTestService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeTestService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeTestService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothLeTestService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    private void dealMessage(HashMap<String,String> map){
        TimerUtil TU = new TimerUtil();
        if(map.get("result").equals("不处理")){
            return;
        }
        //因为下位机受到对码之后可能会继续发送几条空码的缘故 会出现"下位机"和"xx部位"进行波动 然后才稳定为"xx部位"
        connect_result.setText("检测到该设备为:"+map.get("name"));

        String action = map.get("action");
        if(action.equals("心跳码")){
            TU.dealTextView(connect_heart,"检测到心跳","心跳测试",1000);
            if(map.get("8mac")!=null){
                device8Mac = map.get("8mac");
            }
        }else if(action.equals("打击码")){
            TU.dealTextView(connect_beat,"检测到打击","打击测试",1000);
        }else if(action.equals("空码")){
            sendData(flagCode);
        }
    }

    private void ButtonSet(){

        final ApplicationRecorder app = (ApplicationRecorder) getApplication();

        connect_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ConnectActivity.this,BlueToothActivity.class);
                startActivity(it);
            }
        });
        connect_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeTestService.connect(deviceMac);
                ToastUtil("请继续选择开启的下位机部位");
            }
        });
        p1_head_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                flag = "p1_head";
                flagCode=InfoCenter.p1_head_code;
                sendData(flagCode);
                ToastUtil("已发送P1_head对码");
            }
        });
        p1_body_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "p1_body";
                flagCode=InfoCenter.p1_body_code;
                sendData(flagCode);
                ToastUtil("已发送p1_body对码");
            }
        });
        p2_head_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "p2_head";
                flagCode = InfoCenter.p2_head_code;
                sendData(InfoCenter.p2_head_code);
                ToastUtil("已发送p2_head对码");
            }
        });
        p2_body_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "p2_body";
                flagCode = InfoCenter.p2_body_code;
                sendData(InfoCenter.p2_body_code);
                ToastUtil("已发送p2_body对码");
            }
        });
        connect_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(flag==null||device8Mac==null){
                    Toast toast = Toast.makeText(ConnectActivity.this,"请确认设置位置并收到心跳码",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                switch(flag){
                    case "p1_head":
                        app.setP1_head(deviceMac);
                        app.setP1_8_head(device8Mac);
                        break;
                    case "p1_body":
                        app.setP1_body(deviceMac);
                        app.setP1_8_body(device8Mac);
                        break;
                    case "p2_head":
                        app.setP2_head(deviceMac);
                        app.setP2_8_head(device8Mac);
                        break;
                    case "p2_body":
                        app.setP2_body(deviceMac);
                        app.setP2_8_body(device8Mac);
                        break;

                }
            }
        });
    }
    private void sendData(String para){
        byte[] sendBuf = stringToBytes(para);
        mBluetoothLeTestService.writeData(sendBuf);
    }
    private void FindView(){
        connect_name=findViewById(R.id.connect_name);
        connect_mac=findViewById(R.id.connect_mac);
        connect_result=findViewById(R.id.connect_result);
        connect_heart=findViewById(R.id.connect_heart);
        connect_beat=findViewById(R.id.connect_beat);
        connect_back=findViewById(R.id.connect_back);
        connect_test=findViewById(R.id.connect_test);
        p1_head_button=findViewById(R.id.p1_head_button);
        p1_body_button=findViewById(R.id.p1_body_button);
        p2_head_button=findViewById(R.id.p2_head_button);
        p2_body_button=findViewById(R.id.p2_body_button);
        connect_confirm=findViewById(R.id.connect_confirm);
    }

    private void ToastUtil(String para){
        Toast toast=Toast.makeText(ConnectActivity.this,para,Toast.LENGTH_SHORT);
        toast.show();
    }

    private byte[] stringToBytes(String s) {
        byte[] buf = new byte[s.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            try {
                buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }
}
