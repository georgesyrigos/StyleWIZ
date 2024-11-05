package com.example.stylewiz_vol2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    TextView textView;
    private EditText userEditText,emailEditText,passwordEditText;

    private boolean isPasswordVisible = false;
    Button signUpBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialization
        TextView textView = findViewById(R.id.textView8);
        userEditText = findViewById(R.id.editTextText);
        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText = findViewById(R.id.editTextTextPasswordSignUp);
        signUpBtn = findViewById(R.id.buttonSignUp);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
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

    public void signup(View view) {
        String username = userEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if(TextUtils.isEmpty(username)) {
            userEditText.setError("Username is Required!");
            return;
        }
        if(TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is Required!");
            return;
        }
        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is Required!");
            return;
        }
        //authenticate user
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("email", email);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(SignUpActivity.this, UserActivity.class); // Change to your desired activity
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish(); // Finish the SignUpActivity so user cannot go back to it
                    }
                });
            }


        });




    }
}