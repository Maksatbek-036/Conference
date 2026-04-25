package com.example.conference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.conference.Adapters.ParticipantAdapter;
import com.example.conference.Models.Participant;
import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.ActivityVideoHubBinding;

import java.util.ArrayList;

public class VideoHub extends AppCompatActivity {
    private ActivityVideoHubBinding binding;
    private VideoCallViewModel viewModel;

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (allPermissionsGranted()) {
            initVideoChat();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, 100);
        }

        binding.main.setOnClickListener(v -> {
            BottomFragment bottomFragment = new BottomFragment();
            bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
        });
    }

    private void initVideoChat() {
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        // 1. Сначала запускаем WebRTC, чтобы подготовить камеру и EGL контекст
        viewModel.startVideoCall();

        // 2. Настройка списка участников
        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<Participant> participants = new ArrayList<>();
        loadParticipants(participants);

        // 3. Инициализируем адаптер, передавая ему контекст отрисовки и репозиторий
        // Это позволит адаптеру достать камеру для первого элемента (вас)
        ParticipantAdapter adapter = new ParticipantAdapter(
                participants,
                viewModel.getRepository().getEglContext(),
                viewModel.getRepository()
        );
        binding.participantsRecycler.setAdapter(adapter);

        // 4. Подписка на удаленное видео (для тех, кто не в списке или для отдельного окна)
        viewModel.remoteVideoTrack.observe(this, videoTrack -> {
            // Если вам нужно дублировать видео собеседника в отдельное большое окно:

        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && allPermissionsGranted()) {
            initVideoChat();
        } else {
            Toast.makeText(this, "Разрешения необходимы", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.stopVideoCall();
        }
    }

    private void loadParticipants(ArrayList<Participant> participants) {
        // Первый элемент — это ВЫ. Адаптер автоматически подцепит локальное видео для position 0.
        participants.add(new Participant("me", "Я (Вы)", null, true, true, 100));
        participants.add(new Participant("id2", "User 2", "url", true, true, 200));
        participants.add(new Participant("id3", "User 3", "url", true, true, 300));
        participants.add(new Participant("id4", "User 4", "url", true, true, 400));
    }
}