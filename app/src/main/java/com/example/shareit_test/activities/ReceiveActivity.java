package com.example.shareit_test.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.shareit_test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ReceiveActivity extends Activity implements WifiP2pManager.ChannelListener {

    private final int RECEIVER = 0;
    ListView receiverListView;
    TextView statusTextView;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    private WifiP2pManager.ActionListener ActionListener;
    WifiManager wifiManager;
    ArrayList<String> senderArrayList;

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        Log.d("IN Recv Activity", "State: " + isWifiP2pEnabled);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);


        receiverListView = findViewById(R.id.list_receive);
        senderArrayList = new ArrayList<String>();
        statusTextView = findViewById(R.id.status_text);

        /*wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        */

        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,senderArrayList);
        //receiverListView.setAdapter(arrayAdapter);
        //senderArrayList.add("IP 1");
        //senderArrayList.add("IP 2");


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

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
                Toast.makeText(ReceiveActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(ReceiveActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new ReceiverWifiBR(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }



    @Override
    public void onChannelDisconnected() {
        Toast.makeText(this,"Disconnected from Peer",Toast.LENGTH_SHORT).show();
    }



    public void makeServer(){
        //Server(...,...,RECEIVER)
        //create a textView for display and call the server AyncTask
        new Server(this, statusTextView,RECEIVER,null).execute();
    }


    public void makeClient(String destIP){
        //Client(...,...,IP,RECEIVER)
        //create a textView for display and call the client AyncTask
        new Client(this, statusTextView,RECEIVER,destIP,null).execute();
    }

    public void makeToast(String msg) {
        Toast.makeText(ReceiveActivity.this,
                msg,
                Toast.LENGTH_LONG).show();
    }

    public void makeToast(int i) {
        if(i == 1){
            Toast.makeText(this,"P2P state changed",Toast.LENGTH_SHORT).show();
        }
        else if(i==2){
            Toast.makeText(this,"P2P peers changed",Toast.LENGTH_SHORT).show();

        }
        else if(i==3){
            Toast.makeText(this,"P2P is connected",Toast.LENGTH_SHORT).show();

        }
        else if(i==4){
            Toast.makeText(this,"Peers available",Toast.LENGTH_SHORT).show();

        }
    }
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                scanSuccess();
            } else {
                // scan failure handling
                scanFailure();
            }
        }
    };

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        ListIterator<ScanResult> listIterator = results.listIterator();
        while (listIterator.hasNext()){
            senderArrayList.add(listIterator.next().SSID);
        }
        Log.d("Receiver Wifi scan",senderArrayList.toString());
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();

    }
}