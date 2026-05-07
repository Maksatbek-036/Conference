package com.example.conference.ViewModels;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.conference.Models.Message;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatViewModel {
    private HubConnection hubConnection;
    private final List<Message> messagesList = new ArrayList<>();

    // Колбэки для UI
    private Consumer<List<Message>> onMessagesUpdated;
    private Consumer<String> onUserJoined;

    public ChatViewModel(String baseUrl) {
        hubConnection = HubConnectionBuilder.create(baseUrl + "/hubs/chat").build();

        // Подписка на получение сообщения
        hubConnection.on("ReceiveMessage", (Message msg) -> {
            messagesList.add(msg);
            if (onMessagesUpdated != null) {
                onMessagesUpdated.accept(new ArrayList<>(messagesList));
            }
            Log.d("ChatViewModel", "Получено сообщение: " + msg.getContent());
        }, Message.class);

        // Подписка на вход нового пользователя
        hubConnection.on("UserJoined", (String userId) -> {
            if (onUserJoined != null) {
                onUserJoined.accept(userId);
            }
            Log.d("ChatViewModel", "Пользователь присоединился: " + userId);
        }, String.class);
    }


    public void start() {
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start().subscribe(() -> {
                Log.d("ChatViewModel", "Hub connection started");
            }, throwable -> {
                Log.e("ChatViewModel", "Ошибка запуска соединения", throwable);
            });
        }
    }

    public void joinGroup(String confId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("JoinGroup", confId);
            Log.d("ChatViewModel", "Присоединился к группе: " + confId);
        }
    }

    public void sendMessage(Message message) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("SendMessage", message);
            Log.d("ChatViewModel", "Отправлено сообщение: " + message.getContent());
        }
    }

    // Установка колбэков
    public void setOnMessagesUpdated(Consumer<List<Message>> callback) {
        this.onMessagesUpdated = callback;
    }

    public void setOnUserJoined(Consumer<String> callback) {
        this.onUserJoined = callback;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messagesList);
    }

    public void stop() {
        if (hubConnection != null && hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
            hubConnection.stop();
        }
    }


}
