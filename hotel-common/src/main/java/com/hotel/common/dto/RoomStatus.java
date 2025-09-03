package com.hotel.common.dto;

public enum RoomStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    RESERVED("Reserved"),
    OUT_OF_ORDER("Out of Order"),
    MAINTENANCE("Under Maintenance"),
    CLEANING("Being Cleaned");
    
    private final String displayName;
    
    RoomStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
