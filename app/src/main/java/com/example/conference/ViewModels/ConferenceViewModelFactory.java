package com.example.conference.ViewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ConferenceViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ConferenceViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ConferenceViewModel.class)) {
            return (T) new ConferenceViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
