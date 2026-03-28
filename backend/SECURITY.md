# Security Architecture - Pedalons Platform

This document describes the security architecture of the Pedalons backend, including authentication, authorization, and data access control mechanisms.

## Table of Contents

1. [Overview](#overview)
2. [Authentication Layer](#authentication-layer)
3. [Authorization Layer](#authorization-layer)
4. [Data Access Control](#data-access-control)
5. [Permission Matrix](#permission-matrix)
6. [Security Patterns](#security-patterns)
7. [Developer Guidelines](#developer-guidelines)

---

## Overview

Pedalons implements a **defense-in-depth** security model with three layers:

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Request                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Layer 1: Authentication (Database Auth + JWT)              │
│  - JWT token validation                                      │
│  - @RolesAllowed / @PermitAll annotations                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Layer 2: Authorization (@CheckAccess + AccessChecker)      │
│  - Entity-level permission checks                           │
│  - Role-based access control (MEMBER/ORGANIZER/ADMIN)       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  Layer 3: Data Filtering (SQL/JPQL)                         │
│  - Visibility-based query filtering                         │
│  - Soft-delete enforcement                                  │
│  - Feature flag checks                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## Authentication Layer

### Database Authentication with JWT

Authentication is handled directly by the backend using database-stored credentials. Supported authentication methods:
- **Password** - Traditional email/password authentication
- **Magic Link** - Passwordless email-based login
- **Passkeys/WebAuthn** - Biometric and hardware key authentication

The backend issues and validates JWT tokens on every request.

**Key Components:**
- `SecurityIdentity` - Quarkus security context injected from JWT
- `JsonWebToken` - Contains user claims (email, roles)
- `PedalonsQueryContext` - Resolves JWT to User entity

### REST Endpoint Annotations

| Annotation | Usage |
|------------|-------|
| `@RolesAllowed("user")` | Requires authenticated user with "user" role |
| `@RolesAllowed("admin")` | Requires authenticated user with "admin" role |
| `@PermitAll` | Public endpoint, no authentication required |

**Example:**
```java
@Path("/api/teams/{teamSlug}/rides")
public class RideResource {

    @POST
    @RolesAllowed("user")  // Must be authenticated
    public Response createRide(...) { }

    @GET
    @Path("/{rideSlug}")
    @PermitAll  // Public access (authorization checked in service)
    public Response getRide(...) { }
}
```

### User Resolution Flow

```
JWT Token → SecurityIdentity → email claim → UserService.lookupUserByEmail() → User entity
```

The `PedalonsQueryContext` (request-scoped) handles this resolution lazily on first access.

---

## Authorization Layer

### Components

#### 1. @CheckAccess Annotation

Declarative annotation for method-level authorization:

```java
@CheckAccess(entityType = EntityType.RIDE, action = ActionType.CREATE)
public RideDto createRide(RideRequest request) { }
```

**Location:** `fr.pedalons.service.security.annotation.CheckAccess`

#### 2. CheckAccessInterceptor

Jakarta EE interceptor that enforces `@CheckAccess` annotations:

```java
@CheckAccess
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class CheckAccessInterceptor {
    @Inject PedalonsQueryContext context;
    @Inject SecurityVerifier securityVerifier;

    @AroundInvoke
    public Object check(InvocationContext ctx) throws Exception {
        CheckAccess ann = ctx.getMethod().getAnnotation(CheckAccess.class);
        if (ann != null) {
            Object[] parameters = ctx.getParameters();
            securityVerifier.verifyAccess(
                ann.entityType(),
                ann.action(),
                parameters == null ? List.of() : List.of(parameters)
            );
        }
        return ctx.proceed();
    }
}
```

**Location:** `fr.pedalons.service.security.interceptor.CheckAccessInterceptor`

#### 3. SecurityVerifier

Routes access checks to entity-specific AccessChecker implementations:

```java
@ApplicationScoped
public class SecurityVerifier {
    Map<EntityType, AccessChecker> accessCheckerMap;

    public void verifyAccess(EntityType entityType, ActionType action, List<Object> params) {
        if (!hasAccess(entityType, action, params)) {
            throw new ForbiddenException();
        }
    }

    protected boolean hasAccess(EntityType entityType, ActionType action, List<Object> params) {
        AccessChecker accessChecker = getAccessChecker(entityType);
        return accessChecker.hasRights(action, params);
    }
}
```

**Location:** `fr.pedalons.service.security.SecurityVerifier`

#### 4. PedalonsQueryContext

Request-scoped bean that resolves security context from JWT and builds `Context` objects:

```java
@RequestScoped
public class PedalonsQueryContext {
    // Resolved from JWT email claim (lazily initialized)
    @Nullable User user;

    // Build Context from method parameters (params[0] = teamSlug)
    public Context getContext(List<Object> params) {
        String teamSlug = getParam(params, 0);
        return getContext(teamSlug);
    }

    // Build Context from team slug
    public Context getContext(@Nullable String teamSlug) {
        Team team = teamSlug != null ? teamService.getTeam(teamSlug) : null;
        return getContext(team);
    }

    // Build full Context with user and teamRole
    public Context getContext(@Nullable Team team) {
        User user = getUserNullable();
        TeamRole teamRole = null;
        if (user != null && team != null) {
            teamRole = userTeamRepository
                .findByUserAndTeam(user.getId(), team.getId())
                .map(UserTeam::getRole)
                .orElse(null);
        }
        return new Context(team, user, teamRole);
    }
}
```

**Location:** `fr.pedalons.service.security.PedalonsQueryContext`

#### 5. Context Record

Immutable value object that packages security context:

```java
public record Context(@Nullable Team team, @Nullable User user, @Nullable TeamRole teamRole) {}
```

**Location:** `fr.pedalons.service.security.Context`

Created by `PedalonsQueryContext.getContext()` and used by AccessCheckers to make authorization decisions.

### AccessChecker Interface

Each entity type has a dedicated AccessChecker implementation:

```java
public interface AccessChecker {
    EntityType getType();

    boolean hasRights(ActionType action, List<Object> params);
}
```

**Location:** `fr.pedalons.service.security.AccessChecker`

AccessCheckers resolve their own security context by injecting `PedalonsQueryContext` and calling `getContext(params)`.

### AccessChecker Implementations

| Entity Type | AccessChecker | Location |
|-------------|---------------|----------|
| TEAM | TeamAccessChecker | `service/team/` |
| USER_TEAM | UserTeamAccessChecker | `service/team/` |
| RIDE | RideAccessChecker | `service/ride/` |
| POST | PostAccessChecker | `service/post/` |
| TRIP | TripAccessChecker | `service/trip/` |
| ROUTE | RouteAccessChecker | `service/route/` |
| AD | AdAccessChecker | `service/ad/` |
| TEAM_PAGE | TeamPageAccessChecker | `service/page/` |
| ASSET | AssetAccessChecker | `service/asset/` |
| COMMENT | CommentAccessChecker | `service/comment/` |
| PLACE | PlaceAccessChecker | `service/place/` |
| RIDE_TEMPLATE | RideTemplateAccessChecker | `service/ridetemplate/` |
| CALENDAR | CalendarAccessChecker | `service/calendar/` |
| PUBLICATION | AllPublicationAccessChecker | `service/common/` |

### Action Types

```java
public enum ActionType {
    LIST,           // List entities (paginated)
    LIST_ALL_TEAMS, // List across all teams
    CREATE,         // Create new entity
    READ,           // Read single entity
    UPDATE,         // Modify entity
    DELETE,         // Soft-delete entity
    JOIN,           // Join participation (rides, trips)
    LEAVE           // Leave participation
}
```

### Team Roles

```java
public enum TeamRole {
    MEMBER,     // Basic team member
    ORGANIZER,  // Can create/edit content
    ADMIN       // Full team management
}
```

Role hierarchy: `ADMIN > ORGANIZER > MEMBER`

```java
// TeamRole methods
teamRole.isAdmin()     // true for ADMIN only
teamRole.isOrganizer() // true for ORGANIZER and ADMIN
```

---

## Data Access Control

### TeamEntityRepository

The `TeamEntityRepository` interface implements SQL-level security filtering for all team entities (Rides, Posts, Trips, Routes, Ads, TeamPages).

**Location:** `fr.pedalons.repositories.common.TeamEntityRepository`

### Visibility Model

```java
public enum Visibility {
    TEAM,       // Team members only
    PUBLIC      // Anyone can see
}
```

Visibility is enforced at both entity and team level. For public access, BOTH the team AND the entity must be `PUBLIC`.

### SQL Filtering Logic

#### Anonymous Users (not authenticated)
```sql
SELECT te FROM [Entity] te WHERE
    te.team.visibility = 'PUBLIC'
    AND te.visibility = 'PUBLIC'
    AND te.status IN ('PUBLISHED', 'CANCELLED')
    AND TYPE(te) <> Ad  -- Ads require membership
    AND te.team.domain.id = :domainId  -- Multi-tenant isolation
    AND te.deleted = false
    AND te.team.deleted = false
```

#### Authenticated Users
The query uses OR clauses to allow access if ANY condition is met:

1. **Public Content:** Public team + public entity + published status
2. **Team Member:** Active membership + published/cancelled status
3. **Ad Creator:** Own ads (regardless of status)
4. **Organizer:** Team content except DRAFT TeamPages and Ads
5. **Admin:** All team content except DRAFT Ads

### Domain Isolation

All queries enforce multi-tenant isolation by filtering on the current domain:
```sql
AND te.team.domain.id = :domainId
```

### Soft Delete Enforcement

All queries automatically filter deleted entities:
```sql
AND te.deleted = false
AND te.team.deleted = false
```

### Feature Flag Checks

Team features are enforced in queries:
```sql
AND (TYPE(te) <> Trip OR te.team.enableTrips = true)
AND (TYPE(te) <> Ad OR te.team.enableAds = true)
```

---

## Permission Matrix

### Entity Permissions by Role

| Entity | Action | Anonymous | Member | Organizer | Admin |
|--------|--------|-----------|--------|-----------|-------|
| **Team** | LIST | Yes | Yes | Yes | Yes |
| | READ | Public only | Yes | Yes | Yes |
| | CREATE | No | Yes | Yes | Yes |
| | UPDATE | No | No | No | Yes |
| | DELETE | No | No | No | Yes |
| **Ride** | READ | Published+Public | Published | All except DRAFT | All |
| | CREATE | No | No | Yes | Yes |
| | UPDATE | No | No | Yes | Yes |
| | DELETE | No | No | Yes | Yes |
| | JOIN | No | Published only | Published only | Published only |
| **Post** | READ | Published+Public | Published | All except DRAFT | All |
| | CREATE | No | No | Yes | Yes |
| | UPDATE/DELETE | No | No | Yes | Yes |
| **Route** | READ | Published+Public | Published | All except DRAFT | All |
| | LIST_ALL_TEAMS | Yes | Yes | Yes | Yes |
| | CREATE | No | No | Yes | Yes |
| | UPDATE/DELETE | No | No | Yes | Yes |
| **Trip** | READ | Published+Public | Published | All except DRAFT | All |
| | CREATE | No | No | Yes | Yes |
| | JOIN | No | Published only | Published only | Published only |
| **Ad** | READ | No | Yes | Yes | Yes |
| | CREATE | No | Yes | Yes | Yes |
| | UPDATE/DELETE | No | Own only | Own only | Yes |
| **TeamPage** | READ | Published+Public | Published+Public | Published | All |
| | LIST/CREATE | No | No | No | Yes |
| | UPDATE/DELETE | No | No | No | Yes |
| **Asset** | CREATE | No | Yes | Yes | Yes |
| | READ | Depends on parent entity visibility |
| | UPDATE/DELETE | No | No | Yes | Yes |
| **Comment** | LIST/CREATE | No | Yes | Yes | Yes |
| | UPDATE/DELETE | No | Own only | Yes | Yes |
| **Calendar** | LIST_ALL_TEAMS | No | Yes (or token) | Yes (or token) | Yes (or token) |
| | LIST | No | Yes (or token) | Yes (or token) | Yes (or token) |
| | READ/CREATE/UPDATE/DELETE | No | Yes | Yes | Yes |

### Status-Based Access

| Status | Anonymous | Member | Organizer | Admin |
|--------|-----------|--------|-----------|-------|
| DRAFT | No | No | No | Yes (except Ads) |
| PUBLISHED | Yes (if public) | Yes | Yes | Yes |
| CANCELLED | Yes (if public) | Yes | Yes | Yes |

---

## Security Patterns

### Pattern 1: Service Method Authorization

Always use `@CheckAccess` on service methods that modify or read protected data:

```java
@ApplicationScoped
public class RideService {

    @CheckAccess(entityType = EntityType.RIDE, action = ActionType.CREATE)
    public RideDto createRide(RideRequest request) {
        // Implementation
    }

    @CheckAccess(entityType = EntityType.RIDE, action = ActionType.READ)
    public RideDto getDto(String rideSlug) {
        // Implementation
    }

    @CheckAccess(entityType = EntityType.RIDE, action = ActionType.UPDATE)
    public RideDto updateRide(String rideSlug, RideRequest request) {
        // Implementation
    }
}
```

### Pattern 2: Nested Entity Access

For entities attached to parent entities (Assets, Comments), verify parent access:

```java
@Override
public boolean hasRights(ActionType action, List<Object> params) {
    Context context = pedalonsContext.getContext(params);
    Team team = context.team();

    // ... get parent entity
    TeamEntity parent = asset.getTeamEntity();

    // Delegate to parent's AccessChecker
    securityVerifier.verifyAccess(
        parent.getEntityType(),
        ActionType.READ,
        List.of(team.getSlug(), parent.getSlug())
    );
    return true;
}
```

### Pattern 3: Owner-Based Access

For user-created content (Ads, Comments), allow owner modifications:

```java
case UPDATE, DELETE -> {
    User user = context.user();
    TeamRole teamRole = context.teamRole();
    boolean isOwner = user != null && entity.getCreatedBy().getId().equals(user.getId());
    boolean isPrivileged = teamRole != null && teamRole.isOrganizer();
    yield isOwner || isPrivileged;
}
```

### Pattern 4: Status-Gated Actions

For participatory actions, verify entity status:

```java
case JOIN, LEAVE -> {
    String slug = pedalonsContext.getParam(params, 1);
    Ride ride = rideService.findBySlug(team, slug);
    // Only allow joining published rides, must be team member
    yield teamRole != null && ride.getStatus() == Status.PUBLISHED;
}
```

---

## Developer Guidelines

### Adding a New Entity Type

1. **Create AccessChecker implementation:**
```java
@ApplicationScoped
public class MyEntityAccessChecker implements AccessChecker {
    @Inject PedalonsQueryContext pedalonsContext;
    @Inject MyEntityService myEntityService;

    @Override
    public EntityType getType() {
        return EntityType.MY_ENTITY;
    }

    @Override
    public boolean hasRights(ActionType action, List<Object> params) {
        Context context = pedalonsContext.getContext(params);
        Team team = context.team();
        User user = context.user();
        TeamRole teamRole = context.teamRole();

        return switch (action) {
            case CREATE -> teamRole != null && teamRole.isOrganizer();
            case READ -> {
                String slug = pedalonsContext.getParam(params, 1);
                myEntityService.findBySlug(team, slug); // throws 404 if not found
                yield true; // SQL filtering handles visibility
            }
            case UPDATE, DELETE -> teamRole != null && teamRole.isOrganizer();
            case LIST, LIST_ALL_TEAMS, JOIN, LEAVE -> false;
        };
    }
}
```

2. **Add EntityType enum value** in `fr.pedalons.enums.EntityType`

3. **Annotate service methods:**
```java
@CheckAccess(entityType = EntityType.MY_ENTITY, action = ActionType.CREATE)
public MyEntityDto create(String teamSlug, MyEntityRequest request) { }

@CheckAccess(entityType = EntityType.MY_ENTITY, action = ActionType.READ)
public MyEntityDto getDto(String teamSlug, String entitySlug) { }
```

Note: Method parameters are passed to the AccessChecker. The first parameter should be `teamSlug` for team-scoped entities.

4. **If extending TeamEntity,** SQL filtering is automatic via `TeamEntityRepository`

### Security Checklist for New Endpoints

- [ ] REST endpoint has `@RolesAllowed` or `@PermitAll`
- [ ] Service methods have `@CheckAccess` annotations
- [ ] AccessChecker handles all relevant ActionTypes
- [ ] For TeamEntity subclasses, repository extends `TeamEntityRepository`
- [ ] Soft-delete is respected (`deleted = false` filtering)
- [ ] Feature flags are checked if applicable

### Common Mistakes to Avoid

1. **Missing @CheckAccess:** Always annotate service methods, not just REST endpoints
2. **Wrong ActionType:** Use DELETE for deletion, not CREATE
3. **Forgetting status checks:** JOIN/LEAVE should verify PUBLISHED status
4. **Ignoring visibility:** READ access should consider entity visibility, not just existence
5. **Direct repository access:** Use service methods to ensure security checks are applied

### Testing Security

```java
@QuarkusTest
class RideSecurityTest {

    @Test
    void nonMemberCannotCreateRide() {
        // Setup: user not member of team
        given()
            .auth().oauth2(userToken)
            .contentType(ContentType.JSON)
            .body(rideRequest)
        .when()
            .post("/api/teams/{teamSlug}/rides", teamSlug)
        .then()
            .statusCode(403);
    }

    @Test
    void memberCannotUpdateOthersRide() {
        // Only organizers can update rides
    }
}
```

---

## Security Contact

For security vulnerabilities or concerns, contact the development team directly.

---

*Document Version: 1.2*
*Last Updated: January 2026*
*Updated: fixed Visibility enum values, added CalendarAccessChecker, added domain isolation to SQL examples*
