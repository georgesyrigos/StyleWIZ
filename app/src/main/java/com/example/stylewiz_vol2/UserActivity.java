package com.example.stylewiz_vol2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
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

        // Add all fragments upfront and hide them
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, homeFragment, "HOME")
                .add(R.id.frameLayout, outfitsFragment, "OUTFITS").hide(outfitsFragment)
                .add(R.id.frameLayout, newItemFragment, "NEW_ITEM").hide(newItemFragment)
                .add(R.id.frameLayout, suggestionsFragment, "SUGGESTIONS").hide(suggestionsFragment)
                .add(R.id.frameLayout, profileFragment, "PROFILE").hide(profileFragment)
                .commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (item.getItemId() == R.id.bottom_checkroom) {
                transaction.hide(homeFragment).hide(newItemFragment).hide(suggestionsFragment).hide(profileFragment);
                transaction.show(outfitsFragment);
            } else if (item.getItemId() == R.id.bottom_home) {
                transaction.hide(outfitsFragment).hide(newItemFragment).hide(suggestionsFragment).hide(profileFragment);
                transaction.show(homeFragment);
            } else if (item.getItemId() == R.id.bottom_add) {
                transaction.hide(homeFragment).hide(outfitsFragment).hide(suggestionsFragment).hide(profileFragment);
                transaction.show(newItemFragment);
            } else if (item.getItemId() == R.id.bottom_suggest) {
                transaction.hide(homeFragment).hide(outfitsFragment).hide(newItemFragment).hide(profileFragment);
                transaction.show(suggestionsFragment);
            } else {
                transaction.hide(homeFragment).hide(outfitsFragment).hide(newItemFragment).hide(suggestionsFragment);
                transaction.show(profileFragment);
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



}