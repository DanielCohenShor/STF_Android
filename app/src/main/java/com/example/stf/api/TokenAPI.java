package com.example.stf.api;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    String baseUrl;
    public TokenAPI(String baseUrl) {
        this.baseUrl = baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void post(String username, String password, Consumer<String> callback) {
        JsonObject tokenRequest = new JsonObject();
        tokenRequest.addProperty("username", username);
        tokenRequest.addProperty("password", password);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(tokenRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<Void> call = webServiceAPI.createToken(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    String token = removeTokenPrefix(response.headers().get("Set-Cookie"));
                    callback.accept(token);
                    // Handle the token
                } else {
                    String errorResponse;
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
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }

    // remove the token= from the token and everything after ;
    public String removeTokenPrefix(String input) {
        if (input != null && input.contains("token=")) {
            int tokenIndex = input.indexOf("token=");
            int semicolonIndex = input.indexOf(';', tokenIndex);
            if (semicolonIndex != -1) {
                return input.substring(tokenIndex + 6, semicolonIndex);
            } else {
                return input.substring(tokenIndex + 6);
            }
        }
        return input;
    }

}
