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

        if (currentUser != null) {
            // User is already logged in, redirect to HomeActivity
            Intent intent = new Intent(doctor_login.this, doctor_dashboard.class);
            startActivity(intent);
            finish();
            return; // Exit this method as we don't need to show the login UI
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (!email.isEmpty() && ! pass.isEmpty()) {
                    // Firebase authentication sign in with email and password
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(doctor_login.this, task -> {
                                if (task.isSuccessful()) {
                                    // Login success, redirect to dashboard
                                    FirebaseUser user = mAuth.getCurrentUser();
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
