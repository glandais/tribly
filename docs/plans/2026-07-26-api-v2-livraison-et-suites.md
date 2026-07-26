# API v2 — ce que les versions 1.3.0, 1.4.0 et 1.5.0 ont apporté, et ce qui reste

Document de référence du chantier « API v2 » mené en réponse au §3 de
[`docs/audit-ux/BRIEF.md`](../audit-ux/BRIEF.md).

- **Contrat** : `1.2.0` → `1.3.0` (le lot API v2), puis `1.3.0` → `1.4.0` (le relais de contact
  d'annonce, §1.13), puis `1.4.0` → `1.5.0` (le meneur de groupe, §1.14). Trois MINOR : aucun
  retrait, aucun renommage, aucun changement de type.
  Source : `backend/src/main/resources/application.properties:85` (`pedalons.api.version`), qui
  alimente `info.version` de `contracts/openapi.yaml` et `GET /api/version`.
- **Régénéré** : `contracts/openapi.yaml` + `.json`, client web Orval (`frontend/src/api/`), client
  mobile Retrofit/Freezed (`mobile/lib/api/generated/`).
- **Migrations** : `backend/src/main/resources/db/migration/V28__user_preferences.sql` (deux
  colonnes nullables sur `users`), `V29__ad_contact.sql` (table `ad_contacts` + colonne
  `users.contactable_by_members`, nullable) et `V30__ride_group_leader.sql` (colonne
  `ride_groups.leader_id`, nullable). Ce sont les trois seules migrations.
- **Périmètre** : lecture et participation. Deux endpoints d'écriture nouveaux seulement —
  `PATCH /api/users/me/preferences` et `POST /api/teams/{teamSlug}/classifieds/{slug}/contact` — plus
  un champ d'écriture ajouté sur une surface existante (`GroupRequest.leaderId`, §1.14).

Ce document dit **ce qui est là**, **pourquoi c'est fait comme ça**, **ce que le propriétaire du
produit a tranché**, et **ce qui n'a pas été livré** — avec assez de détail pour que les quatre
chantiers restants soient chiffrables sans re-instruction.

---

## 1. Ce qui a été livré

### 1.0 Vue d'ensemble, item par item du §3 du brief

| § | Item | État | Où |
|---|---|---|---|
| 1 | DTO de liste compacts | **Livré** — `?view=compact` | §1.1 |
| 2 | Champs « moi » + `me/participations` | **Livré sauf `waitlisted`** | §1.2 |
| 3 | Géométrie négociable | **Livré sauf `?format=polyline`** | §1.3 |
| 4 | Listes mono-type `GET /api/rides` | **Non livré — remplacé** par `participating` / `status` | §1.4 |
| 5 | `CalendarEventDto` enrichi | **Livré** ; voyages déjà présents avant | §1.5 |
| 6 | `commentCount` + pagination des commentaires | **Livré** | §1.6 |
| 7 | Carte multi-entités `/api/map/features` | **Non livré** | §4.4 |
| 8 | Proximité + `minRole` sur `/api/routes` | **Déjà fait avant ce chantier** | §1.9 |
| 9 | `climbs` / `createdBy` / `waypoints` / `usages` | **Déjà fait avant ce chantier** | §1.9 |
| 10 | Membres et participants paginés | **Déjà fait avant** ; ajout des compteurs d'équipe | §1.9, §1.8 |
| 11 | Push et notifications | **Non livré** | §4.1 |
| 12 | `ConfigDto` étendu | **Livré** | §1.7 |
| 13 | Annonces (`locationGeometry`, filtres, tri, galerie) | **Livré**, canal de contact compris | §1.8, §1.13 |
| 14 | Pagination par curseur | **Non livré** | §4.2 |
| 15 | `GET …/count` | **Livré sur 4 des 5 listes visées** | §1.10 |
| 16 | `ETag` / `updatedSince` / images signées / `blurHash` | **Non livré** | §4.3 |
| 17 | `GET /api/search` unifié / plafond de `size` | **Plafond livré, recherche unifiée non livrée** | §1.11 |

Hors brief mais livré dans le même lot : préférences utilisateur serveur (§1.12), qui est ce qui
rend le mode sombre du §5 réalisable côté produit. Livré ensuite, en 1.4.0 : le relais de contact
d'annonce (§1.13), qui referme le seul manque de l'item 13. Puis en 1.5.0 : le meneur de groupe
(§1.14), qui donne enfin une donnée à la pastille « Organisateur » des maquettes.

---

### 1.1 Item 1 — DTO de liste compacts

**Contrat.** Paramètre `view` (`ListViewMode` : `FULL` par défaut, `COMPACT`, insensible à la casse)
sur :

- `GET /api/publications`
- `GET /api/teams/{teamSlug}/publications`
- `GET /api/routes`
- `GET /api/teams/{teamSlug}/routes`
- `GET /api/teams/{teamSlug}/classifieds`
- `GET /api/users/me/participations`

**Champs ajoutés.** `excerpt` et `thumbnailUrl` sur `RideDto`, `PostDto`, `TripDto`, `RouteDto`,
`AdDto` — **présents dans les deux modes**, pas seulement en compact. En mode `COMPACT`,
`media.markdown` revient vide (`""`) et `media.assets` vide, jamais absents.

**Fichiers principaux.**

- `backend/src/main/java/fr/pedalons/enums/ListViewMode.java` (créé)
- `backend/src/main/java/fr/pedalons/common/MarkdownExcerpt.java` (créé) — aplatit le markdown en
  texte brut, `DEFAULT_MAX_LENGTH = 200` (délibérément > 150 : le client fait la coupe finale avec
  ses métriques de police)
- `backend/src/main/java/fr/pedalons/dto/common/asset/MediaDto.java` — `compact()` et l'overload
  `from(teamEntity, assetService, view)`
- `backend/src/main/java/fr/pedalons/service/asset/ThumbnailLookup.java` (créé) +
  `repository/asset/AssetRepository.findThumbnails` — résolution des vignettes **en masse**, une
  requête par page et non par ligne
- `backend/src/main/java/fr/pedalons/service/common/PublicationService.java`

**Maquettes débloquées.** `11 Accueil` (fil enrichi, extrait 150 car., vignette 80 px),
`21 Parcours` (ligne compacte), `32 Annonces` (extrait + bandeau image).

**Correction du brief.** Le §3.1 demandait « suppression de `stages[]` dans `TripDto` en liste » :
c'était **déjà le cas** avant ce chantier (`TripDto.fromListItem` passait déjà `List.of()`). Le poids
d'une page de fil venait de `media.markdown` intégral et de `media.assets`, pas des étapes.

---

### 1.2 Item 2 — Champs « moi » et `GET /api/users/me/participations`

**Champs ajoutés.**

| DTO | Champs |
|---|---|
| `RideDto` | `registered`, `registeredGroupId`, `full` |
| `RideGroupDto` | `registered`, `full`, `distance`, `elevationGain` |
| `TripDto` | `registered` |
| `CalendarEventDto` | `registered`, `groupName` (cf. §1.5) |

`registered` est `false` — jamais nul — pour un appelant anonyme. `registeredGroupId` est nul quand
l'utilisateur n'est inscrit à aucun groupe de la sortie. `RideDto.full` vaut « tous les groupes sont
pleins » ; une sortie sans groupe n'est pas pleine.

**Endpoint ajouté.** `GET /api/users/me/participations?from&to&status&view&page&size` →
`PublicationListResponse`. Réponse marquée `Cache-Control: private, no-store` : elle est spécifique
à l'appelant par construction. Les sorties et voyages sont triés du plus proche au plus lointain, et
filtrés par les mêmes règles de visibilité que le reste : quitter une équipe fait disparaître ses
sorties de cette liste.

**Fichiers principaux.**

- `backend/src/main/java/fr/pedalons/service/common/ParticipationLookup.java` (créé) — **deux
  requêtes par page** (une pour les sorties, une pour les voyages), jamais une par ligne
- `backend/src/main/java/fr/pedalons/dto/publications/response/UserParticipations.java` (créé)
- `backend/src/main/java/fr/pedalons/api/users/UserResource.java:204` (`listMyParticipations`)
- `backend/src/main/java/fr/pedalons/repository/ride/RideParticipationRepository.java`,
  `repository/trip/TripParticipationRepository.java`

**Maquettes débloquées.** `11 Accueil` (bloc « Ma prochaine sortie », badge `INSCRIT` dans le fil),
`12 Sortie` (action Rejoindre/Quitter/Complet portée par la carte de groupe), `33 Profil`
(« Mes participations »).

**Non livré : `waitlisted`.** Voir §2.3.

---

### 1.3 Item 3 — Géométrie négociable

**Contrat.**

- `GET /api/teams/{teamSlug}/routes/{routeSlug}?simplify=<m>&points=<n>` — les deux composent : la
  tolérance d'abord, le budget de points ensuite. Absents ou nuls ⇒ comportement d'avant, octet pour
  octet.
- `GET /api/teams/{teamSlug}/routes/{routeSlug}/elevation-profile?samples=<n>` →
  `ElevationProfileDto { routeId, slug, samples, distance, minElevation, maxElevation, points[] }`,
  chaque `ElevationPointDto { distance, elevation, grade }`.

**Bornes serveur** (`dto/routes/request/GeometryOptions.java`) : `simplify` plafonné à 1 000 m,
`points` à 100 000 et plancher à 2 ; `samples` ramené dans 2..1 000 puis réduit au nombre de points
réellement stockés. Aucune requête ne peut demander un travail non borné.

**Fichiers principaux.**

- `backend/src/main/java/fr/pedalons/dto/routes/request/GeometryOptions.java` (créé)
- `backend/src/main/java/fr/pedalons/service/route/TrackGeometry.java` (créé) — décimation en
  mémoire sur les `trackPoints` jsonb, pas `st_simplify` : la source de vérité du rendu est le jsonb
  3DM (altitude + distance), pas la colonne `geometry` 2D
- `backend/src/main/java/fr/pedalons/api/routes/RouteResource.java:229-306`

**Maquettes débloquées.** `13 Parcours` (profil altimétrique colorisé par pente, chargement
progressif : métadonnées, puis géométrie simplifiée, puis complète), `25 Étape` (fiche parcours
embarquée).

**Non livré.** `GET …/routes/{slug}/track?format=polyline|geojson`. Le gain (~÷4 sur le poids)
n'est pas nul, mais il impose un décodeur polyline côté Dart et `?points=` couvre déjà le besoin de
la maquette. À rouvrir seulement si une mesure réelle le justifie.

**Réserve connue.** Le coût **base** est inchangé : `elevation-profile` lit le jsonb complet des
`gpx_tracks`. Le gain est purement réseau. Un gain base demanderait une projection SQL sur le jsonb.

---

### 1.4 Item 4 — Listes mono-type : non livré, remplacé

`GET /api/rides` et `GET /api/teams/{t}/rides` **n'ont pas été créés**. Ce que le brief voulait —
« à venir », « je participe », listes typées — a été obtenu en complétant la surface existante :

- `GET /api/publications` : `participating`, `status`, `view` (en plus de `type`, `from`, `to`,
  `search`, `minRole`, `page`, `size` déjà présents)
- `GET /api/teams/{teamSlug}/publications` : `participating`, `status`, `view`

`participating=true` avec un appelant anonyme court-circuite en résultat vide, comme `minRole`.
`status` est **ANDé** avec les règles de visibilité (`AllPublicationRepository:60-65`) : il rétrécit
ce que l'appelant peut voir, il ne déverrouille jamais un statut interdit.

Justification du choix en §2.2.

---

### 1.5 Item 5 — `CalendarEventDto` enrichi

**Champs ajoutés** : `startPlaceName`, `distance`, `elevationGain`, `thumbnailUrl`, `registered`,
`groupName`, `status`.

**Fichiers principaux** : `dto/calendar/response/CalendarEventDto.java`,
`service/calendar/CalendarService.java`, `service/asset/ThumbnailLookup.java`.

**Maquette débloquée** : `22 Calendrier` — carte d'événement à deux lignes avec
`équipe • heure • lieu de départ`, distance, D+, badge `INSCRIT` et nom du groupe. Supprime le N+1
`getRide` par événement, qui était un N+1 **client**.

**Correction du brief.** Le §3.5 réclamait « l'inclusion des voyages » : les **étapes de voyage
étaient déjà des événements** (`CalendarEventType.TRIP_STAGE`) avant ce chantier. Le manque était
côté mobile, qui ne les affichait pas. Le voyage lui-même comme événement multi-jour n'a pas été
ajouté — la maquette `22` se satisfait des étapes.

**Réserve.** Le calendrier n'est toujours pas paginé (fenêtre par défaut −30 j / +180 j).
`ThumbnailLookup` évite l'hydratation des assets par événement, mais la fenêtre reste large.

---

### 1.6 Item 6 — `commentCount` et pagination des commentaires

**`commentCount`** ajouté sur `RideDto`, `PostDto`, `TripDto`, `RouteDto`, `RouteDetailDto`.

**Pagination** sur les quatre endpoints de commentaires (`…/rides/{entitySlug}/comments`,
`…/posts/…`, `…/routes/…`, `…/trips/…`) : `page`, `size`, `sort`, `parentId`.
`CommentDto.replyCount` ajouté. `CommentListResponse` gagne `itemTotal`, `page`, `size` — **`total`
conserve sa sémantique d'origine** (arbre entier), `itemTotal` compte les éléments de la page.

Bornes : `CommentListParams.DEFAULT_SIZE = 20`, `MAX_SIZE = 100`.

**Fichiers principaux.**

- `backend/src/main/java/fr/pedalons/service/comment/CommentCountLookup.java` (créé) — **deux
  requêtes par page** quel que soit le nombre de lignes : une pour les équipes dont l'appelant est
  membre parmi celles de la page, une pour les compteurs des entités survivantes
- `backend/src/main/java/fr/pedalons/dto/comments/request/CommentListParams.java`,
  `CommentListQuery.java`, `dto/comments/response/CommentCounts.java` (créés)
- `backend/src/main/java/fr/pedalons/repository/comment/CommentRepository.java`
  (`countByTeamEntityIds`, joint sur `teamEntity.team.domain.id` — `CommentRepository` n'est pas un
  `TeamEntityRepository`, le filtre de domaine y est explicite)

**Maquettes débloquées.** `12 Sortie`, `13 Parcours`, `31 Publication` (compteur, formulaire,
réponses à un niveau).

**Comportement notable.** `commentCount` est **absent du JSON** — et non à zéro — quand l'appelant
n'a pas le droit de lire les commentaires. Voir §2.7.

---

### 1.7 Item 12 — `ConfigDto` étendu

**Champs ajoutés** : `mapStyles[]` (`MapStyleDto { id, label, url, darkVariant }`),
`tileServerBaseUrl`, `defaultCenter` (`MapCenterDto { lat, lon, zoom }`), `minSupportedAppVersion`.

**Source des valeurs** : `backend/src/main/java/fr/pedalons/service/config/MapConfig.java` (créé),
`@ConfigMapping(prefix = "pedalons.map")` — configuration applicative, pas colonnes sur `Domain`.
Seul `defaultCenter` est dérivé par site (l'équipe sur laquelle le domaine est épinglé) avant repli
sur la configuration.

**Piège à ne pas rouvrir** : `pedalons.map.tile-server-base-url` est l'URL **publique** du serveur de
tuiles, à ne pas confondre avec `tileserver.url`, le rendu **interne** utilisé pour les vignettes,
qui ne doit jamais être remis à un client. `GET /api/config` est `@PermitAll`.

`minSupportedAppVersion` a été mis sur `ConfigDto` et non sur `VersionDto` : c'est `ConfigDto` que le
mobile lit au démarrage. `forceUpdate` n'a pas été ajouté — un client qui compare sa version à
`minSupportedAppVersion` en déduit la même chose.

---

### 1.8 Item 13 — Annonces

**`AdDto` gagne** : `locationGeometry` (GeoJSON Point, **flouté** — cf. §2.4), `excerpt`,
`thumbnailUrl`, `images[]`, `createdByDisplayName`. `createdById` est conservé.

**`GET /api/teams/{teamSlug}/classifieds` gagne** : `minPrice`, `maxPrice`, `nearLat`, `nearLon`,
`nearRadius`, `sortBy` (`AdSortBy` : `DATE_TIME` par défaut, `PRICE`, `NAME`), `sortDir`, `view`.
Les annonces sans prix sortent des filtres de prix (sémantique SQL des `NULL`) ; celles sans
position sortent du filtre de proximité ; en tri par prix elles sortent en dernier dans les deux
sens.

**Fichiers principaux** : `backend/src/main/java/fr/pedalons/common/CoarseLocation.java` (créé),
`dto/ads/response/AdDto.java`, `dto/ads/request/AdSearchParams.java` (créé),
`repository/ad/AdRepository.java:95-112`, `enums/AdSortBy.java` (créé).

**Maquette débloquée** : `32 Annonces` (liste, tri, carte de **secteur** — un cercle ou une pastille
floutée, jamais une punaise, cf. §2.4 et §3.1 — et galerie).

**Canal de contact** : livré depuis, par un relais e-mail qui ne publie aucune adresse. Voir §1.13,
§2.5 et §3.2.

**Ajout connexe** : `TeamDetailDto` gagne `excerpt`, `logoUrl`, `routeCount`, `upcomingRideCount`
(`repository/team/TeamStatsRepository.java`, créé — deux requêtes pour toute une page d'équipes),
et `GET /api/teams` gagne `joinable`. Cela alimente la rangée de statistiques cliquables de
`23 Équipe` et la découverte d'équipes de `34 Découverte`.

---

### 1.9 Items du brief qui étaient **déjà faits** avant ce chantier

C'est une correction du brief, et elle a de la valeur : quatre items ont été instruits, vérifiés,
et n'ont donné lieu à aucun code backend.

| Item du brief | Réalité constatée |
|---|---|
| §3.8 — « exposer `nearLat`/`nearLon`/`nearRadius`/`nearType` et `minRole` sur `/api/routes` » | **Déjà exposés** sur les 3 endpoints globaux (liste, tuile MVT, bounds) ; la proximité l'était aussi sur les 3 endpoints d'équipe. Clause PostGIS dans `RouteRepository.andSpecific`. Chantier **100 % mobile**. |
| §3.9 — « `climbs`, `createdBy`, `waypoints` jamais lus » et « `usages` à consommer » | **Déjà exposés en intégralité** : `RouteDetailDto.createdBy` / `waypoints[]`, `TrackDto.climbs[]` avec catégorie HC→CAT4, plage kilométrique, D+, pente moyenne et maximale, et `GET …/routes/{routeSlug}/usages` avec `referencedDirectly` et `viaChildNames[]`. Chantier **100 % mobile**. |
| §3.5 — « inclusion des voyages » dans le calendrier | **Étapes déjà incluses** (`CalendarEventType.TRIP_STAGE`). |
| §3.1 — « suppression de `stages[]` dans `TripDto` en liste » | **Déjà vide en liste**. |
| §3.10 — « membres paginés » | `GET /api/teams/{t}/members?page&size&role&search` **existait déjà** avec tous ses paramètres. Le mobile chargeait 20 membres sur 1 999 sans le dire. Chantier **mobile**. |
| §3.15 — « la feuille de filtres refait un `fetchRoutes(size:1)` complet » | Le `COUNT(*)` était **déjà** systématique dans `BaseRepository.getPage`. L'économie d'un `/count` porte sur la ligne transportée et sa sérialisation, pas sur le comptage. |
| §3.2 — « `full` » | Dérivable côté client depuis `countParticipants` + `maxParticipants`, déjà exposés. Exposé quand même pour éviter que chaque client refasse le calcul. |

---

### 1.10 Item 15 — Endpoints de comptage

Livrés, avec **exactement les mêmes paramètres de requête que la liste correspondante** :

- `GET /api/publications/count?from&minRole&participating&search&status&to&type`
- `GET /api/teams/{teamSlug}/publications/count?from&participating&search&status&to&type`
- `GET /api/routes/count?hilliness&maxDistance&maxElevationGain&minDistance&minElevationGain&minRole&nearLat&nearLon&nearRadius&nearType&search&surfaceType&windDirection`
- `GET /api/teams/{teamSlug}/routes/count?…` (les mêmes, sans `minRole`)

Réponse : `CountResponse { total }`.

**Non livré** : `GET /api/teams/{teamSlug}/classifieds/count`. Le CTA « Voir N annonces » de la
feuille de filtres des annonces devra, en attendant, lire `total` d'un `…/classifieds?size=1&view=compact`.
C'est un ajout S, symétrique des quatre autres.

**Maquette débloquée** : `21 Parcours` (CTA « Voir N parcours » recalculé en continu).

---

### 1.11 Item 17 — Plafond de `size`

Livré : `BaseRepository.MAX_PAGE_SIZE = 200`, appliqué par `Math.clamp` dans
`BaseRepository.getPage` — le point unique où la pagination est réellement appliquée. Un appelant qui
demande plus obtient le plafond, **pas une erreur** ; `total` reporte toujours le vrai compte, donc
un client qui voulait tout peut paginer. `DEFAULT_PAGE_SIZE = 20` inchangé (un `size=0` vaut 20).

**Non livré** : `GET /api/search?q&types=…&limit`. `GET /api/users/search` existait déjà et reste la
seule recherche transverse.

---

### 1.12 Hors brief — préférences utilisateur côté serveur

Ajouté dans le même lot parce que le §5 du brief exige la parité clair/sombre et que rien ne
stockait ce choix.

- `PATCH /api/users/me/preferences` avec `UserPreferencesRequest { unitSystem, theme, language }`,
  les trois nullables et indépendamment modifiables
- `UserDto` gagne `theme` (`ThemePreference` : `SYSTEM` / `LIGHT` / `DARK`) et `language`
- Migration `V28__user_preferences.sql` : `users.theme`, `users.language`, **nullables** — `null`
  signifie « pas choisi », ce qui n'est ni `SYSTEM` ni la langue par défaut du domaine. Un
  `NOT NULL DEFAULT` aurait fait passer tous les comptes existants pour ayant choisi.

**Maquette débloquée** : `33 Profil` (segmenté Métrique/Impérial, sélecteur de thème, langue), et
c'est ce qui rend le mode sombre pilotable — rappel : **aucune maquette ne fournit le mode sombre**,
il devra être dérivé de `docs/audit-ux/analyse/brand.md`.

---

### 1.13 Contrat 1.4.0 — le relais de contact d'annonce

Ce qui manquait à l'item 13 : le « bouton de contact » de `32 Annonces` n'avait aucune cible. C'est
livré, sous la forme d'un **relais e-mail** — le contrat ne transporte toujours aucune adresse.

**Endpoint ajouté.** `POST /api/teams/{teamSlug}/classifieds/{slug}/contact`, corps
`AdContactRequest { message }` (texte brut, 10 à 2 000 caractères, non blanc), réponse **204 sans
corps**.

Le serveur envoie l'e-mail à l'auteur et pose `Reply-To` sur l'expéditeur : l'auteur répond
directement, dans son client de messagerie. **Aucune adresse n'apparaît dans l'API** — `AdDto` ne
gagne aucun champ de contact, et c'est le point de la conception (§2.5).

**Accès** : `@CheckAccess(entityType = AD, action = READ)` — exactement la règle de lecture de
l'annonce, donc membre de l'équipe. Écrire à sa propre annonce → **400** `AD_CONTACT_SELF`.

**Champs ajoutés.** `contactableByMembers` sur `UserPreferencesRequest`
(`PATCH /api/users/me/preferences`) et sur `UserDto`. Nullable en base, et **`null` vaut
« joignable »** : aucun compte existant n'a vu ses annonces devenir muettes. Un auteur qui s'est
rendu injoignable fait répondre **400** `AD_CONTACT_OPTED_OUT`, et rien n'est envoyé.

**Quota** : `pedalons.ads.contact.max-per-window=10` sur
`pedalons.ads.contact.rate-limit-window-minutes=60`, **par expéditeur, toutes annonces confondues**
(§2.5). Dépassement → **429** `AD_CONTACT_RATE_LIMITED`, avec en-tête `Retry-After` posé par
`GlobalExceptionMapper`.

**Échec d'envoi** → **500** `AD_CONTACT_DELIVERY_FAILED`, jamais un 204 optimiste.

**Traçabilité** : table `ad_contacts` (`ad_id`, `created_by_id`, `created_at`) — qui a écrit à
quelle annonce et quand, **jamais le corps du message**.

**Fichiers principaux.**

- `backend/src/main/resources/db/migration/V29__ad_contact.sql` — table `ad_contacts`, ses deux
  index (`(created_by_id, created_at)` pour le quota, `(ad_id, created_at)` pour l'abus), et
  `users.contactable_by_members` nullable
- `backend/src/main/java/fr/pedalons/domain/ad/AdContact.java`,
  `repository/ad/AdContactRepository.java` (`countRecentBySender`)
- `backend/src/main/java/fr/pedalons/service/ad/AdService.java:233` (`contactAuthor`, `@Transactional`)
- `backend/src/main/java/fr/pedalons/service/ad/AdContactEmailService.java` — construit l'URL de
  l'annonce depuis `ResolvedSite`, choisit la langue de **l'auteur** (c'est lui qui lit), passe
  l'adresse de l'expéditeur en `Reply-To`
- `backend/src/main/java/fr/pedalons/api/ads/AdResource.java:157-198`
- `backend/src/main/java/fr/pedalons/dto/ads/request/AdContactRequest.java`,
  `dto/error/ErrorCode.java` (quatre codes ajoutés)

**Maquette débloquée** : `32 Annonces` — le bouton « Contacter le vendeur » a enfin une cible, sous
la forme d'un champ de message et d'un accusé de remise. La feuille de préférences de `33 Profil`
gagne l'interrupteur « joignable par les membres ».

**Côté exploitation : fait.** Les templates Brevo `ad-contact.fr` / `ad-contact.en` existent sous
les identifiants 10 et 11, déclarés en profil `%prod`, et l'envoi a été validé par un message réel
— la seule recette qui vaille ici, l'API de prévisualisation de Brevo n'étant pas exploitable. Si
un identifiant venait à manquer, `EmailService` lèverait `IllegalStateException: Brevo template ID
not configured for ad-contact.fr` et l'endpoint répondrait 500 en nommant le template absent. En
dev, l'envoi passe par Mailhog et ne dépend d'aucun template.

---

### 1.14 Contrat 1.5.0 — le meneur de groupe

La pastille « Organisateur » des maquettes n'avait aucune donnée à afficher : `RideGroup` n'avait que
`createdBy`, hérité de `BaseEntity`, qui vaut le **créateur de la sortie** et est donc identique sur
tous ses groupes. C'est livré, sous la forme d'une colonne dédiée. Additif : aucun retrait, aucun
renommage, aucun changement de type.

**Migration.** `backend/src/main/resources/db/migration/V30__ride_group_leader.sql` — colonne
`ride_groups.leader_id`, **nullable**, FK vers `users` en **`ON DELETE SET NULL`** (§2.10), plus un
index **partiel** `idx_ride_groups_leader on ride_groups (leader_id) where leader_id is not null` :
la colonne est nulle bien plus souvent que non.

**Entité.** `RideGroup.leader`, `@ManyToOne(FetchType.LAZY)` sur `leader_id`, nullable. Comme les
groupes d'une sortie sont mappés en un passage, le batch fetch résout **tous les meneurs d'une sortie
en une requête**, pas une par groupe — le même invariant que le §2.8.

**Lecture.** `RideGroupDto.leader`, de type `PublicUserDto` (`id`, `displayName`, `avatarUrl`),
**nullable et non requis** : Jackson est configuré `NON_NULL`, le champ **disparaît du JSON** quand
aucun meneur n'est désigné, comme `commentCount` au §2.7. Aucune adresse ni donnée privée n'est
exposée — `PublicUserDto` est la projection publique déjà utilisée ailleurs.

**Écriture.** `GroupRequest.leaderId` (TSID en chaîne), **optionnel**. Envoyer `null` **efface la
désignation** — c'est une opération réelle, pas une omission. `RideTemplateGroupRequest` ne gagne
**aucun champ de meneur** : les gabarits de sortie n'en portent pas, et instancier une sortie depuis
un gabarit ne désigne personne (§2.10).

**Validation.** Le membre désigné **doit appartenir à l'équipe propriétaire de la sortie**, vérifié
par `RideService.resolveLeader` via `UserTeamRepository.findByUserAndTeam`. Sinon **400**
`RIDE_GROUP_LEADER_NOT_MEMBER`. L'appartenance **n'est pas revérifiée ensuite** (§2.10).

**Fichiers principaux.**

- `backend/src/main/resources/db/migration/V30__ride_group_leader.sql`
- `backend/src/main/java/fr/pedalons/domain/ride/RideGroup.java` (champ `leader`)
- `backend/src/main/java/fr/pedalons/dto/rides/response/RideGroupDto.java` (champ `leader`),
  `dto/rides/request/GroupRequest.java` (champ `leaderId`, avec le constructeur d'avant conservé
  pour les appelants existants)
- `backend/src/main/java/fr/pedalons/service/ride/RideService.java:172` (`resolveLeader`)
- `backend/src/main/java/fr/pedalons/dto/error/ErrorCode.java` (`RIDE_GROUP_LEADER_NOT_MEMBER`)

**Test.** `backend/src/test/java/fr/pedalons/api/rides/RideGroupLeaderTest.java` — le meneur exposé
sur le groupe, le cas sans meneur qui reste **nul plutôt que de retomber sur le créateur**, le rejet
d'un non-membre, le rejet d'un membre d'une autre équipe, la désignation puis l'effacement par
`null`, et le test qui garde l'invariant : `groupLeader_isNotTheRideCreator`.

**Maquettes débloquées.** `12 Sortie` (pastille sur la carte de groupe) et `34 Participants`
(pastille sur la ligne du meneur) — **conditionnellement** : `leader` absent ⇒ on n'affiche rien, et
c'est le cas courant, pas le cas dégradé (§2.10).

**Rétrocompatibilité.** Colonne nullable, champ de DTO optionnel, champ de requête optionnel. Les
clients qui ignorent `leader` continuent de fonctionner ; les 665 sorties existantes de `n-peloton`
restent sans meneur jusqu'à ce que quelqu'un en désigne un.

**Reste à câbler.** Le sélecteur de meneur dans l'éditeur de groupes de
`frontend/src/components/ride/RideEditor.tsx`, puis l'écran équivalent du mobile — champ facultatif,
avec une option « aucun ». Aucun écran ne lit ni n'écrit `leader` aujourd'hui.

---

## 2. Décisions de conception et leurs raisons

### 2.1 Compaction additive (`?view=compact`) plutôt que modification en place

Retirer `media` de `RideDto` / `TripDto` en liste aurait été la solution la plus propre en théorie et
un **MAJOR** en pratique : le frontend web consomme les mêmes endpoints et les mêmes DTO.

Trois options étaient sur la table : modifier en place (MAJOR, casse le web), créer des
`…SummaryDto` dédiés (MINOR, mais double chaque DTO de publication et chaque
`…ListResponse`), ou un paramètre additif. Le paramètre l'emporte parce qu'il laisse **le même
schéma** dans les deux modes.

Corollaire assumé : en `COMPACT`, `media.markdown` vaut `""` et `media.assets` est vide — **vides,
pas absents**. Rendre ces deux champs optionnels dans le contrat les aurait rendus nullables dans
tous les clients générés, y compris ceux qui renvoient un `MediaDto` dans un corps de requête, pour
un gain qu'aucun client ne peut exploiter : un client compact lit `excerpt` et `thumbnailUrl`.

### 2.2 `/publications?type=RIDE` reste la surface canonique

`GET /api/rides` n'a pas été créé. La raison est la dette, pas l'effort : deux surfaces pour lister
des sorties, c'est deux jeux de filtres à maintenir en cohérence, et la première divergence
(`participating` sur l'une, pas sur l'autre) est déjà arrivée sur d'autres projets.

Ce que `/api/rides` apportait vraiment au client Dart, c'était un type non polymorphe — `PublicationDto`
est un `oneOf` à discriminant. Le gain a été jugé inférieur au coût : le mobile filtre déjà par
`type` et le `oneOf` est correctement généré. Si un client mono-type devient réellement pénible,
c'est un ajout MINOR à faire plus tard, sans rien à défaire.

**Conséquence à documenter dans les clients** : `type=RIDE` sur `/api/publications` est la façon de
lister les sorties. `RideListResponse` et `TripListResponse` restent des records définis et
retournés par aucun endpoint — ils datent d'avant et n'ont pas été supprimés (ce serait un MAJOR
gratuit).

### 2.3 `waitlisted` non livré, faute de support en base

Le brief le demande sur `RideDto` et `RideGroupDto`. Il n'existe **aucune liste d'attente** :
`RideParticipation` n'a que `user`, `rideGroup`, `registeredAt` — pas de statut, pas de rang, pas de
colonne.

Deux mauvaises options ont été écartées :

- **câbler `waitlisted: false` en dur** — un champ toujours faux que les clients afficheraient,
  qu'on oublierait de brancher, et qui rendrait la vraie fonctionnalité indétectable en revue ;
- **implémenter la liste d'attente** — c'est une migration, une logique de `joinGroup` (rang,
  promotion à la libération d'une place, notification), donc un chantier d'écriture hors du
  périmètre lecture de ce lot.

`waitlisted` est donc **absent du contrat 1.5.0**. Une maquette qui l'affiche doit dégrader vers
`full` (groupe complet, pas d'inscription possible) — ce que fait `12 Sortie` avec son badge
`Complet` désactivé.

### 2.4 Position d'annonce arrondie à ~1 km, **et probe de proximité quantifiée sur la même grille**

La position d'une annonce est en pratique le domicile du vendeur. L'exposer au pixel sur `AdDto` —
lu par tous les membres de l'équipe, alors qu'elle n'était jusqu'ici que sur `AdEditDto` — transforme
« vélo à vendre » en « ce vélo est à cette adresse ».

`CoarseLocation.blur` ramène le point au **centre d'une cellule fixe** de 0,01° de latitude
(≈ 1,11 km), élargie en longitude par `1/cos(lat)` pour rester à peu près carrée en mètres. Le
centrage — plutôt qu'un bruit aléatoire ou une troncature — est ce qui fait tenir le floutage : la
sortie est stable d'un appel à l'autre, donc des lectures répétées ne peuvent pas être moyennées
pour retrouver la vraie position, et elle ne dérive pas vers un coin comme le ferait une troncature.

**Le point non évident, et le vrai contenu de la décision** : flouter la sortie ne suffit pas. Le
filtre `nearLat`/`nearLon`/`nearRadius` lit la colonne **exacte**. Répéter « cette annonce est-elle à
moins de R du point C ? » en déplaçant C multilatère la position réelle, quelle que soit la
résolution de ce qui est publié. Un simple plancher de rayon ne referme pas cet oracle : il borne la
taille du cercle, pas le nombre de cercles qu'on peut intersecter.

La parade est de quantifier **la sonde** sur la même grille que la sortie :
`AdRepository` floute le point de la requête (`CoarseLocation.blur`) **et** arrondit le rayon au
multiple de cellule supérieur (`CoarseLocation.snapRadiusUp`, plancher d'une cellule). Toute question
répondable reste alors à l'intérieur de la résolution que le point flouté donne déjà.

Effet de bord accepté : un rayon demandé de 3 km est servi comme 3,33 km. Un acheteur veut savoir
« à dix minutes de vélo ou à deux heures » ; la cellule répond à ça et à rien d'autre.

**Conséquence de rendu, valable partout où la carte d'une annonce apparaît** : on affiche un
**secteur** — cercle, pastille floutée, zone — et **jamais une punaise**. Une punaise posée sur un
centre de cellule prétend une précision que la donnée n'a pas : elle désigne un point qui n'est pas
le domicile du vendeur, avec l'apparence d'une adresse. La résolution de 1 km est confirmée (§3.1).

### 2.5 Le contact d'annonce passe par un relais : `AdDto` ne porte toujours aucune adresse

`AdDto` porte `createdById` et `createdByDisplayName`. Il ne porte **ni e-mail, ni téléphone, ni
champ de contact**, et `Ad` n'a aucune colonne de contact en base. C'est vrai **après** la
livraison du relais (§1.13) comme avant : l'invariant n'a pas été relâché pour brancher le bouton,
il est ce que le relais existe pour préserver.

**Pourquoi un relais plutôt qu'un champ `contact` sur l'annonce.** Le champ libre était la solution
la moins chère à construire : une colonne, un champ de formulaire, un champ de DTO. Elle publie une
donnée personnelle **irrévocablement** à toute l'équipe — jusqu'à 1 999 personnes sur `n-peloton` —
et ce qui a été lu ne se dépublie pas : l'adresse survit dans les copies, les captures et les
exports RGPD de ceux qui l'ont recopiée. Retirer le champ plus tard ne répare rien. Le relais, lui,
laisse le serveur connaître les deux adresses et l'appelant n'en apprendre aucune ; l'expéditeur
divulgue la sienne à l'auteur seul, et parce qu'il a choisi d'écrire — une divulgation à sens
unique et volontaire, catégoriquement différente d'une publication.

**Pourquoi le quota est par expéditeur, toutes annonces confondues.** Un plafond par annonce ne
plafonne rien : un membre qui peut lire les annonces d'une équipe peut écrire à toutes, donc trois
messages par annonce sur vingt annonces font soixante e-mails partis d'un seul compte. Ce que le
seuil protège, c'est la réputation d'expédition du domaine, et un compte la consomme quel que soit
le destinataire. D'où 10 messages par heure et par expéditeur.

**Pourquoi la ligne de traçabilité est écrite dans la transaction d'envoi.** `contactAuthor` est
`@Transactional` : la ligne `ad_contacts` est persistée **avant** l'appel au fournisseur de mail, et
l'exception d'envoi remonte au lieu d'être avalée. Un envoi refusé annule donc tout — la tentative
ne consomme pas de quota et ne laisse pas une ligne qui prétendrait une livraison. Persister après
l'envoi aurait la faute symétrique : un message parti mais non compté, invisible au quota comme à un
signalement d'abus.

**Pourquoi on ne stocke pas le corps du message.** `ad_contacts` retient qui, quelle annonce, quand
— c'est exactement ce dont le quota et un signalement d'abus ont besoin. Le corps ne vit que dans
l'e-mail qui l'a transporté. Le stocker ferait de la table une messagerie : à exposer, à exporter, à
purger, à modérer, alors que rien dans le produit ne lit ces messages.

**Pourquoi l'échec est explicite et non un 204 optimiste.** Répondre 204 puis laisser tomber le
message est pire que d'échouer : l'expéditeur repart en attendant une réponse qui n'arrivera jamais,
et personne ne l'apprend. `AD_CONTACT_DELIVERY_FAILED` en **500** dit la vérité — le message n'est
pas parti, il peut réessayer.

### 2.6 Plafond de pagination posé dans `BaseRepository.getPage`

Quatorze endpoints exposent `?size=` et aucun ne le bornait : `size=100000` était une requête
supportée. Cela pesait peu tant qu'une ligne de liste était quelques colonnes ; cela pèse maintenant
qu'une ligne porte un extrait généré, une résolution de vignette et un état « suis-je inscrit »
par ligne.

Le plafond est posé **au point unique où la pagination est appliquée**, pas dans chaque resource :
cela couvre les endpoints d'aujourd'hui et ceux que personne ne pensera à borner demain.

### 2.7 `commentCount` **absent** — et non zéro — quand l'appelant n'a pas le droit de lire

Les quatre resources de commentaires sont `@RolesAllowed("user")` au niveau classe et
`CommentAccessChecker` n'accorde `LIST` qu'à un utilisateur ayant un rôle dans l'équipe
propriétaire. Un visiteur qui ne peut pas ouvrir le fil ne doit pas apprendre à quel point il est
actif — le compteur est une fuite d'information, faible mais réelle.

Trois comportements possibles, un seul honnête :

- **compter quand même** : fuite ;
- **renvoyer 0** : mensonge, et indistinguable de « aucun commentaire » ;
- **omettre le champ** : Jackson est configuré `NON_NULL`, donc le champ **disparaît du JSON**. Le
  client sait qu'il ne sait pas et n'affiche pas de compteur.

C'est la troisième. Corollaire dans `CommentCountLookup` : une entité **lisible** sans commentaire
vaut bien `0` ; seule une entité non lisible est absente.

### 2.8 Deux requêtes par page, jamais une par ligne

Règle appliquée par les trois « lookups » créés dans ce lot — `ParticipationLookup`,
`CommentCountLookup`, `ThumbnailLookup` — et c'est l'invariant à protéger en revue. Chacun résout sa
donnée pour **toute la page** en un nombre fixe de requêtes. Un appelant anonyme ne coûte aucune
requête du tout.

C'est ce qui rend les items 1, 2, 5 et 6 compatibles entre eux : sans ces lookups, une page de fil
de 20 lignes aurait gagné 60 allers-retours.

### 2.9 `status` filtrable, mais uniquement en rétrécissement

`status` a été exposé sur les listes de publications, contre l'avis initial de la reconnaissance
(qui recommandait de s'en tenir à `from`/`to`). La clause est **ANDée** avec les règles de
visibilité, jamais substituée : un membre ne peut pas demander `status=DRAFT` pour voir les
brouillons des autres. Ce qu'il gagne, c'est de masquer ce qu'il a déjà le droit de voir.

### 2.10 Le meneur de groupe est une colonne dédiée, nullable, et jamais dérivée de `createdBy`

**Pourquoi une colonne plutôt qu'une lecture de `createdBy`.** `createdBy` vaut le créateur de la
**sortie**, donc la même personne sur tous ses groupes. L'afficher sous le libellé « meneur du
groupe » aurait été faux sur presque tous les groupes, et faux de la façon qui ne se signale pas :
un nom plausible, à la bonne place, que personne ne pense à contester. C'est exactement le défaut que
`leader_id` corrige — **aucun repli sur `createdBy` n'est acceptable nulle part**, ni en base, ni
dans un DTO, ni dans un client. Un repli réintroduirait le bug sous un nom plus crédible.

**Pourquoi nullable, et pourquoi pas de `NOT NULL DEFAULT`.** Un `NOT NULL DEFAULT` n'aurait aucune
valeur honnête à poser : la seule candidate est `createdBy`, qui est précisément la mauvaise réponse.
Et ce n'est pas qu'une contrainte de migration — **la plupart des groupes n'auront jamais de meneur
désigné**. `null` signifie « pas désigné », pas « inconnu » ni « pas encore renseigné ».

**Pourquoi `ON DELETE SET NULL`.** Un `CASCADE` ferait disparaître le groupe, son parcours et ses
participants avec le compte du meneur. La disparition d'un compte ne doit rien emporter d'autre que
la désignation : le groupe retombe simplement dans le cas courant, celui sans meneur.

**Pourquoi « membre de l'équipe » et non « participant du groupe ».** Sans contrôle du tout,
n'importe quel identifiant du domaine pouvait être publié comme meneur d'une sortie que la personne
n'a jamais accepté de mener — une attribution à un tiers, écrite par quelqu'un d'autre. Exiger qu'il
soit **participant** aurait été pire : les groupes se construisent avant toute inscription, la
contrainte rendrait la désignation impossible au moment où on la fait. L'appartenance à l'équipe
propriétaire est la borne juste — elle exclut les inconnus sans interdire le cas normal.

**Pourquoi l'appartenance n'est pas revérifiée ensuite.** Un meneur qui quitte l'équipe garde la
ligne. La sortie a eu lieu, et réécrire l'histoire à chaque changement d'adhésion désattribuerait des
sorties passées — un écran de sortie de l'an dernier perdrait son meneur parce que la personne a
changé de club. Le chemin de lecture rend ce qui est là.

**Pourquoi les gabarits de sortie n'ont pas de meneur.** Décision produit : `RideTemplateGroup` a son
propre type de requête (`RideTemplateGroupRequest`) et ne gagne **aucun champ de meneur** ;
instancier une sortie depuis un gabarit ne désigne personne. Un gabarit décrit une forme de sortie
récurrente, pas qui la mènera cette fois-ci ; une désignation figée dans le gabarit se répéterait sur
chaque instance et vieillirait mal, exactement comme `createdBy`.

**Conséquence d'affichage, imposée et non négociable** : **pas de pastille « Organisateur » quand
`leader` est absent**. Tout écran qui montre la pastille (`12 Sortie`, `34 Participants`) doit la
traiter comme **conditionnelle** : présente si `leader` est présent, absente sinon, et surtout pas
remplacée par un libellé de repli. La nuance compte pour les maquettes : depuis 1.5.0 ce n'est plus
« la donnée manque » mais « la plupart des groupes n'ont pas de meneur ». Le cas « pas de meneur »
est le **cas courant**, à dessiner comme tel, pas comme une dégradation.

---

## 3. Ce que le propriétaire du produit a tranché

Les trois points laissés ouverts par le lot 1.3.0 sont arbitrés, et les trois sont clos : la
résolution du floutage est confirmée, le canal de contact est livré en 1.4.0, le meneur de groupe
en 1.5.0.

### 3.1 Position des annonces : arrondi à ~1 km, **confirmé**

**Décision actée.** La résolution reste la cellule d'environ 1,11 km : ce qui est publié est son
centre, et le filtre de proximité est quantifié sur la même grille (§2.4). Une résolution plus fine
(500 m) rendrait l'oracle de proximité plus utile à qui veut retrouver un domicile ; une plus
grossière (5 km) viderait le filtre « autour de moi » de son sens en zone urbaine. Le champ
conditionnel — position exacte réservée à l'auteur et aux admins — n'est pas retenu : plus complexe
et moins lisible dans le contrat, pour un besoin que personne n'a exprimé.

**Conséquence de conception, à appliquer partout où la carte d'une annonce apparaît** : on affiche
un **secteur** — cercle, pastille floutée, zone — et **jamais une punaise**. Poser une punaise sur
un centre de cellule prétend une précision que la donnée n'a pas. Cela vaut pour `32 Annonces`
(liste et détail), pour toute future carte d'équipe montrant des annonces (§4.4), et pour les deux
clients.

Corollaire déjà visible : un rayon de recherche demandé est servi arrondi au multiple de cellule
supérieur (3 km → 3,33 km). L'interface doit annoncer un ordre de grandeur, pas une valeur exacte.

### 3.2 Canal de contact d'une annonce : **résolu, et livré**

Le relais e-mail est en 1.4.0. Voir **§1.13** pour le contrat et les fichiers, **§2.5** pour les
raisons. Le bouton « Contacter le vendeur » de `32 Annonces` a désormais une cible et la maquette
n'a plus à dégrader.

Trace de ce qui a été écarté : le **champ `contact` libre sur `Ad`** — e-mail ou téléphone saisi par
le vendeur et rendu sur l'annonce — était la solution la moins chère et n'a pas été retenue, parce
qu'elle publie une donnée personnelle irrévocablement à toute l'équipe. Le raisonnement complet est
en §2.5. Le lien vers un profil public ne résolvait rien (il n'y a ni écran de profil public, ni
moyen de joindre depuis là), et la messagerie interne reste hors de proportion avec le besoin.

### 3.3 Meneur de groupe : **résolu, et livré**

La colonne `ride_groups.leader_id`, `RideGroupDto.leader` et `GroupRequest.leaderId` sont en 1.5.0.
Voir **§1.14** pour le contrat, la migration et les fichiers, **§2.10** pour les raisons — dont les
deux qui contraignent les maquettes : **jamais de repli sur `createdBy`**, et la pastille
« Organisateur » reste **conditionnelle**, parce que la plupart des groupes n'auront pas de meneur
désigné.

Décision produit complémentaire, actée en même temps : **les gabarits de sortie n'ont pas de
meneur**. `RideTemplateGroupRequest` ne gagne aucun champ, et instancier une sortie depuis un
gabarit ne désigne personne (§2.10).

Reste à câbler côté clients : le sélecteur de meneur dans l'éditeur de groupes
(`frontend/src/components/ride/RideEditor.tsx`) puis son équivalent mobile, et la pastille sur
`12 Sortie` et `34 Participants`.

---

## 4. Les quatre chantiers d'infrastructure non livrés

Aucun n'a été commencé. Chacun est décrit ici assez précisément pour être chiffré et attaqué
séparément. **Un plan mobile qui en dépend doit le dire explicitement.**

---

### 4.1 Chantier A — Notifications push

**Intérêt.** C'est le seul mécanisme qui ramène un membre dans l'app sans qu'il l'ouvre. Les trois
déclencheurs identifiés par le brief (rappel J-1, annulation de sortie, réponse à un commentaire)
sont exactement les moments où l'information a une valeur périssable. Sans push, le bloc
« Ma prochaine sortie » de `11 Accueil` ne sert que ceux qui ouvrent l'app.

**Périmètre — contrat.**

| Endpoint | Rôle |
|---|---|
| `POST /api/users/me/devices` | Enregistre un jeton FCM/APNs : `{ token, platform, appVersion, locale }`. Idempotent sur `token`. |
| `DELETE /api/users/me/devices/{deviceId}` | Désenregistrement explicite (déconnexion). |
| `GET /api/users/me/notifications?page&size&unreadOnly` | Le centre de notifications in-app, paginé comme les autres listes. |
| `POST /api/users/me/notifications/read` | Marque lu : `{ ids[] }` ou `{ before }`. |
| `GET /api/users/me/notification-preferences` | Les interrupteurs par catégorie de `33 Profil`. |
| `PUT /api/users/me/notification-preferences` | Idem, en écriture. |

**Périmètre — modèle.** Deux ou trois entités et une migration :

- `UserDevice` (`user_id`, `token` unique, `platform`, `app_version`, `locale`, `last_seen_at`) ;
- `Notification` (`user_id`, `type`, `team_entity_id` nullable, `payload` jsonb, `created_at`,
  `read_at` nullable) ;
- `NotificationPreference` — soit une table (`user_id`, `category`, `enabled`), soit une colonne
  jsonb sur `users`. La table est préférable : les catégories vont bouger.

Toutes les entités doivent porter `domain_id` ou être jointes à `users`, qui le porte : le même
e-mail existe sur deux domaines et ne doit pas recevoir les notifications de l'autre.

**Périmètre — déclenchement.**

- *Rappel J-1* : tâche planifiée (Quarkus `@Scheduled`), fenêtre glissante, idempotence obligatoire
  (une marque « rappel envoyé » sur la participation, sinon un redémarrage renotifie tout le monde).
- *Annulation ou modification de sortie* : sur transition de `Ride.status` vers `CANCELLED`, et sur
  changement de date/heure. Destinataires = les participants, à résoudre via
  `RideParticipationRepository`.
- *Réponse à un commentaire* : sur création d'un `Comment` avec `parent != null`, destinataire =
  l'auteur du parent, s'il n'est pas l'auteur de la réponse.

**Dépendances.** Aucune sur les autres chantiers. Nécessite un compte Firebase (FCM) et une clé
APNs, donc une décision d'infrastructure et des secrets déployés.

**Impact déclaratif côté stores.** Non négligeable, et c'est ce qui allonge le délai réel :

- **Apple** : entitlement `aps-environment`, clé APNs (`.p8`) à générer et à déposer, mise à jour de
  la *App Privacy* (identifiant d'appareil collecté), et une justification si les notifications
  servent à autre chose que le fonctionnel.
- **Google** : `google-services.json`, et depuis Android 13 la permission runtime
  `POST_NOTIFICATIONS` — donc un écran de demande à maquetter (il n'est dans aucune maquette
  aujourd'hui) et une *Data safety form* à mettre à jour.
- Les deux stores demandent une nouvelle soumission ; ce n'est pas un déploiement serveur.

**Risques.** L'idempotence du rappel J-1 est le point qui casse en production et pas en test. La
gestion des jetons périmés (retour `UNREGISTERED` de FCM) doit purger `UserDevice`, sinon la table
grossit indéfiniment et le taux d'échec d'envoi devient illisible.

**Ce que ça débloque en maquette.** `11 Accueil` (cloche + pastille de non-lus dans la barre
supérieure), `33 Profil` §4 (interrupteurs par catégorie), et un écran « Notifications » qui n'est
pas encore maquetté.

**Taille : L.**

---

### 4.2 Chantier B — Pagination par curseur

**Intérêt.** L'offset actuel (`page`/`size`) **duplique et saute des éléments** pendant un scroll
infini dès que la liste bouge sous le curseur. Sur `n-peloton` — 1 999 membres, 2 585 parcours,
~665 sorties — ce n'est pas un cas limite : une publication créée pendant le scroll décale toutes
les pages suivantes d'un rang, et l'utilisateur voit deux fois la même carte.

**Périmètre.** Ajouter `cursor` en entrée et `nextCursor` en sortie, **en conservant `total`, `page`
et `size`**. Listings visés, par ordre de gravité :

| Listing | Clé de tri stable proposée |
|---|---|
| `GET /api/publications`, `GET /api/teams/{t}/publications` | `(dateTime desc, id desc)` |
| `GET /api/routes`, `GET /api/teams/{t}/routes` | `(<champ de tri courant>, id desc)` — le tri est déjà variable (`sortBy`/`sortDir`), le curseur doit donc encoder le critère |
| `GET /api/teams/{t}/members` | `(joinedAt desc, id desc)` |
| `GET /api/teams/{t}/classifieds` | `(<AdSortBy>, id desc)` |

La clé doit **toujours** se terminer par `id` : c'est ce qui la rend totale. Un tri sur `dateTime`
seul n'est pas stable — deux sorties peuvent partager la même date à la seconde.

**Cohabitation avec l'offset, sans rupture.** Les trois règles à tenir :

1. `cursor` et `page` sont **mutuellement exclusifs**. `cursor` fourni ⇒ `page` ignoré. Fournir les
   deux est une erreur 400, pas un comportement silencieux.
2. `nextCursor` est renvoyé **toujours**, y compris en mode offset : un client peut démarrer en
   offset (première page) et continuer en curseur. C'est le chemin de migration du mobile.
3. `total` reste calculé et renvoyé. Le brief est explicite là-dessus, et les compteurs de tête de
   liste (« 1 248 publications ») en dépendent. Le curseur ne dispense pas du `COUNT(*)`.

Le curseur est un opaque base64url encodant `(critère de tri, valeurs de la clé)`, signé ou non mais
**versionné** : un curseur émis avant un changement de tri doit être rejeté proprement, pas produire
une page arbitraire.

**Dépendances.** Techniquement autonome. En pratique il touche `BaseRepository.getPage`,
`PedalonsQuery` et tous les `…ListResponse` — donc à faire **avant** que le mobile ne câble le
scroll infini partout, pas après.

**Risques.** Le tri variable des parcours (`sortBy` × `sortDir` × filtres) est là où la
« keyset pagination » devient délicate : chaque critère de tri demande sa comparaison composite
(`(a, id) < (:a, :id)`), et un critère nullable (`price`) demande un `NULLS LAST` cohérent des deux
côtés de la comparaison. Prévoir un test par critère de tri.

**Ce que ça débloque en maquette.** Le « scroll infini par curseur » explicitement demandé par
`11 Accueil` §5, `21 Parcours` et `34 Participants`. Sans lui, ces écrans fonctionnent — avec des
doublons visibles.

**Taille : M** (par famille de listing), **L** au total.

---

### 4.3 Chantier C — Cache, fraîcheur et images

Quatre briques distinctes, regroupées parce qu'elles servent le même objectif — que le mobile
n'ait pas à tout retélécharger — et qu'elles se contraignent mutuellement.

#### C.1 `ETag` / `If-None-Match`

**Intérêt.** Un `304 Not Modified` sur un détail de sortie ou de parcours économise la totalité du
corps, qui est précisément ce que les items 1 et 3 ont cherché à réduire.

**Le verrou à traiter.** Les champs « moi » livrés en §1.2 (`registered`, `registeredGroupId`,
`full`, `commentCount`) rendent les réponses **dépendantes de l'appelant**. Un `ETag` calculé sur le
contenu métier seul serait faux ; un cache partagé (CDN, proxy) servirait à Alice la réponse de Bob.
Deux sorties possibles :

- l'`ETag` intègre l'identité de l'appelant (`userId`) dans son calcul, et la réponse porte
  `Cache-Control: private` — simple, correct, mais aucun cache partagé n'aide ;
- on sépare la partie publique de la partie « moi » en deux réponses — plus cachable, mais c'est un
  changement de contrat majeur et deux requêtes au lieu d'une.

La première est recommandée : le gain visé est le cache **du client**, pas celui d'un CDN.
`GET /api/config` est le seul endpoint réellement partageable et il est déjà trivialement cachable.

**Périmètre.** Filtre JAX-RS calculant un `ETag` faible sur le corps sérialisé, ou fort sur
`(updatedAt, userId, version de contrat)` pour les détails d'entité. Commencer par les détails
(`getRide`, `getRoute`, `getTrip`, `getPost`, `getAd`), pas les listes.

#### C.2 `updatedAt` systématique et `?updatedSince=`

**Intérêt.** C'est ce qui rend une synchronisation incrémentale possible : au réveil, l'app demande
ce qui a changé depuis sa dernière synchro au lieu de recharger la première page de tout.

**Périmètre.** `updatedAt` existe sur `BaseEntity` et est exposé sur les DTO de détail ; il faut
l'exposer **sur tous les DTO de liste** puis ajouter `?updatedSince=<instant>` sur les listings
principaux. Attention : `updatedSince` ne dit rien des **suppressions**. Une synchro correcte demande
soit des tombstones (`deleted_at` interrogeable), soit une resynchronisation périodique complète. À
décider avant de coder, pas après.

#### C.3 URLs d'images signées (`?sig=&exp=`)

**Intérêt.** Les URL d'assets sont aujourd'hui des URL de service. Les signer permet de les rendre
directement servables (CDN, cache HTTP long) sans que l'autorisation dépende d'une session.

**Périmètre.** Signature HMAC sur `(chemin, expiration)`, secret côté serveur, `exp` court (heures)
mais plus long que la durée de vie d'un écran. Point d'attention : une URL signée qui expire pendant
qu'une image est en cache donne une image cassée sans erreur lisible — prévoir une renégociation
côté client. `AssetService.getImageUrl` et `ThumbnailLookup` sont les deux points de production
d'URL à couvrir.

#### C.4 `blurHash`

**Intérêt.** Le placeholder d'une vignette 16:9 ou d'un bandeau 160 px pendant le chargement.
Une trentaine d'octets par image, calculés **une fois à l'upload** et stockés sur `Asset`.

**Périmètre.** Colonne `blur_hash` sur `assets` (migration), calcul dans le pipeline d'upload,
champ sur `MediaDto`/`AssetsDto` et sur `thumbnailUrl` — probablement un objet
`{ url, blurHash, width, height }` plutôt qu'une chaîne, ce qui est un **changement de type** sur
`thumbnailUrl` livré en 1.3.0, donc un MAJOR. Alternative sans rupture : un champ frère
`thumbnailBlurHash`. À trancher au moment de faire, pas maintenant.

**Dépendances.** C.1 dépend de la décision « champs moi » (§1.2, déjà prise et livrée). C.4 est
indépendant. C.2 et C.3 sont indépendants l'un de l'autre.

**Ce que ça débloque en maquette.** Le chargement progressif de `13 Parcours` (métadonnées
immédiates), les squelettes `ShimmerCard` de `11 Accueil` remplacés par de vrais placeholders
colorés, et le hors-ligne, qui n'est dans aucune maquette et qui devrait l'être avant d'être promis.

**Taille : C.1 M, C.2 M, C.3 M, C.4 S.**

---

### 4.4 Chantier D — Carte multi-entités

**Intérêt.** `21 Parcours` prévoit une vue carte, et `12 Sortie` / `24 Voyage` des cartes
multi-tracés. Aujourd'hui, afficher des entités sur une carte impose une requête par famille
d'objets et un DTO complet par objet — pour dessiner un point.

**Périmètre — contrat.**

- `GET /api/map/features?bbox=<minLon,minLat,maxLon,maxLat>&types=RIDE,ROUTE,PLACE,AD&minRole=&teamSlug=`
  → une `FeatureCollection` GeoJSON **légère** : par feature, la géométrie et le strict minimum
  (`type`, `id`, `slug`, `teamSlug`, `name`, et selon le type `dateTime` ou `distance`). Pas de
  markdown, pas d'assets, pas de tracé complet — un parcours y est représenté par son point de
  départ, pas par sa `LineString` (les tracés passent par les tuiles MVT existantes).
- `GET /api/teams/{teamSlug}/places/bounds` → l'emprise des lieux d'une équipe, pour cadrer une
  carte au montage. Symétrique de `…/routes/bounds`, qui existe déjà.

Contrainte à porter dès la conception : les features de `type=AD` sortent **floutées** comme
`AdDto.locationGeometry` (§2.4, §3.1), et le client doit les rendre en **secteur, jamais en
punaise** — c'est la même règle que sur `32 Annonces`, et une carte qui mélange plusieurs types est
précisément l'endroit où l'on serait tenté de dessiner tout le monde avec le même marqueur.

**Périmètre — implémentation.** Le point dur est de **ne pas contourner les règles de visibilité**.
Chaque famille a déjà son `…Query` et son `andSpecific` ; l'endpoint doit être une union de requêtes
projetées (`TeamEntityRepository.QueryShape`, `ordered = false`), une par type demandé, jamais du
HQL brut sur `team_entities`. Il faut aussi un plafond dur de features renvoyées et un refus explicite
des bbox trop grandes, sinon `types=ROUTE` sur le monde entier renvoie 2 585 features.

**Dépendances.** Aucune sur A, B, C. Réutilise `MapConfig`/`ConfigDto` livrés en §1.7 pour le fond
de carte.

**Risques.** `st_intersects` sur une bbox est indexable (index GIST sur les colonnes `geometry`,
à vérifier avec `EXPLAIN` avant de promouvoir l'endpoint). En revanche l'union de quatre requêtes
sous une seule réponse rend le cache et la pagination délicats : cet endpoint doit rester **non
paginé et borné**, pas paginé.

**Ce que ça débloque en maquette.** `21 Parcours` vue carte avec « Rechercher dans cette zone », et
une carte d'équipe (lieux + annonces) qui n'est pas encore maquettée. Note : la vue carte des
parcours **fonctionne déjà** avec les tuiles MVT `routes/tiles/{z}/{x}/{y}.mvt` et `routes/bounds`,
générées côté client mobile mais jamais appelées — ce chantier est ce qui permet d'y **ajouter** les
autres entités, il n'est pas un prérequis de la vue carte des parcours.

**Taille : M.**

---

## 5. Mode d'emploi de vérification

À exécuter par l'utilisateur. **Claude n'exécute jamais les tests backend** (interdiction du projet).

### 5.1 Base de données

Les migrations `V28__user_preferences.sql`, `V29__ad_contact.sql` et `V30__ride_group_leader.sql`
sont appliquées **par Flyway au
démarrage** du backend (`quarkus.flyway.migrate-at-start`, mode `validate` en production). Aucune
commande manuelle. Les tests utilisent `drop-and-create` et ne passent pas par Flyway — un test vert
ne prouve donc **pas** que les migrations s'appliquent sur une base existante. Vérifier au moins un
démarrage réel :

```bash
cd /Users/glandais/code/perso/tribly
docker compose up -d
cd backend && mvn quarkus:dev
# attendre "Migrating schema ... to version 30"
```

Contrôler ensuite que V29 a bien posé ses deux objets :

```bash
docker exec -i pedalons-dev-postgres psql -U pedalons -d pedalons -c '\d ad_contacts'
docker exec -i pedalons-dev-postgres psql -U pedalons -d pedalons \
  -c "select column_name, is_nullable from information_schema.columns
      where table_name='users' and column_name='contactable_by_members';"
# → contactable_by_members | YES   (nullable : null vaut « joignable »)
```

Et que V30 a bien posé les siens — colonne nullable, FK en `SET NULL`, index partiel :

```bash
docker exec -i pedalons-dev-postgres psql -U pedalons -d pedalons \
  -c "select column_name, is_nullable from information_schema.columns
      where table_name='ride_groups' and column_name='leader_id';"
# → leader_id | YES   (nullable : null vaut « pas de meneur désigné », le cas courant)

docker exec -i pedalons-dev-postgres psql -U pedalons -d pedalons -c '\d ride_groups'
# → FOREIGN KEY (leader_id) REFERENCES users(id) ON DELETE SET NULL
#   (surtout pas CASCADE : un compte supprimé n'emporte pas le groupe, son parcours ni ses participants)
# → "idx_ride_groups_leader" btree (leader_id) WHERE leader_id IS NOT NULL
```

### 5.2 Tests backend, module par module

Les 24 classes de test ajoutées (22 par le lot 1.3.0, plus `AdContactResourceTest` en 1.4.0 et
`RideGroupLeaderTest` en 1.5.0),
groupées par item. Les lancer par groupe donne un diagnostic plus lisible qu'un `mvn test` complet.

```bash
cd /Users/glandais/code/perso/tribly/backend

# Item 1 — vues compactes et extraits
mvn test -Dtest='PublicationCompactViewTest,RouteCompactViewTest,AdCompactViewTest,MarkdownExcerptTest'

# Item 2 — champs « moi » et participations
mvn test -Dtest='RideMeFieldsTest,UserParticipationsResourceTest,ParticipationQueryCountTest,PublicationParticipatingFilterTest'

# Item 3 — géométrie négociable et profil altimétrique
mvn test -Dtest='RouteGeometryNegotiationTest,RouteElevationProfileTest,TrackGeometryTest'

# Item 5 — calendrier
mvn test -Dtest='CalendarEventFieldsTest,CalendarQueryCountTest,IcsGenerationServiceTest'

# Item 6 — commentaires
mvn test -Dtest='CommentCountTest,CommentPaginationTest,CommentRepositoryTest'

# Items 12, 13, 15 et divers
mvn test -Dtest='ConfigMapSettingsTest,AdDetailsAndFiltersTest,PublicationCountResourceTest,RouteCountResourceTest,TeamStatsResourceTest,TripTotalsTest,UserPreferencesResourceTest,TeamRepositoryTest'

# Le floutage de position (paquet fr.pedalons.common)
mvn test -Dtest='fr.pedalons.common.*Test'

# 1.4.0 — le relais de contact d'annonce
mvn test -Dtest='AdContactResourceTest'

# 1.5.0 — le meneur de groupe
mvn test -Dtest='RideGroupLeaderTest'

# Et pour finir, la totalité
mvn test
```

`AdContactResourceTest` couvre les six comportements qui font la fonctionnalité : le 204 avec
`Reply-To` sur l'expéditeur, le fait qu'**aucune des deux adresses n'apparaît dans la réponse ni
dans `AdDto`**, le 400 `AD_CONTACT_SELF`, le 400 `AD_CONTACT_OPTED_OUT` (avec vérification qu'aucun
e-mail n'est parti), le 401 anonyme et le 403/404 pour un non-membre, et le 429
`AD_CONTACT_RATE_LIMITED` **atteint en écrivant à des annonces différentes** — c'est ce test-là qui
garde l'invariant « quota par expéditeur » du §2.5.

`RideGroupLeaderTest` couvre les six comportements du meneur : le meneur exposé sur le groupe après
création, le groupe **sans** meneur qui laisse `leader` nul au lieu de retomber sur le créateur, le
400 `RIDE_GROUP_LEADER_NOT_MEMBER` pour un non-membre puis pour un membre d'une autre équipe, la
désignation puis l'**effacement** par `"leaderId": null` sur une mise à jour, et
`groupLeader_isNotTheRideCreator` — c'est ce dernier qui garde l'invariant du §2.10 : il échoue si
quelqu'un réintroduit un repli sur `createdBy`. Ne pas le désactiver pour faire passer un build.

Les tests `…QueryCountTest` (`CalendarQueryCountTest`, `ParticipationQueryCountTest`) sont ceux qui
gardent l'invariant du §2.8 : ils échouent si quelqu'un réintroduit une requête par ligne. Ne pas
les désactiver pour faire passer un build.

### 5.3 Régénération du contrat et des clients

Le contrat et les deux clients sont **déjà régénérés et commités dans le lot**. La commande ci-dessous
sert à vérifier qu'ils sont bien à jour, pas à les produire :

```bash
cd /Users/glandais/code/perso/tribly
bash regenerate.sh          # backend package -DskipTests, puis frontend pnpm check, puis mobile check.sh
git diff --stat contracts/ frontend/src/api/ mobile/lib/api/generated/
```

Le second diff doit être **vide**. S'il ne l'est pas, le contrat commité ne correspond pas au code :
c'est le seul cas où il faut recommiter les fichiers générés.

Vérifier aussi que la version est bien celle attendue :

```bash
grep 'pedalons.api.version' backend/src/main/resources/application.properties   # 1.5.0
grep -A2 '^info:' contracts/openapi.yaml                                        # version: 1.5.0
```

Et l'invariant du relais, qui est ce que la fonctionnalité existe pour tenir : **`AdDto` ne porte
aucun champ de contact**. Le jour où l'un des deux greps ci-dessous ressort quelque chose, le relais
a été contourné.

```bash
# l'endpoint est bien dans le contrat
grep -n 'classifieds/{slug}/contact' contracts/openapi.yaml

# et AdDto ne gagne ni email, ni téléphone, ni champ contact
python3 - <<'EOF'
import re, yaml
spec = yaml.safe_load(open('contracts/openapi.yaml'))
props = spec['components']['schemas']['AdDto']['properties']
leaks = [p for p in props if re.search(r'contact|email|phone|mail|tel', p, re.I)]
print('AdDto:', 'OK — aucun champ de contact' if not leaks else f'FUITE : {leaks}')
EOF
```

Et les invariants du meneur : `leader` présent mais **non requis** en lecture, `leaderId` présent en
écriture, et **rien** sur les gabarits.

```bash
python3 - <<'EOF'
import yaml
s = yaml.safe_load(open('contracts/openapi.yaml'))
sc = s['components']['schemas']

g = sc['RideGroupDto']
print('RideGroupDto.leader :', 'OK' if 'leader' in g['properties'] else 'MANQUANT')
print('  non requis        :', 'OK' if 'leader' not in g.get('required', []) else
      'ERREUR — un leader requis force un objet vide quand personne ne mène')
print('  type              :', g['properties']['leader'].get('$ref'))  # PublicUserDto

print('GroupRequest.leaderId       :', 'OK' if 'leaderId' in sc['GroupRequest']['properties'] else 'MANQUANT')
print('  optionnel                 :', 'OK' if 'leaderId' not in sc['GroupRequest'].get('required', []) else 'ERREUR')

tpl = sc['RideTemplateGroupRequest']['properties']
print('RideTemplateGroupRequest    :', 'OK — aucun meneur'
      if not [p for p in tpl if 'leader' in p.lower()]
      else 'ERREUR — un champ de meneur est apparu sur les gabarits, qui ne doivent pas en porter')
EOF
```

### 5.4 Web

Le web ne consomme aucun champ nouveau ; les seules modifications sont des adaptations de signature
(le paramètre `params` inséré par Orval sur `useGetRoute`, qui a gagné `simplify`/`points`) sur
7 fichiers : `RideGroupCard.tsx`, `useBreadcrumbData.ts`, `EditRoutePage.tsx`, `RouteDetailPage.tsx`,
`RouteFullscreenMapPage.tsx`, `StageDetailPage.tsx`, `StageFullscreenMapPage.tsx`.

```bash
cd /Users/glandais/code/perso/tribly/frontend
pnpm check        # tsgo + lint + build
pnpm dev          # puis vérifier à la main : détail de parcours, carte plein écran, détail d'étape,
                  # carte de groupe d'une sortie (les 4 écrans qui appellent useGetRoute)
```

Le point à vérifier à l'œil est que le tracé complet s'affiche toujours : `useGetRoute(..., undefined, ...)`
signifie « pas d'options de géométrie », donc le comportement d'avant.

Le relais de 1.4.0 a ajouté `contactAdAuthor` au client Orval (`frontend/src/api/endpoints/ads/ads.ts`)
et `contactableByMembers` à `userDto`/`userPreferencesRequest` ; **aucun écran web ne les appelle
encore**. Le bouton de contact et l'interrupteur de préférence restent à câbler.

Le meneur de 1.5.0 a ajouté `leader` à `frontend/src/api/dto/rideGroupDto.ts` et `leaderId` à
`frontend/src/api/dto/groupRequest.ts`, tous deux optionnels ; **aucun écran web ne les lit ni ne
les écrit encore**. Restent à câbler le sélecteur de meneur dans `RideEditor.tsx` et la pastille sur
la carte de groupe — cette dernière **rendue seulement si `leader` est présent**, sans repli sur
`createdBy` (§2.10).

### 5.5 Mobile

Les clients générés sont à jour — y compris `contactAdAuthor` dans
`mobile/lib/api/generated/clients/ads_client.dart`, et `leader` / `leaderId` dans
`mobile/lib/api/generated/models/ride_group_dto.dart` et `group_request.dart` — mais **aucun écran ne
consomme encore les nouveaux champs** ; c'est le chantier suivant.

```bash
cd /Users/glandais/code/perso/tribly/mobile
bash check.sh     # analyse + build_runner + tests
```

### 5.6 Vérifications manuelles qui ne sont pas couvertes par les tests

```bash
# le floutage : la position renvoyée doit être un centre de cellule, identique d'un appel à l'autre
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/teams/gaby/classifieds?size=5" | jq '.ads[].locationGeometry'

# le plafond de pagination : demander 100000 doit renvoyer 200 éléments, pas une erreur
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/publications?size=100000" | jq '.publications | length, .total'

# la vue compacte : markdown vide, excerpt présent
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/publications?view=compact&size=1" \
  | jq '.publications[0] | {markdown: .media.markdown, excerpt, thumbnailUrl}'

# commentCount absent pour un anonyme (et non zéro)
curl -s "http://localhost:8080/api/publications?size=1" | jq '.publications[0] | has("commentCount")'
# → false

# le meneur : sur un groupe sans meneur désigné, la clé « leader » doit être ABSENTE du JSON —
# pas un objet vide, pas un objet portant le créateur de la sortie
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/teams/gaby/rides/<slug>" | jq '[.groups[] | has("leader")]'
# → false sur les groupes sans meneur (le cas courant), true seulement sur ceux qui en ont un

# et quand il y en a un, c'est un PublicUserDto ({id, displayName, avatarUrl}), pas une chaîne
curl -s -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/teams/gaby/rides/<slug>" \
  | jq '[.groups[] | {name, leader}]'
# sur une sortie à plusieurs groupes ayant chacun un meneur distinct, les valeurs doivent différer —
# c'est ce qu'un repli sur le créateur de la sortie rendrait identique partout

# désigner un non-membre doit échouer, pas passer silencieusement
curl -s -X PUT -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"...":"...","groups":[{"name":"G1","leaderId":"<id hors équipe>"}]}' \
  "http://localhost:8080/api/teams/gaby/rides/<slug>" | jq '.code'
# → "RIDE_GROUP_LEADER_NOT_MEMBER" en 400

# effacer une désignation : envoyer null, ce qui est une opération réelle et non une omission
# (rejouer le GET ci-dessus : la clé « leader » doit avoir disparu)

# le relais : 204 sans corps, et l'e-mail visible dans Mailhog (http://localhost:8025)
curl -s -o /dev/null -w '%{http_code}\n' -X POST \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"message":"Bonjour, le vélo est-il toujours disponible ?"}' \
  "http://localhost:8080/api/teams/gaby/classifieds/<slug>/contact"
# → 204 ; dans Mailhog, vérifier que Reply-To porte l'adresse de l'expéditeur
#   et que le corps ne contient l'adresse d'aucun des deux

# se rendre injoignable, puis réessayer : 400 AD_CONTACT_OPTED_OUT
curl -s -X PATCH -H "Authorization: Bearer $AUTHOR_TOKEN" -H 'Content-Type: application/json' \
  -d '{"contactableByMembers":false}' "http://localhost:8080/api/users/me/preferences" \
  | jq '.contactableByMembers'
```

**Avant toute utilisation en production** : les templates Brevo `ad-contact.fr` et `ad-contact.en`
(identifiants 10 et 11, déclarés en profil `%prod`) doivent être **créés dans le compte Brevo**, avec
les paramètres que le relais passe — `appName`, `recipientName`, `senderName`, `adName`, `adUrl`,
`message` — et sans jamais imprimer l'adresse de l'expéditeur, qui ne voyage qu'en `Reply-To`. Tant
qu'un identifiant manque, l'endpoint répond 500 en nommant le template absent. Tâche d'exploitation,
pas de code (§1.13).
