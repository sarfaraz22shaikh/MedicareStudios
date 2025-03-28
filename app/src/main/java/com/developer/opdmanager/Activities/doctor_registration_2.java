package com.developer.opdmanager.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.opdmanager.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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




//             Validate user inputs and save data
            if (validateInputs(spec, experience, locate)) {
                Toast.makeText(this, "The data is valid now sending to saveUser", Toast.LENGTH_SHORT).show();
                saveUserData();
            }
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

    public void saveUserData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();  // ✅ Get the authenticated user's UID
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Intent intent = getIntent();
            String name = intent.getStringExtra("name");
            String email = intent.getStringExtra("email");
            String phoneNumber = intent.getStringExtra("phonenum");
            String gender = intent.getStringExtra("gender");
            String spec = specialization.getText().toString().trim();
            String experience = professional_exp.getText().toString().trim();
            String locate = location.getText().toString().trim();

            Map<String, Object> doctors = new HashMap<>();
            doctors.put("name", name);  // ✅ Make sure field names match Firestore
            doctors.put("email", email);
            doctors.put("phoneNumber", phoneNumber);
            doctors.put("gender", gender);
            doctors.put("specialization", spec);
            doctors.put("year_of_experience", experience);
            doctors.put("location", locate);

            // ✅ Store data using UID instead of generating a random ID
            db.collection("doctors").document(userId)
                    .set(doctors)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Doctor data saved successfully with ID: " + userId);
                        Intent intentNext = new Intent(doctor_registration_2.this, doctor_login.class);
                        startActivity(intentNext);
                        finish();
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error saving doctor data", e));
        } else {
            Log.e("Firestore", "No user is logged in. Cannot save doctor data.");
        }
    }


}
