package com.developer.opdmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class patient_registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button registerButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_registration);

        // Linking XML fields to Java code
        emailField = findViewById(R.id.button6); // Email field
        passwordField = findViewById(R.id.button10); // Password field
        registerButton = findViewById(R.id.register); // Register button

        // FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // RadioButton logic for gender selection
        RadioButton checkboxMale = findViewById(R.id.checkbox_male);
        RadioButton checkboxFemale = findViewById(R.id.checkbox_female);
        TextView gender_view = findViewById(R.id.gender_view);

        checkboxMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxFemale.setChecked(false);
                gender_view.setText("Male");
            }
        });

        checkboxFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMale.setChecked(false);
                gender_view.setText("Female");
            }
        });

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                // Create new user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Registration successful
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(patient_registration.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Registration failed
                                Toast.makeText(patient_registration.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                // Show error if fields are empty
                Toast.makeText(patient_registration.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}