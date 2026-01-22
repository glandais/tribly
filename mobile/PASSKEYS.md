# Passkey Requirements for Mobile Apps

This document describes the requirements for enabling passkeys (WebAuthn) on iOS and Android native apps.

## Overview

For passkeys to work in native mobile apps, the operating system needs to verify that your app is authorized to use credentials for a specific domain (the Relying Party ID). This is done through well-known files hosted on your domain.

**Current RP ID**: `www.pedalons.fr` (configured in `lib/config/app_config.dart`)

## Apple iOS Requirements

### 1. Associated Domains File

Host this file at: `https://www.pedalons.fr/.well-known/apple-app-site-association`

```json
{
  "webcredentials": {
    "apps": ["<TeamID>.com.tribly.mobile"]
  }
}
```

Replace `<TeamID>` with your Apple Developer Team ID (10-character string).

### 2. Xcode Configuration

Add the Associated Domains capability to your app:

1. Open `ios/Runner.xcodeproj` in Xcode
2. Select the Runner target → Signing & Capabilities
3. Click "+ Capability" → Add "Associated Domains"
4. Add: `webcredentials:www.pedalons.fr`

This creates/updates `ios/Runner/Runner.entitlements`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>com.apple.developer.associated-domains</key>
  <array>
    <string>webcredentials:www.pedalons.fr</string>
  </array>
</dict>
</plist>
```

### 3. Apple Requirements for the File

- Must be served over HTTPS
- `Content-Type: application/json`
- No redirects allowed
- File must be accessible without authentication
- Apple CDN caches this file (changes may take up to 24 hours to propagate)

## Android Requirements

### 1. Digital Asset Links File

Host this file at: `https://www.pedalons.fr/.well-known/assetlinks.json`

```json
[
  {
    "relation": ["delegate_permission/common.get_login_creds"],
    "target": {
      "namespace": "android_app",
      "package_name": "com.tribly.mobile",
      "sha256_cert_fingerprints": [
        "<SHA256_FINGERPRINT_DEBUG>",
        "<SHA256_FINGERPRINT_RELEASE>"
      ]
    }
  }
]
```

### 2. Getting SHA256 Fingerprints

**Debug key** (for development):
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android | grep SHA256
```

**Release key** (for production):
```bash
keytool -list -v -keystore /path/to/release-key.keystore | grep SHA256
```

**Play App Signing** (if using Google Play App Signing):
- Go to Google Play Console → Your App → Setup → App signing
- Copy the SHA-256 fingerprint from "App signing key certificate"

### 3. Google Requirements for the File

- Must be served over HTTPS
- `Content-Type: application/json`
- Must be at exact path (no redirects)
- File must be accessible without authentication
- Use [Google's Asset Links Tool](https://developers.google.com/digital-asset-links/tools/generator) to verify

## Traefik Configuration

**Current routing** (from `docker-compose.yml`):
- `/api/*` → Backend (priority 100)
- `/*` → Frontend (priority 1)

**Problem**: `.well-known` requests currently go to frontend and fail.

**Solution**: Add a dedicated router for `.well-known` paths in `docker-compose.yml`:

```yaml
# Add these labels to the backend service (after existing labels)
- "traefik.http.routers.quarkus-wellknown.rule=PathPrefix(`/.well-known`)"
- "traefik.http.routers.quarkus-wellknown.priority=200"
- "traefik.http.routers.quarkus-wellknown.service=quarkus"
```

This routes `/.well-known/*` to the backend with priority 200 (higher than both `/api` at 100 and `/` at 1).

## Backend Implementation

Add endpoints to serve these files in the backend:

**File**: `backend/src/main/java/com/tribly/api/WellKnownResource.java`

```java
@Path("/.well-known")
public class WellKnownResource {

    @GET
    @Path("/apple-app-site-association")
    @Produces(MediaType.APPLICATION_JSON)
    public Response appleAppSiteAssociation() {
        String json = """
            {
              "webcredentials": {
                "apps": ["TEAMID.com.tribly.mobile"]
              }
            }
            """;
        return Response.ok(json).build();
    }

    @GET
    @Path("/assetlinks.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assetLinks() {
        String json = """
            [
              {
                "relation": ["delegate_permission/common.get_login_creds"],
                "target": {
                  "namespace": "android_app",
                  "package_name": "com.tribly.mobile",
                  "sha256_cert_fingerprints": [
                    "SHA256_FINGERPRINT_HERE"
                  ]
                }
              }
            ]
            """;
        return Response.ok(json).build();
    }
}
```

## Verification Checklist

### iOS
- [ ] `apple-app-site-association` file is accessible at domain root
- [ ] Associated Domains capability added in Xcode
- [ ] `webcredentials:www.pedalons.fr` added to entitlements
- [ ] App signed with correct Team ID
- [ ] Tested on real device (simulator has limitations)

### Android
- [ ] `assetlinks.json` file is accessible at domain root
- [ ] SHA256 fingerprints for debug and release keys added
- [ ] Verified with [Google Asset Links Tool](https://developers.google.com/digital-asset-links/tools/generator)
- [ ] Tested on real device

## Troubleshooting

### iOS
- **Passkeys not working**: Check Apple's CDN cache (wait 24h or test with `?mode=developer` query param)
- **Associated Domains not validating**: Use `swcutil` on macOS to debug
- **Simulator issues**: Use a real device for testing passkeys

### Android
- **Credential not found**: Verify assetlinks.json is accessible and fingerprint matches
- **Debug vs Release**: Ensure both fingerprints are in assetlinks.json during development

## References

- [Apple: Supporting Associated Domains](https://developer.apple.com/documentation/xcode/supporting-associated-domains)
- [Apple: Supporting Passkeys](https://developer.apple.com/documentation/authenticationservices/public-private_key_authentication/supporting_passkeys)
- [Google: Digital Asset Links](https://developers.google.com/digital-asset-links)
- [Google: Passkeys for Android](https://developer.android.com/identity/sign-in/credential-manager)
- [FIDO Alliance: Passkeys](https://fidoalliance.org/passkeys/)
