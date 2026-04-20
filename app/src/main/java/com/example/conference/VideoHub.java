package com.example.conference;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Adapters.ParticipantAdapter;
import com.example.conference.Models.Participant;
import com.example.conference.databinding.ActivityVideoHubBinding;

import java.util.ArrayList;
public class VideoHub extends AppCompatActivity {
    ActivityVideoHubBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoHubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Настройка RecyclerView
        binding.participantsRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        ArrayList<Participant> participants = new ArrayList<>();
        loadParticipants(participants);
        ParticipantAdapter adapter = new ParticipantAdapter(participants);
        binding.participantsRecycler.setAdapter(adapter);

        // Обработчик клика на корневой ConstraintLayout
        binding.main.setOnClickListener(v -> {
            BottomFragment bottomFragment = new BottomFragment();
            bottomFragment.show(getSupportFragmentManager(), bottomFragment.getTag());
        });
    }

    private void loadParticipants(ArrayList<Participant> participants) {
        participants.add(new Participant("dadf","asdasd","asdasd",true,true,123123123));
        participants.add(new Participant("dadf","asdasd","asdasd",true,true,123123123));
        participants.add(new Participant("dadf","asdasd","asdasd",true,true,123123123));
    }
}
