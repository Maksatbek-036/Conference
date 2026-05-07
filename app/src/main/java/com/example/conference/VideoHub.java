package com.example.conference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.conference.Adapters.ParticipantAdapter;
import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.Participant;
import com.example.conference.Repositories.ConferenceRepository;
import com.example.conference.ViewModels.ChatViewModel;
import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.ActivityVideoHubBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class VideoHub extends AppCompatActivity {
    private ActivityVideoHubBinding binding;
    private VideoCallViewModel viewModel;
    private ChatViewModel chatViewModel;
    private ConferenceRepository repository;
    ConferenceApi api;
    private ArrayList<Participant> participants = new ArrayList<>();
    private ParticipantAdapter adapter;
    private String roomId;
    private String conferenceId;
    private Cache cache;

    public static final String CHAT_HUB_URL = "http://192.168.0.106:5000";

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        api = RetrofitClient.getApi(ConferenceApi.class);
        cache = new Cache(this);
        repository = new ConferenceRepository(api);

        roomId = getIntent().getStringExtra("ROOM_ID");
        conferenceId = getIntent().getStringExtra("CONFERENCE_ID");
        if (roomId == null) roomId = "DEFAULT_ROOM";

        if (allPermissionsGranted()) {
            initVideoChat();
            connectToChat();
            loadParticipants(); // создаём ChatViewModel один раз
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, 100);
        }
        binding.idRoom.setText("Комната: " + roomId);
        binding.main.setOnClickListener(v -> showBottomMenu());
    }

    private void connectToChat() {
        chatViewModel = new ChatViewModel(CHAT_HUB_URL);
        chatViewModel.start();
        chatViewModel.joinGroup(roomId);
    }

    // 👉 Геттер для ChatViewModel
    public ChatViewModel getChatViewModel() {
        return chatViewModel;
    }

    private void initVideoChat() {
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Используем поле класса, а не создаем локальную переменную
        participants.clear();
        participants.add(new Participant(
                cache.getUserId(),
                cache.getUserName(),
                cache.getAvatarUrl(),
                false,
                true,
                System.currentTimeMillis()
        ));

        adapter = new ParticipantAdapter(
                participants,
                viewModel.getRepository().getEglContext(),
                viewModel.getRepository()
        );

        adapter.setOnItemClickListener(this::showBottomMenu);
        binding.participantsRecycler.setAdapter(adapter);

        viewModel.remoteTracks.observe(this, tracks -> {
            if (tracks != null) {
                for (String userId : tracks.keySet()) {
                    adapter.addParticipant(new Participant(
                            userId,
                            "Участник " + userId.substring(0, 4),
                            null,
                            false,
                            true,
                            System.currentTimeMillis()
                    ));
                }
                adapter.setRemoteTracks(tracks);
            }
        });

        viewModel.startVideoCall(roomId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (allPermissionsGranted()) {
                initVideoChat();
                connectToChat();
                loadParticipants();
            }
        }
    }

    private void showBottomMenu() {
        BottomFragment bottomFragment = new BottomFragment();
        bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (viewModel != null) viewModel.stopVideoCall();
        super.onDestroy();
    }

    private void loadParticipants() {
        // Используем roomId напрямую вместо viewModel.getCurrentRoomId()
        if (conferenceId != null) {
            repository.getParticipants(conferenceId, new ConferenceRepository.ParticipantsCallback() {
                @Override
                public void onSuccess(List<Participant> loadedParticipants) {
                    runOnUiThread(() -> {
                        if (loadedParticipants != null) {
                            for (Participant p : loadedParticipants) {
                                adapter.addParticipant(p);
                            }
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    // Handle error
                }
            });
        }
    }

    public String getConferenceId() {
        return conferenceId;
    }
}