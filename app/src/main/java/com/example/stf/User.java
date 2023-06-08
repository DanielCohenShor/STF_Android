package com.example.stf;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class User {
    @PrimaryKey
    private String username;
    private String password;
    private String displayName;
    private String profilePic;
    private List<Chat> chats;

    public User(String username, String password, String displayName, String profilePic) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }
}
