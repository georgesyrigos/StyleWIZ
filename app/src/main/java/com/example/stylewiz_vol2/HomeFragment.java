package com.example.stylewiz_vol2;

import static android.app.ProgressDialog.show;

import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    TextView textViewUsername;
    private ListenerRegistration usernameListener; // Firestore listener reference
    private LinearLayout emptyStateLayout;
    RecyclerView recyclerView;
    ItemsAdapter adapter;
    List<DataClass> dataList;
    FirebaseFirestore db;
    private boolean isManualUpdate = false;
    EditText searchView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //search bar initialization and getting the char sequence(s)
        searchView = view.findViewById(R.id.searchView);
        ImageView clearIcon = view.findViewById(R.id.clearIcon);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearIcon.setVisibility(View.VISIBLE);
                } else {
                    clearIcon.setVisibility(View.GONE);
                }

                // Trigger search functionality here
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearIcon.setOnClickListener(v -> {
            searchView.setText("");
            clearIcon.setVisibility(View.GONE);
        });

        return view;
    }

    //the new list that has only the searched items
    private void performSearch(String query) {
        // Add logic to filter the RecyclerView based on the query
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if(dataClass.getCategory().toLowerCase().contains(query.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and GridLayoutManager
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
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
        //showUserData(view);
        //text size for username 0dp if needed to show

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


    //listener of firestore for the data
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
                            // Incremental updates based on document changes
                            for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                                DataClass data = dc.getDocument().toObject(DataClass.class);
                                data.setDocumentId(dc.getDocument().getId());
                                //Identifies the type of change for each document applied to the dataList and adapter
                                switch (dc.getType()) {
                                    case ADDED:
                                        dataList.add(data);
                                        adapter.notifyItemInserted(dataList.size() - 1);
                                        break;
                                    case MODIFIED:
                                        updateItemInList(data);
                                        break;
                                    case REMOVED:
                                        removeItemFromList(data.getDocumentId());
                                        break;
                                }
                            }
                        }
                        toggleAddOptionVisibility(); // Check and toggle "add" visibility
                    });
        }
    }


    private void toggleAddOptionVisibility() {
        if (dataList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE); // Show empty state layout (e.g., "add" button or message)
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView

            emptyStateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update the bottom navigation to reflect the new fragment
                    BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavView);
                    if (bottomNavigationView != null) {
                        bottomNavigationView.setSelectedItemId(R.id.bottom_add); //Bottom menu to add new item
                    }

                    // Pop the back stack to return to the HomeFragment
                    requireActivity().getSupportFragmentManager().popBackStack();

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .show(requireActivity().getSupportFragmentManager().findFragmentByTag("NEW_ITEM")) // Go to add new item
                            .hide(HomeFragment.this)
                            .commit();
                }
            });
        } else {
            emptyStateLayout.setVisibility(View.GONE); // Hide empty state layout
            recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
        }
    }

    private void updateItemInList(DataClass updatedItem) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getDocumentId().equals(updatedItem.getDocumentId())) {
                dataList.set(i, updatedItem);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void removeItemFromList(String documentId) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getDocumentId().equals(documentId)) {
                dataList.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }





}