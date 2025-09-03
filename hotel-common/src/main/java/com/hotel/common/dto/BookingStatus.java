package com.hotel.common.dto;

/**
 * Enumeration for booking status
 */
public enum BookingStatus {
    PENDING("Pending Confirmation"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked In"),
    CHECKED_OUT("Checked Out"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show");
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == CHECKED_IN;
    }
    
    public boolean isCompleted() {
        return this == CHECKED_OUT || this == CANCELLED || this == NO_SHOW;
    }
}
