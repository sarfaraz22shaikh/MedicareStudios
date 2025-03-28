package com.developer.opdmanager.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.developer.opdmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class patient_login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button loginButton, forgetPassword, registration;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_login);

        // FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d( "patient login", "onCreate: " + currentUser);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Check in "Doctors" collection
            db.collection("Doctors").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String collectionName = "Doctors";
                    Toast.makeText(getApplicationContext(), "User found in: " + collectionName, Toast.LENGTH_SHORT).show();
                } else {
                    // Check in "Patients" collection if not found in "Doctors"
                    db.collection("Patients").document(userId).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful() && task2.getResult().exists()) {
                            String collectionName = "Patients";
                            Toast.makeText(getApplicationContext(), "User found in: " + collectionName, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(patient_login.this, dashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "User not found in any collection!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

//        if (currentUser != null) {
//            // User is already logged in, redirect to HomeActivity
//            Intent intent = new Intent(patient_login.this, dashboard.class);
//            startActivity(intent);
//            finish();
//            return; // Exit this method as we don't need to show the login UI
//        }

        // Email and password fields
        emailField = findViewById(R.id.emailid);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButtton);
        forgetPassword = findViewById(R.id.forgetPassword);
        registration = findViewById(R.id.patient_Registration);

        // Login button listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    // Firebase authentication sign in with email and password
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(patient_login.this, task -> {
                                if (task.isSuccessful()) {
                                    // Login success, redirect to dashboard
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(patient_login.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                    // Navigate to dashboard
                                    Intent intent = new Intent(patient_login.this, dashboard.class);
                                    startActivity(intent);
                                    finish(); // Taake user wapas login screen pe na aaye
                                } else {
                                    // Login failed, show error message
                                    Toast.makeText(patient_login.this, "Login Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    // Show error if fields are empty
                    Toast.makeText(patient_login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Forget password button listener
        forgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(patient_login.this, forget_password.class);
            startActivity(intent);
        });

        // Registration button listener
        registration.setOnClickListener(v -> {
            Intent intent = new Intent(patient_login.this, patient_registration.class);
            startActivity(intent);
        });

        // Back button to navigate to main screen
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(patient_login.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
