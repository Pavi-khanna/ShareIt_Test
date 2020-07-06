package com.example.shareit_test.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.shareit_test.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.shareit_test.activities.ui.main.SectionsPagerAdapter;

public class BrowseFilesActivity extends AppCompatActivity {

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
        final String message = intent.getStringExtra("IP");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(BrowseFilesActivity.this, SendProcessingActivity.class);
                startActivity(intent1);
                //Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                //        .setAction("Action", null).show();
            }
        });
    }
}