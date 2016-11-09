package com.brioal.cman.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.adapter.DeviceAddAdapter;
import com.brioal.cman.base.CMBaseActivity;
import com.brioal.cman.data.DataLoader;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.interfaces.OnDeviceAddListener;
import com.brioal.cman.interfaces.OnListLoadListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceAddActivity extends CMBaseActivity {

    @Bind(R.id.device_add_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.device_add_refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.device_list_btn_back)
    ImageButton mBtnBack;

    private DeviceAddAdapter mAdapter;
    private List<DeviceEntity> mList;
    private WifiManager wifiManager;


    //初始化RefreshLayout
    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(Color.GREEN, Color.BLUE, Color.RED);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataLocal();
            }
        });
    }


    @Override
    public void initVar() {

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.act_device_add);
        ButterKnife.bind(this);
        initRefreshLayout();
        initAction();
    }

    private void initAction() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void initTheme() {

    }

    @Override
    public void loadDataLocal() {
        if (mList == null) {
            mList = new ArrayList<>();
        } else {
            mList.clear();
        }
        //加载可用设备
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        if (wifiEnabled) { //WIFI可用,加载WIFI设备列表
            List<ScanResult> li = wifiManager.getScanResults();
            for (int i = 0; i < li.size(); i++) {
                String ssidstr = li.get(i).SSID.trim();
                KLog.e(ssidstr);
                if (ssidstr.length() == 19) {
                    if (ssidstr.substring(0, 12).equals("spirits_CMAN")) {
                        String name = "智能机器人基础版" + ssidstr.substring(ssidstr.length() - 7, ssidstr.length());
                        String mac = li.get(i).BSSID;
                        DeviceEntity entity = new DeviceEntity(System.currentTimeMillis(), name, ssidstr, mac, "1234567890");
                        entity.setStren(li.get(i).level);
                        entity.setOnLine(true);
                        mList.add(entity);
                    }
                }
            }
            deleteDouble();
        }
    }

    @Override
    public void loadDataNet() {

    }

    public void deleteDouble() {
        DataLoader.newInstance(mContext).getDeviceInfoLocal(new OnListLoadListener() {
            @Override
            public void succeed(List list) {
                for (int i = 0; i < list.size(); i++) {
                    if (mList.contains(list.get(i))) {
                        mList.remove(list.get(i)); //删除已添加的设备
                    }
                }
                mHandler.sendEmptyMessage(TYPE_SET_VIEW);
            }

            @Override
            public void failed(String errorMessage) {

            }
        });
    }

    @Override
    public void setView() {
        if (mAdapter == null) {
            mAdapter = new DeviceAddAdapter(mContext, mList, new OnDeviceAddListener() {

                @Override
                public void addDevice(DeviceEntity entity) {
                    showEnterPass(entity);
                }
            });
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }

    }

    private String mPass;

    private void showEnterPass(final DeviceEntity entity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_enter_pass, null, false);
        builder.setView(layout);
        builder.setTitle("输入设备密码");
        final EditText etPass = (EditText) layout.findViewById(R.id.dialog_pass_et_title);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                KLog.e("添加");
                String pass = etPass.getText().toString();
                KLog.e(pass);
                if (pass.isEmpty()) {
                    etPass.setError("密码不能为空");
                    return;
                }
                if (pass.length() < 8) {
                    etPass.setError("密码长度过短");
                    return;
                }
                if (pass.length() > 10) {
                    etPass.setError("密码长度过长");
                    return;
                }
                mPass = pass;
                entity.setPass(mPass);
                KLog.e(mPass);
                boolean succeed = DataLoader.newInstance(mContext).addDeviceInfo(entity);
                if (succeed) {
                    Snackbar.make(mRecyclerView, "添加设备成功", Snackbar.LENGTH_SHORT).show();
                    mList.remove(entity);
                    mHandler.sendEmptyMessage(TYPE_SET_VIEW);
                } else {
                    Snackbar.make(mRecyclerView, "添加设备失败", Snackbar.LENGTH_SHORT).show();
                }

            }
        }).setNegativeButton("不添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                KLog.e("不添加");

                dialogInterface.dismiss();
            }
        }).create().show();
    }

//    public void add() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage("确定要添加此设备吗?");
//        builder.setTitle("提示");
//        builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                boolean succeed = DataLoader.newInstance(mContext).addDeviceInfo(entity);
//                if (succeed) {
//                    Snackbar.make(mRecyclerView, "添加设备成功", Snackbar.LENGTH_SHORT).show();
//                    mList.remove(entity);
//                    mHandler.sendEmptyMessage(TYPE_SET_VIEW);
//                } else {
//                    Snackbar.make(mRecyclerView, "添加设备失败", Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });
//        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }

    @Override
    public void updateView() {

    }

    @Override
    public void saveDataLocal() {

    }

}
