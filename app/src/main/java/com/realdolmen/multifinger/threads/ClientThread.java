package com.realdolmen.multifinger.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.realdolmen.multifinger.MainActivity;

import java.io.IOException;

public class ClientThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectedThread connectedThread;
    private Handler mHandler;

    public ClientThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter, ConnectedThread connectedThread, Handler mHandler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.connectedThread = connectedThread;
        this.mHandler = mHandler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MainActivity.MULTIFINGER_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

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

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }


    private void manageConnectedSocket(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket, mHandler);
        connectedThread.start();
    }


    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}