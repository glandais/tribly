# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Tribly is a multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate. It uses a contract-first API development approach.

## Tech Stack

- **Backend**: Java 21, Quarkus 3.30.x, PostgreSQL 16 with PostGIS, Hibernate ORM with Panache, Flyway migrations
- **Frontend**: TypeScript 5.x, React 18+, Vite, TailwindCSS, Zustand (state), React Query (data fetching)
- **Auth**: Keycloak OIDC (docker-compose in dev, Dev Services in test, external server in prod)
- **IDs**: TSID (Time-Sorted IDs) via hypersistence-utils - stored as Long internally, exposed as lowercase strings in API
- **API**: OpenAPI 3.1 contract-first with code generation
- **Testing**: JUnit 5 + REST Assured (backend), Vitest + Testing Library (frontend), Playwright (E2E)

## Commands

### Backend (from `backend/` directory)
```bash
mvn quarkus:dev           # Start dev mode with hot reload (requires docker-compose up for Keycloak + Postgres)
mvn test                  # Run unit/integration tests
mvn test -Dtest=ClassName # Run single test class
mvn verify                # Run all tests including integration
mvn checkstyle:check      # Check code style
```

### Frontend (from `frontend/` directory)
```bash
pnpm dev                  # Start Vite dev server
pnpm build                # Type check + production build
pnpm test                 # Run Vitest tests
pnpm lint                 # ESLint check
pnpm lint:fix             # ESLint auto-fix
pnpm generate-api         # Generate API client from OpenAPI spec
```

### E2E Tests (from `e2e/` directory)
```bash
pnpm test                 # Run Playwright tests
pnpm test:ui              # Run with Playwright UI
pnpm test:headed          # Run with visible browser
```

### Infrastructure
```bash
docker compose up -d                  # Start PostgreSQL + Keycloak (required for dev mode)
docker compose up -d postgres         # Start PostgreSQL only
docker compose --profile tools up -d  # Include pgAdmin + Mailhog
docker compose logs -f keycloak       # View Keycloak logs
```

## Architecture

### Backend Structure (`backend/src/main/java/com/tribly/`)

```
api/           # REST controllers and DTOs
  dto/         # Request/response objects
  users/       # User endpoints
  teams/       # Team endpoints
  rides/       # Ride endpoints
domain/        # Core business entities (JPA/Panache)
  user/        # User, UserRepository
  team/        # Team, UserTeam (membership), repositories
  ride/        # Ride, RideGroup entities
  route/       # Route entity with GPX data
  trip/        # Trip, TripStage entities
  place/       # Meeting places
  common/      # Shared base entities
service/       # Business logic services
infrastructure/# Cross-cutting: security, config, integrations
config/        # Application configuration classes
```

**Key patterns:**
- Panache Active Record pattern for repositories (`extends PanacheRepository<Entity>`)
- DTOs separate from domain entities
- Services contain business logic, controllers are thin
- Flyway migrations in `src/main/resources/db/migration/`

### Frontend Structure (`frontend/src/`)

```
api/           # API client (generated from OpenAPI)
components/    # Reusable React components
pages/         # Route-level page components
hooks/         # Custom React hooks
store/         # Zustand state stores
config/        # App configuration
utils/         # Utility functions
```

### Contract-First Development

1. OpenAPI spec lives in `contracts/openapi.yaml`
2. Backend implements the spec manually
3. Frontend generates typed client: `pnpm generate-api`

## Quarkus/Hibernate Testing Patterns

### Test Transaction Isolation

**Never mix `@Transactional` tests with RestAssured HTTP calls:**

```java
// WRONG: HTTP can't see uncommitted data
@Test
@Transactional
void testWrong() {
    Team team = teamService.createTeam(...);  // Not committed
    given().post("/teams/" + team.getSlug() + "/join");  // Can't find team!
}

// RIGHT: Pure HTTP pattern
@Test
void testCorrect() {
    String slug = given().body("...").post("/teams").extract().path("slug");
    given().post("/teams/" + slug + "/join");  // Works!
}

// RIGHT: Pure service pattern
@Test
@Transactional
void testCorrect2() {
    Team team = teamService.createTeam(...);
    assertEquals("Team", team.getName());  // Same transaction
}
```

### Common Gotchas

1. **Entity ID null after persist**: Use `persistAndFlush()` when you need the ID immediately with `GenerationType.IDENTITY`
2. **RestAssured follows redirects**: Add `.redirects().follow(false)` when testing OAuth redirects
3. **JWT null claims**: Check for null before adding claims with `Jwt.claim()`
4. **FK constraint in test cleanup**: Delete child records before parent records in `@BeforeEach`
5. **SecurityIdentityAugmentor context**: Use `@ActivateRequestContext` on methods needing CDI context

### Test Configuration

- Tests use Quarkus Dev Services (auto-starts containers)
- PostGIS image: `postgis/postgis:16-3.4-alpine`
- Test keys in `src/test/resources/keys/`
- Prefix test properties with `%test.`

## TSID (Time-Sorted IDs)

The project uses TSID instead of sequential Long IDs for all entities:

- **Library**: `io.hypersistence:hypersistence-utils-hibernate-63`
- **Internal storage**: Long (BIGINT in PostgreSQL)
- **API exposure**: Lowercase string (e.g., `0h4a8xzk8jv80`)
- **Conversion**: `TsidUtils.toString(Long)` and `TsidUtils.toLong(String)`
- **Entity annotation**: `@Tsid` on ID field in `BaseEntity`

```java
// In entities (BaseEntity)
@Id
@Tsid
private Long id;

// In DTOs - convert to/from String
public static TeamDto from(Team team) {
    return new TeamDto(TsidUtils.toString(team.getId()), ...);
}

// In resources - convert path params
@GET @Path("/{id}")
public Response get(@PathParam("id") String id) {
    Long entityId = TsidUtils.toLong(id);
    // ...
}
```

## URL Slugs

The project uses human-readable slugs in URLs instead of IDs for entities that are accessed via URL (Teams, Rides, Trips, Routes, etc.):

- **Unique constraint**: Slugs are unique per team (not globally), enforced by DB constraint `UNIQUE (team_id, slug)`
- **Format**: Lowercase letters, numbers, and hyphens only (`^[a-z0-9-]+$`)
- **Generation**: Auto-generated from title/name, with timestamp suffix for collision handling

### Backend Implementation

```java
// Entity - add slug field with unique-per-team constraint
@Entity
@Table(name = "rides", uniqueConstraints = {
    @UniqueConstraint(name = "uk_rides_team_slug", columnNames = {"team_id", "slug"})
})
public class Ride extends BaseEntity {
    @NotBlank @Size(max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$")
    @Column(name = "slug", nullable = false)
    private String slug;
}

// Repository - slug lookup methods
public Optional<Ride> findByTeamAndSlug(Long teamId, String slug) {
    return find("team.id = ?1 and slug = ?2 and deleted = false", teamId, slug).firstResultOptional();
}

// Service - slug generation
private String generateSlug(String input) {
    String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
    String slug = NONLATIN.matcher(normalized).replaceAll("");
    return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
}

// Resource - use slug in path params
@GET @Path("/{rideSlug}")
public Response getRide(@PathParam("slug") String teamSlug, @PathParam("rideSlug") String rideSlug) {
    Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
            .orElseThrow(() -> BusinessException.notFound("Ride not found"));
    return Response.ok(RideDetailDto.from(ride)).build();
}
```

### Frontend Implementation

```typescript
// Routes use slugs not IDs
<Route path="teams/:teamSlug/rides/:rideSlug" element={<RideDetailPage />} />

// Hooks accept slug parameters
const { data: ride } = useRide(teamSlug, rideSlug);

// Links use entity.slug
<Link to={`/teams/${teamSlug}/rides/${ride.slug}`}>
```

### URL Structure

```
/teams/{teamSlug}                    # Team detail
/teams/{teamSlug}/rides              # Ride list
/teams/{teamSlug}/rides/{rideSlug}   # Ride detail
/teams/{teamSlug}/trips/{tripSlug}   # Trip detail
/teams/{teamSlug}/routes/{routeSlug} # Route detail
```

## Keycloak Configuration

### Dev Mode vs Test Mode

- **Dev mode** (`mvn quarkus:dev`): Uses docker-compose Keycloak at `localhost:8180`
  - Requires `docker compose up -d` before starting
  - Realm: `quarkus` (imported from `dev-realm.json`)
  - Dev Services disabled: `%dev.quarkus.keycloak.devservices.enabled=false`

- **Test mode** (`mvn test`): Uses Quarkus Keycloak Dev Services
  - Auto-starts isolated Keycloak container
  - Same realm file: `keycloak/dev-realm.json`
  - Dev Services enabled: `%test.quarkus.keycloak.devservices.enabled=true`

### Test Users (dev-realm.json)

| Username | Password | Roles | Name |
|----------|----------|-------|------|
| admin | admin | admin, user | Admin User |
| user1 | user1 | user | User One |
| user2 | user2 | user | User Two |
| user3 | user3 | user | User Three |

### Keycloak Gotchas

1. **Profile update prompt**: Users must have `firstName` and `lastName` set in realm config, otherwise Keycloak prompts for profile update after login
2. **Realm reimport**: To pick up realm changes, stop Keycloak, remove container (`docker compose rm -f keycloak`), and restart
3. **Client configuration**: `tribly-frontend` client must have correct redirect URIs for the frontend ports

## Frontend/Vite Notes

- Use `import.meta.env.DEV` not `process.env.NODE_ENV`
- Use `globalThis` not `global` for browser compatibility
- Keycloak JS adapter for auth: `keycloak-js`
- **No .env files**: All configuration (Keycloak, maps) is fetched from `/api/config` endpoint at startup

## Internationalization (i18n)

The frontend uses `react-i18next` for internationalization with French as default and English as alternative.

### Structure

```
frontend/src/
  i18n/index.ts           # i18n configuration
  locales/
    fr/                   # French (default)
      common.json         # Nav, buttons, status, roles
      auth.json           # Login, home page
      teams.json          # Team pages
      rides.json          # Ride pages
      profile.json        # User profile
      errors.json         # Error messages
    en/                   # English
      (same structure)
```

### Usage Patterns

```tsx
// Basic usage with namespace
import { useTranslation } from 'react-i18next';

function Component() {
  const { t } = useTranslation('teams');
  return <h1>{t('list.title')}</h1>;
}

// Multiple namespaces
const { t } = useTranslation('teams');
const { t: tCommon } = useTranslation('common');

// Interpolation
t('detail.info.memberCount', { count: 5 })  // "5 members"

// Pluralization (use _one/_other suffixes)
// teams.json: "memberCount_one": "{{count}} member", "memberCount_other": "{{count}} members"
t('memberCount', { count: n })

// Locale-aware date formatting - use i18n.language
const { t, i18n } = useTranslation('rides');
date.toLocaleDateString(i18n.language, { month: 'long', day: 'numeric' })

// Trans component for embedded elements (links, etc.) - lets translation control word order
import { Trans } from 'react-i18next';
// auth.json: "termsText": "By signing in, you agree to our <termsLink>Terms</termsLink>"
<Trans
  i18nKey="login.termsText"
  ns="auth"
  components={{
    termsLink: <a href="/terms" className="text-indigo-600" />,
  }}
/>

// Class components - use Translation render prop
import { Translation } from 'react-i18next';
<Translation ns="errors">
  {(t) => <h1>{t('boundary.title')}</h1>}
</Translation>
```

### Language Switching

Language syncs with user profile locale preference in `UserProfilePage.tsx`:
- `useEffect` syncs i18n when `user.locale` changes
- `handleSave` calls `i18n.changeLanguage(locale)` on profile update

## UI Components

### ConfirmDialog ⚠️ IMPORTANT

**ALWAYS use `ConfirmDialog` for user confirmations. NEVER create custom confirmation dialogs or use inline confirmation patterns.**

The project uses a reusable `ConfirmDialog` component for all user confirmations instead of native `confirm()` dialogs or custom modal implementations.

**Location**: `frontend/src/components/common/ConfirmDialog.tsx`

**Props**:
- `isOpen: boolean` - Controls dialog visibility
- `onClose: () => void` - Called when user cancels or clicks backdrop
- `onConfirm: () => void` - Called when user confirms the action
- `title: string` - Dialog title (usually the action name)
- `message: string` - Confirmation message explaining the action
- `confirmText?: string` - Custom confirm button text (defaults to "Confirm")
- `cancelText?: string` - Custom cancel button text (defaults to "Cancel")
- `variant?: 'danger' | 'warning' | 'info'` - Visual style (defaults to 'warning')
- `isLoading?: boolean` - Shows loading state on confirm button

**Variants**:
- `danger` - Red styling for destructive actions (delete, remove)
- `warning` - Yellow styling for significant changes (unpublish, cancel)
- `info` - Blue styling for informational confirmations (uncancel, restore)

**Usage Pattern**:

```tsx
import { useState } from 'react';
import { ConfirmDialog } from '../components/common/ConfirmDialog';

function Component() {
  const [showConfirm, setShowConfirm] = useState(false);
  const deleteMutation = useDeleteItem();

  const handleDelete = () => {
    deleteMutation.mutate(itemId);
    setShowConfirm(false);
  };

  return (
    <>
      <button onClick={() => setShowConfirm(true)}>
        Delete
      </button>

      <ConfirmDialog
        isOpen={showConfirm}
        onClose={() => setShowConfirm(false)}
        onConfirm={handleDelete}
        title={t('actions.delete')}
        message={t('confirmations.delete')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </>
  );
}
```

**Multi-Dialog Pattern** (when component has multiple confirmation types):

```tsx
function Component() {
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showCancelConfirm, setShowCancelConfirm] = useState(false);

  const handleDelete = () => {
    deleteMutation.mutate(id);
    setShowDeleteConfirm(false);
  };

  const handleCancel = () => {
    cancelMutation.mutate({ status: 'CANCELLED' });
    setShowCancelConfirm(false);
  };

  return (
    <>
      <button onClick={() => setShowDeleteConfirm(true)}>Delete</button>
      <button onClick={() => setShowCancelConfirm(true)}>Cancel</button>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('actions.delete')}
        message={t('confirmations.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />

      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={t('actions.cancel')}
        message={t('confirmations.cancel')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
    </>
  );
}
```

**Translation Keys**:
- Confirmation messages go in the appropriate namespace (e.g., `teams.json`, `rides.json`)
- Common button text is in `common.json`: `buttons.cancel`, `buttons.confirm`, `buttons.loading`

**❌ Anti-Patterns (Never Use)**:

1. **Native dialogs**: Don't use `confirm()` or `alert()` - always use `ConfirmDialog` instead
2. **Custom modal implementations**: Don't create one-off confirmation modals with manual JSX
3. **Inline confirmation patterns**: Don't use conditional rendering of confirm buttons (e.g., `showConfirm ? <ConfirmButtons /> : <ActionButton />`)
4. **Inline danger zones**: Don't show confirmation UI inline - always use the modal pattern

**Examples of what NOT to do**:

```tsx
// ❌ WRONG: Custom modal with manual JSX
{showDeleteConfirm && (
  <div className="fixed inset-0 ...">
    <div className="bg-white ...">
      <h3>Are you sure?</h3>
      <button onClick={handleDelete}>Delete</button>
      <button onClick={() => setShowDeleteConfirm(false)}>Cancel</button>
    </div>
  </div>
)}

// ❌ WRONG: Inline confirmation pattern
{showDeleteConfirm ? (
  <div className="bg-red-50">
    <button onClick={handleDelete}>Confirm Delete</button>
    <button onClick={() => setShowDeleteConfirm(false)}>Cancel</button>
  </div>
) : (
  <button onClick={() => setShowDeleteConfirm(true)}>Delete</button>
)}

// ✅ RIGHT: Use ConfirmDialog component
<button onClick={() => setShowDeleteConfirm(true)}>Delete</button>
<ConfirmDialog
  isOpen={showDeleteConfirm}
  onClose={() => setShowDeleteConfirm(false)}
  onConfirm={handleDelete}
  title="Delete Item"
  message="Are you sure you want to delete this item?"
  variant="danger"
/>
```

**Why ConfirmDialog is better**:
- Consistent UI/UX across the application
- Proper z-index handling (appears above maps and other overlays)
- Built-in loading states and accessibility
- Translation-ready with consistent button text
- Mobile-responsive with proper backdrop
- Prevents code duplication

## Development URLs

- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health
- Frontend: http://localhost:5173
- Keycloak (dev): http://localhost:8180
- pgAdmin (tools profile): http://localhost:5050
- Mailhog UI (tools profile): http://localhost:8025

## Specifications

Feature specs, plans, and data models are in `/specs/001-cycling-team-platform/`:
- `spec.md` - Full feature specification with user stories
- `plan.md` - Implementation phases
- `data-model.md` - Database schema design
