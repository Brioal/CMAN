package com.brioal.cman.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.base.BaseDialogFragment;
import com.brioal.cman.bean.SettingSocketMessage;
import com.brioal.cman.entity.SocketBeanBack;
import com.brioal.cman.interfaces.OnDialogDismissListener;
import com.brioal.cman.util.ApplicationUtil;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.brioal.cman.entity.SocketBeanBack.addZeroForNum;

/**
 * 功能模块设置DialogFragment
 * Created by Brioal on 2016/9/15.
 */
public class FuncationChooseDialog extends BaseDialogFragment {

    @Bind(R.id.fra_envir_btn_close)
    ImageButton mBtnClose;
    @Bind(R.id.funcation_chushi)
    Switch mSWChushi;
    @Bind(R.id.funcation_chuchou)
    Switch mSWChuchou;
    @Bind(R.id.funcation_jiare)
    Switch mSWJiare;
    //模块参数
    private int mJiareVaue = 0; //加热 0关闭 1打开
    private int mChushiValue = 0; //除湿 0关闭 1打开
    private int mChuchouValue = 0; //气味 0关闭 1打开
    //设备连接
    private ApplicationUtil mUtil;
    private DataOutputStream out;
    private DataInputStream in;
    private String mSSID = "";
    private OnDialogDismissListener mDialogDismissListener;
    private static FuncationChooseDialog sFragment;
    private boolean isSendFree = false;
    private Context mContext;

    public void setSSID(String SSID) {
        mSSID = SSID;
    }

    public FuncationChooseDialog setDialogDismissListener(OnDialogDismissListener dialogDismissListener) {
        mDialogDismissListener = dialogDismissListener;
        return this;
    }


    public static FuncationChooseDialog newInstance(int jiare, int chushi, int qiwei) {
        if (sFragment == null) {
            sFragment = new FuncationChooseDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("Jiare", jiare);
        bundle.putInt("Chushi", chushi);
        bundle.putInt("Qiwei", qiwei);
        sFragment.setArguments(bundle);
        return sFragment;
    }

    @Override
    public void initVar() {
        try {
            mContext = getActivity();
            mJiareVaue = getArguments().getInt("Jiare");
            mChushiValue = getArguments().getInt("Chushi");
            mChuchouValue = getArguments().getInt("Qiwei");
            KLog.e("模块开关状态" + mJiareVaue + mChushiValue + mChuchouValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void send(final String ss) {
        if (!isSendFree) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    send(ss);
                }
            }, 1000);//2秒后重试
            return;
        }
        isSendFree = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isSendFree = true;
            }
        }, 1000);
        KLog.e("发送：" + ss);
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
                FuncationChooseDialog.this.dismiss();
                mUtil.finishDeviceActivity();
            }
        }).create().show();
    }

    @Override
    public void initView() {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_funcation_choose, null, false);
        ButterKnife.bind(this, mRootView);
        intiAction();
        initConnect();
        initSwitch();
    }

    private void initConnect() {
        try {
            isSendFree = true;
            mUtil = (ApplicationUtil) getActivity().getApplication();
            out = mUtil.getOut();
            in = mUtil.getIn();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initSwitch() {
        mSWChushi.setChecked(mChushiValue == 1);
        mSWChuchou.setChecked(mChuchouValue == 1);
        mSWJiare.setChecked(mJiareVaue == 1);
    }

    private void intiAction() {
        CompoundButton.OnClickListener checkedChangeListener = new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Switch switchView = (Switch) view;
                    String pack_cmd = "";
                    String sendStr = "";
                    switch (view.getId()) {
                        case R.id.funcation_jiare:
                            pack_cmd = "13";
                            break;
                        case R.id.funcation_chushi:
                            pack_cmd = "14";
                            break;
                        case R.id.funcation_chuchou:
                            pack_cmd = "15";
                            break;
                    }
                    String packinfo = "00";
                    if (switchView.isChecked()) {
                        packinfo = "01";
                        if (pack_cmd.equals("13")) {
                            mJiareVaue = 1;
                        } else if (pack_cmd.equals("14")) {
                            mChushiValue = 1;
                        } else if (pack_cmd.equals("15")) {
                            mChuchouValue = 1;
                        }
                    } else {
                        packinfo = "00";
                        if (pack_cmd.equals("13")) {
                            mJiareVaue = 0;
                        } else if (pack_cmd.equals("14")) {
                            mChushiValue = 0;
                        } else if (pack_cmd.equals("15")) {
                            mChuchouValue = 0;
                        }
                    }
                    String ssid = mUtil.getSSID().substring(13, 19);
                    SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                            pack_cmd, packinfo);
                    sendStr = socketback.toString();
//                    send(sendStr);
                    if (mUtil != null) {
                        mUtil.startSend(new SettingSocketMessage(sendStr));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FuncationChooseDialog.this.dismiss();
            }
        });
        mSWJiare.setOnClickListener(checkedChangeListener);
        mSWChushi.setOnClickListener(checkedChangeListener);
        mSWChuchou.setOnClickListener(checkedChangeListener);
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
            mDialogDismissListener.dismiss(mJiareVaue, mChushiValue, mChuchouValue);
        }
    }
}
