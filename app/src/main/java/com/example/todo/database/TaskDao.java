package com.example.todo.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.todo.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM TaskTable ORDER BY CASE WHEN DueTime IS NULL THEN 1 ELSE 0 END, DueTime ASC, CreateTime ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM TaskTable WHERE (:query != '') AND (Title LIKE '%' || :query || '%') ORDER BY CASE WHEN DueTime IS NULL THEN 1 ELSE 0 END, DueTime ASC, CreateTime ASC")
    LiveData<List<Task>> searchTask(String query);

    @Query("SELECT * FROM TaskTable WHERE IsCompleted = 0 ORDER BY CASE WHEN DueTime IS NULL THEN 1 ELSE 0 END, DueTime ASC, CreateTime ASC")
    LiveData<List<Task>> getUncompletedTasks();

    @Query("SELECT * FROM TaskTable WHERE Id = :taskId")
    Task getTask(int taskId);

    @Query("SELECT DISTINCT Category FROM TaskTable WHERE Category != '' ORDER BY Category ASC")
    LiveData<List<String>> getCategoryList();

    @Query("SELECT * FROM TaskTable WHERE Category = :category AND (:hideCompletedTasks = 0 OR IsCompleted = 0) ORDER BY CASE WHEN DueTime IS NULL THEN 1 ELSE 0 END, DueTime ASC, CreateTime ASC")
    LiveData<List<Task>> getTasksByCategory(String category, boolean hideCompletedTasks);

}
