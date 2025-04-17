package com.developer.opdmanager.Models;
// Review.java
public class Review {
    private String patientId;
    private String doctorId;
    private String review;

    public Review(String patientId, String doctorId, String review) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.review = review;
    }

    // Getters
    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getReview() {
        return review;
    }
}