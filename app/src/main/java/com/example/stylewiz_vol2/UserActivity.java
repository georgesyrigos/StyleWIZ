package com.example.stylewiz_vol2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

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
                    String name = "Jacket";
                    String size = "M";
                    String color = "Blue";
                    String material = "Leather";

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

        // Method to get userId by username and add a wardrobe item
        /*public void addWardrobeItemByUsername(String username, String name, String size, String color, String material) {
            // Step 1: Get the userId by querying the username
            fStore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // Found the user; get the userId (first match)
                                String userId = task.getResult().getDocuments().get(0).getId();
                                System.out.println("Found userId: " + userId);

                                // Step 2: Use the userId to add the wardrobe item
                                addWardrobeItem(userId, name, size, color, material);
                            } else {
                                System.out.println("No user found with username: " + username);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error getting user ID: " + e.getMessage());
                    });
        }

        // Method to add a wardrobe item to a specific user
        private void addWardrobeItem(String userId, String name, String size, String color, String material) {
            // Reference to the user's wardrobe sub-collection
            CollectionReference wardrobeRef = db.collection("users").document(userId).collection("wardrobe");

            // Create a map for the wardrobe item fields
            Map<String, Object> wardrobeItem = new HashMap<>();
            wardrobeItem.put("name", name);
            wardrobeItem.put("size", size);
            wardrobeItem.put("color", color);
            wardrobeItem.put("material", material);

            // Add the wardrobe item to the wardrobe sub-collection
            wardrobeRef.add(wardrobeItem)
                    .addOnSuccessListener(documentReference -> {
                        System.out.println("Wardrobe item added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error adding wardrobe item: " + e.getMessage());
                    });
        }*/
    }
}