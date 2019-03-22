package com.mhd.superwifidirect.Bean;

import android.location.Location;
import android.net.wifi.p2p.WifiP2pDevice;

import java.net.InetAddress;

public class DeviceInfo {
    private WifiP2pDevice wifiP2pDevice;
    public  enum DeviceState{INGROUP,AVALAIBLE,LOST}
    public enum DeviceCharacter{GO,GC}
    private InetAddress deviceInetAddress;
    private Location location;
    private DeviceState deviceState;

    public WifiP2pDevice getWifiP2pDevice() {
        return wifiP2pDevice;
    }


    public DeviceInfo(WifiP2pDevice wifiP2pDevice) {
        this.wifiP2pDevice = wifiP2pDevice;
    }

    public void setWifiP2pDevice(WifiP2pDevice wifiP2pDevice) {
        this.wifiP2pDevice = wifiP2pDevice;
    }

    public InetAddress getDeviceInetAddress() {
        return deviceInetAddress;
    }

    public void setDeviceInetAddress(InetAddress deviceInetAddress) {
        this.deviceInetAddress = deviceInetAddress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DeviceState getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(DeviceState deviceState) {
        this.deviceState = deviceState;
    }
}
