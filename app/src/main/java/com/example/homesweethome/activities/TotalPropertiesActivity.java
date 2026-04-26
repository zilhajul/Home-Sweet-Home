package com.example.homesweethome.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homesweethome.R;
import com.example.homesweethome.activities.fragment.TotalProperties;

public class TotalPropertiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_properties);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new TotalProperties())
                    .commit();
        }
    }
}

