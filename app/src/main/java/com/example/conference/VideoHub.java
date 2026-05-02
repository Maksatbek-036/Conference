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
import java.util.Map;

public class VideoHub extends AppCompatActivity {
    private ActivityVideoHubBinding binding;
    private VideoCallViewModel viewModel;
    private ParticipantAdapter adapter;
    private String roomId;
    private Cache cache;

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
cache=new Cache(this);
        roomId = getIntent().getStringExtra("ROOM_ID");
        if (roomId == null) roomId = "DEFAULT_ROOM";

        if (allPermissionsGranted()) {
            initVideoChat();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, 100);
        }

        binding.main.setOnClickListener(v -> showBottomMenu());
    }

    private void showBottomMenu() {
        BottomFragment bottomFragment = new BottomFragment();
        bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
    }

    private void initVideoChat() {
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);
        
        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<Participant> participants = new ArrayList<>();
        // Добавляем себя первым
        participants.add(new Participant(cache.getUserId(), cache.getUserName(),cache.getAvatarUrl(), false, true, System.currentTimeMillis()));

        adapter = new ParticipantAdapter(
                participants,
                viewModel.getRepository().getEglContext(),
                viewModel.getRepository()
        );
        
        adapter.setOnItemClickListener(this::showBottomMenu);
        binding.participantsRecycler.setAdapter(adapter);

        // Наблюдаем за списком удаленных треков
        viewModel.remoteTracks.observe(this, tracks -> {
            if (tracks != null) {
                // Обновляем список участников на основе полученных треков
                for (String userId : tracks.keySet()) {
                    adapter.addParticipant(new Participant(userId, "Участник " + userId.substring(0, 4), null, false, true, System.currentTimeMillis()));
                }
                
                // Передаем треки в адаптер
                adapter.setRemoteTracks(tracks);
            }
        });

        viewModel.startVideoCall(roomId);
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
