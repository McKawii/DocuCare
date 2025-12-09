package com.example.todo.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.todo.model.Badanie;

import java.util.List;

@Dao
public interface BadanieDao {

    @Insert
    void insertBadanie(Badanie badanie);

    @Update
    void updateBadanie(Badanie badanie);

    @Delete
    void deleteBadanie(Badanie badanie);

    @Query("SELECT * FROM BadanieTable ORDER BY DataOstatniegoBadania DESC")
    LiveData<List<Badanie>> getAllBadania();

    @Query("SELECT * FROM BadanieTable WHERE Id = :badanieId")
    Badanie getBadanie(int badanieId);
}

