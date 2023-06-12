package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.stf.AddNewContactActivity;
import com.example.stf.Login.LoginActivity;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsActivity extends AppCompatActivity {
    private ImageButton btnLogout;

    private ListView listViewContacts;

    private ImageButton btnSettings;

    private FloatingActionButton btnAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        // init the xml and his stuff.
        init();
        //get all the contacts
        getContacts();
        //create listeners
        createListeners();
    }
    private void createListeners() {
        //listener for the logut
        btnLogout.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnAddContact.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, AddNewContactActivity.class);
            startActivity(intent);
        });

    }
    private void init() {
        // Initialize the views
        btnLogout = findViewById(R.id.btnLogout);
        listViewContacts = findViewById(R.id.listViewContacts);
        btnSettings = findViewById(R.id.btnSettings);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

    private void getContacts() {

    }
}