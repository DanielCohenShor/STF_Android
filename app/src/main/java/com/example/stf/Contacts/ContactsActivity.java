package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.stf.AddNewContactActivity;
import com.example.stf.Login.LoginActivity;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.example.stf.adapters.ContactAdapter;
import com.example.stf.entities.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private ImageButton btnLogout;

    private ImageButton btnSettings;

    private FloatingActionButton btnAddContact;

    private ViewModalContacts viewModalContacts;
    private String token;
    private RecyclerView listViewContacts;

    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        // init the xml and his stuff.
        init();
        //get all the contact
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
        viewModalContacts = new ViewModelProvider(this).get(ViewModalContacts.class);
        token = getIntent().getStringExtra("token");
        btnSettings = findViewById(R.id.btnSettings);
        listViewContacts = findViewById(R.id.RecyclerViewContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getContacts() {
        viewModalContacts.performGetContacts(token, this::handleGetContactsCallback);
    }

    private void handleGetContactsCallback(Chat[] contacts) {
        //change the ui use the adapter
        contactAdapter = new ContactAdapter(this, contacts);
        contactAdapter.setContacts(contacts);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
    }
}