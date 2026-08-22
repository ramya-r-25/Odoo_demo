# Dayflow HRMS - Java Spring Boot Project

Dayflow is a modular Human Resource Management System (HRMS) built using Java and Spring Boot.

## Security Roles
- **EMPLOYEE** (`ROLE_EMPLOYEE`): Basic employee user access to overview features.
- **HR / ADMIN** (`ROLE_ADMIN`): Full administrative access for configuration and system management.

## Project Structure
- `src/main/java/com/dayflow/`: Core Java application, controllers, models, and security configurations.
- `src/main/resources/`: Application properties and database configurations.

## Running the Application
```bash
mvn spring-boot:run
```