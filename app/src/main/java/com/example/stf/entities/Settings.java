package com.example.stf.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.hdodenhof.circleimageview.CircleImageView;

@Entity
public class Settings {
    @PrimaryKey @NonNull
    private String serverUrl;

    private Boolean isDarkMode;

    private String displayname;
    private String photo;
    public Settings(String serverUrl, Boolean isDarkMode, String displayname) {
        this.serverUrl = serverUrl;
        this.isDarkMode = isDarkMode;
        this.displayname = displayname;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Boolean getDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(Boolean darkMode) {
        isDarkMode = darkMode;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
