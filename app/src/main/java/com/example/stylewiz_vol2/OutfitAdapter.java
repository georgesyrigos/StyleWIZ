package com.example.stylewiz_vol2;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    private Context context;
    private List<OutfitItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OutfitItem outfitItem);
    }

    public OutfitAdapter(Context context, List<OutfitItem> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clothing, parent, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        OutfitItem outfitItem = itemList.get(position);

        String documentId = outfitItem.getDocumentId(); // Get the Id of the selected item

        Glide.with(context).load(outfitItem.getPhotoUrl()).into(holder.recImage);

        holder.recCategory.setText(outfitItem.getCategory());
        holder.recStyleTag.setText(outfitItem.getStyleTag());
        holder.recSeasonality.setText(outfitItem.getSeason());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(outfitItem));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class OutfitViewHolder extends RecyclerView.ViewHolder {
        ImageView recImage;
        TextView recCategory, recStyleTag, recSeasonality;
        CardView recCard;

        public OutfitViewHolder(@NonNull View itemView) {
            super(itemView);
            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recCategory = itemView.findViewById(R.id.recCategory);
            recStyleTag = itemView.findViewById(R.id.recStyleTag);
            recSeasonality = itemView.findViewById(R.id.recSeasonality);
        }
    }
}
