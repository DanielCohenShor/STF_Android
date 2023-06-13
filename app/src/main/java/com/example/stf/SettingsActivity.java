package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.widget.SwitchCompat;

import com.example.stf.Contacts.ContactsActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnExitSettings;

    SwitchCompat switcher;
    boolean nightMODE;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // init the xml and his stuff.
        init();

        makeDarkMode();

        createListeners();
    }

    private void init() {
        btnExitSettings = findViewById(R.id.btnExitSettings);
        switcher = findViewById(R.id.switchDarkMode);
        token = getIntent().getStringExtra("token");
    }

    private void createListeners() {
        btnExitSettings.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }

    private void makeDarkMode() {
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMODE = sharedPreferences.getBoolean("night", false); // Set default value to true for light mode

        // Update the UI based on the initial value of nightMODE
        if (nightMODE) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


        switcher.setOnClickListener(v -> {
            // Update the UI based on the new value of nightMODE
            if (nightMODE) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("night", true);
            }
            editor.apply();
        });
    }
}