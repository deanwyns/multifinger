package com.realdolmen.multifinger.connection;

public interface ServerCallback extends ConnectionCallback {
    void onServerStarted();
    void onClientConnected(Thread connection);
}
