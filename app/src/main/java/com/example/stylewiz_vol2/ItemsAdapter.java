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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private Context context;
    private List<DataClass> dataList;

    public ItemsAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
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
        //holder.recCategory.setText(dataList.get(position).getCategory());
        //holder.recStyleTag.setText(dataList.get(position).getStyleTag());
        //holder.recSeasonality.setText(dataList.get(position).getSeason());

        holder.recCategory.setText("Category: " + currentData.getCategory());
        holder.recStyleTag.setText("Style Tag: " + currentData.getStyleTag());
        holder.recSeasonality.setText("Seasonality: " + currentData.getSeason());

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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}


class ItemViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recCategory, recStyleTag, recColor, recSeasonality, recDescription;
    CardView recCard;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recCategory = itemView.findViewById(R.id.recCategory);
        recStyleTag = itemView.findViewById(R.id.recStyleTag);
        //recColor = itemView.findViewById(R.id.recColor);
        recSeasonality = itemView.findViewById(R.id.recSeasonality);
        //recDescription = itemView.findViewById(R.id.recDescription);



    }
}