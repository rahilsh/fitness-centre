# Fitness Centre Management System

A Spring Boot REST API for managing fitness centres, user bookings, and activity scheduling.

## 🚀 Features

### ✅ Core Functionality
- **User Management**: Register and manage user accounts
- **Fitness Centre Management**: Create and manage fitness centres with activities
- **Booking System**: Book available slots and manage bookings
- **Activity Search**: Search for available activities across centres
- **Health Check**: Application health monitoring endpoint

### ✅ Recent Enhancements (v1.1.0)

#### Input Validation & Error Handling
- Comprehensive input validation on all request bodies using Jakarta Validation API
- Global exception handling with custom error responses
- Detailed error messages with request paths for debugging
- Proper HTTP status codes (400, 404, 500)

#### API Documentation
- **Swagger UI**: Interactive API explorer at `/swagger-ui.html`
- **OpenAPI Specification**: Machine-readable API spec at `/v3/api-docs`
- Complete endpoint documentation with descriptions and examples
- Request/response model documentation
- HTTP status code documentation

#### Database Layer Improvements
- **JPA Relationships**: Proper foreign key constraints between entities
- **Audit Fields**: Automatic tracking of creation and modification timestamps
- **Database Constraints**: Unique constraints on names, composite uniqueness on slots
- **Lazy Loading**: Optimized query performance with FetchType.LAZY
- **Auto-increment IDs**: Database-managed ID generation (Long type for scalability)

#### Data Integrity
- Foreign key relationships between User ↔ Booking, Booking ↔ Slot, Slot ↔ FitnessCentre
- Cascade delete operations for parent-child relationships
- Unique constraints prevent duplicate data
- Nullable constraints ensure data completeness

## 🛠️ Technology Stack

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 4.1.0
- **Database**: 
  - H2 (local development)
  - PostgreSQL (production)
  - MySQL (alternative production)
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Validation API
- **API Documentation**: Springdoc OpenAPI 2.3.0 / Swagger UI
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Code Coverage**: JaCoCo (91.93% coverage)
- **Build Tool**: Maven

## 📋 Prerequisites

- **Java 21+**: Download from [OpenJDK](https://jdk.java.net/21/) or use [Homebrew](https://brew.sh)
- **Maven 3.8+**: `brew install maven` (macOS) or [Maven Download](https://maven.apache.org/)
- **Git**: `brew install git` (macOS)

## 🏃 Getting Started

### Clone the Repository
```bash
git clone https://github.com/rahilsh/fitness-centre.git
cd fitness-centre
```

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

## 📚 API Endpoints

### Health Check (Public)
```
GET /health - Application health status
```

### Users
```
POST   /users                  - Register a new user (validation required)
GET    /users                  - Get all users
```

### Fitness Centres
```
POST   /fitnessCentres         - Create a fitness centre
GET    /fitnessCentres/{id}/slots     - Get slots for a centre
POST   /fitnessCentres/{id}/slots     - Add activity/slot to a centre
```

### Bookings
```
POST   /bookings               - Create a booking
GET    /bookings               - Get all bookings
GET    /bookings/{id}          - Get booking details
PATCH  /bookings/{id}          - Cancel a booking
```

### Search
```
GET    /search                 - Search available activities
```

### Sample Requests

Create a User:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe"}'
```

Create a Fitness Centre:
```bash
curl -X POST http://localhost:8080/fitnessCentres \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gold Gym",
    "timings": [[9, 10], [10, 11]],
    "supportedActivities": ["YOGA", "WEIGHTS"]
  }'
```

See [FitnessCentre.http](FitnessCentre.http) for more examples.

## ✅ Testing

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test
```bash
mvn test -Dtest=UserServiceTest
```

### View Code Coverage Report
```bash
mvn clean test
open target/site/jacoco/index.html
```

**Current Coverage**: 91.93% ✅ (Exceeds 85% threshold)
**Test Count**: 136 tests (73 unit + 50 integration + 13 handler tests)

## 🗄️ Database Configuration

### Local Development (H2 In-Memory)
Configured automatically in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

No external database setup needed! Data is created fresh on startup.

### Production Deployment

#### PostgreSQL (Default)
```bash
java -jar target/fitness-centre-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://HOST:5432/fitness_centre \
  --spring.datasource.username=USER \
  --spring.datasource.password=PASS
```

#### MySQL
```bash
java -jar target/fitness-centre-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod,mysql \
  --spring.datasource.url=jdbc:mysql://HOST:3306/fitness_centre \
  --spring.datasource.username=USER \
  --spring.datasource.password=PASS
```

## 🔒 Input Validation

All request bodies are validated:

```json
{
  "name": "John Doe"  // Required, 2-100 characters
}
```

Invalid requests return error responses:
```json
{
  "timestamp": "2026-06-27T13:45:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input provided",
  "path": "/users",
  "details": "name: must not be blank"
}
```

## 📊 Project Statistics

- **Lines of Code**: ~2,500+
- **Test Cases**: 136
- **Code Coverage**: 91.93%
- **Java Version**: 21 (LTS)
- **API Endpoints**: 10+
- **Database Tables**: 4 (User, Booking, FitnessCentre, Slot)
- **Documented Endpoints**: 100% in Swagger UI

## 🔄 Recent Commits

```
fe585ab - feat: implement database relationships and audit fields
ca1e15a - feat: add Swagger/OpenAPI documentation
f5b4e2b - feat: add input validation and global error handling
bb8d13a - feat: migrate from in-memory stores to Spring Data JPA
29631e5 - chore: migration to jdk 21
```

## 📁 Project Structure

```
fitness-centre/
├── src/
│   ├── main/java/com/rsh/fitness_centre/
│   │   ├── config/          # Spring configurations (JPA, Swagger)
│   │   ├── controller/      # REST endpoints
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── service/         # Business logic
│   │   ├── exception/       # Custom exceptions & handlers
│   │   └── util/            # Utilities
│   ├── test/java/           # Unit and integration tests
│   └── main/resources/      # Configuration files
├── .docs/                   # Documentation (gitignored)
│   ├── IMPROVEMENTS.md      # Detailed improvement suggestions
│   └── AUTH_DESIGN.md       # Authentication design document
├── pom.xml                  # Maven dependencies
├── README.md                # This file
└── FitnessCentre.http       # API request examples
```

## 🚀 Upcoming Features

- ✅ ~~Input Validation~~ (v1.1.0)
- ✅ ~~API Documentation (Swagger)~~ (v1.1.0)
- ✅ ~~Database Relationships~~ (v1.1.0)
- 🔲 Authentication & Authorization (JWT)
- 🔲 Logging & Monitoring
- 🔲 Pagination & Filtering
- 🔲 Response DTOs
- 🔲 Transaction Management

See [.docs/IMPROVEMENTS.md](.docs/IMPROVEMENTS.md) for detailed improvement roadmap.

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes using Conventional Commits (`git commit -m "feat: add AmazingFeature"`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 👤 Author

**Rahil Shaikh**
- GitHub: [@rahilsh](https://github.com/rahilsh)
- Email: rahilrshk@gmail.com

## 🐛 Found an Issue?

Please open an [Issue](https://github.com/rahilsh/fitness-centre/issues) with:
- Clear description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Java/Spring Boot versions

## 📞 Support

For questions or support, open a GitHub issue or reach out via email.

---

**Last Updated**: June 27, 2026 | **Version**: 1.1.0
