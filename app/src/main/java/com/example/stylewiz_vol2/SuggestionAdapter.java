package com.example.stylewiz_vol2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private List<OutfitSuggestion> suggestions;

    public SuggestionAdapter(List<OutfitSuggestion> suggestions) {
        this.suggestions = suggestions;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion_card, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        OutfitSuggestion suggestion = suggestions.get(position);
        holder.title.setText(suggestion.getTitle());
        holder.description.setText(suggestion.getDescription());

        // Populate images dynamically
        holder.imagesContainer.removeAllViews();
        for (String imageUrl : suggestion.getImageUrls()) {
            ImageView imageView = new ImageView(holder.imagesContainer.getContext());
            LinearLayout.LayoutParams params;

            // Check orientation dynamically if needed
            if (holder.imagesContainer.getOrientation() == LinearLayout.VERTICAL) {
                params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1.0f
                );
            } else {
                params = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                );
            }

            params.setMargins(4, 4, 4, 4);
            imageView.setLayoutParams(params);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // If using Glide for Firestore URLs
            Glide.with(holder.imagesContainer.getContext())
                    .load(imageUrl)
                    .into(imageView);

            holder.imagesContainer.addView(imageView);
        }


    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        LinearLayout imagesContainer;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.suggestionTitle);
            description = itemView.findViewById(R.id.suggestionDescription);
            imagesContainer = itemView.findViewById(R.id.imagesContainer);
        }
    }

    public void updateData(List<OutfitSuggestion> newSuggestions) {
        suggestions = newSuggestions;
        notifyDataSetChanged();
    }
}
