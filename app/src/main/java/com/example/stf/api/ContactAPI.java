package com.example.stf.api;

import com.example.stf.Contacts.Contact;
import com.example.stf.MyApplication;
import com.example.stf.R;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private String token;
    public ContactAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
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
                if (response.isSuccessful()) {
                    Contact[] contacts = response.body();
                    callback.accept(contacts);
                } else {
                    //error from the get contacts?
                    //todo: what we need to return ?
                }
            }

            @Override
            public void onFailure(Call<Contact[]> call, Throwable t) {

            }
        });

    }
}
