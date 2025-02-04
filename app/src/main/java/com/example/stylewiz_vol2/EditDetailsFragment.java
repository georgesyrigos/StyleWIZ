package com.example.stylewiz_vol2;


import android.app.AlertDialog;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EditDetailsFragment extends Fragment {

    Button cancelBtn, saveBtn;
    private ImageView detailImage, backButton, deleteButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_details, container, false);


        backButton = view.findViewById(R.id.backEdit);
        deleteButton = view.findViewById(R.id.deleteEdit);

        // Set up the click listener for back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the back stack to return to the previous Fragment
                requireActivity().getSupportFragmentManager().popBackStack();

                // Optionally, you can also hide EditProfileFragment manually if needed
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(EditDetailsFragment.this)
                        .commit();
            }
        });


        /*
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String documentId = bundle.getString("DocumentId");

                    if (documentId != null) {
                        FirestoreHelper firestoreHelper = new FirestoreHelper();
                        firestoreHelper.deleteItem(userId, documentId, new FirestoreHelper.DeletionCallback() {
                            @Override
                            public void onSuccess() {

                                // Notify adapter about deletion using the Activity/Fragment callback
                                if (getActivity() instanceof ItemsAdapter.OnItemDeleteListener) {
                                    ((ItemsAdapter.OnItemDeleteListener) getActivity()).onItemDelete(documentId);
                                }


                                // Navigate back to HomeFragment instead of just popping the stack
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // Clear back stack

                                // Load HomeFragment
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.frameLayout, new HomeFragment());
                                transaction.commit();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(requireContext(), "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("EditDetailsFragment", "Error deleting item", e);
                            }
                        });
                    }
                }
            }
        });*/



        // Initialize the button
        cancelBtn = view.findViewById(R.id.cancel_button_details);

        // Set up the click listener
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Navigate back to the previous fragment using the back stack
                requireActivity().getSupportFragmentManager().popBackStack();

                // Remove the EditDetailsFragment
                transaction.remove(EditDetailsFragment.this).commit();

            }
        });

        //using the bundle I got from the details to get the document id
        Bundle bundle = getArguments();
        //show the details
        if (bundle != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String documentId = bundle.getString("DocumentId");
            showItemData(view,userId, documentId);
            Log.e("DetailsFragment", userId + "," + documentId);


        }
        else {
            Log.e("DetailsFragment", "Not found");

        }


        return view;
    }


    //showing the current info to the existing fields
    private void showItemData(View view, String userId, String documentId) {
        TextInputLayout categoryInputLayout = view.findViewById(R.id.editCategoryDetails);
        TextInputLayout styleTagInputLayout = view.findViewById(R.id.editStyleTagDetails);
        TextInputLayout colorInputLayout = view.findViewById(R.id.editColorDetails);
        TextInputLayout seasonalityInputLayout = view.findViewById(R.id.editSeasonalityDetails);
        TextInputLayout descriptionInputLayout = view.findViewById(R.id.editDescriptionDetails);

        // Access the TextInputEditTexts inside the TextInputLayouts
        TextInputEditText categoryEditDetails = (TextInputEditText) categoryInputLayout.getEditText();
        TextInputEditText styleTagEditDetails = (TextInputEditText) styleTagInputLayout.getEditText();
        TextInputEditText colorEditDetails = (TextInputEditText) colorInputLayout.getEditText();
        TextInputEditText seasonalityEditDetails = (TextInputEditText) seasonalityInputLayout.getEditText();
        TextInputEditText descriptionEditDetails = (TextInputEditText) descriptionInputLayout.getEditText();

        FirestoreHelper firestoreHelper = new FirestoreHelper();

        //calling the function from firestore helper to get the info
        firestoreHelper.getItemData(userId, documentId, new FirestoreHelper.ItemDataCallback() {
            @Override
            public void onSuccess(String category, String styleTag, String description, String color, String season) {
                if (categoryEditDetails != null) categoryEditDetails.setText(category);
                if (styleTagEditDetails != null) styleTagEditDetails.setText(styleTag);
                if (colorEditDetails != null) colorEditDetails.setText(color);
                if (seasonalityEditDetails != null) seasonalityEditDetails.setText(season);
                if (descriptionEditDetails != null) descriptionEditDetails.setText(description);
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("Error fetching item data: " + e.getMessage());
            }
        });
    }

}