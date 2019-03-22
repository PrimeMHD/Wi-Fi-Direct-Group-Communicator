package com.mhd.superwifidirect.Util;

import android.app.IntentService;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {

    public static String getIp(Context context){
        WifiManager wm=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //检查Wifi状态
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);
        WifiInfo wi=wm.getConnectionInfo();
        //获取32位整型IP地址
        int ipAdd=wi.getIpAddress();
        //把整型地址转换成“*.*.*.*”地址
        String ip=intToIp(ipAdd);
        return ip;
    }
    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo=wifiManager.getDhcpInfo();
        if(dhcpInfo==null){
            try {
                return InetAddress.getByName("255.255.255.255");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        int broadcast=(dhcpInfo.ipAddress&dhcpInfo.netmask)|~dhcpInfo.netmask;
        byte[] quads=new byte[4];
        for (int k=0;k<4;k++){
            quads[k]=(byte)((broadcast>>k*8)&0xFF);
        }
        return InetAddress.getByAddress(quads);
    }
}
