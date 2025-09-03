# Hotel Reservation System - Database Setup

## PostgreSQL Database Setup

### 1. Install PostgreSQL
Download and install PostgreSQL from: https://www.postgresql.org/download/

### 2. Create Databases
Connect to PostgreSQL and run these commands:

```sql
-- Connect as postgres user
psql -U postgres

-- Create databases
CREATE DATABASE hotel_room_db;
CREATE DATABASE hotel_payment_db;

-- Create a user for the application (optional)
CREATE USER hotel_user WITH PASSWORD 'hotel_password';
GRANT ALL PRIVILEGES ON DATABASE hotel_room_db TO hotel_user;
GRANT ALL PRIVILEGES ON DATABASE hotel_payment_db TO hotel_user;

-- Exit psql
\q
```

### 3. Update Configuration
If you created a custom user, update the application.yml files:

**room-service/src/main/resources/application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hotel_room_db
    username: hotel_user
    password: hotel_password
```

**payment-service/src/main/resources/application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hotel_payment_db
    username: hotel_user
    password: hotel_password
```

### 4. For Development (H2 Database Alternative)
If you prefer to use H2 for development, update application.yml:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### 5. Database Schema
The following tables will be automatically created by Flyway migrations:

**Room Service Database (hotel_room_db):**
- `rooms` - Room information and status
- `bookings` - Booking/reservation information

**Payment Service Database (hotel_payment_db):**
- `payments` - Payment transactions and status

### 6. Sample Data
Sample rooms are automatically inserted via migration scripts including:
- Standard rooms (101, 102, 103)
- Deluxe rooms (201, 202, 203)
- Suite rooms (301, 302)
- Presidential suite (401)
- Family rooms (105, 205)
