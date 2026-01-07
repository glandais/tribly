# Security Issues Checklist

Identified during security audit - January 2025

---

## Critical

- [ ] **Asset Download Security Gap**
  - Location: `backend/src/main/java/com/tribly/api/assets/AbstractDownloadAssetResource.java`
  - Issue: `/api/download/team/*` endpoints are `@PermitAll` but URL has no `{teamSlug}`, so `TriblyQueryContext` cannot establish team context
  - `AssetAccessChecker` returns `false` when `team == null`
  - Action: Verify if asset downloads work; if yes, investigate bypass mechanism; if no, fix endpoint

---

## High Priority

- [ ] **RideAccessChecker - READ allows unconditional access**
  - Location: `backend/src/main/java/com/tribly/service/ride/RideAccessChecker.java:41`
  - Issue: `yield true` for READ after entity lookup, ignoring visibility/status
  - Action: Add visibility check for non-members

- [ ] **TripAccessChecker - READ allows unconditional access**
  - Location: `backend/src/main/java/com/tribly/service/trip/TripAccessChecker.java:40`
  - Issue: `yield true` for READ after entity lookup, ignoring visibility/status
  - Action: Add visibility check for non-members

- [ ] **PostAccessChecker - READ allows unconditional access**
  - Location: `backend/src/main/java/com/tribly/service/post/PostAccessChecker.java`
  - Issue: Same pattern as Ride/Trip
  - Action: Add visibility check for non-members

- [ ] **RouteAccessChecker - READ allows unconditional access**
  - Location: `backend/src/main/java/com/tribly/service/route/RouteAccessChecker.java:41`
  - Issue: Same pattern as above
  - Action: Add visibility check for non-members

- [ ] **TeamPageAccessChecker - Inconsistent permissions**
  - Location: `backend/src/main/java/com/tribly/service/page/TeamPageAccessChecker.java:41`
  - Issue: READ yields `true` for any authenticated user, but LIST/CREATE require ADMIN
  - Action: Align READ permissions with LIST (require ADMIN or check visibility)

---

## Medium Priority

- [ ] **AssetService.deleteAsset uses wrong ActionType**
  - Location: `backend/src/main/java/com/tribly/service/asset/AssetService.java:198`
  - Issue: `@CheckAccess(entityType = EntityType.ASSET, action = ActionType.CREATE)` should be `DELETE`
  - Action: Change to `ActionType.DELETE`

- [ ] **RouteAccessChecker - LIST_ALL_TEAMS always true**
  - Location: `backend/src/main/java/com/tribly/service/route/RouteAccessChecker.java:32`
  - Issue: Returns `true` unconditionally, relying solely on SQL filtering
  - Action: Consider adding user authentication requirement or document as intentional

- [ ] **Missing @CheckAccess audit**
  - Action: Review all service methods for missing `@CheckAccess` annotations
  - Files to check:
    - [ ] `AdService.java`
    - [ ] `AssetService.java`
    - [ ] `CommentService.java`
    - [ ] `PlaceService.java`
    - [ ] `PostService.java`
    - [ ] `RideService.java`
    - [ ] `RideTemplateService.java`
    - [ ] `RouteService.java`
    - [ ] `TeamMembershipService.java`
    - [ ] `TeamPageService.java`
    - [ ] `TeamService.java`
    - [ ] `TripService.java`

---

## Low Priority / Improvements

- [ ] **Add security integration tests**
  - Test unauthorized access to DRAFT entities
  - Test cross-team access attempts
  - Test asset download permissions
  - Test JOIN/LEAVE on non-PUBLISHED entities

- [ ] **Standardize READ behavior across AccessCheckers**
  - Current: Some check visibility, some don't
  - Action: Define consistent pattern and apply to all

- [ ] **Document Ad permission model**
  - Issue: Ads have different permission model (creator can edit own)
  - Action: Add explicit documentation in SECURITY.md

---

## Verified as Intentional (No Action Required)

- [x] Organizer cannot see DRAFT Ads or TeamPages (only ADMIN can)
- [x] Ad creators can see/edit their own ads regardless of status
- [x] Soft-delete filtering is consistent across all queries
- [x] Feature flags (enableTrips, enableAds) properly enforced
- [x] All queries use parameterized values (no SQL injection risk)

---

## Testing Commands

```bash
# Test unauthorized asset access
curl -X GET "http://localhost:8080/api/download/team/assets/{assetId}/file.jpg"
# Expected: 403 Forbidden

# Test DRAFT ride access as non-member
curl -X GET "http://localhost:8080/api/teams/{teamSlug}/rides/{draftRideSlug}"
# Expected: 404 Not Found (not 200 with data)

# Test JOIN on DRAFT ride
curl -X POST "http://localhost:8080/api/teams/{teamSlug}/rides/{draftRideSlug}/groups/{groupId}/join" \
  -H "Authorization: Bearer {token}"
# Expected: 403 Forbidden
```

---

*Last updated: January 2025*
