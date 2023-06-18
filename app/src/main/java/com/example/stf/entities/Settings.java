package com.example.stf.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.hdodenhof.circleimageview.CircleImageView;

@Entity
public class Settings {
    @PrimaryKey @NonNull
    private String serverUrl;
    private String displayname;
    private String photo;
    private String cuurentChat;
    public Settings(String serverUrl, String displayname) {
        this.serverUrl = serverUrl;
        this.displayname = displayname;
        cuurentChat = "";
    }

    public void setCuurentChat(String cuurentChat) {
        this.cuurentChat = cuurentChat;
    }

    public String getCuurentChat() {
        return cuurentChat;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
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
