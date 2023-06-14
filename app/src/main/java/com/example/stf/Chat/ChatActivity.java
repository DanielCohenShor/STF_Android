package com.example.stf.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stf.R;
import com.example.stf.entities.Chat;
import com.example.stf.entities.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnExitChat;

    private FloatingActionButton btnSendMessage;

    private CircleImageView contactImg;

    private TextView tvContactName;

    private String token;

    private ViewModalChats viewModalChats;

    private String contactProfilePic;

    private String contactDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // init the xml and his stuff.
        init();

        showContactDetails();

        //create listeners
        createListeners();
    }

    private void init() {
        btnExitChat = findViewById(R.id.btnExitChat);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        contactImg = findViewById(R.id.contactImg);
        tvContactName = findViewById(R.id.tvContactName);
        token = getIntent().getStringExtra("token");
        contactProfilePic = getIntent().getStringExtra("contactProfilePic");
        contactDisplayName = getIntent().getStringExtra("contactDisplayName");
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
        viewModalChats.performGetChat(token, this::handleGetChatCallback);
    }

    private void handleGetChatCallback(Chat chat) {
        AsyncTask.execute(() -> {
            for (Message message : chat.getMessages()) {
//                Contact existingContact = contactsDao.get(contact.getId());
//                if (existingContact == null) {
//                    contactsDao.insert(contact);
//                }
            }
        });
    }

    private void createListeners() {
        btnExitChat.setOnClickListener(v -> finish());
    }
}