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

import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;

public class VideoHub extends AppCompatActivity {
    private ActivityVideoHubBinding binding;
    private VideoCallViewModel viewModel;
    private ParticipantAdapter adapter;

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

        // Логика вызова BottomSheet
        binding.main.setOnClickListener(v -> {
            // Убедитесь, что класс BottomFragment существует
            // BottomFragment bottomFragment = new BottomFragment();
            // bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
        });
    }

    private void initVideoChat() {
        // Инициализируем ViewModel
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        // 1. Подготавливаем локальные ресурсы (камера, микрофон, PeerConnection)
        viewModel.startVideoCall();

        // 2. Настройка списка участников
        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<Participant> participants = new ArrayList<>();
        loadParticipants(participants);

        // 3. Инициализация адаптера
        // Важно: передаем репозиторий для доступа к локальному треку (для "Я")
        adapter = new ParticipantAdapter(
                participants,
                viewModel.getRepository().getEglContext(),
                viewModel.getRepository()
        );
        binding.participantsRecycler.setAdapter(adapter);

        // 4. Подписка на удаленное видео
        viewModel.remoteVideoTrack.observe(this, videoTrack -> {
            if (videoTrack != null) {
                // Находим в списке Participant объект собеседника (например, User 2)
                // и передаем ему трек для отображения в холдере адаптера.

                // В простейшем случае, если у нас 1 на 1, мы знаем, что
                // удаленный трек принадлежит второму участнику (position 1):
                adapter.setRemoteTrack(videoTrack);

                // Если участников много, логика в адаптере должна сопоставлять
                // track -> userId, но для начала проверим на одном.
            }
        });
    }

    private void loadParticipants(ArrayList<Participant> participants) {
        // me — зарезервированный ID для локального пользователя
        participants.add(new Participant("me", "Я (Вы)", null, true, true, 100));
        participants.add(new Participant("target_user", "Собеседник", null, true, true, 200));
        participants.add(new Participant("id3", "User 3", null, true, true, 300));
        participants.add(new Participant("id4", "User 4", null, true, true, 400));
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
            finish(); // Закрываем экран, если нет разрешений
        }
    }

    @Override
    protected void onDestroy() {
        // Очистка ресурсов происходит в ViewModel.onCleared(),
        // но здесь мы дублируем остановку при закрытии Activity
        if (viewModel != null) {
            viewModel.stopVideoCall();
        }
        super.onDestroy();
    }
}