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

    private int notficatins;

    public Contact(int id, User user, String lastMessage, int notficatins) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.notficatins = notficatins;
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

    public int getNotficatins() {
        return notficatins;
    }

}
