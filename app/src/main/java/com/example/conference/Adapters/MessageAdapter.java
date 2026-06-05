package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private List<Message> messages;
    private List<Participant> participants;
    private final String currentUserId;

    public MessageAdapter(List<Message> messages, List<Participant> participants, String currentUserId) {
        this.messages = new ArrayList<>(messages);
        this.participants = new ArrayList<>(participants);
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
        holder.bind(new ArrayList<>(participants), messages.get(position), currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Добавление одного сообщения
    public void addMessage(Message message) {
        List<Message> newList = new ArrayList<>(messages);
        newList.add(message);
        updateMessages(newList);
    }

    // Обновление участников
    public void updateParticipants(List<Participant> newParticipants) {
        this.participants = new ArrayList<>(newParticipants);
        notifyDataSetChanged();
    }

    // Обновление списка сообщений через DiffUtil
    public void updateMessages(List<Message> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return messages.size();
            }

            @Override
            public int getNewListSize() {
                return newMessages.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // Сравниваем по уникальному ID сообщения
                String oldId = messages.get(oldItemPosition).getId();
                String newId = newMessages.get(newItemPosition).getId();
                return oldId != null && oldId.equals(newId);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // Сравниваем содержимое
                return messages.get(oldItemPosition).equals(newMessages.get(newItemPosition));
            }
        });

        messages = new ArrayList<>(newMessages);
        diffResult.dispatchUpdatesTo(this);
    }
}
