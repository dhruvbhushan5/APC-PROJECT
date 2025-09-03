@echo off
echo ===============================================
echo Hotel Reservation System - Stop H2 Demo
echo ===============================================
echo.

echo Stopping all hotel reservation services...

REM Kill Java processes (Spring Boot applications)
echo 🛑 Stopping Java processes...
taskkill /F /IM java.exe >nul 2>&1

REM Clean up log files
echo 🧹 Cleaning up log files...
if exist user-service.log del user-service.log >nul 2>&1
if exist room-service.log del room-service.log >nul 2>&1
if exist payment-service.log del payment-service.log >nul 2>&1
if exist api-gateway.log del api-gateway.log >nul 2>&1

echo.
echo ✅ All services stopped successfully!
echo.
echo The H2 demo has been stopped and cleaned up.
echo.
pause
