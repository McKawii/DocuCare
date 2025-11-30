package com.example.todo.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todo.database.TaskDao;
import com.example.todo.database.TaskDatabase;
import com.example.todo.model.Task;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<String>> categoryList;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        categoryList = taskDao.getCategoryList();
    }

    public void insertTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insertTask(task));
    }

    public void updateTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.updateTask(task));
    }

    public void deleteTask(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.deleteTask(task));
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> searchTask(String query) {
        return taskDao.searchTask(query);
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<Task>> getUncompletedTasks() {
        return taskDao.getUncompletedTasks();
    }

    public LiveData<List<Task>> getTasksByCategory(String category, boolean hideCompletedTasks) {
        return taskDao.getTasksByCategory(category, hideCompletedTasks);
    }

}