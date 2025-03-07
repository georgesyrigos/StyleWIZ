package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class SelectedOutfitFragment extends Fragment {
    ImageView backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_outfit, container, false);

        backButton = view.findViewById(R.id.back);



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
}