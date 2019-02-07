package com.example.aibeacon;


import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.ViewHolder> {

    private static final int TYPE_BLE = 0;
    private static final int TYPE_IBEACON = 1;

    private List<UniversalDeviceModel> bluetoothDeviceList = new ArrayList<>();

    public DevicesListAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.devices_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (bluetoothDeviceList.get(i).getType() == TYPE_IBEACON) {
            viewHolder.deviceParams.setText(viewHolder.deviceParams.getContext().getString(R.string.beacon,
                    bluetoothDeviceList.get(i).getBeacon().getId1().toString(),
                    String.valueOf(bluetoothDeviceList.get(i).getBeacon().getId2().toInt()),
                    String.valueOf(bluetoothDeviceList.get(i).getBeacon().getId3().toInt()),
                    String.valueOf(bluetoothDeviceList.get(i).getBeacon().getTxPower()),
                    String.valueOf(bluetoothDeviceList.get(i).getBeacon().getDistance()),
                    String.valueOf(bluetoothDeviceList.get(i).getBeacon().getRssi())));
        } else {
            viewHolder.deviceParams.setText(viewHolder.deviceParams.getContext().getString(R.string.ble_device,
                    bluetoothDeviceList.get(i).getDevice().getAddress(),
                    String.valueOf(bluetoothDeviceList.get(i).getDeviceRssi())));
        }
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceList.size();
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        if (!bluetoothDeviceList.isEmpty()) {
            for (int i = 0; i < bluetoothDeviceList.size(); i++) {
                if (bluetoothDeviceList.get(i).getType() == TYPE_BLE) {
                    if (device.getAddress().equals(bluetoothDeviceList.get(i).getDevice().getAddress())) {
                        bluetoothDeviceList.get(i).setDevice(device);
                        bluetoothDeviceList.get(i).setDeviceRssi(rssi);
                        Collections.sort(bluetoothDeviceList, (o1, o2) ->
                                (o2.getDeviceRssi() - o1.getDeviceRssi()));
                        notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
        UniversalDeviceModel universalDeviceModel = new UniversalDeviceModel();
        universalDeviceModel.setType(TYPE_BLE);
        universalDeviceModel.setDevice(device);
        universalDeviceModel.setDeviceRssi(rssi);
        bluetoothDeviceList.add(universalDeviceModel);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addBeacon(Beacon beacon) {
        if (!bluetoothDeviceList.isEmpty()) {
            for (int i = 0; i < bluetoothDeviceList.size(); i++) {
                if (bluetoothDeviceList.get(i).getType() == TYPE_IBEACON) {
                    if (beacon.getId1().equals(bluetoothDeviceList.get(i).getBeacon().getId1())) {
                        bluetoothDeviceList.get(i).setBeacon(beacon);
                        bluetoothDeviceList.get(i).setDeviceRssi(beacon.getRssi());
                        Collections.sort(bluetoothDeviceList, (o1, o2) ->
                                (o2.getDeviceRssi() - o1.getDeviceRssi()));
                        notifyDataSetChanged();
                        return;
                    }
                }

            }

        }
        UniversalDeviceModel universalDeviceModel = new UniversalDeviceModel();
        universalDeviceModel.setType(TYPE_IBEACON);
        universalDeviceModel.setBeacon(beacon);
        universalDeviceModel.setDeviceRssi(beacon.getRssi());
        bluetoothDeviceList.add(universalDeviceModel);
        notifyItemInserted(getItemCount() - 1);
    }

    public void clear() {
        bluetoothDeviceList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceParams;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceParams = itemView.findViewById(R.id.deviceParams);
        }
    }
}

