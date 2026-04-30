package com.example.conference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.MessageAdapter;
import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.ViewModels.ChatViewModel;
import com.example.conference.databinding.FragmentChatBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends BottomSheetDialogFragment {

    private FragmentChatBinding binding;
    private Cache cache;
    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private final ArrayList<Message> messageList = new ArrayList<>();
    private final String conferenceId = "154897";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(requireContext());
        
        // Используем Factory для корректного создания ViewModel с параметром
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ChatViewModel(cache.getToken());
            }
        };
        viewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        // Настройка RecyclerView
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(messageList, loadParticipants());
        binding.chatRecycler.setAdapter(adapter);

        // Наблюдаем за сообщениями из LiveData
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                messageList.clear();
                messageList.addAll(messages);
                adapter.notifyDataSetChanged();
                // Скролл к последнему сообщению
                if (!messageList.isEmpty()) {
                    binding.chatRecycler.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        // Инициализация данных и SignalR
        viewModel.loadMessages(cache.getToken(), conferenceId);
        viewModel.connectHub(conferenceId);

        // Отправка сообщения
        binding.sendButton.setOnClickListener(v -> {
            String messageText = binding.messageEdit.getText().toString().trim();
            if (!messageText.isEmpty()) {
                viewModel.sendMessage(messageText, conferenceId);
                binding.messageEdit.setText(""); // Очищаем поле после нажатия
            }
        });

        return binding.getRoot();
    }

    private ArrayList<Participant> loadParticipants() {
        ArrayList<Participant> participants = new ArrayList<>();
        // Здесь можно добавить логику загрузки реальных участников, пока используем заглушки
        participants.add(new Participant("1", "John Doe", null, false, true, 0));
        return participants;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
