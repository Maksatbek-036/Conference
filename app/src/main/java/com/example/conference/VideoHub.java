package com.example.conference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

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

public class VideoHub extends AppCompatActivity {
    private ActivityVideoHubBinding binding;
    private VideoCallViewModel viewModel;
    private ChatViewModel chatViewModel;
    private ConferenceRepository repository;
    private ArrayList<Participant> participants = new ArrayList<>();
    private ParticipantAdapter adapter;
    private String roomId; // Короткий код для SignalR (например, "2874")
    private String conferenceId; // GUID для API (например, "4543db1d-...")
    private Cache cache;

    public static final String CHAT_HUB_URL = "http://185.255.132.217:5000";

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConferenceApi api = RetrofitClient.getApi(ConferenceApi.class);
        cache = new Cache(this);
        repository = new ConferenceRepository(api);

        // Получаем оба ID из интента
        roomId = getIntent().getStringExtra("ROOM_ID");
        conferenceId = getIntent().getStringExtra("CONFERENCE_ID");

        if (roomId == null) roomId = "DEFAULT_ROOM";
        // Если GUID не пришел, используем roomId как запасной вариант для API
        if (conferenceId == null) conferenceId = roomId;

        if (allPermissionsGranted()) {
            initVideoChat();
            connectToChat();
            loadParticipants();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, 100);
        }

        // В UI показываем короткий номер комнаты
        binding.idRoom.setText("Комната: " + roomId);
        binding.main.setOnClickListener(v -> showBottomMenu());
    }

    private void connectToChat() {
        chatViewModel = new ChatViewModel(CHAT_HUB_URL);
        chatViewModel.start();
        // Используем roomId для SignalR чата
        chatViewModel.joinGroup(roomId);
    }

    private void initVideoChat() {
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));

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
                List<String> activeIds = new ArrayList<>(tracks.keySet());
                activeIds.add(cache.getUserId());
                adapter.syncParticipants(activeIds);

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

        // Используем roomId для SignalR видеозвонка
        viewModel.startVideoCall(roomId);
    }

    private void loadParticipants() {
        // GUID по-прежнему нужен для запроса списка участников через API
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
                    Log.e("VideoHub", "Error loading participants: " + message);
                }
            });
        }
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

    public String getConferenceId() {
        return conferenceId;
    }

    public ChatViewModel getChatViewModel() {
        return chatViewModel;
    }
}