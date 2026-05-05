package com.example.conference.Adapters;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.databinding.MessageItemBinding;

import java.util.ArrayList;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    private final MessageItemBinding binding;

    public MessageViewHolder(@NonNull android.view.View itemView) {
        super(itemView);
        binding = MessageItemBinding.bind(itemView);
    }

    public void bind(ArrayList<Participant> participants, Message message, String currentUserId) {
        if (message == null) return;

        // Определяем, моё ли сообщение
        boolean isMine = message.getUserId() != null && message.getUserId().equals(currentUserId);

        // Поиск отправителя по userId
        Participant sender = null;
        if (participants != null && message.getUserId() != null) {
            for (Participant p : participants) {
                if (p != null && message.getUserId().equals(p.getId())) {
                    sender = p;
                    break;
                }
            }
        }

        // Устанавливаем имя отправителя
        binding.senderName.setText(isMine ? "Вы" : (sender != null ? sender.getName() : "Гость"));
        // Устанавливаем текст сообщения
        binding.messageText.setText(message.getContent());
        // Устанавливаем время
        binding.timeStamp.setText(message.getTimestamp());

        // Настройка выравнивания и фона
        LinearLayout root = (LinearLayout) binding.getRoot();
        if (isMine) {
            root.setGravity(Gravity.END);
            binding.messageText.setBackgroundResource(R.drawable.bg_outgoing);
        } else {
            root.setGravity(Gravity.START);
            binding.messageText.setBackgroundResource(R.drawable.bg_incoming);
        }
    }
}
