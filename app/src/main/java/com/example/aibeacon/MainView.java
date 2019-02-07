package com.example.aibeacon;

public interface MainView {
    void checkForLocationPermission();
    void setDevicesRecycler();
    void setBLE();
    void setBeacon();
    void startScan();
    void stopScan();
}
