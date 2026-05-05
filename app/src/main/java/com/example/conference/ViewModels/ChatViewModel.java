package com.example.conference.ViewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.conference.Models.Message;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ChatViewModel extends ViewModel {
    private HubConnection hubConnection;

    private final ArrayList<Message> messagesList = new ArrayList<>();

    // Subjects для подписки
    private final BehaviorSubject<ArrayList<Message>> messagesSubject =
            BehaviorSubject.createDefault(new ArrayList<>());
    private final PublishSubject<String> joinedUserSubject = PublishSubject.create();

    // Методы для подписки во Фрагменте
    public Observable<ArrayList<Message>> getMessagesObservable() {
        return messagesSubject;
    }

    public Observable<String> getJoinedUserObservable() {
        return joinedUserSubject;
    }

    public ArrayList<Message> getMessages() {
        return messagesList;
    }

    // No-arg constructor for ViewModelProvider fallback
    public ChatViewModel() {
    }

    public ChatViewModel(String baseUrl) {
        init(baseUrl);
    }

    public void init(String baseUrl) {
        if (hubConnection != null) return;

        hubConnection = HubConnectionBuilder.create(baseUrl + "/hubs/chat").build();

        // Получение одного сообщения
        hubConnection.on("ReceiveMessage", (Message msg) -> {
            this.messagesList.add(msg);
            // пушим копию ArrayList
            messagesSubject.onNext(new ArrayList<>(this.messagesList));
        }, Message.class);

        // Уведомление о новом пользователе
        hubConnection.on("UserJoined", (String userId) -> {
            joinedUserSubject.onNext(userId);
        }, String.class);
    }

    public void start() {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start().subscribe(() -> {
                Log.d("ChatViewModel", "Hub connection started");
            }, throwable -> {
                Log.e("ChatViewModel", "Error starting hub connection", throwable);
            });
        }
    }

    public void joinGroup(String confId) {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("JoinGroup", confId);
            Log.d("ChatViewModel", "Joined group: " + confId);
        }
    }

    public void sendMessage(Message message) {
        if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("SendMessage", message);
            Log.d("ChatViewModel", "Sent message: " + message.getContent());
        } else {
            Log.e("ChatViewModel", "Cannot send message: Hub is not connected");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (hubConnection != null && hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
            hubConnection.stop();
        }
    }

    // Фабрика для передачи baseUrl
    public static class ChatViewModelFactory implements ViewModelProvider.Factory {
        private final String baseUrl;

        public ChatViewModelFactory(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(baseUrl);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
