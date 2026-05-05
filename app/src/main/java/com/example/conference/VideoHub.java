package com.example.conference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.conference.Adapters.ParticipantAdapter;
import com.example.conference.Models.Participant;
import com.example.conference.ViewModels.ChatViewModel;
import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.ActivityVideoHubBinding;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class VideoHub extends AppCompatActivity {
    private ActivityVideoHubBinding binding;
    private VideoCallViewModel viewModel;
    private ChatViewModel chatViewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private ParticipantAdapter adapter;
    private String roomId;
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

        cache = new Cache(this);

        roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId == null) roomId = "DEFAULT_ROOM";

        if (allPermissionsGranted()) {
            initVideoChat();
            connectToChat(); // подключаем чат сразу
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, 100);
        }

        binding.main.setOnClickListener(v -> showBottomMenu());
    }

    private void connectToChat() {
        // Используем фабрику для передачи baseUrl

        ChatViewModel.ChatViewModelFactory factory =
                new ChatViewModel.ChatViewModelFactory(CHAT_HUB_URL);
        chatViewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        chatViewModel.start();
        chatViewModel.joinGroup(roomId);
    }

    private void initVideoChat() {
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<Participant> participants = new ArrayList<>();
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
}
