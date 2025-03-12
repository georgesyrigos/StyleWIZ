package com.example.stylewiz_vol2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

        ShowOutfitsItem currentData = outfitList.get(position);
        //new addition
        String documentId = currentData.getDocumentId(); // Ensure your model class has this method

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imgTopLeft = convertView.findViewById(R.id.imgTopLeft);
            holder.imgTopRight = convertView.findViewById(R.id.imgTopRight);
            holder.imgBottomLeft = convertView.findViewById(R.id.imgBottomLeft);
            holder.imgBottomRight = convertView.findViewById(R.id.imgBottomRight);
            holder.gridCard = convertView.findViewById(R.id.gridCard);
            holder.caption = convertView.findViewById(R.id.gridCaption);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShowOutfitsItem outfit = outfitList.get(position);

        // Collect available images in a list
        List<String> images = new ArrayList<>();


        /*if (outfit.getOuterwear() != null && !outfit.getOuterwear().equals("none")) images.add(outfit.getOuterwear());
        if (outfit.getTop1() != null && !outfit.getTop1().equals("none")) images.add(outfit.getTop1());
        if (outfit.getTop2() != null && !outfit.getTop2().equals("none")) images.add(outfit.getTop2());
        if (outfit.getBottom() != null && !outfit.getBottom().equals("none")) images.add(outfit.getBottom());
        if (outfit.getOnePiece() != null && !outfit.getOnePiece().equals("none")) images.add(outfit.getOnePiece());
        if (outfit.getLayerOnePiece() != null && !outfit.getLayerOnePiece().equals("none")) images.add(outfit.getLayerOnePiece());
        if (outfit.getShoes() != null && !outfit.getShoes().equals("none")) images.add(outfit.getShoes());
        if (outfit.getAccessory1() != null && !outfit.getAccessory1().equals("none")) images.add(outfit.getAccessory1());
        if (outfit.getAccessory2() != null && !outfit.getAccessory2().equals("none")) images.add(outfit.getAccessory2());*/

        if (outfit.getOuterwear() != null && !outfit.getOuterwear().equals("none")) {
            images.add(outfit.getOuterwear());
        } else {
            images.add("none");
        }

        if (outfit.getTop1() != null && !outfit.getTop1().equals("none")) {
            images.add(outfit.getTop1());
        } else {
            images.add("none");
        }

        if (outfit.getTop2() != null && !outfit.getTop2().equals("none")) {
            images.add(outfit.getTop2());
        } else {
            images.add("none");
        }

        if (outfit.getBottom() != null && !outfit.getBottom().equals("none")) {
            images.add(outfit.getBottom());
        } else {
            images.add("none");
        }

        if (outfit.getOnePiece() != null && !outfit.getOnePiece().equals("none")) {
            images.add(outfit.getOnePiece());
        } else {
            images.add("none");
        }

        if (outfit.getLayerOnePiece() != null && !outfit.getLayerOnePiece().equals("none")) {
            images.add(outfit.getLayerOnePiece());
        } else {
            images.add("none");
        }

        if (outfit.getShoes() != null && !outfit.getShoes().equals("none")) {
            images.add(outfit.getShoes());
        } else {
            images.add("none");
        }

        if (outfit.getAccessory1() != null && !outfit.getAccessory1().equals("none")) {
            images.add(outfit.getAccessory1());
        } else {
            images.add("none");
        }

        if (outfit.getAccessory2() != null && !outfit.getAccessory2().equals("none")) {
            images.add(outfit.getAccessory2());
        } else {
            images.add("none");
        }


        // Set images dynamically, ensuring the first three positions are filled
        /*if (!images.isEmpty()){
            Glide.with(context).load(images.get(0)).into(holder.imgTopLeft);
        } else{
            holder.imgTopLeft.setVisibility(View.INVISIBLE); // Hide if no image
        }

        if (images.size() > 1) Glide.with(context).load(images.get(1)).into(holder.imgTopRight);
        else holder.imgTopRight.setVisibility(View.INVISIBLE); // Hide if no image

        if (images.size() > 2) Glide.with(context).load(images.get(2)).into(holder.imgBottomLeft);
        else holder.imgBottomLeft.setVisibility(View.INVISIBLE); // Hide if no image

        if (images.size() > 3) Glide.with(context).load(images.get(3)).into(holder.imgBottomRight);*/

        // Collect available images in a list (excluding "none")
        List<String> validImages = new ArrayList<>();
        for (String image : images) {
            if (!image.equals("none")) {
                validImages.add(image);
            }
        }


        // Assign images dynamically, only if available
        if (validImages.size() > 0) {
            Glide.with(context).load(validImages.get(0)).into(holder.imgTopLeft);
            holder.imgTopLeft.setVisibility(View.VISIBLE);
        }

        if (validImages.size() > 1) {
            Glide.with(context).load(validImages.get(1)).into(holder.imgTopRight);
            holder.imgTopRight.setVisibility(View.VISIBLE);
        }

        if (validImages.size() > 2) {
            Glide.with(context).load(validImages.get(2)).into(holder.imgBottomLeft);
            holder.imgBottomLeft.setVisibility(View.VISIBLE);
        }

        if (validImages.size() > 3) {
            Glide.with(context).load(validImages.get(3)).into(holder.imgBottomRight);
            holder.imgBottomRight.setVisibility(View.VISIBLE);
        }


        holder.caption.setText("Outfit " + (position + 1));



        //Switch to SelectedOutfit
        holder.gridCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedOutfitFragment selectedOutfitFragment = new SelectedOutfitFragment();

                // Log the selected images array
                Log.d("SelectedOutfit", "Selected images: " + images.toString());

                Bundle bundleOutfit = new Bundle();
                bundleOutfit.putStringArrayList("selected_images", (ArrayList<String>) images);
                selectedOutfitFragment.setArguments(bundleOutfit);

                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Hide current fragment if visible
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

                // Replace with SelectedOutfitFragment
                transaction.add(R.id.frameLayout, selectedOutfitFragment, "SELECTED_OUTFIT")
                        .addToBackStack("SELECTED_OUTFIT")
                        .commit();
            }
        });


        return convertView;
    }

    private static class ViewHolder {
        ImageView imgTopLeft, imgTopRight, imgBottomLeft, imgBottomRight;
        CardView gridCard;
        TextView caption;
    }
}

