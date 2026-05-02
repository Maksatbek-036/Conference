package com.example.conference;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.ParticipantAdapter;
import com.example.conference.Adapters.ParticipantFragAdapter;
import com.example.conference.Models.Participant;
import com.example.conference.databinding.FragmentParticipantBinding;
import com.example.conference.databinding.ParticipantItemFragBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class ParticipantFragment extends BottomSheetDialogFragment {
    Cache cache;

    FragmentParticipantBinding binding;
    ArrayList<Participant> participants=  new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadParticipants();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cache= new Cache(getContext());
        binding=FragmentParticipantBinding.inflate(inflater,container,false);
        binding.participantRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.participantRecycler.setAdapter(new ParticipantFragAdapter(participants));
        return binding.getRoot();

    }
    private void loadParticipants() {
        participants.add(new Participant("id1", "User 1", "url", true, true, 100));
        participants.add(new Participant("id2", "User 2", "url", true, true, 200));
    }
}
