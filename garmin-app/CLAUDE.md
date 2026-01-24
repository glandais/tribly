# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Pédalons Garmin Connect IQ app for Garmin Edge cycling computers. Allows users to browse and download routes from their teams.

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Monkey C (Connect IQ SDK) |
| API Level | 3.3.0+ |
| Auth | Device Code Flow (RFC 8628) |
| Build | Docker-based SDK + Makefile |

## Commands

```bash
# Build for default device (edge1040)
make build

# Build for specific device
make build DEVICE=edge530

# Build for all devices
make build-all

# Run in simulator (Ubuntu 24.04+ requires Docker)
make simulator-docker    # Start simulator first
make run-docker DEVICE=edge1040

# Native simulator (if webkit2gtk-4.0 available)
make simulator
make run DEVICE=edge1040

# Clean build artifacts
make clean

# Create .iq package for Connect IQ Store
make package

# Generate developer key (one-time)
make keygen
```

## Architecture

```
source/
├── PedalonsApp.mc        # Main app entry, Device Code Flow orchestration
├── AuthManager.mc        # Token storage (Toybox.Storage), expiry management
├── ApiClient.mc          # HTTP client, all API calls, token refresh
├── LoginView.mc          # Device code display (user code + verification URL)
├── PedalonsView.mc       # Route list (scrollable, location-sorted)
├── PedalonsDelegate.mc   # Route list navigation input
├── RouteDetailView.mc    # Single route details
├── RouteDetailDelegate.mc # Route detail input + download
└── ErrorView.mc          # Error display
```

## Authentication Flow

Uses Device Code Flow (RFC 8628) since Edge devices have no keyboard:

1. User presses SELECT on LoginView → `PedalonsApp.startDeviceCodeFlow()`
2. App calls `/api/device/oauth/device` with `clientId=garmin`
3. Displays 6-char user code and `pedalons.fr/device` URL
4. Polls `/api/device/oauth/token` until user authenticates on phone/computer
5. Receives JWT tokens, stores via `AuthManager.saveTokens()`

## API Endpoints

| Endpoint | Purpose |
|----------|---------|
| `POST /api/device/oauth/device` | Request device code |
| `POST /api/device/oauth/token` | Poll for token / refresh token |
| `GET /api/garmin/routes` | Fetch routes (accepts `?lat=&lon=` for proximity sort) |
| `GET /api/garmin/routes/{teamSlug}/{routeSlug}/fit` | Download FIT file |

## Key Patterns

- **Callbacks**: Async operations use method references: `method(:onDeviceCodeReceived)`
- **Polling timer**: `Timer.Timer` with 5s minimum interval per RFC 8628
- **Token refresh**: Automatic before API calls when token expires in <10 min
- **Position-based sorting**: Routes sorted by proximity if GPS available
- **FIT download**: Uses `HTTP_RESPONSE_CONTENT_TYPE_FIT` for direct device import

## Supported Devices

edge530, edge540, edge830, edge840, edge1030, edge1030plus, edge1040, edge1050, edgeexplore2

## Garmin Documentation

Reference PDFs in `docs/`:
- `Garmin Developer Program_Start_Guide_1.2.pdf` - Getting started with Garmin development
- `Courses_API.pdf` - Garmin Courses API for route/course management
- `OAuth2PKCE_2.pdf` - OAuth 2.0 PKCE flow documentation

## Critical Notes

- SDK requires Docker on Ubuntu 24.04+ (webkit2gtk-4.0 incompatibility)
- Developer key must be DER format (see `make keygen`)
- Minimum poll interval is 5 seconds per RFC 8628
- Token expiry includes 5-min buffer for API calls
- Base URL is configured in `ApiClient.mc` (`BASE_URL` constant)
