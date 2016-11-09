package com.brioal.cman.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.base.BaseDialogFragment;
import com.brioal.cman.bean.SettingSocketMessage;
import com.brioal.cman.entity.SocketBeanBack;
import com.brioal.cman.interfaces.OnDialogDismissListener;
import com.brioal.cman.util.ApplicationUtil;
import com.brioal.seekbar.OnRangeChangedListener;
import com.brioal.seekbar.RangeBar;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.brioal.cman.entity.SocketBeanBack.addZeroForNum;

/**
 * Envir Setting Fragment
 * Created by Brioal on 2016/9/14.
 */
public class EnvirSettingFragment extends BaseDialogFragment {

    @Bind(R.id.fra_envir_btn_close)
    ImageButton mBtnClose;
    @Bind(R.id.fra_envir_btn_done)
    ImageButton mBtnDone;
    @Bind(R.id.fra_envir_rangeBar_temp)
    RangeBar mBarTemp;
    @Bind(R.id.fra_envir_rangeBar_humi)
    RangeBar mBarHumi;
    @Bind(R.id.fra_envir_rg)
    RadioGroup mRg;
    @Bind(R.id.envir_auto)
    Switch mEnvirAuto;
    @Bind(R.id.envir_clience)
    Switch mSwitchClience;

    //环境参数
    private int mWendu_min = 0;
    private int mWendu_max = 0;
    private int mShidu_min = 0;
    private int mShidu_max = 0;
    private int mQiwei_max = 0;
    private int mAuto = 1;
    private int mClience = 0;

    private String mSSID = "";

    private static EnvirSettingFragment sFragment;
    private Context mContext;
    private ApplicationUtil mUtil;
    private DataOutputStream out;
    private DataInputStream in;
    private OnDialogDismissListener mDialogDismissListener;

    public EnvirSettingFragment setDialogDismissListener(OnDialogDismissListener dialogDismissListener) {
        mDialogDismissListener = dialogDismissListener;
        return this;
    }

    //设置环境
    public EnvirSettingFragment setEnvir(String ssid, int minTemp, int maxTemp, int minHumi, int maxHumi, int qiwei, int auto, int jinyin) {
        this.mSSID = ssid;
        this.mWendu_min = minTemp;
        this.mWendu_max = maxTemp;
        this.mShidu_min = minHumi;
        this.mShidu_max = maxHumi;
        this.mAuto = auto;
        this.mClience = jinyin;
        this.mQiwei_max = qiwei;
        KLog.e("温度范围：" + mWendu_min + ":" + mWendu_max);
        KLog.e("湿度范围：" + mShidu_min + ":" + mShidu_max);
        KLog.e("气味：" + mQiwei_max);
        KLog.e("自动控制：" + mAuto);
        KLog.e("静音：" + mAuto);

        return this;
    }

    public static EnvirSettingFragment newInstance() {
        sFragment = new EnvirSettingFragment();
        return sFragment;
    }


    @Override
    public void initVar() {
        try {
            mContext = getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_envir_setting, null, false);
        ButterKnife.bind(this, mRootView);
        initButton();//初始化按钮
        initRangeBar();//初始化范围选择器
        initRadiuGroup();//初始化灵敏度选择事件
        initAction(); //初始化按钮事件
        initSend();//初始化发送情况

    }

    private void initSend() {
        try {
            mUtil = (ApplicationUtil) getActivity().getApplication();
            out = mUtil.getOut();
            in = mUtil.getIn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String ss) {
        if (mUtil.isIsconnect()) {
            try {
                if (out == null) {
                    out = mUtil.getOut();
                }
                if (out != null) {
                    OutputStreamWriter outSW = new OutputStreamWriter(out, "GBK");
                    BufferedWriter bw = new BufferedWriter(outSW);
                    bw.write(ss);
                    bw.flush();
                    outSW.flush();
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showConnectFailed();
        }
    }

    //显示连接中断
    public void showConnectFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("出错了").setMessage("连接已中断，将会退回到设备列表").setCancelable(false).setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EnvirSettingFragment.this.dismiss();
                mUtil.finishDeviceActivity();
            }
        }).create().show();
    }

    private void initButton() {
        mEnvirAuto.setChecked(mAuto == 1);
        mSwitchClience.setChecked(mClience == 1);
    }

    private void initAction() {
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvirSettingFragment.this.dismiss();
            }
        });
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
                mBtnDone.setEnabled(false);
                EnvirSettingFragment.this.dismiss();
            }
        });
        mEnvirAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAuto = isChecked ? 1 : 2;
            }
        });
        mSwitchClience.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mClience = isChecked ? 1 : 0;
            }
        });

    }

    public void commit() {
        try {
            if (mWendu_min < -30) {
                mWendu_min = -30;
            }
            if (mWendu_min > 50) {
                mWendu_min = 50;
            }
            if (mWendu_max > 50) {
                mWendu_max = 50;
            }
            if (mWendu_max < -30) {
                mWendu_max = 30;
            }
            if (mShidu_min < 0) {
                mShidu_min = 0;
            }
            if (mShidu_max > 100) {
                mShidu_max = 100;
            }
            String shidu_l = addZeroForNum(Integer.toHexString(mShidu_min), 2, false).toUpperCase();
            String shidu_h = addZeroForNum(Integer.toHexString(mShidu_max), 2, false).toUpperCase();
            String qiwei_l = addZeroForNum(Integer.toHexString(0), 2, false);
            String qiwei_h = addZeroForNum(Integer.toHexString(mQiwei_max), 2, false);
            KLog.e(mWendu_min + ":" + mWendu_max);
            String wendu_l = addZeroForNum(Integer.toHexString(mWendu_min), 8, false).substring(6, 8).toUpperCase();
            String wendu_h = addZeroForNum(Integer.toHexString(mWendu_max), 8, false).substring(6, 8).toUpperCase();

            String packinfo = "0" + mClience + "0" + mAuto + shidu_l + shidu_h + wendu_l + wendu_h + qiwei_l + qiwei_h;
            KLog.e(packinfo);
            String ssid = mUtil.getSSID().substring(13, 19);

            KLog.e(wendu_l + ":" + wendu_h);
            SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                    "12", packinfo.toUpperCase());
            KLog.i("SendStr", socketback.toString());
            if (mUtil != null) {
                mUtil.startSend(new SettingSocketMessage(socketback.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToast(getContext(), "环境设置失败,请重试");
        }

    }

    private void initRadiuGroup() {
        switch (mQiwei_max) {
            case 0:
                mRg.check(R.id.fra_envir_rb_yiban);
                break;
            case 1:
                mRg.check(R.id.fra_envir_rb_linming);
                break;
            case 2:
                mRg.check(R.id.fra_envir_rb_minggan);
                break;
        }
        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.fra_envir_rb_yiban:
                        mQiwei_max = 0;
                        break;
                    case R.id.fra_envir_rb_linming:
                        mQiwei_max = 1;
                        break;
                    case R.id.fra_envir_rb_minggan:
                        mQiwei_max = 2;
                        break;
                }
            }
        });
    }

    private void initRangeBar() {
        mBarTemp.setBeginValue(-30);
        mBarTemp.setFinishValue(50);
        mBarTemp.setIndex("℃");
        mBarTemp.setInitValue(mWendu_min, mWendu_max);
        mBarTemp.setRangeChangeListener(new OnRangeChangedListener() {
            @Override
            public void selected(int i, int i1) {
                mWendu_min = i;
                mWendu_max = i1;
                KLog.e(mWendu_min + ":" + mShidu_max);
            }
        });
        mBarHumi.setBeginValue(0);
        mBarHumi.setFinishValue(100);
        mBarHumi.setInitValue(mShidu_min, mShidu_max);
        mBarHumi.setIndex("%");
        mBarHumi.setRangeChangeListener(new OnRangeChangedListener() {
            @Override
            public void selected(int start, int end) {
                mShidu_min = start;
                mShidu_max = end;
                KLog.e(mShidu_min + ":" + mShidu_max);
            }
        });

    }

    @Override
    public void initWindow(WindowManager.LayoutParams params) {
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
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
        if (mDialogDismissListener != null) {
            mDialogDismissListener.dismiss(mWendu_min, mWendu_max, mShidu_min, mShidu_max, mQiwei_max, mAuto, mClience);
        }
    }
}
