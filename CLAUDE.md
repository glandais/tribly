# CLAUDE.md

Tribly: multi-tenant cycling team platform (rides, routes with GPX/maps, posts). Contract-first API development.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 21, Quarkus 3.30.x, PostgreSQL 16 + PostGIS, Hibernate/Panache, Flyway |
| Frontend | TypeScript 5, React 18, Vite, TailwindCSS, Zustand, React Query |
| Auth | Keycloak OIDC (docker-compose dev, Dev Services test) |
| IDs | TSID via hypersistence-utils (Long internally, lowercase string in API) |
| API | OpenAPI 3.1 contract-first with code generation |

## Commands

```bash
# Backend (backend/)
mvn quarkus:dev                    # Dev mode (requires docker-compose up)
mvn test                           # Tests

# Frontend (frontend/)
pnpm dev                           # Dev server
pnpm build                         # Type check + build
pnpm generate-api                  # Generate API client from OpenAPI

# Infrastructure
docker compose up -d               # PostgreSQL + Keycloak
docker compose --profile tools up  # + pgAdmin + Mailhog
```

## Architecture

```
backend/src/main/java/com/tribly/
├── api/              # REST resources (thin controllers)
├── dto/              # Request/response DTOs by domain
├── domain/           # JPA entities, repositories, query builders
├── service/          # Business logic
├── enums/            # Shared enums (Status, TeamRole, Visibility, AssetType)
└── infrastructure/   # Exceptions, security, ID utils

frontend/src/
├── api/              # Generated from OpenAPI (pnpm generate-api)
├── components/       # By domain (common/, team/, ride/, route/, post/)
├── hooks/            # React Query wrappers
├── pages/            # Route-level components
├── locales/{en,fr}/  # i18n (French default)
└── lib/              # apiClient.ts, apiUtils.ts
```

## Contract-First Workflow

1. Annotate backend resources with SmallRye OpenAPI
2. Run `mvn package -DskipTests` in backend/ → generates `contracts/openapi.yaml` and `contracts/openapi.json`
3. Run `pnpm generate-api` in frontend/ → generates TypeScript client from OpenAPI

## Key Patterns

- **Base entities**: `BaseEntity` (TSID, timestamps, soft delete), `TeamEntity` (adds slug, visibility, status)
- **Slugs**: Unique per team, auto-generated from title → see `SlugService.generateSlug()`
- **TSID conversion**: `TsidUtils.toString()` / `TsidUtils.toLong()`
- **Assets**: Upload via `assetsApi.uploadAsset()`, type assigned by field placement in `AssetsDto` → see `AssetService.updateAssets()`
- **Publications**: Rides and Posts implement `Publication` interface for unified feeds

## Critical Gotchas

**OpenAPI**:
- NEVER add `examples` to `LocalTime` fields (breaks YAML parser)
- Empty schemas = missing `@Schema(implementation = ...)` in `@APIResponse`
- See `RideResource.java` for complete annotation example

**Testing**:
- Never mix `@Transactional` with RestAssured HTTP calls (HTTP can't see uncommitted data)
- Use `persistAndFlush()` when ID needed immediately
- See test examples in `backend/src/test/`

**Frontend**:
- Config from `/api/config` endpoint, no .env files
- Always use `ConfirmDialog` for confirmations (never `confirm()` or custom modals)
- `MediaEditor` needs `teamSlug` prop for uploads (hidden during team creation)
- Logos: `TeamAvatar` (with initials fallback) vs `EntityLogo` (no fallback)
- Never use SVG for icons, use `@heroicons`

**Keycloak**:
- Users need `firstName`/`lastName` in realm or prompted for profile update
- Realm changes: `docker compose rm -f keycloak` + restart

## Test Users (keycloak/quarkus-realm.json)

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin | admin, user |
| user1/2/3 | user1/2/3 | user |

## Dev URLs

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| Frontend | http://localhost:5173 |
| Keycloak | http://localhost:8180 |
| pgAdmin | http://localhost:5050 |
