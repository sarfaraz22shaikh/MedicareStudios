package com.developer.opdmanager.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.Models.Bookingrequest;
import com.developer.opdmanager.R;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApprovedBookingAdapter extends RecyclerView.Adapter<ApprovedBookingAdapter.ApprovedBookingViewHolder> {
    private List<Bookingrequest> approvedBookings = new ArrayList<>();
    public void setApprovedBookings(List<Bookingrequest> bookings) {
        this.approvedBookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApprovedBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approvedbooking, parent, false);
        return new ApprovedBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovedBookingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bookingrequest booking = approvedBookings.get(position);

        // Set patient name
        String patientName = booking.getPatientName() != null ? booking.getPatientName() : "Unknown Patient";
        holder.tvName.setText(patientName);

        // Set time slot
        String timeSlot = booking.getStartTime() + " to " + booking.getEndTime();
        holder.tvTime.setText(timeSlot);

        // Set position (1-based index)
        holder.tvPosition.setText(String.valueOf(position + 1));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Done", Toast.LENGTH_SHORT).show();
                updateBookingStatus(booking, "completed", holder,position);
                createReview( booking.getDoctorId() , booking.getPatientId());
                String s = booking.getPatientId();
                Toast.makeText(v.getContext(), " " + s, Toast.LENGTH_SHORT).show();
                holder.itemView.setVisibility(View.GONE);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                updateBookingStatus(booking, "cancelled", holder, position);
                holder.itemView.setVisibility(View.GONE);
            }
        });
    }
    private void createReview(String doctorId,String patientId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String review = "";
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("doctorId", doctorId);
        reviewMap.put("patientId", patientId);
        reviewMap.put("review", review);
        db.collection("rating_review").add(reviewMap);
    }
    private void updateBookingStatus(Bookingrequest booking, String newStatus, ApprovedBookingAdapter.ApprovedBookingViewHolder holder ,int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String doctorId = booking.getDoctorId();
        String slotId = booking.getSlotId();
        String bookingId = booking.getBookingId();

        if (doctorId == null || slotId == null || bookingId == null) {
            Toast.makeText(holder.itemView.getContext(), "Error: Missing booking details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the status in Firestore
        db.collection("doctors").document(doctorId)
                .collection("slots").document(slotId)
                .collection("bookings").document(bookingId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Show confirmation to the user
                    approvedBookings.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, approvedBookings.size());

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(),
                            "Error updating booking: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return approvedBookings.size();
    }

    static class ApprovedBookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvPosition;
        private Button cancel,Done;
        public ApprovedBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvTime = itemView.findViewById(R.id.time);
            tvPosition = itemView.findViewById(R.id.position);
            cancel = itemView.findViewById(R.id.cancelButton);
            Done = itemView.findViewById(R.id.completedButton);
        }
    }
}