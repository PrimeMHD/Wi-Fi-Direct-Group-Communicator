package com.mhd.superwifidirect.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.mhd.superwifidirect.Activity.MainActivity;
import com.mhd.superwifidirect.Bean.EventMessage_MAtoFragmentGroupMap;
import com.mhd.superwifidirect.Bean.EventMessage_ReceiveTaskToMainFragment;
import com.mhd.superwifidirect.Fragment.BasicFragment.MySupportFragment;
import com.mhd.superwifidirect.R;
import com.mhd.superwifidirect.Util.NetUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Iterator;
import java.util.Map;

public class Fragment_GroupMap extends MySupportFragment {
    private static final String TAG = "Fragment_GroupMap";
    private Map<String, LatLng> deviceLocationMap;

    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;


    public static Fragment_GroupMap newInstance() {
        Bundle args = new Bundle();
        Fragment_GroupMap fragment = new Fragment_GroupMap();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.layout_fragment_groupmap, container, false);


        initView(fragmentView);
        return fragmentView;
    }

    private void initView(View view) {
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        deviceLocationMap = mainActivity.getmDeviceLocationMap();
        baiduMap = mMapView.getMap();
        if (isFirstLocate){
            JumeToMySelf();
        }
        refreshMap();


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage_MAtoFragmentGroupMap message) {
        if (isFirstLocate){
            JumeToMySelf();
        }
        if (message.isRefresh()) {
            refreshMap();
        }
    }


    private void JumeToMySelf() {
        if (isFirstLocate) {
            Log.d(TAG,"跳到了我这里");
            String selfMac = MainActivity.getMainActivity().getSelfDevice().deviceAddress;
            LatLng selfLocation = mainActivity.getmDeviceLocationMap().get(selfMac);
            if (selfLocation != null) {
                jumeToLocation(selfLocation);
                isFirstLocate = false;
            }
        }
    }

    private void jumeToLocation(LatLng bdLocation) {

        LatLng ll = bdLocation;
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        update = MapStatusUpdateFactory.zoomTo(16f);
        baiduMap.animateMapStatus(update);

    }


    private void refreshMap() {
        //重新绘制点
        Log.d(TAG,"onRefreshMap");

        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.mapmarker, null);
        TextView tv_deviceName = (TextView) view.findViewById(R.id.tv_deviceName);
        TextView tv_deviceLng = (TextView) view.findViewById(R.id.tv_deviceLng);
        TextView tv_deviceLat = (TextView) view.findViewById(R.id.tv_deviceLat);


        Iterator iter = deviceLocationMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, LatLng> entry = (Map.Entry) iter.next();
            String deviceMac = entry.getKey();
            LatLng bdLocation = entry.getValue();

            //////////////////////获取地理点
            LatLng point = bdLocation;

            tv_deviceName.setText(mainActivity.getDeviceNameByMac(deviceMac));
            tv_deviceLat.setText(String.valueOf(bdLocation.latitude));
            tv_deviceLng.setText(String.valueOf(bdLocation.longitude));
            BitmapDescriptor bd1 = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(view));
            MarkerOptions ooA = new MarkerOptions().position(point).icon(bd1).zIndex(9).draggable(true);

            mMapView.getMap().addOverlay(ooA);//在地图上添加Marker，并显示


//            //定义Maker坐标点
//
////构建Marker图标
//            BitmapDescriptor bitmap = BitmapDescriptorFactory
//                    .fromResource(R.drawable.icon_marka);
//
//            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);//构建MarkerOption，用于在地图上添加Marker
//
//            mMapView.getMap().addOverlay(option);//在地图上添加Marker，并显示


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


}
