package com.abc.projectcba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class List extends AppCompatActivity {
    public static final int REQUEST_ACCESS_ENABLE_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private ListView deviceList;
    private BluetoothAdapter bluetoothAdapter;
    private Button scanningBtn;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = findViewById(R.id.list_view);
        scanningBtn = findViewById(R.id.connect);

        //create simple array for device list
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        deviceList.setAdapter(listAdapter);

        //check bluetooth state
        checkBluetoothState();

        //Register dedicated receiver
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        scanningBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    //check coarse location
                    if (checkCoarseLocationPermission()) {
                        listAdapter.clear();
                        bluetoothAdapter.startDiscovery();
                    }
                } else {
                    checkBluetoothState();
                }
            }
        });
        //Check permission on start
        checkCoarseLocationPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(deviceFoundReceiver);
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(List.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_ENABLE_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private void checkBluetoothState() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(this, "Scanning", Toast.LENGTH_SHORT).show();
                    scanningBtn.setEnabled(true);
                } else {
                    Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
                    scanningBtn.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "You need to enabled bluetooth", Toast.LENGTH_SHORT).show();
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            checkBluetoothState();
        }
    }

    //@Override
    public void setRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCESS_ENABLE_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResuslts[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access location is allowed", Toast.LENGTH_SHORT).show();
                    scanningBtn.setEnabled(true);
                } else {
                    Toast.makeText(this, "Access location is not allowed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //Implementation of receiver when  devices detected

    private final BroadcastReceiver deviceFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(List.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCod  e, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                listAdapter.add(device.getName());
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                scanningBtn.setText("Scanning Finished");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                scanningBtn.setText("Scanning");
            }
        }
    };
    //Finished
}
