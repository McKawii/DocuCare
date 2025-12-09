package com.example.todo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.viewmodel.BadanieViewModel;

public class BadanieViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public BadanieViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BadanieViewModel.class)) {
            return (T) new BadanieViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

