package com.example.stf.Contacts;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.stf.AddNewContactActivity;
import com.example.stf.AppDB;
import com.example.stf.Chat.ChatActivity;
import com.example.stf.ContactClickListener;
import com.example.stf.ContactsListLiveData;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Login.LoginActivity;
import com.example.stf.MessagesListLiveData;
import com.example.stf.Notifications.ChatsNotification;
import com.example.stf.Notifications.UserNotification;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.example.stf.adapters.ContactAdapter;
import com.example.stf.entities.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ContactsActivity extends AppCompatActivity implements ContactClickListener {
    private boolean isFirstTime = true;

    private FloatingActionButton btnAddContact;

    private ViewModalContacts viewModalContacts;
    private String serverToken;
    private RecyclerView listViewContacts;

    private ContactAdapter contactAdapter;

    private AppDB db;
    private ContactsDao contactsDao;
    private MessagesDao messagesDao;
    private String currentUserUsername;
    private String currentUserDisplayName;
    private String currentUserProfilePic;
    private Toolbar toolbar;

    private String serverUrl;

    private ProgressBar progressBar;
    private  SharedPreferences sharedPreferences;
    private final String SERVERURL = "serverUrl";
    private final String USERNAME = "userName";
    private final String SERVERTOKEN = "serverToken";
    private final String DISPLAYNAME = "displayName";
    private final String PROFILEPIC = "photo";
    private final String CURRENTCHAT = "currentChat";
    private ContactsListLiveData contactsLiveDataList;

    private MessagesListLiveData messagesListLiveData;

    private boolean fromBackGround = false;
    private void getSharedPreferences() {
        if (!fromBackGround) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENTCHAT, "");
            editor.apply();
        }
        serverUrl = sharedPreferences.getString(SERVERURL, "");
        viewModalContacts = new ViewModalContacts(serverUrl);
        currentUserUsername = sharedPreferences.getString(USERNAME, "");
        currentUserProfilePic = sharedPreferences.getString(PROFILEPIC, "");
        currentUserDisplayName = sharedPreferences.getString(DISPLAYNAME, "");
        serverToken = sharedPreferences.getString(SERVERTOKEN, "");
        contactsLiveDataList = ContactsListLiveData.getInstance();
        messagesListLiveData = MessagesListLiveData.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContacts();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                }).create().show();
    }


    private void checkIfBackGround() {
        // Retrieve the values from SharedPreferences
        String chatId = sharedPreferences.getString(CURRENTCHAT, null);
        String receiverDisplayName = sharedPreferences.getString("receiverDisplayName", null);
        Log.d("TAG", "chatId: " + chatId);
        Log.d("TAG", "receiverDisplayName: " + receiverDisplayName);
        if (chatId != null && receiverDisplayName != null) {
            fromBackGround = true;
            List<Contact> contactsList = contactsLiveDataList.getList().getValue();
            for (Contact contact : contactsList) {
                if (contact.getId() == Integer.parseInt(chatId)) {
                    // Start ChatActivity
                    Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        fromBackGround = false;
    }

    @Override
    public void finish() {
        super.finish();
        if (fromBackGround) {
            AsyncTask.execute(() -> {
                db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                        .fallbackToDestructiveMigration()
                        .build();
                contactsDao = db.ContactsDao();
                runOnUiThread(this::getContacts);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        // check if i open from background
        checkIfBackGround();

        getSharedPreferences();

        // init the data base
        initDB();




        fetchFromLocalDB();

        //init the views.
        init();

        //listeners
        createListeners();
    }

    private void fetchFromLocalDB() {
        AsyncTask.execute(() -> {
            if (contactsDao.getAllContacts().isEmpty()) {
                // zero contacts
                //get all contacts rom server
                runOnUiThread(this::getContacts);
            } else {
                // not zero contacts
                contactsLiveDataList.setContactsList(contactsDao.getAllContacts());
                runOnUiThread(() -> updateUIWithContacts(contactsLiveDataList.getList().getValue()));
            }
        });
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
                    if (contactAdapter != null) {
                        contactAdapter.getFilter().filter(newText);
                    }
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
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_setting) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            // Start observing changes in the ContactsDao
            runOnUiThread(this::observeContactsChanges);
            messagesDao = db.messagesDao();
        });
    }

    private void createListeners() {
        btnAddContact.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, AddNewContactActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void init() {
        // Initialize the views
        listViewContacts = findViewById(R.id.RecyclerViewContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        progressBar = findViewById(R.id.progressBar);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
        //init the search bar
        // Set the custom toolbar as the activity's action bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Reset each value to its default or empty value
        editor.putString(SERVERTOKEN, "");
        editor.putString(DISPLAYNAME, "");
        editor.putString(USERNAME, "");
        editor.putString(CURRENTCHAT, "");
        editor.putString(PROFILEPIC, "");

        // Apply the changes
        editor.apply();
    }

    private void logOut() {
        resetSharedPreferences();
        viewModalContacts.removeAndroidToken(serverToken);
        AsyncTask.execute(() -> {
            contactsDao.deleteAllContacts();
            messagesDao.deleteAllMessages();
        });
        contactsLiveDataList.setContactsList(Collections.emptyList());
        messagesListLiveData.setMessagesList(Collections.emptyList());
        //Start the new activity here
        Intent intent = new Intent(ContactsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openSettings() {
         //Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
    }

    private void getContacts() {
        progressBar.setVisibility(View.VISIBLE);
        viewModalContacts.performGetContacts(serverToken, this::handleGetContactsCallback);
    }

    private void handleGetContactsCallback(List<Contact> contacts) {
        AsyncTask.execute(() -> {
            for (Contact contact : contacts) {
                String contactId = String.valueOf(contact.getId()); // Convert to string
                Log.d("test1111", contactId); // Print "Hello" for each iteration
                Contact existingContact = contactsDao.get(contact.getId());
                Log.d("test2222", contactId); // Print "Hello" for each iteration
                if (existingContact == null) {
                    Log.d("test3333", contactId); // Print "Hello" for each iteration
                    contactsDao.insert(contact);
                } else {
                    Log.d("test4444", contactId); // Print "Hello" for each iteration
                    if (contact.getLastMessage() != null) {
                        contactsDao.update(contact);
                    }
                    if (existingContact.getLastMessage() != null) {
                        String oldLastMessage = existingContact.getLastMessage().getContent();
                        String newLastMessage = contact.getLastMessage().getContent();
                        //and for time
                        String oldLastMessageTime = existingContact.getLastMessage().getCreated();
                        String newLastMessageTime = contact.getLastMessage().getCreated();
                        if (!Objects.equals(oldLastMessage, newLastMessage) ||
                                !Objects.equals(oldLastMessageTime, newLastMessageTime)) {
                            contactsDao.update(contact);
                        }
                    }
                }
            }
        });
        viewModalContacts.performGetNotifications(serverToken, this::handleGetNotificationsCallback);
    }

    private void updateUIWithContacts(List<Contact> contacts) {
        // Change the UI using the adapter
        contactAdapter = new ContactAdapter(this, contacts, this);
        contactAdapter.setContacts(contacts);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.GONE);
//        viewModalContacts.performGetNotifications(serverToken, this::handleGetNotificationsCallback);
    }

    //        viewModalContacts.performGetNotifications(serverToken, this::handleGetNotificationsCallback);
    private void handleGetNotificationsCallback(UserNotification notifications) {
        AsyncTask.execute(() -> {
            int notification;
            for (ChatsNotification chat : notifications.getChats()) {
                if (!Objects.equals(chat.getUsers().get(0).get("username"), currentUserUsername)) {
                    notification = Integer.parseInt(Objects.requireNonNull(chat.getUsers().get(0).get("notifications")));
                } else {
                    notification = Integer.parseInt(Objects.requireNonNull(chat.getUsers().get(1).get("notifications")));
                }
                Contact updateContact = contactsDao.get(chat.getId());
                updateContact.setNotifications(notification);
                contactsDao.update(updateContact);
            }
            List<Contact> contacts = contactsDao.getAllContacts();
            runOnUiThread(() -> {
                contactsLiveDataList.setContactsList(contacts);
                progressBar.setVisibility(View.GONE);
            });
        });
    }

    private void updateUIWithNotifications(UserNotification notifications) {
        contactAdapter = new ContactAdapter(this, contactAdapter.getContacts(), this);
        contactAdapter.setNotifications(notifications);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {
        // Retrieve the clicked contact from the adapter
        Contact clickedContact = contactAdapter.getContact(position);
        viewModalContacts.performResetNotifications(serverToken, String.valueOf(clickedContact.getId()), this::handleResetNotificationsCallback);
        String chatId = String.valueOf(clickedContact.getId());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENTCHAT, chatId);
        // Apply the changes
        editor.apply();
        // Start the new activity here
        Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
        intent.putExtra("contactDisplayName", clickedContact.getUser().getDisplayName());// If the picture is a Bitmap
        startActivity(intent);
        finish();
    }

    public void handleResetNotificationsCallback(String chatId) {
        AsyncTask.execute(() -> {
            Contact updateContact = contactsDao.get(Integer.parseInt(chatId));
            updateContact.setNotifications(0);
            contactsDao.update(updateContact);

            runOnUiThread(() -> updateUIWithResetNotifications(Integer.parseInt(chatId)));
        });
    }

    private void updateUIWithResetNotifications(int chatId) {
        contactAdapter.resetNotification(chatId);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemLongClick(int position) {
        String displayNameOfChatDelete = contactAdapter.getContact(position).getUser().getDisplayName();
        // Inside your item click listener or where you want to show the dialog
        new AlertDialog.Builder(ContactsActivity.this)
                .setTitle("Delete Chat")
                .setMessage("You will delete the chat with " + displayNameOfChatDelete + ". Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete chat logic here
                    Contact clickedContact = contactAdapter.getContact(position);
                    viewModalContacts.performDeleteChat(serverToken, clickedContact.getId(), ContactsActivity.this::deleteChatById);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // No action needed, dialog will be automatically dismissed
                })
                .show();
    }

    private void deleteChatById(int chatID) {
        if (chatID != -1) {
            AsyncTask.execute(() -> {
                messagesDao.deleteMessagesByChatId(String.valueOf(chatID));
                contactsDao.deleteByChatId(chatID);
            });
            contactsLiveDataList.deleteContact(chatID);
        } else {
            //dont know what to do?
        }
    }

    // rendering when new contact add me message.
    private void observeContactsChanges() {
        contactsLiveDataList.getList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                // Handle the onChanged event here
                updateUIWithContacts(contacts);
            }
        });

        contactsLiveDataList.getSomeoneAddMe().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                // Handle the onChanged event here
                if (value) {
                    // Someone added you
                    //get all contacts rom server
                    runOnUiThread(ContactsActivity.this::getContacts);
                    // Perform the desired action
                    // Update the value of someoneAddMe to false
                    contactsLiveDataList.setSomeoneAddMe(false);
                }
            }
        });
    }
}