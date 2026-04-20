package com.example.conference.Adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.databinding.MessageItemBinding;
import com.example.conference.databinding.ParticipantItemBinding;

import java.util.ArrayList;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    private final MessageItemBinding binding;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = MessageItemBinding.bind(itemView);
    }

    public void bind(ArrayList<Participant> participants, Message message) {
        // 1. Проверка на null обязательна
        if (message == null) return;

        Participant sender = null;

        // 2. Ищем автора КОНКРЕТНОГО сообщения среди участников
        if (participants != null) {
            for (Participant p : participants) {
                // Сравниваем ID участника с userId из текущего сообщения
                if (p.getId() != null && p.getId().equals(message.getUserId())) {
                    sender = p;
                    break;
                }
            }
        }

        // 3. Логика отображения имени
        if (sender != null) {
            binding.senderName.setText(sender.getName());
        } else {
            // Если ID не найден в списке (например, участник вышел)
            binding.senderName.setText("Ушедший участник");
        }

        // 4. Установка текста и времени именно для этого сообщения
        binding.messageText.setText(message.getContent());
        binding.timeStamp.setText(message.getTimestamp());
    }
}