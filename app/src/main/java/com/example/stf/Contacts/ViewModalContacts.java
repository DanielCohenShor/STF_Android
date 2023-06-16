package com.example.stf.Contacts;
import androidx.lifecycle.ViewModel;

import com.example.stf.api.ContactAPI;
import com.example.stf.entities.Contact;

import java.util.function.Consumer;

public class ViewModalContacts  extends ViewModel {

    private final ContactAPI contactAPI;

    public ViewModalContacts(String baseUrl) {
        this.contactAPI = new ContactAPI(baseUrl);
    }

    public void performGetContacts(String token, Consumer<Contact[]> callback) {
        contactAPI.setToken(token);
        contactAPI.get(callback);
    }

    public void performAddContact(String token, String contactUsername, Consumer<Contact> callback) {
        contactAPI.setToken(token);
        //add new thread
        contactAPI.post(contactUsername, callback);
    }
}
