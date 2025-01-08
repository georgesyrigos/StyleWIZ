package com.example.stylewiz_vol2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private Context context;
    private List<DataClass> dataList;


    public interface OnItemDeleteListener {
        void onItemDelete(String documentId);
    }

    private OnItemDeleteListener deleteListener;

    // Constructor accepting the delete listener
    public ItemsAdapter(Context context, List<DataClass> dataList, OnItemDeleteListener deleteListener) {
        this.context = context;
        this.dataList = dataList;
        this.deleteListener = deleteListener;
    }

    public synchronized void deleteItemAtPosition(int position) {
        if (position >= 0 && position < dataList.size()) {
            DataClass itemToDelete = dataList.get(position); // Get the item being deleted
            dataList.remove(position); // Remove it from the list
            notifyItemRemoved(position); // Notify RecyclerView
            notifyItemRangeChanged(position, dataList.size());

            // Notify listener if item has a document ID
            if (deleteListener != null && itemToDelete.getDocumentId() != null) {
                deleteListener.onItemDelete(itemToDelete.getDocumentId());
            }
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DataClass currentData = dataList.get(position);

        Glide.with(context).load(dataList.get(position).getImage()).into(holder.recImage);

        holder.recCategory.setText(currentData.getCategory());
        holder.recStyleTag.setText(currentData.getStyleTag());
        holder.recSeasonality.setText(currentData.getSeason());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DetailsFragment detailsFragment = new DetailsFragment();

                Bundle bundle = new Bundle();
                bundle.putString("Category", dataList.get(holder.getAdapterPosition()).getCategory());
                bundle.putString("StyleTag", dataList.get(holder.getAdapterPosition()).getStyleTag());
                bundle.putString("Color", dataList.get(holder.getAdapterPosition()).getColor());
                bundle.putString("Season", dataList.get(holder.getAdapterPosition()).getSeason());
                bundle.putString("Description", dataList.get(holder.getAdapterPosition()).getDescription());
                bundle.putString("Image", dataList.get(holder.getAdapterPosition()).getImage());
                detailsFragment.setArguments(bundle);

                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Find the currently visible fragment and hide it
                Fragment currentFragment = null;
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment.isVisible()) {
                        currentFragment = fragment;
                        break;
                    }
                }
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }

                // Add DetailsFragment or show if already added
                transaction.add(R.id.frameLayout, detailsFragment, "DETAILS")
                        .addToBackStack("DETAILS")
                        .commit();
            }
        });

        // Set the initial state of the like button based on the 'liked' field
        if (currentData.isLiked()) {
            holder.likeButton.setImageResource(R.drawable.round_favorite_24); // Filled heart
            holder.likeButton.setColorFilter(ContextCompat.getColor(context, R.color.blue));
        } else {
            holder.likeButton.setImageResource(R.drawable.round_favorite_border_24); // Empty heart
            holder.likeButton.setColorFilter(ContextCompat.getColor(context, R.color.blue));
        }

        // Get the userId from authentication and itemId from data class as document id
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String itemId = currentData.getDocumentId();


        // Handle like button click
        holder.likeButton.setOnClickListener(v -> {
            boolean newLikedState = !currentData.isLiked(); // Toggle the like state

            // Update the UI based on the new state
            if (newLikedState) {
                holder.likeButton.setImageResource(R.drawable.round_favorite_24); // Filled heart
                holder.likeButton.setColorFilter(ContextCompat.getColor(context, R.color.blue));
            } else {
                holder.likeButton.setImageResource(R.drawable.round_favorite_border_24); // Empty heart
                holder.likeButton.setColorFilter(ContextCompat.getColor(context, R.color.blue));
            }

            // Update local data
            currentData.setLiked(newLikedState);


            // Call FirestoreHelper to update the liked field in Firestore
            FirestoreHelper firestoreHelper = new FirestoreHelper();
            firestoreHelper.updateLikedField(userId, itemId, newLikedState, new FirestoreHelper.FirestoreCallback() {
                @Override
                public void onSuccess() {
                    Log.d("Firestore", "Liked field updated successfully.");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("Firestore", "Error updating liked field.", e);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public List<DataClass> getDataList() {
        return dataList;
    }


}



class ItemViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage, likeButton;
    TextView recCategory, recStyleTag, recSeasonality;
    CardView recCard;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        likeButton = itemView.findViewById(R.id.likeButton);
        recCard = itemView.findViewById(R.id.recCard);
        recCategory = itemView.findViewById(R.id.recCategory);
        recStyleTag = itemView.findViewById(R.id.recStyleTag);
        recSeasonality = itemView.findViewById(R.id.recSeasonality);

    }

}