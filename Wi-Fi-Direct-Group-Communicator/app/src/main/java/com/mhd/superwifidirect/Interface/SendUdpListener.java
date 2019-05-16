package com.mhd.superwifidirect.Interface;

public interface SendUdpListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCancel();

}
