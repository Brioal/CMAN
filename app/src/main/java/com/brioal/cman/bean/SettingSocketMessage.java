package com.brioal.cman.bean;

/**
 * Created by brioal on 16-11-6.
 * Email : brioal@foxmail.com
 * Github : https://github.com/Brioal
 */

public class SettingSocketMessage extends SocketMessage {

    public SettingSocketMessage(String msg) {
        setTitle(msg);
        setType(TYPE_SETTING);
    }
}
