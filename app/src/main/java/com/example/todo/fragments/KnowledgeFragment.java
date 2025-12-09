package com.example.todo.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.KnowledgeAdapter;
import com.example.todo.KnowledgeCategoryClickInterface;
import com.example.todo.R;
import com.example.todo.model.KnowledgeCategory;
import com.example.todo.repository.KnowledgeRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeFragment extends Fragment implements KnowledgeCategoryClickInterface {

    private RecyclerView recyclerViewCategories;
    private KnowledgeAdapter adapter;
    private TextInputEditText searchEditText;
    private KnowledgeRepository repository;
    private List<KnowledgeCategory> allCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = KnowledgeRepository.getInstance();
        allCategories = repository.getAllCategories();

        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new KnowledgeAdapter(this);
        recyclerViewCategories.setAdapter(adapter);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        adapter.submitList(allCategories);
    }

    private void filterCategories(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.submitList(allCategories);
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<KnowledgeCategory> filtered = new ArrayList<>();
        for (KnowledgeCategory category : allCategories) {
            if (category.getTitle().toLowerCase().contains(lowerQuery)) {
                filtered.add(category);
            }
        }
        adapter.submitList(filtered);
    }

    @Override
    public void onCategoryClick(KnowledgeCategory category) {
        // Otwórz szczegóły artykułu
        KnowledgeDetailFragment detailFragment = KnowledgeDetailFragment.newInstance(category.getId());
        
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
