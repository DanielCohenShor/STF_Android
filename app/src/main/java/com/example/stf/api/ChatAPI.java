package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.R;
import com.example.stf.entities.Contact;
import com.example.stf.entities.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

    private String token;

    public ChatAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googkeapis.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webServiceAPI = retrofit.create(WebServiceAPI.class);


    }

    public void setToken(String token) {
        this.token = token;
    }

    public void get(String chatId, Consumer<Message[]> callback) {
        Call<Message[]> call = webServiceAPI.getMessages("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Message[]>() {
            @Override
            public void onResponse(Call<Message[]> call, Response<Message[]> response) {
                try {
                    if (response.isSuccessful()) {
                        Message[] messages = response.body();
                        callback.accept(messages);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Message[]> call, Throwable t) {
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
            public void onResponse(Call<Message> call, Response<Message> response) {
                try {
                    if (response.isSuccessful()) {
                        Message newMessage = response.body();
                        callback.accept(newMessage);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }

    public void PostNotifaction() {
        Call<Void> call = webServiceAPI.PostNotifaction("test");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }
    public void getUpdate(String chatId) {
        Call<Contact[]> call = webServiceAPI.getUpdateContacts("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Contact[]>() {
            @Override
            public void onResponse(Call<Contact[]> call, Response<Contact[]> response) {
                try {
                    if (response.isSuccessful()) {
                        // okay
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Contact[]> call, Throwable t) {
            }
        });
    }
}