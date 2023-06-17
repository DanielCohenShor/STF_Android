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

    public NotificationsAPI() {
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
            }
        });
    }
}
