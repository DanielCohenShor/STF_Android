package com.example.stf.PushNotfications;

public class PushNotifications {
    // to shoud be aUniqueKey = notification_key
    private String to;
    private Notifications data;


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
