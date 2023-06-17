package com.example.stf.api;



import androidx.annotation.NonNull;

import com.example.stf.entities.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    String token; // Store the token here
    String baseUrl;
    public UserAPI(String baseUrl) {
        this.baseUrl =baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void get(String username, Consumer<String[]> callback) {
        Call<User> call = webServiceAPI.getUser("Bearer {\"token\":\"" + token + "\"}", username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User res = response.body();
                    String[] parm = new String[3];
                    assert res != null;
                    parm[0] = res.getUsername();
                    parm[1] = res.getDisplayName();
                    parm[2] = res.getProfilePic();
                    callback.accept(parm);
                } else  {
                    try {
                        assert response.errorBody() != null;
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                //todo: what to return ?
            }
        });
    }

    public void post(User user, Consumer<String[]> callback) {
        Call<Void> call = webServiceAPI.createUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.accept(new String[0]); // Registration success, no errors
                } else {
                    try {
                        assert response.errorBody() != null;
                        String errorResponse = response.errorBody().string();
                        JsonElement jsonElement = JsonParser.parseString(errorResponse);
                        if (jsonElement.isJsonObject()) {
                            if (response.code() == 409) {
                                String[] errors = new String[1];
                                errors[0] = "username exist";
                                callback.accept(errors);
                            } else {
                                JsonArray errorsArray = jsonElement.getAsJsonObject().getAsJsonArray("errors");
                                String[] errors = new String[errorsArray.size()];
                                for (int i = 0; i < errorsArray.size(); i++) {
                                    errors[i] = errorsArray.get(i).getAsString();
                                }
                                callback.accept(errors);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        String[] errors = {"Error parsing response"};
                        callback.accept(errors);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String[] errors = {"Network error"};
                callback.accept(errors);
            }
        });
    }

    public String getToken() {
        return token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
