package com.example.conference;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.MessageAdapter;
import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.Repositories.ChatRepository;
import com.example.conference.ViewModels.ChatViewModel;
import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.FragmentChatBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends BottomSheetDialogFragment {

    private FragmentChatBinding binding;
    private VideoCallViewModel videoCallVM;
    private ChatViewModel chatViewModel;
    private MessageAdapter messageAdapter;
    private String conferenceId;
    private ArrayList<Participant> participantList;
    private Cache cache;
    private ChatRepository chatRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(requireContext());
        participantList = new ArrayList<>();
        chatRepository = new ChatRepository();
        conferenceId = ((VideoHub) requireActivity()).getConferenceId();
        videoCallVM = new ViewModelProvider(requireActivity()).get(VideoCallViewModel.class);
        chatViewModel = ((VideoHub) requireActivity()).getChatViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        messageAdapter = new MessageAdapter(new ArrayList<>(), participantList, cache.getUserId());
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatRecycler.setAdapter(messageAdapter);

        chatViewModel.start();
        setupClickListeners();
        observeUserJoined();
        observeErrors();
        observeConnectionState();
        getMessageAtRest();



        return binding.getRoot();
    }

    private void observeRealtimeMessages() {
        chatViewModel.getMessagesLiveData().observe(getViewLifecycleOwner(), msgs -> {
            messageAdapter.updateMessages(msgs);
            if (!msgs.isEmpty()) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.chatRecycler.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastVisible >= msgs.size() - 2) {
                        binding.chatRecycler.scrollToPosition(msgs.size() - 1);
                    }
                }
            }
        });
    }

    private void observeUserJoined() {
        chatViewModel.getUserJoinedLiveData().observe(getViewLifecycleOwner(), userId -> {
            Toast.makeText(requireContext(), "User joined: " + userId, Toast.LENGTH_SHORT).show();
        });
    }

    private void observeErrors() {
        chatViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void observeConnectionState() {
        chatViewModel.getConnectionStateLiveData().observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected != null) {
                String status = isConnected ? "Соединение установлено" : "Соединение потеряно";
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        observeRealtimeMessages();
        binding.sendButton.setOnClickListener(v -> {
            String messageContent = binding.messageEdit.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                Message message = Message.create(
                        messageContent,
                        cache.getUserId(),
                        conferenceId
                );
                chatViewModel.sendMessage(message);
                binding.messageEdit.setText("");
            }
        });
    }

    private void getMessageAtRest() {
        chatRepository.getMessages(conferenceId, new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.updateMessages(response.body());
                } else {
                    Log.d("ChatFragment", "Нет данных");
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка загрузки истории", Toast.LENGTH_SHORT).show();
                Log.e("ChatFragment", "Ошибка загрузки истории", t);
            }
        });
    }

    private void updateParticipants(Map<String, Object> tracks) {
        participantList.removeIf(p -> !tracks.containsKey(p.getId()));

        for (String userId : tracks.keySet()) {
            boolean alreadyExists = participantList.stream()
                    .anyMatch(p -> p.getId().equals(userId));
            if (!alreadyExists) {
                participantList.add(new Participant(
                        userId,
                        "Участник " + userId.substring(0, Math.min(userId.length(), 4)),
                        null,
                        false,
                        true,
                        System.currentTimeMillis()
                ));
            }
        }
        messageAdapter.updateParticipants(participantList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatViewModel.stop();
        binding = null;
        messageAdapter = null;
        participantList.clear();
    }
}
