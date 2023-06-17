package com.example.stf.PushNotfications;

public class Notifications {
    private String title;
    private String message;

    public Notifications(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
