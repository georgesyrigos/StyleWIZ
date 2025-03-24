package com.example.stylewiz_vol2;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class SelectedOutfitFragment extends Fragment {
    ImageView  imgOuterwear, imgTop1, imgTop2, imgBottom, imgOnePiece, imgLayerOnePiece, imgShoes, imgAccessory1, imgAccessory2, backButton, deleteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_outfit, container, false);

        backButton = view.findViewById(R.id.back);
        deleteButton = view.findViewById(R.id.deleteOutfit);

        //ImageViews
        imgOuterwear = view.findViewById(R.id.imgOuterwear);
        imgTop1 = view.findViewById(R.id.imgTop1);
        imgTop2 = view.findViewById(R.id.imgTop2);
        imgBottom = view.findViewById(R.id.imgBottom);
        imgOnePiece = view.findViewById(R.id.imgOnePiece);
        imgLayerOnePiece = view.findViewById(R.id.imgLayerOnePiece);
        imgShoes = view.findViewById(R.id.imgShoes);
        imgAccessory1 = view.findViewById(R.id.imgAccessory1);
        imgAccessory2 = view.findViewById(R.id.imgAccessory2);


        // Get data from the bundle
        Bundle bundleOutfit = getArguments();
        if (bundleOutfit != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String documentId = bundleOutfit.getString("DocumentId");
            ArrayList<String> images = bundleOutfit.getStringArrayList("selected_images");

            // Ensure the images list is not null
            if (images != null) {
                setImage(imgOuterwear, getImageAtIndex(images, 0));
                setImage(imgTop1, getImageAtIndex(images, 1));
                setImage(imgTop2, getImageAtIndex(images, 2));
                setImage(imgBottom, getImageAtIndex(images, 3));
                setImage(imgOnePiece, getImageAtIndex(images, 4));
                setImage(imgLayerOnePiece, getImageAtIndex(images, 5));
                setImage(imgShoes, getImageAtIndex(images, 6));
                setImage(imgAccessory1, getImageAtIndex(images, 7));
                setImage(imgAccessory2, getImageAtIndex(images, 8));
            }

            // Set up the click listener for delete
            deleteButton.setOnClickListener(v -> {
                showDeleteConfirmationDialog(userId, documentId);

            });
        }


        // Set up the click listener for back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Find the existing OutfitsFragment
                Fragment outfitsFragment = fragmentManager.findFragmentByTag("OUTFIT");

                if (outfitsFragment != null) {
                    // Just show the existing fragment instead of recreating it
                    transaction.show(outfitsFragment);
                }

                // Remove SelectedOutfitFragment to ensure it's cleared
                transaction.remove(SelectedOutfitFragment.this).commit();

                // Pop from the back stack to ensure proper navigation
                fragmentManager.popBackStack();
            }
        });


        return view;
    }


    private String getImageAtIndex(ArrayList<String> images, int index) {
        return index < images.size() ? images.get(index) : "none";
    }

    private void setImage(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.equals("none")) {
            imageView.setVisibility(View.GONE);  // Hide if no image
        } else {
            imageView.setVisibility(View.VISIBLE);  // Ensure visibility if an image is present
            Glide.with(requireContext())
                    .load(imageUrl)
                    .transform(new RoundedCorners(10))
                    .into(imageView);

        }
    }

    private void showDeleteConfirmationDialog(String userId,String documentId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Outfit")
                .setMessage("Are you sure you want to delete this outfit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteSelectedItem(userId, documentId);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Do nothing, close the dialog
                })
                .setCancelable(false)
                .show();
    }


    private void deleteSelectedItem(String userId, String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("outfits")
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.e("SelectedOutfitFragment", "Outfit deleted successfully");


                    //remove the current fragment and return to outfits
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                    // Clear the back stack completely before navigating
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    // Ensure only the OutfitsFragment is visible
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    Fragment outfitsFragment = fragmentManager.findFragmentByTag("OUTFITS");
                    if (outfitsFragment == null) {
                        // Add the OutfitsFragment if it doesn't exist
                        outfitsFragment = new OutfitsFragment();
                        transaction.add(R.id.frameLayout, outfitsFragment, "OUTFITS");
                    } else {
                        // Show the OutfitsFragment if it exists
                        transaction.show(outfitsFragment);
                    }

                    // Remove SelectedOutfitFragment explicitly to avoid stacking
                    transaction.remove(SelectedOutfitFragment.this).commit();
                })
                .addOnFailureListener(e -> {
                    Log.e("SelectedOutfitFragment", "Failed to delete outfit: " + e.getMessage());

                });
    }

}