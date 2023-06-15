package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.R;
import com.example.stf.entities.Message;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChatAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    private String token;

    public ChatAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
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
}