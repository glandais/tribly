# Tasks: Cycling Team Management Platform

**Input**: Design documents from `/specs/001-cycling-team-platform/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml, quickstart.md

**Tests**: TDD approach per constitution - tests included for all user stories.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `backend/src/main/java/com/tribly/`
- **Frontend**: `frontend/src/`
- **E2E Tests**: `e2e/tests/`
- **Contracts**: `contracts/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization, monorepo structure, and development environment

- [ ] T001 Create monorepo root structure with pom.xml parent POM
- [ ] T002 [P] Initialize backend Quarkus project in backend/ with pom.xml
- [ ] T003 [P] Initialize frontend React/Vite project in frontend/ with package.json
- [ ] T004 [P] Initialize e2e Playwright project in e2e/ with package.json
- [ ] T005 Create docker-compose.yml with PostgreSQL 16 and PostGIS extension
- [ ] T006 [P] Copy contracts/openapi.yaml from specs to contracts/
- [ ] T007 Configure backend OpenAPI code generation in backend/pom.xml
- [ ] T008 Configure frontend OpenAPI client generation in frontend/package.json
- [ ] T009 [P] Configure ESLint and Prettier for frontend in frontend/.eslintrc.js
- [ ] T010 [P] Configure Checkstyle for backend in backend/checkstyle.xml
- [ ] T011 Create .env.example files for backend and frontend
- [ ] T012 Create README.md with quickstart instructions

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**CRITICAL**: No user story work can begin until this phase is complete

### Database Foundation

- [ ] T013 Create Flyway migration V1__initial_schema.sql in backend/src/main/resources/db/migration/
- [ ] T014 Implement BaseEntity abstract class in backend/src/main/java/com/tribly/domain/common/BaseEntity.java
- [ ] T015 [P] Create TeamRole enum in backend/src/main/java/com/tribly/domain/team/TeamRole.java
- [ ] T016 [P] Create RideStatus enum in backend/src/main/java/com/tribly/domain/ride/RideStatus.java
- [ ] T017 [P] Create TripStatus enum in backend/src/main/java/com/tribly/domain/trip/TripStatus.java
- [ ] T018 [P] Create Visibility enum in backend/src/main/java/com/tribly/domain/common/Visibility.java
- [ ] T019 [P] Create PlaceType enum in backend/src/main/java/com/tribly/domain/place/PlaceType.java

### Backend Infrastructure

- [ ] T020 Configure application.properties with database, OIDC, and JWT settings in backend/src/main/resources/
- [ ] T021 Implement GlobalExceptionMapper in backend/src/main/java/com/tribly/infrastructure/exception/GlobalExceptionMapper.java
- [ ] T022 [P] Create ErrorResponse DTO in backend/src/main/java/com/tribly/api/dto/ErrorResponse.java
- [ ] T023 Implement TenantContext for multi-tenancy in backend/src/main/java/com/tribly/infrastructure/multitenancy/TenantContext.java
- [ ] T024 Implement TenantFilter request filter in backend/src/main/java/com/tribly/infrastructure/multitenancy/TenantFilter.java
- [ ] T025 Configure CORS in backend/src/main/java/com/tribly/config/CorsConfig.java

### Frontend Infrastructure

- [ ] T026 Generate API client from OpenAPI using pnpm generate-api
- [ ] T027 Configure TanStack Query provider in frontend/src/main.tsx
- [ ] T028 [P] Create Zustand auth store in frontend/src/store/authStore.ts
- [ ] T029 [P] Create API client configuration in frontend/src/api/client.ts
- [ ] T030 Setup React Router with route structure in frontend/src/App.tsx
- [ ] T031 [P] Create common Layout component in frontend/src/components/common/Layout.tsx
- [ ] T032 [P] Create LoadingSpinner component in frontend/src/components/common/LoadingSpinner.tsx
- [ ] T033 [P] Create ErrorBoundary component in frontend/src/components/common/ErrorBoundary.tsx

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - User Authentication via Strava (Priority: P1)

**Goal**: Users can sign in via Strava OAuth2, create profile, and sign out

**Independent Test**: Complete OAuth flow with Strava, verify profile creation, session management

### Tests for User Story 1

- [ ] T034 [P] [US1] Contract test for OAuth endpoints in backend/src/test/java/com/tribly/contract/AuthContractTest.java
- [ ] T035 [P] [US1] Integration test for Strava OAuth flow in backend/src/test/java/com/tribly/integration/StravaAuthTest.java
- [ ] T036 [P] [US1] E2E test for login flow in e2e/tests/auth.spec.ts

### Backend Implementation for User Story 1

- [ ] T037 [P] [US1] Create User entity in backend/src/main/java/com/tribly/domain/user/User.java
- [ ] T038 [US1] Create UserRepository in backend/src/main/java/com/tribly/domain/user/UserRepository.java
- [ ] T039 [US1] Implement StravaOAuthService in backend/src/main/java/com/tribly/service/auth/StravaOAuthService.java
- [ ] T040 [US1] Implement JwtService for token generation in backend/src/main/java/com/tribly/service/auth/JwtService.java
- [ ] T041 [US1] Implement AuthResource REST endpoints in backend/src/main/java/com/tribly/api/auth/AuthResource.java
- [ ] T042 [US1] Implement UserResource for profile management in backend/src/main/java/com/tribly/api/users/UserResource.java
- [ ] T043 [US1] Create SecurityIdentityAugmentor for JWT validation in backend/src/main/java/com/tribly/infrastructure/security/SecurityIdentityAugmentor.java

### Frontend Implementation for User Story 1

- [ ] T044 [P] [US1] Create LoginPage in frontend/src/pages/auth/LoginPage.tsx
- [ ] T045 [P] [US1] Create OAuthCallbackPage in frontend/src/pages/auth/OAuthCallbackPage.tsx
- [ ] T046 [US1] Create useAuth hook in frontend/src/hooks/useAuth.ts
- [ ] T047 [US1] Create ProtectedRoute component in frontend/src/components/auth/ProtectedRoute.tsx
- [ ] T048 [US1] Create UserProfilePage in frontend/src/pages/auth/UserProfilePage.tsx
- [ ] T049 [US1] Create UserAvatar component in frontend/src/components/common/UserAvatar.tsx

**Checkpoint**: US1 complete - users can authenticate via Strava

---

## Phase 4: User Story 2 - Create and Join a Team (Priority: P1)

**Goal**: Users can create teams, manage members, and join public teams

**Independent Test**: Create team, configure visibility, have another user join

### Tests for User Story 2

- [ ] T050 [P] [US2] Contract test for team endpoints in backend/src/test/java/com/tribly/contract/TeamContractTest.java
- [ ] T051 [P] [US2] Integration test for team CRUD in backend/src/test/java/com/tribly/integration/TeamServiceTest.java
- [ ] T052 [P] [US2] E2E test for team creation in e2e/tests/team.spec.ts

### Backend Implementation for User Story 2

- [ ] T053 [P] [US2] Create Team entity in backend/src/main/java/com/tribly/domain/team/Team.java
- [ ] T054 [P] [US2] Create UserTeam entity in backend/src/main/java/com/tribly/domain/team/UserTeam.java
- [ ] T055 [P] [US2] Create TeamDomain entity in backend/src/main/java/com/tribly/domain/team/TeamDomain.java
- [ ] T056 [US2] Create TeamRepository in backend/src/main/java/com/tribly/domain/team/TeamRepository.java
- [ ] T057 [US2] Create UserTeamRepository in backend/src/main/java/com/tribly/domain/team/UserTeamRepository.java
- [ ] T058 [US2] Implement TeamService in backend/src/main/java/com/tribly/service/team/TeamService.java
- [ ] T059 [US2] Implement TeamMembershipService in backend/src/main/java/com/tribly/service/team/TeamMembershipService.java
- [ ] T060 [US2] Implement TeamResource REST endpoints in backend/src/main/java/com/tribly/api/teams/TeamResource.java
- [ ] T061 [US2] Implement TeamMemberResource in backend/src/main/java/com/tribly/api/teams/TeamMemberResource.java
- [ ] T062 [US2] Add Hibernate tenant filter to Team-owned entities

### Frontend Implementation for User Story 2

- [ ] T063 [P] [US2] Create TeamListPage in frontend/src/pages/team/TeamListPage.tsx
- [ ] T064 [P] [US2] Create TeamDetailPage in frontend/src/pages/team/TeamDetailPage.tsx
- [ ] T065 [P] [US2] Create CreateTeamPage in frontend/src/pages/team/CreateTeamPage.tsx
- [ ] T066 [US2] Create TeamCard component in frontend/src/components/team/TeamCard.tsx
- [ ] T067 [US2] Create TeamMemberList component in frontend/src/components/team/TeamMemberList.tsx
- [ ] T068 [US2] Create useTeam hook in frontend/src/hooks/useTeam.ts
- [ ] T069 [US2] Create TeamSettingsPage in frontend/src/pages/team/TeamSettingsPage.tsx

**Checkpoint**: US2 complete - users can create and join teams

---

## Phase 5: User Story 3 - Create and Publish a Ride (Priority: P1)

**Goal**: Team admins can create rides with groups, publish them, members can register

**Independent Test**: Create ride with 3 groups, publish, verify member registration

### Tests for User Story 3

- [ ] T070 [P] [US3] Contract test for ride endpoints in backend/src/test/java/com/tribly/contract/RideContractTest.java
- [ ] T071 [P] [US3] Integration test for ride CRUD in backend/src/test/java/com/tribly/integration/RideServiceTest.java
- [ ] T072 [P] [US3] E2E test for ride creation flow in e2e/tests/ride.spec.ts

### Backend Implementation for User Story 3

- [ ] T073 [P] [US3] Create Place entity in backend/src/main/java/com/tribly/domain/place/Place.java
- [ ] T074 [P] [US3] Create Ride entity in backend/src/main/java/com/tribly/domain/ride/Ride.java
- [ ] T075 [P] [US3] Create RideGroup entity in backend/src/main/java/com/tribly/domain/ride/RideGroup.java
- [ ] T076 [P] [US3] Create RideParticipation entity in backend/src/main/java/com/tribly/domain/ride/RideParticipation.java
- [ ] T077 [US3] Create RideRepository in backend/src/main/java/com/tribly/domain/ride/RideRepository.java
- [ ] T078 [US3] Create PlaceRepository in backend/src/main/java/com/tribly/domain/place/PlaceRepository.java
- [ ] T079 [US3] Implement RideService in backend/src/main/java/com/tribly/service/ride/RideService.java
- [ ] T080 [US3] Implement RideGroupService in backend/src/main/java/com/tribly/service/ride/RideGroupService.java
- [ ] T081 [US3] Implement RideParticipationService in backend/src/main/java/com/tribly/service/ride/RideParticipationService.java
- [ ] T082 [US3] Implement PlaceService in backend/src/main/java/com/tribly/service/place/PlaceService.java
- [ ] T083 [US3] Implement RideResource REST endpoints in backend/src/main/java/com/tribly/api/rides/RideResource.java
- [ ] T084 [US3] Implement PlaceResource REST endpoints in backend/src/main/java/com/tribly/api/places/PlaceResource.java

### Frontend Implementation for User Story 3

- [ ] T085 [P] [US3] Create RideListPage in frontend/src/pages/ride/RideListPage.tsx
- [ ] T086 [P] [US3] Create RideDetailPage in frontend/src/pages/ride/RideDetailPage.tsx
- [ ] T087 [P] [US3] Create CreateRidePage in frontend/src/pages/ride/CreateRidePage.tsx
- [ ] T088 [US3] Create RideCard component in frontend/src/components/ride/RideCard.tsx
- [ ] T089 [US3] Create RideGroupCard component in frontend/src/components/ride/RideGroupCard.tsx
- [ ] T090 [US3] Create ParticipantList component in frontend/src/components/ride/ParticipantList.tsx
- [ ] T091 [US3] Create RideForm component in frontend/src/components/ride/RideForm.tsx
- [ ] T092 [US3] Create PlaceSelector component in frontend/src/components/place/PlaceSelector.tsx
- [ ] T093 [US3] Create useRide hook in frontend/src/hooks/useRide.ts

**Checkpoint**: US3 complete - teams can organize rides (MVP complete!)

---

## Phase 6: User Story 4 - Upload and View Routes (Priority: P2)

**Goal**: Users can upload GPX files, view routes on maps with elevation profiles

**Independent Test**: Upload GPX file, verify map display, elevation profile, download GPX/FIT

### Tests for User Story 4

- [ ] T094 [P] [US4] Contract test for route endpoints in backend/src/test/java/com/tribly/contract/RouteContractTest.java
- [ ] T095 [P] [US4] Unit test for GPX parsing in backend/src/test/java/com/tribly/unit/GpxParserTest.java
- [ ] T096 [P] [US4] Integration test for route upload in backend/src/test/java/com/tribly/integration/RouteServiceTest.java
- [ ] T097 [P] [US4] E2E test for route upload flow in e2e/tests/route.spec.ts

### Backend Implementation for User Story 4

- [ ] T098 [P] [US4] Create Route entity in backend/src/main/java/com/tribly/domain/route/Route.java
- [ ] T099 [P] [US4] Create GpxTrack entity in backend/src/main/java/com/tribly/domain/route/GpxTrack.java
- [ ] T100 [P] [US4] Create RouteClimb entity in backend/src/main/java/com/tribly/domain/route/RouteClimb.java
- [ ] T101 [P] [US4] Create RoutePointOfInterest entity in backend/src/main/java/com/tribly/domain/route/RoutePointOfInterest.java
- [ ] T102 [P] [US4] Create RouteDifficulty enum in backend/src/main/java/com/tribly/domain/route/RouteDifficulty.java
- [ ] T103 [P] [US4] Create SurfaceType enum in backend/src/main/java/com/tribly/domain/route/SurfaceType.java
- [ ] T104 [US4] Create RouteRepository in backend/src/main/java/com/tribly/domain/route/RouteRepository.java
- [ ] T105 [US4] Implement GpxParserService in backend/src/main/java/com/tribly/infrastructure/gpx/GpxParserService.java
- [ ] T106 [US4] Implement ElevationAnalyzer for climb detection in backend/src/main/java/com/tribly/infrastructure/gpx/ElevationAnalyzer.java
- [ ] T107 [US4] Implement FitConverter service in backend/src/main/java/com/tribly/infrastructure/gpx/FitConverter.java
- [ ] T108 [US4] Implement RouteService in backend/src/main/java/com/tribly/service/route/RouteService.java
- [ ] T109 [US4] Implement RouteResource REST endpoints in backend/src/main/java/com/tribly/api/routes/RouteResource.java

### Frontend Implementation for User Story 4

- [ ] T110 [P] [US4] Create RouteListPage in frontend/src/pages/route/RouteListPage.tsx
- [ ] T111 [P] [US4] Create RouteDetailPage in frontend/src/pages/route/RouteDetailPage.tsx
- [ ] T112 [P] [US4] Create UploadRoutePage in frontend/src/pages/route/UploadRoutePage.tsx
- [ ] T113 [US4] Create RouteMap component with Leaflet in frontend/src/components/map/RouteMap.tsx
- [ ] T114 [US4] Create ElevationProfile chart component in frontend/src/components/route/ElevationProfile.tsx
- [ ] T115 [US4] Create RouteCard component in frontend/src/components/route/RouteCard.tsx
- [ ] T116 [US4] Create RouteStats component in frontend/src/components/route/RouteStats.tsx
- [ ] T117 [US4] Create ClimbList component in frontend/src/components/route/ClimbList.tsx
- [ ] T118 [US4] Create GpxUploader component in frontend/src/components/route/GpxUploader.tsx
- [ ] T119 [US4] Create useRoute hook in frontend/src/hooks/useRoute.ts

**Checkpoint**: US4 complete - routes can be uploaded and visualized

---

## Phase 7: User Story 5 - Plan a Multi-Day Trip (Priority: P2)

**Goal**: Organizers can create trips with stages, participants can register

**Independent Test**: Create trip with 3 stages, verify totals calculation, participant registration

### Tests for User Story 5

- [ ] T120 [P] [US5] Contract test for trip endpoints in backend/src/test/java/com/tribly/contract/TripContractTest.java
- [ ] T121 [P] [US5] Integration test for trip CRUD in backend/src/test/java/com/tribly/integration/TripServiceTest.java
- [ ] T122 [P] [US5] E2E test for trip creation flow in e2e/tests/trip.spec.ts

### Backend Implementation for User Story 5

- [ ] T123 [P] [US5] Create Trip entity in backend/src/main/java/com/tribly/domain/trip/Trip.java
- [ ] T124 [P] [US5] Create TripDay entity in backend/src/main/java/com/tribly/domain/trip/TripDay.java
- [ ] T125 [P] [US5] Create TripDayRide entity in backend/src/main/java/com/tribly/domain/trip/TripDayRide.java
- [ ] T126 [P] [US5] Create TripParticipation entity in backend/src/main/java/com/tribly/domain/trip/TripParticipation.java
- [ ] T127 [US5] Create TripRepository in backend/src/main/java/com/tribly/domain/trip/TripRepository.java
- [ ] T128 [US5] Implement TripService in backend/src/main/java/com/tribly/service/trip/TripService.java
- [ ] T129 [US5] Implement TripStatsCalculator in backend/src/main/java/com/tribly/service/trip/TripStatsCalculator.java
- [ ] T130 [US5] Implement TripResource REST endpoints in backend/src/main/java/com/tribly/api/trips/TripResource.java

### Frontend Implementation for User Story 5

- [ ] T131 [P] [US5] Create TripListPage in frontend/src/pages/trip/TripListPage.tsx
- [ ] T132 [P] [US5] Create TripDetailPage in frontend/src/pages/trip/TripDetailPage.tsx
- [ ] T133 [P] [US5] Create CreateTripPage in frontend/src/pages/trip/CreateTripPage.tsx
- [ ] T134 [US5] Create TripCard component in frontend/src/components/trip/TripCard.tsx
- [ ] T135 [US5] Create TripDayCard component in frontend/src/components/trip/TripDayCard.tsx
- [ ] T136 [US5] Create TripStatsDisplay component in frontend/src/components/trip/TripStatsDisplay.tsx
- [ ] T137 [US5] Create TripTimeline component in frontend/src/components/trip/TripTimeline.tsx
- [ ] T138 [US5] Create useTrip hook in frontend/src/hooks/useTrip.ts

**Checkpoint**: US5 complete - teams can plan multi-day trips

---

## Phase 8: User Story 6 - Comment on Rides and Trips (Priority: P3)

**Goal**: Participants can post and reply to comments on rides/trips

**Independent Test**: Post comment, reply, verify threaded display and deletion

### Tests for User Story 6

- [ ] T139 [P] [US6] Contract test for message endpoints in backend/src/test/java/com/tribly/contract/MessageContractTest.java
- [ ] T140 [P] [US6] Integration test for messaging in backend/src/test/java/com/tribly/integration/MessageServiceTest.java

### Backend Implementation for User Story 6

- [ ] T141 [P] [US6] Create MessageThread entity in backend/src/main/java/com/tribly/domain/message/MessageThread.java
- [ ] T142 [P] [US6] Create Message entity in backend/src/main/java/com/tribly/domain/message/Message.java
- [ ] T143 [P] [US6] Create MessageThreadParticipant entity in backend/src/main/java/com/tribly/domain/message/MessageThreadParticipant.java
- [ ] T144 [US6] Create MessageRepository in backend/src/main/java/com/tribly/domain/message/MessageRepository.java
- [ ] T145 [US6] Implement MessageService in backend/src/main/java/com/tribly/service/message/MessageService.java
- [ ] T146 [US6] Implement MessageResource REST endpoints in backend/src/main/java/com/tribly/api/messages/MessageResource.java

### Frontend Implementation for User Story 6

- [ ] T147 [US6] Create MessageThread component in frontend/src/components/message/MessageThread.tsx
- [ ] T148 [US6] Create MessageItem component in frontend/src/components/message/MessageItem.tsx
- [ ] T149 [US6] Create MessageComposer component in frontend/src/components/message/MessageComposer.tsx
- [ ] T150 [US6] Integrate comments into RideDetailPage
- [ ] T151 [US6] Integrate comments into TripDetailPage

**Checkpoint**: US6 complete - contextual communication enabled

---

## Phase 9: User Story 7 - Browse Public Route Catalog (Priority: P3)

**Goal**: Anonymous users can browse and filter public routes

**Independent Test**: Search with filters, verify matching routes, add to favorites

### Tests for User Story 7

- [ ] T152 [P] [US7] Contract test for catalog endpoints in backend/src/test/java/com/tribly/contract/CatalogContractTest.java
- [ ] T153 [P] [US7] Integration test for catalog search in backend/src/test/java/com/tribly/integration/CatalogServiceTest.java

### Backend Implementation for User Story 7

- [ ] T154 [US7] Implement CatalogService in backend/src/main/java/com/tribly/service/catalog/CatalogService.java
- [ ] T155 [US7] Implement spatial search for routes in CatalogService
- [ ] T156 [US7] Implement CatalogResource REST endpoints in backend/src/main/java/com/tribly/api/catalog/CatalogResource.java
- [ ] T157 [US7] Create Favorite entity in backend/src/main/java/com/tribly/domain/route/Favorite.java
- [ ] T158 [US7] Implement FavoriteService in backend/src/main/java/com/tribly/service/route/FavoriteService.java

### Frontend Implementation for User Story 7

- [ ] T159 [P] [US7] Create CatalogPage in frontend/src/pages/catalog/CatalogPage.tsx
- [ ] T160 [US7] Create CatalogFilters component in frontend/src/components/catalog/CatalogFilters.tsx
- [ ] T161 [US7] Create CatalogMap component with route markers in frontend/src/components/catalog/CatalogMap.tsx
- [ ] T162 [US7] Create CatalogRouteCard component in frontend/src/components/catalog/CatalogRouteCard.tsx
- [ ] T163 [US7] Create FavoritesPage in frontend/src/pages/catalog/FavoritesPage.tsx

**Checkpoint**: US7 complete - public route discovery enabled

---

## Phase 10: User Story 8 - Ride Templates (Priority: P4)

**Goal**: Admins can save ride configurations as templates for quick creation

**Independent Test**: Save template, create ride from it, verify pre-filled values

### Tests for User Story 8

- [ ] T164 [P] [US8] Contract test for template endpoints in backend/src/test/java/com/tribly/contract/RideTemplateContractTest.java
- [ ] T165 [P] [US8] Integration test for templates in backend/src/test/java/com/tribly/integration/RideTemplateServiceTest.java

### Backend Implementation for User Story 8

- [ ] T166 [US8] Create RideTemplate entity in backend/src/main/java/com/tribly/domain/ride/RideTemplate.java
- [ ] T167 [US8] Create RideTemplateRepository in backend/src/main/java/com/tribly/domain/ride/RideTemplateRepository.java
- [ ] T168 [US8] Implement RideTemplateService in backend/src/main/java/com/tribly/service/ride/RideTemplateService.java
- [ ] T169 [US8] Implement RideTemplateResource in backend/src/main/java/com/tribly/api/rides/RideTemplateResource.java

### Frontend Implementation for User Story 8

- [ ] T170 [US8] Create TemplateListPage in frontend/src/pages/ride/TemplateListPage.tsx
- [ ] T171 [US8] Create TemplateSelector component in frontend/src/components/ride/TemplateSelector.tsx
- [ ] T172 [US8] Add "Save as Template" to CreateRidePage
- [ ] T173 [US8] Implement auto-increment counter for template naming

**Checkpoint**: US8 complete - ride creation efficiency improved

---

## Phase 11: User Story 9 - External Integrations (Priority: P4)

**Goal**: Teams can configure webhooks for Mattermost and custom endpoints

**Independent Test**: Configure webhook, publish ride, verify external post

### Tests for User Story 9

- [ ] T174 [P] [US9] Integration test for webhook delivery in backend/src/test/java/com/tribly/integration/WebhookServiceTest.java

### Backend Implementation for User Story 9

- [ ] T175 [US9] Create TeamWebhook entity in backend/src/main/java/com/tribly/domain/team/TeamWebhook.java
- [ ] T176 [US9] Create WebhookRepository in backend/src/main/java/com/tribly/domain/team/WebhookRepository.java
- [ ] T177 [US9] Implement WebhookService in backend/src/main/java/com/tribly/service/integration/WebhookService.java
- [ ] T178 [US9] Implement MattermostFormatter in backend/src/main/java/com/tribly/service/integration/MattermostFormatter.java
- [ ] T179 [US9] Add webhook trigger to RideService on publish
- [ ] T180 [US9] Implement WebhookResource in backend/src/main/java/com/tribly/api/teams/WebhookResource.java

### Frontend Implementation for User Story 9

- [ ] T181 [US9] Create IntegrationSettingsPage in frontend/src/pages/team/IntegrationSettingsPage.tsx
- [ ] T182 [US9] Create WebhookConfigForm component in frontend/src/components/team/WebhookConfigForm.tsx
- [ ] T183 [US9] Create WebhookTestButton component

**Checkpoint**: US9 complete - external integrations enabled

---

## Phase 12: User Story 10 - System Administration (Priority: P4)

**Goal**: Platform admins can manage users and teams across the system

**Independent Test**: Access admin panel, perform user/team management operations

### Tests for User Story 10

- [ ] T184 [P] [US10] Contract test for admin endpoints in backend/src/test/java/com/tribly/contract/AdminContractTest.java
- [ ] T185 [P] [US10] Integration test for admin operations in backend/src/test/java/com/tribly/integration/AdminServiceTest.java

### Backend Implementation for User Story 10

- [ ] T186 [US10] Implement AdminService in backend/src/main/java/com/tribly/service/admin/AdminService.java
- [ ] T187 [US10] Implement UserMergeService in backend/src/main/java/com/tribly/service/admin/UserMergeService.java
- [ ] T188 [US10] Implement AdminResource REST endpoints in backend/src/main/java/com/tribly/api/admin/AdminResource.java
- [ ] T189 [US10] Create SystemSettings entity in backend/src/main/java/com/tribly/domain/admin/SystemSettings.java
- [ ] T190 [US10] Implement admin role check filter

### Frontend Implementation for User Story 10

- [ ] T191 [P] [US10] Create AdminDashboard in frontend/src/pages/admin/AdminDashboard.tsx
- [ ] T192 [P] [US10] Create AdminUserList in frontend/src/pages/admin/AdminUserList.tsx
- [ ] T193 [P] [US10] Create AdminTeamList in frontend/src/pages/admin/AdminTeamList.tsx
- [ ] T194 [US10] Create UserMergeDialog in frontend/src/components/admin/UserMergeDialog.tsx
- [ ] T195 [US10] Create SystemSettingsPage in frontend/src/pages/admin/SystemSettingsPage.tsx

**Checkpoint**: US10 complete - platform administration enabled

---

## Phase 13: Notifications System (Cross-Cutting)

**Purpose**: In-app and email notifications for all user stories

- [ ] T196 [P] Create Notification entity in backend/src/main/java/com/tribly/domain/notification/Notification.java
- [ ] T197 Create NotificationRepository in backend/src/main/java/com/tribly/domain/notification/NotificationRepository.java
- [ ] T198 Implement NotificationService in backend/src/main/java/com/tribly/service/notification/NotificationService.java
- [ ] T199 Implement EmailService in backend/src/main/java/com/tribly/infrastructure/email/EmailService.java
- [ ] T200 Implement NotificationResource in backend/src/main/java/com/tribly/api/notifications/NotificationResource.java
- [ ] T201 Create NotificationBell component in frontend/src/components/common/NotificationBell.tsx
- [ ] T202 Create NotificationList component in frontend/src/components/common/NotificationList.tsx
- [ ] T203 Integrate notification triggers in RideService
- [ ] T204 Integrate notification triggers in TripService
- [ ] T205 Integrate notification triggers in MessageService

---

## Phase 14: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T206 [P] Add loading states to all pages
- [ ] T207 [P] Add error handling with user-friendly messages
- [ ] T208 Implement responsive design for mobile (320px+)
- [ ] T209 Add WCAG 2.1 AA accessibility improvements
- [ ] T210 [P] Add skeleton loaders for data fetching
- [ ] T211 Performance optimization: add database indexes per data-model.md
- [ ] T212 [P] Add Sentry error tracking integration
- [ ] T213 Create comprehensive API documentation in Swagger UI
- [ ] T214 Security hardening: rate limiting, CSRF protection
- [ ] T215 Run quickstart.md validation - verify all setup steps work

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-12)**: All depend on Foundational phase completion
  - US1-3 (P1): Can proceed in parallel after Foundational
  - US4-5 (P2): Can proceed after Foundational (ideally after US1-3)
  - US6-7 (P3): Can proceed after Foundational (ideally after US3-4)
  - US8-10 (P4): Can proceed after Foundational (ideally after US3)
- **Notifications (Phase 13)**: Depends on US1-US6 entities existing
- **Polish (Phase 14)**: Depends on all desired user stories being complete

### User Story Dependencies

| Story | Depends On | Can Start After |
|-------|------------|-----------------|
| US1 (Auth) | Foundational | Phase 2 complete |
| US2 (Teams) | Foundational | Phase 2 complete |
| US3 (Rides) | US2 (Team entity) | Phase 4 or parallel with US2 |
| US4 (Routes) | Foundational | Phase 2 complete |
| US5 (Trips) | US3 (Ride entity), US4 (Route entity) | Phase 5-6 or after US3 |
| US6 (Comments) | US3, US5 (Ride/Trip entities) | Phase 5-7 |
| US7 (Catalog) | US4 (Route entity) | Phase 6 |
| US8 (Templates) | US3 (Ride entity) | Phase 5 |
| US9 (Webhooks) | US2 (Team entity) | Phase 4 |
| US10 (Admin) | US1, US2 | Phase 3-4 |

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Entities before repositories
- Repositories before services
- Services before resources/endpoints
- Backend before frontend for same feature
- Core implementation before integration

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational completes:
  - US1, US2, US4 can all start in parallel
  - US3 can start immediately if US2 entities are done
- Within each story, all [P] tasks can run in parallel
- Different user stories can be worked on by different team members

---

## Parallel Example: Phase 5 (User Story 3)

```bash
# Launch all tests for User Story 3 together:
Task: "Contract test for ride endpoints in backend/src/test/java/com/tribly/contract/RideContractTest.java"
Task: "Integration test for ride CRUD in backend/src/test/java/com/tribly/integration/RideServiceTest.java"
Task: "E2E test for ride creation flow in e2e/tests/ride.spec.ts"

# Launch all entities for User Story 3 together:
Task: "Create Place entity in backend/src/main/java/com/tribly/domain/place/Place.java"
Task: "Create Ride entity in backend/src/main/java/com/tribly/domain/ride/Ride.java"
Task: "Create RideGroup entity in backend/src/main/java/com/tribly/domain/ride/RideGroup.java"
Task: "Create RideParticipation entity in backend/src/main/java/com/tribly/domain/ride/RideParticipation.java"

# Launch all frontend pages together:
Task: "Create RideListPage in frontend/src/pages/ride/RideListPage.tsx"
Task: "Create RideDetailPage in frontend/src/pages/ride/RideDetailPage.tsx"
Task: "Create CreateRidePage in frontend/src/pages/ride/CreateRidePage.tsx"
```

---

## Implementation Strategy

### MVP First (User Stories 1-3 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Authentication)
4. Complete Phase 4: User Story 2 (Teams)
5. Complete Phase 5: User Story 3 (Rides)
6. **STOP and VALIDATE**: Test MVP independently
7. Deploy/demo: Users can authenticate, create teams, organize rides

### Incremental Delivery

1. **MVP** (US1-3) → Users can organize rides → Deploy
2. **+Routes** (US4) → Routes shareable → Deploy
3. **+Trips** (US5) → Multi-day events → Deploy
4. **+Communication** (US6) → Team discussions → Deploy
5. **+Discovery** (US7) → Public catalog → Deploy
6. **+Efficiency** (US8-9) → Templates & integrations → Deploy
7. **+Operations** (US10) → Admin capabilities → Deploy

### Parallel Team Strategy

With 3 developers after Foundational:
- Developer A: US1 (Auth) → US4 (Routes) → US7 (Catalog)
- Developer B: US2 (Teams) → US3 (Rides) → US8 (Templates)
- Developer C: US5 (Trips) → US6 (Comments) → US9-10 (Integrations/Admin)

---

## Summary

| Metric | Count |
|--------|-------|
| **Total Tasks** | 215 |
| **Setup Tasks** | 12 |
| **Foundational Tasks** | 21 |
| **US1 Tasks** | 16 |
| **US2 Tasks** | 20 |
| **US3 Tasks** | 24 |
| **US4 Tasks** | 26 |
| **US5 Tasks** | 19 |
| **US6 Tasks** | 13 |
| **US7 Tasks** | 12 |
| **US8 Tasks** | 10 |
| **US9 Tasks** | 10 |
| **US10 Tasks** | 12 |
| **Notifications Tasks** | 10 |
| **Polish Tasks** | 10 |
| **Parallelizable Tasks** | 89 (~41%) |

### MVP Scope

**Recommended MVP**: Complete US1 + US2 + US3 (72 tasks)
- Users can authenticate via Strava
- Users can create and join teams
- Teams can create and manage rides with groups
- Members can register for rides

---

## Notes

- [P] tasks = different files, no dependencies within phase
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
