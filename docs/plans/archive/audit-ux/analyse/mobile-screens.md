# Pédalons — Application mobile actuelle (Flutter)

Document d'entrée de design. Décrit l'état **réel du code** de `mobile/` (Flutter, Riverpod, GoRouter,
easy_localization), périmètre **consultation / participation** uniquement.
Hors périmètre volontairement omis : administration (`/plateforme`, `/equipes/*/admin`), création et
édition (sorties, parcours, articles, annonces, pages d'équipe, modèles), outils GPX.

Racine du code : `/Users/glandais/code/perso/tribly/mobile/lib/`
Contrat de routes : `/Users/glandais/code/perso/tribly/contracts/routes.yaml` (les entrées `mobile: true`
génèrent `mobile/lib/config/paths.generated.dart`).

**16 écrans en périmètre** (+ 5 écrans d'authentification / appairage d'appareil, décrits en fin de
document pour mémoire).

---

## Sommaire

| # | Écran | Route (fr) | Fichier |
|---|-------|-----------|---------|
| 1 | Accueil / feed global | `/` | `features/home/presentation/pages/home_page.dart` |
| 2 | Mes équipes | `/equipes` | `features/teams/presentation/pages/teams_page.dart` |
| 3 | Fil d'équipe | `/equipes/{teamSlug}` | `features/teams/presentation/pages/team_feed_page.dart` |
| 4 | Calendrier d'équipe (onglet) | `/equipes/{teamSlug}/calendrier` | `config/router.dart` (`_TeamCalendarTab`) + `features/calendar/.../calendar_page.dart` |
| 5 | Parcours d'équipe (onglet) | `/equipes/{teamSlug}/parcours` | `config/router.dart` (`_TeamRoutesTab`) + `features/routes/.../routes_page.dart` |
| 6 | Petites annonces | `/equipes/{teamSlug}/annonces` | `features/ads/presentation/pages/ads_page.dart` |
| 7 | À propos de l'équipe | `/equipes/{teamSlug}/a-propos` | `features/teams/presentation/pages/team_about_page.dart` |
| 8 | Détail sortie | `/equipes/{teamSlug}/sorties/{rideSlug}` | `features/rides/presentation/pages/ride_detail_page.dart` |
| 9 | Détail parcours | `/equipes/{teamSlug}/parcours/{routeSlug}` | `features/routes/presentation/pages/route_detail_page.dart` |
| 10 | Détail article | `/equipes/{teamSlug}/articles/{postSlug}` | `features/posts/presentation/pages/post_detail_page.dart` |
| 11 | Détail voyage | `/equipes/{teamSlug}/voyages/{tripSlug}` | `features/trips/presentation/pages/trip_detail_page.dart` |
| 12 | Détail étape | `/equipes/{teamSlug}/voyages/{tripSlug}/etapes/{stageSlug}` | `features/trips/presentation/pages/stage_detail_page.dart` |
| 13 | Détail annonce | `/equipes/{teamSlug}/annonces/{adSlug}` | `features/ads/presentation/pages/ad_detail_page.dart` |
| 14 | Calendrier global | `/calendrier` | `features/calendar/presentation/pages/calendar_page.dart` |
| 15 | Profil / préférences | `/profil` | `features/profile/presentation/pages/profile_page.dart` |
| 16 | Pages légales | `/confidentialite`, `/cgu` | `features/legal/presentation/pages/legal_page.dart` |

---

## Shell & navigation

Fichiers : `lib/config/router.dart`, `lib/features/navigation/presentation/shell/main_shell.dart`,
`lib/core/adaptive/adaptive_scaffold.dart`, `lib/core/adaptive/navigation_destination.dart`,
`lib/features/teams/presentation/shell/team_shell.dart`,
`lib/features/teams/presentation/shell/team_navigation_destination.dart`.

### Deux shells superposés

L'app a **deux barres de navigation basses distinctes** qui ne coexistent jamais :

1. **`MainShell`** (`ShellRoute` racine) — la nav globale. 4 destinations fixes
   (`kAppDestinations`) :
   | Index | Libellé (clé i18n) | Icône / icône sélectionnée | Route |
   |---|---|---|---|
   | 0 | `nav.home` | `home_outlined` / `home` | `/` |
   | 1 | `nav.teams` | `group_outlined` / `group` | `/equipes` |
   | 2 | `nav.calendar` | `calendar_today_outlined` / `calendar_today` | `/calendrier` |
   | 3 | `nav.profile` | `person_outlined` / `person` | `/profil` |

   L'onglet actif est déduit du chemin (`getDestinationIndex`), en testant **toutes les variantes
   de langue** ; l'accueil (`/`) est le repli par défaut. Les pages du shell utilisent
   `NoTransitionPage` (pas d'animation entre onglets).

2. **`TeamShell`** (second `ShellRoute`, un sous-arbre par locale) — la nav **dans** une équipe.
   Elle **remplace** la nav globale : une fois entré dans une équipe, les onglets Accueil /
   Calendrier / Profil disparaissent, remplacés par les onglets d'équipe. Le retour se fait par la
   flèche « ← » du `TeamSliverAppBar`, qui fait `context.go(Paths.teams())`.

   Destinations construites dynamiquement depuis le `TeamDetailDto` (`buildTeamDestinations`) :
   | Onglet | Condition d'affichage | Icône | Route |
   |---|---|---|---|
   | Fil (`teams.tabs.feed`) | toujours | `dynamic_feed` | `/equipes/{slug}` |
   | Calendrier | **membre** ET (`enableRides` ou `enableTrips`) | `calendar_today` | `.../calendrier` |
   | Parcours | `enableRoutes` | `route` | `.../parcours` |
   | Annonces | **membre** ET `enableAds` | `sell` | `.../annonces` |
   | À propos | toujours | `info` | `.../a-propos` |

   Donc la barre d'équipe compte **entre 2 et 5 onglets** selon la config de l'équipe et le rôle.
   `TeamShell` charge `teamDetailProvider(slug)` et affiche un `Scaffold` + `CircularProgressIndicator`
   pendant le chargement, un état d'erreur avec bouton « Réessayer » sinon.

Les pages de détail (sortie, parcours, article, voyage, étape, annonce) sont déclarées avec
`parentNavigatorKey: _rootNavigatorKey` : elles s'affichent **plein écran, sans barre basse**,
empilées par-dessus le shell.

### Adaptatif

`AdaptiveScaffold` et `_TeamShellContent` commutent selon la largeur (`Breakpoints`, Material 3) :

- `< 600 px` (compact) → `NavigationBar` en bas ;
- `600–840 px` (medium) → `NavigationRail` latéral avec libellés ;
- `≥ 840 px` (expanded) → `NavigationRail` étendu.

`ContentWidthConstraint` (`core/adaptive/content_width_constraint.dart`) centre et borne le contenu :
pleine largeur en compact, 720 px en medium, 960 px en expanded. Il est utilisé quasi partout dans les
slivers. `Breakpoints.gridColumns` donne 1 / 2 / 3 colonnes.

### Aucun FAB

**Aucun écran de l'app ne possède de FloatingActionButton.** C'est la conséquence directe du fait que
la création (sortie, parcours, article, annonce) n'existe pas sur mobile.

### Routage, deep links et auth

- `routerProvider` construit le `GoRouter` **une seule fois** ; les changements d'auth passent par
  `refreshListenable` et relancent seulement `redirect`.
- `redirect` : si non authentifié et route non « auth-adjacent » (login, inscription, vérification
  email, mots de passe, confidentialité, CGU) → `/connexion`. Si authentifié sur `/connexion` → `/`.
- Toutes les variantes de langue de chaque route sont enregistrées (`_perLocale`), donc un lien `fr`
  ouvre la même page qu'un lien `en`.
- Deep links (`app_links`) : `main.dart` attend que l'auth soit initialisée **et** que le router ait
  parsé sa première route, puis reconstruit une pile d'ancêtres (`_deepLinkHierarchies` /
  `ancestorsForDeepLink`) pour que « retour » remonte la hiérarchie logique
  (ex. annonce → `/equipes` → `/equipes/{slug}` → `/equipes/{slug}/annonces` → annonce).
- Page 404 : `errorBuilder` inline dans `router.dart`, avec deux chaînes **codées en dur en français**
  (« Erreur », « Page non trouvée: … », « Retour à l'accueil ») — seul endroit non traduit de l'app.

---

## Thème

Fichiers : `lib/app.dart`, `lib/core/theme/pedalons_theme.dart`, `lib/core/theme/pedalons_colors.dart`.

- `MaterialApp.router` avec `theme` + `darkTheme` ⇒ **suit le thème système**, aucun sélecteur clair/sombre
  dans l'app.
- Material 3 (`useMaterial3: true`), `ColorScheme.fromSeed(seedColor: BrandColors.indigoLight #4c6ef5)`.
  Le bleu de marque `#228be6` n'est **pas** la seed : la couleur primaire réelle est un indigo dérivé.
- Typo : `GoogleFonts.interTextTheme` (Inter) appliqué sur le text theme clair ou sombre.
- Transitions de page : `CupertinoPageTransitionsBuilder` sur **Android comme iOS** (glissement latéral
  façon iOS partout).
- `AppBarTheme` : titre centré, `elevation: 0`, fond `colorScheme.surface`.
- `CardThemeData` : `elevation: 1`, rayon 12.
- `InputDecorationTheme` : rempli, `surfaceContainerHighest` à 30 % d'opacité, bordure arrondie 12.
- `FilledButton` / `OutlinedButton` : rayon 12.
- `BrandColors` (`pedalons_colors.dart`) reprend la palette Mantine (shade-6 clair / shade-8 sombre) :
  blue, indigo, grape, teal, green, orange, red, yellow, gray, violet, cyan, pink, dark. Utilisée
  ponctuellement (couleurs de carte notamment) mais **pas** pour dériver le `ColorScheme`.
- Couleurs carte : trace `#228be6` (clair) / `#4dabf7` (sombre), marqueur départ `#40c057`,
  arrivée `#fa5252`.
- Fonds de carte : styles VersaTiles distants — `colorful` en clair, `eclipse` en sombre
  (`https://tiles.versatiles.org/assets/styles/{style}/style.json`).
- Sélection clair/sombre des vignettes : chaque entité expose `thumbnailLightUrl` / `thumbnailDarkUrl`
  (ou `media.assets.thumbnailLight/Dark`) et la carte choisit selon `Theme.of(context).brightness`.

### Composants transverses

- `AnimatedCard` — `Card` avec retour tactile (scale down au press). Base de toutes les cartes cliquables.
- `StaggeredListView` / `StaggeredSliverList` / `StaggeredGridView` / `StaggeredListItem` — entrée en
  cascade des éléments de liste.
- `AnimatedEmptyState` — icône flottante des états vides.
- `ShimmerPlaceholder`, `ShimmerCard`, `ShimmerCardList`, `ShimmerEventCard`, `ShimmerTeamCard`,
  `ShimmerRouteGridItem` — squelettes de chargement.
- `Hero` — logo d'équipe (`team-logo-{slug}`), vignette de parcours (`route-thumbnail-{slug}`),
  vignette de sortie (`ride-thumbnail-{slug}`).
- `MarkdownContent` — rendu markdown thémé (`markdown_widget`), résolution des directives
  `::asset{id="…" size="…" alt="…"}` en images, images chargées via `AuthenticatedImage`.
  Ne scrolle jamais (le parent possède le scroll, pour préserver le tap-status-bar iOS).
- `AuthenticatedImage` / `AuthenticatedCircleAvatar` / `AuthenticatedDecorationImage` — images
  protégées par le jeton d'accès.
- `TeamBanner` — pastille logo + nom d'équipe + chevron, cliquable vers l'équipe. Présent en tête des
  écrans de détail (sortie, article, voyage, étape, annonce). **Le logo y est toujours `null`** (initiales
  seulement) : le DTO `TeamPublicationDto` ne porte pas d'URL de logo.
- `BackOrHomeButton` — flèche retour si empilable, sinon icône maison (pages hors shell).
- `PagedListFooter` — pied de liste infinie : squelette + « chargement… », erreur avec « Réessayer »,
  ou « fin de liste ».
- `PagedListNotifier` (`core/pagination/`) — pagination générique : page 0 au montage, 20 items par
  page, préchargement à 3 items de la fin, une requête en vol à la fois, déduplication par `itemKey`,
  pull-to-refresh qui ne vide pas la liste en cas d'échec.

---

## 1. Accueil / feed global

**Route** `/` (mobile + deeplink) — **Fichier** `lib/features/home/presentation/pages/home_page.dart`
(+ `lib/features/feed/presentation/widgets/publication_feed_view.dart`,
`lib/features/feed/providers/publication_feed_provider.dart`,
`lib/features/teams/presentation/widgets/publication_card.dart`).

### Structure

`Scaffold` → `PublicationFeedView(teamSlug: null)` → `RefreshIndicator` → `CustomScrollView` :

1. `SliverAppBar` `expandedHeight: 120`, `pinned`, `FlexibleSpaceBar` avec titre
   `home.greeting` (« Bonjour {prénom} », prénom = premier mot de `user.displayName`) sur un dégradé
   vertical `primary → primaryContainer`.
2. (Conditionnel) Carte d'incitation aux **passkeys** si `!authState.hasPasskeys` : icône empreinte,
   titre `auth.passkey.simplifiedLogin`, sous-titre `auth.passkey.enablePrompt`, bouton texte
   « Activer ».
3. Rangée de `FilterChip` de type de publication, scrollable horizontalement : **Tous / Sorties /
   Articles / Voyages** (icônes `directions_bike`, `article`, `hiking`).
4. Liste des publications (cartes séparées de 8 px) + `PagedListFooter`.
5. Padding bas 32 px.

Barre basse = `MainShell` (onglet 0). Pas de FAB.

### Patterns

- Scroll infini (`PagedListNotifier`, 20/page, préchargement à −3).
- Pull-to-refresh.
- 4 états : squelettes (`ShimmerCardList(itemCount: 5)`), erreur initiale (carte avec message +
  « Réessayer »), vide (`AnimatedEmptyState` + icône `dynamic_feed` + `home.feed.empty`), liste.
- Changement de chip ⇒ nouveau jeu de résultats + remontée en haut de liste.
- Tap sur la status bar iOS ⇒ retour en haut (scrollable primaire sans contrôleur propre).

### Données affichées (`PublicationCard`, 3 variantes)

Carte **Sortie** (`PublicationDtoRide`) : vignette 60×60 (`thumbnailLight/DarkUrl`, repli icône vélo sur
`primaryContainer`) · libellé de type « Sorties » en `primary` · `ride.name` (1 ligne, ellipsée) ·
`formatRideDate(dateTime)` + « • » + `rides.participants {count}` · chevron.
Carte **Article** (`PublicationDtoPost`) : pavé 60×60 `tertiaryContainer` + icône `article` (jamais
d'image) · libellé « Articles » · `post.name` · `formatRideDate(dateTime)` · chevron.
Carte **Voyage** (`PublicationDtoTrip`) : vignette 60×60 · libellé « Voyages » · `trip.name` ·
`formatRideDate(dateTime)` + « • » + `{stageCount} étapes` · chevron.

### Actions

- Tap carte → détail (`Paths.ride` / `Paths.post` / `Paths.trip`), en `push` (plein écran).
- Filtre par type via les chips.
- Pull-to-refresh.
- Activer une passkey (bandeau) → `PasskeyService.register(deviceName: 'Mobile')` + snackbar.

### Plus pauvre que le web

- **Pas de recherche.** Le web (`HomePage.tsx`) a un champ de recherche debouncé (`search`) sur le feed.
- **Pas de filtre d'appartenance** (`minRole` : tous / membre / organisateur / admin) — le mobile
  n'envoie jamais `minRole` sur le feed.
- Le nom d'équipe n'apparaît pas sur les cartes du feed global : impossible de savoir de quelle équipe
  vient une publication sans ouvrir. Le web affiche `showTeam` sur `PublicationCard`.
- Pas de pagination explicite ni de compteur total (le web affiche « N publications » et une pagination
  numérotée préchargée).
- Titre de page (« Bienvenue sur … », sous-titre) remplacé par une simple salutation.

---

## 2. Mes équipes

**Route** `/equipes` — **Fichier** `lib/features/teams/presentation/pages/teams_page.dart`.

### Structure

`Scaffold` + `AppBar` titre `teams.title`, **action loupe (`Icons.search`) dont le `onPressed` est un
`// TODO: Navigate to discover teams` vide**. Corps : `RefreshIndicator` + `AnimatedResponsiveGrid`
(padding 16, `childAspectRatio: 2.5`, 1/2/3 colonnes) de `_TeamCard`.
Barre basse = `MainShell` (onglet 1). Pas de FAB.

### Données (`_TeamCard`)

- Avatar circulaire r=28 avec `Hero(tag: 'team-logo-{slug}')` — image `team.about.assets.logo?.url`,
  repli = première lettre du nom.
- `team.name` (titleMedium gras).
- Icône `people` + `teams.members {count}` (`team.memberCount`).
- Puce de rôle si `team.role != null` : `AppFormatters.roleName(role)` sur `primaryContainer`.
- Chevron.

### États

- Chargement : 3 `ShimmerTeamCard`.
- Vide : icône `group_off`, `teams.empty`, `teams.joinPrompt`, bouton « Découvrir » → **également un
  TODO vide**.
- Erreur : icône, message (`getErrorMessage`), bouton « Réessayer ».

### Actions

- Tap carte → `Paths.team(slug)` (entre dans le `TeamShell`).
- Pull-to-refresh.

### Plus pauvre que le web

- **La liste ne contient que MES équipes** : `TeamRepository.getMyTeams()` appelle
  `listTeams(minRole: MinRole.member)` sans pagination ni recherche. Le web liste **toutes** les équipes
  visibles du domaine, avec recherche, filtre d'appartenance et pagination.
- **La découverte d'équipes n'existe pas** (deux boutons TODO morts) ⇒ impossible de rejoindre une
  nouvelle équipe depuis le mobile, alors que `TeamRepository.joinTeam` / `leaveTeam` existent déjà
  côté data.
- Pas de recherche, pas de pagination, pas de total (`teamsData.total`).
- Pas de bouton « Créer une équipe » (hors périmètre, mais explique l'absence de FAB).

---

## 3. Fil d'équipe

**Route** `/equipes/{teamSlug}` — **Fichier** `lib/features/teams/presentation/pages/team_feed_page.dart`
(25 lignes ; toute la logique est dans `PublicationFeedView`).

### Structure

`_TeamTabPageWrapper` (Scaffold + chargement de `teamDetailProvider`) → `PublicationFeedView(teamSlug:
slug)` avec en sliver de tête le **`TeamSliverAppBar`** :
`SliverAppBar` `expandedHeight: 160`, `pinned`, leading « ← » → `/equipes`, `FlexibleSpaceBar` avec le
nom de l'équipe et, en fond, dégradé `primary → primaryContainer` + logo d'équipe `Hero` (r=36).
Puis, identiques à l'accueil : chips de type, liste paginée, footer.
Barre basse = `TeamShell` (onglet Fil). Pas de FAB.

### Données / actions / états

Strictement les mêmes que l'accueil (composant partagé), message vide `teams.feed.empty`, publications
restreintes à l'équipe.

### Plus pauvre que le web

- Mêmes manques que l'accueil (pas de recherche, pas de pagination, pas de total).
- Le web (`PublicationListPage`) présente en plus un **filtre de statut / brouillons** et le contexte
  d'équipe complet (`TeamLayout` avec onglets et actions).

### Note code

`lib/features/teams/presentation/pages/team_detail_page.dart` (514 lignes) — page « détail d'équipe »
riche (statistiques membres/sorties/parcours, section À propos, 3 prochaines sorties, carrousel de
5 parcours) — **n'est plus routée** : `/equipes/{slug}` affiche `TeamFeedPage`. Seuls ses providers
(`teamDetailProvider`, `teamRidesProvider`, `teamRoutesProvider`) sont encore utilisés. C'est du code
mort à l'écran, mais une maquette existante intéressante.

---

## 4. Calendrier d'équipe (onglet)

**Route** `/equipes/{teamSlug}/calendrier` — **Fichiers** `lib/config/router.dart` (`_TeamCalendarTab`)
et `lib/features/calendar/presentation/pages/calendar_page.dart` (mode `embedded: true`).

### Structure

`Column` : une `AppBar` **non-sliver** portant le nom de l'équipe et une flèche « ← » vers `/equipes`,
puis `CalendarPage(teamSlug: …, embedded: true)`. À noter : contrairement aux autres onglets d'équipe,
il n'y a **pas** de `TeamSliverAppBar` (pas de dégradé, pas de logo) — incohérence visuelle assumée
dans le code.

Le corps est celui du calendrier global (voir §14) mais restreint à l'équipe
(`getTeamCalendarEvents`).

### Plus pauvre que le web

Voir §14. En plus : le web a une page dédiée `TeamCalendarPage` avec l'entête d'équipe complète et le
réglage de **flux ICS** (`IcsFeedSettings`) — totalement absent du mobile.

---

## 5. Parcours d'équipe (onglet)

**Route** `/equipes/{teamSlug}/parcours` — **Fichiers** `lib/config/router.dart` (`_TeamRoutesTab`),
`lib/features/routes/presentation/pages/routes_page.dart` (547 l.), plus
`route_search_bar.dart`, `route_filter_chips_bar.dart`, `route_filter_sheet.dart`,
`route_filter_labels.dart`, `domain/route_filters.dart`, `providers/route_list_provider.dart`.

C'est **l'écran le plus abouti de l'app mobile**, et le seul dont l'ergonomie de filtrage dépasse celle
du web.

### Structure

`Column` : `AppBar` (nom d'équipe + « ← » vers `/equipes`) puis `RoutesPage(embedded: true)` :
`RefreshIndicator` → `CustomScrollView` avec

1. **`PinnedHeaderSliver`** = en-tête épinglé, dimensionné par son contenu (résiste au text scaling) :
   - `RouteSearchBar` : `TextField` (placeholder `routes.filters.searchPlaceholder`, icône loupe,
     bouton × quand rempli) **debouncé 350 ms** + `IconButton.filledTonal` « tune » portant un
     `Badge.count` du nombre de filtres actifs.
   - `RouteFilterChipsBar` : bandeau horizontal de puces —
     1) une `ActionChip` **tri** (flèche ↑/↓ + libellé du critère) ouvrant la feuille de tri ;
     2) une `InputChip` sélectionnée et **supprimable (×)** par filtre actif ;
     3) une `ActionChip` grisée par filtre non encore posé, qui ouvre la feuille complète.
   - `Divider`.
2. Compteur `routes.list.count` (pluriel) basé sur `state.total`.
3. `ResponsiveSliverGrid` de `_RouteGridItem` (`childAspectRatio: 1.2`).
4. `PagedListFooter` (squelette `ShimmerRouteGridItem`).

### Carte parcours (`_RouteGridItem`)

Vignette 16/9 `Hero('route-thumbnail-{slug}')` (`media.assets.thumbnailLight/Dark`, repli icône `route`
sur `primaryContainer`) · `route.name` (1 ligne) · deux `_StatChip` : `straighten` + distance précise,
`trending_up` + dénivelé positif. Tap → détail du parcours.

### Filtres (`RouteFilters`)

Champs : `search`, `minDistance`/`maxDistance` (m), `minElevationGain`/`maxElevationGain` (m),
`hilliness`, `surfaceType`, `windDirection`, `sortBy`, `sortDir`
(défauts `dateTime` / `desc`, alignés sur `frontend/src/components/route/routeFilterDefaults.ts`).

**Feuille de filtres** (`showRouteFilterSheet`, modal `isScrollControlled` + drag handle) : édite un
brouillon, rien n'est appliqué avant validation.
- « Distance » : `RangeSlider` 0 → 300 km, pas 5 km (une extrémité sur sa borne = « non borné »).
- « Dénivelé + » : `RangeSlider` 0 → 5000 m, pas 50 m.
- « Type de surface » : `ChoiceChip` « Tout » + valeurs de `SurfaceType`.
- « Vallonnement » : idem avec `Hilliness`.
- Deux lignes de navigation vers des sous-feuilles : « Direction du vent » et « Tri ».
- Bouton plein largeur « Voir N parcours » — **N est recalculé côté serveur en continu**
  (`routeCountProvider`, debounce 350 ms), la dernière valeur connue restant affichée pendant le vol
  pour éviter le clignotement.
- « Réinitialiser » conserve la recherche et le tri.

**Feuille de tri** : liste d'options `RouteSortBy` + `SegmentedButton` asc/desc + bouton « Continuer ».

### État vide « cul-de-sac » (`RoutesEmptyState`)

Bien plus travaillé que le web : icône `search_off`, titre, description qui **cite le terme recherché**,
bouton principal « Retirer le filtre {X} » où X est le filtre **le plus probablement fautif**
(`narrowestField`, priorité dénivelé > distance > vent > vallonnement > surface > recherche), bouton
secondaire « Tout réinitialiser », et **aperçu de 3 parcours** que la levée de ce filtre ramènerait
(`routePreviewWithoutFilterProvider`, tuiles `ListTile` cliquables).

### Actions

Recherche, tri, pose/retrait de filtre à l'unité, réinitialisation, pull-to-refresh, scroll infini,
ouverture d'un parcours.

### Plus pauvre que le web

- **Pas de vue carte.** Le web propose `/equipes/{slug}/parcours/carte` (`RoutesMapPage` /
  `RoutesTileMap`) et un `RouteViewToggle` liste ⟷ carte. Le mobile n'a que la grille.
- Le mobile n'expose pas la **visibilité** du parcours (public / non listé / équipe), affichée en badge
  côté web.
- Pas de `RoutePreviewCompact` / miniature de tracé vectoriel : uniquement l'image de vignette.

---

## 6. Petites annonces

**Route** `/equipes/{teamSlug}/annonces` (`mobileName: teamAds`, **pas de deeplink**) — **Fichier**
`lib/features/ads/presentation/pages/ads_page.dart`.

### Structure

`RefreshIndicator` → `CustomScrollView` : `TeamSliverAppBar` puis liste (`SliverList.separated`,
padding 16, séparateurs 8 px) de `_AdCard`. Barre basse = `TeamShell`. Pas de FAB.

### Données (`_AdCard`)

Pavé 60×60 `secondaryContainer` + icône selon `adType` (`SALE`→`sell`, `RENTAL`→`key`,
`WANTED`→`search`) · libellé `ads.adType.{type}` en `secondary` · `ad.name` (1 ligne) · prix
`{price} €` en gras `primary` si présent · chevron.

### États

- Chargement : `ShimmerCardList(itemCount: 4)`.
- Vide : icône `sell`, `ads.empty`.
- Erreur : icône, message, « Réessayer ».

### Actions

Tap → `Paths.ad(teamSlug, adSlug)`. Pull-to-refresh.

### Plus pauvre que le web

- **Aucune pagination ni scroll infini** : `listAds(teamSlug)` est appelé sans `page`/`size`, la page
  affiche ce que renvoie la première page du serveur et s'arrête là. Sur une équipe volumineuse
  (« n-peloton », 1999 membres) la liste est donc tronquée silencieusement.
- **Pas de recherche ni de filtre par type d'annonce** (le web a `SearchInput` debouncé + `Select`
  `AdType`).
- Pas de compteur total, pas d'image d'annonce dans la carte (le web a `AdCard` avec visuel).

---

## 7. À propos de l'équipe

**Route** `/equipes/{teamSlug}/a-propos` — **Fichier**
`lib/features/teams/presentation/pages/team_about_page.dart`.

### Structure

`CustomScrollView` (dans `_TeamTabPageWrapper`) :
1. `TeamSliverAppBar`.
2. Rangée de 2 statistiques : icône `people` + `team.memberCount` + `teams.membersLabel` ;
   icône `calendar_today` + **année de création** (`team.createdAt.substring(0,4)`) +
   `teams.aboutPage.createdYear`.
3. Carte « À propos » si `team.about.markdown` non vide → `MarkdownContent` avec
   `team.about.assets.images`.
4. **Une carte par page d'équipe** (`team.pages`, filtrées `!deleted`, triées par `order`) : titre de la
   page + **contenu markdown complet chargé à la demande** (`_teamPageProvider`, un appel
   `getPage(teamSlug, pageSlug)` par carte), avec spinner et message d'erreur par carte.
5. Padding bas 32.

Barre basse = `TeamShell` (onglet À propos). Pas de FAB, pas de pull-to-refresh sur cet écran.

### Actions

Aucune, hormis les liens du markdown. Pas de bouton rejoindre/quitter l'équipe.

### Plus pauvre que le web

- **Les pages d'équipe n'ont pas d'écran propre** : le web a `/equipes/{slug}/pages/{pageSlug}`
  (`TeamPageDetailPage`, deeplinkable, avec fil d'Ariane) ; le mobile les empile en accordéon plat sur
  la page À propos. Un lien web vers une page d'équipe n'a **aucune destination mobile**.
- N pages ⇒ N requêtes simultanées, sans pagination ni repli.
- **Pas de liste des membres.** Côté web elle existe (`TeamMembersPage`) mais sous `/admin/members` —
  donc hors périmètre strict ; il reste qu'un membre ne peut voir personne d'autre depuis le mobile.
- Pas d'action « rejoindre / quitter l'équipe ».

---

## 8. Détail sortie

**Route** `/equipes/{teamSlug}/sorties/{rideSlug}` (deeplink) — **Fichier**
`lib/features/rides/presentation/pages/ride_detail_page.dart` (522 l.).

### Structure

Plein écran hors shell. `Scaffold` → `CustomScrollView` :

1. `SliverAppBar` `pinned` — si vignette : `expandedHeight: 200`, `FlexibleSpaceBar` avec le nom en
   16 px et l'image `Hero('ride-thumbnail-{slug}')` en fond ; sinon simple titre.
2. `TeamBanner` (logo initiales + nom, cliquable vers l'équipe).
3. Carte **Date** : icône `calendar_today`, `formatFullDate(dateTime)` en gras + `formatTime(dateTime)`.
4. Carte **Lieu de départ** si `ride.startPlace` : icône `location_on`, `startPlace.name`, sous-titre
   `startPlace.address`.
5. Carte **Parcours** si `ride.routeSlug` : icône `route`, libellé « Parcours », chevron → détail parcours.
6. Ligne **Participants** : icône `people`, `rides.participants {count}`, puis pile des **5 premiers**
   avatars (`ride.topParticipants`, r=14).
7. Section **Groupes** si `ride.groups` non vide : titre + une `Card`/`ListTile` par groupe —
   `group.name` ; sous-titre = `dates.departure {heure}` (si `group.time`) et/ou
   `{averageSpeed} km/h`, joints par « • » ; trailing = `group.countParticipants` + icône `route` si le
   groupe (ou la sortie) a un parcours ; tap → détail du parcours du groupe.
8. Carte **Description** si `ride.media.markdown` non vide.
9. Padding bas 100 (pour la barre d'action).

**Barre basse** (`bottomNavigationBar`, dans une `SafeArea` + `ContentWidthConstraint`) : un seul bouton
pleine largeur —
- **`FilledButton.icon` « Participer »** (icône check) si l'utilisateur n'est dans aucun groupe ;
- **`OutlinedButton.icon` « Se désinscrire »** (icône ×) sinon.
Pendant l'appel, l'icône devient un `CircularProgressIndicator` et le bouton est désactivé.

### Actions

- **Participer** : si 0 groupe → snackbar `rides.noGroupAvailable` ; si 1 groupe → inscription directe ;
  si ≥ 2 → `AlertDialog` « Choisir un groupe » listant `name` / heure de départ / vitesse moyenne /
  nombre de participants, tap = choix. Puis `joinRideGroup`, invalidation du provider, snackbar.
- **Se désinscrire** : **boucle sur tous les groupes** et `leaveRideGroup` jusqu'au premier succès
  (commentaire dans le code : « the one we're in will succeed »). Approximation fonctionnelle.
- Ouvrir le parcours (sortie ou groupe), ouvrir l'équipe.

### Plus pauvre que le web (`frontend/src/pages/ride/RideDetailPage.tsx`, 541 l.)

- **Pas de carte.** Le web affiche un `RoutesMapView` combinant le parcours principal **et le parcours de
  chaque groupe**, avec surbrillance croisée au survol d'un groupe. C'est le manque le plus visible.
- **Pas de statut** : le web affiche un `Badge` `DRAFT` / `PUBLISHED` / `CANCELLED` et la date de
  publication programmée. Sur mobile une sortie annulée est indiscernable d'une sortie normale.
- **Pas de lieu d'arrivée** (`ride.endPlace`) — seul le départ est rendu.
- **Pas de liste nominative des participants par groupe** : le web (`RideGroupCard`) montre qui est dans
  quel groupe ; le mobile ne montre que 5 avatars globaux et un compteur par groupe.
- **Aucun commentaire** : le web a `CommentSection` (rédaction, réponses, modération organisateur).
- Pas de bandeau explicatif pour les non-membres / non-connectés.
- Pas de logo d'entité (`EntityLogo`) ni de `MediaDisplay` complet.
- L'action « quitter » n'est pas ciblée sur le groupe réel (voir ci-dessus).

---

## 9. Détail parcours

**Route** `/equipes/{teamSlug}/parcours/{routeSlug}` (deeplink) — **Fichiers**
`lib/features/routes/presentation/pages/route_detail_page.dart` (502 l.),
`lib/features/routes/presentation/widgets/route_map.dart` (MapLibre).

### Structure

Plein écran, **carte en plein écran + feuille glissante** — le seul écran de l'app avec ce pattern.

- Fond : `RouteMap` (MapLibre, style VersaTiles `colorful`/`eclipse`), qui ajoute une source/couche
  GeoJSON pour la trace (largeur 4, opacité 0.8), un cercle vert au départ, un cercle rouge à l'arrivée,
  et des **marqueurs kilométriques** calculés côté client (intervalle 1/2/5/10 km selon la longueur,
  distance cumulée lue sur la 4ᵉ composante des coordonnées ou recalculée en haversine). `fitBounds` sur
  l'emprise de la trace après 100 ms.
- Overlay haut (`SafeArea`) : bouton retour circulaire semi-opaque + pilule semi-opaque avec le nom du
  parcours (ellipsé).
- `DraggableScrollableSheet` (`initial 0.15`, `min 0.1`, `max 0.7`, `snap` sur 0.15 / 0.45), coins
  arrondis 16, ombre portée, poignée de glissement :
  1. Trois statistiques : `straighten` distance en km (1 décimale), `trending_up` D+ en m,
     `trending_down` D− en m.
  2. Carte **Surface** : icône dérivée du type (`add_road`/`terrain`/`landscape`) + nom localisé.
  3. Carte **Description** si `route.media.markdown` non vide.
  4. Bouton plein largeur **« Télécharger »** (visible seulement s'il y a un GPX/FIT **ou** un service
     GPS connecté).

Pas de barre basse, pas de FAB.

### Feuille de téléchargement (`showModalBottomSheet`)

- `ListTile` « Télécharger le GPX » et/ou « Télécharger le FIT » → `dio.download` vers le répertoire
  temporaire puis **partage système** (`SharePlus.share`) du fichier.
- Si `user.connectedServices` non vide : séparateur, titre « Envoyer vers l'appareil », un `ListTile`
  par service (icône `smartphone` + `service.displayName`) → `gpsServicesClient.uploadRoute(...)` +
  snackbar de succès/erreur.

### Plus pauvre que le web (`RouteDetailView.tsx`)

- **Pas de profil altimétrique** (`ElevationChart` côté web, synchronisé avec la carte).
- **Pas de liste des cols/ascensions** : le web liste chaque `climb` avec catégorie colorée
  (HC/CAT1..CAT4), km de début/fin, D+, pente moyenne et pente max. Le mobile ignore complètement
  `track.climbs`.
- **Pas de « où ce parcours est utilisé »** (`RouteUsages` : sorties et voyages qui le référencent).
- **Pas de badge de visibilité** ni de date de création.
- **Aucun commentaire** (`CommentSection` côté web).
- Pas de mode carte plein écran dédié (`/parcours/{slug}/carte`) — la carte est déjà plein écran mais
  sans les contrôles du mode fullscreen web.
- Pas de conversion d'unités (le web a `useUnits` / `UnitSystemSwitcher` km ⟷ miles ; le mobile est
  **codé en dur en km / m**).

---

## 10. Détail article (post)

**Route** `/equipes/{teamSlug}/articles/{postSlug}` (deeplink) — **Fichier**
`lib/features/posts/presentation/pages/post_detail_page.dart` (129 l. — le plus court des écrans de détail).

### Structure

`Scaffold` → `CustomScrollView` : `SliverAppBar` `pinned` avec `post.name` · `TeamBanner` ·
ligne date (icône `calendar_today` 16 px + `formatFullDate(post.dateTime)` en `outline`) ·
`MarkdownContent(post.media.markdown, post.media.assets.images)` · padding bas 32.

États : spinner plein écran / erreur avec « Réessayer ». Pas de barre basse, pas de FAB, pas de
pull-to-refresh.

### Actions

Ouvrir l'équipe (`TeamBanner`), liens du markdown.

### Plus pauvre que le web (`PostDetailPage.tsx`, 338 l.)

- **Aucun commentaire.**
- Pas d'image de couverture ni de `EntityLogo` / `MediaDisplay` complet.
- Pas d'auteur affiché, pas de statut de publication.
- Pas de partage.

---

## 11. Détail voyage

**Route** `/equipes/{teamSlug}/voyages/{tripSlug}` (deeplink) — **Fichier**
`lib/features/trips/presentation/pages/trip_detail_page.dart` (414 l.).

### Structure

Même grammaire que le détail sortie :
1. `SliverAppBar` `pinned` avec vignette 200 px (`thumbnailLight/DarkUrl`) ou titre simple.
2. `TeamBanner`.
3. Carte **Date** : `formatFullDate` + `formatTime`.
4. Ligne **Participants** : `trips.participants {count}` + 5 avatars.
5. Section **Étapes** : titre + une `Card`/`ListTile` par étape — pastille numérotée (index+1) sur
   `primary`, `stage.name`, sous-titre `formatFullDate(stage.dateTime)`, chevron → détail étape.
   Si aucune étape : texte `trips.stages.empty` en `outline`.
6. Carte **Parcours** si `trip.routeSlug` → détail parcours.
7. Carte **Description** (markdown) si présente.
8. Padding bas 100.

**Barre basse** : bouton unique « Participer » / « Se désinscrire » (même pattern que la sortie), basé
sur `trip.participants.any(p => p.id == user.id)`. Ici l'appel est propre :
`joinTrip(teamSlug, tripSlug)` / `leaveTrip(...)`, invalidation + snackbar.

### Plus pauvre que le web (`TripDetailPage.tsx`, 524 l.)

- **Pas de carte** d'ensemble des étapes.
- **Aucun commentaire.**
- Pas de statut / brouillon, pas de dates de début-fin agrégées, pas de distance totale.
- Pas de liste nominative des participants (5 avatars seulement).

---

## 12. Détail étape

**Route** `/equipes/{teamSlug}/voyages/{tripSlug}/etapes/{stageSlug}` (deeplink) — **Fichier**
`lib/features/trips/presentation/pages/stage_detail_page.dart` (251 l.).

### Structure

L'étape **n'a pas d'endpoint propre** : la page charge `tripDetailProvider` puis cherche
`trip.stages.where(slug == stageSlug)`. Si absente : écran « Étape introuvable » + bouton « Retour au
voyage ».

`CustomScrollView` : `SliverAppBar` `pinned` avec `stage.name` · `TeamBanner` · carte **numéro + date**
(pastille `CircleAvatar` avec le rang, `formatFullDate` + `formatTime`) · carte **Lieu de départ**
(`location_on` vert, `stage.startPlace.name`) · carte **Lieu d'arrivée** (`location_on` rouge,
`stage.endPlace.name`) · carte **« Voir le parcours »** si `stage.route` → détail parcours ·
carte **Description** (markdown) · padding 32.

Pas de barre basse, pas de FAB.

### Plus pauvre que le web (`StageDetailPage.tsx`)

- **Pas de carte du parcours de l'étape** : le web embarque `RouteDetailView` (carte + profil
  altimétrique + statistiques + cols) directement dans la page d'étape, et propose une vue plein écran
  (`/etapes/{slug}/carte`). Le mobile se contente d'un lien vers le parcours.
- Pas de distance / D+ de l'étape.
- Pas d'adresse des lieux (seulement le nom), pas de commentaires.

---

## 13. Détail annonce

**Route** `/equipes/{teamSlug}/annonces/{adSlug}` (deeplink) — **Fichier**
`lib/features/ads/presentation/pages/ad_detail_page.dart` (188 l.).

### Structure

`CustomScrollView` : `SliverAppBar` `pinned` avec `ad.name` · `TeamBanner` · ligne **type + prix**
(`Chip` `ads.adType.{type}` sur `primaryContainer` à gauche ; à droite le prix
`{price} €` (2 décimales) suffixé de `/ {rentalPeriod}` pour une location, ou le texte italique
`ads.detail.priceNegotiable` si aucun prix) · carte **Localisation** si `ad.locationDescription` ·
ligne **date** (`formatFullDate(ad.createdAt)`) · `MarkdownContent` du corps · padding 32.

Pas de barre basse, pas de FAB.

### Plus pauvre que le web (`AdDetailPage.tsx`, 320 l.)

- **Aucun moyen de contacter l'annonceur** : ni auteur affiché, ni bouton contact/message.
- Pas de galerie d'images de l'annonce (seules les images du markdown sont rendues).
- Pas de statut (active / vendue / expirée), pas d'annonces liées.

---

## 14. Calendrier global

**Route** `/calendrier` (deeplink) — **Fichier**
`lib/features/calendar/presentation/pages/calendar_page.dart` (459 l.).
Sert aussi, en mode `embedded`, l'onglet calendrier d'équipe (§4).

### Structure

`Scaffold` + `AppBar` `calendar.title` (mode plein) ; `Column` :

1. **Sélecteur de mois** : `chevron_left` · `formatMonthYear(mois)` en `titleLarge` · `chevron_right`.
2. Liste des événements du mois, **groupés par jour** (`StaggeredListView`, padding 16).

Chaque `_DaySection` : pastille 48×48 (abréviation du jour + numéro), surlignée en `primary` si
aujourd'hui, puis le libellé (« Aujourd'hui » ou `formatDayMonth`), puis les cartes d'événements
indentées de 60 px.

`_EventCard` : barre verticale colorée 4 px + icône selon le type (`RIDE` → `directions_bike`
`primary` ; `TRIP_STAGE` → `hiking` `tertiary`) + `event.title` en gras +
`{event.teamName} • {formatTime(start)}` + chevron. Tap → sortie ou étape de voyage.

**Amorçage malin** : au montage, `_findFirstMonthWithEvents` interroge jusqu'à 6 mois consécutifs et
positionne le calendrier sur le premier mois non vide.

Barre basse = `MainShell` (onglet 2) en mode plein ; `TeamShell` en mode embarqué. Pas de FAB.

### États

Chargement (squelettes : entête de jour + 3 `ShimmerEventCard`), vide (`event_busy` +
`calendar.noEvents`), erreur (message + « Réessayer »), liste avec pull-to-refresh.

### Plus pauvre que le web (`CalendarView.tsx` + `@mantine/schedule`)

- **Pas de vue grille** : le web offre jour / semaine / mois / année ; le mobile n'a qu'une liste
  mensuelle. Pas de bouton « Aujourd'hui ».
- **Pas de flux ICS** (`IcsFeedSettings` côté web) — aucun export vers un calendrier personnel.
- Pas de filtre par type d'événement ni par équipe sur le calendrier global.
- La navigation est mois par mois uniquement (pas de saut à une date).

---

## 15. Profil / préférences

**Route** `/profil` (deeplink) — **Fichier**
`lib/features/profile/presentation/pages/profile_page.dart` (324 l.).

### Structure

`Scaffold` + `AppBar` `profile.title` avec une **action déconnexion** (`Icons.logout`) qui ouvre un
`AlertDialog` de confirmation puis `logout()` + `context.go(Paths.login())`.
Corps : `ListView` centré, contraint à 600 px, padding 16 :

1. **En-tête** : `AuthenticatedCircleAvatar` r=50 (`user.avatarUrl`, repli initiale) ·
   `user.displayName` en `headlineSmall` gras · `user.email` en `outline`.
2. **Sécurité** — carte, une ligne : icône `fingerprint` (colorée si passkey présente), titre
   `profile.passkeys.title`, sous-titre « activée » / « non configurée », bouton
   `FilledButton.tonal` « Ajouter » ou « Remplacer ». L'action enregistre une nouvelle passkey
   (`deviceName: 'Mobile'`) **puis supprime toutes les précédentes** (une seule passkey par compte
   sur mobile).
3. **Préférences** — carte, une seule ligne : `language` → **Langue** (fr / en), sous-titre = langue
   courante, tap ouvre une `showModalBottomSheet` avec deux `ListTile` cochés.
4. **À propos** — carte, 4 lignes :
   - `info_outline` « Version » → `{version} ({buildNumber})` via `package_info_plus` ;
   - `dns_outlined` « Version du serveur » → `{apiVersion} ({commit})` via `GET /api/version` ;
   - `policy_outlined` « Confidentialité » → `Paths.privacy()` ;
   - `description_outlined` « CGU » → `Paths.terms()`.

Barre basse = `MainShell` (onglet 3). Pas de FAB.

### Plus pauvre que le web (`UserProfilePage.tsx`, 281 l.)

C'est, avec les commentaires, l'écart le plus large :

- **Aucune édition** : le nom d'affichage est en lecture seule (le web a un formulaire d'édition).
- **Pas de gestion d'avatar** (upload photo / suppression côté web).
- **Pas de système d'unités** (`UnitSystemSwitcher` km/miles côté web) — le mobile est en km/m en dur.
- **Pas de connexions GPS** (`GpsConnectionsManager` : connecter/déconnecter Garmin, Hammerhead…). Le
  mobile *consomme* `user.connectedServices` pour l'envoi de parcours mais ne permet pas de les gérer.
- **Pas de connexions sociales** (`SocialConnectionsManager`, Strava) ni de callback Strava.
- **Pas d'export de données** (`DataExportManager`, RGPD).
- **Pas de suppression de compte** ni de « déconnecter tous les appareils » (`logoutAll` existe dans
  `AuthNotifier` mais n'est câblé à aucune UI).
- Gestion multi-passkeys impossible (le web a `PasskeyManager` avec liste et suppression unitaire) ;
  le mobile écrase silencieusement les autres passkeys.

---

## 16. Pages légales

**Routes** `/confidentialite` et `/cgu` (deeplink, accessibles **sans authentification**) — **Fichier**
`lib/features/legal/presentation/pages/legal_page.dart` (60 l.).

`Scaffold` + `AppBar` (titre `profile.privacy` / `profile.terms`, leading `BackOrHomeButton`) et
`SingleChildScrollView` + `ContentWidthConstraint` + `MarkdownContent`. Le contenu est lu depuis les
**assets embarqués** (`privacy/privacy-policy.{fr|en}.md`, `privacy/terms-of-service.{fr|en}.md`), pas
depuis l'API. Spinner pendant le chargement du bundle. Hors shell : pas de barre basse.

Équivalent web : `PrivacyPolicyPage` / `TermsOfServicePage`. Parité correcte.

---

## Écrans hors périmètre présents dans l'app (pour mémoire)

| Écran | Route | Fichier | Notes |
|---|---|---|---|
| Connexion / Inscription | `/connexion`, `/inscription` | `features/auth/.../login_page.dart` | Un seul écran à deux modes : formulaire email+mot de passe (autofill), lien « mot de passe oublié », bouton passkey si supporté, bascule inscription (email, nom, mot de passe ×2). |
| Vérification d'email | `/verifier-email?token=` | `features/auth/.../verify_email_page.dart` | Vérifie au montage, propose d'enregistrer une passkey en cas de succès. |
| Mot de passe oublié | `/mot-de-passe-oublie` | `features/auth/.../forgot_password_page.dart` | Formulaire + état « email envoyé ». |
| Nouveau mot de passe | `/nouveau-mot-de-passe?token=` | `features/auth/.../reset_password_page.dart` | |
| Appairage appareil | `/garmin`, `/karoo` (`?code=`) | `features/device/.../device_verify_page.dart` | Saisie/lecture d'un code à 6 caractères, autorisation de l'appareil. |

**Gestion de l'auth** (`features/auth/`) : `AuthNotifier` (StateNotifier) ; le refresh token est
stocké en **secure storage**, l'access token est propagé à `accessTokenHolderProvider` pour
l'`AuthInterceptor` (rafraîchissement sur 401 avec file d'attente des requêtes). Au démarrage,
`initialize()` tente un refresh ; le token n'est effacé que sur 401/403 (pas sur erreur réseau).
Après authentification, l'app appelle `getMe()` puis `listPasskeys()`, et **invalide
`apiClientProvider`** pour purger tout le cache utilisateur (les providers de données ne sont pas
`autoDispose` pour la plupart). Deux instances Dio : `baseDioProvider` (sans auth) et `dioProvider`
(avec intercepteur).

---

## Écrans absents côté mobile

Comparaison avec `frontend/src/pages/`, **périmètre consultation/participation uniquement**.

### Absences complètes (aucun écran mobile)

| Web | Route | Impact |
|---|---|---|
| `route/AllRoutesPage.tsx` | `/parcours` | **Liste inter-équipes des parcours.** Le mobile a tout le code (`RoutesPage(teamSlug: null)`, `routeFiltersProvider(null)`) mais **aucune route ne l'expose** (`allRoutes` est `mobile: false`). Un membre de plusieurs équipes ne peut pas chercher un parcours transversalement. |
| `route/AllRoutesMapPage.tsx` | `/parcours/carte` | Carte de tous les parcours. |
| `route/RoutesMapPage.tsx` | `/equipes/{slug}/parcours/carte` | Vue carte de la liste des parcours d'une équipe (toggle liste/carte). |
| `route/RouteFullscreenMapPage.tsx` | `/equipes/{slug}/parcours/{slug}/carte` | Carte plein écran avec profil altimétrique. |
| `trip/StageFullscreenMapPage.tsx` | `.../etapes/{slug}/carte` | Carte plein écran d'une étape. |
| `team/TeamPageDetailPage.tsx` | `/equipes/{slug}/pages/{pageSlug}` | **Page d'équipe autonome** (deeplinkable côté web). Sur mobile, les pages sont seulement inlinées dans « À propos » : un lien partagé vers une page d'équipe n'a pas de cible mobile. |
| `team/TeamMembersPage.tsx` | `/equipes/{slug}/admin/membres` | Liste des membres. Techniquement sous `/admin` côté web, mais c'est l'unique endroit où voir qui compose l'équipe ; rien d'équivalent sur mobile. |
| `auth/CompleteAccountPage.tsx` | `/completer-le-compte` | Complétion de profil après invitation. |
| `auth/StravaCallbackPage.tsx` | `/strava/callback` | Retour OAuth Strava (corollaire de l'absence de connexions sociales). |
| `NotFoundPage.tsx` | — | Remplacé par un `errorBuilder` minimal codé en dur en français. |

### Fonctionnalités transverses absentes (pas un écran, mais un pan entier de l'UX web)

- **Commentaires** (`components/comment/CommentSection`) : présents côté web sur **sortie, parcours,
  article et voyage**, réservés aux membres, avec réponses et modération organisateur.
  **Zéro trace côté mobile** — ni écran, ni widget, ni appel API.
- **Recherche** : le web a un `SearchInput` debouncé sur le feed, les équipes et les annonces. Sur
  mobile, seule la liste des parcours a une recherche.
- **Filtres d'appartenance** (`minRole` : tous / membre / organisateur / admin) sur le feed et les
  équipes.
- **Pagination visible / compteurs** : le web pagine explicitement avec préchargement de la page
  suivante ; le mobile a du scroll infini (feed, parcours) ou **rien du tout** (annonces, calendrier,
  équipes).
- **Système d'unités** (km ⟷ miles) : `preferencesStore` + `UnitSystemSwitcher` côté web ; mobile en
  métrique figé.
- **Flux ICS** du calendrier.
- **Gestion du compte** : avatar, nom, connexions GPS et sociales, export RGPD, suppression de compte.

### Hors périmètre (rappel, non documenté ici)

Administration (`/plateforme/*`, `/equipes/*/admin/*`), création et édition (sorties, parcours,
articles, annonces, voyages, pages d'équipe, modèles de sortie), outils GPX (`/outils-gpx/*`) :
absents du mobile, ce qui est cohérent avec le positionnement « consultation et participation » de
l'app.
