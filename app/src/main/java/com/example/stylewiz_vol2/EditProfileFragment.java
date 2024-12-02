package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class EditProfileFragment extends Fragment {
    Button cancelBtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize the button
        cancelBtn = view.findViewById(R.id.cancel_button);

        // Set up the click listener
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the ProfileFragment by popping the back stack
                //requireActivity().getSupportFragmentManager().popBackStack();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(EditProfileFragment.this) // Remove the current fragment
                        .show(requireActivity().getSupportFragmentManager().findFragmentByTag("PROFILE")) // Show the previously hidden fragment
                        .commit();
            }
        });


        return view;
    }

}