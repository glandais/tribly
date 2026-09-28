# Mobile end-to-end tests (Patrol)

[Patrol](https://patrol.leancode.co) drives the real app on the iOS simulator against the **e2e
stack** — the same one as the web suite (`frontend/e2e/`): empty database, mail to mailhog only.
Nothing is mocked: accounts are created through the REST API and verified with the link read from
mailhog.

## Running

```bash
flutter pub global activate patrol_cli          # once; add ~/.pub-cache/bin to PATH
../scripts/e2e.sh up                             # from mobile/: start the e2e stack
bash e2e.sh                                      # every test
bash e2e.sh -t patrol_test/logout_test.dart      # one test (any `patrol test` flag passes through)
```

`e2e.sh` reads the ports from `../.env.e2e`, passes them as dart-defines, and runs on the simulator
named "iPhone 17 Pro Max" (or the UDID in `PATROL_DEVICE`). **Never run a bare `patrol test`**:
the app's `API_BASE_URL` defaults to production, and `E2eConfig` refuses to start a test whose API
is not on `localhost`.

`patrol test` **uninstalls the app from the simulator** after the run, `fr.pedalons.mobile`
included — reinstall your dev build afterwards. `flutter test` does not run these files; they
only run through Patrol.

### From an agent (Patrol MCP)

`../.mcp.json` registers a `patrol` server (`tool/patrol_mcp.sh`, the `patrol_mcp` package): an
agent can `run` a test file in a `patrol develop` session, take a `screenshot`, read the
`native-tree`, then `quit`. It uses the same stack, simulator and dart-defines as `e2e.sh` — both
source `tool/e2e_env.sh` — so the stack must be up first. Set `SHOW_TERMINAL=true` in its `env` to
follow the session's logs in a Terminal window.

iOS only for now: the `RunnerUITests` target is set up in `ios/Runner.xcodeproj`, the Android
side (`MainActivityTest.java`, test orchestrator) is not.

## Layout

Follows LeanCode's architecture (the `patrol-test-architecture` skill):

- one test per `*_test.dart` file, each going through `testApp` (`common.dart`), which hands it
  the `modules` and the `apiClients`;
- `openApp($)` mounts the app as a fresh install (keychain and preferences cleared — they outlive
  the app between tests), `openAppSignedIn($, user)` with the user's session already stored;
- `modules/` — one class per feature as the user sees it (`auth`, `navigation`, `profile`); a test
  calls module methods only, never Patrol finders;
- `api/` — `BackendClient` (seeds through the API, each test under a unique name: the database is
  shared with the web suite and never reset) and `MailhogClient`;
- widgets are found **by key only**. Keys live in a `keys.dart` next to their feature
  (`lib/features/auth/keys.dart`, …) and are aggregated in `lib/keys.dart`; add one only for a
  widget a test uses.
