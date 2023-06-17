package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.stf.AddNewContactActivity;
import com.example.stf.AppDB;
import com.example.stf.Chat.ChatActivity;
import com.example.stf.ContactClickListener;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.example.stf.adapters.ContactAdapter;
import com.example.stf.entities.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
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

    private SettingsDao settingsDao;

    private String currentUserUsername;
    private String currentUserDisplayName;
    private String currentUserProfilePic;
    private Toolbar toolbar;

    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // init the data base
        initDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // Find the search item and get the action view
        MenuItem searchItem = menu.findItem(R.id.action_search);
        View actionView = searchItem.getActionView();

        // Find the SearchView within the action view
        SearchView searchView = actionView.findViewById(R.id.searchView);

        // Customize the SearchView and set the listeners
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Handle search query submission
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Handle search query text change
                    contactAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logOut) {
            logOut();
            return true;
        } else if (itemId == R.id.action_setting) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Clear the search query
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        LinearLayout actionView = (LinearLayout) searchItem.getActionView();
        SearchView searchView = actionView.findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchItem.collapseActionView();
        }
    }


    public void initDB() {
        currentUserDisplayName = getIntent().getStringExtra("displayName");
        currentUserProfilePic = getIntent().getStringExtra("profilePic");
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();
            settingsDao = db.settingsDao();
            baseUrl = settingsDao.getFirst().getServerUrl();
            settingsDao.updateDisplayName(baseUrl, currentUserDisplayName);
            settingsDao.updatePhoto(baseUrl, currentUserProfilePic);
            viewModalContacts = new ViewModalContacts(baseUrl);
            init();
            getContacts();
            createListeners();
        });
    }

    private void createListeners() {
        btnAddContact.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, AddNewContactActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }

    private void init() {
        // Initialize the views
        listViewContacts = findViewById(R.id.RecyclerViewContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        currentUserUsername = getIntent().getStringExtra("username");
        token = getIntent().getStringExtra("token");
        currentUserDisplayName = getIntent().getStringExtra("displayName");
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
        //init the search bar
        // Set the custom toolbar as the activity's action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void logOut() {
        // Delete the local database
        AsyncTask.execute(() -> {
            contactsDao.deleteAllContacts();
            messagesDao.deleteAllMessages();
            settingsDao.deleteDisplayName(baseUrl);
            settingsDao.updatePhoto(baseUrl, "");
        });
        finish();
    }

    private void openSettings() {
         //Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, SettingsActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
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
                String baseUrl = settingsDao.getFirst().getServerUrl();
                viewModalContacts.setBaseUrl(baseUrl);
                List<Contact> contacts = contactsDao.indexSortedByDate();
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
            List<Contact> sortredContacts = contactsDao.indexSortedByDate();

            runOnUiThread(() -> updateUIWithContacts(sortredContacts));
        });
    }


    private void updateUIWithContacts(List<Contact> contacts) {
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
        String displayNameOfChatDelete = contactAdapter.getContact(position).getUser().getDisplayName();
        // Inside your item click listener or where you want to show the dialog
        new AlertDialog.Builder(ContactsActivity.this)
                .setTitle("Delete Chat")
                .setMessage("You will delete " + displayNameOfChatDelete + " chat. Are you sure?")
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
                List<Contact> contacts = contactsDao.indexSortedByDate();
                runOnUiThread(() -> updateUIWithContacts(contacts));
            });
        } else {
            //dont know what to do?
        }
    }
}