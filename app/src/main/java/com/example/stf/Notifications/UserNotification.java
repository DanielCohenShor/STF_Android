package com.example.stf.Notifications;

public class UserNotification {
    private String username;

    private String password;

    private String displayName;

    private String profilePic;

    private ChatsNotification[] chats;

    public UserNotification(String username, String password, String displayName, String profilePic, ChatsNotification[] chats) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.profilePic = profilePic;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public ChatsNotification[] getChats() {
        return chats;
    }

    public void setChats(ChatsNotification[] chats) {
        this.chats = chats;
    }
}
