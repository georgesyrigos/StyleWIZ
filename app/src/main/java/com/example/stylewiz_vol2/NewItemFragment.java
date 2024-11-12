package com.example.stylewiz_vol2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewItemFragment extends Fragment {

    private FirestoreHelper firestoreHelper;
    private String user; // Store username as a class-level variable
    TextView textView;
    Button newItem;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_item, container, false);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


        // Initialize Firebase instances
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize FirestoreHelper with fStore
        firestoreHelper = new FirestoreHelper();

        // Retrieve the username from the Intent passed to the Activity
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("username")) {
            user = intent.getStringExtra("username");

            // Display the username in a TextView
            TextView textViewUsername = view.findViewById(R.id.textView10);
            if (textViewUsername != null) {
                textViewUsername.setText("Welcome " + user + "!");
            }
        }



        // Set up the button to add a wardrobe item with default values
        newItem = view.findViewById(R.id.buttonNew);
        newItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the class-level username variable here
                if (user != null) {
                    // Define default values for the wardrobe item
                    String name = "Sweatshirt";
                    String size = "M";
                    String color = "Blue";
                    String material = "Cotton";

                    // Call Firestore helper function to add wardrobe item using the username

                    firestoreHelper.addWardrobeItemByUsername(user, name, size, color, material);
                } else {
                    System.out.println("Username not available.");
                }
            }
        });

    }
}
