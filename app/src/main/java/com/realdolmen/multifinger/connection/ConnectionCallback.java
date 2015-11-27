package com.realdolmen.multifinger.connection;

public interface ConnectionCallback {
    void onDataReceived(NetworkCommand command);
    void onDisconnect();
}
