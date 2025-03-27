package com.developer.opdmanager;

public class Bookingrequest {
    private String patientName;
    private String symptoms;
    private String timeSlot;
    private String photoUrl;

    public Bookingrequest() {
        // Default constructor for Firebase
    }

    public Bookingrequest(String patientName, String symptoms, String timeSlot, String photoUrl) {
        this.patientName = patientName;
        this.symptoms = symptoms;
        this.timeSlot = timeSlot;
        this.photoUrl = photoUrl;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}