package com.realdolmen.multifinger.connection;

public interface ConnectionCallback {
    void onDataReceived(byte[] bytes);
}
