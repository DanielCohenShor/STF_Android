package com.example.stf.api;

import androidx.annotation.NonNull;

import com.example.stf.entities.Contact;
import com.example.stf.MyApplication;
import com.example.stf.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ContactAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private String token;
    String baseUrl;
    public ContactAPI(String baseUrl) {
        this.baseUrl = baseUrl;
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void get(Consumer<Contact[]> callback) {
        Call<Contact[]> call = webServiceAPI.getContacts("Bearer {\"token\":\"" + token + "\"}");

        call.enqueue(new Callback<Contact[]>() {
            @Override
            public void onResponse(Call<Contact[]> call, Response<Contact[]> response) {
                try {
                    if (response.isSuccessful()) {
                        Contact[] contacts = response.body();
                        callback.accept(contacts);
                    } else {
                        // error from the get contacts?
                        //todo: what we need to return ?
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Contact[]> call, Throwable t) {
                int x = 5;
            }
        });

    }
    public void post (String contactUsername, Consumer<Contact> callback) {
        String reqToken = "Bearer {\"token\":\"" + token + "\"}";

        JsonObject usernameRequest = new JsonObject();
        usernameRequest.addProperty("username", contactUsername);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(usernameRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        // Make the API call
        Call<Contact> call = webServiceAPI.addContact(reqToken, requestBody);
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if (response.isSuccessful()) {
                    Contact contact = response.body();
                    callback.accept(contact);
                } else {
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {

            }
        });
    }

    public void removeAndroidToken() {
        Call<Void> call = webServiceAPI.removeAndroidToken("Bearer {\"token\":\"" + token + "\"}");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // okay
                } else  {
                    // error
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                //todo: what to return ?
                int x= 4;
            }
        });
    }
}