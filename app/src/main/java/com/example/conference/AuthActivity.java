package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conference.Api.AuthApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.ViewModels.AuthViewModel;
import com.example.conference.databinding.ActivityAuthBinding;

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
        viewModel = new AuthViewModel(authApi);

        binding.signButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();

            // вызываем login → обновляется LiveData
            viewModel.login(email, password);

            // подписываемся на токен
            viewModel.getToken().observe(this, token -> {
                if (token != null && !token.isEmpty()) {
                    cache.saveToken(token); // сохраняем токен
                    Toast.makeText(this, "Успешный вход"+token, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainScreen.class));
                } else {
                    Toast.makeText(this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
