package com.example.stf.Chat;

import androidx.lifecycle.ViewModel;

import com.example.stf.api.ChatAPI;
import com.example.stf.api.NotificationsAPI;
import com.example.stf.entities.Message;

import java.util.List;
import java.util.function.Consumer;

public class ViewModalChats extends ViewModel {
    private final ChatAPI chatAPI;

    private final NotificationsAPI notificationsAPI;

    public ViewModalChats(String baseUrl) {
        this.chatAPI = new ChatAPI(baseUrl);
        this.notificationsAPI = new NotificationsAPI(baseUrl);
    }

    public void performGetMessages(String token, String chatId, Consumer<List<Message>> callback) {
        chatAPI.setToken(token);
        chatAPI.get(chatId, callback);
    }

    public void performAddMessage(String token, String chatId, String content, Consumer<Message> callback) {
        chatAPI.setToken(token);
        chatAPI.post(chatId, content, callback);
    }

    public void performUpdateContacts(String token, String chatId) {
        chatAPI.setToken(token);
        chatAPI.getUpdate(chatId);
    }

    public void performAddNotifications(String token, String chatId) {
        notificationsAPI.setToken(token);
        notificationsAPI.addNotifications(chatId);
    }
}