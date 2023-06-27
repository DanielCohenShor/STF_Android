package com.example.stf.Contacts;

import androidx.lifecycle.ViewModel;

import com.example.stf.Notifications.UserNotification;
import com.example.stf.api.ChatAPI;
import com.example.stf.api.ContactAPI;
import com.example.stf.api.NotificationsAPI;
import com.example.stf.entities.Contact;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ViewModalContacts  extends ViewModel {

    private ContactAPI contactAPI;
    private ChatAPI chatAPI;
    private NotificationsAPI notificationsAPI;
    private final String baseUrl;

    public ViewModalContacts(String baseUrl) {
        this.baseUrl = baseUrl;
        this.contactAPI = new ContactAPI(baseUrl);
        this.chatAPI = new ChatAPI(baseUrl);
        this.notificationsAPI = new NotificationsAPI(baseUrl);
    }

    public void performGetContacts(String token, Consumer<List<Contact>> callback) {
        contactAPI.setToken(token);
        contactAPI.get(callback);
    }

    public void performAddContact(String token, String contactUsername, Consumer<Contact> callback) {
        contactAPI.setToken(token);
        contactAPI.post(contactUsername, callback);
    }

    public void setBaseUrl(String baseUrl) {
        if (!Objects.equals(baseUrl, this.baseUrl)) {
            this.contactAPI = new ContactAPI(baseUrl);
            this.chatAPI = new ChatAPI(baseUrl);
            this.notificationsAPI = new NotificationsAPI(baseUrl);
        }
    }
    public void performDeleteChat(String token, int chatId, Consumer<Integer> callback) {
        chatAPI.setToken(token);
        chatAPI.deleteChat(chatId, callback);
    }

    public void performGetNotifications(String token, Consumer<UserNotification> callback) {
        notificationsAPI.setToken(token);
        notificationsAPI.getNotifications(callback);
    }

    public void performResetNotifications(String token, String chatId, Consumer<String> callback) {
        notificationsAPI.setToken(token);
        notificationsAPI.resetNotifications(chatId, callback);
    }

    public void removeAndroidToken(String token) {
        contactAPI.setToken(token);
        contactAPI.removeAndroidToken();
    }
}
