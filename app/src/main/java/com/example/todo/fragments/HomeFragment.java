package com.example.todo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo.BadanieViewModelFactory;
import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.model.Badanie;
import com.example.todo.viewmodel.BadanieViewModel;

import java.util.List;

public class HomeFragment extends Fragment {

    // Teksty z kafelka "Kalendarz badań"
    private TextView textCalendarOk;
    private TextView textCalendarSoon;
    private TextView textCalendarUrgent;
    private TextView textCalendarOverdueBadge;

    // Karty w homepage
    private CardView calendarCard;
    private CardView messagesCard;
    private CardView knowledgeCard;

    private BadanieViewModel badanieViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Powiązania z ID z fragment_home.xml
        textCalendarOk = view.findViewById(R.id.textCalendarOk);
        textCalendarSoon = view.findViewById(R.id.textCalendarSoon);
        textCalendarUrgent = view.findViewById(R.id.textCalendarUrgent);
        textCalendarOverdueBadge = view.findViewById(R.id.textCalendarOverdueBadge);

        calendarCard = view.findViewById(R.id.calendarCard);
        messagesCard = view.findViewById(R.id.messagesCard);
        knowledgeCard = view.findViewById(R.id.knowledgeCard);

        // ----- KLIKNIĘCIA KART -----

        calendarCard.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_calendar);
            }
        });

        messagesCard.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_messages);
            }
        });

        knowledgeCard.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.nav_knowledge);
            }
        });

        // ----- OBSERWACJA BADAŃ -----

        BadanieViewModelFactory factory =
                new BadanieViewModelFactory(requireActivity().getApplication());

        badanieViewModel = new ViewModelProvider(
                requireActivity(),
                factory
        ).get(BadanieViewModel.class);

        badanieViewModel.getAllBadania()
                .observe(getViewLifecycleOwner(), this::updateBadaniaSummary);
    }

    /**
     * Liczenie OK / wkrótce / pilne / po terminie
     * na podstawie Badanie.getDniDoWygasniecia()
     */
    private void updateBadaniaSummary(List<Badanie> badania) {
        int ok = 0;
        int soon = 0;
        int urgent = 0;
        int overdue = 0;

        if (badania != null) {
            for (Badanie b : badania) {
                long dni = b.getDniDoWygasniecia();  // masz to już w BadanieAdapter

                if (dni > 30) {
                    ok++;
                } else if (dni > 7) {
                    soon++;
                } else if (dni >= 0) {
                    urgent++;
                } else {
                    overdue++;
                }
            }
        }

        if (textCalendarOk != null) {
            textCalendarOk.setText(ok + " OK");
        }
        if (textCalendarSoon != null) {
            textCalendarSoon.setText(soon + " wkrótce");
        }
        if (textCalendarUrgent != null) {
            textCalendarUrgent.setText(urgent + " pilne");
        }
        if (textCalendarOverdueBadge != null) {
            textCalendarOverdueBadge.setText(overdue + " po terminie");
        }
    }
}
