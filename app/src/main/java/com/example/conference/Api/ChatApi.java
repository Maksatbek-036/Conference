package com.example.conference.Api;

import com.example.conference.Contracts.SendMessageRequest;
import com.example.conference.Models.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApi {

    // Получить все сообщения конференции
    @GET("api/chat/{confId}/messages")
    Call<List<Message>> getMessages(@Path("confId") String confId);

    // Отправить новое сообщение
    @POST("api/chat/send")
    Call<Message> sendMessage(@Body SendMessageRequest request);

    // Удалить сообщение
    @DELETE("api/chat/message/{id}")
    Call<Void> deleteMessage(@Path("id") String messageId);
}