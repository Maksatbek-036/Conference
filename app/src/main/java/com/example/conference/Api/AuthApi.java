package com.example.conference.Api;

import com.example.conference.Contracts.LoginResponce;
import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("/api/auth/register")
    Call<Void> register(@Body RegisterUserRequest registerUserRequest);
    @POST("/api/auth/login")
    Call<String> login(@Body LoginUserRequest loginUserRequest);

}
