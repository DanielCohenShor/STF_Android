package com.example.stf.PushNotfications;

public class PushNotifications {
    private Notifications data;
    private String to;

    public PushNotifications(Notifications data, String to) {
        this.data = data;
        this.to = to;
    }

    public Notifications getData() {
        return data;
    }

    public String getTo() {
        return to;
    }
}
