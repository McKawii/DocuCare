package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.model.KnowledgeCategory;

public class KnowledgeAdapter extends ListAdapter<KnowledgeCategory, KnowledgeAdapter.KnowledgeViewHolder> {

    private KnowledgeCategoryClickInterface clickInterface;

    public KnowledgeAdapter(KnowledgeCategoryClickInterface clickInterface) {
        super(DIFF_CALLBACK);
        this.clickInterface = clickInterface;
    }

    private static final DiffUtil.ItemCallback<KnowledgeCategory> DIFF_CALLBACK = new DiffUtil.ItemCallback<KnowledgeCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull KnowledgeCategory oldItem, @NonNull KnowledgeCategory newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull KnowledgeCategory oldItem, @NonNull KnowledgeCategory newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) && 
                   oldItem.getArticleCount() == newItem.getArticleCount();
        }
    };

    @NonNull
    @Override
    public KnowledgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_knowledge_category, parent, false);
        return new KnowledgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KnowledgeViewHolder holder, int position) {
        KnowledgeCategory category = getItem(position);
        
        holder.titleText.setText(category.getTitle());
        
        if (category.isExternalLink()) {
            holder.articleCountText.setText("Link zewnętrzny");
        } else {
            String countText = category.getArticleCount() == 1 ? "artykuł" : 
                              (category.getArticleCount() >= 2 && category.getArticleCount() <= 4 ? "artykuły" : "artykułów");
            holder.articleCountText.setText(category.getArticleCount() + " " + countText);
        }
        
        holder.iconView.setImageResource(category.getIconResId());
        holder.iconView.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), category.getColorResId()));
        
        holder.itemView.setOnClickListener(v -> {
            if (clickInterface != null) {
                clickInterface.onCategoryClick(category);
            }
        });
    }

    public static class KnowledgeViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView iconView;
        private final TextView titleText;
        private final TextView articleCountText;

        public KnowledgeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.categoryCard);
            iconView = itemView.findViewById(R.id.categoryIcon);
            titleText = itemView.findViewById(R.id.categoryTitle);
            articleCountText = itemView.findViewById(R.id.articleCount);
        }
    }
}

