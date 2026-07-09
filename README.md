# Pedalons - Cycling Team Management Platform

Multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate.

## Tech Stack

- **Backend**: Java 21, Quarkus 3.31.x, PostgreSQL 17 with PostGIS
- **Frontend**: TypeScript 5.9, React 19, Vite 7, Mantine UI 8
- **Mobile**: Flutter, Dart, Riverpod 3
- **Karoo**: Kotlin, Jetpack Compose, ktor-client-karoo
- **Garmin**: Monkey C, Connect IQ SDK
- **API**: OpenAPI 3.1 contract-driven development
- **Testing**: JUnit 5, REST Assured, Vitest

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+
- pnpm 10+
- Docker 24+
- Docker Compose 2.20+

### Clone and Setup

```bash
git clone https://github.com/glandais/pedalons.git
cd pedalons

# Copy environment template
cp .env.example .env
```

### Environment Variables

Required environment variables for production:

| Variable | Description |
|----------|-------------|
| `ENCRYPTION_KEY` | Base64-encoded 32-byte key for token encryption (generate with `openssl rand -base64 32`) |


### Install and configure mkcert

```bash
# Windows (chocolatey)
choco install mkcert

# Windows (scoop)
scoop install mkcert

# macOS
brew install mkcert
```

Install local CA (one time):

```bash
mkcert -install
```

Generate certificates in the frontend folder:

```bash
cd frontend
mkcert localhost 127.0.0.1 192.168.50.20
# Creates localhost+2.pem and localhost+2-key.pem
```


### 2. Start Infrastructure

```bash
# Start dev services (PostgreSQL, MinIO, imgproxy, valhalla, tileserver, mailhog)
cd backend
docker compose up -d

# Wait for PostgreSQL to be ready
docker compose exec postgres pg_isready -U pedalons
```

### 3. Start Backend

```bash
cd backend
./mvnw quarkus:dev
```

Backend available at:
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health

### Create a domain for localhost

Nothing works until at least one domain exists — every request resolves its tenant from the `Host` header. Open a `psql` prompt on the dev database:

```bash
docker exec -it pedalons-dev-postgres psql -U pedalons -d pedalons
```

Then insert a domain matching the host you browse the frontend with:

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    '192.168.50.20',
    'Pedalons',
    'https://192.168.50.20:5173',
    false,
    true,
    false,
    NOW(),
    NOW(),
    0
);
```

See [Running SQL](#running-sql) for the deployed stack, and [Bootstrapping a new deployment](#bootstrapping-a-new-deployment) for what to do next.

### Start Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend available at https://localhost:5173

## Project Structure

```
pedalons/
├── backend/          # Quarkus backend (Java 21)
├── frontend/         # React 19 SPA (Mantine UI)
├── mobile/           # Flutter mobile app (iOS/Android)
├── karoo/            # Hammerhead Karoo extension (Kotlin/Compose)
├── garmin-app/       # Garmin Connect IQ app (Monkey C)
├── contracts/        # OpenAPI specifications
├── services/         # Docker service configs (valhalla, Varnish)
├── scripts/          # Utility scripts
├── data/             # Runtime data (segments, tileserver, keys)
└── docker-compose.yml  # Production deployment
```

## Features

### GPS Device Integration

Users can connect GPS devices from their profile to upload routes directly to their devices.

**Supported devices:**
- Hammerhead Karoo
- Garmin Edge devices (via Garmin Connect)

**Setup:**

- Set `ENCRYPTION_KEY` for secure token storage (required in production)

**Usage:**
1. Navigate to Profile > GPS Devices
2. Click "Connect" next to your device
3. Authorize the application via Device Code Flow (QR code or URL)
4. On any route detail page, use "Send to Device" to upload routes

## Development

### Generate API Client

```bash
cd frontend
pnpm generate-api
```

### Generate UI Route Paths

UI routes (per-locale URL templates, deeplinks, path builders) are declared in `contracts/routes.yaml`. Regenerate `paths.generated.ts` / `paths.generated.dart`, AASA and the AndroidManifest deeplink section with:

```bash
cd frontend
pnpm generate-routes
```

See [APP_LINKS.md](APP_LINKS.md) for the full workflow.

### Run Tests

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && pnpm test
```

### Code Quality

```bash
# Backend linting
cd backend && ./mvnw checkstyle:check

# Frontend linting
cd frontend && pnpm lint
```

## Running SQL

Two different PostgreSQL containers exist depending on how you run Pedalons. Check which one you have with `docker ps` before running anything.

| Setup | Compose file | Container | Credentials |
|-------|--------------|-----------|-------------|
| Local dev | `backend/docker-compose.yml` | `pedalons-dev-postgres` | Hardcoded (`pedalons` / `pedalons`) |
| Deployed stack | `docker-compose.yml` (root) | `pedalons-postgres` | From `.env` (not versioned) |

**Local dev** — the port is published on `127.0.0.1:5432`, so any client works:

```bash
docker exec -it pedalons-dev-postgres psql -U pedalons -d pedalons
```

**Deployed stack** — no port is published, so go through the container. Read the credentials from the container's own environment rather than typing them, which keeps secrets out of your shell history:

```bash
# Interactive session
docker exec -it pedalons-postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'

# One-off statement
docker exec pedalons-postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1 -c "SELECT domain, name, active FROM domains;"'
```

Use `-v ON_ERROR_STOP=1` for anything that writes: without it `psql` reports the error and carries on to the next statement, so a failed migration script looks like it succeeded.

### Writing to entity tables by hand

Tables backing a JPA entity carry two columns Hibernate manages for you, and hand-written SQL has to maintain them:

- `updated_at` — set it to `NOW()` on every UPDATE.
- `version` — optimistic locking. **Increment it on every UPDATE.** If you don't, an entity already loaded in memory can silently overwrite your change the next time it is persisted.

```sql
UPDATE users
   SET platform_role = 'PLATFORM_ADMIN',
       updated_at    = NOW(),
       version       = COALESCE(version, 0) + 1
 WHERE email = 'your-email@example.com'
   AND deleted = false;
```

IDs are TSIDs (`bigint`), not sequences. Generate one inline when inserting:

```sql
(EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT
```

## Multi-Tenancy

Pedalons is multi-tenant: each domain (hostname) has isolated teams and users. The domain is resolved from the `Host` or `X-Forwarded-Host` HTTP header.

### Bootstrapping a new deployment

Domains and platform admins are managed from the admin UI (`/admin`), but a brand-new database can't reach it: you need a domain before you can register a user, and a user before anyone can be an admin. Break the cycle with SQL, once, then use the UI for everything after.

**1. Create the first domain** (see [Running SQL](#running-sql) for how to get a `psql` prompt):

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    'monclub.fr',                    -- domain: hostname used to access the site
    'Mon Club Cycliste',             -- name: displayed in emails, UI, WebAuthn prompts
    'https://monclub.fr',            -- base_url: full URL for email/calendar links
    false,                           -- single_team: if true, domain has only one team
    true,                            -- active
    false,                           -- deleted
    NOW(),
    NOW(),
    0
);
```

| Field | Description |
|-------|-------------|
| `domain` | HTTP hostname (matched against `Host`/`X-Forwarded-Host` header) |
| `name` | App name shown in emails, WebAuthn prompts, and UI |
| `base_url` | Full URL with protocol, used in email links and calendar feeds |

Verify the backend resolves it — a known host returns `200`, an unknown one `404 DOMAIN_NOT_FOUND`:

```bash
curl -s -H 'Host: monclub.fr' http://localhost:8080/api/config
```

**2. Register a user** through the normal signup flow. This sends a verification email, so the mailer must work: in `prod` the backend sends via Brevo, which rejects calls from IPs missing from its [authorised IPs](https://app.brevo.com/security/authorised_ips) allowlist. A rejected call surfaces as a misleading `401 UNKNOWN` on `/api/auth/register`, because `GlobalExceptionMapper` replays the upstream status verbatim.

**3. Grant the platform admin role** via SQL:

```sql
UPDATE users
   SET platform_role = 'PLATFORM_ADMIN',
       updated_at    = NOW(),
       version       = COALESCE(version, 0) + 1
 WHERE email = 'your-email@example.com'
   AND deleted = false;
```

`platform_role` is constrained to `PLATFORM_ADMIN` or `NULL` — it is the only role in the `PlatformRole` enum. No re-login is needed: the role is not carried in the JWT, `AdminInterceptor` reads it from the database on every request.

The role lives on `users`, a table scoped by `domain_id`. You are therefore an admin *of that domain*, despite the "platform admin" name — add a second domain and you'll need a fresh account and a fresh `UPDATE` there.

### Using the admin interface

Once you are a platform admin, the "Admin" link appears in the header menu, providing access to:

- **Dashboard**: Platform statistics
- **Domains**: Manage domains (create, edit, activate/deactivate)
- **Teams**: View all teams, archive/restore
- **Users**: View all users, grant/revoke platform admin role

## Team Governance

Platform admins control the following per-team attributes (configurable via the team admin page):

| Attribute | Description |
|-----------|-------------|
| `visibilityEditable` | If `true`, team admins can change visibility. If `false`, only platform admins can. |
| `joinable` | If `true` and the team is public, any domain user can self-join. |
| `addMemberAllowed` | If `true`, team admins can add members directly. If `false`, only platform admins can. |

When a user creates a team, defaults are:
- `visibility`: `TEAM` (enforced by the backend)
- `visibilityEditable`: `false`
- `joinable`: `false`
- `addMemberAllowed`: `false`

A non-platform-admin user can create at most one team per domain.

## Garmin Connect IQ App Development

The Garmin app (`garmin-app/`) allows users to browse and download routes directly to their Garmin devices. See README inside `garmin-app/` folder
