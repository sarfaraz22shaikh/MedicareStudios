package com.developer.opdmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    private List<Booking> bookingList = new ArrayList<>();
    private FirebaseFirestore db;

    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String todayString = today.format(formatter);


    public BookingsAdapter() {
        db = FirebaseFirestore.getInstance();
        fetchDoctorAppointments("8JVZXzZi4H4LLH9caBax");
    }

    private void fetchDoctorAppointments(String doctorId) {
        db.collection("Appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("date", todayString)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookingList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("userId");
                            String timeSlot = "8:00 to 8:15";

                            // Fetch patient name using userId
                            db.collection("Users").document(userId).get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (userDoc.exists()) {
                                            String patientName = userDoc.getString("Name");
                                            int imageResource = R.drawable.user; // Default image
                                            bookingList.add(new Booking(patientName, timeSlot, imageResource));
                                            notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.name.setText(booking.getPatientName());
        holder.time.setText(booking.getTimeSlot());
        holder.position.setText(String.valueOf(position + 1));
        holder.imageView.setImageResource(booking.getImageResource()); // Set patient image
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, position;
        ShapeableImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            position = itemView.findViewById(R.id.position);
            imageView = itemView.findViewById(R.id.imageView10);
        }
    }
}
