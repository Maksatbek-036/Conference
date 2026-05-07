package com.example.conference.Repositories;

import android.util.Log;

import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Contracts.ConferenceResponce;
import com.example.conference.Contracts.CreateConferenceRequest;
import com.example.conference.Models.Conference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceRepository {
    private final ConferenceApi api;

    public interface ConferenceCallback {
        void onSuccess(List<ConferenceResponce> conferences);
        void onError(String message);
    }

    public interface CreateCallback {
        void onSuccess(Conference conference);
        void onError(String message);
    }

    public ConferenceRepository(ConferenceApi api) {
        this.api = api != null ? api : RetrofitClient.getApi(ConferenceApi.class);
    }

    public void fetchConferences(ConferenceCallback callback, String userId) {
        api.getConferences(userId).enqueue(new Callback<List<ConferenceResponce>>() {
            @Override
            public void onResponse(Call<List<ConferenceResponce>> call, Response<List<ConferenceResponce>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ConferenceResponce>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createConference(String userId,String title, String description, String location, String date, String startTime, String endTime, boolean isOnline, CreateCallback callback) {
        CreateConferenceRequest request = new CreateConferenceRequest(userId,title, description, date, startTime, endTime, location, isOnline);
        api.createConference(request).enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e("ConferenceRepository", "Ошибка сервера: " + response.code());
                    callback.onError("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Log.e("ConferenceRepository", "Ошибка при создании: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }
}
