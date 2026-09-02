# Flutter/Dart conventions — Pedalons mobile

Project-specific conventions only. General Dart/Flutter practice (Effective Dart, `const`
constructors, `ListView.builder`, `Expanded`/`Flexible`, null safety, dartdoc style) is not repeated
here — follow <https://dart.dev/effective-dart> and the analyzer.

Visual design, colours, typography, icons, spacing and dark mode are **not** decided in this file:
`lib/core/pdl/README.md` is the authority, and its three grep-enforced rules (no literal, 44 px tap
target, no DTO import in `core/pdl`) override any generic Material advice. There is deliberately no
`ColorScheme.fromSeed` and no `google_fonts`-driven `TextTheme` in the app — see
`lib/core/theme/pedalons_theme.dart` for why.

## Tooling

* `../format.sh mobile` before every commit; `bash check.sh` is the full gate
  (pub get + generators + `flutter analyze`).
* The Dart MCP server (`dart-mcp-server`, registered in `../.mcp.json`) provides `analyze_files`,
  `hot_reload`/`hot_restart`, `get_runtime_errors` and `widget_inspector` against a running app.
* Add dependencies with `flutter pub add <package>` (`dev:<package>` for dev dependencies).

## Architecture

* Feature-based: `lib/features/<feature>/{data,domain,providers,presentation}`; shared code in
  `lib/core/`. Layer roles as in `CLAUDE.md`.
* Abstract data sources behind repositories so screens stay testable.

## State management — Riverpod 3

* Widgets are `ConsumerWidget` / `ConsumerStatefulWidget`.
* `StateNotifierProvider` for multi-field state (`AuthNotifier` / `AuthState`), `FutureProvider`
  (`.family`) for async loads, `Provider` for injection (repositories, API clients).
* `ref.watch` in `build()`, `ref.read` in callbacks, `ref.invalidate` to force a re-fetch.
* Widget-local ephemeral state (toggles, animation controllers) stays in `StatefulWidget` /
  `ValueNotifier`.

```dart
final authProvider = StateNotifierProvider<AuthNotifier, AuthState>(
  (ref) => AuthNotifier(ref.watch(authRepositoryProvider)),
);
```

## Navigation

GoRouter, five fixed shell branches, locale-aware `Paths.xxx()` — see `CLAUDE.md` and
`../APP_LINKS.md`. `Navigator` remains fine for dialogs and other non-deep-linkable views.

## Data and code generation

* API models are Freezed classes generated from the OpenAPI contract into
  `lib/api/generated/models/` — never edited by hand.
* Build order: freezed → json_serializable → retrofit_generator (`build.yaml`).
* Regenerate with `dart run build_runner build` (it deletes conflicting outputs by default).

## Logging

Use `log` from `dart:developer` (never `print`), with `name` and `error`/`stackTrace` for failures:

```dart
import 'dart:developer' as developer;

try {
  // ...
} catch (e, s) {
  developer.log('Failed to fetch data', name: 'pedalons.network', level: 1000, error: e, stackTrace: s);
}
```

## Testing

* `flutter test`; unit tests with `package:test`, widget tests with `package:flutter_test`,
  end-to-end flows with `package:integration_test` (see `CLAUDE.md` on the Flutter Driver flag).
* Arrange-Act-Assert; cover domain logic, repositories and notifiers, then the widgets that
  compose them.
* Prefer fakes or stubs over generated mocks; the project pulls in no mocking package today, so
  adding one is a deliberate decision, not a default.

## Accessibility

* 4.5:1 contrast for body text (3:1 for large text) — the `PdlColors` tables are built for it.
* The UI stays usable at increased system font sizes.
* `Semantics` labels on icon-only controls; test with TalkBack and VoiceOver.
