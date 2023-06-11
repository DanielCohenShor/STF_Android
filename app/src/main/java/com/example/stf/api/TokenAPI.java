package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.R;
import com.example.stf.entities.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    public TokenAPI() {

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void post(String username, String password, Consumer<String> callback) {
        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("username", username);
        tokenRequest.put("password", password);

        Call<String> call = webServiceAPI.createToken(tokenRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String token = response.body();
                    callback.accept(token);
                    // Handle the token
                } else {
                    String errorResponse = null;
                    try {
                        errorResponse = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    callback.accept(errorResponse);
                    // Handle unsuccessful response
                }
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                int x = 5;
            }
        });
    }

}
