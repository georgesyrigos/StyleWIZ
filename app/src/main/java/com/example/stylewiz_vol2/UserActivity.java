package com.example.stylewiz_vol2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    private FirestoreHelper firestoreHelper;
    private String user; // Store username as a class-level variable
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        // Initialize fragments only once
        HomeFragment homeFragment = new HomeFragment();
        OutfitsFragment outfitsFragment = new OutfitsFragment();
        NewItemFragment newItemFragment = new NewItemFragment();
        SuggestionsFragment suggestionsFragment = new SuggestionsFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        EditProfileFragment editProfileFragment = new EditProfileFragment();


        // Add all fragments upfront and hide them
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, homeFragment, "HOME")
                .add(R.id.frameLayout, outfitsFragment, "OUTFITS").hide(outfitsFragment)
                .add(R.id.frameLayout, newItemFragment, "NEW_ITEM").hide(newItemFragment)
                .add(R.id.frameLayout, suggestionsFragment, "SUGGESTIONS").hide(suggestionsFragment)
                .add(R.id.frameLayout, profileFragment, "PROFILE").hide(profileFragment)
                .add(R.id.frameLayout, editProfileFragment, "EDIT_PROFILE").hide(editProfileFragment)
                .commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Clear back stack (removes fragments added with addToBackStack)
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


            // Hide all other fragments first
            transaction.hide(homeFragment)
                    .hide(outfitsFragment)
                    .hide(newItemFragment)
                    .hide(suggestionsFragment)
                    .hide(profileFragment)
                    .hide(editProfileFragment); // Explicitly hide EditProfileFragment


            // Show the selected fragment based on the item clicked
            if (item.getItemId() == R.id.bottom_checkroom) {
                transaction.show(outfitsFragment);
            } else if (item.getItemId() == R.id.bottom_home) {
                transaction.show(homeFragment);
            } else if (item.getItemId() == R.id.bottom_add) {
                transaction.show(newItemFragment);
            } else if (item.getItemId() == R.id.bottom_suggest) {
                transaction.show(suggestionsFragment);
            } else {
                transaction.show(profileFragment); // Default to profileFragment
            }

            transaction.commit();
            return true;
        });

        // Default fragment
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize FirestoreHelper here
        firestoreHelper = new FirestoreHelper();

        // Retrieve the username from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            user = intent.getStringExtra("username");


        }




    }
    private void switchFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment, tag)
                .addToBackStack(null)  // Add to back stack
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Check if EditProfileFragment is visible
        Fragment editProfileFragment = fragmentManager.findFragmentByTag("EDIT_PROFILE");
        if (editProfileFragment != null && editProfileFragment.isVisible()) {
            // Remove EditProfileFragment and show ProfileFragment
            fragmentManager.beginTransaction()
                    .hide(editProfileFragment)
                    .show(fragmentManager.findFragmentByTag("PROFILE"))
                    .commit();
            return; // Stop further back button processing
        }

        // If no specific fragment handling is needed, use default behavior
        super.onBackPressed();
    }



}