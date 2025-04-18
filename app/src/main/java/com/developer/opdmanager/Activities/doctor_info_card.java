package com.developer.opdmanager.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.Adapters.ReviewAdapter;
import com.developer.opdmanager.Models.ReviewModel;
import com.developer.opdmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class doctor_info_card extends AppCompatActivity {
    private FirebaseFirestore db;
    private String doctorId;
    RecyclerView recyclerView;
    TextView doctorNameTextView;
    Button bookAppointment;
    ImageView callDial;
    String name;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_info_card);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        doctorNameTextView = findViewById(R.id.textView22);
        bookAppointment = findViewById(R.id.AppointmentButton);
        callDial = findViewById(R.id.callDial);

        // Get doctor name from Intent
        name = getIntent().getStringExtra("name");

        if (name != null) {
            doctorNameTextView.setText(name);
            fetchDoctorId(name);
        }

        // Book Appointment button click
        bookAppointment.setOnClickListener(v -> {
            if (doctorId != null) {
                Intent intent = new Intent(doctor_info_card.this, BookingActivity.class);
                intent.putExtra("doctor_id", doctorId);
                intent.putExtra("doctor_name", name);
                startActivity(intent);
            } else {
                Toast.makeText(doctor_info_card.this, "Doctor ID not found!", Toast.LENGTH_SHORT).show();
            }
        });

        // Call Dial button click
        callDial.setOnClickListener(v -> {
            if (doctorId != null) {
                fetchDoctorPhoneNumber(doctorId);
            } else {
                Toast.makeText(doctor_info_card.this, "Doctor ID not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Fetch Doctor ID from Firestore
    private void fetchDoctorId(String doctorName) {
        db.collection("doctors")
                .whereEqualTo("name", doctorName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        doctorId = document.getId();
                        Log.d("DoctorInfo", "Doctor ID: " + doctorId);
                        Toast.makeText(this, "Doctor found: " + doctorId, Toast.LENGTH_SHORT).show();

                        // Fetch Reviews only after doctorId is fetched
                        fetchReviews(doctorId);

                    } else {
                        Log.e("DoctorInfo", "Doctor not found");
                        Toast.makeText(this, "Doctor not found!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchPatientName(String patientId, PatientNameCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Patients")
                .document(patientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String patientName = task.getResult().getString("name");
                        callback.onPatientNameFetched(patientName);
                    } else {
                        callback.onPatientNameFetched("Unknown Patient");
                    }
                });
    }



    // Fetch Reviews from Firestore
    private void fetchReviews(String doctorId) {
        List<ReviewModel> reviewList = new ArrayList<>();

        db.collection("rating_review")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String patientId = document.getString("patientId");
                        String reviewText = document.getString("review");
                        Long ratingLong = document.getLong("rating");
                        float rating = ratingLong != null ? ratingLong.floatValue() : 0f;

                        // Callback se naam lekar ReviewModel me daal
                        fetchPatientName(patientId, patientName -> {
                            reviewList.add(new ReviewModel(patientName, rating, reviewText));

                            // Adapter sirf jab pura data aa jaye tab set kar — ya ek kaam yeh bhi kar sakte
                            if (reviewList.size() == queryDocumentSnapshots.size()) {
                                ReviewAdapter adapter = new ReviewAdapter(reviewList);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No reviews found!", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("DoctorInfo", "Error fetching reviews", e);
                    Toast.makeText(this, "Failed to load reviews: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public interface PatientNameCallback {
        void onPatientNameFetched(String patientName);
    }


    //    private void getPatientNameById(String patientId) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // 'Patients' collection se document id se fetch karenge
//        DocumentReference docRef = db.collection("Patients").document(patientId);
//
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    String name = documentSnapshot.getString("name");
//                    Log.d("PatientName", "Patient Name: " + name);
//                    // yaha aap chahe to TextView pe set kar sakte ho
//                    // patientNameTextView.setText(name);
//                } else {
//                    Log.d("PatientName", "No such document");
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("PatientName", "Failed to fetch: " + e.getMessage());
//            }
//        });
//    }
    // Fetch Doctor's Phone Number
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
                    Log.e("DoctorInfo", "Error fetching phone number", e);
                    Toast.makeText(this, "Error fetching doctor details!", Toast.LENGTH_SHORT).show();
                });
    }


    // Dial the given Phone Number
    private void dialPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
