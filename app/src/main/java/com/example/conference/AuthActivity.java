package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conference.Api.AuthApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.JWTPayload;
import com.example.conference.ViewModels.AuthViewModel;
import com.example.conference.databinding.ActivityAuthBinding;
import com.google.gson.Gson;

public class AuthActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private ActivityAuthBinding binding;
    private Cache cache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(this);

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AuthApi authApi = RetrofitClient.getApi(AuthApi.class);
        viewModel = new AuthViewModel();

        binding.signButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();

            // вызываем login → обновляется LiveData
            viewModel.login(email, password);
            var token=viewModel.getToken();

            String json = JWTDecoder.decodedPayload(token);

            if (json != null) {
                JWTPayload payload = new Gson().fromJson(json, JWTPayload.class);

                // Пример использования
                cache.saveUserInfo(payload);
                String username = payload.getName();
                Log.d("AUTH", "Добро пожаловать, " + username);
            }

            cache.saveToken(viewModel.getToken());
            if(cache.getToken()!=null){
                startActivity(new Intent(this, MainScreen.class));
            }

        });
        binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));

        });
    }
}
