package com.developer.opdmanager;

public class Bookingrequest {
    private String patientName;
    private String timeSlot;

    public Bookingrequest() {
        // Default constructor for Firebase
    }

    public Bookingrequest(String patientName,String timeSlot) {
        this.patientName = patientName;
        this.timeSlot = timeSlot;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}