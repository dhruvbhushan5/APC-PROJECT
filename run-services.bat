@echo off
echo ===============================================
echo Hotel Reservation System - Starting Services
echo ===============================================
echo.

echo Checking system requirements...
echo.

echo Using H2 in-memory databases for quick startup
echo PostgreSQL setup can be done later if needed
echo.

echo System ready ✓
echo.

echo Starting Hotel Reservation Services...
echo.
echo Services will start in this order:
echo 1. User Service (Authentication) - Port 8083
echo 2. Room Service (Rooms & Bookings) - Port 8081
echo 3. Payment Service (Payments) - Port 8082
echo 4. API Gateway (Main Entry Point) - Port 8080
echo.

echo ===============================================
echo Starting User Service...
echo ===============================================
start "User Service" cmd /k "cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
echo User Service starting in new window...
echo Waiting 30 seconds for service to initialize...
timeout /t 30 /nobreak >nul

echo ===============================================
echo Starting Room Service...
echo ===============================================
start "Room Service" cmd /k "cd room-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
echo Room Service starting in new window...
echo Waiting 30 seconds for service to initialize...
timeout /t 30 /nobreak >nul

echo ===============================================
echo Starting Payment Service...
echo ===============================================
start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
echo Payment Service starting in new window...
echo Waiting 30 seconds for service to initialize...
timeout /t 30 /nobreak >nul

echo ===============================================
echo Starting API Gateway...
echo ===============================================
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"
echo API Gateway starting in new window...
echo.

echo ===============================================
echo ALL SERVICES STARTED!
echo ===============================================
echo.
echo Service URLs:
echo.
echo 🌐 API Gateway (Main Entry): http://localhost:8080
echo 🔐 User Service (Auth):      http://localhost:8083
echo 🏨 Room Service:             http://localhost:8081
echo 💳 Payment Service:          http://localhost:8082
echo.
echo API Documentation (Swagger):
echo 🔐 User Service API:         http://localhost:8083/swagger-ui.html
echo 🏨 Room Service API:         http://localhost:8081/swagger-ui.html
echo 💳 Payment Service API:      http://localhost:8082/swagger-ui.html
echo.
echo ===============================================
echo DEFAULT TEST USERS:
echo ===============================================
echo Admin:    admin@hotelreservation.com / admin123
echo Staff:    staff@hotelreservation.com / admin123  
echo Customer: customer@hotelreservation.com / admin123
echo.
echo To stop all services, close all the opened command windows.
echo.
pause
