package com.example.stf.api;

import androidx.annotation.NonNull;

import com.example.stf.entities.Contact;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
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

    public void get(Consumer<List<Contact>> callback) {
        Call<List<Contact>> call = webServiceAPI.getContacts("Bearer {\"token\":\"" + token + "\"}");

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contact>> call, @NonNull Response<List<Contact>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Contact> contacts = response.body();
                        callback.accept(contacts);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contact>> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<Contact> call, @NonNull Response<Contact> response) {
                if (response.isSuccessful()) {
                    Contact contact = response.body();
                    callback.accept(contact);
                } else {
                    callback.accept(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Contact> call, @NonNull Throwable t) {

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
            }
        });
    }
}