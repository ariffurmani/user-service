# User Service
User authentication microservice for signup, login, and JWT token validation.

## 2. Table of Contents
- [1. Service Name](#user-service)
- [2. Table of Contents](#2-table-of-contents)
- [3. Overview](#3-overview)
- [4. Tech Stack](#4-tech-stack)
- [5. Architecture](#5-architecture)
- [6. Getting Started](#6-getting-started)
- [7. Configuration](#7-configuration)
- [8. API Endpoints](#8-api-endpoints)
- [9. Inter-Service Communication](#9-inter-service-communication)
- [10. Database](#10-database)
- [11. Running Tests](#11-running-tests)
- [12. Docker](#12-docker)
- [13. Environment Variables](#13-environment-variables)

## 3. Overview
This service manages user identity workflows: user registration, credential verification, and JWT issuance/validation. It owns user, role, and token-related domain logic and persistence for this bounded context. It does not own business domains such as product, order, payment, or inventory management.

## 4. Tech Stack
| Layer | Technology |
|---|---|
| Runtime | Java 17 |
| Framework | Spring Boot |
| Web/API | Spring MVC (`spring-boot-starter-webmvc`) |
| Security | Spring Security + JWT (`jjwt`) |
| Persistence | Spring Data JPA |
| Database Driver | MySQL Connector/J |
| Build Tool | Maven Wrapper (`mvnw`) |
| Boilerplate Reduction | Lombok |
| Testing | Spring Boot test starters for JPA and Web MVC |

## 5. Architecture
Within a microservices system, this service acts as the identity/auth provider for user-facing or backend services that need signup, login, or token validation. Upstream dependencies are API clients or other services calling HTTP endpoints exposed by this service. The primary downstream dependency is a MySQL database used for persisting users and roles.

## 6. Getting Started
Prerequisites:
- Java 17
- Maven (or use the included Maven Wrapper)
- MySQL running locally (or reachable by configured datasource URL)

```bash
git clone <repository-url>
cd user-service
./mvnw clean package
./mvnw spring-boot:run
```

## 7. Configuration
The current codebase uses `src/main/resources/application.properties` for runtime configuration. `application.yml` is not present yet; if YAML-based config is preferred, add `application.yml` plus profile-specific files such as `application-dev.yml` and `application-prod.yml`. No explicit `dev`/`prod` Spring profiles are configured in the current repository.

## 8. API Endpoints
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/user/signup` | Register a new user with name, email, password, and role. | No |
| POST | `/user/login` | Authenticate user credentials and return a JWT token. | No |
| GET | `/user/validateToken` | Validate a JWT and return authenticated user details (email and roles). | No |

## 9. Inter-Service Communication
| Communicates With | Protocol | Purpose |
|---|---|---|
| None detected in codebase | N/A | No Feign clients, `RestTemplate`, Kafka producers, or Kafka consumers are implemented. |

## 10. Database
The service uses MySQL via `spring.datasource.*` properties. No schema migration tool (Flyway/Liquibase) or migration directory is configured; schema evolution is currently driven by Hibernate `ddl-auto=update`. Main JPA entities are `User`, `Role`, and `Token` (with shared fields in `BaseEntity` as a mapped superclass).

## 11. Running Tests
```bash
./mvnw test
./mvnw verify
```

Unit and integration tests are both run through Maven's standard test lifecycle in the current setup. JaCoCo is not configured in `pom.xml`.

## 12. Docker
```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=user-service:local
docker run --rm -p 8082:8082 --name user-service user-service:local
```

## 13. Environment Variables
| Variable | Description | Example |
|---|---|---|
| `SPRING_APPLICATION_NAME` | Spring application name. | `user-service` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate DDL mode. | `update` |
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL datasource. | `jdbc:mysql://localhost:3306/user-service` |
| `SPRING_DATASOURCE_USERNAME` | Database username. | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password. | `root` |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | JDBC driver class. | `com.mysql.cj.jdbc.Driver` |
| `SPRING_JPA_SHOW_SQL` | Enable SQL logging. | `true` |
| `SERVER_PORT` | HTTP server port. | `8082` |

