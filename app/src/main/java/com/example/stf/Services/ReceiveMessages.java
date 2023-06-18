package com.example.stf.Services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.example.stf.AppDB;
import com.example.stf.Chat.ChatActivity;
import com.example.stf.Dao.SettingsDao;
import com.example.stf.Login.LoginActivity;
import com.example.stf.Login.ViewModelLogin;
import com.example.stf.R;
import com.example.stf.entities.Settings;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.logging.LogRecord;

public class ReceiveMessages extends FirebaseMessagingService {
    int notificationId;
    AppDB db;
    private SettingsDao settingsDao;
    private String chatID;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationId = 0;
        initDB();
    }

    public void initDB() {
        AsyncTask.execute(() -> {
            db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "STF_DB")
                    .fallbackToDestructiveMigration()
                    .build();
            settingsDao = db.settingsDao();
        });
    }

    public void test() {
        AsyncTask.execute(() -> {
            chatID = settingsDao.getFirst().getCuurentChat();
            Log.d("TAG",  "the chat id is: "+ chatID);
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        test();
        sendNotification(message.getNotification().getBody());
    }

    private void sendNotification(String messageBody) {
        // need to pass the chad id, the displayname, the token to the chat.
        // so it will open the right chat with the right messeages.
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

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
