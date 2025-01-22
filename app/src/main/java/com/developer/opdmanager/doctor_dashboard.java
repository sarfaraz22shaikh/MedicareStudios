package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TabStopSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class doctor_dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);
        SpannableString spannableString = new SpannableString("Today's\tAppointments");
        TabStopSpan tabStop = new TabStopSpan.Standard(450); // Adjust the value based on your screen width
        spannableString.setSpan(tabStop, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.formaltext);
        textView.setText(spannableString);

        recyclerView = findViewById(R.id.favoritesRecyclerView);

    }
}