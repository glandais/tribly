# Soft Delete: `deleted` Attribute + Undelete Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose `deleted` in all entity DTOs and add undelete endpoints + frontend restore actions for rides, posts, trips, routes, team pages, and ads.

**Architecture:** Add a `deleted` boolean to each response DTO, add `undeleteXxx()` service methods (with the same `@CheckAccess(action = ActionType.DELETE)` permission as delete), and add `POST /{slug}/undelete` REST endpoints. The frontend regenerates its API client then adds a "Restore" menu item in each entity's existing action menu, visible only when `deleted === true`.

**Tech Stack:** Java 21 / Quarkus 3 (backend), TypeScript / React 19 / Mantine UI / React Query (frontend), Orval code generation.

**Important:** CLAUDE.md says _"Never run backend tests by yourself — give instructions to user."_ All backend test-run steps must be done by the user, not the agent.

---

## Files Modified

**Backend:**
- `backend/src/main/java/fr/pedalons/service/route/RouteService.java` — remove file deletion from soft-delete
- `backend/src/test/java/fr/pedalons/service/route/RouteServiceTest.java` — update delete test after bug fix
- `backend/src/main/java/fr/pedalons/dto/rides/response/RideDto.java`
- `backend/src/main/java/fr/pedalons/dto/posts/response/PostDto.java`
- `backend/src/main/java/fr/pedalons/dto/trips/response/TripDto.java`
- `backend/src/main/java/fr/pedalons/dto/routes/response/RouteDto.java`
- `backend/src/main/java/fr/pedalons/dto/routes/response/RouteDetailDto.java`
- `backend/src/main/java/fr/pedalons/dto/ads/response/AdDto.java`
- `backend/src/main/java/fr/pedalons/dto/ads/response/AdEditDto.java`
- `backend/src/main/java/fr/pedalons/dto/pages/response/TeamPageDto.java`
- `backend/src/main/java/fr/pedalons/dto/pages/response/TeamPageSummaryDto.java`
- `backend/src/main/java/fr/pedalons/service/ride/RideService.java`
- `backend/src/main/java/fr/pedalons/service/post/PostService.java`
- `backend/src/main/java/fr/pedalons/service/trip/TripService.java`
- `backend/src/main/java/fr/pedalons/service/route/RouteService.java`
- `backend/src/main/java/fr/pedalons/service/ad/AdService.java`
- `backend/src/main/java/fr/pedalons/service/page/TeamPageService.java`
- `backend/src/main/java/fr/pedalons/api/rides/RideResource.java`
- `backend/src/main/java/fr/pedalons/api/posts/PostResource.java`
- `backend/src/main/java/fr/pedalons/api/trips/TripResource.java`
- `backend/src/main/java/fr/pedalons/api/routes/RouteResource.java`
- `backend/src/main/java/fr/pedalons/api/ads/AdResource.java`
- `backend/src/main/java/fr/pedalons/api/pages/TeamPageResource.java`
- `backend/src/test/java/fr/pedalons/service/ride/RideServiceTest.java`
- `backend/src/test/java/fr/pedalons/service/post/PostServiceTest.java`
- `backend/src/test/java/fr/pedalons/service/trip/TripServiceTest.java`
- `backend/src/test/java/fr/pedalons/service/route/RouteServiceTest.java`
- `backend/src/test/java/fr/pedalons/service/ad/AdServiceTest.java`
- `backend/src/test/java/fr/pedalons/service/page/TeamPageServiceTest.java`

**Frontend:**
- `frontend/src/locales/en/common.json`
- `frontend/src/locales/fr/common.json`
- `frontend/src/pages/ride/RideDetailPage.tsx`
- `frontend/src/pages/post/PostDetailPage.tsx`
- `frontend/src/pages/trip/TripDetailPage.tsx`
- `frontend/src/pages/route/RouteDetailPage.tsx`
- `frontend/src/pages/ad/AdDetailPage.tsx`
- `frontend/src/pages/team/TeamPagesAdminPage.tsx`

---

## Task 1: Fix RouteService — don't delete files on soft-delete

**Files:**
- Modify: `backend/src/main/java/fr/pedalons/service/route/RouteService.java`
- Modify: `backend/src/test/java/fr/pedalons/service/route/RouteServiceTest.java`

- [ ] **Step 1: Remove file deletion from `deleteRoute()`**

  In `RouteService.java`, find `deleteRoute()` and remove the `gpxProcessingService.deleteRouteFiles(route)` call:

  ```java
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.DELETE)
  @Transactional
  public void deleteRoute(String teamSlug, String slug) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, slug);

    route.setDeleted(true);
    routeRepository.persist(route);
  }
  ```

- [ ] **Step 2: Update the test — stop skipping `@AfterEach` cleanup**

  In `RouteServiceTest.java`, find `deleteRoute_shouldSoftDeleteRoute()` and remove `createdRoute = null` (the route files now still exist after soft-delete, so normal cleanup should run):

  ```java
  @Test
  void deleteRoute_shouldSoftDeleteRoute() throws Exception {
    Path gpxPath = getExampleGpxPath();
    RouteRequest request =
        new RouteRequest(
            "To Delete", MediaDto.builder().build(), null, Visibility.PUBLIC, List.of());

    queryContext.setUserForTest(admin);
    createdRoute = routeService.createRoute(team.getSlug(), request, gpxPath);
    String routeSlug = getCreatedRouteSlug();

    queryContext.setUserForTest(admin);
    routeService.deleteRoute(team.getSlug(), routeSlug);

    queryContext.setUserForTest(member);
    assertThrows(PedalonsException.class, () -> routeService.getDto(team.getSlug(), routeSlug));
  }
  ```

- [ ] **Step 3: Ask user to run the route service test**

  ```
  mvn test -Dtest=RouteServiceTest -pl backend
  ```
  Expected: all tests pass.

- [ ] **Step 4: Commit**

  ```bash
  git add backend/src/main/java/fr/pedalons/service/route/RouteService.java \
          backend/src/test/java/fr/pedalons/service/route/RouteServiceTest.java
  git commit -m "fix: don't delete route files on soft-delete"
  ```

---

## Task 2: Add `deleted` field to all response DTOs

**Files:**
- Modify: `backend/src/main/java/fr/pedalons/dto/rides/response/RideDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/posts/response/PostDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/trips/response/TripDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/routes/response/RouteDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/routes/response/RouteDetailDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/ads/response/AdDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/ads/response/AdEditDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/pages/response/TeamPageDto.java`
- Modify: `backend/src/main/java/fr/pedalons/dto/pages/response/TeamPageSummaryDto.java`

`RideDto`, `PostDto`, `TripDto` use the `@Getter` class style. `RouteDto`, `RouteDetailDto`, `AdDto`, `AdEditDto`, `TeamPageDto`, `TeamPageSummaryDto` use Java `record` style.

- [ ] **Step 1: Add `deleted` to `RideDto`**

  In `RideDto.java`, add field, constructor parameter, and wire in `from()`:

  ```java
  @Schema(description = "Whether the ride is soft-deleted", required = true)
  final boolean deleted;
  ```

  Add `boolean deleted` as the **last** constructor parameter (after `thumbnailDarkUrl`):

  ```java
  public RideDto(
      // ... all existing params ...,
      @Nullable String thumbnailLightUrl,
      @Nullable String thumbnailDarkUrl,
      boolean deleted) {
    // ... existing assignments ...
    this.thumbnailLightUrl = thumbnailLightUrl;
    this.thumbnailDarkUrl = thumbnailDarkUrl;
    this.deleted = deleted;
  }
  ```

  In `from()`, add `ride.isDeleted()` as the last argument to `new RideDto(...)`:

  ```java
  return new RideDto(
      // ... all existing args ...,
      thumbnailLightUrl,
      thumbnailDarkUrl,
      ride.isDeleted());
  ```

- [ ] **Step 2: Add `deleted` to `PostDto`**

  In `PostDto.java`, add field after `createdAt`:

  ```java
  @Schema(description = "Whether the post is soft-deleted", required = true)
  final boolean deleted;
  ```

  Add `boolean deleted` as the last constructor parameter (after `createdAt`):

  ```java
  public PostDto(
      // ... all existing params ...,
      @Nullable Instant createdAt,
      boolean deleted) {
    // ... existing assignments ...
    this.createdAt = createdAt;
    this.deleted = deleted;
  }
  ```

  In `from()`, add `post.isDeleted()` as the last argument to `new PostDto(...)`:

  ```java
  return new PostDto(
      // ... all existing args ...,
      post.getCreatedAt(),
      post.isDeleted());
  ```

- [ ] **Step 3: Add `deleted` to `TripDto`**

  Same pattern as `RideDto`. In `TripDto.java`, add field after the last existing field, add constructor param `boolean deleted`, assign `this.deleted = deleted`, and add `trip.isDeleted()` as the last arg in `from()`.

  ```java
  @Schema(description = "Whether the trip is soft-deleted", required = true)
  final boolean deleted;
  ```

- [ ] **Step 4: Add `deleted` to record DTOs**

  For each record DTO, add `@Schema(description = "...", required = true) boolean deleted` as the **last** record component, and add `entity.isDeleted()` as the last arg in the `from()` factory method.

  **`RouteDto.java`** — add to record:
  ```java
  public record RouteDto(
      @Schema(description = "Route ID (TSID)", required = true) String id,
      @Schema(description = "Route slug", required = true) String slug,
      @Schema(description = "Team", required = true) TeamPublicationDto team,
      @Schema(description = "Route name", required = true) String name,
      @Schema(description = "Route description", required = true) MediaDto media,
      @Schema(description = "Distance in meters", required = true) Float distance,
      @Schema(description = "Total elevation gain in meters", required = true) Float elevationGain,
      @Schema(description = "Total elevation loss in meters", required = true) Float elevationLoss,
      @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
      @Schema(description = "Whether the route is public", required = true) Visibility visibility,
      @Schema(description = "Creation timestamp", required = true) Instant createdAt,
      @Schema(description = "Whether the route is soft-deleted", required = true) boolean deleted) {
    public static RouteDto from(Route route, AssetService assetService) {
      return new RouteDto(
          TsidUtils.toString(route.getId()),
          route.getSlug(),
          TeamPublicationDto.from(route.getTeam()),
          route.getName(),
          MediaDto.from(route, assetService),
          route.getDistance(),
          route.getElevationGain(),
          route.getElevationLoss(),
          route.getSurfaceType(),
          route.getVisibility(),
          route.getCreatedAt(),
          route.isDeleted());
    }
  }
  ```

  **`RouteDetailDto.java`** — add `boolean deleted` as last record component and `route.isDeleted()` as last arg in `from()`.

  **`AdDto.java`** — add `@Schema(description = "Whether the ad is soft-deleted", required = true) boolean deleted` as last record component; add `ad.isDeleted()` as last arg in `from()`.

  **`AdEditDto.java`** — same as `AdDto`.

  **`TeamPageDto.java`** — add `@Schema(description = "Whether the page is soft-deleted", required = true) boolean deleted` as last record component; add `page.isDeleted()` as last arg in `from()`.

  **`TeamPageSummaryDto.java`** — same as `TeamPageDto`.

- [ ] **Step 5: Ask user to run a build to verify compilation**

  ```
  mvn package -DskipTests -pl backend
  ```
  Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

  ```bash
  git add backend/src/main/java/fr/pedalons/dto/
  git commit -m "feat: add deleted field to entity response DTOs"
  ```

---

## Task 3: Add undelete service methods

**Files:**
- Modify: `backend/src/main/java/fr/pedalons/service/ride/RideService.java`
- Modify: `backend/src/main/java/fr/pedalons/service/post/PostService.java`
- Modify: `backend/src/main/java/fr/pedalons/service/trip/TripService.java`
- Modify: `backend/src/main/java/fr/pedalons/service/route/RouteService.java`
- Modify: `backend/src/main/java/fr/pedalons/service/ad/AdService.java`
- Modify: `backend/src/main/java/fr/pedalons/service/page/TeamPageService.java`
- Modify: `backend/src/test/java/fr/pedalons/service/ride/RideServiceTest.java`
- Modify: `backend/src/test/java/fr/pedalons/service/post/PostServiceTest.java`
- Modify: `backend/src/test/java/fr/pedalons/service/trip/TripServiceTest.java`
- Modify: `backend/src/test/java/fr/pedalons/service/route/RouteServiceTest.java`
- Modify: `backend/src/test/java/fr/pedalons/service/ad/AdServiceTest.java`
- Modify: `backend/src/test/java/fr/pedalons/service/page/TeamPageServiceTest.java`

- [ ] **Step 1: Write failing tests for undelete in `RideServiceTest`**

  Add after the existing `deleteRide_shouldThrowForNonOrganizer` test:

  ```java
  // ==================== Undelete Ride ====================

  @Test
  void undeleteRide_shouldRestoreDeletedRide() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());
    userService.setUserForTest(organizer);
    rideService.deleteRide(team.getSlug(), "test");

    userService.setUserForTest(organizer);
    RideDto restored = rideService.undeleteRide(team.getSlug(), "test");

    assertFalse(restored.isDeleted());
    assertEquals("test", restored.getSlug());
  }

  @Test
  void undeleteRide_shouldThrowForNonOrganizer() {
    dataService.createRide(team, admin, "Test", "test", Instant.now());
    userService.setUserForTest(organizer);
    rideService.deleteRide(team.getSlug(), "test");

    userService.setUserForTest(member);
    assertThrows(PedalonsException.class, () -> rideService.undeleteRide(team.getSlug(), "test"));
  }
  ```

- [ ] **Step 2: Add `undeleteRide()` to `RideService`**

  Add after `deleteRide()`:

  ```java
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.DELETE)
  @Transactional
  public RideDto undeleteRide(String teamSlug, String rideSlug) {
    Team team = teamService.getTeam(teamSlug);
    Ride ride = findBySlug(team, rideSlug);
    ride.setDeleted(false);
    rideRepository.persist(ride);
    return RideDto.from(ride, true, assetService);
  }
  ```

- [ ] **Step 3: Write failing tests for undelete in `PostServiceTest`**

  Add after the existing delete tests:

  ```java
  // ==================== Undelete Post ====================

  @Test
  void undeletePost_shouldRestoreDeletedPost() {
    dataService.createPost(team, admin, "Test", "test");
    userService.setUserForTest(organizer);
    postService.deletePost(team.getSlug(), "test");

    userService.setUserForTest(organizer);
    PostDto restored = postService.undeletePost(team.getSlug(), "test");

    assertFalse(restored.isDeleted());
    assertEquals("test", restored.getSlug());
  }

  @Test
  void undeletePost_shouldThrowForNonOrganizer() {
    dataService.createPost(team, admin, "Test", "test");
    userService.setUserForTest(organizer);
    postService.deletePost(team.getSlug(), "test");

    userService.setUserForTest(member);
    assertThrows(PedalonsException.class, () -> postService.undeletePost(team.getSlug(), "test"));
  }
  ```

  (Check `PostServiceTest` for the actual field name of `userService` — it may be `pedalonsQueryContext` or `userService`; use the same name as existing tests in that file.)

- [ ] **Step 4: Add `undeletePost()` to `PostService`**

  Add after `deletePost()`:

  ```java
  @CheckAccess(entityType = EntityType.POST, action = ActionType.DELETE)
  @Transactional
  public PostDto undeletePost(String teamSlug, String postSlug) {
    Team team = teamService.getTeam(teamSlug);
    Post post = findBySlug(team, postSlug);
    post.setDeleted(false);
    postRepository.persist(post);
    return PostDto.from(post, assetService);
  }
  ```

- [ ] **Step 5: Write failing tests for undelete in `TripServiceTest`**

  Add after the existing delete tests, using the same field names as in `TripServiceTest`:

  ```java
  // ==================== Undelete Trip ====================

  @Test
  void undeleteTrip_shouldRestoreDeletedTrip() {
    dataService.createTrip(team, admin, "Test", "test", Instant.now(), Instant.now().plusSeconds(86400));
    // use the field name for userService as found in TripServiceTest
    tripService.deleteTrip(team.getSlug(), "test");

    TripDto restored = tripService.undeleteTrip(team.getSlug(), "test");

    assertFalse(restored.isDeleted());
    assertEquals("test", restored.getSlug());
  }

  @Test
  void undeleteTrip_shouldThrowForNonOrganizer() {
    dataService.createTrip(team, admin, "Test", "test", Instant.now(), Instant.now().plusSeconds(86400));
    tripService.deleteTrip(team.getSlug(), "test");

    // set user to member then expect exception
    assertThrows(PedalonsException.class, () -> tripService.undeleteTrip(team.getSlug(), "test"));
  }
  ```

  (Adapt field names and `dataService.createTrip(...)` signature by reading the existing tests in `TripServiceTest.java`.)

- [ ] **Step 6: Add `undeleteTrip()` to `TripService`**

  Add after `deleteTrip()`:

  ```java
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.DELETE)
  @Transactional
  public TripDto undeleteTrip(String teamSlug, String tripSlug) {
    Team team = teamService.getTeam(teamSlug);
    Trip trip = findBySlug(team, tripSlug);
    trip.setDeleted(false);
    tripRepository.persist(trip);
    return TripDto.from(trip, true, assetService);
  }
  ```

- [ ] **Step 7: Write failing tests for undelete in `RouteServiceTest`**

  Add after `deleteRoute_shouldThrowForNonOrganizer`:

  ```java
  // ==================== Undelete Route ====================

  @Test
  void undeleteRoute_shouldRestoreDeletedRoute() throws Exception {
    Path gpxPath = getExampleGpxPath();
    RouteRequest request =
        new RouteRequest(
            "To Restore", MediaDto.builder().build(), null, Visibility.PUBLIC, List.of());
    queryContext.setUserForTest(admin);
    createdRoute = routeService.createRoute(team.getSlug(), request, gpxPath);
    String routeSlug = getCreatedRouteSlug();

    queryContext.setUserForTest(admin);
    routeService.deleteRoute(team.getSlug(), routeSlug);

    queryContext.setUserForTest(admin);
    RouteDetailDto restored = routeService.undeleteRoute(team.getSlug(), routeSlug);

    assertFalse(restored.deleted());
    assertEquals(routeSlug, restored.slug());
  }

  @Test
  void undeleteRoute_shouldThrowForNonOrganizer() {
    Route route = dataService.createRoute(team, admin, "Test");
    queryContext.setUserForTest(admin);
    routeService.deleteRoute(team.getSlug(), route.getSlug());

    queryContext.setUserForTest(member);
    assertThrows(
        PedalonsException.class,
        () -> routeService.undeleteRoute(team.getSlug(), route.getSlug()));
  }
  ```

- [ ] **Step 8: Add `undeleteRoute()` to `RouteService`**

  Add after `deleteRoute()`:

  ```java
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.DELETE)
  @Transactional
  public RouteDetailDto undeleteRoute(String teamSlug, String slug) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, slug);
    route.setDeleted(false);
    routeRepository.persist(route);
    return RouteDetailDto.from(route, assetService);
  }
  ```

- [ ] **Step 9: Write failing tests for undelete in `AdServiceTest`**

  Add after the existing delete tests (check `AdServiceTest` for the `userService`/`queryContext` field name and `dataService.createAd(...)` signature):

  ```java
  // ==================== Undelete Ad ====================

  @Test
  void undeleteAd_shouldRestoreDeletedAd() {
    Ad ad = dataService.createAd(team, admin, "Test", "test");
    // set user to admin/organizer
    adService.deleteAd(team.getSlug(), ad.getSlug());

    AdEditDto restored = adService.undeleteAd(team.getSlug(), ad.getSlug());

    assertFalse(restored.deleted());
    assertEquals(ad.getSlug(), restored.slug());
  }

  @Test
  void undeleteAd_shouldThrowForNonOrganizer() {
    Ad ad = dataService.createAd(team, admin, "Test", "test");
    adService.deleteAd(team.getSlug(), ad.getSlug());

    // set user to member
    assertThrows(PedalonsException.class, () -> adService.undeleteAd(team.getSlug(), ad.getSlug()));
  }
  ```

  (Adapt `dataService.createAd(...)` call and user-switching by reading existing tests in `AdServiceTest.java`.)

- [ ] **Step 10: Add `undeleteAd()` to `AdService`**

  Check what type `deleteAd()` returns and what `getDto()` returns in `AdService`. Add after `deleteAd()`:

  ```java
  @CheckAccess(entityType = EntityType.AD, action = ActionType.DELETE)
  @Transactional
  public AdEditDto undeleteAd(String teamSlug, String adSlug) {
    Team team = teamService.getTeam(teamSlug);
    Ad ad = findBySlug(team, adSlug);
    ad.setDeleted(false);
    adRepository.persist(ad);
    return AdEditDto.from(ad, assetService);
  }
  ```

- [ ] **Step 11: Write failing tests for undelete in `TeamPageServiceTest`**

  Add after the existing delete tests (check `TeamPageServiceTest` for field names):

  ```java
  // ==================== Undelete Page ====================

  @Test
  void undeletePage_shouldRestoreDeletedPage() {
    dataService.createTeamPage(team, admin, "Test", "test");
    // set user to admin
    teamPageService.deletePage(team.getSlug(), "test");

    TeamPageDto restored = teamPageService.undeletePage(team.getSlug(), "test");

    assertFalse(restored.deleted());
    assertEquals("test", restored.slug());
  }

  @Test
  void undeletePage_shouldThrowForNonAdmin() {
    dataService.createTeamPage(team, admin, "Test", "test");
    teamPageService.deletePage(team.getSlug(), "test");

    // set user to organizer or member
    assertThrows(PedalonsException.class, () -> teamPageService.undeletePage(team.getSlug(), "test"));
  }
  ```

- [ ] **Step 12: Add `undeletePage()` to `TeamPageService`**

  Check `TeamPageService` for the `pageRepository` field name and what `toDto()` returns. Add after `deletePage()`:

  ```java
  @CheckAccess(entityType = EntityType.TEAM_PAGE, action = ActionType.DELETE)
  @Transactional
  public TeamPageDto undeletePage(String teamSlug, String pageSlug) {
    Team team = teamService.getTeam(teamSlug);
    TeamPage page = findBySlug(team, pageSlug);
    page.setDeleted(false);
    getRepository().persist(page);
    return toDto(page);
  }
  ```

- [ ] **Step 13: Ask user to run all service tests**

  ```
  mvn test -Dtest="RideServiceTest,PostServiceTest,TripServiceTest,RouteServiceTest,AdServiceTest,TeamPageServiceTest" -pl backend
  ```
  Expected: all tests pass.

- [ ] **Step 14: Commit**

  ```bash
  git add backend/src/main/java/fr/pedalons/service/ \
          backend/src/test/java/fr/pedalons/service/
  git commit -m "feat: add undelete service methods for all soft-deletable entities"
  ```

---

## Task 4: Add undelete REST endpoints

**Files:**
- Modify: `backend/src/main/java/fr/pedalons/api/rides/RideResource.java`
- Modify: `backend/src/main/java/fr/pedalons/api/posts/PostResource.java`
- Modify: `backend/src/main/java/fr/pedalons/api/trips/TripResource.java`
- Modify: `backend/src/main/java/fr/pedalons/api/routes/RouteResource.java`
- Modify: `backend/src/main/java/fr/pedalons/api/ads/AdResource.java`
- Modify: `backend/src/main/java/fr/pedalons/api/pages/TeamPageResource.java`

- [ ] **Step 1: Add undelete endpoint to `RideResource`**

  Add after the `deleteRide` method:

  ```java
  @POST
  @Path("/{rideSlug}/undelete")
  @Operation(
      operationId = "undeleteRide",
      summary = "Restore ride",
      description = "Restore a soft-deleted ride. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ride restored successfully",
        content = @Content(schema = @Schema(implementation = RideDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this ride",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ride not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeleteRide(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {
    RideDto dto = rideService.undeleteRide(teamSlug, rideSlug);
    return Response.ok(dto).build();
  }
  ```

- [ ] **Step 2: Add undelete endpoint to `PostResource`**

  Add after the `deletePost` method (check the existing post slug path parameter name — it may be `postSlug`):

  ```java
  @POST
  @Path("/{postSlug}/undelete")
  @Operation(
      operationId = "undeletePost",
      summary = "Restore post",
      description = "Restore a soft-deleted post. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post restored successfully",
        content = @Content(schema = @Schema(implementation = PostDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this post",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or post not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeletePost(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug) {
    PostDto dto = postService.undeletePost(teamSlug, postSlug);
    return Response.ok(dto).build();
  }
  ```

- [ ] **Step 3: Add undelete endpoint to `TripResource`**

  Same pattern. Check the existing trip slug path parameter name in `TripResource.java`:

  ```java
  @POST
  @Path("/{tripSlug}/undelete")
  @Operation(
      operationId = "undeleteTrip",
      summary = "Restore trip",
      description = "Restore a soft-deleted trip. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Trip restored successfully",
        content = @Content(schema = @Schema(implementation = TripDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this trip",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeleteTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {
    TripDto dto = tripService.undeleteTrip(teamSlug, tripSlug);
    return Response.ok(dto).build();
  }
  ```

- [ ] **Step 4: Add undelete endpoint to `RouteResource`**

  Check route slug path param name (likely `routeSlug`):

  ```java
  @POST
  @Path("/{routeSlug}/undelete")
  @Operation(
      operationId = "undeleteRoute",
      summary = "Restore route",
      description = "Restore a soft-deleted route. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route restored successfully",
        content = @Content(schema = @Schema(implementation = RouteDetailDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this route",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeleteRoute(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route URL slug") @PathParam("routeSlug") String routeSlug) {
    RouteDetailDto dto = routeService.undeleteRoute(teamSlug, routeSlug);
    return Response.ok(dto).build();
  }
  ```

- [ ] **Step 5: Add undelete endpoint to `AdResource`**

  Check ad slug path param name (likely `adSlug` or `slug`):

  ```java
  @POST
  @Path("/{adSlug}/undelete")
  @Operation(
      operationId = "undeleteAd",
      summary = "Restore ad",
      description = "Restore a soft-deleted ad. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad restored successfully",
        content = @Content(schema = @Schema(implementation = AdEditDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeleteAd(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("adSlug") String adSlug) {
    AdEditDto dto = adService.undeleteAd(teamSlug, adSlug);
    return Response.ok(dto).build();
  }
  ```

- [ ] **Step 6: Add undelete endpoint to `TeamPageResource`**

  Check page slug path param name (likely `pageSlug`):

  ```java
  @POST
  @Path("/{pageSlug}/undelete")
  @Operation(
      operationId = "undeletePage",
      summary = "Restore page",
      description = "Restore a soft-deleted team page. Requires admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Page restored successfully",
        content = @Content(schema = @Schema(implementation = TeamPageDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this page",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or page not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeletePage(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Page URL slug") @PathParam("pageSlug") String pageSlug) {
    TeamPageDto dto = teamPageService.undeletePage(teamSlug, pageSlug);
    return Response.ok(dto).build();
  }
  ```

- [ ] **Step 7: Ask user to run a build to generate the updated OpenAPI contract**

  ```
  mvn package -DskipTests -pl backend
  ```
  Expected: BUILD SUCCESS. The files `contracts/openapi.yaml` and `contracts/openapi.json` are regenerated.

- [ ] **Step 8: Commit**

  ```bash
  git add backend/src/main/java/fr/pedalons/api/ contracts/
  git commit -m "feat: add undelete REST endpoints for all soft-deletable entities"
  ```

---

## Task 5: Regenerate frontend API client

**Files:** `frontend/src/api/` (generated, all changes under this directory)

- [ ] **Step 1: Run code generation**

  ```bash
  cd frontend && pnpm generate-api
  ```
  Expected: generates new hooks `useUndeleteRide`, `useUndeletePost`, `useUndeleteTrip`, `useUndeleteRoute`, `useUndeleteAd`, `useUndeletePage` (under `frontend/src/api/endpoints/`), and updates all DTO types to include `deleted: boolean`.

- [ ] **Step 2: Commit**

  ```bash
  git add frontend/src/api/
  git commit -m "feat: regenerate API client with undelete endpoints and deleted field"
  ```

---

## Task 6: Add i18n keys

**Files:**
- Modify: `frontend/src/locales/en/common.json`
- Modify: `frontend/src/locales/fr/common.json`

- [ ] **Step 1: Add keys to EN locale**

  Add to `frontend/src/locales/en/common.json` in alphabetical order (after `"actions.unpublish"`):

  ```json
  "actions.restore": "Restore",
  ```

  Add per-entity restored notifications (in their respective sections):

  ```json
  "ads.notifications.restored": "Ad restored successfully",
  "posts.notifications.restored": "Post restored successfully",
  "rides.notifications.restored": "Ride restored successfully",
  "routes.notifications.restored": "Route restored successfully",
  "teams.pages.notifications.restored": "Page restored successfully",
  "trips.notifications.restored": "Trip restored successfully",
  ```

- [ ] **Step 2: Add keys to FR locale**

  Add to `frontend/src/locales/fr/common.json` in alphabetical order (after `"actions.unpublish"`):

  ```json
  "actions.restore": "Restaurer",
  ```

  Add per-entity restored notifications:

  ```json
  "ads.notifications.restored": "Annonce restaurée avec succès",
  "posts.notifications.restored": "Publication restaurée avec succès",
  "rides.notifications.restored": "Sortie restaurée avec succès",
  "routes.notifications.restored": "Parcours restauré avec succès",
  "teams.pages.notifications.restored": "Page restaurée avec succès",
  "trips.notifications.restored": "Voyage restauré avec succès",
  ```

- [ ] **Step 3: Validate i18n keys**

  ```bash
  cd frontend && pnpm i18n:lint
  ```
  Expected: no errors.

- [ ] **Step 4: Commit**

  ```bash
  git add frontend/src/locales/
  git commit -m "feat: add restore i18n keys"
  ```

---

## Task 7: Add Restore action to frontend pages

**Files:**
- Modify: `frontend/src/pages/ride/RideDetailPage.tsx`
- Modify: `frontend/src/pages/post/PostDetailPage.tsx`
- Modify: `frontend/src/pages/trip/TripDetailPage.tsx`
- Modify: `frontend/src/pages/route/RouteDetailPage.tsx`
- Modify: `frontend/src/pages/ad/AdDetailPage.tsx`
- Modify: `frontend/src/pages/team/TeamPagesAdminPage.tsx`

All pages follow the same pattern. The restore item appears **before** the `<Menu.Divider />` that precedes the delete item, and only when `entity.deleted === true`.

- [ ] **Step 1: Update `RideDetailPage.tsx`**

  Add `useUndeleteRide` and `getGetRideQueryKey` to imports:

  ```tsx
  import {
    useGetRide,
    useUpdateRide,
    useDeleteRide,
    useUndeleteRide,
    useJoinGroup,
    useLeaveGroup,
    getGetRideQueryKey,
    getListPublicationsQueryKey,  // already imported via getListPublicationsQueryKey
  } from '../../api/endpoints/rides/rides'
  ```

  (Note: `getListPublicationsQueryKey` is imported from publications, not rides. Adjust import source accordingly.)

  Add the mutation after `deleteMutation`:

  ```tsx
  const undeleteMutation = useUndeleteRide()
  ```

  Add the handler after `handleDelete`:

  ```tsx
  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, rideSlug: rideSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRideQueryKey(teamSlug!, rideSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('rides.notifications.restored'), color: 'green' })
        },
      }
    )
  }
  ```

  In the `Menu.Dropdown`, add before `<Menu.Divider />`:

  ```tsx
  {ride.deleted && (
    <Menu.Item onClick={handleRestore} color="green" disabled={undeleteMutation.isPending}>
      {t('actions.restore')}
    </Menu.Item>
  )}
  ```

- [ ] **Step 2: Update `PostDetailPage.tsx`**

  Add `useUndeletePost` and `getGetPostQueryKey` to imports from posts endpoint. Add mutation:

  ```tsx
  const undeleteMutation = useUndeletePost()
  ```

  Add handler:

  ```tsx
  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('posts.notifications.restored'), color: 'green' })
        },
      }
    )
  }
  ```

  In `Menu.Dropdown`, add before `<Menu.Divider />`:

  ```tsx
  {post.deleted && (
    <Menu.Item onClick={handleRestore} color="green" disabled={undeleteMutation.isPending}>
      {t('actions.restore')}
    </Menu.Item>
  )}
  ```

- [ ] **Step 3: Update `TripDetailPage.tsx`**

  Add `useUndeleteTrip` to imports from trips endpoint. Add mutation:

  ```tsx
  const undeleteMutation = useUndeleteTrip()
  ```

  Add handler (check `TripDetailPage` for param names — likely `tripSlug` from `useParams`):

  ```tsx
  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, tripSlug: tripSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetTripQueryKey(teamSlug!, tripSlug!) })
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('trips.notifications.restored'), color: 'green' })
        },
      }
    )
  }
  ```

  In `Menu.Dropdown`, add before `<Menu.Divider />`:

  ```tsx
  {trip.deleted && (
    <Menu.Item onClick={handleRestore} color="green" disabled={undeleteMutation.isPending}>
      {t('actions.restore')}
    </Menu.Item>
  )}
  ```

- [ ] **Step 4: Update `RouteDetailPage.tsx`**

  Add `useUndeleteRoute`, `getGetRouteQueryKey` to imports:

  ```tsx
  import {
    useGetRoute,
    useDeleteRoute,
    useUndeleteRoute,
    getListRoutesQueryKey,
    getGetRouteQueryKey,
  } from '@/api/endpoints/routes/routes'
  ```

  Add mutation:

  ```tsx
  const undeleteMutation = useUndeleteRoute()
  ```

  Add handler:

  ```tsx
  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, routeSlug: routeSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRouteQueryKey(teamSlug!, routeSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug!) })
          notifications.show({ message: t('routes.notifications.restored'), color: 'green' })
        },
      }
    )
  }
  ```

  The `RouteDetailPage` uses a `Button.Group` (like `RideDetailPage`). Add the restore item in the `Menu.Dropdown` before the delete item:

  ```tsx
  {route.deleted && (
    <Menu.Item onClick={handleRestore} color="green" disabled={undeleteMutation.isPending}>
      {t('actions.restore')}
    </Menu.Item>
  )}
  <Menu.Divider />
  <Menu.Item onClick={() => setShowDeleteConfirm(true)} color="danger">
    {t('actions.delete')}
  </Menu.Item>
  ```

  (If there is no `Menu.Divider` before delete currently in `RouteDetailPage`, add one above the restore item.)

- [ ] **Step 5: Update `AdDetailPage.tsx`**

  Add `useUndeleteAd` to imports from ads endpoint. Add mutation:

  ```tsx
  const undeleteMutation = useUndeleteAd()
  ```

  Add handler (check `AdDetailPage` for slug param name — likely `adSlug` from `useParams`, and check what `invalidateAds()` already does):

  ```tsx
  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, adSlug: adSlug! },
      {
        onSuccess: () => {
          invalidateAds()
          notifications.show({ message: t('ads.notifications.restored'), color: 'green' })
        },
      }
    )
  }
  ```

  In `Menu.Dropdown`, add before `<Menu.Divider />`:

  ```tsx
  {ad.deleted && (
    <Menu.Item onClick={handleRestore} color="green" disabled={undeleteMutation.isPending}>
      {t('actions.restore')}
    </Menu.Item>
  )}
  ```

- [ ] **Step 6: Update `TeamPagesAdminPage.tsx`**

  `TeamPagesAdminPage` uses `ActionIcon` buttons (not a `Menu`). Add `useUndeletePage` import and mutation:

  ```tsx
  import {
    useListPages,
    useDeletePage,
    useUndeletePage,
    useReorderPages,
    getListPagesQueryKey,
  } from '@/api/endpoints/team-pages/team-pages'
  ```

  ```tsx
  const undeleteMutation = useUndeletePage()
  ```

  Add handler:

  ```tsx
  const handleRestore = (page: TeamPageSummaryDto) => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, pageSlug: page.slug },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPagesQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetTeamQueryKey(teamSlug!) })
          notifications.show({
            message: i18next.t('teams.pages.notifications.restored'),
            color: 'green',
          })
        },
      }
    )
  }
  ```

  In the page item row, add the restore icon **between the edit icon and delete icon**, visible only when `page.deleted`:

  ```tsx
  {page.deleted && (
    <ActionIcon
      variant="subtle"
      color="green"
      onClick={() => handleRestore(page)}
      title={t('actions.restore')}
      loading={undeleteMutation.isPending}
    >
      <IconRestore size={20} />
    </ActionIcon>
  )}
  ```

  Add `IconRestore` to the import from `@tabler/icons-react`:

  ```tsx
  import { IconPlus, IconFileText, IconPencil, IconTrash, IconMenu2, IconRestore } from '@tabler/icons-react'
  ```

  (The correct Tabler icon name is `IconRestore` — verify it exists at `@tabler/icons-react` by checking https://tabler.io/icons. If it doesn't exist, use `IconArchiveOff` which is definitely available.)

- [ ] **Step 7: Build frontend to verify TypeScript compiles**

  ```bash
  cd frontend && pnpm typecheck
  ```
  Expected: no errors.

- [ ] **Step 8: Commit**

  ```bash
  git add frontend/src/pages/
  git commit -m "feat: add restore action to entity detail pages"
  ```

---

## Task 8: Final verification

- [ ] **Step 1: Ask user to run all backend tests**

  ```
  mvn test -pl backend
  ```
  Expected: all tests pass.

- [ ] **Step 2: Frontend lint and format check**

  ```bash
  cd frontend && pnpm lint && pnpm format:check && pnpm i18n:lint
  ```
  Expected: no errors.

- [ ] **Step 3: Commit if any lint fixes were applied**

  ```bash
  git add frontend/src/
  git commit -m "fix: apply lint and format fixes"
  ```
