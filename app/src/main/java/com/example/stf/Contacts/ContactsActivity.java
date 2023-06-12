package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stf.Login.LoginActivity;
import com.example.stf.R;
import com.example.stf.Register.ViewModelRegister;
import com.example.stf.adapters.ContactAdapter;

import java.util.Objects;
import com.example.stf.SettingsActivity;

public class ContactsActivity extends AppCompatActivity {
    private ImageButton btnLogout;
    private ViewModalContacts viewModalContacts;
    private String token;
    private RecyclerView listViewContacts;
    private ContactAdapter contactAdapter;

    private ImageButton btnSettings;

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

    }
    private void init() {
        // Initialize the views
        btnLogout = findViewById(R.id.btnLogout);
        viewModalContacts = new ViewModelProvider(this).get(ViewModalContacts.class);
        token = getIntent().getStringExtra("token");
        contactAdapter = new ContactAdapter(this);
        listViewContacts.setAdapter(contactAdapter);
        btnSettings = findViewById(R.id.btnSettings);
    }

    private void getContacts() {
        viewModalContacts.performGetContacts(token, this::handleGetContactsCallback);
    }


    private void handleGetContactsCallback(Contact [] contacts) {
        //change the ui use the adapter

    }
}