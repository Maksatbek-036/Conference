package com.example.conference.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Models.Conference;
import com.example.conference.Repositories.ConferenceRepository;

import java.util.List;

public class ConferenceViewModel extends ViewModel {
    private final ConferenceRepository repository;
    private LiveData<List<Conference>> conferences;

    public ConferenceViewModel() {
        repository = new ConferenceRepository();
    }

    public void loadConferences(String token) {
        conferences = repository.fetchConferences(token);
    }

    public LiveData<List<Conference>> getConferences() {
        return conferences;
    }
}
