# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Pedalons mobile app: Flutter client for the cycling team platform. See parent `../CLAUDE.md` for full project context.

## Commands

```bash
flutter pub get                    # Install dependencies
flutter run                        # Run on connected device/emulator
flutter test                       # Run tests
flutter analyze                    # Static analysis

# Code generation (after modifying models or API)
dart run build_runner build

# API client generation from OpenAPI
dart run openapi_retrofit_generator
```

## Architecture

```
lib/
├── main.dart              # Entry point, deep link handling, ProviderScope
├── app.dart               # PedalonsApp widget, theme configuration
├── config/
│   ├── router.dart            # GoRouter configuration with auth redirects (registers all locale variants)
│   ├── paths.dart             # Re-export of paths.generated.dart
│   ├── paths.generated.dart   # Generated from ../../../contracts/routes.yaml — DO NOT EDIT
│   ├── locale_context.dart    # Mutable current-locale, synced from context.locale in app.dart
│   └── app_config.dart        # API URLs, WebAuthn config (compile-time env)
├── api/
│   ├── pedalons_api_client.dart  # Dio providers, client providers
│   ├── interceptors/           # Auth interceptor with token refresh
│   └── generated/              # Auto-generated from OpenAPI (DO NOT EDIT)
│       ├── clients/            # Retrofit API clients
│       ├── models/             # Freezed DTOs
│       └── export.dart         # Barrel file
└── features/              # Feature-based organization
    ├── auth/
    │   ├── data/              # SecureTokenStorage, AuthRepository
    │   ├── domain/            # AuthState (freezed)
    │   ├── providers/         # AuthNotifier (StateNotifier)
    │   ├── presentation/      # LoginPage, VerifyEmailPage
    │   └── services/          # PasskeyService
    ├── home/
    ├── teams/
    ├── rides/
    ├── routes/
    ├── calendar/
    ├── profile/
    └── navigation/            # MainShell with bottom navigation
```

## Contract-First API Workflow

1. Backend generates `contracts/openapi.yaml`
2. Run `dart run openapi_retrofit_generator` → generates `lib/api/generated/`
3. Run `dart run build_runner build` for freezed/json_serializable

Generated code uses:
- **Retrofit** for HTTP clients (annotations → `.g.dart`)
- **Freezed** for immutable models (→ `.freezed.dart`, `.g.dart`)

## Key Patterns

**State Management**: Riverpod with StateNotifier

```dart
// Provider definition
final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) => ...);

// Usage in widgets
class MyWidget extends ConsumerWidget {
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(authProvider);
    final notifier = ref.read(authProvider.notifier);
  }
}
```

**Navigation**: GoRouter with `Paths` class

```dart
// Always use Paths.xxx() instead of hardcoded strings
// Paths is locale-aware: returns /equipes/... when locale is fr, /teams/... for en
context.go(Paths.team(teamSlug));
context.go(Paths.ride(teamSlug, rideSlug));
```

Path declarations live in `../contracts/routes.yaml` (single source of truth shared with the frontend). After editing the YAML, run `pnpm generate-routes` from `frontend/` — it regenerates `lib/config/paths.generated.dart` and the Android deeplink section. See [../APP_LINKS.md](../APP_LINKS.md).

`router.dart` registers every locale variant so deep links in any supported language match. Flat routes use `_perLocale(PathVariants.xxx(), ...)`; the team shell uses `_teamShellTrees()` which derives segments from `PathVariants` via `_relativeTo`.

**API Clients**: Provider-based dependency injection

```dart
// Unauthenticated (for login/register)
final baseApiClientProvider = Provider<PedalonsApiClient>((ref) => ...);

// Authenticated (has auth interceptor)
final apiClientProvider = Provider<PedalonsApiClient>((ref) => ...);

// Individual clients
final teamsClientProvider = Provider<TeamsClient>((ref) => ref.watch(apiClientProvider).teams);
```

**Auth Flow**: Token stored in secure storage, synced to `accessTokenHolderProvider` for interceptor

```dart
// Token refresh handled by AuthInterceptor on 401
// Failed requests queued and retried after refresh
```

## Critical Gotchas

- **Never edit generated code** in `lib/api/generated/` or `lib/config/paths.generated.dart` — edit source and regenerate (see Contract-First and Path Management)
- **Build order matters**: freezed → json_serializable → retrofit_generator (configured in `build.yaml`)
- **Two Dio instances**: `baseDioProvider` (no auth) for login/register, `dioProvider` (with auth interceptor) for protected endpoints
- **Token sync**: Auth state updates must call `_syncTokenToHolder()` for interceptor to see new token
- **Deep links**: Handled by `app_links` package, GoRouter processes the path. Manifest intent-filters are generated from `../contracts/routes.yaml` — see [../APP_LINKS.md](../APP_LINKS.md)
- **Locale**: `app.dart` propagates `context.locale.languageCode` into `locale_context.dart` so `Paths.xxx()` returns the right variant
- Config via `--dart-define`: `flutter run --dart-define=API_BASE_URL=http://localhost:8080`

## Detailed Guidelines

See `rules.md` for comprehensive Flutter/Dart coding standards including:
- State management patterns (ValueNotifier, ChangeNotifier, MVVM)
- JSON serialization with `json_serializable`
- Testing best practices (unit, widget, integration)
- Visual design and theming
- Accessibility requirements
