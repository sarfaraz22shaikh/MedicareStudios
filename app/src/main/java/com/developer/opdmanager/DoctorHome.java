package com.developer.opdmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoctorHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoctorHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DoctorHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorHome.
     */
    // TODO: Rename and change types and number of parameters
    public static DoctorHome newInstance(String param1, String param2) {
        DoctorHome fragment = new DoctorHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Button CreateSlot;
    public TextView doctorName;
    private String userId;
    private TextView specialization;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);
        CreateSlot = view.findViewById(R.id.create_slot_button);
        doctorName = view.findViewById(R.id.doctor_name);
        specialization = view.findViewById(R.id.specialization);

        fetchDoctorData();

        CreateSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pogo","pankaj");
                Intent intent = new Intent(getActivity(), CreateSlots.class);
                intent.putExtra("doctorId", userId);
                startActivity(intent);
            }
        });

        return view;
    }
    private void fetchDoctorData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("FirestoreFetch", "Fetching data for User ID: " + userId);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("doctors").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
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
        // passing id to next intent

    }
}