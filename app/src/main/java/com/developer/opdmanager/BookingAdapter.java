package com.developer.opdmanager;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Bookingrequest> bookings = new ArrayList<>();

    public void setBookings(List<Bookingrequest> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookingrequest, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Bookingrequest booking = bookings.get(position);

        // Set time slot
        String timeSlot = booking.getStartTime() + " - " + booking.getEndTime();
        holder.tvTimeSlot.setText(timeSlot);

        // Set patient name (already fetched in BookingFetcher)
        String patientName = booking.getPatientName() != null ? booking.getPatientName() : "Unknown Patient";
        holder.tvPatientName.setText(patientName);

        // Set symptoms (empty since not in Firestore)
        holder.tvSymptoms.setText("");

        // Set patient photo (using placeholder drawable)
        holder.ivPatientPhoto.setImageResource(R.drawable.user);

        // Set up button click listeners
        holder.btnAccept.setOnClickListener(v -> {

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Approval")
                    .setMessage("Are you sure you want to approve this booking?")
                    .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus(booking, "approved", position, holder))
                    .setNegativeButton("No", null)
                    .show();

        });

        holder.btnSkip.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirm Rejection")
                    .setMessage("Are you sure you want to reject this booking?")
                    .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus(booking, "rejected", position, holder))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void updateBookingStatus(Bookingrequest booking, String newStatus, int position, BookingViewHolder holder) {
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
                    Toast.makeText(holder.itemView.getContext(),
                            "Booking " + (newStatus.equals("approved") ? "Approved" : "Rejected"),
                            Toast.LENGTH_SHORT).show();

                    // Remove the booking from the list since it's no longer "pending"
                    bookings.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookings.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(holder.itemView.getContext(),
                            "Error updating booking: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot, tvPatientName, tvSymptoms;
        ImageView ivPatientPhoto;
        Button btnAccept, btnSkip;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvSymptoms = itemView.findViewById(R.id.tv_symptoms);
            ivPatientPhoto = itemView.findViewById(R.id.iv_patient_photo);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnSkip = itemView.findViewById(R.id.btn_skip);
        }
    }
}