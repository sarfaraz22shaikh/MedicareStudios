package com.developer.opdmanager.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.opdmanager.R;
import com.google.firebase.auth.FirebaseAuth;

public class forget_password extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_pat);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link XML elements
        emailField = findViewById(R.id.emailField);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Set OnClickListener on the Reset Button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(forget_password.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                } else {
                    // Send Password Reset Email
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(forget_password.this, "Password reset link sent to your email", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(forget_password.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}
