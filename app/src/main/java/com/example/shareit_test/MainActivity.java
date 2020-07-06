package com.example.shareit_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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