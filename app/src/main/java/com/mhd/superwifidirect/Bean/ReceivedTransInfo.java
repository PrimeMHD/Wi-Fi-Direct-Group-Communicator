package com.mhd.superwifidirect.Bean;

public class ReceivedTransInfo {
    private String incomingDevice;
    private String digest;
    private int progress;

    public ReceivedTransInfo(String incomingDevice, String digest, int progress) {
        this.incomingDevice = incomingDevice;
        this.digest = digest;
        this.progress = progress;
    }

    public String getIncomingDevice() {
        return incomingDevice;
    }

    public void setIncomingDevice(String incomingDevice) {
        this.incomingDevice = incomingDevice;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "ReceivedTransInfo{" +
                "incomingDevice='" + incomingDevice + '\'' +
                ", digest='" + digest + '\'' +
                ", progress=" + progress +
                '}';
    }
}
