# Hotel Reservation System

A microservices-based hotel reservation system built with Spring Boot, Spring Cloud, and PostgreSQL.

## 🏗️ Architecture

This system is built using a microservices architecture with the following components:

- **Frontend** (`frontend`) - React TypeScript application with Webpack dev server and hot reload
- **API Gateway** (`api-gateway`) - Entry point for all client requests with routing, authentication, and load balancing
- **User Service** (`user-service`) - Manages user accounts, authentication, and user-related operations
- **Room Service** (`room-service`) - Manages hotel rooms, availability, and room-related operations  
- **Payment Service** (`payment-service`) - Handles payment processing and transaction management
- **Hotel Common** (`hotel-common`) - Shared DTOs, utilities, and common components

## 🚀 Technology Stack

### Backend
- **Framework**: Spring Boot 3.1.5
- **Microservices**: Spring Cloud 2022.0.4
- **Database**: H2 (in-memory) / PostgreSQL (production)
- **Authentication**: Spring Security with JWT
- **API Gateway**: Spring Cloud Gateway
- **Service Discovery**: Netflix Eureka (optional)
- **Circuit Breaker**: Resilience4j
- **Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven 3.9+
- **Java Version**: 17+

### Frontend
- **Framework**: React 18.2.0
- **Language**: TypeScript 4.9.5
- **Build Tool**: Webpack 5.76.0
- **Dev Server**: Webpack Dev Server (Hot Reload)
- **HTTP Client**: Axios
- **Routing**: React Router DOM
- **State Management**: Zustand
- **UI Components**: Lucide React, Framer Motion

## 📁 Project Structure

```
hotel-reservation/
├── pom.xml                          # Parent POM with dependency management
├── hotel-common/                    # Shared components
│   ├── src/main/java/com/hotel/common/
│   │   ├── dto/                     # Data Transfer Objects
│   │   ├── exception/               # Common exceptions
│   │   └── util/                    # Utility classes
│   └── pom.xml
├── room-service/                    # Room management microservice
│   ├── src/main/java/com/hotel/room/
│   ├── src/main/resources/application.yml
│   └── pom.xml
├── payment-service/                 # Payment processing microservice
│   ├── src/main/java/com/hotel/payment/
│   ├── src/main/resources/application.yml
│   └── pom.xml
└── api-gateway/                     # API Gateway
    ├── src/main/java/com/hotel/gateway/
    ├── src/main/resources/application.yml
    └── pom.xml
```

## 🛠️ Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.9 or higher  
- Node.js 16+ (for frontend development)
- PostgreSQL 12 or higher (optional - H2 in-memory database included)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Quick Start (Recommended)

**🚀 One-Click Startup:**
```bash
# Start the complete system with all services and frontend
start-all-services.bat
```

This script will:
- ✅ Check prerequisites (Java and Node.js)
- ✅ Clean up any existing services
- ✅ Build all Maven projects
- ✅ Install frontend dependencies
- ✅ Start all services in the correct order
- ✅ Launch the frontend with hot-reload
- ✅ Open the application in your browser

**📊 Check System Status:**
```bash
# Quick status check with interactive menu
check-status.bat
```

**🛑 Stop All Services:**
```bash
# Stop all running services
stop-services.bat
```

### Manual Setup (Alternative)

1. **Clone and build the project:**
   ```bash
   git clone <repository-url>
   cd hotel-reservation
   mvn clean install
   ```

2. **Database Setup (Optional):**
   The system uses H2 in-memory databases by default. For PostgreSQL:
   ```sql
   CREATE DATABASE hotel_user_db;
   CREATE DATABASE hotel_room_db;
   CREATE DATABASE hotel_payment_db;
   ```

3. **Start services manually:**
   ```bash
   # Terminal 1 - User Service
   cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2
   
   # Terminal 2 - Room Service
   cd room-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2
   
   # Terminal 3 - Payment Service  
   cd payment-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2
   
   # Terminal 4 - API Gateway
   cd api-gateway && mvn spring-boot:run
   
   # Terminal 5 - Frontend
   cd frontend && npm install && npm start
   ```

## 🌐 Service Endpoints

### Frontend Application (Port 3000)
- **Main Application**: `http://localhost:3000`
- **Hot Reload**: Enabled for development

### API Gateway (Port 8080)
- **Health Check**: `GET http://localhost:8080/actuator/health`
- **Room Service Routes**: `http://localhost:8080/api/rooms/**`
- **User Service Routes**: `http://localhost:8080/api/users/**`
- **Payment Service Routes**: `http://localhost:8080/api/payments/**`

### User Service (Port 8083)
- **Health Check**: `GET http://localhost:8083/actuator/health`
- **API Documentation**: `http://localhost:8083/swagger-ui.html`
- **H2 Database Console**: `http://localhost:8083/h2-console`

### Room Service (Port 8081)
- **Health Check**: `GET http://localhost:8081/actuator/health`
- **API Documentation**: `http://localhost:8081/swagger-ui.html`
- **H2 Database Console**: `http://localhost:8081/h2-console`
- **Direct API**: `GET http://localhost:8081/api/rooms`

### Payment Service (Port 8082)
- **Health Check**: `GET http://localhost:8082/actuator/health`
- **API Documentation**: `http://localhost:8082/swagger-ui.html`
- **H2 Database Console**: `http://localhost:8082/h2-console`

### H2 Database Connection Details
- **JDBC URL**: `jdbc:h2:mem:hotel_*_db` (where * is user, room, or payment)
- **Username**: `sa`
- **Password**: (leave empty)

## 📊 Current Development Status

✅ **Completed (Phase 1, Day 1)**:
- Multi-module Maven project structure
- Parent POM with dependency management
- Basic microservice applications
- Common DTOs and utilities
- Configuration files for all services
- Spring Cloud Gateway setup
- Service discovery configuration

✅ **Completed (Phase 1, Day 2)**:
- Database schema design with JPA entities
- Room entity with comprehensive attributes
- Booking entity with relationship mapping
- Payment entity with gateway integration fields
- Flyway database migration scripts
- Sample data for testing
- Optimistic locking and auditing
- Database indexes for performance

✅ **Completed (Phase 1, Day 3)**:
- Repository layer with Spring Data JPA
- Custom query methods for complex searches
- Advanced repository operations (availability checks, statistics)
- Service layer interfaces with comprehensive methods
- Repository unit test framework setup
- Date utility functions for booking operations
- JPA repository configuration and activation

🔄 **Next Steps (Phase 2, Day 4)**:
- Service layer implementation
- Business logic development
- DTO mapping with MapStruct
- Transaction management

## 🎮 System Management Scripts

The project includes several convenience scripts for easy system management:

### 🚀 `start-all-services.bat`
**Complete system startup with all services and frontend**
- Checks prerequisites (Java 17+, Node.js 16+)
- Cleans up existing processes on ports 8080-8083, 3000
- Builds all Maven projects
- Installs frontend dependencies
- Starts all services in the correct order:
  1. User Service (Port 8083)
  2. Room Service (Port 8081)
  3. Payment Service (Port 8082)
  4. API Gateway (Port 8080)
  5. Frontend Webpack Dev Server (Port 3000)
- Waits for all services to be ready
- Opens application in browser automatically
- Provides interactive status monitoring

### 📊 `check-status.bat`
**Real-time system status monitoring**
- Health checks for all services
- Interactive menu system
- Quick access to start/stop operations
- Browser launching shortcuts
- Port usage monitoring

### 🛑 `stop-services.bat`
**Graceful shutdown of all services**
- Stops services by specific ports (more precise)
- Terminates Node.js processes (frontend)
- Shows shutdown status confirmation
- Verifies all ports are properly freed

### 💡 Usage Examples
```bash
# Start everything
start-all-services.bat

# Check what's running
check-status.bat

# Stop everything
stop-services.bat
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific service tests
cd room-service && mvn test
cd payment-service && mvn test
```

## 📝 Development Timeline

Following a structured 17-day development plan:
- **Phase 1 (Days 1-3)**: Project setup, database design, repository layer
- **Phase 2 (Days 4-6)**: Business logic and DTOs
- **Phase 3 (Days 7-9)**: REST API development
- **Phase 4 (Days 10-11)**: Security implementation
- **Phase 5 (Days 12-15)**: Microservices architecture
- **Phase 6 (Days 16-17)**: Testing and final integration

## 🤝 Contributing

1. Follow the established project structure
2. Implement one component at a time
3. Write unit tests for each component
4. Update documentation as needed

## 📄 License

This project is for educational and development purposes.
