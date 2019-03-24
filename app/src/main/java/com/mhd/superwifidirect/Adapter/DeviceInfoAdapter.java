package com.mhd.superwifidirect.Adapter;

import android.location.Location;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.mhd.superwifidirect.Bean.DeviceInfo;
import com.mhd.superwifidirect.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.ViewHolder>{
    private static final String TAG="DeviceInfoAdapter";
//    private List<DeviceInfo> deviceInfoList=new ArrayList<>();
    private List<WifiP2pDevice>mWifiP2pPeersList;//可用节点列表
    private List<WifiP2pDevice>mGroupDeviceList;//组群成员节点列表
    private Map<String, InetAddress>mDeviceIpMap;//保存组群设备的IP
    private Map<String, BDLocation>mDeviceLocationMap;//保存组群设备的位置
    private WifiP2pGroup mWifiP2pGroup;
    //获得mainactivity中的这四个数据结构的引用。


    //TODO 这种，在类里使用new的方式创建的这个对象，会导致内存泄漏吗
    private OnClickListner clickListner;
    public interface OnClickListner{
        void onItemClick(int position);
    }
    public DeviceInfoAdapter(List<WifiP2pDevice>wifiP2pPeersList,List<WifiP2pDevice>groupDeviceList,Map<String, InetAddress>deviceIpMap,Map<String, BDLocation>deviceLocationMap){
        mWifiP2pPeersList=wifiP2pPeersList;
        mGroupDeviceList=groupDeviceList;
        mDeviceIpMap=deviceIpMap;
        mDeviceLocationMap=deviceLocationMap;

//        deviceInfoList.addAll(deviceInfoHashMap.values());
    }

//    public void refreshDataList(HashMap<String,DeviceInfo>deviceInfoHashMap){
//        Log.d(TAG,deviceInfoList.toString());
//        deviceInfoList.clear();
//        deviceInfoList.addAll(deviceInfoHashMap.values());
//        Log.d(TAG,deviceInfoList.toString());
//        this.notifyDataSetChanged();
//    }
//    public void refreshDataList(WifiP2pGroup wifiP2pGroup){
//
//    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_deviceName;
        private TextView tv_deviceAddress;
        private TextView tv_deviceState;
        private TextView tv_deviceLocation;
        ViewHolder(View itemView){
            super(itemView);
            tv_deviceName=(TextView)itemView.findViewById(R.id.tv_deviceName);
            tv_deviceAddress=(TextView)itemView.findViewById(R.id.tv_deviceAddress);
            tv_deviceState=(TextView)itemView.findViewById(R.id.tv_deviceDetails);
            tv_deviceLocation=(TextView)itemView.findViewById(R.id.tv_deviceLocation);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_device,viewGroup,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListner!=null){
                    clickListner.onItemClick((Integer)view.getTag());
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_deviceName.setText(mWifiP2pPeersList.get(position).deviceName);
        holder.tv_deviceAddress.setText(mWifiP2pPeersList.get(position).deviceAddress);
        Log.e(TAG,"这里onBindView");

        if(mWifiP2pGroup!=null){
            Log.d(TAG,"这里onBindView，GO是"+mWifiP2pGroup.getOwner().toString());
            Log.d(TAG,"这里onBindView,判断的devices是："+mWifiP2pPeersList.get(position));

        }else {
            Log.d(TAG,"这里onBindView，wifip2pGroup是null");
        }




        if(mGroupDeviceList.contains(mWifiP2pPeersList.get(position))){
            holder.tv_deviceState.setText(DeviceInfo.DeviceState.INGROUP.toString());
        }else if(mWifiP2pGroup!=null&&mWifiP2pGroup.getOwner().equals(mWifiP2pPeersList.get(position))){
            holder.tv_deviceState.setText(DeviceInfo.DeviceState.INGROUP.toString());
        }
        else {
            holder.tv_deviceState.setText(DeviceInfo.DeviceState.AVALAIBLE.toString());
        }
        holder.itemView.setTag(position);


        if(mDeviceLocationMap.containsKey(mWifiP2pPeersList.get(position).deviceAddress)){
            StringBuilder stringBuilder=new StringBuilder();
            BDLocation bdLocation=mDeviceLocationMap.get(mWifiP2pPeersList.get(position).deviceAddress);
            if (bdLocation==null){
                stringBuilder.append("位置未知");
            }else{
                stringBuilder.append("经度:");
                stringBuilder.append(bdLocation.getLongitude());
                stringBuilder.append("纬度:");
                stringBuilder.append(bdLocation.getLatitude());
            }


            holder.tv_deviceLocation.setText(stringBuilder.toString());
        }else{
            Log.d(TAG,"并不含有这样的设备："+mWifiP2pPeersList.get(position).deviceAddress);
            Log.d(TAG,mDeviceLocationMap.toString());
        }

    }

    public void setClickListner(OnClickListner clickListner){
        this.clickListner=clickListner;
    }

    @Override
    public int getItemCount() {
        return mWifiP2pPeersList.size();
    }

//    public List<DeviceInfo> getDeviceInfoList() {
//        return deviceInfoList;
//    }

    public void setmWifiP2pGroup(WifiP2pGroup mWifiP2pGroup) {
        this.mWifiP2pGroup = mWifiP2pGroup;
    }
}
