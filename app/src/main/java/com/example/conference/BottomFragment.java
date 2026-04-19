package com.example.conference;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.conference.databinding.BottomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class BottomFragment extends BottomSheetDialogFragment {

    // Делаем binding nullable, чтобы избежать утечек
    private BottomBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Используем переданный inflater
        binding = BottomBinding.inflate(inflater, container, false);

        binding.chatButton.setOnClickListener(View ->{
                    ChatFragment chatFragment = new ChatFragment();
                    chatFragment.show(getParentFragmentManager(), chatFragment.getTag());

                }
        );
       binding.participantButton.setOnClickListener(View ->{
           ParticipantFragment participantFragment = new ParticipantFragment();
           participantFragment.show(getParentFragmentManager(), participantFragment.getTag());
       });
        return binding.getRoot();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ОБЯЗАТЕЛЬНО обнуляем binding
        binding = null;
    }
}