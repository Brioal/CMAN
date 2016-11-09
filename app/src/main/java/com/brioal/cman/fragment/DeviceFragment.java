package com.brioal.cman.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.activity.DeviceAddActivity;
import com.brioal.cman.adapter.DeviceListAdapter;
import com.brioal.cman.base.CMBaseFragment;
import com.brioal.cman.data.DataLoader;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.interfaces.OnListLoadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Home Fragment For MainActivity
 * Created by Brioal on 2016/9/11.
 */
public class DeviceFragment extends CMBaseFragment {
    private static DeviceFragment sFragment;
    @Bind(R.id.fra_home_refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.fra_home_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.fra_home_btn_add)
    ImageButton mBtnAdd;

    private List<DeviceEntity> mList;
    private DeviceListAdapter mAdapter;
    private WifiManager wifiManager;

    public static DeviceFragment newInstance() {
        if (sFragment == null) {
            sFragment = new DeviceFragment();
        }
        return sFragment;
    }

    @Override
    public void initVar() {

    }

    @Override
    public void initView(Bundle saveInstanceState) {
        mRootView = inflater.inflate(R.layout.fra_device, container, false);
        ButterKnife.bind(this, mRootView);
        initRefreshLayout();
        initAction();
    }

    private void initAction() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DeviceAddActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(Color.GREEN, Color.BLUE, Color.RED);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataLocal();
            }
        });
    }

    private void initRecyclerView() {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        DataLoader.newInstance(mContext).getDeviceInfoLocal(new OnListLoadListener() {
            @Override
            public void succeed(List list) {
                KLog.i("加载本地数据成功");
                for (int i = 0; i < list.size(); i++) {
                    mList.add((DeviceEntity) list.get(i));
                }
                //获取本地的设备信息
                getDeviceState();
            }

            @Override
            public void failed(final String errorMessage) {
                KLog.i("加载本地数据失败");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(mContext, errorMessage);
                        mHandler.sendEmptyMessage(TYPE_SET_VIEW);
                    }
                });
            }
        });
        //刷新完毕,关闭刷新状态
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    //获取设备信息
    public void getDeviceState() {
        List<String> ssids = new ArrayList<>();
        HashMap<String, Integer> assics = new HashMap<>();
        wifiManager = (WifiManager) getActivity().getApplication().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        if (wifiEnabled) { //WIFI可用
            wifiManager.startScan();
            List<ScanResult> li = wifiManager.getScanResults();
            for (int i = 0; i < li.size(); i++) {
                String ssidstr = li.get(i).SSID.trim();
                int rssi = li.get(i).level;
                ssids.add(ssidstr);//存储SSID
                assics.put(ssidstr, rssi); //存储信号轻度
            }
            //设置设备状态
            for (int i = 0; i < mList.size(); i++) {
                String ssid = mList.get(i).getSSID();
                if (ssids.contains(ssid)) {
                    mList.get(i).setOnLine(true); //设置在线
                    mList.get(i).setStren(assics.get(ssid));//设置信号强度
                }
            }
            //视图更新
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplication(), "手机wifi模块没有开启，无法搜索设备！" + "\n", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if (mList.size() > 1) {
            Collections.sort(mList);
        }
        mHandler.sendEmptyMessage(TYPE_SET_VIEW);

    }


    @Override
    public void loadDataLocal() {
        initRecyclerView();
    }

    @Override
    public void loadDataNet() {

    }

    @Override
    public void setView() {
//        if (mAdapter == null) {
        mAdapter = new DeviceListAdapter(mContext, mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
//        } else {
//            mAdapter.notifyDataSetChanged();
//        }
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(false);

                }
            });
        }

    }

    @Override
    public void updateView() {

    }

    @Override
    public void saveDataLocal() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            loadDataLocal();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
