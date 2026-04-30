package com.example.conference.Repositories;

import com.example.conference.Models.Message;
import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class ChatHub {
    private final HubConnection hubConnection;

    public ChatHub(String baseUrl, String token) {
        // Убедитесь, что эндпоинт совпадает с MapHub<ChatHub>("/chatHub") на бэкенде
        hubConnection = HubConnectionBuilder.create(baseUrl + "/chatHub")
                .withHeader("Authorization", "Bearer " + token)
                .build();
    }

    public void start() {
        // blockingAwait() может вызвать NetworkOnMainThreadException,
        // лучше вызывать start() в фоновом потоке.
        hubConnection.start();
    }

    public void stop() {
        hubConnection.stop();
    }

    // Вход в комнату (вызываем сразу после start)
    public void joinGroup(String confId) {
        // На бэкенде: public async Task JoinGroup(string confId)
        hubConnection.send("JoinGroup", confId);
    }

    // Отправка сообщения
    public void sendMessage(Message message) {
        // ВАЖНО: В вашем C# хабе метод SendMessage принимает только ОДИН аргумент:
        // public async Task SendMessage(Message message)
        // Поэтому в Java передаем только объект.
        // Убедитесь, что внутри message уже установлен conferenceId.
        hubConnection.send("SendMessage", message);
    }

    // Подписка на получение сообщений
    public void onMessageReceived(Action1<Message> handler) {
        // На бэкенде: SendAsync("ReceiveMessage", saved)
        hubConnection.on("ReceiveMessage", handler, Message.class);
    }

    // Подписка на уведомление о входе нового пользователя
    public void onUserJoined(Action1<String> handler) {
        // На бэкенде: SendAsync("UserJoined", Context.ConnectionId)
        hubConnection.on("UserJoined", handler, String.class);
    }
}