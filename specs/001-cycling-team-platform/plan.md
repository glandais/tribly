# Implementation Plan: Cycling Team Management Platform

**Branch**: `001-cycling-team-platform` | **Date**: 2025-12-10 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-cycling-team-platform/spec.md`

## Summary

Build a multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate. The platform uses a monorepo architecture with contract-driven development (OpenAPI), React frontend, Quarkus backend (monolith), and PostgreSQL storage. Docker Compose handles deployment. Kafka is available for async event processing if needed.

## Technical Context

**Language/Version**: Java 21 (backend), TypeScript 5.x (frontend)
**Primary Dependencies**: Quarkus 3.x (backend), React 18+ with Vite (frontend), OpenAPI Generator (contract-first)
**Storage**: PostgreSQL 16+ with Hibernate ORM/Panache
**Testing**: JUnit 5 + REST Assured (backend), Vitest + React Testing Library (frontend), Playwright (E2E)
**Target Platform**: Linux containers (Docker), web browsers (Chrome, Firefox, Safari, Edge)
**Project Type**: Monorepo with multiple artifacts (backend, frontend, mobile, garmin)
**Performance Goals**: <200ms API p95, <2s page load, 100+ concurrent teams
**Constraints**: Single backend application (no microservices), 12-Factor compliance, KISS/DRY/YAGNI/SOLID principles
**Scale/Scope**: 100+ teams, 500+ members per team, thousands of routes

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Evidence |
|-----------|--------|----------|
| I. Code Quality | PASS | Linting (ESLint, Checkstyle), formatters (Prettier, google-java-format), SRP via layered architecture |
| II. Testing Standards | PASS | JUnit 5 + Vitest for unit, REST Assured + RTL for integration, Playwright for E2E, TDD approach |
| III. UX Consistency | PASS | React component library, consistent error handling, WCAG 2.1 AA target, loading states |
| IV. Performance | PASS | <200ms API p95, PostgreSQL indexes, lazy loading, performance budgets defined |

**Quality Gates Compliance:**
- Linting: ESLint (frontend), Checkstyle (backend) - zero tolerance
- Type Checking: TypeScript strict mode, Java compile-time checks
- Unit Tests: 80% coverage target for business logic
- Integration Tests: All API endpoints, all DB operations
- Code Review: Required via PR process

## Project Structure

### Documentation (this feature)

```text
specs/001-cycling-team-platform/
├── plan.md              # This file
├── research.md          # Phase 0: Technology decisions and patterns
├── data-model.md        # Phase 1: Entity definitions and relationships
├── quickstart.md        # Phase 1: Developer setup guide
├── contracts/           # Phase 1: OpenAPI specifications
│   ├── openapi.yaml     # Main API specification
│   └── schemas/         # Shared schema definitions
└── tasks.md             # Phase 2: Implementation tasks (created by /speckit.tasks)
```

### Source Code (repository root)

```text
tribly/
├── docker-compose.yml           # Local development environment
├── docker-compose.prod.yml      # Production deployment
├── pom.xml                      # Parent POM for monorepo (Maven)
│
├── contracts/                   # OpenAPI specifications (source of truth)
│   ├── openapi.yaml
│   └── schemas/
│
├── backend/                     # Quarkus monolith application
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/tribly/
│       │   │   ├── TriblyApplication.java
│       │   │   ├── domain/              # Domain entities (JPA)
│       │   │   │   ├── user/
│       │   │   │   ├── team/
│       │   │   │   ├── ride/
│       │   │   │   ├── trip/
│       │   │   │   ├── route/
│       │   │   │   └── common/          # Base entities, soft delete
│       │   │   ├── api/                 # REST resources (generated stubs)
│       │   │   │   ├── auth/
│       │   │   │   ├── teams/
│       │   │   │   ├── rides/
│       │   │   │   ├── trips/
│       │   │   │   ├── routes/
│       │   │   │   └── admin/
│       │   │   ├── service/             # Business logic
│       │   │   │   ├── auth/
│       │   │   │   ├── team/
│       │   │   │   ├── ride/
│       │   │   │   ├── trip/
│       │   │   │   ├── route/
│       │   │   │   ├── notification/
│       │   │   │   └── integration/     # Strava, Garmin, webhooks
│       │   │   ├── infrastructure/      # Cross-cutting concerns
│       │   │   │   ├── security/
│       │   │   │   ├── multitenancy/
│       │   │   │   ├── gpx/             # GPX parsing, FIT conversion
│       │   │   │   └── email/
│       │   │   └── config/
│       │   └── resources/
│       │       ├── application.properties
│       │       └── db/migration/        # Flyway migrations
│       └── test/
│           ├── java/com/tribly/
│           │   ├── unit/
│           │   ├── integration/
│           │   └── contract/
│           └── resources/
│
├── frontend/                    # React SPA
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── src/
│       ├── main.tsx
│       ├── App.tsx
│       ├── api/                 # Generated API client from OpenAPI
│       ├── components/          # Reusable UI components
│       │   ├── common/
│       │   ├── map/             # Leaflet/MapLibre components
│       │   ├── ride/
│       │   ├── trip/
│       │   └── route/
│       ├── pages/               # Route-based pages
│       │   ├── auth/
│       │   ├── team/
│       │   ├── ride/
│       │   ├── trip/
│       │   ├── route/
│       │   ├── catalog/
│       │   └── admin/
│       ├── hooks/               # Custom React hooks
│       ├── store/               # State management (Zustand or TanStack Query)
│       ├── utils/
│       └── styles/
│
├── mobile/                      # React Native (future)
│   └── README.md                # Placeholder
│
├── garmin/                      # Garmin Connect IQ app (future)
│   └── README.md                # Placeholder
│
└── e2e/                         # Playwright E2E tests
    ├── package.json
    └── tests/
        ├── auth.spec.ts
        ├── team.spec.ts
        ├── ride.spec.ts
        └── route.spec.ts
```

**Structure Decision**: Monorepo with Maven parent POM coordinating backend build and npm workspaces for frontend/e2e. Contract-first approach with OpenAPI specs in `/contracts/` generating both server stubs (Quarkus) and client SDK (TypeScript). Single Quarkus application handles all backend functionality (no microservices). Docker Compose orchestrates PostgreSQL, backend, and frontend for local development.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Multiple artifacts in monorepo | Different deployment targets (web, mobile, garmin) | Single artifact cannot serve web + mobile + device apps |
| OpenAPI code generation | Contract-driven development requirement | Manual API sync leads to drift and bugs |
| Kafka (optional) | Async notifications, webhook delivery | Direct calls block request threads for slow integrations |

## Design Principles Applied

Per user requirements, all implementation decisions follow:

1. **KISS**: Single backend app, straightforward layered architecture
2. **DRY**: OpenAPI contract generates both server and client code
3. **YAGNI**: Start with PostgreSQL only, add Kafka only if notification volume requires it
4. **SOLID**: Domain entities, services, and resources have single responsibilities
5. **12-Factor**: Config via environment, stateless processes, port binding, disposability
6. **Clean Code**: Meaningful names, small functions, comments explain "why" not "what"
7. **Fail Fast**: Validation at API boundaries, explicit error handling
8. **Tests**: TDD for business logic, contract tests for API, E2E for critical paths
