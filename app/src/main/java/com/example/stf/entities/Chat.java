package com.example.stf.entities;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Chat {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private int id;

    @Embedded
    private User user;

    private User[] users;

    @Embedded
    private Message[] messages;

    @Embedded
    @ColumnInfo(name = "last_message_id")
    private Message lastMessage;

    // create object of type with the two senders and receivers.
    public Chat(int id, User[] users, Message[] messages) {
        this.id = id;
        this.users = users;
        this.messages = messages;
        this.user = null;
    }

    // create object of type with only one (for create contact)
    public Chat(int id, User user, Message lastMessage) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.users = null;
        this.messages = null;
    }

    public int getId() {
        return id;
    }

    public User[] getUsers() {
        return users;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setUser(User[] users) {
        this.users = users;
    }

    public User getUser() {
        if (user != null) {
            return user;
        }
        return null;
    }
    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
