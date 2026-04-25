package com.example.conference;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conference.Api.AuthApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.ViewModels.AuthViewModel;
import com.example.conference.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация ViewBinding
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем API через RetrofitClient
        AuthApi authApi = RetrofitClient.getApi(AuthApi.class);

        // Передаём API в ViewModel
        viewModel = new AuthViewModel(authApi);

        // Пример: обработка кнопки входа
        binding.signButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();

            viewModel.login(email, password);
        });

    }
}
