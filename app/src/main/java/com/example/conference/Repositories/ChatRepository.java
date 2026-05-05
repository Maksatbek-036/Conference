package com.example.conference.Repositories;

import com.example.conference.Api.ChatApi;
import com.example.conference.Models.Message;
import com.example.conference.Api.RetrofitClient;
import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnectionState;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import retrofit2.Callback;

public class ChatRepository {
    private final ChatApi chatApi;


    public ChatRepository(String token) {

        this.chatApi = RetrofitClient.getApi(ChatApi.class);
    }

    public void getMessages(String confId, Callback<List<Message>> callback) {
        chatApi.getMessages(confId).enqueue(callback);
    }


}