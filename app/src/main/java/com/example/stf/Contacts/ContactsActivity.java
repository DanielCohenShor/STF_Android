package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stf.Login.LoginActivity;
import com.example.stf.R;
import com.example.stf.Register.ViewModelRegister;

import java.util.Objects;

public class ContactsActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private ImageView profileImageView;

    private TextView displayNameTextView;
    private ImageButton btnLogout;

    private ListView listViewContacts;
    private ViewModalContacts viewModalContacts;
    private String token;

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
        viewModalContacts = new ViewModelProvider(this).get(ViewModalContacts.class);
        token = getIntent().getStringExtra("token");
    }

    private void getContacts() {
        viewModalContacts.performGetContacts(token, this::handleGetContactsCallback);
    }


    private void handleGetContactsCallback(Contact [] contacts) {
        //change the ui
    }
}