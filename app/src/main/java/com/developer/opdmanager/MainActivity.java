package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button optionDoctor;
    Button optionPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
