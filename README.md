# NGO Posting Service 🏢

## Overview

The NGO Posting Service is a core microservice within the **Volunteer Resource Management System (VRMS)** that manages NGO posting creation, updates, volunteer registrations, and posting lifecycle management. This service acts as the central hub for NGO organizations to publish volunteer opportunities and track volunteer participation.<br>
<a href="https://cla-assistant.io/udaysingh21/Volunteer-Resource-Management-System"><img src="https://cla-assistant.io/readme/badge/udaysingh21/Volunteer-Resource-Management-System" alt="CLA assistant" /></a>

## VRMS Ecosystem

This service is part of a comprehensive volunteer management platform consisting of:

- **User Service** - Handles user registration, authentication, and profile management
- **NGO Posting Service** (This Service) - Manages NGO postings, profiles, and volunteer applications
- **Volunteer Service** - Manages volunteer profiles and activity tracking  
- **Matching Service** - Intelligent matching between volunteer skills and NGO requirements
- **Analytics Service** - Provides administrative insights and platform analytics

## Key Features

### NGO Posting Management
- ✅ Create, update, and delete volunteer opportunity postings
- 📝 Rich posting details including title, description, location, and requirements
- 📅 Date-based posting scheduling with start/end dates
- 🏷️ Domain-based categorization (Education, Health, Environment, etc.)
- 📍 Location-based filtering with city, state, and pincode support
- 👥 Volunteer capacity management with spots tracking

### Volunteer Registration System
- 🙋‍♀️ Volunteer registration for specific postings
- 📊 Real-time tracking of available spots
- 🔒 Role-based access control (NGO, VOLUNTEER, ADMIN)
- 📈 Registration analytics and monitoring

### Advanced Search & Filtering
- 🔍 Search by domain, location, and NGO
- 📖 Paginated results with customizable sorting
- 🗂️ Status-based filtering (Active, Closed, Draft, Archived)

### Security & Authentication
- 🔐 JWT-based authentication integration
- 👤 Role-based authorization (NGO/ADMIN for creation, All authenticated for viewing)
- 🛡️ Request validation and sanitization
- 🚫 Cross-origin resource sharing (CORS) configured

## 📋 API Endpoints

### Posting Management

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| `POST` | `/api/v1/postings` | Create new posting | NGO, ADMIN |
| `GET` | `/api/v1/postings/{id}` | Get posting by ID | Authenticated Users |
| `PUT` | `/api/v1/postings/{id}` | Update posting | NGO (own), ADMIN |
| `DELETE` | `/api/v1/postings/{id}` | Delete posting | NGO (own), ADMIN |

### Listing & Search

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| `GET` | `/api/v1/postings` | Get all postings (paginated) | Authenticated Users |
| `GET` | `/api/v1/postings/ngo/{ngoId}` | Get postings by NGO | NGO (own), ADMIN |
| `GET` | `/api/v1/postings/domain/{domain}` | Get postings by domain | Authenticated Users |

### Volunteer Registration

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| `POST` | `/api/v1/postings/{postingId}/register/{volunteerId}` | Register volunteer for posting | VOLUNTEER (self), ADMIN |

## 📊 Data Model

### NGO Post Entity

```json
{
  "id": "Long",
  "title": "String (max 255)",
  "description": "String (max 2000)",
  "domain": "String (Education, Health, Environment, etc.)",
  "location": "String",
  "city": "String",
  "state": "String", 
  "country": "String",
  "pincode": "String",
  "effortRequired": "String",
  "volunteersNeeded": "Integer",
  "volunteersSpotLeft": "Integer",
  "startDate": "LocalDate",
  "endDate": "LocalDate", 
  "ngoId": "Long",
  "contactEmail": "String",
  "contactPhone": "String",
  "status": "ACTIVE | CLOSED | DRAFT | ARCHIVED",
  "volunteersRegistered": "Set<Long>",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 13+**
- **Docker & Docker Compose** (for database)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd NGO-Postings-Service
   ```

2. **Start PostgreSQL Database**
   ```bash
   docker-compose up -d
   ```
   This starts PostgreSQL on port `5435` with database `ngo_posting_db`

3. **Configure Application Properties**
   
   Create/Update `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5435/ngo_posting_db
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   spring.datasource.driver-class-name=org.postgresql.Driver

   # JPA/Hibernate Configuration  
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true

   # Server Configuration
   server.port=8082

   # JWT Configuration
   jwt.secret=mySecretKey
   jwt.expiration=86400

   # Application Configuration
   spring.application.name=ngo-posting-service
   ```

4. **Build the Application**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR file:
   ```bash
   java -jar target/ngo-posting-service-0.0.1-SNAPSHOT.jar
   ```

The service will start on `http://localhost:8082`

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8082 |
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5435 |
| `DB_NAME` | Database name | ngo_posting_db |
| `DB_USERNAME` | Database username | postgres |
| `DB_PASSWORD` | Database password | postgres |
| `JWT_SECRET` | JWT signing secret | mySecretKey |
| `JWT_EXPIRATION` | JWT expiration time (seconds) | 86400 |

### CORS Configuration

The service is configured to accept requests from:
- `http://localhost:3000` (React dev server)
- `http://localhost:5173` (Vite dev server) 
- `http://localhost:5174` (Alternative Vite port)
- `http://localhost:8080` (Spring Boot default)

## Testing

### Run Tests
```bash
mvn test
```

### Integration Testing
```bash
mvn integration-test
```

### API Testing with curl

**Create a posting:**
```bash
curl -X POST http://localhost:8082/api/v1/postings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "title": "Beach Cleanup Drive",
    "description": "Help clean up the local beach and marine environment",
    "domain": "Environment",
    "location": "Marine Drive, Mumbai",
    "city": "Mumbai",
    "state": "Maharashtra", 
    "pincode": "400001",
    "effortRequired": "4 hours",
    "volunteersNeeded": 20,
    "startDate": "2024-01-15",
    "endDate": "2024-01-15",
    "contactEmail": "contact@ngo.org"
  }'
```

## API Documentation

Once the application is running, visit:
- **Swagger UI**: `http://localhost:8082/swagger-ui.html`
- **API Docs**: `http://localhost:8082/v3/api-docs`

## Docker Support

### Build Docker Image
```bash
docker build -t ngo-posting-service:latest .
```

### Run with Docker Compose
```bash
docker-compose up
```

## Architecture

### Technology Stack
- **Framework**: Spring Boot 3.5.6
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: Spring Security with JWT
- **API Documentation**: SpringDoc OpenAPI 3
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

### Design Patterns
- **Repository Pattern** for data access
- **Service Layer** for business logic  
- **DTO Pattern** for data transfer
- **Exception Handling** with global exception handlers

## Security

- **JWT Authentication** required for all endpoints
- **Role-based authorization** (NGO, VOLUNTEER, ADMIN)
- **Input validation** with Jakarta Bean Validation
- **SQL Injection** prevention through JPA/Hibernate
- **CORS** protection configured

## Health Check

Check service health:
```bash
curl http://localhost:8082/actuator/health
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is part of the Volunteer Resource Management System (VRMS).

## Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the API documentation at `/swagger-ui.html`

---

**Part of the Volunteer Resource Management System (VRMS) **
