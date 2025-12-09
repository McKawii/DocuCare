package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessagesAdapter extends ListAdapter<Message, MessagesAdapter.MessageViewHolder> {

    private MessageClickInterface clickInterface;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());

    public MessagesAdapter(MessageClickInterface clickInterface) {
        super(DIFF_CALLBACK);
        this.clickInterface = clickInterface;
    }

    private static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = getItem(position);
        
        holder.titleText.setText(message.getTitle());
        holder.descriptionText.setText(message.getDescription());
        
        if (message.getDate() != null) {
            holder.dateText.setText(dateFormat.format(message.getDate()));
        }
        
        // Ustaw kolor badge w zależności od kategorii
        int badgeColor;
        switch (message.getCategory()) {
            case "vaccinations":
                badgeColor = R.color.info_blue;
                break;
            case "examinations":
                badgeColor = R.color.primary_burgundy;
                break;
            case "appointments":
                badgeColor = R.color.status_orange;
                break;
            default:
                badgeColor = R.color.text_secondary;
        }
        holder.categoryBadge.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), badgeColor));
        holder.categoryBadge.setText(getCategoryLabel(message.getCategory()));
        
        // Czerwona kropka dla nieprzeczytanych
        holder.unreadDot.setVisibility(message.isRead() ? View.GONE : View.VISIBLE);
        
        holder.itemView.setOnClickListener(v -> {
            if (clickInterface != null) {
                clickInterface.onMessageClick(message);
            }
        });
    }

    private String getCategoryLabel(String category) {
        switch (category) {
            case "vaccinations":
                return "Szczepienia";
            case "examinations":
                return "Badania";
            case "appointments":
                return "Terminy";
            default:
                return "Ogólne";
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView dateText;
        private final TextView categoryBadge;
        private final View unreadDot;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.messageCard);
            titleText = itemView.findViewById(R.id.messageTitle);
            descriptionText = itemView.findViewById(R.id.messageDescription);
            dateText = itemView.findViewById(R.id.messageDate);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
            unreadDot = itemView.findViewById(R.id.unreadDot);
        }
    }
}

