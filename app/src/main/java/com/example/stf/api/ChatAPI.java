package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.R;
import com.example.stf.entities.Chat;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    private String token;

    public ChatAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void get(Consumer<Chat> callback) {
        Call<Chat> call = webServiceAPI.getChat("Bearer {\"token\":\"" + token + "\"}");

        call.enqueue(new Callback<Chat>() {
            @Override
            public void onResponse(Call<Chat> call, Response<Chat> response) {
                try {
                    if (response.isSuccessful()) {
                        Chat chat = response.body();
                        callback.accept(chat);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Chat> call, Throwable t) {
            }
        });
    }
}