package com.developer.opdmanager.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.opdmanager.Fragments.DoctorHome;
import com.developer.opdmanager.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateSlots extends AppCompatActivity {
    private Button createSlots;
    private Spinner morningSpinner;
    private EditText openingTime;
    private EditText closingTime;
    private EditText hourInput;
    private EditText minInput;
    private Spinner maxPersonSpinner;
    private String doctorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_slots);

        doctorId = getIntent().getStringExtra("doctorId");

        createSlots = findViewById(R.id.create_slot_button);
        morningSpinner = findViewById(R.id.morning_spinner);
        openingTime = findViewById(R.id.opening_time_input);
        closingTime = findViewById(R.id.closing_time_input);
        hourInput = findViewById(R.id.hours_input);
        minInput = findViewById(R.id.mins_input);
        maxPersonSpinner = findViewById(R.id.max_person_spinner);



        createSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p1 = openingTime.getText().toString();
                String p2 = closingTime.getText().toString();
                String p3 = hourInput.getText().toString();
                String p4 = minInput.getText().toString();
                String p5 = maxPersonSpinner.getSelectedItem().toString().trim();
                generateSlots(doctorId,p1,p2,p3,p4,p5);
                Intent intent = new Intent(CreateSlots.this, DoctorHome.class);
                startActivity(intent);
            }
        });
    }
    void generateSlots(String doctorId, String openingTime, String closingTime, String slotHours, String slotMinutes, String maxPersons) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Validate that none of the inputs are empty or null
        if (openingTime == null || openingTime.isEmpty() ||
                closingTime == null || closingTime.isEmpty() ||
                slotHours == null || slotHours.isEmpty() ||
                slotMinutes == null || slotMinutes.isEmpty() ||
                maxPersons == null || maxPersons.isEmpty()) {

            Log.e("Firestore", "Error: One or more input values are empty");
            return; // Stop execution
        }

        try {
            // Convert String inputs to Integers
            int openHour = Integer.parseInt(openingTime);
            int closeHour = Integer.parseInt(closingTime);
            int durationHours = Integer.parseInt(slotHours);
            int durationMinutes = Integer.parseInt(slotMinutes);
            int maxBookings = Integer.parseInt(maxPersons);

            int startHour = openHour;
            int startMinute = 0;

            while (startHour < closeHour) {
                // Format start time (e.g., 09:00 AM)
                String startTime = String.format("%02d:%02d", startHour, startMinute);
                int endHour = startHour + durationHours;
                int endMinute = startMinute + durationMinutes;

                if (endMinute >= 60) {
                    endHour += endMinute / 60;
                    endMinute = endMinute % 60;
                }

                if (endHour > closeHour) break; // Stop when clinic closes

                String endTime = String.format("%02d:%02d", endHour, endMinute);

                // Create slot document
                Map<String, Object> slot = new HashMap<>();
                slot.put("startTime", startTime);
                slot.put("endTime", endTime);
                slot.put("maxBookings", maxBookings);
                slot.put("currentBookings", 0);

                String slotId = db.collection("doctors").document(doctorId)
                        .collection("slots").document().getId(); // Auto ID

                db.collection("doctors").document(doctorId)
                        .collection("slots").document(slotId)
                        .set(slot)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Slot added: " + startTime + " - " + endTime))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error adding slot", e));

                // Move to next slot
                startHour = endHour;
                startMinute = endMinute;
                Toast.makeText(getApplicationContext(), "slot created successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Log.e("Firestore", "Error: Invalid number format", e);
        }
    }

}