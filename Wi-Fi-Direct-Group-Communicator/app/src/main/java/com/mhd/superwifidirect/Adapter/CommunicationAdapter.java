package com.mhd.superwifidirect.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;

import com.mhd.superwifidirect.Bean.ReceivedTransInfo;
import com.mhd.superwifidirect.R;

import java.util.List;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.ViewHolder>{
    private static final String TAG="CommunicationAdapter";
    private List<ReceivedTransInfo>receivedFilePath;

    private OnClickListner clickListner;
    public interface OnClickListner{
        void onItemClick(int position);
    }

    public CommunicationAdapter(List<ReceivedTransInfo> receivedFilePath) {
        this.receivedFilePath = receivedFilePath;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_commDevice;
        private TextView tv_commDigest;
        private TextView tv_commState;

        ViewHolder(View itemView){
            super(itemView);
            tv_commDevice=(TextView)itemView.findViewById(R.id.tv_commDevice);
            tv_commDigest=(TextView)itemView.findViewById(R.id.tv_commDigest);
            tv_commState=(TextView)itemView.findViewById(R.id.tv_commState);


        }

    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_com,viewGroup,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListner!=null){
                    clickListner.onItemClick((Integer)view.getTag());
                }
            }
        });
        Log.d(TAG,"执行点1");

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i) {

        String CommDevice=receivedFilePath.get(i).getIncomingDevice();
        String CommDigest=receivedFilePath.get(i).getDigest();
        int CommProgress=receivedFilePath.get(i).getProgress();
        Log.d(TAG,"执行点2");


        viewHolder.tv_commDevice.setText(CommDevice);
        viewHolder.tv_commDigest.setText(CommDigest);
        viewHolder.tv_commState.setText(String.valueOf(CommProgress));

        viewHolder.itemView.setTag(i);
    }


    public void setClickListner(OnClickListner clickListner) {
        this.clickListner = clickListner;
    }

    @Override
    public int getItemCount() {
        return receivedFilePath.size();
    }
}
