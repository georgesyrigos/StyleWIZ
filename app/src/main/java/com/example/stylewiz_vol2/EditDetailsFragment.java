package com.example.stylewiz_vol2;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class EditDetailsFragment extends Fragment {

    Button cancelBtn, saveBtn;
    private ImageView editDetailsImage, backButton, deleteButton;
    String edit_Category, edit_StyleTag, edit_Color, edit_Seasonality;

    String[] category = {"Top", "One-Piece", "Bottom", "Outerwear", "Shoes", "Accessory"};
    String[] styleTag = {"Sport", "Casual", "Formal", "Seasonal", "Streetwear", "Work"};
    String[] seasonality = {"Autumn/Winter", "Spring/Summer", "All season"};
    String[] color = {"Black", "White", "Gray", "Red", "Blue", "Green", "Yellow", "Brown", "Beige", "Pink", "Gold"};
    AutoCompleteTextView categoryDropdown, styleTagDropdown, colorDropdown, seasonalityDropdown;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize dropdown
        categoryDropdown = view.findViewById(R.id.categoryDropdown);

        // Setup the adapter for the AutoCompleteTextView
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, category);
        categoryDropdown.setAdapter(categoryAdapter);

        // Allow the AutoCompleteTextView to remain focusable
        categoryDropdown.setFocusable(true);
        categoryDropdown.setClickable(true);

        // Handle touch events to open the dropdown without opening the keyboard
        categoryDropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Open the dropdown if it's not already showing
                    if (!categoryDropdown.isPopupShowing()) {
                        categoryDropdown.showDropDown();
                    }

                    // Hide the keyboard if it's showing
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(categoryDropdown.getWindowToken(), 0);
                    }
                }
                return false; // Return false to allow normal touch behavior
            }
        });

        // Set the selected category if it exists
        if (!TextUtils.isEmpty(edit_Category)) {
            categoryDropdown.setText(edit_Category, false);
        }

        // Handle selection from the dropdown
        categoryDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // Update the selected category
                edit_Category = adapterView.getItemAtPosition(i).toString();

                // After selecting an item, we should stop the keyboard from appearing when the user taps the field again
                categoryDropdown.setText(edit_Category, false);
            }
        });


        // Initialize dropdown styleTag
        styleTagDropdown = view.findViewById(R.id.styleTagDropdown);

        // Setup the adapter for the AutoCompleteTextView
        ArrayAdapter<String> styleTagAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, styleTag);
        styleTagDropdown.setAdapter(styleTagAdapter);

        // Allow the AutoCompleteTextView to remain focusable and clickable
        styleTagDropdown.setFocusable(true);
        styleTagDropdown.setClickable(true);

        // Handle touch events to open the dropdown without opening the keyboard
        styleTagDropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Open the dropdown if it's not already showing
                    if (!styleTagDropdown.isPopupShowing()) {
                        styleTagDropdown.showDropDown();
                    }

                    // Hide the keyboard if it's showing
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(styleTagDropdown.getWindowToken(), 0);
                    }

                    // Call performClick for accessibility compliance
                    v.performClick();
                }
                return false; // Return false to allow normal touch behavior
            }
        });

        // Override performClick to remove lint warning and support accessibility
        styleTagDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the dropdown on click as well
                if (!styleTagDropdown.isPopupShowing()) {
                    styleTagDropdown.showDropDown();
                }
            }
        });

        // Set the selected style if it exists
        if (!TextUtils.isEmpty(edit_StyleTag)) {
            styleTagDropdown.setText(edit_StyleTag, false);
        }

        // Handle selection from the dropdown
        styleTagDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // Update the selected style tag
                edit_StyleTag = adapterView.getItemAtPosition(i).toString();

                // After selecting an item, set the text without opening keyboard
                styleTagDropdown.setText(edit_StyleTag, false);
            }
        });


        // Initialize dropdown color
        colorDropdown = view.findViewById(R.id.colorDropdown);

        // Setup the adapter for the AutoCompleteTextView
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, color);
        colorDropdown.setAdapter(colorAdapter);

        // Allow the AutoCompleteTextView to remain focusable and clickable
        colorDropdown.setFocusable(true);
        colorDropdown.setClickable(true);

        // Handle touch events to open the dropdown without opening the keyboard
        colorDropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Open the dropdown if it's not already showing
                    if (!colorDropdown.isPopupShowing()) {
                        colorDropdown.showDropDown();
                    }

                    // Hide the keyboard if it's showing
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(colorDropdown.getWindowToken(), 0);
                    }

                    // Call performClick for accessibility compliance
                    v.performClick();
                }
                return false; // Return false to allow normal touch behavior
            }
        });

        // Override performClick to remove lint warning and support accessibility
        colorDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the dropdown on click as well
                if (!colorDropdown.isPopupShowing()) {
                    colorDropdown.showDropDown();
                }
            }
        });

        // Set the selected style if it exists
        if (!TextUtils.isEmpty(edit_Color)) {
            colorDropdown.setText(edit_Color, false);
        }

        // Handle selection from the dropdown
        colorDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // Update the selected style tag
                edit_Color = adapterView.getItemAtPosition(i).toString();

                // After selecting an item, set the text without opening keyboard
                colorDropdown.setText(edit_Color, false);
            }
        });

        // Initialize dropdown seasonality
        seasonalityDropdown = view.findViewById(R.id.seasonalityDropdown);

        // Setup the adapter for the AutoCompleteTextView
        ArrayAdapter<String> seasonalityAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, seasonality);
        seasonalityDropdown.setAdapter(seasonalityAdapter);

        // Allow the AutoCompleteTextView to remain focusable and clickable
        seasonalityDropdown.setFocusable(true);
        seasonalityDropdown.setClickable(true);

        // Handle touch events to open the dropdown without opening the keyboard
        seasonalityDropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Open the dropdown if it's not already showing
                    if (!seasonalityDropdown.isPopupShowing()) {
                        seasonalityDropdown.showDropDown();
                    }

                    // Hide the keyboard if it's showing
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(seasonalityDropdown.getWindowToken(), 0);
                    }

                    // Call performClick for accessibility compliance
                    v.performClick();
                }
                return false; // Return false to allow normal touch behavior
            }
        });

        // Override performClick to remove lint warning and support accessibility
        seasonalityDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the dropdown on click as well
                if (!seasonalityDropdown.isPopupShowing()) {
                    seasonalityDropdown.showDropDown();
                }
            }
        });

        // Set the selected style if it exists
        if (!TextUtils.isEmpty(edit_Seasonality)) {
            seasonalityDropdown.setText(edit_Seasonality, false);
        }

        // Handle selection from the dropdown
        seasonalityDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // Update the selected style tag
                edit_Seasonality = adapterView.getItemAtPosition(i).toString();

                // After selecting an item, set the text without opening keyboard
                seasonalityDropdown.setText(edit_Seasonality, false);
            }
        });



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_details, container, false);

        // Initialize the button
        backButton = view.findViewById(R.id.backEdit);
        deleteButton = view.findViewById(R.id.deleteEdit);
        cancelBtn = view.findViewById(R.id.cancel_button_details);
        saveBtn = view.findViewById(R.id.save_button_details);
        editDetailsImage = view.findViewById(R.id.editDetailsImage);

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
            String imageUrl = bundle.getString("PhotoUrl");

            //Load image using Glide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(imageUrl)
                        .into(editDetailsImage);
            }
            //show the rest of the data
            showItemData(view,userId, documentId);
            Log.e("DetailsFragment", userId + "," + documentId);

            // Set up the click listener for delete
            deleteButton.setOnClickListener(v -> {
                showDeleteConfirmationDialog(userId, documentId);

            });




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
        AutoCompleteTextView categoryEditDetails = (AutoCompleteTextView) categoryInputLayout.getEditText();
        AutoCompleteTextView styleTagEditDetails = (AutoCompleteTextView) styleTagInputLayout.getEditText();
        AutoCompleteTextView colorEditDetails = (AutoCompleteTextView) colorInputLayout.getEditText();
        AutoCompleteTextView seasonalityEditDetails = (AutoCompleteTextView) seasonalityInputLayout.getEditText();
        TextInputEditText descriptionEditDetails = (TextInputEditText) descriptionInputLayout.getEditText();

        FirestoreHelper firestoreHelper = new FirestoreHelper();

        //calling the function from firestore helper to get the info
        firestoreHelper.getItemData(userId, documentId, new FirestoreHelper.ItemDataCallback() {
            @Override
            public void onSuccess(String category, String styleTag, String description, String color, String season) {
                if (categoryEditDetails != null) {
                    categoryEditDetails.post(() -> {
                        categoryEditDetails.setText(category, false); // Prevent filtering
                        categoryEditDetails.clearFocus(); // Optional: Prevents unwanted keyboard popup

                    });
                }
                if (styleTagEditDetails != null) {
                    styleTagEditDetails.post(() -> {
                        styleTagEditDetails.setText(styleTag, false); // Prevent filtering
                        styleTagEditDetails.clearFocus(); // Optional: Prevents unwanted keyboard popup

                    });
                }
                if (colorEditDetails != null) {
                    colorEditDetails.post(() -> {
                        colorEditDetails.setText(color, false); // Prevent filtering
                        colorEditDetails.clearFocus(); // Optional: Prevents unwanted keyboard popup

                    });
                }
                if (seasonalityEditDetails != null) {
                    seasonalityEditDetails.post(() -> {
                        seasonalityEditDetails.setText(season, false); // Prevent filtering
                        seasonalityEditDetails.clearFocus(); // Optional: Prevents unwanted keyboard popup

                    });
                }
                if (descriptionEditDetails != null) descriptionEditDetails.setText(description);

                //save the changes to firestore
                saveBtn.setOnClickListener(v -> {
                    if (categoryEditDetails != null){
                        // Get the updated text from input fields
                        String updatedCategory = categoryEditDetails.getText().toString().trim();
                        String updatedStyleTag = styleTagEditDetails.getText().toString().trim();
                        String updatedColor = colorEditDetails.getText().toString().trim();
                        String updatedSeason = seasonalityEditDetails.getText().toString().trim();
                        String updatedDescription = descriptionEditDetails.getText().toString().trim();
                        // Call the update function with the new values
                        saveDetailsChanges(userId, documentId, updatedCategory, updatedStyleTag, updatedDescription, updatedColor, updatedSeason);

                        //remove the current fragment and return to home
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                        // Clear the back stack completely before navigating
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        // Ensure only the HomeFragment is visible
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        Fragment homeFragment = fragmentManager.findFragmentByTag("HOME");
                        if (homeFragment == null) {
                            // Add the HomeFragment if it doesn't exist
                            homeFragment = new HomeFragment();
                            transaction.add(R.id.frameLayout, homeFragment, "HOME");
                        } else {
                            // Show the HomeFragment if it exists
                            transaction.show(homeFragment);
                        }

                        // Remove EditDetailsFragment explicitly to avoid stacking
                        transaction.remove(EditDetailsFragment.this).commit();
                    }
                    else {
                        Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    }

                });


            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("Error fetching item data: " + e.getMessage());
            }
        });


    }


    private void saveDetailsChanges(String userId, String itemId, String category, String styleTag, String description, String color, String season) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> updates = new HashMap<>();
        updates.put("category", category);
        updates.put("styleTag", styleTag);
        updates.put("description", description);
        updates.put("color", color);
        updates.put("season", season);

        db.collection("users")
                .document(userId)
                .collection("wardrobe")
                .document(itemId)
                .update(updates) // Updates only these fields
                .addOnSuccessListener(aVoid -> System.out.println("Item successfully updated!"))
                .addOnFailureListener(e -> System.err.println("Error updating item: " + e.getMessage()));
    }



    //confirmation to delete
    private void showDeleteConfirmationDialog(String userId,String documentId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteSelectedItem(userId, documentId);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Do nothing, close the dialog
                })
                .setCancelable(false)
                .show();
    }


    private void deleteSelectedItem(String userId, String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("wardrobe")
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.e("EditDetailsFragment", "Item deleted successfully");


                    //remove the current fragment and return to home
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                    // Clear the back stack completely before navigating
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    // Ensure only the HomeFragment is visible
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    Fragment homeFragment = fragmentManager.findFragmentByTag("HOME");
                    if (homeFragment == null) {
                        // Add the HomeFragment if it doesn't exist
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.frameLayout, homeFragment, "HOME");
                    } else {
                        // Show the HomeFragment if it exists
                        transaction.show(homeFragment);
                    }

                    // Remove DetailsFragment explicitly to avoid stacking
                    transaction.remove(EditDetailsFragment.this).commit();
                })
                .addOnFailureListener(e -> {
                    Log.e("EditDetailsFragment", "Failed to delete item: " + e.getMessage());

                });
    }

}