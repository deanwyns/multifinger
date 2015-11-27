package com.realdolmen.multifinger.connection;

import android.os.Handler;

import java.util.List;

public interface Connection {
    int CONNECTED = 1;
    int CLIENT_CONNECTED = 2;
    int MESSAGE_READ = 3;

    enum Commands {
        CLEAR,
        STROKE_DRAWN
    }

    boolean isAvailable();

    void connect(Device device, Handler dataHandler);
    void host(Handler dataHandler);
    void disconnect();

    void write(NetworkCommand command);

    List<Device> getUsers();
}
