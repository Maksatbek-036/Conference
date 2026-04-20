package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private ArrayList<Message> messages;
    private ArrayList<Participant> participants;

    public MessageAdapter(ArrayList<Message> messages, ArrayList<Participant> participants) {
        this.messages = messages;
        this.participants = participants;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем View для одной строки чата
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // ОШИБКА ИСПРАВЛЕНА: Передаем список участников И конкретное сообщение для этой позиции
        Message currentMessage = messages.get(position);
        holder.bind(participants, currentMessage);
    }

    @Override
    public int getItemCount() {
        // ОШИБКА ИСПРАВЛЕНА: Если вернуть 0, список всегда будет пустым
        return messages != null ? messages.size() : 0;
    }
}
