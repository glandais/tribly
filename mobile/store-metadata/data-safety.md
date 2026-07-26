# Data collection declarations — source of truth

Canonical record of what the **Pédalons mobile app** collects, and the exact answers to give in
each store's privacy form. Three artefacts must agree, and this file is the one they are derived
from:

| Artefact | Where | Kept in sync by |
|---|---|---|
| iOS privacy manifest | `mobile/ios/Runner/PrivacyInfo.xcprivacy` | hand-edit alongside this file |
| App Store Connect → App Privacy | App Store Connect (web form) | copy §4 into the form |
| Google Play Console → Data safety | Play Console (web form) | copy §5 into the form |
| Published privacy policy | `mobile/privacy/privacy-policy.{en,fr}.md` | must not contradict §2 |

- **App**: Pédalons, `fr.pedalons.mobile`, version `1.0.0+23` (`mobile/pubspec.yaml`)
- **Backend**: `https://www.pedalons.fr` (`mobile/lib/config/app_config.dart`)
- **Last verified against the code**: 2026-07-26

> Scope note. These declarations describe **the mobile app binary**, not the whole Pedalons
> platform. The web frontend can do considerably more than the app (see §7). Declaring platform
> capabilities the app does not have is as wrong as omitting ones it does.

---

## 1. What the app actually does

The mobile app authenticates, then reads team content. Two capabilities beyond reading touch
personal data: **picking a profile picture** from the photo library, and reading a **coarse
device position** to sort content by proximity. There is no content authoring, no camera capture,
no file import, and no analytics.

Two capabilities added, and their exact boundary:

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

Verified absent from `mobile/pubspec.yaml`, `mobile/pubspec.lock`, `mobile/lib/`, `mobile/ios/`
and `mobile/android/`:

- No `camera`, `photo_manager`, `image_cropper`, `file_picker` — the only media entry point is
  the system photo picker.
- No precise location, no background location, no `permission_handler` (permission prompts are
  `geolocator`'s own), and no MapLibre user-location layer.
- No GPX/FIT **import**. Route files are download-only: fetched to `getTemporaryDirectory()` and
  handed to the OS share sheet (`lib/features/routes/presentation/pages/route_detail_page.dart`).
  Coordinates flow server → device, never device → server.
- No analytics, crash-reporting, advertising or attribution SDK (no Firebase/Crashlytics/Sentry/
  Amplitude/AppsFlyer/Adjust). `device_info_plus` and `ua_client_hints` are present only as
  transitive dependencies of `passkeys` and are not on any executed code path.
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
| 8 | **Session security metadata** | Server-recorded on sign-in: IP address, user agent, last-login / last-use timestamps (per `mobile/privacy/privacy-policy.en.md` §"Session Data") | Yes | Yes |
| 9 | **Profile picture** | Photo chosen from the system photo library (`image_picker`) and sent to `POST /api/users/me/avatar` | Yes | Yes |
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

Answer **"Yes, we collect data from this app"**, then declare exactly these seven, none used for
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
| Other Data | Other Data Types | Yes | No | App Functionality |

For *Other Data Types*, describe it as: **"Session security metadata (IP address, user agent and
sign-in timestamps) recorded to detect suspicious account activity."**

Explicitly answer **No / do not select**: Precise Location, Audio Data, Contacts, Health, Fitness,
Payment Info, Purchase History, Device ID, Product Interaction, Advertising Data, Crash Data,
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

*Photos* is **optional**: the account works without a profile picture, and the photo library is
only reached when the user taps "change picture".

*Approximate location* is **optional** and **processed ephemerally**: it is read only when the user
turns on the "around me" filter, sent as `nearLat`/`nearLon`/`nearRadius` on a read request, and
neither stored on the device nor persisted server-side. It is not linked to the account and is not
used for tracking or advertising. Precise location is **not** collected — the app requests
`ACCESS_COARSE_LOCATION` only, and `LocationAccuracy.low`.

Everything else in the form is **not collected**: Precise location, Financial info, Health and
fitness, Messages, Videos, Audio files, Files and docs, Calendar, Contacts, App interactions,
In-app search history, Installed apps, Other user-generated content, Web browsing history, Crash
logs, Diagnostics, Other app performance data, Device or other IDs.

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

Expected merged release permission set: `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`,
`ACCESS_COARSE_LOCATION`, `USE_BIOMETRIC`, `USE_FINGERPRINT`, `USE_CREDENTIALS`,
`CREDENTIAL_MANAGER_SET_ORIGIN` — and **no** `ACCESS_FINE_LOCATION`. To re-check after a release
build:

```bash
# The FINAL merged manifest — not manifest-merger-release-report.txt, which keeps the rejected
# nodes in its decision log and so still mentions both permissions even when the strip worked.
grep -o 'android:name="android.permission.[A-Z_]*"' \
  mobile/build/app/intermediates/merged_manifest/release/*/AndroidManifest.xml | sort -u
```

The other merged permissions are not declarable data types and stay: `INTERNET`,
`ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` (MapLibre connectivity detection), `USE_BIOMETRIC`,
`USE_FINGERPRINT`, `USE_CREDENTIALS`, `CREDENTIAL_MANAGER_SET_ORIGIN` (passkeys).

Android needs no photo-library permission: `image_picker` goes through the system photo picker
(`ACTION_PICK_IMAGES` / `ACTION_GET_CONTENT`), which returns a single user-chosen item without
`READ_MEDIA_IMAGES`. Do not add that permission — it would over-declare.

---

## 6. Third parties contacted by the app

No SDK sends data to a third party. Two **network endpoints** outside our infrastructure receive
the device's IP address as an unavoidable consequence of an HTTP request:

| Endpoint | Why | What it sees |
|---|---|---|
| `tiles.versatiles.org` | Map styles and raster/vector tiles (`lib/features/routes/presentation/widgets/route_map.dart`) | IP address; the map viewport being browsed |
| `fonts.gstatic.com` | Inter font fetched at first launch by `google_fonts` — no font files are bundled (`lib/core/theme/pedalons_theme.dart`) | IP address |

⚠️ **These contradict the published privacy policy.** `mobile/privacy/privacy-policy.en.md` §4
lists only OVHcloud and Brevo as technical providers and states that image processing and route
calculation are self-hosted "and do not transmit any data to third parties". Neither versatiles.org
nor Google is mentioned, and the gstatic transfer is a US transfer with no stated legal basis.
Tracked in §8 — not fixed here, because it is a code/policy change rather than a store declaration.

---

## 7. Not declared, and why

| Not declared | Why |
|---|---|
| Precise Location | Only `ACCESS_COARSE_LOCATION` is requested and only `LocationAccuracy.low` is used; `ACCESS_FINE_LOCATION` is stripped from the merged manifest. No background location, no ride recording, no user-location puck. Route coordinates are **downloaded** for display; describing where a *route* is is not describing where the *user* is. |
| Videos | `image_picker` is restricted to still images for the avatar; no video is ever read. |
| Camera | No `NSCameraUsageDescription` and no camera permission — the photo library is the only entry point. |
| Files and docs | GPX/FIT is download-and-share only; there is no import path. |
| Fitness / Health | No HealthKit, no Motion & Fitness, no activity recording. |
| Device ID | No advertising identifier and no device-level ID is read or sent. The passkey `deviceName` is the constant string `"Mobile"`. |
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
| Any analytics or crash SDK | Apple `…ProductInteraction` / `…CrashData` (+ re-check `NSPrivacyTracking` and `NSPrivacyTrackingDomains`); Play *App info and performance*, *App activity → App interactions* |
| In-app purchases | Apple `…PurchaseHistory` / `…PaymentInfo`; Play *Financial info* |

**Known open items** (not store declarations — tracked here so they are not lost):

1. **In-app account deletion** is required by Play policy for apps with in-app registration. Wire
   the existing `DELETE /api/users/me` into the profile page. (§5)
2. **The privacy policy has not caught up with §2 #9 and #10.**
   `mobile/privacy/privacy-policy.{en,fr}.md` predates the avatar picker and the proximity filter
   and describes neither. It must gain a profile-picture entry and an approximate-location entry
   ("read on demand, sent as a search parameter, not retained") **before** either capability ships
   to users — §4/§5 and the policy contradicting each other is itself a rejection motive.
3. **Undisclosed third-party endpoints.** Either bundle the Inter font locally instead of fetching
   from `fonts.gstatic.com`, self-host map tiles, or add both to the privacy policy's provider
   table with a legal basis for the transfer. (§6)
4. **`android:allowBackup` is unset**, so it defaults to `true`: app data is eligible for Google
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

# manifest is well-formed
plutil -lint ios/Runner/PrivacyInfo.xcprivacy
```
