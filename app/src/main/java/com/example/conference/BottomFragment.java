package com.example.conference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.conference.ViewModels.VideoCallViewModel;
import com.example.conference.databinding.BottomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomFragment extends BottomSheetDialogFragment {

    private BottomBinding binding;
    private VideoCallViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = BottomBinding.inflate(inflater, container, false);
        
        // Используем requireActivity(), чтобы получить тот же экземпляр ViewModel, что и в VideoHub
        viewModel = new ViewModelProvider(requireActivity()).get(VideoCallViewModel.class);

        binding.chatButton.setOnClickListener(v -> {
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.show(getParentFragmentManager(), chatFragment.getTag());
            dismiss();
        });

        binding.participantButton.setOnClickListener(v -> {
            ParticipantFragment participantFragment = new ParticipantFragment();
            participantFragment.show(getParentFragmentManager(), participantFragment.getTag());
            dismiss();
        });

        binding.callEndButton.setOnClickListener(v -> {
            // Останавливаем звонок через общую ViewModel
            viewModel.stopVideoCall();
            // Закрываем BottomSheet
            dismiss();
            // Завершаем Activity, чтобы выйти из экрана конференции
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
