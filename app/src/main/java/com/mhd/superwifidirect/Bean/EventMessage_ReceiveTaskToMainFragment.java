package com.mhd.superwifidirect.Bean;

public class EventMessage_ReceiveTaskToMainFragment {

    private String incomingDevice;
    private String filePath;
    private String ReceiveState;
    private int progress;

    public EventMessage_ReceiveTaskToMainFragment(String incomingDevice, String filePath, String receiveState, int progress) {
        this.incomingDevice = incomingDevice;
        this.filePath = filePath;
        ReceiveState = receiveState;
        this.progress = progress;
    }

    public String getIncomingDevice() {
        return incomingDevice;
    }

    public void setIncomingDevice(String incomingDevice) {
        this.incomingDevice = incomingDevice;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getReceiveState() {
        return ReceiveState;
    }

    public void setReceiveState(String receiveState) {
        ReceiveState = receiveState;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
