package com.example.shareit_test.activities;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.Bundle;

import com.example.shareit_test.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.shareit_test.activities.ui.main.SectionsPagerAdapter;

public class BrowseFilesActivity extends AppCompatActivity {

    private String filepath = null;
    private WifiP2pConfig dest_config;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001:
                if (resultCode == RESULT_OK) {
                    filepath = data.getData().getPath();
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_files);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        Intent intent = getIntent();
        dest_config = new WifiP2pConfig();
        dest_config.deviceAddress = intent.getStringExtra("IP"); // this is the device MAC which has no use case right now

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent1 = new Intent(BrowseFilesActivity.this, SendProcessingActivity.class);
                //startActivity(intent1);

                Intent filesOpener = new Intent(Intent.ACTION_GET_CONTENT);
                //filesOpener.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //filesOpener.addCategory(Intent.CATEGORY_OPENABLE);
                filesOpener.setType("*/*");
                startActivityForResult(filesOpener, 1001);

                Snackbar.make(view, filepath, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}