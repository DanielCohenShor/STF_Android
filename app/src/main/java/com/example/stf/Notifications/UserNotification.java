package com.example.stf.Notifications;

public class UserNotification {
    private String username;

    private String password;

    private final ChatsNotification[] chats;

    public UserNotification(String username, String password, String displayName, ChatsNotification[] chats) {
        this.username = username;
        this.password = password;
        this.chats = chats;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ChatsNotification[] getChats() {
        return chats;
    }
}
