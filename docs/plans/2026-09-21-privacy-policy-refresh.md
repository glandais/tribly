# Privacy policy refresh — 2026-09-21

Follow-up to [2026-07-25-privacy-policy-audit.md](2026-07-25-privacy-policy-audit.md) after ~392 commits
landed on develop since `da38f5ce` (notifications pipeline, self-service export, Wahoo, time zone, SSR
session, served basemaps, mobile location, invitations, beta sign-ups, ad contact relay, biketeam
migration, Garmin logout). Findings were verified against code; this is the evidence ledger.
"PP" = `privacy/privacy-policy.{en,fr}.md` (kept strictly parallel), "ToS" =
`privacy/terms-of-service.{en,fr}.md`, "Opp" = `2026-07-25-privacy-improvement-opportunities.md`.
Both policies and both ToS now read "Last updated: September 21, 2026".
Finding IDs (`NTF-nn`, `ACT-nn`, `MAP-nn`, `CLI-nn`, `Rn`) are local to this document, not ledger IDs.
`docs/SECURITY_AUDIT.md` H1 has since been fixed (a code is burnt after 5 wrong attempts); M4 is still open.

## Policy and terms changes

| ID | Section | Change | Evidence | Commit |
|----|---------|--------|----------|--------|
| NTF-01, NTF-09 | PP §1 new "Notifications" | What a notification stores (type, dates, read state, snapshot incl. actor, team, subject, 280-char reply excerpt), defaults per type, only overrides stored, push device record, lock-screen visibility | `V37__notifications.sql:14-41,57-67,100-110`; `V39__push_devices.sql:7-21`; `NotificationRecipientResolver.java:48,156-160`; `NotificationDispatchService.java:213-225`; `NotificationType.java:21-33`; `push_device_repository.dart:45-63`; `FcmClient.java:145` | eca2a3fe, e241d921, 0bd0db44 |
| NTF-02, ACT-07 | PP §4 Brevo row + paragraph | Brevo also receives invitation, ad-contact, notification and export e-mails; "single-use" limited to auth items | `EmailNotificationSender.java:38-52`; `TeamInvitationEmailService.java:58-70`; `AdContactEmailService.java:40-55`; `application.properties:438-458` | eca2a3fe, 7e195b8f, 41da5201 |
| NTF-03, MAP-5 | PP §4 providers | New rows: Google FCM, Apple APNs (relayed by FCM) | `FcmClient.java:111-165`; `PushNotificationSender.java:65-72,110-121`; `NotificationChannel.java:15-19`; `application.properties:482` | e241d921, a0a1f99f |
| NTF-04, MAP-5 | PP §5 | Bullets: Firebase contacts Google at every launch; push content via Google/Apple (US); TODO extended to Google/Apple/Garmin safeguards | `main.dart:28-36`; `push_gateway.dart:162`; `FcmClient.java:39-40,144-157` | 0bd0db44, 48cd3df1 |
| NTF-05 | PP §3 | Row: notifications (inbox/e-mail/push) = performance of contract with per-type opt-out (not consent: Android ≤12 registers without prompt) | `NotificationChannel.java:22-25`; `NotificationResource.java:133-158`; `push_provider.dart:84-92` | eca2a3fe |
| NTF-06, R4 | PP §6 + §9 Deletion | Rows: notifications 90 d, settings, push devices (no inactivity purge); reply extract survives comment deletion | `application.properties:470`; `NotificationRetentionService.java:34-56`; `NotificationScheduler.java:66`; `PushDeviceRepository.java:63-75`; `PushNotificationSender.java:74-98`; `CommentService.java:207-218` | eca2a3fe, ab2fe5f9 |
| NTF-07, R9 | PP §1 end + §6 closure | Sign-out drops only this device's push registration; closure keeps push devices, settings, inbox; no notification to closed accounts | `auth_provider.dart:289-320`; `AuthService.java:462-465`; `UserService.java:112-118`; `NotificationDeliveryService.java:113-118` | e241d921, a0a1f99f |
| NTF-08, R2 | PP §7 | Archive does not yet include time zone, ad-contact setting, notifications/settings, push devices, invitations, ad-contact records | `UserExportBuilder.java:153-183`; `AccountExport.java:37-52` | 71af8eae |
| NTF-10, R3, ACT-01, CLI-4 | PP §1 Preferences | Unit, theme, language, time zone, ad-contact flag, notification settings stored in account once chosen; e-mail language rules; Paris-time fallback. "French only" and "language not stored" removed | `User.java:45-69`; `UserService.java:80-103`; `AuthEmailService.java:28-32`; `NotificationTexts.java:33,67-73,85-90`; `TimezonePreference.tsx`; `LanguageSwitcher.tsx:12-19` | 53a1687f, 15a4d7b4, 41da5201, e066417e, 1624d4fc |
| NTF-11, CLI-5, MAP-11 | PP §1 local storage (mobile) + §8 | Display prefs incl. map/hillshade/list layout, map cache, downloaded files in temp dir, push token kept on device; only Firebase messaging, no analytics | `config_provider.dart:23,50`; `user_preferences_provider.dart:90-92`; `route_list_provider.dart:82-83`; `media_attachments.dart:108-127`; `push_provider.dart:73-80`; `pubspec.yaml:83-91`; `GoogleService-Info.plist` | e066417e, 4d82d193, 5ae69cd8, b661de4c |
| NTF-12 | PP §11 | "by email or in-app notification" → "by email" + TODO (no policy-change type) | `NotificationType.java:21-33` | — |
| NTF-13, MAP-9, ACT-12 | ToS §2 | Notifications bullet; GPS bullet incl. Wahoo; ad contact relay; invitations; member-list visibility; route planner "when enabled" | `NotificationType.java`; `GpsServiceType` (WAHOO); `AdService.java:261-297`; `TeamInvitationService.java:104-158`; `V32`, `V33` | eca2a3fe, 2f267211, 41da5201, 7e195b8f, 311b8334 |
| NTF-14 | `mobile/store-metadata/data-safety.md` §1 | Push token claim corrected: Android ≤12 / previously granted → auto-registration at signed-in start; Firebase init at launch | `push_provider.dart:84-92`; `push_gateway.dart:147-150,239-246`; `main.dart:28-36`; `AndroidManifest.xml:115-130` | 0bd0db44 |
| R1, CLI-7 | PP §7 | Export available in the mobile app (Profile → Your data → Request an export) | `profile_page.dart:83-88`; `data_and_account_section.dart:37-47`; `en.json:790,798` | fce64d47 |
| R5 | PP §6 + §7 | Rows: export ZIP 7 d (+30 d backups), export history 90 d | `application.properties:549-558`; `UserExportCleanupScheduler` | 71af8eae, 1c03fec7 |
| R6, ACT-03, ACT-04 | PP §1 new "Team Invitations", §2, §3, §6 | Invitation data, hashed link, per-address cap, language, 14 d validity, 365 d retention; "from other members" collection bullet; legal basis | `V34__team_invitations.sql:16-35`; `application.properties:114-120`; `TeamInvitationScheduler.java:28-38`; `TeamInvitationRepository.java:118-123`; `UserInvitationResource.java:37-61` | 7e195b8f |
| R7, ACT-08, CLI-8 | PP §1 new "Beta Programme Sign-ups", §2, §3, §4 admin bullet, §5, §6 | Data, no confirmation, admin list across sites, manual hand-off to TestFlight/Play/Connect IQ; no retention → row + TODO | `V35__beta_signups.sql`; `BetaSignupService.java:29-44`; `AdminBetaSignupResource.java` | 116347e9, dad1042f |
| R8, ACT-06 | PP §1 Content + §6 | "Messages about a classified ad" (2,000 chars, reply-to = sender, body not stored, 10/h); ad_contacts kept until ad hard-deleted | `V29__ad_contact.sql`; `AdService.java:261-297`; `AdContactEmailService.java:40-55`; `application.properties:100-103` | 41da5201, 60cfca8d |
| R10, CLI-2 | PP §9 Secure cookies | SameSite=Strict → Lax + cross-site request filter | `application.properties:66,342`; `RefreshTokenCookieFactory.java:36-37`; `CrossSiteRequestFilter.java` | f0d224d0, 79761ce9 |
| R11, CLI-9 | PP §1 GPS device + §6 row | Device sign-out clears local tokens only; server session lives 90 d; only "Sign out of every device" revokes it (no per-device removal) | `HomeMenuDelegate.mc:93-99`; `AuthManager.mc:80-85`; `karoo MainActivity.kt:552-558`; `AuthService.java:462-465` | d3776226 |
| R12 | PP §9 TODO | Four TODOs kept (still unresolved); brute-force TODO now cites docs/SECURITY_AUDIT.md H1 (high) / M4 (medium) | `AuthCleanupScheduler.java:32-44`; `UserService.java:110-117`; `LoggedInterceptor.java:19-25`; `AuthService.java:276-291`; `docs/SECURITY_AUDIT.md:25,48` | 1ac13534 |
| MAP-1 | PP §2 + §4 map table/intro | Place search now proxied by our server (signed-in only, 24 h cache); Nominatim split into its own row | `GeocodeResource.java:28-37`; `NominatimClient.java:23,32`; `NominatimLookup.java:42`; `application.properties:243-249` | b379128c |
| MAP-3 | PP §4 | Row: Michelin basemap (tiles.pedalons.fr) + TODO(owner) on who runs it | `application.properties:223-229`; `MapStyleService.java:86-104` | b379128c |
| MAP-4 | PP §4 intro + Mapterhorn row, §5 Esri | Basemap list from our server, tiles direct; relief only when switched on; Esri also from the app | `map_style_options.dart:10-20,45-55`; `config_provider.dart:65-66`; `mapStyleStore.ts:33-34`; `MapStyleService.java:89` | 4488bd52, 7d963e91, b661de4c |
| MAP-7, CLI-1 | PP §1 location + Session Data + TODO | Mobile "Around me" (coarse, one reading, sent with route-list requests while on, logged); Garmin reads position when main screen shown | `location_service.dart:92-97`; `routes_near_me_controls.dart:108-127`; `route_repository.dart:40-41`; `AndroidManifest.xml:13-14`; `PedalonsView.mc:20-31`; `ApiClient.mc:283-291` | 42213120, bf403613, 184c32ba |
| MAP-8 | PP §1 third-party + §4 row | External ID only where returned (not Wahoo); what is sent per service; Wahoo scopes | `WahooClient.java:60-65,183-185,277-288`; `GpsService.java:256-289` | 2f267211, 29c4abd7 |
| MAP-10 | ToS §6 new "Maps and geographic data" | Credits (OSM/ODbL incl. Valhalla & Nominatim, CyclOSM, VersaTiles, IGN, Esri, Mapterhorn, Michelin) + as-is | b379128c commit message; `application.properties:185-229` | b379128c |
| ACT-02 | PP §1 memberships + §4 display name | Member list rules (organisers, directory off by default, admin e-mail search); site-wide user search removed | `UserTeamAccessChecker.java:47-49`; `TeamMembershipService.java:63-70`; `V33__member_directory_toggle.sql:4` | 7e195b8f |
| ACT-05 | PP §1 ads + location | Ads team-only; exact point to author/team admins/platform admins, ~1 km area to others; photo GPS warning | `AdDto.java:63-72,111-113`; `CoarseLocation.java:29-58`; `AdService.java:313`; `AdAccessChecker.java:43-61` | 53a1687f, c9063307, 6abd777e, 9088ce9c |
| ACT-09 | PP §1 imported accounts + TODO | Verified flag, bcrypt hash on proven e-mail, case-conflict merge, placeholder forms; TODO: import still open, deletion flag ignored on first import | `BiketeamMigrationService.java:608-635,682-691,705-731,762-803` | 0a4c211a, 5e9c0f21 |
| ACT-10 | PP §6 content row + §9 Deletion | Soft-deleted items still visible to team admins, "Deleted" badge | `IncludeDeletedService`; `AdDto.java:87` | ebb87094 |
| ACT-11 | PP §1 GPS tracks / imported files | Route planner off by default; tracks served contain only position/altitude/distance | `V32__route_planner_toggle.sql:1-2`; `GpxPreviewService.java:185-189`; `TrackDto` | 311b8334, 31baa525 |
| ACT-13 | ToS §4 | No unsolicited messages / harassment / address harvesting via ad contact or invitations | `application.properties:102-103,114-118`; `AdContactEmailService.java:55` | 41da5201, 7e195b8f |
| CLI-3 | PP §1 web local storage + §8 row | refresh_token sent on page loads (SSR), expiry renewed on refresh, session still 30 d; SSR pages carry 15-min token, no-store | `RefreshTokenCookieFactory.java:28,46-51`; `AuthResource.java:249-256`; `ssrSession.ts:27-65`; `server.js:159-166,204-209` | f0d224d0, 1e0f6669 |
| CLI-6 | PP §8 + §1 web | Row: `pendingInvitationToken` (sessionStorage) | `AcceptInvitationPage.tsx:24,50,69` | 7e195b8f |
| (refresh) | PP §1/§6 wording | "log out of all devices" renamed to the real label "Sign out of every device", located in the mobile app's profile; TODO(owner): the website has no such button | `mobile/assets/l10n/en.json:805`; only `frontend/src/api/endpoints/authentication/authentication.ts` references logout-all | fce64d47 |

## TODO(owner) comments

- **Removed:** none — the July TODOs are all still open (R12).
- **Updated:** imported-accounts TODO (import still open; deletion flag gap), server-log TODO (mobile
  nearLat/nearLon), EXIF TODO (ad photos first), §5 transfer TODO (Google Firebase, Apple, Garmin
  Connect IQ), account-closure TODO (push devices), §9 brute-force TODO (docs/SECURITY_AUDIT.md H1/M4).
- **Added:** "Sign out of every device" missing on the website (§1); DPA and transfer safeguard for
  FCM/APNs before push goes live (§4); who runs `tiles.pedalons.fr` (§4); beta sign-up retention period
  (§6); restore "in-app notification" in §11 once a policy-change type exists.

## Opportunities doc

Done: #2 (iOS manifest, d0705864/0bd0db44), #4 (export, 71af8eae/1c03fec7/fce64d47), #9(b)
(geocoding proxy, b379128c). Amended: #5 (ad photos), #7 (mobile coordinates), #8 (Firebase), #9 (tile
proxy now easy, geocode query in logs), #12 (OTP → P0), #14 (concrete strings, data-safety §5), #15
(notification mechanism exists, unsubscribe), #18 (mobile uses a symlink). New: #20 push device
lifecycle, #21 GPS-device logout revocation, #22 export completeness, #23 Firebase deferral, #24 push
content minimisation, #25 token out of URL, #26 invitation/ad-contact/beta retention, #27 Biketeam
display-name fallback.

## Follow-up after rebase on 1a5af242

`1a5af242` (push wired for production, notifications in the export, purge at account closure) landed
on develop after the refresh. Adjusted in both policies:

| Section | Change | Evidence |
|---------|--------|----------|
| PP §6 closure paragraph | Closure erases inbox (with its deliveries), notification settings and push devices; notifications the user triggered for others keep their display name until the 90-day expiry | `UserService.java` `deleteUser` → `NotificationService.forgetUser` |
| PP §6 table | Push device row: "until you close your account"; notification settings "erased when you close it" | same |
| PP §7 export | Archive now includes 90 days of notifications with their sends, notification settings and push devices; push token listed among withheld secrets; "does not yet include" list shortened | `UserExportBuilder.java` sections `notifications/inbox.json`, `account/notification-preferences.json`, `account/push-devices.json`; `NotificationExport.PushDeviceEntry` |
| PP §4 TODO | Push goes live with `PEDALONS_PUSH_ENABLED=true` + FCM service account in `data/keys` | `application.properties` push block; `.env.example` |
| PP §6 TODO (Art. 17) | Notes push devices/settings/inbox already erased at closure | — |
| data-safety.md #11 | Push token also deleted server-side at account closure | — |
| Opp #20, #22 | Marked partly done | — |

## Follow-up after rebase on 2a1af2b3

develop landed its own policy edits meanwhile (cc8c05f9, 6d9a5d3e, fe4f39b5, f548a747). The rebase kept
this branch's wording in every conflicting hunk, then folded develop's facts back in:

| Section | Change | Evidence |
|---------|--------|----------|
| PP §1 profile picture | Mobile uses the system photo picker: no camera, no library access | `image_picker`, 6d9a5d3e |
| PP §1 on-device storage | Android: app data excluded from cloud backup and device transfer | `AndroidManifest.xml` `allowBackup=false`, `data_extraction_rules.xml` |
| PP §3 | "Display maps — legitimate interest" row (owner's choice in cc8c05f9); basis dropped from the §4 TODO | cc8c05f9 |
| PP §4 maps | Providers act as independent controllers; fonts bundled, map-label glyphs still from VersaTiles | cc8c05f9, `mobile/pubspec.yaml` `fonts:` |
| PP §4 / §5 | Google Fonts row and transfer bullet removed, TODOs trimmed | Inter bundled in `mobile/assets/fonts/inter/` |
| PP §6 | Account-data, notification-settings, push-device and "after deletion" rows: erased at deletion; closure paragraph and its Art. 17 TODO removed | `AccountErasureService` |
| PP §7 | New "Delete your account" section (develop's text, completed: GPS-device sessions, calendar token, Strava link, ad-contact records, ads blanked, others' notifications keep the name 90 days, provider-side OAuth grant not revoked) | `AccountErasureService.erase`, `NotificationService.forgetUser` (recipient only) |
| PP §9 deletion | Accounts no longer soft-deleted; content still is (develop's "effective deletion" bullet over-claimed for content) | `AccountErasureService`; content `deleted` flag unchanged |
| Terms §8 | Deletion immediate and irreversible; content credited to "Ancien membre"; comments and ads erased | same |
| Opp #1, #8, #10, #20 | Marked done / partly done | — |

## Rejected

None — every finding submitted for this refresh was confirmed (corrections noted in the rows above
were folded into the applied wording).

## Sync

`pnpm copy-legal` resyncs the gitignored `frontend/src/assets/legal/`. The mobile app reads
`mobile/privacy`, a symlink to `../privacy`, so it needs no copy.

## Open points moved out of the policy (2026-09-29)

Removed during the rebase onto develop (2026-09-29): the policy files are rendered verbatim on the
public /privacy page (react-markdown without rehype-raw) and in the app, so HTML comments showed up
as text. The branch had 16 per language; FR carried the same notes. Text below is the EN version,
with stale facts updated. Candidates for `LEGAL-n` / `SEC-n` entries in `docs/LEDGER_NEXT.md`.

### §1 Account Data — Accounts Imported from a Previous Platform (now "Accounts and Teams Coming from Biketeam")

Name the source platform operator and the legal basis relied on for the earlier dump imports
(contract / legitimate interest), state the date range of those imports, and confirm whether the
members concerned were informed individually (GDPR Art. 14 requires notice within one month of
obtaining data indirectly) — `LEGAL-1`, closed 2026-09-29 as moot: no imported member account remains
in staging or prod, and the policy now describes team transfers only.

Updated: the dump import no longer exists on develop (d93fd3af removed it, including
`BiketeamMigrationService.updateUser`/`createUser`), so the "still being re-run from fresh dumps"
remark and the "deleted-on-Biketeam account imported active" bug are moot; the range is now closed.
The live migration (01968985) imports no member data. Open: `biketeam_migrations` rows are never
deleted (`BiketeamMigrationJobRepository`: "Nothing is ever deleted") and are not touched by
`AccountErasureService` — the policy now says so; decide on a retention period or anonymise them.
No placeholder `strava_…`/`facebook_…`/`google_…` account remains in staging or prod (checked
2026-09-29); the policy's paragraphs about them were removed.

### §1 Session Data — server logs

State the actual server-log rotation/retention period configured on the host and add it to the §6
table — no retention limit is configured today. Consider moving the Garmin app's lat/lon and the
mobile app's nearLat/nearLon out of the query string (or rounding them) so coordinates stop being
logged.

### §1 Location and GPS Data — imported GPX files

Consider stripping `<time>` and gpxtpx sensor extensions at import (privacy by design, GDPR
Art. 5(1)(c); Art. 9 for heart rate) — that would allow removing the "Data contained in the files you
import" disclosure.

### §1 Content You Create — photo metadata

Consider stripping EXIF on upload instead (ad photos first) — then replace the photo-metadata
paragraph with a statement that metadata is removed.

### §1 Data Stored Locally — GPS device

If you would rather encrypt the Karoo token store (e.g. encrypted DataStore or Keystore-wrapped
values), do that and delete the sentence about the absence of additional encryption.

### §1 Data Stored Locally — sign out of every device

The website has no "sign out of every device" button (only the mobile app does, auth/logout-all;
develop's frontend has the generated `logoutAll` endpoint but no page uses it) — add one on the web
profile or keep naming the app here.

### §4 Visibility — platform administration

Confirm how many people hold PLATFORM_ADMIN and whether administrator access is logged; if it is,
say so here — if not, consider adding audit logging.

### §4 Technical Service Providers — push providers

Original: confirm the contract (DPA) and transfer safeguard for Google Firebase Cloud Messaging and
Apple Push Notification service before push is switched on in production.

Updated: push is live (mobile since 2026-09-21, web since 2026-09-29; `PEDALONS_PUSH_ENABLED=true` in
prod, 7b961dd1). The policy now states develop's FCM safeguard (Google Ireland Limited, SCC + Google
LLC's EU–US DPF certification). Still to confirm: the DPA with Google, and the safeguard for Apple
APNs and for browser vendors' push services (`LEGAL-5`).

### §4 Map Display — tiles.pedalons.fr

Resolved: develop states the Michelin background is served by us (tiles.pedalons.fr). The policy now
says so. If it ever relays or caches third-party tiles, name the operator and country.

### §4 Map Display — Mapterhorn and provider policies

Confirm where tiles.mapterhorn.com is hosted (add to §5 if outside the EU, or self-host the terrain
tiles); add each map provider's privacy policy link. Also confirm where OpenStreetMap France serves
CyclOSM from (the policy no longer claims EU hosting for it).

### §5 International Transfers — safeguards

Original: state the transfer safeguard relied on for Strava, Google (FCM), Apple (push, TestFlight)
and Garmin Connect IQ; Firebase keeps Google in this section unless it is initialised only when push
is wanted; confirm Mapterhorn's hosting country.

Updated: Strava is gone; Google (FCM) and GitHub safeguards are now stated. Still open: Apple (APNs,
TestFlight), Garmin Connect IQ, browser push services (`LEGAL-5`), Mapterhorn. The mobile app still
calls `Firebase.initializeApp()` at start (main.dart), so the Firebase bullet stays. New: team
webhooks can send announcements to Slack/Discord (US) — the policy presents this as the team's
choice; decide whether that framing is enough.

### §6 Data Retention — beta sign-ups

Choose a retention period for beta sign-ups and implement it (e.g. delete once the beta has opened and
the address has been invited, or after 12 months) — see opportunities.

### §6 Data Retention — clean-up jobs

Schedule the existing clean-ups for expired pairing codes and expired WebAuthn challenges, then
simplify the "not yet covered by a clean-up job" sentence and the corresponding table rows.
(Strava hand-off codes removed from this note: the table was dropped in V45.)

### §6 Data Retention — backups

State where the backup machine is hosted (country / provider), and confirm whether the backup volume
is encrypted at rest — the snapshot also contains the JWT signing keys and the encryption key for
stored GPS-service tokens.

### §9 Security — brute-force protection

Original: if per-IP / per-account throttling or lockout is added for password, sign-in-code and
passkey attempts, restore an explicit "Rate limiting" bullet. See SECURITY_AUDIT.md H1 and M4.
Consider hashing the calendar token to remove that exception.

Updated: H1 is fixed (911e93a2: an OTP is burnt after 5 wrong attempts,
`pedalons.auth.otp.max-verify-attempts`); the policy's §9 bullet now says so. M4 (no password
throttling) is still open — see `docs/SECURITY_AUDIT.md` (the file moved under `docs/`). Hashing the
calendar token is still worth considering (`LEGAL-8` context).

### §11 Changes to This Policy

Restore "or in-app notification" once a policy-change notification type exists (NotificationType has
none today — still true on develop).

### Facts stated in the merged policy that could not be checked in the repository

- Website: the Firebase messaging library keeps its own registration data in browser storage
  (IndexedDB) once web push is enabled — library behaviour, not visible in our code. The §8 row
  "Firebase messaging data" relies on it.
