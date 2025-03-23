package com.developer.opdmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class doctor_info_card extends AppCompatActivity {
    private FirebaseFirestore db;
    private String doctorId; // To store fetched doctor ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_info_card);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Retrieve doctor's name from intent
        String name = getIntent().getStringExtra("name");

        // Find the views in the layout
        TextView doctorNameTextView = findViewById(R.id.textView22);
        Button bookAppointment = findViewById(R.id.AppointmentButton);
        ImageView callDial = findViewById(R.id.callDial);


        // Set doctor's name in UI
        if (name != null) {
            doctorNameTextView.setText(name);
            fetchDoctorId(name); // Fetch doctor ID based on name
        }

        // Handle button click for appointment booking
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doctorId != null) {
                    Intent intent = new Intent(doctor_info_card.this, BookingActivity.class);
                    intent.putExtra("doctor_id", doctorId);   // Passing doctor ID
                    intent.putExtra("doctor_name", name);    // Passing doctor name
                    startActivity(intent);
                } else {
                    Toast.makeText(doctor_info_card.this, "Doctor ID not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(doctor_info_card.this, "call dial clicked" + doctorId, Toast.LENGTH_SHORT).show();
                fetchDoctorPhoneNumber(doctorId);

            }
        });
    }

    // Method to fetch doctor ID from Firestore based on name
    private void fetchDoctorId(String doctorName) {
        db.collection("doctors")
                .whereEqualTo("name", doctorName) // Query by doctor name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        doctorId = document.getId(); // Get document ID (Doctor ID)
                        Log.d("DoctorInfo", "Doctor ID: " + doctorId);
                        Toast.makeText(this, "Yes Foundit doctorid" + doctorId, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("DoctorInfo", "Doctor not found");
                        Toast.makeText(this, "Doctor not found!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchDoctorPhoneNumber(String doctorId) {
        db.collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        dialPhoneNumber(phoneNumber);

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DoctorInfo", "Error fetching data", e);
                    Toast.makeText(this, "Error fetching doctor details!", Toast.LENGTH_SHORT).show();
                });
    }
    private void dialPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber)); // Format the number correctly

        startActivity(intent); // Start the dialer
    }


}
