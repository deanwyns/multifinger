package com.realdolmen.multifinger.bluetooth;

import android.bluetooth.BluetoothDevice;

public class DeviceEntry {
    private String name;
    private String address;

    public DeviceEntry(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return getName();
    }
}
