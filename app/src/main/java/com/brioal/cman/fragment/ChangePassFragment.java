package com.brioal.cman.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.R;
import com.brioal.cman.base.BaseDialogFragment;
import com.brioal.cman.bean.SettingSocketMessage;
import com.brioal.cman.data.DataLoader;
import com.brioal.cman.entity.DeviceEntity;
import com.brioal.cman.entity.SocketBeanBack;
import com.brioal.cman.interfaces.OnDialogDismissListener;
import com.brioal.cman.util.ApplicationUtil;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.brioal.cman.entity.SocketBeanBack.addZeroForNum;

/**
 * Change Wifi PassWord Dialog
 * Created by Brioal on 2016/9/14.
 */
public class ChangePassFragment extends BaseDialogFragment {
    private static ChangePassFragment sFragment;
    @Bind(R.id.change_pass_btn_close)
    ImageButton mBtnClose;
    @Bind(R.id.change_pass_btn_done)
    ImageButton mBtnDone;
    @Bind(R.id.change_pass_et_old)
    EditText mEtOld;
    @Bind(R.id.change_pass_et_new)
    EditText mEtNew;

    private DeviceEntity mDeviceEntity;

    private ApplicationUtil mUtil;
    private DataOutputStream out;
    private DataInputStream in;
    private Handler mHandler = new Handler();

    private OnDialogDismissListener mDismissListener;

    public static ChangePassFragment newInstance(DeviceEntity entity) {
        if (sFragment == null) {
            sFragment = new ChangePassFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("Entity", entity);
        sFragment.setArguments(bundle);
        return sFragment;
    }

    @Override
    public void initVar() {
        try {
            mDeviceEntity = (DeviceEntity) getArguments().getSerializable("Entity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDismissListener(OnDialogDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    private void send(String ss) {
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
        }
    }

    @Override
    public void initView() {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_change_password, null, false);
        ButterKnife.bind(this, mRootView);
        initAction();//初始化事件处理
        initConnect();//初始化连接
    }

    private void initConnect() {
        mUtil = (ApplicationUtil) getActivity().getApplication();
        out = mUtil.getOut();
        in = mUtil.getIn();
    }

    private void initAction() {
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDismissListener != null) {
                    mDismissListener.dismiss();
                }
                ChangePassFragment.this.dismiss();
            }
        });
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old = mEtOld.getText().toString();
                String newPass = mEtNew.getText().toString();
                KLog.e(old);
                KLog.e(newPass);
                if (old.isEmpty()) {
                    ToastUtils.showToast(getActivity(), "原密码不能为空");
                    mEtOld.findFocus();
                    return;
                }
                if (newPass.isEmpty()) {
                    ToastUtils.showToast(getActivity(), "新密码不能为空");
                    return;
                }
                if (!old.equals(mDeviceEntity.getPass())) {
                    ToastUtils.showToast(getActivity(), "原密码不匹配");
                    return;
                }
                if (newPass.length() < 8) {
                    ToastUtils.showToast(getActivity(), "密码长度过短");
                    return;
                }
                if (newPass.length() > 10) {
                    mEtNew.setError("密码过长");
                    return;
                }
                try {
                    DataLoader.newInstance(getActivity()).changePass(mDeviceEntity.getSSID(), newPass);
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < newPass.length(); i++) {
                        char ch = newPass.charAt(i);
                        buffer.append(Integer.toHexString((int) ch) + "");
                    }
                    String sendPass = buffer.toString().toUpperCase();
                    KLog.e("SendPass:" + sendPass);
                    SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(mDeviceEntity.getSSID().substring(13, 19), 8, false),
                            "18", addZero(sendPass, 20));
                    String sendStr = socketback.toString();
                    KLog.e("新密码", newPass);
                    KLog.e("修改密码" + sendStr);
                    mUtil.startSend(new SettingSocketMessage(sendStr));
                    showPassChanged();
                    showWait("请稍等", "正在修改密码中");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideWait();
                            showPassChanged();
                        }
                    }, 5500);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public String addZero(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    private ProgressDialog mProgressDialog;

    public void showWait(String title, String msg) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void hideWait() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showPassChanged() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("密码修改已完成").setMessage("密码已经修改，将会退回设备列表页面").setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ChangePassFragment.this.dismiss();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mUtil.finishDeviceActivity();
                    }
                }, 1000);

            }
        }).setCancelable(false).create().show();
    }


    @Override
    public void initWindow(WindowManager.LayoutParams params) {
        params.gravity = Gravity.CENTER;
        params.width = getActivity().getWindowManager().getDefaultDisplay().getWidth() - 50;
        params.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() / 3 - 50;
    }

    @Override
    public void initAnimation(Dialog dialog) {
        dialog.getWindow().setWindowAnimations(R.style.AnimationSlideBottom);
        dialog.setTitle("Wifi密码更换");
    }

    @Override
    public void changeTheme() {
        mTheme = R.style.WhiteBackDialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
