package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class DoctorHome extends Fragment {

    private Button CreateSlot;
    public TextView doctorName;
    private String userId;
    private TextView specialization;
    private RecyclerView recyclerView;
    private BookingRequestAdapter adapter;
    private List<Bookingrequest> appointmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        CreateSlot = view.findViewById(R.id.create_slot_button);
        doctorName = view.findViewById(R.id.doctor_name);
        specialization = view.findViewById(R.id.specialization);
        recyclerView = view.findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        appointmentList = new ArrayList<>();

        // Dummy data (ensure this data is not empty)
        appointmentList.add(new Bookingrequest("Julie Rick", "", "10 AM - 11 AM", "https://example.com/patient1.jpg"));
        appointmentList.add(new Bookingrequest("John Doe", "", "11 AM - 12 PM", "https://example.com/patient2.jpg"));

        adapter = new BookingRequestAdapter(getContext(), appointmentList, new BookingRequestAdapter.OnAppointmentActionListener() {
            @Override
            public void onAccept(Bookingrequest booking) {
                Toast.makeText(getActivity(), "Accepted: " + booking.getPatientName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSkip(Bookingrequest booking) {
                Toast.makeText(getActivity(), "Skipped: " + booking.getPatientName(), Toast.LENGTH_SHORT).show();
            }
        });


        recyclerView.setAdapter(adapter);

        CreateSlot.setOnClickListener(v -> {
            Log.d("pogo", "pankaj");
            Intent intent = new Intent(getActivity(), CreateSlots.class);
            intent.putExtra("doctorId", userId);
            startActivity(intent);
        });

        fetchDoctorData(); // Fetch doctor data only after UI setup

        return view;
    }

    private void fetchDoctorData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("FirestoreFetch", "Fetching data for User ID: " + userId);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("doctors").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String speciality = documentSnapshot.getString("specialization");

                            // Prevent crash if values are null
                            if (speciality == null) speciality = "No specialization found";
                            specialization.setText(speciality);

                            if (name != null) {
                                String[] nameParts = name.split(" ", 2);
                                if (nameParts.length > 1) {
                                    doctorName.setText("Dr. " + nameParts[0] + "\n" + nameParts[1]);
                                } else {
                                    doctorName.setText("Dr. " + nameParts[0]);
                                }
                            } else {
                                doctorName.setText("No name found");
                            }
                        } else {
                            doctorName.setText("No data available");
                            specialization.setText("No data available");
                            Log.e("FirestoreData", "No document found for userId: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching data", e));
        } else {
            Log.e("FirestoreFetch", "No user is logged in");
        }
    }
}
