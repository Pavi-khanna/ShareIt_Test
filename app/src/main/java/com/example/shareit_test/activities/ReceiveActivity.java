package com.example.shareit_test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.shareit_test.R;

import java.util.ArrayList;

public class ReceiveActivity extends AppCompatActivity {

    ListView receiverListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        receiverListView = findViewById(R.id.list_receive);
        ArrayList<String> senderArrayList = new ArrayList<>();
        senderArrayList.add("IP 1");
        senderArrayList.add("IP 2");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,senderArrayList);
        receiverListView.setAdapter(arrayAdapter);
    }
}