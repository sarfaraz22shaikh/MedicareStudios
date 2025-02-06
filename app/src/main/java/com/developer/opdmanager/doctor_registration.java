package com.developer.opdmanager;
import com.google.firebase.firestore.FirebaseFirestore;

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

public class doctor_registration extends AppCompatActivity {


    private static final String TAG = "PatientRegistration";

    private EditText fullname, emailInput, passwordInput, phonenumber;
    private Button registerButton;
    private RadioGroup genderGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_registration);


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
        databaseReference = database.getReference("Doctors");
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

                registerUser(email,password);

                Intent intent = new Intent(this, doctor_registration_2.class);
                intent.putExtra("name" , name);
                intent.putExtra("email" , email);
                intent.putExtra("phoneNumber" , phonenum);
                intent.putExtra("gender" , gender);

                Toast.makeText(this, "Data is sended through intent", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
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
    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        String userId = mAuth.getCurrentUser().getUid(); // Get the unique Firebase UID
//                        saveUserData(userId, name, phoneNumber, email, gender); // Save data to Firebase
                        Toast.makeText(this, "Authentication complete", Toast.LENGTH_SHORT).show();
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    // Save user data to Firebase Realtime Database
    private void saveUserData(String userId, String name, String phoneNumber, String email, String gender) {
        HashMap<String, Object> doctors = new HashMap<>();
        doctors.put("name", name);
        doctors.put("email", email);
        doctors.put("phoneNumber", phoneNumber);
        doctors.put("gender", gender);  // Add gender data to user data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth.getCurrentUser();
        if(mAuth != null) {
            String uid = mAuth.getUid();

            db.collection("doctors").document(userId).set(doctors)
                    .addOnSuccessListener(unused -> {
                        Log.d(TAG, "User data saved successfully");
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        navigateToDoctor2(); // Navigate to login activity
                    })
                    .addOnFailureListener(this::handleDatabaseError);
        }
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
    private void navigateToDoctor2() {
        Intent intent = new Intent(this, doctor_registration_2.class);
        startActivity(intent);
        finish();
    }
}