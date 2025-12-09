package com.example.todo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MessageClickInterface;
import com.example.todo.MessagesAdapter;
import com.example.todo.R;
import com.example.todo.model.Message;
import com.example.todo.repository.MessagesRepository;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MessagesFragment extends Fragment implements MessageClickInterface {

    private RecyclerView recyclerViewMessages;
    private MessagesAdapter adapter;
    private MaterialButton filterAllButton;
    private MaterialButton filterVaccinationsButton;
    private MaterialButton filterExaminationsButton;
    private MaterialButton filterAppointmentsButton;
    private TextView subtitleText;
    private MessagesRepository repository;
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = MessagesRepository.getInstance();
        adapter = new MessagesAdapter(this);

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMessages.setAdapter(adapter);

        subtitleText = view.findViewById(R.id.subtitleText);
        filterAllButton = view.findViewById(R.id.filterAllButton);
        filterVaccinationsButton = view.findViewById(R.id.filterVaccinationsButton);
        filterExaminationsButton = view.findViewById(R.id.filterExaminationsButton);
        filterAppointmentsButton = view.findViewById(R.id.filterAppointmentsButton);

        updateUnreadCount();
        setFilter("all");

        filterAllButton.setOnClickListener(v -> setFilter("all"));
        filterVaccinationsButton.setOnClickListener(v -> setFilter("vaccinations"));
        filterExaminationsButton.setOnClickListener(v -> setFilter("examinations"));
        filterAppointmentsButton.setOnClickListener(v -> setFilter("appointments"));
    }

    private void updateUnreadCount() {
        int unreadCount = repository.getUnreadCount();
        subtitleText.setText(unreadCount + " nieprzeczytanych");
    }

    private void setFilter(String filter) {
        currentFilter = filter;

        // Update button styles
        updateButtonStyle(filterAllButton, filter.equals("all"));
        updateButtonStyle(filterVaccinationsButton, filter.equals("vaccinations"));
        updateButtonStyle(filterExaminationsButton, filter.equals("examinations"));
        updateButtonStyle(filterAppointmentsButton, filter.equals("appointments"));

        // Update list
        List<Message> messages = repository.getMessagesByCategory(filter);
        adapter.submitList(messages);
    }

    private void updateButtonStyle(MaterialButton button, boolean isActive) {
        if (isActive) {
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary_burgundy));
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_burgundy));
        }
    }

    @Override
    public void onMessageClick(Message message) {
        // Oznacz jako przeczytane
        repository.markAsRead(message.getId());
        updateUnreadCount();
        
        // Odśwież listę
        List<Message> messages = repository.getMessagesByCategory(currentFilter);
        adapter.submitList(messages);
        
        // Pokaż szczegóły (na razie Toast)
        Toast.makeText(getContext(), "Szczegóły: " + message.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
