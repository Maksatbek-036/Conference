package com.example.conference;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.conference.Adapters.MessageAdapter;
import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.ViewModels.ChatViewModel;
import com.example.conference.databinding.FragmentChatBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;


public class ChatFragment extends BottomSheetDialogFragment {

FragmentChatBinding binding;
Cache cache;
ChatViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
cache = new Cache(getContext());
viewModel= new ViewModelProvider(this).get(ChatViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        // Добавляем LayoutManager (вертикальный список)
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Установка адаптера
        binding.chatRecycler.setAdapter(new MessageAdapter(loadMessages(), loadParticipants()));

        return binding.getRoot();
    }
    private ArrayList<Message> loadMessages() {
        var messages = new ArrayList<Message>();
       viewModel.loadMessages(cache.getToken(),"154897");
          return messages;
    }
    private ArrayList<Participant> loadParticipants() {
        var participants = new ArrayList<Participant>();
        participants.add(new Participant("1", "John Doe", "https://example.com/avatar1.jpg", false, true, 1234567890));
        participants.add(new Participant("2", "Jane Smith", "https://example.com/avatar2.jpg", true, false, 976543210));
        return participants;
    }

}