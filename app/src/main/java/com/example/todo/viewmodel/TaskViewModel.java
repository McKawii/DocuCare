package com.example.todo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends ViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<String>> categoryList;
    private MutableLiveData<Boolean> hideCompletedTasks = new MutableLiveData<>();
    private MutableLiveData<Boolean> chooseByCategory = new MutableLiveData<>();
    private MutableLiveData<String> category = new MutableLiveData<>();

    public TaskViewModel(Application application) {
        repository = new TaskRepository(application);
        categoryList = repository.getCategoryList();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        hideCompletedTasks.setValue(preferences.getBoolean("hide_finished_tasks", false));
        chooseByCategory.setValue(preferences.getBoolean("category_filter", false));
        category.setValue(preferences.getString("category_list", ""));

        allTasks = Transformations.switchMap(hideCompletedTasks, hideCompleted ->
                Transformations.switchMap(chooseByCategory, chooseCategory ->
                        Transformations.switchMap(category, currentCategory -> {
                            if (Boolean.TRUE.equals(chooseCategory) && !currentCategory.isEmpty()) {
                                return repository.getTasksByCategory(currentCategory, Boolean.TRUE.equals(hideCompleted));
                            } else {
                                if (Boolean.TRUE.equals(hideCompleted)) {
                                    return repository.getUncompletedTasks();
                                } else {
                                    return repository.getAllTasks();
                                }
                            }
                        })
                )
        );
    }

    public void setHideCompletedTasks(boolean hide) {
        hideCompletedTasks.setValue(hide);
    }

    public void setChooseByCategory(boolean choose) {
        chooseByCategory.setValue(choose);
    }

    public void setCategory(String category) {
        this.category.setValue(category);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> searchTask(String query) {
        return repository.searchTask(query);
    }

    public LiveData<List<String>> getCategoryList() {
        return categoryList;
    }

    public void updateTaskCompletedStatus(Task task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        repository.updateTask(task);
    }

    public LiveData<List<Task>> getUncompletedTasks() {
        return repository.getUncompletedTasks();
    }


    public LiveData<List<Task>> getTasks() {
        return Transformations.switchMap(hideCompletedTasks, hideCompleted -> {
            if (Boolean.TRUE.equals(hideCompleted)) {
                return getUncompletedTasks();
            } else {
                return getAllTasks();
            }
        });
    }

    public LiveData<List<Task>> getTasksByCategory(String category, boolean hideCompletedTasks) {
        return repository.getTasksByCategory(category, hideCompletedTasks);}

}
