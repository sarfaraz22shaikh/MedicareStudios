package com.developer.opdmanager;

public class Booking {
    private String patientName;
    private String timeSlot;
    private int imageResource; // For patient's image

    public Booking(String patientName, String timeSlot, int imageResource) {
        this.patientName = patientName;
        this.timeSlot = timeSlot;
        this.imageResource = imageResource;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public int getImageResource() {
        return imageResource;
    }
}
