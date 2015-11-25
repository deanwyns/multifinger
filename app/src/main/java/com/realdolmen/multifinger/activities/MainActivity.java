package com.realdolmen.multifinger.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.inject.Inject;
import com.realdolmen.multifinger.R;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.Device;

import java.util.List;
import java.util.UUID;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {
    public static final UUID MULTIFINGER_UUID = UUID.fromString("0e164db8-8d1e-4486-9d3f-6182e4b410da");
    public static final String MULTFINGER_SERVICE = "Multifinger";

    public static final int REQUEST_BLUETOOTH = 1;

    @InjectView(R.id.bluetoothDevicesListView)
    private ListView bluetoothDevicesListView;

    @InjectView(R.id.singlePlayerButton)
    Button singlePlayerButton;
    @InjectView(R.id.hostButton)
    private Button hostButton;

    @Inject
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drawingIntent = new Intent(getApplicationContext(), DrawingActivity.class);
                startActivity(drawingIntent);
            }
        });

        if (!connection.isAvailable()) {
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

        List<Device> devices = connection.getUsers();
        ArrayAdapter<Device> userArrayAdapter = new ArrayAdapter<Device>(this, R.layout.bluetooth_device_list_item, devices);
        bluetoothDevicesListView.setAdapter(userArrayAdapter);

        bluetoothDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = (Device) parent.getItemAtPosition(position);
                if (device == null)
                    return;

                Intent intent = new Intent(getApplicationContext(), DrawingActivity.class);
                intent.putExtra("DEVICE", device);
                startActivity(intent);
            }
        });
    }
}