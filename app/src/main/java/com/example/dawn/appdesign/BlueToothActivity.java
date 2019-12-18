package com.example.dawn.appdesign;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BlueToothActivity extends Activity {

    private final String TAG = "blueTooth";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BluetoothAdapter btAdapt ;
    private List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
    private List<String> listDeviceName = new ArrayList<String>();
//    private boolean bIsConnected;
//    private BluetoothDevice myBluetooth;
//    private BluetoothGatt mBluetoothGatt;

    private ListView btList;
    ArrayAdapter<String> adapter;
    private Button search_button;
    private TextView condition_view;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth2);
        search_button = findViewById(R.id.search_button);//linear Layout
        btList = findViewById(R.id.btList);
        condition_view = findViewById(R.id.condition_view);

        btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能
        btAdapt.enable(); // 强行打开 非Demo不建议使用
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
//        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(searchDevicesBroadcast,filter);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Android M Permission check
                    if (BlueToothActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }else{
                        if(btAdapt.startDiscovery()){
                            condition_view.setText("正在搜索");
                        }
                    }
                }
            }
        });

        btList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(BlueToothActivity.this,ConnectActivity.class);
                String[] params = adapter.getItem(position).split("\n");
                it.putExtra("name",params[0]);
                it.putExtra("mac",params[1]);
                startActivity(it);
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        btAdapt.cancelDiscovery();
        condition_view.setText("搜索停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(searchDevicesBroadcast);
    }

    @Override
    public void onBackPressed(){
        btAdapt.cancelDiscovery();
        condition_view.setText("搜索停止");
        Intent it = new Intent(BlueToothActivity.this,BtBridgeActivity.class);
        startActivity(it);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(btAdapt.startDiscovery()){
                        condition_view.setText("正在搜索");
                    }
                }
                break;
        }
    }

    private BroadcastReceiver searchDevicesBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (TextUtils.isEmpty(device.getName())) {
//                return;
//            }
            String str = device.getName() + "\n" + device.getAddress();
            if (listDeviceName.indexOf(str) == -1) {// 防止重复添加
                listDevices.add(device); // 获取设备名称和mac地址
                listDeviceName.add(str);
                dealList();
            }
        }
    };

    private void dealList(){
        String [] devices = new String[listDevices.size()+1];
        int i = 0;
        for(BluetoothDevice device:listDevices){
            // 把名字和地址取出来添加到适配器中
            devices[i]=(device.getName()+"\n"+ device.getAddress());
            i++;
        }
        devices[i]="测试数据\n不对应设备";
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,devices);
        btList.setAdapter(adapter);  //显示记录
    }

    }
