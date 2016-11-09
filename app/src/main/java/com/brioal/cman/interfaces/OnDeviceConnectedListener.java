package com.brioal.cman.interfaces;



public interface OnDeviceConnectedListener {
    void connected();

    void falied(String errorMessage);

    void onDisConnected();

    void connectedTimeOut();
}
