package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.model.Badanie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BadanieAdapter extends ListAdapter<Badanie, BadanieAdapter.BadanieViewHolder> {

    private final BadanieClickInterface badanieClickInterface;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public BadanieAdapter(BadanieClickInterface badanieClickInterface) {
        super(DIFF_CALLBACK);
        this.badanieClickInterface = badanieClickInterface;
    }

    private static final DiffUtil.ItemCallback<Badanie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Badanie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Badanie oldItem, @NonNull Badanie newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Badanie oldItem, @NonNull Badanie newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public BadanieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.badanie_item, parent, false);
        return new BadanieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadanieViewHolder holder, int position) {
        Badanie currentBadanie = getItem(position);
        
        // Set examination name
        holder.badanieNazwa.setText(currentBadanie.getNazwa());
        
        // Set last examination date
        if (currentBadanie.getDataOstatniegoBadania() != null) {
            String dateStr = dateFormat.format(currentBadanie.getDataOstatniegoBadania());
            holder.dataOstatniegoBadania.setText("Ostatnie: " + dateStr);
        } else {
            holder.dataOstatniegoBadania.setText("Ostatnie: -");
        }
        
        // Calculate status and validity
        long dniDoWygasniecia = currentBadanie.getDniDoWygasniecia();
        boolean isWazne = currentBadanie.isWazne();
        
        // Set status badge
        if (isWazne) {
            holder.statusBadge.setText("Brak akcji");
            holder.statusBadge.setBackgroundResource(R.drawable.status_badge_green);
        } else {
            holder.statusBadge.setText("Po terminie");
            holder.statusBadge.setBackgroundResource(R.drawable.status_badge_red);
        }
        
        // Set validity text and color
        if (dniDoWygasniecia > 0) {
            holder.waznoscText.setText(Math.abs(dniDoWygasniecia) + " dni");
            holder.waznoscText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_green));
            holder.statusDot.setBackgroundResource(R.drawable.status_dot_green);
        } else if (dniDoWygasniecia == 0) {
            holder.waznoscText.setText("Dziś");
            holder.waznoscText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_orange));
            holder.statusDot.setBackgroundResource(R.drawable.status_dot_orange);
        } else {
            holder.waznoscText.setText(Math.abs(dniDoWygasniecia) + " dni po terminie");
            holder.waznoscText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_red));
            holder.statusDot.setBackgroundResource(R.drawable.status_dot_red);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (badanieClickInterface != null) {
                badanieClickInterface.onBadanieClick(currentBadanie);
            }
        });
    }

    public static class BadanieViewHolder extends RecyclerView.ViewHolder {
        private final TextView badanieNazwa;
        private final TextView dataOstatniegoBadania;
        private final TextView statusBadge;
        private final TextView waznoscText;
        private final View statusDot;

        public BadanieViewHolder(@NonNull View itemView) {
            super(itemView);
            badanieNazwa = itemView.findViewById(R.id.badanieNazwa);
            dataOstatniegoBadania = itemView.findViewById(R.id.dataOstatniegoBadania);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            waznoscText = itemView.findViewById(R.id.waznoscText);
            statusDot = itemView.findViewById(R.id.statusDot);
        }
    }
}

