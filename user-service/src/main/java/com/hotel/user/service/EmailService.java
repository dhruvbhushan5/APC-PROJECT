package com.hotel.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.mail.from:noreply@hotelreservation.com}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public void sendEmailVerification(String toEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Reservation - Email Verification");
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
            String text = String.format(
                "Welcome to Hotel Reservation System!\n\n" +
                "Please click the following link to verify your email address:\n%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not create an account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Hotel Reservation Team",
                verificationUrl
            );
            
            message.setText(text);
            
            mailSender.send(message);
            log.info("Email verification sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email verification", e);
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Reservation - Password Reset");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String text = String.format(
                "You have requested to reset your password for Hotel Reservation System.\n\n" +
                "Please click the following link to reset your password:\n%s\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Hotel Reservation Team",
                resetUrl
            );
            
            message.setText(text);
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    public void sendPasswordChangeNotification(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Reservation - Password Changed");
            
            String text = String.format(
                "Your password for Hotel Reservation System has been successfully changed.\n\n" +
                "If you did not make this change, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "Hotel Reservation Team"
            );
            
            message.setText(text);
            
            mailSender.send(message);
            log.info("Password change notification sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send password change notification to: {}", toEmail, e);
            // Don't throw exception for notification emails
        }
    }
    
    public void sendLoginNotification(String toEmail, String deviceInfo, String ipAddress) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Reservation - New Login");
            
            String text = String.format(
                "A new login to your Hotel Reservation account was detected.\n\n" +
                "Device: %s\n" +
                "IP Address: %s\n\n" +
                "If this was not you, please change your password immediately.\n\n" +
                "Best regards,\n" +
                "Hotel Reservation Team",
                deviceInfo,
                ipAddress
            );
            
            message.setText(text);
            
            mailSender.send(message);
            log.info("Login notification sent to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send login notification to: {}", toEmail, e);
            // Don't throw exception for notification emails
        }
    }
}
