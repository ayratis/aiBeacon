package com.example.aibeacon;

public class MainPresenter {

    private MainView view;


    public void attach(MainView view){
        this.view = view;
        view.checkForLocationPermission();
        view.setDevicesRecycler();
        view.setBLE();
        view.setBeacon();
    }

    public void detach(){
        view = null;
    }

    public void startScan(){
        view.startScan();
    }

    public void stopScan(){
        view.stopScan();
    }
}
