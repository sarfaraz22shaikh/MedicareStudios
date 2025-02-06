package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TabStopSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class doctor_dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    public TextView doctorName;
    private String userId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);
        SpannableString spannableString = new SpannableString("Today's\tAppointments");
        TabStopSpan tabStop = new TabStopSpan.Standard(450); // Adjust the value based on your screen width
        spannableString.setSpan(tabStop, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.formaltext);
        textView.setText(spannableString);
        doctorName = findViewById(R.id.doctor_name);
        TextView specialization = findViewById(R.id.specialization);
        recyclerView = findViewById(R.id.favoritesRecyclerView);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get user ID

        }
        BookingsAdapter adapter = new BookingsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}