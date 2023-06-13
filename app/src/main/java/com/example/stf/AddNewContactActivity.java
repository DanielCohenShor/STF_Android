package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.stf.Contacts.Contact;
import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.Contacts.ViewModalContactsLive;
import com.example.stf.entities.Chat;

public class AddNewContactActivity extends AppCompatActivity {

    private ImageButton btnExitAddNewContact;

    private AppCompatButton btnAddContact;

    private ViewModalContacts contactsViewModel;

    private  ViewModalContactsLive viewModalContactsLive;

    private String token;

    private EditText etChooseContact;

    private Chat newContact;

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
        etChooseContact = findViewById(R.id.etChooseContact);
        token = getIntent().getStringExtra("token");

        contactsViewModel = new ViewModelProvider(this).get(ViewModalContacts.class);
        viewModalContactsLive = new ViewModelProvider(this).get(ViewModalContactsLive.class);
    }

    private void performAddContact() {
        contactsViewModel.performAddContact(
                token,
                etChooseContact.getText().toString(),
                this::handleAddContactCallback
        );
    }

    private void handleAddContactCallback(Chat contact) {
        newContact = contact;
        if (contact != null) {
            // make the edit text to be regular
            etChooseContact.setBackgroundResource(R.drawable.edittext_background);
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("newContact", newContact);
//            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            etChooseContact.setBackgroundResource(R.drawable.invalid_edit_text);

            etChooseContact.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // This method is called before the text is changed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // This method is called when the text is being changed
                }

                // This method is called after the text has been changed
                @Override
                public void afterTextChanged(Editable s) {
                    // change the border of the text view to none
                    etChooseContact.setBackgroundResource(R.drawable.edittext_background);
                }
            });
        }
    }

    private void initViewModel() {
        // create listener for the btnRegister
        btnAddContact.setOnClickListener(view -> {
            performAddContact();
        });
    }


    private void createListeners() {
        btnExitAddNewContact.setOnClickListener(v -> {
            finish();
        });
    }
}