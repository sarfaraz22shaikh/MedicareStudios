package com.developer.opdmanager;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {
    private List<SlotModel> slotList;

    public SlotAdapter(List<SlotModel> slotList) {
        this.slotList = slotList;
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
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView startTime, endTime;
        ImageView arrowButton;
        public SlotViewHolder(View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            arrowButton = itemView.findViewById(R.id.arrowButton);
        }
    }

    private void bookSlot(String slotId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference slotRef = db.collection("doctors").document("doctor123").collection("slots").document(slotId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(slotRef);
            int currentBookings = snapshot.getLong("currentBookings").intValue();
            int maxBookings = snapshot.getLong("maxBookings").intValue();

            if (currentBookings < maxBookings) {
                transaction.update(slotRef, "currentBookings", currentBookings + 1);
                return true;
            } else {
                return false;
            }
        }).addOnSuccessListener(success -> {
            if (success) {
                Log.d("Firestore", "Booking successful");
            } else {
                Log.d("Firestore", "Slot is full");
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Booking failed", e));
    }
}
