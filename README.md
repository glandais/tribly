# Tribly - Cycling Team Management Platform

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
git clone https://github.com/your-org/tribly.git
cd tribly

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
# Start dev services (PostgreSQL, MinIO, imgproxy, brouter, tileserver, mailhog)
cd backend
docker compose up -d

# Wait for PostgreSQL to be ready
docker compose exec postgres pg_isready -U tribly
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

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    '192.168.50.20',
    'Tribly',
    'https://192.168.50.20:5173',
    false,
    true,
    false,
    NOW(),
    NOW(),
    0
);
```

### Start Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend available at https://localhost:5173

## Project Structure

```
tribly/
├── backend/          # Quarkus backend (Java 21)
├── frontend/         # React 19 SPA (Mantine UI)
├── mobile/           # Flutter mobile app (iOS/Android)
├── karoo/            # Hammerhead Karoo extension (Kotlin/Compose)
├── garmin-app/       # Garmin Connect IQ app (Monkey C)
├── contracts/        # OpenAPI specifications
├── services/         # Docker service configs (BRouter, Varnish)
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

## Multi-Tenancy

Tribly is multi-tenant: each domain (hostname) has isolated teams and users. The domain is resolved from the `Host` or `X-Forwarded-Host` HTTP header.

### Creating a Domain

No admin UI yet. Create domains directly in PostgreSQL:

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

### Setting Up a Platform Admin

To access the platform admin interface (`/admin`), a user must have the `PLATFORM_ADMIN` role:

1. **Create a domain** (see above) and start the application
2. **Register a user** through the normal signup flow
3. **Grant platform admin role** via SQL:

```sql
UPDATE users
SET platform_role = 'PLATFORM_ADMIN'
WHERE email = 'your-email@example.com';
```

After this, the "Admin" link will appear in the header menu, providing access to:
- **Dashboard**: Platform statistics
- **Domains**: Manage domains (create, edit, activate/deactivate)
- **Teams**: View all teams, archive/restore
- **Users**: View all users, grant/revoke platform admin role

## Garmin Connect IQ App Development

The Garmin app (`garmin-app/`) allows users to browse and download routes directly to their Garmin devices. See README inside `garmin-app/` folder
