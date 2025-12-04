# NGO Posting Service

## 1. Project Overview

The NGO Posting Service is a core microservice within the **Volunteer Resource Management System (VRMS)** that manages NGO posting creation, updates, volunteer registrations, and posting lifecycle management. This service acts as the central hub for NGO organizations to publish volunteer opportunities and track volunteer participation.<br>
<a href="https://cla-assistant.io/udaysingh21/Volunteer-Resource-Management-System"><img src="https://cla-assistant.io/readme/badge/udaysingh21/Volunteer-Resource-Management-System" alt="CLA assistant" /></a>

### VRMS Ecosystem

This service is one component of a comprehensive volunteer management platform:
* **User Service**: Handles user registration, authentication, and profile management.
* **NGO Posting Service (This Service)**: Manages postings, profiles, and volunteer applications.
* **Volunteer Service**: Manages volunteer profiles and activity tracking.
* **Matching Service**: Provides intelligent matching between volunteer skills and NGO requirements.
* **Analytics Service**: Offers administrative insights and platform analytics.

---

## 2. Key Service Features

### 2.1. Posting and Capacity Management
* Supports full **CRUD** (Create, update, and delete) operations for volunteer opportunity postings.
* Manages rich posting details, including title, description, location, requirements, and domain-based categorization (e.g., Education, Health).
* Enables date-based posting scheduling with explicit start and end dates.
* Provides **volunteer capacity management** and real-time spot tracking.
* Includes **location-based filtering** (city, state, and pincode support).

### 2.2. Volunteer Registration and Security
* Implements a dedicated system for **volunteer registration** to specific postings.
* Features **Role-Based Access Control (RBAC)** to manage access for NGO, VOLUNTEER, and ADMIN roles.
* Integrates **JWT-based authentication** for securing all endpoints.
* Enforces **Role-based authorization** (NGO/ADMIN for creation, all authenticated users for viewing).
* Includes **request validation and sanitization** for enhanced security.
* Provides status-based filtering (Active, Closed, Draft, Archived).

---

## 3. Technology and Architecture

### 3.1. Technology Stack
* **Framework**: Spring Boot 3.5.6.
* **Database**: PostgreSQL with JPA/Hibernate.
* **Security**: Spring Security with JWT.
* **API Documentation**: SpringDoc OpenAPI 3.
* **Build Tool**: Maven.
* **Validation**: Jakarta Bean Validation.
* **Testing**: JUnit 5, Spring Boot Test.

### 3.2. Design Patterns
The service utilizes several established design patterns to ensure robustness and separation of concerns:
* **Repository Pattern**: Used for clean data access.
* **Service Layer**: Dedicated layer for housing complex business logic.
* **DTO Pattern**: Used for structured data transfer between layers.
* **Exception Handling**: Implemented with global exception handlers.

### 3.3. Security Measures
The service is secured through multiple layers:
* **JWT Authentication** required for all endpoints.
* **Role-based authorization** enforced across the application.
* **Input validation** using Jakarta Bean Validation.
* **SQL Injection** prevention facilitated through JPA/Hibernate.
* **CORS** protection configured.

---

## 4. Getting Started

### 4.1. Prerequisites
Ensure the following software is installed on your system:
* **Java 17** or higher.
* **Maven 3.6+**.
* **PostgreSQL 13+**.
* **Docker & Docker Compose** (required for database setup).

### 4.2. Installation and Setup

1.  **Clone the repository**
    ```bash
    git clone <repository-url>
    cd NGO-Postings-Service
    ```
2.  **Start PostgreSQL Database**
    ```bash
    docker-compose up -d
    ```
    *This starts PostgreSQL on port `5435` with database `ngo_posting_db`*.
3.  **Configure Application Properties**
    *Create or update `src/main/resources/application.properties` with the required database, server, and JWT configurations (detailed below).*
4.  **Build the Application**
    ```bash
    mvn clean install
    ```
5.  **Run the Application**
    ```bash
    mvn spring-boot:run
    ```
    *Alternatively, run the JAR file:*
    ```bash
    java -jar target/ngo-posting-service-0.0.1-SNAPSHOT.jar
    ```
    *The service will start on `http://localhost:8082`*.

---

## 5. Configuration Reference

### 5.1. Database and Server Properties
The following properties are configured in `application.properties`:

| Configuration Group | Key Property | Value |
| :--- | :--- | :--- |
| **Database** | `spring.datasource.url` | `jdbc:postgresql://localhost:5435/ngo_posting_db` |
| **Server** | `server.port` | `8082` |
| **JPA/Hibernate** | `spring.jpa.hibernate.ddl-auto` | `update` |
| **Application** | `spring.application.name` | `ngo-posting-service` |

### 5.2. Environment Variables
For environment-specific configuration, the following variables are used:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `SERVER_PORT` | Application port | 8082 |
| `DB_HOST` | Database host | localhost |
| `DB_NAME` | Database name | ngo\_posting\_db |
| `JWT_SECRET` | JWT signing secret | mySecretKey |
| `JWT_EXPIRATION` | JWT expiration time (seconds) | 86400 |

### 5.3. CORS Configuration
The service is configured to accept requests from specific development hosts:
* `http://localhost:3000` (React dev server)
* `http://localhost:5173` (Vite dev server)
* `http://localhost:8080` (Spring Boot default)

---

## 6. API Endpoints and Data Model

### 6.1. API Endpoints

| Method | Endpoint | Description | Access Level |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/postings` | Create new posting | NGO, ADMIN |
| `GET` | `/api/v1/postings/{id}` | Get posting by ID | Authenticated Users |
| `PUT` | `/api/v1/postings/{id}` | Update posting | NGO (own), ADMIN |
| `GET` | `/api/v1/postings` | Get all postings (paginated) | Authenticated Users |
| `GET` | `/api/v1/postings/ngo/{ngoId}` | Get postings by NGO | NGO (own), ADMIN |
| `POST` | `/api/v1/postings/{postingId}/register/{volunteerId}` | Register volunteer for posting | VOLUNTEER (self), ADMIN |

### 6.2. NGO Post Entity Data Model
The primary entity managed by the service:

```json
{
  "id": "Long",
  "title": "String (max 255)",
  "description": "String (max 2000)",
  "domain": "String (Education, Health, Environment, etc.)",
  "city": "String",
  "startDate": "LocalDate",
  "endDate": "LocalDate", 
  "volunteersNeeded": "Integer",
  "volunteersSpotLeft": "Integer",
  "ngoId": "Long",
  "status": "ACTIVE | CLOSED | DRAFT | ARCHIVED",
  "volunteersRegistered": "Set<Long>",
  "createdAt": "LocalDateTime"
}
```

### 6.3. API Documentation Links
Once the application is running on port 8082, documentation is accessible at:
* **Swagger UI**: `http://localhost:8082/swagger-ui.html`
* **API Docs**: `http://localhost:8082/v3/api-docs`

---

## 7. Testing and Maintenance

### 7.1. Testing Commands
* **Unit Tests:** 
```bash 
  mvn test
```

* **Integration Tests:** 
```bash
  mvn integration-test
```

### 7.2. Health Check
Check service health via the actuator endpoint:
```bash
curl http://localhost:8082/actuator/health
```

### 7.3. Docker Support
* **Build Image:** 
```bash
  docker build -t ngo-posting-service:latest .
```

* **Run with Compose:**
```bash
  docker-compose up
```

---

## 8. Contributing and Support

### 8.1. Contributing Guidelines
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/new-feature-name`).
3. Commit changes (`git commit -m 'Commit Message'`).
4. Push to branch (`git push origin feature/new-feature-name`).
5. Open a Pull Request.

**Part of the Volunteer Resource Management System (VRMS) **
