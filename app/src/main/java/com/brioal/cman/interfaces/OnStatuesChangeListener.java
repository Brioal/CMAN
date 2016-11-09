package com.brioal.cman.interfaces;

/**
 * Created by Brioal on 2016/10/9.
 */

public interface OnStatuesChangeListener {
    void changed(int minWendu, int maxWendu, int minShidu, int maxShidu, int qiwei, int auto, int clience, int shuixiang, int jiankang, int currentWendu, int currentShidu, int currentQiwei,int jiare , int chushi , int chuchou, int currentVolume);//最低温度，最高温度 ，最低湿度，最高湿度，气味，自动模式 ， 静音模式 ， 水箱 ，健康 , 当前温度， 当前湿度 ， 当前气味
}
