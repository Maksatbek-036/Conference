package com.example.conference;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.conference.Api.ConferenceApi;
import com.example.conference.Api.RetrofitClient;
import com.example.conference.Models.Conference;
import com.example.conference.Repositories.ConferenceRepository;
import com.example.conference.databinding.ActivityScheduleConferenceBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class ScheduleConference extends BottomSheetDialogFragment {
    private ActivityScheduleConferenceBinding binding;
    private ConferenceRepository conferenceRepository;
    private ConferenceApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityScheduleConferenceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        api = RetrofitClient.getApi(ConferenceApi.class);
        conferenceRepository = new ConferenceRepository(api);

        // Настройка всех барабанов
        setupAllPickers();

        binding.saveConferenceButton.setOnClickListener(v -> {
            saveConference();
        });
    }

    private void setupAllPickers() {
        Calendar c = Calendar.getInstance();

        // 1. Настройка даты (День, Месяц, Год)
        int year = c.get(Calendar.YEAR);
        binding.npYear.setMinValue(year);
        binding.npYear.setMaxValue(year + 5);
        binding.npYear.setValue(year);

        binding.npMonth.setMinValue(1);
        binding.npMonth.setMaxValue(12);
        binding.npMonth.setValue(c.get(Calendar.MONTH) + 1);
        binding.npMonth.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));

        binding.npDay.setMinValue(1);
        binding.npDay.setMaxValue(c.getActualMaximum(Calendar.DAY_OF_MONTH));
        binding.npDay.setValue(c.get(Calendar.DAY_OF_MONTH));

        // 2. Настройка времени НАЧАЛА
        setupTimePicker(binding.npStartHour, binding.npStartMin, c.get(Calendar.HOUR_OF_DAY), 0);

        // 3. Настройка времени ОКОНЧАНИЯ (по умолчанию +1 час)
        setupTimePicker(binding.npEndHour, binding.npEndMin, (c.get(Calendar.HOUR_OF_DAY) + 1) % 24, 0);

        // 4. Логика динамических дней (февраль и т.д.)
        setupDynamicDays();

        // 5. Блокировка клавиатуры для всех
        blockAllKeyboards();
    }

    private void setupTimePicker(NumberPicker hp, NumberPicker mp, int hour, int min) {
        hp.setMinValue(0);
        hp.setMaxValue(23);
        hp.setValue(hour);
        hp.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));

        mp.setMinValue(0);
        mp.setMaxValue(59);
        mp.setValue(min);
        mp.setFormatter(i -> String.format(Locale.getDefault(), "%02d", i));
    }

    private void setupDynamicDays() {
        NumberPicker.OnValueChangeListener listener = (picker, oldVal, newVal) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, binding.npYear.getValue());
            calendar.set(Calendar.MONTH, binding.npMonth.getValue() - 1);

            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (binding.npDay.getValue() > maxDays) {
                binding.npDay.setValue(maxDays);
            }
            binding.npDay.setMaxValue(maxDays);
        };
        binding.npMonth.setOnValueChangedListener(listener);
        binding.npYear.setOnValueChangedListener(listener);
    }

    private void saveConference() {
        String title = binding.conferenceTitle.getText().toString();
        String description = binding.conferenceDescription.getText().toString();
        String location = binding.conferenceLocation.getText().toString();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format(Locale.US, "%d-%02d-%02d",
                binding.npYear.getValue(),
                binding.npMonth.getValue(),
                binding.npDay.getValue());

        String startTime = String.format(Locale.US, "%02d:%02d:00",
                binding.npStartHour.getValue(),
                binding.npStartMin.getValue());

        String endTime = String.format(Locale.US, "%02d:%02d:00",
                binding.npEndHour.getValue(),
                binding.npEndMin.getValue());

        boolean isOnline = binding.conferenceIsOnline.isChecked();

        // Отправляем в репозиторий с использованием callback
        conferenceRepository.createConference(title, description, location, date, startTime, endTime, isOnline, new ConferenceRepository.CreateCallback() {
            @Override
            public void onSuccess(Conference conference) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Конференция создана", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void blockAllKeyboards() {
        NumberPicker[] pickers = {
                binding.npDay, binding.npMonth, binding.npYear,
                binding.npStartHour, binding.npStartMin,
                binding.npEndHour, binding.npEndMin
        };
        for (NumberPicker np : pickers) {
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
