# Tribly Backend

Quarkus REST API backend for the Tribly cycling team management platform.

## Tech Stack

- **Runtime**: Java 21, Quarkus 3.30.x
- **Database**: PostgreSQL 17 + PostGIS (Hibernate Spatial, Panache, Flyway)
- **Auth**: JWT (SmallRye JWT) + WebAuthn/Passkeys (webauthn4j)
- **Storage**: S3-compatible (MinIO in dev)
- **API**: OpenAPI 3.1 contract-first (SmallRye OpenAPI)
- **GPX**: gpx2web library for track processing, timeshape for timezone lookup
- **Images**: imgproxy for on-the-fly optimization (WebP, AVIF, JXL)
- **Routing**: BRouter for cycling route computation and elevation profiles

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

## Getting Started

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts:

| Service | Port | Purpose |
|---------|------|---------|
| PostgreSQL + PostGIS | 5432 | Database |
| MinIO | 9000 (API), 9001 (console) | S3-compatible object storage |
| imgproxy | 38080 | Image transformation |
| BRouter | 17777 | Cycling route engine |
| Mailhog | 1025 (SMTP), 8025 (web) | Email testing |

### 2. Start the backend

```bash
mvn quarkus:dev
```

The API is available at http://localhost:8080/api with Swagger UI at http://localhost:8080/q/swagger-ui.

Quarkus dev mode provides live reload — code changes are reflected automatically on the next request.

### 3. Create a domain

The platform is multi-tenant by HTTP domain. You need at least one domain entry to use the app:

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    'localhost',
    'Tribly Dev',
    'http://localhost:5173',
    false, true, false, NOW(), NOW(), 0
);
```

## Commands

```bash
mvn quarkus:dev                                        # Dev mode with live reload
mvn test                                               # Run all tests (TestContainers)
mvn test -Dtest=RideResourceTest                       # Single test class
mvn test -Dtest="RideResourceTest#testCreateRide"      # Single test method
mvn spotless:apply                                     # Format (Google Java Format)
mvn checkstyle:check                                   # Lint
mvn package -DskipTests                                # Build + generate OpenAPI contract
```

### Code Coverage

```bash
mvn test
# Reports generated in target/jacoco-report/ (csv, xml, html)

./scripts/coverage-report.sh                              # All classes, sorted by coverage
./scripts/coverage-report.sh 'com.tribly.service'         # Filter by package
./scripts/coverage-report.sh 'com.tribly.repository' missed  # Sort by missed lines
```

## Project Structure

```
src/main/java/com/tribly/
├── api/               # REST resources organized by domain
│   ├── admin/         #   Platform admin endpoints
│   ├── auth/          #   Login, passkeys, magic links
│   ├── device/        #   Device code flow (Karoo, Garmin)
│   └── ...            #   rides, routes, posts, trips, teams, etc.
├── common/            # TsidUtils, custom exceptions, ErrorCode
├── domain/            # JPA entities
│   ├── common/        #   BaseEntity, TeamEntity, Publication
│   ├── ride/          #   Ride, RideGroup, RideParticipation
│   ├── route/         #   Route, GpxTrack, GpxWaypoint
│   ├── trip/          #   Trip, TripStage, TripParticipation
│   └── ...            #   post, comment, team, user, auth, gps, etc.
├── dto/               # Request/response DTOs by domain
├── enums/             # Status, TeamRole, Visibility, AssetType
├── infrastructure/    # Security, caching, brouter client, imgproxy
├── repository/        # Panache repositories with query builder
└── service/           # Business logic

src/main/resources/
├── application.properties    # Configuration (dev/test/prod profiles)
├── db/migration/             # Flyway migrations (V1–V5)
└── keys/                     # JWT key pair (dev only)
```

## API Domains

The API covers these functional areas:

| Domain | Resources | Description |
|--------|-----------|-------------|
| Auth | AuthResource, PasskeyResource | Login (password, magic link, passkeys), token refresh |
| Teams | TeamResource, TeamMemberResource | Team CRUD, member management, roles |
| Rides | RideResource, RideTemplateResource | Scheduled group rides with participation |
| Routes | RouteResource, AllRouteResource | GPX routes with tracks, waypoints, elevation |
| Posts | PostResource | Team blog posts |
| Trips | TripResource | Multi-day trips with stages |
| Comments | Post/Ride/Route/TripCommentResource | Comments on any entity |
| Assets | AssetResource, Download*Resource | File uploads (images, GPX) via S3 |
| Places | PlaceResource | Named locations |
| Calendar | CalendarResource, TeamCalendarResource | iCal feed generation |
| GPS | GpsResource | GPS device integrations (Hammerhead, Garmin) |
| Device | Device*Resource | Device code flow + routes for Karoo/Garmin apps |
| Admin | Admin*Resource | Platform admin (domains, teams, users) |
| Config | ConfigResource | Frontend app configuration |
| Router | RouterResource | BRouter proxy for route computation |

## Configuration

Configuration uses Quarkus profiles (`%dev`, `%test`, `%prod`) in `application.properties`.

**Dev defaults** are provided — no `.env` file needed for local development with `docker compose up`.

**Production** requires environment variables:

| Variable | Purpose |
|----------|---------|
| `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB` | Database |
| `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY` | S3 storage |
| `IMGPROXY_URL` | Image proxy |
| `BROUTER_URL` | Route engine |
| `ENCRYPTION_KEY` | Token encryption (base64-encoded 32-byte key) |
| `TILESERVER_URL` | Tile server for map thumbnails |

## Contract-First Workflow

The backend generates the OpenAPI contract consumed by the frontend:

1. Annotate resources with SmallRye OpenAPI (`@Tag`, `@Operation`, `@APIResponses`)
2. Run `mvn package -DskipTests` — generates `../contracts/openapi.yaml` and `openapi.json`
3. In `../frontend/`, run `pnpm generate-api` — generates TypeScript client from the contract

## Multi-Tenancy

Each HTTP domain has isolated data. `DomainResolver` extracts the domain from `X-Forwarded-Host` or `Host` headers and resolves it to a `Domain` entity. All database queries filter by `domainId`.
