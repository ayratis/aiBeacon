package com.example.aibeacon;

import android.bluetooth.BluetoothDevice;

import org.altbeacon.beacon.Beacon;

public class UniversalDeviceModel {
    private int type;
    private Beacon beacon;
    private BluetoothDevice device;
    private int deviceRssi;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getDeviceRssi() {
        return deviceRssi;
    }

    public void setDeviceRssi(int deviceRssi) {
        this.deviceRssi = deviceRssi;
    }
}
