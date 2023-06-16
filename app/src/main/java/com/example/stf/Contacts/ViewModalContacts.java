package com.example.stf.Contacts;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.ChatAPI;
import com.example.stf.api.ContactAPI;
import com.example.stf.entities.Contact;

import java.util.function.Consumer;

public class ViewModalContacts  extends ViewModel {

    private final ContactAPI contactAPI;
    private final ChatAPI chatAPI;


    public ViewModalContacts() {
        this.contactAPI = new ContactAPI();
        this.chatAPI = new ChatAPI();
    }

    public void performGetContacts(String token, Consumer<Contact[]> callback) {
        contactAPI.setToken(token);
        contactAPI.get(callback);
    }

    public void performAddContact(String token, String contactUsername, Consumer<Contact> callback) {
        contactAPI.setToken(token);
        contactAPI.post(contactUsername, callback);
    }

    public void performDeleteChat(String token, int chatId, Consumer<Integer> callback) {
        chatAPI.setToken(token);
        Log.d("Tag", "inside on performDeleteChat inside view model");
        chatAPI.deleteChat(chatId, callback);
    }
}
