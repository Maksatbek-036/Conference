package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.ConferenceAdapter;
import com.example.conference.ViewModels.ConferenceViewModel;
import com.example.conference.ViewModels.ConferenceViewModelFactory;
import com.example.conference.databinding.ActivityMainScreenBinding;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {
    private ActivityMainScreenBinding binding;
    private ConferenceViewModel viewModel;
    private ConferenceAdapter adapter;
    private Cache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cache = new Cache(this);
        
        // Настройка ViewModel
        ConferenceViewModelFactory factory = new ConferenceViewModelFactory(this);
        viewModel = new ViewModelProvider(this, factory).get(ConferenceViewModel.class);

        // Настройка RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConferenceAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);

        // Наблюдение за данными
        viewModel.getConferences().observe(this, conferences -> {
            if (conferences != null) {
                Log.d("MainScreen", "Loaded " + conferences.size() + " conferences");
                adapter.setConferences(conferences);
                adapter.notifyDataSetChanged();
            } else {
                Log.e("MainScreen", "Failed to load conferences or list is empty");
            }
        });

        // Загрузка данных
        viewModel.loadConferences(cache.getToken());

        // Кнопка "Новая встреча" (Генерируем случайный ID для комнаты)
        binding.button.setOnClickListener(v -> {
            String randomRoomId = "ROOM_" + (int)(Math.random() * 1000000);
            Intent intent = new Intent(this, VideoHub.class);
            intent.putExtra("ROOM_ID", randomRoomId);
            startActivity(intent);
        });

        // Кнопка "Присоединиться" (показать/скрыть форму)
        binding.myButton.setOnClickListener(v -> {
            int visibility = binding.joinedForm.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            binding.joinedForm.setVisibility(visibility);
        });

        // Кнопка "Войти" (по коду из EditText)
        binding.joinButton.setOnClickListener(v -> {
            String roomCode = binding.joinCodeInput.getText().toString().trim();
            if (!roomCode.isEmpty()) {
                Intent intent = new Intent(this, VideoHub.class);
                intent.putExtra("ROOM_ID", roomCode);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Введите код встречи", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка "Запланировать"
        binding.sheduleButton.setOnClickListener(v -> {
            ScheduleConference scheduleFragment = new ScheduleConference();
            scheduleFragment.show(getSupportFragmentManager(), scheduleFragment.getTag());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем список при возврате на экран
        if (cache.getToken() != null) {
            viewModel.loadConferences(cache.getToken());
        }
    }
}
