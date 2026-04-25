package com.example.conference.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.conference.Api.AuthApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Contracts.LoginResponce;
import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;

    String token=";";
    public AuthRepository() {
        this.authApi = RetrofitClient.getApi(AuthApi.class);
    }

    // Регистрация пользователя
    public void authenticateUser(RegisterUserRequest registerUserRequest) {
        Call<Void> call = authApi.register(registerUserRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Log.d("AuthRepository", "Регистрация успешна");
                } else {
                    Log.e("AuthRepository", "Ошибка регистрации: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });
    }

    // Логин пользователя
    public String oauthLogin(LoginUserRequest loginUserRequest) {
        Call<LoginResponce> call = authApi.login(loginUserRequest);
        call.enqueue(new Callback<LoginResponce>() {
            @Override
            public void onResponse(Call<LoginResponce> call, @NonNull Response<LoginResponce> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    token=response.body().getToken();
                    Log.d("AuthRepository", "Авторизация успешна");

                }else{
                    Log.e("AuthRepository", "Ошибка авторизации: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<LoginResponce> call, Throwable t) {
            Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });


        return "";
    }
}
