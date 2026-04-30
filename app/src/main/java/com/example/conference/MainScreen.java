package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.ConferenceAdapter;
import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.Conference;
import com.example.conference.Repositories.ConferenceRepository;
import com.example.conference.databinding.ActivityMainScreenBinding;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {
    private ActivityMainScreenBinding binding;
    private ConferenceRepository repository;
    private ConferenceApi api= RetrofitClient.getApi(ConferenceApi.class);
    private ConferenceAdapter adapter;

    private Cache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cache = new Cache(this);


        // Настройка RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new ConferenceAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        
        loadConferences();



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

    private void loadConferences() {
        repository = new ConferenceRepository(api);
        repository.fetchConferences(new ConferenceRepository.ConferenceCallback() {
            @Override
            public void onSuccess(List<Conference> conferences) {
                adapter.setConferences(conferences);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainScreen.this, "Ошибка загрузки: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
