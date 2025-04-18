package com.developer.opdmanager.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.Adapters.AppointmentAdapter;
import com.developer.opdmanager.Models.Appointment;
import com.developer.opdmanager.Models.CompletedAppointment;
import com.developer.opdmanager.Models.Review;
import com.developer.opdmanager.R;
import com.developer.opdmanager.Utils.LocaleHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AppointmentSectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AppointmentSectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment secondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static appointment_section newInstance(String param1, String param2) {
        appointment_section fragment = new appointment_section();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("lang", "default");

        if (!lang.equals("default")) {
            LocaleHelper.setLocale(getActivity(), lang);
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<CompletedAppointment> appointmentList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Map<String, Review> reviewMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appointment_section, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recycler_view_appointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
//        appointmentList = new ArrayList<>();
//        appointmentList.add(new CompletedAppointment("Dr. John Smith", 4.5f, "April 15, 2025", "Cardiology"));
//        appointmentList.add(new CompletedAppointment("Dr. Emily Johnson", 4.8f, "April 14, 2025", "Neurology"));

//        adapter = new AppointmentAdapter(appointmentList);
//        recyclerView.setAdapter(adapter);
        fetchReviewsForCurrentUser();
        return view;
    }
    private void fetchReviewsForCurrentUser() {

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String patientId = currentUser.getUid();

        db.collection("rating_review")
                .whereEqualTo("patientId", patientId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    appointmentList = new ArrayList<>();

                    if (queryDocumentSnapshots.isEmpty()) {
                        // No reviews found
                        adapter = new AppointmentAdapter(getContext(),appointmentList);
                        recyclerView.setAdapter(adapter);
                        return;
                    }

                    int totalReviews = queryDocumentSnapshots.size();
                    AtomicInteger fetchedCount = new AtomicInteger(0);

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String doctorId = document.getString("doctorId");
                        String review = document.getString("review"); // You can use this if needed

                        db.collection("doctors")
                                .document(doctorId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String doctorName = documentSnapshot.getString("name");
                                        String doctorSpecality = documentSnapshot.getString("specialization");
                                        String ratingValue = documentSnapshot.getString("rating");

                                     float rating = Float.parseFloat(ratingValue);
                                        appointmentList.add(new CompletedAppointment(
                                                doctorName, rating, "April 15, 2025", doctorSpecality , doctorId
                                        ));
                                    }

                                    // Check if all doctor details have been fetched
                                    if (fetchedCount.incrementAndGet() == totalReviews) {
                                        adapter = new AppointmentAdapter(getContext(),appointmentList);
                                        recyclerView.setAdapter(adapter);
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error fetching doctor: " + e.getMessage());
                                    if (fetchedCount.incrementAndGet() == totalReviews) {
                                        adapter = new AppointmentAdapter(getContext(),appointmentList);
                                        recyclerView.setAdapter(adapter);
                                    }
                                });
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching reviews: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error: " + e.getMessage());
                });
    }

}

