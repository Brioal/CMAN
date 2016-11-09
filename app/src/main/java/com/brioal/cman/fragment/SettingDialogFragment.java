package com.brioal.cman.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.base.BaseDialogFragment;
import com.brioal.cman.bean.FreeSocketMessage;
import com.brioal.cman.bean.SettingSocketMessage;
import com.brioal.cman.data.DataLoader;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.entity.SocketBeanBack;
import com.brioal.cman.interfaces.OnDeviceStatusChangedListener;
import com.brioal.cman.interfaces.OnDialogDismissListener;
import com.brioal.cman.util.ApplicationUtil;
import com.brioal.cman.util.MessageDealUtil;
import com.brioal.cman.view.HorizontalScroll;
import com.brioal.settingview.adapter.SettingViewAdapter;
import com.brioal.settingview.entity.SettingEntity;
import com.brioal.settingview.interfaces.OnItemClickListener;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.brioal.cman.entity.SocketBeanBack.addZeroForNum;

/**
 * Created by Brioal on 2016/10/18.
 * Email : brioal@foxmail.com
 * Github : https://github.com/brioal
 */

public class SettingDialogFragment extends BaseDialogFragment {


    @Bind(R.id.tablayout)
    TabLayout mTablayout;
    @Bind(R.id.base_recycler)
    RecyclerView mBaseRecycler;
    @Bind(R.id.advance_recycler)
    RecyclerView mAdvanceRecycler;
    @Bind(R.id.dialog_setting_scrollview)
    HorizontalScroll mScrollview;

    private static SettingDialogFragment sFragment;
    private Context mContext;

    //环境参数
    private int mWendu_min = 0;
    private int mWendu_max = 0;

    private int mShidu_min = 0;
    private int mShidu_max = 0;

    private int mQiwei_max = 0;

    private int mCurrentVolume = 0;

    private int mAuto = 1; //自动控制 2,手动控制
    private int mClience = 0; //不静音 1.静音
    private int mJiare = 0; //不加热 1.加热
    private int mChushi = 0;//不除湿 1.除湿
    private int mChuchou = 0; //不除臭 1.除臭

    private Handler mHandler = new Handler();
    //设备连接
    public boolean isRunning = false;
    private boolean isSendFree = false;
    private ApplicationUtil mUtil;
    private DataOutputStream out;
    private DataInputStream in;
    private Thread mReceivedThread = null;
    private DeviceEntity mDeviceEntity;

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        mDeviceEntity = deviceEntity;
    }

    public void setLastBaowen(String lastBaowen) {
        if (!lastBaowen.isEmpty()) {
            dealMsg(lastBaowen);
        }
    }


    public static SettingDialogFragment getInstance(DeviceEntity entity, String lastBaowen) {
        if (sFragment == null) {
            sFragment = new SettingDialogFragment();
        }
        sFragment.setDeviceEntity(entity);
        sFragment.setLastBaowen(lastBaowen);
        return sFragment;
    }

    @Override
    public void initVar() {
        mContext = getActivity();
    }

    @Override
    public void initView() {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_dialog_setting, null, false);
        ButterKnife.bind(this, mRootView);
        initTabLayout();//初始化TabLayout
        initSetting();//初始化设置界面
        initDeviceInfo();//初始化获取设备信息
    }

    private void initDeviceInfo() {
        try {
            isRunning = true;
            mUtil = (ApplicationUtil) getActivity().getApplication();
            out = mUtil.getOut();
            in = mUtil.getIn();
            initReceivedThread();
            isSendFree = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendFress();
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //发送空闲报文
    public void sendFress() {
        String ssid = mUtil.getSSID().substring(13, 19);
        SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                "10", "");
        String sendStr = socketback.toString();
//        sendNormal(sendStr);
        mUtil.startSend(new FreeSocketMessage(sendStr));
    }


    public void sendNormal(final String s) {
        if (!isSendFree) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendNormal(s);
                }
            }, 2000);
            return;
        }
        isSendFree = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isSendFree = true;
            }
        }, 1000);
        String str = "[login]|" + s;
        try {
            if (out == null) {
                out = mUtil.getOut();
            }
            if (out != null) {
                OutputStreamWriter outSW = new OutputStreamWriter(out, "GBK");
                BufferedWriter bw = new BufferedWriter(outSW);
                bw.write(str);
                bw.flush();
                outSW.flush();
                out.flush();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //接受信息的线程
    private void initReceivedThread() {
        mReceivedThread = new Thread(new Runnable() {
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        Thread.sleep(1000);
                        if (mUtil.isIsconnect()) {
                            if (in == null) {
                                in = mUtil.getIn();
                            }
                            if (in != null && mUtil.isIsconnect()) {
                                int r = in.available();
                                while (r == 0) {
                                    if (in != null) r = in.available();
                                }
                                byte[] b = new byte[r];
                                in.read(b);
                                String content = new String(b, "gbk");
                                KLog.e(content);
                                dealMsg(content);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mReceivedThread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
    }

    private void dealMsg(String s) {
        MessageDealUtil.DealMessage(s, new OnDeviceStatusChangedListener() {
            @Override
            public void changed(int wendu_min, int wendu_max, int shidu_min, int shidu_max, int qiwe_max, int auto, int clience, int shuixiang, int jiankang, int currentWendu, int currentShidu, int currentQiwei, int jiare, int chushi, int chuchou, int currentVolume) {
                mWendu_min = wendu_min;
                mWendu_max = wendu_max;
                mShidu_min = shidu_min;
                mShidu_max = shidu_max;
                mQiwei_max = qiwe_max;
                KLog.e(auto);
                mAuto = auto;
                mClience = clience;
                mJiare = jiare;
                mChushi = chushi;
                mChuchou = chuchou;
                mCurrentVolume = currentVolume;
            }
        });
    }

    private void initSetting() {
        mTablayout.addTab(mTablayout.newTab().setText("基础设置"));
        mTablayout.addTab(mTablayout.newTab().setText("高级设置"));
        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    mScrollview.prev();
                } else {
                    mScrollview.next();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initTabLayout() {
        initBaseSetting();
        initAdvance();
    }

    private AlertDialog.Builder mBuild;

    //恢复出厂
    public void initDeviceSetting() {
        mBuild = new AlertDialog.Builder(mContext);
        mBuild.setTitle("警告").setMessage("确定恢复出厂设置？").setPositiveButton("恢复", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    SocketBeanBack bean = new SocketBeanBack(addZeroForNum(mDeviceEntity.getSSID().substring(13, 19), 8, false),
                            "20", "");
                    KLog.e("恢复出厂设置");
                    KLog.e(bean.toString());
                    sendNormal(bean.toString());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, "恢复出厂设置成功");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, "恢复出厂设置失败");
                        }
                    });
                }

            }
        }).setNegativeButton("不恢复", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    //初始化密码
    public void initPass() {
        mBuild = new AlertDialog.Builder(mContext);
        mBuild.setTitle("警告").setMessage("确定初始化设备密码？").setPositiveButton("初始化", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                KLog.e("初始化密码");
                KLog.e(mDeviceEntity.getSSID());
                SocketBeanBack bean = new SocketBeanBack(addZeroForNum(mDeviceEntity.getSSID().substring(13, 19), 8, false),
                        "19", "");
                KLog.e(bean.toString());
                mUtil.startSend(new SettingSocketMessage(bean.toString()));
//                sendNormal(bean.toString());
                dialogInterface.dismiss();
                DataLoader.newInstance(mContext).changePass(mDeviceEntity.getSSID(), "");
                showWait("请稍等", "设备密码重置中....");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideWait();
                        showPassInited();
                    }
                }, 5000);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private ProgressDialog mProgressDialog;

    public void showWait(String title, String pass) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
        }
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(pass);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void hideWait() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showPassInited() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("初始化已完成").setMessage("密码已经初始化，将会退回设备列表页面，需重新输入设备密码").setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingDialogFragment.this.dismiss();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mUtil.finishDeviceActivity();
                    }
                },1000);
            }
        }).setCancelable(false).create().show();
    }


    private void initAdvance() {
        List<SettingEntity> list = new ArrayList<>();
        list.add(new SettingEntity("环境设置", "自定义温度,湿度,气味灵敏度", null).setItemClickListener(new OnItemClickListener() {
            @Override
            public void clicked(SettingEntity settingEntity) {
                isRunning = false;
                //显示环境设置,并且传入参数
                EnvirSettingFragment.newInstance().setEnvir(mDeviceEntity.getSSID(), mWendu_min, mWendu_max, mShidu_min, mShidu_max, mQiwei_max, mAuto, mClience).setDialogDismissListener(new OnDialogDismissListener() {
                    @Override
                    public void dismiss(int... value) {
                        mWendu_min = value[0];
                        mWendu_max = value[1];
                        mShidu_min = value[2];
                        mShidu_max = value[3];
                        mQiwei_max = value[4];
                        mAuto = value[5];
                        mClience = value[6];
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!isRunning) {
                                    cancel();
                                }
                                KLog.e("重新开始发送空闲");
                                initDeviceInfo();
                            }
                        }, 5000);

                    }
                }).show(getChildFragmentManager(), "");
            }
        }));
        list.add(new SettingEntity("音量设置", null, null).setItemClickListener(new OnItemClickListener() {
            @Override
            public void clicked(SettingEntity settingEntity) {
                isRunning = false;
                KLog.e("音量设置");
                VolumeDialogFragment.getInstance(mCurrentVolume).setOnDialogDismissListener(new OnDialogDismissListener() {
                    @Override
                    public void dismiss(int... value) {
                        mCurrentVolume = value[0];
                        KLog.e(value[0]);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!isRunning) {
                                    cancel();
                                }
//                                initDeviceInfo();
                                KLog.e("重新开始发送空闲");
                            }
                        }, 5000);
                    }
                }).show(getChildFragmentManager(), "");
            }
        }));
        list.add(new SettingEntity("功能开启", "选择要启用的功能", null).setItemClickListener(new OnItemClickListener() {
            @Override
            public void clicked(SettingEntity settingEntity) {
                if (mAuto == 1) {
                    //自动控制模式
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, "当前是自动控制,功能设置不可用");
                        }
                    });
                } else {
                    isRunning = false;
                    //显示功能设置并且传入参数
                    FuncationChooseDialog dialog = FuncationChooseDialog.newInstance(mJiare, mChushi, mChuchou);
                    dialog.setSSID(mDeviceEntity.getSSID());
                    dialog.setDialogDismissListener(new OnDialogDismissListener() {
                        @Override
                        public void dismiss(int... values) {
                            KLog.e("功能设置返回值");
                            mJiare = values[0];
                            mChushi = values[1];
                            mChuchou = values[2];
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!isRunning) {
                                        cancel();
                                    }
                                    KLog.e("重新开始发送空闲");
//                                    initDeviceInfo();
                                }
                            }, 5000);
                        }
                    });
                    dialog.show(getChildFragmentManager(), "");
                }

            }
        }));
        mAdvanceRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mAdvanceRecycler.setAdapter(new SettingViewAdapter(mContext, list));
    }


    private void initBaseSetting() {
        List<SettingEntity> list = new ArrayList<>();
        list.add(new SettingEntity("初始化Wifi密码", null, null).setItemClickListener(new OnItemClickListener() {
            @Override
            public void clicked(SettingEntity settingEntity) {
                initPass();
            }
        }));
        list.add(new SettingEntity("更改Wifi密码", null, null).setItemClickListener(new OnItemClickListener() {
            @Override
            public void clicked(SettingEntity settingEntity) {
                ChangePassFragment.newInstance(mDeviceEntity).show(getChildFragmentManager(), "");
            }
        }));
        list.add(new SettingEntity("恢复出厂设置", null, null).setItemClickListener(new OnItemClickListener() {
            @Override
            public void clicked(SettingEntity settingEntity) {
                initDeviceSetting();
            }
        }));
        mBaseRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBaseRecycler.setAdapter(new SettingViewAdapter(mContext, list));
    }

    @Override
    public void initWindow(WindowManager.LayoutParams params) {
        params.gravity = Gravity.BOTTOM;//设置方向
        params.width = getActivity().getWindowManager().getDefaultDisplay().getWidth();//设置 宽度
        params.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() * 2 / 3;//设置高度
    }

    @Override
    public void initAnimation(Dialog dialog) {
        dialog.getWindow().setWindowAnimations(R.style.AnimationSlideBottom);
    }

    @Override
    public void changeTheme() {
        mTheme = R.style.WhiteBackDialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        closeConnect();
    }

    public void closeConnect() {
        try {
            isRunning = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
