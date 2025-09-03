package com.hotel.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    @GetMapping("/")
    public String home() {
        return """
                <html>
                <head><title>Hotel Reservation System</title></head>
                <body>
                    <h1>🏨 Hotel Reservation System API Gateway</h1>
                    <h2>Welcome to the Hotel Reservation System</h2>
                    <h3>Available Services:</h3>
                    <ul>
                        <li><a href="http://localhost:8083/swagger-ui.html">🔐 User Service API</a> - Authentication & User Management</li>
                        <li><a href="http://localhost:8081/swagger-ui.html">🏨 Room Service API</a> - Room & Booking Management</li>
                        <li><a href="http://localhost:8082/swagger-ui.html">💳 Payment Service API</a> - Payment Processing</li>
                    </ul>
                    <h3>Service Routes via Gateway:</h3>
                    <ul>
                        <li><a href="/api/auth/login">/api/auth/*</a> → User Service (Port 8083)</li>
                        <li><a href="/api/rooms">/api/rooms/*</a> → Room Service (Port 8081)</li>
                        <li><a href="/api/bookings">/api/bookings/*</a> → Room Service (Port 8081)</li>
                        <li><a href="/api/payments">/api/payments/*</a> → Payment Service (Port 8082)</li>
                    </ul>
                    <h3>Test Credentials:</h3>
                    <ul>
                        <li><strong>Admin:</strong> admin@hotelreservation.com / admin123</li>
                        <li><strong>Staff:</strong> staff@hotelreservation.com / admin123</li>
                        <li><strong>Customer:</strong> customer@hotelreservation.com / admin123</li>
                    </ul>
                    <p><em>API Gateway running on port 8080</em></p>
                </body>
                </html>
                """;
    }
    
    @GetMapping("/health")
    public String health() {
        return "API Gateway is running!";
    }
}
