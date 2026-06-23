Author and Book Management System
A comprehensive RESTful API for managing authors and books in a library system, built with Spring Boot, Hibernate/JPA, Docker, and deployed on Azure Cloud.

Java
Spring Boot
License

📋 Table of Contents
Overview
Features
Technologies
Architecture
Getting Started
API Documentation
Security
Testing
Docker Deployment
CI/CD Pipeline
Azure Deployment
Design Patterns & SOLID Principles
Project Structure
Contributing
License
🎯 Overview
This project is a production-ready RESTful API for library management that demonstrates best practices in Spring Boot development, including:

Clean Architecture with clear separation of concerns
SOLID principles and Design Patterns
Comprehensive security with role-based access control
Bulk operations with CSV import/export
Complete CI/CD pipeline with Jenkins
Containerization with Docker
Cloud deployment on Azure
✨ Features
Core Functionality
✅ Complete CRUD Operations for Authors and Books
✅ Bulk Operations - Import/Export data via CSV files
✅ Search & Filter - Search authors by name, books by title
✅ Relationship Management - One-to-Many relationship between Authors and Books
✅ Data Validation - Comprehensive validation rules
✅ Duplicate Prevention - Ensures no duplicate authors or ISBNs
Security Features
🔒 Role-Based Access Control (RBAC)
Admin: Full CRUD access to all resources
Librarian: Read-only access (GET operations)
🔒 BCrypt Password Encryption
🔒 HTTP Basic Authentication
🔒 Secure endpoints with Spring Security
Quality & DevOps
📝 Interactive API Documentation - Swagger/OpenAPI 3.0
🧪 Comprehensive Testing - Unit & Integration tests with 80%+ coverage
📊 Multi-level Logging - Separate log files for application, errors, and SQL
🐳 Docker Support - Multi-stage builds with Docker Compose
🔄 CI/CD Pipeline - Automated testing and deployment with Jenkins
☁️ Cloud Ready - Deployable to Azure App Service
🛠 Technologies
Category	Technologies
Backend	Java 17, Spring Boot 3.1.5, Spring Data JPA, Hibernate
Security	Spring Security, BCrypt
Database	PostgreSQL (Production), H2 (Development/Testing)
API Documentation	SpringDoc OpenAPI (Swagger)
Build Tool	Maven
Libraries	Lombok, OpenCSV, Jackson
Testing	JUnit 5, Mockito, AssertJ, Spring Boot Test
Containerization	Docker, Docker Compose
CI/CD	Jenkins
Cloud	Azure (App Service, Container Registry, PostgreSQL)
🏗 Architecture
Layered Architecture
text

┌─────────────────────────────────────────┐
│   Controllers (REST API Layer)          │  ← HTTP Requests/Responses
├─────────────────────────────────────────┤
│   DTOs (Data Transfer Objects)          │  ← API Models
├─────────────────────────────────────────┤
│   Services (Business Logic Layer)       │  ← Business Rules
├─────────────────────────────────────────┤
│   Repositories (Data Access Layer)      │  ← Database Operations
├─────────────────────────────────────────┤
│   Entities (Domain Models)              │  ← JPA Entities
├─────────────────────────────────────────┤
│   Database (PostgreSQL/H2)              │  ← Persistent Storage
└─────────────────────────────────────────┘
Key Components
Controllers: Handle HTTP requests, input validation, and response formatting
Services: Implement business logic, transaction management, and orchestration
Repositories: Abstract data access using Spring Data JPA
Entities: JPA entities representing database tables
DTOs: Data Transfer Objects for API communication
Exception Handlers: Global exception handling for consistent error responses
🚀 Getting Started
Prerequisites
Bash

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (optional)
- PostgreSQL (for production mode)
- Git
Installation & Setup
Clone the repository
Bash

git clone https://github.com/your-username/author-book-management.git
cd author-book-management
Build the project
Bash

mvn clean install
Run the application (Development Mode)
Bash

mvn spring-boot:run
The application will start on http://localhost:8080 using H2 in-memory database.

Access the application
API Base URL: http://localhost:8080/api/v1
Swagger UI: http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:librarydb
Username: sa
Password: (leave empty)
Default Test Users
Username	Password	Role	Permissions
admin	admin123	ADMIN	Full CRUD
librarian	librarian123	LIBRARIAN	Read Only
📚 API Documentation
Swagger UI
Access interactive API documentation at: http://localhost:8080/swagger-ui.html

Quick API Reference
Authors API
Method	Endpoint	Description	Required Role
GET	/api/v1/authors	Get all authors	ADMIN, LIBRARIAN
GET	/api/v1/authors/{id}	Get author by ID	ADMIN, LIBRARIAN
GET	/api/v1/authors/search?lastName={name}	Search authors	ADMIN, LIBRARIAN
POST	/api/v1/authors	Create new author	ADMIN
PUT	/api/v1/authors/{id}	Update author	ADMIN
DELETE	/api/v1/authors/{id}	Delete author	ADMIN
Books API
Method	Endpoint	Description	Required Role
GET	/api/v1/books	Get all books	ADMIN, LIBRARIAN
GET	/api/v1/books/{id}	Get book by ID	ADMIN, LIBRARIAN
GET	/api/v1/books/search?title={title}	Search books	ADMIN, LIBRARIAN
GET	/api/v1/books/author/{id}	Get books by author	ADMIN, LIBRARIAN
POST	/api/v1/books	Create new book	ADMIN
PUT	/api/v1/books/{id}	Update book	ADMIN
PUT	/api/v1/books/bulk	Bulk update books	ADMIN
DELETE	/api/v1/books/{id}	Delete book	ADMIN
Bulk Operations API
Method	Endpoint	Description	Required Role
POST	/api/v1/bulk/authors/import	Import authors from CSV	ADMIN
POST	/api/v1/bulk/books/import	Import books from CSV	ADMIN
GET	/api/v1/bulk/authors/export	Export authors to CSV	ADMIN, LIBRARIAN
GET	/api/v1/bulk/books/export	Export books to CSV	ADMIN, LIBRARIAN
Sample API Requests
Create an Author
Bash

curl -X POST "http://localhost:8080/api/v1/authors" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Austen",
    "email": "jane.austen@example.com",
    "biography": "English novelist known for romantic fiction",
    "birthYear": 1775
  }'
Create a Book
Bash

curl -X POST "http://localhost:8080/api/v1/books" \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Pride and Prejudice",
    "isbn": "978-0-14-143951-8",
    "publicationYear": 1813,
    "genre": "Romance",
    "price": 12.99,
    "availableCopies": 25,
    "description": "Romantic novel of manners",
    "authorId": 1
  }'
Get All Authors
Bash

curl -X GET "http://localhost:8080/api/v1/authors" \
  -u librarian:librarian123
Import Authors from CSV
Bash

curl -X POST "http://localhost:8080/api/v1/bulk/authors/import" \
  -u admin:admin123 \
  -F "file=@sample-authors.csv"
Export Books to CSV
Bash

curl -X GET "http://localhost:8080/api/v1/bulk/books/export" \
  -u librarian:librarian123 \
  -o books-export.csv
CSV File Formats
Authors CSV Format
csv

First Name,Last Name,Email,Biography,Birth Year
Jane,Austen,jane.austen@example.com,English novelist,1775
George,Orwell,george.orwell@example.com,English novelist and essayist,1903
Books CSV Format
csv

Title,ISBN,Publication Year,Genre,Price,Available Copies,Description,Author ID
1984,978-0-452-28423-4,1949,Dystopian,19.99,20,Dystopian fiction,1
Animal Farm,978-0-452-28424-1,1945,Political Satire,15.99,18,Allegorical novella,1
🔒 Security
Authentication
The API uses HTTP Basic Authentication. Include credentials in requests:

Bash

# Using curl
curl -u username:password http://localhost:8080/api/v1/authors

# Or with Authorization header
curl -H "Authorization: Basic base64(username:password)" http://localhost:8080/api/v1/authors
Authorization (Role-Based Access Control)
Role	Permissions
ADMIN	✅ All CRUD operations<br>✅ Bulk import/export<br>✅ User management
LIBRARIAN	✅ View all data (GET requests)<br>✅ Export data<br>❌ Create/Update/Delete operations
Password Security
All passwords are encrypted using BCrypt with strength factor of 10
Passwords are never stored in plain text
Password hashing is handled automatically by Spring Security
Endpoint Security Matrix
Endpoint Pattern	ADMIN	LIBRARIAN	Anonymous
GET /api/v1/**	✅	✅	❌
POST /api/v1/**	✅	❌	❌
PUT /api/v1/**	✅	❌	❌
DELETE /api/v1/**	✅	❌	❌
/swagger-ui/**	✅	✅	✅
🧪 Testing
Running Tests
Bash

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthorServiceTest

# Run tests with coverage report
mvn clean test jacoco:report

# Run only integration tests
mvn test -Dtest=*IntegrationTest

# Run only unit tests
mvn test -Dtest=*Test -Dtest=!*IntegrationTest
Test Coverage
View coverage report at: target/site/jacoco/index.html

Coverage Goals:

Overall: 80%+
Service Layer: 90%+
Repository Layer: 70%+
Controller Layer: 85%+
Test Structure
text

src/test/java/
└── com.library.management
    ├── controller/
    │   ├── AuthorControllerTest.java           (Unit Tests)
    │   ├── AuthorControllerIntegrationTest.java (Integration Tests)
    │   └── BookControllerIntegrationTest.java
    ├── service/
    │   ├── AuthorServiceTest.java
    │   └── BookServiceTest.java
    └── repository/
        └── AuthorRepositoryTest.java
Sample Test Cases
✅ CRUD operations for Authors and Books
✅ Validation rules enforcement
✅ Duplicate prevention logic
✅ Security and authorization
✅ Exception handling
✅ Bulk operations
✅ Search functionality
🐳 Docker Deployment
Build Docker Image
Bash

docker build -t library-management:latest .
Run with Docker Compose
Bash

# Start all services (PostgreSQL + Application)
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
Docker Services
Service	Port	Description
app	8080	Spring Boot Application
postgres	5432	PostgreSQL Database
Environment Variables
Bash

# Database Configuration
DATABASE_URL=jdbc:postgresql://postgres:5432/librarydb
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# Application Profile
SPRING_PROFILES_ACTIVE=prod
Docker Image Details
Base Image: eclipse-temurin:17-jre-alpine
Size: ~180 MB (optimized with multi-stage build)
Security: Runs as non-root user
Health Check: Configured for container orchestration
🔄 CI/CD Pipeline
Jenkins Pipeline Overview
The project includes a complete Jenkins pipeline with the following stages:

text

1. Checkout        → Clone repository
2. Build           → Compile source code
3. Unit Tests      → Run tests with JaCoCo coverage
4. Code Quality    → Static code analysis
5. Package         → Create JAR file
6. Docker Build    → Build container image
7. Docker Push     → Push to registry
8. Deploy Test     → Deploy to test environment
9. Integration     → Run integration tests
10. Security Scan  → Vulnerability scanning
11. Deploy Azure   → Deploy to Azure (main branch only)
Jenkins Setup
Required Plugins:

Git
Maven Integration Plugin
Docker Pipeline
JUnit Plugin
JaCoCo Plugin
Email Extension Plugin
Azure CLI Plugin
Triggering Builds
Automatic: Webhook on push to repository
Manual: Trigger from Jenkins UI
Scheduled: Nightly builds (optional)
Pipeline Configuration
The pipeline is defined in Jenkinsfile at the root of the repository.

groovy

pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.5'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Build') { ... }
        stage('Test') { ... }
        stage('Deploy') { ... }
    }
}
Build Notifications
✅ Success: Email notification with build summary
❌ Failure: Email notification with error details
📊 Reports: Test results and coverage reports archived
☁️ Azure Deployment
Prerequisites
Azure CLI installed and configured
Azure subscription
Permissions to create resources
Quick Deployment
Bash

# Make script executable
chmod +x deploy-azure.sh

# Run deployment
./deploy-azure.sh
Azure Resources Created
Resource	Type	Purpose
Resource Group	Resource Container	Groups all resources
Container Registry	ACR	Stores Docker images
PostgreSQL Server	Flexible Server	Production database
App Service Plan	Linux B1	Hosting plan
Web App	Container	Runs the application
Access Deployed Application
After successful deployment:

text

Application URL: https://{your-app-name}.azurewebsites.net
Swagger UI: https://{your-app-name}.azurewebsites.net/swagger-ui.html
View Application Logs
Bash

az webapp log tail \
  --name {your-app-name} \
  --resource-group library-management-rg