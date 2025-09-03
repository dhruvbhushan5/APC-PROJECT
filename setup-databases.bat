@echo off
echo ===============================================
echo Hotel Reservation System - Database Setup
echo ===============================================
echo.

echo This script will help you set up PostgreSQL databases for the hotel reservation system.
echo.
echo Prerequisites:
echo 1. PostgreSQL must be installed and running
echo 2. PostgreSQL bin directory should be in your PATH
echo 3. You should know your PostgreSQL superuser password
echo.

set /p POSTGRES_PASSWORD="Enter PostgreSQL superuser password: "
echo.

echo Creating databases and users...
echo.

echo Creating user-service database...
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "CREATE DATABASE hotel_user_db;" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "CREATE USER hotel_user WITH PASSWORD 'hotel_password';" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE hotel_user_db TO hotel_user;" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "ALTER USER hotel_user CREATEDB;" 2>nul

echo Creating room-service database...
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "CREATE DATABASE hotel_room_db;" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "CREATE USER hotel_room WITH PASSWORD 'hotel_password';" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE hotel_room_db TO hotel_room;" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "ALTER USER hotel_room CREATEDB;" 2>nul

echo Creating payment-service database...
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "CREATE DATABASE hotel_payment_db;" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "CREATE USER hotel_payment WITH PASSWORD 'hotel_password';" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE hotel_payment_db TO hotel_payment;" 2>nul
"C:\Program Files\PostgreSQL\17\bin\psql" -U postgres -c "ALTER USER hotel_payment CREATEDB;" 2>nul

echo.
echo ===============================================
echo Database setup completed!
echo.
echo Created databases:
echo - hotel_user_db (for user-service)
echo - hotel_room_db (for room-service)
echo - hotel_payment_db (for payment-service)
echo.
echo Created users:
echo - hotel_user (password: hotel_password)
echo - hotel_room (password: hotel_password)
echo - hotel_payment (password: hotel_password)
echo ===============================================
echo.
pause
