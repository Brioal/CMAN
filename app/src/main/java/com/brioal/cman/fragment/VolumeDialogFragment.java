package com.brioal.cman.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

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
 * 音量设置DialogFragment
 * Dialog Fragment To Adjust Volume
 * Created by Brioal on 2016/9/15.
 */
public class VolumeDialogFragment extends BaseDialogFragment {
    @Bind(R.id.volume_btn_close)
    ImageButton mBtnClose;
    @Bind(R.id.volume_btn_done)
    ImageButton mBtnDone;
    @Bind(R.id.fra_volume_tv_current)
    TextView mTvCurrent;
    @Bind(R.id.fra_volume_seek)
    SeekBar mSeek;

    private static VolumeDialogFragment sFragment;
    private Context mContext;
    private int mCurrentVolume = 100;//当前音量
    //Socket通信连接工具累
    private ApplicationUtil mUtil;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isSendFree = false;
    //监听器
    private OnDialogDismissListener mOnDialogDismissListener;


    /*
    * 参数设置
    * */
    //设置Dialog消失监听器
    public VolumeDialogFragment setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
        mOnDialogDismissListener = onDialogDismissListener;
        return this;
    }

    //设置当前音量
    public void setCurrentVolume(int currentVolume) {
        mCurrentVolume = currentVolume;
    }

    public static VolumeDialogFragment getInstance(int volume) {
        if (sFragment == null) {
            sFragment = new VolumeDialogFragment();
        }
        sFragment.setCurrentVolume(volume);
        return sFragment;
    }

    @Override
    public void initVar() {
        mContext = getActivity();
    }

    @Override
    public void initView() {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_volume, null, false);
        ButterKnife.bind(this, mRootView);
        initAction();//初始化按钮事件
        initConnect();//初始化连接
    }

    //初始化连接
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

    //初始化动作事件
    private void initAction() {
        mTvCurrent.setText(mCurrentVolume + "");
        mSeek.setProgress(mCurrentVolume);
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurrentVolume = progress;
                mTvCurrent.setText(mCurrentVolume + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolumeDialogFragment.this.dismiss();
            }
        });
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KLog.e("设置音量");
                String ssid = mUtil.getSSID().substring(13, 19);
                SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                        "17", addZeroForNum(Integer.toHexString(mCurrentVolume), 2, false).toUpperCase());
                String sendStr = socketback.toString();
                mUtil.startSend(new SettingSocketMessage(sendStr));
                VolumeDialogFragment.this.dismiss();

            }
        });
    }

    //发送报文
    public void sendMsg(final String ss) {
        if (!isSendFree) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMsg(ss);
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
        }, 1000);//1秒后自动变成空闲
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
                VolumeDialogFragment.this.dismiss();
                mUtil.finishDeviceActivity();
            }
        }).create().show();
    }

    @Override
    public void initWindow(WindowManager.LayoutParams params) {
        params.gravity = Gravity.BOTTOM;
        params.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() / 4;
        params.width = getActivity().getWindowManager().getDefaultDisplay().getWidth();

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
        if (mOnDialogDismissListener != null) {
            mOnDialogDismissListener.dismiss(mCurrentVolume);
        }
    }

}
