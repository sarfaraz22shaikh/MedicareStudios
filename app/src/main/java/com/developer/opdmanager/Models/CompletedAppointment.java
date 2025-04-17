package com.developer.opdmanager.Models;

// Appointment.java
public class CompletedAppointment {
    private String doctorName;
    private float rating;
    private String appointmentDate;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    private String specialty;
    private String doctorId;

    public CompletedAppointment(String doctorName, float rating, String appointmentDate, String specialty,String doctorId) {
        this.doctorName = doctorName;
        this.rating = rating;
        this.appointmentDate = appointmentDate;
        this.specialty = specialty;
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public float getRating() {
        return rating;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getSpecialty() {
        return specialty;
    }
}