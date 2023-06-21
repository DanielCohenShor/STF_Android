package com.example.stf.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stf.AppDB;
import com.example.stf.Contacts.ContactsActivity;
import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.ContactsListLiveData;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.MessagesListLiveData;
import com.example.stf.R;
import com.example.stf.adapters.MessageAdapter;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnExitChat;

    private EditText etSendMessage;

    private FloatingActionButton btnSendMessage;

    private CircleImageView contactImg;

    private TextView tvContactName;

    private String serverToken;

    private int chatId;

    private String contactProfilePic;

    private String contactDisplayName;

    private ViewModalChats viewModalChats;
    private ViewModalContacts viewModalContacts;

    private RecyclerView listViewMessages;

    private AppDB db;
    private MessagesDao messagesDao;

    private ContactsDao contactDao;

    private MessageAdapter messageAdapter;

    private String currentUserUsername;

    private String serverUrl;

    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private final String SERVERURL = "serverUrl";
    private final String USERNAME = "userName";
    private final String SERVERTOKEN = "serverToken";
    private final String DISPLAYNAME = "displayName";
    private final String PROFILEPIC = "photo";
    private final String CURRENTCHAT = "currentChat";

    private MessagesListLiveData messagesListLiveData;
    private ContactsListLiveData contactsLiveDataList;



    private void getSharedPreferences() {
        serverUrl = sharedPreferences.getString(SERVERURL, "");
        currentUserUsername = sharedPreferences.getString(USERNAME, "");
        serverToken = sharedPreferences.getString(SERVERTOKEN, "");
        viewModalContacts =  new ViewModalContacts(serverUrl);
        viewModalChats = new ViewModalChats(serverUrl);
        if (sharedPreferences.getString(CURRENTCHAT, "").isEmpty()) {
            // enter this if only if we arrive from notifications.
            chatId = Integer.parseInt(getIntent().getStringExtra("newchatId"));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CURRENTCHAT, String.valueOf(chatId));
            editor.apply();
            viewModalContacts.performResetNotifications(serverToken, String.valueOf(chatId), this::handleResetNotificationsCallback);
        } else {
            chatId = Integer.parseInt(sharedPreferences.getString(CURRENTCHAT, ""));
        }
        // Retrieve the Parcelable extra "picture" as a Bitmap
        contactDisplayName = getIntent().getStringExtra("contactDisplayName");
        messagesListLiveData = MessagesListLiveData.getInstance();
        contactsLiveDataList = ContactsListLiveData.getInstance();

        //reset the notfications in db
    }

    public void handleResetNotificationsCallback(String chatId) {
        AsyncTask.execute(() -> {
            Contact updateContact = contactDao.get(Integer.parseInt(chatId));
            updateContact.setNotifications(0);
            contactDao.update(updateContact);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_chat);
        // init the db
        initDB();

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        getSharedPreferences();
        // init the xml and his stuff.

        init();

        initViews();

        // create listeners
        createListeners();
        // fecth from localdb
        fetchFromLocalDB();

    }

    private void fetchFromLocalDB() {
        // need to fetch only from db
        AsyncTask.execute(() -> {
            List<Message> messagesList = messagesDao.getAllMessages(chatId);
            if (!messagesList.isEmpty()) {
                runOnUiThread(() -> messagesListLiveData.setMessagesList(messagesList));
            } else {
                // Handle the case when the list of dao is empty
                //get all messages.
                runOnUiThread(this::getMessages);
            }
        });
    }

    private void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            messagesDao = db.messagesDao();
            contactDao = db.ContactsDao();
        });
    }

    private void initViews() {
        AsyncTask.execute(() -> {
            contactProfilePic = contactDao.get(chatId).getUser().getProfilePic();
            runOnUiThread(() -> {
                showContactDetails();
                observeContactsChanges();
            });
        });

    }

    private void init() {
        btnExitChat = findViewById(R.id.btnExitChat);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        contactImg = findViewById(R.id.contactImg);
        tvContactName = findViewById(R.id.tvContactName);
        listViewMessages = findViewById(R.id.RecyclerViewMessages);
        etSendMessage = findViewById(R.id.etSendMessage);
        progressBar = findViewById(R.id.progressBar);
        listViewMessages.setLayoutManager(new LinearLayoutManager(this));
    }

    private Bitmap decodeBase64Image(String base64Image) {
        try {
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);
            byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showContactDetails() {
        // Convert the Base64-encoded string to a Bitmap
        Bitmap bitmap = decodeBase64Image(contactProfilePic);

        if (bitmap != null) {
            // Set the Bitmap as the image source for the ImageView
            contactImg.setImageBitmap(bitmap);
        }
        tvContactName.setText(contactDisplayName);
    }

    private void getMessages() {
        progressBar.setVisibility(View.VISIBLE);
        // request to the server - running on new thread
        viewModalChats.performGetMessages(serverToken, Integer.toString(chatId), this::handleGetMessagesCallback);
    }

    private void handleGetMessagesCallback(@NonNull List<Message> messages) {
        AsyncTask.execute(() -> {
            for (Message message : messages) {
                Message existingMessage = messagesDao.get(message.getId());
                if (existingMessage == null) {
                    message.setChatId(chatId);
                    messagesDao.insert(message);
                }
            }
            runOnUiThread(() -> messagesListLiveData.setMessagesList(messages));
        });
    }

    private void updateUIWithMessages(List<Message> messages) {
        // Change the UI using the adapter
        messageAdapter = new MessageAdapter(this, messages, currentUserUsername);
        messageAdapter.setMessages(messages);
        listViewMessages.setAdapter(messageAdapter);
        listViewMessages.setLayoutManager(new LinearLayoutManager(this));
        int lastPosition = messageAdapter.getItemCount() - 1;
        if (lastPosition >= 0) {
            listViewMessages.scrollToPosition(lastPosition);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void createListeners() {
        btnExitChat.setOnClickListener(v -> exitChat());

        btnSendMessage.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etSendMessage.getText().toString().trim())) {
                sendNewMessage(etSendMessage.getText().toString());
                etSendMessage.setText("");
            }
        });

        etSendMessage.setOnClickListener(v -> {
            int lastPosition = messageAdapter.getItemCount() - 1;
            if (lastPosition >= 0) {
                listViewMessages.scrollToPosition(lastPosition);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENTCHAT, "");
        editor.apply();
    }

    public void exitChat() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENTCHAT, "");
        editor.apply();
        messagesListLiveData.setMessagesList(Collections.emptyList());
        Intent intent = new Intent(ChatActivity.this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateContacts() {
        try {
            // request to the server - running on new thread
            viewModalChats.performUpdateContacts(serverToken, Integer.toString(chatId));
        }
        catch (Exception e) {
            // error
        }
    }

    private void sendNewMessage(String content) {
        // request to the server - running on new thread
        viewModalChats.performAddMessage(serverToken, Integer.toString(chatId), content, this::handleAddNewMessageCallback);
        updateContacts();

        // request to the server - add notification
        viewModalChats.performAddNotifications(serverToken, Integer.toString(chatId));
    }

    private void handleAddNewMessageCallback(Message newMessage) {
        AsyncTask.execute(() -> {
            newMessage.setChatId(chatId);
            messagesDao.insert(newMessage);
            Contact currentContact = contactDao.get(newMessage.getChatId());
            currentContact.setLastMessage(newMessage);
            contactDao.update(currentContact);
            runOnUiThread(() -> updateUIWithNewMessage(newMessage));
        });
    }

    private void updateUIWithNewMessage(Message newMessage) {
        messageAdapter.addMessage(newMessage);
        listViewMessages.setAdapter(messageAdapter);
        listViewMessages.setLayoutManager(new LinearLayoutManager(this));
        int lastPosition = messageAdapter.getItemCount() - 1;
        if (lastPosition >= 0) {
            listViewMessages.scrollToPosition(lastPosition);
        }
    }

    private void newContactArrived() {
        viewModalContacts.performGetContacts(serverToken, this::handleGetContactsCallback);
    }

    private void handleGetContactsCallback(List<Contact> contacts) {
        AsyncTask.execute(() -> {
            for (Contact contact : contacts) {
                Contact existingContact = contactDao.get(contact.getId());
                if (existingContact == null) {
                    contactDao.insert(contact);
                    contactsLiveDataList.addContact(contact);
                } else {
                    if (contact.getLastMessage() != null) {
                        contactDao.update(contact);
                    }
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void observeContactsChanges() {
        messagesListLiveData.getList().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messageList) {
                // Handle the onChanged event here
                updateUIWithMessages(messageList);
            }
        });

        contactsLiveDataList.getSomeoneDeleteMe().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                // Handle the onChanged event here
                if (value) {
                    exitChat();
                    contactsLiveDataList.setSomeoneDeleteMe(false);
                }
            }
        });

        contactsLiveDataList.getSomeoneAddMe().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                // Handle the onChanged event here
                if (value) {
                    // Someone added you
                    //get all contacts rom server
                    runOnUiThread(ChatActivity.this::newContactArrived);
                    // Perform the desired action
                    // Update the value of someoneAddMe to false
                    contactsLiveDataList.setSomeoneAddMe(false);
                }
            }
        });
    }
}