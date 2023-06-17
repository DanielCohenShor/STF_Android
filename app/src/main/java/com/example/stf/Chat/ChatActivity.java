package com.example.stf.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stf.AppDB;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.R;
import com.example.stf.adapters.MessageAdapter;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnExitChat;

    private EditText etSendMessage;

    private FloatingActionButton btnSendMessage;

    private CircleImageView contactImg;

    private TextView tvContactName;

    private String token;

    private int chatId;

    private String contactProfilePic;

    private String contactDisplayName;

    private ViewModalChats viewModalChats;

    private RecyclerView listViewMessages;

    private AppDB db;
    private MessagesDao messagesDao;

    private ContactsDao contactDao;

    private MessageAdapter messageAdapter;

    private String currentUserUsername;

    private String baseUrl;

    private SettingsDao settingsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_chat);

        initDB();

        // init the xml and his stuff.
        init();
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            messagesDao = db.messagesDao();
            contactDao = db.ContactsDao();
            settingsDao = db.settingsDao();
            baseUrl = settingsDao.getFirst().getServerUrl();
            viewModalChats = new ViewModalChats(baseUrl);
            //create listeners
            createListeners();

            showContactDetails();

            getMessages();
        });
    }

    private void init() {
        btnExitChat = findViewById(R.id.btnExitChat);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        contactImg = findViewById(R.id.contactImg);
        tvContactName = findViewById(R.id.tvContactName);
        listViewMessages = findViewById(R.id.RecyclerViewMessages);
        etSendMessage = findViewById(R.id.etSendMessage);

        token = getIntent().getStringExtra("token");
        contactProfilePic = getIntent().getStringExtra("contactProfilePic");
        contactDisplayName = getIntent().getStringExtra("contactDisplayName");
        currentUserUsername = getIntent().getStringExtra("currentUserUsername");
        chatId = getIntent().getIntExtra("chatId", 0);
        baseUrl = getIntent().getStringExtra("baseUrl");

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
        // request to the server - running on new thread
        viewModalChats.performGetMessages(token, Integer.toString(chatId), this::handleGetMessagesCallback);

        // request to the local database - running on new thread
        AsyncTask.execute(() -> {
            Message[] messages = messagesDao.getAllMessages(chatId);
            runOnUiThread(() -> updateUIWithMessages(messages));
        });
    }

    private void handleGetMessagesCallback(@NonNull Message[] messages) {
        AsyncTask.execute(() -> {
            for (Message message : messages) {
                Message existingMessage = messagesDao.get(message.getId());
                if (existingMessage == null) {
                    message.setChatId(chatId);
                    messagesDao.insert(message);
                }
            }
            runOnUiThread(() -> updateUIWithMessages(messages));
        });
    }

    private void updateUIWithMessages(Message[] messages) {
        // Change the UI using the adapter
        messageAdapter = new MessageAdapter(this, messages, currentUserUsername);
        messageAdapter.setMessages(messages);
        listViewMessages.setAdapter(messageAdapter);
        listViewMessages.setLayoutManager(new LinearLayoutManager(this));
        int lastPosition = messageAdapter.getItemCount() - 1;
        if (lastPosition >= 0) {
            listViewMessages.scrollToPosition(lastPosition);
        }
    }

    private void createListeners() {
        btnExitChat.setOnClickListener(v -> finish());

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

    private void updateContacts() {
        try {
            // request to the server - running on new thread
            viewModalChats.performUpdateContacts(token, Integer.toString(chatId));
        }
        catch (Exception e) {
            // error
        }
    }

    private void sendNewMessage(String content) {
        // request to the server - running on new thread
        viewModalChats.performAddMessage(token, Integer.toString(chatId), content, this::handleAddNewMessageCallback);
        updateContacts();
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
}