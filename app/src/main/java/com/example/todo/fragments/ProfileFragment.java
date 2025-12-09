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

public class ProfileFragment extends Fragment {

    private TextView userNameText;
    private TextView userEmailText;
    private Switch pushNotificationsSwitch;
    private Switch emailNotificationsSwitch;
    private Switch examRemindersSwitch;
    private Switch patientEntersDatesSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userNameText = view.findViewById(R.id.userNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        pushNotificationsSwitch = view.findViewById(R.id.pushNotificationsSwitch);
        emailNotificationsSwitch = view.findViewById(R.id.emailNotificationsSwitch);
        examRemindersSwitch = view.findViewById(R.id.examRemindersSwitch);
        patientEntersDatesSwitch = view.findViewById(R.id.patientEntersDatesSwitch);

        // Ustaw dane użytkownika - dynamicznie z Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            // Jeśli nie ma displayName, użyj emaila bez domeny jako fallback
            if (displayName == null || displayName.isEmpty()) {
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    displayName = email.split("@")[0];
                    // Kapitalizuj pierwszą literę
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
        }

        // Przycisk Wyloguj się
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> logout());

        // Przyciski w sekcji Prywatność i bezpieczeństwo
        view.findViewById(R.id.gdprConsentsButton).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Zgody RODO - funkcja w przygotowaniu", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.privacyPolicyButton).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Polityka prywatności - funkcja w przygotowaniu", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.termsButton).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Regulamin aplikacji - funkcja w przygotowaniu", Toast.LENGTH_SHORT).show();
        });

        // Przycisk Historia działań
        view.findViewById(R.id.activityHistoryButton).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Historia działań - funkcja w przygotowaniu", Toast.LENGTH_SHORT).show();
        });
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

