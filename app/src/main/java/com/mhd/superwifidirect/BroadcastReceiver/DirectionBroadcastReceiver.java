package com.mhd.superwifidirect.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DirectionBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "DirectBroadcastReceiver";

    private WifiP2pManager mWifiP2pManager;

    private WifiP2pManager.Channel mChannel;

    private DirectActionListener mDirectActionListener;

    public DirectionBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, DirectActionListener directActionListener) {
        mWifiP2pManager = wifiP2pManager;
        mChannel = channel;
        mDirectActionListener = directActionListener;
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
        //广播过滤器，规定了接收什么
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "接收到广播： " + intent.getAction());
        if (!TextUtils.isEmpty(intent.getAction())) {
            switch (intent.getAction()) {
                // 用于指示 Wifi P2P 是否可用
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION: {
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        mDirectActionListener.wifiP2pEnabled(true);
                    } else {
                        mDirectActionListener.wifiP2pEnabled(false);
//                        List<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();
//                        mDirectActionListener.onPeersAvailable(wifiP2pDeviceList);
                    }
                    break;
                }
                // 对等节点列表发生了变化
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: {

                    final WifiP2pGroup[] tempWifiP2pGroup = new WifiP2pGroup[1];
                    mWifiP2pManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                            tempWifiP2pGroup[0] =wifiP2pGroup;
                        }
                    });

                    mWifiP2pManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Log.d(TAG,"调用了onPeersAvailable");
                            mDirectActionListener.onPeersAvailable(peers.getDeviceList(),tempWifiP2pGroup[0]);
                        }
                    });
                    break;
                }
                // Wifi P2P 的连接状态发生了改变
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: {
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.isConnected()) {
                        mWifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                            @Override
                            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                mDirectActionListener.onConnectionInfoAvailable(info);
                            }
                        });
                        Log.e(TAG, "已连接p2p设备");
                    } else {
                        mDirectActionListener.onDisconnection();
                        Log.e(TAG, "与p2p设备已断开连接");
                    }
                    break;
                }
                //本设备的设备信息发生了变化
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: {
                    mDirectActionListener.onSelfDeviceAvailable((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

                    break;
                }
            }
        }
    }

}
