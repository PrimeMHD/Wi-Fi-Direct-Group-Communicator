package com.mhd.superwifidirect.Bean;

import android.net.wifi.p2p.WifiP2pGroup;

public class EventMessage_MAtoMainFragment {
    private String messageType;
    private WifiP2pGroup wifiP2pGroup;

    public EventMessage_MAtoMainFragment(String messageType, WifiP2pGroup wifiP2pGroup) {
        this.messageType = messageType;
        this.wifiP2pGroup = wifiP2pGroup;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public WifiP2pGroup getWifiP2pGroup() {
        return wifiP2pGroup;
    }

    public void setWifiP2pGroup(WifiP2pGroup wifiP2pGroup) {
        this.wifiP2pGroup = wifiP2pGroup;
    }
}
