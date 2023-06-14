package com.example.stf.Chat;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.ChatAPI;
import com.example.stf.entities.Chat;

import java.util.function.Consumer;

public class ViewModalChats extends ViewModel {
    private final ChatAPI chatAPI;

    public ViewModalChats() {
        this.chatAPI = new ChatAPI();
    }

    public void performGetChat(String token, Consumer<Chat> callback) {
        chatAPI.setToken(token);
        chatAPI.get(callback);
    }
}