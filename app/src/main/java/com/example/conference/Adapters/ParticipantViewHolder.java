package com.example.conference.Adapters;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.databinding.ParticipantItemBinding;

import org.webrtc.SurfaceViewRenderer; // Импортируем WebRTC рендерер

public class ParticipantViewHolder extends RecyclerView.ViewHolder {
    public final ParticipantItemBinding binding;
    public final SurfaceViewRenderer videoView; // Ссылка для адаптера

    public ParticipantViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ParticipantItemBinding.bind(itemView);
        // Убедитесь, что в XML тип этого элемента org.webrtc.SurfaceViewRenderer
        videoView = binding.videoSurface;
    }

    public void bind(Participant participant) {
        binding.participantName.setText(participant.getName());

        // Заглушка для аватара
        if (participant.getAvatarUrl() == null) {
            binding.avatarImage.setImageResource(R.drawable.iconstack_io____user_);
        } else {
            binding.avatarImage.setImageResource(R.drawable.iconstack_io____user_);
        }

        // Логика видимости видео
        if (participant.isVideoEnabled()) {
            binding.videoSurface.setVisibility(View.VISIBLE);
            binding.avatarImage.setVisibility(View.GONE); // Скрываем аватар, если есть видео
        } else {
            binding.videoSurface.setVisibility(View.GONE);
            binding.avatarImage.setVisibility(View.VISIBLE); // Показываем аватар
        }
    }
}