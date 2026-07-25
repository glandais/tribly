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

- **App**: Pédalons, `fr.pedalons.mobile`, version `1.0.0+21` (`mobile/pubspec.yaml`)
- **Backend**: `https://www.pedalons.fr` (`mobile/lib/config/app_config.dart`)
- **Last verified against the code**: 2026-07-25

> Scope note. These declarations describe **the mobile app binary**, not the whole Pedalons
> platform. The web frontend can do considerably more than the app (see §7). Declaring platform
> capabilities the app does not have is as wrong as omitting ones it does.

---

## 1. What the app actually does

The mobile app is a **near read-only client**. It authenticates, then reads team content. It has
no content authoring, no media capture, no file import, and no analytics.

Verified absent from `mobile/pubspec.yaml`, `mobile/pubspec.lock`, `mobile/lib/`, `mobile/ios/`
and `mobile/android/`:

- No `image_picker`, `file_picker`, `camera`, `photo_manager`, `image_cropper` — the app cannot
  reach the photo library or camera. No `NSPhotoLibraryUsageDescription` / `NSCameraUsageDescription`
  exists in `mobile/ios/Runner/Info.plist`, so iOS cannot grant that access.
- No `geolocator`, `location`, `permission_handler`, and no MapLibre user-location layer. No
  `NSLocationWhenInUseUsageDescription` in `Info.plist`, so iOS cannot grant location access.
- No GPX/FIT **import**. Route files are download-only: fetched to `getTemporaryDirectory()` and
  handed to the OS share sheet (`lib/features/routes/presentation/pages/route_detail_page.dart`).
  Coordinates flow server → device, never device → server.
- No analytics, crash-reporting, advertising or attribution SDK (no Firebase/Crashlytics/Sentry/
  Amplitude/AppsFlyer/Adjust). `device_info_plus` and `ua_client_hints` are present only as
  transitive dependencies of `passkeys` and are not on any executed code path.
- No StoreKit / `in_app_purchase` / payment SDK.
- No multipart or `FormData` upload outside the unused generated OpenAPI client.

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

`…Purpose` above abbreviates `NSPrivacyCollectedDataTypePurpose`.

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

Answer **"Yes, we collect data from this app"**, then declare exactly these five, all
**Data Linked to You**, none used for **Tracking**, all purpose **App Functionality**:

| Category | Data type | Linked | Tracking | Purpose |
|---|---|---|---|---|
| Contact Info | Email Address | Yes | No | App Functionality |
| Contact Info | Name | Yes | No | App Functionality |
| Identifiers | User ID | Yes | No | App Functionality |
| User Content | Other User Content | Yes | No | App Functionality |
| Other Data | Other Data Types | Yes | No | App Functionality |

For *Other Data Types*, describe it as: **"Session security metadata (IP address, user agent and
sign-in timestamps) recorded to detect suspicious account activity."**

Explicitly answer **No / do not select**: Precise Location, Coarse Location, Photos or Videos,
Audio Data, Contacts, Health, Fitness, Payment Info, Purchase History, Device ID, Product
Interaction, Advertising Data, Crash Data, Performance Data, Search History, Browsing History.

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

**Per-data-type answers.** For every row: *Collected* = Yes, *Shared* = No, *Processed
ephemerally* = No.

| Category | Data type | Required / optional | Purposes |
|---|---|---|---|
| Personal info | Name | Required | App functionality, Account management |
| Personal info | Email address | Required | App functionality, Account management |
| Personal info | User IDs | Required | App functionality, Account management, Fraud prevention, security, and compliance |
| App activity | Other actions | Optional | App functionality |

Everything else in the form is **not collected**: Location (approximate and precise), Financial
info, Health and fitness, Messages, Photos and videos, Audio files, Files and docs, Calendar,
Contacts, App interactions, In-app search history, Installed apps, Other user-generated content,
Web browsing history, Crash logs, Diagnostics, Other app performance data, Device or other IDs.

**Deliberate divergences from §4**

1. *Ride/trip/team participation* → Play **"App activity → Other actions"** (Google's example for that
   type is likes and dialog choices, which is what a join/leave toggle is), but Apple
   **"Other User Content"** (Apple has no "actions" type). Same underlying data, different bucket.
2. *Session security metadata* (#8) is declared to Apple under **Other Data Types** but has **no
   Play equivalent** — Google's form has no IP-address or user-agent type, and "Device or other
   IDs" means device-level identifiers such as an advertising ID, which the app does not collect.
   Not declaring it on Play is correct, not an omission.

**There is no Android equivalent of `PrivacyInfo.xcprivacy`.** Google has no privacy-manifest file
that ships inside the APK/AAB — the Data safety declaration exists **only** as a Play Console web
form, which is exactly why §5 of this file exists. Nothing in `mobile/android/` encodes it, and
`fastlane`'s `upload_to_play_store` cannot upload it either (`skip_upload_metadata` in
`android/fastlane/Fastfile` is unrelated). If you prefer bulk entry, Play Console offers
*Data safety → Import from CSV*; download the template from the Console rather than hand-writing
one, and fill it from the tables above.

What Android *does* encode is the **permission set**, and that must not contradict §5.

**Location permissions (fixed).** The merged release manifest used to declare
`ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`. They were never in our source manifest — the
merger injected them from `org.maplibre.gl:android-sdk-opengl:13.0.3-pre0`, pulled in by the
`maplibre` plugin (`build/app/outputs/logs/manifest-merger-release-report.txt:528-535`). MapLibre
needs them only for its LocationComponent (the user-location puck), which this app never enables.
Left alone they would show on the Play listing and contradict the "no location collected"
declaration, so `mobile/android/app/src/main/AndroidManifest.xml` now strips both with
`tools:node="remove"`.

Verified on the merged release manifest — both permissions are absent, and only these remain:
`INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `USE_BIOMETRIC`, `USE_FINGERPRINT`,
`USE_CREDENTIALS`, `CREDENTIAL_MANAGER_SET_ORIGIN`. To re-check after a release build:

```bash
# The FINAL merged manifest — not manifest-merger-release-report.txt, which keeps the rejected
# nodes in its decision log and so still mentions both permissions even when the strip worked.
grep -o 'android:name="android.permission.[A-Z_]*"' \
  mobile/build/app/intermediates/merged_manifest/release/*/AndroidManifest.xml | sort -u
```

The remaining merged permissions are not declarable data types and stay: `INTERNET`,
`ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` (MapLibre connectivity detection), `USE_BIOMETRIC`,
`USE_FINGERPRINT`, `USE_CREDENTIALS`, `CREDENTIAL_MANAGER_SET_ORIGIN` (passkeys).

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
| Precise / Coarse Location | The app never reads device location: no location plugin, no runtime permission request, no `NSLocationWhenInUseUsageDescription`. Route coordinates are **downloaded** for display; describing where a *route* is is not describing where the *user* is. |
| Photos or Videos | No photo-library or camera access exists in the binary. Avatars and images are download-only. Profile-picture upload exists on the **web** frontend, not in the app. |
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
| Avatar upload (`POST /api/users/me/avatar`) + an image picker | Apple `NSPrivacyCollectedDataTypePhotosorVideos`; Play *Photos and videos → Photos*; plus `NSPhotoLibraryUsageDescription` / `NSCameraUsageDescription` in `Info.plist` |
| Post / comment / ride / route authoring | Apple `NSPrivacyCollectedDataTypeOtherUserContent` (already present — widen its description); Play *App activity → Other user-generated content* |
| GPX **import** from the device | Play *Files and docs*; Apple `NSPrivacyCollectedDataTypeOtherUserContent`. If the GPX describes the user's own rides, also Apple `…PreciseLocation` and Play *Location → Precise location* |
| Live location / "record a ride" / follow-me on the map | Apple `NSPrivacyCollectedDataTypePreciseLocation`; Play *Location → Precise location*; `NSLocationWhenInUseUsageDescription`; runtime Android permission request |
| Any analytics or crash SDK | Apple `…ProductInteraction` / `…CrashData` (+ re-check `NSPrivacyTracking` and `NSPrivacyTrackingDomains`); Play *App info and performance*, *App activity → App interactions* |
| In-app purchases | Apple `…PurchaseHistory` / `…PaymentInfo`; Play *Financial info* |

**Known open items** (not store declarations — tracked here so they are not lost):

1. **In-app account deletion** is required by Play policy for apps with in-app registration. Wire
   the existing `DELETE /api/users/me` into the profile page. (§5)
2. **Undisclosed third-party endpoints.** Either bundle the Inter font locally instead of fetching
   from `fonts.gstatic.com`, self-host map tiles, or add both to the privacy policy's provider
   table with a legal basis for the transfer. (§6)
3. **`android:allowBackup` is unset**, so it defaults to `true`: app data is eligible for Google
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

# capability negatives — all four should print nothing
grep -rnE "image_picker|file_picker|camera|photo_manager|geolocator|permission_handler" pubspec.yaml
grep -rn "UsageDescription" ios/Runner/Info.plist
grep -rn "MultipartFile\|FormData" lib | grep -v lib/api/generated
grep -rniE "currentPosition|getLocation|userLocation|myLocation|LocationPermission" lib | grep -v lib/api/generated

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
