package com.example.conference;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.conference.Adapters.ParticipantFragAdapter;
import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.Participant;
import com.example.conference.Repositories.ConferenceRepository;
import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.FragmentParticipantBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ParticipantFragment extends BottomSheetDialogFragment {
    Cache cache;
    ConferenceApi api;
    FragmentParticipantBinding binding;
    ConferenceRepository conferenceRepository;
    VideoCallViewModel videoCallVM;
    private String conferenceId;
    ArrayList<Participant> participants = new ArrayList<>();
    ParticipantFragAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = new Cache(requireContext());
        api = RetrofitClient.getApi(ConferenceApi.class);
        conferenceRepository = new ConferenceRepository(api);
        conferenceId = ((VideoHub) requireActivity()).getConferenceId();
        videoCallVM = new ViewModelProvider(requireActivity()).get(VideoCallViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentParticipantBinding.inflate(inflater, container, false);

        adapter = new ParticipantFragAdapter(participants);
        binding.participantRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.participantRecycler.setAdapter(adapter);

        loadParticipants();

        return binding.getRoot();
    }

    private void loadParticipants() {

        if (conferenceId != null) {
            conferenceRepository.getParticipants(conferenceId, new ConferenceRepository.ParticipantsCallback() {
                @Override
                public void onSuccess(List<Participant> loadedParticipants) {
                    if (getActivity() == null) return;
                    
                    participants.clear();
                    participants.addAll(loadedParticipants);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(String message) {
                    if (getActivity() == null) return;
                    Log.e("ParticipantFragment", "Error loading participants: " + message);
                    Toast.makeText(getContext(), "Ошибка загрузки участников", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}