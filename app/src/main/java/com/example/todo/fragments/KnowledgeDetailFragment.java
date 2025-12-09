package com.example.todo.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.KnowledgeArticle;
import com.example.todo.repository.KnowledgeRepository;

import java.util.List;

public class KnowledgeDetailFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private KnowledgeRepository repository;
    private KnowledgeArticle article;

    public static KnowledgeDetailFragment newInstance(String categoryId) {
        KnowledgeDetailFragment fragment = new KnowledgeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = KnowledgeRepository.getInstance();
        
        String categoryId = getArguments() != null ? getArguments().getString(ARG_CATEGORY_ID) : null;
        if (categoryId != null) {
            article = repository.getArticleByCategoryId(categoryId);
            displayArticle(view);
        }

        // Przycisk powrotu
        view.findViewById(R.id.backButton).setOnClickListener(v -> {
            if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void displayArticle(View view) {
        if (article == null) return;

        TextView categoryBadge = view.findViewById(R.id.categoryBadge);
        TextView titleText = view.findViewById(R.id.articleTitle);
        TextView readTimeText = view.findViewById(R.id.readTimeText);
        TextView updateDateText = view.findViewById(R.id.updateDateText);
        TextView contentText = view.findViewById(R.id.articleContent);
        RecyclerView importantPointsList = view.findViewById(R.id.importantPointsList);
        RecyclerView usefulLinksList = view.findViewById(R.id.usefulLinksList);
        TextView disclaimerText = view.findViewById(R.id.disclaimerText);

        categoryBadge.setText(article.getCategoryBadge());
        titleText.setText(article.getTitle());
        readTimeText.setText(article.getReadTimeMinutes() + " min czytania");
        updateDateText.setText("Aktualizacja: " + article.getUpdateDate());
        contentText.setText(article.getContent());

        // Lista ważnych punktów
        if (article.getImportantPoints() != null && !article.getImportantPoints().isEmpty()) {
            importantPointsList.setVisibility(View.VISIBLE);
            ImportantPointsAdapter pointsAdapter = new ImportantPointsAdapter(article.getImportantPoints());
            importantPointsList.setLayoutManager(new LinearLayoutManager(getContext()));
            importantPointsList.setAdapter(pointsAdapter);
        } else {
            importantPointsList.setVisibility(View.GONE);
        }

        // Lista przydatnych linków
        if (article.getUsefulLinks() != null && !article.getUsefulLinks().isEmpty()) {
            usefulLinksList.setVisibility(View.VISIBLE);
            UsefulLinksAdapter linksAdapter = new UsefulLinksAdapter(article.getUsefulLinks());
            usefulLinksList.setLayoutManager(new LinearLayoutManager(getContext()));
            usefulLinksList.setAdapter(linksAdapter);
        } else {
            usefulLinksList.setVisibility(View.GONE);
        }

        // Disclaimer
        if (article.getDisclaimer() != null && !article.getDisclaimer().isEmpty()) {
            disclaimerText.setText(article.getDisclaimer());
            disclaimerText.setVisibility(View.VISIBLE);
        } else {
            disclaimerText.setVisibility(View.GONE);
        }
    }

    // Prosty adapter dla ważnych punktów
    private static class ImportantPointsAdapter extends RecyclerView.Adapter<ImportantPointsAdapter.ViewHolder> {
        private List<String> points;

        ImportantPointsAdapter(List<String> points) {
            this.points = points;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText("• " + points.get(position));
        }

        @Override
        public int getItemCount() {
            return points != null ? points.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View view) {
                super(view);
                textView = (TextView) view;
            }
        }
    }

    // Adapter dla przydatnych linków
    private static class UsefulLinksAdapter extends RecyclerView.Adapter<UsefulLinksAdapter.ViewHolder> {
        private List<KnowledgeArticle.ExternalLink> links;

        UsefulLinksAdapter(List<KnowledgeArticle.ExternalLink> links) {
            this.links = links;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_useful_link, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            KnowledgeArticle.ExternalLink link = links.get(position);
            holder.titleText.setText(link.getTitle());
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return links != null ? links.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleText;

            ViewHolder(View view) {
                super(view);
                titleText = view.findViewById(R.id.linkTitle);
            }
        }
    }
}

