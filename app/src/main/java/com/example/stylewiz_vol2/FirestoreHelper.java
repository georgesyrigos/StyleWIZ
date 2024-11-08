package com.example.stylewiz_vol2;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }
    // Method to get userId by username and add a wardrobe item
    public void addWardrobeItemByUsername(String username, String name, String size, String color, String material) {
        // Query Firestore to find the userId by username
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Get the first document ID as userId
                            String userId = task.getResult().getDocuments().get(0).getId();
                            System.out.println("Found userId: " + userId);

                            // Call the method to add the wardrobe item with default values
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
    }


}
