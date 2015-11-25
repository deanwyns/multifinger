package com.realdolmen.multifinger.config;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.bluetooth.BluetoothConnection;

public class GuiceConfig implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(Connection.class).to(BluetoothConnection.class);
    }
}
