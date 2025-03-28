package com.developer.opdmanager.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.R;
import com.developer.opdmanager.Adapters.SlotAdapter;
import com.developer.opdmanager.Models.SlotModel;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class BookingActivity extends AppCompatActivity {

    private TabItem selectDateIcon;
    private LinearLayout LinearLayout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView selectedDateText;
    private RecyclerView recyclerView;
    private SlotAdapter slotAdapter;
    private List<SlotModel> slotList = new ArrayList<>();
    String doctorId;
    String PatientId;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        doctorId = getIntent().getStringExtra("doctor_id");
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        PatientId = currentUser.getUid();
        Log.d("Firestore", "onCreate: " + PatientId + " 1  " + doctorId);


        recyclerView = findViewById(R.id.recyclerViewSlots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        slotAdapter = new SlotAdapter(slotList ,doctorId , PatientId );
        recyclerView.setAdapter(slotAdapter);

        fetchAvailableSlots();
        TextView timeDetail = findViewById(R.id.timeDetail);
        TextView doctorName = findViewById(R.id.DoctorName);
        String name = getIntent().getStringExtra("doctor_name");
        doctorName.setText("Dr " + name);
        timeDetail.setText("Dr "+name + " online booking opens");
        TabLayout tabLayout = findViewById(R.id.tabLayoutDates);

        String today = getFormattedDate(0);
        String tomorrow = getFormattedDate(1);
        String dayAfterTomorrow = getFormattedDate(2);
        if (tabLayout.getTabAt(0) != null) {
            tabLayout.getTabAt(0).setText("Today\n" + today);
        }
        if (tabLayout.getTabAt(1) != null) {
            tabLayout.getTabAt(1).setText("Tomorrow\n" + tomorrow);
        }
        if (tabLayout.getTabAt(2) != null) {
            tabLayout.getTabAt(2).setText("Day After\n" + dayAfterTomorrow);
        }

        selectDateIcon = findViewById(R.id.select_date_icon);
//
//        selectDateIcon.setOnClickListener(view -> showDatePicker());
        // Example: Handling a single slot (Repeat this for other slots)
    }


    private void bookAppointment(String doctorId, String doctorName, String slotTime, TextView slotStatus) {
        if (currentUser == null) {
            Toast.makeText(this, "You need to log in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid(); // Get logged-in user's UID

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Patients").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("name"); // Fetch 'name' from Firestore
                if (userName != null) {
                    saveAppointment(userId, userName, doctorId, doctorName, slotTime, slotStatus);
                } else {
                    Toast.makeText(this, "User name not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User record not found!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to fetch user details!", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveAppointment(String userId, String userName, String doctorId, String doctorName, String slotTime, TextView slotStatus) {
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("doctorId", doctorId);
        appointment.put("doctorName", doctorName);
        appointment.put("userId", userId);
        appointment.put("userName", userName);
        appointment.put("slotTime", slotTime);
        appointment.put("status", "Booked");

        // Instead of setting a document with a fixed ID, use `add()` to generate a unique ID
        db.collection("appointments")
                .add(appointment)  // This will create a new document with a random unique ID
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(BookingActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                    slotStatus.setText("Booked");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingActivity.this, "Failed to book appointment!", Toast.LENGTH_SHORT).show();
                });
    }



    private void checkIfSlotBooked(String slotTime, TextView slotStatus, LinearLayout slotLayout) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("appointments").document(userId + "_" + slotTime)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        slotStatus.setText("Booked");
                        slotLayout.setClickable(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingActivity.this, "Error checking bookings!", Toast.LENGTH_SHORT).show();
                });
    }
    private String getFormattedDate(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd); // Add days to current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
    private void fetchAvailableSlots() {
        doctorId = getIntent().getStringExtra("doctor_id");
        db.collection("doctors").document(doctorId).collection("slots")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    slotList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        SlotModel slot = doc.toObject(SlotModel.class);
                        slot.setSlotId(doc.getId()); // Store slot ID
                        slotList.add(slot);
                    }
                    slotAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching slots", e));
    }

}

//    private void showDatePicker() {
//        final Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
//                    // Update the TextView with the selected date
//                    calendar.set(selectedYear, selectedMonth, selectedDay);
//                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault());
//                    String formattedDate = sdf.format(calendar.getTime());
//                    selectedDateText.setText(formattedDate);
//                }, year, month, day);
//
//        datePickerDialog.show();
//    }


