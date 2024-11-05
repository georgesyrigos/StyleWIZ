package com.example.stylewiz_vol2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    private EditText passwordEditText;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView4);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the password EditText
        passwordEditText = findViewById(R.id.editTextTextPassword2);

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
}