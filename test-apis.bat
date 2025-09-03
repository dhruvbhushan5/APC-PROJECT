@echo off
echo ===============================================
echo Hotel Reservation System - API Testing
echo ===============================================
echo.

echo This script provides sample API calls to test your hotel reservation system.
echo Make sure all services are running before testing.
echo.

set BASE_URL=http://localhost
set USER_SERVICE=%BASE_URL%:8083
set ROOM_SERVICE=%BASE_URL%:8081
set PAYMENT_SERVICE=%BASE_URL%:8082
set API_GATEWAY=%BASE_URL%:8080

echo Service URLs:
echo User Service: %USER_SERVICE%
echo Room Service: %ROOM_SERVICE%
echo Payment Service: %PAYMENT_SERVICE%
echo API Gateway: %API_GATEWAY%
echo.

echo ===============================================
echo Testing Options:
echo ===============================================
echo.
echo 1. Check service health
echo 2. Test user authentication
echo 3. Test room operations
echo 4. Test booking operations
echo 5. Test payment operations
echo 6. Open API documentation
echo 7. Exit
echo.

set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto :check_health
if "%choice%"=="2" goto :test_auth
if "%choice%"=="3" goto :test_rooms
if "%choice%"=="4" goto :test_bookings
if "%choice%"=="5" goto :test_payments
if "%choice%"=="6" goto :open_docs
if "%choice%"=="7" goto :end

echo Invalid choice!
goto :end

:check_health
echo.
echo ===============================================
echo CHECKING SERVICE HEALTH
echo ===============================================
echo.

echo Checking User Service...
curl -s %USER_SERVICE%/actuator/health || echo Service not responding

echo.
echo Checking Room Service...
curl -s %ROOM_SERVICE%/actuator/health || echo Service not responding

echo.
echo Checking Payment Service...
curl -s %PAYMENT_SERVICE%/actuator/health || echo Service not responding

echo.
goto :end

:test_auth
echo.
echo ===============================================
echo TESTING USER AUTHENTICATION
echo ===============================================
echo.

echo 1. Login as Customer...
curl -X POST %USER_SERVICE%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"customer@hotelreservation.com\",\"password\":\"admin123\"}"

echo.
echo.
echo 2. Login as Admin...
curl -X POST %USER_SERVICE%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@hotelreservation.com\",\"password\":\"admin123\"}"

echo.
goto :end

:test_rooms
echo.
echo ===============================================
echo TESTING ROOM OPERATIONS
echo ===============================================
echo.

echo 1. Get all rooms...
curl -s %ROOM_SERVICE%/api/rooms

echo.
echo.
echo 2. Check room availability...
curl -s "%ROOM_SERVICE%/api/rooms/available?checkInDate=2025-09-15&checkOutDate=2025-09-17"

echo.
goto :end

:test_bookings
echo.
echo ===============================================
echo TESTING BOOKING OPERATIONS
echo ===============================================
echo.

echo 1. Get all bookings...
curl -s %ROOM_SERVICE%/api/bookings

echo.
echo.
echo 2. Get booking statistics...
curl -s %ROOM_SERVICE%/api/bookings/statistics

echo.
goto :end

:test_payments
echo.
echo ===============================================
echo TESTING PAYMENT OPERATIONS
echo ===============================================
echo.

echo 1. Get all payments...
curl -s %PAYMENT_SERVICE%/api/payments

echo.
echo.
echo 2. Get payment statistics...
curl -s %PAYMENT_SERVICE%/api/payments/statistics

echo.
goto :end

:open_docs
echo.
echo ===============================================
echo OPENING API DOCUMENTATION
echo ===============================================
echo.

echo Opening Swagger UI for all services...
echo.

start "" "%USER_SERVICE%/swagger-ui.html"
start "" "%ROOM_SERVICE%/swagger-ui.html"
start "" "%PAYMENT_SERVICE%/swagger-ui.html"

echo API documentation opened in your default browser.
echo.
goto :end

:end
echo.
pause
