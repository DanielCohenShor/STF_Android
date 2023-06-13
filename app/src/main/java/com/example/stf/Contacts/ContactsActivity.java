package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.stf.AddNewContactActivity;
import com.example.stf.AppDB;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Login.LoginActivity;
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
    private String token;
    private RecyclerView listViewContacts;

    private ContactAdapter contactAdapter;

    private AppDB db;
    private ContactsDao contactsDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        // init the data base
        initDB();
        // init the xml and his stuff.
        init();
        //get all the contacts
        getContacts();
        //create listeners
        createListeners();
    }

    public void initDB() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "ContactsDB")
                        .fallbackToDestructiveMigration()
                        .build();
                contactsDao = db.ContactsDao();
            }
        });
    }
    private void createListeners() {
        //listener for the logut
        btnLogout.setOnClickListener(v -> {
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

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Contact[] contacts = contactsDao.index();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUIWithContacts(contacts);
                    }
                });
            }
        });
    }


    private void getContacts() {
        viewModalContacts.performGetContacts(token, this::handleGetContactsCallback);
    }

    private void handleGetContactsCallback(Contact[] contacts) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (Contact contact : contacts) {
                    Contact existingContact = contactsDao.get(contact.getId());
                    if (existingContact == null) {
                        contactsDao.insert(contact);
                    }
                }
            }
        });

        updateUIWithContacts(contacts);
    }


    private void updateUIWithContacts(Contact[] contacts) {
        // Change the UI using the adapter
        contactAdapter = new ContactAdapter(this, contacts);
        contactAdapter.setContacts(contacts);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
    }

}