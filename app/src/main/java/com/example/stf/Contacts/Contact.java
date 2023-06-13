package com.example.stf.Contacts;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.stf.Chat.Message;
import com.example.stf.entities.User;

@Entity
public class Contact {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private int id;

    @Embedded(prefix = "user_") // Prefix for User fields
    private User user;

    @Embedded(prefix = "last_message_") // Prefix for Message fields
    private Message lastMessage;

    private int notifications;

    public Contact(int id, User user, Message lastMessage, int notifications) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.notifications = notifications;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public int getNotifications() {
        return notifications;
    }
}
