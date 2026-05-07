package com.example.conference.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Contracts.ConferenceResponce;
import com.example.conference.Repositories.ConferenceRepository;
import com.example.conference.VideoHub;
import com.example.conference.databinding.PlanItemBinding;

import org.jetbrains.annotations.UnknownNullability;

public class ConferenceViewHolder extends RecyclerView.ViewHolder {

    PlanItemBinding binding;

    public ConferenceViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = PlanItemBinding.bind(itemView);
    }

    public void bind(@UnknownNullability ConferenceResponce conference, ConferenceRepository repository,String userId) {
        binding.startButton.setOnClickListener(v -> {
            // Используем ID конференции как ID комнаты, если он есть
            String roomId = conference.getCode();
            String conferenceid=conference.getId();

            repository.joinConferenceByCode(roomId,userId );
            Log.d("RoomID", "Room ID: " + roomId);
            Log.d("ConferenceID", "Conference ID: " + conferenceid);

            Intent intent = new Intent(itemView.getContext(), VideoHub.class);
            intent.putExtra("ROOM_ID", roomId);
            intent.putExtra("CONFERENCE_ID", conferenceid);
            itemView.getContext().startActivity(intent);
        });
        
        binding.listPlansName.setText(conference.getTitles());
        binding.listPlansTime.setText(conference.getStartTime());
    }
}
