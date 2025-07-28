package com.example.stylewiz_vol2;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private List<OutfitSuggestion> suggestions;
    private String selectedOutfitHash = null; // Tracks currently selected

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

        List<ImageItem> images = suggestion.getImages();
        holder.imagesContainer.removeAllViews();


        // Define desired category order
        List<String> desiredOrder = Arrays.asList(
                "accessory", "outerwear", "one-piece", "top", "bottom", "shoes"
        );

        // Sort images by category according to desiredOrder
        Collections.sort(images, (img1, img2) -> {
            String cat1 = img1.getCategory() != null ? img1.getCategory().toLowerCase() : "";
            String cat2 = img2.getCategory() != null ? img2.getCategory().toLowerCase() : "";

            int index1 = desiredOrder.indexOf(cat1);
            int index2 = desiredOrder.indexOf(cat2);


            // If category not found, put at end
            if (index1 == -1) index1 = desiredOrder.size();
            if (index2 == -1) index2 = desiredOrder.size();

            return Integer.compare(index1, index2);
        });

        // Build outfit hash for this item
        List<String> imageUrls = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        for (ImageItem img : images) {
            imageUrls.add(img.getUrl());
            categories.add(img.getCategory());
        }

        List<String> sortedUrlsForHash = new ArrayList<>(imageUrls);
        Collections.sort(sortedUrlsForHash);
        String currentHash = TextUtils.join("_", sortedUrlsForHash);

        // Populate images dynamically
        holder.imagesContainer.setWeightSum(images.size());
        for (ImageItem imageItem : images) {
            ImageView imageView = new ImageView(holder.imagesContainer.getContext());
            LinearLayout.LayoutParams params;

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
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(holder.imagesContainer.getContext())
                    .load(imageItem.getUrl())
                    .into(imageView);

            holder.imagesContainer.addView(imageView);
        }

        // Update button state based on selectedOutfitHash
        if (currentHash.equals(selectedOutfitHash)) {
            holder.selectButton.setText("Selected");
            holder.selectButton.setEnabled(false);
            holder.selectButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.gray)
            ));
        } else {
            holder.selectButton.setText("Select");
            holder.selectButton.setEnabled(true);
            holder.selectButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.blue)
            ));
        }

        // Select button click listener
        holder.selectButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                db.collection("users")
                        .document(user.getUid())
                        .collection("selectedOutfits")
                        .whereEqualTo("outfitHash", currentHash)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(holder.itemView.getContext(), "This outfit is already saved!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Build images list
                                List<Map<String, String>> imagesList = new ArrayList<>();
                                for (int i = 0; i < imageUrls.size(); i++) {
                                    Map<String, String> imageItem = new HashMap<>();
                                    imageItem.put("category", categories.get(i).toLowerCase().trim());
                                    imageItem.put("url", imageUrls.get(i));
                                    imagesList.add(imageItem);
                                }

                                // Create outfit data
                                Map<String, Object> outfitData = new HashMap<>();
                                outfitData.put("description", suggestion.getDescription());
                                outfitData.put("timestamp", FieldValue.serverTimestamp());
                                outfitData.put("images", imagesList);
                                outfitData.put("outfitHash", currentHash);

                                db.collection("users")
                                        .document(user.getUid())
                                        .collection("selectedOutfits")
                                        .add(outfitData)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(holder.itemView.getContext(), "Outfit saved!", Toast.LENGTH_SHORT).show();

                                            // Update selected hash and dataset efficiently
                                            selectedOutfitHash = currentHash;

                                            int previousSize = suggestions.size();
                                            suggestions.clear();
                                            suggestions.add(suggestion);

                                            notifyItemRangeRemoved(0, previousSize);
                                            notifyItemInserted(0);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(holder.itemView.getContext(), "Failed to save outfit", Toast.LENGTH_SHORT).show();
                                            Log.e("FirestoreSave", "Error saving outfit", e);
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Error checking existing outfits", Toast.LENGTH_SHORT).show();
                            Log.e("FirestoreQuery", "Error querying for duplicates", e);
                        });
            }
        });

        holder.viewFullOutfit.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            List<ImageItem> imageItems = new ArrayList<>(suggestion.getImages());

            FullSuggestedOutfitDialog dialog = new FullSuggestedOutfitDialog(context, imageItems);
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        LinearLayout imagesContainer;
        Button selectButton;
        ImageView viewFullOutfit;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.suggestionTitle);
            description = itemView.findViewById(R.id.suggestionDescription);
            imagesContainer = itemView.findViewById(R.id.imagesContainer);
            selectButton = itemView.findViewById(R.id.selectButton);
            viewFullOutfit = itemView.findViewById(R.id.viewFullOutfit);

        }
    }

    public void updateData(List<OutfitSuggestion> newSuggestions) {
        suggestions = newSuggestions;
        notifyDataSetChanged();
    }
}
