package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class doctor_login extends AppCompatActivity {
    Button registration;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_login);
        // FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

//        mAuth =  FirebaseAuth.getInstance().signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("doctors");
        mAuth = FirebaseAuth.getInstance();
        Log.d( "patient login", "onCreate: " + currentUser);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Check in "Doctors" collection
            db.collection("Doctors").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String collectionName = "Doctors";
                    Toast.makeText(getApplicationContext(), "User found in: " + collectionName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(doctor_login.this, doctor_dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Check in "Patients" collection if not found in "Doctors"
                    db.collection("Patients").document(userId).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful() && task2.getResult().exists()) {
                            String collectionName = "Patients";
                            Toast.makeText(getApplicationContext(), "User found in: " + collectionName, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "User not found in any collection!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


        registration = findViewById(R.id.DocRegister);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(doctor_login.this, "Registration button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(doctor_login.this , doctor_registration.class);
                startActivity(intent);
            }
        });
        Button login = findViewById(R.id.loginButton);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        FirebaseAuth finalMAuth = mAuth;
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (!email.isEmpty() && ! pass.isEmpty()) {
                    // Firebase authentication sign in with email and password
                    finalMAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(doctor_login.this, task -> {
                                if (task.isSuccessful()) {
                                    // Login success, redirect to dashboard
                                    FirebaseUser user = finalMAuth.getCurrentUser();
                                    Toast.makeText(doctor_login.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                    // Navigate to dashboard
                                    Intent intent = new Intent(doctor_login.this, doctor_dashboard.class);
                                    startActivity(intent);
                                    finish(); // Taake user wapas login screen pe na aaye
                                } else {
                                    // Login failed, show error message
                                    Toast.makeText(doctor_login.this, "Login Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    // Show error if fields are empty
                    Toast.makeText(doctor_login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_login.this, forget_password.class);
                startActivity(intent);
            }
        });
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_login.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
