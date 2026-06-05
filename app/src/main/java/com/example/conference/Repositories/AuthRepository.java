package com.example.conference.Repositories;

import android.annotation.SuppressLint;
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

    // Интерфейс для обратного вызова
    public interface Callback {
        void onSuccess(String token);
        void onError(String errorMessage);
    }

    // Логин пользователя — асинхронно
    public void oauthLoginAsync(LoginUserRequest loginUserRequest, Callback callback) {
        authApi.login(loginUserRequest).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    Log.d("AuthRepository", "Token: " + response.body());
                } else {
                    callback.onError("Ошибка авторизации: " + response.code());
                    Log.e("AuthRepository", "Ошибка авторизации: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onError("Ошибка сети: " + t.getMessage());
                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });
    }

    // Регистрация пользователя — асинхронно
    public void registerAsync(RegisterUserRequest registerUserRequest, Callback callback) {
        authApi.register(registerUserRequest).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Регистрация успешна");
                    Log.d("AuthRepository", "Регистрация успешна");
                } else {
                    callback.onError("Ошибка регистрации: " + response.code());
                    Log.e("AuthRepository", "Ошибка регистрации: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Ошибка сети: " + t.getMessage());
                Log.e("AuthRepository", "Ошибка сети: " + t.getMessage());
            }
        });
    }
}

