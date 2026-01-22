# Tribly Karoo Extension

Hammerhead Karoo extension for syncing routes from Tribly directly to your device.

## Features

- Browse routes from your Tribly teams
- One-tap sync to Karoo
- Device code authentication (no typing on Karoo)
- Automatic token refresh

## Requirements

- Hammerhead Karoo (new generation, not Karoo 2)
- Tribly account with team membership
- ADB for sideloading

## Installation

### From APK

1. Download the latest APK from releases
2. Enable ADB on your Karoo (Settings > Advanced > Developer Options)
3. Connect via USB or WiFi ADB
4. Install:
   ```bash
   adb install -r app-release.apk
   ```

### From Source

1. Clone the repository
2. Configure GitHub Packages authentication in `~/.gradle/gradle.properties`:
   ```properties
   gpruser=YOUR_GITHUB_USERNAME
   gprkey=YOUR_GITHUB_TOKEN
   ```
3. Build and install:
   ```bash
   ./gradlew installDebug
   ```

## Usage

1. Open the Tribly app on your Karoo
2. Tap "Connect" to start authentication
3. Scan the QR code or visit the URL shown and enter the code
4. Log in to your Tribly account on your phone/computer
5. Once authenticated, browse your routes
6. Tap a route to sync it to your Karoo

## Authentication

The app uses OAuth Device Code flow (RFC 8628) since Karoo devices don't have a keyboard:

1. App requests a device code from the server
2. Karoo displays a QR code and 6-character code
3. User scans QR or visits `pedalons.fr/device` and enters the code
4. User authenticates with their Tribly account
5. App polls until authentication completes
6. Tokens are stored securely on device

## Development

### Build

```bash
./gradlew assembleDebug      # Debug build
./gradlew assembleRelease    # Release build (requires signing config)
```

### Project Structure

```
app/src/main/kotlin/com/tribly/karoo/
├── TriblyExtension.kt    # Karoo extension service
├── MainActivity.kt       # Route browser
├── auth/
│   ├── AuthActivity.kt   # Device code auth flow
│   └── AuthManager.kt    # Token storage
├── api/
│   ├── TriblyApiClient.kt  # HTTP client
│   └── Models.kt           # Data classes
└── ui/theme/
    └── Theme.kt          # Dark theme
```

### Key Dependencies

- [karoo-ext](https://github.com/hammerheadnav/karoo-ext) - Karoo Extension SDK
- [ktor-client-karoo](https://github.com/jonasfranz/ktor-client-karoo) - HTTP client engine for Karoo
- Jetpack Compose - UI framework
- Kotlinx Serialization - JSON parsing

### Notes

- HTTP requests go through Karoo System Service (required for network access)
- Response size limited to 100KB by Karoo System Service
- Dark theme optimized for outdoor visibility on Karoo display

## License

Proprietary - Tribly
