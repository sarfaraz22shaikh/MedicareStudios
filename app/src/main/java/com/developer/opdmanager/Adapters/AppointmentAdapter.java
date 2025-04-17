package com.developer.opdmanager.Adapters;

// AppointmentAdapter.java
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.Models.Appointment;
import com.developer.opdmanager.Models.CompletedAppointment;
import com.developer.opdmanager.Models.Review;
import com.developer.opdmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private List<CompletedAppointment> appointmentList;
    Context context;
    // Interface for button click events
    public interface OnItemClickListener {
        void onGiveReviewClick(int position);
        void onPayClick(int position);
    }

    public AppointmentAdapter(Context context,List<CompletedAppointment> appointmentList) {
        this.appointmentList = appointmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_booking_adapter, parent, false);
        return new AppointmentViewHolder(view);
    }
    public void onGiveReviewClick(int position) {
        // Handle Give Review button click
        CompletedAppointment appointment = appointmentList.get(position);
        // Example: Show a dialog or start an activity for review
        Log.d("trial review","works correctly");
    }

    public void onPayClick(int position) {
        // Handle Pay button click
        CompletedAppointment appointment = appointmentList.get(position);
        // Example: Start a payment activity
        // Toast.makeText(getContext(), "Pay for " + appointment.getDoctorName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        CompletedAppointment appointment = appointmentList.get(position);
        holder.doctorName.setText(appointment.getDoctorName());
        holder.specialty.setText("Specialty: "+appointment.getSpecialty());
        holder.ratingValue.setText("("+appointment.getRating()+")");
        holder.ratingBar.setRating(appointment.getRating());
        // Set button click listeners
        holder.giveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.write_review_card, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();

                RatingBar ratingBar = dialogView.findViewById(R.id.reviewRatingBar);
                EditText reviewEditText = dialogView.findViewById(R.id.reviewEditText);
                Button submitButton = dialogView.findViewById(R.id.submitReviewButton);
                TextView doctorName = dialogView.findViewById(R.id.reviewDoctorName);

                doctorName.setText("Dr. " + appointmentList.get(holder.getAdapterPosition()).getDoctorName());

                submitButton.setOnClickListener(btn -> {
                    float rating = ratingBar.getRating();
                    String review = reviewEditText.getText().toString().trim();

                    if (rating == 0 || review.isEmpty()) {
                        Toast.makeText(context, "Please enter both rating and review.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("rating_review")
                            .whereEqualTo("doctorId",appointment.getDoctorId())
                            .whereEqualTo("patientId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                                        document.getReference().update("review", review, "rating", rating);
                                        Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                } else {
                                    Toast.makeText(context, "Error updating review", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error finding review document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

                dialog.show();
            }
        });
        holder.payButton.setOnClickListener(v -> onPayClick(position));

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        ImageView doctorAvatar;
        TextView doctorName;
        RatingBar ratingBar;
        TextView ratingValue;
        TextView appointmentDate;
        TextView specialty;
        Button giveReviewButton;
        Button payButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorAvatar = itemView.findViewById(R.id.doctor_avatar);
            doctorName = itemView.findViewById(R.id.doctor_name);
            ratingBar = itemView.findViewById(R.id.doctor_rating);
            ratingValue = itemView.findViewById(R.id.rating_value);
            appointmentDate = itemView.findViewById(R.id.appointment_date);
            specialty = itemView.findViewById(R.id.specialty);
            giveReviewButton = itemView.findViewById(R.id.give_review_button);
            payButton = itemView.findViewById(R.id.pay_button);
        }
    }
}