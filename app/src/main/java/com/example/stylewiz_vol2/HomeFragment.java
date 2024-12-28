package com.example.stylewiz_vol2;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    TextView textViewUsername;
    private ListenerRegistration usernameListener; // Firestore listener reference
    RecyclerView recyclerView;
    ItemsAdapter adapter;
    List<DataClass> dataList;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and GridLayoutManager
        recyclerView = view.findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize dataList and adapter
        dataList = new ArrayList<>();
        adapter = new ItemsAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        //Load user data for username
        showUserData(view);

        // Fetch data from Firestore
        fetchDataFromFirestore();



    }




    private void showUserData(View view) {
        textViewUsername = view.findViewById(R.id.homeFragment);

        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();

            if (email != null) {

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
                                    textViewUsername.setText("Welcome "+ username + "!");

                                } else {
                                    textViewUsername.setText("Unknown User");

                                }
                            } else {
                                textViewUsername.setText("Unknown User");
                            }
                        });

            } else {
                Toast.makeText(getActivity(), "No email found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

    }


    private void fetchDataFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(userId)
                    .collection("wardrobe") // Access the 'wardrobe' sub-collection
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Toast.makeText(getActivity(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null) {
                            dataList.clear();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                DataClass data = document.toObject(DataClass.class);
                                dataList.add(data);
                            }
                            adapter.notifyDataSetChanged(); // Update RecyclerView
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }




}