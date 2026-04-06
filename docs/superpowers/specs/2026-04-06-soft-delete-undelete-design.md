# Soft Delete: `deleted` attribute + Undelete Feature

**Date:** 2026-04-06

## Summary

Expose the `deleted` flag in API response DTOs and add undelete (restore) endpoints for all soft-deletable entity types. Also fix a bug where route GPX/FIT/image files are deleted during soft-delete instead of being preserved for potential restoration.

## Affected Entity Types

Rides, Posts, Trips, Routes, Team Pages, Ads.

## Architecture

### Backend

#### 1. Bug fix — RouteService file deletion

`RouteService.deleteRoute()` currently calls `gpxProcessingService.deleteRouteFiles(route)` on soft-delete. This is incorrect — files must be preserved so the route can be restored. Remove this call. File cleanup belongs to a future hard-delete step.

#### 2. Add `deleted: boolean` to response DTOs

Add a required `deleted` boolean field to all affected DTOs. This allows the frontend (and API consumers) to know whether an entity is currently soft-deleted. Only admins/organizers receive deleted entities from the API (existing `IncludeDeletedService` behaviour is unchanged).

DTOs to update:
- `RideDto`
- `PostDto`
- `TripDto`
- `RouteDto`, `RouteDetailDto`
- `AdDto`, `AdEditDto`
- `TeamPageDto`, `TeamPageSummaryDto`

#### 3. Undelete service methods

Add an `undeleteXxx(teamSlug, slug)` method to each service:
- `RideService.undeleteRide()`
- `PostService.undeletePost()`
- `TripService.undeleteTrip()`
- `RouteService.undeleteRoute()`
- `AdService.undeleteAd()`
- `TeamPageService.undeletePage()`

Each method:
- Finds the entity via `findBySlug()` (with `includeDeleted=true`, since the entity is deleted)
- Sets `entity.setDeleted(false)`
- Persists the entity
- Annotated with `@CheckAccess(action = ActionType.DELETE)` — same permission as delete (organizers/admins)
- Returns the restored entity DTO

#### 4. Undelete REST endpoints

Add `POST /{slug}/undelete` to each resource, returning 200 with the entity DTO.

| Resource | Endpoint |
|---|---|
| `RideResource` | `POST /api/teams/{teamSlug}/rides/{rideSlug}/undelete` |
| `PostResource` | `POST /api/teams/{teamSlug}/posts/{postSlug}/undelete` |
| `TripResource` | `POST /api/teams/{teamSlug}/trips/{tripSlug}/undelete` |
| `RouteResource` | `POST /api/teams/{teamSlug}/routes/{routeSlug}/undelete` |
| `AdResource` | `POST /api/teams/{teamSlug}/ads/{adSlug}/undelete` |
| `TeamPageResource` | `POST /api/teams/{teamSlug}/pages/{pageSlug}/undelete` |

### Frontend

#### 1. Regenerate API client

Run `pnpm generate-api` in `frontend/` after backend contract is updated.

#### 2. Add Restore action in existing menus

For each entity detail/admin page, add a **Restore** menu item that:
- Only renders when `entity.deleted === true`
- Calls the generated undelete mutation hook
- Shows a success notification on completion
- Invalidates the relevant React Query cache keys
- Does not require a confirmation dialog (non-destructive action)

Pages to update:
- `RideDetailPage` — in the `Menu.Dropdown` alongside delete
- `PostDetailPage` — same pattern
- `TripDetailPage` — same pattern
- `RouteDetailPage` — in the `Button.Group` menu
- `AdDetailPage` — in the actions menu
- `TeamPagesAdminPage` — add a restore `ActionIcon` next to the existing edit/delete icons, visible only when `page.deleted`

#### 3. i18n keys

Add `actions.restore` translation key (EN + FR) and per-entity notification keys, e.g. `rides.notifications.restored`.

## Data Flow

```
Admin views deleted entity (existing flow: IncludeDeletedService returns it)
  → Frontend sees deleted: true in DTO
  → "Restore" action appears in menu
  → POST /{slug}/undelete
  → Service: entity.setDeleted(false), persist
  → Returns updated DTO (deleted: false)
  → Frontend invalidates cache, shows notification
```

## Out of Scope

- Hard delete (permanent removal with file cleanup)
- Undelete for Teams (handled in admin panel, different flow)
- Undelete for Trips' internal stages (managed automatically by trip update logic)
