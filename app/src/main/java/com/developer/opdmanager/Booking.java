package com.developer.opdmanager;

import java.io.Serializable;

public class Booking implements Serializable {
    private String doctorName;
    private String timing;
    private String position;

    public Booking(String doctorName, String timing, String position) {
        this.doctorName = doctorName;
        this.timing = timing;
        this.position = position;
    }
    // Getters
    public String getDoctorName() { return doctorName; }
    public String getTiming() { return timing; }
    public String getPosition() { return position; }
}
