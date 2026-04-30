package com.example.conference.Adapters;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.VideoHub;
import com.example.conference.databinding.PlanItemBinding;

public class ConferenceViewHolder extends RecyclerView.ViewHolder {

    PlanItemBinding binding;

    public ConferenceViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = PlanItemBinding.bind(itemView);
    }

    public void bind(Conference conference) {
        binding.startButton.setOnClickListener(v -> {
            // Используем ID конференции как ID комнаты, если он есть
            String roomId = conference.getId();
            if (roomId == null || roomId.isEmpty()) {
                roomId = "ROOM_" + (int)(Math.random() * 9999);
            }
            
            Intent intent = new Intent(itemView.getContext(), VideoHub.class);
            intent.putExtra("ROOM_ID", roomId);
            itemView.getContext().startActivity(intent);
        });
        
        binding.listPlansName.setText(conference.getTitles());
        binding.listPlansTime.setText(conference.getStartTime());
    }
}
