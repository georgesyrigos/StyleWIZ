package com.example.stylewiz_vol2;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
    private boolean isManualUpdate = false;




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
        adapter = new ItemsAdapter(getContext(), dataList, documentId -> {
            isManualUpdate = true; // Lock updates
            deleteItemFromFirestore(documentId); // Delete from Firestore
        });
        recyclerView.setAdapter(adapter);

        // Attach SwipeToDeleteCallback to RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(getContext(), adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

    private void deleteItemFromFirestore(String documentId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users")
                    .document(userId)
                    .collection("wardrobe")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Document successfully deleted.");
                        isManualUpdate = false; // Unlock updates
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error deleting document", e);
                        isManualUpdate = false; // Unlock updates even on failure
                    });
        }
    }


    private void fetchDataFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users")
                    .document(userId)
                    .collection("wardrobe")
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Toast.makeText(getActivity(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null) {
                            // Skip updates if manual operation is in progress
                            if (isManualUpdate) {
                                Log.d("Firestore", "Manual update in progress, skipping snapshot update.");
                                return;
                            }

                            List<DataClass> updatedList = new ArrayList<>();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                DataClass data = document.toObject(DataClass.class);
                                if (data != null) {
                                    data.setDocumentId(document.getId());
                                    updatedList.add(data);
                                }
                            }

                            // Update dataList and RecyclerView
                            dataList.clear();
                            dataList.addAll(updatedList);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }





}