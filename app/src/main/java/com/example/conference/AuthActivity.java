package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

        viewModel = new AuthViewModel();

        // Если токен уже сохранён — сразу идём на главный экран
        if (cache.getToken() != null) {
            startActivity(new Intent(this, MainScreen.class));
            finish();
            return;
        }

        binding.signButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(email, password);
        });

        // Подписка на результат авторизации
        viewModel.getAuthResult().observe(this, result -> {
            if (result.isSuccess()) {
                String token = result.getToken();
                if (token != null) {
                    cache.saveToken(token);

                    String json = JWTDecoder.decodedPayload(token);
                    if (json != null) {
                        JWTPayload payload = new Gson().fromJson(json, JWTPayload.class);
                        cache.saveUserInfo(payload);
                        Log.d("AUTH", "Добро пожаловать, " + payload.getName());
                    }
                }

                Toast.makeText(this, "Вход успешен", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainScreen.class));
                finish();
            } else {
                Toast.makeText(this, "Ошибка входа", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

