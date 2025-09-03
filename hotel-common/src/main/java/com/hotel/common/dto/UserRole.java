package com.hotel.common.dto;

public enum UserRole {
    ADMIN("ROLE_ADMIN", "Administrator with full system access"),
    MANAGER("ROLE_MANAGER", "Hotel manager with booking and room management access"),
    STAFF("ROLE_STAFF", "Hotel staff with limited operational access"),
    CUSTOMER("ROLE_CUSTOMER", "Customer with booking and payment access"),
    GUEST("ROLE_GUEST", "Guest user with read-only access");
    
    private final String authority;
    private final String description;
    
    UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }
    
    public String getAuthority() {
        return authority;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static UserRole fromAuthority(String authority) {
        for (UserRole role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role found for authority: " + authority);
    }
}
