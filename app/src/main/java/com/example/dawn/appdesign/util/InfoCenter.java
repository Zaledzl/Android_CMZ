package com.example.dawn.appdesign.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public  class InfoCenter {
    public static final String TAG = "infoCenter";

    static final String testConnectCode = "1";
    static final String testHeartCode = "2";
    static final String testBeatCode = "3";

    public static final String p1_head_code = "f628f628000000040000A5A5A5A5";
    public static final String p1_body_code = "f628f628000000050000A5A5A5A5";
    public static final String p2_head_code = "f628f628000000060000A5A5A5A5";
    public static final String p2_body_code = "f628f628000000070000A5A5A5A5";


    private static StringBuilder message = new StringBuilder();
    private static List<Byte> list = new ArrayList<>();


    public static HashMap<String,String> dealMessageTest(byte[] para){
        HashMap<String,String> map = new HashMap<>();
        String message = asciiToString(para);
        if(testConnectCode.equals(message)){
            map.put("name","通信测试设备");
            map.put("action","下位机对码");
        }else if(testHeartCode.equals(message)){
            map.put("name","通信测试设备");
            map.put("action","心跳码");
        }else if(testBeatCode.equals(message)){
            map.put("name","通信测试设备");
            map.put("action","打击码");
        }else{
            map.put("name","非有效设备");
        }
        return map;
    }
    public static HashMap<String,String> messageBuffConnect(byte[] data,String part){
        String test = bytesToString(data);
        Log.v(TAG,"缓冲池记录:"+test);
        HashMap<String,String> map = new HashMap<String,String>();
        if(data.length==15){
            return dealMessageConnect(data,part);
        }
        if(data.length==20){
            for(byte cur:data){
                list.add(cur);
            }
            map.put("result","不处理");
            map.put("reason","部分信息");
            return map;
        }
        if(data.length==6){
            byte[] rowData = new byte[26];
            for(int i=0;i<20;i++){
                rowData[i]=list.get(i);
            }
            for(int j=0;j<6;j++){
                rowData[20+j]=data[j];
            }
            list.clear();
            return dealMessageConnect(rowData,part);
        }
        if(data.length==18){
            byte[] rowData = new byte[58];
            for(int i=0;i<40;i++){
                rowData[i]=list.get(i);
            }
            for(int j=0;j<18;j++){
                rowData[40+j]=data[j];
            }
            list.clear();
            return dealMessageConnect(rowData,part);
        }
        map.put("result","不处理");
        map.put("reason","异常格式");
        list.clear();
        return map;
    }
    public static HashMap<String,String> dealMessageConnect(byte[] rawData,String part){
        HashMap<String,String> map = new HashMap<>(); //返回结果
        String test = bytesToString(rawData);
        Log.v(TAG,"接受信息(完整)"+test);
        if(rawData.length!=26&&rawData.length!=58&&rawData.length!=15){ //实际上经过缓存处理这里多余了
            map.put("result","不处理");
            map.put("reason","异常格式");
            return map;
        }else{
            map.put("result","正常处理");
        }
        byte[] para = deleteNoMeaning(rawData); //去掉最后四个无用字节
        int flag = 0; //0--mac8Address和beater都无效  1--mac8Address有效beater无效  2--两者都有效
        byte[] commend = new byte[2];  //指令两个字节
        byte[] mac8Address = new byte[8];  //mac地址8个字节
        byte[] beater = new byte[2];    //mifare卡存储的写死的数据仅仅取两个字节用以判断击打方
        if(para.length>=20) {
            flag=1;
            if (para.length==22) { //处理后的心跳码都是22(26-4)字节
                for(int i=0;i<8;i++){
                    mac8Address[i]=para[12+i];
                }
            }else{ //不然只可能是8003
                flag=2;
                for(int i=0;i<8;i++){
//                    mac8Address[i]=para[42+i];  58
                    mac8Address[i]=para[44+i];
                }
                beater[0]=para[26];
                beater[1]=para[27];
            }
        }
        commend[0] = para[6];
        commend[1] = para[7];
        String message = bytesToString(para);
        Log.v(TAG,"收到信息 "+message);
        String commendString = bytesToString(commend);
        String mac8AddressString = flag>=1?bytesToString(mac8Address):null;
        String beaterString = flag==2?bytesToString(beater):null;
        //假设p1对应卡存储数据1111  p2对应卡存储数据2222  自己击中自己不做处理  ConnectActivity不需要误触处理
//        if(
//                (beaterString.equals("1111")&&(mac8AddressString.equals(app.getP1_8_body())||mac8AddressString.equals(app.getP1_8_head())))
//                        ||
//                        (beaterString.equals("2222")&&(mac8AddressString.equals(app.getP2_8_body())||mac8AddressString.equals(app.getP2_8_head())))
//                ){
//            map.put("result","不处理");
//            map.put("reason","误触");
//            return map;
//        }else{
//            map.put("result","正常处理");
//        }
        switch(commendString){
            case "8000":  //空码不稳定性太大 先不获取8Mac
                map.put("name","竞赛用蓝牙下位机");
                map.put("action","空码");
                break;
            case "8001":
                //mifare1卡密码A下载结果
                break;
            case "8002":
                //mifare2卡密码B下载结果
                break;
            case "8003":
                //受击码
                map.put("name",part);
                map.put("action","打击码");
                break;
            case "8004":
                map.put("name","p1_head");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8005":
                map.put("name","p1_body");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8006":
                map.put("name","p2_head");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8007":
                map.put("name","p2_body");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8008":
                //p1_head 灵敏度设置结果
                break;
            case "8009":
                //p1_body 灵敏度设置结果
                break;
            case "8010":
                //p2_head 灵敏度设置结果
                break;
            case "8011":
                //p2_body 灵敏度设置结果
                break;
            default :
                map.put("result","不处理");
                map.put("reason","command异常");
                break;
        }
        return map;
    }

    public static HashMap<String,String> dealMessage(byte[] rawData,ApplicationRecorder app){
        HashMap<String,String> map = new HashMap<>(); //返回结果
        if(rawData.length!=26&&rawData.length!=58&&rawData.length!=15){
            map.put("result","不处理");
            map.put("reason","异常格式");
            return map;
        }
        byte[] para = deleteNoMeaning(rawData); //去掉最后四个无用字节
        String p1_8_head = app.getP1_8_head();//注意可能为空
        String p1_8_body = app.getP1_8_body();
        String p2_8_head = app.getP2_8_head();
        String p2_8_body = app.getP2_8_body();
        int flag = 0; //0--mac8Address和beater都无效  1--mac8Address有效beater无效  2--两者都有效
        byte[] commend = new byte[2];  //指令两个字节
        byte[] mac8Address = new byte[8];  //mac地址8个字节
        byte[] beater = new byte[2];    //mifare卡存储的写死的数据仅仅取两个字节用以判断击打方
        if(para.length>=20) {
            flag=1;
            if (para.length==22) { //处理后的心跳码都是22(26-4)字节
                for(int i=0;i<8;i++){
                    mac8Address[i]=para[12+i];
                }
            }else{ //不然只可能是8003
                flag=2;
                for(int i=0;i<8;i++){
                    mac8Address[i]=para[42+i];
                }
                beater[0]=para[26];
                beater[1]=para[27];
            }
        }
        commend[0] = para[6];
        commend[1] = para[7];
//        String message = bytesToString(para);
        String commendString = bytesToString(commend);
        String mac8AddressString = flag>=1?bytesToString(mac8Address):null;
        String beaterString = flag==2?bytesToString(beater):null;
        //假设p1对应卡存储数据1111  p2对应卡存储数据2222  自己击中自己不做处理
        if(
                (beaterString.equals("1111")&&(mac8AddressString.equals(app.getP1_8_body())||mac8AddressString.equals(app.getP1_8_head())))
                ||
                (beaterString.equals("2222")&&(mac8AddressString.equals(app.getP2_8_body())||mac8AddressString.equals(app.getP2_8_head())))
                ){
            map.put("result","不处理");
            map.put("reason","误触");
            return map;
        }else{
            map.put("result","正常处理");
        }
        switch(commendString){
            case "8000":
                map.put("name","竞赛用蓝牙");
                break;
            case "8001":
                //mifare1卡密码A下载结果
                break;
            case "8002":
                //mifare2卡密码B下载结果
                break;
            case "8003":
                //受击码
                if(mac8AddressString.equals(p1_8_head)){
                    map.put("name","p1_head");
                }else if(mac8AddressString.equals(p1_8_body)){
                    map.put("name","p1_body");
                }else if(mac8AddressString.equals(p2_8_head)){
                    map.put("name","p2_head");
                }else if(mac8AddressString.equals(p2_8_body)){
                    map.put("name","p2_body");
                }else{
                    map.put("name","非有效设备");
                }
                map.put("action","打击码");
                break;
            case "8004":
                map.put("name","p1_head");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8005":
                map.put("name","p1_body");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8006":
                map.put("name","p2_head");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8007":
                map.put("name","p2_body");
                map.put("action","心跳码");
                map.put("8mac",mac8AddressString);
                break;
            case "8008":
                //p1_head 灵敏度设置结果
                break;
            case "8009":
                //p1_body 灵敏度设置结果
                break;
            case "8010":
                //p2_head 灵敏度设置结果
                break;
            case "8011":
                //p2_body 灵敏度设置结果
                break;
            default :
                map.put("result","不处理");
                map.put("reason","command异常");
                break;
        }
        return map;
    }

    public static String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
//            sb.append(' ');  这个空格是为了有时候观察方便
        }
        return sb.toString();
    }
    public static String asciiToString(byte[] bytes) {
        char[] buf = new char[bytes.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char) bytes[i];
            sb.append(buf[i]);
        }
        return sb.toString();
    }
    private static byte[] deleteNoMeaning(byte[] para){
        byte[] result = new byte[para.length-4];  //仅仅删除后面四个字节
        for(int i = 0;i<result.length;i++){
            result[i]=para[i];
        }
        return result;
    }
}
