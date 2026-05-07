package com.example.conference.Api;

import com.example.conference.Contracts.ConferenceResponce;
import com.example.conference.Contracts.CreateConferenceRequest;
import com.example.conference.Models.Conference;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConferenceApi {

        @GET("api/conference")
        Call<List<ConferenceResponce>> getConferences(@Query("userId") String userId);

        @POST("api/conference")
        Call<Conference> createConference(@Body CreateConferenceRequest request);

}
