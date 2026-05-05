package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private ArrayList<Message> messages;
    private ArrayList<Participant> participantMap; // Было ArrayList
    private String currentUserId;

    public MessageAdapter(ArrayList<Message> messages, ArrayList<Participant> participantMap, String currentUserId) {
        this.messages = messages;
        this.participantMap = participantMap;
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
        holder.bind(participantMap, messages.get(position),currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }


}
