package com.developer.opdmanager;

public class SlotModel {
    private String slotId;
    private String startTime;
    private String endTime;
    private int maxBookings;
    private int currentBookings;

    public SlotModel() { } // Empty constructor for Firestore

    public SlotModel(String slotId, String startTime, String endTime, int maxBookings, int currentBookings) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxBookings = maxBookings;
        this.currentBookings = currentBookings;
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
}
