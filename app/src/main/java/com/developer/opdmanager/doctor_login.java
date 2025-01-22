package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class doctor_login extends AppCompatActivity {
    Button registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_login);
        registration = findViewById(R.id.DocRegister);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_login.this, doctor_registration.class);
                startActivity(intent);
            }
        });
        Button login = findViewById(R.id.loginButton);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("user") && password.getText().toString().equals("1234")){
                    Intent intent = new Intent(doctor_login.this,doctor_dashboard.class);
                    startActivity(intent);
                    Toast.makeText(doctor_login.this, "logged in", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(doctor_login.this, "Wrong id/Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button forgetPassword = findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_login.this, forget_password.class);
                startActivity(intent);
            }
        });
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_login.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
