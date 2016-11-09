package com.brioal.cman.bean;

import com.brioal.cman.entity.SocketBeanBack;

import static com.brioal.cman.entity.SocketBeanBack.addZeroForNum;

/**
 * 空闲报文
 * Created by brioal on 16-11-6.
 * Email : brioal@foxmail.com
 * Github : https://github.com/Brioal
 */

public class FreeSocketMessage extends SocketMessage {

    public FreeSocketMessage(String SSID) {
        String ssid = SSID.substring(13, 19);
        SocketBeanBack socketback = new SocketBeanBack(addZeroForNum(ssid, 8, false),
                "10", "");
        String sendStr = socketback.toString();
        setTitle(sendStr);
        setType(SocketMessage.TYPE_FREE);
    }
}
