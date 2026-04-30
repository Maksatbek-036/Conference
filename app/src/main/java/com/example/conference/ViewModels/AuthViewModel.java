package com.example.conference.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;
import com.example.conference.Repositories.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
private String token="";

    public AuthViewModel( ) {
        this.repository = new AuthRepository();
    }

    // Регистрация
    public Boolean register(String name, String email, String password) {
        return repository.authenticateUser(RegisterUserRequest.Create(name, email, password));
    }

    // Логин
    public void login(String email, String password) {
        LoginUserRequest request = LoginUserRequest.Create(email, password);
        token = repository.oauthLogin(request);
    }

    public String getToken() {
        return token;
    }
}
