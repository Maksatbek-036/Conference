package com.example.conference.Repositories;

import com.example.conference.Api.ChatApi;
import com.example.conference.Models.Message;
import com.example.conference.Api.RetrofitClient;

import java.util.List;

import retrofit2.Callback;

public class ChatRepository {
    private final ChatApi chatApi;


    public ChatRepository() {

        this.chatApi = RetrofitClient.getApi(ChatApi.class);
    }

    public void getMessages(String confId, Callback<List<Message>> callback) {
        chatApi.getMessages(confId).enqueue(callback);
    }


}