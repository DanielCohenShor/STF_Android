package com.example.stf.api;

import com.example.stf.MyApplication;
import com.example.stf.R;
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
    public TokenAPI() {

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
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
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String token = removeTokenPrefix(response.headers().get("Set-Cookie"));
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
            public void onFailure(Call<Void> call, Throwable t) {
                //todo: what to return ?
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
