# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Pedalons: multi-tenant cycling team platform (rides, routes with GPX/maps, posts). Contract-first API development.

See [BRANDING.md](BRANDING.md) for logo, icon assets, brand colors, and full theme reference.

## Tech Stack

| Layer | Technologies |
|-------|-------------|
| Backend | Java 21, Quarkus 3.30.x, PostgreSQL 17 + PostGIS, Hibernate/Panache, Flyway |
| Frontend | TypeScript 5 (tsgo for type checking), React 19, Vite 8, Mantine UI, Zustand, React Query |
| Mobile | Flutter, Dart (see `mobile/rules.md` for detailed guidelines) |
| Karoo | Kotlin, Jetpack Compose, karoo-ext SDK, ktor-client-karoo |
| Auth | Database auth with JWT (password, magic link, passkeys/WebAuthn) |
| IDs | TSID via hypersistence-utils (Long internally, lowercase string in API) |
| API | OpenAPI 3.1 contract-first with code generation |

## Commands

```bash
# Backend (backend/)
mvn quarkus:dev                    # Dev mode (requires docker-compose up)
mvn test                           # All tests
mvn test -Dtest=RideResourceTest   # Single test class
mvn test -Dtest="RideResourceTest#testCreateRide"  # Single test method
mvn spotless:apply                 # Format code (Google Java Format, runs automatically on build)
mvn checkstyle:check               # Lint check

# Frontend (frontend/)
pnpm dev                           # Dev server
pnpm build                         # Vite build (no type checking)
pnpm typecheck                     # Type checking via tsgo (typescript-go)
pnpm generate-api                  # Generate API client from OpenAPI
pnpm lint                          # ESLint
pnpm lint:fix                      # ESLint with auto-fix
pnpm format                        # Prettier format
pnpm format:check                  # Check formatting without applying
pnpm test                          # Run tests
pnpm test:coverage                 # Run tests with coverage
pnpm i18n:lint                     # Validate i18n keys
pnpm i18n:status                   # Check translation status

# Mobile (mobile/)
flutter pub get                    # Install dependencies
flutter run                        # Run on connected device/emulator
flutter test                       # Run tests
flutter analyze                    # Static analysis
dart run build_runner build --delete-conflicting-outputs  # Code generation

# Karoo Extension (karoo/)
./gradlew assembleDebug            # Build debug APK
./gradlew installDebug             # Install on connected Karoo device
adb install -r app/build/outputs/apk/debug/app-debug.apk  # ADB install

# Garmin Connect IQ App (garmin-app/)
make build DEVICE=edge1040         # Build for specific device
make build-all                     # Build for all devices
make simulator-docker && make run-docker DEVICE=edge1040  # Run in simulator

# Infrastructure
docker compose up -d               # PostgreSQL + imgproxy + valhalla
docker compose --profile tools up  # + pgAdmin + Mailhog
```

## Code Coverage

```bash
# Run tests with coverage (backend/)
mvn test
# Reports in target/jacoco-report/ (csv, xml, html)

# Readable coverage report
./scripts/coverage-report.sh                              # All classes, sorted by coverage
./scripts/coverage-report.sh 'fr.pedalons.service'         # Filter by package
./scripts/coverage-report.sh 'fr.pedalons.repository' missed  # Sort by missed lines
```

## Backend Services

| Service | Port | Purpose |
|---------|------|---------|
| imgproxy | 38080 | Image optimization/transformation (WebP, AVIF, JXL) |
| valhalla | 8002 | Routing engine (Valhalla turn-by-turn) |

## Architecture

```
backend/src/main/java/fr/pedalons/
├── api/              # REST resources (thin controllers)
├── common/           # Utilities (TsidUtils, exceptions)
├── dto/              # Request/response DTOs by domain
├── domain/           # JPA entities organized by subdomain
│   └── common/       # BaseEntity, TeamEntity, Publication
├── enums/            # Shared enums (Status, TeamRole, Visibility, AssetType)
├── infrastructure/   # Cross-cutting (security, cache, valhalla, imgproxy)
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

mobile/lib/
├── main.dart         # Application entry point
├── presentation/     # Widgets and screens
├── domain/           # Business logic
├── data/             # Models and API clients
└── core/             # Shared utilities and extensions

karoo/app/src/main/kotlin/fr/pedalons/karoo/
├── PedalonsExtension.kt    # KarooExtension service (entry point)
├── MainActivity.kt       # Route browser (Compose UI)
├── auth/
│   ├── AuthActivity.kt   # Device code flow (QR + polling)
│   └── AuthManager.kt    # Token storage (DataStore)
├── api/
│   ├── PedalonsApiClient.kt  # Ktor HTTP client
│   └── Models.kt           # API data classes
└── ui/theme/
    └── Theme.kt          # Dark theme for outdoor visibility

garmin-app/source/
├── PedalonsApp.mc          # Main app entry, Device Code Flow
├── AuthManager.mc        # Token storage (Toybox.Storage)
├── ApiClient.mc          # HTTP client, token refresh
├── LoginView.mc          # Device code display
├── PedalonsView.mc         # Route list (scrollable)
└── RouteDetailView.mc    # Route details + FIT download
```

## Flyway Migrations

Backend database migrations live in `backend/src/main/resources/db/migration/`. Naming convention: `V{number}__{description}.sql` (double underscore separator, sequential numbering).

## Contract-First Workflow

1. Annotate backend resources with SmallRye OpenAPI
2. Run `mvn package -DskipTests` in backend/ → generates `contracts/openapi.yaml` and `contracts/openapi.json`
3. Run `pnpm generate-api` in frontend/ → generates TypeScript client from OpenAPI

## Key Patterns

- **Base entities**: `BaseEntity` (TSID, timestamps), `TeamEntity` (adds slug, visibility, status, soft delete via `deleted` field) in `domain/common/`
- **Publications**: `Publication` is an abstract @Entity extending TeamEntity (single-table inheritance). Rides and Posts extend it.
- **Slugs**: Unique per team, auto-generated from title → see `SlugService.generateSlug()`
- **TSID conversion**: `TsidUtils.toString()` / `TsidUtils.toLong()` in `common/` package
- **Assets**: Upload via `assetsApi.uploadAsset()`, type assigned by field placement in `AssetsDto` → see `AssetService.updateAssets()`

## Multi-Tenancy

- **Domain-based isolation**: Each HTTP domain has its own teams/users. Same email can exist on different domains.
- **Domain resolution**: `DomainResolver` extracts domain from `X-Forwarded-Host` → `Host` header → finds `Domain` entity
- **Domain entity**: Stores `domain` (hostname), `name`, `baseUrl`. WebAuthn/email settings derived from Domain.
- **All queries must filter by domainId**: Use `pedalonsQueryContext.getDomainId()` in query builders
- **Tests**: Call `dataService.getOrCreateDefaultDomain()` + `domainResolver.setDomainForTest(domain)` in setUp()

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

**JPQL/HQL**:
- Use `IS NOT NULL` / `IS NULL` — never use `<> null` or `= null` (always evaluates to UNKNOWN/FALSE due to SQL null semantics)
- Example: After left join, check `ut IS NOT NULL` to verify the join produced a match

**Frontend**:
- Uses Mantine UI as component library → check https://mantine.dev/llms.txt for docs
- Frontend config from `/api/config` endpoint, no .env files
- Always use `ConfirmDialog` for confirmations (never `confirm()` or custom modals)
- `MediaEditor` needs `teamSlug` prop for uploads (hidden during team creation)
- Logos: `TeamAvatar` (with initials fallback) vs `EntityLogo` (no fallback)
- Never use SVG for icons, use `@tabler/icons-react`
- Never use hard coded links, use paths.XXX(YYYslug) from `config/paths.ts`
- Templated i18n keys must use type annotations: `t(\`status.\${x satisfies 'DRAFT' | 'PUBLISHED'}\`)` (validated by `pnpm i18n:lint`)

**Mobile**:
- See `mobile/rules.md` for comprehensive Flutter/Dart guidelines
- Use `go_router` for navigation, `json_serializable` for JSON parsing
- Prefer Flutter's built-in state management (ValueNotifier, ChangeNotifier) over third-party packages
- Run `dart run build_runner build --delete-conflicting-outputs` after modifying serializable models

**Karoo Extension**:
- Follow Hammerhead SDK guidelines: no wildcard imports, Java 11 target
- Uses `ktor-client-karoo` which routes HTTP through Karoo System Service
- Response size limit: 100KB (keep route DTOs lightweight ~200 bytes each)
- Only works on new Karoo (not Karoo 2) due to ktor-client-karoo limitation
- Dark theme required for outdoor visibility on Karoo display
- Device code flow for auth (no keyboard on device)

**Garmin Connect IQ App**:
- Language: Monkey C (Connect IQ SDK 3.3.0+)
- Requires Docker on Ubuntu 24.04+ (webkit2gtk-4.0 incompatibility)
- Device code flow for auth (RFC 8628), minimum 5s poll interval
- Supported: edge530, edge540, edge830, edge840, edge1030, edge1030plus, edge1040, edge1050, edgeexplore2
- See `garmin-app/BUILD.md` for SDK setup

## Dev URLs

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| Frontend | http://localhost:5173 |
| pgAdmin | http://localhost:5050 |
