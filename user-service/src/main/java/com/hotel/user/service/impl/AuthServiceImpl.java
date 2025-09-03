package com.hotel.user.service.impl;

import com.hotel.common.dto.*;
import com.hotel.user.entity.RefreshToken;
import com.hotel.user.entity.User;
import com.hotel.user.entity.UserRoleAssignment;
import com.hotel.user.mapper.UserMapper;
import com.hotel.user.repository.RefreshTokenRepository;
import com.hotel.user.repository.UserRepository;
import com.hotel.user.security.JwtTokenProvider;
import com.hotel.user.service.AuthService;
import com.hotel.user.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final EmailService emailService;
    
    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .enabled(true)
                .emailVerified(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .emailVerificationToken(UUID.randomUUID().toString())
                .emailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();
        
        // Assign default CUSTOMER role
        UserRoleAssignment roleAssignment = UserRoleAssignment.builder()
                .user(user)
                .role(UserRole.CUSTOMER)
                .assignedBy("SYSTEM")
                .active(true)
                .build();
        
        user.setRoleAssignments(Set.of(roleAssignment));
        
        User savedUser = userRepository.save(user);
        
        // Send email verification
        try {
            emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getEmailVerificationToken());
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", savedUser.getEmail(), e);
            // Don't fail registration if email fails
        }
        
        log.info("User registered successfully: {}", savedUser.getEmail());
        return userMapper.toDto(savedUser);
    }
    
    @Override
    @Transactional
    public JwtAuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
            
            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate tokens
            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshTokenStr = tokenProvider.generateRefreshToken(userDetails);
            
            // Save refresh token
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(refreshTokenStr)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration()))
                    .deviceInfo(getDeviceInfo(httpRequest))
                    .ipAddress(getClientIpAddress(httpRequest))
                    .build();
            
            refreshTokenRepository.save(refreshToken);
            
            // Clean up old tokens for this user
            cleanupExpiredTokens(user);
            
            LocalDateTime expiresAt = tokenProvider.getTokenExpirationAsLocalDateTime(accessToken);
            
            log.info("User logged in successfully: {}", user.getEmail());
            
            return new JwtAuthResponse(accessToken, refreshTokenStr, expiresAt, userMapper.toDto(user));
            
        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }
    
    @Override
    @Transactional
    public JwtAuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        log.debug("Refreshing token");
        
        String refreshTokenStr = request.getRefreshToken();
        
        // Validate refresh token format
        if (!tokenProvider.validateToken(refreshTokenStr) || !tokenProvider.isRefreshToken(refreshTokenStr)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        // Check if token is valid
        if (!refreshToken.isValid()) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token is expired or revoked");
        }
        
        User user = refreshToken.getUser();
        
        // Generate new access token
        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);
        
        // Update refresh token
        refreshToken.setToken(newRefreshToken);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration()));
        refreshToken.setDeviceInfo(getDeviceInfo(httpRequest));
        refreshToken.setIpAddress(getClientIpAddress(httpRequest));
        
        refreshTokenRepository.save(refreshToken);
        
        LocalDateTime expiresAt = tokenProvider.getTokenExpirationAsLocalDateTime(newAccessToken);
        
        log.debug("Token refreshed successfully for user: {}", user.getEmail());
        
        return new JwtAuthResponse(newAccessToken, newRefreshToken, expiresAt, userMapper.toDto(user));
    }
    
    @Override
    @Transactional
    public void logout(String refreshTokenStr) {
        log.debug("Logging out user");
        
        if (refreshTokenStr != null) {
            refreshTokenRepository.revokeToken(refreshTokenStr);
            log.debug("Refresh token revoked");
        }
    }
    
    @Override
    @Transactional
    public void logoutFromAllDevices(String email) {
        log.info("Logging out user from all devices: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        refreshTokenRepository.revokeAllUserTokens(user);
        
        log.info("User logged out from all devices: {}", email);
    }
    
    @Override
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);
        
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid email verification token"));
        
        if (user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Email verification token has expired");
        }
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        
        userRepository.save(user);
        
        log.info("Email verified successfully for user: {}", user.getEmail());
    }
    
    @Override
    @Transactional
    public void forgotPassword(String email) {
        log.info("Password reset requested for email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        
        userRepository.save(user);
        
        // Send password reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email");
        }
        
        log.info("Password reset email sent to: {}", email);
    }
    
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password with token: {}", token);
        
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));
        
        if (user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        
        userRepository.save(user);
        
        // Revoke all refresh tokens for this user
        refreshTokenRepository.revokeAllUserTokens(user);
        
        log.info("Password reset successfully for user: {}", user.getEmail());
    }
    
    @Override
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        log.info("Changing password for user: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Revoke all refresh tokens except current session
        refreshTokenRepository.revokeAllUserTokens(user);
        
        log.info("Password changed successfully for user: {}", email);
    }
    
    @Override
    @Transactional
    public void resendEmailVerification(String email) {
        log.info("Resending email verification for: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        
        if (user.getEmailVerified()) {
            throw new RuntimeException("Email is already verified");
        }
        
        String verificationToken = UUID.randomUUID().toString();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        
        userRepository.save(user);
        
        // Send email verification
        try {
            emailService.sendEmailVerification(user.getEmail(), verificationToken);
        } catch (Exception e) {
            log.error("Failed to resend email verification to: {}", email, e);
            throw new RuntimeException("Failed to send email verification");
        }
        
        log.info("Email verification resent to: {}", email);
    }
    
    private void cleanupExpiredTokens(User user) {
        try {
            refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        } catch (Exception e) {
            log.warn("Failed to cleanup expired tokens", e);
        }
    }
    
    private String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 200)) : "Unknown";
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
