package com.example.stf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.entities.Contact;
import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.Dao.ContactsDao;

import java.util.Objects;

public class AddNewContactActivity extends AppCompatActivity {

    private ImageButton btnExitAddNewContact;

    private AppCompatButton btnAddContact;

    private ViewModalContacts contactsViewModel;

    private EditText etChooseContact;
    private AppDB db;
    private ContactsDao contactsDao;

    private String baseUrl;

    private String currentUsername;

    private SharedPreferences sharedPreferences;
    private String serverToken;


    private void getSharedPreferences() {
        baseUrl = sharedPreferences.getString("serverUrl", "");
        serverToken = sharedPreferences.getString("serverToken", "");
        currentUsername = sharedPreferences.getString("userName", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        getSharedPreferences();

        // init the data base
        initDB();
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            // init the xml and his stuff.
            runOnUiThread(() -> {
                init();

                //create listeners
                createListeners();
            });
        });
    }

    private void init() {
        btnExitAddNewContact = findViewById(R.id.btnExitAddNewContact);
        btnAddContact = findViewById(R.id.btnAddContact);
        etChooseContact = findViewById(R.id.etChooseContact);
        contactsViewModel = new ViewModalContacts(baseUrl);
    }

    private void performAddContact() {
        String newContact = etChooseContact.getText().toString();
        if (!Objects.equals(currentUsername, newContact)) {
            contactsViewModel.performAddContact(serverToken, newContact, this::handleAddContactCallback);
        } else {
            handleAddContactCallback(null);
        }
    }

    private void handleAddContactCallback(Contact contact) {
        if (contact != null) {
            // make the edit text to be regular
            int styleResId = R.style.EDIT_TEXT;
            TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
            int drawableResId = styledAttributes.getResourceId(0, 0);
            styledAttributes.recycle();
            etChooseContact.setBackgroundResource(drawableResId);
            //push to the data base local
            AsyncTask.execute(() -> {
                // Perform insert operation on a background thread
                contactsDao.insert(contact);
            });
            // Start the new activity here
            Intent intent = new Intent(AddNewContactActivity.this, ContactsActivity.class);
            startActivity(intent);
            finish();
        } else {

            int styleResId = R.style.INVALID_EDIT_TEXT;
            TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
            int drawableResId = styledAttributes.getResourceId(0, 0);
            styledAttributes.recycle();
            etChooseContact.setBackgroundResource(drawableResId);

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
                    // make the edit text to be regular
                    int styleResId = R.style.EDIT_TEXT;
                    TypedArray styledAttributes = obtainStyledAttributes(styleResId, new int[]{android.R.attr.background});
                    int drawableResId = styledAttributes.getResourceId(0, 0);
                    styledAttributes.recycle();
                    etChooseContact.setBackgroundResource(drawableResId);
                }
            });
        }
    }

    private void createListeners() {
        btnExitAddNewContact.setOnClickListener(v -> {
            // back to the activity here
            Intent intent = new Intent(AddNewContactActivity.this, ContactsActivity.class);
            startActivity(intent);
            finish();
        });

        btnAddContact.setOnClickListener(view -> performAddContact());

    }
}