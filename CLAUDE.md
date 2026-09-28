# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PuppyNote Server is a Spring Boot 3.4.2 REST API for a pet care management platform. It handles user authentication, pet profiles, walking activity, supply tracking, community posts, notifications, and AI-assisted food Q&A.

## Build & Test Commands

This is a Gradle multi-module project. The entire application currently lives in the `apps/legacy` module (see "Module Layout" below), so build/test/run commands must be scoped to it:

```bash
# Build
./gradlew :apps:legacy:build

# Run tests
./gradlew :apps:legacy:test

# Run a single test class
./gradlew :apps:legacy:test --tests "com.puppynoteserver.ClassName"

# Run application (dev profile)
./gradlew :apps:legacy:bootRun --args='--spring.profiles.active=dev'

# Generate REST API documentation
./gradlew :apps:legacy:asciidoctor

# Build Docker image artifact
./gradlew :apps:legacy:bootJar
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

2. **Entity conversion**: Service requests must have a `toEntity()` method to convert to JPA entities. The service layer calls `request.toEntity()` — never calls individual getters on a service request to construct an entity.

3. **User identity**: Never pass `userId` directly from the controller. In the service layer, always use `SecurityService.getCurrentLoginUserInfo()` to get the current user.

4. **Cross-service access**: A service can only call its own repository. To access another domain's data, call that domain's service — e.g., `UserService` must call `OrderService`, not `OrderRepository` directly.

5. **Language**: Comments and log/error messages are written in Korean.

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
- Authorization rules in `SecurityConfig` are evaluated **in declaration order** — `permitAll` matchers must be declared before the broad `/api/**` `authenticated()` rule
- CORS: allowed origins are **not** hardcoded. Add them to `cors.allowed-origin-patterns` in the profile yml (exact origins and port patterns like `http://localhost:[*]` are both supported); bound by `global/security/config/CorsProperties.java`

### Database

- Production: MySQL 8
- Testing: H2 in-memory (PostgreSQL dialect, `ddl-auto: create`)
- ORM: Spring Data JPA + QueryDSL (generated sources go to `src/main/generated`)
- Base entity: `BaseTimeEntity` (audit fields: `createdDate`, `updatedDate`)

### Key Configuration Files

| File | Purpose |
|---|---|
| `apps/legacy/src/main/resources/application-dev.yml` | Dev environment (MySQL, OAuth URLs, JWT from env vars) |
| `apps/legacy/src/main/resources/application-prd.yml` | Production environment |
| `apps/legacy/src/main/resources/application-test.yml` | Test environment (H2) |
| `apps/legacy/build.gradle` | Dependencies, QueryDSL setup, REST Docs, env var injection |
| `Dockerfile` | Java 17 Alpine-based image (packages `apps/legacy/build/libs/*.jar`) |

### Test Base Classes

- `ControllerTestSupport` — base for `@WebMvcTest` controller tests
- `IntegrationTestSupport` — base for full integration tests
- `RestDocsSupport` — base for Spring REST Docs API documentation tests

### Module Layout (MSA migration in progress)

This repo is being restructured toward an MSA layout modeled on `chatplanet-server` (Gradle multi-module, one `apps/*` module per bounded context). This is step one of that migration: domain boundaries have been decided and empty module skeletons created, but **no code has been moved out of the monolith yet** — all existing code still lives untouched in `apps/legacy`.

- `apps/legacy` — the entire current application, unchanged and still the only deployed artifact. All packages listed below still live under `apps/legacy/src/main/java/com/puppynoteserver/`.
- `apps/user`, `apps/pet`, `apps/community`, `apps/foodChat`, `apps/notification`, `apps/petTip`, `apps/weather`, `apps/appVersion` — empty domain module skeletons (`package-info.java` placeholders only) that code will be migrated into over time. Each depends on `contracts:common`.
- `contracts/common` (`com.puppynoteserver.jwt`, `com.puppynoteserver.global.{security,exception,logagent,config}`) — a **copy** of `apps/legacy`'s JWT/Security/exception-handling/BaseTimeEntity code, so every new domain module has the auth baseline every service needs. This was copied rather than moved: `apps/legacy` keeps its own original copies untouched and keeps working exactly as before. The two copies are duplicated on purpose until `apps/legacy`'s domains are actually migrated out, at which point `apps/legacy` should be switched to depend on `contracts:common` and its local copies deleted. Named `contracts/` to mirror `chatplanet-server`'s convention of keeping shared/cross-service modules separate from the deployable `apps/*` services.

Planned domain boundaries for the eventual migration:

- `user` — authentication/signup (`users`), refresh tokens (`refreshToken`), push notifications (`push`), user item categories (`userItemCategories`)
- `pet` — pet profiles (`pets`), family member associations (`familyMembers`), supplies (`petItems`, `petItemPurchase`), walking activity (`walk`), walk alarms (`petWalkAlarms`) — kept as one domain for now due to tight coupling between these sub-features
- `community` — community posts (`post`) and likes (`like`)
- `notification` — `alertSetting`, `alertHistory`, `expo` (push dispatch), and the device-token part of `user.push`
- `petTip` — pet care tips
- `foodChat` — AI food Q&A (Gemini / Ollama)
- `weather` — weather lookup (Open-Meteo)
- `appVersion` — app version metadata
- `contracts/common` — `jwt` (JWT provider/filters, already copied in), `redis` (caches), `storage` (S3 upload), `global` (security config, exceptions, interceptors, utilities — security/exception/logagent/BaseTimeEntity already copied in)
- `home` (BFF/home-screen aggregation) and `batch` (scheduled jobs) have no module yet — deferred until real service extraction begins, since they aggregate/reach across the other domains.
