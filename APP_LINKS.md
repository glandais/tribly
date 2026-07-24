# App Links

Ce document décrit comment un lien web `https://www.pedalons.fr/...` ouvre l'app mobile au lieu du navigateur.

## Source de vérité : `contracts/routes.yaml`

Toutes les routes d'UI — leurs variantes de langue, les plateformes concernées et leur éligibilité aux deep links — sont déclarées dans `contracts/routes.yaml`. Le script `scripts/generate-routes.mjs` régénère les fichiers plateforme à partir de ce YAML.

Exécution :

```bash
cd frontend
pnpm generate-routes
```

Fichiers générés / maintenus :

| Fichier | Rôle |
|---------|------|
| `frontend/src/config/paths.generated.ts` | Builders TypeScript typés (+ `pathVariants`, `LOCALES`) |
| `mobile/lib/config/paths.generated.dart` | Classe `Paths` + `PathVariants` (Dart) |
| `frontend/public/.well-known/apple-app-site-association` | iOS Universal Links — toutes les variantes de langue |
| `mobile/android/app/src/main/AndroidManifest.xml` | Section `<intent-filter>` entre les marqueurs `BEGIN/END generated-deeplinks` |

Fichiers **non** générés mais nécessaires :

| Fichier | Rôle |
|---------|------|
| `frontend/src/config/paths.ts` | Re-export de `paths.generated.ts` (point d'import stable) |
| `frontend/src/config/locale-context.ts` | Lit la locale courante via `i18next` |
| `mobile/lib/config/paths.dart` | Re-export de `paths.generated.dart` |
| `mobile/lib/config/locale_context.dart` | Variable globale de locale, synchronisée depuis `context.locale.languageCode` dans `app.dart` |
| `frontend/src/config/routes.config.ts` | Déclaration des routes web avec `pathVariants.xxx()` |
| `mobile/lib/config/router.dart` | GoRouter — enregistre toutes les variantes via `_perLocale(...)` et `_buildTeamShellTrees()`, et déclare les hiérarchies de deep link (`_deepLinkHierarchies`) |
| `frontend/public/.well-known/assetlinks.json` | Associe le domaine au package Android (SHA256 fingerprint) |
| `mobile/ios/Runner/Runner.entitlements` | Domaines associés iOS |
| `mobile/lib/main.dart` | Deep link handler (package `app_links`) |

## Ajouter ou modifier une route

### 1. Éditer `contracts/routes.yaml`

```yaml
- id: ride
  path:
    en: /teams/{teamSlug}/rides/{rideSlug}
    fr: /equipes/{teamSlug}/sorties/{rideSlug}
  params:
    - teamSlug
    - rideSlug
  web: true
  mobile: true
  deeplink: true
```

Champs :
- `id` : identifiant unique camelCase, utilisé comme nom de builder (`paths.ride`, `Paths.ride`)
- `path` : map `locale → template`. `{name}` pour les paramètres.
- `params` : liste de noms de paramètres. Chaque nom doit apparaître comme `{name}` dans toutes les locales.
- `web` / `mobile` : émettre un builder dans `paths.generated.ts` / `paths.generated.dart` (défauts : `web: true`, `mobile: false`)
- `mobileName` (optionnel) : nom de méthode Dart différent de `id` (ex. `ads` → `Paths.teamAds`)
- `deeplink` : inclure dans AASA + AndroidManifest (défaut `false`)

### 2. Régénérer

```bash
cd frontend && pnpm generate-routes
```

Le générateur :
- Émet des builders locale-aware : `paths.ride(a, b)` retourne l'URL dans la locale courante (`getCurrentLocale()`).
- Émet `pathVariants.ride(a, b)` retournant `{en: '...', fr: '...'}` pour enregistrer toutes les variantes dans les routeurs.
- Agrège et déduplique les patterns dans AASA et AndroidManifest (`*` pour iOS, `.*` pour Android).

### 3. Câbler la page dans les routeurs

**Web** — Ajouter une entrée dans `frontend/src/config/routes.config.ts` :

```ts
{
  id: 'ride-detail',
  paths: pathVariants.ride(':teamSlug', ':rideSlug'),
  component: RideDetailPage,
  auth: 'public',
  parentId: 'team-detail',
  breadcrumb: { type: 'dynamic', entity: 'ride' },
}
```

`RouteGenerator.tsx` émet un `<Route>` React Router par variante unique.

**Mobile** — Ajouter une `GoRoute` dans `mobile/lib/config/router.dart` :

- Pour une route plate : `..._perLocale(PathVariants.xxx(), (ctx, st) => MyPage())`
- Pour une route dans la team shell : l'ajouter dans `_teamShellTree(locale)` en dérivant le segment via `underTeam(PathVariants.xxx(':teamSlug', ...))` (qui utilise `_relativeTo`)

### 4. Vérifier

```bash
# Frontend
cd frontend && pnpm typecheck && pnpm lint && pnpm build

# Mobile
cd mobile && flutter analyze

# Android App Links
curl -s https://www.pedalons.fr/.well-known/assetlinks.json | jq .
adb shell pm get-app-links fr.pedalons.mobile
adb shell am start -a android.intent.action.VIEW \
  -d "https://www.pedalons.fr/teams/mon-equipe/rides/sortie" fr.pedalons.mobile

# iOS Universal Links
curl -s https://www.pedalons.fr/.well-known/apple-app-site-association | jq .
# Sur device : Réglages > Développeur > Universal Links > Diagnostics
```

## Multi-locale

Une URL dans n'importe quelle langue supportée (`en`, `fr`) est reconnue par l'app et par le deep linking. Les URLs **générées** par `Paths.xxx()` / `paths.xxx()` utilisent la locale courante de l'utilisateur :

- Frontend : `i18next.language` → détecteur navigateur + préférence utilisateur
- Mobile : `context.locale.languageCode` propagé dans `locale_context.dart` par `PedalonsApp.build()`

Donc un user FR partage `/equipes/mon-club/sorties/balade-dimanche` ; un user EN reçoit le lien, l'OS ouvre l'app (AASA/manifest acceptent la variante FR), GoRouter/React Router la matche et affiche la bonne page.

## Deep link handler mobile

`mobile/lib/main.dart` utilise le package `app_links` :
- Au lancement : `appLinks.getInitialLink()` remplit `initialDeepLinkProvider`.
- En cours d'exécution : `appLinks.uriLinkStream` émet chaque lien reçu — **y compris le lien de
  lancement**, rejoué au démarrage.

Les deux sources convergent vers `_requestOpen()`, qui ne garde que la dernière cible et attend que
l'app soit navigable avant d'ouvrir :

1. **auth initialisée** — `app.dart` affiche son écran de chargement tant que ce n'est pas le cas,
   donc `MaterialApp.router` n'est pas monté (sans timeout : restaurer une session peut demander un
   aller-retour réseau) ;
2. **router monté** — `GoRouter.push` empile sur `routerDelegate.currentConfiguration`, vide tant
   que le `Router` n'a pas parsé sa première route. Pousser avant écrase silencieusement les
   ancêtres et laisse une pile à une seule entrée, sans retour possible.

Ensuite `ancestorsForDeepLink()` (dans `router.dart`) fournit les ancêtres à empiler sous la cible :
`go(premier ancêtre)` puis `push(...)` jusqu'à la page visée. Une page légale ouverte depuis la fiche
Play Store obtient ainsi Accueil → Confidentialité ; une sortie obtient Équipes → équipe → sortie.
Les routes qui portent déjà leur navigation (accueil, onglets du shell principal, pages d'auth) n'ont
pas d'ancêtre et sont ouvertes par un simple `go()`.

Deux règles à ne pas casser :

- **Le `GoRouter` est construit une seule fois** — `routerProvider` n'observe pas l'état d'auth, les
  changements passent par `refreshListenable`. Le recréer réinitialise la pile depuis
  `initialLocation` et efface la hiérarchie reconstruite.
- **Une chaîne d'ancêtres n'empile pas deux onglets du shell principal** — go_router les fusionne en
  un seul shell dont l'état reste sur le premier, et le mauvais onglet resterait surligné. Les
  chaînes d'équipe partent donc de l'onglet Équipes, pas de l'accueil.

Une nouvelle route deeplinkable hors shell doit déclarer sa hiérarchie dans `_deepLinkHierarchies`
(couvert par `mobile/test/deep_link_hierarchy_test.dart`).

## Références

- [Android App Links](https://developer.android.com/training/app-links)
- [iOS Universal Links](https://developer.apple.com/documentation/bundleresources/applinks)

### Empreintes de signature Android

```bash
# Debug
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android

# Release
keytool -list -v -keystore <release-keystore> -alias <alias>
```

Le SHA256 doit correspondre à ce que déclare `frontend/public/.well-known/assetlinks.json`.
