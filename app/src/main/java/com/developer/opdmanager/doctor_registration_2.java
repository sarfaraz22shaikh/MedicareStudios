package com.developer.opdmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class doctor_registration_2 extends AppCompatActivity {
    private AutoCompleteTextView locationAutoComplete;
    private EditText professional_exp, location, specialization;
    private Button register;
    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private String userId;

    @SuppressLint("CutPasteId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_registration_2);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        initializeViews();

        register.setOnClickListener(v -> {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
            String spec = specialization.getText().toString().trim();
            String experience = professional_exp.getText().toString().trim();
            String locate = location.getText().toString().trim();

//             Fetch user data
            fetchUserData();

//             Validate user inputs and save data
//            if (validateInputs(spec, experience, locate)) {
//                saveUserData(spec, experience, locate);
//            }
        });

        // Get current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("doctors").child(userId);
        }
    }

    private void initializeViews() {
        specialization = findViewById(R.id.specialization);
        professional_exp = findViewById(R.id.professional_exp);
        location = findViewById(R.id.locationAutoComplete);
        register = findViewById(R.id.register);
    }

    private boolean validateInputs(String spec, String experience, String locate) {
        if (spec.isEmpty() || experience.isEmpty() || locate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void saveUserData(String name , String email , String phonenumber , String gender ,  String spec, String experience, String location) {
        Map<String, Object> doctors = new HashMap<>();
        doctors.put("Name", name);
        doctors.put("Email", email);
        doctors.put("PhoneNumber", phonenumber);
        doctors.put("Gender", gender);
        doctors.put("specialization", spec);
        doctors.put("year_of_experience", experience);
        doctors.put("location", location);

        db.collection("doctors")
                .add(doctors)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firestore", "Document added with ID: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.w("Firestore", "Error adding document", e));
        Intent intent = new Intent(doctor_registration_2.this , doctor_login.class);
        startActivity(intent);
        finish();
    }

    private void fetchUserData() {
        if (userId == null) {
            Log.e("Firebase", "User ID is null");
            return;
        }

        DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference()
                .child("Doctors") // Make sure this matches your database structure
                .child(userId)  // Fetch data for the specific user
                .child("email"); // Get the email field

        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.getValue(String.class);
                    Toast.makeText(doctor_registration_2.this, email, Toast.LENGTH_SHORT).show();
                    fetchGender(email);
                } else {
                    Toast.makeText(doctor_registration_2.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching email: " + error.getMessage());
            }
        });
    }


    private void fetchGender(String email) {
        DatabaseReference genderRef = FirebaseDatabase.getInstance().getReference()
                .child("Doctors") // Make sure this matches your database structure
                .child(userId)  // Fetch data for the specific user
                .child("gender");

        genderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gender = snapshot.exists() ? snapshot.getValue(String.class) : "N/A";
                fetchName(email, gender);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching gender: " + error.getMessage());
            }
        });
    }

    private void fetchName(String email, String gender) {

        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference()
                .child("Doctors") // Make sure this matches your database structure
                .child(userId)  // Fetch data for the specific user
                .child("name");


        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.exists() ? snapshot.getValue(String.class) : "N/A";
                fetchPhoneNumber(email, gender, name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching name: " + error.getMessage());
            }
        });
    }

    private void fetchPhoneNumber(String email, String gender, String name) {

        DatabaseReference phoneRef = FirebaseDatabase.getInstance().getReference()
                .child("Doctors") // Make sure this matches your database structure
                .child(userId)  // Fetch data for the specific user
                .child("phoneNumber");

        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phoneNumber = snapshot.exists() ? snapshot.getValue(String.class) : "N/A";
                Toast.makeText(doctor_registration_2.this, email + " " + gender + " " + name + " " + phoneNumber, Toast.LENGTH_SHORT).show();

                String spec = specialization.getText().toString().trim();
                String experience = professional_exp.getText().toString().trim();
                String locate = location.getText().toString().trim();

                    saveUserData( name , email , phoneNumber , gender ,spec, experience, locate);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching phone number: " + error.getMessage());
            }
        });
    }
}
