package com.developer.opdmanager.Models;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class SlotModel {
    private String slotId;
    private String startTime;
    private String endTime;
    private int maxBookings;
    private int currentBookings;
    private boolean isBookButtonVisible = false;
    private String doctorId;
    private boolean isUserBooked = false;

    // Constructors
    public SlotModel() {} // Required for Firestore

    public SlotModel(String slotId, String startTime, String endTime,
                     int maxBookings, int currentBookings, String doctorId) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxBookings = maxBookings;
        this.currentBookings = currentBookings;
        this.doctorId = doctorId;
    }

    // Main booking check method
    public Task<Boolean> checkUserBooking() {
        String userId = getCurrentUserId();
        if (userId == null || doctorId == null) {
            return Tasks.forResult(false);
        }

        return FirebaseFirestore.getInstance()
                .collection("doctors")
                .document(doctorId)
                .collection("slots")
                .document(slotId)
                .collection("bookings")
                .whereEqualTo("patientID", userId)
                .whereEqualTo("status", "pending")
                .limit(1)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        isUserBooked = !task.getResult().isEmpty();
                        return isUserBooked;
                    } else {
                        Log.e("SlotModel", "Booking check failed", task.getException());
                        return false;
                    }
                });
    }

    // Alternative synchronous method (call on background thread)
    public boolean checkUserBookingSync() {
        try {
            return Tasks.await(checkUserBooking());
        } catch (Exception e) {
            Log.e("SlotModel", "Sync booking check failed", e);
            return false;
        }
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    // Getters and Setters
    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getMaxBookings() { return maxBookings; }
    public void setMaxBookings(int maxBookings) { this.maxBookings = maxBookings; }

    public int getCurrentBookings() { return currentBookings; }
    public void setCurrentBookings(int currentBookings) { this.currentBookings = currentBookings; }

    public boolean isBookButtonVisible() { return isBookButtonVisible; }
    public void setBookButtonVisible(boolean visible) { isBookButtonVisible = visible; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public boolean isUserBooked() { return isUserBooked; }
}