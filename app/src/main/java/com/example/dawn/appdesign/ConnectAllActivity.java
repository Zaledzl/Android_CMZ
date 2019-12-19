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

public class ConnectAllActivity extends Activity {

    private final String TAG = "connect";

    private TextView connectAll_name;
    private TextView connectAll_mac;
    private Button connectAll_back;
    private Button connectAll_test;
    private TextView connectAll_voidCode;
    private TextView connectAll_p1head_heart;
    private TextView connectAll_p1body_heart;
    private TextView connectAll_p2head_heart;
    private TextView connectAll_p2body_heart;
    private TextView connectAll_p1head_beat;
    private TextView connectAll_p1body_beat;
    private TextView connectAll_p2head_beat;
    private TextView connectAll_p2body_beat;
    private Button connectAll_p1head_button;
    private Button connectAll_p1body_button;
    private Button connectAll_p2head_button;
    private Button connectAll_p2body_button;
    private Button connectAll_confirm;


    String deviceName;
    String deviceMac;
    String device8Mac;


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

        Intent it = this.getIntent();
        Bundle bd = it.getExtras();
        deviceName = (String)bd.get("name");
        deviceMac = (String)bd.get("mac");
        connectAll_name.setText(deviceName);
        connectAll_mac.setText(deviceMac);

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
                ApplicationRecorder app = (ApplicationRecorder)getApplication();
                HashMap<String, String> map = InfoCenter.messageBuff(data,app,0);
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
        unbindService(mServiceConnection);
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
        Intent it = new Intent(ConnectAllActivity.this,BtBridgeActivity.class);
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
        TimerUtil TU = new TimerUtil();  //也不知道一个timer顶不顶得住
        if(map.get("result").equals("不处理")){
            return;
        }
        String name = map.get("name");
        String action = map.get("action");
        if(action.equals("心跳码")){
            switch (name){
                case "p1_head":
                    TU.dealColorView(connectAll_p1head_heart,1000);
                    break;
                case "p1_body":
                    TU.dealColorView(connectAll_p1body_heart,1000);
                    break;
                case "p2_head":
                    TU.dealColorView(connectAll_p2head_heart,1000);
                    break;
                case "p2_body":
                    TU.dealColorView(connectAll_p2body_heart,1000);
                    break;
                default:
                    Log.v(TAG,"检测到无法确定来源的心跳码");
                    break;
            }
        }else if(action.equals("打击码")){
            switch (name){
                case "p1_head":
                    TU.dealColorView(connectAll_p1head_beat,1000);
                    break;
                case "p1_body":
                    TU.dealColorView(connectAll_p1body_beat,1000);
                    break;
                case "p2_head":
                    TU.dealColorView(connectAll_p2head_beat,1000);
                    break;
                case "p2_body":
                    TU.dealColorView(connectAll_p2body_beat,1000);
                    break;

                default:
                    Log.v(TAG,"检测到无法确定来源的击打码");
                    break;
            }
        }else if(action.equals("空码")){
            TU.dealColorView(connectAll_voidCode,1000);
        }
    }

    private void ButtonSet(){

        final ApplicationRecorder app = (ApplicationRecorder) getApplication();

        connectAll_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ConnectAllActivity.this,BlueToothActivity.class);
                startActivity(it);
            }
        });
        connectAll_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeTestService.connect(deviceMac);
                ToastUtil("请继续选择开启的下位机部位");
            }
        });
        connectAll_p1head_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendData(InfoCenter.p1_head_code);
                ToastUtil("已发送P1_head对码");
            }
        });
        connectAll_p1body_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(InfoCenter.p1_body_code);
                ToastUtil("已发送p1_body对码");
            }
        });
        connectAll_p2head_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(InfoCenter.p2_head_code);
                ToastUtil("已发送p2_head对码");
            }
        });
        connectAll_p2body_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(InfoCenter.p2_body_code);
                ToastUtil("已发送p2_body对码");
            }
        });
        connectAll_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int deviceNumber = 0;
                if(app.getP1_8_head()!=null) deviceNumber++;
                if(app.getP1_8_body()!=null) deviceNumber++;
                if(app.getP2_8_head()!=null) deviceNumber++;
                if(app.getP2_8_body()!=null) deviceNumber++;

                Toast toast = Toast.makeText(ConnectAllActivity.this,"完成"+deviceNumber+"个下位机的对码",Toast.LENGTH_SHORT);
                toast.show();

                app.setBluetoothMac(deviceMac);

                Intent it = new Intent(ConnectAllActivity.this,BtBridgeActivity.class);
                startActivity(it);
            }
        });
    }
    private void sendData(String para){
        byte[] sendBuf = stringToBytes(para);
        mBluetoothLeTestService.writeData(sendBuf);
    }
    private void FindView(){
        connectAll_name=findViewById(R.id.connectAll_name);
        connectAll_mac=findViewById(R.id.connectAll_mac);
        connectAll_back=findViewById(R.id.connectAll_back);
        connectAll_test=findViewById(R.id.connectAll_test);
        connectAll_voidCode=findViewById(R.id.connectAll_void_code);
        connectAll_p1head_heart=findViewById(R.id.connectAll_p1head_heart);
        connectAll_p1body_heart=findViewById(R.id.connectAll_p1body_heart);
        connectAll_p2head_heart=findViewById(R.id.connectAll_p2head_heart);
        connectAll_p2body_heart=findViewById(R.id.connectAll_p2body_heart);
        connectAll_p1head_beat=findViewById(R.id.connectAll_p1head_beat);
        connectAll_p1body_beat=findViewById(R.id.connectAll_p1body_beat);
        connectAll_p2head_beat=findViewById(R.id.connectAll_p2head_beat);
        connectAll_p2body_beat=findViewById(R.id.connectAll_p2body_beat);
        connectAll_p1head_button=findViewById(R.id.connectAll_p1head_button);
        connectAll_p1body_button=findViewById(R.id.connectAll_p1body_button);
        connectAll_p2head_button=findViewById(R.id.connectAll_p2head_button);
        connectAll_p2body_button=findViewById(R.id.connectAll_p2body_button);
        connectAll_confirm=findViewById(R.id.connectAll_confirm);
    }

    private void ToastUtil(String para){
        Toast toast=Toast.makeText(ConnectAllActivity.this,para,Toast.LENGTH_SHORT);
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
