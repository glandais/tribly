# Pedalons Backend

Quarkus REST API backend for the Pedalons cycling team management platform.

## Tech Stack

- **Runtime**: Java 21, Quarkus 3.30.x
- **Database**: PostgreSQL 17 + PostGIS (Hibernate Spatial, Panache, Flyway)
- **Auth**: JWT (SmallRye JWT) + WebAuthn/Passkeys (webauthn4j)
- **Storage**: S3-compatible (MinIO in dev)
- **API**: OpenAPI 3.1 contract-first (SmallRye OpenAPI)
- **GPX**: gpx2web library for track processing, timeshape for timezone lookup
- **Images**: imgproxy for on-the-fly optimization (WebP, AVIF, JXL)
- **Routing**: valhalla for cycling route computation and elevation profiles

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

## Getting Started

### 1. Start infrastructure

The backing services live in the repository-root stack — `docker-compose.yml` plus the workstation
overlay `docker-compose.local.yml`, which is what publishes the loopback ports below. See
[Development Setup](../README.md#development-setup) for the `.env` a workstation needs.

```bash
cd .. && docker compose up -d
```

`backend`, `frontend` and `traefik` sit behind an `app` profile in the overlay, so this starts the
backing services alone — which is what dev mode wants. They provide:

| Service | Port | Purpose |
|---------|------|---------|
| PostgreSQL + PostGIS | 5432 | Database |
| MinIO | 9000 (API), 9001 (console) | S3-compatible object storage |
| imgproxy | 38080 | Image transformation |
| valhalla | 8002 | Cycling route engine |
| tileserver | 18080 | Server-side raster map rendering |
| Mailhog | 1025 (SMTP), 8025 (web) | Email testing |

### 2. Start the backend

```bash
source ../scripts/dev-env.sh   # postgres + MinIO credentials, from the .env the stack reads
mvn quarkus:dev
```

`dev-env.sh` exports those five values and nothing else: Quarkus reads environment variables above
`application.properties`, so sourcing the whole `.env` would replace the `%dev` bootstrap domain
(`localhost`, the WebAuthn origin of dev passkeys) with the stack's own.

The API is available at http://localhost:8080/api with Swagger UI at http://localhost:8080/q/swagger-ui.

Quarkus dev mode provides live reload — code changes are reflected automatically on the next request.

### 3. Create a domain

The platform is multi-tenant by HTTP domain. You need at least one domain entry to use the app.

Open a `psql` prompt on the database started by `docker compose up -d`:

```bash
docker exec -it pedalons-dev-postgres psql -U pedalons -d pedalons
```

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    'localhost',
    'Pedalons Dev',
    'http://localhost:5173',
    false, true, false, NOW(), NOW(), 0
);
```

`id` is a TSID generated inline, not a sequence. When hand-writing UPDATEs against entity tables, also bump `version` and set `updated_at = NOW()` — Hibernate uses `version` for optimistic locking. The deployed stack uses a different container (`pedalons-postgres`) with credentials from `.env`; see [Running SQL](../README.md#running-sql) in the root README.

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
./scripts/coverage-report.sh 'fr.pedalons.service'         # Filter by package
./scripts/coverage-report.sh 'fr.pedalons.repository' missed  # Sort by missed lines
```

## Project Structure

```
src/main/java/com/pedalons/
├── api/               # REST resources organized by domain
│   ├── admin/         #   Platform admin endpoints
│   ├── auth/          #   Login, passkeys, OTP
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
├── infrastructure/    # Security, caching, valhalla client, imgproxy
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
| Auth | AuthResource, PasskeyResource | Login (password, OTP, passkeys), token refresh |
| Teams | TeamResource, TeamMemberResource | Team CRUD, member management, roles |
| Rides | RideResource, RideTemplateResource | Scheduled group rides with participation |
| Routes | RouteResource, AllRouteResource | GPX routes with tracks, waypoints, elevation |
| Posts | PostResource | Team blog posts |
| Trips | TripResource | Multi-day trips with stages |
| Comments | Post/Ride/Route/TripCommentResource | Comments on any entity |
| Assets | AssetResource, Download*Resource | File uploads (images, GPX) via S3 |
| Places | PlaceResource | Named locations |
| Calendar | CalendarResource, TeamCalendarResource | iCal feed generation |
| GPS | GpsResource | GPS device integrations (Hammerhead, Garmin, Wahoo) |
| Device | Device*Resource | Device code flow + routes for Karoo/Garmin apps |
| Admin | Admin*Resource | Platform admin (domains, teams, users) |
| Config | ConfigResource | Frontend app configuration |
| Router | RouterResource | valhalla proxy for route computation |

## Configuration

Configuration uses Quarkus profiles (`%dev`, `%test`, `%prod`) in `application.properties`.

**Dev defaults** match `.env.example`, so a stack left at those credentials needs no export at all.
A stack with its own `POSTGRES_*` / `MINIO_*` values needs `source ../scripts/dev-env.sh` first —
`%dev` reads them from the environment, defaulting to the `.env.example` ones.

**Production** requires environment variables:

| Variable | Purpose |
|----------|---------|
| `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB` | Database |
| `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY` | S3 storage |
| `IMGPROXY_URL` | Image proxy |
| `VALHALLA_URL` | Route engine |
| `ENCRYPTION_KEY` | Token encryption (base64-encoded 32-byte key) |
| `TILESERVER_URL` | Tile server for map thumbnails |

## Contract-First Workflow

The backend generates the OpenAPI contract consumed by the frontend:

1. Annotate resources with SmallRye OpenAPI (`@Tag`, `@Operation`, `@APIResponses`)
2. Run `mvn package -DskipTests` — generates `../contracts/openapi.yaml` and `openapi.json`
3. In `../frontend/`, run `pnpm generate-api` — generates TypeScript client from the contract

## Multi-Tenancy

Each HTTP domain has isolated data. `DomainResolver` extracts the domain from `X-Forwarded-Host` or `Host` headers and resolves it to a `Domain` entity. All database queries filter by `domainId`.
