package com.example.stf.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.stf.Chat.Message;

@Entity
public class Chat {
    @PrimaryKey
    private int id;

    private User user;
    private User[] users;

    private Message[] messages;

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
