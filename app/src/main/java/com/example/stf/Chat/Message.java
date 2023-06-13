package com.example.stf.Chat;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.stf.entities.User;

@Entity
public class Message {
    @PrimaryKey
    private int id;

    private String created;

    private User sender;

    private String content;

    public Message(int id, String created, User sender, String content) {
        this.id = id;
        this.created = created;
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public User getUsers() {
        return sender;
    }

    public void setUsers(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
