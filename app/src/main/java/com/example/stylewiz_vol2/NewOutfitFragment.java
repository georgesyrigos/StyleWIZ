package com.example.stylewiz_vol2;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
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
import com.bumptech.glide.request.target.Target;
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

        // Set a listener on the CheckBox for One-Piece
        chkLayerOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgLayerOnePiece.setVisibility(View.VISIBLE);  // Show image if checked
                } else {
                    imgLayerOnePiece.setVisibility(View.INVISIBLE);  // Hide image if unchecked
                }
            }
        });


        // Set a listener on the Switch for One-Piece
        switchOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onePieceSection.setVisibility(View.VISIBLE);
                    topSection.setVisibility(View.GONE);
                    bottomSection.setVisibility(View.GONE);

                } else {
                    onePieceSection.setVisibility(View.GONE);
                    topSection.setVisibility(View.VISIBLE);
                    bottomSection.setVisibility(View.VISIBLE);
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

        if (chkAccessory.isChecked()){
            imgAccessory2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showItemSelectionDialog("Accessory", imgAccessory2);
                }
            });
        }

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

        if (switchOnePiece.isChecked())
        {
            imgOnePiece.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showItemSelectionDialog("One-Piece", imgOnePiece);
                }
            });
            if (chkOnePiece.isChecked()){
                imgLayerOnePiece.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showItemSelectionDialog("Top", imgLayerOnePiece);
                    }
                });
            }
        }


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

        OutfitAdapter adapter = new OutfitAdapter(requireContext(), itemList, selectedItem -> {
            // Ensure the targetImageView is valid before updating
            if (targetImageView != null) {
                String photoUrl = selectedItem.getPhotoUrl(); // Get the URL of the selected item's photo
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Log.e("Photo URL", "URL: " + photoUrl);  // Debugging

                    // Load the image into the ImageView using Glide
                    Glide.with(requireContext())
                            .load(photoUrl)
                            .placeholder(R.drawable.round_image_search_24)
                            .error(R.drawable.round_image_search_24)
                            .transform(new RoundedCorners(10))
                            .into(targetImageView);
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
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                OutfitItem item = doc.toObject(OutfitItem.class);
                                item.setDocumentId(doc.getId());
                                itemList.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }

        // Show the dialog after setting up everything
        alertDialog.show();
    }

}
