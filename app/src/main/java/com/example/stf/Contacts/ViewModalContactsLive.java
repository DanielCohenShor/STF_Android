package com.example.stf.Contacts;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stf.entities.Chat;

public class ViewModalContactsLive extends ViewModel {

    private MutableLiveData<Chat> contacts;

    public MutableLiveData<Chat> getContacts() {
        if (contacts == null) {
            contacts = new MutableLiveData<Chat>();
        }
        return contacts;
    }
}
