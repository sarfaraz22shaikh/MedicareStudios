package com.developer.opdmanager.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.opdmanager.Activities.search_doctor;
import com.developer.opdmanager.R;
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
        patientName.setText("Hello ");

        // button code
        RelativeLayout popupOverlay = view.findViewById(R.id.popupOverlay);
        LinearLayout popupBox = view.findViewById(R.id.popupBox);
        TextView symptomTitle = view.findViewById(R.id.symptomTitle);
        TextView nameTitle = view.findViewById(R.id.textView11);
        TextView symptomDescription = view.findViewById(R.id.symptomDescription);
        Button btnClosePopup = view.findViewById(R.id.btnClosePopup);

        Button btnFever = view.findViewById(R.id.btnFever);
        Button btnCough = view.findViewById(R.id.btnCough);
        Button btnSnuffle = view.findViewById(R.id.btnSnuffle);





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





        // button code

        // Assign click listeners
        setPopupButton(view, btnFever, popupOverlay, popupBox, symptomTitle, symptomDescription,
                "Fever", "Fever is a temporary increase in body temperature, often due to an illness.");

        setPopupButton(view, btnCough, popupOverlay, popupBox, symptomTitle, symptomDescription,
                "Cough", "A cough is a reflex that helps clear the airways of mucus and irritants.");

        setPopupButton(view, btnSnuffle, popupOverlay, popupBox, symptomTitle, symptomDescription,
                "Snuffle", "A snuffle is a mild nasal congestion often caused by allergies or a cold.");

        // Close button listener
        btnClosePopup.setOnClickListener(v -> closePopup(popupOverlay,popupBox));










        return view;

    }

    // Reusable function to show popup
    private void setPopupButton(View view, Button button, RelativeLayout popupOverlay, LinearLayout popupBox,
                                TextView symptomTitle, TextView symptomDescription,
                                String title, String description) {
        button.setOnClickListener(v -> {
            symptomTitle.setText(title);
            symptomDescription.setText(description);

            popupOverlay.setVisibility(View.VISIBLE);
            popupBox.setVisibility(View.VISIBLE);

            // Fade-in animation for background
            AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(300);
            popupOverlay.startAnimation(fadeIn);

            // Scale-in animation for popup
            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    0.5f, 1.0f, 0.5f, 1.0f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(300);
            scaleAnimation.setFillAfter(true);
            popupBox.startAnimation(scaleAnimation);
        });
    }

    // Reusable function to close popup
    private void closePopup(RelativeLayout popupOverlay, LinearLayout popupBox) {
        // Fade-out animation for background
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(300);
        popupOverlay.startAnimation(fadeOut);

        // Scale-out animation for popup
        ScaleAnimation scaleOut = new ScaleAnimation(
                1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleOut.setDuration(200);
        popupBox.startAnimation(scaleOut);

        popupOverlay.postDelayed(() -> {
            popupOverlay.setVisibility(View.GONE);
            popupBox.setVisibility(View.GONE);
        }, 300);
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
                                String[] nameParts = name.split(" ", 2);
                                patientName.setText("Hello " + nameParts[0]);
                            }
                        else {
                            patientName.setText("No name found");
                        }


//                            if (name != null) {
//                                patientName.setText("Hello " + name);
//                                Log.d("FirestoreFetch", "Fetched patient name: " + name);
//                            } else {
//                                patientName.setText("No name found");
//                                Log.e("FirestoreFetch", "Field 'name' is null");
//                            }
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