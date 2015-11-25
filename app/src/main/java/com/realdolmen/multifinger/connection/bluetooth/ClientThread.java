package com.realdolmen.multifinger.connection.bluetooth;

import android.bluetooth.*;

import com.realdolmen.multifinger.activities.MainActivity;
import com.realdolmen.multifinger.connection.ClientCallback;

import java.io.IOException;

public class ClientThread extends Thread {
    private ClientCallback callback;
    private final BluetoothSocket mmSocket;
    private final android.bluetooth.BluetoothDevice mmDevice;

    public ClientThread(BluetoothDevice device, ClientCallback callback) {
        this.callback = callback;

        android.bluetooth.BluetoothDevice realDevice = device.getDevice();
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = realDevice;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = realDevice.createRfcommSocketToServiceRecord(MainActivity.MULTIFINGER_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        ConnectionThread connectionThread = new ConnectionThread(mmSocket, callback);
        callback.onConnected(connectionThread);
        connectionThread.start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}