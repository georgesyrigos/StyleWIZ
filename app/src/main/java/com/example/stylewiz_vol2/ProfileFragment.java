package com.example.stylewiz_vol2;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

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
TextView profileUsername, profileEmail, profilePassword;
TextView itemsNumberTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileUsername = view.findViewById(R.id.profileUsername);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePassword = view.findViewById(R.id.profilePassword);
        itemsNumberTextView = view.findViewById(R.id.itemsNumber);
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

        return view;
    }

    private void showUserData() {

        Intent intent = getActivity().getIntent();

        String usernameUser = intent.getStringExtra("username");
        String emailUser = intent.getStringExtra("email");
        String passwordUser = intent.getStringExtra("password");

        //profileUsername.setText(usernameUser);
        if (usernameUser != null && !usernameUser.isEmpty()) {
            profileUsername.setText(usernameUser);
            getWardrobeItemCount(usernameUser, itemsNumberTextView);
        } else {
            profileUsername.setText("Unknown User");
            itemsNumberTextView.setText("0");
        }

        profileEmail.setText(emailUser);
        profilePassword.setText(passwordUser);

    }

    private void getWardrobeItemCount(String username, TextView itemsNumberTextView) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Query the "users" collection to find the document with the given username
        database.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Get the first document matching the query
                        String userId = task.getResult().getDocuments().get(0).getId();

                        // Access the "wardrobe" subcollection of the user
                        database.collection("users")
                                .document(userId)
                                .collection("wardrobe")
                                .get()
                                .addOnCompleteListener(wardrobeTask -> {
                                    if (wardrobeTask.isSuccessful() && wardrobeTask.getResult() != null) {
                                        int itemCount = wardrobeTask.getResult().size();
                                        itemsNumberTextView.setText(String.valueOf(itemCount));
                                    } else {
                                        itemsNumberTextView.setText("0");
                                    }
                                });
                    } else {
                        // No user found with the given username
                        itemsNumberTextView.setText("0");
                    }
                });
    }
}