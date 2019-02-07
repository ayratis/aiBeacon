package com.example.aibeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_LOCATION_REQUEST_CODE = 11;

    private RecyclerView devicesRecycler;
    private Button scanButton;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private boolean isScanning = false;

    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21;i:22-23,p:24-24";
//    private static final String ALTBEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21;i:22-23,p:24-24,d:25-25";
    private BeaconManager beaconManager;
    private Region beaconRegion;

    private DevicesListAdapter devicesListAdapter;

    private BluetoothAdapter.LeScanCallback leScanCallback;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == AppCompatActivity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.cant_work_without_bt, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, getString(R.string.cant_work_without_location), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesRecycler = findViewById(R.id.devicesRecycler);
        scanButton = findViewById(R.id.scanButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }

        devicesListAdapter = new DevicesListAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        devicesRecycler.setLayoutManager(layoutManager);
        devicesRecycler.setAdapter(devicesListAdapter);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        leScanCallback = (bluetoothDevice, rssi, scanRecord) -> devicesListAdapter.addDevice(bluetoothDevice, rssi);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_LAYOUT));
        beaconManager.bind(this);


        scanButton.setOnClickListener(v -> {
            if (!isScanning) {
                scanButton.setText("STOP");
                isScanning = true;
                startBeaconMonitoring();
                bluetoothAdapter.startLeScan(leScanCallback);
            } else {
                bluetoothAdapter.stopLeScan(leScanCallback);
                stopBeaconMonitoring();
                isScanning = false;
                scanButton.setText("SCAN");
                devicesListAdapter.clear();
                devicesRecycler.getAdapter().notifyDataSetChanged();
            }
        });


    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.addRangeNotifier((collection, region) -> {
            if (collection.size() > 0) {
                devicesListAdapter.addBeacon(collection.iterator().next());
            }
        });
    }

    private void startBeaconMonitoring() {
        try {
            beaconRegion = new Region("e9131178-4833-41cd-af5b-9d132f4d3770",
                    null, null, null);

            beaconManager.startRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopBeaconMonitoring() {
        try {

            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
