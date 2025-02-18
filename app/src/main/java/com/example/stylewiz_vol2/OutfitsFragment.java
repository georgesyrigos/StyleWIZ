package com.example.stylewiz_vol2;

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

import java.util.ArrayList;
import java.util.List;

public class OutfitsFragment extends Fragment {

    private LinearLayout emptyStateLayout;
    GridView gridView;
    ItemsAdapter adapter;
    List<DataClass> dataList;
    FirebaseFirestore db;
    private boolean isManualUpdate = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outfits, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_outfits);


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

                // Add DetailsFragment or show if already added
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




}