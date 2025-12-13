# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Tribly is a multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate. It uses a contract-first API development approach.

## Tech Stack

- **Backend**: Java 21, Quarkus 3.30.x, PostgreSQL 16 with PostGIS, Hibernate ORM with Panache, Flyway migrations
- **Frontend**: TypeScript 5.x, React 18+, Vite, TailwindCSS, Zustand (state), React Query (data fetching)
- **Auth**: Keycloak OIDC (Dev Services in dev/test, external server in prod)
- **API**: OpenAPI 3.1 contract-first with code generation
- **Testing**: JUnit 5 + REST Assured (backend), Vitest + Testing Library (frontend), Playwright (E2E)

## Commands

### Backend (from `backend/` directory)
```bash
mvn quarkus:dev           # Start dev mode with hot reload (auto-starts Keycloak + Postgres via Dev Services)
mvn test                  # Run unit/integration tests
mvn test -Dtest=ClassName # Run single test class
mvn verify                # Run all tests including integration
mvn checkstyle:check      # Check code style
```

### Frontend (from `frontend/` directory)
```bash
pnpm dev                  # Start Vite dev server
pnpm build                # Type check + production build
pnpm test                 # Run Vitest tests
pnpm lint                 # ESLint check
pnpm lint:fix             # ESLint auto-fix
pnpm generate-api         # Generate API client from OpenAPI spec
```

### E2E Tests (from `e2e/` directory)
```bash
pnpm test                 # Run Playwright tests
pnpm test:ui              # Run with Playwright UI
pnpm test:headed          # Run with visible browser
```

### Infrastructure
```bash
docker compose up -d postgres  # Start PostgreSQL only
docker compose --profile tools up -d  # Include pgAdmin + Mailhog
```

## Architecture

### Backend Structure (`backend/src/main/java/com/tribly/`)

```
api/           # REST controllers and DTOs
  dto/         # Request/response objects
  users/       # User endpoints
  teams/       # Team endpoints
  rides/       # Ride endpoints
domain/        # Core business entities (JPA/Panache)
  user/        # User, UserRepository
  team/        # Team, UserTeam (membership), repositories
  ride/        # Ride, RideGroup entities
  route/       # Route entity with GPX data
  trip/        # Trip, TripStage entities
  place/       # Meeting places
  common/      # Shared base entities
service/       # Business logic services
infrastructure/# Cross-cutting: security, config, integrations
config/        # Application configuration classes
```

**Key patterns:**
- Panache Active Record pattern for repositories (`extends PanacheRepository<Entity>`)
- DTOs separate from domain entities
- Services contain business logic, controllers are thin
- Flyway migrations in `src/main/resources/db/migration/`

### Frontend Structure (`frontend/src/`)

```
api/           # API client (generated from OpenAPI)
components/    # Reusable React components
pages/         # Route-level page components
hooks/         # Custom React hooks
store/         # Zustand state stores
config/        # App configuration
utils/         # Utility functions
```

### Contract-First Development

1. OpenAPI spec lives in `contracts/openapi.yaml`
2. Backend implements the spec manually
3. Frontend generates typed client: `pnpm generate-api`

## Quarkus/Hibernate Testing Patterns

### Test Transaction Isolation

**Never mix `@Transactional` tests with RestAssured HTTP calls:**

```java
// WRONG: HTTP can't see uncommitted data
@Test
@Transactional
void testWrong() {
    Team team = teamService.createTeam(...);  // Not committed
    given().post("/teams/" + team.getSlug() + "/join");  // Can't find team!
}

// RIGHT: Pure HTTP pattern
@Test
void testCorrect() {
    String slug = given().body("...").post("/teams").extract().path("slug");
    given().post("/teams/" + slug + "/join");  // Works!
}

// RIGHT: Pure service pattern
@Test
@Transactional
void testCorrect2() {
    Team team = teamService.createTeam(...);
    assertEquals("Team", team.getName());  // Same transaction
}
```

### Common Gotchas

1. **Entity ID null after persist**: Use `persistAndFlush()` when you need the ID immediately with `GenerationType.IDENTITY`
2. **RestAssured follows redirects**: Add `.redirects().follow(false)` when testing OAuth redirects
3. **JWT null claims**: Check for null before adding claims with `Jwt.claim()`
4. **FK constraint in test cleanup**: Delete child records before parent records in `@BeforeEach`
5. **SecurityIdentityAugmentor context**: Use `@ActivateRequestContext` on methods needing CDI context

### Test Configuration

- Tests use Quarkus Dev Services (auto-starts containers)
- PostGIS image: `postgis/postgis:16-3.4-alpine`
- Test keys in `src/test/resources/keys/`
- Prefix test properties with `%test.`

## Frontend/Vite Notes

- Use `import.meta.env.DEV` not `process.env.NODE_ENV`
- Use `globalThis` not `global` for browser compatibility
- Keycloak JS adapter for auth: `keycloak-js`

## Development URLs

- Backend API: http://localhost:8080/v1
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health
- Frontend: http://localhost:5173
- Keycloak (dev): http://localhost:8180
- pgAdmin (tools profile): http://localhost:5050
- Mailhog UI (tools profile): http://localhost:8025

## Specifications

Feature specs, plans, and data models are in `/specs/001-cycling-team-platform/`:
- `spec.md` - Full feature specification with user stories
- `plan.md` - Implementation phases
- `data-model.md` - Database schema design
