# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Karoo extension for the Pédalons cycling team platform. Allows Hammerhead Karoo device users to browse and sync routes from their Pédalons teams directly to their device.

## Commands

```bash
# Build
./gradlew assembleDebug            # Build debug APK
./gradlew assembleRelease          # Build release APK (requires signing)

# Install on device
./gradlew installDebug             # Install via ADB on connected Karoo
adb install -r app/build/outputs/apk/debug/app-debug.apk  # Manual install

# Clean
./gradlew clean
```

## Architecture

```
app/src/main/kotlin/fr/pedalons/karoo/
├── PedalonsExtension.kt  # KarooExtension service entry point (bonus action handler)
├── MainActivity.kt       # Route browser UI (Compose)
├── auth/
│   ├── AuthActivity.kt   # Device code flow UI (QR code + polling)
│   └── AuthManager.kt    # Token storage via DataStore
├── api/
│   ├── PedalonsApiClient.kt  # Ktor HTTP client with Karoo engine
│   └── Models.kt             # Kotlinx serialization data classes
└── ui/theme/
    └── Theme.kt          # Dark theme for outdoor visibility
```

## Key Patterns

**Device Code Flow Authentication:**
- No keyboard on Karoo, uses OAuth Device Code flow (RFC 8628)
- Shows QR code + 6-character user code
- User authenticates on phone/computer at `pedalons.fr/device`
- App polls `/api/device/oauth/token` until authorized

**HTTP via Karoo System Service:**
- Uses `ktor-client-karoo` which routes HTTP through Karoo System Service
- Requires `KarooSystemService.connect()` before making API calls
- Client created in Activities after system service connects

**Token Storage:**
- DataStore preferences for access/refresh tokens
- Auto-refresh when token expires (5-minute buffer)

## Critical Constraints

- **100KB response limit**: Karoo System Service limits HTTP response size. Keep route DTOs lightweight (~200 bytes each).
- **New Karoo only**: `ktor-client-karoo` only works on new Karoo devices, not Karoo 2.
- **Dark theme required**: Karoo display is used outdoors, dark theme improves visibility.
- **Java 11 target**: Build targets Java 11 for Karoo compatibility.
- **No wildcard imports**: Follow Hammerhead SDK guidelines.

## Dependencies

Key dependencies from `gradle/libs.versions.toml`:
- `karoo-ext` (1.1.8): Hammerhead Karoo Extension SDK
- `ktor-client-karoo` (1.0.1): Routes HTTP through Karoo System Service
- `compose-bom`: Jetpack Compose for UI
- `datastore`: Token persistence
- `zxing`: QR code generation

## GitHub Packages Authentication

`ktor-client-karoo` is hosted on GitHub Packages. Configure in `~/.gradle/gradle.properties`:
```properties
gpruser=YOUR_GITHUB_USERNAME
gprkey=YOUR_GITHUB_TOKEN
```

## API Endpoints Used

| Endpoint | Purpose |
|----------|---------|
| `POST /api/device/oauth/device` | Request device code for auth |
| `POST /api/device/oauth/token` | Poll for tokens / refresh token |
| `GET /api/device/routes` | Get routes for authenticated user |
| `POST /api/device/routes/{teamSlug}/{routeSlug}/sync` | Sync route to Karoo |

## String Resources

Localized strings in `app/src/main/res/values/strings.xml` and `values-fr/strings.xml`.
