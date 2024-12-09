package com.example.stylewiz_vol2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EditProfileFragment extends Fragment {
    Button cancelBtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        EditText currentPassEditProfile = view.findViewById(R.id.editTextPasswordEditProfile);
        EditText newPassEditProfile = view.findViewById(R.id.editTextNewPasswordEditProfile);
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
    private void updateUserProfile(String currentPassword, String newEmail, String newUsername, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get credentials for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);



        // Re-authenticate user
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update email if new email is provided
                if (newEmail != null && !newEmail.isEmpty()) {
                    user.updateEmail(newEmail).addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Email updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update email: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

}