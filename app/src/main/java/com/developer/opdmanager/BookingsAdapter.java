package com.developer.opdmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.opdmanager.Appointment;
import com.developer.opdmanager.R;

import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    private List<Appointment> appointmentList;

    public BookingsAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.userName.setText("Patient: " + appointment.getUserName());
        holder.slotTime.setText("Time: " + appointment.getSlotTime());
        holder.status.setText("Status: " + appointment.getStatus());
    }



    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, slotTime, status;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            slotTime = itemView.findViewById(R.id.slotTime);
            status = itemView.findViewById(R.id.status);
        }
    }

    // 🔹 Call this method to update the list and refresh RecyclerView
    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointmentList.clear();
        this.appointmentList.addAll(newAppointments);
        notifyDataSetChanged(); // Refresh UI
    }
}
