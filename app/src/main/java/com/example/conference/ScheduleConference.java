package com.example.conference;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.conference.ViewModels.ConferenceViewModel;
import com.example.conference.ViewModels.ConferenceViewModelFactory;
import com.example.conference.databinding.ActivityScheduleConferenceBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ScheduleConference extends BottomSheetDialogFragment {
    private ActivityScheduleConferenceBinding binding;
    private ConferenceViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_schedule_conference, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = ActivityScheduleConferenceBinding.bind(view);

        ConferenceViewModelFactory viewModelFactory = new ConferenceViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ConferenceViewModel.class);

        binding.saveConferenceButton.setOnClickListener(v -> {
            String title = binding.conferenceTitle.getText().toString();
            String description = binding.conferenceDescription.getText().toString();
            String dateStr = binding.conferenceDate.getText().toString();
            String startTime = binding.conferenceStartTime.getText().toString();
            String endTime = binding.conferenceEndTime.getText().toString();
            String location = binding.conferenceLocation.getText().toString();
            boolean isOnline = binding.conferenceIsOnline.isChecked();

            if (title.isEmpty() || description.isEmpty() || dateStr.isEmpty()
                    || startTime.isEmpty() || endTime.isEmpty() || location.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            long dateMillis;
            try {
                // Пример: "2026-04-25"
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dateMillis = sdf.parse(dateStr).getTime();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Неверный формат даты", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.createConference(title, description, dateMillis, startTime, endTime, location, isOnline);
            Toast.makeText(getContext(), "Конференция создана", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
