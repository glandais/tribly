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

## Code Coverage

`mvn test` writes reports to `target/jacoco-report/` (csv, xml, html).

```bash
./scripts/coverage-report.sh                                  # All classes, sorted by coverage
./scripts/coverage-report.sh 'fr.pedalons.service'            # Filter by package
./scripts/coverage-report.sh 'fr.pedalons.repository' missed  # Sort by missed lines
```

## Supporting Services

| Service | Port | Purpose |
|---------|------|---------|
| imgproxy | 38080 | Image optimization/transformation (WebP, AVIF, JXL) |
| valhalla | 8002 | Routing engine (Valhalla turn-by-turn) |

## Source Layout

```
src/main/java/fr/pedalons/
├── api/              # REST resources (thin controllers)
├── common/           # Utilities (TsidUtils, exceptions)
├── dto/              # Request/response DTOs by domain
├── domain/           # JPA entities organized by subdomain
│   └── common/       # BaseEntity, TeamEntity, Publication
├── enums/            # Shared enums (Status, TeamRole, Visibility, AssetType)
├── infrastructure/   # Cross-cutting (security, cache, valhalla, imgproxy)
├── repository/       # Panache repositories
└── service/          # Business logic
```

## Architecture Layers

**Resource → Service → Repository**, each layer has a specific role:

- **Resources** (`api/`): Thin REST controllers. OpenAPI annotations only. No business logic. `@RolesAllowed("user")` for auth.
- **Services** (`service/`): `@ApplicationScoped`. `@Transactional` on write methods. `@CheckAccess` for authorization. Extends `TeamEntityService<Entity, Repository, Dto>` for standard CRUD.
- **Repositories** (`repository/`): Extends `TeamEntityRepository<T, Q>`. Query builder pattern via `PedalonsQuery` with `AndClause`/`OrClause`/`SimpleClause`. All queries filter by `domainId` automatically.

## Entity Hierarchy

```
BaseEntity (TSID id, timestamps, version — no soft delete)
└── TeamEntity (slug, visibility, status, team reference, deleted)
    └── Publication (single-table inheritance)
        ├── Ride
        └── Post
```

`Team`, `User`, and `Domain` each carry their own `deleted` field. Every other
`BaseEntity` subclass (Comment, Place, Asset, UserTeam, RideGroup, RideParticipation,
TripParticipation, RideTemplate, RideTemplateGroup, GpxTrack, GpxWaypoint, CalendarToken)
and the standalone `Passkey` / `GpsServiceConnection` use hard delete.

## Key Patterns

- **Slugs**: Unique per team, auto-generated from title → see `SlugService.generateSlug()`
- **TSID conversion**: `TsidUtils.toString()` / `TsidUtils.toLong()` in `common/`
- **Assets**: Upload via `assetsApi.uploadAsset()`, type assigned by field placement in `AssetsDto` → see `AssetService.updateAssets()`

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

## JPQL/HQL

- Use `IS NOT NULL` / `IS NULL` — never `<> null` or `= null` (always evaluates to UNKNOWN/FALSE due to SQL null semantics)
- Example: after a left join, check `ut IS NOT NULL` to verify the join produced a match

## OpenAPI

- Empty schemas = missing `@Schema(implementation = ...)` in `@APIResponse`
- See `RideResource.java` for a complete annotation example

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
