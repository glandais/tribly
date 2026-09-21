# Data collection declarations — source of truth

Canonical record of what the **Pédalons mobile app** collects, and the exact answers to give in
each store's privacy form. Three artefacts must agree, and this file is the one they are derived
from:

| Artefact | Where | Kept in sync by |
|---|---|---|
| iOS privacy manifest | `mobile/ios/Runner/PrivacyInfo.xcprivacy` | hand-edit alongside this file |
| App Store Connect → App Privacy | App Store Connect (web form) | copy §4 into the form |
| Google Play Console → Data safety | Play Console (web form) | copy §5 into the form |
| Published privacy policy | `privacy/privacy-policy.{en,fr}.md` | must not contradict §2 |

- **App**: Pédalons, `fr.pedalons.mobile`, version `1.0.0+23` (`mobile/pubspec.yaml`)
- **Backend**: `https://www.pedalons.fr` (`mobile/lib/config/app_config.dart`)
- **Last verified against the code**: 2026-09-21 (push notifications added)

> Scope note. These declarations describe **the mobile app binary**, not the whole Pedalons
> platform. The web frontend can do considerably more than the app (see §7). Declaring platform
> capabilities the app does not have is as wrong as omitting ones it does.

---

## 1. What the app actually does

The mobile app authenticates, then reads team content. Three capabilities beyond reading touch
personal data: **picking a profile picture** from the photo library, reading a **coarse
device position** to sort content by proximity, and registering the device for **push
notifications**. There is no content authoring, no camera capture, no file import, and no
analytics.

Three capabilities added, and their exact boundary:

- **`image_picker`** — photo library only, to feed `POST /api/users/me/avatar`. No camera
  capture (`NSCameraUsageDescription` is deliberately absent, so iOS cannot grant it).
  `NSPhotoLibraryUsageDescription` exists in `mobile/ios/Runner/Info.plist`, localized in
  `Runner/{en,fr}.lproj/InfoPlist.strings`.
- **`geolocator`** — `LocationAccuracy.low` only, while the app is in use. The coordinates become
  the `nearLat`/`nearLon`/`nearRadius` query parameters of an existing list endpoint and are never
  written to disk nor attached to the account. `NSLocationWhenInUseUsageDescription` (fr + en);
  Android requests **`ACCESS_COARSE_LOCATION` only** — `ACCESS_FINE_LOCATION` is still stripped
  with `tools:node="remove"` (see §5). No background location, no MapLibre user-location puck, no
  ride recording.

- **`firebase_core` + `firebase_messaging`** — Firebase Cloud Messaging, *messaging only*. The app
  sends the FCM registration token to `POST /api/push-devices` with the device model and the app
  version, and deletes it with `DELETE /api/push-devices/{token}` on sign-out. Nothing else of
  Firebase is initialized: **no Analytics, no Crashlytics, no Performance, no Remote Config, no
  Installations-based measurement** — the Firebase project itself was created with Analytics
  switched off (`docs/plans/2026-09-18-notifications-ledger.md`). The token is requested **only
  after the member grants notification permission from the notifications screen**
  (`lib/features/notifications/providers/push_provider.dart`); the app never asks at launch.
  `device_info_plus` is now on an executed path — it supplies that device name, and nothing else.

Verified absent from `mobile/pubspec.yaml`, `mobile/pubspec.lock`, `mobile/lib/`, `mobile/ios/`
and `mobile/android/`:

- No `camera`, `photo_manager`, `image_cropper`, `file_picker` — the only media entry point is
  the system photo picker.
- No precise location, no background location, no `permission_handler` (permission prompts are
  `geolocator`'s own), and no MapLibre user-location layer.
- No GPX/FIT **import**. Route files are download-only: fetched to `getTemporaryDirectory()` and
  handed to the OS share sheet (`lib/features/routes/presentation/pages/route_detail_page.dart`).
  Coordinates flow server → device, never device → server.
- No analytics, crash-reporting, advertising or attribution SDK (no Crashlytics/Sentry/Amplitude/
  AppsFlyer/Adjust). Firebase **is** present, but only `firebase_core` and `firebase_messaging` —
  see the push entry above; no other Firebase product is a dependency, which
  `grep -n "firebase" pubspec.yaml` shows in two lines. `ua_client_hints` remains a transitive
  dependency of `passkeys` and is on no executed code path; `device_info_plus` no longer is —
  it names the device at push registration.
- No StoreKit / `in_app_purchase` / payment SDK.
- The only multipart / `FormData` upload reachable from the UI is the avatar upload
  (`POST /api/users/me/avatar`). Every other multipart operation in the generated client stays
  unreachable.

The generated client in `mobile/lib/api/generated/` mirrors the **entire** backend API (avatar
upload, post/comment/route CRUD, admin). Almost none of it is wired to UI — it is dead code and
must not be read as evidence of collection. See §7 for the trigger list.

---

## 2. Data inventory (evidence)

Everything below leaves the device to `https://www.pedalons.fr` unless stated otherwise.

| # | Data | Collected where | Off-device? | Linked to identity |
|---|---|---|---|---|
| 1 | **Email address** | Registration, login, forgot-password, reset-password forms — `lib/features/auth/presentation/pages/{login_page,forgot_password_page,reset_password_page}.dart` | Yes | Yes |
| 2 | **Password** | Same forms; `POST /api/auth/register`, `/login`, `/reset-password` | Yes (over TLS, hashed server-side) | Credential — see note |
| 3 | **Display name** | Registration only — `RegisterRequest(email, displayName, password)`, `lib/features/auth/providers/auth_provider.dart` | Yes | Yes |
| 4 | **Account / user ID, session tokens** | `GET /api/users/me`, JWT subject, refresh token | Yes | Yes |
| 5 | **WebAuthn credential material** | `lib/features/auth/services/passkey_service.dart` — credential id, rawId, clientDataJSON, signature, userHandle, plus the literal device label `"Mobile"` | Yes | Yes |
| 6 | **Ride / trip / team participation** | Join & leave — `lib/features/rides/data/ride_repository.dart`, `lib/features/trips/data/trip_repository.dart`, `lib/features/teams/data/team_repository.dart` (team join/leave is wired in the repository but has no UI entry point yet) | Yes | Yes |
| 7 | **GPS-device pairing code** | 6-character code — `lib/features/device/presentation/pages/device_verify_page.dart` | Yes | Yes |
| 8 | **Session security metadata** | Server-recorded on sign-in: IP address, user agent, last-login / last-use timestamps (per `privacy/privacy-policy.en.md` §"Session Data") | Yes | Yes |
| 9 | **Profile picture** | Photo chosen from the system photo library (`image_picker`) and sent to `POST /api/users/me/avatar` | Yes | Yes |
| 11 | **Push registration token** | `lib/features/notifications/providers/push_provider.dart` → `POST /api/push-devices`. Issued by FCM, identifies the *installation*, deleted server-side at sign-out | Yes, to us **and to Google** (FCM issues it and routes every message) | Yes |
| 12 | **Device model and app version** | Sent alongside #11 — `lib/features/notifications/data/push_device_repository.dart` (`device_info_plus`, `package_info_plus`) | Yes | Yes |
| 10 | **Approximate location** | `geolocator` at `LocationAccuracy.low`, while in use, only when the user turns on the "around me" filter — becomes the `nearLat`/`nearLon`/`nearRadius` query parameters | Yes, as query parameters of a read request | **No** — not stored server-side, not written to the account |

Stored **on device only**, never transmitted:

- `refresh_token` in the iOS Keychain / Android Keystore via `flutter_secure_storage`
  (`lib/features/auth/data/secure_storage.dart`), cleared on logout.
- Access token in memory only (`lib/api/pedalons_api_client.dart`), never persisted.
- UI locale via `shared_preferences` (written by `easy_localization`).
- Disk caches: downloaded avatars/images (`cached_network_image`) and temp GPX/FIT files.

**Password note.** Neither store's form has a "password" data type. Apple's guidance is that
credentials are covered by the account data types already declared; Google's form likewise has no
password category. Passwords are therefore not a separate line item in §4/§5 — this is expected,
not an omission.

---

## 3. iOS privacy manifest

Mirrors `mobile/ios/Runner/PrivacyInfo.xcprivacy`. Values are the literal strings Apple accepts
(see *Describing data use in privacy manifests*); Xcode will not generate a privacy report if a
value is invented.

- `NSPrivacyTracking` = `false` — no tracking as ATT defines it.
- `NSPrivacyTrackingDomains` = empty — nothing to declare while tracking is false.

| `NSPrivacyCollectedDataType` | Linked | Tracking | Purposes | Covers |
|---|---|---|---|---|
| `NSPrivacyCollectedDataTypeEmailAddress` | `true` | `false` | `…PurposeAppFunctionality` | inventory #1 |
| `NSPrivacyCollectedDataTypeName` | `true` | `false` | `…PurposeAppFunctionality` | #3 |
| `NSPrivacyCollectedDataTypeUserID` | `true` | `false` | `…PurposeAppFunctionality` | #4, #5, #7 |
| `NSPrivacyCollectedDataTypeOtherUserContent` | `true` | `false` | `…PurposeAppFunctionality` | #6 |
| `NSPrivacyCollectedDataTypeOtherDataTypes` | `true` | `false` | `…PurposeAppFunctionality` | #8 |
| `NSPrivacyCollectedDataTypePhotosorVideos` | `true` | `false` | `…PurposeAppFunctionality` | #9 |
| `NSPrivacyCollectedDataTypeCoarseLocation` | **`false`** | `false` | `…PurposeAppFunctionality` | #10 |
| `NSPrivacyCollectedDataTypeDeviceID` | `true` | `false` | `…PurposeAppFunctionality` | #11, #12 |

`…Purpose` above abbreviates `NSPrivacyCollectedDataTypePurpose`. Note the lowercase `or` in
`PhotosorVideos` — that is Apple's literal spelling, not a typo.

`CoarseLocation` is the only **unlinked** row: the coordinates travel as query parameters of a
read request and are never persisted against the account. Every other row is linked.

`Info.plist` carries the matching usage strings — `NSPhotoLibraryUsageDescription` and
`NSLocationWhenInUseUsageDescription`, plus `LSApplicationQueriesSchemes` (`webcal`, `mailto`,
`tel`) for `url_launcher`. The user-facing text is localized in
`ios/Runner/{en,fr}.lproj/InfoPlist.strings`; the values inline in `Info.plist` are the English
fallback and must stay in sync with `en.lproj`.

`NSPrivacyAccessedAPITypes` is unchanged and remains the standard Flutter set:
`UserDefaults` (`CA92.1`, `shared_preferences`), `FileTimestamp` (`C617.1`, `path_provider` /
`flutter_cache_manager`), `SystemBootTime` (`35F9.1`, elapsed-time measurement), `DiskSpace`
(`E174.1`, cache eviction).

Validate after editing:

```bash
plutil -lint mobile/ios/Runner/PrivacyInfo.xcprivacy
```

---

## 4. App Store Connect → App Privacy

Answer **"Yes, we collect data from this app"**, then declare exactly these eight, none used for
**Tracking**, all purpose **App Functionality**. All are **Data Linked to You** except Coarse
Location, which is **Data Not Linked to You**:

| Category | Data type | Linked | Tracking | Purpose |
|---|---|---|---|---|
| Contact Info | Email Address | Yes | No | App Functionality |
| Contact Info | Name | Yes | No | App Functionality |
| Identifiers | User ID | Yes | No | App Functionality |
| User Content | Other User Content | Yes | No | App Functionality |
| User Content | Photos or Videos | Yes | No | App Functionality |
| Location | Coarse Location | **No** | No | App Functionality |
| Identifiers | Device ID | Yes | No | App Functionality |
| Other Data | Other Data Types | Yes | No | App Functionality |

For *Other Data Types*, describe it as: **"Session security metadata (IP address, user agent and
sign-in timestamps) recorded to detect suspicious account activity."**

*Device ID* covers the **push registration token** and the device model sent with it (#11, #12) —
not an advertising identifier, which the app still never reads.

Explicitly answer **No / do not select**: Precise Location, Audio Data, Contacts, Health, Fitness,
Payment Info, Purchase History, Product Interaction, Advertising Data, Crash Data,
Performance Data, Search History, Browsing History.

Privacy policy URL: `https://www.pedalons.fr/privacy` (EN) · `https://www.pedalons.fr/confidentialite` (FR).

---

## 5. Google Play Console → Data safety

Same substance as §4, remapped to Google's taxonomy. The two taxonomies are not identical — the
divergences are called out below and are deliberate.

**Store-level answers**

| Question | Answer | Basis |
|---|---|---|
| Does your app collect or share any of the required user data types? | **Yes** | §2 |
| Is all of the user data collected by your app encrypted in transit? | **Yes** | HTTPS-only base URL; no `usesCleartextTraffic`, no `networkSecurityConfig`, no `NSAppTransportSecurity` exception |
| Do you provide a way for users to request that their data be deleted? | **Yes** | see below |
| Data deletion URL | `https://www.pedalons.fr/profile` | web profile "danger zone" → `useDeleteCurrentUser` (`frontend/src/pages/auth/UserProfilePage.tsx`, `frontend/src/hooks/useAuth.ts`) |
| Privacy policy URL | `https://www.pedalons.fr/privacy` | |

⚠️ **Open item** — Play policy requires an **in-app** account-deletion path for apps that allow
in-app account creation, in addition to the web URL. The app registers accounts but has no
deletion UI. `DELETE /api/users/me` already exists in the generated client
(`lib/api/generated/clients/users_client.dart`) and is unused. See §8.

**Per-data-type answers.** *Shared* = No on every row. *Collected* and *Processed ephemerally* are
per-row, because approximate location is the one type that is processed ephemerally.

| Category | Data type | Collected | Processed ephemerally | Required / optional | Purposes |
|---|---|---|---|---|---|
| Personal info | Name | Yes | No | Required | App functionality, Account management |
| Personal info | Email address | Yes | No | Required | App functionality, Account management |
| Personal info | User IDs | Yes | No | Required | App functionality, Account management, Fraud prevention, security, and compliance |
| App activity | Other actions | Yes | No | Optional | App functionality |
| Photos and videos | Photos | Yes | No | Optional | App functionality |
| Location | Approximate location | Yes | **Yes** | Optional | App functionality |
| Device or other IDs | Device or other IDs | Yes | No | Optional | App functionality |

*Photos* is **optional**: the account works without a profile picture, and the photo library is
only reached when the user taps "change picture".

*Approximate location* is **optional** and **processed ephemerally**: it is read only when the user
turns on the "around me" filter, sent as `nearLat`/`nearLon`/`nearRadius` on a read request, and
neither stored on the device nor persisted server-side. It is not linked to the account and is not
used for tracking or advertising. Precise location is **not** collected — the app requests
`ACCESS_COARSE_LOCATION` only, and `LocationAccuracy.low`.

*Device or other IDs* is **optional** and covers the **push registration token** (#11) with the
device model sent beside it: it exists only for a member who turned notifications on, and
sign-out deletes it. It is **not** an advertising ID — none is read — and Google's own form has no
finer bucket for a messaging token.

Everything else in the form is **not collected**: Precise location, Financial info, Health and
fitness, Messages, Videos, Audio files, Files and docs, Calendar, Contacts, App interactions,
In-app search history, Installed apps, Other user-generated content, Web browsing history, Crash
logs, Diagnostics, Other app performance data.

**Deliberate divergences from §4**

1. *Ride/trip/team participation* → Play **"App activity → Other actions"** (Google's example for that
   type is likes and dialog choices, which is what a join/leave toggle is), but Apple
   **"Other User Content"** (Apple has no "actions" type). Same underlying data, different bucket.
2. *Session security metadata* (#8) is declared to Apple under **Other Data Types** but has **no
   Play equivalent** — Google's form has no IP-address or user-agent type, and "Device or other
   IDs" means device-level identifiers such as an advertising ID, which the app does not collect.
   Not declaring it on Play is correct, not an omission.
3. *Approximate location* (#10) is declared to both, but Play additionally has a **"processed
   ephemerally"** flag, which we set. Apple has no such flag — the equivalent signal there is
   `NSPrivacyCollectedDataTypeLinked = false`. Same fact, two encodings.

**There is no Android equivalent of `PrivacyInfo.xcprivacy`.** Google has no privacy-manifest file
that ships inside the APK/AAB — the Data safety declaration exists **only** as a Play Console web
form, which is exactly why §5 of this file exists. Nothing in `mobile/android/` encodes it, and
`fastlane`'s `upload_to_play_store` cannot upload it either (`skip_upload_metadata` in
`android/fastlane/Fastfile` is unrelated). If you prefer bulk entry, Play Console offers
*Data safety → Import from CSV*; download the template from the Console rather than hand-writing
one, and fill it from the tables above.

What Android *does* encode is the **permission set**, and that must not contradict §5.

**Location permissions.** `mobile/android/app/src/main/AndroidManifest.xml` declares
`ACCESS_COARSE_LOCATION` and **only** that one, matching the *Approximate location / no precise
location* answer above.

`ACCESS_FINE_LOCATION` is still stripped with `tools:node="remove"`. It was never in our source
manifest — the merger injects it from `org.maplibre.gl:android-sdk-opengl:13.0.3-pre0`, pulled in
by the `maplibre` plugin (`build/app/outputs/logs/manifest-merger-release-report.txt:528-535`) for
its LocationComponent (the user-location puck), which this app never enables. `geolocator` does not
need it either: `LocationAccuracy.low` is served by the coarse permission. Left alone it would show
on the Play listing and over-declare against §5.

**Notification permission.** `POST_NOTIFICATIONS` (Android 13+) is declared and requested from the
notifications screen, never at launch. It is a permission, not a data type: Play's form has no row
for it, and the data it enables is declared as *Device or other IDs* above. The merge also brings in
`VIBRATE` and `WAKE_LOCK` from FCM and `flutter_local_notifications` — how a notification buzzes and
wakes the screen, not data, and not declarable.

Expected merged release permission set: `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`,
`ACCESS_COARSE_LOCATION`, `POST_NOTIFICATIONS`, `VIBRATE`, `WAKE_LOCK`, `USE_BIOMETRIC`,
`USE_FINGERPRINT`, `USE_CREDENTIALS`, `CREDENTIAL_MANAGER_SET_ORIGIN` — and **no**
`ACCESS_FINE_LOCATION` (verified on the merged debug manifest, 21 September 2026). To re-check after a release
build:

```bash
# The FINAL merged manifest — not manifest-merger-release-report.txt, which keeps the rejected
# nodes in its decision log and so still mentions both permissions even when the strip worked.
grep -o 'android:name="android.permission.[A-Z_]*"' \
  mobile/build/app/intermediates/merged_manifest/release/*/AndroidManifest.xml | sort -u
```

The other merged permissions are not declarable data types and stay: `INTERNET`,
`ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` (MapLibre connectivity detection), `USE_BIOMETRIC`,
`USE_FINGERPRINT`, `USE_CREDENTIALS`, `CREDENTIAL_MANAGER_SET_ORIGIN` (passkeys), `VIBRATE` and
`WAKE_LOCK` (push).

Android needs no photo-library permission: `image_picker` goes through the system photo picker
(`ACTION_PICK_IMAGES` / `ACTION_GET_CONTENT`), which returns a single user-chosen item without
`READ_MEDIA_IMAGES`. Do not add that permission — it would over-declare.

---

## 6. Third parties contacted by the app

No SDK sends data to a third party other than FCM. **Map endpoints** outside our infrastructure
receive the device's IP address as an unavoidable consequence of an HTTP request. They are not
hard-coded in the app: the basemap list comes from `GET /api/config`
(`pedalons.map.*` in `backend/src/main/resources/application.properties`), so re-read that file
rather than this table when it changes.

| Endpoint | Why | What it sees |
|---|---|---|
| `tiles.versatiles.org` | Default basemap (style, vector tiles, glyphs, sprites) | IP address; the map viewport being browsed |
| `tiles.mapterhorn.com` | Relief shading (raster-DEM), when on | Same |
| `data.geopf.fr` (IGN Géoplateforme) | IGN, Satellite (IGN), SCAN 25 basemaps, when chosen | Same |
| `server.arcgisonline.com` (Esri, US) | Satellite (ESRI) basemap, when chosen | Same |
| `tile.openstreetmap.org` (OSMF, UK) | OpenStreetMap basemap, when chosen | Same |
| `*.tile-cyclosm.openstreetmap.fr` | CyclOSM basemap, when chosen | Same |
| Firebase Cloud Messaging (`*.googleapis.com`, APNs via Firebase) | Issues the registration token and routes every push (`lib/features/notifications/services/push_gateway.dart`) | IP address, the token it issued, the device and app version it registers, and **the content of each notification** — title and body are rendered server-side and travel through Google |

FCM is a genuine **sub-processor**: it does not merely see an IP address, it carries the message.
The map providers are independent controllers of a request the device makes; none receives an
account identifier. Both are in the published policy since 2026-09-21 (§4 *Technical service
providers* and *Basemaps*, §5 for the US and UK transfers).

**No font is fetched.** Inter is bundled (`assets/fonts/inter/`, declared in `pubspec.yaml`) since
2026-09-21; `google_fonts`, which pulled it from `fonts.gstatic.com` at first launch, is no longer
a dependency. Do not bring it back — it would re-open a US transfer for a typeface.

---

## 7. Not declared, and why

| Not declared | Why |
|---|---|
| Precise Location | Only `ACCESS_COARSE_LOCATION` is requested and only `LocationAccuracy.low` is used; `ACCESS_FINE_LOCATION` is stripped from the merged manifest. No background location, no ride recording, no user-location puck. Route coordinates are **downloaded** for display; describing where a *route* is is not describing where the *user* is. |
| Videos | `image_picker` is restricted to still images for the avatar; no video is ever read. |
| Camera | No `NSCameraUsageDescription` and no camera permission — the photo library is the only entry point. |
| Files and docs | GPX/FIT is download-and-share only; there is no import path. |
| Fitness / Health | No HealthKit, no Motion & Fitness, no activity recording. |
| Advertising ID | No advertising identifier is read or sent. The declared *Device ID* row covers the FCM registration token only (#11). The passkey `deviceName` is still the constant string `"Mobile"`. |
| Product Interaction / Usage / Advertising Data | No analytics or advertising SDK of any kind. |
| Crash Data / Performance Data | Nothing is collected by us. Apple- and Google-side crash reporting the user opts into is the platform's collection, not ours. |
| Purchase History / Payment Info | No purchases in the app. |
| Emails or Text Messages, Contacts, Audio, Search History, Browsing History | No corresponding capability. |

---

## 8. Update triggers

Re-run the audit and update **this file, the manifest, and both store forms together** when any of
these ships:

| If the app gains… | Add |
|---|---|
| Profile editing (`PUT /api/users/me`) | nothing new — Name/Email already declared |
| Camera capture for the avatar | `NSCameraUsageDescription` in `Info.plist` (+ its two `InfoPlist.strings`); no new data type — Photos or Videos already covers it |
| Post / comment / ride / route authoring | Apple `NSPrivacyCollectedDataTypeOtherUserContent` (already present — widen its description); Play *App activity → Other user-generated content* |
| GPX **import** from the device | Play *Files and docs*; Apple `NSPrivacyCollectedDataTypeOtherUserContent`. If the GPX describes the user's own rides, also Apple `…PreciseLocation` and Play *Location → Precise location* |
| Live location / "record a ride" / follow-me on the map / a MapLibre user-location puck | Apple `NSPrivacyCollectedDataTypePreciseLocation` (and flip Coarse Location's `Linked` if it becomes persisted); Play *Location → Precise location* and drop the *processed ephemerally* flag; stop stripping `ACCESS_FINE_LOCATION`; `NSLocationAlwaysAndWhenInUseUsageDescription` if it ever runs in the background |
| Storing the user's position server-side (saved "home area", proximity history) | flip Apple `NSPrivacyCollectedDataTypeCoarseLocation` → `Linked: true` and clear Play's *processed ephemerally* |
| Any other Firebase product (Analytics, Crashlytics, Remote Config, In-App Messaging) | It is no longer "no Firebase, full stop": re-read §1, and expect Apple `…ProductInteraction` / `…CrashData` and Play *App info and performance* to become due |
| Any analytics or crash SDK | Apple `…ProductInteraction` / `…CrashData` (+ re-check `NSPrivacyTracking` and `NSPrivacyTrackingDomains`); Play *App info and performance*, *App activity → App interactions* |
| In-app purchases | Apple `…PurchaseHistory` / `…PaymentInfo`; Play *Financial info* |

**Known open items** (not store declarations — tracked here so they are not lost):

1. **In-app account deletion** is required by Play policy for apps with in-app registration. Wire
   the existing `DELETE /api/users/me` into the profile page. (§5)
2. ~~**The privacy policy has not caught up with §2 #9 and #10.**~~ Done 2026-09-21: the policy
   says the app receives only the photo picked through the system picker, and gained an
   *Approximate location* subsection (read on demand, in use only, sent as a search parameter,
   not stored with the account — and, honestly, present in the web server's access log like any
   request URL) plus a consent-based purpose.
3. ~~**The privacy policy says nothing about push.**~~ Done 2026-09-21: `privacy/privacy-policy.{en,fr}.md`
   gained a *Notifications* subsection (inbox, preferences, delivery records, phone registration,
   opt-in and sign-out deletion), FCM (Google Ireland) in the provider table, the US transfer and
   its safeguards, the 90-day retention, and the export contents. Brevo's row now says it carries
   notification content, not just an address.
4. ~~**Undisclosed third-party endpoints.**~~ Done 2026-09-21: Inter is bundled and `google_fonts`
   removed; every basemap provider is listed in the policy with what it sees, and the Esri (US)
   and OSMF (UK) transfers are stated. (§6)
5. **`android:allowBackup` is unset**, so it defaults to `true`: app data is eligible for Google
   Drive backup and device-to-device transfer. Two consequences worth a decision — it is a data
   flow to Google that the privacy policy does not mention, and `flutter_secure_storage` is known
   to restore badly under it (the ciphertext is backed up but the Keystore key is not, so the
   restored `refresh_token` is undecryptable). Fixing it means setting `android:allowBackup` /
   `android:dataExtractionRules` on `<application>`, which changes restore behaviour — a product
   call, deliberately not made here.

---

## How to re-verify

```bash
cd mobile

# capability negatives — these should still print nothing
grep -rnE "file_picker|^ *camera:|photo_manager|image_cropper|permission_handler" pubspec.yaml
grep -rn "NSCameraUsageDescription\|LocationAlways" ios/Runner/Info.plist
grep -rn "READ_MEDIA_IMAGES\|ACCESS_BACKGROUND_LOCATION" android/app/src/main/AndroidManifest.xml

# capability positives — exactly two usage descriptions, in three files that must agree
grep -rn "UsageDescription" ios/Runner/Info.plist ios/Runner/en.lproj/InfoPlist.strings \
  ios/Runner/fr.lproj/InfoPlist.strings

# location must stay coarse and foreground-only
grep -rn "ACCESS_.*_LOCATION" android/app/src/main/AndroidManifest.xml
grep -rniE "LocationAccuracy|getCurrentPosition|getPositionStream" lib | grep -v lib/api/generated

# uploads: only the avatar path should show up outside the generated client
grep -rn "MultipartFile\|FormData" lib | grep -v lib/api/generated

# Every mutating operation the app can actually reach, to confirm §2 is still complete.
# App code calls generated retrofit clients, so grepping for `.post(` in lib/ finds almost
# nothing — resolve the @POST/@PUT/@PATCH/@DELETE method names first, then see which are referenced.
grep -rhA3 "@\(POST\|PUT\|PATCH\|DELETE\)(" lib/api/generated \
  | grep -oE "Future<[^>]*> [a-zA-Z0-9_]+\(" \
  | sed -E 's/.*> ([a-zA-Z0-9_]+)\(/\1/' | sort -u \
  | while read -r m; do
      grep -rqE "\.${m}\(" lib/features lib/core && echo "USED: $m"
    done

# push stays messaging-only: exactly firebase_core and firebase_messaging, nothing else
grep -n "firebase" pubspec.yaml
# no font is fetched from Google: must stay empty
grep -n "google_fonts" pubspec.yaml

# the notification permission must be asked from a screen, never at launch
grep -rn "requestAuthorization" lib | grep -v lib/api/generated

# manifest is well-formed
plutil -lint ios/Runner/PrivacyInfo.xcprivacy
```
