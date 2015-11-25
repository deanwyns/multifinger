package com.realdolmen.multifinger.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.inject.Inject;
import com.realdolmen.multifinger.R;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.Device;
import com.realdolmen.multifinger.connection.StrokeDto;
import com.realdolmen.multifinger.connection.bluetooth.ConversionUtil;
import com.realdolmen.multifinger.fragments.GraphicsFragment;

import java.nio.ByteBuffer;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import yuku.ambilwarna.AmbilWarnaDialog;

@ContentView(R.layout.activity_drawing)
public class DrawingActivity extends RoboActivity {
    public static final int MESSAGE_READ = 1;

    @InjectView(R.id.clearButton)
    Button clearButton;
    @InjectView(R.id.colorPickerButton)
    Button colorPickerButton;
    //@InjectFragment(R.id.drawingFragment)
    private GraphicsFragment graphicsFragment;

    @Inject
    private Connection connection;
    @Inject
    private ConversionUtil conversionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        graphicsFragment = (GraphicsFragment)getFragmentManager().findFragmentById(R.id.drawingFragment);

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

        final Handler dataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == Connection.MESSAGE_READ)
                    handleDataReceived((byte[]) msg.obj);
            }
        };

        Bundle extras = getIntent().getExtras();
        if(extras == null)
            return;

        if(extras.getBoolean("HOST")) {
            connection.host(dataHandler);
        } else {
            Device device = extras.getParcelable("DEVICE");
            connection.connect(device, dataHandler);
        }
    }

    private void handleDataReceived(byte[] bytes) {
        for(int i = 0; i < bytes.length; i += 18) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes, i, 18);
            StrokeDto strokeDto = conversionUtil.fromBytes(buffer.array());
            graphicsFragment.drawOpponentStroke(strokeDto);
        }
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
}
