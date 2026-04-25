package com.example.conference.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Api.AuthApi;
import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;
import com.example.conference.Models.User;
import com.example.conference.Repositories.AuthRepository;

public class AuthViewModel extends ViewModel {
    public MutableLiveData<User> user = new MutableLiveData<>();
    private final AuthRepository repository;
    private MutableLiveData<String> tokenLiveData = new MutableLiveData<>();

    public AuthViewModel(AuthApi authApi) {
        this.repository = new AuthRepository();
    }

    // Регистрация
    public LiveData<Boolean> register(String name, String email, String password) {
        return repository.authenticateUser(RegisterUserRequest.Create(name, email, password));
    }

    // Логин
    public void login(String email, String password) {
        LoginUserRequest request = LoginUserRequest.Create(email, password);
        tokenLiveData = repository.oauthLogin(request);
    }

    public LiveData<String> getToken() {
        return tokenLiveData;
    }
}
