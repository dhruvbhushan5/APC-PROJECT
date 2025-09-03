@echo off
title Hotel Reservation System - Stop All Services
color 0C
echo.
echo ===============================================
echo  🛑 Hotel Reservation System - Stop All Services
echo ===============================================
echo.

echo [INFO] Stopping all Hotel Reservation services...
echo.

REM Stop services by port (more precise)
echo [INFO] Stopping services on specific ports...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080"') do (
    echo [INFO] Stopping API Gateway on port 8080 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8081"') do (
    echo [INFO] Stopping Room Service on port 8081 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8082"') do (
    echo [INFO] Stopping Payment Service on port 8082 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8083"') do (
    echo [INFO] Stopping User Service on port 8083 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":3000"') do (
    echo [INFO] Stopping Frontend Server on port 3000 (PID: %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

REM Also kill by window title for backup
echo [INFO] Stopping services by window title...
taskkill /f /fi "windowtitle eq User Service*" >nul 2>&1
taskkill /f /fi "windowtitle eq Room Service*" >nul 2>&1
taskkill /f /fi "windowtitle eq Payment Service*" >nul 2>&1
taskkill /f /fi "windowtitle eq API Gateway*" >nul 2>&1
taskkill /f /fi "windowtitle eq Frontend*" >nul 2>&1

REM Kill Node.js processes that might be running the frontend
echo [INFO] Stopping Node.js processes...
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq node.exe" /fo table /nh ^| findstr "node.exe"') do (
    echo [INFO] Stopping Node.js process %%i...
    taskkill /f /pid %%i >nul 2>&1
)

echo.
echo [INFO] Waiting for services to shut down...
timeout /t 3 /nobreak >nul

echo.
echo ===============================================
echo        🔴 Service Shutdown Status
echo ===============================================
echo.

REM Check if ports are now free
echo [INFO] Checking if ports are now available...

netstat -ano | findstr ":8080" >nul 2>&1
if %errorlevel% equ 0 (
    echo   Port 8080 (API Gateway):     ❌ Still in use
) else (
    echo   Port 8080 (API Gateway):     ✅ Available
)

netstat -ano | findstr ":8081" >nul 2>&1
if %errorlevel% equ 0 (
    echo   Port 8081 (Room Service):    ❌ Still in use
) else (
    echo   Port 8081 (Room Service):    ✅ Available
)

netstat -ano | findstr ":8082" >nul 2>&1
if %errorlevel% equ 0 (
    echo   Port 8082 (Payment Service): ❌ Still in use
) else (
    echo   Port 8082 (Payment Service): ✅ Available
)

netstat -ano | findstr ":8083" >nul 2>&1
if %errorlevel% equ 0 (
    echo   Port 8083 (User Service):    ❌ Still in use
) else (
    echo   Port 8083 (User Service):    ✅ Available
)

netstat -ano | findstr ":3000" >nul 2>&1
if %errorlevel% equ 0 (
    echo   Port 3000 (Frontend):        ❌ Still in use
) else (
    echo   Port 3000 (Frontend):        ✅ Available
)

echo.
echo ===============================================
echo           ✅ All Services Stopped!
echo ===============================================
echo.
echo 💡 To restart the system, run: start-all-services.bat
echo.
pause
