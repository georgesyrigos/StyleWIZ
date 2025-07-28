package com.example.stylewiz_vol2;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

public class FullSuggestedOutfitDialog extends Dialog {
    public FullSuggestedOutfitDialog(@NonNull Context context, List<ImageItem> imageItems) {
        super(context);
        setContentView(R.layout.dialog_full_outfit);


        LinearLayout imagesContainer = findViewById(R.id.fullImagesContainer);
        ImageView closeBtn = findViewById(R.id.closeBtn);


        imagesContainer.removeAllViews();
        imagesContainer.setWeightSum(imageItems.size());

        for (ImageItem item : imageItems) {
            ImageView imageView = new ImageView(context);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f // divide space evenly
            );
            params.setMargins(4, 4, 4, 4);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(context)
                    .load(item.getUrl())
                    .into(imageView);

            imagesContainer.addView(imageView);
        }


        closeBtn.setOnClickListener(v -> dismiss());

        // Match parent width and height for consistent layout
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


}
