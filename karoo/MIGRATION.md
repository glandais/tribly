# Plan: Authentification Device Code Flow générique

## Objectif

Refactorer les endpoints `/api/karoo/oauth/*` → `/api/device/oauth/*` pour supporter Karoo et Garmin avec le même Device Code Flow (PIN + polling).

**Résultat:**
- Karoo et Garmin utilisent les mêmes endpoints `/api/device/oauth/*`
- Frontend `/device/verify` remplace `/karoo/verify`
- `GarminOAuthResource` (Authorization Code Flow) devient obsolète et peut être supprimé

---

## Architecture cible

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│ Karoo / Garmin  │     │  Backend Tribly  │     │   Téléphone     │
└────────┬────────┘     └────────┬─────────┘     └────────┬────────┘
         │                       │                        │
         │ POST /api/device/     │                        │
         │      oauth/device     │                        │
         │ {clientId: "karoo"|   │                        │
         │          "garmin"}    │                        │
         │──────────────────────>│                        │
         │ {deviceCode,          │                        │
         │  userCode: "ABC123",  │                        │
         │  verificationUri,     │                        │
         │  expiresIn, interval} │                        │
         │<──────────────────────│                        │
         │                       │                        │
         │ Affiche "ABC123"      │                        │
         │ + "pedalons.fr/device"│      Entre le code     │
         │                       │<───────────────────────│
         │                       │     S'authentifie      │
         │                       │<───────────────────────│
         │                       │                        │
         │ POST /api/device/oauth/token                   │
         │ (polling toutes les 5s)                        │
         │──────────────────────>│                        │
         │ 400 AUTHORIZATION_PENDING ou 200 {tokens}      │
         │<──────────────────────│                        │
```

---

## Implémentation Garmin Connect IQ

### Modifications requises dans `garmin-app/`

#### 1. `ApiClient.mc` - Utiliser les nouveaux endpoints `/api/device/oauth/*`

```monkeyc
// Nouvelle méthode: demander un device code
function requestDeviceCode(callback) {
    var url = BASE_URL + "/api/device/oauth/device";
    var params = {"clientId" => "garmin"};  // Différencie de "karoo"
    Communications.makeWebRequest(url, params, {
        :method => Communications.HTTP_REQUEST_METHOD_POST,
        :headers => {"Content-Type" => "application/json"},
        :responseType => Communications.HTTP_RESPONSE_CONTENT_TYPE_JSON
    }, callback);
}

// Polling pour récupérer les tokens
function pollForToken(deviceCode, callback) {
    var url = BASE_URL + "/api/device/oauth/token";
    var params = {
        "grantType" => "urn:ietf:params:oauth:grant-type:device_code",
        "deviceCode" => deviceCode
    };
    Communications.makeWebRequest(url, params, {
        :method => Communications.HTTP_REQUEST_METHOD_POST,
        :headers => {"Content-Type" => "application/json"},
        :responseType => Communications.HTTP_RESPONSE_CONTENT_TYPE_JSON
    }, callback);
}

// Supprimer l'ancien exchangeCodeForTokens() qui utilisait Authorization Code Flow
```

#### 2. `AuthManager.mc` - Ajouter stockage device code

```monkeyc
// Clés additionnelles
const DEVICE_CODE_KEY = "device_code";
const USER_CODE_KEY = "user_code";
const CODE_EXPIRY_KEY = "code_expiry";

function saveDeviceCode(deviceCode, userCode, expiresIn) {
    Storage.setValue(DEVICE_CODE_KEY, deviceCode);
    Storage.setValue(USER_CODE_KEY, userCode);
    Storage.setValue(CODE_EXPIRY_KEY, Time.now().value() + expiresIn);
}

function getDeviceCode() { return Storage.getValue(DEVICE_CODE_KEY); }
function getUserCode() { return Storage.getValue(USER_CODE_KEY); }
function isDeviceCodeExpired() {
    var expiry = Storage.getValue(CODE_EXPIRY_KEY);
    return expiry == null || Time.now().value() > expiry;
}
function clearDeviceCode() {
    Storage.deleteValue(DEVICE_CODE_KEY);
    Storage.deleteValue(USER_CODE_KEY);
    Storage.deleteValue(CODE_EXPIRY_KEY);
}
```

#### 3. `LoginView.mc` - Afficher le code PIN (remplacer le contenu)

```monkeyc
class LoginView extends WatchUi.View {
    var _userCode = null;
    var _isPolling = false;
    var _statusText = "";

    function initialize() {
        View.initialize();
    }

    function setUserCode(code) {
        _userCode = code;
        _isPolling = true;
        _statusText = "En attente...";
        WatchUi.requestUpdate();
    }

    function setStatus(text) {
        _statusText = text;
        WatchUi.requestUpdate();
    }

    function onUpdate(dc) {
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_BLACK);
        dc.clear();

        var centerX = dc.getWidth() / 2;
        var centerY = dc.getHeight() / 2;

        if (_userCode != null) {
            // Instructions
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.drawText(centerX, 20, Graphics.FONT_XTINY,
                "Allez sur", Graphics.TEXT_JUSTIFY_CENTER);
            dc.drawText(centerX, 40, Graphics.FONT_TINY,
                "pedalons.fr/device", Graphics.TEXT_JUSTIFY_CENTER);

            // Code PIN en grand
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(centerX, centerY - 20, Graphics.FONT_NUMBER_HOT,
                _userCode, Graphics.TEXT_JUSTIFY_CENTER);

            // Status
            dc.setColor(Graphics.COLOR_BLUE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(centerX, centerY + 40, Graphics.FONT_TINY,
                _statusText, Graphics.TEXT_JUSTIFY_CENTER);
        } else {
            // État initial
            dc.drawText(centerX, centerY - 10, Graphics.FONT_SMALL,
                "Appuyez sur SELECT", Graphics.TEXT_JUSTIFY_CENTER);
            dc.drawText(centerX, centerY + 10, Graphics.FONT_SMALL,
                "pour vous connecter", Graphics.TEXT_JUSTIFY_CENTER);
        }
    }
}
```

#### 4. `TriblyApp.mc` - Nouveau flow avec polling

```monkeyc
// Remplacer startOAuthFlow() par:
var _pollTimer = null;
var _deviceCode = null;

function startDeviceCodeFlow() {
    _apiClient.requestDeviceCode(method(:onDeviceCodeReceived));
}

function onDeviceCodeReceived(responseCode, data) {
    if (responseCode == 200 && data != null) {
        _deviceCode = data["deviceCode"];
        var userCode = data["userCode"];
        var expiresIn = data["expiresIn"];
        var interval = data["interval"];

        _authManager.saveDeviceCode(_deviceCode, userCode, expiresIn);

        // Afficher le code
        if (_loginView != null) {
            _loginView.setUserCode(userCode);
        }

        // Démarrer le polling
        _pollTimer = new Timer.Timer();
        _pollTimer.start(method(:pollForToken), interval * 1000, true);
    } else {
        showError("Erreur réseau");
    }
}

function pollForToken() {
    if (_authManager.isDeviceCodeExpired()) {
        _pollTimer.stop();
        _authManager.clearDeviceCode();
        showError("Code expiré");
        return;
    }
    _apiClient.pollForToken(_deviceCode, method(:onPollResponse));
}

function onPollResponse(responseCode, data) {
    if (responseCode == 200 && data != null) {
        // Succès! Tokens reçus
        _pollTimer.stop();
        _authManager.clearDeviceCode();
        _authManager.saveTokens(
            data["accessToken"],
            data["refreshToken"],
            data["expiresIn"]
        );
        showRouteList();
    } else if (responseCode == 400) {
        // Vérifier le code d'erreur
        var errorCode = data != null ? data["code"] : null;
        if (errorCode != null && errorCode.equals("AUTHORIZATION_PENDING")) {
            // Normal, continuer le polling
            return;
        }
        // Autre erreur
        _pollTimer.stop();
        showError("Erreur d'authentification");
    }
}
```

#### 5. `LoginDelegate.mc` - Déclencher le nouveau flow

```monkeyc
function onSelect() {
    // Remplacer l'appel à startOAuthFlow()
    getApp().startDeviceCodeFlow();
    return true;
}
```

#### 6. `resources/strings/strings.xml` et `strings-fre.xml` - Nouveaux textes

```xml
<!-- strings-fre.xml -->
<string id="login_instruction">Allez sur</string>
<string id="login_url">pedalons.fr/device</string>
<string id="login_waiting">En attente...</string>
<string id="login_expired">Code expiré</string>
<string id="login_error">Erreur</string>

<!-- strings.xml (English) -->
<string id="login_instruction">Go to</string>
<string id="login_url">pedalons.fr/device</string>
<string id="login_waiting">Waiting...</string>
<string id="login_expired">Code expired</string>
<string id="login_error">Error</string>
```

---

---

## Refactoring Backend

### Fichiers à renommer/modifier

| Ancien | Nouveau |
|--------|---------|
| `api/karoo/KarooOAuthResource.java` | `api/device/DeviceOAuthResource.java` |
| `service/karoo/KarooAuthService.java` | `service/device/DeviceAuthService.java` |
| `service/karoo/KarooJwtService.java` | `service/device/DeviceJwtService.java` |
| `dto/karoo/request/KarooDeviceAuthRequest.java` | `dto/device/request/DeviceAuthRequest.java` |
| `dto/karoo/request/KarooTokenRequest.java` | `dto/device/request/DeviceTokenRequest.java` |
| `dto/karoo/response/KarooDeviceCodeResponse.java` | `dto/device/response/DeviceCodeResponse.java` |
| `dto/karoo/response/KarooTokenResponse.java` | `dto/device/response/DeviceTokenResponse.java` |

### Path annotations à modifier

```java
// Avant
@Path("/api/karoo/oauth")
public class KarooOAuthResource { ... }

// Après
@Path("/api/device/oauth")
public class DeviceOAuthResource { ... }
```

### Endpoints résultants

- `POST /api/device/oauth/device` → génère deviceCode + userCode
- `POST /api/device/oauth/token` → polling ou échange de tokens
- `POST /api/device/oauth/complete` → marque comme autorisé (appelé par frontend)
- `GET /api/device/oauth/verify` → vérifie si code autorisé

### À supprimer (obsolètes)

| Fichier | Raison |
|---------|--------|
| `api/garmin/GarminOAuthResource.java` | Remplacé par DeviceOAuthResource |
| `service/garmin/GarminAuthService.java` | Authorization Code Flow plus utilisé |
| `service/garmin/GarminJwtService.java` | Fusionné dans DeviceJwtService |
| `dto/garmin/request/GarminTokenRequest.java` | Plus utilisé |
| `dto/garmin/request/GarminCallbackRequest.java` | Plus utilisé |
| `dto/garmin/response/GarminTokenResponse.java` | Plus utilisé |
| `GarminOAuthResourceTest.java` | Plus de resource à tester |

**Note**: Garder `dto/garmin/response/GarminRouteDto.java` et `GarminRoutesResponse.java` si l'API routes Garmin reste séparée, ou les fusionner aussi.

---

## Refactoring Frontend

### Fichiers à renommer/déplacer

| Ancien | Nouveau |
|--------|---------|
| `pages/karoo/KarooVerifyPage.tsx` | `pages/device/DeviceVerifyPage.tsx` |

### Configuration à modifier

**`config/paths.ts`:**
```typescript
// Avant
karooVerify: () => '/karoo/verify',
// Après
deviceVerify: () => '/device/verify',
```

**`config/routes.config.ts`:**
```typescript
// Avant
const KarooVerifyPage = lazy(() => import('@/pages/karoo/KarooVerifyPage'));
// ...
{ path: paths.karooVerify(), element: <KarooVerifyPage />, ... }

// Après
const DeviceVerifyPage = lazy(() => import('@/pages/device/DeviceVerifyPage'));
// ...
{ path: paths.deviceVerify(), element: <DeviceVerifyPage />, ... }
```

### i18n à renommer

**`locales/fr/common.json` et `locales/en/common.json`:**
```json
// Avant
"karoo": {
  "title": "Vérification Karoo",
  ...
}

// Après
"device": {
  "title": "Vérification appareil",
  ...
}
```

### API générée (auto après `pnpm generate-api`)

Les fichiers dans `api/endpoints/karoo-oauth/` et `api/dto/karoo*.ts` seront régénérés automatiquement après modification du backend OpenAPI.

---

## Refactoring Karoo app

Mettre à jour les URLs dans `karoo/app/src/main/kotlin/com/tribly/karoo/api/TriblyApiClient.kt`:

```kotlin
// Avant
private const val DEVICE_CODE_URL = "$BASE_URL/api/karoo/oauth/device"
private const val TOKEN_URL = "$BASE_URL/api/karoo/oauth/token"

// Après
private const val DEVICE_CODE_URL = "$BASE_URL/api/device/oauth/device"
private const val TOKEN_URL = "$BASE_URL/api/device/oauth/token"
```

---

## Résumé des fichiers à modifier

### Backend (Java)
| Fichier | Action |
|---------|--------|
| `api/karoo/KarooOAuthResource.java` | Renommer → `api/device/DeviceOAuthResource.java`, changer `@Path` |
| `service/karoo/KarooAuthService.java` | Renommer → `service/device/DeviceAuthService.java` |
| `service/karoo/KarooJwtService.java` | Renommer → `service/device/DeviceJwtService.java` |
| `dto/karoo/*` | Renommer → `dto/device/*` |
| `api/garmin/GarminOAuthResource.java` | **Supprimer** |
| `service/garmin/GarminAuthService.java` | **Supprimer** |
| `service/garmin/GarminJwtService.java` | **Supprimer** |
| `dto/garmin/request/*` | **Supprimer** (sauf si utilisé ailleurs) |

### Frontend (TypeScript)
| Fichier | Action |
|---------|--------|
| `pages/karoo/KarooVerifyPage.tsx` | Renommer → `pages/device/DeviceVerifyPage.tsx` |
| `config/paths.ts` | `karooVerify` → `deviceVerify` |
| `config/routes.config.ts` | Mettre à jour import et route |
| `locales/*/common.json` | `karoo.*` → `device.*` |

### Karoo app (Kotlin)
| Fichier | Action |
|---------|--------|
| `karoo/app/.../TriblyApiClient.kt` | `/api/karoo/oauth/*` → `/api/device/oauth/*` |

### Garmin app (Monkey C)
| Fichier | Action |
|---------|--------|
| `garmin-app/source/ApiClient.mc` | Remplacer Authorization Code par Device Code Flow |
| `garmin-app/source/AuthManager.mc` | Ajouter stockage device code |
| `garmin-app/source/LoginView.mc` | Afficher code PIN + status polling |
| `garmin-app/source/TriblyApp.mc` | Nouveau flow avec Timer pour polling |
| `garmin-app/source/LoginDelegate.mc` | Appeler `startDeviceCodeFlow()` |
| `garmin-app/resources/strings/*.xml` | Nouveaux textes UI |

---

## Vérification

### Backend
1. `cd backend && mvn quarkus:dev`
2. Tester `POST /api/device/oauth/device` → doit retourner deviceCode + userCode
3. Tester `GET /api/device/oauth/verify?code=XXX` → doit retourner status
4. Vérifier que les anciens endpoints `/api/karoo/*` ne répondent plus (404)
5. `mvn package -DskipTests` → régénère OpenAPI

### Frontend
1. `cd frontend && pnpm generate-api` → régénère les clients API
2. `pnpm dev` → vérifier que `/device/verify` fonctionne
3. Tester le flow complet: entrer un code → s'authentifier → voir "Appareil autorisé"

### Karoo app
1. Rebuild et tester que le login fonctionne toujours avec les nouveaux endpoints

### Garmin app
1. `cd garmin-app && make build`
2. Tester dans le simulateur Connect IQ
3. Vérifier que le code PIN s'affiche
4. Vérifier le polling (logs réseau dans simulateur)
5. Tester l'authentification complète via `/device/verify`
6. Tester sur appareil physique

