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
    private TextView specialization;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);
        SpannableString spannableString = new SpannableString("Today's\tAppointments");
        TabStopSpan tabStop = new TabStopSpan.Standard(450); // Adjust the value based on your screen width
        spannableString.setSpan(tabStop, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.formaltext);
        textView.setText(spannableString);
        doctorName = findViewById(R.id.doctor_name);
        specialization = findViewById(R.id.specialization);
        recyclerView = findViewById(R.id.favoritesRecyclerView);

        fetchDoctorData();

        BookingsAdapter adapter = new BookingsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void fetchDoctorData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("FirestoreFetch", "Fetching data for User ID: " + userId);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("doctors").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // ✅ Use the correct field names (check Firestore Console)
                            String name = documentSnapshot.getString("name");  // Changed "Name" → "name"
                            String speciality = documentSnapshot.getString("specialization");

                            if (name != null) {
                                String[] nameParts = name.split(" ", 2);
                                specialization.setText(speciality != null ? speciality : "No specialization found");

                                if (nameParts.length > 1) {
                                    doctorName.setText("Dr. " + nameParts[0] + "\n" + nameParts[1]);
                                } else {
                                    doctorName.setText("Dr. " + nameParts[0]);
                                }
                            } else {
                                doctorName.setText("No name found");
                                specialization.setText("No specialization found");
                            }
                        } else {
                            doctorName.setText("No data available");
                            specialization.setText("No data available");
                            Log.e("FirestoreData", "No document found for userId: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching data", e));
        } else {
            Log.e("FirestoreFetch", "No user is logged in");
        }
    }


}