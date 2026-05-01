package com.example.conference.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Models.Message;
import com.example.conference.Repositories.ChatRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {
    private final ChatRepository repository;
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();

    public ChatViewModel(String token) {
        repository = new ChatRepository(token);
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void loadMessages(String token, String confId) {
        repository.getMessages(token, confId, new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages.postValue(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
               Log.e("ChatViewModel", "Ошибка при загрузке сообщений", t);
            }
        });
    }
public void sendMessage(String messageText, String confId) {
        var time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Message message = new Message(null, messageText, time, null,confId);
        repository.sendMessage(message);
}
    public void connectHub(String confId) {
        repository.connectHub();
        repository.joinGroup(confId);
        repository.subscribeMessages(message -> {
            List<Message> current = messages.getValue();
            if (current == null) current = new ArrayList<>();
            current.add(message);
            messages.postValue(current);
        });
    }
}
