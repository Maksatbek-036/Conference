package com.example.conference;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // Важно!

import com.example.conference.Adapters.ConferenceAdapter;
import com.example.conference.Models.Conference;
import com.example.conference.databinding.ActivityMainScreenBinding;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    // Рекомендуется использовать интерфейс List для объявления
    private final ArrayList<Conference> conferences = new ArrayList<>();
    private ActivityMainScreenBinding binding;
    private ConferenceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Инициализация Binding
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Настройка RecyclerView (LayoutManager обязателен!)
        // Если он не прописан в XML, добавьте эту строку:
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Загрузка данных
        loadConferences();

        // 4. Инициализация адаптера
        adapter = new ConferenceAdapter(conferences);
        binding.recyclerView.setAdapter(adapter);

        binding.button.setOnClickListener(View -> {
            // Внутри Activity или Fragment
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.show(getSupportFragmentManager(), "ChatFragment");

        });

    }

    private void loadConferences() {
        // Очищаем список перед добавлением, если метод вызывается повторно
        conferences.clear();
        conferences.add(new Conference("1", "Android Dev", "Conf", 151515L, "Astana", true));
        conferences.add(new Conference("2", "Java Summit", "Workshop", 161616L, "Almaty", false));
        conferences.add(new Conference("3", "Kotlin Meetup", "Conf", 171717L, "Astana", true));
        conferences.add(new Conference("4", "Swift Conference", "Workshop", 181818L, "Almaty", false));
        conferences.add(new Conference("5", "Flutter Expo", "Conf", 191919L, "Astana", true));



    }
    }

