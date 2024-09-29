package com.developer.opdmanager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class doctor_registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_registration);
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
        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(doctor_registration.this, doctor_registration_2.class);
                    startActivity(intent);
                }
        });
    }
}
