package com.example.stf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stf.entities.Contact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactsListLiveData {
    private static ContactsListLiveData instance;
    private MutableLiveData<List<Contact>> data;
    private MutableLiveData<Boolean> someoneAddMe;
    private MutableLiveData<Boolean> someoneDeleteMe;

    private ContactsListLiveData() {
        data = new MutableLiveData<>();
        data.postValue(new ArrayList<>());
        someoneAddMe = new MutableLiveData<>();
        someoneAddMe.postValue(false);
        someoneDeleteMe = new MutableLiveData<>();
        someoneDeleteMe.postValue(false);
    }

    public static synchronized ContactsListLiveData getInstance() {
        if (instance == null) {
            instance = new ContactsListLiveData();
        }
        return instance;
    }

    public LiveData<Boolean> getSomeoneAddMe() {
        return someoneAddMe;
    }

    public void setSomeoneAddMe(boolean value) {
        someoneAddMe.postValue(value);
    }

    public LiveData<Boolean> getSomeoneDeleteMe() {
        return someoneDeleteMe;
    }

    public void setSomeoneDeleteMe(boolean value) {
        someoneDeleteMe.postValue(value);
    }

    public void setContactsList(List<Contact> contactsList) {
        data.postValue(contactsList);
    }

    public LiveData<List<Contact>> getList() {
        return data;
    }

    public void addContact(Contact contact) {
        List<Contact> currentList = data.getValue();
        if (currentList != null) {
            currentList.add(contact);
            data.postValue(currentList);
        }
    }

    public void deleteContact(int chatId) {
        List<Contact> currentList = data.getValue();
        if (currentList != null) {
            Iterator<Contact> iterator = currentList.iterator();
            while (iterator.hasNext()) {
                Contact contact = iterator.next();
                if (contact.getId() == chatId) {
                    iterator.remove();
                    break;
                }
            }
            data.postValue(currentList);
        }
    }
}
