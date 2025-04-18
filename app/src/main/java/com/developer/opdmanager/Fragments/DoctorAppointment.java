package com.developer.opdmanager.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.opdmanager.Adapters.ApprovedBookingAdapter;
import com.developer.opdmanager.Models.Bookingrequest;
import com.developer.opdmanager.R;
import com.developer.opdmanager.Utils.BookingFetcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorAppointment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorAppointment extends Fragment implements BookingFetcher.BookingFetchListener {
    private static final String TAG = "DoctorAppointment"; // Updated TAG to reflect the Fragment name
    private RecyclerView recyclerView;
    private ApprovedBookingAdapter adapter;
    private BookingFetcher bookingFetcher; // Use BookingFetcher instead of ApprovedBookingFetcher
    private String doctorId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoctorAppointment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorAppointment.
     */
    public static DoctorAppointment newInstance(String param1, String param2) {
        DoctorAppointment fragment = new DoctorAppointment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_appointment, container, false);
        SpannableString spannableString = new SpannableString("Today's\tAppointments");
        TabStopSpan tabStop = new TabStopSpan.Standard(450); // Adjust the value based on your screen width
        spannableString.setSpan(tabStop, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = view.findViewById(R.id.formaltext);
        textView.setText(spannableString);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // or your data refresh method
            bookingFetcher.fetchApprovedBookings();
            // Optional: stop the refresh animation after some delay
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApprovedBookingAdapter();
        recyclerView.setAdapter(adapter);

        // Fetch doctorId from FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            doctorId = currentUser.getUid();
            Log.d(TAG, "Doctor ID: " + doctorId);

            // Fetch approved bookings using BookingFetcher
            bookingFetcher = new BookingFetcher(doctorId, this);
            Log.d(TAG, "Starting to fetch approved bookings for doctor: " + doctorId);
            bookingFetcher.fetchApprovedBookings();
        } else {
            Log.e(TAG, "No user is logged in");
        }

        return view;
    }

    @Override
    public void onBookingsFetched(List<Bookingrequest> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            Log.w(TAG, "No approved bookings found for the doctor.");
        } else {
            Log.d(TAG, "Received " + bookings.size() + " approved bookings:");
            for (Bookingrequest booking : bookings) {
                Log.d(TAG, "Booking: patientId=" + booking.getPatientId() + ", status=" + booking.getStatus());
            }
        }
        // Update RecyclerView
        adapter.setApprovedBookings(bookings);
    }
}