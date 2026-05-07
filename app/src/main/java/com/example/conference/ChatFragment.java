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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends BottomSheetDialogFragment {

    private FragmentChatBinding binding;
    private VideoCallViewModel videoCallVM;
    private ChatViewModel chatViewModel;
    private MessageAdapter messageAdapter;

    private ArrayList<Participant> participantList;
    private Cache cache;
    private ChatRepository chatRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(requireContext());
        participantList = new ArrayList<>();
        chatRepository = new ChatRepository(cache.getToken());

        videoCallVM = new ViewModelProvider(requireActivity()).get(VideoCallViewModel.class);
        chatViewModel = ((VideoHub) requireActivity()).getChatViewModel();

        chatViewModel.setOnUserJoined(userId ->
                Toast.makeText(requireContext(), "User joined: " + userId, Toast.LENGTH_SHORT).show()
        );

        // Добавляем текущего пользователя в список участников
        participantList.add(new Participant(
                cache.getUserId(),
                cache.getUserName(),
                cache.getAvatarUrl(),
                false,
                true,
                System.currentTimeMillis()
        ));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        // создаём адаптер с пустым списком
        messageAdapter = new MessageAdapter(new ArrayList<>(), participantList, cache.getUserId());
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatRecycler.setAdapter(messageAdapter);

        setupClickListeners();
        observeRemoteParticipants();
        observeRealtimeMessages();
        getMessageAtRest(); // загрузка истории

        return binding.getRoot();
    }

    private void observeRealtimeMessages() {
        chatViewModel.setOnMessagesUpdated(msgs -> {
            messageAdapter.updateMessages(msgs);
            binding.chatRecycler.scrollToPosition(msgs.size() - 1);
        });
    }

    private void setupClickListeners() {
        binding.sendButton.setOnClickListener(v -> {
            String messageContent = binding.messageEdit.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                Message message = Message.create(
                        messageContent,
                        cache.getUserId(),
                        videoCallVM.getCurrentRoomId()
                );
                chatViewModel.sendMessage(message);
                binding.messageEdit.setText("");
            }
        });
    }

    private void getMessageAtRest() {
        chatRepository.getMessages(videoCallVM.getCurrentRoomId(), new Callback<List<Message>>() {
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
                Log.e("ChatFragment", "Ошибка загрузки истории", t);
            }
        });
    }

    private void observeRemoteParticipants() {
        videoCallVM.remoteTracks.observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null) {
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
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        messageAdapter = null;
    }
}
