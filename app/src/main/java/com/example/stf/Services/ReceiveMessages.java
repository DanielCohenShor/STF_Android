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
import com.example.stf.Dao.ContactsDao;
import com.example.stf.Dao.MessagesDao;
import com.example.stf.R;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.example.stf.entities.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ReceiveMessages extends FirebaseMessagingService {
    int notificationId;
    AppDB db;
    ContactsDao contactsDao;
    MessagesDao messagesDao;
    private String chatID;
    private SharedPreferences sharedPreferences;
    private final String SERVERTOKEN = "serverToken";
    private final String DISPLAYNAME = "displayName";
    private final String PROFILEPIC = "photo";
    private final String CURRENTCHAT = "currentChat";
    private final String SERVERURL = "serverUrl";
    private final String USERNAME = "userName";

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
    }

    public void getChatId() {
        chatID = sharedPreferences.getString("currentChat", "");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        getChatId();
        String newChatId = message.getData().get("chatId");
        assert newChatId != null;
        if (!newChatId.equals(chatID)) {
            // not in the chat render the localdb to be in the right order
            // add the new message to the messages db

            String messageId = message.getData().get("messageId");
            String created = message.getData().get("messageDate");
            String senderUsername = message.getData().get("senderUsername");
            Contact contact = contactsDao.get(Integer.parseInt(newChatId));
            User sender = null;
            if (contact != null) {
                sender = contact.getUser();
            } else {
                // get from server
            }
            String content = message.getNotification().getBody();
            Message newMessage = new Message(Integer.parseInt(messageId), created, sender, content, Integer.parseInt(newChatId));
            messagesDao.insert(newMessage);

            // update the last message in the db
            contact.setLastMessage(newMessage);
            contactsDao.update(contact);

            //not in the chat need to open this chat
            sendNotification(message.getNotification().getBody(), newChatId, message.getNotification().getTitle());
        } else {
            // meant i am in the chat need to update the message live
            // reset the notfication
            // update last message
            // update localdb
            Log.d("tests", "not sending");
        }
    }

    private void addMessageToDB(int messageID, String created, User sender, String content, int chatid) {
        //get contact in the db and get the infromation from the db
    }

    private void sendNotification(String messageBody, String newchatId, String displayName) {
        //update the current chat to be the right chat.
        updateSharedPreferences(newchatId);
        // need to pass the chad id, the displayname.
        // so it will open the right chat with the right messeages.
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("contactDisplayName", displayName);
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

    private void updateSharedPreferences(String newchatId) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENTCHAT, newchatId);
        editor.apply();
    }
}
