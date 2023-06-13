package com.example.stf.Contacts;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.ContactAPI;
import com.example.stf.entities.Chat;
import com.example.stf.entities.User;

import java.util.function.Consumer;

public class ViewModalContacts  extends ViewModel {

    private ContactAPI contactAPI;

    public ViewModalContacts() {
        this.contactAPI = new ContactAPI();
    }

    public void performGetContacts(String token, Consumer<Chat[]> callback) {
        contactAPI.setToken(token);
        contactAPI.get(callback);
    }

    public void performAddContact(String token, User user, Consumer<Contact> callback) {
        contactAPI.setToken(token);
        contactAPI.post(user,callback);
    }
}