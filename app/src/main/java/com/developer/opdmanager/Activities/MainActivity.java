package com.developer.opdmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.developer.opdmanager.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {

    Button optionDoctor;
    Button optionPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);
        optionDoctor = findViewById(R.id.optionDoctor);
        optionPatient = findViewById(R.id.optionPatient);




        optionDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, doctor_login.class);
                startActivity(intent);
            }
        });
        optionPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, patient_login.class);
                startActivity(intent);
            }
        });
    }
}
