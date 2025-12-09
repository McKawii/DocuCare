package com.example.todo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.model.Badanie;
import com.example.todo.viewmodel.BadanieViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText editTextNazwa;
    private TextInputEditText editTextDataOstatniegoBadania;
    private TextInputEditText editTextOkresWaznosci;
    private TextInputEditText editTextNotatki;
    private Button saveButton;
    private Button cancelButton;
    private ImageButton backButton;
    private BadanieViewModel badanieViewModel;
    private Badanie badanie;
    private Date selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

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
        badanieViewModel = new ViewModelProvider(this, new BadanieViewModelFactory(this.getApplication())).get(BadanieViewModel.class);

        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {
            badanie = (Badanie) getIntent().getSerializableExtra("badanie");
            if (badanie != null) {
                loadBadanie();
            }
        } else {
            badanie = new Badanie();
        }
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        editTextNazwa = findViewById(R.id.editTextNazwa);
        editTextDataOstatniegoBadania = findViewById(R.id.editTextDataOstatniegoBadania);
        editTextOkresWaznosci = findViewById(R.id.editTextOkresWaznosci);
        editTextNotatki = findViewById(R.id.editTextNotatki);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Date picker
        editTextDataOstatniegoBadania.setOnClickListener(v -> showDatePicker());

        // Set default validity period
        if (editTextOkresWaznosci.getText().toString().isEmpty()) {
            editTextOkresWaznosci.setText("365");
        }

        saveButton.setOnClickListener(v -> {
            try {
                saveBadanie();
                Toast.makeText(this, "Badanie zapisane", Toast.LENGTH_SHORT).show();
                finish();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadBadanie() {
        if (badanie == null) return;

        editTextNazwa.setText(badanie.getNazwa());
        if (badanie.getDataOstatniegoBadania() != null) {
            editTextDataOstatniegoBadania.setText(dateFormat.format(badanie.getDataOstatniegoBadania()));
            selectedDate = badanie.getDataOstatniegoBadania();
        }
        editTextOkresWaznosci.setText(String.valueOf(badanie.getOkresWaznosciDni()));
        if (badanie.getNotatki() != null) {
            editTextNotatki.setText(badanie.getNotatki());
        }
    }

    private void saveBadanie() throws IllegalArgumentException {
        String nazwa = editTextNazwa.getText().toString().trim();
        if (nazwa.isEmpty()) {
            throw new IllegalArgumentException("Nazwa badania nie może być pusta");
        }

        if (selectedDate == null) {
            throw new IllegalArgumentException("Data ostatniego badania jest wymagana");
        }

        String okresStr = editTextOkresWaznosci.getText().toString().trim();
        if (okresStr.isEmpty()) {
            throw new IllegalArgumentException("Okres ważności jest wymagany");
        }

        int okresWaznosci;
        try {
            okresWaznosci = Integer.parseInt(okresStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Okres ważności musi być liczbą");
        }

        badanie.setNazwa(nazwa);
        badanie.setDataOstatniegoBadania(selectedDate);
        badanie.setOkresWaznosciDni(okresWaznosci);
        badanie.setNotatki(editTextNotatki.getText().toString().trim());

        Date currentDate = new Date();
        if (badanie.getCreateTime() == null) {
            badanie.setCreateTime(currentDate);
        }
        badanie.setModifyTime(currentDate);

        boolean editMode = getIntent().getBooleanExtra("editMode", false);
        if (editMode) {
            badanieViewModel.updateBadanie(badanie);
        } else {
            badanieViewModel.insertBadanie(badanie);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            this,
            year,
            month,
            day
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        selectedDate = calendar.getTime();
        editTextDataOstatniegoBadania.setText(dateFormat.format(selectedDate));
    }
}
