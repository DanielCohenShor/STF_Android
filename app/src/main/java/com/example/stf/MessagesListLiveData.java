package com.example.stf;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stf.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class MessagesListLiveData {
    private static MessagesListLiveData instance;
    private MutableLiveData<List<Message>> data;


    private MessagesListLiveData() {
        data = new MutableLiveData<>();
        data.postValue(new ArrayList<>());

    }

    public static synchronized MessagesListLiveData getInstance() {
        if (instance == null) {
            instance = new MessagesListLiveData();
        }
        return instance;
    }



    public void setMessagesList(List<Message> messagesList) {
        data.postValue(messagesList);
    }

    public LiveData<List<Message>> getList() {
        return data;
    }

    public void addMessage(Message message) {
        List<Message> currentList = data.getValue();
        if (currentList != null) {
            Log.d("Tag", "get the list size: " + currentList.size());
            currentList.add(message);
            Log.d("Tag", "get the new list size: " + currentList.size());
            data.postValue(currentList); // Update the LiveData with the updated list
        }
    }

}


