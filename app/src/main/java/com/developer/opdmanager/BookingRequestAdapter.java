package com.developer.opdmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {

    private Context context;
    private List<Bookingrequest> appointmentList;
    private OnAppointmentActionListener listener;

    // Define the interface for handling button clicks
    public interface OnAppointmentActionListener {
        void onAccept(Bookingrequest appointment);
        void onSkip(Bookingrequest appointment);
    }

    // Constructor
    public BookingRequestAdapter(Context context, List<Bookingrequest> appointmentList, OnAppointmentActionListener listener) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookingrequest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookingrequest appointment = appointmentList.get(position);

        // Set text values with null safety checks
        holder.tvPatientName.setText(appointment.getPatientName() != null ? appointment.getPatientName() : "Unknown");
        holder.tvTimeSlot.setText(appointment.getTimeSlot() != null ? appointment.getTimeSlot() : "Time Not Available");



        // Handle button clicks
        holder.btnAccept.setOnClickListener(v -> listener.onAccept(appointment));
        holder.btnSkip.setOnClickListener(v -> listener.onSkip(appointment));
    }
    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvSymptoms, tvTimeSlot;
        ImageView ivPatientPhoto;
        Button btnAccept, btnSkip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvSymptoms = itemView.findViewById(R.id.tv_symptoms);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            ivPatientPhoto = itemView.findViewById(R.id.iv_patient_photo);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnSkip = itemView.findViewById(R.id.btn_skip);
        }
    }
}
