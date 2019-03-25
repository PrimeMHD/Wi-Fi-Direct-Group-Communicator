package com.mhd.superwifidirect.Bean;


import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import java.net.InetAddress;

public class BroadcastObject {


    public BroadcastObject(String selfMac,InetAddress inetAddress, LatLng location) {
        this.selfMac=selfMac;
        this.inetAddress = inetAddress;
        this.location = location;
    }

    //用于压缩和解压发送的UDP数据
    //发送自己的IP和Location
    private String selfMac;
    private InetAddress inetAddress;
    private LatLng location;

    public String getSelfMac() {
        return selfMac;
    }

    public void setSelfMac(String selfMac) {
        this.selfMac = selfMac;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
