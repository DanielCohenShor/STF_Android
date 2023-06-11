package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stf.Login.LoginActivity;
import com.example.stf.R;

public class ContactsActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView profileImageView;

    private TextView displayNameTextView;
    private ImageButton btnLogout;

    private ListView listViewContacts;

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

    }
    private void init() {
        // Initialize the views
        displayNameTextView = findViewById(R.id.tvContactName);
        // Retrieve the data from the intent extras
        String displayName = getIntent().getStringExtra("displayName");
        // Set the values in the respective views
        displayNameTextView.setText(displayName);
        // init the rest of the views
        btnLogout = findViewById(R.id.btnLogout);
        listViewContacts = findViewById(R.id.listViewContacts);
    }

    private void getContacts() {

    }
}