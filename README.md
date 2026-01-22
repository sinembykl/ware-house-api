# 📦 WareHouse Management System
Quarkus REST API · Docker · Maven Standard Lifecycle

---

## 1. Overview

The **WareHouse Management System** is a backend RESTful API implemented using the **Quarkus** framework.
It manages core warehouse operations such as **employees, items, orders, and order processing**.

The project is designed for:
- reproducible builds
- clean configuration
- strict Maven lifecycle compliance
- containerized execution with Docker

All endpoints are documented via **OpenAPI / Swagger UI**.

---

## 2. Technology Stack

- Java 17 / 21
- Quarkus 3.10.x
- Maven
- Hibernate ORM / JPA
- MariaDB (runtime)
- H2 (in-memory, tests only)
- Docker

---

## 3. Prerequisites

The following software must be installed:

- Docker Desktop (running)
- Java JDK 17 or 21
- Apache Maven
- Git

Verification:
java -version  
mvn -version  
docker --version

---

## 4. Clone Repository

git clone https://github.com/sinembykl/ware-house-api.git  
cd ware-house-api

Ensure you are in the directory containing `pom.xml`.

---

## 5. Build & Verification (Maven)

### Full verification build (required)

mvn clean verify

This command:
- compiles the project
- runs all unit and integration tests
- uses H2 in-memory database for tests
- packages the application

---

## 6. Build Artifact

After a successful build, the following executable file is created:

target/WareHouse-1.0-SNAPSHOT-runner.jar

This is the Quarkus runner JAR used for Docker execution.

---

## 7. Running the Application with Docker

### Build Docker image

docker build -f docker/Dockerfile.jvm -t warehouse-app:1.0 .

### Run Docker container

docker rm -f final-check  
docker run -p 8080:8080 --name final-check warehouse-app:1.0

Application URL:
http://localhost:8080

---

## 8. API Documentation (Swagger)

Swagger UI is available at:

http://localhost:8080/q/swagger-ui/

It allows:
- endpoint inspection
- schema validation
- interactive API testing

---

## 9. Database Configuration

### Runtime
- MariaDB
- Configured via environment variables:
    - QUARKUS_DATASOURCE_JDBC_URL
    - QUARKUS_DATASOURCE_USERNAME
    - QUARKUS_DATASOURCE_PASSWORD

### Tests
- H2 in-memory database
- Automatically used during:
  mvn test  
  mvn verify

No external database setup is required for tests.

---

## 10. API Usage Examples

Base URL:
http://localhost:8080

### Employee

Create employee:
curl -X POST http://localhost:8080/warehouse/employee

Get employee:
curl http://localhost:8080/warehouse/employee/1

Update employee:
curl -X PUT http://localhost:8080/warehouse/employee/1

Delete employee:
curl -X DELETE http://localhost:8080/warehouse/employee/1

---

### Item

Create item:
curl -X POST http://localhost:8080/warehouse/item

Get item:
curl http://localhost:8080/warehouse/item/ITEM-001

Update item:
curl -X PUT http://localhost:8080/warehouse/item/ITEM-001

List items:
curl http://localhost:8080/warehouse/items

---

### Order

Create order:
curl -X POST http://localhost:8080/warehouse/order

Get order:
curl http://localhost:8080/warehouse/order/1

Add item to order:
curl -X POST http://localhost:8080/warehouse/order/1/items

Assign employee:
curl -X PUT http://localhost:8080/warehouse/order/1/assign/1

---

### Order Item Processing

Pick order item:
curl -X PUT http://localhost:8080/warehouse/orderItem/1/pick

---

### Complete Order

Complete order:
curl -X PUT http://localhost:8080/warehouse/order/1/complete

---

## 11. Common Commands Summary

mvn clean test  
mvn clean verify  
mvn clean package -DskipTests  
docker build -f docker/Dockerfile.jvm -t warehouse-app:1.0 .  
docker run -p 8080:8080 warehouse-app:1.0

---

## 12. Final Notes

- No source code changes are required to run or evaluate the project
- Fully reproducible using Maven and Docker
- Swagger UI provides complete interactive API validation
- Configuration follows Quarkus best practices

---