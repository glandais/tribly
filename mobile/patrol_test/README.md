# Mobile end-to-end tests (Patrol)

[Patrol](https://patrol.leancode.co) drives the real app on the iOS simulator against the **e2e
stack** — the same one as the web suite (`frontend/e2e/`): empty database, mail to mailpit only.
Nothing is mocked: accounts are created through the REST API and verified with the link read from
mailpit.

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
  shared with the web suite and never reset) and `MailpitClient`;
- widgets are found **by key only**. Keys live in a `keys.dart` next to their feature
  (`lib/features/auth/keys.dart`, …) and are aggregated in `lib/keys.dart`; add one only for a
  widget a test uses.

Three helpers of `common.dart` go beyond mounting the app:

- `openLink($, path)` opens an app path as a shared link or a push notification does — through
  `pendingPushRouteProvider`, the same waiting logic as a web link, ancestors rebuilt underneath.
  That is how a test reaches a ride, a post or `/karoo?code=…` without walking the UI to it;
- `eventually(read, until: …)` polls the API for what the backend does on its own schedule (the
  notification dispatcher runs every 15 s);
- `testAppKnownDefect(…, defect: …)` is Playwright's `test.fail`: the body describes the fixed
  app, fails today for the documented defect, and the wrapper passes. The day the defect is fixed
  the wrapper fails and asks for `testApp` back — never loosen the assertions instead.

The platform admin (the stack's `PEDALONS_BOOTSTRAP_ADMIN_EMAIL`) seeds what only it may do: add
members, open a team to the public, make it joinable. It logs in by OTP, rate-limited to 3 per
5 minutes, so `BackendClient.admin()` keeps its refresh token in the app's temporary directory for
the rest of the run.

## Coverage

Transposed from the P0 of [`frontend/E2E_COVERAGE_AUDIT.md`](../../frontend/E2E_COVERAGE_AUDIT.md)
to what the app does — it reads and takes part, it doesn't edit, and it has no server rendering:

| Audit P0 | Test |
|---|---|
| #1, #9 — denied roles, private team | `private_team_test` — a members-only team stays out of discovery; its ride and team links show an error to an outsider. `load_error_delay_test`: that error shows within 10 s — a 4xx is never retried (`providerRetry` on the app's `ProviderScope`) |
| #2 — joining a team | `team_join_test` (join, then leave), `team_invite_only_test` (« Sur invitation », disabled) |
| #3 — leader who left the team | `ride_leader_test` — the group still names its leader, never the ride's creator; joining, then switching groups through the exclusivity banner |
| #4 — ad rights | `ad_rights_test` (a non-author may contact, report, block — never delete), `ad_author_test` |
| #5 — notifications | `notification_post_test` (bell badge, entry in the team's name, opens the post, reads it), `notification_mute_test` |
| #6 — moderation | `report_post_test` (report from the post, three reports hide it), `block_user_test` (block a comment's author, unblock from the profile) |
| #7 — slug change | `slug_change_test` — links under the old team and ride slugs |
| #8 — device pairing | `device_link_test` (`/karoo?code=`), `device_manual_code_test` (unknown code, « Réessayer », lowercase), `device_link_signed_out_test` (a link opened signed out survives the login: the device is paired once signed in) |
