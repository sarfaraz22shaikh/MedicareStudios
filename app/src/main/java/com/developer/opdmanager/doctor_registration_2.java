package com.developer.opdmanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class doctor_registration_2 extends AppCompatActivity {
    Button register;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_registration_2);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_registration_2.this, dashboard.class);
                startActivity(intent);
            }
        });
        TextView specialization = findViewById(R.id.specialization);
        Drawable drawable = getResources().getDrawable(R.drawable.care);
        drawable.setBounds(0, 0, 70, 70);  // width and height in pixels
        specialization.setCompoundDrawables(drawable, null, null, null);
        TextView professional_exp = findViewById(R.id.professional_exp);
        Drawable drawable2 = getResources().getDrawable(R.drawable.consumerresearch);
        drawable2.setBounds(0, 0, 60, 60);  // width and height in pixels
        professional_exp.setCompoundDrawables(drawable2, null, null, null);
        TextView location = findViewById(R.id.location);
        Drawable drawable3 = getResources().getDrawable(R.drawable.pinmap);
        drawable3.setBounds(0, 0, 60, 60);  // width and height in pixels
        location.setCompoundDrawables(drawable3, null, null, null);
    }
}
