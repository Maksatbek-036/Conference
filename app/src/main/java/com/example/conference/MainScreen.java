package com.example.conference;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.ConferenceAdapter;
import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Contracts.ConferenceResponce;
import com.example.conference.Models.Conference;
import com.example.conference.Repositories.ConferenceRepository;
import com.example.conference.databinding.ActivityMainScreenBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainScreen extends AppCompatActivity {
    private ActivityMainScreenBinding binding;
    private ConferenceRepository repository;
    private ConferenceApi api ;
    private List<ConferenceResponce> conferences;
    private ConferenceAdapter adapter;
    private Cache cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        api=RetrofitClient.getApi(ConferenceApi.class);
        repository =  new ConferenceRepository(api);
        cache = new Cache(getApplicationContext());

        String token = cache.getToken();
        if (token != null) {
            Log.d("MainScreen", "Token payload: " + JWTDecoder.decodedPayload(token));
        } else {
            Log.e("MainScreen", "Token отсутствует в кэше");
        }



        // Настройка RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConferenceAdapter(new ArrayList<>(),repository,cache.getUserId());
        binding.recyclerView.setAdapter(adapter);

        repository = new ConferenceRepository(api);
conferences=new ArrayList();
        loadConferences();

        // Кнопка "Новая встреча"


        binding.button.setOnClickListener(v -> {
            // Получаем текущую дату и время
            Date now = new Date();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);

            // Вычисляем время окончания (+1 час)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            String endTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

            repository.createConference(
                    cache.getUserId(),
                    "Новая встреча от " + cache.getUserName(),
                    "нет описания",
                    " ",
                    currentDate,
                    currentTime,
                    endTime,
                    true,
                    new ConferenceRepository.CreateCallback() {
                        @Override
                        public void onSuccess(Conference conference) {
                           conferences.add(new ConferenceResponce(
                                   conference.getId(),
                                   conference.getTitles(),
                                   conference.getDescription(),
                                   conference.getDate(),
                                   conference.getStartTime(),
                                   conference.getEndTime(),
                                   conference.getLocation()
                           ));
                            // например, вызвать метод в ViewModel:
                            // chatViewModel.addSystemMessage("Создана новая конференция: " + conference.getTitle());
                        }

                        @Override
                        public void onError(String message) {

                        }
                    }
            );

    });
        binding.outButton.setOnClickListener(v->{
            cache.clear();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
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
                repository.joinConferenceByCode(roomCode, cache.getUserId(), new ConferenceRepository.JoinCallback() {
                    @Override
                    public void onSuccess(ConferenceResponce conference) {
                        Intent intent = new Intent(MainScreen.this, VideoHub.class);
                        intent.putExtra("ROOM_ID", roomCode);
                        intent.putExtra("CONFERENCE_ID", conference.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainScreen.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Введите код встречи", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка "Запланировать"
        binding.sheduleButton.setOnClickListener(v -> {
            ScheduleConference scheduleFragment = new ScheduleConference();
            scheduleFragment.show(getSupportFragmentManager(), scheduleFragment.getTag());
        });
        binding.recyclerView.setOnClickListener(v->{
            loadConferences();
        });
    }


    private void loadConferences() {
        String userId = cache.getUserId();

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Ошибка: UserId отсутствует", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.fetchConferences(new ConferenceRepository.ConferenceCallback() {
            @Override
            public void onSuccess(List<ConferenceResponce> conferencesres) {
            conferences.addAll(conferencesres);
            adapter.setConferences(conferences);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainScreen.this, "Ошибка загрузки: " + message, Toast.LENGTH_SHORT).show();
            }
        }, userId);
    }
}
