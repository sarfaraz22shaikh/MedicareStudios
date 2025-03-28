package com.developer.opdmanager;

import com.google.firebase.firestore.PropertyName;

public class Bookingrequest {
    private String patientId;
    private String status;
    private String startTime;
    private String endTime;
    private String patientName;
    private String doctorId;  // Added
    private String slotId;    // Added
    private String bookingId; // Added

    public Bookingrequest() {
    }

    public Bookingrequest(String patientId, String status, String startTime, String endTime, String patientName,
                          String doctorId, String slotId, String bookingId) {
        this.patientId = patientId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.bookingId = bookingId;
    }

    @PropertyName("patientID")
    public String getPatientId() {
        return patientId;
    }

    @PropertyName("patientID")
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("startTime")
    public String getStartTime() {
        return startTime;
    }

    @PropertyName("startTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @PropertyName("endTime")
    public String getEndTime() {
        return endTime;
    }

    @PropertyName("endTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    // Getters and setters for new fields
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}