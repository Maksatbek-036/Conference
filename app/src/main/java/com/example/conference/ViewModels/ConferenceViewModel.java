package com.example.conference.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Cache;
import com.example.conference.Models.Conference;
import com.example.conference.Repositories.ConferenceRepository;

import java.util.List;

public class ConferenceViewModel extends ViewModel {
    private  ConferenceRepository repository;
    private LiveData<List<Conference>> conferences;
Cache cache;
public ConferenceViewModel() {

}
    public ConferenceViewModel(Context context) {
        cache=new Cache(context);
        repository=new ConferenceRepository(context,cache);
    }

public void createConference(String title, String description, long date, String startTime,
                                    String endTime, String location, boolean isOnline){
       repository.saveConference(title,description,date,startTime,endTime,location,isOnline);
}
    public void loadConferences(String token) {
        conferences = repository.fetchConferences(token);
    }

    public LiveData<List<Conference>> getConferences() {
        return conferences;
    }
}
