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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import java.io.IOException;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class NewItemFragment extends Fragment {

    private FirestoreHelper firestoreHelper;
    private String user; // Store username as a class-level variable
    EditText mDes, mCol;
    String item_Category, item_StyleTag, item_Seasonality;
    AppCompatButton addItem;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference mStorageRef;
    private ActivityResultLauncher<Intent> launcher;
    private MaterialCardView selectPhoto;
    private ImageView ItemImageView;
    private Uri ImageUri;
    private Bitmap bitmap;

    String[] category = {"Top", "Bottom", "Outwear", "Shoes", "Hats"};
    String[] styleTag = {"Sport", "Casual", "Formal"};
    String[] seasonality = {"Autumn/Fall", "Spring/Summer", "All season"};
    AutoCompleteTextView categoryDropdown, styleTagDropdown, seasonalityDropdown;



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

        // Dropdown for category
        categoryDropdown = view.findViewById(R.id.textCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, category);
        categoryDropdown.setAdapter(categoryAdapter);
        if (!TextUtils.isEmpty(item_Category)) {
            categoryDropdown.setText(item_Category, false);
        }
        categoryDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                item_Category = adapterView.getItemAtPosition(i).toString();

            }
        });


        //dropdown select style Tag
        styleTagDropdown = view.findViewById(R.id.textStyleTag);
        ArrayAdapter<String> styleTagAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, styleTag);
        styleTagDropdown.setAdapter(styleTagAdapter);
        styleTagDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item_StyleTag = adapterView.getItemAtPosition(i).toString();
            }
        });


        //dropdown select seasonality
        seasonalityDropdown= view.findViewById(R.id.textSeasonality);
        ArrayAdapter<String> seasonalityAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, seasonality);
        seasonalityDropdown.setAdapter(seasonalityAdapter);
        seasonalityDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                item_Seasonality = adapterView.getItemAtPosition(i).toString();

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
        fStorage = FirebaseStorage.getInstance();
        mStorageRef = fStorage.getReference();

        // Initialize FirestoreHelper with fStore
        firestoreHelper = new FirestoreHelper();

        // Retrieve the username from the Intent passed to the Activity
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("username")) {
            user = intent.getStringExtra("username");

        }



        //add item button app compat
        addItem = view.findViewById(R.id.AddItemBtn);
        mDes = view.findViewById(R.id.textDescription);
        mCol = view.findViewById(R.id.textColor);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat = item_Category;
                String tag = item_StyleTag;
                String des = mDes.getText().toString().trim();
                String col = mCol.getText().toString().trim();
                String sea = item_Seasonality;
                boolean liked = false;

                if (cat == null || cat.isEmpty() || tag == null || tag.isEmpty() || des.isEmpty() || col.isEmpty() || sea == null || sea.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (ImageUri == null) {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    if (user != null) {
                        UploadImage(user, cat, tag, des, col, sea, liked);
                    } else {
                        System.out.println("Username not available");
                    }
                }
            }
        });


    }





    //Check the permission to pick images from the gallery
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

    //function to pick image that calls the launcher
    private void PickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //call launcher
        launcher.launch(intent);
    }


    //makes the correct orientation forr the chosen image
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


    //makes the appropriate scale for the chosen image
    private Bitmap scaleBitmapToFitImageView(Bitmap bitmap, ImageView imageView) {

        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();

        // Get original bitmap dimensions
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // Calculate the aspect ratio
        float aspectRatio = (float) bitmapWidth / (float) bitmapHeight;

        int scaledWidth, scaledHeight;

        if (bitmapWidth > bitmapHeight) {
            // Landscape image
            scaledWidth = imageViewWidth;
            scaledHeight = Math.round(imageViewWidth / aspectRatio);
        } else {
            // Portrait or square image
            scaledHeight = imageViewHeight;
            scaledWidth = Math.round(imageViewHeight * aspectRatio);
        }

        // Scale bitmap while maintaining aspect ratio
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }


    //for every image selected calls the above functions and displays it
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

            // Identify the dominant color
            //identifyDominantColor(scaledBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to reset fields
    private void resetFields() {
        // Reset the input fields
        mDes.setText("");  // Clear description
        mCol.setText("");  // Clear color

        // Clear the AutoCompleteTextViews
        categoryDropdown.setText("");  // Clear the selected category
        styleTagDropdown.setText("");  // Clear the selected style tag
        seasonalityDropdown.setText("");  // Clear the selected seasonality


        // Clear the corresponding variables
        item_Category = "";  // Clear the category variable
        item_StyleTag = "";  // Clear the style tag variable
        item_Seasonality = "";  // Clear the seasonality variable



        // Clear the image URI and reset the ImageView
        ImageUri = null;  // Clear the selected image URI
        ItemImageView.setImageResource(R.drawable.round_image_search_24); // Set the default drawable


    }


    /*private void identifyDominantColor(Bitmap bitmap) {
        if (bitmap == null) return;

        // Scale down the image to improve performance
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);

        int[] colorArray = new int[scaledBitmap.getWidth() * scaledBitmap.getHeight()];
        scaledBitmap.getPixels(colorArray, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        int r = 0, g = 0, b = 0;
        int totalPixels = colorArray.length;

        // Loop through all pixels to calculate average color
        for (int color : colorArray) {
            r += Color.red(color);
            g += Color.green(color);
            b += Color.blue(color);
        }

        // Calculate the average RGB values
        r /= totalPixels;
        g /= totalPixels;
        b /= totalPixels;

        // Create a color string in HEX format
        String hexColor = String.format("#%02x%02x%02x", r, g, b);

        // Set the identified color to the EditText (or use elsewhere)
        mCol.setText(hexColor);

        // Optionally, set the background color of the color field for preview
        mCol.setBackgroundColor(Color.rgb(r, g, b));
    }*/



    //upload image into Firebase storage and store Image Url into Firebase firestore
    private void UploadImage(String username, String category, String styleTag, String description, String color, String season, boolean liked) {
        if (ImageUri != null) {
            final StorageReference myRef = mStorageRef.child("wardrobe_images/" + System.currentTimeMillis() + "_" + ImageUri.getLastPathSegment());

            myRef.putFile(ImageUri)
                    .addOnSuccessListener(taskSnapshot -> myRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (uri != null) {
                            String photoUrl = uri.toString();
                            firestoreHelper.addWardrobeItemByUsername(username, category, styleTag, description, color, season, liked, photoUrl);
                            //Toast.makeText(getActivity(), "New item added!", Toast.LENGTH_SHORT).show();
                            resetFields();
                        }
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }




}
