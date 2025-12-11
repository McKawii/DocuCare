package com.example.todo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todo.LoginActivity;
import com.example.todo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView userNameText;
    private TextView userEmailText;
    private TextView userBirthDateText;
    private TextView userGenderText;

    private Switch pushNotificationsSwitch;
    private Switch emailNotificationsSwitch;
    private Switch examRemindersSwitch;
    private Switch patientEntersDatesSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userNameText = view.findViewById(R.id.userNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        userBirthDateText = view.findViewById(R.id.userBirthDateText);
        userGenderText = view.findViewById(R.id.userGenderText);

        pushNotificationsSwitch = view.findViewById(R.id.pushNotificationsSwitch);
        emailNotificationsSwitch = view.findViewById(R.id.emailNotificationsSwitch);
        examRemindersSwitch = view.findViewById(R.id.examRemindersSwitch);
        patientEntersDatesSwitch = view.findViewById(R.id.patientEntersDatesSwitch);

        // Dane użytkownika z Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(this::applyUserData)
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                getContext(),
                                "Błąd pobierania danych użytkownika",
                                Toast.LENGTH_SHORT
                        ).show();
                        applyAuthFallback(user);
                    });
        }

        // Przycisk Wyloguj się
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> logout());

        // Przyciski w sekcji Prywatność i bezpieczeństwo
        view.findViewById(R.id.gdprConsentsButton).setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Zgody RODO - funkcja w przygotowaniu",
                        Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.privacyPolicyButton).setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Polityka prywatności - funkcja w przygotowaniu",
                        Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.termsButton).setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Regulamin aplikacji - funkcja w przygotowaniu",
                        Toast.LENGTH_SHORT).show());

        // Przycisk Historia działań
        view.findViewById(R.id.activityHistoryButton).setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Historia działań - funkcja w przygotowaniu",
                        Toast.LENGTH_SHORT).show());
    }

    private void applyUserData(DocumentSnapshot doc) {
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        if (authUser == null) return;

        String displayName = null;
        String emailToShow = null;
        String birthDateToShow = null;
        String genderToShow = null;

        if (doc != null && doc.exists()) {
            String fullName = doc.getString("fullName");
            String firstName = doc.getString("firstName");
            String lastName = doc.getString("lastName");
            String emailFromDb = doc.getString("email");
            String birthDate = doc.getString("birthDate");
            Long ageLong = doc.getLong("age");
            String gender = doc.getString("gender");

            // Imię + nazwisko / fullName
            if (fullName != null && !fullName.isEmpty()) {
                displayName = fullName;
            } else if (firstName != null && !firstName.isEmpty()) {
                if (lastName != null && !lastName.isEmpty()) {
                    displayName = firstName + " " + lastName;
                } else {
                    displayName = firstName;
                }
            }

            if (emailFromDb != null && !emailFromDb.isEmpty()) {
                emailToShow = emailFromDb;
            }

            if (birthDate != null && ageLong != null) {
                birthDateToShow = birthDate + " (" + ageLong + " lat)";
            } else if (birthDate != null) {
                birthDateToShow = birthDate;
            }

            if (gender != null && !gender.isEmpty()) {
                genderToShow = gender;
            }
        }

        // Fallback jeśli czegoś nie ma w dokumencie
        if (displayName == null || displayName.isEmpty()
                || emailToShow == null || emailToShow.isEmpty()) {
            applyAuthFallback(authUser);
        } else {
            userNameText.setText(displayName);
            userEmailText.setText(emailToShow);
        }

        if (birthDateToShow != null) {
            userBirthDateText.setText(birthDateToShow);
        }

        if (genderToShow != null) {
            userGenderText.setText(genderToShow);
        }
    }

    /** Fallback: użyj danych z Firebase Auth (tylko nagłówek) */
    private void applyAuthFallback(FirebaseUser user) {
        String displayName = user.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                displayName = email.split("@")[0];
                if (displayName.length() > 0) {
                    displayName = displayName.substring(0, 1).toUpperCase() +
                            (displayName.length() > 1 ? displayName.substring(1) : "");
                }
            } else {
                displayName = "Użytkownik";
            }
        }
        userNameText.setText(displayName);
        userEmailText.setText(user.getEmail());
        // birthDate / gender zostają takie, jak w layoucie, jeśli nie ma ich w bazie
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
