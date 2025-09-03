package com.hotel.user.controller;

import com.hotel.common.dto.*;
import com.hotel.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and authorization operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and sends email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<UserDto>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("User registration request received for email: {}", request.getEmail());
        
        UserDto user = authService.register(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.hotel.common.dto.ApiResponse.success(
                        "User registered successfully. Please check your email for verification.",
                        user
                ));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "423", description = "Account is locked")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<JwtAuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Login request received for email: {}", request.getEmail());
        
        JwtAuthResponse authResponse = authService.login(request, httpRequest);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Login successful", authResponse)
        );
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT token", description = "Generates new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "403", description = "Refresh token expired")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<JwtAuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        
        log.debug("Token refresh request received");
        
        JwtAuthResponse authResponse = authService.refreshToken(request, httpRequest);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Token refreshed successfully", authResponse)
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidates the current refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> logout(
            @RequestBody(required = false) RefreshTokenRequest request) {
        
        log.info("Logout request received");
        
        String refreshToken = request != null ? request.getRefreshToken() : null;
        authService.logout(refreshToken);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Logout successful")
        );
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", description = "Invalidates all refresh tokens for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out from all devices"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> logoutFromAllDevices(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Logout from all devices request for user: {}", userDetails.getUsername());
        
        authService.logoutFromAllDevices(userDetails.getUsername());
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Logged out from all devices successfully")
        );
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address", description = "Verifies user email using verification token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification token")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> verifyEmail(
            @RequestParam String token) {
        
        log.info("Email verification request received for token: {}", token);
        
        authService.verifyEmail(token);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Email verified successfully")
        );
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification", description = "Sends a new email verification token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification email sent"),
            @ApiResponse(responseCode = "400", description = "Email already verified or user not found")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> resendEmailVerification(
            @RequestParam String email) {
        
        log.info("Resend email verification request for: {}", email);
        
        authService.resendEmailVerification(email);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Verification email sent successfully")
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Sends password reset email to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> forgotPassword(
            @RequestParam String email) {
        
        log.info("Forgot password request for email: {}", email);
        
        authService.forgotPassword(email);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Password reset email sent successfully")
        );
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets user password using reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        
        log.info("Password reset request received for token: {}", token);
        
        authService.resetPassword(token, newPassword);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Password reset successfully")
        );
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Changes user password (requires current password)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Current password is incorrect"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        
        log.info("Password change request for user: {}", userDetails.getUsername());
        
        authService.changePassword(userDetails.getUsername(), currentPassword, newPassword);
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("Password changed successfully")
        );
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns current authenticated user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.hotel.common.dto.ApiResponse<UserDetails>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.debug("Get current user request for: {}", userDetails.getUsername());
        
        return ResponseEntity.ok(
                com.hotel.common.dto.ApiResponse.success("User information retrieved successfully", userDetails)
        );
    }
}
