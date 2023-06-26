package com.example.stf.Notifications;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

public class ChatsNotification {
    private int id;
    private final ArrayList<LinkedTreeMap<String, String>> users;

    public ChatsNotification(int id, ArrayList<LinkedTreeMap<String, String>> users) {
        this.id = id;
        this.users = users;
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
}
