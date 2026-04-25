package com.example.conference.Api;

import com.example.conference.Contracts.CreateConferenceRequest;
import com.example.conference.Models.Conference;
import com.example.conference.Models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConferenceApi {
    @GET("/api/conferences")
    Call<List<Conference>> getConferences(@Header("Authorization")String token);
    @POST("/api/conferences")
   Call<Conference>  createConference(@Header("Authorization")String token, @Body CreateConferenceRequest conference);
    @GET("/api/conferences/{id}")
    Call<Conference> getConference(@Header("Authorization")String token, @Path("id") String id);

    @DELETE("/api/conferences/{id}")
    Call<Void> deleteConference(@Header("Authorization")String token, @Path("id") String id);

}
