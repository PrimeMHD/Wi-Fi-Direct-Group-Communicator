package com.mhd.superwifidirect.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mhd.superwifidirect.Adapter.DeviceInfoAdapter;
import com.mhd.superwifidirect.Bean.BroadcastObject;
import com.mhd.superwifidirect.Bean.DeviceInfo;
import com.mhd.superwifidirect.Bean.EventMessage_UdpToActivity;
import com.mhd.superwifidirect.BroadcastReceiver.DirectActionListener;
import com.mhd.superwifidirect.BroadcastReceiver.DirectionBroadcastReceiver;
import com.mhd.superwifidirect.Interface.MyLocationListener;
import com.mhd.superwifidirect.R;
import com.mhd.superwifidirect.Service.NetworkService;
import com.mhd.superwifidirect.Util.EnumPack;
import com.mhd.superwifidirect.Util.LocationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DirectActionListener {

    private static final String TAG = "MainActivity";
    private static boolean isP2pEnabled = false;
    private WifiP2pManager wifiP2pManager;//提供接口给上层调用，控制WifiP2pService
    private WifiP2pManager.Channel channel;
    private WifiP2pGroup mWifiP2pGroup;
    private static WifiP2pDevice selfDevice;
    private WifiP2pInfo mWifiP2pInfo;
    private DirectionBroadcastReceiver directionBroadcastReceiver;
    private EnumPack.AppUserType appUserType;
    private NetworkService.NetworkServiceBinder networkServiceBinder;
    private DeviceInfoAdapter deviceInfoAdapter;
    private LocationClient mLocationClient = null;
    private MyLocationListener myLocationListener=new MyLocationListener(){
        @Override
        public void onReceiveLocation(BDLocation location){
            super.onReceiveLocation(location);
            Log.e(TAG,"[BDonReceiveLocation],城市为："+location.getCity()+",纬度为"+location.getLatitude()+",经度为:"+location.getLongitude()+"街道为："+location.getStreet());
            Log.e(TAG,"返回错误码:"+location.getLocType());
            if(networkServiceBinder!=null){
                networkServiceBinder.broadcastSelfIpAndLocation(location);
            }
        }

    };


    /**********************管理类****************************/


    //private HashMap<String,DeviceInfo> DeviceInfoMap=new HashMap<>();  //最重要的数据结构，存储了周边设备（组内+组外）的全部信息。
    private List<WifiP2pDevice>mWifiP2pPeersList=new ArrayList<>();//可用节点列表
    private List<WifiP2pDevice>mGroupDeviceList=new ArrayList<>();//组群成员节点列表
    private Map<String, InetAddress>mDeviceIpMap=new HashMap<>();//保存组群设备的IP
    private Map<String, BDLocation>mDeviceLocationMap=new HashMap<>();//保存组群设备的位置
    private Location selfLocation;
    /*******************实用数据类****************************/



    private RecyclerView rv_deviceList;
    private Button button_searchGroup;
    private Button button_createGroup;
    private Button button_quitGroup;
    private TextView tv_testForGPS;
    /****************控件类********************************/



    public MainActivity() {
    }
    //TODO 会引起内存泄漏吗？



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);
        initView();
        EventBus.getDefault().register(this);
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), this);
        directionBroadcastReceiver = new DirectionBroadcastReceiver(wifiP2pManager, channel, this);
        registerReceiver(directionBroadcastReceiver, DirectionBroadcastReceiver.getIntentFilter());
        bindService(new Intent(this, NetworkService.class), networkServiceConnection, BIND_AUTO_CREATE);
        //启动网络服务

        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            Log.e(TAG,"这里准备去requestPermissions");
            Log.d(TAG,"未获得的权限有"+permissionList.toString());
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            Log.e(TAG,"这不准备去requestPermissions");
            startRequestLocation();
        }






    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(directionBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(networkServiceConnection);
        mLocationClient.stop();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if(result!= PackageManager.PERMISSION_GRANTED){
                            showToast("必须同意所有权限才能使用！");
                            finish();
                            return;
                        }
                    }
                    startRequestLocation();
                }else {
                    showToast("发生未知错误！");
                    finish();
                }
                break;
                default:
        }



    }

    private void startRequestLocation() {
        //注册定位监听
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1200);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setOpenGps(true);
        option.setIgnoreKillProcess(true);
        option.setLocationNotify(true);
        //option.setIsNeedAddress(true);
        //option.setIsNeedLocationDescribe(true);
        //option.setNeedDeviceDirect(true);

        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }

    private ServiceConnection networkServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            networkServiceBinder = (NetworkService.NetworkServiceBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };





    private void initView(){
        rv_deviceList=(RecyclerView)findViewById(R.id.rv_deviceInfo);
        button_createGroup=(Button)findViewById(R.id.button_createGroup);
        button_searchGroup=(Button)findViewById(R.id.button_searchGroup);
        button_quitGroup=(Button)findViewById(R.id.button_quitGroup);
        tv_testForGPS=(TextView)findViewById(R.id.tv_testForGPS);



        button_createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "createGroup onSuccess");
                        //dismissLoadingDialog();
                        showToast("创建组群成功！");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "createGroup onFailure: " + reason);
                        // dismissLoadingDialog();
                        showToast("创建组群失败！");
                    }
                });
            }
        });

        button_searchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isP2pEnabled){
                    //TODO 这里也不知道对不对
                    showToast("请先打开wifi");
                }else{
                    showToast("正在搜索中");
                    //mainActivity.wifiP2pMasterList.clear();
                    wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            showToast("Success");

                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            showToast("Failure");

                        }
                    });
                }
            }
        });

        button_quitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "removeGroup onSuccess");
                        showToast("onSuccess");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "removeGroup onFailure,reason:"+reason);
                        showToast("onFailure");
                    }
                });
            }
        });
        deviceInfoAdapter=new DeviceInfoAdapter(mWifiP2pPeersList,mGroupDeviceList,mDeviceIpMap,mDeviceLocationMap);
        deviceInfoAdapter.setClickListner(new DeviceInfoAdapter.OnClickListner() {
            @Override
            public void onItemClick(int position) {
                WifiP2pDevice wifiP2pDevice=mWifiP2pPeersList.get(position);
                connect(wifiP2pDevice);
            }
        });

        rv_deviceList.setAdapter(deviceInfoAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(this));




    }



/*************************EventBus*******************************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage_UdpToActivity eventMessage_udpToActivity){
        BroadcastObject broadcastObject=eventMessage_udpToActivity.getBroadcastObject();
        String deviceMac=broadcastObject.getSelfMac();
        InetAddress inetAddress=broadcastObject.getInetAddress();
        BDLocation location=broadcastObject.getLocation();
        mDeviceIpMap.put(deviceMac,inetAddress);
        mDeviceLocationMap.put(deviceMac,location);
        Log.d(TAG,"onMessageEvent"+mDeviceIpMap.toString());
        Log.d(TAG,"onMessageEvent"+mDeviceLocationMap.toString());
        deviceInfoAdapter.notifyDataSetChanged();
    }







/***************************************************************************************/
    /*******************下面皆是实现接口的方法******************************/

    @Override
    public void wifiP2pEnabled(boolean enabled) {

        isP2pEnabled = enabled;
        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                mWifiP2pGroup = wifiP2pGroup;
                deviceInfoAdapter.setmWifiP2pGroup(wifiP2pGroup);
                Log.e(TAG, "Master状态获取到了wifiP2pGroup的信息");
                Log.e(TAG, wifiP2pGroup + "");
                if (mWifiP2pGroup != null) {
                    mWifiP2pPeersList.clear();
                    mWifiP2pPeersList.addAll(mWifiP2pGroup.getClientList());
                    Log.e(TAG, "PeerList有" + mWifiP2pPeersList.size());
//                    EventBus.getDefault().post(new Event_FunctionFragmentEvent(onConnectionInfoAvailable));
                }
                deviceInfoAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        //连接状态发生了变化，可能是连接 到了某设备，或者某设备断开了连接
        mWifiP2pInfo=wifiP2pInfo;
        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                mWifiP2pGroup = wifiP2pGroup;
                deviceInfoAdapter.setmWifiP2pGroup(wifiP2pGroup);
                Log.e(TAG, "Master状态获取到了wifiP2pGroup的信息");
                Log.e(TAG, wifiP2pGroup + "");
                if (mWifiP2pGroup != null) {
                    mGroupDeviceList.clear();
                    mGroupDeviceList.addAll(mWifiP2pGroup.getClientList());
                    Log.e(TAG, "PeerList有" + mWifiP2pPeersList.size());
                    if(mWifiP2pGroup!=null){

                        Log.d(TAG,"这里试试能不能广播一下");
                        if (networkServiceBinder!=null)
                            networkServiceBinder.broadcastSelfIpAndLocation();
                    }else {
                        Log.d(TAG,"这里试试能不能广播一下,结果Group==null");

                    }
//                    EventBus.getDefault().post(new Event_FunctionFragmentEvent(onConnectionInfoAvailable));
                }
            }
        });


        deviceInfoAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDisconnection() {

    }

    @Override
    public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {

        selfDevice=wifiP2pDevice;
        wifiP2pManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                mWifiP2pGroup = wifiP2pGroup;
                deviceInfoAdapter.setmWifiP2pGroup(wifiP2pGroup);
                Log.e(TAG, "Master状态获取到了wifiP2pGroup的信息");
                Log.e(TAG, wifiP2pGroup + "");
                if (mWifiP2pGroup != null) {
                    mGroupDeviceList.clear();
                    mGroupDeviceList.addAll(mWifiP2pGroup.getClientList());
                    Log.e(TAG, "PeerList有" + mWifiP2pPeersList.size());
                    if(mWifiP2pGroup!=null){

                        Log.d(TAG,"这里试试能不能广播一下");
                        if (networkServiceBinder!=null)
                            networkServiceBinder.broadcastSelfIpAndLocation();
                    }else {
                        Log.d(TAG,"这里试试能不能广播一下,结果Group==null");

                    }
//                    EventBus.getDefault().post(new Event_FunctionFragmentEvent(onConnectionInfoAvailable));
                }
            }
        });
    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList,WifiP2pGroup wifiP2pGroup) {
        //对比已存的谁被列表，和新获取到的，修改DeviceInfoList
        //只有GO来维护这张表,其他的GC会在别处调用maintenceList来和GO同步。
        mWifiP2pPeersList.clear();
        mGroupDeviceList.clear();
        mWifiP2pPeersList.addAll(wifiP2pDeviceList);
        if(wifiP2pGroup!=null)
            mGroupDeviceList.addAll(wifiP2pGroup.getClientList());
        deviceInfoAdapter.setmWifiP2pGroup(wifiP2pGroup);

        Log.d(TAG,"调用了onPeersAvailable");
        wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                mWifiP2pInfo=wifiP2pInfo;
                //TODO 下面这一段是临时的，记得删除
                Log.d(TAG,"onPeersAvailable这里试试能不能广播一下");
                if (networkServiceBinder!=null)
                    networkServiceBinder.broadcastSelfIpAndLocation();
            }
        });
        deviceInfoAdapter.notifyDataSetChanged();





//        if(mWifiP2pInfo==null){
//            //TODO 本括号里的东西是暂时的
//            Log.d(TAG,"调用了onPeersAvailable且执行到A");
//            maintenanceDeviceInfoList(wifiP2pDeviceList,wifiP2pGroup);
//            //TODO 这里是
//            //networkServiceBinder.broadcastNewDeviceList(DeviceInfoMap);
//            Log.d(TAG,getDeviceInfoMap().toString());
//            deviceInfoAdapter.refreshDataList(getDeviceInfoMap());
//        }
//        else if (mWifiP2pInfo.isGroupOwner) {
//
//            maintenanceDeviceInfoList(wifiP2pDeviceList,wifiP2pGroup);
//            networkServiceBinder.broadcastNewDeviceList(DeviceInfoMap);
//            Log.d(TAG,getDeviceInfoMap().toString());
//            deviceInfoAdapter.refreshDataList(getDeviceInfoMap());
//            //控制NerworkService通报变动后的表。
//        }


    }

    @Override
    public void onChannelDisconnected() {

    }

//    public HashMap<String,DeviceInfo> getDeviceInfoMap() {
//        return DeviceInfoMap;
//    }
//    private void maintenanceDeviceInfoList(Collection<WifiP2pDevice> wifiP2pDeviceList,WifiP2pGroup wifiP2pGroup){
//        //TODO 这里的算法实现很不好，但是时间紧迫先这样了
//        Log.d(TAG,"在执行maintenanceDeviceInfoList");
//
//        for(Map.Entry<String, DeviceInfo> entry:DeviceInfoMap.entrySet()){
//            //查看等级表中的所有设备现在的状态，进行增添或者修改
//            DeviceInfo tempDeviceInfo=entry.getValue();
//            tempDeviceInfo.setDeviceState(DeviceInfo.DeviceState.LOST);
//            DeviceInfoMap.put(entry.getKey(),tempDeviceInfo);
//
//        }
//
//        for(WifiP2pDevice wifiP2pDevice:wifiP2pDeviceList){
//            Log.d(TAG,"临时测试maintenanceDeviceInfoList内部:"+wifiP2pDevice);
//            if(DeviceInfoMap.containsKey(wifiP2pDevice.deviceAddress)){
//                //如果在DeviceInfoMap中
//                DeviceInfo newDeviceInfo=DeviceInfoMap.get(wifiP2pDevice.deviceAddress);
//                if(wifiP2pGroup!=null&&wifiP2pGroup.getClientList().contains(wifiP2pDevice)){
//                    //置为INGROUP还是AVALABLE呢
//                    newDeviceInfo.setDeviceState(DeviceInfo.DeviceState.INGROUP);
//                }else{
//                    newDeviceInfo.setDeviceState(DeviceInfo.DeviceState.AVALAIBLE);
//                }
//                newDeviceInfo.setWifiP2pDevice(wifiP2pDevice);
//                DeviceInfoMap.put(wifiP2pDevice.deviceAddress,newDeviceInfo);
//
//            }else{
//                //如果不再DeviceInfoMap中,则将它加入。新加入的这种，状态是AVALAIBLLE
//                DeviceInfo newDeviceInfo1=new DeviceInfo(wifiP2pDevice);
//                newDeviceInfo1.setDeviceState(DeviceInfo.DeviceState.AVALAIBLE);
//                DeviceInfoMap.put(wifiP2pDevice.deviceAddress,newDeviceInfo1);
//            }
//
//        }
//    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void connect(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && wifiP2pDevice != null) {
            config.deviceAddress = wifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            //showLoadingDialog("正在连接 " + mWifiP2pDevice.deviceName);
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    //主动加入Group后的操作，为了能够双向操作
                    //与Master进行握手交换初始信息。分为3个阶段
                    showToast("连接成功");
                    Log.e(TAG, "connect onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("连接失败 " + reason);
                    //dismissLoadingDialog();
                }
            });
        }
    }

    public static WifiP2pDevice getSelfDevice(){
        return selfDevice;
    }

}
