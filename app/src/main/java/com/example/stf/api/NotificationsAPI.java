package com.example.stf.api;

import androidx.annotation.NonNull;

import com.example.stf.Notifications.UserNotification;

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
            public void onResponse(@NonNull Call<UserNotification> call, @NonNull Response<UserNotification> response) {
                try {
                    if (response.isSuccessful()) {
                        UserNotification notifications = response.body();
                        callback.accept(notifications);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserNotification> call, @NonNull Throwable t) {
            }
        });
    }

    public void resetNotifications(String chatId, Consumer<String> callback) {
        Call<Void> call = webServiceAPI.resetNotifications("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                try {
                    if (response.isSuccessful()) {
                        callback.accept(chatId);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }

    public void addNotifications(String chatId) {
        Call<Void> call = webServiceAPI.addNotifications("Bearer {\"token\":\"" + token + "\"}", chatId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                try {
                    if (response.isSuccessful()) {
                        // okay
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }


}