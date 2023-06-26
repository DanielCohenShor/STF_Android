package com.example.stf.api;

import androidx.annotation.NonNull;

import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.function.Consumer;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChatAPI {

    Retrofit retrofit;
    Retrofit retrofitForFireBAse;
    WebServiceAPI webServiceAPI;
    WebServiceAPI webServiceAPIFireBase;

    private String token;

    String baseUrl;

    public ChatAPI(String baseUrl) {
        this.baseUrl = baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitForFireBAse = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webServiceAPI = retrofit.create(WebServiceAPI.class);

        webServiceAPIFireBase = retrofitForFireBAse.create(WebServiceAPI.class);


    }


    public void setToken(String token) {
        this.token = token;
    }

    public void get(String chatId, Consumer<List<Message>> callback) {
        Call< List<Message>> call = webServiceAPI.getMessages("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call< List<Message>> call, @NonNull Response< List<Message>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Message> messages = response.body();
                        callback.accept(messages);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(@NonNull Call< List<Message>> call, @NonNull Throwable t) {
            }
        });
    }

    public void post(String chatId, String content, Consumer<Message> callback) {
        String reqToken = "Bearer {\"token\":\"" + token + "\"}";

        JsonObject messageRequest = new JsonObject();
        messageRequest.addProperty("msg", content);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(messageRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<Message> call = webServiceAPI.addMessage(reqToken, chatId, requestBody);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                try {
                    if (response.isSuccessful()) {
                        Message newMessage = response.body();
                        callback.accept(newMessage);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
            }
        });
    }

    public void getUpdate(String chatId) {
        Call<Contact[]> call = webServiceAPI.getUpdateContacts("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Contact[]>() {
            @Override
            public void onResponse(@NonNull Call<Contact[]> call, @NonNull Response<Contact[]> response) {
                try {
                    if (response.isSuccessful()) {
                        // okay
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Contact[]> call, @NonNull Throwable t) {
            }
        });
    }

    public void deleteChat(int chatId, Consumer<Integer> callback) {
        Call<Void> call = webServiceAPI.deleteChat("Bearer {\"token\":\"" + token + "\"}", String.valueOf(chatId));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // delete
                if (response.isSuccessful()) {
                    callback.accept(chatId);
                } else {
                    callback.accept(-1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }
}