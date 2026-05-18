# User Service

`user-service` is a Spring Boot microservice for user signup, login, and JWT token validation.

## What It Does

- Registers users with role assignment
- Hashes passwords using BCrypt
- Authenticates users and issues JWT tokens
- Validates JWT tokens and returns user details
- Persists users/roles in MySQL via Spring Data JPA

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JJWT 0.13.0
- MySQL
- Maven Wrapper (`./mvnw`)

## Project Structure

- `src/main/java/org/furmani/userservice/controllers` - REST endpoints
- `src/main/java/org/furmani/userservice/services` - business logic
- `src/main/java/org/furmani/userservice/models` - JPA entities
- `src/main/java/org/furmani/userservice/repositories` - data access
- `src/main/java/org/furmani/userservice/configs` - security and bean configuration
- `src/main/java/org/furmani/userservice/exceptions` - custom exceptions and global handler
- `src/main/resources/application.properties` - runtime configuration

## Prerequisites

- JDK 17
- MySQL running locally (or update datasource settings)
- `JAVA_HOME` set correctly

## Configuration

Current defaults from `src/main/resources/application.properties`:

```properties
spring.application.name=user-service
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/user-service
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
```

### Recommended overrides (production)

- Do not keep database credentials in source code.
- Externalize secrets using environment variables or a secret manager.

Example environment-variable-based run:

```bash
SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/user_service" \
SPRING_DATASOURCE_USERNAME="root" \
SPRING_DATASOURCE_PASSWORD="root" \
./mvnw spring-boot:run
```

## Run Locally

```bash
./mvnw spring-boot:run
```

Build jar:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

## API Endpoints

Base URL: `http://localhost:8080/user`

### 1) Signup

- Method: `POST`
- Path: `/signup`
- Request body:

```json
{
  "name": "Arif",
  "email": "arif@example.com",
  "password": "StrongPass123",
  "role": "USER"
}
```

- Success: `201 Created`
- Response body (`UserDto`):

```json
{
  "name": "Arif",
  "email": "arif@example.com",
  "roles": [
    {
      "id": 1,
      "creationDate": "2026-05-18T10:20:30.000+00:00",
      "modificationDate": "2026-05-18T10:20:30.000+00:00",
      "value": "USER"
    }
  ]
}
```

Curl:

```bash
curl -X POST "http://localhost:8080/user/signup" \
  -H "Content-Type: application/json" \
  -d '{"name":"Arif","email":"arif@example.com","password":"StrongPass123","role":"USER"}'
```

### 2) Login

- Method: `POST`
- Path: `/login`
- Request body:

```json
{
  "email": "arif@example.com",
  "password": "StrongPass123"
}
```

- Success: `200 OK`
- Response body: JWT token string

Curl:

```bash
curl -X POST "http://localhost:8080/user/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"arif@example.com","password":"StrongPass123"}'
```

### 3) Validate Token

- Method: `GET`
- Path: `/validateToken`
- Query param: `token`
- Success: `200 OK`
- Response body: `UserDto`

Curl:

```bash
curl "http://localhost:8080/user/validateToken?token=<JWT_TOKEN>"
```

## Error Handling

A global exception handler returns consistent JSON error responses:

```json
{
  "timestamp": "2026-05-18T10:35:10.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Email cannot be null or empty"
}
```

Mapped statuses:

- `400 Bad Request` - invalid input
- `401 Unauthorized` - invalid credentials/token
- `404 Not Found` - user not found
- `409 Conflict` - user already exists
- `500 Internal Server Error` - unexpected errors

## Security Notes

- Passwords are hashed with BCrypt.
- CSRF is disabled for this stateless API.
- Session policy is stateless.
- Current auth rules allow all endpoints (`permitAll`) and rely on service-level checks.
- JWT signing key is generated at startup; tokens issued before restart become invalid after restart.

## Known Improvements

- Add request validation annotations (`@Valid`, `@NotBlank`, etc.) on DTOs.
- Replace wildcard imports and align exception contracts in service interface.
- Persist/rotate JWT secret key using secure external configuration.
- Add integration tests for signup/login/token validation and failure cases.
- Configure CORS explicitly instead of disabling it when needed by a frontend.

## Troubleshooting

- If Maven fails with `JAVA_HOME` errors, set it before running:

```bash
export JAVA_HOME="/path/to/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"
```

- If database connection fails, verify MySQL is running and the schema in `spring.datasource.url` exists.
