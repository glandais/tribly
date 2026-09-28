# Mobile end-to-end tests (Patrol)

[Patrol](https://patrol.leancode.co) drives the real app on the iOS simulator or an Android emulator against the **e2e
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

### On Android

```bash
<sdk>/emulator/emulator -avd <avd> -no-snapshot-save &   # one emulator
E2E_PLATFORM=android bash e2e.sh -t patrol_test/logout_test.dart
```

`<sdk>` is `$ANDROID_HOME`; without it, `e2e.sh` looks for adb where Android Studio installs the SDK:
`~/Library/Android/sdk` on macOS, `~/Android/Sdk` on Linux.

`E2E_PLATFORM=android` runs on `emulator-5554` (or the adb serial in `PATROL_DEVICE`) and never
touches a simulator, so it runs beside an iOS run on the same stack — the tests seed under unique
names. Don't `reset`, `down` or `build` the stack while the other one runs. The app keeps
`API_BASE_URL=http://localhost:…`: `e2e.sh` waits for the boot to complete, then `adb reverse`s the
stack's HTTP and mailpit ports. Plain HTTP to `localhost` is allowed by
`android/app/src/debug/res/xml/network_security_config.xml`, debug builds only.

### From an agent (Patrol MCP)

`../.mcp.json` registers a `patrol` server (`tool/patrol_mcp.sh`, the `patrol_mcp` package): an
agent can `run` a test file in a `patrol develop` session, take a `screenshot`, read the
`native-tree`, then `quit`. It uses the same stack, device and dart-defines as `e2e.sh` — both
source `tool/e2e_env.sh` — so the stack must be up first. Set `SHOW_TERMINAL=true` in its `env` to
follow the session's logs in a Terminal window, `E2E_PLATFORM=android` to drive the emulator.

Native wiring: the `RunnerUITests` target in `ios/Runner.xcodeproj`; on Android the
`PatrolJUnitRunner` with the test orchestrator (`android/app/build.gradle.kts`) and
`android/app/src/androidTest/…/MainActivityTest.java`.

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

Helpers of `common.dart` beyond mounting the app:

- `openLink($, path)` opens an app path as a shared link or a push notification does — through
  `pendingPushRouteProvider`, the same waiting logic as a web link, ancestors rebuilt underneath.
  That is how a test reaches a ride, a post or `/karoo?code=…` without walking the UI to it;
- `eventually(read, until: …)` polls the API for what the backend does on its own schedule (the
  notification dispatcher runs every 15 s);
- `openApp`/`openAppSignedIn` may be called twice in a test (a second user, a relaunch): the
  previous app is unmounted first, its `ProviderScope` and cache with it;
- `testAppKnownDefect(…, defect: …)` is Playwright's `test.fail`: the body describes the fixed
  app, fails today for the documented defect, and the wrapper passes. The day the defect is fixed
  the wrapper fails and asks for `testApp` back — never loosen the assertions instead. It only
  catches what the body throws: a defect the app reports through `FlutterError` (a debug
  assertion, a layout overflow) fails the test whatever the wrapper.

A ride or a trip opened from the home or by `openLink` is pushed above the tab shell: there is no
tab bar to tap until it is gone. `openLink($, Paths.home())` then `Home.waitUntilShown()` brings
the tabs back. The « À venir » carousel is a lazy horizontal list that keeps its scroll position:
only the cards near the screen are built, so `Home.rewindUpcoming()` goes back to the first ones.

`Module.scrolledTo(key)` needs its widget hit-testable at its centre; a section whose middle is
empty (a short comment's row, a wrap of badges) goes through `scrolledIntoView(key)`, or through a
button inside it.

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
| #7 — slug change | `slug_change_test` — links under the old team and ride slugs; a member opening the team by its old slug is offered « Quitter » (the page moves onto the current slug, one membership state) |
| #8 — device pairing | `device_link_test` (`/karoo?code=`), `device_manual_code_test` (unknown code, « Réessayer », lowercase), `device_link_signed_out_test` (a link opened signed out survives the login: the device is paired once signed in) |

Beyond the P0 — the P1/P2 of the audit and the app's own screens. A **[known defect]** test would
be a `testAppKnownDefect`, pinning a defect of the app with the evidence in its doc comment; none is
left today.

| Scenario | Test |
|---|---|
| Rides — « Complet » (`rides.e2e.ts` › registration) | `ride_group_full_rollback_test` — a group that fills up while the page is open: the optimistic join is rolled back, the banner names that group and outlives the refetch, the card says « Complet », the other group still takes the rider |
| Audit P2 — « Quitter » from « Ma prochaine sortie » | `next_ride_card_test` — the home card follows a leave from its own button and a join from the ride page |
| Home — « À venir » (no web counterpart) | `upcoming_carousel_order_test` — the nearest outings come first (`/api/publications?sortDir=ASC`, ten slots for twelve outings); « Rejoindre » registers through `autoJoin`, « Choisir un groupe » registers nothing, and back on the home the joined card says « Inscrit » |
| Trips — join, leave (`flow-trips.e2e.ts`) | `trip_join_leave_test` — a trip that started yesterday is still open: join (participants name the member), stage card, stages rail, « Aperçu », leave; cancelled behind the app, a pull-to-refresh shows « Voyage annulé » and no action bar |
| P1 — comments | `comment_reply_test` — comment from the composer, reply nested under a teammate's comment (no third level), delete one's own, a third party's comment offers report/block but no delete; `COMMENT_REPLY` to the commenter, `COMMENT_ON_MY_PUBLICATION` (never `COMMENT_REPLY`) to the post's author |
| P1 — invitations | `team_invitation_accept_test` — the `TEAM_INVITATION` entry opens « Mes équipes », whose pending card accepts it: the team joins the list, the invitation leaves the API |
| P1 — notifications | `notifications_unread_filter_test` — « Non lues » lists unread entries only; « Tout marquer lu » empties it (its empty state, « Toutes » as the way out) and the bell |
| P1 — team pages | `team_pages_visibility_test` — members read TEAM and PUBLIC pages; an outsider of a public team only sees the PUBLIC one (`TeamDetailDto.pages` is filtered by the same rule as reading a page) |
| Account — sign-up (`flow-account.e2e.ts`) | `sign_up_terms_test` — terms unticked: inline error and no mail; ticked: back to the login with the address filled in, the verification mail leaves, no sign-in before it is followed. |
| Account — e-mail verification (`flow-account.e2e.ts`) | `sign_up_verify_test` — the same form, then the link read from mailpit, opened as a mail link: the page signs the rider in, the password works from then on |
| Account — forgotten password | `password_reset_test` — a link opened while signed out is kept; « Mot de passe oublié ? » → mailed link → new password → signed in on that post; old password refused. The link replayed after logout shows « Lien invalide »; « Demander un nouveau lien » with an unknown address shows the same screen and sends no mail |
| Account — deletion | `account_deletion_test` — sole admin of a team with a member: blocked banner names the team, no confirmation. Once alone: the sheet names the team, confirming signs out, the login and another session are refused, the public team answers 404, and the login form refuses the address |
| Profile — participations | `profile_participations_list_test` (a ride joined in the app is listed under « Mes sorties à venir » and opens from there); `profile_participations_count_test` — the badge counts a registration made in the app |
