package com.developer.opdmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class patient_registration extends AppCompatActivity {

    private static final String TAG = "PatientRegistration";

    private EditText fullname, emailInput, passwordInput, phonenumber;
    private Button registerButton;
    private RadioGroup genderGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_registration);

        // Initialize UI elements
        initializeViews();

        // Initialize Firebase Authentication and Database Reference
        initializeFirebase();

        // Set up the register button click event
        setupRegisterButton();
    }

    // Initialize UI components
    private void initializeViews() {
        fullname = findViewById(R.id.fullname);
        phonenumber = findViewById(R.id.phonenumber);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.register);
        genderGroup = findViewById(R.id.gender_group);
    }

    // Initialize Firebase
    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://opd-manager-f48cd-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = database.getReference("Patients");
    }

    // Handle register button click event
    private void setupRegisterButton() {
        registerButton.setOnClickListener(v -> {
            String name = fullname.getText().toString().trim();
            String phonenum = phonenumber.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Get the selected gender
            String gender = getSelectedGender();

            // Validate user inputs
            if (validateInputs(name, phonenum, email, password, gender)) {
                registerUser(name, phonenum, email, password, gender);
            }
        });
    }

    // Validate inputs before registration
    private boolean validateInputs(String name, String phonenum, String email, String password, String gender) {
        if (name.isEmpty() || phonenum.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Get selected gender from RadioGroup
    private String getSelectedGender() {
        int selectedId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedId);
        return selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";
    }

    // Register user in Firebase Authentication and save data in Realtime Database
    private void registerUser(String name, String phoneNumber, String email, String password, String gender) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid(); // Get the unique Firebase UID
                        saveUserData(userId, name, phoneNumber, email, gender); // Save data to Firebase
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    // Save user data to Firebase Realtime Database
    private void saveUserData(String userId, String name, String phoneNumber, String email, String gender) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("gender", gender);  // Add gender data to user data

        databaseReference.child(userId).setValue(userMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "User data saved successfully");
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    navigateToLogin(); // Navigate to login activity
                })
                .addOnFailureListener(this::handleDatabaseError);
    }

    // Handle registration errors
    private void handleRegistrationError(Exception exception) {
        Log.e(TAG, "Registration Failed", exception);
        Toast.makeText(this, "Registration Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // Handle database save errors
    private void handleDatabaseError(Exception exception) {
        Log.e(TAG, "Database Save Failed", exception);
        Toast.makeText(this, "Failed to save data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // Navigate to login screen after successful registration
    private void navigateToLogin() {
        Intent intent = new Intent(this, patient_login.class);
        startActivity(intent);
        finish();
    }
}
