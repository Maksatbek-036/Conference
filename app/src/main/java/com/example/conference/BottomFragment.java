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

        // Наблюдаем за состоянием видео для обновления UI
        viewModel.isVideoEnabled.observe(getViewLifecycleOwner(), isEnabled -> {
            if (isEnabled) {
                binding.videoButton.setImageResource(R.drawable.iconstack_io____video_camera_);
                binding.videoButton.setBackgroundResource(R.drawable.icon_form_circle);
            } else {
                binding.videoButton.setImageResource(R.drawable.iconstack_io____video_camera_);
                binding.videoButton.setBackgroundResource(R.drawable.icon_form_circle_red);
            }
        });

        // Наблюдаем за состоянием микрофона для обновления UI
        viewModel.isAudioEnabled.observe(getViewLifecycleOwner(), isEnabled -> {
            if (isEnabled) {
                binding.microButton.setImageResource(R.drawable.iconstack_io____mic_);
                binding.microButton.setBackgroundResource(R.drawable.icon_form_circle);
            } else {
                binding.microButton.setImageResource(R.drawable.iconstack_io____mic_off_);
                binding.microButton.setBackgroundResource(R.drawable.icon_form_circle_red);
            }
        });

        binding.chatButton.setOnClickListener(v -> {
            new ChatFragment().show(getParentFragmentManager(), TAG_CHAT);
            dismiss();
        });

        binding.participantButton.setOnClickListener(v -> {
            new ParticipantFragment().show(getParentFragmentManager(), TAG_PARTICIPANTS);
            dismiss();
        });
        binding.swapScreenButton.setOnClickListener(v -> {
            viewModel.switchCamera();
        });
        binding.callEndButton.setOnClickListener(v -> {
            // Сначала останавливаем звонок
            viewModel.stopVideoCall();
            dismiss();

            // Завершаем работу Activity.
            requireActivity().finish();
        });

        binding.microButton.setOnClickListener(v -> {
            viewModel.toggleAudio();
        });

        binding.videoButton.setOnClickListener(v -> {
            viewModel.toggleVideo();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
