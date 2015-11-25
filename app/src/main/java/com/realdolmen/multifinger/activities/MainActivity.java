package com.realdolmen.multifinger.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.realdolmen.multifinger.R;
import com.realdolmen.multifinger.connection.bluetooth.DeviceEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    public static final UUID MULTIFINGER_UUID = UUID.fromString("0e164db8-8d1e-4486-9d3f-6182e4b410da");
    public static final String MULTFINGER_SERVICE = "Multifinger";

    public static final int REQUEST_BLUETOOTH = 1;

    @Bind(R.id.bluetoothDevicesListView)
    ListView bluetoothDevicesListView;

    @Bind(R.id.hostButton)
    Button hostButton;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Butter Knife injection
        ButterKnife.bind(this);

        // Find bluetooth devices
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        hostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DrawingActivity.class);
                intent.putExtra("HOST", true);
                startActivity(intent);
            }
        });

        final List<DeviceEntry> deviceEntries = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bluetoothDevice : pairedDevices) {
            deviceEntries.add(new DeviceEntry(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
        }

        ArrayAdapter<DeviceEntry> bluetoothDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_device_list_item, deviceEntries);
        bluetoothDevicesListView.setAdapter(bluetoothDeviceArrayAdapter);

        bluetoothDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceEntry deviceEntry = (DeviceEntry) parent.getItemAtPosition(position);
                if (deviceEntry == null)
                    return;

                Intent intent = new Intent(getApplicationContext(), DrawingActivity.class);
                intent.putExtra("BLUETOOTH_DEVICE", deviceEntry.getName());
                startActivity(intent);
            }
        });
    }
}