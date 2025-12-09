package com.example.todo.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.todo.model.Badanie;
import com.example.todo.repository.BadanieRepository;

import java.util.List;

public class BadanieViewModel extends ViewModel {
    private BadanieRepository repository;
    private LiveData<List<Badanie>> allBadania;

    public BadanieViewModel(Application application) {
        repository = new BadanieRepository(application);
        allBadania = repository.getAllBadania();
    }

    public void insertBadanie(Badanie badanie) {
        repository.insertBadanie(badanie);
    }

    public void updateBadanie(Badanie badanie) {
        repository.updateBadanie(badanie);
    }

    public void deleteBadanie(Badanie badanie) {
        repository.deleteBadanie(badanie);
    }

    public LiveData<List<Badanie>> getAllBadania() {
        return allBadania;
    }
}

