package com.example.todo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.model.Task;
import com.example.todo.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements TaskClickInterface {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fabAddTask;
    private Toolbar toolbar;
    private TaskViewModel taskViewModel;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                if (key.equals("hide_finished_tasks")) {
                    boolean hideFinishedTasks = sharedPreferences.getBoolean("hide_finished_tasks", false);
                    taskViewModel.setHideCompletedTasks(hideFinishedTasks);
                } else if (key.equals("category_filter")) {
                    boolean categoryFilter = sharedPreferences.getBoolean("category_filter", false);
                    taskViewModel.setChooseByCategory(categoryFilter);
                } else if (key.equals("category_list")) {
                    String categoryList = sharedPreferences.getString("category_list", "");
                    taskViewModel.setCategory(categoryList);
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        initUI();

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }





    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void initUI() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabAddTask = findViewById(R.id.floatingActionButton);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        taskAdapter = new TaskAdapter(this);
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(this.getApplication())).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> {
            taskAdapter.submitList(tasks);
        });


        recyclerViewTasks.setAdapter(taskAdapter);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_search_main) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("task", task);

        startActivity(intent);
    }

    @Override
    public void onTaskCheckBoxClick(Task task) {
        taskViewModel.updateTaskCompletedStatus(task, !task.isCompleted());
    }

}