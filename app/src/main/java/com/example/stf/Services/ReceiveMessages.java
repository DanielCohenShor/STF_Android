package com.example.stf.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.stf.AppDB;
import com.example.stf.Chat.ChatActivity;
import com.example.stf.Contacts.ViewModalContacts;
import com.example.stf.ContactsListLiveData;
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.MessagesListLiveData;
import com.example.stf.R;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.example.stf.entities.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class ReceiveMessages extends FirebaseMessagingService {
    int notificationId;
    AppDB db;
    ContactsDao contactsDao;
    MessagesDao messagesDao;
    private String chatID;
    private ViewModalContacts viewModalContacts;
    private SharedPreferences sharedPreferences;
    private final String SERVERTOKEN = "serverToken";
    private final String DISPLAYNAME = "displayName";
    private final String PROFILEPIC = "photo";
    private final String CURRENTCHAT = "currentChat";
    private final String SERVERURL = "serverUrl";
    private final String USERNAME = "userName";
    private String serverToken;
    private String baseUrl;
    private ContactsListLiveData contactsLiveDataList;
    private MessagesListLiveData messagesListLiveData;



    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        notificationId = 0;
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            contactsDao = db.ContactsDao();
            messagesDao = db.messagesDao();
        });
        contactsLiveDataList = ContactsListLiveData.getInstance();
        messagesListLiveData = MessagesListLiveData.getInstance();
    }

    public void getSharedPreferences() {
        chatID = sharedPreferences.getString(CURRENTCHAT, "");
        serverToken = sharedPreferences.getString(SERVERTOKEN, "");
        baseUrl = sharedPreferences.getString(SERVERURL, "");
        viewModalContacts = new ViewModalContacts(baseUrl);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String type = message.getData().get("type");
        getSharedPreferences();
        String newChatId = message.getData().get("chatId");
        assert newChatId != null;
        switch (type) {
            case "message":
                receiveMessage(message, newChatId);
                break;
            case "new contact":
                performGetNewContact();
                break;
            default:
                //delete chat
                deleteChat(newChatId);
                break;
        }
    }

    private void performGetNewContact() {
        contactsLiveDataList.setSomeoneAddMe(true);
    }

    private void deleteChat(String newChatId) {
        AsyncTask.execute(() -> {contactsDao.deleteByChatId(Integer.parseInt(newChatId));});
        contactsLiveDataList.setSomeoneDeleteMe(true);
        contactsLiveDataList.deleteContact(Integer.parseInt(newChatId));
    }

    private void receiveMessage(@NonNull RemoteMessage message, String newChatId) {
        if (!newChatId.equals(chatID)) {
            // not in the chat render the localdb to be in the right order
            // add the new message to the messages db
            addMessageToDB(message, newChatId);
        } else {
            // meant i am in the chat need to update the message live
            // reset the notfication
            // update last message
            // update localdb
            Log.d("tests", "not sending");
            createMessageANDupdateLastMessage(message, newChatId);
            resetNotfications(newChatId);
        }
    }

    private void resetNotfications(String newChatId) {
        //dont know what to do
    }
    private void createMessageANDupdateLastMessage(@NonNull RemoteMessage message, String newChatId) {
        //get contact in the db and get the infromation from the db
        String messageId = message.getData().get("messageId");
        String created = message.getData().get("messageDate");
        Contact contact = contactsDao.get(Integer.parseInt(newChatId));
        String content = message.getNotification().getBody();
        User sender = contact.getUser();
        int integerNewChatId = Integer.parseInt(newChatId);
        assert messageId != null;
        Message newLastMessage = new Message(Integer.parseInt(messageId), created, sender, content,integerNewChatId);
        AsyncTask.execute(() -> {messagesDao.insert(newLastMessage);});
        messagesListLiveData.addMessage(newLastMessage);
        AsyncTask.execute(() -> {
            int notfications = contactsDao.get(integerNewChatId).getNotifications() + 1;
            Contact updateContact = new Contact(integerNewChatId, sender, newLastMessage, notfications);
            contactsDao.update(updateContact);
        });
        sendNotification(message.getNotification().getBody(), newChatId, message.getNotification().getTitle(), "in chat");
    }

    private void addMessageToDB(@NonNull RemoteMessage message, String newChatId) {
        //get contact in the db and get the infromation from the db
        String messageId = message.getData().get("messageId");
        String created = message.getData().get("messageDate");
        Contact contact = contactsDao.get(Integer.parseInt(newChatId));
        String content = message.getNotification().getBody();
        User sender = null;
        if (contact != null) {
            Log.d("not new chat", "not new chat ");
            //the contact is exist.
             sender = contact.getUser();
            assert messageId != null;
            int integerNewChatId = Integer.parseInt(newChatId);
            Log.d("Tag", "After change to int: " + integerNewChatId);
            Message newLastMessage = new Message(Integer.parseInt(messageId), created, sender, content,integerNewChatId);
            AsyncTask.execute(() -> {messagesDao.insert(newLastMessage);});
            updateLastMessage(integerNewChatId, newLastMessage, sender);
            //not in the chat need to open this chat
            sendNotification(message.getNotification().getBody(), newChatId, message.getNotification().getTitle(), "exist chat");
        } else {
            Log.d("new chat", "new chat");
            //the contact not exist
            // get from server
            newChatAdded(newChatId);
            sendNotification(message.getNotification().getBody(), newChatId, message.getNotification().getTitle(), "new chat");

        }
    }

    private void updateLastMessage(int newChatId, Message newLastMessage, User sender) {
        //TODO: check if need to update in the server the notfications ??
        AsyncTask.execute(() -> {
            int notfications = contactsDao.get(newChatId).getNotifications() + 1;
            Contact updateContact = new Contact(newChatId, sender, newLastMessage, notfications);
            contactsDao.update(updateContact);
            contactsLiveDataList.setContactsList(contactsDao.getAllContacts());
        });
    }

    private void sendNotification(String messageBody, String newchatId, String displayName, String flag) {
        if (!Objects.equals(flag, "in chat")) {

            //update the current chat to be the right chat.
            updateSharedPreferences(newchatId);
            // need to pass the chad id, the displayname.
            // so it will open the right chat with the right messeages.
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("contactDisplayName", displayName);
            intent.putExtra("flag", flag);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Updated code
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_MUTABLE;
            } else {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

            String channelId = "any_value";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("my new notification")
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    private void updateSharedPreferences(String newchatId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENTCHAT, newchatId);
        editor.apply();
    }

    private void newChatAdded(String newChatId) {
        // create req to the server to get this contact and add him to the local db


    }
}
