package com.example.todo;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.viewmodel.TaskViewModel;

public class TaskViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;

    public TaskViewModelFactory(Application application) {
        mApplication = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TaskViewModel(mApplication);
    }
}