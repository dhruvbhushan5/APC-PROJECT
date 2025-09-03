package com.hotel.user.service;

import com.hotel.common.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    
    /**
     * Register a new user
     */
    UserDto register(RegisterRequest request);
    
    /**
     * Authenticate user and generate tokens
     */
    JwtAuthResponse login(LoginRequest request, HttpServletRequest httpRequest);
    
    /**
     * Refresh access token using refresh token
     */
    JwtAuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest);
    
    /**
     * Logout user and revoke tokens
     */
    void logout(String refreshToken);
    
    /**
     * Logout from all devices
     */
    void logoutFromAllDevices(String email);
    
    /**
     * Verify email with token
     */
    void verifyEmail(String token);
    
    /**
     * Send password reset email
     */
    void forgotPassword(String email);
    
    /**
     * Reset password with token
     */
    void resetPassword(String token, String newPassword);
    
    /**
     * Change password for authenticated user
     */
    void changePassword(String email, String currentPassword, String newPassword);
    
    /**
     * Resend email verification
     */
    void resendEmailVerification(String email);
}
