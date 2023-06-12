package com.example.stf.Contacts;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.ContactAPI;
import com.example.stf.api.UserAPI;
import com.example.stf.entities.User;

import java.util.Objects;
import java.util.function.Consumer;

public class ViewModalContacts  extends ViewModel {

    private ContactAPI contactAPI;

    public ViewModalContacts() {
        this.contactAPI = new ContactAPI();
    }

    public void performGetContacts(String token, Consumer<Contact[]> callback) {
        contactAPI.setToken(token);
        contactAPI.get(callback);
    }

}

