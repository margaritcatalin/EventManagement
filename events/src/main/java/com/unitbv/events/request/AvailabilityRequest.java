package com.unitbv.events.request;

public class AvailabilityRequest {
    Boolean isAvailable;
    String currentEmail;
    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }
}