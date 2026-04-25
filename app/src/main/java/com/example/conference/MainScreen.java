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
        viewModel = new ViewModelProvider(this).get(ConferenceViewModel.class);

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
    }
}
