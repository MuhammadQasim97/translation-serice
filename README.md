# Translation Management Service

A high-performance, scalable Translation Management Service built with Spring Boot that supports multiple locales and contextual tagging.

## Features

- **Multi-locale Support**: Store translations for multiple languages (en, fr, es) with easy extensibility
- **Contextual Tagging**: Tag translations with context information (mobile, desktop, web, etc.)
- **High Performance**: All endpoints respond in < 200ms, JSON export in < 500ms
- **Scalable Architecture**: Optimized for handling 100k+ translation records
- **RESTful API**: Complete CRUD operations with search functionality
- **JWT Authentication**: Token-based security for API access
- **Caching**: Built-in caching for improved performance
- **Docker Support**: Containerized deployment with Docker Compose
- **Comprehensive Testing**: >95% test coverage with unit, integration, and performance tests
- **OpenAPI Documentation**: Swagger UI for API exploration

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose (optional)

### Running with H2 (Development)

1. Clone the repository:
\`\`\`bash
git clone <repository-url>
cd translation-management-service
\`\`\`

2. Build the application:
\`\`\`bash
mvn clean package
\`\`\`

3. Run the application:
\`\`\`bash
java -jar target/translation-management-1.0.0.jar
\`\`\`

4. Access the application:
   - API Base URL: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Running with Docker

1. Build and start services:
\`\`\`bash
docker-compose up --build
\`\`\`

2. The application will be available at http://localhost:8080

### Seeding Test Data

To populate the database with 100k+ test records:

\`\`\`bash
java -jar target/translation-management-1.0.0.jar seed
\`\`\`

## Authentication

### Default Credentials
- Username: \`admin\`
- Password: \`admin123\`

### Getting JWT Token

\`\`\`bash
curl -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{"username": "admin", "password": "admin123"}'
\`\`\`

### Using JWT Token

Include the token in the Authorization header:
\`\`\`bash
curl -H "Authorization: Bearer <your-jwt-token>" \\
  http://localhost:8080/api/translations
\`\`\`

## API Endpoints

### Authentication
- \`POST /api/auth/login\` - Authenticate and get JWT token

### Translations
- \`POST /api/translations\` - Create translation
- \`GET /api/translations/{id}\` - Get translation by ID
- \`PUT /api/translations/{id}\` - Update translation
- \`DELETE /api/translations/{id}\` - Delete translation
- \`GET /api/translations/search\` - Search translations
- \`GET /api/translations/export/{locale}\` - Export translations for locale
- \`GET /api/translations/locales\` - Get available locales
- \`GET /api/translations/count\` - Get total translation count

### Search Parameters
- \`key\` - Search by translation key
- \`content\` - Search by translation content
- \`locale\` - Filter by locale
- \`tag\` - Filter by tag name
- \`page\` - Page number (default: 0)
- \`size\` - Page size (default: 20)
- \`sort\` - Sort field (default: key)
- \`direction\` - Sort direction (asc/desc, default: asc)

## Example Usage

### Create Translation

\`\`\`bash
curl -X POST http://localhost:8080/api/translations \\
  -H "Authorization: Bearer <token>" \\
  -H "Content-Type: application/json" \\
  -d '{
    "key": "welcome.message",
    "locale": "en",
    "content": "Welcome to our application!",
    "tags": ["mobile", "desktop"]
  }'
\`\`\`

### Search Translations

\`\`\`bash
curl "http://localhost:8080/api/translations/search?key=welcome&locale=en&tag=mobile" \\
  -H "Authorization: Bearer <token>"
\`\`\`

### Export Translations

\`\`\`bash
curl http://localhost:8080/api/translations/export/en \\
  -H "Authorization: Bearer <token>"
\`\`\`

Response:
\`\`\`json
{
  "welcome.message": "Welcome to our application!",
  "error.notfound": "Resource not found",
  "button.submit": "Submit"
}
\`\`\`

## Database Schema

### Tables
- \`translations\` - Main translation storage
- \`tags\` - Tag definitions
- \`translation_tags\` - Many-to-many relationship
- \`users\` - User authentication

### Indexes
- Optimized indexes on frequently queried columns
- Composite indexes for complex queries
- Full-text search capabilities

## Performance Optimizations

1. **Database Indexing**: Strategic indexes on key columns
2. **Batch Processing**: Hibernate batch inserts/updates
3. **Caching**: Spring Cache for frequently accessed data
4. **Connection Pooling**: Optimized database connections
5. **Lazy Loading**: JPA lazy loading for associations
6. **Query Optimization**: Custom queries for complex operations

## Testing

### Run All Tests
\`\`\`bash
mvn test
\`\`\`

### Generate Coverage Report
\`\`\`bash
mvn jacoco:report
\`\`\`

Coverage report will be available at \`target/site/jacoco/index.html\`

### Test Categories
- **Unit Tests**: Service and component testing
- **Integration Tests**: Full application workflow testing
- **Performance Tests**: Response time validation
- **Security Tests**: Authentication and authorization testing

## Configuration

### Application Properties

Key configuration options in \`application.properties\`:

\`\`\`properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# JWT
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=3600000

# Performance
spring.jpa.properties.hibernate.jdbc.batch_size=50
\`\`\`

### Production Configuration

For production deployment, use \`application-prod.properties\`:

\`\`\`properties
spring.profiles.active=prod
spring.datasource.url=jdbc:postgresql://localhost:5432/translation_db
spring.jpa.hibernate.ddl-auto=validate
\`\`\`

## Architecture & Design

### SOLID Principles Implementation

1. **Single Responsibility**: Each class has a single, well-defined purpose
2. **Open/Closed**: Extensible design through interfaces and abstractions
3. **Liskov Substitution**: Proper inheritance hierarchies
4. **Interface Segregation**: Focused, cohesive interfaces
5. **Dependency Inversion**: Dependency injection throughout

### Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer between layers
- **Factory Pattern**: Object creation (data seeding)
- **Strategy Pattern**: Authentication strategies
- **Observer Pattern**: Event-driven updates

### Security Features

- JWT-based authentication
- Role-based access control
- CORS configuration
- SQL injection prevention
- Input validation and sanitization

## Monitoring & Health Checks

### Actuator Endpoints
- \`/actuator/health\` - Application health status
- \`/actuator/metrics\` - Application metrics
- \`/actuator/info\` - Application information

### Logging

Structured logging with different levels:
- \`INFO\` - General application flow
- \`DEBUG\` - Detailed debugging information
- \`WARN\` - Warning conditions
- \`ERROR\` - Error conditions

## Deployment

### Docker Deployment

1. Build the application:
\`\`\`bash
mvn clean package
\`\`\`

2. Build Docker image:
\`\`\`bash
docker build -t translation-management .
\`\`\`

3. Run with Docker Compose:
\`\`\`bash
docker-compose up -d
\`\`\`

### Production Considerations

1. **Database**: Use PostgreSQL or MySQL for production
2. **Caching**: Consider Redis for distributed caching
3. **Load Balancing**: Use nginx or similar for load balancing
4. **Monitoring**: Implement application monitoring (Prometheus, Grafana)
5. **Backup**: Regular database backups
6. **SSL/TLS**: Enable HTTPS in production

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the GitHub repository
- Check the API documentation at \`/swagger-ui.html\`
- Review the test cases for usage examples
