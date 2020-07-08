package com.example.shareit_test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.shareit_test.R;

import java.util.ArrayList;

public class SendActivity extends AppCompatActivity {

    ListView senderListView;
    Button createhotspot;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        createhotspot = findViewById(R.id.hotspot);
        senderListView = findViewById(R.id.avail_devices_list);
        ArrayList<String> senderArrayList = new ArrayList<>();
        senderArrayList.add("IP 1");
        senderArrayList.add("IP 2");


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


        //manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //channel = manager.initialize(this, getMainLooper(), null);


        createhotspot.setVisibility(View.INVISIBLE);
        createhotspot.postDelayed(new Runnable() {
            public void run() {
                createhotspot.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }
}