package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class DetailsFragment extends Fragment {

    private TextView tvTitle, detailCat, detailStyleTag, detailColor, detailSeason, detailDesc;
    private ImageView detailImage, backButton, editButton, likeButton;

    private boolean isLiked; // Local state for like button
    private String userId;  // Firebase User ID
    private String documentId;  // Document ID
    private FirebaseFirestore db; // Firestore instance



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the buttons
        backButton = view.findViewById(R.id.back);
        editButton = view.findViewById(R.id.edit);
        likeButton = view.findViewById(R.id.like);
        //Initialize the values
        detailCat = view.findViewById(R.id.detailCat);
        detailStyleTag = view.findViewById(R.id.detailStyleTag);
        detailColor = view.findViewById(R.id.detailColor);
        detailSeason = view.findViewById(R.id.detailSeason);
        detailDesc = view.findViewById(R.id.detailDesc);


        // Get user ID from FirebaseAuth
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up the click listener for back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Pop the back stack to return to the HomeFragment
                fragmentManager.popBackStack();

                // Ensure only the HomeFragment is visible
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment homeFragment = fragmentManager.findFragmentByTag("HOME");
                if (homeFragment == null) {
                    // Add the HomeFragment if it doesn't exist
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.frameLayout, homeFragment, "HOME");
                } else {
                    // Show the HomeFragment if it exists
                    transaction.show(homeFragment);
                }

                // Remove DetailsFragment explicitly to avoid stacking
                transaction.remove(DetailsFragment.this).commit();
            }
        });


        //get the details from the adapter
        Bundle bundle = getArguments();
        //show the details
        if (bundle != null) {
            String category = bundle.getString("Category");
            String styleTag = bundle.getString("StyleTag");
            String color = bundle.getString("Color");
            String season = bundle.getString("Season");
            String description = bundle.getString("Description");
            documentId = bundle.getString("DocumentId");


            detailCat.setText(category);
            detailStyleTag.setText(styleTag);
            detailColor.setText(color);
            detailSeason.setText(season);
            detailDesc.setText(description);

            // Load image using Glide
            //Glide.with(requireContext()).load(image).into(detailImage);
            Log.e("DetailsFragment", userId + "," + documentId);


            // Set up the click listener for edit and getting the document id passed to edit details
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditDetailsFragment editDetailsFragment = new EditDetailsFragment();
                    //getting from the bundle above all the elements
                    editDetailsFragment.setArguments(bundle); //Pass documentId to fragment


                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
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
                    transaction.add(R.id.frameLayout, editDetailsFragment, "EDIT_DETAILS")
                            .addToBackStack("EDIT_DETAILS")
                            .commit();

                }
            });



        }

        // Fetch current like state from Firestore
        fetchLikeStateFromFirestore();


        // Handle like button click
        likeButton.setOnClickListener(v -> toggleLike());

        return view;
    }

    private void fetchLikeStateFromFirestore() {
        if (documentId == null || documentId.isEmpty()) {
            Log.e("DetailsFragment", "Cannot fetch like state: DocumentId is null or empty");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("wardrobe")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("liked")) {
                        isLiked = documentSnapshot.getBoolean("liked"); // Get current liked state
                        updateLikeButtonUI(); // Update the button UI
                    } else {
                        Log.w("DetailsFragment", "Document does not exist or 'liked' field not found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("DetailsFragment", "Error fetching like state", e));
    }

    private void toggleLike() {
        // Toggle the like state
        isLiked = !isLiked;

        // Update Firestore
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.updateLikedField(userId, documentId, isLiked, new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess() {
                Log.d("DetailsFragment", "Liked state updated successfully in Firestore.");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("DetailsFragment", "Failed to update liked state in Firestore.", e);
            }
        });

        // Update the like button UI
        updateLikeButtonUI();
    }

    private void updateLikeButtonUI() {
        if (isLiked) {
            likeButton.setImageResource(R.drawable.round_favorite_24); // Filled heart
            likeButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue));
            Log.e("DetailsFragment", "Liked state updated successfully in Firestore.");

        } else {
            likeButton.setImageResource(R.drawable.round_favorite_border_24); // Empty heart
            likeButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue));
            Log.e("DetailsFragment", "Liked state not updated successfully in Firestore.");

        }
    }


}