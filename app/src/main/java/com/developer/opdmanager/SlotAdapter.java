package com.developer.opdmanager;

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

import com.google.api.LogDescriptor;
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
        holder.startTime.setText(slot.getStartTime());
        holder.endTime.setText(slot.getEndTime());
        Log.d( "SlotID", "onBindViewHolder: " + slot.getSlotId() );

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
          Toast.makeText( holder.itemView.getContext() , "Book button clicked", Toast.LENGTH_SHORT).show();
          Log.d( "SlotID", "onBindViewHolder: " + slot.getSlotId() );

          bookSlot(doctorId ,  slot.getSlotId() ,currentUser );
      }
  });
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView startTime, endTime;
        ImageView arrowButton;
        Button bookButton;  // Added bookButton reference

        public SlotViewHolder(View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            arrowButton = itemView.findViewById(R.id.arrowButton);
            bookButton = itemView.findViewById(R.id.bookButton);
        }
    }

    void bookSlot(String doctorId, String slotId, String patientId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference slotRef = db.collection("doctors").document(doctorId)
                .collection("slots").document(slotId);

        slotRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long currentBookings = documentSnapshot.getLong("currentBookings");
                long maxBookings = documentSnapshot.getLong("maxBookings");

                if (currentBookings < maxBookings) {
                    String bookingId = slotRef.collection("bookings").document().getId();

                    Map<String, Object> booking = new HashMap<>();
                    booking.put("patientId", patientId);
                    booking.put("status", "pending");  // Waiting for doctor confirmation

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
