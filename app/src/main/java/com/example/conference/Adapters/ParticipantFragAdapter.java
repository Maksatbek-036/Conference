package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Participant;
import com.example.conference.R;

import java.util.ArrayList;

public class ParticipantFragAdapter extends RecyclerView.Adapter<ParticipantFragViewHolder>{
private ArrayList<Participant> participants;

    public ParticipantFragAdapter(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    @NonNull
    @Override
    public ParticipantFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParticipantFragViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_item_frag,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantFragViewHolder holder, int position) {
holder.bind(participants.get(position));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }
}
