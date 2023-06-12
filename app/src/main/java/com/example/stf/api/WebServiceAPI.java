package com.example.stf.api;

import com.example.stf.Contacts.Contact;
import com.example.stf.entities.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebServiceAPI {
    @GET("Users/{username}")
    Call<User> getUser(@Header("Authorization") String token, @Path("username") String username);


    @POST("Users")
    Call<Void> createUser(@Body User user);

    @POST("Tokens")
    Call<Void> createToken(@Body RequestBody tokenRequest);

    @GET("Chats")
    Call<Contact[]> getContacts(@Header("Authorization") String token);

    //more req
}
