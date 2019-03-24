package com.mhd.superwifidirect.Util;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.content.ContextCompat;

import java.io.EOFException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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

    public static InetAddress getSelfInetAddress(Context context){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use :-)
                            return inetAddress;
                        }
                        //return inetAddress.getHostAddress().toString(); // Galaxy Nexus returns IPv6
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        } catch (NullPointerException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        }
        return null;
//        WifiManager wm=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        //检查Wifi状态
//        if(!wm.isWifiEnabled())
//            wm.setWifiEnabled(true);
//        WifiInfo wi=wm.getConnectionInfo();
//        //获取32位整型IP地址
//        int ipAdd=wi.getIpAddress();
//        //把整型地址转换成“*.*.*.*”地址
//        String ip=intToIp(ipAdd);
//        try {
//            return InetAddress.getByName(ip);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return null;
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
//        if(dhcpInfo==null){
//            try {
//                return InetAddress.getByName("255.255.255.255");
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//        }
        int broadcast=(dhcpInfo.ipAddress&dhcpInfo.netmask)|~dhcpInfo.netmask;
        byte[] quads=new byte[4];
        for (int k=0;k<4;k++){
            quads[k]=(byte)((broadcast>>k*8)&0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    @SuppressLint("HardwareIds")
    public static String getSelfMac(Context context){
        WifiManager wm=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //检查Wifi状态
        if(!wm.isWifiEnabled())
            wm.setWifiEnabled(true);
        WifiInfo wi=wm.getConnectionInfo();
        return  wi.getMacAddress();

    }





}
