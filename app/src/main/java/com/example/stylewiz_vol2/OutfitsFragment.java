package com.example.stylewiz_vol2;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OutfitsFragment extends Fragment {

    private GridView gridView;
    private List<ShowOutfitsItem> outfitList;
    private ShowOutfitAdapter outfitAdapter;
    private FirebaseFirestore db;
    private LinearLayout emptyStateLayout;
    private boolean isManualUpdate = false;
    private ListenerRegistration outfitsListener;
    private FloatingActionButton fab;



    public OutfitsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outfits, container, false);



        gridView = view.findViewById(R.id.gridViews);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayoutOutfits);
        fab = view.findViewById(R.id.fab_outfits);
        db = FirebaseFirestore.getInstance();
        outfitList = new ArrayList<>();
        outfitAdapter = new ShowOutfitAdapter(requireContext(), outfitList);
        gridView.setAdapter(outfitAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewOutfitFragment newOutfitFragment = new NewOutfitFragment();


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

                // Add NewOutfitFragment or show if already added
                transaction.add(R.id.frameLayout, newOutfitFragment , "NEW_OUTFIT")
                        .addToBackStack("NEW_OUTFIT")
                        .commit();

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onStart() {
        super.onStart();
        listenForOutfitChanges();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (outfitsListener != null) {
            outfitsListener.remove();  // Stop listening when fragment is not visible
        }
    }

    private void listenForOutfitChanges() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;
        String userId = user.getUid();
        outfitsListener = db.collection("users")
                .document(userId)
                .collection("outfits")
                .orderBy("createdAt", Query.Direction.ASCENDING) // Ensure ordering
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(requireContext(), "Error loading outfits", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        outfitList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            ShowOutfitsItem outfit = doc.toObject(ShowOutfitsItem.class);
                            outfit.setDocumentId(doc.getId());
                            outfitList.add(outfit);
                        }

                        // Toggle visibility based on outfit list size
                        if (outfitList.isEmpty()) {
                            gridView.setVisibility(View.GONE);
                            fab.setVisibility(View.GONE);
                            emptyStateLayout.setVisibility(View.VISIBLE);
                            ImageView addIcon = emptyStateLayout.findViewById(R.id.addIconOutfits);

                            addIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    NewOutfitFragment newOutfitFragment = new NewOutfitFragment();


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

                                    // Add NewOutfitFragment or show if already added
                                    transaction.add(R.id.frameLayout, newOutfitFragment , "NEW_OUTFIT")
                                            .addToBackStack("NEW_OUTFIT")
                                            .commit();

                                }
                            });

                        } else {
                            gridView.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            emptyStateLayout.setVisibility(View.GONE);
                        }

                        outfitAdapter.notifyDataSetChanged();  // Refresh GridView
                    }
                });
    }


}




