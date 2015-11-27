package com.realdolmen.multifinger.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.inject.Inject;
import com.realdolmen.multifinger.R;
import com.realdolmen.multifinger.connection.Connection;
import com.realdolmen.multifinger.connection.Device;
import com.realdolmen.multifinger.connection.NetworkCommand;
import com.realdolmen.multifinger.connection.StrokeDto;
import com.realdolmen.multifinger.connection.bluetooth.ConversionUtil;
import com.realdolmen.multifinger.fragments.GraphicsFragment;
import com.realdolmen.multifinger.views.DrawingView;

import java.nio.ByteBuffer;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import yuku.ambilwarna.AmbilWarnaDialog;

@ContentView(R.layout.activity_drawing)
public class DrawingActivity extends RoboActivity implements NumberPicker.OnValueChangeListener {
    public static final int MESSAGE_READ = 1;

    @InjectView(R.id.clearButton)
    private Button clearButton;
    @InjectView(R.id.colorPickerButton)
    private Button colorPickerButton;
    //@InjectFragment(R.id.drawingFragment)
    private GraphicsFragment graphicsFragment;
    @InjectView(R.id.widthNumberPicker)
    private NumberPicker widthNumberPicker;

    @Inject
    private Connection connection;
    @Inject
    private ConversionUtil conversionUtil;

    @Override
    public void onBackPressed() {
        connection.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        graphicsFragment = (GraphicsFragment)getFragmentManager().findFragmentById(R.id.drawingFragment);

        widthNumberPicker.setMaxValue(127);
        widthNumberPicker.setMinValue(1);
        widthNumberPicker.setWrapSelectorWheel(false);
        widthNumberPicker.setValue(DrawingView.strokeWidth);
        widthNumberPicker.setOnValueChangedListener(this);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicsFragment.clearView(true);
            }
        });

        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorpicker();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null)
            return;

        final ProgressDialog serverDialog = new ProgressDialog(this);
        serverDialog.setTitle("Hosting");
        serverDialog.setMessage("Waiting for player to connect...");
        serverDialog.setIndeterminate(true);

        final ProgressDialog clientDialog = new ProgressDialog(this);
        clientDialog.setTitle("Connecting");
        clientDialog.setMessage("Please wait...");
        clientDialog.setIndeterminate(true);

        final Handler dataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case Connection.CONNECTED:
                        clientDialog.dismiss();
                        break;
                    case Connection.CLIENT_CONNECTED:
                        serverDialog.dismiss();
                        break;
                    case Connection.MESSAGE_READ:
                        handleDataReceived((NetworkCommand) msg.obj);
                        break;
                }
            }
        };

        if(extras.getBoolean("HOST")) {
            serverDialog.show();
            connection.host(dataHandler);
        } else {
            clientDialog.show();
            Device device = extras.getParcelable("DEVICE");
            connection.connect(device, dataHandler);
        }
    }

    private void handleDataReceived(NetworkCommand command) {
        switch(command.getCommand()) {
            case CLEAR:
                graphicsFragment.clearView(false);
                break;

            case STROKE_DRAWN:
                graphicsFragment.drawOpponentStroke((StrokeDto)command.getDto());
                break;
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
            }
        });
        dialog.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        graphicsFragment.setStrokeWidth(newVal);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}
