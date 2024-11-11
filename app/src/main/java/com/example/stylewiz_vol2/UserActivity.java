package com.example.stylewiz_vol2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    TextView textView;
    Button addBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        HomeFragment homeFragment = new HomeFragment();
        OutfitsFragment outfitsFragment = new OutfitsFragment();
        NewItemFragment newItemFragment = new NewItemFragment();
        SuggestionsFragment suggestionsFragment = new SuggestionsFragment();

        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.bottom_checkroom){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout,outfitsFragment)
                            .commit();

                    return true;

                }
                else if (item.getItemId()==R.id.bottom_home){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout,homeFragment)
                            .commit();

                    return true;
                }
                else if (item.getItemId()==R.id.bottom_add){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout,newItemFragment)
                            .commit();

                    return true;
                }
                else if (item.getItemId()==R.id.bottom_suggest){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout,suggestionsFragment)
                            .commit();

                    return true;
                }
                else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout,newItemFragment)
                            .commit();

                    return true;

                }


            }
        });
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        TextView logout = findViewById(R.id.textView11);

        addBtn = findViewById(R.id.buttonAdd);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize FirestoreHelper here
        firestoreHelper = new FirestoreHelper();

        // Retrieve the username from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            user = intent.getStringExtra("username");

            // Display the username in a TextView
            TextView textViewUsername = findViewById(R.id.textView10);
            if (textViewUsername != null) {
                textViewUsername.setText("Welcome " + user+"!");
            }
        }


        // Set up the button to add a wardrobe item with default values
        Button addButton = findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the class-level username variable here
                if (user != null) {
                    // Define default values for the wardrobe item
                    String name = "T-Shirt";
                    String size = "S";
                    String color = "Black";
                    String material = "Cotton";

                    // Call Firestore helper function to add wardrobe item using the username

                    firestoreHelper.addWardrobeItemByUsername(user, name, size, color, material);
                } else {
                    System.out.println("Username not available.");
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Sign out the user
                startActivity(new Intent(UserActivity.this, MainActivity.class)); // Redirect to LoginActivity
                finish(); // Close current activity
            }
        });


    }

}