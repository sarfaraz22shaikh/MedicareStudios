package com.developer.opdmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home_section#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home_section extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home_section() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment firstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_section newInstance(String param1, String param2) {
        Home_section fragment = new Home_section();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TextView patientName;
    private FirebaseFirestore db;
    private String patientId;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_section, container, false);
        ImageView myImageView = view.findViewById(R.id.search_button);
        patientName = view.findViewById(R.id.textView11);
        patientName.setText("Hello Shaikh");
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        fetchPatientData();

        myImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), search_doctor.class);
                startActivity(intent);
            }
        });
        return view;

    }
    private void fetchPatientData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            patientId = currentUser.getUid();
            Log.d("FirestoreFetch", "Fetching data for patient ID: " + patientId);

            db.collection("Patients").document(patientId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Log.d("FirestoreFetch", "Firestore documentSnapshot received.");

                        if (documentSnapshot.exists()) {
                            Log.d("FirestoreFetch", "Document exists for patient ID: " + patientId);
                            String name = documentSnapshot.getString("name");

                            if (name != null) {
                                patientName.setText("Hello " + name);
                                Log.d("FirestoreFetch", "Fetched patient name: " + name);
                            } else {
                                patientName.setText("No name found");
                                Log.e("FirestoreFetch", "Field 'name' is null");
                            }
                        } else {
                            Log.e("FirestoreFetch", "No document found for patient ID: " + patientId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreFetch", "Error fetching patient data: ", e);
                    });
        } else {
            Log.e("FirestoreFetch", "No user is logged in");
        }
    }

}