# CLAUDE.md

Tribly: multi-tenant cycling team platform (rides, routes with GPX/maps, posts). Contract-first API development.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 21, Quarkus 3.30.x, PostgreSQL 17 + PostGIS, Hibernate/Panache, Flyway |
| Frontend | TypeScript 5, React 19, Vite, Mantine UI, Zustand, React Query |
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
docker compose up -d               # PostgreSQL + Keycloak + imgproxy + brouter
docker compose --profile tools up  # + pgAdmin + Mailhog
```

## Backend Services

| Service | Port | Purpose |
|---------|------|---------|
| imgproxy | 38080 | Image optimization/transformation (WebP, AVIF, JXL) |
| brouter | 17777 | Routing engine and elevation profiles |

## Architecture

```
backend/src/main/java/com/tribly/
├── api/              # REST resources (thin controllers)
├── common/           # Utilities (TsidUtils, exceptions)
├── dto/              # Request/response DTOs by domain
├── domain/           # JPA entities organized by subdomain
│   └── common/       # BaseEntity, TeamEntity, Publication
├── enums/            # Shared enums (Status, TeamRole, Visibility, AssetType)
├── infrastructure/   # Cross-cutting (security, cache, brouter, imgproxy)
├── repository/       # Panache repositories
└── service/          # Business logic

frontend/src/
├── api/              # Generated from Orval (pnpm generate-api)
│   ├── dto/          # Generated DTOs
│   ├── endpoints/    # Generated API functions
│   └── zod/          # Generated Zod schemas
├── components/       # By domain (common/, team/, ride/, route/, post/, trip/, etc.)
├── config/           # paths.ts, routes.config.ts, appConfig.ts
├── hooks/            # React Query wrappers
├── lib/              # axiosInstance.ts, apiUtils.ts
├── locales/{en,fr}/  # i18n (French default)
├── pages/            # Route-level components
├── store/            # Zustand stores
├── types/            # TypeScript type definitions
└── utils/            # Utility functions
```

## Contract-First Workflow

1. Annotate backend resources with SmallRye OpenAPI
2. Run `mvn package -DskipTests` in backend/ → generates `contracts/openapi.yaml` and `contracts/openapi.json`
3. Run `pnpm generate-api` in frontend/ → generates TypeScript client from OpenAPI

## Key Patterns

- **Base entities**: `BaseEntity` (TSID, timestamps, soft delete), `TeamEntity` (adds slug, visibility, status) in `domain/common/`
- **Publications**: `Publication` is an abstract @Entity extending TeamEntity (single-table inheritance). Rides and Posts extend it.
- **Slugs**: Unique per team, auto-generated from title → see `SlugService.generateSlug()`
- **TSID conversion**: `TsidUtils.toString()` / `TsidUtils.toLong()` in `common/` package
- **Assets**: Upload via `assetsApi.uploadAsset()`, type assigned by field placement in `AssetsDto` → see `AssetService.updateAssets()`

## Critical Gotchas

**OpenAPI**:
- Empty schemas = missing `@Schema(implementation = ...)` in `@APIResponse`
- See `RideResource.java` for complete annotation example

**Testing**:
- Never mix `@Transactional` with RestAssured HTTP calls (HTTP can't see uncommitted data)
- Use `persistAndFlush()` when ID needed immediately
- See test examples in `backend/src/test/`
- Do not update entities without saving them with a transaction in TestDataService
- Never run backend tests by yourself, give instructions to user. You're bad at fixing tests from tests outcomes

**Frontend**:
- Uses Mantine UI as component library → check https://mantine.dev/llms.txt for docs
- Frontend config from `/api/config` endpoint, no .env files (backend uses `.env` for OIDC secret only)
- Always use `ConfirmDialog` for confirmations (never `confirm()` or custom modals)
- `MediaEditor` needs `teamSlug` prop for uploads (hidden during team creation)
- Logos: `TeamAvatar` (with initials fallback) vs `EntityLogo` (no fallback)
- Never use SVG for icons, use `@tabler/icons-react`
- Never use hard coded links, use paths.XXX(YYYslug) from `config/paths.ts`
- Templated i18n keys must use type annotations: `t(\`status.\${x satisfies 'DRAFT' | 'PUBLISHED'}\`)` (validated by `pnpm i18n:lint`)

**Keycloak**:
- Users need `firstName`/`lastName` in realm or prompted for profile update
- Realm changes: `docker compose rm -f keycloak` + restart

## Test Users (keycloak/quarkus-realm.json)

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin | admin, user |
| user1-6 | user1-6 | user |

## Dev URLs

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| Frontend | http://localhost:5173 |
| Keycloak | http://localhost:8180 |
| pgAdmin | http://localhost:5050 |
