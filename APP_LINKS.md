# App Links

Ce document décrit la procédure pour qu'un lien web `https://www.pedalons.fr/...` ouvre l'app mobile au lieu du navigateur.

## Domaine

Toutes les configurations pointent vers `www.pedalons.fr`.

## Fichiers concernés

| Fichier | Rôle |
|---------|------|
| `frontend/src/config/paths.ts` | Paths frontend (source de vérité) |
| `mobile/lib/config/paths.dart` | Paths mobile (miroir du frontend) |
| `mobile/lib/config/router.dart` | GoRouter - routes Flutter |
| `frontend/public/.well-known/apple-app-site-association` | iOS Universal Links |
| `frontend/public/.well-known/assetlinks.json` | Android App Links |
| `mobile/ios/Runner/Runner.entitlements` | Domaines associés iOS |
| `mobile/android/app/src/main/AndroidManifest.xml` | Intent filters Android |
| `mobile/lib/main.dart` | Deep link handler (package `app_links`) |

## Ajouter un app link pour une nouvelle route

### 1. S'assurer que la route existe dans le mobile

- Ajouter le path builder dans `mobile/lib/config/paths.dart`
- Ajouter la route dans `mobile/lib/config/router.dart`
- Vérifier que le path est identique à celui de `frontend/src/config/paths.ts`

### 2. iOS - apple-app-site-association

Fichier : `frontend/public/.well-known/apple-app-site-association`

Ajouter le pattern dans `applinks.details[0].paths` :

```json
{
  "applinks": {
    "details": [
      {
        "appID": "7Q49262697.fr.pedalons.mobile",
        "paths": [
          "/teams/*/rides/*",
          "/teams/*/routes/*",
          "/nouvelle/route/*"
        ]
      }
    ]
  }
}
```

Syntaxe des patterns :
- `*` = n'importe quelle sous-chaîne (un seul segment ou plus)
- `?` = un seul caractère
- `NOT /path` = exclure un path

Ref : https://developer.apple.com/documentation/bundleresources/applinks

### 3. Android - AndroidManifest.xml

Fichier : `mobile/android/app/src/main/AndroidManifest.xml`

Ajouter un `<data android:pathPattern="..." />` dans l'intent-filter existant :

```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https" android:host="www.pedalons.fr" />
    <data android:pathPattern="/teams/.*/rides/.*" />
    <data android:pathPattern="/teams/.*/routes/.*" />
    <data android:pathPattern="/nouvelle/route/.*" />
</intent-filter>
```

Syntaxe `pathPattern` :
- `.*` = n'importe quelle séquence de caractères
- `.` = un seul caractère quelconque
- `\\` = échapper un caractère littéral

Ref : https://developer.android.com/training/app-links

### 4. assetlinks.json (Android uniquement)

Fichier : `frontend/public/.well-known/assetlinks.json`

Ce fichier ne filtre pas par path, il associe le domaine au package Android. Il ne nécessite de modification que si le package name ou le certificat de signature change.

Pour obtenir le SHA256 fingerprint :

```bash
# Debug
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android

# Release
keytool -list -v -keystore <release-keystore> -alias <alias>
```

### 5. Entitlements iOS

Fichier : `mobile/ios/Runner/Runner.entitlements`

Ne nécessite de modification que si le domaine change. Le domaine `www.pedalons.fr` est déjà configuré.

## Deep link handler

Le traitement des deep links est dans `mobile/lib/main.dart` via le package `app_links` :
- Au lancement : `appLinks.getInitialLink()` récupère le lien qui a ouvert l'app
- En cours d'exécution : `appLinks.uriLinkStream` écoute les nouveaux liens
- Le path est passé directement à `GoRouter.go()`

Aucune modification nécessaire dans le handler sauf si un traitement spécial est requis (query params, etc.).

## Vérification

### iOS

```bash
# Vérifier que le fichier est accessible
curl -s https://www.pedalons.fr/.well-known/apple-app-site-association | jq .

# Sur device : Réglages > Développeur > Universal Links > Diagnostics
```

### Android

```bash
# Vérifier que le fichier est accessible
curl -s https://www.pedalons.fr/.well-known/assetlinks.json | jq .

# Vérifier la configuration sur device
adb shell pm get-app-links fr.pedalons.mobile

# Tester un lien
adb shell am start -a android.intent.action.VIEW -d "https://www.pedalons.fr/teams/mon-equipe/rides/sortie" fr.pedalons.mobile
```

## Routes actuellement couvertes

| Pattern | Exemple |
|---------|---------|
| `/teams/*/rides/*` | `/teams/mon-equipe/rides/sortie-dimanche` |
| `/teams/*/routes/*` | `/teams/mon-equipe/routes/col-du-galibier` |
