# UAT Test Scenarios for Tribly

## Overview

Comprehensive User Acceptance Test scenarios for the Tribly cycling team platform. Organized by **shared behaviors** (consolidated cross-cutting concerns) followed by **entity-specific** scenarios.

---

## Part 1: Shared Behavior Scenarios

These scenarios test behaviors common across multiple entities, written once and applicable to Rides, Posts, Trips, Routes, and Teams.

---

### SB-1: Content Media Management

**Applies to**: Team, Ride, Post, Trip, Route, TripStage

| ID     | Scenario                            | Steps                                                                                  | Expected Result                                      |
| ------ | ----------------------------------- | -------------------------------------------------------------------------------------- | ---------------------------------------------------- |
| SB-1.1 | Create entity with markdown content | 1. Create entity with markdown text<br>2. Save entity<br>3. View entity detail         | Markdown content displayed correctly with formatting |
| SB-1.2 | Upload logo image                   | 1. Create/edit entity<br>2. Upload logo via MediaEditor<br>3. Save                     | Logo displayed in entity header                      |
| SB-1.3 | Upload multiple images              | 1. Edit entity<br>2. Add 3 images to content<br>3. Save                                | All images stored and displayed in content           |
| SB-1.4 | Upload attachment file              | 1. Edit entity<br>2. Add PDF/document attachment<br>3. Save                            | Attachment downloadable from entity                  |
| SB-1.5 | Update existing media               | 1. Edit entity with existing media<br>2. Replace logo<br>3. Update markdown<br>4. Save | New media replaces old, markdown updated             |
| SB-1.6 | Remove media assets                 | 1. Edit entity with assets<br>2. Remove one image<br>3. Save                           | Asset removed, others preserved                      |

---

### SB-2: Asset Upload & Management

**Applies to**: All content entities via MediaEditor

| ID     | Scenario             | Steps                                                                        | Expected Result                                       |
| ------ | -------------------- | ---------------------------------------------------------------------------- | ----------------------------------------------------- |
| SB-2.1 | Upload image asset   | 1. Open MediaEditor<br>2. Click upload<br>3. Select image file<br>4. Confirm | Image uploaded with progress indicator, preview shown |
| SB-2.2 | Reorder assets       | 1. Upload multiple assets<br>2. Drag to reorder<br>3. Save                   | Assets saved in new order                             |
| SB-2.3 | Delete asset         | 1. View asset in editor<br>2. Click delete<br>3. Confirm                     | Asset removed from entity                             |
| SB-2.4 | Asset type detection | 1. Upload logo (placed in logo field)<br>2. Upload image (placed in content) | Correct AssetType assigned (LOGO vs IMAGE)            |

---

### SB-3: Status Workflow (Publications)

**Applies to**: Ride, Post, Trip

| ID     | Scenario                     | Steps                                                                   | Expected Result                                 |
| ------ | ---------------------------- | ----------------------------------------------------------------------- | ----------------------------------------------- |
| SB-3.1 | Create as Draft              | 1. Create new publication<br>2. Leave status as Draft<br>3. Save        | Entity created with DRAFT status                |
| SB-3.2 | Publish Draft                | 1. View draft entity<br>2. Click "Publish"<br>3. Confirm                | Status changes to PUBLISHED, visible to members |
| SB-3.3 | Unpublish to Draft           | 1. View published entity<br>2. Click "Unpublish"<br>3. Confirm          | Status returns to DRAFT                         |
| SB-3.4 | Cancel publication           | 1. View published entity<br>2. Click "Cancel"<br>3. Confirm             | Status changes to CANCELLED                     |
| SB-3.5 | Uncancel publication         | 1. View cancelled entity<br>2. Click "Uncancel"<br>3. Confirm           | Status returns to DRAFT                         |
| SB-3.6 | Schedule publication         | 1. Create entity<br>2. Set publishAt to future date<br>3. Save as Draft | Entity auto-publishes at scheduled time         |
| SB-3.7 | Draft visibility (organizer) | 1. Login as organizer<br>2. Navigate to team<br>3. View drafts          | Drafts visible in list                          |
| SB-3.8 | Draft visibility (member)    | 1. Login as member<br>2. Navigate to team<br>3. View publications       | Drafts NOT visible in list                      |

---

### SB-4: Visibility Patterns

**Applies to**: Team, Ride, Post, Trip, Route

| ID     | Scenario                  | Steps                                                        | Expected Result                     |
| ------ | ------------------------- | ------------------------------------------------------------ | ----------------------------------- |
| SB-4.1 | Create PUBLIC entity      | 1. Create entity<br>2. Set visibility PUBLIC<br>3. Save      | Entity visible to non-members       |
| SB-4.2 | Create TEAM entity        | 1. Create entity<br>2. Set visibility TEAM<br>3. Save        | Entity visible only to team members |
| SB-4.3 | View PUBLIC as non-member | 1. Logout/switch user<br>2. Navigate to public entity URL    | Entity details displayed            |
| SB-4.4 | View TEAM as non-member   | 1. Logout/switch user<br>2. Navigate to team-only entity URL | Access denied or redirect           |
| SB-4.5 | Change visibility         | 1. Edit PUBLIC entity<br>2. Change to TEAM<br>3. Save        | Entity now restricted to members    |

---

### SB-5: Slug Generation

**Applies to**: Team, Ride, Post, Trip, Route

| ID     | Scenario                         | Steps                                                                                   | Expected Result                                 |
| ------ | -------------------------------- | --------------------------------------------------------------------------------------- | ----------------------------------------------- |
| SB-5.1 | Auto-generate slug from name     | 1. Create entity with name "Sunday Morning Ride"<br>2. Save                             | Slug generated as "sunday-morning-ride"         |
| SB-5.2 | Handle special characters        | 1. Create entity with name "Ride #1 - Été 2024!"<br>2. Save                             | Slug sanitized (alphanumeric + hyphens only)    |
| SB-5.3 | Handle duplicate slugs           | 1. Create entity "Test"<br>2. Create another entity "Test" in same team<br>3. Save both | Second entity gets unique slug (e.g., "test-1") |
| SB-5.4 | Same slug different entity types | 1. Create ride "Sunday"<br>2. Create post "Sunday" in same team                         | Both entities can have slug "sunday"            |

---

### SB-6: Soft Delete

**Applies to**: All entities

| ID     | Scenario              | Steps                                                        | Expected Result                    |
| ------ | --------------------- | ------------------------------------------------------------ | ---------------------------------- |
| SB-6.1 | Delete entity         | 1. View entity<br>2. Click Delete<br>3. Confirm in dialog    | Entity removed from lists          |
| SB-6.2 | Deleted not in search | 1. Delete entity<br>2. Search for entity by name             | Deleted entity not in results      |
| SB-6.3 | Deleted URL access    | 1. Copy entity URL<br>2. Delete entity<br>3. Navigate to URL | 404 Not Found or appropriate error |

---

### SB-7: Pagination & Search

**Applies to**: Ride list, Post list, Trip list, Route list, Publications, Members

| ID     | Scenario              | Steps                                          | Expected Result                          |
| ------ | --------------------- | ---------------------------------------------- | ---------------------------------------- |
| SB-7.1 | View first page       | 1. Navigate to list page<br>2. Default view    | First 20 items displayed with pagination |
| SB-7.2 | Navigate to next page | 1. View list with >20 items<br>2. Click "Next" | Second page of results shown             |
| SB-7.3 | Search by name        | 1. Enter search term<br>2. Submit              | Results filtered to matching entities    |
| SB-7.4 | Clear search          | 1. Perform search<br>2. Clear search field     | Full list restored                       |
| SB-7.5 | Empty search results  | 1. Search for non-existent term                | "No results" message displayed           |

---

### SB-8: Form Validation

**Applies to**: All create/edit forms

| ID     | Scenario                  | Steps                                                         | Expected Result                      |
| ------ | ------------------------- | ------------------------------------------------------------- | ------------------------------------ |
| SB-8.1 | Required field validation | 1. Open create form<br>2. Leave name empty<br>3. Submit       | Error message on name field          |
| SB-8.2 | Max length validation     | 1. Enter name with 300+ characters<br>2. Submit               | Error message about max length (255) |
| SB-8.3 | Valid submission          | 1. Fill all required fields correctly<br>2. Submit            | Entity created, redirect to detail   |
| SB-8.4 | Cancel form               | 1. Open create form<br>2. Fill some fields<br>3. Click Cancel | Redirect to list, no entity created  |

---

## Part 2: Entity-Specific Scenarios

---

### E-TEAM: Team Management

| ID       | Scenario             | Steps                                                                               | Expected Result                    |
| -------- | -------------------- | ----------------------------------------------------------------------------------- | ---------------------------------- |
| E-TEAM.1 | Create team          | 1. Click "Create Team"<br>2. Fill name, description<br>3. Set visibility<br>4. Save | Team created, user is ADMIN        |
| E-TEAM.2 | Edit team settings   | 1. Go to team settings<br>2. Update name<br>3. Save                                 | Team name updated                  |
| E-TEAM.3 | Enable/disable trips | 1. Go to team settings<br>2. Toggle enableTrips<br>3. Save                          | Trips tab shown/hidden accordingly |
| E-TEAM.4 | Add team member      | 1. Go to Members<br>2. Search user<br>3. Add as MEMBER                              | User added to team                 |
| E-TEAM.5 | Change member role   | 1. Go to Members<br>2. Change user role to ORGANIZER<br>3. Save                     | Role updated                       |
| E-TEAM.6 | Remove team member   | 1. Go to Members<br>2. Click remove on user<br>3. Confirm                           | User removed from team             |
| E-TEAM.7 | Join public team     | 1. View public team as non-member<br>2. Click "Join"<br>3. Confirm                  | User becomes MEMBER                |
| E-TEAM.8 | Leave team           | 1. View team as member<br>2. Click "Leave"<br>3. Confirm                            | User removed from team             |
| E-TEAM.9 | Delete team          | 1. Go to settings as ADMIN<br>2. Click "Delete Team"<br>3. Confirm                  | Team and all content deleted       |

---

### E-PLACE: Place Management

| ID        | Scenario          | Steps                                                                                    | Expected Result                             |
| --------- | ----------------- | ---------------------------------------------------------------------------------------- | ------------------------------------------- |
| E-PLACE.1 | Create place      | 1. Go to team settings<br>2. Add Place<br>3. Enter name, address, coordinates<br>4. Save | Place created and available in autocomplete |
| E-PLACE.2 | Edit place        | 1. Select existing place<br>2. Update address<br>3. Save                                 | Place updated                               |
| E-PLACE.3 | Delete place      | 1. Select place<br>2. Click delete<br>3. Confirm                                         | Place removed                               |
| E-PLACE.4 | Mark as start/end | 1. Edit place<br>2. Toggle startPlace/endPlace flags<br>3. Save                          | Place filtered in appropriate contexts      |

---

### E-RIDE: Ride Management

| ID        | Scenario                   | Steps                                                                              | Expected Result                                  |
| --------- | -------------------------- | ---------------------------------------------------------------------------------- | ------------------------------------------------ |
| E-RIDE.1  | Create ride with date/time | 1. Create ride<br>2. Set date/time<br>3. Save                                      | Ride displays correct date/time                  |
| E-RIDE.2  | Add ride group             | 1. Edit ride<br>2. Add group "Fast Pace"<br>3. Set average speed 28km/h<br>4. Save | Group shown with pace info                       |
| E-RIDE.3  | Set group max participants | 1. Edit ride group<br>2. Set maxParticipants to 10<br>3. Save                      | Capacity shown, enforced on join                 |
| E-RIDE.4  | Assign route to ride       | 1. Edit ride<br>2. Select existing route<br>3. Save                                | Route map displayed on ride detail               |
| E-RIDE.5  | Assign route to group      | 1. Edit ride group<br>2. Select different route<br>3. Save                         | Group shows its specific route                   |
| E-RIDE.6  | Set start/end places       | 1. Edit ride<br>2. Select start place<br>3. Select end place<br>4. Save            | Places displayed on ride detail                  |
| E-RIDE.7  | Join ride group            | 1. View published ride as member<br>2. Click "Join" on group<br>3. Confirm         | User added to participants                       |
| E-RIDE.8  | Leave ride group           | 1. View ride as participant<br>2. Click "Leave"<br>3. Confirm                      | User removed from group                          |
| E-RIDE.9  | Group capacity full        | 1. Join group until maxParticipants reached<br>2. Try to join as another user      | Join button disabled or error                    |
| E-RIDE.10 | View ride as non-member    | 1. View public ride as non-member                                                  | Ride details shown, join prompts team join first |

---

### E-ROUTE: Route Management

| ID        | Scenario              | Steps                                                                        | Expected Result                          |
| --------- | --------------------- | ---------------------------------------------------------------------------- | ---------------------------------------- |
| E-ROUTE.1 | Create route with GPX | 1. Create route<br>2. Upload GPX file<br>3. Save                             | Route created with geometry              |
| E-ROUTE.2 | Auto-extract metrics  | 1. Upload GPX<br>2. Check extracted data                                     | Distance, elevation gain/loss calculated |
| E-ROUTE.3 | Detect climbs         | 1. Upload GPX with climbs<br>2. View route detail                            | Climbs listed with category (HC-CAT4)    |
| E-ROUTE.4 | Set surface type      | 1. Create route<br>2. Select surface type (Road/Gravel/MTB/Mixed)<br>3. Save | Surface type displayed on route          |
| E-ROUTE.5 | Download GPX          | 1. View route detail<br>2. Click "Download GPX"                              | Original GPX file downloaded             |
| E-ROUTE.6 | Download FIT          | 1. View route with FIT available<br>2. Click "Download FIT"                  | FIT file downloaded                      |
| E-ROUTE.7 | Interactive map       | 1. View route detail<br>2. Interact with map                                 | Map pans, zooms, shows elevation chart   |
| E-ROUTE.8 | Route reuse           | 1. Create ride<br>2. Select existing route                                   | Route appears on ride without re-upload  |

---

### E-POST: Post Management

| ID       | Scenario                 | Steps                                                       | Expected Result                     |
| -------- | ------------------------ | ----------------------------------------------------------- | ----------------------------------- |
| E-POST.1 | Create post with content | 1. Create post<br>2. Add title, markdown content<br>3. Save | Post created with formatted content |
| E-POST.2 | Set post date            | 1. Create/edit post<br>2. Set date/time<br>3. Save          | Post shows correct date             |
| E-POST.3 | Post in feed             | 1. Create published post<br>2. View team publications       | Post appears in unified feed        |

---

### E-TRIP: Trip Management

| ID       | Scenario             | Steps                                                                         | Expected Result                     |
| -------- | -------------------- | ----------------------------------------------------------------------------- | ----------------------------------- |
| E-TRIP.1 | Create trip          | 1. Create trip<br>2. Set name, date, description<br>3. Save                   | Trip created with default stage     |
| E-TRIP.2 | Add trip stage       | 1. Edit trip<br>2. Add stage "Day 2"<br>3. Set date, route, places<br>4. Save | Multiple stages displayed           |
| E-TRIP.3 | Stage with route     | 1. Edit stage<br>2. Assign route<br>3. Save                                   | Stage shows route on map            |
| E-TRIP.4 | Stage places         | 1. Edit stage<br>2. Set start place, end place<br>3. Save                     | Places shown on stage card          |
| E-TRIP.5 | Join trip            | 1. View published trip as member<br>2. Click "Join"<br>3. Confirm             | User added to trip participants     |
| E-TRIP.6 | Leave trip           | 1. View trip as participant<br>2. Click "Leave"<br>3. Confirm                 | User removed from trip              |
| E-TRIP.7 | Participant count    | 1. Multiple users join trip<br>2. View trip detail                            | Correct participant count shown     |
| E-TRIP.8 | Trips tab visibility | 1. Team with enableTrips=false                                                | Trips tab not visible in navigation |

---

### E-FEED: Unified Publication Feed

| ID       | Scenario                | Steps                                | Expected Result                             |
| -------- | ----------------------- | ------------------------------------ | ------------------------------------------- |
| E-FEED.1 | View team publications  | 1. Navigate to team publications     | Combined list of rides, posts, trips        |
| E-FEED.2 | Publication type badges | 1. View feed                         | Each item shows type badge (Ride/Post/Trip) |
| E-FEED.3 | Home page feed          | 1. Navigate to home page             | Publications from all user's teams          |
| E-FEED.4 | Search publications     | 1. Enter search on feed<br>2. Submit | Filtered results across all types           |

---

### E-AUTH: Authentication & Authorization

| ID       | Scenario              | Steps                                               | Expected Result                       |
| -------- | --------------------- | --------------------------------------------------- | ------------------------------------- |
| E-AUTH.1 | Login via Keycloak    | 1. Click Login<br>2. Enter credentials<br>3. Submit | Redirected to app, user authenticated |
| E-AUTH.2 | Logout                | 1. Click Logout                                     | Session ended, redirect to login      |
| E-AUTH.3 | ADMIN permissions     | 1. Login as ADMIN<br>2. Navigate team               | Settings, Members tabs visible        |
| E-AUTH.4 | ORGANIZER permissions | 1. Login as ORGANIZER<br>2. Navigate team           | Create buttons visible, no settings   |
| E-AUTH.5 | MEMBER permissions    | 1. Login as MEMBER<br>2. Navigate team              | View only, can join events            |
| E-AUTH.6 | Protected route       | 1. Access /teams without auth                       | Redirect to login                     |

---

## Test User Matrix

| User  | Password | Team Role | Use For                   |
| ----- | -------- | --------- | ------------------------- |
| admin | admin    | ADMIN     | Full permissions testing  |
| user1 | user1    | ORGANIZER | Content creation testing  |
| user2 | user2    | MEMBER    | Participation testing     |
| user3 | user3    | None      | Non-member access testing |

---

## Part 3: Role Permissions Scenarios (Detailed)

### Permission Matrix Reference

| Action                | ADMIN          | ORGANIZER | MEMBER | Non-Member (Public Team) | Non-Member (Private Team) |
| --------------------- | -------------- | --------- | ------ | ------------------------ | ------------------------- |
| View team             | ✓              | ✓         | ✓      | ✓                        | ✗                         |
| View publications     | ✓              | ✓         | ✓      | ✓ (if public)            | ✗                         |
| View drafts           | ✓              | ✓         | ✗      | ✗                        | ✗                         |
| Create ride/post/trip | ✓              | ✓         | ✗      | ✗                        | ✗                         |
| Create route          | ✓              | ✓         | ✗      | ✗                        | ✗                         |
| Edit own content      | ✓              | ✓         | ✗      | ✗                        | ✗                         |
| Edit any content      | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| Delete content        | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| Publish/Unpublish     | ✓              | ✓         | ✗      | ✗                        | ✗                         |
| Manage places         | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| View members list     | ✓              | ✓         | ✓      | ✗                        | ✗                         |
| Manage members        | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| Change member roles   | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| Team settings         | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| Delete team           | ✓              | ✗         | ✗      | ✗                        | ✗                         |
| Join ride/trip        | ✓              | ✓         | ✓      | ✗                        | ✗                         |
| Leave ride/trip       | ✓              | ✓         | ✓      | ✗                        | ✗                         |
| Join team             | N/A            | N/A       | N/A    | ✓                        | ✗                         |
| Leave team            | ✗ (last admin) | ✓         | ✓      | N/A                      | N/A                       |

---

### P-ADMIN: Admin Permission Scenarios

| ID         | Scenario             | Steps                                                                       | Expected Result                  |
| ---------- | -------------------- | --------------------------------------------------------------------------- | -------------------------------- |
| P-ADMIN.1  | Access team settings | 1. Login as ADMIN<br>2. Navigate to team<br>3. Check navigation             | Settings tab visible, accessible |
| P-ADMIN.2  | Access members page  | 1. Login as ADMIN<br>2. Navigate to Members                                 | Full member management UI shown  |
| P-ADMIN.3  | Add member           | 1. Go to Members<br>2. Search user<br>3. Select role<br>4. Add              | User added with selected role    |
| P-ADMIN.4  | Remove member        | 1. Go to Members<br>2. Click remove on user<br>3. Confirm                   | User removed from team           |
| P-ADMIN.5  | Promote to ORGANIZER | 1. Go to Members<br>2. Change MEMBER to ORGANIZER<br>3. Save                | Role updated successfully        |
| P-ADMIN.6  | Promote to ADMIN     | 1. Go to Members<br>2. Change user to ADMIN<br>3. Save                      | User now has ADMIN privileges    |
| P-ADMIN.7  | Demote ADMIN         | 1. Go to Members (as ADMIN)<br>2. Change another ADMIN to MEMBER<br>3. Save | Role downgraded                  |
| P-ADMIN.8  | Delete any content   | 1. View another user's ride<br>2. Click Delete<br>3. Confirm                | Content deleted                  |
| P-ADMIN.9  | Edit any content     | 1. View another user's post<br>2. Click Edit<br>3. Modify and save          | Content updated                  |
| P-ADMIN.10 | Manage places        | 1. Go to Settings<br>2. Open Places section                                 | Full CRUD on places available    |
| P-ADMIN.11 | Delete team          | 1. Go to Settings<br>2. Click Delete Team<br>3. Confirm                     | Team and all content deleted     |
| P-ADMIN.12 | Last admin leave     | 1. Be only ADMIN<br>2. Try to leave team                                    | Prevented with error message     |

---

### P-ORG: Organizer Permission Scenarios

| ID       | Scenario                    | Steps                                                                 | Expected Result                                 |
| -------- | --------------------------- | --------------------------------------------------------------------- | ----------------------------------------------- |
| P-ORG.1  | Create ride                 | 1. Login as ORGANIZER<br>2. Navigate to Rides<br>3. Click Create      | Create form accessible                          |
| P-ORG.2  | Create post                 | 1. Login as ORGANIZER<br>2. Navigate to Posts<br>3. Click Create      | Create form accessible                          |
| P-ORG.3  | Create trip                 | 1. Login as ORGANIZER<br>2. Navigate to Trips<br>3. Click Create      | Create form accessible                          |
| P-ORG.4  | Create route                | 1. Login as ORGANIZER<br>2. Navigate to Routes<br>3. Click Create     | Create form accessible                          |
| P-ORG.5  | Edit own ride               | 1. Create ride as ORGANIZER<br>2. View ride<br>3. Click Edit          | Edit form accessible                            |
| P-ORG.6  | Publish own draft           | 1. Create draft ride<br>2. Click Publish                              | Status changes to PUBLISHED                     |
| P-ORG.7  | Cannot edit other's content | 1. View ride created by another ORGANIZER<br>2. Check for Edit button | Edit button NOT visible                         |
| P-ORG.8  | Cannot delete content       | 1. View own ride<br>2. Check for Delete                               | Delete button NOT visible                       |
| P-ORG.9  | Cannot access settings      | 1. Navigate to team<br>2. Check navigation                            | Settings tab NOT visible                        |
| P-ORG.10 | Cannot manage members       | 1. Navigate to Members<br>2. Check UI                                 | No add/remove/role-change buttons               |
| P-ORG.11 | View drafts                 | 1. Navigate to Rides list                                             | Own drafts and other organizer's drafts visible |
| P-ORG.12 | Leave team                  | 1. View team<br>2. Click Leave<br>3. Confirm                          | Successfully removed from team                  |

---

### P-MEM: Member Permission Scenarios

| ID       | Scenario                | Steps                                            | Expected Result                        |
| -------- | ----------------------- | ------------------------------------------------ | -------------------------------------- |
| P-MEM.1  | View publications       | 1. Login as MEMBER<br>2. Navigate to team        | Published rides/posts/trips visible    |
| P-MEM.2  | Cannot see drafts       | 1. Navigate to Rides<br>2. Check list            | Only PUBLISHED content visible         |
| P-MEM.3  | No create buttons       | 1. Navigate to Rides<br>2. Check page            | No "Create Ride" button                |
| P-MEM.4  | No edit option          | 1. View published ride<br>2. Check UI            | No Edit button visible                 |
| P-MEM.5  | Join ride group         | 1. View published ride<br>2. Click Join on group | Successfully joined                    |
| P-MEM.6  | Leave ride group        | 1. View ride as participant<br>2. Click Leave    | Successfully left group                |
| P-MEM.7  | Join trip               | 1. View published trip<br>2. Click Join          | Successfully joined trip               |
| P-MEM.8  | Leave trip              | 1. View trip as participant<br>2. Click Leave    | Successfully left trip                 |
| P-MEM.9  | Cannot access settings  | 1. Try URL /teams/{slug}/settings                | Redirect or access denied              |
| P-MEM.10 | View members (readonly) | 1. Navigate to team<br>2. Check Members tab      | Members visible as list, no management |
| P-MEM.11 | Leave team              | 1. Click Leave Team<br>2. Confirm                | Successfully removed                   |
| P-MEM.12 | View route details      | 1. Navigate to route<br>2. View detail           | Full route info, map, downloads        |

---

### P-NON: Non-Member Permission Scenarios (Public Team)

| ID      | Scenario            | Steps                                             | Expected Result                       |
| ------- | ------------------- | ------------------------------------------------- | ------------------------------------- |
| P-NON.1 | View public team    | 1. Not a member<br>2. Navigate to public team URL | Team info and publications visible    |
| P-NON.2 | View public ride    | 1. Navigate to public ride                        | Ride details displayed                |
| P-NON.3 | Cannot join ride    | 1. View public ride<br>2. Check Join button       | "Join team first" message or disabled |
| P-NON.4 | Cannot see drafts   | 1. Navigate to team publications                  | Only PUBLISHED visible                |
| P-NON.5 | Cannot see members  | 1. Check navigation                               | Members tab not visible               |
| P-NON.6 | Join team           | 1. Click Join Team<br>2. Confirm                  | Become MEMBER of team                 |
| P-NON.7 | View public route   | 1. Navigate to public route                       | Route details and map visible         |
| P-NON.8 | Download public GPX | 1. View public route<br>2. Click Download         | GPX file downloads                    |

---

### P-PRI: Non-Member Permission Scenarios (Private Team)

| ID      | Scenario                | Steps                                   | Expected Result                     |
| ------- | ----------------------- | --------------------------------------- | ----------------------------------- |
| P-PRI.1 | Cannot view team        | 1. Navigate to private team URL         | Access denied or 404                |
| P-PRI.2 | Cannot view rides       | 1. Navigate to private ride URL         | Access denied or 404                |
| P-PRI.3 | Cannot join team        | 1. Try to access team                   | No Join button visible              |
| P-PRI.4 | Search excludes private | 1. Search home page<br>2. Check results | Private team content not in results |

---

### P-EDGE: Permission Edge Cases

| ID       | Scenario                      | Steps                                                                             | Expected Result                |
| -------- | ----------------------------- | --------------------------------------------------------------------------------- | ------------------------------ |
| P-EDGE.1 | Role change while logged in   | 1. ORGANIZER is logged in<br>2. ADMIN changes to MEMBER<br>3. ORGANIZER refreshes | New permissions applied        |
| P-EDGE.2 | Removed while logged in       | 1. User logged in<br>2. ADMIN removes user<br>3. User refreshes                   | Redirect to team list or error |
| P-EDGE.3 | Visibility change PUBLIC→TEAM | 1. Non-member viewing public ride<br>2. ADMIN changes to TEAM<br>3. Refresh       | Access denied                  |
| P-EDGE.4 | Create then demote            | 1. ORGANIZER creates ride<br>2. Demoted to MEMBER<br>3. View own ride             | Can view but cannot edit       |
| P-EDGE.5 | Draft author demoted          | 1. ORGANIZER creates draft<br>2. Demoted to MEMBER<br>3. View drafts              | Own draft no longer visible    |
| P-EDGE.6 | Admin self-demote             | 1. ADMIN demotes self to MEMBER<br>2. Check permissions                           | No longer ADMIN, cannot undo   |
| P-EDGE.7 | Multiple admins               | 1. Team has 2 ADMINs<br>2. One tries to leave                                     | Allowed (other ADMIN remains)  |

---

## Execution Priority

### P1 - Critical Path (Run First)

- E-AUTH.1, E-AUTH.2 (authentication)
- E-TEAM.1, E-TEAM.7 (team creation, joining)
- SB-3.1, SB-3.2 (draft/publish workflow)
- E-RIDE.1, E-RIDE.7 (ride creation, participation)

### P2 - Core Features

- All SB-1.x (media management)
- All SB-4.x (visibility)
- E-ROUTE.1-E-ROUTE.3 (route basics)
- E-POST.1-E-POST.3 (post basics)

### P3 - Extended Features

- E-TRIP.x (trip management)
- E-PLACE.x (place management)
- SB-7.x (pagination/search)

### P4 - Edge Cases

- SB-5.3 (duplicate slugs)
- E-RIDE.9 (capacity limits)
- SB-6.x (soft delete)

---

## Total Scenario Count

| Category                     | Count             |
| ---------------------------- | ----------------- |
| **Shared Behaviors (SB)**    |                   |
| - Content Media (SB-1)       | 6                 |
| - Asset Upload (SB-2)        | 4                 |
| - Status Workflow (SB-3)     | 8                 |
| - Visibility (SB-4)          | 5                 |
| - Slug Generation (SB-5)     | 4                 |
| - Soft Delete (SB-6)         | 3                 |
| - Pagination/Search (SB-7)   | 5                 |
| - Form Validation (SB-8)     | 4                 |
| **Entity-Specific (E)**      |                   |
| - Team (E-TEAM)              | 9                 |
| - Place (E-PLACE)            | 4                 |
| - Ride (E-RIDE)              | 10                |
| - Route (E-ROUTE)            | 8                 |
| - Post (E-POST)              | 3                 |
| - Trip (E-TRIP)              | 8                 |
| - Feed (E-FEED)              | 4                 |
| - Auth (E-AUTH)              | 6                 |
| **Role Permissions (P)**     |                   |
| - Admin (P-ADMIN)            | 12                |
| - Organizer (P-ORG)          | 12                |
| - Member (P-MEM)             | 12                |
| - Non-Member Public (P-NON)  | 8                 |
| - Non-Member Private (P-PRI) | 4                 |
| - Edge Cases (P-EDGE)        | 7                 |
| **TOTAL**                    | **146 scenarios** |
