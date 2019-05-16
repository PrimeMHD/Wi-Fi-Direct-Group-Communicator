package com.mhd.superwifidirect.Task;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mhd.superwifidirect.Activity.MainActivity;
import com.mhd.superwifidirect.Bean.EventMessage_ReceiveTaskToMainFragment;
import com.mhd.superwifidirect.Bean.FileTransfer;
import com.mhd.superwifidirect.Util.Md5Util;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

public class ReceiveFileTask extends AsyncTask<Socket, Integer, Boolean> {


    private static final String TAG = "ReceiveFileTask";
    private Socket client;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private FileOutputStream fileOutputStream;
    private File file;
    private String incomingDevice;

    public ReceiveFileTask() {
        super();
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
    protected Boolean doInBackground(Socket... sockets) {


        try {
            client = sockets[0];
            Log.e(TAG, "客户端IP地址 : " + client.getInetAddress().getHostAddress());
            InetAddress incomingIPNet=client.getInetAddress();
            incomingDevice="UNKNOWN";
            for(String icm: MainActivity.getMainActivity().getmDeviceIpMap().keySet()){
                if(Objects.equals(MainActivity.getMainActivity().getmDeviceIpMap().get(icm), incomingIPNet)){
                    incomingDevice=icm;
                }
            }




            inputStream = client.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            FileTransfer fileTransfer = (FileTransfer) objectInputStream.readObject();
            Log.e(TAG, "待接收的文件: " + fileTransfer);
            String name = new File(fileTransfer.getFilePath()).getName();
            //将文件存储至指定位置
            file = new File(Environment.getExternalStorageDirectory() + "/" + name);
            fileOutputStream = new FileOutputStream(file);
            byte buf[] = new byte[512];
            int len;
            long total = 0;
            int progress;
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
                total += len;
                progress = (int) ((total * 100) / fileTransfer.getFileLength());
                Log.e(TAG, "文件接收进度: " + progress);
                //EventBus.getDefault().post(new Event_ServiceToFragment(Event_ServiceToFragment.TransEvent.DOING));

            }
            //EventBus.getDefault().post(new Event_ServiceToFragment(Event_ServiceToFragment.TransEvent.FINISH));

            clean();
            Log.e(TAG, "文件接收成功，文件的MD5码是：" + Md5Util.getMd5(file));
        } catch (Exception e) {
            Log.e(TAG, "文件接收 Exception: " + e.getMessage());
        } finally {
            if (file!=null&&file.exists()){
                EventBus.getDefault().post(new EventMessage_ReceiveTaskToMainFragment(incomingDevice,file.getPath(),"FINISHED",100));
            }else{
                EventBus.getDefault().post(new EventMessage_ReceiveTaskToMainFragment(null,"","FAILED",0));

            }

            clean();

        }


        return null;
    }


    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
        clean();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        clean();
    }

    private void clean() {
        if (client != null) {
            try {
                client.close();
                client = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (objectInputStream != null) {
            try {
                objectInputStream.close();
                objectInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }






}
