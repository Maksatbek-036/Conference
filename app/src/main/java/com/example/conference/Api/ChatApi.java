package com.example.conference.Api;

import com.example.conference.Models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ChatApi {
    @GET("/api/chat/{confId}/messages")
    Call<List<Message>> getMessages(
            @Header("Authorization") String token,
            @Path("confId") String confId
    );
    @DELETE("/api/chat/message/{confId}")
    Call<Void> deleteMessage(@Header("Authorization")String token, @Path("confId") String confId);


}
