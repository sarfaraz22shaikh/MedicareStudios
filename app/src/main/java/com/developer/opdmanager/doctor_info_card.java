package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class doctor_info_card extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_info_card);

        // Retrieve data passed from the search_doctor activity
        String name = getIntent().getStringExtra("name");
        String specialization = getIntent().getStringExtra("speciality");


        // Find the views in the layout to update with the doctor data
        TextView doctorNameTextView = findViewById(R.id.textView22);
        TextView doctorSpecializationTextView = findViewById(R.id.textView21);
        // Assuming this is the doctor's image view

        // Set the data to the UI elements
        if (name != null) {
            doctorNameTextView.setText(name);
        }
        if (specialization != null) {
            doctorSpecializationTextView.setText(specialization);
        }
        Button book_appointment = findViewById(R.id.AppointmentButton);
        book_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_info_card.this,BookingActivity.class);
                startActivity(intent);
            }
        });
    }
}
