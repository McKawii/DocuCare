package com.example.todo.database;

import android.content.Context;

import androidx.room.*;

import com.example.todo.model.Badanie;
import com.example.todo.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, Badanie.class}, version = 2, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class TaskDatabase extends RoomDatabase {
    private static TaskDatabase instance;

    public abstract TaskDao taskDao();
    public abstract BadanieDao badanieDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //def baza room
    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TaskDatabase.class, "TaskDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}