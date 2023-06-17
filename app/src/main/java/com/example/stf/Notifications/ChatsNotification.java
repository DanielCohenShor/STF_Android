package com.example.stf.Notifications;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class ChatsNotification {
    private int id;
    private ArrayList<LinkedTreeMap<String, String>> users;
    private Object messages;

    public ChatsNotification(int id, ArrayList<LinkedTreeMap<String, String>> users, Object messages) {
        this.id = id;
        this.users = users;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<LinkedTreeMap<String, String>> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<LinkedTreeMap<String, String>> users) {
        this.users = users;
    }

    public Object getMessages() {
        return messages;
    }

    public void setMessages(Object messages) {
        this.messages = messages;
    }
}
