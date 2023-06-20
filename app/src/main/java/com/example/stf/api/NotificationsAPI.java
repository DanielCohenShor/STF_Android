package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.Notifications.UserNotification;
import com.example.stf.R;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotificationsAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private String token;

    public NotificationsAPI(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void getNotifications(Consumer<UserNotification> callback) {
        Call<UserNotification> call = webServiceAPI.getNotifications("Bearer {\"token\":\"" + token + "\"}");

        call.enqueue(new Callback<UserNotification>() {
            @Override
            public void onResponse(Call<UserNotification> call, Response<UserNotification> response) {
                try {
                    if (response.isSuccessful()) {
                        UserNotification notifications = response.body();
                        callback.accept(notifications);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<UserNotification> call, Throwable t) {
                int x =5;
            }
        });
    }

    public void resetNotifications(String chatId, Consumer<String> callback) {
        Call<Void> call = webServiceAPI.resetNotifications("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    if (response.isSuccessful()) {
                        callback.accept(chatId);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void addNotifications(String chatId) {
        Call<Void> call = webServiceAPI.addNotifications("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
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
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }


}