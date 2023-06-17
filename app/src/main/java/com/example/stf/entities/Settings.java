package com.example.stf.entities;

import androidx.room.Entity;

@Entity
public class Settings {
    private String serverUrl;

    private Boolean isDarkMode;

    private Boolean isShowDetails;

    public Settings(String serverUrl, Boolean isDarkMode, Boolean isShowDetails) {
        this.serverUrl = serverUrl;
        this.isDarkMode = isDarkMode;
        this.isShowDetails = isShowDetails;
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

    public Boolean getShowDetails() {
        return isShowDetails;
    }

    public void setShowDetails(Boolean showDetails) {
        isShowDetails = showDetails;
    }
}
