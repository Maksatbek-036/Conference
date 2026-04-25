package com.example.conference.Repositories;

import com.example.conference.Models.Message;
import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class ChatHub {
    private final HubConnection hubConnection;

    public ChatHub(String baseUrl, String token) {
        hubConnection = HubConnectionBuilder.create(baseUrl + "/hubs/chat")
                .withHeader("Authorization", "Bearer " + token)
                .build();
    }

    public void start() {
        hubConnection.start().blockingAwait();
    }

    // Вход в комнату
    public void joinGroup(String confId) {
        hubConnection.send("JoinGroup", confId);
    }

    // Отправка сообщения
    public void sendMessage(Message message, String confId) {
        hubConnection.send("SendMessage", message, confId);
    }

    // Подписка на получение сообщений
    public void onMessageReceived(Action1<Message> handler) {
        hubConnection.on("ReceiveMessage", handler, Message.class);
    }
}
