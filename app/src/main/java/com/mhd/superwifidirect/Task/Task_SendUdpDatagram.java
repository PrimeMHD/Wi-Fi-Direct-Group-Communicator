package com.mhd.superwifidirect.Task;

import android.os.AsyncTask;
import android.util.Log;

import com.mhd.superwifidirect.Bean.UdpDatagramParam;
import com.mhd.superwifidirect.Interface.SendUdpListener;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Task_SendUdpDatagram extends AsyncTask<UdpDatagramParam, Integer, Boolean> {
    private static final int TIMEOUT = 3000;   // 设置超时为3秒
    private static final int MAXTRIES = 5;     // 最大重发次数5次
    public static final String TAG = "Task_SendUdpDatagram";
    private SendUdpListener sendUdpListener;

    public Task_SendUdpDatagram() {
        super();
    }

    public Task_SendUdpDatagram(SendUdpListener sendUdpListener) {
        this.sendUdpListener = sendUdpListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Boolean doInBackground(UdpDatagramParam... udpDatagramParams) {
        DatagramSocket datagramSocket = null;
        byte[] byteToSend = udpDatagramParams[0].getByteToSend();
        InetAddress targetAddress = udpDatagramParams[0].getTargetAddress();
        int targetPort = udpDatagramParams[0].getTargetPort();
        DatagramPacket sendPack;
        DatagramPacket receivePacket;
        //Step1 构造要发送的数据包
        sendPack = new DatagramPacket(byteToSend, byteToSend.length, targetAddress, targetPort);
        receivePacket = new DatagramPacket(new byte[byteToSend.length], byteToSend.length); // 相当于空的接收包
        Log.e(TAG, "targetAddress:" + targetAddress.toString());
        Log.e(TAG, "targetPort:" + targetPort);
        Log.e(TAG, "byteToSend:" + byteToSend);


        //Step2 准备发送数据的Socket
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(TIMEOUT);
            datagramSocket.setBroadcast(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Step3 发送数据包
        int tries = 0;
        boolean receivedResponse = false;

        try {
            if (datagramSocket == null) {
                Log.e(TAG, "datagramSocket是null!!");
                cancel(true);
            }
            Log.e(TAG, "datagramSocket不是null了!!" + sendPack.toString());
            Log.e(TAG, "tryTime=" + tries);
            datagramSocket.send(sendPack);
            tries += 1;
            //datagramSocket.receive(receivePacket);
//                    if (receivePacket.getAddress().equals(targetAddress)){
//                        receivedResponse=true;
//                    }
        } catch (InterruptedIOException e) {
            tries += 1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        if (sendUdpListener != null)
            sendUdpListener.onSuccess();//
        datagramSocket.close();


        return null;
    }
}
