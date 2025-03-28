package com.developer.opdmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.Models.Bookingrequest;
import com.developer.opdmanager.R;

import java.util.ArrayList;
import java.util.List;

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
    public void onBindViewHolder(@NonNull ApprovedBookingViewHolder holder, int position) {
        Bookingrequest booking = approvedBookings.get(position);

        // Set patient name
        String patientName = booking.getPatientName() != null ? booking.getPatientName() : "Unknown Patient";
        holder.tvName.setText(patientName);

        // Set time slot
        String timeSlot = booking.getStartTime() + " to " + booking.getEndTime();
        holder.tvTime.setText(timeSlot);

        // Set position (1-based index)
        holder.tvPosition.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return approvedBookings.size();
    }

    static class ApprovedBookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvPosition;

        public ApprovedBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvTime = itemView.findViewById(R.id.time);
            tvPosition = itemView.findViewById(R.id.position);
        }
    }
}