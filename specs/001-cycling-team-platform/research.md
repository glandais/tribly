# Research: Cycling Team Management Platform

**Branch**: `001-cycling-team-platform` | **Date**: 2025-12-10
**Purpose**: Document technology decisions, patterns, and resolved unknowns

## Technology Decisions

### Backend Framework: Quarkus 3.x

**Decision**: Use Quarkus 3.x with Java 21 for the backend monolith.

**Rationale**:
- Native support for RESTEasy (JAX-RS) with OpenAPI code generation
- Hibernate ORM with Panache for simplified JPA operations
- Fast startup time and low memory footprint (important for containerized deployment)
- Excellent developer experience with live reload
- Built-in support for reactive programming if needed later
- Strong community and Red Hat backing

**Alternatives Considered**:
- Spring Boot: Heavier footprint, slower startup, though more mature ecosystem
- Micronaut: Similar benefits but smaller community
- Vert.x: Too low-level for this application's needs

### Frontend Framework: React 18+ with Vite

**Decision**: Use React 18+ with Vite build tooling and TypeScript.

**Rationale**:
- Industry-standard component model and ecosystem
- Vite provides fast HMR and optimized builds
- TypeScript enables contract-driven development with generated clients
- Rich ecosystem for maps (react-leaflet), forms (react-hook-form), state (TanStack Query)
- Team familiarity (assumed from user requirements)

**Alternatives Considered**:
- Vue 3: Excellent alternative, but React has broader ecosystem for maps/GIS
- SvelteKit: Newer, smaller ecosystem for complex apps
- Angular: More opinionated, heavier learning curve

### Database: PostgreSQL 16+

**Decision**: Use PostgreSQL 16+ with Flyway migrations.

**Rationale**:
- PostGIS extension for geospatial queries (routes, places, distances)
- JSONB support for flexible metadata storage
- Excellent performance for complex queries
- Mature, battle-tested in production
- Strong Hibernate/Panache integration

**Alternatives Considered**:
- MySQL: Weaker geospatial support
- MongoDB: Not ideal for relational data with complex joins

### API Design: OpenAPI 3.1 Contract-First

**Decision**: Use OpenAPI 3.1 specifications as the single source of truth.

**Rationale**:
- Generates Quarkus server stubs (interfaces only, implementation separate)
- Generates TypeScript client SDK for frontend
- Self-documenting API with Swagger UI
- Contract testing validates implementation matches spec
- Prevents API drift between frontend and backend

**Code Generation Strategy**:
- Backend: Generate JAX-RS interfaces, implement in `@ApplicationScoped` classes
- Frontend: Generate TypeScript client with fetch, integrate with TanStack Query
- Validation: OpenAPI schemas drive both server-side and client-side validation

### Multi-Tenancy: Schema-per-Tenant with Domain Routing

**Decision**: Implement multi-tenancy at application level with tenant context.

**Rationale**:
- Each team can have custom domain (per clarification)
- Tenant resolved from domain or subdomain at request time
- Hibernate filters ensure data isolation without separate schemas
- Simpler operational model than schema-per-tenant

**Implementation Pattern**:
```java
@RequestScoped
public class TenantContext {
    private Long teamId;
    // Resolved from domain name or JWT claims
}

@Entity
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "teamId", type = Long.class))
@Filter(name = "tenantFilter", condition = "team_id = :teamId")
public class Ride extends BaseEntity {
    @ManyToOne
    private Team team;
}
```

### Authentication: OAuth2 with JWT

**Decision**: OAuth2 for external providers, JWT for session management.

**Rationale**:
- Strava OAuth2 required for activity sync
- Google/Facebook OAuth2 optional for convenience
- JWT tokens for stateless API authentication
- Quarkus OIDC extension handles token validation

**Flow**:
1. User authenticates via OAuth2 provider
2. Backend validates and creates/updates user record
3. Backend issues JWT with user ID and team memberships
4. Frontend stores JWT, sends in Authorization header
5. Backend validates JWT, sets TenantContext

### GPX Processing: gpx-parser + custom elevation analysis

**Decision**: Use established GPX parsing library with custom analysis.

**Rationale**:
- GPX is XML-based, well-defined schema
- Need to extract: waypoints, tracks, metadata
- Custom logic for: distance calculation, elevation gain, climb detection
- FIT conversion for Garmin devices (future)

**Libraries**:
- `io.jenetics:jpx` - Java GPX parsing library
- Custom elevation analysis using gradient thresholds
- Haversine formula for distance calculations

### Maps: Leaflet with MapLibre GL

**Decision**: Use Leaflet for route display, MapLibre GL for advanced features.

**Rationale**:
- Leaflet: Simple, lightweight, excellent React integration
- MapLibre GL: Free, open-source vector tiles for performance
- OpenStreetMap as base layer (free, community-maintained)
- Custom tile server optional for offline/caching

### State Management: TanStack Query + Zustand

**Decision**: TanStack Query for server state, Zustand for UI state.

**Rationale**:
- TanStack Query: Automatic caching, refetching, optimistic updates
- Works perfectly with generated OpenAPI client
- Zustand: Simple, lightweight for UI-only state (modals, filters)
- No Redux complexity for this application scale

### Messaging: PostgreSQL LISTEN/NOTIFY (start simple)

**Decision**: Start with PostgreSQL pub/sub, add Kafka only if needed.

**Rationale**:
- YAGNI: Most notifications are low-volume
- PostgreSQL LISTEN/NOTIFY handles real-time updates adequately
- Kafka adds operational complexity
- Can migrate to Kafka later if notification volume requires it

**Kafka Migration Triggers**:
- Notification volume > 1000/minute sustained
- Need for event sourcing or replay
- Integration webhooks require guaranteed delivery

## Patterns and Standards

### Layered Architecture

```
┌─────────────────────────────────────────────────┐
│                   API Layer                      │
│  (Generated JAX-RS interfaces + implementations) │
├─────────────────────────────────────────────────┤
│                 Service Layer                    │
│      (Business logic, orchestration)             │
├─────────────────────────────────────────────────┤
│                Domain Layer                      │
│    (Entities, value objects, repositories)       │
├─────────────────────────────────────────────────┤
│              Infrastructure Layer                │
│  (Database, external services, cross-cutting)    │
└─────────────────────────────────────────────────┘
```

### Entity Pattern

All entities extend `BaseEntity` with common fields:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private boolean deleted = false; // Soft delete

    @Version
    private Long version; // Optimistic locking
}
```

### Repository Pattern with Panache

```java
@ApplicationScoped
public class RideRepository implements PanacheRepository<Ride> {

    public List<Ride> findByTeam(Long teamId) {
        return find("team.id = ?1 and deleted = false", teamId).list();
    }

    public void softDelete(Long id) {
        update("deleted = true where id = ?1", id);
    }
}
```

### API Response Pattern

Consistent response structure across all endpoints:

```yaml
# Success response
{ "data": {...}, "meta": { "timestamp": "...", "requestId": "..." } }

# Error response
{ "error": { "code": "VALIDATION_ERROR", "message": "...", "details": [...] } }

# List response with pagination
{ "data": [...], "meta": { "page": 1, "pageSize": 20, "total": 150 } }
```

### Error Handling

```java
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    // Map domain exceptions to HTTP responses
    // ValidationException -> 400
    // NotFoundException -> 404
    // ForbiddenException -> 403
    // Unexpected -> 500 with logging
}
```

### Testing Strategy

| Layer | Tool | Coverage Target |
|-------|------|-----------------|
| Unit (services) | JUnit 5 + Mockito | 80% |
| Integration (API) | REST Assured + TestContainers | 100% of endpoints |
| Contract | OpenAPI validator | 100% compliance |
| E2E | Playwright | Critical user journeys |

## Resolved Unknowns

### From Clarification Sessions

| Question | Resolution | Impact |
|----------|------------|--------|
| Ride group capacity limits | Optional, configurable per group | Add `maxParticipants` nullable field to RideGroup |
| Route ownership/sharing | Strictly owned by one team | No cross-team route references, simplifies permissions |
| Multi-team membership | Users can belong to multiple teams | User-Team many-to-many with role per team |
| Custom domains | Each team can have own domain | Domain-to-team mapping table, tenant resolver from host |

### Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Microservices vs Monolith | Monolith | KISS, single deployment unit, can extract later |
| Sync vs Async notifications | Start sync, add async if needed | YAGNI, PostgreSQL NOTIFY sufficient initially |
| Real-time updates | WebSocket for activity feed | Required for live ride tracking |
| File storage | Local filesystem initially | Can migrate to S3/MinIO later |
| Search | PostgreSQL full-text | Adequate for team/ride/route search |

## Risk Register

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| GPX parsing edge cases | Medium | Low | Comprehensive test suite with real-world files |
| Multi-tenancy data leaks | Low | Critical | Hibernate filters + integration tests per tenant |
| OAuth provider changes | Low | Medium | Abstract provider behind interface |
| Map tile costs at scale | Medium | Medium | Self-hosted tile server if needed |
| Mobile app complexity | Medium | Medium | React Native shares code with web |

## Performance Considerations

### Database Indexes

```sql
-- Required indexes for common queries
CREATE INDEX idx_ride_team_date ON ride(team_id, date) WHERE deleted = false;
CREATE INDEX idx_route_team ON route(team_id) WHERE deleted = false;
CREATE INDEX idx_user_team ON user_team(user_id, team_id);
CREATE INDEX idx_gpx_track_route ON gpx_track USING GIST(geometry);
```

### Caching Strategy

- **API responses**: HTTP Cache-Control headers for static content
- **Database queries**: Hibernate second-level cache for reference data
- **GPX processing**: Cache parsed results, invalidate on file change
- **Map tiles**: Browser caching + optional tile cache server

### Pagination

All list endpoints support cursor-based pagination:

```yaml
GET /api/rides?teamId=123&cursor=abc123&limit=20
```

## Dependencies Summary

### Backend (pom.xml)

```xml
<!-- Core -->
<dependency>quarkus-resteasy-reactive-jackson</dependency>
<dependency>quarkus-hibernate-orm-panache</dependency>
<dependency>quarkus-jdbc-postgresql</dependency>
<dependency>quarkus-flyway</dependency>

<!-- Security -->
<dependency>quarkus-oidc</dependency>
<dependency>quarkus-smallrye-jwt</dependency>

<!-- OpenAPI -->
<dependency>quarkus-smallrye-openapi</dependency>

<!-- GPX -->
<dependency>io.jenetics:jpx</dependency>

<!-- Testing -->
<dependency>quarkus-junit5</dependency>
<dependency>rest-assured</dependency>
<dependency>testcontainers-postgresql</dependency>
```

### Frontend (package.json)

```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-router-dom": "^6.x",
    "@tanstack/react-query": "^5.x",
    "zustand": "^4.x",
    "react-leaflet": "^4.x",
    "leaflet": "^1.9.x",
    "react-hook-form": "^7.x",
    "zod": "^3.x",
    "axios": "^1.x"
  },
  "devDependencies": {
    "typescript": "^5.x",
    "vite": "^5.x",
    "@vitejs/plugin-react": "^4.x",
    "vitest": "^1.x",
    "@testing-library/react": "^14.x",
    "openapi-typescript-codegen": "^0.25.x"
  }
}
```

## Next Steps

1. **Data Model**: Define all entities with relationships in `data-model.md`
2. **API Contracts**: Create OpenAPI specification in `contracts/`
3. **Developer Setup**: Document quickstart in `quickstart.md`
4. **Task Breakdown**: Generate implementation tasks in `tasks.md`
