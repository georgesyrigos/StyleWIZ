package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class EditDetailsFragment extends Fragment {

    Button cancelBtn, saveBtn;
    private ImageView detailImage, backButton, likeButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_details, container, false);


        backButton = view.findViewById(R.id.backEdit);

        // Set up the click listener for back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the back stack to return to the ProfileFragment
                requireActivity().getSupportFragmentManager().popBackStack();

                // Optionally, you can also hide EditProfileFragment manually if needed
                requireActivity().getSupportFragmentManager().beginTransaction()
                        //.hide(EditProfileFragment.this) // Hide the current fragment
                        .show(requireActivity().getSupportFragmentManager().findFragmentByTag("EDIT_DETAILS")) // Show the ProfileFragment
                        .remove(EditDetailsFragment.this)
                        .commit();
            }
        });


        // Initialize the button
        cancelBtn = view.findViewById(R.id.cancel_button_details);

        // Set up the click listener
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the back stack to return to the DetailsFragment
                //requireActivity().getSupportFragmentManager().popBackStack();

                // Optionally, you can also hide EditProfileFragment manually if needed
                requireActivity().getSupportFragmentManager().beginTransaction()
                        //.hide(EditProfileFragment.this) // Hide the current fragment
                        .show(requireActivity().getSupportFragmentManager().findFragmentByTag("DETAILS")) // Show the ProfileFragment
                        .remove(EditDetailsFragment.this)
                        .commit();
            }
        });
        return view;
    }
}