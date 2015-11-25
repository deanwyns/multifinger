package com.realdolmen.multifinger.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.realdolmen.multifinger.activities.MainActivity;
import com.realdolmen.multifinger.connection.ServerCallback;

import java.io.IOException;

public class ServerThread extends Thread {
    private ServerCallback callback;
    private final BluetoothServerSocket mmServerSocket;

    public ServerThread(ServerCallback callback) {
        this.callback = callback;

        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(MainActivity.MULTFINGER_SERVICE, MainActivity.MULTIFINGER_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                callback.onServerStarted();
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }

            // If a connection was accepted
            if (socket != null) {
                ConnectionThread connectionThread = new ConnectionThread(socket, callback);
                callback.onClientConnected(connectionThread);
                connectionThread.start();

                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}