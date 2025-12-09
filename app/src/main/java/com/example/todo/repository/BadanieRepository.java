package com.example.todo.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todo.database.BadanieDao;
import com.example.todo.database.TaskDatabase;
import com.example.todo.model.Badanie;

import java.util.List;

public class BadanieRepository {
    private BadanieDao badanieDao;
    private LiveData<List<Badanie>> allBadania;

    public BadanieRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        badanieDao = database.badanieDao();
        allBadania = badanieDao.getAllBadania();
    }

    public void insertBadanie(Badanie badanie) {
        TaskDatabase.databaseWriteExecutor.execute(() -> badanieDao.insertBadanie(badanie));
    }

    public void updateBadanie(Badanie badanie) {
        TaskDatabase.databaseWriteExecutor.execute(() -> badanieDao.updateBadanie(badanie));
    }

    public void deleteBadanie(Badanie badanie) {
        TaskDatabase.databaseWriteExecutor.execute(() -> badanieDao.deleteBadanie(badanie));
    }

    public LiveData<List<Badanie>> getAllBadania() {
        return allBadania;
    }
}

