package com.example.stylewiz_vol2;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class NewOutfitFragment extends Fragment {
    CheckBox chkAccessory, chkTop, chkOnePiece, chkLayerOnePiece;
    ImageView imgAccessory1, imgAccessory2, imgOutwear, imgTop1, imgTop2, imgOnePiece, imgLayerOnePiece, imgBottom, imgShoes;
    LinearLayout topSection, bottomSection, onePieceSection;
    Switch switchOnePiece;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_outfit, container, false);

        //Checkboxes and switches
        chkAccessory = view.findViewById(R.id.chkSecondAccessory);
        chkTop = view.findViewById(R.id.chkSecondLayer);
        chkLayerOnePiece = view.findViewById(R.id.chkLayerOnePiece);
        switchOnePiece = view.findViewById(R.id.switchOnePiece);

        //ImageViews
        imgAccessory1 = view.findViewById(R.id.imgAccessory1);
        imgAccessory2 = view.findViewById(R.id.imgAccessory2);
        imgOutwear = view.findViewById(R.id.imgOutwear);
        imgTop1 = view.findViewById(R.id.imgTop1);
        imgTop2 = view.findViewById(R.id.imgTop2);
        imgOnePiece = view.findViewById(R.id.imgOnePiece);
        imgLayerOnePiece = view.findViewById(R.id.imgLayerOnePiece);
        imgBottom = view.findViewById(R.id.imgBottom);
        imgShoes = view.findViewById(R.id.imgShoes);

        //Linear Layouts
        topSection = view.findViewById(R.id.topSection);
        bottomSection = view.findViewById(R.id.bottomSection);
        onePieceSection = view.findViewById(R.id.onePieceSection);

        // Apply gray tint if default placeholder is shown
        imgAccessory1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgAccessory2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgOutwear.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgTop1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgTop2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgOnePiece.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgLayerOnePiece.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgBottom.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
        imgShoes.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);





        // Set a listener on the CheckBox for Accessories
        chkAccessory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgAccessory2.setVisibility(View.VISIBLE);  // Show image if checked
                } else {
                    imgAccessory2.setVisibility(View.INVISIBLE);  // Hide image if unchecked
                }
            }
        });

        // Set a listener on the CheckBox for Tops
        chkTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgTop2.setVisibility(View.VISIBLE);  // Show image if checked
                } else {
                    imgTop2.setVisibility(View.INVISIBLE);  // Hide image if unchecked
                }
            }
        });

        // Set a listener on the switch for One-Piece

        switchOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onePieceSection.setVisibility(View.VISIBLE);
                    topSection.setVisibility(View.GONE);
                    bottomSection.setVisibility(View.GONE);

                    // Assign the click listener dynamically when switch is turned on
                    imgOnePiece.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showItemSelectionDialog("One-Piece", imgOnePiece);
                        }
                    });

                    chkLayerOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                imgLayerOnePiece.setVisibility(View.VISIBLE);
                                imgLayerOnePiece.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showItemSelectionDialog("Top", imgLayerOnePiece);
                                    }
                                });
                            } else {
                                imgLayerOnePiece.setVisibility(View.INVISIBLE);
                                imgLayerOnePiece.setOnClickListener(null); // Remove click listener
                            }
                        }
                    });

                } else {
                    onePieceSection.setVisibility(View.GONE);
                    topSection.setVisibility(View.VISIBLE);
                    bottomSection.setVisibility(View.VISIBLE);

                    // Remove click listener when switch is off
                    imgOnePiece.setOnClickListener(null);
                    imgLayerOnePiece.setOnClickListener(null);
                }
            }
        });



        db = FirebaseFirestore.getInstance();

        //Call the dialog with the category requested
        imgAccessory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Accessory", imgAccessory1);
            }
        });

        imgAccessory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Accessory", imgAccessory2);
            }
        });


        imgOutwear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Outerwear", imgOutwear);
            }
        });

        imgTop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Top", imgTop1);
            }
        });

        imgTop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Top", imgTop2);
            }
        });


        imgBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Bottom", imgBottom);
            }
        });

        imgShoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Shoes", imgShoes);
            }
        });




        return view;
    }

    private void showItemSelectionDialog(String category, ImageView targetImageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_item_picker, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<OutfitItem> itemList = new ArrayList<>();

        // Create the dialog FIRST and store it in a final variable
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        OutfitAdapter adapter = new OutfitAdapter(requireContext(), itemList, selectedItem -> {
            // Ensure the targetImageView is valid before updating
            if (targetImageView != null) {
                String photoUrl = selectedItem.getPhotoUrl(); // Get the URL of the selected item's photo
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Log.e("Photo URL", "URL: " + photoUrl);  // Debugging

                    // Load the image into the ImageView using Glide
                    Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.round_add_24)
                            .error(R.drawable.round_add_24)
                            .transform(new RoundedCorners(10))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (isAdded()) {
                                        targetImageView.setImageDrawable(resource);
                                        targetImageView.clearColorFilter(); // Remove tint when an actual image is loaded
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    if (isAdded()) {
                                        targetImageView.setImageDrawable(placeholder);
                                        if (placeholder != null && placeholder.getConstantState() == ContextCompat.getDrawable(requireContext(), R.drawable.round_add_24).getConstantState()) {
                                            targetImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
                                        }
                                    }
                                }
                            });

                } else {
                    Log.e("Image Selection", "Photo URL is empty or invalid");
                }
            }
            alertDialog.dismiss();
        });
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users")
                    .document(userId)
                    .collection("wardrobe")
                    .whereEqualTo("category", category)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.e("Firestore", "No items found for category: " + category);
                        } else {
                            int startPosition = itemList.size(); // Store the start position before adding items
                            List<OutfitItem> newItems = new ArrayList<>(); // Temporary list to hold new items

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                OutfitItem item = doc.toObject(OutfitItem.class);
                                item.setDocumentId(doc.getId());
                                newItems.add(item); // Add to temp list
                            }

                            // Add all new items at once
                            itemList.addAll(newItems);
                            adapter.notifyItemRangeInserted(startPosition, newItems.size()); // Notify batch update
                        }
                    });
        }


        // Show the dialog after setting up everything
        alertDialog.show();
    }

}
