package com.example.stf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.Contacts.ViewModalContacts;

public class AddNewContactActivity extends AppCompatActivity {

    private ImageButton btnExitAddNewContact;

    private AppCompatButton btnAddContact;

    private ViewModalContacts contactsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        // init the xml and his stuff.
        init();

        //create listeners
        createListeners();

        //init the view model
        initViewModel();
    }

    private void init() {
        btnExitAddNewContact = findViewById(R.id.btnExitAddNewContact);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

//    private void performAddContact() {
//        contactsViewModel.performAddContact(
//                this::handleAddContactCallback
//        );
//    }

    private void handleAddContactCallback(@NonNull String[] errors) {

    }

    private void initViewModel() {
//        contactsViewModel = new ViewModelProvider(this).get(ViewModalContacts.class);
//        // create listener for the btnRegister
//        btnAddContact.setOnClickListener(view -> performAddContact());
    }


    private void createListeners() {
        btnExitAddNewContact.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(AddNewContactActivity.this, ContactsActivity.class);
            startActivity(intent);
        });
    }
}