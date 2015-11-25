package com.realdolmen.multifinger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.realdolmen.multifinger.threads.ClientThread;
import com.realdolmen.multifinger.threads.ConnectedThread;
import com.realdolmen.multifinger.threads.ServerThread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import yuku.ambilwarna.AmbilWarnaDialog;

public class DrawingActivity extends AppCompatActivity {

    @Bind(R.id.clearButton)
    Button clearButton;
    @Bind(R.id.colorPickerButton)
    Button colorPickerButton;
    private GraphicsFragment graphicsFragment;

    private BluetoothAdapter mBluetoothAdapter;

    private ConnectedThread connectedThread;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        ButterKnife.bind(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        graphicsFragment = (GraphicsFragment) getFragmentManager().findFragmentById(R.id.drawingFragment);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicsFragment.clearView();
            }
        });

        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorpicker();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras.getBoolean("HOST")) {
            new ServerThread(mBluetoothAdapter, mHandler).start();
        } else {
            String bluetoothDeviceName = extras.getString("BLUETOOTH_DEVICE");
            if(bluetoothDeviceName != null) {
                BluetoothDevice bluetoothDevice = null;
                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : devices) {
                    if(device.getName().equals(bluetoothDeviceName))
                        bluetoothDevice = device;
                }

                if(bluetoothDevice != null)
                    new ClientThread(bluetoothDevice, mBluetoothAdapter, connectedThread, mHandler).start();
            }
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    TouchMoveDto touchMoveDto = (TouchMoveDto)convertFromBytes((byte[])msg.obj);
                    graphicsFragment.dv.touch_move(touchMoveDto.getX(), touchMoveDto.getY());
                    graphicsFragment.dv.invalidate();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void colorpicker() {
        //     initialColor is the initially-selected color to be shown in the rectangle on the left of the arrow.
        //     for example, 0xff000000 is black, 0xff0000ff is blue. Please be aware of the initial 0xff which is the alpha.

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xff0000ff, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            // Executes, when user click Cancel button
            @Override
            public void onCancel(AmbilWarnaDialog dialog){
            }

            // Executes, when user click OK button
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                graphicsFragment.setPaintColor(color);
                Toast.makeText(getBaseContext(), "Selected Color : " + color, Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    public void write(byte[] bytes) {
        connectedThread.write(bytes);
    }


    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}
