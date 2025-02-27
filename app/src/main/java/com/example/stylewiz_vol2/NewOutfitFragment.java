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
import android.widget.Button;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewOutfitFragment extends Fragment {
    CheckBox chkAccessory, chkTop, chkLayerOnePiece;
    ImageView imgAccessory1, imgAccessory2, imgOuterwear, imgTop1, imgTop2, imgOnePiece, imgLayerOnePiece, imgBottom, imgShoes;
    LinearLayout topSection, bottomSection, onePieceSection;
    Switch switchOnePiece;
    FirebaseFirestore db;
    Button addOutfitBtn;
    private Map<Integer, String> selectedItemUrls = new HashMap<>();  // Store selected image URLs




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

        //Button initialization
        addOutfitBtn = view.findViewById(R.id.addOutfitBtn);


        //ImageViews
        imgAccessory1 = view.findViewById(R.id.imgAccessory1);
        imgAccessory2 = view.findViewById(R.id.imgAccessory2);
        imgOuterwear = view.findViewById(R.id.imgOuterwear);
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
        imgOuterwear.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);
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
                            showItemSelectionDialog("One-Piece", imgOnePiece, R.id.imgOnePiece);
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
                                        showItemSelectionDialog("Top", imgLayerOnePiece, R.id.imgLayerOnePiece);
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
                showItemSelectionDialog("Accessory", imgAccessory1, R.id.imgAccessory1);
            }
        });

        imgAccessory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Accessory", imgAccessory2, R.id.imgAccessory2);
            }
        });


        imgOuterwear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Outerwear", imgOuterwear, R.id.imgOuterwear);
            }
        });

        imgTop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Top", imgTop1, R.id.imgTop1);
            }
        });

        imgTop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Top", imgTop2, R.id.imgTop2);
            }
        });


        imgBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Bottom", imgBottom, R.id.imgBottom);
            }
        });

        imgShoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemSelectionDialog("Shoes", imgShoes, R.id.imgShoes);
            }
        });



        addOutfitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOutfit();
            }
        });




        return view;
    }

    private void showItemSelectionDialog(String category, ImageView targetImageView, int imageViewId) {
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
                    selectedItemUrls.put(imageViewId, photoUrl);

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

    private void saveOutfit() {
        if (selectedItemUrls.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> outfitData = new HashMap<>();

            List<Integer> allItemIds = Arrays.asList(
                    R.id.imgAccessory1, R.id.imgAccessory2,
                    R.id.imgOuterwear, R.id.imgTop1,
                    R.id.imgTop2, R.id.imgBottom,
                    R.id.imgOnePiece, R.id.imgLayerOnePiece,
                    R.id.imgShoes
            );

            for (int imageViewId : allItemIds) {
                String imageUrl = selectedItemUrls.getOrDefault(imageViewId, "none");
                String itemType = getItemTypeFromId(imageViewId);
                outfitData.put(itemType, imageUrl);
            }

            db.collection("users")
                    .document(userId)
                    .collection("outfits")
                    .add(outfitData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(requireContext(), "Outfit saved!", Toast.LENGTH_SHORT).show();
                        resetImageViews();  // Reset images after saving
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error saving outfit.", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private String getItemTypeFromId(int imageViewId) {
        if (imageViewId == R.id.imgAccessory1) return "accessory1";
        if (imageViewId == R.id.imgAccessory2) return "accessory2";
        if (imageViewId == R.id.imgOuterwear) return "outerwear";
        if (imageViewId == R.id.imgTop1) return "top1";
        if (imageViewId == R.id.imgTop2) return "top2";
        if (imageViewId == R.id.imgBottom) return "bottom";
        if (imageViewId == R.id.imgShoes) return "shoes";
        if (imageViewId == R.id.imgOnePiece) return "one-piece";
        if (imageViewId == R.id.imgLayerOnePiece) return "layer one-piece";

        return "unknown";
    }

    // Function to reset all ImageView elements to their default state
    private void resetImageViews() {
        List<Integer> allItemIds = Arrays.asList(
                R.id.imgAccessory1, R.id.imgAccessory2,
                R.id.imgOuterwear, R.id.imgTop1,
                R.id.imgTop2, R.id.imgBottom,
                R.id.imgOnePiece, R.id.imgLayerOnePiece,
                R.id.imgShoes
        );

        for (int imageViewId : allItemIds) {
            ImageView imageView = requireView().findViewById(imageViewId);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.round_add_24);
                imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray), PorterDuff.Mode.SRC_IN);

            }
        }

        selectedItemUrls.clear(); // Clear the selected items
    }

}
