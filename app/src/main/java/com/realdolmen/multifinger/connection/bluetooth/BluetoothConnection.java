package com.realdolmen.multifinger.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

import com.google.inject.Singleton;
import com.realdolmen.multifinger.connection.ClientCallback;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.Device;
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
    public void write(Connection.Commands command, byte[] bytes) {
        if(connectionThread == null)
            return;

        ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 1);
        buffer.put((byte)command.ordinal());
        buffer.put(bytes);
        connectionThread.write(buffer.array());
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
        public void onDataReceived(byte[] bytes) {
            dataHandler.obtainMessage(MESSAGE_READ, bytes).sendToTarget();
        }
    }

    private class ClientCallbackImpl implements ClientCallback {

        @Override
        public void onConnected(Thread connection) {
            connectionThread = (ConnectionThread)connection;
        }

        @Override
        public void onDataReceived(byte[] bytes) {
            dataHandler.obtainMessage(MESSAGE_READ, bytes).sendToTarget();
        }
    }
}
