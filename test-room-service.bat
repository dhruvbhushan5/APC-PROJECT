@echo off
echo Testing Room Service APIs...
echo.

echo 1. Adding a menu item...
curl -X POST http://localhost:8083/api/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Grilled Chicken\",\"description\":\"Tender grilled chicken with herbs\",\"price\":18.99,\"category\":\"MAIN_COURSE\",\"available\":true,\"preparationTime\":\"20-25 minutes\",\"allergens\":\"None\",\"vegetarian\":false,\"vegan\":false,\"glutenFree\":true}"
echo.
echo.

echo 2. Getting menu items...
curl -X GET http://localhost:8083/api/menu
echo.
echo.

echo 3. Adding another menu item...
curl -X POST http://localhost:8083/api/menu ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Caesar Salad\",\"description\":\"Fresh romaine lettuce with parmesan cheese\",\"price\":12.99,\"category\":\"APPETIZERS\",\"available\":true,\"preparationTime\":\"10 minutes\",\"allergens\":\"Dairy\",\"vegetarian\":true,\"vegan\":false,\"glutenFree\":false}"
echo.
echo.

echo 4. Creating a food order...
curl -X POST http://localhost:8083/api/food-orders ^
  -H "Content-Type: application/json" ^
  -d "{\"roomNumber\":\"101\",\"guestName\":\"John Doe\",\"guestPhone\":\"555-0123\",\"bookingId\":1,\"items\":[{\"menuItemId\":1,\"quantity\":2,\"specialRequests\":\"No onions\"}],\"specialInstructions\":\"Please deliver quietly\"}"
echo.
echo.

echo 5. Creating a housekeeping request...
curl -X POST http://localhost:8083/api/housekeeping ^
  -H "Content-Type: application/json" ^
  -d "{\"roomNumber\":\"101\",\"guestName\":\"John Doe\",\"guestPhone\":\"555-0123\",\"bookingId\":1,\"requestType\":\"CLEANING\",\"priority\":\"MEDIUM\",\"description\":\"Room cleaning required\",\"specialInstructions\":\"Extra towels needed\"}"
echo.
echo.

echo Room Service API testing complete!
pause
