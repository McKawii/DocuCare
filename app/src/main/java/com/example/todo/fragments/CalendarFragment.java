package com.example.todo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.AddEditTaskActivity;
import com.example.todo.BadanieAdapter;
import com.example.todo.BadanieClickInterface;
import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.model.Badanie;
import com.example.todo.viewmodel.BadanieViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements BadanieClickInterface {

    private RecyclerView recyclerViewBadania;
    private BadanieAdapter badanieAdapter;
    private FloatingActionButton fabAddBadanie;
    private Button filterAllButton;
    private Button filterSoonButton;
    private Button filterOverdueButton;
    private BadanieViewModel badanieViewModel;
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabAddBadanie = view.findViewById(R.id.floatingActionButton);
        filterAllButton = view.findViewById(R.id.filterAllButton);
        filterSoonButton = view.findViewById(R.id.filterSoonButton);
        filterOverdueButton = view.findViewById(R.id.filterOverdueButton);

        recyclerViewBadania = view.findViewById(R.id.recyclerViewBadania);
        recyclerViewBadania.setLayoutManager(new LinearLayoutManager(getContext()));
        badanieAdapter = new BadanieAdapter(this);
        recyclerViewBadania.setAdapter(badanieAdapter);

        badanieViewModel = new ViewModelProvider(requireActivity(), new com.example.todo.BadanieViewModelFactory(requireActivity().getApplication())).get(BadanieViewModel.class);

        // Set initial filter
        setFilter("all");

        // Observe all examinations and filter them
        badanieViewModel.getAllBadania().observe(getViewLifecycleOwner(), badania -> {
            if (badania != null) {
                List<Badanie> filtered = filterBadania(badania);
                badanieAdapter.submitList(filtered);
            }
        });

        filterAllButton.setOnClickListener(v -> setFilter("all"));
        filterSoonButton.setOnClickListener(v -> setFilter("soon"));
        filterOverdueButton.setOnClickListener(v -> setFilter("overdue"));

        fabAddBadanie.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
            startActivity(intent);
        });

        // Dodaj ItemTouchHelper do usuwania przez swipe
        setupSwipeToDelete();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                Badanie deletedBadanie = badanieAdapter.getCurrentList().get(position);
                if (deletedBadanie != null) {
                    
                    // Usuń z bazy
                    badanieViewModel.deleteBadanie(deletedBadanie);
                    
                    // Pokaż Snackbar z opcją cofnięcia
                    Snackbar snackbar = Snackbar.make(
                        recyclerViewBadania,
                        "Badanie usunięte",
                        Snackbar.LENGTH_LONG
                    );
                    snackbar.setAction("Cofnij", v -> {
                        // Przywróć badanie
                        badanieViewModel.insertBadanie(deletedBadanie);
                    });
                    snackbar.show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewBadania);
    }

    private void setFilter(String filter) {
        currentFilter = filter;

        // Update button styles
        if (filter.equals("all")) {
            filterAllButton.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.primary_burgundy));
            filterAllButton.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            filterAllButton.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white));
            filterAllButton.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_burgundy));
        }

        if (filter.equals("soon")) {
            filterSoonButton.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.primary_burgundy));
            filterSoonButton.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            filterSoonButton.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white));
            filterSoonButton.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_burgundy));
        }

        if (filter.equals("overdue")) {
            filterOverdueButton.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.primary_burgundy));
            filterOverdueButton.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            filterOverdueButton.setBackgroundTintList(androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white));
            filterOverdueButton.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_burgundy));
        }

        // Refresh list
        LiveData<List<Badanie>> allBadania = badanieViewModel.getAllBadania();
        if (allBadania.getValue() != null) {
            List<Badanie> filtered = filterBadania(allBadania.getValue());
            badanieAdapter.submitList(filtered);
        }
    }

    private List<Badanie> filterBadania(List<Badanie> badania) {
        if (badania == null) {
            return new ArrayList<>();
        }

        List<Badanie> filtered = new ArrayList<>();
        for (Badanie badanie : badania) {
            if (currentFilter.equals("all")) {
                filtered.add(badanie);
            } else if (currentFilter.equals("soon")) {
                if (badanie.isWkrotce()) {
                    filtered.add(badanie);
                }
            } else if (currentFilter.equals("overdue")) {
                if (!badanie.isWazne()) {
                    filtered.add(badanie);
                }
            }
        }
        return filtered;
    }

    @Override
    public void onBadanieClick(Badanie badanie) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("badanie", badanie);
        startActivity(intent);
    }
}

