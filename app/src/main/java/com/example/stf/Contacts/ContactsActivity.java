package com.example.stf.Contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
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
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Login.LoginActivity;
import com.example.stf.Notifications.ChatsNotification;
import com.example.stf.Notifications.UserNotification;
import com.example.stf.R;
import com.example.stf.SettingsActivity;
import com.example.stf.adapters.ContactAdapter;
import com.example.stf.entities.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
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


    private void getSharedPreferences() {
        serverUrl = sharedPreferences.getString(SERVERURL, "");
        currentUserUsername = sharedPreferences.getString(USERNAME, "");
        currentUserProfilePic = sharedPreferences.getString(PROFILEPIC, "");
        currentUserDisplayName = sharedPreferences.getString(DISPLAYNAME, "");
        serverToken = sharedPreferences.getString(SERVERTOKEN, "");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
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
            if (contactsDao.index().length != 0) {
                viewModalContacts = new ViewModalContacts(serverUrl);
                List<Contact> contacts = contactsDao.indexSortedByDate();
                runOnUiThread(() -> updateUIWithContacts(contacts));
            }
            //get all contacts
            runOnUiThread(this::getContacts);
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
            logOut();
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
            messagesDao = db.messagesDao();
        });
    }


    private void createListeners() {
        btnAddContact.setOnClickListener(v -> {
            // Start the new activity here
            Intent intent = new Intent(ContactsActivity.this, AddNewContactActivity.class);
            startActivity(intent);
        });
    }

    private void init() {
        // Initialize the views
        listViewContacts = findViewById(R.id.RecyclerViewContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
        progressBar = findViewById(R.id.progressBar);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));
        viewModalContacts = new ViewModalContacts(serverUrl);
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

        // Apply the changes
        editor.apply();
    }

    private void logOut() {
        // Delete the local database and the shared
        resetSharedPreferences();
        AsyncTask.execute(() -> {
            contactsDao.deleteAllContacts();
            messagesDao.deleteAllMessages();
        });
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
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isFirstTime) {
//            // This code will run only the first time
//            isFirstTime = false;
//        } else {
//            serverToken = sharedPreferences.getString(SERVERTOKEN, "");
//            viewModalContacts.setBaseUrl(serverUrl);
//            AsyncTask.execute(() -> {
//                List<Contact> contacts = contactsDao.indexSortedByDate();
//                runOnUiThread(() -> updateUIWithContacts(contacts));
//            });
//        }
//    }

    private void getContacts() {
        progressBar.setVisibility(View.VISIBLE);
        viewModalContacts.performGetContacts(serverToken, this::handleGetContactsCallback);
    }

    private void handleGetContactsCallback(Contact[] contacts) {
        AsyncTask.execute(() -> {
            for (Contact contact : contacts) {
                String contactId = String.valueOf(contact.getId()); // Convert to string
                Log.d("MyApp", contactId); // Print "Hello" for each iteration
                Contact existingContact = contactsDao.get(contact.getId());
                if (existingContact == null) {
                    String base64Photo = removePrefix(contact.getUser().getProfilePic());
                    String compressedPhoto = decreasePhoto(base64Photo);
                    contact.getUser().setProfilePic(compressedPhoto);
                    contactsDao.insert(contact);
                }
            }
            List<Contact> sortedContacts = contactsDao.indexSortedByDate();
            runOnUiThread(() -> updateUIWithContacts(sortedContacts));
        });
    }


    public String removePrefix(String input) {
        input = input.substring("data:image/png;base64,".length());
        return input;
    }

    private String decreasePhoto(String photo) {
        Log.d("photo", "in start decrease");
        // Decode the Base64 string to obtain the image byte array
        byte[] imageBytes = Base64.decode(photo, Base64.DEFAULT);
        // Convert the byte array to a Bitmap object
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        if (bitmap != null) {
            // Compress the bitmap using the compressBitmap() method
            Bitmap compressedBitmap = compressBitmap(bitmap);

            if (compressedBitmap != null) {
                // Encode the compressed bitmap back to a Base64 string
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] compressedBytes = outputStream.toByteArray();
                Log.d("photo", "in finish good decrease");
                return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
            } else {
                Log.d("photo", "in finish compression fails decrease");
                return null; // Return null if compression fails
            }
        } else {
            Log.d("photo", "in finish decoding fails decrease");
            return null; // Return null if decoding fails
        }
    }
    private Bitmap compressBitmap(Bitmap bitmap) {
        Log.d("photo", "in compress");
        if (bitmap == null) {
            return null;
        }

        try {
            // Calculate the desired dimensions for the compressed bitmap
            int desiredSize = 75; // Desired size in pixels

            // Get the original dimensions of the bitmap
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();

            // Calculate the aspect ratio of the original bitmap
            float aspectRatio = (float) originalWidth / originalHeight;

            // Calculate the new dimensions for the compressed bitmap
            int newWidth = Math.round(aspectRatio > 1 ? desiredSize : desiredSize * aspectRatio);
            int newHeight = Math.round(aspectRatio > 1 ? desiredSize / aspectRatio : desiredSize);

            // Create the compressed bitmap with the new dimensions
            Bitmap compressedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Compress the bitmap further if needed
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            // Create the final compressed bitmap
            byte[] compressedBytes = outputStream.toByteArray();
            Bitmap finalBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);

            // Return the final compressed bitmap
            return finalBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateUIWithContacts(List<Contact> contacts) {
        // Change the UI using the adapter
        contactAdapter = new ContactAdapter(this, contacts, this);
        contactAdapter.setContacts(contacts);
        listViewContacts.setAdapter(contactAdapter);
        listViewContacts.setLayoutManager(new LinearLayoutManager(this));

        viewModalContacts.performGetNotifications(serverToken, this::handleGetNotificationsCallback);
    }

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
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                updateUIWithNotifications(notifications);
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
        Log.d("test", "1");
        viewModalContacts.performResetNotifications(serverToken, String.valueOf(clickedContact.getId()), this::handleResetNotificationsCallback);
        Log.d("test", "2");

        String chatId = String.valueOf(clickedContact.getId());
        Log.d("test", "3");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("test", "4");

        editor.putString(CURRENTCHAT, chatId);
        Log.d("test", "5");

        // Apply the changes
        editor.apply();
        Log.d("test", "6");

        // Start the new activity here
        Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
        Log.d("test", "7");

        intent.putExtra("contactDisplayName", clickedContact.getUser().getDisplayName());
        Log.d("test", "8");
// If the picture is a Bitmap
        startActivity(intent);
        Log.d("test", "9");
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
                List<Contact> contacts = contactsDao.indexSortedByDate();
                runOnUiThread(() -> updateUIWithContacts(contacts));
            });
        } else {
            //dont know what to do?
        }
    }
}