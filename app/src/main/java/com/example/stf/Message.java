package com.example.stf;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;

@Entity
public class Message {
    @PrimaryKey (autoGenerate = true)
    private int id;
    private SimpleDateFormat created;
    private User sender;
    private String content;

    public Message(SimpleDateFormat created, User sender, String content) {
        this.created = created;
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public SimpleDateFormat getCreated() {
        return created;
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
