package com.example.conference.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Models.Message;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private HubConnection hubConnection;
    private final List<Message> messagesList = new ArrayList<>();

    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> userJoinedLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> connectionStateLiveData = new MutableLiveData<>(false);

    public ChatViewModel(String baseUrl) {
        hubConnection = HubConnectionBuilder.create(baseUrl + "/hubs/chat").build();

        hubConnection.on("ReceiveMessage", (Message msg) -> {
            messagesList.add(msg);
            messagesLiveData.postValue(new ArrayList<>(messagesList));
            Log.d("ChatViewModel", "Получено сообщение: " + msg.getContent());
        }, Message.class);

        hubConnection.on("UserJoined", (String userId) -> {
            userJoinedLiveData.postValue(userId);
            Log.d("ChatViewModel", "Пользователь присоединился: " + userId);
        }, String.class);
    }

    public void start() {
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start().subscribe(() -> {
                connectionStateLiveData.postValue(true);
                Log.d("ChatViewModel", "Hub connection started");
            }, throwable -> {
                errorLiveData.postValue("Ошибка запуска соединения: " + throwable.getMessage());
                Log.e("ChatViewModel", "Ошибка запуска соединения", throwable);
            });
        }
    }

    public void joinGroup(String confId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("JoinGroup", confId);
            Log.d("ChatViewModel", "Присоединился к группе: " + confId);
        } else {
            errorLiveData.postValue("Не удалось присоединиться: соединение не установлено");
        }
    }

    public void sendMessage(Message message) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.send("SendMessage", message);
            Log.d("ChatViewModel", "Отправлено сообщение: " + message.getContent());
        } else {
            errorLiveData.postValue("Сообщение не отправлено: соединение отсутствует");
        }
    }

    public LiveData<List<Message>> getMessagesLiveData() {
        return messagesLiveData;
    }

    public LiveData<String> getUserJoinedLiveData() {
        return userJoinedLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getConnectionStateLiveData() {
        return connectionStateLiveData;
    }

    public void stop() {
        if (hubConnection != null && hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
            hubConnection.stop().subscribe(() -> {
                connectionStateLiveData.postValue(false);
                Log.d("ChatViewModel", "Hub connection stopped");
            }, throwable -> {
                errorLiveData.postValue("Ошибка остановки соединения: " + throwable.getMessage());
                Log.e("ChatViewModel", "Ошибка остановки соединения", throwable);
            });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stop();
    }
}
