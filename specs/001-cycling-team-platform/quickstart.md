# Quickstart: Cycling Team Management Platform

**Branch**: `001-cycling-team-platform` | **Date**: 2025-12-10
**Purpose**: Developer setup guide for local development

## Prerequisites

### Required Software

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21+ | Backend runtime |
| Maven | 3.9+ | Backend build tool |
| Node.js | 20+ | Frontend runtime |
| pnpm | 8+ | Frontend package manager |
| Docker | 24+ | Container runtime |
| Docker Compose | 2.20+ | Container orchestration |
| Git | 2.40+ | Version control |

### Verify Installation

```bash
# Check all prerequisites
java --version        # Should show 21+
mvn --version         # Should show 3.9+
node --version        # Should show 20+
pnpm --version        # Should show 8+
docker --version      # Should show 24+
docker compose version # Should show 2.20+
```

## Quick Start (5 minutes)

### 1. Clone and Setup

```bash
# Clone repository
git clone https://github.com/your-org/tribly.git
cd tribly

# Checkout feature branch
git checkout 001-cycling-team-platform

# Copy environment templates
cp .env.example .env
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

### 2. Start Infrastructure

```bash
# Start PostgreSQL + other services
docker compose up -d postgres

# Wait for PostgreSQL to be ready
docker compose exec postgres pg_isready -U tribly
```

### 3. Start Backend

```bash
# Navigate to backend
cd backend

# Run in development mode (with live reload)
./mvnw quarkus:dev
```

Backend will be available at:
- API: http://localhost:8080/v1
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health

### 4. Start Frontend

```bash
# In a new terminal, navigate to frontend
cd frontend

# Install dependencies
pnpm install

# Start development server
pnpm dev
```

Frontend will be available at:
- App: http://localhost:5173
- Storybook: http://localhost:6006 (optional: `pnpm storybook`)

## Project Structure

```
tribly/
├── docker-compose.yml      # Local infrastructure
├── contracts/              # OpenAPI specifications
│   └── openapi.yaml        # API contract (source of truth)
├── backend/                # Quarkus application
│   ├── pom.xml
│   └── src/
├── frontend/               # React SPA
│   ├── package.json
│   └── src/
└── e2e/                    # Playwright tests
    └── tests/
```

## Environment Variables

### Backend (.env)

```bash

```

### Frontend (.env)

```bash
# API
VITE_API_URL=http://localhost:8080/v1

# OAuth Redirect
VITE_OAUTH_REDIRECT_URI=http://localhost:5173/auth/callback

# Maps
VITE_MAP_TILE_URL=https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png
```

## Development Workflows

### Generate API Client from OpenAPI

```bash
# From repository root
cd frontend

# Generate TypeScript client
pnpm generate-api

# This creates/updates src/api/ with typed API client
```

### Database Migrations

```bash
cd backend

# Create new migration
./mvnw flyway:migrate

# Check migration status
./mvnw flyway:info

# Clean and re-run all migrations (dev only!)
./mvnw flyway:clean flyway:migrate
```

### Running Tests

```bash
# Backend unit + integration tests
cd backend
./mvnw test

# Backend with coverage report
./mvnw test jacoco:report
# Report at: target/site/jacoco/index.html

# Frontend tests
cd frontend
pnpm test

# Frontend with coverage
pnpm test:coverage

# E2E tests (requires running backend + frontend)
cd e2e
pnpm test

# E2E with UI mode
pnpm test:ui
```

### Code Quality

```bash
# Backend formatting
cd backend
./mvnw spotless:apply

# Backend linting
./mvnw checkstyle:check

# Frontend formatting
cd frontend
pnpm format

# Frontend linting
pnpm lint
pnpm lint:fix
```

## Common Tasks

### Add a New API Endpoint

1. **Update OpenAPI spec** (`contracts/openapi.yaml`)
2. **Generate code**:
   ```bash
   cd backend && ./mvnw generate-sources
   cd frontend && pnpm generate-api
   ```
3. **Implement backend** (in `src/main/java/.../api/`)
4. **Use in frontend** (typed client available in `src/api/`)

### Add a New Database Table

1. **Create migration** (`backend/src/main/resources/db/migration/V{N}__{description}.sql`)
2. **Create entity** (`backend/src/main/java/.../domain/`)
3. **Create repository** (extend `PanacheRepository`)
4. **Run migration**: `./mvnw flyway:migrate`

### Debug Backend

```bash
cd backend

# Debug mode (suspends until debugger attaches)
./mvnw quarkus:dev -Dsuspend=y

# Attach debugger on port 5005
```

### Debug Frontend

1. Open browser DevTools (F12)
2. Use React Developer Tools extension
3. Use TanStack Query DevTools (built-in in dev mode)

## Docker Compose Services

### Available Services

```yaml
services:
  postgres:     # PostgreSQL 16 with PostGIS
  pgadmin:      # Database admin UI (optional)
  mailhog:      # Email testing UI (optional)
```

### Start All Services

```bash
# Start everything
docker compose up -d

# Start specific service
docker compose up -d postgres

# View logs
docker compose logs -f postgres

# Stop all
docker compose down

# Stop and remove volumes (fresh start)
docker compose down -v
```

### Service URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| PostgreSQL | localhost:5432 | tribly / tribly_dev_password |
| pgAdmin | http://localhost:5050 | admin@tribly.app / admin |
| MailHog | http://localhost:8025 | - |

## Troubleshooting

### Port Already in Use

```bash
# Find process using port
lsof -i :8080  # Backend
lsof -i :5173  # Frontend
lsof -i :5432  # PostgreSQL

# Kill process
kill -9 <PID>
```

### Database Connection Failed

```bash
# Check PostgreSQL is running
docker compose ps postgres

# Check PostgreSQL logs
docker compose logs postgres

# Restart PostgreSQL
docker compose restart postgres
```

### Quarkus Dev Mode Issues

```bash
# Clear Quarkus cache
rm -rf backend/target

# Rebuild
cd backend && ./mvnw clean compile quarkus:dev
```

### Frontend Build Issues

```bash
# Clear node_modules and cache
cd frontend
rm -rf node_modules .vite
pnpm install
pnpm dev
```

### API Client Out of Sync

```bash
# Regenerate from OpenAPI spec
cd frontend
pnpm generate-api
```

## IDE Setup

### IntelliJ IDEA (Backend)

1. Import as Maven project
2. Install plugins: Quarkus, Lombok
3. Enable annotation processing: Settings → Build → Compiler → Annotation Processors
4. Set Java SDK to 21

### VS Code (Frontend)

Recommended extensions:
- ESLint
- Prettier
- TypeScript Vue Plugin (Volar)
- Tailwind CSS IntelliSense
- REST Client

### VS Code (Full Stack)

```json
// .vscode/settings.json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.preferences.importModuleSpecifier": "relative",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/path/to/java-21",
      "default": true
    }
  ]
}
```

## Next Steps

1. **Run the app**: Follow Quick Start above
2. **Explore the API**: Open Swagger UI at http://localhost:8080/q/swagger-ui
3. **Read the spec**: Review `specs/001-cycling-team-platform/spec.md`
4. **Check the data model**: Review `specs/001-cycling-team-platform/data-model.md`
5. **Start implementing**: See `specs/001-cycling-team-platform/tasks.md`

## Getting Help

- **Documentation**: `specs/001-cycling-team-platform/`
- **API Reference**: http://localhost:8080/q/swagger-ui
- **Issues**: Create a GitHub issue with reproduction steps
