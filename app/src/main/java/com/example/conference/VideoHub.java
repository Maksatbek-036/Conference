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
    private ParticipantAdapter adapter;
    private String roomId; // Храним ID комнаты

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем ID комнаты из Intent. Если его нет (например, прямая ссылка), ставим дефолт.
        roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId == null) roomId = "DEFAULT_ROOM";

        if (allPermissionsGranted()) {
            initVideoChat();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, 100);
        }

        // Вызов BottomFragment при нажатии на фон экрана
        binding.main.setOnClickListener(v -> showBottomMenu());
    }

    private void showBottomMenu() {
        BottomFragment bottomFragment = new BottomFragment();
        bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
    }

    private void initVideoChat() {
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        // Передаем roomId в метод старта
        viewModel.startVideoCall(roomId);

        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(new Participant("me", "Я (Вы)", null, true, true, 100));

        adapter = new ParticipantAdapter(
                participants,
                viewModel.getRepository().getEglContext(),
                viewModel.getRepository()
        );
        
        // Вызов BottomFragment при нажатии на любого участника в списке
        adapter.setOnItemClickListener(this::showBottomMenu);

        binding.participantsRecycler.setAdapter(adapter);

        viewModel.remoteVideoTrack.observe(this, videoTrack -> {
            if (videoTrack != null) {
                adapter.setRemoteTrack(videoTrack);
            }
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
            Toast.makeText(this, "Разрешения камеры и микрофона необходимы для звонка", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (viewModel != null) {
            viewModel.stopVideoCall();
        }
        super.onDestroy();
    }
}
