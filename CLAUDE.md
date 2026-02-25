# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PuppyNote Server is a Spring Boot 3.4.2 REST API for a pet care management platform. It handles user authentication, pet profiles, health records, feeding logs, walking activity, reminders, and supply tracking.

## Build & Test Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.puppynoteserver.ClassName"

# Run application (dev profile)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Generate REST API documentation
./gradlew asciidoctor

# Build Docker image artifact
./gradlew bootJar
```

## Architecture

### Layered Pattern (per domain)

```
Controller (API entry) → Service (business logic) → Repository (data access)
```

Each domain follows this strict structure with its own packages:
- `controller/` + `controller/request/` — API layer, input validation with `@Valid`
- `service/` + `service/request/` + `service/response/` — business logic layer
- `repository/` — data access layer

### Key Architectural Rules

1. **Request conversion**: Controller requests must have a `toServiceRequest()` method to convert to service-layer DTOs before passing to the service.

2. **User identity**: Never pass `userId` directly from the controller. In the service layer, always use `SecurityService.getCurrentLoginUserInfo()` to get the current user.

3. **Cross-service access**: A service can only call its own repository. To access another domain's data, call that domain's service — e.g., `UserService` must call `OrderService`, not `OrderRepository` directly.

4. **Language**: Comments and log/error messages are written in Korean.

### API Response Format

All responses are wrapped in `ApiResponse<T>`:
```json
{
  "statusCode": 200,
  "httpStatus": "OK",
  "message": "OK",
  "data": { ... }
}
```

### Security

- JWT-based authentication via `JwtAuthenticationFilter` and `JwtExceptionHandlerFilter`
- Custom exceptions: `PuppyNoteException`, `NotFoundException`, `UnauthenticatedException`, `JwtTokenException`
- Global exception handler: `global/exception/ApiControllerAdvice.java`
- OAuth2 social login (Kakao, Google) via OpenFeign

### Database

- Production: MySQL 8
- Testing: H2 in-memory (PostgreSQL dialect, `ddl-auto: create`)
- ORM: Spring Data JPA + QueryDSL (generated sources go to `src/main/generated`)
- Base entity: `BaseTimeEntity` (audit fields: `createdDate`, `updatedDate`)
- Push notifications: MongoDB (disabled in test profile)

### Key Configuration Files

| File | Purpose |
|---|---|
| `src/main/resources/application-dev.yml` | Dev environment (MySQL, OAuth URLs, JWT from env vars) |
| `src/main/resources/application-prd.yml` | Production environment |
| `src/main/resources/application-test.yml` | Test environment (H2, MongoDB disabled) |
| `build.gradle` | Dependencies, QueryDSL setup, REST Docs, env var injection |
| `Dockerfile` | Java 17 Alpine-based image |

### Test Base Classes

- `ControllerTestSupport` — base for `@WebMvcTest` controller tests
- `IntegrationTestSupport` — base for full integration tests
- `RestDocsSupport` — base for Spring REST Docs API documentation tests

### Domain Modules

- `user` — authentication, JWT, OAuth, push notifications
- `pet` — pet profiles, family member associations
- `healthRecord` — pet health tracking
- `feeding` — feeding logs and schedules
- `walk` — walking activity
- `reminder` — care reminders
- `supply` — supply management
- `global` — shared security config, exceptions, utilities
