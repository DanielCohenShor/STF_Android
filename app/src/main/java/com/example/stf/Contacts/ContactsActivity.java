package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.stf.AddNewContactActivity;
import com.example.stf.AppDB;
import com.example.stf.Chat.ChatActivity;
import com.example.stf.ContactClickListener;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Login.LoginActivity;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.example.stf.adapters.ContactAdapter;
import com.example.stf.entities.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class ContactsActivity extends AppCompatActivity implements ContactClickListener {
    private boolean isFirstTime = true;

    private ImageButton btnLogout;

    private ImageButton btnSettings;

    private FloatingActionButton btnAddContact;

    private ViewModalContacts viewModalContacts;
    private String token;
    private RecyclerView listViewContacts;

    private ContactAdapter contactAdapter;

    private AppDB db;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;


    private String currentUserUsername;
    private String currentUserDisplayName;
    private String currentUserProfilePic;

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
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();
        });
    }

    private void createListeners() {
        //listener for the logout
        btnLogout.setOnClickListener(v -> {logOut();});

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
        currentUserUsername = getIntent().getStringExtra("username");
        currentUserDisplayName = getIntent().getStringExtra("displayName");
        currentUserProfilePic = getIntent().getStringExtra("profilePic");
        btnSettings = findViewById(R.id.btnSettings);
        listViewContacts = findViewById(R.id.RecyclerViewContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    private void logOut() {
        // Delete the local database
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                contactsDao.deleteAllContacts();
                messagesDao.deleteAllMessages();
            }
        });
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstTime) {
            // This code will run only the first time
            isFirstTime = false;
        } else {
            Log.d("Tag", "inside on resume");
            AsyncTask.execute(() -> {
                Contact[] contacts = contactsDao.indexSortedByDate();
                runOnUiThread(() -> updateUIWithContacts(contacts));
            });
        }
    }

    private void getContacts() {
        viewModalContacts.performGetContacts(token, this::handleGetContactsCallback);
    }

    private void handleGetContactsCallback(Contact[] contacts) {
        AsyncTask.execute(() -> {
            for (Contact contact : contacts) {
                String contactId = String.valueOf(contact.getId()); // Convert to string
                Log.d("MyApp", contactId); // Print "Hello" for each iteration
                Contact existingContact = contactsDao.get(contact.getId());
                if (existingContact == null) {
                    contactsDao.insert(contact);
                }
            }
            Contact[] sortredContacts = contactsDao.indexSortedByDate();

            runOnUiThread(() -> updateUIWithContacts(sortredContacts));
        });
    }


    private void updateUIWithContacts(Contact[] contacts) {
        // Change the UI using the adapter
        contactAdapter = new ContactAdapter(this, contacts, this);
        contactAdapter.setContacts(contacts);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        // Retrieve the clicked contact from the adapter
        Contact clickedContact = contactAdapter.getContact(position);

        // Start the new activity here
        Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);

        intent.putExtra("token", token);
        intent.putExtra("contactProfilePic", clickedContact.getUser().getProfilePic());
        intent.putExtra("contactDisplayName", clickedContact.getUser().getDisplayName());
        intent.putExtra("chatId", clickedContact.getId());
        intent.putExtra("currentUserUsername", currentUserUsername);

        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        // Inside your item click listener or where you want to show the dialog
        new AlertDialog.Builder(ContactsActivity.this)
                .setTitle("Delete Chat")
                .setMessage("You will delete this chat. Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete chat logic here
                        Contact clickedContact = contactAdapter.getContact(position);
                        viewModalContacts.performDeleteChat(token, clickedContact.getId(), ContactsActivity.this::deleteChatById);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No action needed, dialog will be automatically dismissed
                    }
                })
                .show();
    }

    private void deleteChatById(int chatID) {
        if (chatID != -1) {
            AsyncTask.execute(() -> {
                messagesDao.deleteMessagesByChatId(String.valueOf(chatID));
                contactsDao.deleteByChatId(chatID);
                Contact[] contacts = contactsDao.indexSortedByDate();
                runOnUiThread(() -> updateUIWithContacts(contacts));
            });
        } else {
            //dont know what to do?
        }
    }

}