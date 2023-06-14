package com.example.stf.api;

import com.example.stf.entities.Chat;
import com.example.stf.entities.Contact;
import com.example.stf.entities.User;

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

    @POST("Chats")
    Call<Contact> addContact(@Header("Authorization") String token, @Body RequestBody username);

    @GET("Chats/:id")
    Call<Chat> getChat(@Header("Authorization") String token);

    //more req
}
