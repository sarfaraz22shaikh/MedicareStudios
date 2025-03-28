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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class DoctorHome extends Fragment implements BookingFetcher.BookingFetchListener {

    private Button CreateSlot;
    private TextView doctorName;
    private String userId;
    private TextView specialization;
    private BookingAdapter adapter;
    private RecyclerView recyclerView;
    private static final String TAG = "DoctorHome";
    private BookingFetcher bookingFetcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);

        // Initialize UI components
        CreateSlot = view.findViewById(R.id.create_slot_button);
        doctorName = view.findViewById(R.id.doctor_name);
        specialization = view.findViewById(R.id.specialization);
        recyclerView = view.findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Set up RecyclerView adapter
        adapter = new BookingAdapter();
        recyclerView.setAdapter(adapter);

        // Fetch doctor data (this will set userId)
        fetchDoctorData();

        // Set up Create Slot button
        CreateSlot.setOnClickListener(v -> {
            if (userId != null) {
                Intent intent = new Intent(getActivity(), CreateSlots.class);
                intent.putExtra("doctorId", userId);
                startActivity(intent);
            } else {
                Log.e(TAG, "User ID is null, cannot start CreateSlots activity");
            }
        });

        return view;
    }

    private void fetchDoctorData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "Fetching data for User ID: " + userId);

            // Initialize BookingFetcher after getting userId
            bookingFetcher = new BookingFetcher(userId, this);
            Log.d(TAG, "Starting to fetch pending bookings for doctor: " + userId);
            bookingFetcher.fetchPendingBookings();

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
                            Log.e(TAG, "No document found for userId: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching data", e));
        } else {
            Log.e(TAG, "No user is logged in");
        }
    }

    @Override
    public void onBookingsFetched(List<Bookingrequest> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            Log.w(TAG, "No pending bookings found for the doctor.");
        } else {
            Log.d(TAG, "Received " + bookings.size() + " pending bookings:");
            for (Bookingrequest booking : bookings) {

            }
        }
        // Update RecyclerView
        adapter.setBookings(bookings);
    }
}