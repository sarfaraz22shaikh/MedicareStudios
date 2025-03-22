package com.developer.opdmanager;

public class Appointment {
    private String doctorId;
    private String doctorName;
    private String slotTime;
    private String status;
    private String userId;
    private String userName;

    // Empty constructor required for Firestore
    public Appointment() {}

    // Getters
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getSlotTime() { return slotTime; }
    public String getStatus() { return status; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }

    // Setters (Required for Firestore)
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }
    public void setStatus(String status) { this.status = status; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
}
