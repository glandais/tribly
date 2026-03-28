# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

See also the root [../CLAUDE.md](../CLAUDE.md) for full-stack context.

## Commands

```bash
mvn quarkus:dev                    # Dev mode (requires docker-compose up)
mvn test                           # All tests (uses TestContainers for PostgreSQL + MinIO)
mvn test -Dtest=RideResourceTest   # Single test class
mvn test -Dtest="RideResourceTest#testCreateRide"  # Single test method
mvn spotless:apply                 # Format code (Google Java Format)
mvn checkstyle:check               # Lint check
mvn package -DskipTests            # Build + generate contracts/openapi.yaml
```

## Architecture Layers

**Resource → Service → Repository**, each layer has a specific role:

- **Resources** (`api/`): Thin REST controllers. OpenAPI annotations only. No business logic. `@RolesAllowed("user")` for auth.
- **Services** (`service/`): `@ApplicationScoped`. `@Transactional` on write methods. `@CheckAccess` for authorization. Extends `TeamEntityService<Entity, Repository, Dto>` for standard CRUD.
- **Repositories** (`repository/`): Extends `TeamEntityRepository<T, Q>`. Query builder pattern via `PedalonsQuery` with `AndClause`/`OrClause`/`SimpleClause`. All queries filter by `domainId` automatically.

## Entity Hierarchy

```
BaseEntity (TSID id, timestamps, soft delete)
└── TeamEntity (slug, visibility, status, team reference)
    └── Publication (single-table inheritance)
        ├── Ride
        └── Post
```

## Security & Authorization

- **JWT**: `JwtService` generates tokens with claims: email, userId, displayName, domainId, groups. 15 min expiry, 30-day refresh tokens.
- **`@CheckAccess(entityType=..., action=...)`**: Method-level interceptor on services. `CheckAccessInterceptor` extracts parameters (teamSlug, entitySlug) and delegates to `SecurityVerifier`.
- **Request context**: `PedalonsQueryContext` (RequestScoped) holds current user/domain. `DomainResolver` resolves domain from `X-Forwarded-Host` → `Host` header.
- **Other annotations**: `@Public` (no login required), `@Admin`, `@Logged`.

## Query Builder

`PedalonsQuery` automatically applies:
- Domain filtering (multi-tenancy)
- Visibility rules: PUBLIC (anyone), TEAM (team members), PRIVATE (organizers/admins)
- Status filtering: PUBLISHED, DRAFT, CANCELLED
- Role-based access per user's team membership

Repository methods follow: `findByTeamAndSlug(domainId, teamId, userId, slug)`, `findByTeamAndId(...)`.

## Error Handling

Custom exception hierarchy mapped to HTTP responses by `GlobalExceptionMapper`:
- `PedalonsException` (abstract) → `BusinessException`, `ConflictException`, `BadRequestException`
- Each uses `ErrorCode` enum for client-facing error codes
- Constraint violations → field-level validation errors

## Database

- **Flyway migrations**: `src/main/resources/db/migration/V{number}__{description}.sql`
- **Tests use `drop-and-create`** (no Flyway), production uses `validate`
- **PostGIS**: Hibernate Spatial for geographic data
- **Naming**: CamelCaseToUnderscores (JPA → SQL)

## Test Infrastructure

**Base class**: `AbstractResourceTest`
- Injects `TestDataService`, `TestDataCleaner`, `JwtService`
- Pre-configured test users: user1–user5 (standard emails)
- `setUp()` calls `dataCleaner.cleanAll()` first
- `getAccessToken()` generates JWT for test requests
- TestContainers: PostgreSQL + MinIO (via `MinioTestResource`)

**Critical rules**:
- Never `@Transactional` on test methods that use RestAssured (HTTP can't see uncommitted data)
- Always `persistAndFlush()` when ID needed immediately
- Always call `domainResolver.setDomainForTest(domain)` in setUp
- Entity updates in `TestDataService` must be in a `@Transactional` method
- Never run backend tests by yourself — give instructions to the user

## Adding a New Entity (Checklist)

1. Entity in `domain/` extending `TeamEntity` or `BaseEntity`
2. Repository in `repository/` extending `TeamEntityRepository`
3. DTOs in `dto/{domain}/` (request + response)
4. Service in `service/` extending `TeamEntityService`, add `@CheckAccess`
5. Resource in `api/` with OpenAPI annotations (`@Tag`, `@Operation`, `@APIResponses` with `@Schema(implementation=...)`)
6. Flyway migration in `db/migration/`
7. Run `mvn package -DskipTests` → regenerate frontend client with `pnpm generate-api`
