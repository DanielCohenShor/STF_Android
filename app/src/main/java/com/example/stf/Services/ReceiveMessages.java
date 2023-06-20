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
import com.example.stf.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ReceiveMessages extends FirebaseMessagingService {
    int notificationId;
    AppDB db;
    private String chatID;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        notificationId = 0;
    }


    public void getChatId() {
        chatID = sharedPreferences.getString("currentChat", "");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        getChatId();
        sendNotification(message.getNotification().getBody());
    }

    private void sendNotification(String messageBody) {
        // need to pass the chad id, the displayname, the token to the chat.
        // so it will open the right chat with the right messeages.
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


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
