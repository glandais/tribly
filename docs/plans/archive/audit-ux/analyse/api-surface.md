# Surface API Pédalons — ce que le mobile peut afficher, et ce qui manque

> Périmètre : **consultation et participation d'un membre**. Administration, création,
> édition, outils GPX et routes `/admin` / `/platform` sont **hors scope** et ne sont
> mentionnés que lorsqu'ils expliquent une lacune côté lecture.
>
> Sources : `contracts/openapi.yaml` (v **1.2.0**, 11 798 lignes),
> `backend/src/main/java/fr/pedalons/api/**/*Resource.java`,
> `mobile/lib/api/generated/` + `mobile/lib/features/**/data/`,
> `frontend/src/api/endpoints/` + `frontend/src/pages/`.

---

## 1. Inventaire des endpoints de lecture en scope

### 1.1 Vue d'ensemble

| Domaine | Endpoint | Réponse | Pagination | Client mobile généré | Utilisé par le mobile |
|---|---|---|---|---|---|
| Config | `GET /api/config` | `ConfigDto` | — | oui | oui |
| Config | `GET /api/version` | `VersionDto` | — | oui | oui |
| Profil | `GET /api/users/me` | `UserDto` | — | oui | oui |
| Équipes | `GET /api/teams?minRole&search&page&size` | `TeamListResponse` | oui | oui | oui |
| Équipes | `GET /api/teams/{teamSlug}` | `TeamDetailDto` | — | oui | oui |
| Équipes | `GET /api/teams/{teamSlug}/members?role&search&page&size` | `MemberListResponse` | oui | oui | oui — gradué par rôle et par `Team.enableMemberDirectory` depuis `3.0.0` (voir NEXT.md §3.1) |
| Pages équipe | `GET /api/teams/{teamSlug}/pages` | `TeamPageSummaryDto[]` | — | oui | non (déjà inclus dans `TeamDetailDto.pages`) |
| Pages équipe | `GET /api/teams/{teamSlug}/pages/{pageSlug}` | `TeamPageDto` | — | oui | oui |
| Publications | `GET /api/publications?type&search&from&to&minRole&page&size` | `PublicationListResponse` | oui | oui | oui (feed accueil) |
| Publications | `GET /api/teams/{teamSlug}/publications?type&search&from&to&page&size` | `PublicationListResponse` | oui | oui | oui (feed équipe + « sorties à venir ») |
| Sorties | `GET /api/teams/{teamSlug}/rides/{rideSlug}` | `RideDto` | — | oui | oui |
| Voyages | `GET /api/teams/{teamSlug}/trips/{tripSlug}` | `TripDto` | — | oui | oui |
| Publications | `GET /api/teams/{teamSlug}/posts/{postSlug}` | `PostDto` | — | oui | oui |
| Parcours | `GET /api/routes?…` (tous teams) | `RouteListResponse` | oui | oui | oui |
| Parcours | `GET /api/teams/{teamSlug}/routes?…` | `RouteListResponse` | oui | oui | oui |
| Parcours | `GET /api/teams/{teamSlug}/routes/{routeSlug}` | `RouteDetailDto` | — | oui | oui |
| Parcours | `GET /api/routes/bounds?…` et `GET /api/teams/{teamSlug}/routes/bounds?…` | `RouteBoundsResponse` | — | oui | **non** |
| Parcours | `GET /api/routes/tiles/{z}/{x}/{y}.mvt` et variante équipe | MVT binaire | — | oui | **non** |
| Parcours | `GET /api/teams/{teamSlug}/routes/{routeSlug}/usages` | `RouteUsagesResponse` | — | oui | **non** |
| Commentaires | `GET /api/teams/{t}/{rides\|routes\|posts\|trips}/{slug}/comments` | `CommentListResponse` | **non** | oui | **non** |
| Annonces | `GET /api/teams/{teamSlug}/classifieds?adType&search&from&to&page&size` | `AdListResponse` | oui | oui | oui |
| Annonces | `GET /api/teams/{teamSlug}/classifieds/{slug}` | `AdDto` | — | oui | oui |
| Lieux | `GET /api/teams/{teamSlug}/places?search&filterStart&filterEnd&page&size` | `PlaceListResponse` | oui | oui | **non** |
| Lieux | `GET /api/teams/{teamSlug}/places/{placeId}` | `PlaceDetailDto` | — | oui | **non** |
| Calendrier | `GET /api/calendar/events?from&to` | `CalendarEventsResponse` | **non** | oui | oui |
| Calendrier | `GET /api/teams/{teamSlug}/calendar/events?from&to` | `CalendarEventsResponse` | **non** | oui | oui |
| Calendrier | `GET /api/calendar/token` | `CalendarTokenDto` | — | oui | **non** |
| Calendrier | `GET /api/calendar/ics?token`, `GET /api/teams/{t}/calendar/ics?token` | `text/calendar` | — | oui | **non** |
| Médias | `GET /api/download/{public\|public_unlisted\|team}/assets/{teamSlug}/{assetId}/{fileName}` | binaire | — | non (hors client typé) | oui (Dio brut) |
| Médias | `GET /api/download/{scope}/images/{teamSlug}/{assetId}/{size}` | image | — | non | oui (`AuthenticatedImage`) |
| Médias | `GET /api/download/public/avatars/{fileId}/{size}` | image | — | non | oui |

Actions de **participation** en scope (écritures, pour mémoire) :
`POST /api/teams/{t}/rides/{r}/groups/{g}/join` · `.../leave` ·
`POST /api/teams/{t}/trips/{tr}/join` · `.../leave` ·
`POST /api/teams/{t}/members/join` · `.../leave` ·
`POST /api/teams/{t}/{entity}/{slug}/comments` · `DELETE .../comments/{id}`.

### 1.2 Champs principaux des DTO de réponse

#### Feed / publications — `PublicationListResponse`
```
publications: PublicationDto[]   # oneOf(RideDto|PostDto|TripDto), discriminant "type"
total: int64, page: int32, size: int32
```

`PublicationDto` est un **polymorphe discriminé** sur `type` (`RIDE` / `POST` / `TRIP`).
Le mobile le décode en `PublicationDtoRide` / `…Post` / `…Trip` (freezed union).

**`RideDto`** — `type, team{id,name,slug,visibility}, id, slug, name,
media{markdown, assets{logo, images[], attachments[], originalGpx, gpx, fit, thumbnailLight, thumbnailDark}},
dateTime, status(DRAFT|PUBLISHED|CANCELLED), visibility(TEAM|PUBLIC_UNLISTED|PUBLIC),
publishAt, createdAt, routeSlug, participantCount, groupCount,
groups[RideGroupDto], startPlace{PlaceDetailDto}, endPlace{PlaceDetailDto},
topParticipants[PublicUserDto] (max 5), thumbnailLightUrl, thumbnailDarkUrl, deleted`

> En **liste**, `groups` est volontairement vide (`RideDto.fromListItem`) : seuls
> `groupCount` / `participantCount` / `topParticipants` (5 max, agrégés en bulk via
> `RideSummaryRepository`) sont renseignés. En **détail** (`getRide`) `groups` est
> complet avec `participants` nominatifs.

**`RideGroupDto`** — `id, name, time(LocalTime), routeSlug, averageSpeed,
maxParticipants, countParticipants, participants[PublicUserDto], sortOrder`

**`PostDto`** — `type, team, id, slug, name, media, dateTime, status, visibility,
publishAt, createdAt, deleted`

**`TripDto`** — `type, team, id, slug, name, media, dateTime, status, visibility,
publishAt, createdAt, routeSlug, participantCount, stageCount, stages[TripStageDto],
participants[PublicUserDto], thumbnailLightUrl, thumbnailDarkUrl, deleted`

**`TripStageDto`** — `id, slug, name, dateTime, route{RouteDto}, startPlace{PlaceDetailDto},
endPlace{PlaceDetailDto}, media, sortOrder`
→ `route` est un **`RouteDto` sans géométrie** : pas de tracé pour l'étape.

#### Parcours
**`RouteDto`** (liste) — `id, slug, team, name, media, distance, elevationGain,
elevationLoss, surfaceType(ROAD|GRAVEL|MTB|MIXED), visibility, createdAt, deleted`

**`RouteDetailDto`** — tout `RouteDto` + `start{GeoJsonPoint}, end{GeoJsonPoint},
createdBy{PublicUserDto}, updatedAt, tracks[TrackDto], waypoints[WaypointDto]`

**`TrackDto`** — `line{GeoJsonLineString: coordinates[][lon,lat,ele,dist]}, climbs[ClimbDto]`
**`ClimbDto`** — `startDistance, endDistance, elevationGain, averageGradient,
maxGradient, category(HC|CAT1..CAT4), parts[ClimbPartDto{startDistance,endDistance,elevationGain,grade}]`

Filtres de liste (identiques sur `/api/routes`, `/api/teams/{t}/routes`, `bounds`, `tiles`) :
`search, minDistance, maxDistance, minElevationGain, maxElevationGain,
hilliness(FLAT|HILLY|MOUNTAINOUS), surfaceType, windDirection(8 secteurs),
nearLat, nearLon, nearRadius, nearType(START|END|START_OR_END),
sortBy(DISTANCE|ELEVATION_GAIN|HILLINESS|DATE_TIME), sortDir(ASC|DESC), minRole` (sur `/api/routes` uniquement).

**`RouteUsagesResponse`** — `usages[{type(RIDE|POST|TRIP), slug, name, dateTime, teamSlug,
referencedDirectly, viaChildNames[]}]`

#### Équipes / membres
**`TeamDetailDto`** — `id, name, slug, about{MediaDto}, pages[TeamPageSummaryDto],
visibility, enableTrips, enableAds, enablePosts, enableRides, enableRoutes,
visibilityEditable, joinable, addMemberAllowed, memberCount, role(TeamRole|null),
createdAt, geometry{GeoJsonPoint}`
→ `role` porte l'appartenance de l'utilisateur courant : `null` = non membre.

**`MemberDto`** — `team{TeamPublicationDto}, id, user{PublicUserDto}, role(MEMBER|ORGANIZER|ADMIN), joinedAt`

#### Annonces
**`AdDto`** — `team, id, slug, name, media, status, visibility, adType(SALE|RENTAL|WANTED),
price, rentalPeriod(DAY|WEEK|MONTH), locationDescription, createdAt, updatedAt, createdById, deleted`
→ **`locationGeometry` n'est exposé que par `AdEditDto`** (`/edit`, hors scope consultation) :
en lecture pure on n'a que `locationDescription` textuelle, donc **pas de carte des annonces**.

#### Calendrier
**`CalendarEventDto`** — `id, title, start, end, allDay, type(RIDE|TRIP_STAGE),
teamSlug, teamName, entitySlug, tripSlug`

#### Commentaires
**`CommentListResponse`** — `items[CommentDto], total`
**`CommentDto`** — `id, content, author{PublicUserDto}, createdAt, parentId, replies[CommentDto]` (arbre imbriqué)

#### Profil
**`UserDto`** — `id, email, displayName, avatarUrl, createdAt, unitSystem(METRIC|IMPERIAL),
platformRole, emailVerified, requiresEmail, connectedServices[GpsServiceConnectionDto],
socialIdentities[SocialIdentityDto]`

---

## 2. Ce que le WEB consomme et que le MOBILE n'utilise pas

Le client Dart est régénéré intégralement depuis le contrat : **tous les endpoints existent
dans `mobile/lib/api/generated/clients/`**. Le vrai écart est donc *fonctionnel*, pas
*structurel* — le mobile n'appelle simplement jamais ces clients.

| Capacité web | Endpoint(s) | Client mobile | Appelé par l'app mobile |
|---|---|---|---|
| **Commentaires** sur sortie / parcours / publication / voyage (fil imbriqué, réponses, suppression) | `…/comments` × 4 entités, `RideCommentsClient`, `RouteCommentsClient`, `PostCommentsClient`, `TripCommentsClient` | présent | **non** — aucun écran de commentaires |
| **Carte de tous les parcours** (tuiles vectorielles + cadrage) | `routes/tiles/{z}/{x}/{y}.mvt`, `routes/bounds` | présent | **non** — le mobile n'a qu'une carte par parcours (`RouteMap`) |
| **« Utilisé dans »** sur un parcours | `routes/{slug}/usages` | présent | **non** |
| **Liste des membres paginée / filtrée par rôle / recherchable** | `teams/{t}/members?page&size&role&search` | présent | partiel : `TeamRepository.getTeamMembers()` appelle sans `page`/`size` → **20 premiers membres seulement** |
| **Lieux de l'équipe** (départs / arrivées, carte, détail) | `teams/{t}/places`, `…/{placeId}` | présent | **non** |
| **Abonnement calendrier ICS** (token + URL de flux) | `calendar/token`, `calendar/ics`, `teams/{t}/calendar/ics` | présent | **non** |
| **Filtres de publications** : `search`, `from`/`to`, `minRole` | `publications`, `teams/{t}/publications` | présent | **non** — le mobile ne passe que `type` et `page`/`size` (`publication_feed_provider.dart`) |
| **Filtre `minRole` sur la liste de parcours** (« mes équipes » vs tout) | `/api/routes?minRole` | présent | **non** |
| **Proximité géographique** sur les parcours (`nearLat`/`nearLon`/`nearRadius`/`nearType`) | listes + bounds + tiles | présent | **non** — pourtant c'est le filtre le plus naturel sur mobile |
| **Filtre `from`/`to` sur les annonces** | `teams/{t}/classifieds?from&to` | présent | **non** |
| **Modèles de sortie** (consultation d'un gabarit) | `teams/{t}/ride-templates` | présent | non (assimilable à de l'outillage d'organisateur) |
| **Détail d'étape avec carte** | web fait `getTrip` puis `getRoute(stage.route.slug)` | présent | **non** — `stage_detail_page.dart` n'affiche aucune carte |
| **Page d'étape / carte plein écran** | pages web dédiées | — | **non** |

Champs présents dans le contrat mais **jamais lus par le mobile** :
`RouteDetailDto.tracks[].climbs` (profil de cols), `RouteDetailDto.createdBy`,
`RouteDetailDto.waypoints`, `TeamDetailDto.geometry`, `TeamDetailDto.memberCount`
sur les cartes d'équipe, `RideGroupDto.sortOrder`, `AdDto.rentalPeriod`.

---

## 3. Capacités visibles sur le site sans endpoint dédié → évolutions d'API requises

### 3.1 Il n'existe **aucune liste de sorties, de voyages ou de publications par type autrement que via `/publications`**

Le contrat déclare `RideListResponse`, `TripListResponse` et `PostListResponse`…
mais **aucun `GET` ne les retourne**. Ce sont des schémas morts. Conséquence directe :
`mobile/lib/features/rides/data/ride_repository.dart` appelle
`listPublications(type: RIDE)` puis **reconstruit un `RideDto` champ par champ** à partir
du membre d'union `PublicationDtoRide` (24 lignes de recopie manuelle).

> **À ajouter** : `GET /api/teams/{teamSlug}/rides` et `GET /api/rides` avec
> `from`/`to`/`status`/`participating`/`page`/`size`, retournant `RideListResponse`.
> Idem pour `trips`. Cela supprime l'union polymorphe côté agenda et rend le filtre
> « à venir » natif.

### 3.2 Aucune notion de « moi » dans les agrégats

Rien dans l'API ne dit **si l'utilisateur courant est inscrit** à une sortie, un groupe
ou un voyage. Le mobile doit charger `groups[].participants[]` en entier
(`ride_detail_page.dart` ligne 483 compare `authProvider.user.id` à la liste) et le web
fait pareil. Sur une sortie à 200 inscrits c'est plusieurs centaines de `PublicUserDto`
transférés pour répondre à un booléen.

> **À ajouter** : sur `RideDto` / `RideGroupDto` / `TripDto`, des champs calculés
> `registered: boolean`, `registeredGroupId: string|null`, `waitlisted: boolean`,
> `full: boolean` (dérivé de `maxParticipants`). Et un
> `GET /api/users/me/participations?from&to&status` renvoyant les inscriptions de
> l'utilisateur, indispensable pour un onglet « Mes sorties » et pour des rappels push.

### 3.3 Aucun compteur de commentaires

`CommentListResponse` a un `total`, mais **aucun `RideDto` / `PostDto` / `TripDto` /
`RouteDto` ne porte de `commentCount`**. Afficher « 12 commentaires » sur une carte de
feed impose un appel par élément → N+1 garanti sur une liste de 20.

> **À ajouter** : `commentCount` sur les DTO de publication et de parcours
> (agrégé en bulk comme `participantCount` l'est déjà via `RideSummaryRepository`).

### 3.4 Commentaires non paginés et non triés

`GET …/comments` ne prend **aucun paramètre** : pas de `page`, `size`, `sort`, ni
`parentId`. Tout l'arbre (`CommentDto.replies` récursif) est renvoyé d'un bloc.
Sur `n-peloton`, un fil populaire fait exploser la réponse.

> **À ajouter** : `page`, `size`, `sort=CREATED_ASC|CREATED_DESC`, `parentId`
> (chargement paresseux des réponses), + `replyCount` par nœud.

### 3.5 Pas de tuiles ni de bounds pour autre chose que les parcours

Le web a `AllRoutesMapPage` / `RoutesMapPage` (MVT + `bounds`), mais il n'existe
**aucun équivalent pour les sorties, les lieux ou les annonces**. Le web contourne pour
les lieux en chargeant toute la page `PlaceListResponse` et en posant des marqueurs.

> **À ajouter** : `GET /api/teams/{t}/places/bounds`, et surtout un
> `GET /api/map/features?bbox=&types=RIDE,ROUTE,PLACE,AD&…` renvoyant du GeoJSON léger —
> une carte « autour de moi » multi-entités est *la* fonctionnalité mobile évidente
> qui n'a aucun endpoint aujourd'hui.

### 3.6 Pas de géométrie sur les annonces en lecture

`AdDto` n'expose que `locationDescription`. `locationGeometry` n'existe que dans
`AdEditDto`, servi par `…/classifieds/{slug}/edit` (édition, hors scope). Impossible de
faire « annonces près de moi ».

> **À ajouter** : `locationGeometry` sur `AdDto`, plus `nearLat`/`nearLon`/`nearRadius`
> et `minPrice`/`maxPrice`/`sortBy` sur `GET …/classifieds`.

### 3.7 Calendrier : pas de bornes, pas de pagination, pas d'assez de contenu

`CalendarEventsResponse` = `{events: […]}` brut, sans total ni curseur. `CalendarEventDto`
n'a ni lieu, ni distance, ni vignette, ni statut d'inscription → une vue agenda mobile
correcte doit refaire un `getRide` par événement.

> **À ajouter** : `startPlaceName`, `distance`, `elevationGain`, `thumbnailUrl`,
> `registered`, `groupName`, `status` sur `CalendarEventDto` ; et un plafond explicite
> sur la fenêtre `from`/`to`.

### 3.8 Pas de recherche transverse

Le site n'a que des recherches par liste (`search=` sur teams, publications, routes,
members, ads, places). Une barre de recherche unique mobile devrait fan-out sur 6
endpoints.

> **À ajouter** : `GET /api/search?q&types=TEAM,RIDE,ROUTE,POST,TRIP,AD,MEMBER&limit`
> renvoyant des résultats typés et légers.

### 3.9 Pas de configuration de carte servie par l'API

`ConfigDto` = `{webAuthnRpId, appName, singleTeam, pinnedTeamSlug}`. Les styles de carte
sont **codés en dur des deux côtés** : `https://tiles.versatiles.org/assets/styles/{colorful|eclipse}/style.json`
dans `mobile/lib/features/routes/presentation/widgets/route_map.dart`, et
`frontend/src/components/map/mapStyles.ts` côté web. Changer de fournisseur impose de
publier une nouvelle version de l'app.

> **À ajouter** : `mapStyles[{id,label,url,darkVariant}]`, `tileServerBaseUrl`,
> `defaultCenter`, `minSupportedAppVersion` / `forceUpdate` dans `ConfigDto`.

### 3.10 Pas de notifications

Aucun endpoint d'enregistrement de token push, aucun flux d'activité, aucun compteur non-lu.
Une app mobile ambitieuse (rappel de sortie J-1, réponse à mon commentaire, nouvelle
publication de mon équipe, annulation de sortie) n'a rien sur quoi s'appuyer.

> **À ajouter** : `POST /api/users/me/devices` (token FCM/APNs, plateforme, locale),
> `GET /api/users/me/notifications?page&size&unreadOnly`,
> `POST /api/users/me/notifications/read`, et
> `GET|PUT /api/users/me/notification-preferences`.

### 3.11 Pas de synchronisation incrémentale ni de cache HTTP

Aucun `ETag`, `Last-Modified` ni `Cache-Control` sur les endpoints JSON : seul
`RouteTiles.java` pose un `CacheControl`. Aucun paramètre `since`/`updatedAfter` nulle part.
Une app mobile ne peut ni rafraîchir en delta, ni servir du contenu hors-ligne validé.

> **À ajouter** : `ETag` + `If-None-Match` sur les détails (`getRide`, `getRoute`,
> `getTeam`, `getPost`, `getTrip`), `updatedAt` systématique sur tous les DTO de liste,
> et `?updatedSince=` sur les listes principales.

### 3.12 Pagination par offset uniquement

Toutes les listes sont `page`/`size` + `total`. Sur un feed trié par date qui bouge
(nouvelle publication pendant le scroll), l'offset décale et duplique/saute des éléments.
`n-peloton` (1999 membres, beaucoup de contenu) est exactement le cas qui casse.

> **À ajouter** : pagination par curseur (`cursor` + `nextCursor`) sur
> `/api/publications`, `/api/teams/{t}/publications`, `/api/routes`, `…/members`,
> `…/classifieds`, en gardant `total` pour l'affichage.

### 3.13 Pas de sélection de champs / de vue « compacte »

Aucun mécanisme (`fields=`, `view=summary`) pour demander une réponse allégée.
Voir §4.1.

---

## 4. Points de friction pour le mobile

### 4.1 Payloads de liste beaucoup trop lourds — `media.markdown` intégral dans le feed

`RideDto`, `PostDto`, `TripDto` embarquent `media: MediaDto` = **le markdown complet du
corps** + `assets{logo, images[], attachments[], originalGpx, gpx, fit, thumbnailLight,
thumbnailDark}`. Une page de 20 publications transporte 20 corps d'article entiers alors
que la carte de feed n'affiche qu'un titre, une vignette et une date
(`mobile/lib/features/teams/presentation/widgets/publication_card.dart`).

En plus, `TripDto` en liste embarque **`stages[]` au complet** — et chaque
`TripStageDto` contient un `RouteDto` complet avec son propre `MediaDto`. Un voyage de
10 étapes = 11 `MediaDto` imbriqués **par ligne de feed**.

> **Correctif** : DTO de liste dédiés (`…SummaryDto`) avec `excerpt` (n premiers
> caractères, généré serveur), `thumbnailUrl`, et **sans** `assets` ni `stages`.
> Ou un paramètre `view=compact|full` sur les endpoints de liste.

### 4.2 `RouteDetailDto` : la géométrie complète, sans simplification ni pagination

`TrackDto.line` est un `LineString<G3DM>` : **un point tous les quelques mètres, chacun
avec `[lon, lat, ele, dist]`**, sérialisé en JSON. Un parcours de 150 km fait
typiquement plusieurs milliers de points → plusieurs mégaoctets de JSON à parser sur un
téléphone d'entrée de gamme, dans l'isolate principal (`freezed`/`json_serializable`).
Il n'existe aucun paramètre de tolérance, de zoom, ni de format binaire.

> **Correctif** : `GET …/routes/{slug}?simplify=<tolérance m>` ou `?points=<max>`
> (Douglas-Peucker serveur), un endpoint séparé
> `GET …/routes/{slug}/track?format=polyline|geojson|mvt` pour ne pas payer la géométrie
> quand on ne veut que les métadonnées, et un
> `GET …/routes/{slug}/elevation-profile?samples=300` pour le graphe d'altitude
> (aujourd'hui il faut toute la trace pour dessiner un profil de 300 pixels de large).

### 4.3 Membres : `getMembers` appelé sans pagination

`TeamRepository.getTeamMembers(slug)` appelle `getMembers(teamSlug: slug)` sans `page`
ni `size` → **20 membres**, silencieusement. Sur `n-peloton` (1999 membres) l'écran est
faux. L'endpoint supporte pourtant `page`, `size`, `role` et `search` : c'est un manque
côté app, mais il révèle aussi qu'il n'y a **aucun endpoint « membres autour de moi » ni
d'avatars groupés**.

### 4.4 N+1 structurels

| Écran | Ce qui manque | Appels supplémentaires |
|---|---|---|
| Détail d'étape de voyage | `TripStageDto.route` est un `RouteDto` **sans géométrie** | 1 `getRoute` par étape affichée (le web le fait déjà, le mobile a renoncé à la carte) |
| Détail de sortie avec carte | `RideDto` n'a que `routeSlug` + vignettes | 1 `getRoute` (le web charge `RoutesMapView`, le mobile ne montre rien) |
| Groupes d'une sortie avec parcours différents | `RideGroupDto.routeSlug` seul | 1 `getRoute` **par groupe** |
| Compteur de commentaires sur une carte de feed | pas de `commentCount` | 1 appel `/comments` par élément |
| Agenda enrichi | `CalendarEventDto` trop maigre | 1 `getRide` par événement |
| Statut d'inscription dans une liste de sorties | pas de `registered` | impossible sans charger le détail de chaque sortie |

### 4.5 Deux mécaniques d'images incohérentes et coûteuses

Les images passent par `/api/download/{scope}/images/{teamSlug}/{assetId}/{size}` avec un
placeholder `{size}` résolu côté client (`resolveImageUrl`), **et exigent l'en-tête
`Authorization`** — `AuthenticatedImage` doit donc injecter le Bearer dans
`CachedNetworkImage`. Conséquences mobiles : pas de préchargement natif, invalidation du
cache à chaque rotation de token, et impossibilité d'utiliser un CDN.

> **Correctif** : URLs signées à durée de vie (`?sig=&exp=`) servies directement dans les
> DTO, plus un jeu de tailles déclaré côté serveur (`srcset`-like) et `blurHash`/
> `dominantColor` pour les placeholders.

### 4.6 Recopie manuelle de DTO due au polymorphe

`PublicationDto` étant un `oneOf` discriminé, le mobile manipule
`PublicationDtoRide|Post|Trip` et doit **reconstruire un `RideDto`** pour réutiliser ses
widgets. C'est fragile : tout nouveau champ sur `RideDto` doit être recopié à la main dans
`ride_repository.dart`, sans erreur de compilation si on l'oublie. Des endpoints de liste
mono-type (§3.1) suppriment le problème.

### 4.7 Pas de garde-fou sur `size`

Aucun plafond serveur sur `size` (aucun `Math.min(size, MAX)` dans le code des ressources).
Une app mobile peut demander `size=10000` sur `/api/publications` et faire tomber le
backend. Symétriquement, il n'y a pas de `size` maximal documenté pour dimensionner un
prefetch.

### 4.8 `countRoutes` gaspille une requête complète

`RouteRepository.countRoutes()` refait un `fetchRoutes(size: 1)` juste pour lire `total`,
à chaque modification d'un filtre dans la feuille de filtres. Il n'existe pas de
`GET …/routes/count`.

> **Correctif** : `GET /api/routes/count?…mêmes filtres` (ou `HEAD` + en-tête
> `X-Total-Count`), et par extension pour les publications et annonces.

---

## 5. Évolutions d'API proposées, classées

| # | Évolution | Débloque | Effort backend |
|---|---|---|---|
| 1 | **DTO de liste compacts** (`view=compact` ou `…SummaryDto`) : `excerpt` au lieu de `markdown`, `thumbnailUrl` au lieu de `assets`, pas de `stages[]` | feed fluide, données mobiles divisées par 5-20 | moyen |
| 2 | **Champs « moi »** : `registered`, `registeredGroupId`, `full` sur `RideDto`/`RideGroupDto`/`TripDto` + `GET /api/users/me/participations` | onglet « Mes sorties », état d'inscription sans charger 200 participants, rappels | moyen |
| 3 | **Géométrie négociable** : `?simplify=`/`?points=`, `…/routes/{slug}/track?format=polyline`, `…/elevation-profile?samples=` | détail de parcours utilisable en 4G, carte de sortie, carte d'étape | moyen |
| 4 | **Push + notifications** : `POST /api/users/me/devices`, `GET /api/users/me/notifications`, préférences | rappel J-1, réponse à commentaire, annulation de sortie | élevé |
| 5 | **Listes mono-type** `GET /api/rides`, `GET /api/teams/{t}/rides` (+ `trips`) avec `from`/`to`/`participating` | supprime la recopie manuelle du polymorphe, agenda natif | faible |
| 6 | **Curseurs** (`cursor`/`nextCursor`) sur feed, parcours, membres, annonces | scroll infini correct sur `n-peloton` | moyen |
| 7 | **`commentCount`** sur publications et parcours + pagination des commentaires (`page`,`size`,`parentId`,`replyCount`) | fil de commentaires mobile viable | faible |
| 8 | **Carte multi-entités** `GET /api/map/features?bbox&types` + `locationGeometry` sur `AdDto` + filtres `near*` sur annonces | « autour de moi », carte d'annonces | moyen |
| 9 | **`CalendarEventDto` enrichi** (`registered`, `groupName`, `thumbnailUrl`, `distance`, `startPlaceName`, `status`) | agenda sans N+1 | faible |
| 10 | **`ConfigDto` étendu** (`mapStyles`, `tileServerBaseUrl`, `minSupportedAppVersion`) | styles de carte pilotés serveur, mise à jour forcée | faible |
| 11 | **`ETag` / `If-None-Match` + `updatedAt` partout + `?updatedSince=`** | rafraîchissement delta, mode hors-ligne | moyen |
| 12 | **URLs d'images signées** dans les DTO (+ `blurHash`, tailles déclarées) | cache image natif, CDN, préchargement | moyen |
| 13 | **`GET …/count`** (ou `X-Total-Count`) sur parcours / publications / annonces | feuille de filtres sans requête complète | faible |
| 14 | **`GET /api/search?q&types`** transverse | barre de recherche unique | moyen |
| 15 | **Plafond serveur sur `size`** (ex. 100) documenté dans le contrat | robustesse | trivial |
