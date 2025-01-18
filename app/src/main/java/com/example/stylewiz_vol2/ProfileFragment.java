package com.example.stylewiz_vol2;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

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
        showUserData(view);


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
                transaction.add(R.id.frameLayout, editProfileFragment, "EDIT_PROFILE")
                        .addToBackStack("EDIT_PROFILE")
                        .commit();



            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh user data when the fragment resumes
        View view = getView(); // Ensure you get the current view
        if (view != null) {
            showUserData(view);
        }
    }


    private ListenerRegistration usernameListener; // Firestore listener reference

    private void showUserData(View view) {
        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirestoreHelper firestoreHelper = new FirestoreHelper();

        if (user != null) {
            String email = user.getEmail();
            profileEmail = view.findViewById(R.id.profileEmail);

            if (email != null) {
                profileEmail.setText(email);

                // Set up a listener for username changes
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                usernameListener = db.collection("users")
                        .document(user.getUid()) // Use UID for unique identification
                        .addSnapshotListener((snapshot, error) -> {
                            if (error != null) {
                                Toast.makeText(getActivity(), "Failed to listen for username changes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                String username = snapshot.getString("username");
                                if (username != null) {
                                    profileUsername.setText(username);

                                    // Update wardrobe and outfit counts
                                    listenToWardrobeItemCount(username, itemsNumberTextView);
                                    listenToOutfitsItemCount(username, outfitsNumberTextView);
                                } else {
                                    profileUsername.setText("Unknown User");
                                    itemsNumberTextView.setText("0");
                                    outfitsNumberTextView.setText("0");
                                }
                            } else {
                                profileUsername.setText("Unknown User");
                            }
                        });

            } else {
                profileEmail.setHint("No email found");
            }
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        profilePassword.setText("******");
    }


    //listener for wardrobe collection
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



    //listener for outfits collection
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