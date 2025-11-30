package com.example.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.model.Task;
import com.example.todo.viewmodel.TaskViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;


public class AddEditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AttachmentClickInterface, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CODE_SELECT_FILES = 2137;
    private RecyclerView recyclerViewAttachments;
    private AttachmentsRecyclerViewAdapter attachmentsRecyclerViewAdapter;
    private CheckBox checkBoxTaskStatus;
    private EditText editTextTaskTitle;
    private TextView editTextTaskDueDate;
    private CheckBox enableNotification;
    private EditText editTextCategory;
    private TextView textViewAddAttachment;
    private EditText editTextTaskDescription;
    private TextView textViewCreatedTime;
    private TextView textViewUpdatedTime;
    private ImageView imageViewClearCategory;
    private ImageView imageViewMoreCategory;
    private Toolbar toolbar;
    private LinearLayout linearLayoutCreatedModifiedTime;
    private TaskViewModel taskViewModel;
    private ImageView imageViewClearDueDate;
    private Date newDueDate;

    private Task task;

    private List<String> attachments;
    private List<String> tempAttachmentsPaths;

    private List<String> attachmentsToRemove;

    private boolean noPermission = false;
    private List<String> categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AddEditTaskLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUI();
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(this.getApplication())).get(TaskViewModel.class);
        tempAttachmentsPaths = new ArrayList<>();

        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {
            getSupportActionBar().setTitle("Edit Task");
            // get task from intent
            task = (Task) getIntent().getSerializableExtra("task");
            loadTask();
            if (task.getAttachmentsPaths() != null) {
                attachments = task.getAttachmentsPaths();
            } else {
                attachments = new ArrayList<>();
            }
            if (getIntent().getBooleanExtra("notification", false)) {
                enableNotification.setChecked(false);
                try {
                    saveTask();
                } catch (IllegalArgumentException ignored) {
                }
            }
        } else {
            getSupportActionBar().setTitle("Add Task");
            task = new Task();
            attachments = new ArrayList<>();
            linearLayoutCreatedModifiedTime.setVisibility(View.INVISIBLE);
            enableNotification.setEnabled(false);
        }


        taskViewModel.getCategoryList().observe(this, categoryList -> {
            this.categoryList = categoryList;
        });


//kategoerie
        imageViewMoreCategory.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a category");

            // Convert the categories list to an array
            String[] categoriesArray = new String[categoryList.size()];
            categoryList.toArray(categoriesArray);

            builder.setItems(categoriesArray, (dialog, which) -> {
                // The 'which' argument contains the index position of the selected item
                editTextCategory.setText(categoriesArray[which]);
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        attachmentsToRemove = new ArrayList<>();


        if(!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }



    public void loadTask() {
        editTextTaskTitle.setText(task.getTitle());
        checkBoxTaskStatus.setChecked(task.isCompleted());
        editTextTaskDescription.setText(task.getDescription());
        Date createDate = task.getCreateTime();
        Date updateDate = task.getModifyTime();
        linearLayoutCreatedModifiedTime.setVisibility(View.VISIBLE);
        textViewCreatedTime.setText(createDate.toString());
        textViewUpdatedTime.setText(updateDate.toString());
        if (task.getDueTime() != null) {
            editTextTaskDueDate.setText(task.getDueTime().toString());
            newDueDate = task.getDueTime();
        } else {
            enableNotification.setEnabled(false);
        }
        if (task.getCategory() != null) {
            editTextCategory.setText(task.getCategory());
        }
        if (task.isNotificationEnabled()) {
            enableNotification.setChecked(true);
        }
        attachments = task.getAttachmentsPaths();
        attachmentsRecyclerViewAdapter.setAttachments(attachments);
    }

    private void removeDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    removeDirectory(file);
                }
            }
        }
        directory.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        menu.findItem(R.id.action_delete).setVisible(editMode);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_save) {

            try {
                saveTask();
            } catch (IllegalArgumentException e) {
                return false;
            }

            Toast savedToast = Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT);
            savedToast.show();

            if (enableNotification.isChecked() && newDueDate != null) {
                scheduleNotification(task.getTitle(), newDueDate.getTime(), task.getId());
            } else {
                cancelNotification(task.getId());
            }
            finish();

            return true;
        } else if (id == R.id.action_delete) {
            Task task = (Task) getIntent().getSerializableExtra("task");
            File directory = new File(getExternalFilesDir(null), task.getCreateTime().getTime()+"");
            removeDirectory(directory);
            taskViewModel.deleteTask(task);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveTask() throws IllegalArgumentException {
        if (editTextTaskTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Title cannot be empty");
        }
        task.setTitle(editTextTaskTitle.getText().toString());
        task.setCompleted(checkBoxTaskStatus.isChecked());
        task.setDescription(editTextTaskDescription.getText().toString());
        task.setCategory(editTextCategory.getText().toString());
        task.setNotificationEnabled(enableNotification.isChecked());
        // get current date from system
        Date currentDate = new Date();
        if (task.getCreateTime() == null) {
            task.setCreateTime(currentDate);
        }
        task.setModifyTime(currentDate);

        if (newDueDate != null) {
            task.setDueTime(newDueDate);
        } else {
            task.setDueTime(null);
        }


        // todo: fix remove
        List<String> newAttachments = task.getAttachmentsPaths();
        if (attachmentsToRemove != null) {
            for (String attachment : attachmentsToRemove) {
                newAttachments.remove(attachment);
                File file = new File(getExternalFilesDir(null), task.getCreateTime().getTime()+"/"+attachment);
                if (file.exists()) {
                    file.delete();
                }
            }
        }


        task.setAttachmentsPaths(attachments);
        saveAttachmentsToExternalStorage();

        if (getIntent().getBooleanExtra("editMode", false)) {
            taskViewModel.updateTask(task);
        } else {
            taskViewModel.insertTask(task);
        }
    }

    private void scheduleNotification(String title, long dueTimeMillis, int taskId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int notificationTime = Integer.parseInt(sharedPreferences.getString("notification_time", "15"));
        long triggerTime = dueTimeMillis - (notificationTime * 60 * 1000);

        Log.d("Notification", "Scheduling notification for task ID: " + taskId +
                " at: " + new Date(triggerTime) +
                " (due: " + new Date(dueTimeMillis) + ")");

        if (triggerTime < System.currentTimeMillis()) {
            Log.w("Notification", "Notification time is in the past, skipping");
            return;
        }

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("task", task);
        notificationIntent.setAction("com.example.todo.NOTIFICATION");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void cancelNotification(int taskId) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("task", task);
        notificationIntent.setAction("com.example.todo.NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select Files"), REQUEST_CODE_SELECT_FILES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_FILES && resultCode == RESULT_OK) {
            if (data != null) {


                Uri fileUri = data.getData();

                tempAttachmentsPaths.add(data.getDataString());
                String filename = queryName(getContentResolver(), fileUri);
                attachments.add(filename);

                attachmentsRecyclerViewAdapter.setAttachments(attachments);
            }
        }
    }

    private void saveAttachmentsToExternalStorage() {
        for (String attachment : tempAttachmentsPaths) {
            Uri uri = Uri.parse(attachment);

            try {
                String filename = queryName(getContentResolver(), uri);
                // create directory for task by task create time
                File directory = new File(getExternalFilesDir(null), task.getCreateTime().getTime()+"");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                File destinationFile = new File(getExternalFilesDir(null), task.getCreateTime().getTime()+"/"+filename);
                // save file to external storage
                InputStream inputStream = getContentResolver().openInputStream(uri);
                OutputStream outputStream = new FileOutputStream(destinationFile);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                outputStream.write(buffer);
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void openFile(String path) {
        File file = new File(getExternalFilesDir(null), path);
        if (file.exists()) {
            try {
                Uri uri = MyFileProvider.getUriForFile(this, "com.example.todo.fileprovider", file);
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String mimeType = mime.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, mimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Cannot open file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttachmentClick(String attachment) {
        openFile(attachment);
    }

    @Override
    public void onRemoveAttachmentClick(String attachment) {
        attachmentsToRemove.add(attachment);
        attachments.remove(attachment);
        attachmentsRecyclerViewAdapter.setAttachments(attachments);
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Add Task");
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkBoxTaskStatus = findViewById(R.id.checboxCompleted);
        editTextTaskTitle = findViewById(R.id.editTextTitle);
        editTextTaskDueDate = findViewById(R.id.textViewDueTime);
        enableNotification = findViewById(R.id.checkboxNotification);
        editTextCategory = findViewById(R.id.editTextCategory);
        textViewAddAttachment = findViewById(R.id.textViewAttachment);
        editTextTaskDescription = findViewById(R.id.editTextDescription);
        textViewCreatedTime = findViewById(R.id.textViewCreatedOn);
        textViewUpdatedTime = findViewById(R.id.textViewUpdatedOn);
        imageViewClearCategory = findViewById(R.id.imageViewClearCategory);
        imageViewMoreCategory = findViewById(R.id.imageViewCategory);
        linearLayoutCreatedModifiedTime = findViewById(R.id.linearLayoutCreatedModifiedTime);
        imageViewClearDueDate = findViewById(R.id.imageViewClearDueTime);

        recyclerViewAttachments = findViewById(R.id.recyclerViewAttachments);
        recyclerViewAttachments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAttachments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        attachmentsRecyclerViewAdapter = new AttachmentsRecyclerViewAdapter(this);
        recyclerViewAttachments.setAdapter(attachmentsRecyclerViewAdapter);

        editTextTaskDueDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        imageViewClearDueDate.setOnClickListener(v -> {
            editTextTaskDueDate.setText("");
            newDueDate = null;
            enableNotification.setChecked(false);
            enableNotification.setEnabled(false);
        });

        imageViewClearCategory.setOnClickListener(v -> {
            editTextCategory.setText("");
        });

        textViewAddAttachment.setOnClickListener(v -> {
            openFilePicker();
        });

//        enableNotification.setOnClickListener(v -> {
//            if(!enableNotification.isEnabled() && noPermission) {
//                Toast.makeText(this, "Please grant notification permission first", Toast.LENGTH_SHORT).show();
//                enableNotification.setChecked(false);
//            } else if (!noPermission) {
//                Toast.makeText(this, "Set due date first", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                noPermission = false;
            } else {
                noPermission = true;
            }
        }
    }




    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        newDueDate = c.getTime();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(newDueDate);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        newDueDate = c.getTime();
        editTextTaskDueDate.setText(newDueDate.toString());
        if(!noPermission)
            enableNotification.setEnabled(true);
    }

//wybor daty
    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), (AddEditTaskActivity) requireActivity(), year, month, day);
        }

    }
//wybor godziny
    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker.
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it.
            return new TimePickerDialog(requireContext(), (AddEditTaskActivity) requireActivity(), hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

    }


}