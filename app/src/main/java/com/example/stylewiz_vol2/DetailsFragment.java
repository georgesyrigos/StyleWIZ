package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class DetailsFragment extends Fragment {

    private TextView tvTitle, detailCat, detailStyleTag, detailColor, detailSeason, detailDesc;
    private ImageView detailImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        //get the details from the adapter
        Bundle bundle = getArguments();
        //show the details
        if (bundle != null) {
            String category = bundle.getString("Category");
            String styleTag = bundle.getString("StyleTag");
            String color = bundle.getString("Color");
            String season = bundle.getString("Season");
            String description = bundle.getString("Description");

            detailCat = view.findViewById(R.id.detailCat);
            detailCat.setText("Category: " + category);

            detailStyleTag = view.findViewById(R.id.detailStyleTag);
            detailStyleTag.setText("Style Tag: " + styleTag);

            detailColor = view.findViewById(R.id.detailColor);
            detailColor.setText("Color: " + color);

            detailSeason = view.findViewById(R.id.detailSeason);
            detailSeason.setText("Seasonality: " + season);

            detailDesc = view.findViewById(R.id.detailDesc);
            detailDesc.setText("Description: " + description);
        }

        return view;
    }


}