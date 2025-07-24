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

        List<String> imageUrls = suggestion.getImageUrls();
        List<String> categories = suggestion.getCategories();

        List<String> desiredOrder = Arrays.asList(
                "accessory", "outerwear", "one-piece", "top", "bottom", "shoes"
        );

        if (imageUrls == null || imageUrls.isEmpty()) {
            return; // No images to show
        }

        List<Pair<String, String>> pairedList = new ArrayList<>();

        if (categories != null && categories.size() == imageUrls.size()) {
            // ✅ Pair each URL with its category
            for (int i = 0; i < imageUrls.size(); i++) {
                String category = categories.get(i) != null ? categories.get(i).toLowerCase().trim() : "";
                pairedList.add(new Pair<>(imageUrls.get(i), category));
            }

            // ✅ Sort by desired category order
            Collections.sort(pairedList, (p1, p2) -> {
                int index1 = desiredOrder.indexOf(p1.second);
                int index2 = desiredOrder.indexOf(p2.second);
                if (index1 == -1) index1 = desiredOrder.size();
                if (index2 == -1) index2 = desiredOrder.size();
                return Integer.compare(index1, index2);
            });
        } else {
            // 🔴 Fallback: categories missing or mismatched
            for (String url : imageUrls) {
                pairedList.add(new Pair<>(url, "")); // empty category
            }
        }

        // ✅ Populate images in sorted order
        for (Pair<String, String> pair : pairedList) {
            addImageView(holder, pair.first);
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
