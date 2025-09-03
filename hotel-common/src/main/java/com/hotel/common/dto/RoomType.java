package com.hotel.common.dto;

public enum RoomType {
    STANDARD("Standard Room"),
    DELUXE("Deluxe Room"),
    SUITE("Suite"),
    PRESIDENTIAL("Presidential Suite"),
    FAMILY("Family Room"),
    TWIN("Twin Room"),
    DOUBLE("Double Room"),
    SINGLE("Single Room");
    
    private final String displayName;
    
    RoomType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
