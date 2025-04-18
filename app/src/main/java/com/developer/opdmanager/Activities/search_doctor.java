package com.developer.opdmanager.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.developer.opdmanager.R;
import com.developer.opdmanager.Utils.LocaleHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class search_doctor extends AppCompatActivity {
    private EditText searchInput;
    private ListView listView;
    private TextView categoryLabel;
    private TextView recentSearchesLabel;
    private ArrayList<String> doctorList = new ArrayList<>();
    private HashMap<String, QueryDocumentSnapshot> doctorDataMap = new HashMap<>();
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore db;
    private LinearLayout Linear;
    private TextView recentSearches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("lang", "default");

        if (!lang.equals("default")) {
            LocaleHelper.setLocale(this, lang);
        }
        setContentView(R.layout.activity_search_doctor);

        TextView search = findViewById(R.id.searchInput);
        Drawable drawable = getResources().getDrawable(R.drawable.search);
        drawable.setBounds(0, 0, 70, 70);  // width and height in pixels
        search.setCompoundDrawables(drawable, null, null, null);

        searchInput = findViewById(R.id.searchInput);
        listView = findViewById(R.id.listView);
        categoryLabel = findViewById(R.id.categoryLabel);
        Linear = findViewById(R.id.categoryLinear);
        recentSearchesLabel = findViewById(R.id.recentSearchesLabel);
        recentSearches = findViewById(R.id.recentSearches);
        db = FirebaseFirestore.getInstance();

        listView.setVisibility(View.GONE);
        // Set up ListView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, doctorList);
        listView.setAdapter(adapter);

        // Add a TextWatcher to filter the list as the user types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDoctors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Load all doctors initially
        searchDoctors("");

        // Handle list item clicks
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String doctorName = doctorList.get(position);
            QueryDocumentSnapshot doctorData = doctorDataMap.get(doctorName);

            if (doctorData != null) {
//                 Extract doctor details
                String name = doctorData.getString("name");
                String specialization = doctorData.getString("specialization");

                // Pass data to DoctorInfoActivity
                Intent intent = new Intent(search_doctor.this, doctor_info_card.class);
                intent.putExtra("name", name);
                intent.putExtra("specialty", specialization);
                startActivity(intent);
            }
        });
    }

    private void searchDoctors(String query) {
        db.collection("doctors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctorList.clear();
                        doctorDataMap.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
                                doctorList.add(name);
                                doctorDataMap.put(name, document);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(search_doctor.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        searchInput.setOnClickListener(v -> showSearchResults());
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showSearchResults();
            }
        });
    }

    private void showSearchResults() {
        // Hide Category and Recent Searches sections
        categoryLabel.setVisibility(View.GONE);
        recentSearchesLabel.setVisibility(View.GONE);
        Linear.setVisibility(View.GONE);
        recentSearches.setVisibility(View.GONE);
        // Show the ListView
        listView.setVisibility(View.VISIBLE);
    }
}
