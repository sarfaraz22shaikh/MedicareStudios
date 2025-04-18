package com.developer.opdmanager.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.developer.opdmanager.R;
import com.developer.opdmanager.Utils.LocaleHelper;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {

    Button optionDoctor;
    Button optionPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("lang", "default");

        if (!lang.equals("default")) {
            LocaleHelper.setLocale(this, lang);
        }

        setContentView(R.layout.activity_main);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            showLanguageDialog();
        }
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
    private void showLanguageDialog() {
        String[] languages = {"English", "हिंदी", "System Default"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");

        builder.setItems(languages, (dialog, which) -> {
            String selectedLang;
            if (which == 0) selectedLang = "en";
            else if (which == 1) selectedLang = "hi";
            else selectedLang = "default";

            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            prefs.edit()
                    .putString("lang", selectedLang)
                    .putBoolean("isFirstRun", false) // mark first run complete
                    .apply();

            if (!selectedLang.equals("default")) {
                LocaleHelper.setLocale(this, selectedLang);
            }

            recreate(); // Apply language change
        });

        builder.setCancelable(false); // force user to choose
        builder.show();
    }

}
