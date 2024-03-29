package com.example.stf.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    private String username;

    private String password;

    private String displayName;

    @ColumnInfo(name = "user_profile_pic")
    private String profilePic;

    // Empty constructor
    public User() {
        // Required empty constructor for Room
    }
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
