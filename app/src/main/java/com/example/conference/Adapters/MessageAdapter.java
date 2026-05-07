package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private final ArrayList<Message> messages;
    private final ArrayList<Participant> participants;
    private final String currentUserId;

    public MessageAdapter(ArrayList<Message> messages, ArrayList<Participant> participants, String currentUserId) {
        this.messages = messages;
        this.participants = participants;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(participants, messages.get(position), currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Добавление одного сообщения
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // Обновление всего списка
    public void updateMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged(); // обязательно!
    }
}
