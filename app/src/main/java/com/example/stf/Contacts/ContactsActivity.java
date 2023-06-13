package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.stf.AddNewContactActivity;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.example.stf.adapters.ContactAdapter;
import com.example.stf.entities.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsActivity extends AppCompatActivity {
    private ImageButton btnLogout;

    private ImageButton btnSettings;

    private FloatingActionButton btnAddContact;

    private ViewModalContacts viewModalContacts;

    private  ViewModalContactsLive viewModalContactsLive;
    private String token;
    private RecyclerView listViewContacts;

    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // init the xml and his stuff.
        init();

//        //get all the contacts
//        getContacts();

        //create listeners
        createListeners();

        viewModalContacts.getContacts().observe(this, chat -> {
            contactAdapter.addContact(chat);
            listViewContacts.setAdapter(contactAdapter);
            listViewContacts.setLayoutManager(new LinearLayoutManager(this));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Use the extra value received from the current activity
                Chat contact = (Chat) data.getSerializableExtra("newContact");
                viewModalContacts.getContacts().setValue(contact);
            }
        }
    }

    private void createListeners() {
        //listener for the logout
        btnLogout.setOnClickListener(v -> {
            // Start the new activity here
            finish();
        });

        btnSettings.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, SettingsActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        btnAddContact.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, AddNewContactActivity.class);
            intent.putExtra("token", token);
            startActivityForResult(intent, 1);
        });

    }
    private void init() {
        // Initialize the views
        btnLogout = findViewById(R.id.btnLogout);
        token = getIntent().getStringExtra("token");
        btnSettings = findViewById(R.id.btnSettings);
        listViewContacts = findViewById(R.id.RecyclerViewContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));

        viewModalContacts = new ViewModelProvider(this).get(ViewModalContacts.class);
        viewModalContactsLive = new ViewModelProvider(this).get(ViewModalContactsLive.class);
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