package com.mhd.superwifidirect.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mhd.superwifidirect.Bean.DeviceInfo;
import com.mhd.superwifidirect.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.ViewHolder>{
    private static final String TAG="DeviceInfoAdapter";
    private List<DeviceInfo> deviceInfoList=new ArrayList<>();
    //TODO 这种，在类里使用new的方式创建的这个对象，会导致内存泄漏吗
    private OnClickListner clickListner;
    public interface OnClickListner{
        void onItemClick(int position);
    }
    public DeviceInfoAdapter(HashMap<String,DeviceInfo>deviceInfoHashMap){
        deviceInfoList.addAll(deviceInfoHashMap.values());
    }

    public void refreshDataList(HashMap<String,DeviceInfo>deviceInfoHashMap){
        Log.d(TAG,deviceInfoList.toString());
        deviceInfoList.clear();
        deviceInfoList.addAll(deviceInfoHashMap.values());
        Log.d(TAG,deviceInfoList.toString());
        this.notifyDataSetChanged();
    }

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
        holder.tv_deviceName.setText(deviceInfoList.get(position).getWifiP2pDevice().deviceName);
        holder.tv_deviceAddress.setText(deviceInfoList.get(position).getWifiP2pDevice().deviceAddress);
        holder.tv_deviceState.setText(deviceInfoList.get(position).getDeviceState().toString());
        holder.itemView.setTag(position);
    }

    public void setClickListner(OnClickListner clickListner){
        this.clickListner=clickListner;
    }

    @Override
    public int getItemCount() {
        return deviceInfoList.size();
    }

    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }
}
