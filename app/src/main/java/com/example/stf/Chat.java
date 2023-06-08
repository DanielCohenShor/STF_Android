package com.example.stf;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Chat {
    @PrimaryKey (autoGenerate = true)
    private int id;
    private List<User> users;
    private List<Message> messages;

    public Chat(List<User> users, List<Message> messages) {
        this.users = users;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
