package com.example.stylewiz_vol2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class EditProfileFragment extends Fragment {
    Button cancelBtn, saveBtn;
    private boolean isPasswordVisible = false;
    EditText usernameEditProfile, emailEditProfile, currentPassEditProfile, newPassEditProfile;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        usernameEditProfile = view.findViewById(R.id.editTextUsernameEditProfile);
        emailEditProfile = view.findViewById(R.id.editTextEmailEditProfile);
        newPassEditProfile = view.findViewById(R.id.editTextNewPasswordEditProfile);
        currentPassEditProfile = view.findViewById(R.id.editTextPasswordEditProfile);

        //set text with the current values
        showUserData(view);


        // Initialize the button
        cancelBtn = view.findViewById(R.id.cancel_button);

        // Set up the click listener
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the back stack to return to the ProfileFragment
                requireActivity().getSupportFragmentManager().popBackStack();

                // Optionally, you can also hide EditProfileFragment manually if needed
                requireActivity().getSupportFragmentManager().beginTransaction()
                        //.hide(EditProfileFragment.this) // Hide the current fragment
                        .show(requireActivity().getSupportFragmentManager().findFragmentByTag("PROFILE")) // Show the ProfileFragment
                        .remove(EditProfileFragment.this)
                        .commit();
            }
        });

        // Initialize the button
        saveBtn = view.findViewById(R.id.save_button);

        // Set up the click listener
        saveBtn.setOnClickListener(v -> {
            String newEmail = emailEditProfile.getText().toString().trim();
            String newUsername = usernameEditProfile.getText().toString().trim();
            String newPassword = newPassEditProfile.getText().toString().trim();
            String currentPassword = currentPassEditProfile.getText().toString().trim();

            if (currentPassword.isEmpty()) {
                currentPassEditProfile.setError("Current password is required");
                return;
            }

            if (newEmail.isEmpty() && newUsername.isEmpty() && newPassword.isEmpty()) {
                Toast.makeText(getContext(), "Please make changes before saving.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserProfile(newEmail, newUsername, newPassword, currentPassword);
        });




        // Set an OnTouchListener on the current password EditText to toggle visibility
        currentPassEditProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get the drawable on the right side
                    int drawableEnd = 2; // Index 2 is for the right drawable
                    if (currentPassEditProfile.getCompoundDrawables()[drawableEnd] != null) {
                        // Calculate if the touch is within bounds of the drawable
                        float touchableAreaStart = currentPassEditProfile.getRight() - currentPassEditProfile.getCompoundDrawables()[drawableEnd].getBounds().width()
                                - currentPassEditProfile.getPaddingEnd();
                        if (event.getRawX() >= touchableAreaStart) {
                            // Toggle password visibility
                            isPasswordVisible = !isPasswordVisible;
                            if (isPasswordVisible) {
                                // Show password
                                currentPassEditProfile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                currentPassEditProfile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_24, 0);
                            } else {
                                // Hide password
                                currentPassEditProfile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                currentPassEditProfile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_off_24, 0);
                            }
                            // Move cursor to the end of the text
                            currentPassEditProfile.setSelection(currentPassEditProfile.getText().length());
                            return true; // Indicate the touch event is handled
                        }
                    }
                }
                return false;
            }
        });



        // Set an OnTouchListener on the new password EditText to toggle visibility
        newPassEditProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get the drawable on the right side
                    int drawableEnd = 2; // Index 2 is for the right drawable
                    if (newPassEditProfile.getCompoundDrawables()[drawableEnd] != null) {
                        // Calculate if the touch is within bounds of the drawable
                        float touchableAreaStart = newPassEditProfile.getRight() - newPassEditProfile.getCompoundDrawables()[drawableEnd].getBounds().width()
                                - newPassEditProfile.getPaddingEnd();
                        if (event.getRawX() >= touchableAreaStart) {
                            // Toggle password visibility
                            isPasswordVisible = !isPasswordVisible;
                            if (isPasswordVisible) {
                                // Show password
                                newPassEditProfile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                newPassEditProfile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_24, 0);
                            } else {
                                // Hide password
                                newPassEditProfile.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                newPassEditProfile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_off_24, 0);
                            }
                            // Move cursor to the end of the text
                            newPassEditProfile.setSelection(newPassEditProfile.getText().length());
                            return true; // Indicate the touch event is handled
                        }
                    }
                }
                return false;
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh user data when the fragment resumes
        View view = getView(); // Ensure you get the current view
        if (view != null) {
            showUserData(view);
        }
    }

    private void showUserData(View view) {
        EditText usernameEditProfile = view.findViewById(R.id.editTextUsernameEditProfile);
        EditText emailEditProfile = view.findViewById(R.id.editTextEmailEditProfile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirestoreHelper firestoreHelper = new FirestoreHelper();


        if (user != null) {
            String email = user.getEmail();
            if (email!=null){
                emailEditProfile.setText(email);
                firestoreHelper.getUsername(email, new FirestoreHelper.UsernameCallback() {
                    @Override
                    public void onSuccess(String username) {
                        usernameEditProfile.setText(username);

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        usernameEditProfile.setText("Unknown User");
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });




            } else {
                emailEditProfile.setHint("No email found");
            }
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }


    }

    // Method to handle changes
    private void updateUserProfile(String newEmail, String newUsername, String newPassword, String currentPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "Unable to retrieve user email for re-authentication.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Get credentials for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);



        // Re-authenticate user
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update email if new email is provided
                if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(user.getEmail())) {
                    confirmEmailChange(user, newEmail, currentPassword);
                }

                // Update username in Firestore
                if (!newUsername.isEmpty()) {
                    db.collection("users").document(user.getUid())
                            .update("username", newUsername)
                            .addOnCompleteListener(userTask -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Username updated.", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getContext(), "Username update failed.", Toast.LENGTH_SHORT).show();

                                }
                            });
                }

                // Update password if new password is provided
                if (newPassword != null && !newPassword.isEmpty()) {
                    user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                        if (passwordTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update password: " + passwordTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void confirmEmailChange(FirebaseUser user, String newEmail, String currentPassword) {
        if (newEmail == null || newEmail.isEmpty()) {
            Toast.makeText(getContext(), "New email cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Email Change")
                .setMessage("Are you sure you want to change your email to " + newEmail + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with email update
                    updateEmailWithoutVerification(user, newEmail, currentPassword);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User canceled the action
                    dialog.dismiss();
                })
                .show();
    }

    private void updateEmailWithoutVerification(FirebaseUser user, String newEmail, String currentPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        // Re-authenticate user
        user.reauthenticate(credential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                // Update email directly
                user.updateEmail(newEmail).addOnCompleteListener(emailTask -> {
                    if (emailTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Email updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = emailTask.getException() != null
                                ? emailTask.getException().getMessage()
                                : "Failed to update email.";
                        Log.e("DEBUG", "Email update error: " + errorMessage);
                        Toast.makeText(getContext(), "Failed to update email: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String errorMessage = authTask.getException() != null
                        ? authTask.getException().getMessage()
                        : "Re-authentication failed.";
                Log.e("DEBUG", "Re-authentication error: " + errorMessage);
                Toast.makeText(getContext(), "Re-authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}