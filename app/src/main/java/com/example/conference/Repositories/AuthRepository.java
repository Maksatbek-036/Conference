package com.example.conference.Repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.conference.Api.AuthApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi authApi;
    private String token="";
    private Boolean result=false;

    public AuthRepository() {
        this.authApi = RetrofitClient.getApi(AuthApi.class);
    }

    // Логин пользователя — асинхронно
    public String oauthLogin(LoginUserRequest loginUserRequest) {


        authApi.login(loginUserRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                   token = response.body();
                    Log.d("AuthRepository", "Token: " + response.body());
                } else {
                    token="";
                    Log.e("AuthRepository", "Ошибка авторизации: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
              token="";
                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });

        return token;
    }

    // Регистрация пользователя — асинхронно
    public Boolean authenticateUser(RegisterUserRequest registerUserRequest) {
     result = false;

        authApi.register(registerUserRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    result = true;
                    Log.d("AuthRepository", "Регистрация успешна");
                } else {
                    result=false;
                    Log.e("AuthRepository", "Ошибка регистрации: " + response.code());
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });

        return result;
    }
}
