package com.brioal.cman.entity;

import java.io.Serializable;

/**
 * Data Entity For Device List Adapter
 * Created by Brioal on 2016/9/11.
 */
public class DeviceEntity implements Serializable, Comparable {
    private long mTime; //设备序号
    private String mTitle; //设备标题
    private String mSSID; //设置SSID名称
    private String mPass ;
    private String mMac; //设备MAC地址
    private boolean mOnLine = false; //是否在线
    private int mStren = 0;//0~-100

    public DeviceEntity(long time, String title, String SSID, String mac,String pass) {
        mTitle = title;
        this.mTime = time;
        mSSID = SSID;
        mMac = mac;
        mPass = pass;
    }
    //获取密码
    public String getPass() {
        return mPass;
    }
    //设置密码
    public void setPass(String pass) {
        mPass = pass;
    }

    public int getStren() {
        return mStren;
    }

    public void setStren(int stren) {
        mStren = stren;
    }

    public void setOnLine(boolean onLine) {
        mOnLine = onLine;
    }

    public boolean isOnLine() {
        return mOnLine;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String SSID) {
        mSSID = SSID;
    }

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    @Override
    public boolean equals(Object obj) { //SSID相同,则为同一设备
        return this.getSSID().equals(((DeviceEntity) obj).getSSID());
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    @Override
    public int compareTo(Object o) {
        DeviceEntity lh = (DeviceEntity) o;
        long lhIndex = lh.getTime();
        if (this.isOnLine()) {
            lhIndex *= 10;
        }
        return (this.getTime() + "").compareTo(lhIndex + "");
    }
}
