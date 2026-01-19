# GPS Service Connectors Implementation Plan

## Overview
Add GPS service connectors to Tribly, starting with Hammerhead. Users can connect GPS services from their profile, and upload routes directly to connected devices.

## Hammerhead API Summary
- **OAuth 2.0** with authorization code flow
- Auth URL: `https://api.hammerhead.io/v1/auth/oauth/authorize`
- Token URL: `https://api.hammerhead.io/v1/auth/oauth/token`
- Route upload: `POST /routes/file` (multipart, accepts .gpx, .fit)
- Required scope: `route:write`
- Token response includes: `access_token`, `refresh_token`, `expires_in`, `user_id`

---

## Phase 1: Database & Entity

### 1.1 Flyway Migration
**File:** `backend/src/main/resources/db/migration/V4__gps_service_connections.sql`

```sql
create table gps_service_connections (
    id bigint not null,
    deleted boolean not null default false,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    version bigint,

    user_id bigint not null,
    service_type varchar(20) not null,

    access_token_encrypted bytea not null,
    refresh_token_encrypted bytea,
    token_expires_at timestamp(6) with time zone,
    external_user_id varchar(100),

    connected_at timestamp(6) with time zone not null,
    last_used_at timestamp(6) with time zone,

    primary key (id),
    constraint uk_gps_connection_user_service unique (user_id, service_type),
    constraint fk_gps_connection_user foreign key (user_id) references users
);

create index idx_gps_connections_user on gps_service_connections (user_id) where deleted = false;
```

### 1.2 Enum
**File:** `backend/src/main/java/com/tribly/enums/GpsServiceType.java`

```java
public enum GpsServiceType {
    HAMMERHEAD("Hammerhead");

    private final String displayName;
    // constructor, getter
}
```

### 1.3 Entity
**File:** `backend/src/main/java/com/tribly/domain/gps/GpsServiceConnection.java`

- Extends `BaseEntity`
- Fields: `user` (ManyToOne), `serviceType`, `accessTokenEncrypted`, `refreshTokenEncrypted`, `tokenExpiresAt`, `externalUserId`, `connectedAt`, `lastUsedAt`
- Helper: `isTokenExpired()`, `markUsed()`

### 1.4 Repository
**File:** `backend/src/main/java/com/tribly/repository/gps/GpsServiceConnectionRepository.java`

- `findByUserAndService(Long userId, GpsServiceType)`
- `findByUser(Long userId)`

---

## Phase 2: Backend Services

### 2.1 Token Encryption Service
**File:** `backend/src/main/java/com/tribly/infrastructure/security/TokenEncryptionService.java`

- AES-256-GCM encryption using configured key
- Methods: `encrypt(String) -> byte[]`, `decrypt(byte[]) -> String`

### 2.2 GPS Client Interface (Strategy Pattern)
**File:** `backend/src/main/java/com/tribly/infrastructure/gps/GpsServiceClient.java`

```java
public interface GpsServiceClient {
    GpsServiceType getServiceType();
    String getAuthorizationUrl(String state, String redirectUri);
    TokenResponse exchangeCode(String code, String redirectUri);
    TokenResponse refreshToken(String refreshToken);
    RouteUploadResult uploadRoute(String accessToken, byte[] gpxContent, String routeName);
}
```

### 2.3 Hammerhead Client
**File:** `backend/src/main/java/com/tribly/infrastructure/gps/HammerheadClient.java`

- Implements `GpsServiceClient`
- Uses REST client to call Hammerhead API
- Config: `tribly.gps.hammerhead.client-id`, `tribly.gps.hammerhead.client-secret`

### 2.4 Main GPS Service
**File:** `backend/src/main/java/com/tribly/service/gps/GpsService.java`

- `initiateOAuth(GpsServiceType)` - Generate state, return auth URL
- `handleCallback(GpsServiceType, code, state)` - Exchange code, store tokens
- `disconnect(GpsServiceType)` - Soft delete connection
- `getConnectionsForUser(Long userId)` - Get user's connections
- `uploadRoute(GpsServiceType, teamSlug, routeSlug)` - Upload route to service

---

## Phase 3: DTOs & API

### 3.1 Response DTOs
**File:** `backend/src/main/java/com/tribly/dto/gps/response/`

- `GpsServiceConnectionDto` - serviceType, displayName, connectedAt (no tokens!)
- `GpsOAuthUrlResponse` - authorizationUrl
- `RouteUploadResponse` - success, message, externalRouteId

### 3.2 Update UserDto
**File:** `backend/src/main/java/com/tribly/dto/users/response/UserDto.java`

Add field: `List<GpsServiceConnectionDto> connectedServices`

Update `UserService.getMe()` to include connections in UserDto.

### 3.3 REST Resource
**File:** `backend/src/main/java/com/tribly/api/gps/GpsResource.java`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/gps/connect/{serviceType}` | Get OAuth authorization URL |
| GET | `/api/gps/callback/{serviceType}` | OAuth callback (redirects to frontend) |
| DELETE | `/api/gps/disconnect/{serviceType}` | Disconnect service |
| POST | `/api/gps/upload/{serviceType}/{teamSlug}/{routeSlug}` | Upload route to service |

---

## Phase 4: Frontend

### 4.1 API Generation
Run `pnpm generate-api` after backend OpenAPI is generated.

### 4.2 Hook
**File:** `frontend/src/hooks/useGpsConnections.ts`

- Query for user's connections (from UserDto)
- Mutations: disconnect, uploadRoute
- Function: `initiateConnect(serviceType)` - redirect to OAuth URL

### 4.3 GPS Connections Manager Component
**File:** `frontend/src/components/profile/GpsConnectionsManager.tsx`

- List available GPS services (Hammerhead for now)
- Show connected status with "Disconnect" button
- Show "Connect" button for unconnected services
- Use `ConfirmDialog` for disconnect confirmation

### 4.4 Update UserProfilePage
**File:** `frontend/src/pages/auth/UserProfilePage.tsx`

Add `<GpsConnectionsManager />` section after PasskeyManager (line ~220).

### 4.5 Update RouteDetailView
**File:** `frontend/src/components/route/RouteDetailView.tsx`

Add "Send to Device" dropdown menu after download buttons:
- Only show if user is authenticated and has connections
- Menu items for each connected service
- Shows loading state during upload
- Shows success/error notification

### 4.6 Translations
**Files:** `frontend/src/locales/{en,fr}/common.json`

Keys needed:
- `gps.title`, `gps.description`
- `gps.connect`, `gps.disconnect`, `gps.connectedSince`
- `gps.disconnectConfirm.*`
- `gps.notifications.*`
- `gps.services.hammerhead`
- `routes.detail.sendToDevice`

---

## Phase 5: Configuration

### 5.1 Application Properties
**File:** `backend/src/main/resources/application.properties`

```properties
tribly.gps.hammerhead.client-id=${HAMMERHEAD_CLIENT_ID:}
tribly.gps.hammerhead.client-secret=${HAMMERHEAD_CLIENT_SECRET:}
tribly.encryption.key=${ENCRYPTION_KEY:dev-key-for-testing-only}
```

---

## Files to Modify/Create

### Backend (New Files)
- `backend/src/main/resources/db/migration/V4__gps_service_connections.sql`
- `backend/src/main/java/com/tribly/enums/GpsServiceType.java`
- `backend/src/main/java/com/tribly/domain/gps/GpsServiceConnection.java`
- `backend/src/main/java/com/tribly/repository/gps/GpsServiceConnectionRepository.java`
- `backend/src/main/java/com/tribly/infrastructure/security/TokenEncryptionService.java`
- `backend/src/main/java/com/tribly/infrastructure/gps/GpsServiceClient.java`
- `backend/src/main/java/com/tribly/infrastructure/gps/HammerheadClient.java`
- `backend/src/main/java/com/tribly/service/gps/GpsService.java`
- `backend/src/main/java/com/tribly/dto/gps/response/*.java`
- `backend/src/main/java/com/tribly/api/gps/GpsResource.java`

### Backend (Modify)
- `backend/src/main/java/com/tribly/dto/users/response/UserDto.java`
- `backend/src/main/java/com/tribly/service/user/UserService.java`
- `backend/src/main/resources/application.properties`

### Frontend (New Files)
- `frontend/src/hooks/useGpsConnections.ts`
- `frontend/src/components/profile/GpsConnectionsManager.tsx`

### Frontend (Modify)
- `frontend/src/pages/auth/UserProfilePage.tsx`
- `frontend/src/components/route/RouteDetailView.tsx`
- `frontend/src/locales/en/common.json`
- `frontend/src/locales/fr/common.json`

---

## Verification

1. **OAuth Flow**: Connect Hammerhead from profile, verify redirect and token storage
2. **Token Security**: Verify tokens are encrypted in database (not plaintext)
3. **Disconnect**: Verify soft delete and UI update
4. **UserDto**: Verify connected services appear in user profile response
5. **Route Upload**: Upload a route from RouteDetailView, verify success on Hammerhead
6. **Token Refresh**: Test with expired token to verify automatic refresh
7. **Error Handling**: Test with invalid credentials, network errors
