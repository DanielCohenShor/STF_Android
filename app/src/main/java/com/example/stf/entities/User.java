package com.example.stf.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    private String username;

    private String password;

    private String displayName;

    private String profilePic;

    public User(String username, String password, String displayName, String propilePic) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.profilePic = propilePic;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPropilePic(String propilePic) {
        this.profilePic = propilePic;
    }


}
