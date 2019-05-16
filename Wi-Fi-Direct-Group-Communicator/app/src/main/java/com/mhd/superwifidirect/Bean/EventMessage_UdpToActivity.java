package com.mhd.superwifidirect.Bean;

public class EventMessage_UdpToActivity {
    public EventMessage_UdpToActivity(BroadcastObject broadcastObject) {
        this.broadcastObject = broadcastObject;
    }

    private BroadcastObject broadcastObject;

    public BroadcastObject getBroadcastObject() {
        return broadcastObject;
    }

    public void setBroadcastObject(BroadcastObject broadcastObject) {
        this.broadcastObject = broadcastObject;
    }
}
