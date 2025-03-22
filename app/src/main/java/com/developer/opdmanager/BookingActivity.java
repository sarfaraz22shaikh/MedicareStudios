package com.developer.opdmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class BookingActivity extends AppCompatActivity {

    private TabItem selectDateIcon;
    private LinearLayout LinearLayout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        TextView doctorName = findViewById(R.id.DoctorName);
        String name = getIntent().getStringExtra("doctor_name");
        String doctorId = getIntent().getStringExtra("doctor_id");
        doctorName.setText("Dr " + name);

//        selectDateIcon = findViewById(R.id.select_date_icon);
//
//        selectDateIcon.setOnClickListener(view -> showDatePicker());
        // Example: Handling a single slot (Repeat this for other slots)
        LinearLayout slot2 = findViewById(R.id.slot2); // Replace with your actual ID
        final TextView status2 = findViewById(R.id.status2); // Replace with your actual ID

        slot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status2.getText().toString().equals("Available")) {
                    // Show a success Toast message
                    Toast.makeText(BookingActivity.this, "You booked the slot 10:30 AM - 11:00 AM successfully!", Toast.LENGTH_SHORT).show();

                    // Change the text to "Booked"
                    status2.setText("Booked");

                    bookAppointment(doctorId, name, "10:00 AM - 10:30 PM", status2);
                } else {
                    // Show an error if the slot is already booked
                    Toast.makeText(BookingActivity.this, "This slot is already booked!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // slot 3

        LinearLayout slot3 = findViewById(R.id.slot3); // Replace with your actual ID
        final TextView slot3status = findViewById(R.id.slot3status); // Replace with your actual ID

        slot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slot3status.getText().toString().equals("Available")) {
                    // Show a success Toast message
                    Toast.makeText(BookingActivity.this, "You booked the slot 11:00 AM - 12:30 AM successfully!", Toast.LENGTH_SHORT).show();

                    // Change the text to "Booked"
                    slot3status.setText("Booked");
//                    checkIfSlotBooked("11:00 AM - 12:30 PM", slot3status, slot3);
                    bookAppointment(doctorId, name, "11:00 AM - 12:30 PM", slot3status);
                } else {
                    // Show an error if the slot is already booked
                    slot3status.setText("Booked");
                    Toast.makeText(BookingActivity.this, "This slot is already booked!", Toast.LENGTH_SHORT).show();
                }
            }
        });


//   slot4
        LinearLayout slot4 = findViewById(R.id.slot4); // Replace with your actual ID
        final TextView slot4status = findViewById(R.id.slot4status); // Replace with your actual ID

        slot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slot4status.getText().toString().equals("Available")) {
                    // Show a success Toast message
                    Toast.makeText(BookingActivity.this, "You booked the slot 11:00 AM - 12:30 AM successfully!", Toast.LENGTH_SHORT).show();

                    // Change the text to "Booked"
                    slot4status.setText("Booked");

                    bookAppointment(doctorId, name, "02:00 AM - 02:30 PM", slot4status);
                } else {
                    // Show an error if the slot is already booked
                    Toast.makeText(BookingActivity.this, "This slot is already booked!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


