package com.example.stylewiz_vol2;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {
    TextView textViewUsername;
    private String user; // Store username as a class-level variable



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showUserData(view);


    }

    private void showUserData(View view) {
        textViewUsername = view.findViewById(R.id.homeFragment);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirestoreHelper firestoreHelper = new FirestoreHelper();


        if (user != null) {
            String email = user.getEmail();
            if (email!=null){
                firestoreHelper.getUsername(email, new FirestoreHelper.UsernameCallback() {
                    @Override
                    public void onSuccess(String username) {
                        textViewUsername.setText("Welcome "+ username + "!");

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        textViewUsername.setText("Unknown User");
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });




            } else {
                Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No email found", Toast.LENGTH_SHORT).show();
        }


    }
}