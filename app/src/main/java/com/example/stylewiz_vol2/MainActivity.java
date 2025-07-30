package com.example.stylewiz_vol2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    private EditText emailEditText,passwordEditText;
    private boolean isPasswordVisible = false;
    Button signInBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialization
        TextView textView = findViewById(R.id.textView4);
        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPasswordLogin);
        signInBtn = findViewById(R.id.buttonLogin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });



        // Set an OnTouchListener on the password EditText to toggle visibility
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get the drawable on the right side
                    int drawableEnd = 2; // Index 2 is for the right drawable
                    if (passwordEditText.getCompoundDrawables()[drawableEnd] != null) {
                        // Calculate if the touch is within bounds of the drawable
                        float touchableAreaStart = passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[drawableEnd].getBounds().width()
                                - passwordEditText.getPaddingEnd();
                        if (event.getRawX() >= touchableAreaStart) {
                            // Toggle password visibility
                            isPasswordVisible = !isPasswordVisible;
                            if (isPasswordVisible) {
                                // Show password
                                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_24, 0);
                            } else {
                                // Hide password
                                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_lock_24, 0, R.drawable.outline_visibility_off_24, 0);
                            }
                            // Move cursor to the end of the text
                            passwordEditText.setSelection(passwordEditText.getText().length());
                            return true; // Indicate the touch event is handled
                        }
                    }
                }
                return false;
            }
        });
    }

    public void signin(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is Required!");
            return;
        }
        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is Required!");
            return;
        }

        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();

                    // Retrieve username from Firestore
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String username = documentSnapshot.getString("username");
                                String email = documentSnapshot.getString("email");

                                // Navigate to User Activity
                                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed to retrieve username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                } else {
                    Toast.makeText(MainActivity.this, "There is no user with this info!", Toast.LENGTH_SHORT).show();
                    passwordEditText.setText("");
                }


            }
        });

    }
}