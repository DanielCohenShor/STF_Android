package com.example.stf.api;

import com.example.stf.entities.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WebServiceAPI {
    @GET("Users")
    Call<User> getUser(@Body String username);

    @POST("Users")
    Call<Void> createUser(@Body User user);

    @POST("Tokens")
    Call<String> createToken(@Body Map<String, String> tokenRequest);


    //more req
}
