package com.developer.opdmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class doctor_dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView doctorName, specialization;
    private BookingsAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();
    private FirebaseFirestore db;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);

        // Styling "Today's Appointments" text
        SpannableString spannableString = new SpannableString("Today's\tAppointments");
        TabStopSpan tabStop = new TabStopSpan.Standard(450);
        spannableString.setSpan(tabStop, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.formaltext);
        textView.setText(spannableString);

        // Initialize UI elements
        doctorName = findViewById(R.id.doctor_name);
        specialization = findViewById(R.id.specialization);
        recyclerView = findViewById(R.id.appointmentsRecyclerView);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        adapter = new BookingsAdapter(appointmentList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch doctor data and appointments
        fetchDoctorData();
    }

    private void fetchDoctorData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            doctorId = currentUser.getUid();
            Log.d("FirestoreFetch", "Fetching data for Doctor ID: " + doctorId);

            db.collection("doctors").document(doctorId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String speciality = documentSnapshot.getString("specialization");

                            if (name != null) {
                                String[] nameParts = name.split(" ", 2);
                                specialization.setText(speciality != null ? speciality : "No specialization found");

                                if (nameParts.length > 1) {
                                    doctorName.setText("Dr. " + nameParts[0] + "\n" + nameParts[1]);
                                } else {
                                    doctorName.setText("Dr. " + nameParts[0]);
                                }
                            } else {
                                doctorName.setText("No name found");
                                specialization.setText("No specialization found");
                            }

                            // Fetch Appointments after doctor data is loaded
                            fetchAppointments();
                        } else {
                            Log.e("FirestoreData", "No document found for doctor ID: " + doctorId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching doctor data", e));
        } else {
            Log.e("FirestoreFetch", "No user is logged in");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchAppointments() {
        if (doctorId == null) {
            Log.e("FirestoreFetch", "Doctor ID is null. Cannot fetch appointments.");
            return;
        }

        db.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("FirestoreFetch", "Total Appointments: " + queryDocumentSnapshots.size()); // Debugging log

                    appointmentList.clear(); // Clear previous data
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Log.d("FirestoreFetch", "Appointment Data: " + document.getData()); // Debugging log

                        Appointment appointment = document.toObject(Appointment.class);
                        if (appointment != null) {
                            appointmentList.add(appointment);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (appointmentList.isEmpty()) {
                        Log.d("FirestoreFetch", "No appointments found for this doctor.");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching appointments", e));
    }
}