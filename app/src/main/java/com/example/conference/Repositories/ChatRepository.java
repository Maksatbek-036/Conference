package com.example.conference.Repositories;

import com.example.conference.Api.ChatApi;
import com.example.conference.Hubs.ChatHub;
import com.example.conference.Models.Message;
import com.example.conference.Api.RetrofitClient;
import com.microsoft.signalr.Action1;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatRepository {
    private final ChatApi chatApi;
    private final ChatHub chatHub;

    public ChatRepository(String token) {
        this.chatApi = RetrofitClient.getApi(ChatApi.class);
        this.chatHub = new ChatHub("http://192.168.0.106:5000", token);
    }

    // REST: загрузка истории
    public void getMessages(String token, String confId, Callback<List<Message>> callback) {
        chatApi.getMessages("Bearer " + token, confId).enqueue(callback);
    }

    // REST: удаление сообщения
    public void deleteMessage(String token, String messageId, Callback<Void> callback) {
        chatApi.deleteMessage("Bearer " + token, messageId).enqueue(callback);
    }

    // SignalR: старт хаба
    public void connectHub() {
        chatHub.start();
    }

    public void joinGroup(String confId) {
        chatHub.joinGroup(confId);
    }

    public void sendMessage(Message message, String confId) {
        chatHub.sendMessage(message, confId);
    }

    public void subscribeMessages(Action1<Message> handler) {
        chatHub.onMessageReceived(handler);
    }
}
