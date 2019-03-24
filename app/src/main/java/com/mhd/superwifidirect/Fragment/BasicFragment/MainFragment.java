package com.mhd.superwifidirect.Fragment.BasicFragment;

import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mhd.superwifidirect.Bean.EventMessage_MAtoMainFragment;
import com.mhd.superwifidirect.Bean.EventMessage_ReceiveTaskToMainFragment;
import com.mhd.superwifidirect.Bean.ReceivedTransInfo;
import com.mhd.superwifidirect.Fragment.Fragment_GroupMap;
import com.mhd.superwifidirect.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainFragment extends MySupportFragment {


    private static final String TAG = "MainFragment";
    private RecyclerView rv_deviceList;
    private RecyclerView rv_commList;
    private Button button_searchGroup;
    private Button button_createGroup;
    private Button button_quitGroup;
    private Button button_groupMap;

    private TextView tv_GroupState;
    private TextView tv_GroupOwner;
    private TextView tv_GroupOwnerAddr;
    private TextView tv_MemberNum;






    /****************控件类********************************/


    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_home, container, false);
        initView(view);
        EventBus.getDefault().register(this);
//        tv_testForGPS=(TextView)findViewById(R.id.tv_testForGPS);


        return view;
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage_MAtoMainFragment message){
        if (message.getMessageType().equals("EmptyGroup")){
            tv_GroupOwner.setText("");
            tv_GroupOwnerAddr.setText("");
            tv_GroupState.setText("不可用");
            tv_MemberNum.setText("");
        } else{
            WifiP2pGroup wifiP2pGroup=message.getWifiP2pGroup();
            tv_GroupOwner.setText(wifiP2pGroup.getNetworkName());
            tv_GroupOwnerAddr.setText(wifiP2pGroup.getOwner().deviceAddress);
            tv_GroupState.setText("可用");
            tv_MemberNum.setText(String.valueOf(wifiP2pGroup.getClientList().size()+1));
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage_ReceiveTaskToMainFragment message) {
        Log.e(TAG,"收到了EventMessage_ReceiveTaskToMainFragment");

        ReceivedTransInfo receivedTransInfo=new ReceivedTransInfo(message.getIncomingDevice(),message.getFilePath(),message.getProgress());
        mainActivity.getReceivedFilePath().add(receivedTransInfo);
        mainActivity.getCommunicationAdapter().notifyDataSetChanged();
        Log.e(TAG,mainActivity.getReceivedFilePath().toString());
//        if (message.getReceiveState().equals("FINISHED")){
//
//            ReceivedTransInfo receivedTransInfo=new ReceivedTransInfo(message.getIncomingDevice(),message.getFilePath(),message.getProgress());
//            mainActivity.getReceivedFilePath().add(receivedTransInfo);
//            mainActivity.getCommunicationAdapter().notifyDataSetChanged();
//            //mainActivity.openFile(message.getFilePath());
//        }

    }





    private void initView(View view) {
        rv_deviceList = (RecyclerView) view.findViewById(R.id.rv_SlaveList);
        rv_commList=(RecyclerView)view.findViewById(R.id.rv_CommReqList);
        button_createGroup = (Button) view.findViewById(R.id.button_createGroup);
        button_searchGroup = (Button) view.findViewById(R.id.button_searchGroup);
        button_quitGroup = (Button) view.findViewById(R.id.button_quitGroup);
        button_groupMap=(Button)view.findViewById(R.id.button_showGroupLocation);
        tv_GroupState=(TextView)view.findViewById(R.id.tv_GroupState);
        tv_GroupOwner=(TextView)view.findViewById(R.id.tv_GroupOwner);
        tv_GroupOwnerAddr=(TextView)view.findViewById(R.id.tv_GroupOwnerAddr);
        tv_MemberNum=(TextView)view.findViewById(R.id.tv_MemberNum);




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
                        Log.e(TAG, "removeGroup onFailure,reason:" + reason);
                        showToast("onFailure");
                    }
                });

            }
        });

        button_groupMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(Fragment_GroupMap.newInstance());
            }
        });

        //LinearLayoutManager要各用各的！
        rv_deviceList.setAdapter(deviceInfoAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(mainActivity));
        rv_commList.setAdapter(communicationAdapter);
        rv_commList.setLayoutManager(new LinearLayoutManager(mainActivity));


    }






}
