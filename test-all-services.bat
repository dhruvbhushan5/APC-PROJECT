@echo off
echo =========================================
echo    HOTEL SYSTEM COMPREHENSIVE TEST
echo =========================================
echo.

echo Checking which services are running...
echo.

echo 1. Testing Room Service (Port 8083)...
curl -s -o nul -w "%%{http_code}" http://localhost:8083/actuator/health
if %ERRORLEVEL% == 0 (
    echo    ✓ Room Service is RUNNING
) else (
    echo    ✗ Room Service is NOT responding
)
echo.

echo 2. Testing Payment Service (Port 8082)...
curl -s -o nul -w "%%{http_code}" http://localhost:8082/actuator/health
if %ERRORLEVEL% == 0 (
    echo    ✓ Payment Service is RUNNING
) else (
    echo    ✗ Payment Service is NOT responding
)
echo.

echo 3. Testing Room Service API (Port 8081)...
curl -s -o nul -w "%%{http_code}" http://localhost:8081/actuator/health
if %ERRORLEVEL% == 0 (
    echo    ✓ Room Service API is RUNNING
) else (
    echo    ✗ Room Service API is NOT responding
)
echo.

echo 4. Testing Frontend (Port 3000)...
curl -s -o nul -w "%%{http_code}" http://localhost:3000
if %ERRORLEVEL% == 0 (
    echo    ✓ Frontend is RUNNING
) else (
    echo    ✗ Frontend is NOT responding
)
echo.

echo =========================================
echo      FOOD SERVICE API TESTS
echo =========================================
echo.

echo 5. Adding sample menu items...
curl -X POST http://localhost:8083/api/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Grilled Chicken Breast\",\"description\":\"Tender grilled chicken with herbs and spices\",\"price\":24.99,\"category\":\"MAIN_COURSE\",\"available\":true,\"preparationTime\":\"25-30 minutes\",\"allergens\":\"None\",\"vegetarian\":false,\"vegan\":false,\"glutenFree\":true}"
echo.

curl -X POST http://localhost:8083/api/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Caesar Salad\",\"description\":\"Fresh romaine lettuce with parmesan cheese and croutons\",\"price\":14.99,\"category\":\"APPETIZERS\",\"available\":true,\"preparationTime\":\"10 minutes\",\"allergens\":\"Dairy, Gluten\",\"vegetarian\":true,\"vegan\":false,\"glutenFree\":false}"
echo.

curl -X POST http://localhost:8083/api/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Fresh Orange Juice\",\"description\":\"Freshly squeezed orange juice\",\"price\":6.99,\"category\":\"BEVERAGES\",\"available\":true,\"preparationTime\":\"5 minutes\",\"allergens\":\"None\",\"vegetarian\":true,\"vegan\":true,\"glutenFree\":true}"
echo.

echo 6. Retrieving menu items...
curl -X GET http://localhost:8083/api/menu
echo.
echo.

echo 7. Creating a food order...
curl -X POST http://localhost:8083/api/food-orders ^
  -H "Content-Type: application/json" ^
  -d "{\"roomNumber\":\"101\",\"guestName\":\"John Doe\",\"guestPhone\":\"555-0123\",\"bookingId\":1,\"items\":[{\"menuItemId\":1,\"quantity\":1,\"specialRequests\":\"Medium rare\"},{\"menuItemId\":2,\"quantity\":1,\"specialRequests\":\"Extra dressing\"}],\"specialInstructions\":\"Please deliver between 7-8 PM\"}"
echo.
echo.

echo 8. Retrieving food orders...
curl -X GET http://localhost:8083/api/food-orders
echo.
echo.

echo =========================================
echo    HOUSEKEEPING SERVICE API TESTS
echo =========================================
echo.

echo 9. Creating housekeeping requests...
curl -X POST http://localhost:8083/api/housekeeping ^
  -H "Content-Type: application/json" ^
  -d "{\"roomNumber\":\"101\",\"guestName\":\"John Doe\",\"guestPhone\":\"555-0123\",\"bookingId\":1,\"requestType\":\"CLEANING\",\"priority\":\"HIGH\",\"description\":\"Deep cleaning required\",\"specialInstructions\":\"Extra towels and toiletries needed\"}"
echo.

curl -X POST http://localhost:8083/api/housekeeping ^
  -H "Content-Type: application/json" ^
  -d "{\"roomNumber\":\"102\",\"guestName\":\"Jane Smith\",\"guestPhone\":\"555-0456\",\"bookingId\":2,\"requestType\":\"MAINTENANCE\",\"priority\":\"MEDIUM\",\"description\":\"Air conditioning not working properly\",\"specialInstructions\":\"Please check before noon\"}"
echo.

echo 10. Retrieving housekeeping requests...
curl -X GET http://localhost:8083/api/housekeeping
echo.
echo.

echo =========================================
echo         PAYMENT SYSTEM TEST
echo =========================================
echo.

echo 11. Testing payment processing...
curl -X POST http://localhost:8082/api/payments ^
  -H "Content-Type: application/json" ^
  -d "{\"bookingId\":1,\"amount\":250.00,\"currency\":\"USD\",\"cardNumber\":\"4111111111111111\",\"expiryMonth\":12,\"expiryYear\":2025,\"cvv\":\"123\",\"cardHolderName\":\"John Doe\",\"status\":\"PENDING\"}"
echo.
echo.

echo =========================================
echo           SYSTEM STATUS SUMMARY
echo =========================================
echo.

echo ✓ Room Service App (Food & Housekeeping): http://localhost:8083
echo ✓ Payment Service: http://localhost:8082  
echo ✓ Room Service API: http://localhost:8081
echo ✓ Frontend Application: http://localhost:3000
echo.
echo Test Interfaces Available:
echo - Room Service Test: file:///c:/Users/VICTUS/OneDrive/Desktop/hotel-reservation/room-service-test.html
echo - H2 Database Console: http://localhost:8083/h2-console
echo   (JDBC URL: jdbc:h2:mem:roomservicedb, User: SA, Password: blank)
echo.
echo =========================================
echo          ALL TESTS COMPLETED!
echo =========================================
echo.
pause
