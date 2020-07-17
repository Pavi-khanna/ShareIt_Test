package com.example.shareit_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shareit_test.activities.ReceiveActivity;
import com.example.shareit_test.activities.SendActivity;
import com.example.shareit_test.activities.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    Button send;
    Button receive;
    private final int FINE_LOC_CODE = 100;
    private final int WIFI_STATE_CODE = 101;
    private final int INTERNET_ACCESS_CODE = 102;
    private final int ACCESS_MEDIA_CODE = 103;
    private final int WRITE_EXTERNAL = 104;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_MEDIA_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int [] perm_codes = {FINE_LOC_CODE,WIFI_STATE_CODE,INTERNET_ACCESS_CODE,ACCESS_MEDIA_CODE,WRITE_EXTERNAL};
        checkPermission(permissions,perm_codes);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_test));
        }

        send = findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFiles();
            }
        });

        receive = findViewById(R.id.button_receive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveFiles();
            }
        });
    }

    public void checkPermission(String[] permissions, int[] perm_codes) {
        int i = 0;
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                    == PackageManager.PERMISSION_DENIED) {

                // Requesting the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions,
                        perm_codes[i]);
            }
            else {
                //Toast.makeText(MainActivity.this,"Permission already granted: "+perm_codes[i],Toast.LENGTH_SHORT).show();
            }
            i++;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                Toast.makeText(this, "Share selected",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void sendFiles() {
        Intent sendIntent = new Intent(this, SendActivity.class);
        startActivity(sendIntent);
    }

    public void receiveFiles() {
        Intent receiveIntent = new Intent(this, ReceiveActivity.class);
        startActivity(receiveIntent);
    }

}