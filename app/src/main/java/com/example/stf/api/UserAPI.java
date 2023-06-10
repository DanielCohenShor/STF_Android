package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.R;
import com.example.stf.entities.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    public UserAPI() {

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void get(String username) {
        Call<User> call = webServiceAPI.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                return;
            }
        });
    }

    public void post(User user, Consumer<String[]> callback) {
        Call<Void> call = webServiceAPI.createUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.accept(new String[0]); // Registration success, no errors
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        JsonElement jsonElement = JsonParser.parseString(errorResponse);
                        if (jsonElement.isJsonObject()) {
                            JsonArray errorsArray = jsonElement.getAsJsonObject().getAsJsonArray("errors");
                            String[] errors = new String[errorsArray.size()];
                            for (int i = 0; i < errorsArray.size(); i++) {
                                errors[i] = errorsArray.get(i).getAsString();
                            }
                            callback.accept(errors);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        String[] errors = {"Error parsing response"};
                        callback.accept(errors);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String[] errors = {"Network error"};
                callback.accept(errors);
            }
        });
    }


}
