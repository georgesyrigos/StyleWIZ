package com.example.stylewiz_vol2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ShowOutfitAdapter extends BaseAdapter {
    private Context context;
    private List<ShowOutfitsItem> outfitList;

    public ShowOutfitAdapter(Context context, List<ShowOutfitsItem> outfitList) {
        this.context = context;
        this.outfitList = outfitList;
    }

    @Override
    public int getCount() {
        return outfitList.size();
    }

    @Override
    public Object getItem(int position) {
        return outfitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imgTopLeft = convertView.findViewById(R.id.imgTopLeft);
            holder.imgTopRight = convertView.findViewById(R.id.imgTopRight);
            holder.imgBottomLeft = convertView.findViewById(R.id.imgBottomLeft);
            holder.imgBottomRight = convertView.findViewById(R.id.imgBottomRight);
            holder.caption = convertView.findViewById(R.id.gridCaption);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShowOutfitsItem outfit = outfitList.get(position);

        // Collect available images in a list
        List<String> images = new ArrayList<>();



        if (outfit.getTop1() != null && !outfit.getTop1().equals("none")) images.add(outfit.getTop1());
        if (outfit.getOnePiece() != null && !outfit.getOnePiece().equals("none")) images.add(outfit.getOnePiece());
        if (outfit.getLayerOnePiece() != null && !outfit.getLayerOnePiece().equals("none")) images.add(outfit.getLayerOnePiece());
        if (outfit.getTop2() != null && !outfit.getTop2().equals("none")) images.add(outfit.getTop2());
        if (outfit.getBottom() != null && !outfit.getBottom().equals("none")) images.add(outfit.getBottom());
        if (outfit.getOuterwear() != null && !outfit.getOuterwear().equals("none")) images.add(outfit.getOuterwear());
        if (outfit.getShoes() != null && !outfit.getShoes().equals("none")) images.add(outfit.getShoes());
        if (outfit.getAccessory1() != null && !outfit.getAccessory1().equals("none")) images.add(outfit.getAccessory1());
        if (outfit.getAccessory2() != null && !outfit.getAccessory2().equals("none")) images.add(outfit.getAccessory2());

        // Set images dynamically, ensuring the first three positions are filled
        if (!images.isEmpty()){
            Glide.with(context).load(images.get(0)).into(holder.imgTopLeft);
        } else{
            holder.imgTopLeft.setVisibility(View.INVISIBLE); // Hide if no image
        }

        if (images.size() > 1) Glide.with(context).load(images.get(1)).into(holder.imgTopRight);
        else holder.imgTopRight.setVisibility(View.INVISIBLE); // Hide if no image

        if (images.size() > 2) Glide.with(context).load(images.get(2)).into(holder.imgBottomLeft);
        else holder.imgBottomLeft.setVisibility(View.INVISIBLE); // Hide if no image

        if (images.size() > 3) Glide.with(context).load(images.get(3)).into(holder.imgBottomRight);

        holder.caption.setText("Outfit " + (position + 1));

        return convertView;
    }

    private static class ViewHolder {
        ImageView imgTopLeft, imgTopRight, imgBottomLeft, imgBottomRight;
        TextView caption;
    }
}

