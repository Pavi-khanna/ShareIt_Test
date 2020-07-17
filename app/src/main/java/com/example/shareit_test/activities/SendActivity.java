package com.example.shareit_test.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareit_test.activities.DeviceListFragment.DeviceActionListener;
import com.example.shareit_test.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SendActivity extends Activity implements WifiP2pManager.ChannelListener, DeviceListFragment.DeviceActionListener {

    private final int SENDER = 1;
    //ListView senderListView;
    Button createhotspot,selectfilebutton;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    TextView file_path;
    String file_path_string = null;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    private WifiP2pManager.ActionListener ActionListener;
    private WifiP2pConfig group_config;
    private WifiP2pConfig.Builder config_builder;
    private Intent browseFilesIntent;
    private boolean connectionReady = false;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:
                if (resultCode == RESULT_OK) {
                    file_path_string = data.getData().getPath();
                    file_path.setText(file_path_string);
                }
                break;
        }

    }

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        Log.d("IN Send Activity", "State: " + isWifiP2pEnabled);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        createhotspot = findViewById(R.id.hotspot);
        selectfilebutton = findViewById(R.id.select_file);
        file_path = findViewById(R.id.file_path);
        //senderListView = findViewById(R.id.avail_devices_list);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);



        ArrayList<String> senderArrayList = new ArrayList<>();
        senderArrayList.add("IP 1");
        senderArrayList.add("IP 2");

        /*
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, senderArrayList);
        senderListView.setAdapter(arrayAdapter);

        senderListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent browseFilesIntent = new Intent(SendActivity.this, BrowseFilesActivity.class);
                browseFilesIntent.putExtra("IP", senderListView.getItemAtPosition(i).toString());
                startActivity(browseFilesIntent);
            }
        });
         */

        browseFilesIntent = new Intent(SendActivity.this, BrowseFilesActivity.class);
        createhotspot.setVisibility(View.INVISIBLE);
        createhotspot.postDelayed(new Runnable() {
            public void run() {
                createhotspot.setVisibility(View.VISIBLE);
            }
        }, 5000);

        createhotspot.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    config_builder = new WifiP2pConfig.Builder();
                    group_config = new WifiP2pConfig();
                    config_builder.setNetworkName("DIRECT-NEW_HOTPOT");
                    config_builder.setPassphrase("yash12345");//8-16 req
                    config_builder.setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_AUTO);
                    //config_builder.enablePersistentMode(true);
                    group_config = config_builder.build();
                    manager.createGroup(channel,group_config,new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            // Device is ready to accept incoming connections from peers.
                            Log.d("SendActivity","Device ready group created");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(SendActivity.this, "P2P group creation failed. Retry. :"+reason,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SendActivity.this,"Cannot create a Hotpost.." +
                            "Please create manually",Toast.LENGTH_SHORT).show();
                    //open settings
                }

            }
        });

        selectfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectionReady) {
                    Intent filesOpener = new Intent(Intent.ACTION_GET_CONTENT);
                    filesOpener.setType("*/*");
                    startActivityForResult(filesOpener, 1001);
                } else {
                    makeToast("No Peer Connection Established");
                }
            }
        });

        final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.avail_list_frag);
        fragment.onInitiateDiscovery();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(SendActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(SendActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.avail_list_frag);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void connect(final WifiP2pConfig config) {

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                browseFilesIntent.putExtra("IP", config.deviceAddress);
                connectionReady = true;
            }
            @Override
            public void onFailure(int reason) {
                Toast.makeText(SendActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.disconnect();
        Toast.makeText(SendActivity.this, "Disconnecting with the Peer",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connectionReady = false;
                // later
            }
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }
        });
    }
    @Override
    public void showDetails(WifiP2pDevice device) {
            Log.d("SendActivity",device.deviceName);
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            connect(config);
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.avail_list_frag);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(SendActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(SendActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void makeToast(String msg) {
        Toast.makeText(SendActivity.this,
                msg,
                Toast.LENGTH_LONG).show();
    }

    public void makeServer(){
        // TODO: create a textView for display and call the server AsyncTask
        new Server(this, file_path, SENDER, file_path_string);
    }


    public void makeClient(String destIP){
        // TODO: create a textView for display and call the server AsyncTask
        //new Client(this, textView, RECEIVER, destIP, filepath);
    }

    public void setMobileDataState(boolean mobileDataEnabled)
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error setting mobile data state", ex);
        }
    }

    public boolean getMobileDataState()
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
            boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
            return mobileDataEnabled;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error getting mobile data state", ex);
        }

        return false;
    }
}