@echo off
echo ===============================================
echo Hotel Reservation System - H2 Demo Start
echo ===============================================
echo.
echo Starting microservices with H2 in-memory database...
echo This demo version uses H2 database for quick demonstration.
echo.

REM Kill any existing Java processes
echo Stopping any existing Java processes...
taskkill /F /IM java.exe >nul 2>&1

echo.
echo Building the project...
call mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo ✅ Build completed successfully!
echo.

echo Starting services with H2 database...
echo.

REM Start User Service (Authentication)
echo 🚀 Starting User Service on port 8083...
start /B "User Service" cmd /c "mvn spring-boot:run -pl user-service -Dspring-boot.run.profiles=h2 > user-service.log 2>&1"
timeout /t 5 >nul

REM Start Room Service  
echo 🚀 Starting Room Service on port 8081...
start /B "Room Service" cmd /c "mvn spring-boot:run -pl room-service -Dspring-boot.run.profiles=h2 > room-service.log 2>&1"
timeout /t 5 >nul

REM Start Payment Service
echo 🚀 Starting Payment Service on port 8082...
start /B "Payment Service" cmd /c "mvn spring-boot:run -pl payment-service -Dspring-boot.run.profiles=h2 > payment-service.log 2>&1"
timeout /t 5 >nul

REM Start API Gateway
echo 🚀 Starting API Gateway on port 8080...
start /B "API Gateway" cmd /c "mvn spring-boot:run -pl api-gateway > api-gateway.log 2>&1"

echo.
echo ⏳ Waiting for services to start (this may take 30-60 seconds)...
timeout /t 15 >nul

echo.
echo 🔍 Checking service health...

REM Check if services are responding
echo.
echo Checking User Service (port 8083)...
curl -s http://localhost:8083/api/health >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ User Service is running
) else (
    echo ⚠️  User Service not ready yet
)

echo Checking Room Service (port 8081)...
curl -s http://localhost:8081/api/health >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ Room Service is running
) else (
    echo ⚠️  Room Service not ready yet
)

echo Checking Payment Service (port 8082)...
curl -s http://localhost:8082/api/health >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ Payment Service is running
) else (
    echo ⚠️  Payment Service not ready yet
)

echo Checking API Gateway (port 8080)...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo ✅ API Gateway is running
) else (
    echo ⚠️  API Gateway not ready yet
)

echo.
echo ===============================================
echo 🎉 Hotel Reservation System Started!
echo ===============================================
echo.
echo 📍 Access Points:
echo   • Main Application: http://localhost:8080
echo   • API Documentation: http://localhost:8080/swagger-ui.html
echo   • User Service API: http://localhost:8083/swagger-ui.html
echo   • Room Service API: http://localhost:8081/swagger-ui.html
echo   • Payment Service API: http://localhost:8082/swagger-ui.html
echo.
echo 🗄️  H2 Database Consoles (for debugging):
echo   • User DB: http://localhost:8083/h2-console
echo   • Room DB: http://localhost:8081/h2-console
echo   • Payment DB: http://localhost:8082/h2-console
echo.
echo 📋 Test Credentials:
echo   Username: admin@hotel.com
echo   Password: admin123
echo.
echo 🚀 The system is ready for demonstration!
echo.
echo Log files:
echo   • user-service.log
echo   • room-service.log  
echo   • payment-service.log
echo   • api-gateway.log
echo.
echo Press any key to open the main application...
pause >nul
start http://localhost:8080
