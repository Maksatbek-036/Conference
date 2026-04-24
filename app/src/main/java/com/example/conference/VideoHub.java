package com.example.conference;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            return;
        }
        if(checkSelfPermission(Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 100);
            return;
        }


        // ПРАВИЛЬНАЯ инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);

        // Настройка RecyclerView
        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        ArrayList<Participant> participants = new ArrayList<>();
        loadParticipants(participants);
        ParticipantAdapter adapter = new ParticipantAdapter(participants);
        binding.participantsRecycler.setAdapter(adapter);

        // Запуск логики WebRTC
        viewModel.startVideoCall();

        binding.main.setOnClickListener(v -> {
            BottomFragment bottomFragment = new BottomFragment();
            bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Остановка звонка при закрытии Activity
        if (viewModel != null) {
            viewModel.stopVideoCall();
        }
    }


    private void loadParticipants(ArrayList<Participant> participants) {
        participants.add(new Participant("id1", "User 1", "url", true, true, 100));
        participants.add(new Participant("id2", "User 2", "url", true, true, 200));
        participants.add(new Participant("id3", "User 3", "url", true, true, 300));
        participants.add(new Participant("id4", "User 4", "url", true, true, 400));
    }
}