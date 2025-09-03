@echo off
title Hotel Reservation System - Complete Startup
color 0A
echo.
echo ===============================================
echo    🏨 Hotel Reservation System Startup
echo ===============================================
echo.

echo [INFO] Starting complete hotel reservation system...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java and try again.
    pause
    exit /b 1
)

echo [✓] Java detected
echo.

REM Kill any existing services on our ports
echo [INFO] Cleaning up existing services...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8083') do (
    echo [INFO] Stopping process on port 8083...
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    echo [INFO] Stopping process on port 8080...
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000') do (
    echo [INFO] Stopping process on port 3000...
    taskkill /F /PID %%a >nul 2>&1
)

timeout /t 2 /nobreak >nul

echo [✓] Ports cleared
echo.

REM Build the project
echo [INFO] Building the project...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Build failed
    pause
    exit /b 1
)
echo [✓] Project built successfully
echo.

REM Start User Service
echo [INFO] Starting User Service (Port 8083)...
start "User Service" cmd /c "cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 5 /nobreak >nul

REM Start API Gateway
echo [INFO] Starting API Gateway (Port 8080)...
start "API Gateway" cmd /c "cd api-gateway && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

REM Start Frontend Server
echo [INFO] Starting Frontend Server (Port 3000)...
start "Frontend Server" cmd /c "cd frontend && python -m http.server 3000"
timeout /t 3 /nobreak >nul

echo.
echo ===============================================
echo           🎉 SYSTEM STARTUP COMPLETE
echo ===============================================
echo.
echo 📊 Service Status:
echo   User Service:    http://localhost:8083
echo   API Gateway:     http://localhost:8080  
echo   Frontend:        http://localhost:3000
echo.
echo 🔗 Quick Access Links:
echo   Main App:        http://localhost:3000
echo   API Docs:        http://localhost:8083/swagger-ui.html
echo   Database:        http://localhost:8083/h2-console
echo   Admin Panel:     http://localhost:3000/admin.html
echo.
echo 🔑 Default Login:
echo   Email:    admin@hotel.com
echo   Password: Admin123!
echo.
echo ⏰ Services are starting... Please wait 30 seconds for full initialization.
echo.

REM Wait for services to start
echo [INFO] Waiting for services to initialize...
:wait_loop
timeout /t 3 /nobreak >nul
curl -s http://localhost:8083/swagger-ui.html >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] User Service is ready
    goto services_ready
)
echo [INFO] Still waiting for services...
goto wait_loop

:services_ready
echo.
echo [✓] All services are ready!
echo.

REM Open the application in browser
echo [INFO] Opening application in browser...
start http://localhost:3000
timeout /t 2 /nobreak >nul
start http://localhost:8083/swagger-ui.html

echo.
echo ===============================================
echo     🚀 Hotel Reservation System is LIVE!
echo ===============================================
echo.
echo Press any key to view system status or Ctrl+C to exit...
pause >nul

REM Show system status
:status_loop
cls
echo ===============================================
echo        🏨 Hotel Reservation System Status
echo ===============================================
echo.
echo 📊 Service Health Check:
echo.

REM Check User Service
curl -s http://localhost:8083/swagger-ui.html >nul 2>&1
if %errorlevel% equ 0 (
    echo   User Service:    ✅ RUNNING  (Port 8083)
) else (
    echo   User Service:    ❌ STOPPED  (Port 8083)
)

REM Check API Gateway
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   API Gateway:     ✅ RUNNING  (Port 8080)
) else (
    echo   API Gateway:     ❌ STOPPED  (Port 8080)
)

REM Check Frontend
curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo   Frontend:        ✅ RUNNING  (Port 3000)
) else (
    echo   Frontend:        ❌ STOPPED  (Port 3000)
)

echo.
echo 🔗 Application URLs:
echo   Main App:        http://localhost:3000
echo   API Docs:        http://localhost:8083/swagger-ui.html
echo   Database:        http://localhost:8083/h2-console
echo.
echo 📊 Port Usage:
netstat -ano | findstr ":8080\|:8083\|:3000" | findstr LISTENING

echo.
echo [R] Restart Services  [S] Stop All  [O] Open Apps  [Q] Quit
choice /c RSOQ /n /m "Choose option: "

if %errorlevel% equ 1 goto restart_services
if %errorlevel% equ 2 goto stop_all
if %errorlevel% equ 3 goto open_apps
if %errorlevel% equ 4 goto exit_script

:restart_services
echo.
echo [INFO] Restarting all services...
goto cleanup_and_start

:stop_all
echo.
echo [INFO] Stopping all services...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8083\|:8080\|:3000"') do (
    taskkill /F /PID %%a >nul 2>&1
)
echo [✓] All services stopped
pause
goto exit_script

:open_apps
start http://localhost:3000
start http://localhost:8083/swagger-ui.html
start http://localhost:8083/h2-console
goto status_loop

:cleanup_and_start
REM Kill existing services
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8083\|:8080\|:3000"') do (
    taskkill /F /PID %%a >nul 2>&1
)
timeout /t 3 /nobreak >nul
REM Restart services (simplified restart)
start "User Service" cmd /c "cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 5 /nobreak >nul
start "API Gateway" cmd /c "cd api-gateway && mvn spring-boot:run"
timeout /t 5 /nobreak >nul
start "Frontend Server" cmd /c "cd frontend && python -m http.server 3000"
echo [✓] Services restarted
timeout /t 3 /nobreak >nul
goto status_loop

:exit_script
echo.
echo [INFO] Shutting down Hotel Reservation System...
echo Thank you for using our system!
echo.
exit /b 0
