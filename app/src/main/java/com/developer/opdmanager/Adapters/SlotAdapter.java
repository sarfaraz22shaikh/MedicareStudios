package com.developer.opdmanager.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.R;
import com.developer.opdmanager.Models.SlotModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {
    private List<SlotModel> slotList;
    private String doctorId;
    private String currentUser;
    private String startTime;
    private String endTime;
    private String patientName;


    public SlotAdapter(List<SlotModel> slotList , String doctorId , String currentUser) {
        this.slotList = slotList;
        this.currentUser = currentUser;
        this.doctorId = doctorId;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slots, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        SlotModel slot = slotList.get(position);
        boolean isSlotAvailable = slot.getCurrentBookings() < slot.getMaxBookings();

        FirebaseFirestore.getInstance()
                .collection("doctors")
                .document(doctorId)
                .collection("slots")
                .document(slot.getSlotId())
                .collection("bookings")
                .whereEqualTo("patientId", currentUser)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed", error);
                        holder.status.setText("Available");
                        holder.status.setTextColor(Color.parseColor("#4CAF50"));
                        holder.bookButton.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot bookingDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String bookingStatus = bookingDoc.getString("status");

                        if (bookingStatus != null) {
                            switch (bookingStatus.toLowerCase()) {
                                case "approved":
                                    holder.status.setText("Approved");
                                    holder.status.setTextColor(Color.parseColor("#4CAF50"));
                                    holder.bookButton.setVisibility(View.GONE);
                                    break;
                                case "completed":
                                    holder.status.setText("Completed");
                                    holder.status.setTextColor(Color.parseColor("#4CAF50"));
                                    holder.bookButton.setVisibility(View.GONE);
                                    break;
                                case "rejected":
                                    holder.status.setText("Rejected");
                                    holder.status.setTextColor(Color.parseColor("#FF5555"));
                                    holder.bookButton.setVisibility(View.GONE);
                                    break;
                                case "pending":
                                default:
                                    holder.status.setText("Pending");
                                    holder.status.setTextColor(Color.parseColor("#FFA500"));
                                    holder.bookButton.setVisibility(View.GONE);
                                    break;
                            }
                        } else {
                            holder.status.setText("Pending");
                            holder.status.setTextColor(Color.parseColor("#FFA500"));
                            holder.bookButton.setVisibility(View.GONE);
                        }
                    } else if (isSlotAvailable) {
                        holder.status.setText("Available");
                        holder.status.setTextColor(Color.parseColor("#4CAF50"));
                        holder.bookButton.setVisibility(View.VISIBLE);
                    } else {
                        holder.status.setText("Booked");
                        holder.status.setTextColor(Color.parseColor("#FF5555"));
                        holder.bookButton.setVisibility(View.GONE);
                    }
                });
        holder.startTime.setText(slot.getStartTime());
        holder.endTime.setText(slot.getEndTime());
        Log.d("SlotID", "onBindViewHolder: " + slot.getSlotId());

        // Set button visibility based on model state
        holder.bookButton.setVisibility(slot.isBookButtonVisible() ? View.VISIBLE : View.GONE);

        // Handle arrow button click
        holder.arrowButton.setOnClickListener(v -> {
            // Toggle visibility state
            slot.setBookButtonVisible(!slot.isBookButtonVisible());

            // Notify adapter to refresh the specific item
            notifyItemChanged(position);
        });

        // Handle Book button click
        holder.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), "Book button clicked", Toast.LENGTH_SHORT).show();
                Log.d("SlotID", "onBindViewHolder: " + slot.getSlotId());

                bookSlot(doctorId, slot.getSlotId(), currentUser);
                holder.arrowButton.setVisibility(View.GONE);
                holder.bookButton.setVisibility(View.GONE);
                holder.status.setText("Pending");
                holder.status.setTextColor(Color.parseColor("#FFA500"));
            }
        });
    }
    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView startTime, endTime, status;
        ImageView arrowButton;
        Button bookButton;  // Added bookButton reference

        public SlotViewHolder(View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            arrowButton = itemView.findViewById(R.id.arrowButton);
            bookButton = itemView.findViewById(R.id.bookButton);
            status = itemView.findViewById(R.id.status);
        }
    }

    void bookSlot(String doctorId, String slotId, String patientId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference slotRef = db.collection("doctors").document(doctorId)
                .collection("slots").document(slotId);

//        startTime =  db.collection("doctors").document(doctorId)
//                .collection("slots").document(slotId).get("startTime");

        slotRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long currentBookings = documentSnapshot.getLong("currentBookings");
                long maxBookings = documentSnapshot.getLong("maxBookings");
                startTime = documentSnapshot.getString("startTime");
                endTime = documentSnapshot.getString("endTime");
                Log.d("Starttime", "bookSlot: " + startTime + " | " + endTime);

                if (currentBookings < maxBookings) {
                    String bookingId = slotRef.collection("bookings").document().getId();

                    Map<String, Object> booking = new HashMap<>();
                    booking.put("patientId", patientId);
                    booking.put("startTime", startTime);
                    booking.put("endTime", endTime);
                    booking.put("status", "pending");
                    // Waiting for doctor confirmation

                    // Add booking to the subcollection
                    slotRef.collection("bookings").document(bookingId)
                            .set(booking)
                            .addOnSuccessListener(aVoid -> {
                                // Increment currentBookings count

                                slotRef.update("currentBookings", currentBookings + 1)
                                        .addOnSuccessListener(aVoid2 -> Log.d("Firestore", "Booking requested, waiting for confirmation"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error updating slot", e));
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error booking slot", e));
                } else {
                    Log.e("Firestore", "Slot is fully booked");
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching slot", e));
    }
}
