@echo off
echo ===============================================
echo Hotel Reservation System - H2 Database Mode
echo ===============================================
echo.

echo Starting Hotel Reservation Services with H2 in-memory databases...
echo This is perfect for development and testing!
echo.
echo Services will start in this order:
echo 1. User Service (Authentication) - Port 8083
echo 2. Room Service (Rooms and Bookings) - Port 8081
echo 3. Payment Service (Payments) - Port 8082
echo 4. API Gateway (Main Entry Point) - Port 8080
echo.

echo ===============================================
echo Starting User Service with H2...
echo ===============================================
start "User Service (H2)" cmd /k "cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
echo User Service starting in new window...
echo Waiting 30 seconds for service to initialize...
timeout /t 30 /nobreak >nul

echo ===============================================
echo Starting Room Service with H2...
echo ===============================================
start "Room Service (H2)" cmd /k "cd room-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
echo Room Service starting in new window...
echo Waiting 30 seconds for service to initialize...
timeout /t 30 /nobreak >nul

echo ===============================================
echo Starting Payment Service with H2...
echo ===============================================
start "Payment Service (H2)" cmd /k "cd payment-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
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
echo ALL SERVICES STARTED WITH H2 DATABASES!
echo ===============================================
echo.
echo Service URLs:
echo.
echo 🌐 API Gateway (Main Entry): http://localhost:8080
echo 🔐 User Service (Auth):      http://localhost:8083
echo 🏨 Room Service:             http://localhost:8081
echo 💳 Payment Service:          http://localhost:8082
echo.
echo H2 Database Consoles:
echo 🔐 User Service H2:          http://localhost:8083/h2-console
echo 🏨 Room Service H2:          http://localhost:8081/h2-console
echo 💳 Payment Service H2:       http://localhost:8082/h2-console
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
echo Note: Data is stored in memory and will be lost when services stop.
echo For persistent data, use PostgreSQL setup later.
echo.
echo To stop all services, close all the opened command windows.
echo.
pause
