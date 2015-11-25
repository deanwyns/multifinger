package com.realdolmen.multifinger.connection.bluetooth;

import android.bluetooth.BluetoothSocket;

import com.realdolmen.multifinger.connection.ConnectionCallback;
import com.realdolmen.multifinger.connection.NetworkCommand;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by DWSAX40 on 25/11/2015.
 */
public class ConnectionThread extends Thread {
    private ConnectionCallback callback;
    private final BluetoothSocket mmSocket;
    private final ObjectInputStream mmInStream;
    private final ObjectOutputStream mmOutStream;

    public ConnectionThread(BluetoothSocket socket, ConnectionCallback callback) {
        this.callback = callback;

        mmSocket = socket;
        ObjectOutputStream tmpOut = null;
        ObjectInputStream tmpIn = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            tmpOut.flush();
            tmpIn = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) { e.printStackTrace(); }

        mmOutStream = tmpOut;
        mmInStream = tmpIn;
    }

    public void run() {
        //byte[] buffer = new byte[95];  // buffer store for the stream
        //int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                //bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                try {
                    NetworkCommand command = (NetworkCommand)mmInStream.readObject();
                    callback.onDataReceived(command);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(Object object) {
        try {
            mmOutStream.writeObject(object);
            mmOutStream.flush();
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}