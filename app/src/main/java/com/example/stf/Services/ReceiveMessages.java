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
import android.view.View;

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

import java.util.List;
import java.util.Objects;

public class ReceiveMessages extends FirebaseMessagingService {
    int notificationId;
    AppDB db;
    ContactsDao contactsDao;
    MessagesDao messagesDao;
    private String currentChatID;
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
        currentChatID = sharedPreferences.getString(CURRENTCHAT, "");
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
        if (Objects.equals(newChatId, currentChatID)) {
            contactsLiveDataList.setSomeoneDeleteMe(true);
        }
        contactsLiveDataList.deleteContact(Integer.parseInt(newChatId));
    }

    private void receiveMessage(@NonNull RemoteMessage message, String newChatId) {
        Log.d("Tag", "the cuurent chat id: " + currentChatID);
        Log.d("Tag", "the newChatId chat id: " + newChatId);
        if (!newChatId.equals(currentChatID)) {
            // not in the chat render the localdb to be in the right order
            // add the new message to the messages db
            addMessageToDB(message, newChatId);
        } else {
            // meant i am in the chat need to update the message live
            // update last message
            createMessageANDupdateLastMessage(message, newChatId);
            // reset the notification
            resetNotifications(newChatId);
        }
    }

    private void resetNotifications(String newChatId) {
        //send to server req
        viewModalContacts.performResetNotifications(serverToken, newChatId, this::handleResetNotificationsCallback);
    }

    public void handleResetNotificationsCallback(String chatId) {
        AsyncTask.execute(() -> {
            Contact updateContact = contactsDao.get(Integer.parseInt(chatId));
            updateContact.setNotifications(0);
            contactsDao.update(updateContact);
        });
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
    }

    private void addMessageToDB(@NonNull RemoteMessage message, String newChatId) {
        //get contact in the db and get the infromation from the db
        String messageId = message.getData().get("messageId");
        String created = message.getData().get("messageDate");
        Contact contact = contactsDao.get(Integer.parseInt(newChatId));
        String content = message.getNotification().getBody();
        User sender;
        Log.d("not new chat", "not new chat ");
        //the contact is exist.
        sender = contact.getUser();
        assert messageId != null;
        int integerNewChatId = Integer.parseInt(newChatId);
        Log.d("Tag", "After change to int: " + integerNewChatId);
        Message newLastMessage = new Message(Integer.parseInt(messageId), created, sender, content,integerNewChatId);
        AsyncTask.execute(() -> {
            List<Message> messagesList = messagesDao.getAllMessages(Integer.parseInt(newChatId));
            Log.d("Tag", "size of the list: " + messagesList.size());
            if (!messagesList.isEmpty()) {
                messagesDao.insert(newLastMessage);
            }
        });
        updateLastMessage(integerNewChatId, newLastMessage, sender);
        //not in the chat need to open this chat
        sendNotification(message.getNotification().getBody(), newChatId, message.getNotification().getTitle());
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

    private void sendNotification(String content, String newchatId, String displayName) {
        //update the current chat to be the right chat.
        // need to pass the chad id, the displayname.
        // so it will open the right chat with the right messeages.
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("contactDisplayName", displayName);
        intent.putExtra("newchatId", newchatId);
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
                        .setContentTitle(displayName)
                        .setContentText(content)
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
        notificationManager.notify(Integer.parseInt(newchatId), notificationBuilder.build());
    }


}
