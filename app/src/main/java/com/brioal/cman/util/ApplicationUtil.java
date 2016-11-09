package com.brioal.cman.util;

import android.app.Application;
import android.graphics.Typeface;

import com.brioal.baselib.util.klog.KLog;
import com.brioal.cman.Ui.DeviceDetailActivity;
import com.brioal.cman.bean.SocketMessage;
import com.brioal.cman.interfaces.OnDeviceConnectedListener;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * 与远程服务器通讯的类，供多个活动页面使用同一个socket连接
 */
public class ApplicationUtil extends Application {
    private Socket socket;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    public static boolean isconnect = false;
    private String mSSID = "";

    public String getSSID() {
        return mSSID;
    }

    private Typeface mTypeface;
    private DeviceDetailActivity mMainActivity;
    private List<SocketMessage> mMessageQueue = new ArrayList<>();
    private OnDeviceConnectedListener mConnectedListener;

    public void setConnectedListener(OnDeviceConnectedListener connectedListener) {
        mConnectedListener = connectedListener;
    }

    public void setDeviceDetailActivity(DeviceDetailActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void setSSID(String SSID) {
        mSSID = SSID;
    }

    public void finishDeviceActivity() {
        if (mMainActivity != null) {
            mMainActivity.finish();
        }
        mMainActivity = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTypeface = Typeface.createFromAsset(getAssets(), "fz.ttf");
        try {
            Field field = Typeface.class.getDeclaredField("SERIF");
            field.setAccessible(true);
            field.set(null, mTypeface);
        } catch (Exception e) {

        }
    }

    public void startSend(SocketMessage message) {
        if (message.getType() == SocketMessage.TYPE_FREE) {
            message.setTitle("[login]|" + message.getTitle());
        } else {
            //删除所有的空闲报文
            Iterator<SocketMessage> iterator = mMessageQueue.iterator();
            while (iterator.hasNext()) {
                SocketMessage i = iterator.next();
                if (i.getType() == SocketMessage.TYPE_FREE) {
                    iterator.remove();
                }
            }
        }
        mMessageQueue.add(message);
    }

    public void initSendThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    synchronized (ArrayList.class) {
                        if (mMessageQueue.size() > 0) {
                            try {
                                sendSetting(mMessageQueue.get(0).getTitle());
                                mMessageQueue.remove(0);
                                Thread.sleep(3000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }
            }
        };
        thread.start();
    }

    public boolean isSocketConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public void init(String ssid) {
        this.mSSID = ssid;
        try {
            while (this.out == null || this.in == null) {
                this.socket = new Socket("192.168.4.1", 8080);
                this.out = new DataOutputStream(socket.getOutputStream());
                this.in = new DataInputStream(socket.getInputStream());
            }
            setIsconnect(true);
        } catch (Exception e) {//服务器未开启
            e.printStackTrace();
            setIsconnect(false);
        }
    }

    public void socketClose() {
        if (socket != null) {
            try {
                out.close();
                in.close();
                socket.close();
                out = null;
                in = null;
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //发送设置报文
    public void sendSetting(final String msg) {
        KLog.e("SendMessage");
        try {
            if (!isIsconnect()) {
                init(mSSID);
                out = getOut();
                in = getIn();
            }
            if (out == null) {
                out = getOut();
            }
            if (out != null) {
                OutputStreamWriter outSW = new OutputStreamWriter(out, "GBK");
                BufferedWriter bw = new BufferedWriter(outSW);
                bw.write(msg);
                bw.flush();
                outSW.flush();
                out.flush();
                KLog.e("发送成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mConnectedListener != null) {
                mConnectedListener.onDisConnected();
            }
        }

    }

    public DataOutputStream getOut() {
        return out;
    }


    public DataInputStream getIn() {
        return in;
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }

    public boolean isIsconnect() {
        return isconnect;
    }

    public void setIsconnect(boolean Isconnect) {
        isconnect = Isconnect;
    }
}

