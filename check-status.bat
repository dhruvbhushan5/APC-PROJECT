@echo off
title Hotel Reservation System - Quick Status Check
color 0B
echo.
echo ===============================================
echo    📊 Hotel Reservation System - Status Check
echo ===============================================
echo.

:status_check
echo [INFO] Checking service status...
echo.

REM Check User Service
curl -s http://localhost:8083/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   User Service:     ✅ RUNNING  (Port 8083)
) else (
    echo   User Service:     ❌ STOPPED  (Port 8083)
)

REM Check Room Service
curl -s http://localhost:8081/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   Room Service:     ✅ RUNNING  (Port 8081)
) else (
    echo   Room Service:     ❌ STOPPED  (Port 8081)
)

REM Check Payment Service
curl -s http://localhost:8082/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   Payment Service:  ✅ RUNNING  (Port 8082)
) else (
    echo   Payment Service:  ❌ STOPPED  (Port 8082)
)

REM Check API Gateway
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   API Gateway:      ✅ RUNNING  (Port 8080)
) else (
    echo   API Gateway:      ❌ STOPPED  (Port 8080)
)

REM Check Frontend
curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo   Frontend:         ✅ RUNNING  (Port 3000)
) else (
    echo   Frontend:         ❌ STOPPED  (Port 3000)
)

echo.
echo 🔗 Quick Access Links:
echo   Main App:         http://localhost:3000
echo   Room API Docs:    http://localhost:8081/swagger-ui.html
echo   User API Docs:    http://localhost:8083/swagger-ui.html
echo   Payment API Docs: http://localhost:8082/swagger-ui.html
echo   Gateway Health:   http://localhost:8080/actuator/health
echo.

echo 📊 Active Ports:
netstat -ano | findstr ":8080\|:8081\|:8082\|:8083\|:3000" | findstr LISTENING

echo.
echo [R] Refresh Status  [O] Open Apps  [S] Start System  [T] Stop System  [Q] Quit
choice /c ROSTQ /n /m "Choose option: "

if %errorlevel% equ 1 goto status_check
if %errorlevel% equ 2 goto open_apps
if %errorlevel% equ 3 goto start_system
if %errorlevel% equ 4 goto stop_system
if %errorlevel% equ 5 goto exit_script

:open_apps
echo [INFO] Opening all application URLs...
start http://localhost:3000
start http://localhost:8081/swagger-ui.html
start http://localhost:8083/swagger-ui.html
start http://localhost:8082/swagger-ui.html
echo [✓] Apps opened in browser
timeout /t 2 /nobreak >nul
goto status_check

:start_system
echo [INFO] Starting the complete system...
start "" "start-all-services.bat"
timeout /t 3 /nobreak >nul
goto status_check

:stop_system
echo [INFO] Stopping all services...
call stop-services.bat
goto status_check

:exit_script
echo.
echo Thank you for using Hotel Reservation System!
echo.
exit /b 0
