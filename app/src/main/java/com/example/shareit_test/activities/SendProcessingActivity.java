package com.example.shareit_test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.shareit_test.R;

import java.util.ArrayList;

public class SendProcessingActivity extends AppCompatActivity {

    Button cancel;
    ProgressBar sendProgressBar;
    ListView sendFilesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_processing);

        cancel = findViewById(R.id.button_cancel_processing);
        sendProgressBar = findViewById(R.id.progressBar_send);
        sendFilesListView = findViewById(R.id.list_send_files_processing);

        ArrayList<String> senderArrayList = new ArrayList<>();
        senderArrayList.add("File1");
        senderArrayList.add("File2");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, senderArrayList);
        sendFilesListView.setAdapter(arrayAdapter);

    }
}