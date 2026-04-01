# Audit Pedalons — Fevrier 2026

## Resume executif

Pedalons est une plateforme multi-tenant mature pour equipes cyclistes, comprenant 5 composants (backend Java/Quarkus, frontend React/Mantine, mobile Flutter, extension Karoo Kotlin/Compose, app Garmin Monkey C). L'audit 360 degres revele un projet bien architecture avec des fondations solides, mais des lacunes critiques en securite, CI/CD, tests et documentation.

### Top 10 des actions prioritaires

| # | Action | Composant | Severite | Effort |
|---|--------|-----------|----------|--------|
| 1 | **Securiser l'endpoint `/api/device/oauth/complete`** — actuellement sans authentification, permet l'usurpation d'identite | Securite | CRITIQUE | S |
| 2 | **Activer le CI sur `develop`** — actuellement sur branche `tmp`, zero validation automatique | Infra | CRITIQUE | S |
| 3 | **Mettre en place les backups PostgreSQL et MinIO** — aucun backup, perte de donnees possible | Infra | CRITIQUE | M |
| 4 | **Ajouter des tests frontend** — 0 tests malgre Vitest installe | Frontend | CRITIQUE | L |
| 5 | **Ajouter des tests mobile** — 0 tests, pas meme de repertoire test/ | Mobile | CRITIQUE | M |
| 6 | **Corriger le bug `TeamEntityType.AD` hardcode dans `updateSlug()`** — slug redirects ne fonctionnent pas pour les non-ads | Backend | CRITIQUE | S |
| 7 | **Ajouter rate limiting sur `/api/device/oauth/complete`** — brute force possible sur les user codes | Securite | CRITIQUE | S |
| 8 | **Creer un pipeline CD** — aucun deploiement automatise, images tagguees `:latest` | Infra | CRITIQUE | L |
| 9 | **Retirer `maximum-scale=1.0`** du viewport — bloque le zoom pour les malvoyants | Frontend | CRITIQUE | S |
| 10 | **Ajouter des healthchecks Docker** a tous les services | Infra | CRITIQUE | M |

---

## Methodologie

8 agents d'audit specialises ont analyse le projet en parallele :
1. **Backend** — Architecture, tests, BDD, performance, qualite de code
2. **Frontend** — Dependances, tests, accessibilite, i18n, performance, UX, SEO
3. **Mobile** — Architecture, tests, feature parity, qualite de code, API client
4. **Karoo** — Dependances, qualite de code, tests, UX outdoor, contraintes device
5. **Garmin** — Qualite de code, URL hardcodee, devices, build, auth, UX
6. **Infrastructure & CI/CD** — CI/CD, Docker, deploiement, monitoring, backups
7. **Securite** — Auth, multi-tenancy, OWASP Top 10, secrets, cookies, uploads
8. **Documentation** — README, CLAUDE.md, PRODUCT_SHEET, BACKLOG, rules.md

Chaque agent a explore le code source, identifie les problemes et classe les actions par severite (Critique/Important/Mineur) et effort (S/M/L/XL).

---

## 1. Backend

### Etat actuel

Backend Quarkus 3.31.2, Java 21 (compile en Java 25), ~90 fichiers de test. Architecture excellente avec separation en couches (Resource -> Service -> Repository -> Domain) renforcee par ArchUnit. Null safety via `@NullMarked` sur tous les packages. Zero TODO/FIXME dans le code.

### Points forts
- ArchUnit avec 7 regles d'architecture (CheckAccess obligatoire, @Valid, layering)
- Multi-tenancy bien implemente via `PedalonsQuery` + `DomainResolver`
- Systeme de slugs avec redirections automatiques
- OTP avec rate limiting, hashage SHA-256, validation par domaine
- DTOs en records Java, formatage Spotless automatique

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| B1 | Bug `TeamEntityType.AD` hardcode dans `updateSlug()` — les redirections de slug ne fonctionnent que pour les Ads | Critique | S | `TeamEntityService.java:90` |
| B2 | `<release>25</release>` dans pom.xml vs parent `maven.compiler.release=21` — risque en prod si JDK 21 | Important | S | `backend/pom.xml:273` |
| B3 | `hibernate-spatial` version geree hors BOM Quarkus — risque d'incompatibilite | Important | S | `backend/pom.xml:96-99` |
| B4 | `@Transactional` duplique sur 10 Resources (deja present dans Services) | Important | M | `RideResource`, `PostResource`, `TripResource`, etc. |
| B5 | `GlobalExceptionMapper` logue toutes les exceptions en ERROR (y compris 4xx) | Important | S | `GlobalExceptionMapper.java:33` |
| B6 | 12 `RuntimeException` dans le code prod au lieu d'exceptions metier | Important | M | `RouteService`, `GarminClient`, `TokenEncryptionService`, etc. |
| B7 | Traitement GPX complet dans une seule transaction (connexion DB longue) | Important | L | `GpxProcessingService.java` |
| B8 | Etat OAuth stocke en `ConcurrentHashMap` (pas multi-instance, fuite memoire) | Important | M | `GpsService.java:52` |
| B9 | Requetes N+1 sur les listings (pas de JOIN FETCH) | Important | L | `TeamEntityRepository.java` |
| B10 | `PedalonsQueryContext.getUserNullable()` re-requete la DB a chaque appel | Important | S | `PedalonsQueryContext.java:92-95` |
| B11 | `FetchType.EAGER` sur plusieurs `@ManyToOne` (Ride.route, Ride.start, etc.) | Important | M | `Ride.java`, `RideGroup.java`, `Team.java` |
| B12 | Pas de test pour `DeviceAuthService` (device code flow) | Important | M | Nouveau fichier test |
| B13 | Code commente dans `PedalonsException` (~40 lignes) | Mineur | S | `PedalonsException.java:23-65` |
| B14 | Logique cle S3 dupliquee (`AssetService` vs `AssetRemoveListener`) | Mineur | S | `AssetService.java`, `AssetRemoveListener.java` |
| B15 | Indexes redondants avec contraintes UNIQUE sur `device_codes` | Mineur | S | `V5__device_codes.sql` |

---

## 2. Frontend

### Etat actuel

React 19, TypeScript 5.9, Vite 7, Mantine 8, ~97 composants TSX. 0 tests ecrits, infrastructure Vitest complete. Architecture config-driven des routes, paths type-safe, chunk splitting avance (12 chunks), prefetching intelligent.

### Points forts
- Lazy loading de toutes les 40+ pages via `React.lazy()`
- Prefetching intelligent avec `requestIdleCallback`
- Breadcrumbs avec ARIA, pagination accessible
- Dark mode complet avec prevention FOUC
- i18n FR/EN avec `satisfies` pour les cles dynamiques
- ConfirmDialog partout (jamais `confirm()` natif)

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| F1 | `maximum-scale=1.0` dans viewport — bloque le zoom, violation WCAG 2.1 | Critique | S | `index.html:6` |
| F2 | 0 tests malgre Vitest + testing-library installes | Critique | L | Nouveau `src/**/*.test.ts(x)` |
| F3 | Configuration Vitest manquante (pas de vitest.config.ts) | Critique | S | `vite.config.ts` ou nouveau fichier |
| F4 | Cle i18n `common.back` inexistante (affiche la cle brute) | Critique | S | `LoginPage.tsx:385,440` |
| F5 | FullCalendar 7.0.0-beta.6 — API instable, pas de support prod | Important | M | `package.json` |
| F6 | Pas de titres de page dynamiques (titre statique partout) | Important | M | Toutes les pages |
| F7 | Skip-to-content non implemente (cle i18n existe) | Important | S | `Layout.tsx` |
| F8 | Images sans `loading="lazy"` | Important | S | `CardImage.tsx`, `AssetImage.tsx` |
| F9 | "Groupe" hardcode en francais dans RideEditor | Important | S | `RideEditor.tsx:96` |
| F10 | Message validation Zod hardcode en anglais | Important | S | `RideEditor.tsx:42` |
| F11 | Liens `/terms` et `/privacy` vers pages inexistantes | Important | M | `LoginPage.tsx:280-281` |
| F12 | Sitemap.xml manquant | Important | M | Backend endpoint |
| F13 | Pas de SSR = SEO limite pour les bots | Important | XL | Migration architecturale |
| F14 | `dayjs` en dependance inutile | Mineur | S | `package.json` |
| F15 | 8 cles `_many` manquantes en EN (coherence structurelle) | Mineur | S | `en/common.json` |

---

## 3. Mobile

### Etat actuel

Flutter, Dart 3.10+, Riverpod 3, GoRouter 17. 8 features (auth, home, teams, rides, routes, calendar, profile, navigation). 0 tests. 49 fichiers Dart non-generes, 385 modeles generes, 58 clients API generes.

### Points forts
- Architecture feature-based propre avec data/presentation layers
- Client API genere automatiquement (contract-first)
- Auth interceptor avec queue de requetes et retry
- Systeme adaptatif responsive (bottom nav / navigation rail)
- Animations bien implementees (shimmer, staggered, hero)

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| M1 | 0 tests — pas meme de repertoire `test/` | Critique | M | Nouveau `mobile/test/` |
| M2 | Memory leak — stream subscription non dispose dans `_DeepLinkHandler` | Critique | S | `main.dart:60` |
| M3 | `rules.md` recommande ValueNotifier mais le code utilise Riverpod — contradiction | Important | S | `rules.md` |
| M4 | Dependances inutilisees : `flutter_hooks`, `hooks_riverpod`, `riverpod_generator` | Important | S | `pubspec.yaml` |
| M5 | Labels de navigation hardcodes en francais | Important | S | `navigation_destination.dart` |
| M6 | Widgets dupliques (`_RideCard`, `_StatItem`, `_formatDate`) | Important | M | `home_page.dart`, `team_detail_page.dart` |
| M7 | Couleurs hardcodees (`Colors.green/blue/red`) | Important | S | `verify_email_page.dart` |
| M8 | Pas de renderer Markdown pour les descriptions | Important | M | `ride_detail_page.dart`, `route_detail_page.dart` |
| M9 | Feature parity manquante : Posts, Comments, Publications feed | Important | XL | Nouveaux features |
| M10 | 6 TODOs non implementes (discover teams, delete account, notifications) | Important | L | `profile_page.dart`, `teams_page.dart` |
| M11 | Lint rules trop minimales (`analysis_options.yaml`) | Moyen | S | `analysis_options.yaml` |
| M12 | `debugPrint` au lieu de `dart:developer.log` | Mineur | S | `main.dart` |
| M13 | Pas de gestion offline | Mineur | XL | Transversal |

---

## 4. Karoo

### Etat actuel

Extension Kotlin/Compose pour Hammerhead Karoo. Package `fr.pedalons.karoo`. 7 fichiers source, ~2745 lignes (dont 1519 dans `MainActivity.kt`). 0 tests. Dark theme correct pour usage outdoor.

### Points forts
- Dark theme coherent pour lisibilite outdoor
- Auth device code flow conforme RFC 8628
- DTOs legers (~200 bytes/route)
- Navigation par boutons physiques bien geree

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| K1 | `MainActivity.kt` monolithique (1519 lignes) — toute l'UI + logique | Critique | M | `MainActivity.kt` |
| K2 | Aucun test, pas de repertoire test | Critique | M | Nouveau `src/test/` |
| K3 | Pas de ViewModel — tout l'etat dans `remember`, perdu a la recreation | Critique | L | `MainActivity.kt` |
| K4 | Compose BOM 2024.09.02 — plus d'un an de retard | Important | S | `libs.versions.toml` |
| K5 | ktor 3.0.3 vs 3.4.0 disponible | Important | M | `libs.versions.toml` |
| K6 | `startActivityForResult` deprece | Important | S | `MainActivity.kt` |
| K7 | Logique refresh token dupliquee dans 3 endroits | Important | S | `MainActivity.kt`, `GpsConnectActivity.kt` |
| K8 | `generateQrCode` dupliquee entre 2 fichiers | Important | S | `AuthActivity.kt`, `GpsConnectActivity.kt` |
| K9 | Pas de gestion `slow_down` RFC 8628 | Important | S | `AuthActivity.kt` |
| K10 | Pas de pagination des routes — risque depassement 100KB | Important | M | `PedalonsApiClient.kt` |
| K11 | Package `fr.pedalons.karoo` vs `fr.pedalons.karoo` dans CLAUDE.md | Mineur | S | Documentation |
| K12 | DataStore non chiffre (tokens en clair) | Mineur | M | `AuthManager.kt` |
| K13 | Navigation Compose non utilisee malgre la dependance | Mineur | S | `build.gradle.kts` |

---

## 5. Garmin

### Etat actuel

App Connect IQ Monkey C pour GPS Edge Garmin. 15 fichiers source, 1838 lignes. 13 devices supportes. Device code flow pour l'auth. Build Docker pour contourner webkit2gtk.

### Points forts
- Architecture bien decoupee (views, delegates, API, utils)
- JSDoc coherente sur chaque classe publique
- Device code flow respecte le minimum 5s de polling
- Tri des routes par proximite GPS
- Shortcut intelligent (1 entry → detail direct)

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| G1 | URL production hardcodee `https://www.pedalons.fr` — bloque le multi-tenant | Critique | M | `ApiClient.mc:12` |
| G2 | Pas de fonctionnalite de deconnexion | Critique | S | `HomeMenuDelegate.mc`, `AuthManager.mc` |
| G3 | Documentation CLAUDE.md incorrecte : `/api/garmin/routes` vs `/api/device/routes` | Critique | S | `garmin-app/CLAUDE.md` |
| G4 | `loadResource()` appele dans `onUpdate()` — performances | Important | S | `RouteDetailView.mc`, `FormatUtils.mc` |
| G5 | AM/PM hardcodes au lieu d'utiliser les strings i18n | Important | S | `FormatUtils.mc:105-106` |
| G6 | SDK version hardcodee dans Makefile Docker | Important | S | `Makefile:123,141` |
| G7 | Comparaison d'etat par chaine localisee — fragile | Important | S | `RouteDetailView.mc:114` |
| G8 | Pas de gestion `slow_down` RFC 8628 | Important | S | `ApiClient.mc` |
| G9 | Layouts a offsets fixes — mal adaptes aux 3 resolutions d'ecran | Important | M | `LoginView.mc`, `RouteDetailView.mc` |
| G10 | Collision potentielle sur `_tokenCallback` (refresh vs poll) | Important | S | `ApiClient.mc` |
| G11 | Code debug commente (`System.println`) | Mineur | S | `ApiClient.mc`, `AuthManager.mc` |
| G12 | 4 strings non utilisees (Back, Logout, AM, PM) | Mineur | S | `resources/strings.xml` |
| G13 | BUILD.md liste 7 devices, manifest en a 13 | Mineur | S | `BUILD.md` |

---

## 6. Infrastructure & CI/CD

### Etat actuel

Docker Compose avec 9 services (prod) / 6 services (dev). GitHub Actions CI sur branche `tmp` uniquement. Dependabot 9 ecosystemes. Aucun pipeline CD. Aucun monitoring. Aucun backup.

### Points forts
- Infrastructure Docker complete (Traefik, PostGIS, imgproxy, valhalla, MinIO, Varnish, TileServer)
- Dependabot bien configure (9 ecosystemes)
- `.env.example` bien documente avec placeholders

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| I1 | CI desactivee sur `develop` — branches ciblees = `['tmp']` | Critique | S | `ci.yml` |
| I2 | Aucun backup PostgreSQL ni MinIO | Critique | M | Scripts cron |
| I3 | Aucun pipeline CD — images poussees manuellement | Critique | L | Nouveau `cd.yml` |
| I4 | Tag `:latest` sur images backend/frontend — pas de rollback | Critique | S | `docker-compose.yml` |
| I5 | Pas de healthchecks Docker (sauf PostgreSQL) | Critique | M | `docker-compose.yml` |
| I6 | Aucune collecte de metriques (pas de Prometheus/Micrometer) | Critique | M | `pom.xml`, config |
| I7 | Aucun alerting | Critique | XL | Infrastructure |
| I8 | Tests frontend commentes dans le CI | Important | S | `ci.yml` |
| I9 | Version Node CI (20) vs Dockerfile frontend (25.6) | Important | S | `ci.yml` |
| I10 | `forwardedHeaders.insecure=true` sur Traefik | Important | S | `docker-compose.yml` |
| I11 | Frontend Dockerfile : `pnpm install` sans `--frozen-lockfile` | Important | S | `frontend/Dockerfile` |
| I12 | Image nginx tierce `steebchen/nginx-spa:stable` | Important | M | `frontend/Dockerfile` |
| I13 | Access logs Traefik non persistes (volume manquant) | Important | S | `docker-compose.yml` |
| I14 | Aucune limite de ressources sur les containers | Important | S | `docker-compose.yml` |
| I15 | VCL Varnish minimale (pas de purge, grace, ban) | Important | M | `varnish.vcl` |
| I16 | `backend/.env` tracke dans git avec MAPBOX_API_KEY | Important | S | `backend/.env` |
| I17 | imgproxy sans signature URL (IMGPROXY_KEY/SALT) | Important | M | `docker-compose.yml` |
| I18 | PRs Dependabot non testees (CI desactivee sur develop) | Critique | S | `ci.yml` |
| I19 | Pas de procedure de rotation des secrets | Important | M | Documentation |
| I20 | Pas de test de recovery documente | Critique | L | Documentation + scripts |

---

## 7. Securite

### Etat actuel

Multi-tenancy par domaine HTTP avec filtrage SQL. Auth JWT 15min (web) / 60min (device), refresh 30j/90j hashes SHA-256. OTP 6 chiffres avec rate limiting. WebAuthn challenges single-use. Chiffrement AES-256-GCM pour tokens tiers.

### Points forts
- Tokens hashes SHA-256 avant stockage
- AES-256-GCM avec IV aleatoire pour le chiffrement
- OTP rate limiting (max 3/5min) et invalidation apres usage
- WebAuthn challenges single-use, 5min expiry
- Isolation multi-tenant via `PedalonsQuery` + `DomainResolver`
- SecureRandom pour toute generation de tokens

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| S1 | **Endpoint `/api/device/oauth/complete` sans authentification** — userId fourni par le client, permet usurpation d'identite | Critique | S | `DeviceOAuthResource.java:81-99` |
| S2 | **Aucun rate limiting sur `/complete`** — user code 6 chars bruteforcable | Critique | S | `DeviceOAuthResource.java` |
| S3 | Pas de rotation du refresh token au refresh — vol exploitable 30/90 jours | Important | M | `AuthService.java:209-243` |
| S4 | `UserRepository.findActiveById()` sans filtre domainId — pattern fragile | Important | S | `UserRepository.java:22-24` |
| S5 | `PasskeyRepository.findByCredentialId()` sans filtre domainId | Important | S | `PasskeyRepository.java:12-14` |
| S6 | Device JWT 60min — genereux pour un token non-revocable | Important | S | `application.properties` |
| S7 | Rate limiting global HTTP absent | Important | M | Configuration infra |
| S8 | CORS origines dev en defaut | Mineur | S | `application.properties` |
| S9 | LIKE wildcards non echappees dans la recherche | Mineur | S | `UserRepository.java` |
| S10 | Header injection potentielle dans Content-Disposition | Mineur | S | `DeviceRoutesResource.java` |
| S11 | Logs insuffisants pour detecter les tentatives de brute force | Mineur | S | `AuthService.java` |
| S12 | Cookie `secure=false` par defaut | Mineur | S | `application.properties` |
| S13 | DataStore Karoo non chiffre (tokens en clair) | Mineur | M | `AuthManager.kt` |
| S14 | Pas de validation/scanning des fichiers uploades | Mineur | L | `AssetService.java` |

---

## 8. Documentation

### Etat actuel

Documentation dispersee entre CLAUDE.md (racine + 4 sous-projets), README.md, PRODUCT_SHEET.md, BACKLOG.md, BACKLOG_old.md, mobile/rules.md, garmin-app/BUILD.md. Les CLAUDE.md par composant sont les plus fiables.

### Points forts
- CLAUDE.md bien structure avec commandes, architecture, patterns, gotchas
- Workflow contract-first bien documente
- `.env.example` avec placeholders clairs

### Problemes

| # | Probleme | Severite | Effort | Fichiers |
|---|----------|----------|--------|----------|
| D1 | README.md : PostgreSQL 16→17, React 18→19, /v1→/api, Playwright fantome, frontend/.env.example inexistant | Critique | S | `README.md` |
| D2 | `mobile/rules.md` recommande ValueNotifier — le code utilise Riverpod | Critique | M | `rules.md` |
| D3 | BACKLOG.md : Garmin, Karoo, Mobile, GPS uploads marques non faits — ils existent | Critique | S | `BACKLOG.md` |
| D4 | PRODUCT_SHEET.md : "Soft delete for data preservation" — retire par V9 | Critique | S | `PRODUCT_SHEET.md` |
| D5 | CLAUDE.md racine : "magic link" → OTP, Java 21 → compile 25, Quarkus 3.30→3.31, structure mobile/karoo/garmin incorrecte | Important | M | `CLAUDE.md` |
| D6 | PRODUCT_SHEET.md : Roadmap liste mobile/Garmin/calendar comme "potentiel" — ils existent | Important | M | `PRODUCT_SHEET.md` |
| D7 | garmin-app/BUILD.md : API 3.2.0 vs manifest 3.3.0, 7 devices vs 13 | Important | S | `BUILD.md` |
| D8 | garmin-app/CLAUDE.md : endpoints `/api/garmin/routes` vs `/api/device/routes` | Important | S | `garmin-app/CLAUDE.md` |
| D9 | CLAUDE.md racine : arborescence Karoo dit `fr.pedalons.karoo` et `PedalonsExtension.kt` — c'est `fr.pedalons` et `PedalonsExtension.kt` | Important | S | `CLAUDE.md` |
| D10 | BACKLOG_old.md redondant avec BACKLOG.md — peut etre supprime | Mineur | S | `BACKLOG_old.md` |
| D11 | Guide de deploiement manquant | Mineur | M | Documentation |

---

## 9. Actions transversales

### 9.1 Tests (tous composants)

| Composant | Tests actuels | Objectif recommande | Effort |
|-----------|--------------|---------------------|--------|
| Backend | ~90 fichiers de test | Ajouter DeviceAuth, Admin, Router | M |
| Frontend | 0 | Phase 1 : utils/hooks/stores | L |
| Mobile | 0 | Phase 1 : AuthNotifier, AuthInterceptor, repos | M |
| Karoo | 0 | AuthManager, Models, formatage | M |
| Garmin | 0 (pas de framework) | Non applicable | - |

### 9.2 Device Code Flow (Karoo + Garmin)

Les deux clients partagent des problemes communs :
- Pas de gestion `slow_down` RFC 8628
- Reprise de flow interrompu non implementee
- Resilience reseau insuffisante pendant le polling
- URL hardcodee dans Garmin (`pedalons.fr`)

### 9.4 Logging et monitoring

- Backend : Ajouter `quarkus-micrometer-registry-prometheus`, passer les 4xx en WARN, ajouter des logs de securite
- Infra : Deployer Prometheus + Grafana, configurer des alertes
- Tous : Centraliser les logs (format JSON structuree)

---

## 10. Roadmap priorisee

### P0 — Immediat (< 1 semaine, effort S)

| # | Action | Composant |
|---|--------|-----------|
| 1 | Securiser `/api/device/oauth/complete` : `@RolesAllowed("user")` + userId du JWT | Securite |
| 2 | Ajouter rate limiting sur `/api/device/oauth/complete` | Securite |
| 3 | Changer `branches: ['tmp']` en `['develop']` dans `ci.yml` | Infra |
| 4 | Corriger le bug `TeamEntityType.AD` dans `TeamEntityService.updateSlug()` | Backend |
| 5 | Retirer `maximum-scale=1.0` de `index.html` | Frontend |
| 6 | Corriger la cle i18n `common.back` → `actions.back` dans LoginPage | Frontend |
| 7 | Corriger "Groupe" hardcode et validation Zod non traduite dans RideEditor | Frontend |
| 8 | Corriger le memory leak stream subscription dans `main.dart` (mobile) | Mobile |
| 9 | Corriger les documentations (README, BACKLOG, PRODUCT_SHEET, rules.md) | Docs |

### P1 — Court terme (1-4 semaines)

| # | Action | Composant |
|---|--------|-----------|
| 10 | Mettre en place les backups PostgreSQL et MinIO | Infra |
| 11 | Ajouter des healthchecks Docker a tous les services | Infra |
| 12 | Creer le pipeline CD (build, tag, push images) | Infra |
| 13 | Ajouter `quarkus-micrometer-registry-prometheus` | Backend |
| 14 | Implementer la rotation des refresh tokens | Securite |
| 15 | Aligner `<release>` Java (21 ou 25) dans le POM | Backend |
| 16 | Retirer `@Transactional` des Resources | Backend |
| 17 | Passer les logs 4xx en WARN dans GlobalExceptionMapper | Backend |
| 18 | Cacher le User dans `PedalonsQueryContext` | Backend |
| 19 | Ajouter des tests frontend Phase 1 (utils, hooks, stores) | Frontend |
| 20 | Ajouter des tests mobile Phase 1 (AuthNotifier, interceptor, repos) | Mobile |
| 21 | Supprimer les dependances inutilisees (mobile : hooks, riverpod_generator) | Mobile |
| 22 | Internationaliser les labels de navigation mobile | Mobile |
| 23 | Ajouter filtre domainId a `findActiveById` et passkey queries | Securite |
| 24 | Implementer le logout dans l'app Garmin | Garmin |
| 25 | Configurer `trustedIPs` Traefik et `--frozen-lockfile` pnpm | Infra |

### P2 — Moyen terme (1-3 mois)

| # | Action | Composant |
|---|--------|-----------|
| 26 | Refactorer `MainActivity.kt` Karoo (extraire screens, ajouter ViewModel) | Karoo |
| 27 | Mettre a jour les dependances Karoo (Compose BOM, ktor, core-ktx) | Karoo |
| 28 | Rendre l'URL Garmin configurable (Properties Connect IQ) | Garmin |
| 29 | Ajouter des titres de page dynamiques (frontend) | Frontend |
| 30 | Ajouter skip-to-content et aria-labels manquants | Frontend |
| 31 | Ajouter `loading="lazy"` sur les images | Frontend |
| 32 | Evaluer remplacement FullCalendar beta | Frontend |
| 33 | Ajouter JOIN FETCH / entity graphs pour les listings | Backend |
| 34 | Extraire le traitement fichier/S3 hors transaction GPX | Backend |
| 35 | Migrer l'etat OAuth en DB/Redis | Backend |
| 36 | Ajouter renderer Markdown dans le mobile | Mobile |
| 37 | Extraire widgets dupliques dans le mobile | Mobile |
| 38 | Generer un sitemap.xml dynamique | Frontend/Backend |
| 39 | Securiser imgproxy (KEY/SALT) | Infra |
| 40 | Enrichir la config VCL Varnish | Infra |
| 41 | Ajouter des tests Karoo (AuthManager, Models) | Karoo |
| 42 | Adapter les layouts Garmin aux differentes resolutions | Garmin |
| 43 | Cacher les `loadResource()` dans les constructeurs Garmin | Garmin |
| 44 | Procedure de rotation des secrets | Infra |
| 45 | Ajouter headers de securite HTTP (CSP, HSTS) | Infra |

### P3 — Long terme

| # | Action | Composant |
|---|--------|-----------|
| 46 | Feature parity mobile : Posts, Comments, Publications feed | Mobile |
| 47 | Gestion offline mobile | Mobile |
| 48 | SSR/SSG pour le SEO | Frontend |
| 49 | Environnement staging | Infra |
| 50 | Monitoring complet (Prometheus + Grafana + alerting) | Infra |
| 51 | Logging centralise (JSON structure, ELK/Loki) | Infra |
| 52 | Rate limiting global HTTP | Infra |
| 53 | Pre-rendering meta tags OG dynamiques | Frontend |
| 54 | Pagination des routes Karoo | Karoo/Backend |
| 55 | Tests frontend Phase 2-3 (composants, pages) | Frontend |
| 56 | Tests mobile Phase 2-3 (widgets, integration) | Mobile |

---

## Comptage par severite

| Severite | Backend | Frontend | Mobile | Karoo | Garmin | Infra | Securite | Docs | Total |
|----------|---------|----------|--------|-------|--------|-------|----------|------|-------|
| Critique | 1 | 4 | 2 | 3 | 3 | 8 | 2 | 4 | **27** |
| Important | 11 | 9 | 8 | 6 | 7 | 12 | 5 | 7 | **65** |
| Mineur | 3 | 2 | 3 | 3 | 3 | 5 | 7 | 2 | **28** |
| **Total** | **15** | **15** | **13** | **12** | **13** | **25** | **14** | **13** | **120** |
