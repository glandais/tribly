# Tribly - Cycling Team Management Platform

Multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate.

## Tech Stack

- **Backend**: Java 21, Quarkus 3.x, PostgreSQL 16 with PostGIS
- **Frontend**: TypeScript 5.x, React 18+, Vite
- **API**: OpenAPI 3.1 contract-driven development
- **Testing**: JUnit 5, REST Assured, Vitest, Playwright

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+
- pnpm 8+
- Docker 24+
- Docker Compose 2.20+

### 1. Clone and Setup

```bash
git clone https://github.com/your-org/tribly.git
cd tribly

# Copy environment templates
cp backend/.env.example backend/.env
cp frontend/.env.example frontend/.env
```

### 2. Start Infrastructure

```bash
# Start PostgreSQL
docker compose up -d postgres

# Wait for PostgreSQL to be ready
docker compose exec postgres pg_isready -U tribly
```

### 3. Start Backend

```bash
cd backend
./mvnw quarkus:dev
```

Backend available at:
- API: http://localhost:8080/v1
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health

### 4. Start Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend available at http://localhost:5173

## Project Structure

```
tribly/
├── backend/          # Quarkus backend
├── frontend/         # React SPA
├── e2e/              # Playwright E2E tests
├── contracts/        # OpenAPI specifications
└── docker-compose.yml
```

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

# E2E
cd e2e && pnpm test
```

### Code Quality

```bash
# Backend linting
cd backend && ./mvnw checkstyle:check

# Frontend linting
cd frontend && pnpm lint
```

## Documentation

See `/specs/001-cycling-team-platform/` for:
- `spec.md` - Feature specification
- `plan.md` - Implementation plan
- `data-model.md` - Database schema
- `quickstart.md` - Developer guide
