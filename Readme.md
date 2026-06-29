# 📚 Author & Book Management System

A production-ready **RESTful Library Management API** built with **Spring Boot**, **Hibernate/JPA**, **PostgreSQL**, **Docker**, **Jenkins**, and **Azure Cloud**.

The project demonstrates enterprise-level backend development practices including clean architecture, security, testing, CI/CD, containerization, and cloud deployment.

-----

## 🚀 Features

### 📖 Library Management

* Complete CRUD operations for Authors and Books
* Search authors by name
* Search books by title
* One-to-Many relationship between Authors and Books
* Bulk import/export using CSV
* Duplicate validation for Authors and ISBNs
* Input validation with meaningful error handling

---

### 🔐 Security

* Spring Security
* HTTP Basic Authentication
* BCrypt password encryption
* Role-Based Access Control (RBAC)

| Role          | Permissions      |
| ------------- | ---------------- |
| **ADMIN**     | Full CRUD access |
| **LIBRARIAN** | Read-only access |

---

### 🧪 Quality & DevOps

* Swagger / OpenAPI Documentation
* Unit & Integration Testing
* JaCoCo Code Coverage
* Docker & Docker Compose
* Jenkins CI/CD Pipeline
* Azure App Service Deployment
* Structured Logging

---

# 🛠 Tech Stack

| Category         | Technologies                |
| ---------------- | --------------------------- |
| Language         | Java 21                     |
| Framework        | Spring Boot 3               |
| ORM              | Hibernate / Spring Data JPA |
| Database         | PostgreSQL, H2              |
| Security         | Spring Security, BCrypt     |
| Documentation    | Swagger (OpenAPI 3)         |
| Build Tool       | Maven                       |
| Testing          | JUnit 5, Mockito, AssertJ   |
| Containerization | Docker, Docker Compose      |
| CI/CD            | Jenkins                     |
| Cloud            | Azure App Service           |
| Libraries        | Lombok, Jackson, OpenCSV    |

---

# 🏗 Architecture

```
Client
   │
REST Controllers
   │
DTO Layer
   │
Service Layer
   │
Repository Layer
   │
JPA Entities
   │
PostgreSQL / H2
```

### Layers

* **Controllers** – REST endpoints
* **DTOs** – Request & Response models
* **Services** – Business logic
* **Repositories** – Database operations
* **Entities** – Domain models
* **Exception Handlers** – Centralized error handling

---

# 📂 Project Structure

```
src
├── main
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── config
│   ├── security
│   ├── exception
│   └── util
│
└── test
    ├── controller
    ├── service
    ├── repository
    └── integration
```

---

# ⚙ Getting Started

## Prerequisites

* Java 21+
* Maven 3.6+
* Docker (Optional)
* PostgreSQL
* Git

---

## Clone Repository

```bash
git clone https://github.com/your-username/author-book-management.git

cd author-book-management
```

---

## Build

```bash
mvn clean install
```

---

## Run

```bash
mvn spring-boot:run
```

Application starts at

```
http://localhost:8080
```

---

# 📑 API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Base URL

```
http://localhost:8080/api/v1
```

---

# 🔑 Default Credentials

| Username  | Password     | Role      |
| --------- | ------------ | --------- |
| admin     | admin123     | ADMIN     |
| librarian | librarian123 | LIBRARIAN |

---

# 📚 API Endpoints

## Authors

| Method | Endpoint          |
| ------ | ----------------- |
| GET    | `/authors`        |
| GET    | `/authors/{id}`   |
| GET    | `/authors/search` |
| POST   | `/authors`        |
| PUT    | `/authors/{id}`   |
| DELETE | `/authors/{id}`   |

---

## Books

| Method | Endpoint             |
| ------ | -------------------- |
| GET    | `/books`             |
| GET    | `/books/{id}`        |
| GET    | `/books/search`      |
| GET    | `/books/author/{id}` |
| POST   | `/books`             |
| PUT    | `/books/{id}`        |
| PUT    | `/books/bulk`        |
| DELETE | `/books/{id}`        |

---

## Bulk Operations

| Method | Endpoint               |
| ------ | ---------------------- |
| POST   | `/bulk/authors/import` |
| POST   | `/bulk/books/import`   |
| GET    | `/bulk/authors/export` |
| GET    | `/bulk/books/export`   |

---

# 📄 CSV Format

### Authors

```csv
First Name,Last Name,Email,Biography,Birth Year
```

### Books

```csv
Title,ISBN,Publication Year,Genre,Price,Available Copies,Description,Author ID
```

---

# 🧪 Testing

Run all tests

```bash
mvn test
```

Generate JaCoCo Report

```bash
mvn clean test jacoco:report
```

Coverage Goals

* Overall → 80%+
* Service Layer → 90%+
* Controller Layer → 85%+
* Repository Layer → 70%+

---

# 🐳 Docker

## Build Image

```bash
docker build -t author-book-management .
```

## Start Containers

```bash
docker-compose up -d
```

## Stop Containers

```bash
docker-compose down
```

---

# 🔄 CI/CD Pipeline

The Jenkins pipeline automates:

* Checkout Source
* Build
* Unit Tests
* JaCoCo Coverage
* Docker Image Build
* Docker Push
* Azure Deployment
* Integration Tests
* Security Scan

Pipeline configuration is available in:

```
Jenkinsfile
```

---

# ☁ Azure Deployment

### Resources

* Azure App Service
* Azure Container Registry
* Azure Database for PostgreSQL
* Resource Group

Deploy using

```bash
./deploy-azure.sh
```

Application URL

```
https://<app-name>.azurewebsites.net
```

Swagger

```
https://<app-name>.azurewebsites.net/swagger-ui.html
```

---

# 🔒 Security Summary

* Spring Security
* HTTP Basic Authentication
* BCrypt Password Encryption
* RBAC Authorization
* Endpoint Protection
* Input Validation

---

# 📈 Future Enhancements

* JWT Authentication
* Redis Caching
* Elasticsearch
* Kubernetes Deployment
* Prometheus & Grafana Monitoring
* Kafka Event Streaming
* API Rate Limiting

---

# 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Open a Pull Request

---

# 📜 License

This project is licensed under the **MIT License**.

---

## 👨‍💻 Author

**Tarun Singh**

If you found this project useful, don't forget to ⭐ the repository.
