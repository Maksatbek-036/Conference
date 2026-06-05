package com.example.conference.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;
import com.example.conference.Repositories.AuthRepository;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();

    public AuthViewModel() {
        this.repository = new AuthRepository();
    }

    // Регистрация
    public void register(String name, String email, String password) {
        RegisterUserRequest request = RegisterUserRequest.Create(name, email, password);

        repository.registerAsync(request, new AuthRepository.Callback() {
            @Override
            public void onSuccess(String tokenOrMessage) {
                // Для регистрации токен не нужен, просто успех
                authResult.postValue(new AuthResult(true, null, null));
            }

            @Override
            public void onError(String errorMessage) {
                authResult.postValue(new AuthResult(false, null, errorMessage));
            }
        });
    }

    // Логин
    public void login(String email, String password) {
        LoginUserRequest request = LoginUserRequest.Create(email, password);

        repository.oauthLoginAsync(request, new AuthRepository.Callback() {
            @Override
            public void onSuccess(String token) {
                authResult.postValue(new AuthResult(true, token, null));
            }

            @Override
            public void onError(String errorMessage) {
                authResult.postValue(new AuthResult(false, null, errorMessage));
            }
        });
    }

    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    // Вспомогательный класс для результата
    public static class AuthResult {
        private final boolean success;
        private final String token;
        private final String errorMessage;

        public AuthResult(boolean success, String token, String errorMessage) {
            this.success = success;
            this.token = token;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public String getToken() { return token; }
        public String getErrorMessage() { return errorMessage; }
    }
}
