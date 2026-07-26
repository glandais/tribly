# Portage web des idées de la v2 mobile — analyse et plan

Le brief de design `docs/audit-ux/BRIEF.md` a été écrit pour combler l'écart de l'application
Flutter vis-à-vis du site. Sur presque tous les axes, le site est le modèle et non l'élève : la
carte multi-tracés, le profil altimétrique colorisé par pente, « Cols et montées », les
commentaires, la recherche debouncée et le panneau de filtres existent déjà côté React. Ce
document ne décrit donc **pas** une mise à niveau fonctionnelle du web. Il répond à une question
plus étroite et plus utile : *parmi les idées de conception produites pour la v2 mobile, lesquelles
corrigent une faiblesse réelle du site, et à quel coût ?*

Trois convergences justifient ce lot de travail. D'abord, la v2 mobile a été conçue autour de la
participation (« Ma prochaine sortie », badge `Inscrit`, anneau au calendrier) — et l'inventaire du
frontend montre que le site ne sait **rien** de la participation de l'utilisateur avant qu'il
n'ouvre une sortie. Ensuite, les maquettes mobiles imposent une densité et une hiérarchie d'états
(vide absolu / vide filtré / erreur récupérable) que le web n'applique que par endroits. Enfin,
l'API v2 (contrat `1.3.0`, puis `1.4.0` qui ajoute le relais de contact d'annonce et la préférence
`contactableByMembers`, puis `1.5.0` qui ajoute le meneur de groupe — tous livrés et régénérés dans
`frontend/src/api/`) apporte exactement
les champs qui manquaient aux deux plateformes : `registered`, `registeredGroupId`, `full`,
`GET /api/users/me/participations`, `?view=COMPACT`, `…/count`, `…/elevation-profile`, la
pagination des commentaires et un `CalendarEventDto` enrichi.

Le portage est de la **composition Mantine**, pas de la refonte : le site ne compte que trois
fichiers CSS écrits à la main et aucun composant de bas niveau maison. Les règles du projet
(`frontend/CLAUDE.md`) s'appliquent sans exception : Mantine exclusivement, `@tabler/icons-react`
pour les icônes, filtres et pagination dans la query string via `useUrlFilters`, jamais d'édition
de `src/api/`, clés i18n **plates** ajoutées dans `fr` **et** `en`, `ConfirmDialog` pour les
confirmations, invariants SSR (pas de global navigateur au niveau module, isolation par requête).

## Suivi d'avancement

Chaque tâche du §3 porte son état dans son titre, juste après son identifiant :
**☐** à faire · **▶** en cours · **☑** terminée.

Ces marqueurs sont la seule source de vérité sur l'avancement. Ce plan est fait pour être exécuté
sur plusieurs sessions repartant chacune d'un contexte vierge ; sans eux, une session reconstitue
l'état à partir des commits, ce qui échoue dès qu'une tâche est à moitié faite.

**Cocher dans le même commit que le code**, et seulement quand le critère de fin de la tâche est
vérifié — pas quand le code est écrit. Un état mis à jour plus tard est faux pendant l'intervalle
où la session suivante démarre.

## Sommaire

1. [Tableau de tri des idées de la v2 mobile](#1-tableau-de-tri-des-idées-de-la-v2-mobile)
2. [Les candidats retenus, argumentés](#2-les-candidats-retenus-argumentés)
3. [Plan d'implémentation](#3-plan-dimplémentation)
4. [Ce qu'il ne faut pas porter](#4-ce-quil-ne-faut-pas-porter)
5. [Dépendances non livrées et angles morts](#5-dépendances-non-livrées-et-angles-morts)

---

## 1. Tableau de tri des idées de la v2 mobile

Trois verdicts : **déjà web** (l'idée vient du site, rien à faire), **à porter** (le site a une
faiblesse que l'idée corrige), **sans objet** (l'idée résout une contrainte propre au tactile ou au
petit écran). La colonne « vérifié » indique le fichier consulté pour trancher.

### 1.1 `accueil-aujourdhui` (BRIEF §4.1)

| Idée v2 mobile | Verdict | Vérifié dans |
|---|---|---|
| Barre supérieure compacte rétractable au scroll | déjà web | `Layout.tsx` (`useHeadroom`) |
| **Bloc « Ma prochaine sortie »** | **à porter** | aucun appel de participation dans `src/pages`, `src/hooks`, `src/components` |
| **Rangée « À venir » (30 jours, toutes équipes)** | **à porter** (sans le carrousel) | `HomePage.tsx` est purement rétrospectif |
| Recherche debouncée + filtre de portée + filtre de type | déjà web | `useDebouncedSearch.ts`, `MembershipSelect`, `homeFilters.ts` |
| Carte de fil enrichie (bandeau, badges, ligne sociale, stats) | déjà web — c'est la source | `PublicationCard.tsx` (199 l.) |
| **Badge « Inscrit » sur la carte de fil** | **à porter** | `PublicationCard.tsx` ne compare jamais avec `user.id` |
| **Compteur total en tête de liste (« 1 248 publications »)** | **à porter** | `total` est dans la réponse, affiché nulle part |
| **Vide absolu ≠ vide filtré, erreur avec « Réessayer »** | **à porter** | `HomePage.tsx:176-186` = une ligne de texte |
| Scroll infini par curseur, « Vous avez tout vu » | sans objet | pagination assumée, curseur non livré |
| Barre d'outils épinglée, chips à fondu de bord | sans objet | contrainte de barre tactile étroite |

### 1.2 `sortie-detail` (§4.2)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Carte multi-groupes, palette de 10, survol croisé bidirectionnel | déjà web | `RideDetailPage.tsx:96-118`, `RoutesMapView.tsx:33-44` |
| Carte de groupe à 4 lignes (métriques, avatars, GPX/FIT/appareil) | déjà web | `RideGroupCard.tsx` (260 l.) |
| Inscription depuis la carte de groupe, jamais en boîte de dialogue | déjà web | `RideDetailPage.tsx:444-446` |
| **Bascule optimiste + erreur en bandeau persistant avec le motif réel** | **à porter** | `handleJoinGroup` n'a pas d'`onError` ; toast générique dans `axiosInstance.ts:169-176` |
| **Badge `TERMINÉE` et masquage des actions sur sortie passée** | **à porter — défaut fonctionnel** | `RideDetailPage.tsx:147` : `canJoinRide` ne teste pas la date |
| Bandeau d'annulation | déjà web | `RideDetailPage.tsx:316` |
| Lieux de départ et d'arrivée avec pastilles vert/rouge | déjà web | `RideDetailPage.tsx:368-395` |
| Feuille des participants nominatifs | déjà web | `ParticipantListModal.tsx` |
| **Pastille de meneur sur la carte de groupe et sur le participant** | **à porter** | `RideGroupDto.leader` livré en `1.5.0` ; la prop `isOrganizer` de `ParticipantListModal.tsx:10,65` n'est encore alimentée par personne |
| Encart non-membre | déjà web | `RideDetailPage.tsx:465-483` |
| **Squelette structuré (carte + 3 blocs) au lieu d'un spinner** | **à porter** | `RideDetailPage.tsx:127-129` bloque toute la page |
| **Boutons « Partager » et « Ajouter à mon calendrier »** | **à porter** (petit) | aucun bouton de partage sur les pages de détail |
| App bar transparente sur carte avec voile | sans objet | le desktop a un en-tête plein |

### 1.3 `parcours-detail` (§4.3)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Profil altimétrique colorisé par pente, réticule synchronisé | déjà web | `ElevationChart.tsx` (404 l.) |
| « Cols et montées (N) » avec badges de catégorie pleins | déjà web | `RouteDetailView.tsx:171-247` |
| « Utilisée dans » | déjà web | `RouteUsages.tsx` |
| Trois statistiques en unités utilisateur | déjà web | `RouteDetailView.tsx` |
| **Chargement progressif : métadonnées → géométrie simplifiée → complète** | **à porter** | `?simplify=` / `?points=` disponibles, jamais utilisés |
| Feuille à trois crans, barre d'actions collante en pied de feuille | sans objet | gabarit tactile |

### 1.4 `parcours-exploration` (§4.4)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Parcours hors du silo d'équipe (liste + carte, toutes équipes) | déjà web | `AllRoutesPage.tsx`, `AllRoutesMapPage.tsx` |
| Bascule Liste/Carte conservant les filtres | déjà web | `RouteViewToggle.tsx` |
| Deux `RangeSlider` avec bornes affichées, chips revêtement/relief | déjà web (en `RangeInput`) | `RouteFilterPanel.tsx` (265 l.) |
| Vue carte en tuiles vectorielles, cadrage figé au montage | déjà web | `RoutesTileMap.tsx`, `mapConstants.ts` |
| **Compteur « 2 585 parcours »** | **à porter** | aucune page n'affiche `total` |
| **Deux densités de liste (vignette 16:9 / ligne compacte 80 px)** | **à porter** | aucun commutateur de densité ; 12 cartes hautes / page |
| **État vide « cul-de-sac » avec aperçu de la levée de filtre** | **à porter** | `RouteListContent.tsx:83-98` masque la description quand des filtres sont actifs |
| **CTA « Voir N parcours » recalculé en continu** | **à porter** | `useCountAllRoutes` / `useCountRoutes` livrés |
| **Chips de filtres actifs supprimables** | **à porter** (léger) | « Effacer » est global, jamais unitaire |
| « Autour de moi » (`nearLat`/`nearLon`/`nearRadius`) | **à porter** (optionnel) | `routeFilters.ts` n'expose aucun champ de proximité |
| « Rechercher dans cette zone » après déplacement de carte | **à porter** (optionnel) | cadrage volontairement figé au montage |
| Feuille de filtres recouvrant la barre d'onglets | sans objet | pas de barre d'onglets basse en web |

### 1.5 `calendrier` (§4.5)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Vue mois en grille, semaine au lundi, marqueur du jour | déjà web | `CalendarView.tsx` (`@mantine/schedule`) |
| Flux ICS avec copie, `webcal://` et régénération de jeton | déjà web | `IcsFeedSettings.tsx` |
| Sélecteur de portée (toutes équipes / une équipe) | déjà web (deux pages) | `CalendarPage.tsx`, `TeamCalendarPage.tsx` |
| **Anneau / marque « je suis inscrit » sur le jour et l'événement** | **à porter** | `CalendarView.tsx:88-92` ne lit que `id/title/start/end/type` |
| **Événement enrichi : lieu de départ, distance, D+, nom du groupe** | **à porter** | idem — les champs viennent d'arriver sur `CalendarEventDto` |
| **Distinction visuelle passé / à venir** | **à porter** (léger) | aucun traitement |
| Inclusion des voyages dans le calendrier | sans objet côté web *et* non livré | `CalendarEventType` reste `RIDE \| TRIP_STAGE` |

### 1.6 `equipe-accueil` (§4.6)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Onglets d'équipe filtrés par modules activés | déjà web | `TeamLayout.tsx`, `PublicationListPage.tsx` |
| Pages libres d'équipe en entrées de navigation deeplinkables | déjà web | `TeamPageDetailPage.tsx`, route `teamPage` |
| Accès permanent à la racine depuis une équipe | déjà web (desktop) | `Layout.tsx` + `Breadcrumb.tsx` |
| **En-tête d'équipe compact (≤ 120 px)** | **à porter** | ~300 px de chrome avant le premier contenu |
| **Rangée de statistiques cliquables (membres / sorties à venir / parcours)** | **à porter** | `TeamAboutPage.tsx` affiche 2 stats non cliquables ; `routeCount` et `upcomingRideCount` viennent d'arriver |
| **Trombinoscope accessible en lecture** | **à porter** | `teamMembers` est sous `/teams/{slug}/admin/members` |
| **Rétablir l'accès à l'équipe depuis une page de détail en mobile** | **à porter** | `Breadcrumbs visibleFrom="sm"`, détails hors `TeamLayout` |

### 1.7 `voyage-detail` et `etape-detail` (§4.7, §4.8)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Carte d'ensemble, une couleur par étape, sélection croisée | déjà web | `TripDetailPage.tsx` + `RoutesMapView` |
| Fiche parcours embarquée dans l'étape | déjà web | `StageDetailPage.tsx` (`RouteDetailView showInfo={false}`) |
| Rail de navigation entre étapes | déjà web (vertical) | `StageTabs.tsx` |
| **Distance totale, D+ cumulé, date de fin du voyage** | **à porter** | `TripDto.totalDistance`, `totalElevationGain`, `endDate` viennent d'arriver |
| **Participants d'un voyage en avatars cliquables** | **à porter** | `TripDetailPage.tsx:435-438` : des `Badge` de noms |
| **Rail d'étapes horizontal en mobile** | **à porter** | `TripLayout` garde `Tabs orientation="vertical"` en 1 colonne |
| Barre d'action basse « Participer » | sans objet | — |

### 1.8 `publication-detail` et `annonces` (§4.9, §4.10)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Markdown complet, liens cliquables, pièces jointes | déjà web | `MarkdownDisplay.tsx` (205 l.), `MediaDisplay.tsx` |
| Prix formaté, badges de type et de statut, extrait | déjà web | `AdCard.tsx` |
| Recherche + filtre de type sur les annonces | déjà web | `AdListPage.tsx`, `adFilters.ts` |
| **Filtres prix / proximité et tri sur les annonces** | **à porter** | `adFiltersSchema` n'a que `search` + `adType` ; `minPrice`, `maxPrice`, `near*`, `sortBy`/`sortDir` viennent d'arriver |
| **Galerie d'images, auteur et localisation sur le détail d'annonce** | **à porter** | `AdDto.images`, `createdByDisplayName`, `locationGeometry` viennent d'arriver |
| **Contacter le vendeur (relais e-mail)** | **à porter** | `POST /api/teams/{teamSlug}/classifieds/{slug}/contact` livré en `1.4.0` (`useContactAdAuthor`) ; aucun appel côté web |
| **Se rendre injoignable (`contactableByMembers`)** | **à porter** | `UserDto.contactableByMembers` et `UserPreferencesRequest.contactableByMembers` livrés ; le profil web n'expose rien |
| Navigation « publication précédente / suivante » | **à porter** (faible valeur) | aucun endpoint dédié ; se dériverait de la liste en cache |

La position d'une annonce est **volontairement floutée à ~1 km** : `AdDto.locationGeometry` est le
centre d'une cellule fixe, jamais l'adresse du vendeur (le point exact reste sur `AdEditDto`, que
seul le propriétaire lit). Toute carte d'annonce doit donc rendre un **secteur** — un cercle — et
jamais une punaise : poser un marqueur ponctuel sur un centre de cellule prétend une précision que
la donnée n'a pas. La règle vaut pour le détail d'annonce comme pour toute vue de liste
cartographiée.

### 1.9 `profil-preferences` et `participants-et-membres` (§4.11, §4.12)

| Idée | Verdict | Vérifié dans |
|---|---|---|
| Unités, passkeys en liste, GPS, Strava, export RGPD, suppression | déjà web | `UserProfilePage.tsx` (281 l.) |
| Feuille des participants | déjà web | `ParticipantListModal.tsx` |
| Découverte d'équipes avec recherche | déjà web | `TeamListPage.tsx` |
| **Section « Mes participations » (à venir + historique, avec compteurs)** | **à porter** | aucune section de participation dans le profil |
| **Préférences persistées serveur (thème, langue, unités, `contactableByMembers`)** | **à porter** | `preferencesStore` est local et ne connaît que thème/langue/unités ; `PATCH /api/users/me/preferences` vient d'arriver et porte les quatre champs |
| **Bouton « Rejoindre » conditionné à `joinable`** | **à porter** (léger) | `?joinable` vient d'arriver sur `GET /api/teams` |
| Notifications par catégorie, cloche de non-lus | sans objet — **non livré** | aucun endpoint de notification |

### 1.10 Règles transverses (§5)

| Règle | Verdict | Vérifié dans |
|---|---|---|
| Parité clair / sombre | déjà web, **avec trois régressions à corriger** | `RouteThumbnail.tsx:44,69,76` (`gray-1/3/5`), `PublicationListPage.tsx:227` |
| Densité cible, aucun en-tête décoratif au-delà de 120 px | **à porter** | override `Button` inopérant dans `theme.ts` → 44 px partout |
| Jamais de texte posé sur une tuile | déjà web | `getOverlayBg()` dans `lib/colors.ts` |
| Micro-copie : titre nominal, vide absolu ≠ vide filtré, erreur + action | **à porter** | cf. §2.2 |
| Cibles 44 px et libellés explicites sur les icônes-actions | **à porter** | 26 `aria-label` pour 124 `ActionIcon` |
| Unités selon la préférence, espace insécable, séparateur de milliers | déjà web | `useUnits`, `RangeInput` avec `displayMultiplier` |
| Feuilles modales au-dessus de la barre d'onglets | sans objet | — |

**Bilan.** Sur ~63 idées, 27 sont déjà au site, 27 sont à porter (dont 8 seulement à fort
rendement) et 9 sont sans objet en desktop. Plus aucune n'attend une évolution du modèle de données :
la pastille de meneur, la dernière bloquée, est débloquée par `RideGroupDto.leader`, livré en
`1.5.0` (cf. T1.7). Le portage n'est donc pas un chantier de refonte mais
une série de compléments ciblés — sauf sur la participation, qui est un manque structurant.

---

## 2. Les candidats retenus, argumentés

### 2.1 Agrégat de participation — le portage à plus fort rendement

**Constat vérifié.** Un `grep` sur `participations|registered|upcoming` dans `src/pages`,
`src/hooks` et `src/components` ne retourne rien. Le seul endroit du site qui sait que
l'utilisateur est inscrit est `RideDetailPage.tsx:143-147`, et il le **calcule côté client**
(`ride.groups.some(g => g.participants.some(p => p.id === user.id))`) : l'information n'existe donc
qu'après ouverture de la sortie. `PublicationCard` lit `topParticipants` pour afficher des avatars
mais ne compare jamais avec `user.id`. `CalendarView.tsx:88-92` ne projette que
`id / title / start / end / color`. `UserProfilePage.tsx` n'a aucune section de participation.

**Ce que l'API v2 débloque, vérifié dans le contrat et dans les DTO générés :**

- `GET /api/users/me/participations` — paramètres `from`, `to`, `status`, `page`, `size`, `view` ;
  réponse `PublicationListResponse`. Hook généré : `useListMyParticipations`
  (`src/api/endpoints/users/users.ts:976`).
- `RideDto.registered` (booléen requis), `RideDto.registeredGroupId`, `RideDto.full`.
- `RideGroupDto.registered`, `RideGroupDto.full`, **et surtout** `RideGroupDto.distance` /
  `RideGroupDto.elevationGain`.
- `TripDto.registered`, `TripDto.totalDistance`, `TripDto.totalElevationGain`, `TripDto.endDate`.
- `CalendarEventDto.registered`, `.groupName`, `.startPlaceName`, `.distance`, `.elevationGain`,
  `.status`, `.thumbnailUrl`.
- `GET /api/publications` et `GET /api/teams/{teamSlug}/publications` acceptent désormais
  `participating` et `status` (`ListAllPublicationsParams`).

Trois surfaces en découlent, sans toucher au socle visuel : un bloc « Ma prochaine sortie » en tête
d'accueil, un badge `Inscrit` sur `PublicationCard` et dans le calendrier, une section « Mes
participations » au profil. C'est le seul candidat qui apporte une capacité produit nouvelle plutôt
qu'une amélioration de finition — d'où sa place en tête du plan.

### 2.2 États vides et états d'erreur

**Constat vérifié.** `isError` n'est traité que dans trois fichiers (`HomePage.tsx`,
`AllRoutesPage.tsx`, `RouteListContent.tsx`). Ailleurs, une erreur réseau produit soit un état vide
trompeur, soit un « introuvable » abusif : `RideDetailPage.tsx:131-141` teste `error || !ride ||
!team` et affiche « Sortie introuvable » **y compris sur un 500**. Aucun bouton « Réessayer » n'est
monté dans les pages du périmètre alors que `ErrorMessage` (dans `ErrorBoundary.tsx`) en fournit
un. Le seul retour d'erreur d'action est le toast générique de `axiosInstance.ts:169-176` : un échec
de « Rejoindre » s'affiche loin du bouton, disparaît, et la carte de groupe ne change pas d'état —
`handleJoinGroup` (`RideDetailPage.tsx:233-245`) n'a ni `onError` ni bascule optimiste.

Côté états vides, le site a deux qualités et n'utilise la bonne qu'une fois :
`PublicationListPage.tsx:216-249` (icône, titre distinguant vide absolu et vide filtré, description,
bouton « Effacer la recherche ») contre `RouteListContent.tsx:83-98`, qui **masque la description
dès que des filtres sont actifs** (`{!hasFiltersOrSearch && <Text>…}`) — exactement le cas où
l'utilisateur a besoin d'aide — et ne propose aucun moyen de lever un filtre depuis l'état vide.

Le pattern « cul-de-sac » de la maquette 21 est plus riche que tout ce que le site fait : titre
nominal, phrase citant littéralement le terme recherché **et** les filtres fautifs, bouton
« Retirer le filtre X », bouton « Tout réinitialiser », puis un séparateur et un aperçu de trois
parcours que la levée ramènerait, sous le titre « Sans le filtre Gravel, 3 parcours reviennent ».
Le web peut le calculer proprement : les endpoints `count` donnent, pour chaque filtre actif retiré,
le nombre de résultats — on retient celui qui maximise le total — et une requête `size=3` fournit
l'aperçu. Le nombre de filtres actifs varie d'un rendu à l'autre : ces comptes se demandent donc en
**un seul `useQueries`** et jamais en une boucle de hooks (cf. T2.2).

### 2.3 Densité

**Constat vérifié.** `src/lib/theme.ts` contient :

```ts
Button: { styles: { root: { minHeight: 'var(--button-min-height, 44px)',
  '@media (minWidth: 48em)': { '--button-min-height': '36px' } } } }
```

Une media query dans un objet `styles` Mantine est un style inline : elle n'est **jamais**
appliquée, et `minWidth` n'est de toute façon pas une syntaxe CSS valide. **Tous les boutons du
site font 44 px, y compris en desktop**, ce qui gonfle chaque barre d'action et chaque carte. C'est
la correction au meilleur rapport bénéfice/coût du frontend.

S'y ajoutent : des grilles à 3 colonnes de 12 cartes hautes (`PublicationCard` empile image 160 px
+ ligne équipe + titre + extrait + ligne sociale + `StatGroup`), aucun mode compact pour les
2 585 parcours de `n-peloton`, et ~300 px de chrome sur une page d'équipe (en-tête + `TeamAvatar
size="xl"` + titre h1 + bande `NavButtons` de ~80 px) avant le premier contenu. La maquette 21
définit précisément la ligne compacte à porter : vignette carrée 80 px, titre 15/700 sur une ligne,
distance et D+ en `stats--nowrap`, badges revêtement + visibilité, chevron — hauteur totale 100 px,
contre ~330 px pour la carte à vignette.

### 2.4 Performance

**Constat vérifié.** `RoutesMapView.tsx:98-125` charge les parcours dans une boucle `for` avec
`await`, **séquentiellement**, en appelant la fonction brute `getRoute(teamSlug, item.routeSlug)` —
donc hors React Query : pas de cache, pas de déduplication, pas d'état d'erreur, pas de `staleTime`,
et un `useState` local pour le résultat. En parallèle, chaque `RideGroupCard` refait son propre
`useGetRoute` (`RideGroupCard.tsx:69-72`). Sur une sortie à 10 groupes : ~20 téléchargements de
géométrie complète pour une seule page.

Ce que remplace quoi, précisément :

| Aujourd'hui | Remplacé par | Gain |
|---|---|---|
| `useGetRoute` de `RideGroupCard` pour lire distance et D+ | `group.distance` / `group.elevationGain` (`RideGroupDto`) | supprime N requêtes par sortie |
| `useGetRoute` de `RideGroupCard` pour les URL GPX/FIT | même appel, **différé** au clic du groupe d'actions | déplace N requêtes hors du chemin critique |
| boucle `for … await getRoute()` de `RoutesMapView` | `useQueries` sur `getGetRouteQueryOptions(teamSlug, slug, { simplify })` | parallélise, met en cache, déduplique avec les autres consommateurs |
| géométrie complète pour tracer une carte d'aperçu | `GET …/routes/{routeSlug}?simplify=<m>` (`GetRouteParams.simplify`, borné à 1000) ou `?points=<n>` | réduit la charge utile d'un ordre de grandeur |
| `route.tracks[].line.coordinates` pour dessiner le profil | `GET …/routes/{routeSlug}/elevation-profile?samples=300` → `ElevationProfileDto { points[{distance, elevation, grade}], minElevation, maxElevation }` (hook `useGetRouteElevationProfile`) | découple le graphe de la géométrie |
| `size=1` pour connaître un total | `GET /api/routes/count`, `…/teams/{teamSlug}/routes/count`, `…/publications/count` → `CountResponse.total` (hooks `useCountAllRoutes`, `useCountRoutes`, `useCountAllPublications`, `useCountPublications`) | requête constante |
| `media.markdown` intégral transporté par chaque item de liste | `?view=COMPACT` (`ListViewMode.COMPACT`) + lecture de `excerpt` / `thumbnailUrl` | allège les 12 items d'une page de liste |

Attention sur `view=COMPACT` : la documentation du contrat est explicite — « returns
`media.markdown` empty and `media.assets` empty ». `PublicationCard.tsx:148` rend
`<CardDescription markdown={true} media={publication.media} />` et `CardImage media={…}` : passer en
compact **impose** de basculer ces deux points sur `excerpt` et `thumbnailUrl` d'abord, sinon les
cartes se vident. Idem pour `RouteCard.tsx:20-25` (`route.media.assets.thumbnailLight/Dark`) et
`AdCard.tsx:50-59`.

### 2.5 Accessibilité

**Constat vérifié.** 26 `aria-label` pour 124 usages d'`ActionIcon`. Sont sans nom accessible : le
bouton de copie du flux ICS (`IcsFeedSettings.tsx`), le bouton d'envoi d'un commentaire
(`CommentForm.tsx`), les boutons photo/suppression du profil (`UserProfilePage.tsx`),
`PasskeyManager`, `SlugEditor` (3), `MediaEditor`, `PlaceList` (2), `GpsConnectionsManager`,
`SocialConnectionsManager`, `DomainFormModal` (9). La zone participants de
`RideGroupCard.tsx:184-188` est un `UnstyledButton` avec un simple `title=`, et le déclencheur du
menu « Envoyer vers l'appareil » (`RideGroupCard.tsx:243`) n'a ni `aria-label` ni rôle.
`RouteThumbnail.tsx:52` porte `alt="Route preview"` **en dur et en anglais** sur toutes les
vignettes du fil. `RouteThumbnail.tsx:44,69,76` et `PublicationListPage.tsx:227` utilisent
`--mantine-color-gray-1/3/5`, valeurs claires en toutes circonstances : bordure et cercle clairs en
thème sombre. `MapMarkers.tsx:82` force `#000000` sur les bornes kilométriques.

Aucun de ces points n'est une idée de la v2 mobile à proprement parler, mais la règle §5 du brief
(« libellés explicites sur toutes les icônes-actions ») les rend justiciables du même lot, et leur
coût unitaire est négligeable.

### 2.6 Responsive et rupture du silo — en miroir de l'app

**Constat vérifié.** Le fil d'Ariane complet est `visibleFrom="sm"` (`Breadcrumb.tsx`), remplacé en
mobile par un unique lien retour ; les menus déroulants `IconDots` qui donnent accès aux autres
onglets du niveau sont desktop-only. Les pages de détail (sortie, article, voyage, annonce) ne sont
pas montées dans `TeamLayout` : pas d'en-tête d'équipe, pas d'onglets. Un mobile qui ouvre une
sortie depuis l'accueil **perd donc tout chemin vers l'équipe** — c'est exactement le silo que le
brief reproche à l'app, en symétrique. `TripLayout` conserve `Tabs orientation="vertical"` même
quand `SimpleGrid` retombe à une colonne : une étape d'un voyage de 10 jours affiche 10 onglets
empilés avant le contenu. `NavButtons` défile horizontalement dans un `ScrollArea type="never"`
sans indice de débordement — la maquette corrige ce même défaut par un `mask-image` de 28 px.

### 2.7 Commentaires

**Constat vérifié.** `src/hooks/useComments.ts:49-69` appelle `listRideComments(teamSlug, slug)`
sans aucun paramètre et charge l'arbre entier ; `CommentSection.tsx` affiche « Commentaires (N) » à
partir de la longueur du tableau ; aucune carte de liste ne porte de compteur.

L'API v2 livre `page`, `size`, `sort` (`SortDirection`) et `parentId` sur les quatre endpoints de
commentaires (rides, posts, trips, routes — voir `ListRideCommentsParams`), avec une note explicite :
« Omit both page and size to get the whole comment tree, as before » — la compatibilité est donc
garantie et la migration peut être incrémentale. S'y ajoutent `CommentListResponse.itemTotal / page
/ size`, `CommentDto.replyCount`, et `commentCount` sur `RideDto`, `PostDto`, `TripDto`, `RouteDto`,
`RouteDetailDto`.

### 2.8 Sortie passée toujours « rejoignable » — défaut fonctionnel

`RideDetailPage.tsx:147` :

```ts
const canJoinRide = isMember && ride.status === Status.PUBLISHED && !hasJoinedAnyGroup
```

Aucun test de `ride.dateTime`. Une sortie de l'an dernier affiche donc un bouton « Rejoindre »
pleinement actif. La maquette 12 impose un badge gris `TERMINÉE` et le masquage de toute action
d'inscription. C'est le seul élément de ce document qui est un bug plutôt qu'une amélioration ; il
est traité avec le lot participation parce qu'il touche les mêmes lignes.

---

## 3. Plan d'implémentation

Cinq lots ordonnés. Le lot 0 est un prérequis technique des lots 1 à 3 ; les lots 4 et 5 sont
indépendants et peuvent glisser. Tailles : **S** = un fichier et ses clés i18n ; **M** = un
composant nouveau plus 2 à 4 points d'appel ; **L** = un composant nouveau, un schéma de filtres et
une refonte de page.

### Lot 0 — Socle transverse

Rien de visible ne se fait proprement sans ces cinq briques ; toutes sont de la composition Mantine
pure.

---

**T0.1 ☑ — Corriger l'override `Button` du thème**
*Fichiers* : `frontend/src/lib/theme.ts` (modifié), `frontend/src/index.css` (modifié).
*Détail* : retirer la media query de l'objet `styles` (inopérante) et poser la variable dans
`index.css` : `:root { --button-min-height: 44px }` puis `@media (min-width: 48em) { :root {
--button-min-height: 36px } }`. `theme.ts` conserve `minHeight: 'var(--button-min-height, 44px)'`.
Aucune dépendance sur un global navigateur : compatible SSR.
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : en viewport ≥ 768 px, `getComputedStyle` d'un `<Button>` retourne
`min-height: 36px` ; en dessous de 768 px, `44px`. Aucune régression visuelle sur `RideGroupCard`,
`RouteFilterPanel` et `Layout`.

---

**T0.2 ☑ — `EmptyState`, le composant d'état vide unique**
*Fichiers* : `frontend/src/components/common/EmptyState.tsx` (créé),
`frontend/src/locales/{fr,en}/common.json` (modifiés).
*Détail* : reprendre littéralement le bon pattern de `PublicationListPage.tsx:216-249`, généralisé.
Props : `icon: ReactNode`, `title: string`, `description?: string`, `actions?: ReactNode`,
`variant: 'absolute' | 'filtered'`. Composition : `Paper withBorder py="xl"` → `Center` → `Stack
align="center"` → icône 48 px `c="dimmed"` + `Title order={3}` + `Text c="dimmed" maw={400}
ta="center"` + `Group` d'actions. Icônes `IconSearchOff` (filtré) et l'icône de domaine (absolu).
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : le composant rend correctement les deux variantes ; aucune couleur `gray-N` en
dur (uniquement `c="dimmed"` et `var(--mantine-color-dimmed)`), donc lisible en thème sombre.

---

**T0.3 ☑ — `QueryStateBoundary` : chargement, erreur récupérable, vide**
*Fichiers* : `frontend/src/components/common/QueryStateBoundary.tsx` (créé),
`frontend/src/components/common/DetailPageSkeleton.tsx` (créé),
`frontend/src/components/common/ErrorBoundary.tsx` (modifié — exporter `ErrorMessage` proprement).
*Détail* : `QueryStateBoundary` prend `isLoading`, `isError`, `error`, `isEmpty`, `onRetry`,
`skeleton`, `empty` et rend l'un des quatre cas. La discrimination 404 / autre se fait sur
`ApiClientError.status` (`src/lib/apiError.ts`) : 404 → « introuvable » avec lien de retour ;
tout autre statut → `ErrorMessage` avec bouton « Réessayer » branché sur `refetch`.
`DetailPageSkeleton` compose la structure « carte + 3 blocs » de la maquette avec `Skeleton` Mantine
(bloc carte à la hauteur de `MAP_HEIGHT_CSS.standard` de `useResponsive.ts`, puis 3 `Paper` de
titres/lignes).
*Dépendances* : T0.2. *Taille* : **M**.
*Critère de fin* : un 500 simulé sur `GET /api/teams/{slug}/rides/{slug}` affiche « Chargement
impossible » + « Réessayer » et non « Sortie introuvable » ; un 404 affiche « Sortie introuvable » +
lien vers le fil d'équipe.

---

**T0.4 ☐ — `ResultCount` : le total en tête de liste**
*Fichiers* : `frontend/src/components/common/ResultCount.tsx` (créé),
`frontend/src/locales/{fr,en}/common.json` (modifiés : clés plates `list.count_one` /
`list.count_other` déclinées par domaine, avec `{{count}}`).
*Détail* : `Text size="sm" c="dimmed"` rendant « 2 585 parcours ». Alimenté par `total` de la
réponse de liste quand il est déjà là (cas général), et par les endpoints `count`
(`useCountAllRoutes` / `useCountRoutes` / `useCountAllPublications` / `useCountPublications`, ou
leurs `…QueryOptions` sous un `useQueries` quand le nombre de comptes varie — cf. T2.2) quand on a
besoin du total **d'un jeu de filtres qu'on n'affiche pas** (T2.2 et T3.2).
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : `HomePage`, `PublicationListPage`, `RouteListPage`, `AllRoutesPage` et
`AdListPage` affichent leur total au-dessus de la grille ; le nombre est formaté avec le séparateur
de milliers de la locale.

---

**T0.5 ☐ — Lot d'accessibilité et de thème sombre**
*Fichiers modifiés* : `frontend/src/components/route/RouteThumbnail.tsx` (alt traduit via `t()`,
`gray-3` → `var(--mantine-color-default-border)`, `gray-1` → `var(--mantine-color-default-hover)`,
`gray-5` → `var(--mantine-color-dimmed)`), `frontend/src/pages/publication/PublicationListPage.tsx`
(`gray-1`), `frontend/src/components/ride/RideGroupCard.tsx` (`aria-label` sur la zone participants
et sur le déclencheur « Envoyer vers l'appareil »), `frontend/src/components/calendar/IcsFeedSettings.tsx`,
`frontend/src/components/comment/CommentForm.tsx`, `frontend/src/pages/auth/UserProfilePage.tsx`,
`frontend/src/components/common/SlugEditor.tsx`, `frontend/src/components/team/PlaceList.tsx`,
`frontend/src/components/profile/{GpsConnectionsManager,SocialConnectionsManager}.tsx`,
`frontend/src/components/auth/PasskeyManager.tsx`, `frontend/src/components/map/MapMarkers.tsx`
(borne kilométrique : couleur dérivée du `colorScheme` au lieu de `#000000`).
*Détail* : `aria-label` sur chaque `ActionIcon` sans texte ; remplacer le `UnstyledButton`
« participants » par un `Button variant="subtle"` ou un `UnstyledButton` portant `aria-label` +
`component="button"`.
*Dépendances* : aucune. *Taille* : **M**.
*Critère de fin* : `grep -c 'aria-label' src/components src/pages` ≥ nombre d'`ActionIcon` sans
enfant textuel ; aucune occurrence de `--mantine-color-gray-[135]` hors code cartographique ; la
vignette de parcours a un `alt` issu de `t()`.

---

### Lot 1 — Participation

Le lot à plus fort rendement. Il ne crée aucun composant de bas niveau : tout se compose à partir
de `Card`, `Badge`, `Stat`, `UserAvatarGroup`, `Progress` et `RouteThumbnail` existants.

---

**T1.1 ☐ — Hook et bloc « Ma prochaine sortie »**
*Fichiers* : `frontend/src/hooks/useMyParticipations.ts` (créé — mince façade sur
`useListMyParticipations`, pas de refetch manuel), `frontend/src/components/home/NextRideCard.tsx`
(créé), `frontend/src/pages/home/HomePage.tsx` (modifié), `frontend/src/locales/{fr,en}/common.json`.
*Détail* : `useMyParticipations({ from: nowIso, status: Status.PUBLISHED, size: 5, view:
ListViewMode.COMPACT })`. Le hook **ne s'active que si `isAuthenticated`** (`query: { enabled }`) — le
SSR est anonyme, donc le bloc se rend côté client après hydratation et ne doit pas figurer dans le
`prefetch` de la route `home`. La carte compose : `Card` (primitive maison) + `CardImage` alimenté
par `thumbnailUrl` + `TypeBadge` + un badge « Inscrit » (`Badge color="primary" variant="light"
leftSection={<IconCheck size={12}/>}`) + `Title order={4}` + compte à rebours relatif (dayjs
`fromNow`) + `Stat` date, heure, lieu, distance, D+ + `UserAvatarGroup` + `Group` de deux actions
(`Button` « Voir la sortie », `Button variant="outline"` « Se désinscrire » → `ConfirmDialog`).
Les métriques du **groupe** viennent de `registeredGroupId` croisé avec `ride.groups` ; en `COMPACT`
les groupes restent présents (seuls `media.markdown` et `media.assets` sont vidés) — vérifier au
premier rendu, et retomber sur les métriques de la sortie si `registeredGroupId` est absent.
*Dépendances* : T0.1. *Taille* : **M**.
*Critère de fin* : connecté avec une inscription future, l'accueil affiche le bloc en tête ; sans
inscription future, le bloc ne se rend pas du tout (pas de squelette résiduel) ; en anonyme, aucune
requête `/api/users/me/participations` n'est émise ; `curl` de la page d'accueil (SSR) ne contient
pas le bloc et n'échoue pas.

---

**T1.2 ☐ — Badge « Inscrit » et places dans le fil**
*Fichiers modifiés* : `frontend/src/components/card/PublicationCard.tsx`,
`frontend/src/components/card/PublicationCardProgress.tsx`, `frontend/src/locales/{fr,en}/common.json`.
*Détail* : ajouter le badge `Inscrit` dans la pile de badges existante (elle porte déjà type /
statut / visibilité) quand `(publication as RideDto | TripDto).registered` est vrai — **plus aucun
calcul côté client**, on lit le champ. `PublicationCardProgress` utilise `ride.full` pour le libellé
« Complet » au lieu de recalculer sur les capacités de groupes.
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : le badge apparaît sur les cartes du fil d'accueil et du fil d'équipe pour les
sorties et voyages où l'utilisateur est inscrit, et jamais en anonyme (`registered` vaut alors
`false` par contrat).

---

**T1.3 ☐ — Calendrier : inscription, lieu, distance et groupe**
*Fichiers modifiés* : `frontend/src/components/calendar/CalendarView.tsx`.
*Détail* : la projection `CalendarEventDto → Schedule` (`CalendarView.tsx:88-92`) ne retient
aujourd'hui que cinq champs. Ajouter `startPlaceName`, `distance`, `elevationGain`, `groupName`,
`registered`, `status` dans le rendu personnalisé de l'événement (`Schedule` accepte un rendu de
contenu ; sinon composer un `Tooltip` + un `Popover`, tous deux Mantine). Marquer l'inscription par
une bordure gauche primaire de 3 px sur l'événement, et non par un anneau de jour — la grille de
`@mantine/schedule` n'expose pas de rendu de cellule de jour. Les événements passés passent à
`opacity: 0.55`. Unités via `useUnits`.
*Dépendances* : T0.1. *Taille* : **M**.
*Critère de fin* : un événement du calendrier affiche « équipe · heure · lieu » et, si
`registered`, le nom du groupe ; le rendu est identique dans les deux thèmes ; aucune requête
supplémentaire par événement (pas de N+1 `getRide`).

---

**T1.4 ☐ — Section « Mes participations » au profil**
*Fichiers* : `frontend/src/components/profile/MyParticipations.tsx` (créé),
`frontend/src/pages/auth/UserProfilePage.tsx` (modifié), `frontend/src/locales/{fr,en}/common.json`.
*Détail* : deux lignes cliquables — « Mes sorties à venir » (`from = maintenant`, badge indigo) et
« Historique » (`to = maintenant`, badge neutre) — chacune ouvrant une liste. Décision à trancher :
soit deux `Accordion` in situ (aucune route nouvelle), soit une page dédiée. **Recommandation :
`Accordion` Mantine**, pour éviter d'ajouter une entrée à `contracts/routes.yaml` pour une vue
secondaire. Si une page dédiée est retenue, elle passe par `contracts/routes.yaml` +
`pnpm generate-routes` + une entrée dans `frontend/src/config/routes.config.ts`, jamais par une
édition de `paths.generated.ts`.
*Dépendances* : T1.1 (le hook), T0.4 (les compteurs). *Taille* : **M**.
*Critère de fin* : les deux compteurs sont exacts (`total` de la réponse) ; le dépliage charge une
page paginée ; la pagination est dans la query string via `useUrlFilters` si une page dédiée est
retenue.

---

**T1.5 ☐ — Filtre « je participe » et « à venir » sur les fils**
*Fichiers modifiés* : `frontend/src/hooks/filters/publicationFilters.ts` (ajouter
`participating: z.coerce.boolean().default(false)` et `scope: z.enum(['all','upcoming'])`),
`frontend/src/hooks/filters/homeFilters.ts` (alias courts, p. ex. `participating` → `me`),
`frontend/src/pages/home/HomePage.tsx`, `frontend/src/pages/publication/PublicationListPage.tsx`.
*Détail* : projeter sur les paramètres `participating`, `status` et `from` de
`ListAllPublicationsParams` / `ListPublicationsParams`. Contrôle : un `SegmentedControl` Mantine
(« Tout / À venir / Je participe ») posé à côté des `Select` existants. **Jamais de `useState`** :
tout passe par `useUrlFilters`.
*Dépendances* : aucune. *Taille* : **M**.
*Critère de fin* : les trois états produisent des URL distinctes et partageables ; le retour arrière
restaure l'état exact ; « Je participe » est masqué pour un visiteur anonyme (le contrat précise
« Yields nothing for an anonymous visitor »).

---

**T1.6 ☐ — Sortie passée et sortie complète**
*Fichiers modifiés* : `frontend/src/pages/ride/RideDetailPage.tsx`,
`frontend/src/components/ride/RideGroupCard.tsx`, `frontend/src/locales/{fr,en}/common.json`.
*Détail* : `canJoinRide` devient
`isMember && ride.status === Status.PUBLISHED && !ride.registered && !isPast && !ride.full`, avec
`isPast = dayjs(ride.dateTime).isBefore(dayjs())`. Ajouter un `Badge color="gray"` « Terminée »
dans la pile de badges quand `isPast`. `RideGroupCard` utilise `group.full` au lieu de recalculer
`group.maxParticipants && group.countParticipants >= group.maxParticipants`
(`RideGroupCard.tsx:64`), et `group.registered` au lieu de l'appartenance calculée dans la page.
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : sur une sortie dont `dateTime` est passée, aucun bouton « Rejoindre » ni
« Quitter » n'est rendu et le badge « Terminée » apparaît ; sur un groupe `full`, le badge gris
« Complet » remplace l'action.

---

**T1.7 ☐ — Meneur de groupe : avatar, nom et pastille**
*Fichiers modifiés* : `frontend/src/components/ride/RideGroupCard.tsx`,
`frontend/src/components/ride/ParticipantListModal.tsx`,
`frontend/src/locales/{fr,en}/common.json`.
*Détail* : le contrat `1.5.0` livre `RideGroupDto.leader`, un `PublicUserDto` (`id`, `displayName`,
`avatarUrl`) **nullable**. `RideGroupCard` rend, sous le nom du groupe, un `Group gap="xs"` :
`UserAvatar size="sm"` (de `common/UserAvatar`) + `Text size="sm"` du `displayName` + `Badge
variant="light" leftSection={<IconShieldCheck size={12}/>}` « Meneur » (`@tabler/icons-react`, icône
déjà importée par `ParticipantListModal`). Quand `group.leader` est nul, **rien n'est rendu** : ni
pastille, ni ligne vide, ni libellé « Aucun meneur ». Ce n'est pas un cas dégradé mais **le cas
courant** — la plupart des groupes n'auront pas de meneur désigné, et la carte doit se lire
exactement comme aujourd'hui sans cette ligne. Dans `ParticipantListModal`, la prop `isOrganizer`
(présente depuis l'origine, alimentée par personne) se calcule enfin dans `RideGroupCard` :
`participant.id === group.leader?.id`. **Jamais de repli sur `createdBy`** : cf. §4. L'appartenance
du meneur à l'équipe est vérifiée à l'écriture seulement et **n'est pas revérifiée ensuite** — un
meneur qui a quitté l'équipe reste affiché, parce que la sortie a eu lieu ; le front ne doit donc
tenter aucune validation d'appartenance à l'affichage. Aucune requête supplémentaire : le meneur
arrive dans la réponse de la sortie (l'association est en `@ManyToOne(LAZY)` et le batch fetch
résout tous les meneurs d'une sortie en une requête).
L'**attribution** n'est pas dans cette tâche : `GroupRequest.leaderId` est acceptée par l'API depuis
`1.5.0`, mais `RideEditor.tsx` reste hors périmètre de ce document (cf. §4).
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : sur un groupe avec meneur, l'avatar et le nom apparaissent sur la carte et la
pastille sur la ligne du participant correspondant dans la feuille des participants ; sur un groupe
sans meneur, la carte est strictement identique à l'existant ; aucune requête supplémentaire n'est
émise ; les clés de libellé sont plates et présentes dans `fr` **et** `en`.

---

### Lot 2 — États, erreurs et cul-de-sac

---

**T2.1 ☐ — Généraliser `EmptyState`**
*Fichiers modifiés* : `frontend/src/pages/home/HomePage.tsx`,
`frontend/src/components/route/RouteListContent.tsx`,
`frontend/src/pages/publication/PublicationListPage.tsx` (remplacer le pattern local par le
composant), `frontend/src/pages/ad/AdListPage.tsx`, `frontend/src/pages/team/TeamListPage.tsx`,
`frontend/src/pages/ride/RideDetailPage.tsx` (le « Aucun groupe » de la ligne 437),
`frontend/src/components/comment/CommentSection.tsx`, `frontend/src/locales/{fr,en}/common.json`.
*Détail* : dans `RouteListContent.tsx`, **supprimer** la garde `{!hasFiltersOrSearch && <Text>…}` :
le cas filtré est précisément celui qui doit porter la description la plus riche. Micro-copie
imposée par §5 du brief : titre nominal, phrase explicative, vide absolu distinct du vide filtré.
*Dépendances* : T0.2. *Taille* : **M**.
*Critère de fin* : les six surfaces rendent le même composant ; un vide filtré porte toujours au
moins un bouton de sortie (« Effacer la recherche » ou « Effacer les filtres »).

---

**T2.2 ☐ — État vide « cul-de-sac » sur les parcours**
*Fichiers* : `frontend/src/components/route/RouteDeadEnd.tsx` (créé),
`frontend/src/components/route/RouteListContent.tsx` (modifié),
`frontend/src/locales/{fr,en}/common.json`.
*Détail* : quand le résultat est vide **et** qu'au moins un filtre est actif, demander le compte
qu'obtiendrait chaque filtre actif levé un par un (au plus 7 : `search`,
`minDistance`+`maxDistance`, `minElevationGain`+`maxElevationGain`, `hilliness`, `surfaceType`,
`windDirection`) et retenir celui qui maximise `CountResponse.total`. Le nombre de filtres actifs
change d'un rendu à l'autre : **un appel de hook par filtre violerait les règles des hooks React**
(le nombre de hooks d'un composant doit être constant). Passer donc par un `useQueries` unique, sur
le modèle de T3.3 : `useQueries({ queries: activeFilterKeys.map(k =>
getCountAllRoutesQueryOptions({ ...params, ...cleared(k) })) })` (et
`getCountRoutesQueryOptions(teamSlug, …)` sur la liste d'équipe). Le tableau de résultats est
ensuite réduit en `useMemo` — le hook lui-même reste appelé une fois, quelle que soit la longueur du
tableau. Rendre alors : `EmptyState`
avec la phrase citant le terme recherché et les filtres actifs, un `Button variant="outline"
size="sm"` « Retirer le filtre {X} » (qui fait un `setFilters({ [k]: undefined })`) et un « Tout
réinitialiser », puis un `Divider` et un aperçu de 3 parcours obtenu par un `useListAllRoutes` /
`useListRoutes` avec le filtre levé et `size: 3` — rendus en ligne compacte (T3.2), 56 px de
vignette.
*Dépendances* : T0.2, T0.4, T3.2 (pour la ligne compacte). *Taille* : **L**.
*Critère de fin* : sur `?search=ventoux&surf=GRAVEL&hill=HILLY` sans résultat, la page propose de
retirer le filtre dont la levée ramène le plus de parcours, et affiche trois exemples ; les appels
`count` ne sont émis que dans l'état vide, jamais dans le chemin nominal (chaque option du
`useQueries` porte `enabled: isEmpty && hasFiltersOrSearch`).

---

**T2.3 ☐ — Distinguer 404 et erreur sur les pages de détail**
*Fichiers modifiés* : `frontend/src/pages/ride/RideDetailPage.tsx`,
`frontend/src/pages/post/PostDetailPage.tsx`, `frontend/src/pages/trip/TripDetailPage.tsx`,
`frontend/src/pages/trip/StageDetailPage.tsx`, `frontend/src/pages/route/RouteDetailPage.tsx`,
`frontend/src/pages/ad/AdDetailPage.tsx`.
*Détail* : remplacer les `if (error || !x) return <introuvable/>` par `QueryStateBoundary`, et
remplacer le `LoadingPage` (spinner nu, utilisé par 39 fichiers) par `DetailPageSkeleton` sur ces
six pages seulement.
*Dépendances* : T0.3. *Taille* : **M**.
*Critère de fin* : les six pages distinguent 404 et 5xx, offrent « Réessayer » sur 5xx, et
n'affichent plus de spinner plein écran.

---

**T2.4 ☐ — Retour d'erreur contextuel et bascule optimiste sur l'inscription**
*Fichiers modifiés* : `frontend/src/pages/ride/RideDetailPage.tsx`,
`frontend/src/components/ride/RideGroupCard.tsx`, `frontend/src/locales/{fr,en}/common.json`.
*Détail* : ajouter `onMutate` (bascule optimiste de `group.registered` / `ride.registered` dans le
cache de `getGetRideQueryKey`), `onError` (rollback + message **dans la carte du groupe**, via une
prop `error?: string` rendue en `Alert color="red" variant="light"` persistant) et `onSettled`
(invalidation). Le message est dérivé du `code` de `ErrorResponse` (`ApiClientError.error`) et
traduit par une clé plate `rides.join.error.<code>`, avec un repli générique. Le toast global de
`axiosInstance.ts` reste en place pour tout le reste — ne pas le désactiver globalement.
*Dépendances* : T1.6 (mêmes lignes). *Taille* : **M**.
*Critère de fin* : un échec « groupe complet » affiche un bandeau rouge **dans la carte du groupe
concerné**, la carte revient à son état antérieur, et le bandeau persiste jusqu'à la prochaine
tentative ; le clic sur « Rejoindre » bascule visuellement avant la réponse serveur.

---

**T2.5 ☐ — `ErrorBoundary` par section**
*Fichiers modifiés* : `frontend/src/components/common/ErrorBoundary.tsx` (variante `inline` sans
`mih="100vh"`), `frontend/src/config/RouteGenerator.tsx` (enveloppe par route),
`frontend/src/pages/ride/RideDetailPage.tsx` et `frontend/src/pages/trip/TripDetailPage.tsx`
(enveloppe autour du `Suspense` de `RoutesMapView`).
*Dépendances* : T0.3. *Taille* : **S**.
*Critère de fin* : une exception jetée dans `RoutesMapView` n'efface plus la page ; elle affiche un
bloc d'erreur à la place de la carte.

---

### Lot 3 — Densité et performance

---

**T3.1 ☐ — Passer les listes en `view=COMPACT`**
*Fichiers modifiés* : `frontend/src/pages/home/HomePage.tsx`,
`frontend/src/pages/publication/PublicationListPage.tsx`,
`frontend/src/components/route/RouteListContent.tsx` (et ses deux pages appelantes),
`frontend/src/components/card/PublicationCard.tsx`, `frontend/src/components/card/RouteCard.tsx`,
`frontend/src/components/card/common/Card.tsx` (`CardDescription` y est défini),
`frontend/src/components/card/common/CardImage.tsx`.
*Détail* : passer `view: ListViewMode.COMPACT` aux hooks de liste, **après** avoir basculé
`CardDescription` sur `excerpt` (avec repli sur le rendu markdown quand `excerpt` est absent) et
`CardImage` / `RouteCard` sur `thumbnailUrl` (avec le jeton `{size}` remplacé comme aujourd'hui, en
×2 pour le HiDPI). L'ordre est impératif : l'inverse vide les cartes.
*Dépendances* : aucune. *Taille* : **M**.
*Critère de fin* : la charge utile d'une page de 12 publications diminue de façon mesurable
(onglet réseau) et aucune carte ne perd son extrait ni sa vignette ; le détail (`getRide`,
`getPost`, `getTrip`) reste en `FULL`.

---

**T3.2 ☐ — Commutateur de densité sur les listes de parcours**
*Fichiers* : `frontend/src/components/route/RouteRow.tsx` (créé — la ligne compacte),
`frontend/src/components/route/RouteDensityToggle.tsx` (créé),
`frontend/src/components/route/RouteListContent.tsx` (modifié),
`frontend/src/hooks/filters/routeFilters.ts` (modifié : `density: z.enum(['card','row'])` avec
défaut dérivé du total), `frontend/src/locales/{fr,en}/common.json`.
*Détail* : `RouteRow` = `Card component={Link}` en `Group` : `RouteThumbnail size="lg"` (80 px) +
`Stack gap={4}` (titre `fw={700}` sur une ligne, `StatGroup` distance + D+ en `wrap="nowrap"`,
`Group` de badges revêtement + visibilité) + `IconChevronRight`. Le commutateur est un
`SegmentedControl` Mantine à deux icônes (`IconLayoutGrid` / `IconLayoutList`), posé à côté de
`RouteViewToggle`. La densité est un **filtre d'URL** (`d=row`), jamais un `useState`. Défaut
`row` au-delà de 200 résultats, conformément à la maquette 21 — la bascule automatique se lit sur
le `total` déjà présent dans la réponse.
*Dépendances* : T0.1. *Taille* : **L**.
*Critère de fin* : sur `n-peloton`, l'ouverture de la liste de parcours affiche la densité compacte
par défaut ; le choix survit à la navigation et au partage d'URL ; en compact, au moins 8 lignes
tiennent dans un viewport de 900 px de haut.

---

**T3.3 ☐ — `RoutesMapView` : sortir la boucle `await` de React**
*Fichiers modifiés* : `frontend/src/components/route/RoutesMapView.tsx`.
*Détail* : remplacer le `useEffect` + `for … await getRoute()` + `useState` (lignes 96-130) par
`useQueries({ queries: items.filter(i => i.routeSlug).map(i => getGetRouteQueryOptions(teamSlug,
i.routeSlug, { simplify: 25 })) })`. Les données deviennent dérivées (`useMemo` sur les résultats)
au lieu d'être stockées ; l'état d'erreur devient exploitable ; le cache est partagé avec
`RideGroupCard` et `RouteDetailPage`. Le paramètre `simplify` est plafonné à 1000 par le contrat ;
25 m est un point de départ à ajuster visuellement.
*Dépendances* : T2.5 (pour l'affichage d'erreur). *Taille* : **M**.
*Critère de fin* : sur une sortie à 10 groupes, l'onglet réseau montre 10 requêtes **parallèles**
et non séquentielles, et zéro requête au retour sur la page dans la fenêtre de `staleTime` ;
le tracé reste visuellement fidèle.

---

**T3.4 ☐ — `RideGroupCard` : supprimer le second téléchargement de parcours**
*Fichiers modifiés* : `frontend/src/components/ride/RideGroupCard.tsx`.
*Détail* : distance et D+ se lisent désormais sur `group.distance` / `group.elevationGain`
(`RideGroupDto`). Le `useGetRoute` (`RideGroupCard.tsx:69-72`), qui ne sert plus qu'à construire les
URL GPX/FIT et l'envoi vers l'appareil, passe en `enabled: false` et n'est activé qu'à l'ouverture
du groupe d'actions (état local d'interaction — ce n'est ni un filtre ni un tri, `useState` est
légitime ici). Repli sur la sortie quand le groupe n'a pas de parcours propre (logique
`effectiveRouteSlug` déjà en place).
*Dépendances* : T3.3 (pour ne pas se priver du cache partagé). *Taille* : **S**.
*Critère de fin* : au chargement d'une sortie à 10 groupes, aucune requête `getRoute` n'est émise
par les cartes de groupe ; les métriques affichées sont identiques à l'existant ; les
téléchargements fonctionnent toujours.

---

**T3.5 ☐ — Profil altimétrique alimenté par `elevation-profile`**
*Fichiers modifiés* : `frontend/src/components/route/ElevationChart.tsx` (accepter une source
`ElevationProfileDto` en plus des `tracks`), `frontend/src/components/route/RoutesMapView.tsx`
(overlay de profil), `frontend/src/components/route/RouteMapView.tsx`.
*Détail* : `useGetRouteElevationProfile(teamSlug, routeSlug, { samples: 300 })` renvoie
`{ points: [{distance, elevation, grade}], minElevation, maxElevation, distance }`. Le champ
`grade` remplace le calcul local de pente ; la colorisation continue d'utiliser
`getColorFromGradient` de `map/mapUtils.ts` pour rester cohérente avec la carte. **Ne pas toucher**
à `RouteFullscreenView` ni à la synchronisation carte↔graphique (`useMapChartZoomSync`) : la vue
plein écran a besoin de la géométrie complète de toute façon.
*Dépendances* : T3.3. *Taille* : **M**.
*Critère de fin* : le profil d'un parcours s'affiche sans que la géométrie complète soit chargée
sur les vues d'aperçu ; la colorisation est visuellement identique à l'existant ; l'import dynamique
de `chartjs-plugin-zoom` reste en place (invariant SSR).

---

**T3.6 ☐ — Réduire le chrome d'équipe**
*Fichiers modifiés* : `frontend/src/components/team/TeamLayout.tsx`,
`frontend/src/components/common/NavButtons.tsx`.
*Détail* : `TeamAvatar size="xl"` → `size="lg"`, `Title order={1}` conservé mais avec un
`lineClamp={1}`, actions d'adhésion regroupées sur la même ligne que le titre (`Group
justify="space-between" wrap="nowrap"`). `NavButtons` : réduire le carré à 40 px et le libellé à une
ligne au-delà de `sm`, et ajouter un indice de débordement (`mask-image` en `style` sur le
`ScrollArea`, à la manière de la maquette : dégradé sur les 28 derniers pixels). Ne pas remplacer
`NavButtons` par `Tabs` dans ce lot — voir §4.
*Dépendances* : T0.1. *Taille* : **M**.
*Critère de fin* : sur une page d'équipe en 1440×900, le premier élément de contenu apparaît à
moins de 220 px du haut du viewport (contre ~300 aujourd'hui) ; le débordement horizontal des
onglets est visuellement signalé.

---

### Lot 4 — Commentaires

---

**T4.1 ☐ — Pagination et compteur de commentaires**
*Fichiers modifiés* : `frontend/src/hooks/useComments.ts` (passer `page`, `size`, `sort` et
`parentId` aux quatre fonctions générées), `frontend/src/components/comment/CommentSection.tsx`,
`frontend/src/components/comment/CommentItem.tsx` (bouton « Voir les N réponses » alimenté par
`CommentDto.replyCount`, chargées par `parentId`), `frontend/src/components/card/PublicationCard.tsx`
et `RouteCard.tsx` (`Stat` avec `IconMessageCircle` + `commentCount`),
`frontend/src/locales/{fr,en}/common.json`.
*Détail* : `size: 20`, `sort: SortDirection.DESC` pour afficher les plus récents d'abord ; bouton
« Charger plus » plutôt qu'une pagination numérotée (le fil de commentaires n'est pas une vue
partageable). Le total vient de `CommentListResponse.itemTotal`. Le contrat garantit que
l'omission simultanée de `page` et `size` conserve l'ancien comportement : la migration peut se
faire endpoint par endpoint.
*Dépendances* : aucune. *Taille* : **M**.
*Critère de fin* : une sortie à plus de 20 commentaires n'en charge que 20 au premier rendu ; le
compteur du titre vient de `itemTotal` et non de `comments.length` ; les cartes de liste affichent
le nombre de commentaires.

---

### Lot 5 — Responsive et rupture du silo

---

**T5.1 ☐ — Fil d'Ariane et raccourcis d'onglets en mobile**
*Fichiers modifiés* : `frontend/src/components/common/Breadcrumb.tsx`.
*Détail* : remplacer le `visibleFrom="sm"` par un rendu compact en dessous de `sm` : `Breadcrumbs`
Mantine avec les deux derniers niveaux et un `Menu` (`IconDots`) reprenant les `subRouteIds` du
niveau — le mécanisme existe déjà en desktop, il suffit de ne plus le masquer.
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : en 390 px de large, sur `équipe → parcours → parcours X`, le fil affiche au
moins l'équipe et la section, et le menu d'onglets est atteignable.

---

**T5.2 ☐ — Rattacher les pages de détail au contexte d'équipe**
*Fichiers modifiés* : `frontend/src/pages/ride/RideDetailPage.tsx`,
`frontend/src/pages/post/PostDetailPage.tsx`, `frontend/src/pages/trip/TripDetailPage.tsx`,
`frontend/src/pages/ad/AdDetailPage.tsx`, `frontend/src/components/team/TeamLayout.tsx`
(variante `compact` sans onglets).
*Détail* : ne **pas** monter ces pages dans `TeamLayout` (elles perdraient leur mise en page à deux
colonnes) : ajouter en tête un bandeau d'équipe cliquable réutilisant `TeamAvatar` + nom +
`IconChevronRight`, exactement comme `CardTeamLink` le fait dans les cartes. C'est l'équivalent web
du bandeau d'équipe de toutes les maquettes de détail.
*Dépendances* : T5.1. *Taille* : **M**.
*Critère de fin* : depuis une sortie ouverte via l'accueil, un mobile atteint la page d'équipe en un
clic.

---

**T5.3 ☐ — `StageTabs` horizontal en mobile**
*Fichiers modifiés* : `frontend/src/components/trip/StageTabs.tsx`,
`frontend/src/components/trip/TripLayout.tsx`, `frontend/src/index.css` (si une règle
`.trip-layout-content` doit accompagner le changement).
*Détail* : `Tabs orientation` dérivée d'un `useMediaQuery` Mantine (`(min-width: 64em)`) →
`vertical` en desktop, `horizontal` + `ScrollArea` en dessous. `useMediaQuery` s'exécute côté
client : fournir une valeur initiale sûre (`{ getInitialValueInEffect: true }`) pour ne pas casser
la parité d'hydratation SSR.
*Dépendances* : aucune. *Taille* : **S**.
*Critère de fin* : sur un voyage de 10 étapes en 390 px, le contenu de l'étape commence avant le
premier tiers de l'écran.

---

**T5.4 ☐ — Compléments d'équipe et de voyage**
*Fichiers* : `frontend/src/pages/team/TeamAboutPage.tsx` (modifié — rangée de trois statistiques
cliquables : `N membres`, `N sorties à venir` via `TeamDetailDto.upcomingRideCount`, `N parcours`
via `TeamDetailDto.routeCount`), `contracts/routes.yaml` + `frontend/src/config/routes.config.ts`
(nouvelle route publique de trombinoscope, hors `/admin`),
`frontend/src/components/team/TeamMemberList.tsx` (réutilisé en lecture seule),
`frontend/src/pages/trip/TripDetailPage.tsx` (modifié — `totalDistance`, `totalElevationGain`,
`endDate` en `StatGroup` ; participants en `UserAvatarGroup` + modale, comme les sorties).
*Détail* : le trombinoscope public nécessite une entrée `teamMembersPublic` dans
`contracts/routes.yaml` puis `pnpm generate-routes` — jamais d'édition de `paths.generated.ts`.
*Dépendances* : T0.4. *Taille* : **L**.
*Critère de fin* : « N membres » de la page À propos est cliquable et mène à une liste paginée
accessible à un membre non-admin ; le détail d'un voyage affiche distance totale, D+ cumulé et date
de fin.

---

**T5.5 ☐ — Compléments d'annonces (optionnel)**
*Fichiers* : `frontend/src/hooks/filters/adFilters.ts` (modifié : `minPrice`, `maxPrice`, `sortBy`
(`AdSortBy`), `sortDir`, avec alias courts), `frontend/src/pages/ad/AdListPage.tsx` (modifié —
`RangeInput` de prix + `Select` de tri), `frontend/src/pages/ad/AdDetailPage.tsx` (modifié —
`Carousel` Mantine sur `AdDto.images`, auteur via `createdByDisplayName`, carte de localisation via
`locationGeometry`).
*Détail* : `@mantine/carousel` n'est pas dans les dépendances actuelles ; si l'ajout n'est pas
souhaité, composer une galerie avec `Image` + `Group` de vignettes, sans nouveau paquet.
**La carte de localisation rend un secteur, pas une punaise** : `AdDto.locationGeometry` est le
centre d'une cellule d'environ 1 km, donc un `circle` MapLibre (couche `circle` au rayon
correspondant, ou un polygone de cercle en source GeoJSON) en remplissage translucide de la couleur
primaire avec un contour de 1 px — jamais un `Marker` ni une couche `symbol`. Le cadrage se règle
sur l'emprise du cercle, jamais sur un zoom élevé qui suggérerait une adresse. Une légende explicite
sous la carte (« Position approximative, à environ 1 km près ») évite l'interprétation erronée.
*Dépendances* : T0.1. *Taille* : **M**.
*Critère de fin* : les filtres de prix et le tri sont dans la query string et partageables ; le
détail d'annonce affiche la galerie et l'auteur ; la carte affiche un disque et aucun marqueur
ponctuel, dans les deux thèmes.

---

**T5.6 ☐ — Préférences persistées côté serveur** (la partie thème/langue/unités reste optionnelle ;
l'interrupteur `contactableByMembers` ne l'est pas, il conditionne T5.7)
*Fichiers modifiés* : `frontend/src/store/preferencesStore.ts`,
`frontend/src/components/common/{ColorSchemeSwitcher,LanguageSwitcher,UnitSystemSwitcher}.tsx`,
`frontend/src/pages/auth/UserProfilePage.tsx`, `frontend/src/locales/{fr,en}/common.json`.
*Détail* : `PATCH /api/users/me/preferences` (hook `useUpdateMyPreferences`,
`UserPreferencesRequest { unitSystem, theme, language, contactableByMembers }`) devient la source de
vérité pour un utilisateur connecté ; `localStorage` reste le repli anonyme et le cache de premier
rendu. `UserDto` porte désormais `language`, `theme` et `contactableByMembers`. Le web ne traitait
jusqu'ici **aucune** préférence côté serveur (`preferencesStore` est purement local) : c'est donc
ici, et nulle part ailleurs, que se branche le premier `useUpdateMyPreferences`.
`contactableByMembers` s'ajoute au profil en `Switch` Mantine — libellé « Recevoir les messages des
membres au sujet de mes annonces », description rappelant que l'annonce reste visible mais cesse
d'être joignable et que l'adresse n'est jamais publiée. Le champ est requis sur `UserDto` et vaut
`true` pour un compte antérieur à la préférence : l'interrupteur part donc coché. Envoi en `PATCH`
partiel (n'envoyer que le champ modifié), avec invalidation de `getGetMeQueryKey`. Attention SSR :
la lecture de `localStorage` au niveau module reste interdite (`frontend/CLAUDE.md`, invariants SSR).
*Dépendances* : aucune. *Taille* : **M**.
*Critère de fin* : un changement de thème sur le web est repris par l'app mobile après
reconnexion, et réciproquement ; décocher l'interrupteur puis tenter d'écrire à une annonce de ce
membre depuis un autre compte renvoie `AD_CONTACT_OPTED_OUT`.

---

**T5.7 ☐ — « Contacter le vendeur » sur le détail d'annonce**
*Fichiers* : `frontend/src/components/ad/AdContactModal.tsx` (créé),
`frontend/src/components/ad/index.ts` (modifié), `frontend/src/pages/ad/AdDetailPage.tsx` (modifié),
`frontend/src/lib/apiError.ts` et `frontend/src/lib/axiosInstance.ts` (modifiés — voir plus bas),
`frontend/src/locales/{fr,en}/common.json`.
*Détail* : le contrat `1.4.0` livre `POST /api/teams/{teamSlug}/classifieds/{slug}/contact`, corps
`AdContactRequest { message }` (10 à 2000 caractères, `pattern: \S`), réponse **204 sans corps**.
Le serveur envoie l'e-mail à l'auteur et pose `Reply-To` sur l'expéditeur : **aucune adresse
n'apparaît dans l'API**, `AdDto` ne porte aucun champ de contact, et c'est le point de la
conception — ne pas chercher d'adresse à afficher, il n'y en a pas. L'accès est celui de la lecture
de l'annonce (membre de l'équipe).

Composition : un `Button leftSection={<IconMail size={16}/>}` « Contacter le vendeur » dans la barre
d'actions du détail, masqué pour l'auteur de l'annonce (`AdDto.createdById === user.id`) et pour un
visiteur non membre. Il ouvre un `Modal` Mantine contenant un `Textarea autosize minRows={4}`
piloté par `useForm` (`@mantine/form`) et validé sur le schéma généré `ContactAdAuthorBody` de
`src/api/zod/ads/ads.zod.ts` (constantes `contactAdAuthorBodyMessageMin` / `…Max`, plus
`contactAdAuthorBodyMessageRegExp` qui interdit un message tout en blancs), avec un compteur
« n / 2000 » alimenté par ces mêmes constantes en
`Text size="xs" c="dimmed"` sous le champ et l'envoi désactivé tant que la longueur est hors bornes.
Un `Alert variant="light"` posé **au-dessus du champ, avant l'envoi**, énonce le contrat de
divulgation : le message part par e-mail vers le vendeur, qui verra l'adresse de l'expéditeur et
pourra y répondre directement, tandis que la sienne reste inconnue. Mutation via le hook Orval
généré `useContactAdAuthor` (`src/api/endpoints/ads/ads.ts`) — pas d'édition de `src/api/`.

Les quatre issues, traitées explicitement et discriminées sur `ErrorResponse.code` plutôt que sur le
seul statut :

| Issue | Rendu |
|---|---|
| **204** | fermeture de la modale + `notifications.show` de succès « Message envoyé » ; le champ est vidé |
| **`AD_CONTACT_OPTED_OUT`** | la modale reste ouverte, `Alert color="orange"` : ce membre a choisi de ne pas recevoir de messages ; l'envoi reste désactivé |
| **`AD_CONTACT_RATE_LIMITED`** (429) | `Alert color="orange"` citant le délai d'attente lu sur l'en-tête `Retry-After`, formaté en minutes ; le brouillon est **conservé** |
| **`AD_CONTACT_DELIVERY_FAILED`** (500) | `Alert color="red"` : le message n'est pas parti, réessayer plus tard ; le brouillon est **conservé** et le bouton reste actif |

`AD_CONTACT_SELF` n'a pas de rendu propre : le bouton n'est pas affiché à l'auteur. Repli générique
sur tout autre code. Le quota est de **10 messages par heure et par expéditeur, toutes annonces
confondues** — un membre qui peut lire les annonces peut écrire à toutes, donc un plafond par
annonce ne plafonnerait rien : la micro-copie du cas 429 ne doit surtout pas laisser croire que
c'est l'annonce qui est bloquée. `AD_CONTACT_SELF` et `AD_CONTACT_OPTED_OUT` arrivent en
`400` : ce sont des règles sur le message, pas des refus d'autorisation — l'appelant a bien le
droit d'utiliser cet endpoint sur cette annonce. Discriminer sur `code`, jamais sur le statut, qui
est partagé avec les erreurs de validation.

Deux points techniques à ne pas manquer. D'abord `ApiClientError` (`src/lib/apiError.ts`) ne porte
aujourd'hui que `status` et `error` : l'en-tête `Retry-After` est **perdu** par `axiosMutator`
(`src/lib/axiosInstance.ts:157-172`). Il faut lui ajouter un `retryAfterSeconds?: number` lu sur
`axiosError.response?.headers['retry-after']`, sans quoi le message de quota ne peut pas citer de
délai. Cet en-tête est posé par le backend (`GlobalExceptionMapper`) et **déclaré sur la réponse
`429` de `contactAdAuthor`** ; le lire tout de même de façon défensive, avec repli sur un message
sans délai, car les autres endpoints à quota ne le déclarent pas encore. Ensuite,
`axiosMutator` déclenche déjà un toast global pour toute erreur portant un `code` : les quatre clés
`errors.api.AD_CONTACT_*` doivent donc exister dans `fr` **et** `en`, sinon la clé brute s'affiche en
plus de l'`Alert` de la modale. Ne pas désactiver le toast global (même arbitrage qu'en T2.4).
Toutes les clés de la tâche (`ads.contact.button`, `.title`, `.disclosure`, `.counter`, `.success`,
et les quatre `errors.api.AD_CONTACT_*`) sont **plates**, ajoutées dans les deux locales, puis
`pnpm i18n:extract` et `pnpm i18n:lint`.

*Dépendances* : T5.6 (l'interrupteur `contactableByMembers` doit exister pour qu'un membre puisse
sortir du relais). T5.5 touche le même fichier de détail d'annonce : à séquencer avec, mais ce n'est
pas une dépendance — T5.7 se livre seule si T5.5 glisse.
*Taille* : **M**.
*Critère de fin* : un membre écrit à une annonce et reçoit un 204 puis une confirmation ; l'auteur
reçoit l'e-mail et son « Répondre » vise l'expéditeur ; le bouton n'apparaît pas sur sa propre
annonce ; les trois cas d'erreur affichent un message distinct **dans la modale** sans perdre le
brouillon ; aucune adresse e-mail n'apparaît dans une réponse d'API ni dans le DOM.
*Exploitation* : les gabarits Brevo du message (`ad-contact.fr` / `ad-contact.en`) sont créés et
l'envoi a été validé par un message réel — rien ne bloque la recette de bout en bout.
d'exploitation à faire avant la mise en service, pas une tâche de code — mais elle bloque la recette
de bout en bout, et un `AD_CONTACT_DELIVERY_FAILED` systématique en recette ne signifie donc pas
forcément un défaut du front.

---

### Ordre recommandé

`T0.1 → T0.2 → T0.3 → T0.4 → T0.5` puis, en parallèle possible :
lot 1 (`T1.1 … T1.7`) et lot 3 (`T3.1 → T3.3 → T3.4 → T3.5`).
Le lot 2 suit le lot 0 (`T2.1`, `T2.3`, `T2.5`) sauf `T2.2` qui attend `T3.2`, et `T2.4` qui suit
`T1.6`. Les lots 4 et 5 sont indépendants et peuvent être décalés sans bloquer quoi que ce soit —
à une contrainte interne près : `T5.6` précède `T5.7`, l'interrupteur d'opt-out doit exister avant
qu'on ouvre le relais de contact.

`T0.1` seul est le meilleur premier commit du chantier : une ligne de CSS, un effet visible sur
toutes les pages.

---

## 4. Ce qu'il ne faut pas porter

**Les gabarits tactiles.** `DraggableScrollableSheet` à trois crans, feuilles modales à poignée,
barre d'onglets basse à cinq entrées, app bar interpolée de 120 à 56 px, chips horizontales à fondu
de bord en remplacement des `Select`. Ces dispositifs résolvent une contrainte que le desktop n'a
pas (un pouce, 402 px de large, pas de survol). Les transposer produirait des composants maison hors
Mantine — exactement ce que `frontend/CLAUDE.md` interdit — pour un gain nul.

**Le minimum de 44 px sur les boutons en desktop.** `pedalons.css` impose `min-height: 44px` y
compris sur `.btn--sm`, avec un commentaire explicite. C'est une règle **tactile**. Le web doit au
contraire descendre à 36 px au-dessus de 768 px (T0.1). Ne pas prendre la maquette pour une
spécification web sur ce point.

**Le système de jetons `--pdl-*`.** La palette, les rayons, les ombres et les dégradés de
`pedalons.css` sont une reformulation de la charte pour un moteur HTML de maquettage. Le site a déjà
la même charte, exprimée en thème Mantine (`virtualColor`, `autoContrast`, variables
`--mantine-*`) et en tokens dérivés (`badgeColors.ts`, `MAP_HEIGHT_CSS`, `getOverlayBg`). Introduire
une seconde couche de variables créerait deux sources de vérité. Le seul travail de couleur à faire
côté web est la correction des `gray-1/3/5` (T0.5).

**Le mode sombre dérivé.** Le tableau de parité clair/sombre de `design-system.md` §A.13 est un
livrable **pour Flutter**, parce qu'aucune maquette ne fournit le mode sombre alors que le §5 du
brief l'exige. Le web a déjà un thème sombre fonctionnel via Mantine ; il n'a rien à dériver.

**Le scroll infini par curseur.** Le brief §4.1 et §4.4 le demandent pour le mobile, mais la
pagination par curseur (`cursor`/`nextCursor`) **n'a pas été livrée** avec l'API v2. Surtout, le
scroll infini est incompatible avec la règle structurante du frontend : filtres et pagination
vivent dans la query string, donc toute vue est partageable et le retour arrière restaure l'état
exact. `usePaginatedQuery` précharge déjà la page suivante **et** la précédente, ce qui rend la
pagination quasi instantanée. Ne pas remplacer un pattern supérieur par un pattern inférieur.

**Le remplacement de `NavButtons` par `Tabs`.** L'inventaire signale à juste titre que `NavButtons`
n'a ni sémantique de `tablist` ni navigation clavier fléchée. Mais il porte le pattern visuel
identitaire du site (carré 48 + libellé) sur toutes les pages Accueil et Équipe : le réécrire est un
chantier de design, pas un portage. T3.6 se limite à réduire sa hauteur et à signaler le
débordement ; la refonte sémantique est une tâche à instruire séparément.

**Le carrousel horizontal « À venir ».** L'idée (voir les sorties des 30 prochains jours) mérite le
portage ; le carrousel non. En desktop, un `SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }}` de trois à
six cartes est plus lisible qu'un défilement horizontal, et n'introduit pas de dépendance
supplémentaire. T1.1 et T1.5 couvrent le besoin.

**Les notifications.** Cloche de non-lus, préférences par catégorie, rappel J-1 : aucun endpoint
n'existe (`POST /api/users/me/devices`, `…/notifications`, `…/notification-preferences` sont des
propositions du brief, pas des livrables). Ne rien maquetter, ne rien câbler.

**La liste d'attente.** Le champ `waitlisted` du brief §3.2 n'a **pas** été livré et il n'existe
aucune liste d'attente en base. Le portage se limite à `registered`, `registeredGroupId` et `full`.

**Le repli sur `createdBy` pour le meneur.** `RideGroup.createdBy` vaut le créateur de la **sortie**
pour **tous** ses groupes : s'en servir de repli afficherait le même nom comme meneur dans chaque
groupe, donc à tort presque partout. C'est exactement le défaut que la colonne `leader_id` corrige,
et un repli le réintroduirait sous un nom plus crédible. La règle vaut partout où la pastille
apparaît : `leader` nul ⇒ **rien** (T1.7). Ne pas non plus étendre la pastille au fil ni au
calendrier : le meneur est une propriété du **groupe**, et ni `PublicationCard` ni
`CalendarEventDto` n'en portent.

**Le sélecteur de meneur dans le formulaire de sortie.** L'affichage du meneur est à porter (T1.7),
son **attribution** non. `GroupRequest.leaderId` (TSID en chaîne, optionnel) est acceptée depuis le
contrat `1.5.0` et `RideEditor.tsx` édite bien les groupes d'une sortie, mais ce document porte sur
la consultation et la participation : il ne décrit aucune autre tâche de formulaire, et n'ouvre pas
celle-ci. Un `Select` de meneur suppose la liste des membres de l'équipe dans l'éditeur, la gestion
du `null` explicite (envoyer `null` **efface** la désignation — c'est une opération réelle, pas une
omission) et le rendu du `400 RIDE_GROUP_LEADER_NOT_MEMBER`, renvoyé quand la personne désignée
n'appartient pas à l'équipe propriétaire de la sortie. C'est un lot d'édition à instruire
séparément, avec les autres champs de `RideEditor`.

**Le meneur sur les gabarits de sortie.** Décision produit : les gabarits n'en ont pas.
`RideTemplateGroupRequest` est un type de requête distinct de `GroupRequest` et ne gagne aucun champ
de meneur ; instancier une sortie depuis un gabarit ne désigne personne. Ne prévoir ni champ, ni
colonne, ni pastille de meneur dans les vues de gabarit.

**Les voyages au calendrier.** `CalendarEventType` reste `RIDE | TRIP_STAGE` : les étapes y sont, le
voyage en tant qu'objet non. Le brief §3.5 le demandait ; ce n'est pas livré.

**La carte multi-entités.** `GET /api/map/features?bbox&types=…` n'existe pas. Toute idée de carte
mixte (sorties + parcours + lieux + annonces) est hors périmètre.

**ETag, images signées, blurHash.** Non livrés. Ne pas construire de stratégie de cache client
au-dessus d'hypothèses.

---

## 5. Dépendances non livrées et angles morts

| Idée de la v2 | Bloquée par | Dégradation acceptable |
|---|---|---|
| Anneau « inscrit » sur la **cellule de jour** du calendrier | `@mantine/schedule` n'expose pas de rendu de cellule de jour | marquer l'**événement** (filet primaire) plutôt que le jour — retenu en T1.3 |
| Voyages (et non seulement étapes) au calendrier | `CalendarEventType` | ne rien afficher, ne pas simuler |
| Compte à rebours « dans 3 jours » sur la carte du fil | aucun blocage — dérivable de `dateTime` | — |
| Aperçu de la levée de filtre sur les **publications** | pas de `count` par filtre côté publications hors `…/publications/count` (qui existe) | faisable ; non retenu faute de valeur — le fil a peu de filtres |
| Métriques de **groupe** dans « Ma prochaine sortie » quand `registeredGroupId` est nul | rien côté API | retomber sur les métriques de la sortie |
| Trombinoscope public | route à créer dans `contracts/routes.yaml` ; vérifier que `GET /api/teams/{teamSlug}/members` est autorisé à un membre non-admin | si l'autorisation est réservée aux admins, le lien « N membres » reste non cliquable pour les autres — à confirmer côté backend avant T5.4 |
| Préférence de thème / langue synchronisée | livré (`PATCH /api/users/me/preferences`, `UserDto.language`, `UserDto.theme`) | — |
| Contact du vendeur d'une annonce | livré en `1.4.0` (`POST …/classifieds/{slug}/contact`, `contactableByMembers`) ; gabarits Brevo créés et envoi validé par un message réel | aucune |
| Délai exact du quota de contact (429) | `Retry-After` est posé par le backend mais **non déclaré dans le contrat**, et `ApiClientError` ne conserve pas les en-têtes | lire l'en-tête défensivement (T5.7) ; à défaut, message de quota sans délai chiffré |
| Pastille de meneur d'un groupe | livré en `1.5.0` (`RideGroupDto.leader`, `PublicUserDto` nullable ; `GroupRequest.leaderId` en écriture) ; les gabarits n'en auront pas (`RideTemplateGroupRequest` inchangé) | aucune pour l'affichage (T1.7) : `leader` nul ⇒ rien, et c'est le cas courant, pas le cas dégradé ; jamais de repli sur `createdBy` (§4) |
| Position exacte d'une annonce | volontaire : `AdDto.locationGeometry` est floutée à ~1 km, le point exact ne sort que sur `AdEditDto` | rendre un secteur (cercle) et non une punaise (§1.8, T5.5) |

Deux points d'attention transverses à ne pas oublier au moment d'écrire le code :

- **SSR.** Tout ce qui dépend de l'utilisateur connecté (participations, badge `Inscrit`, section de
  profil) se rend **après hydratation** : le serveur ne transmet ni cookie ni `Authorization`. Ces
  blocs ne doivent jamais figurer dans le `prefetch` d'une route publique, et leurs hooks doivent
  porter `enabled: isAuthenticated`. Lire `frontend/SSR.md` avant de toucher `HomePage`,
  `routes.config.ts` ou `entry-server.tsx`.
- **i18n.** Chaque tâche ajoute ses clés **plates avec des points** dans
  `frontend/src/locales/fr/common.json` **et** `frontend/src/locales/en/common.json`, avec
  `_one`/`_other` et `{{count}}` pour les compteurs, puis `pnpm i18n:extract` et `pnpm i18n:lint`.
  Toute clé templatée (badges de statut, codes d'erreur d'inscription) exige une annotation
  `satisfies`. Enfin, `./format.sh frontend` avant chaque commit.
