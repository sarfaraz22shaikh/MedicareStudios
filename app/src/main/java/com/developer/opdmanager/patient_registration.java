package com.developer.opdmanager;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class patient_registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_registration);
        RadioButton checkboxMale = findViewById(R.id.checkbox_male);
        RadioButton checkboxFemale = findViewById(R.id.checkbox_female);
        TextView gender_view = findViewById(R.id.gender_view);
        checkboxMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxFemale.setChecked(false);
                gender_view.setText("Male");

            }
        });

        checkboxFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxMale.setChecked(false);
                gender_view.setText("Female");
            }
        });
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patient_registration.this,dashboard.class);
                startActivity(intent);
            }
        });
    }
}
