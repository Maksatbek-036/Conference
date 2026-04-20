package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.Models.Participant;
import com.example.conference.R;

import java.util.ArrayList;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantViewHolder>{
private ArrayList<Participant> participants;

    public ParticipantAdapter(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParticipantViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        holder.bind(participants.get(position));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }
}
