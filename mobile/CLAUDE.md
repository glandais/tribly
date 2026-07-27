# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Pedalons mobile app: Flutter client for the cycling team platform. See parent `../CLAUDE.md` for full project context.

The app is at **v2** (July 2026): a rewritten design system (`core/pdl`), a single five-tab shell, light *and* dark themes, metric *and* imperial units, and twelve reworked screens. Two documents explain the shape of it — read them before adding a widget or a screen:

- **[lib/core/pdl/README.md](lib/core/pdl/README.md)** — the component library's contract, its naming, and the two `grep`s that enforce it in review. Not optional reading: a widget that can't meet the contract belongs in its feature, without the `Pdl` prefix.
- **[../docs/plans/archive/2026-07-26-mobile-v2-implementation.md](../docs/plans/archive/2026-07-26-mobile-v2-implementation.md)** — the plan that produced v2, kept for the *why* of ~15 arbitrations that still constrain the code (no `createdBy` fallback for a group leader, offset over cursor pagination, device timezone over team timezone, Material icons over Tabler, GeoJSON fallback over `.mvt` tiles).

What remains to do — and what was deliberately left out — is in **[../docs/NEXT.md](../docs/NEXT.md)**.

## Commands

```bash
flutter pub get                    # Install dependencies
flutter run                        # Run on connected device/emulator
flutter test                       # Run tests
flutter analyze                    # Static analysis
bash check.sh                      # pub get + both generators + analyze — the full gate
../format.sh mobile                # dart format (skips generated code) — run before every commit

# Code generation (after modifying models or API)
dart run build_runner build

# API client generation from OpenAPI
dart run openapi_retrofit_generator
```

## Architecture

```
lib/
├── main.dart              # Entry point, deep link handling, ProviderScope
├── app.dart               # PedalonsApp widget, themeMode from user preferences
├── config/
│   ├── router.dart            # GoRouter: StatefulShellRoute.indexedStack, 5 fixed branches
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
├── core/
│   ├── pdl/               # THE component library — ~60 Pdl* widgets. See pdl/README.md
│   │   ├── pdl.dart           # Single barrel: import this, never a file directly
│   │   ├── elevation/         # PdlElevationProfile — two hand-written CustomPainters
│   │   └── map/               # PdlMap, its overlays, and the mass GeoJSON fallback
│   ├── theme/             # pdl_colors, pdl_tokens, pdl_typography, pdl_icons, enum_colors
│   ├── units/             # UnitSystem — metric/imperial
│   ├── preferences/       # user_preferences_provider — theme, units, language, contactable
│   ├── adaptive/          # kAppDestinations (the five tabs), breakpoints, responsive grid
│   ├── pagination/        # PagedListNotifier (offset-based, dedup by itemKey)
│   ├── geo/               # location_service, polyline_index (client-side hit-testing)
│   ├── animations/, config/, utils/, widgets/
├── dev/                   # PdlGallery, map demo, shell demo — the library's living doc
└── features/              # Feature-based organization
    ├── auth/
    │   ├── data/              # SecureTokenStorage, AuthRepository
    │   ├── domain/            # AuthState (freezed)
    │   ├── providers/         # AuthNotifier (StateNotifier)
    │   ├── presentation/      # LoginPage, VerifyEmailPage
    │   └── services/          # PasskeyService
    ├── home/, teams/, rides/, routes/, calendar/, trips/, posts/, ads/
    ├── comments/, participants/, feed/, profile/, legal/, device/
    └── navigation/presentation/shell/main_shell.dart
```

### The design system (`core/pdl`)

Read [lib/core/pdl/README.md](lib/core/pdl/README.md) before touching it. Three rules, all
`grep`-verifiable, all enforced in review:

1. **No colour, size, duration or icon literal** — everything comes from `context.pdl`
   (`PdlColors`), `PdlSpacing` / `PdlRadii` / `PdlMetrics`, `context.pdlText`, `PdlMotion`,
   `PdlIcons`. Dark mode then works *by construction*; a screen that patches a colour with a
   literal is the bug, not the fix.
2. **44 px minimum tap target** (`PdlMetrics.tapTarget`), including for compact sizes — `sm`
   tightens typography and gutters, never the target.
3. **`core/pdl` imports no generated DTO and translates nothing.** A component needing a business
   tint takes a `PdlTone`; a component needing a label takes a `String`. Localisation keys belong to
   screens.

```bash
grep -rn --include='*.dart' "api/generated" lib/core/pdl   # must stay empty
grep -rn --include='*.dart' '\bIcons\.'     lib/core/pdl   # must stay empty — PdlIcons only
```

`PdlIcons` is the only file in the app allowed to name `Icons.*`, so swapping icon sets later
touches one file. `lib/dev/pdl_gallery_page.dart` renders the whole library in both themes — the
fastest way to see what already exists before writing a new widget.

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

`router.dart` registers every locale variant so deep links in any supported language match. Flat routes use `_perLocale(PathVariants.xxx(), ...)`.

**The shell has five fixed branches and only five** — `StatefulShellRoute.indexedStack`, one
`StatefulShellBranch` per tab, declared once in `kAppDestinations` (`core/adaptive/navigation_destination.dart`).
There is no second `NavigationBar`: team sections are **content** (a sticky chip row under the team
header, `features/teams/.../team_sections.dart`), not a nested tab bar. The old `TeamShell` was
removed in v2 — it was the direct cause of sheets rendering under the tab bar and of stacked error
states. Two tests guard this: `shell_branches_test.dart` (branch count and order) and
`destination_index_test.dart` (which tab lights up for a given path — most specific wins, so a team's
route library stays on Teams, not Routes).

A route opened straight from a link starts with an empty back stack, so `_deepLinkHierarchies` declares the ancestors to push underneath it (`ancestorsForDeepLink`). Add an entry for any new deep-linkable route that lives outside a shell — see [../APP_LINKS.md](../APP_LINKS.md) and `test/deep_link_hierarchy_test.dart`.

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
- **The router is built once**: `routerProvider` must never `ref.watch` auth state — recreating the `GoRouter` restarts the navigator from `initialLocation` and wipes the back stack rebuilt for a deep link. Auth changes flow through `refreshListenable` and re-run `redirect`
- **Deep links wait for the app to be navigable**: `main.dart` opens the pending link only once auth is initialized *and* the router has parsed its first route — `GoRouter.push` stacks onto `routerDelegate.currentConfiguration`, which is empty before that
- **Locale**: `app.dart` propagates `context.locale.languageCode` into `locale_context.dart` so `Paths.xxx()` returns the right variant
- Config via `--dart-define`: `flutter run --dart-define=API_BASE_URL=http://localhost:8080`
- **Adding a capability that touches personal data** (image picker, location, file import, any analytics SDK) means updating `ios/Runner/PrivacyInfo.xcprivacy` and both store privacy forms. [store-metadata/data-safety.md](store-metadata/data-safety.md) is the source of truth and lists which feature triggers which declaration — App Review rejects an app whose manifest under-declares

### v2 invariants — a review rejects a change that breaks one

- **No colour literal outside `core/theme`**: `grep -rnE '0xFF[0-9A-Fa-f]{6}' lib/features` must stay empty. A dark-mode contrast problem is fixed in the token table, never on the screen that shows it
- **`PdlSheet` is the only caller of `showModalBottomSheet`** — that is what keeps sheets above the tab bar
- **A group's leader is `RideGroupDto.leader`, and nothing else.** It is nullable, and null is the *common* case (most groups have no designated leader) — render nothing. **Never fall back to `createdBy`**, which is the *ride's* creator and therefore identical across all its groups: the fallback would be wrong almost everywhere, and wrong in the way that doesn't announce itself. The backend keeps a test named `groupLeader_isNotTheRideCreator` for exactly this
- **An ad's position is blurred to ~1 km** — render a sector (circle, zone), **never a pin**. A pin on a cell centre claims a precision the data doesn't have, and points at somewhere that isn't the seller's home while looking like an address
- **No notification bell anywhere** — no endpoint exists, and an action icon without effect is forbidden. The Notifications section of the profile screen is *not rendered* rather than rendered empty
- **No waiting list**: "full" is a terminal state. Don't wire a hardcoded `waitlisted: false`
- **Units always go through the single formatter** driven by `UserDto.unitSystem` (`core/units/`, `core/utils/formatters.dart`) — never a hardcoded `km`
- **Pagination is offset-based** (`PagedListNotifier`, dedup by `itemKey`). Cursor pagination isn't in the contract, and the mocked list footer ("60 members of 1,999") needs the `total` a cursor doesn't provide. See [../docs/NEXT.md](../docs/NEXT.md) §4.1 before changing this
- **Dates render in the device timezone**, matching the web. The contract carries no timezone field; keep formatting in one function so a future `Team.timezone` touches one file

## Detailed Guidelines

`lib/core/pdl/README.md` is the authority on the component library (contract, naming, what not to
port). `../docs/NEXT.md` lists what's left and what was ruled out.

See `rules.md` for comprehensive Flutter/Dart coding standards including:
- State management patterns (ValueNotifier, ChangeNotifier, MVVM)
- JSON serialization with `json_serializable`
- Testing best practices (unit, widget, integration)
- Visual design and theming
- Accessibility requirements
