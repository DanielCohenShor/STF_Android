package com.example.stf.Contacts;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.ChatAPI;
import com.example.stf.api.ContactAPI;
import com.example.stf.entities.Contact;

import java.util.Objects;
import java.util.function.Consumer;

public class ViewModalContacts  extends ViewModel {

    private ContactAPI contactAPI;
    private final ChatAPI chatAPI;
    private String baseUrl;

    public ViewModalContacts(String baseUrl) {
        this.baseUrl = baseUrl;
        this.contactAPI = new ContactAPI(baseUrl);
        this.chatAPI = new ChatAPI(baseUrl);
    }

    public void performGetContacts(String token, Consumer<Contact[]> callback) {
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
        }
    }
    public void performDeleteChat(String token, int chatId, Consumer<Integer> callback) {
        chatAPI.setToken(token);
        chatAPI.deleteChat(chatId, callback);
    }
}
