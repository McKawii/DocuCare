package com.example.todo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private TextView welcomeText;
    private TextView qualificationStatus;
    private CardView calendarCard;
    private CardView messagesCard;
    private CardView knowledgeCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        welcomeText = view.findViewById(R.id.welcomeText);
        qualificationStatus = view.findViewById(R.id.qualificationStatus);
        calendarCard = view.findViewById(R.id.calendarCard);
        messagesCard = view.findViewById(R.id.messagesCard);
        knowledgeCard = view.findViewById(R.id.knowledgeCard);

        // Ustaw dane użytkownika - użyj pełnego imienia z Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName == null || displayName.isEmpty()) {
                // Jeśli nie ma displayName, użyj emaila (tylko część przed @)
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    String emailPart = email.split("@")[0];
                    // Kapitalizuj pierwszą literę
                    if (emailPart.length() > 0) {
                        displayName = emailPart.substring(0, 1).toUpperCase() + 
                                     (emailPart.length() > 1 ? emailPart.substring(1) : "");
                    } else {
                        displayName = "Użytkownik";
                    }
                } else {
                    displayName = "Użytkownik";
                }
            }
            // Wyświetl tylko imię (pierwszy wyraz) jeśli jest pełne imię i nazwisko
            String[] nameParts = displayName.split("\\s+");
            String firstName = nameParts.length > 0 ? nameParts[0] : displayName;
            welcomeText.setText("Witaj, " + firstName);
        }

        // Kliknięcie w kafelek Kalendarz badań -> przełącz na zakładkę Kalendarz
        calendarCard.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_calendar);
            }
        });

        // Kliknięcie w kafelek Komunikaty -> przełącz na zakładkę Komunikaty
        messagesCard.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_messages);
            }
        });

        // Kliknięcie w kafelek Baza wiedzy -> przełącz na zakładkę Wiedza
        knowledgeCard.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_knowledge);
            }
        });

        // Przycisk Panel lekarza (na razie Toast)
        view.findViewById(R.id.doctorPanelButton).setOnClickListener(v -> {
            android.widget.Toast.makeText(getContext(), "Panel lekarza - funkcja w przygotowaniu", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}

