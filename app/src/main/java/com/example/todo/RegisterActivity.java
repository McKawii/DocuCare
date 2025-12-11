package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText birthDateEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText passwordConfirmEdit;
    private RadioGroup genderGroup;
    private CheckBox rodoCheck;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /** Callback do asynchronicznego generowania kodu użytkownika */
    private interface UserCodeCallback {
        void onCodeGenerated(String code);
        void onError(Exception e);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameEdit = findViewById(R.id.editTextFirstName);
        lastNameEdit = findViewById(R.id.editTextLastName);
        birthDateEdit = findViewById(R.id.editTextBirthDate);
        emailEdit = findViewById(R.id.editTextEmailRegister);
        passwordEdit = findViewById(R.id.editTextPasswordRegister);
        passwordConfirmEdit = findViewById(R.id.editTextPasswordConfirm);
        genderGroup = findViewById(R.id.radioGroupGender);
        rodoCheck = findViewById(R.id.checkBoxRodo);
        Button createAccountButton = findViewById(R.id.buttonCreateAccount);

        createAccountButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = firstNameEdit.getText().toString().trim();
        String lastName = lastNameEdit.getText().toString().trim();
        String birthDate = birthDateEdit.getText().toString().trim(); // format: dd.MM.rrrr
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String passwordConfirm = passwordConfirmEdit.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Hasła nie są takie same", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Hasło musi mieć min. 6 znaków", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedGenderId = genderGroup.getCheckedRadioButtonId();
        if (checkedGenderId == -1) {
            Toast.makeText(this, "Wybierz płeć", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!rodoCheck.isChecked()) {
            Toast.makeText(this, "Musisz zaakceptować RODO", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton checked = findViewById(checkedGenderId);
        String gender = checked.getText().toString(); // "Kobieta" / "Mężczyzna"

        int age;
        try {
            age = calculateAgeFromString(birthDate);
        } catch (Exception e) {
            Toast.makeText(this, "Niepoprawny format daty (użyj dd.MM.rrrr)", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Tworzymy konto w Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        Toast.makeText(this, "Błąd: użytkownik jest null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 2. Generujemy unikalny 8-cyfrowy kod usera
                    generateUniqueUserCode(new UserCodeCallback() {
                        @Override
                        public void onCodeGenerated(String code) {
                            // 3. Zapisujemy komplet danych profilu + userCode
                            saveUserData(user.getUid(), firstName, lastName, birthDate,
                                    age, gender, email, code);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(RegisterActivity.this,
                                    "Błąd generowania ID użytkownika: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Błąd rejestracji: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private int calculateAgeFromString(String birthDate) {
        // oczekujemy formatu dd.MM.rrrr
        String[] parts = birthDate.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("Bad date");

        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // Calendar: 0-11
        int year = Integer.parseInt(parts[2]);

        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    /** Generuje unikalny 8-cyfrowy kod i sprawdza w kolekcji "users", czy nie ma kolizji */
    private void generateUniqueUserCode(UserCodeCallback callback) {
        String code = String.format(Locale.getDefault(), "%08d",
                new Random().nextInt(100_000_000));

        db.collection("users")
                .whereEqualTo("userCode", code)
                .get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) {
                        // kod nieużyty – bierzemy
                        callback.onCodeGenerated(code);
                    } else {
                        // kolizja – generujemy jeszcze raz
                        generateUniqueUserCode(callback);
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    private void saveUserData(String uid,
                              String firstName,
                              String lastName,
                              String birthDate,
                              int age,
                              String gender,
                              String email,
                              String userCode) {

        Map<String, Object> data = new HashMap<>();
        data.put("firstName", firstName);
        data.put("lastName", lastName);
        data.put("fullName", firstName + " " + lastName);
        data.put("birthDate", birthDate);   // np. "15.03.1978"
        data.put("age", age);               // np. 46
        data.put("gender", gender);         // "Kobieta" / "Mężczyzna"
        data.put("email", email);
        data.put("rodoAccepted", true);
        data.put("userCode", userCode);     // 8-cyfrowy kod użytkownika
        data.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .set(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Konto utworzone. Twój ID: " + userCode,
                            Toast.LENGTH_LONG).show();
                    goToMain();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Błąd zapisu danych: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void goToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
