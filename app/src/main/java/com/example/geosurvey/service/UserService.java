package com.example.geosurvey.service;

import com.example.geosurvey.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {
    @POST("register")
    @FormUrlEncoded
    Call<User> registerUser(@Field("username") String username,
                            @Field("password") String password,
                            @Field("email") String email);

    @POST("register")
    Call<User> registerUser(@Body User user);

    @POST("login")
    Call<User> login(@Body User user);
}
