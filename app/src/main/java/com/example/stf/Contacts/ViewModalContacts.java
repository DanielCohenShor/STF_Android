package com.example.stf.Contacts;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.ContactAPI;
import com.example.stf.entities.Chat;

import java.io.Serializable;
import java.util.function.Consumer;

public class ViewModalContacts extends ViewModel {

    private ContactAPI contactAPI;

    private MutableLiveData<Chat> contacts;

    public MutableLiveData<Chat> getContacts() {
        if (contacts == null) {
            contacts = new MutableLiveData<Chat>();
        }
        return contacts;
    }

    public ViewModalContacts() {
        this.contactAPI = new ContactAPI();
    }

    public void performGetContacts(String token, Consumer<Chat[]> callback) {
        contactAPI.setToken(token);
        contactAPI.get(callback);
    }

    public void performAddContact(String token, String contactUsername, Consumer<Chat> callback) {
        contactAPI.setToken(token);
        contactAPI.post(contactUsername, callback);
    }
}
