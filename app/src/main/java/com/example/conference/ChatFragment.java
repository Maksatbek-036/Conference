package com.example.conference;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends BottomSheetDialogFragment {

    private FragmentChatBinding binding;
    private VideoCallViewModel videoCallVM;
    private ChatViewModel chatViewModel;
    private MessageAdapter messageAdapter;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final ArrayList<Message> messageList = new ArrayList<>();
    private final ArrayList<Participant> participantList = new ArrayList<>();
    private ChatRepository  chatRepository;

    private Cache cache;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(requireContext());

        videoCallVM = new ViewModelProvider(requireActivity()).get(VideoCallViewModel.class);

        // Фабрика для ChatViewModel
        ChatViewModel.ChatViewModelFactory factory =
                new ChatViewModel.ChatViewModelFactory(VideoHub.CHAT_HUB_URL);
        chatViewModel = new ViewModelProvider(requireActivity(), factory).get(ChatViewModel.class);
    chatRepository=new ChatRepository(cache.getToken());
        // Добавляем текущего пользователя в список участников
        if (participantList.isEmpty()) {
            participantList.add(new Participant(
                    cache.getUserId(),
                    cache.getUserName(),
                    cache.getAvatarUrl(),
                    false,
                    true,
                    System.currentTimeMillis()
            ));
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        observeMessages();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageAdapter = new MessageAdapter(messageList, participantList, cache.getUserId());
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatRecycler.setAdapter(messageAdapter);

        // Отправка сообщения
        binding.sendButton.setOnClickListener(v -> {
            String messageContent = binding.messageEdit.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                Message message = Message.create(
                        messageContent,
                        cache.getUserId(),
                        videoCallVM.getCurrentRoomId()
                );
                chatViewModel.sendMessage(message); // исправлено: передаём объект
                messageAdapter.notifyDataSetChanged();

                binding.messageEdit.setText("");
            }
        });


        // Наблюдение за участниками
        videoCallVM.remoteTracks.observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null) {
                boolean changed = false;
                for (String userId : tracks.keySet()) {
                    boolean exists = participantList.stream()
                            .anyMatch(p -> p.getId().equals(userId));
                    if (!exists) {
                        participantList.add(new Participant(
                                userId,
                                "Участник " + userId.substring(0, Math.min(userId.length(), 4)),
                                null,
                                false,
                                true,
                                System.currentTimeMillis()
                        ));
                        changed = true;
                    }
                }
                if (changed) {
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });

        loadMessages();
    }

    private void loadMessages() {
       chatRepository.getMessages(videoCallVM.getCurrentRoomId(), new Callback<List<Message>>() {
           @Override
           public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
               messageList.addAll(response.body());
               messageAdapter.notifyDataSetChanged();
               if(response.isSuccessful()){
                   Log.d("dddd","all good");
               }
           }

           @Override
           public void onFailure(Call<List<Message>> call, Throwable t) {

           }
       });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        binding = null;
        messageAdapter = null;
    }
    private void observeMessages() {
        disposable.add(chatViewModel.getMessagesObservable()
                .observeOn(AndroidSchedulers.mainThread()) // Переходим в UI поток
                .subscribe(newMessages -> {
                    // Очищаем старый список и добавляем все актуальные сообщения
                    messageList.clear();
                    messageList.addAll(newMessages);

                    // Уведомляем адаптер
                    messageAdapter.notifyDataSetChanged();

                    // Авто-скролл вниз при получении нового сообщения
                    if (messageList.size() > 0) {
                        binding.chatRecycler.scrollToPosition(messageList.size() - 1);
                    }
                }, throwable -> {
                    Log.e("ChatFragment", "Ошибка при получении списка сообщений", throwable);
                }));
    }
}
