package com.mhd.superwifidirect.Service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.mhd.superwifidirect.Bean.DeviceInfo;
import com.mhd.superwifidirect.Bean.UdpDatagramParam;
import com.mhd.superwifidirect.Task.Task_SendUdpDatagram;
import com.mhd.superwifidirect.Util.ByteUtil;
import com.mhd.superwifidirect.Util.NetUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

    private static final int UDP_PORT=34567;
    private static final int TCP_PORT=34657;

    private NetworkServiceBinder networkServiceBinder;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //启动两个线程，分别监听UDP和TCP的报文、连接

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
        return networkServiceBinder;
    }

    //静态内部类不会隐式持有外部类的引用
    public class NetworkServiceBinder extends Binder {

        /*GO发送广播通知所有GC，新的DeviceList*/
        //这里面也包含了所有设备的位置信息！
        public boolean broadcastNewDeviceList(HashMap<String,DeviceInfo> DeviceInfoMap){


            InetAddress broadcastAddress= null;
            try {
                broadcastAddress = NetUtil.getBroadcastAddress(getApplicationContext());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            UdpDatagramParam udpDatagramParam=new UdpDatagramParam(ByteUtil.transFromObjectToByte(DeviceInfoMap),broadcastAddress,UDP_PORT);
            new Task_SendUdpDatagram().execute(udpDatagramParam);
            return true;//这个现在还没有意义
        }





    }
    public boolean receiveUdpDatagram(){
        return true;
    }


}
