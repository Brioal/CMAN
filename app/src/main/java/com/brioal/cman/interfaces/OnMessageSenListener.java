package com.brioal.cman.interfaces;

/**
 * Created by brioal on 16-11-5.
 * Email : brioal@foxmail.com
 * Github : https://github.com/Brioal
 */

public interface OnMessageSenListener {
    void succeed();

    void failed(String message);
}
