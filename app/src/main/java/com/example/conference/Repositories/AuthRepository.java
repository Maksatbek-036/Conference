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

    public AuthRepository() {
        this.authApi = RetrofitClient.getApi(AuthApi.class);
    }

    // Логин пользователя — асинхронно
    public MutableLiveData<String> oauthLogin(LoginUserRequest loginUserRequest) {
        MutableLiveData<String> tokenLiveData = new MutableLiveData<>();

        authApi.login(loginUserRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenLiveData.setValue(response.body());
                    Log.d("AuthRepository", "Token: " + response.body());
                } else {
                    tokenLiveData.setValue("");
                    Log.e("AuthRepository", "Ошибка авторизации: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                tokenLiveData.setValue("");
                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });

        return tokenLiveData;
    }

    // Регистрация пользователя — асинхронно
    public MutableLiveData<Boolean> authenticateUser(RegisterUserRequest registerUserRequest) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        authApi.register(registerUserRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                resultLiveData.setValue(response.isSuccessful());
                if (response.isSuccessful()) {
                    Log.d("AuthRepository", "Регистрация успешна");
                } else {
                    Log.e("AuthRepository", "Ошибка регистрации: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultLiveData.setValue(false);
                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });

        return resultLiveData;
    }
}
