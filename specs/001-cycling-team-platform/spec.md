# Feature Specification: Cycling Team Management Platform

**Feature Branch**: `001-cycling-team-platform`
**Created**: 2025-12-10
**Status**: Draft
**Input**: User description: "Multi-tenant web platform for cycling teams to organize rides, trips, manage routes with GPX/maps, and communicate"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - User Authentication via Strava (Priority: P1)

A cyclist wants to sign in to the platform using their existing Strava account so they can quickly access the platform without creating yet another account, and their cycling identity is already established.

**Why this priority**: Authentication is the foundation—no other feature works without users being able to sign in. Strava is the primary identity provider in the cycling ecosystem.

**Independent Test**: Can be fully tested by completing OAuth flow with Strava and verifying user profile creation. Delivers immediate value: users can access the platform.

**Acceptance Scenarios**:

1. **Given** a user with a Strava account visits the platform, **When** they click "Sign in with Strava", **Then** they are redirected to Strava's authorization page
2. **Given** a user authorizes the application on Strava, **When** Strava redirects back, **Then** the user is signed in and their profile is created with their Strava name, profile picture, and Strava ID
3. **Given** a returning user, **When** they sign in again, **Then** their existing profile is used (not duplicated)
4. **Given** a signed-in user, **When** they choose to sign out, **Then** their session ends and they must re-authenticate to access protected content

---

### User Story 2 - Create and Join a Team (Priority: P1)

A cycling club organizer wants to create a team space for their club so members can access shared rides, routes, and communications in one place.

**Why this priority**: Teams are the core multi-tenancy unit. Without teams, users have no context to organize content.

**Independent Test**: Can be tested by creating a team, configuring visibility, and having another user join. Delivers value: club has its digital home.

**Acceptance Scenarios**:

1. **Given** a signed-in user, **When** they create a new team with name "Vélo Club Paris" and slug "vcp", **Then** the team is created and they become its administrator
2. **Given** a team marked as "Public", **When** any user searches for teams, **Then** the team appears in search results
3. **Given** a public team, **When** a signed-in user clicks "Join", **Then** they become a member of that team
4. **Given** a team administrator, **When** they promote a member to admin, **Then** that member gains administrative privileges
5. **Given** a private team, **When** an unauthenticated user tries to access it, **Then** they see an access denied message

---

### User Story 3 - Create and Publish a Ride (Priority: P1)

A team administrator wants to create a Saturday group ride with multiple pace groups so that cyclists of different abilities can participate at their comfort level.

**Why this priority**: Rides are the primary use case—the reason teams exist. This is the core value proposition of the platform.

**Independent Test**: Can be tested by creating a ride with groups, publishing it, and verifying team members can see and register. Delivers immediate value: team can organize its first ride.

**Acceptance Scenarios**:

1. **Given** a team administrator, **When** they create a ride with title, date, start/end points, and multiple groups (Fast 30km/h, Moderate 25km/h, Leisure 20km/h), **Then** the ride is saved as a draft
2. **Given** a draft ride, **When** the administrator publishes it, **Then** team members receive notifications (in-app and optionally email)
3. **Given** a published ride, **When** a team member views it, **Then** they see all groups with their meeting times, speeds, and participant counts
4. **Given** a published ride, **When** a user registers for a group, **Then** they appear in that group's participant list
5. **Given** a ride the user is registered for, **When** they choose to unregister, **Then** they are removed from the participant list

---

### User Story 4 - Upload and View Routes (Priority: P2)

A team member wants to upload a GPX file from their cycling computer or route planner so the route can be shared with the team and attached to rides.

**Why this priority**: Routes are essential for rides but can function independently as a shareable resource.

**Independent Test**: Can be tested by uploading a GPX file and verifying map display, elevation profile, and statistics. Delivers value: routes become shareable team resources.

**Acceptance Scenarios**:

1. **Given** a team member, **When** they upload a valid GPX file, **Then** the route is parsed and displayed on an interactive map
2. **Given** an uploaded route, **When** viewing it, **Then** the user sees distance (km), positive elevation, negative elevation, and elevation profile chart
3. **Given** a route, **When** the system analyzes it, **Then** climbs are automatically detected with gradient segments color-coded (green=easy, red=steep)
4. **Given** a route, **When** a user wants to download it, **Then** they can choose GPX or FIT format
5. **Given** a user with a linked Garmin account, **When** they click "Send to Garmin", **Then** the route is uploaded to their Garmin Connect account

---

### User Story 5 - Plan a Multi-Day Trip (Priority: P2)

A team organizer wants to plan a 3-day cycling trip with daily stages so participants can see the full itinerary with routes, distances, and elevations for each day.

**Why this priority**: Trips extend the platform's value beyond single rides to longer cycling events, differentiating from simple calendar tools.

**Independent Test**: Can be tested by creating a trip with multiple stages, each with an attached route, and verifying totals calculation. Delivers value: teams can plan cycling vacations together.

**Acceptance Scenarios**:

1. **Given** a team administrator, **When** they create a trip with start/end dates and route type (Road/Gravel/MTB), **Then** the trip is created with a stages container
2. **Given** a trip, **When** the administrator adds stages with dates, names, and routes, **Then** total distance and elevation are automatically calculated
3. **Given** a published trip, **When** a team member views it, **Then** they see all stages with individual and cumulative statistics
4. **Given** a trip with notes, **When** viewing details, **Then** markdown-formatted planning information (logistics, accommodation) is displayed
5. **Given** a trip, **When** a user registers as participant, **Then** they appear in the participant list and receive trip-related notifications

---

### User Story 6 - Comment on Rides and Trips (Priority: P3)

A ride participant wants to ask a question about the meeting point or share excitement about an upcoming ride so the team can communicate contextually.

**Why this priority**: Communication enhances engagement but the platform functions without it.

**Independent Test**: Can be tested by posting a comment, replying, and verifying threaded display. Delivers value: contextual team communication.

**Acceptance Scenarios**:

1. **Given** a user viewing a ride they're participating in, **When** they post a message, **Then** the message appears with their name and timestamp
2. **Given** an existing message, **When** a user replies, **Then** the reply appears threaded under the original
3. **Given** a message the user posted, **When** they delete it, **Then** it is removed from the thread
4. **Given** any message, **When** a team administrator views it, **Then** they have the option to delete it
5. **Given** a new message on a ride, **When** it's posted, **Then** other participants receive notifications

---

### User Story 7 - Browse Public Route Catalog (Priority: P3)

A cyclist visiting the platform wants to discover new routes in their area without joining a team, using filters for distance, elevation, and route type.

**Why this priority**: Public catalog drives organic discovery and user acquisition but requires routes to exist first.

**Independent Test**: Can be tested by searching with filters and verifying matching routes appear. Delivers value: platform becomes a route discovery tool.

**Acceptance Scenarios**:

1. **Given** an anonymous user, **When** they access the public map catalog, **Then** they see routes from teams marked as public
2. **Given** the catalog, **When** the user filters by location (city + radius), **Then** only routes within that area appear
3. **Given** the catalog, **When** the user filters by distance range (50-100km) and elevation (>1000m), **Then** matching routes are displayed
4. **Given** search results, **When** the user sorts by rating, **Then** highest-rated routes appear first
5. **Given** a route in the catalog, **When** a signed-in user clicks "Add to Favorites", **Then** it appears in their personal favorites collection

---

### User Story 8 - Ride Templates for Recurring Events (Priority: P4)

A team administrator wants to save their weekly Saturday ride configuration as a template so creating next week's ride takes seconds instead of minutes.

**Why this priority**: Templates improve efficiency but require the ride feature to be mature first.

**Independent Test**: Can be tested by saving a template, creating a ride from it, and verifying pre-filled values. Delivers value: significant time savings for active teams.

**Acceptance Scenarios**:

1. **Given** a team administrator creating a ride, **When** they choose "Save as Template", **Then** all current settings (groups, places, type) are saved
2. **Given** a saved template with counter enabled, **When** creating a new ride from it, **Then** the title auto-increments (e.g., "Saturday Ride #43")
3. **Given** a template, **When** used to create a ride, **Then** all groups, meeting times, and speeds are pre-filled
4. **Given** multiple templates, **When** viewing template list, **Then** administrators can edit or delete templates

---

### User Story 9 - External Integrations (Priority: P4)

A team wants to automatically post new rides to their Mattermost channel so members who don't check the platform regularly still see announcements.

**Why this priority**: Integrations extend reach but are additive to core functionality.

**Independent Test**: Can be tested by configuring webhook and publishing a ride, verifying the external post. Delivers value: meets users where they already are.

**Acceptance Scenarios**:

1. **Given** a team administrator, **When** they configure a Mattermost webhook URL, **Then** it is saved in team settings
2. **Given** a configured integration, **When** a ride is published, **Then** a formatted message is posted to the configured channel
3. **Given** integration settings, **When** an administrator configures separate channels for rides vs. comments, **Then** messages route appropriately
4. **Given** a generic webhook URL configured, **When** content is published, **Then** a JSON payload is sent to the URL

---

### User Story 10 - System Administration (Priority: P4)

A platform administrator needs to manage users across all teams, handle account merges, and configure system-wide settings.

**Why this priority**: Admin features are essential for operations but don't directly serve end users.

**Independent Test**: Can be tested by accessing admin panel and performing user/team management operations. Delivers value: platform can be maintained.

**Acceptance Scenarios**:

1. **Given** a system administrator, **When** they access the admin panel, **Then** they see all users across all teams
2. **Given** duplicate user accounts (same person, different auth providers), **When** admin initiates merge, **Then** content is consolidated under one account
3. **Given** the admin panel, **When** viewing teams, **Then** admin can delete teams or modify their visibility
4. **Given** system settings, **When** admin configures legal pages (terms, privacy), **Then** these appear in the application footer

---

### Edge Cases

- What happens when a user's Strava account is deleted? System retains local profile with "Strava Disconnected" status; user can link different Strava account
- How does the system handle malformed GPX files? Validation errors are displayed with specific issues; file is rejected but user can retry
- What happens when a ride date passes? Ride moves to "Past" status; registrations are preserved for historical reference; editing is disabled
- How does the system handle concurrent registrations filling a group? First-come-first-served; late registrations fail gracefully with "Group Full" message
- What happens when an administrator is the last admin and tries to leave? System prevents this; they must promote another member first
- How does the system handle very large GPX files (>10MB)? Files are processed asynchronously; user sees progress indicator; timeout after reasonable duration

## Requirements *(mandatory)*

### Functional Requirements

**Authentication & Users**
- **FR-001**: System MUST support OAuth2 authentication with Strava as required provider
- **FR-002**: System MUST support OAuth2 authentication with Google and Facebook as optional providers
- **FR-003**: System MUST allow users to link their Garmin account for route synchronization
- **FR-004**: Users MUST be able to edit their profile (name, email, picture, location)
- **FR-005**: Users MUST be able to permanently delete their account with all associated data
- **FR-006**: Users MUST be able to configure email notification preferences (new rides, trips, publications)

**Teams**
- **FR-007**: Users MUST be able to create teams with unique URL-friendly slugs
- **FR-008**: Teams MUST support five visibility levels: Public, Public Unlisted, Private, Private Unlisted, User Space
- **FR-009**: Team administrators MUST be able to manage members (add, remove, promote, demote)
- **FR-010**: Teams MUST support profile information: description, contact details, social links, logo
- **FR-011**: Teams MUST have configurable timezone for scheduling
- **FR-012**: System MUST support custom domains for teams; when accessed via a custom domain, only the associated team's content is displayed (single-team view, no platform navigation to other teams)

**Rides**
- **FR-013**: Team members MUST be able to create rides with title, description, date, type, and meeting points
- **FR-014**: Rides MUST support multiple groups with different meeting times, target speeds, and optional capacity limits
- **FR-015**: Each ride group MAY have an associated route/map
- **FR-016**: Rides MUST support Draft and Published statuses
- **FR-017**: Users MUST be able to register/unregister for specific ride groups
- **FR-018**: Registration for a ride MUST automatically add user to team membership if not already a member
- **FR-019**: Ride templates MUST be saveable with auto-increment naming counters

**Trips**
- **FR-020**: Team members MUST be able to create multi-day trips with start/end dates
- **FR-021**: Trips MUST support multiple stages, each with date, name, and route
- **FR-022**: System MUST automatically calculate total distance and elevation from stage routes
- **FR-023**: Trips MUST support markdown notes for planning information
- **FR-024**: Trips MAY be published to a public catalog

**Routes/Maps**
- **FR-025**: System MUST accept GPX file uploads and parse route data
- **FR-026**: System MUST support route import from URL and OpenRunner ID
- **FR-027**: Routes MUST display on interactive maps with elevation profiles
- **FR-028**: System MUST automatically calculate distance, elevation gain/loss, and detect climbs
- **FR-029**: Climbs MUST be visualized with gradient-based color coding
- **FR-030**: Users MUST be able to download routes in GPX and FIT formats
- **FR-031**: Users MUST be able to upload routes directly to linked Garmin Connect accounts
- **FR-032**: Users MUST be able to rate routes (1-5 stars) and view average ratings
- **FR-033**: Users MUST be able to save routes to personal favorites
- **FR-034**: System MUST support GPX merging tool combining two routes

**Places**
- **FR-035**: Teams MUST be able to define reusable meeting places with name, address, and coordinates
- **FR-036**: Places MUST be categorizable as start points and/or end points

**Communication**
- **FR-037**: Users MUST be able to post comments on rides and trips
- **FR-038**: Comments MUST support threaded replies
- **FR-039**: Users MUST be able to delete their own comments
- **FR-040**: Team administrators MUST be able to delete any comment in their team

**Notifications**
- **FR-041**: System MUST provide in-app notifications for new messages, rides, and trips
- **FR-042**: System MUST send email notifications based on user preferences

**Public Catalog**
- **FR-043**: System MUST provide a public route catalog browsable by anonymous users
- **FR-044**: Catalog MUST support filtering by location (address + radius), distance, elevation, type, and tags
- **FR-045**: Catalog MUST support sorting by distance, elevation, and rating
- **FR-046**: System MUST provide a public trip catalog for published trips

**Publications**
- **FR-047**: Teams MUST be able to create announcements with title, rich text, and optional cover image
- **FR-048**: Publications MAY enable event registration with name/email and email verification

**Integrations**
- **FR-049**: Teams MUST be able to configure Mattermost webhooks for automatic posting
- **FR-050**: Teams MUST be able to configure custom webhook URLs for rides, trips, and publications

**Administration**
- **FR-051**: System administrators MUST be able to view and manage all users and teams
- **FR-052**: System administrators MUST be able to merge duplicate user accounts
- **FR-053**: System administrators MUST be able to configure legal pages and terms

**Data Management**
- **FR-054**: All major entities MUST use soft deletion with a deletion flag
- **FR-055**: System MUST provide SEO-friendly, shareable permalinks for all content

### Key Entities

- **User**: Person who can authenticate, join multiple teams simultaneously, participate in rides/trips. Has profile, preferences, linked OAuth accounts, and personal workspace.

- **Team**: Multi-tenant container for cycling club content. Has visibility settings, membership list with roles, configuration, optional custom domain (single-team view when accessed via domain), and owns rides/trips/routes/places/publications.

- **Ride**: Single-day cycling event with date, meeting points, and multiple pace groups. Belongs to a team, has participants per group.

- **RideGroup**: Pace group within a ride with meeting time, target speed, optional route, optional capacity limit, and participant list.

- **RideTemplate**: Reusable configuration for creating rides quickly. Belongs to a team.

- **Trip**: Multi-day cycling event with date range, stages, and aggregated statistics. Has participants and markdown notes.

- **TripStage**: Single day/segment of a trip with date, name, route, and optional alternative route.

- **Route/Map**: GPX-based cycling route with calculated metrics (distance, elevation, climbs), type classification, ratings. Strictly owned by one team (no cross-team sharing).

- **Place**: Reusable meeting point with name, address, coordinates, and start/end classification. Belongs to a team.

- **Publication**: Team announcement with rich text content, optional event registration. Belongs to a team.

- **Comment/Message**: User-generated content attached to rides or trips. Supports threading.

- **Notification**: In-app alert for user about relevant activity. Has read/unread status.

- **Favorite**: User's bookmarked route for quick access.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: New users can complete sign-in via Strava in under 30 seconds
- **SC-002**: Team administrators can create and publish a ride with 3 groups in under 3 minutes
- **SC-003**: GPX files up to 5MB are processed and displayed in under 10 seconds
- **SC-004**: 95% of route uploads successfully parse and display on first attempt
- **SC-005**: Public catalog search returns relevant results in under 2 seconds
- **SC-006**: Platform supports 100+ teams with 500+ members each without performance degradation
- **SC-007**: Users can discover and join a public team in under 1 minute
- **SC-008**: 90% of users successfully register for a ride on their first attempt
- **SC-009**: Multi-day trip total statistics (distance, elevation) calculate within 1 second of stage changes
- **SC-010**: Email notifications are delivered within 5 minutes of triggering events
- **SC-011**: Mobile users can complete all primary tasks (sign in, view ride, register) on devices with screen width 320px+
- **SC-012**: Route downloads (GPX/FIT) initiate within 2 seconds of request

## Clarifications

### Session 2025-12-10

- Q: Should ride groups have participant capacity limits? → A: Capacity limits are optional, configurable per group
- Q: Can routes be shared across teams? → A: Routes are strictly owned by one team, no sharing
- Q: Can a user belong to multiple teams simultaneously? → A: Yes, users can belong to multiple teams
- Q: How do custom domains work for teams? → A: Each team can have its own domain; when accessed via that domain, only the associated team's content is displayed (single-team view)

## Assumptions

- Strava API remains available and maintains current OAuth2 flow compatibility
- Users have valid Strava accounts to authenticate (required provider)
- GPX files follow standard format (GPX 1.1); non-standard extensions may not be processed
- Garmin Connect API is available for route upload integration
- Email delivery depends on properly configured email service
- Map tiles are provided by standard tile providers (OpenStreetMap or similar)
- Users accept standard web session cookie behavior for "Remember Me" functionality
- Default data retention follows standard practices (soft-deleted data purged after 30 days)
- Default performance targets assume standard web hosting infrastructure
