# Pedalons Mobile

Flutter mobile app for the Pedalons cycling team platform.

## Prerequisites

- Flutter SDK 3.10.4+
- Dart SDK 3.10.4+
- iOS: Xcode 15+ (for iOS development)
- Android: Android Studio with SDK 21+ (for Android development)

## Setup

```bash
# Install dependencies
flutter pub get

# Generate API clients and models from OpenAPI
dart run openapi_retrofit_generator

# Generate freezed/json_serializable code
dart run build_runner build
```

## Development

### iOS Simulator

```bash
# List available simulators
xcrun simctl list devices

# Start a specific simulator
open -a Simulator

# Or boot a specific device
xcrun simctl boot "iPhone 15 Pro"

# Run app on iOS simulator
flutter run -d iphone
```

### Android Emulator

```bash
# List available emulators
emulator -list-avds

# Start an emulator
emulator -avd <emulator_name>

# Or via Android Studio: Tools > Device Manager > Start

# Run app on Android emulator
flutter run -d android
```

### Physical iPhone

1. Connect iPhone via USB
2. On iPhone: Settings > Privacy & Security > Developer Mode > Enable
3. Trust the computer when prompted
4. In Xcode: Preferences > Accounts > Add Apple ID (for signing)
5. Open `ios/Runner.xcworkspace` in Xcode, select your Team in Signing & Capabilities

```bash
# Run on connected iPhone
flutter run -d <device_id>

# Or let Flutter pick the device
flutter run
```

### Physical Android

1. On device: Settings > About phone > Tap "Build number" 7 times (enables Developer options)
2. Settings > Developer options > Enable "USB debugging"
3. Connect device via USB, accept "Allow USB debugging" prompt

```bash
# Run on connected Android device
flutter run -d <device_id>

# Or let Flutter pick the device
flutter run
```

### General Commands

```bash
# List all available devices
flutter devices

# Run on a specific device
flutter run -d <device_id>

# Run with custom API URL
flutter run --dart-define=API_BASE_URL=http://localhost:8080

# Run tests
flutter test

# Static analysis
flutter analyze
```

## Code Generation

After modifying models or when the backend API changes:

```bash
# Regenerate API client from OpenAPI spec
dart run openapi_retrofit_generator

# Regenerate freezed/json models
dart run build_runner build
```

## Project Structure

```
lib/
├── api/               # HTTP client, interceptors, generated API
├── config/            # Router, paths, app configuration
├── core/              # Shared widgets and utilities
└── features/          # Feature modules (auth, teams, rides, routes, etc.)
```

## Configuration

Environment variables via `--dart-define`:

| Variable | Default | Description |
|----------|---------|-------------|
| `API_BASE_URL` | `https://www.pedalons.fr` | Backend API URL |
| `WEBAUTHN_RP_ID` | `www.pedalons.fr` | WebAuthn Relying Party ID |
| `DEEP_LINK_HOST` | `www.pedalons.fr` | Deep link host |

## Related Documentation

- `CLAUDE.md` - AI assistant guidance for this codebase
- `rules.md` - Flutter/Dart coding standards and best practices
- `../CLAUDE.md` - Full project documentation (backend, frontend, mobile, karoo)
