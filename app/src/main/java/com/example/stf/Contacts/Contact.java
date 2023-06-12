package com.example.stf.Contacts;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.stf.entities.User;

@Entity
public class Contact {
    @PrimaryKey
    private int id;

    private User user;

    private String lastMessage;

    private int notifications;

    public Contact(int id, User user, String lastMessage, int notifications) {
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

    public String getLastMessage() {
        return lastMessage;
    }

    public int getNotifications() {
        return notifications;
    }
}
