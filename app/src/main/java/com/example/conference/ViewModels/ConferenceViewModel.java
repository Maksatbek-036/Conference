package com.example.conference.ViewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.conference.Cache;
import com.example.conference.Models.Conference;
import com.example.conference.Repositories.ConferenceRepository;

import java.util.List;

public class ConferenceViewModel extends AndroidViewModel {
    private final ConferenceRepository repository;
    private final Cache cache;
    private LiveData<List<Conference>> conferences;

    public ConferenceViewModel(@NonNull Application application) {
        super(application);
        this.cache = new Cache(application);
        this.repository = new ConferenceRepository(application, cache);
        // Инициализируем LiveData пустой ссылкой или загружаем сразу
        this.conferences = new MutableLiveData<>();
    }

    public void loadConferences() {
        String token = cache.getToken();
        if (token != null && !token.isEmpty()) {
            this.conferences = repository.fetchConferences(token);
        }
    }

    public LiveData<List<Conference>> getConferences() {
        return conferences;
    }

    public void createConference(String title, String description, long date, String startTime,
                                 String endTime, String location, boolean isOnline) {
        repository.saveConference(title, description, date, startTime, endTime, location, isOnline);
        // После сохранения можно перезагрузить список
        loadConferences();
    }
}
