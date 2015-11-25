package com.realdolmen.multifinger.connection;

public interface ClientCallback extends ConnectionCallback {
    void onConnected(Thread connection);
}
