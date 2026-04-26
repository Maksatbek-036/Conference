package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager; // Важно!

import com.example.conference.Adapters.ConferenceAdapter;
import com.example.conference.Models.Conference;
import com.example.conference.ViewModels.ConferenceViewModel;
import com.example.conference.ViewModels.ConferenceViewModelFactory;
import com.example.conference.databinding.ActivityMainScreenBinding;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {
    private ActivityMainScreenBinding binding;
    private ConferenceViewModel viewModel;
    private ConferenceViewModelFactory viewModelFactory;
    private ConferenceAdapter adapter;
    private Cache cache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelFactory = new ConferenceViewModelFactory(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ConferenceViewModel.class);

        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cache=new Cache(this);
        // Инициализация адаптера с пустым списком
        adapter = new ConferenceAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        viewModel.loadConferences(cache.getToken());
        // Подписка на LiveData
        viewModel.getConferences().observe(this, conferences -> {
            adapter.setConferences(conferences); // метод в адаптере для обновления списка
            adapter.notifyDataSetChanged();
        });

        binding.button.setOnClickListener(v -> {
            // 1. Создание конференции (можно задать тестовые данные или взять из формы)
            String title = "Новая конференция";
            String description = "Автоматически создана при нажатии кнопки";
            long date = System.currentTimeMillis(); // текущая дата
            String startTime = "19:00";
            String endTime = "20:00";
            String location = "Онлайн";
            boolean isOnline = true;

            viewModel.createConference(title, description, date, startTime, endTime, location, isOnline);

            // 2. Запуск видеозвонка
            Intent intent = new Intent(this, VideoHub.class);
            startActivity(intent);
        });

        binding.myButton.setOnClickListener(v -> {
            if (binding.joinedForm.getVisibility() == View.GONE) {
                binding.joinedForm.setVisibility(View.VISIBLE);
            } else {
                binding.joinedForm.setVisibility(View.GONE);
            }
        });
        binding.sheduleButton.setOnClickListener(v -> {
            ScheduleConference bottomFragment = new ScheduleConference();
            bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
        });
    }
}
