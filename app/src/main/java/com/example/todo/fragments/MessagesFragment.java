package com.example.todo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.adapters.MessageAdapter;
import com.example.todo.models.ExamMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment implements MessageAdapter.OnMessageDoubleClickListener {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private MessageAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public MessagesFragment() {
        // wymagany pusty konstruktor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.messagesRecyclerView);
        emptyText = view.findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MessageAdapter(this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMessages();
    }

    private void loadMessages() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Brak zalogowanego użytkownika", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference messagesRef = db.collection("clients")
                .document(currentUser.getUid())
                .collection("messages");

        messagesRef
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(requireContext(),
                                    "Błąd wczytywania komunikatów", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value == null || value.isEmpty()) {
                            adapter.setItems(new ArrayList<>());
                            emptyText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            return;
                        }

                        List<ExamMessage> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            ExamMessage msg = doc.toObject(ExamMessage.class);
                            msg.setId(doc.getId());
                            list.add(msg);
                        }

                        adapter.setItems(list);
                        emptyText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onMessageDoubleClick(ExamMessage message) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openCalendarForExamId(message.getExamId());
        } else {
            Toast.makeText(requireContext(),
                    "Nie udało się otworzyć kalendarza", Toast.LENGTH_SHORT).show();
        }
    }
}
