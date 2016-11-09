package com.brioal.cman.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.cman.R;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.interfaces.OnDeviceAddListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Device Add Activity RecyclerView Adapter
 * Created by Brioal on 2016/9/13.
 */
public class DeviceAddAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<DeviceEntity> mList;
    private int NODATA = 0;
    private int DEVICE = 1;
    private OnDeviceAddListener mAddListener;

    public DeviceAddAdapter(Context context, List<DeviceEntity> list, OnDeviceAddListener listener) {
        mContext = context;
        mList = list;
        mAddListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NODATA) { //无数据
            return new NoDeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_no_device, parent, false));
        } else if (viewType == DEVICE) {
            return new DeviceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_device_add, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeviceViewHolder) {
            final DeviceEntity entity = mList.get(position);
            ((DeviceViewHolder) holder).mTvTitle.setText(entity.getTitle());
            if (entity.getStren() > -50) {
                ((DeviceViewHolder) holder).mTvState.setText("信号良好");
            } else {
                ((DeviceViewHolder) holder).mTvState.setText("信号欠佳");
            }
            ((DeviceViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAddListener != null) {
                        mAddListener.addDevice(entity);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        } else {
            return mList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mList.size() == 0) {
            return NODATA;
        } else {
            return DEVICE;
        }
    }

    //无数据ViewHolder
    class NoDeviceViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.no_data_tv)
        TextView mNoDataTv;


        public NoDeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            mNoDataTv.setText("暂无可用设备");
        }
    }

    //DeviceViewHolder
    class DeviceViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.device_add_tv_title)
        TextView mTvTitle;
        @Bind(R.id.device_add_tv_state)
        TextView mTvState;

        View itemView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
