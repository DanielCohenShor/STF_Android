package com.example.stf.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stf.AppDB;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.R;
import com.example.stf.adapters.MessageAdapter;
import com.example.stf.entities.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnExitChat;

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

    private MessageAdapter messageAdapter;

    private String currentUserUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initDB();

        // init the xml and his stuff.
        init();

        showContactDetails();

        getMessages();

        //create listeners
        createListeners();
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "MessagesDB")
                    .fallbackToDestructiveMigration()
                    .build();
            messagesDao = db.messagesDao();
        });
    }

    private void init() {
        btnExitChat = findViewById(R.id.btnExitChat);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        contactImg = findViewById(R.id.contactImg);
        tvContactName = findViewById(R.id.tvContactName);
        listViewMessages = findViewById(R.id.RecyclerViewMessages);
        listViewMessages.setLayoutManager(new LinearLayoutManager(this));
        token = getIntent().getStringExtra("token");
        contactProfilePic = getIntent().getStringExtra("contactProfilePic");
        contactDisplayName = getIntent().getStringExtra("contactDisplayName");
        currentUserUsername = getIntent().getStringExtra("currentUserUsername");
        chatId = getIntent().getIntExtra("chatId", 0);
        viewModalChats = new ViewModelProvider(this).get(ViewModalChats.class);
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
        viewModalChats.performGetChat(token, Integer.toString(chatId), this::handleGetMessagesCallback);
    }

    private void handleGetMessagesCallback(@NonNull Message[] messages) {
        AsyncTask.execute(() -> {
            for (Message message : messages) {
                Message existingMessage = messagesDao.get(message.getId());
                if (existingMessage == null) {
                    messagesDao.insert(message);
                }
            }
        });

        updateUIWithMessages(messages);
    }

    private void updateUIWithMessages(Message[] messages) {
        // Change the UI using the adapter
        messageAdapter = new MessageAdapter(this, messages, currentUserUsername);
        messageAdapter.setMessages(messages);
        listViewMessages.setAdapter(messageAdapter);
        listViewMessages.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createListeners() {
        btnExitChat.setOnClickListener(v -> finish());
    }
}