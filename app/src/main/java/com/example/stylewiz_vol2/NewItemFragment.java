package com.example.stylewiz_vol2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class NewItemFragment extends Fragment {

    private FirestoreHelper firestoreHelper;
    private String user; // Store username as a class-level variable
    TextView textView;
    Button newItem;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private MaterialCardView selectPhoto;
    private ImageView ItemImageView;
    private Uri ImageUri;
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_item, container, false);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //Add image on click
        selectPhoto = view.findViewById(R.id.selectImage);
        ItemImageView = view.findViewById(R.id.itemImage);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission
                CheckStoragePermission();

                PickFromGallery();

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

    private void CheckStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else {
                //method to pick image from gallery
                PickFromGallery();
            }
        }else {
            //method to pick image from gallery
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
    ActivityResultLauncher<Intent> launcher
            =registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
            result -> {
                        if (result.getResultCode()== Activity.RESULT_OK){
                            Intent data=result.getData();
                            if(data!= null && data.getData()!=null){

                                ImageUri=data.getData();

                                //convert image to bitmap
                                try {
                                    bitmap= MediaStore.Images.Media.getBitmap(
                                            getActivity().getContentResolver(),
                                            ImageUri
                                    );
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                            //set image into imageview
                            if (ImageUri!=null){
                                ItemImageView.setImageBitmap(bitmap);

                            }



                        }

            }

    );

    //upload image into Firebase storage in store Image Url into Firebase firestore

    //make a method to Upload Image into firebase storage



}
