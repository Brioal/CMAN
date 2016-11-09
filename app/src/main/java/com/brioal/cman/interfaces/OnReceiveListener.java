package com.brioal.cman.interfaces;

/**
 * Created by Brioal on 2016/10/9.
 */

public interface OnReceiveListener {
    void received(String msg);

    void failed(String error);
}
