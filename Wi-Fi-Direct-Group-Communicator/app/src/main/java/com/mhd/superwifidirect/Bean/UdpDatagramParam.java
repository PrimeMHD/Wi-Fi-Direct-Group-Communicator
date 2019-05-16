package com.mhd.superwifidirect.Bean;

import java.net.InetAddress;

public class UdpDatagramParam {
    private byte[] byteToSend;
    private InetAddress targetAddress;
    private int targetPort;

    public UdpDatagramParam(byte[] byteToSend, InetAddress targetAddress, int targetPort) {
        this.byteToSend = byteToSend;
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
    }

    public byte[] getByteToSend() {
        return byteToSend;
    }

    public InetAddress getTargetAddress() {
        return targetAddress;
    }

    public int getTargetPort() {
        return targetPort;
    }
}
