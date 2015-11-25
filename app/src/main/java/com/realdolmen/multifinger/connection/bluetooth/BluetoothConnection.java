package com.realdolmen.multifinger.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.net.Network;
import android.os.Handler;

import com.google.inject.Singleton;
import com.realdolmen.multifinger.connection.ClientCallback;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.Device;
import com.realdolmen.multifinger.connection.NetworkCommand;
import com.realdolmen.multifinger.connection.ServerCallback;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BluetoothConnection implements Connection {
    private ConnectionThread connectionThread;
    private Handler dataHandler;
    private BluetoothAdapter mAdapter;
    private boolean isConnected;

    public BluetoothConnection() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public boolean isAvailable() {
        return mAdapter.isEnabled();
    }

    @Override
    public void connect(Device device, Handler dataHandler) {
        if(isConnected)
            throw new IllegalStateException("Connection already started");

        this.dataHandler = dataHandler;
        new ClientThread((BluetoothDevice) device, new ClientCallbackImpl()).start();
        this.isConnected = true;
    }

    @Override
    public void host(Handler dataHandler) {
        if(isConnected)
            throw new IllegalStateException("Connection already started");

        this.dataHandler = dataHandler;
        new ServerThread(new ServerCallbackImpl()).start();
        this.isConnected = true;
    }

    @Override
    public void write(NetworkCommand command) {
        if(connectionThread == null)
            return;

        connectionThread.write(command);
    }

    @Override
    public List<Device> getUsers() {
        List<Device> devices = new ArrayList<>();
        for(android.bluetooth.BluetoothDevice bluetoothDevice : mAdapter.getBondedDevices()) {
            devices.add(new BluetoothDevice(bluetoothDevice));
        }

        return devices;
    }

    private class ServerCallbackImpl implements ServerCallback {

        @Override
        public void onServerStarted() {

        }

        @Override
        public void onClientConnected(Thread connection) {
            connectionThread = (ConnectionThread)connection;
        }

        @Override
        public void onDataReceived(NetworkCommand command) {
            dataHandler.obtainMessage(MESSAGE_READ, command).sendToTarget();
        }
    }

    private class ClientCallbackImpl implements ClientCallback {

        @Override
        public void onConnected(Thread connection) {
            connectionThread = (ConnectionThread)connection;
        }

        @Override
        public void onDataReceived(NetworkCommand command) {
            dataHandler.obtainMessage(MESSAGE_READ, command).sendToTarget();
        }
    }
}
