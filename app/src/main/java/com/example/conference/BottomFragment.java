package com.example.conference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.conference.ParticipantFragment;
import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.BottomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomFragment extends BottomSheetDialogFragment {

    private BottomBinding binding;
    private VideoCallViewModel viewModel;

    // Константы для тегов
    private static final String TAG_CHAT = "CHAT_DIALOG";
    private static final String TAG_PARTICIPANTS = "PARTICIPANTS_DIALOG";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = BottomBinding.inflate(inflater, container, false);

        // Получаем общую ViewModel уровня Activity
        viewModel = new ViewModelProvider(requireActivity()).get(VideoCallViewModel.class);

        binding.chatButton.setOnClickListener(v -> {
            new ChatFragment().show(getParentFragmentManager(), TAG_CHAT);
            dismiss();
        });

        binding.participantButton.setOnClickListener(v -> {
            new ParticipantFragment().show(getParentFragmentManager(), TAG_PARTICIPANTS);
            dismiss();
        });

        binding.callEndButton.setOnClickListener(v -> {
            // Сначала останавливаем звонок
            viewModel.stopVideoCall();
            dismiss();

            // Завершаем работу Activity.
            // Используем requireActivity(), так как фрагмент в этот момент точно прикреплен.
            requireActivity().finish();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}