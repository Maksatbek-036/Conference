package com.example.conference.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.Conference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceRepository {
    private final ConferenceApi api;

    public ConferenceRepository() {
        this.api = RetrofitClient.getApi(ConferenceApi.class);
    }

    public LiveData<List<Conference>> fetchConferences(String token) {
        MutableLiveData<List<Conference>> conferencesLiveData = new MutableLiveData<>();

        api.getConferences("Bearer " + token).enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    conferencesLiveData.setValue(response.body());
                } else {
                    conferencesLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                conferencesLiveData.setValue(null);
            }
        });

        return conferencesLiveData;
    }
}
