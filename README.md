This project is a backend implementation for a Warehouse Management System built with the Quarkus framework. It implements a hexagonal architecture to manage inventory, tracking items and their stock levels through a RESTful API.

## 📥 Installation and Setup

## 📋 Prerequisites

Before evaluating the project, please ensure the following are ready:

- **Docker Desktop**: Must be **running** in the background.
- **Java 17/21**: Installed and configured in your PATH.
- **Maven**: Installed and configured (to run `mvn` commands).

### 1. Clone the Repository

To get a local copy of this project, open your terminal and run:

Shell

`git clone https://github.com/sinembykl/ware-house-api.git
cd ware-house-api`

### 2. Verify Project Structure

Ensure you are in the root directory where the `pom.xml` is located. The project includes:

- `src/`: Source code and tests.
- `docker/`: Contains the `Dockerfile.jvm`.
- `pom.xml`: Maven configuration for building the **46.4 MB Uber-JAR**.

---

## 🛠️ Build and Verification (Professor's Instructions)

As per the strict evaluation requirements, the system uses the standard Maven lifecycle for verification.

Shell

`mvn clean verify`

- **Execution**: This command compiles the code, runs all unit and integration tests, and packages the application.
- **Artifact**: Upon a **BUILD SUCCESS**, the file `WareHouse-1.0-SNAPSHOT-runner.jar` is generated in the `target/` directory.

---

## 🐳 Running the System with Docker

To evaluate the running system, follow these steps to build and start the containerized environment:

### 1. Build the Docker Image

Shell

`docker run`

`docker build -f docker/Dockerfile.jvm -t warehouse-app:1.0 .`

### 2. Start the Container

Shell

`# Remove any existing instance to avoid naming conflicts
docker rm -f final-check

# Launch the application
docker run -p 8080:8080 --name final-check warehouse-app:1.0`
