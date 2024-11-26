package com.example.stylewiz_vol2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;

public class NewItemFragment extends Fragment {

    private FirestoreHelper firestoreHelper;
    private String user; // Store username as a class-level variable
    TextView textView;
    Button newItem;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private ActivityResultLauncher<Intent> launcher;
    private MaterialCardView selectPhoto;
    private ImageView ItemImageView;
    private Uri ImageUri;
    private Bitmap bitmap;

    String[] category = {"Top", "Bottom", "Outwear"};
    String[] styleTag = {"Sport", "Casual", "Formal"};
    String[] seasonality = {"Autumn/Fall", "Spring/Summer", "All season"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the ActivityResultLauncher in onCreate
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri newImageUri = data.getData();
                            // Call the method to handle the new image
                            handleNewImage(newImageUri);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_item, container, false);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //dropdown select category
        autoCompleteTextView = view.findViewById(R.id.textCategory);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item, category);

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String item_Category = adapterView.getItemAtPosition(i).toString();

            }
        });


        //dropdown select style Tag
        autoCompleteTextView = view.findViewById(R.id.textStyleTag);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item, styleTag);

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item_StyleTag = adapterView.getItemAtPosition(i).toString();
            }
        });


        //dropdown select seasonality
        autoCompleteTextView = view.findViewById(R.id.textSeasonality);
        adapterItems = new ArrayAdapter<String>(getActivity(),R.layout.list_item, seasonality);

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String item_Seasonality = adapterView.getItemAtPosition(i).toString();

            }
        });




        //Add image on click
        selectPhoto = view.findViewById(R.id.selectImage);
        ItemImageView = view.findViewById(R.id.itemImage);



        //when fragment is changed the image stays on display with the correct orientation
        if (ImageUri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        ImageUri
                );
                if (bitmap != null) {
                    Bitmap orientedBitmap = handleImageOrientation(ImageUri);
                    ItemImageView.setImageBitmap(orientedBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission
                CheckStoragePermission();

            }
        });

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


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save ImageUri to restore later
        if (ImageUri != null) {
            outState.putString("ImageUri", ImageUri.toString());
        }
    }

    private void CheckStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},1);
            }else {
                // Permission already granted, pick image from gallery
                PickFromGallery();
            }
        }else {
            // For older versions, directly pick image from gallery
            PickFromGallery();
        }
    }

    private void PickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //call launcer
        launcher.launch(intent);
    }


    private Bitmap handleImageOrientation(Uri uri) throws IOException {
        InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        // Get the EXIF orientation
        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        int orientation = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                orientation = cursor.getInt(0);
            }
            cursor.close();
        }

        // Rotate bitmap based on orientation
        Matrix matrix = new Matrix();
        if (orientation == 90) {
            matrix.postRotate(90);
        } else if (orientation == 180) {
            matrix.postRotate(180);
        } else if (orientation == 270) {
            matrix.postRotate(270);
        }

        return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
    }


    private Bitmap scaleBitmapToFitImageView(Bitmap bitmap, ImageView imageView) {
        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();

        // Scale the bitmap while maintaining aspect ratio
        float widthRatio = (float) imageViewWidth / bitmap.getWidth();
        float heightRatio = (float) imageViewHeight / bitmap.getHeight();
        float scale = Math.min(widthRatio, heightRatio);

        int scaledWidth = Math.round(bitmap.getWidth() * scale);
        int scaledHeight = Math.round(bitmap.getHeight() * scale);

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }


    private void handleNewImage(Uri imageUri) {
        try {
            // Handle orientation
            Bitmap orientedBitmap = handleImageOrientation(imageUri);

            // Scale the bitmap to fit the ImageView
            Bitmap scaledBitmap = scaleBitmapToFitImageView(orientedBitmap, ItemImageView);

            // Set the processed bitmap to the ImageView
            ItemImageView.setImageBitmap(scaledBitmap);

            // Save the image URI for future use (if necessary, e.g., for saving or restoring)
            ImageUri = imageUri;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //upload image into Firebase storage in store Image Url into Firebase firestore

    //make a method to Upload Image into firebase storage



}
