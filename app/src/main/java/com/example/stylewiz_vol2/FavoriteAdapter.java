package com.example.stylewiz_vol2;

import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<FavoriteSuggestion> favorites;

    public FavoriteAdapter(List<FavoriteSuggestion> favorites) {
        this.favorites = favorites;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_suggestion, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteSuggestion suggestion = favorites.get(position);

        holder.description.setText(suggestion.getDescription());
        holder.imagesContainer.removeAllViews();

        List<FavoriteImageItem> images = suggestion.getImages();

        if (images == null || images.isEmpty()) {
            return;
        }

        // Desired order
        List<String> desiredOrder = Arrays.asList("accessory", "outerwear", "one-piece", "top", "bottom", "shoes");

        // Sort by category order (null safe)
        Collections.sort(images, (i1, i2) -> {
            String cat1 = i1.getCategory() != null ? i1.getCategory().toLowerCase() : "";
            String cat2 = i2.getCategory() != null ? i2.getCategory().toLowerCase() : "";

            int index1 = desiredOrder.indexOf(cat1);
            int index2 = desiredOrder.indexOf(cat2);

            if (index1 == -1) index1 = desiredOrder.size();
            if (index2 == -1) index2 = desiredOrder.size();
            return Integer.compare(index1, index2);
        });

        // Add ImageViews
        for (FavoriteImageItem imageItem : images) {
            addImageView(holder, imageItem.getUrl());
        }
    }

    private void addImageView(@NonNull FavoriteViewHolder holder, @NonNull String imageUrl) {
        ImageView imageView = new ImageView(holder.imagesContainer.getContext());

        int sizeInDp = 70;
        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDp,
                holder.imagesContainer.getResources().getDisplayMetrics()
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(4, 4, 4, 4);

        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(holder.imagesContainer.getContext())
                .load(imageUrl)
                .into(imageView);

        holder.imagesContainer.addView(imageView);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        LinearLayout imagesContainer;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.favoriteDescription);
            imagesContainer = itemView.findViewById(R.id.favoriteImagesContainer);
        }
    }
}
