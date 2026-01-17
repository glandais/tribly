# Tribly Garmin Connect IQ App

## Overview

Garmin Connect IQ app for Edge cycling computers that allows Tribly users to download routes directly to their GPS, with intelligent route suggestions based on upcoming rides.

## Status

| Component | Status |
|-----------|--------|
| Garmin App (Monkey C) | **Done** - Builds for all devices |
| Backend API | Pending |
| Frontend OAuth Page | Pending |

## Garmin App

### Build

See [garmin-app/BUILD.md](garmin-app/BUILD.md) for detailed instructions.

```bash
cd garmin-app

# Build for all devices
make build-all

# Test in simulator (Ubuntu 24.04+)
make simulator-docker
make run-docker DEVICE=edge1040
```

### Supported Devices

Requires Connect IQ API 3.2.0+:

| Device | Product ID |
|--------|------------|
| Edge 530 | edge530 |
| Edge 540 | edge540 |
| Edge 830 | edge830 |
| Edge 840 | edge840 |
| Edge 1030 | edge1030 |
| Edge 1030 Plus | edge1030plus |
| Edge 1040 | edge1040 |

### Source Files

```
garmin-app/
├── manifest.xml              # App manifest (permissions, devices)
├── monkey.jungle             # Build configuration
├── Makefile                  # Build automation
├── BUILD.md                  # Build instructions
├── Dockerfile.sdk-manager    # Docker for SDK Manager (Ubuntu 24.04+)
├── Dockerfile.build          # Docker for building
├── source/
│   ├── TriblyApp.mc          # Main app entry, OAuth flow
│   ├── AuthManager.mc        # Token storage/refresh
│   ├── ApiClient.mc          # HTTP API client
│   ├── LoginView.mc          # Login prompt screen
│   ├── TriblyView.mc         # Route list view
│   ├── TriblyDelegate.mc     # Route list input handling
│   ├── RouteDetailView.mc    # Route detail + download UI
│   ├── RouteDetailDelegate.mc # Route detail input
│   └── ErrorView.mc          # Error display
└── resources/
    ├── drawables/            # Launcher icon
    └── strings/
        ├── strings.xml       # English strings
        └── strings-fre.xml   # French strings
```

---

## Backend API (To Implement)

### OAuth Endpoints

**File:** `backend/src/main/java/com/tribly/api/garmin/GarminOAuthResource.java`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/garmin/oauth/authorize` | GET | Redirects to frontend `/garmin/login?...` |
| `/api/garmin/oauth/callback` | POST | Receives auth from frontend, redirects to `connectiq://oauth` |
| `/api/garmin/oauth/token` | POST | Exchange auth code for JWT tokens |

**Token configuration:**
- Access token: 1 hour expiry (longer than web due to device connectivity)
- Refresh token: 90 days

### OAuth Flow

```
Garmin App -> /api/garmin/oauth/authorize
           -> 302 Redirect to frontend /garmin/login?client_id=...&redirect_uri=...
           -> User logs in via magic link or passkey (existing auth)
           -> Frontend POST /api/garmin/oauth/callback with user token
           -> Backend generates auth code
           -> 302 Redirect to connectiq://oauth?code=...
           -> Garmin App receives code, exchanges for tokens
```

### Routes Endpoint

**File:** `backend/src/main/java/com/tribly/api/garmin/GarminRoutesResource.java`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/garmin/routes` | GET | Get routes for authenticated user |
| `/api/garmin/routes/{teamSlug}/{routeSlug}/fit` | GET | Download FIT file |

**Route selection logic:**
1. Get user's teams (memberships)
2. For each team:
   - Find PUBLISHED rides where dateTime is in [-1 day, +7 days]
   - IF rides exist: Extract routes from RideGroup.route, label with group name + ride dateTime
   - ELSE: Get latest 10 published routes
3. Sort by ride dateTime proximity, then by distance from user GPS (if provided)

### DTOs

```java
record GarminRouteDto(
    String teamSlug,
    String routeSlug,
    String name,
    String label,           // e.g., "Rapides - Sam 18 Jan 09:00"
    Instant rideDateTime,   // null if not from ride
    float distance,         // meters
    float elevationGain,    // meters
    Double startLat,
    Double startLon,
    Double distanceFromUser // meters, if GPS provided
) {}
```

### Backend Files to Create

1. `backend/src/main/java/com/tribly/api/garmin/GarminOAuthResource.java`
2. `backend/src/main/java/com/tribly/api/garmin/GarminRoutesResource.java`
3. `backend/src/main/java/com/tribly/service/garmin/GarminService.java`
4. `backend/src/main/java/com/tribly/dto/garmin/GarminRouteDto.java`
5. `backend/src/main/java/com/tribly/dto/garmin/GarminTokenRequest.java`
6. `backend/src/main/java/com/tribly/dto/garmin/GarminRoutesResponse.java`

---

## Frontend (To Implement)

### OAuth Login Page

**File:** `frontend/src/pages/GarminLoginPage.tsx`

Mobile-friendly page that:
1. Shows Tribly branding
2. Allows login via magic link or passkey (existing auth)
3. On success, POSTs to `/api/garmin/oauth/callback`
4. Backend redirects to `connectiq://oauth?code=...`

### Frontend Files to Create

1. `frontend/src/pages/GarminLoginPage.tsx`
2. Update `frontend/src/config/routes.config.ts` - Add `/garmin/login` route
3. `frontend/src/locales/{en,fr}/garmin.json` - i18n strings (optional)

---

## Testing

### Backend Testing
```bash
cd backend && mvn quarkus:dev

# Test OAuth authorize
curl http://localhost:8080/api/garmin/oauth/authorize?client_id=garmin-connect-iq

# Test routes (with valid JWT)
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/garmin/routes

# Test FIT download
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/garmin/routes/team-slug/route-slug/fit -o test.fit
```

### Garmin App Testing
```bash
cd garmin-app

# Build
make build DEVICE=edge1040

# Start simulator and load app
make simulator-docker
make run-docker DEVICE=edge1040
```

### End-to-End Testing
1. Start backend with `mvn quarkus:dev`
2. Start frontend with `pnpm dev`
3. Load app in simulator
4. Test OAuth flow (requires phone companion simulator)
5. Test route list and download
