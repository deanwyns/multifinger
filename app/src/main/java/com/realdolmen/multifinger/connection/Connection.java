package com.realdolmen.multifinger.connection;

import android.os.Handler;

import com.google.inject.Singleton;

import java.util.List;

@Singleton
public interface Connection {
    int MESSAGE_READ = 1;

    boolean isAvailable();

    void connect(Device device, Handler dataHandler);
    void host(Handler dataHandler);

    void write(byte[] bytes);

    List<Device> getUsers();
}
