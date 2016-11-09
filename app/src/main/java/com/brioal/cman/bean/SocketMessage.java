package com.brioal.cman.bean;

/**
 * Created by brioal on 16-11-6.
 * Email : brioal@foxmail.com
 * Github : https://github.com/Brioal
 */

public  class SocketMessage {
    public static final int TYPE_FREE = 0;
    public static final int TYPE_SETTING = 1;

    String mTitle;
    int mType;
    //是否是空闲报文
    public boolean isFree() {
        return mType == TYPE_FREE;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
