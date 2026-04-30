package com.example.conference.Repositories;

import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.Conference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceRepository {
    private final ConferenceApi api;

    public interface ConferenceCallback {
        void onSuccess(List<Conference> conferences);
        void onError(String message);
    }

    public ConferenceRepository(ConferenceApi api) {
        this.api = api != null ? api : RetrofitClient.getApi(ConferenceApi.class);
    }

    public void fetchConferences(ConferenceCallback callback) {
        api.getConferences().enqueue(new Callback<List<Conference>>() {
            @Override
            public void onResponse(Call<List<Conference>> call, Response<List<Conference>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Conference>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
