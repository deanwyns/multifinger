package com.realdolmen.multifinger.connection.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

import com.realdolmen.multifinger.connection.Device;

public class BluetoothDevice implements Device {
    private android.bluetooth.BluetoothDevice device;

    public BluetoothDevice(android.bluetooth.BluetoothDevice device) {
        this.device = device;
    }

    public android.bluetooth.BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(android.bluetooth.BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return this.device.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
    }

    private BluetoothDevice(Parcel in) {
        this.device = in.readParcelable(android.bluetooth.BluetoothDevice.class.getClassLoader());
    }

    public static final Parcelable.Creator<BluetoothDevice> CREATOR = new Parcelable.Creator<BluetoothDevice>() {

        @Override
        public BluetoothDevice createFromParcel(Parcel source) {
            return new BluetoothDevice(source);
        }

        @Override
        public BluetoothDevice[] newArray(int size) {
            return new BluetoothDevice[size];
        }
    };
}
