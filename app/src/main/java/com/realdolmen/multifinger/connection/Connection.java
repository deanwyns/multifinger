package com.realdolmen.multifinger.connection;

import android.os.Handler;

import java.util.List;

public interface Connection {
    int MESSAGE_READ = 1;

    enum Commands {
        CLEAR,
        STROKE_DRAWN
    }

    boolean isAvailable();

    void connect(Device device, Handler dataHandler);
    void host(Handler dataHandler);

    void write(Connection.Commands command, byte[] bytes);

    List<Device> getUsers();
}
