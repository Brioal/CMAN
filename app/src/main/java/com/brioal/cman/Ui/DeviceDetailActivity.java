package com.brioal.cman.Ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.base.CMBaseActivity;
import com.brioal.cman.bean.FreeSocketMessage;
import com.brioal.cman.bean.SettingSocketMessage;
import com.brioal.cman.data.DataLoader;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.entity.SocketBeanBack;
import com.brioal.cman.fragment.SettingDialogFragment;
import com.brioal.cman.interfaces.OnDeviceConnectedListener;
import com.brioal.cman.interfaces.OnDeviceStatusChangedListener;
import com.brioal.cman.util.ApplicationUtil;
import com.brioal.cman.util.MessageDealUtil;
import com.brioal.cman.view.LoadView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.brioal.cman.entity.SocketBeanBack.addZeroForNum;


public class DeviceDetailActivity extends CMBaseActivity implements OnDeviceConnectedListener {


    @Bind(R.id.btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.device_detail_tv_title)
    TextView mTvTitle;
    @Bind(R.id.btn_setting)
    ImageButton mBtnSetting;
    @Bind(R.id.device_statues_wendu)
    TextView mTvWendu;
    @Bind(R.id.device_statues_shidu)
    TextView mTvShidu;
    @Bind(R.id.device_statues_qiwei)
    TextView mTvQiwei;
    @Bind(R.id.device_statues_shuixiang)
    TextView mTvShuixiang;
    @Bind(R.id.device_statues_jiankang)
    TextView mTvJiankang;
    @Bind(R.id.device_loadview)
    LoadView mLoadview;
    @Bind(R.id.device_loadview_done)
    ImageView mLoadviewDone;
    @Bind(R.id.device_loadview_failed)
    LoadView mLoadviewFailed;
    @Bind(R.id.device_tv_statues)
    TextView mTvStatues;
    @Bind(R.id.device_lbtn_connect)
    Button mBtnConnect;


    //开关变量
    private int mJiare = 0;//加热状态
    private int mChushi = 0;//除湿开关状态
    private int mChuchou = 0;//除臭开关状态
    private int mClience = 0;//语音开关状态
    private int mAuto = 1;//自动控制开关状态
    private int mShuixiang = 0; //水箱
    private int mJiankang = 0; //健康
    //参数变量
    private int mWendu_min = 0;
    private int mWendu_max = 0;
    private int mShidu_min = 0;
    private int mShidu_max = 0;
    private int mQiwei_max = 0;
    private int mCurrentWendu = 0;
    private int mCurrentShidu = 0;
    private int mCurrentQiwei = 0;
    private int mCurrentVolume = 0;

    private DeviceEntity mEntity;
    private ApplicationUtil mUtil;
    private boolean isRunning = false;
    private boolean isSendFree = false;
    private boolean isConnected = false;
    private boolean isTimeSended = false;
    private DataOutputStream out;
    private DataInputStream in;
    private OnDeviceConnectedListener mConnectedListener;
    private OnDeviceStatusChangedListener mStatuesChangeListener;
    private Timer mTimer;
    private String mLastBaowen = "";
    private WifiManager mWifiManager;

    @Override
    public void initVar() {
        try {
            mEntity = (DeviceEntity) getIntent().getSerializableExtra("Entity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.act_device_detail);
        ButterKnife.bind(this);
        initAction();//初始化按钮动作
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectedListener = this;
        isConnected = false;
        isTimeSended = false;
        showStartConnect();
        showErrorValue();
    }

    //开始连接的状态
    private void showStartConnect() {
        mTvStatues.setText("正在连接");
        mBtnConnect.setVisibility(View.GONE);
        mLoadview.setVisibility(View.VISIBLE);
        mLoadview.startRotate();
        mLoadviewDone.setVisibility(View.GONE);
        mLoadviewFailed.setVisibility(View.GONE);
        mLoadviewFailed.stopRotate();
    }

    private void connectDevice() {
        KLog.e("SSID" + mEntity.getSSID());
        KLog.e("PASSWORD:" + mEntity.getPass());
        mWifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        WifiConfiguration config = new WifiConfiguration();
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        config.SSID = "\"" + mEntity.getSSID() + "\"";
        config.preSharedKey = "\"" + mEntity.getPass() + "\"";
        config.hiddenSSID = true;
        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        int netId = mWifiManager.addNetwork(config);
        boolean connected = mWifiManager.enableNetwork(netId, true);
        mUtil = (ApplicationUtil) mContext.getApplication();
        mUtil.setSSID(mEntity.getSSID());
        mUtil.setDeviceDetailActivity(DeviceDetailActivity.this);
        mUtil.setConnectedListener(this);
        KLog.e("connected:" + connected);
        if (connected) {
            isRunning = true;
            isSendFree = true;
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
            initSocket();
            initReceived();
            initSend();
            mUtil.initSendThread();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //连接超时
                    if (!isConnected) {
                        isRunning = false;
                        if (mConnectedListener != null) {
                            mConnectedListener.connectedTimeOut();
                        }
                    }
                }
            }, 10000);
        } else {
            KLog.e("WIFI连接失败");

            if (mConnectedListener != null) {
                mConnectedListener.falied("设备连接失败");
            }
        }
    }

    //显示密码错误提示
    private void showPassError() {
        mTvStatues.setText("连接失败，设备密码错误");//状态显示
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("出错了").setMessage("设备密码输入错误，请返回重试").setPositiveButton("返回重新连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                DeviceDetailActivity.this.finish();
            }
        }).create().show();
    }

    private Thread mSendThread = null;

    private void initSend() {
        mSendThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (isRunning) {
                    try {
                        if (isConnected) {
                            Thread.sleep(10000);
                        } else {
                            Thread.sleep(2000);
                        }
                        if (isSendFree) {
                            KLog.e("发送报文");
                            String ssid = mUtil.getSSID().substring(13, 19);
                            SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                                    "10", "");
                            String sendStr = socketback.toString();
                            if (mUtil != null) {
                                mUtil.startSend(new FreeSocketMessage(sendStr));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (mConnectedListener != null) {
                            if (isConnected) {
                                mConnectedListener.onDisConnected();
                            } else {
                                mConnectedListener.falied(e.getMessage());
                            }
                            isConnected = false;
                        }
                    }
                }
            }
        };
        mSendThread.start();
    }

    private Thread mReceivedThread = null;

    private void initReceived() {
        mReceivedThread = new Thread(new Runnable() {
            public void run() {
                while (isRunning) {
                    KLog.e("接受");
                    try {
                        Thread.sleep(3000);
                        if (!mUtil.isIsconnect()) {
                            mUtil.init(mEntity.getSSID());
                            out = mUtil.getOut();
                            in = mUtil.getIn();
                        }
                        if (in == null) {
                            in = mUtil.getIn();
                        }
                        if (in != null && mUtil.isSocketConnected()) {
                            //创建一个缓冲字节数
                            int r = in.available();
                            while (r == 0 && mUtil.isSocketConnected()) {
                                if (in != null) r = in.available();
                            }
                            byte[] b = new byte[r];
                            in.read(b);
                            final String content = new String(b, "gbk");
                            KLog.e(content);
                            if (!isConnected) {
                                if (mConnectedListener != null) {
                                    mConnectedListener.connected();
                                }
                                isConnected = true;
                                sendTime();
                            }
                            mLastBaowen = content;
                            dealMsg(content);
                        } else {
                            mUtil.setIsconnect(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mUtil.setIsconnect(false);
                        if (mConnectedListener != null) {
                            if (isConnected) {
                                mConnectedListener.onDisConnected();
                            } else {
                                mConnectedListener.falied(e.getMessage());
                            }
                            isConnected = false;
                        }
                    }
                }
            }
        });
        mReceivedThread.start();
    }

    public void sendTime() {
        KLog.e("BuildConnect");
        if (isTimeSended) {
            return;
        }
        isTimeSended = true;
        Date datetime = new Date();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("HHmmss");
        String datestr = format1.format(datetime);
        String timestr = format2.format(datetime);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 7;
        } else {
            week = week - 1;
        }
        KLog.e("对钟");
        String ssid = mUtil.getSSID().substring(13, 19);
        SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                "16", datestr + "0" + week + timestr);
        final String sendStr = socketback.toString();
        if (mUtil != null) {
            mUtil.startSend(new SettingSocketMessage(sendStr));
        }
    }


    //未获取数据状态下的显示
    public void showErrorValue() {
        mTvWendu.setText("温度：X");
        mTvShidu.setText("湿度：X");
        mTvQiwei.setText("气味：X");
        mTvShuixiang.setText("水箱：X");
        mTvJiankang.setText("健康：X");

        mTvWendu.setTextColor(Color.RED);
        mTvShidu.setTextColor(Color.RED);
        mTvQiwei.setTextColor(Color.RED);
        mTvShuixiang.setTextColor(Color.RED);
        mTvJiankang.setTextColor(Color.RED);
    }

    //处理返回报文
    public void dealMsg(String s) {
        MessageDealUtil.DealMessage(s, new OnDeviceStatusChangedListener() {
            @Override
            public void changed(int wendu_min, int wendu_max, int shidu_min, int shidu_max, int qiwe_max, int auto, int clience, int shuixiang, int jiankang, int currentWendu, int currentShidu, int currentQiwei, int jiare, int chushi, int chuchou, int currentVolume) {
                final StringBuffer buffer = new StringBuffer();

                buffer.append("当前温度：" + currentWendu + "℃");
                buffer.append("\n" + "当前湿度：" + currentShidu + "%");
                buffer.append("\n" + "当前气味：" + currentQiwei);
                buffer.append("\n" + "当前音量：" + currentVolume);
                buffer.append("\n" + "当前水箱状态：" + shuixiang);
                buffer.append("\n" + "当前健康状态：" + jiankang);
                buffer.append("\n" + "当前加热开关状态：" + jiare);
                buffer.append("\n" + "当前除湿开关状态：" + chushi);
                buffer.append("\n" + "当前除臭开关状态：" + chuchou);
                buffer.append("\n" + "当前控制模式：" + auto);
                buffer.append("\n" + "当前静音模式：" + clience);
                buffer.append("\n" + "-----------------------------------");
                buffer.append("\n" + "温度范围：" + wendu_min + "->" + wendu_max);
                buffer.append("\n" + "湿度范围：" + shidu_min + "->" + shidu_max);
                buffer.append("\n" + "气味最大值：" + qiwe_max);
                mWendu_min = wendu_min;
                mWendu_max = wendu_max;
                mShidu_min = shidu_min;
                mShidu_max = shidu_max;
                mQiwei_max = qiwe_max;
                mAuto = auto;
                mClience = clience;
                mShuixiang = shuixiang;
                mJiankang = jiankang;
                mCurrentWendu = currentWendu;
                mCurrentShidu = currentShidu;
                mCurrentQiwei = currentQiwei;
                mJiare = jiare;
                mChushi = chushi;
                mChuchou = chuchou;
                mCurrentVolume = currentVolume;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTvWendu.setTextColor(Color.BLACK);
                        mTvShidu.setTextColor(Color.BLACK);
                        mTvQiwei.setTextColor(Color.BLACK);
                        mTvShuixiang.setTextColor(Color.BLACK);
                        mTvJiankang.setTextColor(Color.BLACK);

                        mTvWendu.setText("温度：" + mCurrentWendu + "℃");
                        mTvShidu.setText("湿度：" + mCurrentShidu + "%");
                        mTvQiwei.setText("气味：" + mCurrentQiwei);
                        if (mShuixiang == 0) {
                            mTvShuixiang.setTextColor(Color.BLACK);
                            mTvShuixiang.setText("水箱：正常");
                        } else {
                            mTvShuixiang.setTextColor(Color.RED);
                            mTvShuixiang.setText("水箱：已满");
                        }
                        if (mJiankang == 0) {
                            mTvJiankang.setTextColor(Color.BLACK);
                            mTvJiankang.setText("健康：正常");
                        } else {
                            mTvJiankang.setTextColor(Color.RED);
                            mTvJiankang.setText("健康：异常");
                        }
                    }
                });


            }
        });

    }

    private Thread mInitThread = null;

    private void initSocket() {
        mUtil.init(mEntity.getSSID());
        out = mUtil.getOut();
        in = mUtil.getIn();
        mInitThread = new Thread(new Runnable() {
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(1000);
                        if (!mUtil.isIsconnect()) {
                            mUtil.init(mEntity.getSSID());
                            out = mUtil.getOut();
                            in = mUtil.getIn();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isConnected) {
                            mConnectedListener.onDisConnected();
                        } else {
                            mConnectedListener.falied(e.getMessage());
                        }
                        isConnected = false;
                    }
                }
            }
        });
        mInitThread.start();
    }

    private void initUI() {
        String title = "";
        try {
            title = mEntity.getTitle();
        } catch (Exception e) {
            e.printStackTrace();
            title = "设备详情";
        }
        mTvTitle.setText(title);
        mTvTitle.setSelected(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        KLog.e("Focus" + hasFocus);
        if (hasFocus) {
            mBtnSetting.setEnabled(true);
            try {
                mConnectedListener = this;
                connectDevice();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void initAction() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, "设备未连接,请稍后");
                        }
                    });
                    return;
                }
                //  打开设置
                isRunning = false;
                mBtnSetting.setEnabled(false);
                SettingDialogFragment fragment = new SettingDialogFragment();
                fragment.setDeviceEntity(mEntity);
                fragment.setLastBaowen(mLastBaowen);
                fragment.show(getSupportFragmentManager(), "");
                mConnectedListener = null;
            }
        });
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected) {
                    showStartConnect();
                    connectDevice();
                }
            }
        });
    }


    @Override
    public void initTheme() {

    }

    @Override
    public void loadDataLocal() {

    }

    @Override
    public void loadDataNet() {

    }

    @Override
    public void setView() {

    }

    @Override
    public void updateView() {

    }

    @Override
    public void saveDataLocal() {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void connected() {
        //连接成功之后保存密码
        DataLoader.newInstance(mContext).changePass(mEntity.getSSID(), mEntity.getPass());
        //连接完成
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTvStatues.setText("已连接");
                mBtnConnect.setVisibility(View.GONE);
                mLoadview.setVisibility(View.GONE);
                mLoadview.stopRotate();
                mLoadviewDone.setVisibility(View.VISIBLE);
                mLoadview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadviewFailed.setVisibility(View.GONE);
                    }
                }, 2500);
                mLoadviewFailed.stopRotate();
            }
        });
    }

    @Override
    public void falied(String errorMessage) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBtnConnect.setVisibility(View.VISIBLE);//重试按钮
                mLoadview.setVisibility(View.GONE);
                mLoadview.stopRotate();
                mLoadviewDone.setVisibility(View.GONE);
                mLoadviewDone.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadviewFailed.setVisibility(View.VISIBLE);
                    }
                }, 2000);

                mLoadviewFailed.startRotate();
                showErrorValue();
            }
        });
    }

    @Override
    public void onDisConnected() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTvStatues.setText("连接已断开");//状态显示
                mBtnConnect.setVisibility(View.VISIBLE);//重试按钮
                mLoadview.stopRotate();
                mLoadview.setVisibility(View.GONE);
                mLoadviewDone.setVisibility(View.GONE);
                mLoadviewDone.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadviewFailed.setVisibility(View.VISIBLE);
                    }
                }, 2000);

                mLoadviewFailed.startRotate();
                closeConnect();
                showErrorValue();
            }
        });
    }

    @Override
    public void connectedTimeOut() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //连接超时
                mTvStatues.setText("连接超时，请重试");//状态显示
                mLoadview.setVisibility(View.GONE);
                mLoadview.stopRotate();
                mLoadview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadviewDone.setVisibility(View.GONE);
                        mBtnConnect.setVisibility(View.VISIBLE);//重试按钮
                        mLoadviewFailed.setVisibility(View.VISIBLE);
                    }
                }, 2000);
                showErrorValue();
                try {
                    WifiInfo info = mWifiManager.getConnectionInfo();
                    if (info != null) {
                        String ssid = mWifiManager.getConnectionInfo().getSSID();
                        if (ssid.equals(mEntity.getSSID())) {
                            //已连接设备但是连接不成功
                            mTvStatues.setText("连接失败，请重试");//状态显示
                        } else {
                            showPassError();
                        }
                    } else {
                        //WIFI未连接
                        showPassError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeConnect();
    }

    //关闭WIFI
    public void closeWifi() {
        //断开设备的Wifi连接
        try {
            int netId = mWifiManager.getConnectionInfo().getNetworkId();
            mWifiManager.disableNetwork(netId);
            mWifiManager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //关闭连接
    public void closeConnect() {
        mConnectedListener = null;
        isRunning = false;
        isSendFree = false;
        isTimeSended = false;
        isConnected = false;
        mUtil.socketClose();
        closeWifi();
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            out = null;
            in = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
