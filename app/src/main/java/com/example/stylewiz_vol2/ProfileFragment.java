package com.example.stylewiz_vol2;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
Button logoutBtn;
Button editProfileBtn;
TextView profileUsername, profileEmail, profilePassword;
TextView itemsNumberTextView, outfitsNumberTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Initialize edit profile fragment
        EditProfileFragment editProfileFragment = new EditProfileFragment();


        profileUsername = view.findViewById(R.id.profileUsername);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePassword = view.findViewById(R.id.profilePassword);
        itemsNumberTextView = view.findViewById(R.id.itemsNumber);
        outfitsNumberTextView = view.findViewById(R.id.outfitsNumber);
        showUserData();


        logoutBtn = view.findViewById(R.id.logout_button);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Sign out the user

                // Redirect to LoginActivity
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                startActivity(intent);

                // Close current activity
                requireActivity().finish();
            }
        });

        editProfileBtn = view.findViewById(R.id.editProfile_button);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();

                // Check if EditProfileFragment already exists
                Fragment existingFragment = requireActivity().getSupportFragmentManager().findFragmentByTag("EDIT_PROFILE");
                if (existingFragment == null) {
                    // Add the EditProfileFragment if it doesn't exist
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .hide(ProfileFragment.this) // Hide the current fragment
                            .add(R.id.frameLayout, editProfileFragment, "EDIT_PROFILE") // Add the new fragment
                            .addToBackStack("EDIT_PROFILE") // Add this transaction to the back stack
                            .commit();
                } else {
                    // If it exists, just show it
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .hide(ProfileFragment.this)
                            .show(existingFragment)
                            .commit();
                }



            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showUserData(); // Refresh data when the fragment becomes visible
    }


    private void showUserData() {

        Intent intent = getActivity().getIntent();

        String usernameUser = intent.getStringExtra("username");
        String emailUser = intent.getStringExtra("email");
        String passwordUser = intent.getStringExtra("password");

        //profileUsername.setText(usernameUser);
        if (usernameUser != null && !usernameUser.isEmpty()) {
            profileUsername.setText(usernameUser);
            listenToWardrobeItemCount(usernameUser, itemsNumberTextView);
            listenToOutfitsItemCount(usernameUser, outfitsNumberTextView);

        } else {
            profileUsername.setText("Unknown User");
            itemsNumberTextView.setText("0");
            outfitsNumberTextView.setText("0");

        }

        profileEmail.setText(emailUser);
        profilePassword.setText(passwordUser);

    }

    private void listenToWardrobeItemCount(String username, TextView itemsNumberTextView) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first document matching the query
                        String userId = task.getResult().getDocuments().get(0).getId();

                        // Set up a real-time listener on the "wardrobe" subcollection
                        database.collection("users")
                                .document(userId)
                                .collection("wardrobe")
                                .addSnapshotListener((snapshot, error) -> {
                                    if (error != null) {
                                        itemsNumberTextView.setText("0");
                                        return;
                                    }
                                    if (snapshot != null) {
                                        int itemCount = snapshot.size();
                                        itemsNumberTextView.setText(String.valueOf(itemCount));
                                    } else {
                                        itemsNumberTextView.setText("0");
                                    }
                                });
                    } else {
                        itemsNumberTextView.setText("0");
                    }
                });
    }


    private void listenToOutfitsItemCount(String username, TextView itemsNumberTextView) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first document matching the query
                        String userId = task.getResult().getDocuments().get(0).getId();

                        // Set up a real-time listener on the "outfits" subcollection
                        database.collection("users")
                                .document(userId)
                                .collection("outfits")
                                .addSnapshotListener((snapshot, error) -> {
                                    if (error != null) {
                                        itemsNumberTextView.setText("0");
                                        return;
                                    }
                                    if (snapshot != null) {
                                        int itemCount = snapshot.size();
                                        itemsNumberTextView.setText(String.valueOf(itemCount));
                                    } else {
                                        itemsNumberTextView.setText("0");
                                    }
                                });
                    } else {
                        outfitsNumberTextView.setText("0");
                    }
                });
    }
}