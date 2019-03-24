package com.mhd.superwifidirect.Service;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mhd.superwifidirect.Activity.MainActivity;
import com.mhd.superwifidirect.Bean.BroadcastObject;
import com.mhd.superwifidirect.Bean.DeviceInfo;
import com.mhd.superwifidirect.Bean.EventMessage_UdpToActivity;
import com.mhd.superwifidirect.Bean.UdpDatagramParam;
import com.mhd.superwifidirect.Task.Task_SendUdpDatagram;
import com.mhd.superwifidirect.Util.ByteUtil;
import com.mhd.superwifidirect.Util.LocationUtils;
import com.mhd.superwifidirect.Util.NetUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

/*
*
* 1.监听从GO发送过来的GroupMemInfoList
* 2.定期向GO发送（为什么不用广播？为了节省系统的网络开销。）
* 3.刚加入组群的时候，向GO汇报自己的IP地址
* 4.收到GC汇报的IP地址后，回复ACK
* */

public class NetworkService extends Service {

    private static final String TAG="NetworkService";
    private static final int UDP_PORT=34567;
    private static final int TCP_PORT=34657;
    private static final int BUF_SIZE=1024;
    private final Gson mGson=new GsonBuilder().serializeNulls().setLenient().create();
    private NetworkServiceBinder networkServiceBinder=new NetworkServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"网络服务已经启动！");
//        //启动两个线程，分别监听UDP和TCP的报文、连接
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                DatagramSocket udpReceiveSocket = null;
//                byte[]buf=new byte[512];
//                try {
//                    udpReceiveSocket=new DatagramSocket(UDP_PORT);
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//                DatagramPacket packet=new DatagramPacket(buf,512);
//                while(true){
//                    try {
//                        assert udpReceiveSocket != null;
//                        udpReceiveSocket.receive(packet);
//
//                        //TODO udp报文比较短，暂时没有用多并发
//                        Log.e(TAG,"卧槽，居然收到了！"+packet.toString());
//                        //通过Eventbus向Activity传送收到的数据（整理好）
//                        BroadcastObject broadcastObjectToSend=new String()
//
//
//
//
//
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//
//
//
//            }
//        }).start();
//        //监听UDP包
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
//        //监听TCP连接

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"网络服务已经启动！");
        //启动两个线程，分别监听UDP和TCP的报文、连接
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"UDP监听线程启动");
                DatagramSocket udpReceiveSocket = null;
                byte[]buf=new byte[BUF_SIZE];
                try {
                    udpReceiveSocket=new DatagramSocket(UDP_PORT);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                DatagramPacket packet=new DatagramPacket(buf,BUF_SIZE);
                while(true){
                    try {
                        //这里，如果buf不清零会导致json字符串后面有多的东西。
                        for(int i=0;i<BUF_SIZE;i++){
                            buf[i]=0;
                        }
                        //TODO 这里效率不好，但是时间紧急。

                        assert udpReceiveSocket != null;
                        udpReceiveSocket.receive(packet);
                        Log.e(TAG,"卧槽，居然收到了！"+packet.toString());
                        Log.e(TAG,new String(buf));
                        Log.d(TAG,"收到的东西是："+ByteUtil.byteToStr(buf));

                        //通过Eventbus向Activity传送收到的数据（整理好）

                        BroadcastObject broadcastObjectToSend=mGson.fromJson(ByteUtil.byteToStr(buf),BroadcastObject.class);
                        if (broadcastObjectToSend!=null) {
                            EventBus.getDefault().post(new EventMessage_UdpToActivity(broadcastObjectToSend));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //监听UDP包






        new Thread(new Runnable() {
            @Override
            public void run() {









            }
        }).start();
        //监听TCP连接




        return networkServiceBinder;
    }

    //静态内部类不会隐式持有外部类的引用
    public class NetworkServiceBinder extends Binder {


        //应该是定时广播
        /*广播自己的位置和IP*/
        public boolean broadcastSelfIpAndLocation(){
            Log.d(TAG,"准备发送广播报文");
            //用json发送


            String jsonString=mGson.toJson(new BroadcastObject(MainActivity.getSelfDevice().deviceAddress,NetUtil.getSelfInetAddress(getApplicationContext()),null));

            InetAddress broadcastAddress= null;
//            try {
//                broadcastAddress = NetUtil.getBroadcastAddress(getApplicationContext());
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
            try {
                broadcastAddress=InetAddress.getByName("192.168.49.255");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"准备发送广播报文1:"+jsonString);
            Log.d(TAG,"发送报文的广播地址为："+broadcastAddress.toString());
            Log.d(TAG,"准备发送报文的端口为:"+UDP_PORT);
            UdpDatagramParam udpDatagramParam=new UdpDatagramParam(jsonString.getBytes(),broadcastAddress,UDP_PORT);
            Task_SendUdpDatagram task_sendUdpDatagram=new Task_SendUdpDatagram();
            task_sendUdpDatagram.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,udpDatagramParam);
            return true;//这个现在还没有意义
        }

        public boolean broadcastSelfIpAndLocation(BDLocation location){
            Log.d(TAG,"准备发送广播报文");
            //用json发送

            String jsonString=mGson.toJson(new BroadcastObject(MainActivity.getSelfDevice().deviceAddress,NetUtil.getSelfInetAddress(getApplicationContext()),location));

            InetAddress broadcastAddress= null;
//            try {
//                broadcastAddress = NetUtil.getBroadcastAddress(getApplicationContext());
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
            try {
                broadcastAddress=InetAddress.getByName("192.168.49.255");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"准备发送广播报文2:"+jsonString);
            Log.d(TAG,"发送报文的广播地址为："+broadcastAddress.toString());
            Log.d(TAG,"准备发送报文的端口为:"+UDP_PORT);
            UdpDatagramParam udpDatagramParam=new UdpDatagramParam(jsonString.getBytes(),broadcastAddress,UDP_PORT);
            Task_SendUdpDatagram task_sendUdpDatagram=new Task_SendUdpDatagram();
            task_sendUdpDatagram.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,udpDatagramParam);
            return true;//这个现在还没有意义
        }


    }
    public boolean receiveUdpDatagram(){
        return true;
    }



}
