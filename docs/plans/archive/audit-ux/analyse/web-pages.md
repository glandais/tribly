# Pédalons — Documentation du frontend web (entrant design)

> Document généré par lecture du code (`frontend/`, React 19 + Mantine UI 8 + React Router 7 + TanStack Query + Zustand).
> **Périmètre :** uniquement les pages de **consultation et de participation** d'un membre.
> Sont volontairement **exclus** : administration (`/admin`, `/plateforme`), création/édition (sorties, parcours, articles, annonces, pages d'équipe, modèles), outils GPX.
> **Locale de référence : français.** Les chemins FR sont ceux utilisés en production.
> Contexte d'observation : utilisateur connecté « Gaby Landais », équipes de test `gaby` (7 membres) et `n-peloton` (1999 membres).

---

## Sommaire

1. [Layout & navigation](#1-layout--navigation)
2. [Accueil / fil d'actualités](#2-accueil--fil-dactualités) — `/`
3. [Liste des équipes](#3-liste-des-équipes) — `/equipes`
4. [Détail d'équipe — fil d'actualités](#4-détail-déquipe--fil-dactualités) — `/equipes/{slug}`
5. [Équipe — À propos](#5-équipe--à-propos) — `/equipes/{slug}/a-propos`
6. [Équipe — Page libre (CMS)](#6-équipe--page-libre-cms) — `/equipes/{slug}/pages/{pageSlug}`
7. [Calendrier global](#7-calendrier-global) — `/calendrier`
8. [Calendrier d'équipe](#8-calendrier-déquipe) — `/equipes/{slug}/calendrier`
9. [Détail d'une sortie (ride)](#9-détail-dune-sortie-ride) — `/equipes/{slug}/sorties/{rideSlug}`
10. [Détail d'un article (post)](#10-détail-dun-article-post) — `/equipes/{slug}/articles/{postSlug}`
11. [Détail d'un voyage (trip)](#11-détail-dun-voyage-trip) — `/equipes/{slug}/voyages/{tripSlug}`
12. [Détail d'une étape](#12-détail-dune-étape) — `/equipes/{slug}/voyages/{tripSlug}/etapes/{stageSlug}`
13. [Carte plein écran d'une étape](#13-carte-plein-écran-dune-étape)
14. [Parcours d'une équipe — liste](#14-parcours-dune-équipe--liste) — `/equipes/{slug}/parcours`
15. [Parcours d'une équipe — carte](#15-parcours-dune-équipe--carte) — `/equipes/{slug}/parcours/carte`
16. [Tous les parcours — liste](#16-tous-les-parcours--liste) — `/parcours`
17. [Tous les parcours — carte](#17-tous-les-parcours--carte) — `/parcours/carte`
18. [Détail d'un parcours](#18-détail-dun-parcours) — `/equipes/{slug}/parcours/{routeSlug}`
19. [Carte plein écran d'un parcours](#19-carte-plein-écran-dun-parcours)
20. [Petites annonces — liste](#20-petites-annonces--liste) — `/equipes/{slug}/annonces`
21. [Détail d'une annonce](#21-détail-dune-annonce) — `/equipes/{slug}/annonces/{adSlug}`
22. [Profil & préférences](#22-profil--préférences) — `/profil`
23. [Page 404](#23-page-404)
24. [Composants partagés](#24-composants-partagés)
25. [État global, données, formats](#25-état-global-données-formats)
26. [Annexe — pages périphériques](#26-annexe--pages-périphériques)

---

## 1. Layout & navigation

### 1.1 Coquille applicative — `frontend/src/components/common/Layout.tsx`

Toutes les pages « normales » sont rendues dans un **`AppShell` Mantine**. Seules les cartes plein écran
(`layout: 'bare'` dans `routes.config.ts`) sortent de cette coquille.

**Structure verticale :**

1. **Header** (`AppShell.Header`)
   - Hauteur : **56 px en mobile, 60 px à partir de `sm`**.
   - Comportement **headroom** (`useHeadroom({ fixedAt: 120 })`) : le header se **rétracte au scroll vers le bas** et réapparaît au scroll vers le haut.
   - Contenu dans un `Container size="lg"`, `Group justify="space-between"` :
     - **Gauche** : nom de l'app (`Text size="xl" fw={700} c="primary"`) — lien vers `/`. Le nom vient de la configuration du domaine (`useAppName`), pas d'une constante.
     - **Droite (desktop, `visibleFrom="sm"`)** :
       - `ColorSchemeSwitcher` — `ActionIcon variant="default"`, icône `IconMoon` / `IconSun`, bascule clair/sombre.
       - `LanguageSwitcher` — `NativeSelect size="xs"` listant les langues supportées (libellés natifs).
       - **Si connecté** : `Menu` déclenché par un `UnstyledButton` = `Avatar` (photo ou initiale, `radius="xl"`, `size="sm"`, couleur primaire) + nom affiché (`visibleFrom="md"`). Dropdown :
         - `Profil` (`IconUser`) → `/profil`
         - `Outils GPX` (`IconMapSearch`) → `/outils-gpx` *(hors périmètre)*
         - `Administration` (`IconShield`) → seulement si `platformRole === PLATFORM_ADMIN` *(hors périmètre)*
         - séparateur
         - `Se déconnecter` (`IconLogout`, couleur `danger`)
       - **Si non connecté** : bouton plein `Se connecter` → `/connexion`.
     - **Mobile (`hiddenFrom="sm"`)** : un `Burger`.

2. **Navbar mobile** (`AppShell.Navbar`, largeur 300, **jamais visible en desktop**) — ouverte par le burger, fermée par `Échap`. `Stack` vertical : sélecteur de thème, sélecteur de langue, `Divider`, puis (si connecté) ligne avatar+nom cliquable vers le profil, boutons `subtle` « Outils GPX », « Administration » (si admin), « Se déconnecter » (rouge). Sinon un bouton « Se connecter ».

3. **Main** (`AppShell.Main`) dans un `Container size="lg" px={0}` :
   - **Bandeau e-mail manquant** (conditionnel) : `Alert` orange, icône `IconMail`, fermable, titre + message + bouton « Compléter » vers `/completer-le-compte`. Affiché seulement si `user.requiresEmail === true` (compte créé via Strava sans e-mail).
   - **Fil d'Ariane** (`Breadcrumb`) — voir 1.4.
   - `<Outlet />` : la page.

4. **Footer** (`Box component="footer"`, bordure supérieure, `py="xl"`) : une seule ligne centrée, texte `dimmed size="sm"`, éléments séparés par un `·` :
   `© {année} {nom de l'app}` · `Politique de confidentialité` · `Conditions d'utilisation` · `version {apiVersion} ({commit})`.

**Comportements transverses du layout :**
- Le **titre du document** est `"{dernier fil d'Ariane} — {nom de l'app}"`.
- **Scroll** : remise à zéro du scroll sur navigation `PUSH` ; restauration du scroll sur `POP` (retour arrière) ; **aucun mouvement** sur `REPLACE` (= modification d'un filtre, cf. §25).
- Padding du `main` : `xs` en mobile, `md` à partir de `sm`.

### 1.2 Barre d'onglets « Accueil » — `HomeLayout` + `NavButtons`

Sur les 4 pages de premier niveau, un bandeau d'onglets horizontal apparaît sous le fil d'Ariane :

| Onglet | Libellé FR | Icône | Cible | Condition |
|---|---|---|---|---|
| `feed` | Accueil | `IconNews` | `/` | toujours |
| `teams` | Équipes | `IconUsers` | `/equipes` | masqué si le site est mono-équipe |
| `calendar` | Calendrier | `IconCalendar` | `/calendrier` | connecté uniquement |
| `routes` | Parcours | `IconRoute` | `/parcours` | toujours |

**Rendu d'un `NavButton`** (`components/common/NavButtons.tsx`) — pattern visuel fort et réutilisé partout :
- Colonne verticale : **carré 48×48 px, `radius="md"`** contenant l'icône 24 px, + libellé en dessous (`size="xs"`, 2 lignes max, `lineClamp={2}`, centré).
- **Actif** : carré rempli en couleur primaire, icône blanche, libellé en poids 600 et couleur de texte normale.
- **Inactif** : carré en `--mantine-color-default-hover`, icône couleur texte, libellé `dimmed`.
- Largeur 64–80 px, le tout dans un `ScrollArea type="never"` horizontal (défilement tactile, pas de barre visible) — c'est ce qui permet aux longues listes d'onglets d'équipe de tenir en mobile.

### 1.3 Barre d'onglets « Équipe » — `TeamLayout`

`components/team/TeamLayout.tsx` enveloppe **toutes** les pages d'équipe (fil, calendrier, parcours liste+carte, annonces, à propos, pages libres).

**En-tête d'équipe** (au-dessus des onglets) :
- `TeamAvatar size="xl"` (logo, ou initiales sur fond de couleur déterministe issue d'un hash du nom).
- `Title order={1}` = nom de l'équipe + `VisibilityBadge` (Publique / Non listée / Équipe).
- À droite, groupe de boutons contextuels :
  - **`Rejoindre l'équipe`** — bouton plein, visible si : connecté, non membre, et visibilité ≠ `TEAM`. État de chargement « Adhésion… ».
  - **`Quitter l'équipe`** — bouton `variant="default"`, visible si membre **non admin** → ouvre un `ConfirmDialog` (variante `warning`), puis redirige vers `/equipes`.
  - **`Gérer`** — visible pour ADMIN/ORGANIZER, vers l'admin d'équipe *(hors périmètre)*.
- Effet de bord : la **favicon du site devient le logo de l'équipe** (`useFavicon`).

**Onglets d'équipe** (`useTeamNavItems`), dans cet ordre :

| id | Libellé FR | Icône | Condition d'affichage |
|---|---|---|---|
| `publications` | Fil d'actualités | `IconNews` | toujours |
| `calendar` | Calendrier | `IconCalendar` | **membre** ET (`enableRides` ou `enableTrips`) |
| `routes` | Parcours | `IconRoute` | `enableRoutes` |
| `ads` | Annonces | `IconTags` | **membre** ET `enableAds` |
| `about` | À propos | `IconInfoCircle` | toujours |
| *(dynamiques)* | titre de la page | `IconFileText` | une entrée par page libre visible (les pages `TEAM` sont masquées aux non-membres) |

> Conséquence design : le nombre d'onglets d'équipe est **variable** (5 minimum, potentiellement 8–10 avec les pages libres). La barre doit rester scrollable horizontalement.

### 1.4 Fil d'Ariane — `components/common/Breadcrumb.tsx`

Construit automatiquement depuis la hiérarchie `parentId` de `config/routes.config.ts`.

- **Desktop (`visibleFrom="sm"`)** : `Breadcrumbs` Mantine complet. Chaque niveau est un lien (`Anchor size="sm"`), le dernier est du texte `dimmed`.
  - Certains niveaux portent un **`Menu` déroulant** déclenché par un `ActionIcon variant="subtle"` / `IconDots` : il reprend **exactement** les onglets du niveau (accueil ou équipe). C'est un raccourci de navigation latérale depuis le fil d'Ariane.
- **Mobile** : le fil complet est masqué ; on n'affiche qu'un **lien retour** `‹ {niveau parent}` (`IconChevronLeft`, `dimmed`, `size="sm"`).
- Sur les pages marquées `showBackLink` (essentiellement les pages de création/édition), seul le lien retour est affiché, y compris en desktop.

Chaînes de fil d'Ariane utiles (périmètre) :
- `Accueil` → *(racine)*
- `Équipes` → `{Nom d'équipe}` → `À propos` / `Calendrier` / `{Titre de page}` / `Parcours` → `{Nom de parcours}` → `Carte plein écran`
- `Équipes` → `{Nom d'équipe}` → `{Nom de sortie}` (les sorties/voyages/articles ont l'équipe pour parent, il n'y a **pas** de page liste dédiée)
- `Équipes` → `{Nom d'équipe}` → `Annonces` → `{Titre d'annonce}`
- Sur un site mono-équipe, le maillon « Équipes » est supprimé.

### 1.5 Routage, locales, SSR

- Les chemins viennent de `contracts/routes.yaml` (source unique) → `paths.xxx()` retourne l'URL **dans la locale courante**, `pathVariants.xxx()` retourne toutes les variantes.
- **Chaque route est enregistrée dans les deux langues** : `/teams/gaby/routes` et `/equipes/gaby/parcours` mènent à la même page.
- Les pages de détail appellent `useCanonicalPath(...)` : arrivée sur une URL non canonique (mauvaise langue, ancien slug) ⇒ **redirection `replace` silencieuse** vers l'URL canonique, en conservant la query string.
- Les pages publiques sont **rendues côté serveur** (SSR) puis hydratées ; le contenu authentifié n'apparaît qu'après hydratation. Les aperçus de lien (Open Graph) sont générés côté serveur.
- **Chargement paresseux** : chaque page est un chunk `lazy()`, le fallback est un simple `<Loader />` Mantine. Les cartes et graphiques (~1,1 Mo) sont dans des chunks séparés chargés à la demande, avec un `Skeleton height={500}` en attendant.

### 1.6 Thème — `frontend/src/lib/theme.ts`

- `primaryColor: 'primary'` = **indigo** (identique en clair et en sombre, via `virtualColor`).
- Couleurs sémantiques : `success` → vert, `warning` → jaune, `danger` → rouge.
- Police : **Inter**, `system-ui` en repli. `defaultRadius: 'md'`. `autoContrast: true`.
- **Typographie fluide** : `h1: clamp(1.5rem, 5vw, 2.125rem)`, `h2: clamp(1.25rem, 4vw, 1.625rem)`, `h3: clamp(1.125rem, 3vw, 1.375rem)`, `h4: clamp(1rem, 2.5vw, 1.125rem)`.
- **Cibles tactiles** : hauteur minimale des boutons **44 px en mobile**, 36 px à partir de 48em ; les `ActionIcon` sont en `size="lg"` par défaut.
- Mode clair/sombre : `defaultColorScheme="auto"` (préférence système), surchargeable par l'utilisateur (persisté).
- Notifications : `@mantine/notifications` positionnées **en haut à droite**.

---

## 2. Accueil / fil d'actualités

| | |
|---|---|
| **Route FR** | `/` |
| **Route EN** | `/` |
| **Fichier** | `src/pages/home/HomePage.tsx` |
| **Accès** | public (le filtre « équipes » n'apparaît que connecté) |
| **Layout** | `HomeLayout currentTab="feed"` |

### Structure verticale
1. Barre d'onglets Accueil (§1.2).
2. **Bloc d'accroche** : `Title order={2}` « Bienvenue sur {nom de l'app} » + sous-titre `dimmed` « Votre plateforme pour organiser vos sorties cyclistes ».
3. `Title order={2}` **« Dernières publications »**.
4. **Barre de filtres** (`Group align="flex-end" wrap="wrap"`) :
   - `SearchInput` — libellé « Rechercher des publications », placeholder « Rechercher par titre ou description… », icône loupe à gauche, croix d'effacement à droite quand rempli. **Débounce** avant écriture dans l'URL.
   - `Select` **type de publication** (largeur 160 px desktop, 100 % mobile) : `Toutes` / `Sorties` / `Articles` / `Voyages`.
   - `Select` **appartenance** (connecté uniquement, 180 px) : `Toutes les équipes` / `Membre` / `Organisateur` / `Admin`. **Valeur par défaut intelligente** : « Membre » si l'utilisateur appartient à au moins une équipe, sinon « Toutes les équipes » ; la valeur est **toujours écrite dans l'URL** pour que les liens partagés soient sans ambiguïté.
5. **Grille de publications** : `SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg"`, **12 éléments par page**.
6. **Pagination** numérotée centrée (voir §24.4).

### États
- **Chargement** : 6 `PublicationCardSkeleton` dans la même grille.
- **Erreur** : `Paper withBorder p="xl"` centré, `IconNews` 48 px rouge + « Erreur de chargement ».
- **Vide** : `Paper withBorder p="xl"` centré, `IconNews` 48 px gris + « Aucune publication pour le moment » (ou « Aucune publication ne correspond à votre recherche. » si une recherche est active).

### Données affichées (carte de publication)
Voir §24.1 — la carte agrège sortie / article / voyage. Sur l'accueil, le **nom de l'équipe est affiché** en haut de carte (sauf site mono-équipe).

### Actions membre
- Rechercher, filtrer par type, filtrer par appartenance, paginer.
- Cliquer une carte → détail de la sortie / article / voyage.
- Cliquer le nom d'équipe dans la carte → page de l'équipe.

### Navigation
- **Entrante** : logo du header, onglet « Accueil », lien « Retour à l'accueil » de la 404.
- **Sortante** : détails de publication, page d'équipe, autres onglets Accueil.

---

## 3. Liste des équipes

| | |
|---|---|
| **Route FR** | `/equipes` |
| **Route EN** | `/teams` |
| **Fichier** | `src/pages/team/TeamListPage.tsx` |
| **Accès** | public — **masquée sur un site mono-équipe** (redirection vers `/`) |
| **Layout** | `HomeLayout currentTab="teams"` |

### Structure verticale
1. Onglets Accueil.
2. En-tête : `Title order={2}` **« Équipes »** + sous-titre `dimmed` « Parcourez les équipes cyclistes ou créez la vôtre ». À droite, bouton `+ Créer une équipe` (connecté ; masqué si site mono-équipe déjà pourvu) — *hors périmètre*.
3. Filtres : `SearchInput` « Rechercher des équipes » (flex) + `Select` appartenance (`Toutes les équipes` / `Membre` / `Organisateur` / `Admin`, 160 px, connecté uniquement).
4. `Alert` rouge en cas d'erreur API.
5. Grille `SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }}`, **12 équipes par page**, `TeamCard`.
6. Pagination.

### Données affichées (`TeamCard`)
- Bandeau image **120 px** : première image de la présentation d'équipe, sinon **dégradé violet→indigo avec icône `IconUsersGroup` blanche**.
- Ligne titre : `TeamAvatar size="md"` + nom de l'équipe.
- Extrait de la description (markdown aplati, **150 caractères max**, `size="sm" dimmed`).
- Pied de carte : `Stat` `IconUsers` « N membres » | badge de visibilité (uniquement si `TEAM`) + **badge de rôle** (`Admin` violet / `Organisateur` bleu / `Membre` gris).

### États
- **Chargement** : 6 `TeamCardSkeleton`.
- **Vide** : `Paper` centré, `IconUsersGroup` 48 px, « Aucune équipe trouvée » + « Aucune équipe ne correspond à votre recherche. » (+ bouton de création si autorisé).

### Actions membre
Rechercher, filtrer par rôle, paginer, ouvrir une équipe.

---

## 4. Détail d'équipe — fil d'actualités

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}` |
| **Route EN** | `/teams/{teamSlug}` |
| **Fichier** | `src/pages/publication/PublicationListPage.tsx` |
| **Accès** | public |
| **Layout** | `TeamLayout currentTab="publications"` |

C'est la **page d'atterrissage d'une équipe**.

### Structure verticale
1. En-tête d'équipe (avatar XL, nom, badge de visibilité, boutons Rejoindre / Quitter / Gérer) — §1.3.
2. Barre d'onglets d'équipe.
3. Ligne titre : `Title order={2}` **« Fil d'actualités »** ; à droite, pour ADMIN/ORGANIZER uniquement, un `Button.Group` « bouton principal + chevron » ouvrant un menu de création (Sortie / Article / Voyage / Parcours selon les modules activés) — *hors périmètre*.
4. Ligne recherche + filtre : `SearchInput` pleine largeur + `Select` type (`Toutes` / `Sorties` / `Articles` / `Voyages`, largeur 120→150 px). **Le select n'affiche que les types activés pour l'équipe.**
5. Grille de `PublicationCard` — `cols={{ base: 1, sm: 2, lg: 3 }}`, 12 par page. Ici `showTeam={false}` (on est déjà dans l'équipe).
6. Pagination.

### États
- **Chargement de l'équipe** : page de chargement plein écran (`Loader` + « Fil d'actualités »).
- **Équipe introuvable** : redirection vers `/equipes`.
- **Chargement des publications** : 6 squelettes.
- **Vide** : `Paper withBorder p={48}` centré → cercle gris contenant `IconNews` 48 px, `Title order={3}` « Aucune publication » (ou « Aucun résultat »), texte descriptif `dimmed maw={400}`, et si une recherche est active un bouton `variant="light"` **« Effacer la recherche »**.

### Actions membre
Rechercher, filtrer, paginer, ouvrir une publication, rejoindre/quitter l'équipe, naviguer entre onglets.

---

## 5. Équipe — À propos

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/a-propos` |
| **Route EN** | `/teams/{teamSlug}/about` |
| **Fichier** | `src/pages/team/TeamAboutPage.tsx` |
| **Accès** | public |
| **Layout** | `TeamLayout currentTab="about"` |

### Structure
En-tête + onglets d'équipe, puis un unique `Paper p="lg" withBorder` :
1. `Title order={2}` « À propos de l'équipe ».
2. **Description** : `MediaDisplay` = markdown riche rendu (titres, listes, tableaux, citations, code, images d'assets) + bloc « Pièces jointes » si présent. Si vide : « Aucune description disponible. » en italique `dimmed`.
3. Séparateur (bordure haute), puis **statistiques** en `SimpleGrid cols={{ base: 1, sm: 2 }}` :
   - `IconUsers` — libellé `dimmed` « Membres » / valeur `size="lg" fw={500}` « N membres ».
   - `IconCalendar` — « Création » / date longue localisée (`12 mars 2024`).

### Actions membre
Lecture seule + téléchargement des pièces jointes. Les actions d'adhésion sont dans l'en-tête d'équipe.

---

## 6. Équipe — Page libre (CMS)

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/pages/{pageSlug}` |
| **Route EN** | `/teams/{teamSlug}/pages/{pageSlug}` |
| **Fichier** | `src/pages/team/TeamPageDetailPage.tsx` |
| **Accès** | public (les pages de visibilité `TEAM` ne sont listées qu'aux membres) |
| **Layout** | `TeamLayout currentTab={page.slug}` |

### Structure
En-tête + onglets, puis `Paper p="lg" withBorder` :
- `Group` : `Title order={2}` = titre de la page + `VisibilityBadge` (**seulement si visibilité `TEAM` et que l'on est membre**).
- Contenu : `MediaDisplay` (markdown + pièces jointes). Si vide : « Aucun contenu » en italique `dimmed`.

Page introuvable → redirection vers le fil de l'équipe. Équipe introuvable → `/equipes`.

**Usage design** : ces pages servent de « pages statiques » d'équipe (règlement, matériel, contacts…). Leur nombre est libre et chacune ajoute un onglet.

---

## 7. Calendrier global

| | |
|---|---|
| **Route FR** | `/calendrier` |
| **Route EN** | `/calendar` |
| **Fichier** | `src/pages/calendar/CalendarPage.tsx` |
| **Accès** | **authentifié** |
| **Layout** | `HomeLayout currentTab="calendar"` |

### Structure verticale
1. Onglets Accueil.
2. `Title order={2}` « Calendrier ».
3. **`CalendarView`** — composant `Schedule` de `@mantine/schedule` :
   - Vues **jour / semaine / mois / année**, vue initiale **mois**, **semaine commençant le lundi**.
   - Barre de contrôle native du composant : « Aujourd'hui », « Précédent », « Suivant », sélecteur de vue, sélecteurs mois/année — **tous les libellés sont traduits**.
   - Événements colorés par type : **sortie = bleu `#228be6`**, **étape de voyage = vert `#40c057`**.
   - `LoadingOverlay` pendant le chargement de la plage.
   - Le titre de l'événement est le nom de la sortie / de l'étape. **Clic → navigation** vers le détail de la sortie ou de l'étape.
   - La plage visible pilote la requête serveur (`from`/`to`), avec un cache de 5 minutes.
4. **`IcsFeedSettings`** — `Paper withBorder p="lg" radius="md"` :
   - `Alert` bleu (`IconInfoCircle`) : « Abonnez-vous à ce calendrier depuis votre application de calendrier (Apple Calendar, Google Calendar, Outlook…) ».
   - `TextInput` en lecture seule avec l'URL du flux + `CopyButton` (`ActionIcon`, `IconCopy` → `IconCheck` teal après copie).
   - Boutons : **« S'abonner »** (lien `webcal://`, `IconCalendarPlus`) et **« Régénérer le lien »** (`variant="outline" color="orange"`, `IconRefresh`) → `ConfirmDialog` d'avertissement.
   - Squelette pendant le chargement du jeton.

### Actions membre
Changer de vue/période, ouvrir un événement, copier / s'abonner au flux ICS, régénérer le jeton.

---

## 8. Calendrier d'équipe

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/calendrier` |
| **Route EN** | `/teams/{teamSlug}/calendar` |
| **Fichier** | `src/pages/calendar/TeamCalendarPage.tsx` |
| **Accès** | public au niveau route, mais **l'onglet n'est proposé qu'aux membres** (et si sorties ou voyages activés) |
| **Layout** | `TeamLayout currentTab="calendar"` |

Identique au calendrier global, mais :
- restreint aux événements de l'équipe ;
- `IcsFeedSettings` reçoit le `teamSlug` → l'URL est celle du **flux de l'équipe**, et le texte d'aide devient « Abonnez-vous au calendrier de cette équipe… ».

---

## 9. Détail d'une sortie (ride)

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/sorties/{rideSlug}` |
| **Route EN** | `/teams/{teamSlug}/rides/{rideSlug}` |
| **Fichier** | `src/pages/ride/RideDetailPage.tsx` |
| **Accès** | public |
| **Layout** | **hors `TeamLayout`** — `Container size="xl" py="xl"` (pas d'en-tête ni d'onglets d'équipe) |

> **Point design notable** : les pages de détail sortie / article / voyage / annonce **quittent le contexte visuel de l'équipe**. Seul le fil d'Ariane rappelle l'équipe.

### Structure verticale

**1. Bloc en-tête** — `Paper shadow="xs" p="lg" withBorder` :
- `EntityLogo size="lg"` (48 px, arrondi `md`, uniquement si logo défini) + `Title order={2}` (nom, tronqué sur une ligne) + **badge de statut** (`Brouillon` gris / `Publiée` vert / `Annulée` rouge).
- À droite, **si ADMIN/ORGANIZER seulement** : `Button.Group` « Modifier » + chevron ouvrant un menu (Publier / Dépublier / Annuler / Réactiver / Restaurer / Supprimer) — *hors périmètre*.
- **Description** : `MediaDisplay` (markdown riche + images + pièces jointes).
- Si brouillon avec publication programmée : ligne jaune `IconCalendar` « Publication prévue le … ».
- Ligne de méta : `IconCalendar` + **date et heure formatées** ; `IconUsers` + « N participants ».
- **Lieux** (si définis) : `IconMapPin` **vert** « Départ : {nom} (adresse) » et `IconMapPin` **rouge** « Arrivée : {nom} (adresse) ».

**2. Carte + groupes** — `SimpleGrid cols={{ base: 1, md: 2, xl: 3 }}` :
- **Colonne carte** (`order: 1`, donc **première en mobile**) — classe `.detail-map` : **collante (sticky) à partir de 1024 px**, sous le header ; **occupe 2 colonnes à partir de 1440 px**.
  - `RoutesMapView` : carte MapLibre affichant **le parcours principal de la sortie + le parcours de chaque groupe**, chacun d'une couleur d'une palette de 10 (`#566B13`, `#1d32a8`, `#732C7B`, …).
  - Marqueur de départ, marqueur d'arrivée.
  - **Overlay graphique d'altitude** en haut à droite (`Paper shadow="lg"`, 100 %/50 %/40 % de large selon le breakpoint, 120–150 px de haut) montrant le profil du parcours **survolé/sélectionné**.
  - **Survol croisé bidirectionnel** : survoler une carte de groupe met sa trace en avant (épaisseur 8 vs 5, opacité 0,9 vs 0,5) et bascule le graphique ; cliquer une trace sélectionne le groupe.
  - Hauteur : `clamp(300px, 52dvh, 560px)`. Chargée en lazy avec un `Skeleton height={500}`.
- **Colonne groupes** (`order: 2`) : `Title order={4}` **« Groupes »** puis une pile de `RideGroupCard`. Si aucun groupe : texte `dimmed` « Aucun groupe ».

**3. Encarts contextuels**
- Connecté mais **non membre** : `Alert` jaune « Rejoignez cette équipe pour participer aux sorties. » + lien vers l'équipe.
- **Non connecté** : `Alert` bleu « Connectez-vous et rejoignez cette équipe pour participer aux sorties. » + lien « Se connecter ».

**4. Commentaires** — **uniquement pour les membres** (§24.6).

### `RideGroupCard` — cœur de la participation
`components/ride/RideGroupCard.tsx`, `Paper withBorder p="md"` :

- **Ligne 1** : nom du groupe (tronqué, `fw={500}`) + badge `Inscrit` (primaire, clair) si l'utilisateur en fait partie. À droite l'action :
  - membre inscrit → bouton `variant="outline" size="xs"` **« Quitter »** ;
  - membre non inscrit, groupe non complet, sortie publiée → bouton plein `size="xs"` **« Rejoindre »** ;
  - groupe complet → badge gris **« Complet »** ;
  - non-membre / sortie non publiée → **aucune action**.
- **Ligne 2 (méta)** : `IconClock` heure de départ · `IconBolt` vitesse moyenne (formatée selon le système d'unités) · `IconArrowsMaximize` distance du parcours · `IconArrowUp` dénivelé positif · puis un bloc cliquable : `UserAvatarGroup` (5 avatars max + pastille « +N ») + `IconUsers` « X/Y participants » (ou « X participants » sans limite) + lien **« Voir tout »** si > 5 participants → **`ParticipantListModal`**.
- **Ligne 3 (parcours)**, si le groupe a un parcours (propre ou hérité de la sortie), liens `size="xs" dimmed` :
  - `IconMap` **« Voir le parcours »** → détail du parcours ;
  - `IconDownload` **« GPX »** ; `IconDownload` **« FIT »** (si les fichiers existent) ;
  - `IconDeviceMobile` **« Envoyer vers l'appareil »** → `Menu` listant les services GPS connectés (ex. Hammerhead), avec `Loader` pendant l'envoi. Visible uniquement si connecté **et** au moins un service lié.
- **États visuels** : carte **survolée** → bordure et fond en couleur primaire claire + `shadow="md"` ; carte **rejointe** → contour primaire permanent. Transition 150 ms.

**`ParticipantListModal`** : `Modal size="xl"` titré du nom du groupe ; compteur « N participants » en primaire ; liste (`ul`) de lignes avatar `md` + nom en `fw={600}` ; les organisateurs portent une pastille `IconShieldCheck` sur l'avatar et la mention « Organisateur du groupe ». Vide → `IconUsers` 48 px + « Aucun groupe ».

### Actions d'un membre non-admin
Rejoindre / quitter **un** groupe (un seul groupe à la fois : le bouton « Rejoindre » disparaît des autres groupes dès qu'on est inscrit quelque part), consulter les participants, voir/télécharger le parcours, l'envoyer à un appareil GPS, commenter (et supprimer ses propres commentaires), survoler la carte.

### Navigation
- **Entrante** : carte de publication (accueil / fil d'équipe), calendrier, section « Utilisée dans » d'un parcours, lien profond mobile.
- **Sortante** : détail du parcours, page d'équipe, connexion, téléchargements.

---

## 10. Détail d'un article (post)

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/articles/{postSlug}` |
| **Route EN** | `/teams/{teamSlug}/posts/{postSlug}` |
| **Fichier** | `src/pages/post/PostDetailPage.tsx` |
| **Accès** | public |
| **Layout** | `Container size="md" py="xl"` — **colonne étroite, format lecture** |

### Structure
`Stack` de deux blocs :
1. **`Paper withBorder p="lg" radius="md"`** :
   - `EntityLogo size="lg"` + `Title order={2}` (1 ligne) + badge de statut.
   - (ADMIN/ORGANIZER : `Button.Group` Modifier + menu — *hors périmètre*.)
   - **Corps de l'article** : `MediaDisplay` — c'est le contenu principal (markdown riche : titres, listes, tableaux `striped`, citations cyan, code, images d'assets pleine largeur arrondies, liens externes en nouvel onglet).
   - Si brouillon programmé : ligne jaune « Publication prévue le … ».
   - Pied : `IconCalendar` + date/heure `dimmed`.
2. **Commentaires** (membres uniquement).

**Introuvable** : `Paper` centré, `Title order={2}` « Article introuvable » + message + bouton vers le fil de l'équipe.

### Actions membre
Lire, télécharger les pièces jointes, commenter/répondre/supprimer ses commentaires.

---

## 11. Détail d'un voyage (trip)

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/voyages/{tripSlug}` |
| **Route EN** | `/teams/{teamSlug}/trips/{tripSlug}` |
| **Fichier** | `src/pages/trip/TripDetailPage.tsx` |
| **Accès** | public ; redirige vers l'équipe si `enableTrips` ou `enableRoutes` est désactivé |
| **Layout** | `Container size="xl" py="xl"` |

### Structure verticale

**1. En-tête** — `Paper withBorder p="lg"` :
- `EntityLogo size="lg"` + `Title order={2}` + badge de statut.
- **Actions de participation, visibles pour un membre** :
  - **« Participer »** (bouton plein) si membre, voyage publié, pas encore inscrit ;
  - **« Ne plus participer »** (`variant="outline"`) si déjà inscrit.
  - (+ groupe Modifier/menu pour ADMIN/ORGANIZER.)
- Description `MediaDisplay`.
- Ligne de méta : `IconCalendar` date/heure · `IconUsers` « N participants » · `IconStack2` « N étapes ».

**2. Carte + étapes** — `SimpleGrid cols={{ base: 1, md: 2, xl: 3 }}` :
- **Carte** (première en mobile, sticky ≥1024 px, 2 colonnes ≥1440 px) : `RoutesMapView` affichant **le parcours global du voyage s'il existe, sinon le parcours de chaque étape** (une couleur par étape). Survol croisé avec les cartes d'étape.
- **Liste des étapes** : `Title order={3}` « Étapes » puis pile de `TripStageCard`. Vide → « Aucune étape ».

**`TripStageCard`** (`Paper withBorder p="md" radius="md"`, **cliquable en entier** vers l'étape) :
- Ligne 1 : logo d'étape **ou** badge circulaire avec le **numéro d'étape** (`Badge size="lg" circle` primaire clair) + nom en `fw={600}` tronqué ; à droite `IconCalendar` + date/heure `dimmed`.
- Description markdown si présente.
- Lieux : `IconMapPin` vert « Départ : … » / rouge « Arrivée : … ».
- Si parcours : lien **« Voir le parcours »** (intercepte le clic pour ne pas suivre le lien de la carte) + `IconArrowsMaximize` distance + `IconArrowUp` dénivelé positif.
- **Survol** : bordure + fond primaire clair + `shadow="md"` (miroir avec la carte).

**3. Participants** (si ≥1) — `Paper withBorder p="lg"` : `Title order={3}` « Participants » puis une grappe de `Badge variant="light" color="gray" size="lg"` contenant chaque nom affiché. *(Pas d'avatars ici, contrairement aux sorties.)*

**4. Encarts non-membre / non-connecté** (mêmes patterns que la sortie).

**5. Commentaires** (membres).

### Actions membre
Participer / ne plus participer au voyage (au niveau du voyage entier, pas par étape), ouvrir une étape, ouvrir un parcours, commenter, survoler la carte.

---

## 12. Détail d'une étape

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/voyages/{tripSlug}/etapes/{stageSlug}` |
| **Route EN** | `/teams/{teamSlug}/trips/{tripSlug}/stages/{stageSlug}` |
| **Fichier** | `src/pages/trip/StageDetailPage.tsx` |
| **Accès** | public |
| **Layout** | `Container size="xl" py="xl"` + **`TripLayout` avec barre latérale d'étapes** |

### Structure verticale
1. **En-tête du voyage** — `Paper withBorder p="lg"` : logo (de l'étape sinon du voyage) + **lien `dimmed` vers le voyage** (nom du voyage) au-dessus du `Title order={2}` = nom de l'étape + badge de statut **du voyage**. (Bouton « Modifier » pour ADMIN/ORGANIZER.)
2. **`TripLayout`** — `SimpleGrid cols={{ base: 1, md: 4 }}` :
   - **Colonne 1 : `StageTabs`** — `Tabs orientation="vertical" variant="pills"` : un onglet **« Aperçu »** (`IconHome`) vers le voyage, puis un onglet par étape avec, en section gauche, le **logo de l'étape ou son numéro**, et en libellé le **nom (1 ligne) + la date en petit (opacité 0,7)**. En mobile la grille passe à 1 colonne → les onglets s'affichent **au-dessus** du contenu.
   - **Colonnes 2–4 : contenu** (`.trip-layout-content`, `grid-column: span 3` ≥1024 px).
3. **Encart de l'étape** — `Paper withBorder p="lg"` : `Badge size="xl" circle` avec le numéro d'étape + `Title order={2}` nom + `IconCalendar` date/heure ; description markdown ; puis les lieux en blocs verticaux (`IconMapPin` vert 20 px, sur-titre `dimmed` « Départ », valeur `fw={500}` ; idem rouge pour « Arrivée »).
4. **Parcours de l'étape** (si présent) : `Group` titre `Title order={3}` = nom du parcours + bouton `variant="subtle" size="sm"` **« Voir les détails du parcours »**, puis le composant `RouteDetailView` **sans le bloc info** (`showInfo={false}`) — carte interactive + profil altimétrique + boutons de téléchargement + statistiques + cols (§18).

### Actions membre
Naviguer entre étapes via la barre latérale, revenir à l'aperçu du voyage, télécharger GPX/FIT, envoyer vers un appareil, ouvrir la carte plein écran, ouvrir la fiche parcours.

### États d'erreur
Voyage introuvable → `Paper` centré « Voyage introuvable » + retour au fil d'équipe. Étape introuvable → « Étape introuvable » + retour au voyage.

---

## 13. Carte plein écran d'une étape

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/voyages/{tripSlug}/etapes/{stageSlug}/carte` |
| **Route EN** | `.../stages/{stageSlug}/map` |
| **Fichier** | `src/pages/trip/StageFullscreenMapPage.tsx` → `RouteFullscreenView` |
| **Layout** | **`bare`** : ni header, ni fil d'Ariane, ni footer |

Voir §19 pour la description détaillée de `RouteFullscreenView` (identique). Le titre de la barre est le **nom de l'étape**, le bouton retour ramène au détail de l'étape. Une étape sans parcours redirige vers le détail de l'étape.

---

## 14. Parcours d'une équipe — liste

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/parcours` |
| **Route EN** | `/teams/{teamSlug}/routes` |
| **Fichier** | `src/pages/route/RouteListPage.tsx` |
| **Accès** | public (onglet visible si `enableRoutes`) |
| **Layout** | `TeamLayout currentTab="routes"` |

### Structure verticale
1. En-tête + onglets d'équipe.
2. Ligne titre : `Title order={2}` **« Parcours »** ; à droite : (ADMIN/ORGANIZER : import GPX + « Créer un parcours » — *hors périmètre*) puis le **`RouteViewToggle`**.
3. **`RouteFilterPanel`** (voir §24.5) — barre bouton « Filtres » repliable.
4. **`RouteListContent`** : grille `cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg"`, **12 parcours par page**, `RouteCard`.
5. Pagination (marge supérieure `xl`).

**`RouteViewToggle`** : `SegmentedControl` à deux segments — `IconList` **« Liste »** / `IconMap` **« Carte »**. Il **conserve la query string** en changeant de vue, donc les filtres survivent au bascule liste↔carte.

### Données affichées (`RouteCard`)
- **Vignette du parcours** en pleine largeur : image générée du tracé, **variante claire ou sombre selon le thème**.
- (Nom d'équipe seulement sur la liste cross-équipe.)
- `EntityLogo size="md"` + titre + extrait markdown 150 caractères.
- `StatGroup` : `IconMap` **distance** · `IconArrowUp` **dénivelé positif** (unités selon la préférence utilisateur).
- Badges : **type de revêtement** (`Route` foncé / `Gravel` orange / `VTT` vert / `Mixte` teal) + **visibilité**.

### États
- **Chargement** : 6 `RouteCardSkeleton` (image 200 px).
- **Erreur** : `Paper` centré, `IconMap` 48 px rouge, « Erreur de chargement ».
- **Vide** : `Paper` centré, `IconMap` 48 px gris, « Aucun parcours » + description (ou « Aucun parcours ne correspond à votre recherche. » si filtres/recherche actifs) + éventuel bouton d'action.

### Actions membre
Filtrer (distance, dénivelé, relief, revêtement, orientation du vent), trier, rechercher, paginer, basculer en vue carte, ouvrir un parcours.

---

## 15. Parcours d'une équipe — carte

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/parcours/carte` |
| **Route EN** | `/teams/{teamSlug}/routes/map` |
| **Fichier** | `src/pages/route/RoutesMapPage.tsx` |
| **Layout** | `TeamLayout currentTab="routes"` |

### Structure
1. En-tête + onglets d'équipe.
2. Ligne titre « Parcours » + `RouteViewToggle` positionné sur « Carte ».
3. `RouteFilterPanel` **sans le bloc de tri** (`showSort={false}` — une carte n'ordonne rien).
4. **`RoutesTileMap`** : carte MapLibre alimentée par des **tuiles vectorielles serveur** (donc capable d'afficher des milliers de parcours, cas `n-peloton`).
   - Hauteur `clamp(400px, 72dvh, 820px)`, bordure + rayon `sm`.
   - Toutes les traces en une **couleur unique**, opacité 0,75, épaisseur interpolée selon le zoom (1 px à z5 → 4 px à z14). Une couche « hit » transparente de 12 px facilite le clic sur les traces fines.
   - **Survol** : la trace passe en couleur de survol, curseur `pointer`.
   - **Clic** : `Popup` MapLibre avec le **nom du parcours en lien** + distance + dénivelé positif en petit `dimmed`.
   - Le cadrage initial est calculé côté serveur (emprise des parcours filtrés) et **figé au montage** : resserrer les filtres ne recadre pas la carte sous les doigts de l'utilisateur.

---

## 16. Tous les parcours — liste

| | |
|---|---|
| **Route FR** | `/parcours` |
| **Route EN** | `/routes` |
| **Fichier** | `src/pages/route/AllRoutesPage.tsx` |
| **Accès** | public |
| **Layout** | `HomeLayout currentTab="routes"` |

Version **cross-équipes** de la liste. Différences avec §14 :
- Pas de titre « Parcours » ni de boutons de création. À la place, une ligne `Group justify="space-between"` : **`MembershipSelect`** à gauche (connecté uniquement : `Toutes les équipes` / `Membre` / `Organisateur` / `Admin`) et le `RouteViewToggle` poussé à droite (`ml="auto"`).
- Les cartes affichent le **nom de l'équipe** (`CardTeamLink`, sauf site mono-équipe).
- Même panneau de filtres, même grille 3 colonnes, même pagination 12/page.

---

## 17. Tous les parcours — carte

| | |
|---|---|
| **Route FR** | `/parcours/carte` |
| **Route EN** | `/routes/map` |
| **Fichier** | `src/pages/route/AllRoutesMapPage.tsx` |
| **Layout** | `HomeLayout currentTab="routes"` |

Identique à §15 mais cross-équipes : ligne `MembershipSelect` + `RouteViewToggle`, `RouteFilterPanel` sans tri, `RoutesTileMap` sur les tuiles globales filtrées par appartenance.

---

## 18. Détail d'un parcours

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/parcours/{routeSlug}` |
| **Route EN** | `/teams/{teamSlug}/routes/{routeSlug}` |
| **Fichier** | `src/pages/route/RouteDetailPage.tsx` (+ `components/route/RouteDetailView.tsx`) |
| **Accès** | public |
| **Layout** | `Box maw={1280} mx="auto" px="md" py="xl"` — hors `TeamLayout` |

### Structure verticale

**1. En-tête** : `EntityLogo size="lg"` + `Title order={1}` (nom du parcours), puis `MediaDisplay` (description markdown + pièces jointes). À droite, pour ADMIN/ORGANIZER : Modifier / Restaurer / Supprimer — *hors périmètre*.

**2. `RouteDetailView`** — le bloc central, réutilisé aussi sur les étapes de voyage :

- **a. Carte interactive** (`RouteMapView`, `Paper withBorder` avec `overflow: hidden`) :
  - Hauteur `clamp(300px, 52dvh, 560px)`.
  - **Trace colorée par gradient de pente** (segments recolorés selon l'inclinaison), épaisseur 8, opacité 0,8.
  - Marqueurs : **départ**, **arrivée**, **points de passage nommés** (waypoints), **bornes kilométriques**, et un **marqueur de survol** synchronisé avec le graphique.
  - Contrôle de navigation MapLibre (zoom) en **haut à gauche**.
  - **Sélecteur de fond de carte** (`MapStyleSwitcher`) en **haut à droite** : `ActionIcon` `IconMap` qui déplie un `Paper` de 160 px de large regroupé en sections — **Vecteur** (Light, Dark, IGN France), **Satellite** (VersaTiles, ESRI, IGN), **Raster** (OpenStreetMap, CyclOSM, IGN SCAN 25) — plus deux `Switch` : **Relief 3D** et **Ombrage**. La sélection est mémorisée en local.
  - **Bouton plein écran** : `ActionIcon` `IconArrowsMaximize` en haut à droite (à gauche du sélecteur de style).
  - **Overlay profil altimétrique** ancré en bas de la carte, hauteur `clamp(110px, 38%, 200px)`, fond semi-opaque, `shadow="lg"`. Survol du graphique → **réticule vertical** + marqueur sur la carte, et inversement.
  - Si le parcours n'a pas de tracé : zone grise centrée « Aucune donnée de tracé ».
- **b. Boutons de téléchargement** (`Group`, boutons `variant="default" size="sm"`) : **« Télécharger le GPX »**, **« Télécharger le FIT »**, et **« Envoyer vers l'appareil »** (`Menu` des services GPS connectés, connecté uniquement).
- **c. Statistiques** — `Group gap="xl"`, trois blocs icône + sur-titre `dimmed size="xs"` + valeur `size="lg" fw={700}` :
  - `IconMap` (primaire) **Distance**
  - `IconArrowUp` (vert) **Dénivelé positif**
  - `IconArrowDown` (rouge) **Dénivelé négatif**
- **d. Informations** : badges `size="lg"` — **type de revêtement** (vert) + **visibilité** (gris) — puis « Créé le : {date} » en `size="sm" dimmed`.
- **e. Cols et montées** (si détectés) : `Title order={4}` « Cols et montées (N) » puis une liste de lignes séparées par des bordures. Chaque ligne :
  - **Badge de catégorie plein** : `HC` violet, `Cat. 1` rouge, `Cat. 2` orange, `Cat. 3` jaune, `Cat. 4` vert.
  - Libellé « Montée N », puis la plage kilométrique (« du 12,4 au 18,1 km »).
  - À droite : **Dénivelé** (valeur en gras), **Pente moyenne** (%, 1 décimale), **Pente max** (%).

**3. « Utilisée dans »** (`RouteUsages`) — n'apparaît que s'il y a des usages :
- `Title order={2}` « Utilisée dans », grille `cols={{ base: 1, sm: 2 }}`.
- Une carte cliquable par usage : titre tronqué + `TypeBadge` (`Sortie` bleu / `Voyage` teal), `Stat` `IconCalendar` date/heure, et une mention `dimmed` du type « via le groupe X » / « via l'étape Y » lorsque l'usage est indirect.
- Squelettes (2 blocs de 96 px) pendant le chargement.

**4. Commentaires** — membres uniquement.

### Actions membre
Explorer la carte (zoom, pan, changement de fond, relief 3D, ombrage), survoler le profil, ouvrir le plein écran, télécharger GPX/FIT, envoyer vers un appareil GPS, ouvrir une sortie/voyage utilisant le parcours, commenter.

### États
- Chargement : squelettes (titre 32 px, carte 384 px, 3 blocs de 96 px).
- Introuvable : texte `dimmed` centré « Introuvable ».

---

## 19. Carte plein écran d'un parcours

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/parcours/{routeSlug}/carte` |
| **Route EN** | `/teams/{teamSlug}/routes/{routeSlug}/map` |
| **Fichier** | `src/pages/route/RouteFullscreenMapPage.tsx` → `components/route/RouteFullscreenView.tsx` |
| **Layout** | **`bare`** — occupe `100dvh`, aucune coquille applicative |

### Structure (3 bandes verticales, sans scroll)
1. **Barre d'outils, 48 px**, bordure basse :
   - `ActionIcon variant="subtle"` `IconArrowLeft` → retour au détail du parcours.
   - Titre `fw={600}` tronqué (nom du parcours / de l'étape), occupe l'espace restant.
   - À droite, en `dimmed` : `IconRoute` distance (toujours), `IconArrowUp` D+ et `IconArrowDown` D− (**à partir de `sm` seulement**).
2. **Carte**, `flex: 1` : `RouteTrackMap` avec padding de cadrage 50 px. Mêmes marqueurs et sélecteur de fond que la carte intégrée.
3. **Bande de profil altimétrique fixe, 160 px**, bordure haute, fond semi-opaque : graphique **rempli** avec **zoom/pan (molette, pincement) sur l'axe des distances**, synchronisé bidirectionnellement avec le zoom de la carte. Le survol est neutralisé pendant un geste de zoom pour éviter les sauts de réticule.

Parcours sans tracé → écran centré « Aucune donnée de tracé ». Cette page **gère elle-même le titre de l'onglet navigateur**.

---

## 20. Petites annonces — liste

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/annonces` |
| **Route EN** | `/teams/{teamSlug}/classifieds` |
| **Fichier** | `src/pages/ad/AdListPage.tsx` |
| **Accès** | public au niveau route ; **l'onglet n'est proposé qu'aux membres** et si `enableAds` |
| **Layout** | `TeamLayout currentTab="ads"` |

### Structure verticale
1. En-tête + onglets d'équipe.
2. Ligne titre : `Title order={2}` **« Annonces »** ; à droite, **pour tout membre**, bouton `+ Créer une annonce` — *(la création elle-même est hors périmètre, mais noter que ce bouton n'est pas réservé aux organisateurs)*.
3. Filtres : `SearchInput` « Rechercher des annonces » (flex, min 200 px) + `Select` type (180 px) : `Tous les types` / `Vente` / `Location` / `Recherche`.
4. Grille `cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg"`, **12 annonces par page**, `AdCard`. Pendant un rechargement de page la grille passe à **opacité 0,5** (au lieu de disparaître).
5. Pagination (affichée seulement si > 1 page).

### Données affichées (`AdCard`)
- Bandeau image **120 px** : première photo, sinon **dégradé orange→jaune avec icône `IconTag`**.
- `EntityLogo size="md"` + titre + extrait markdown 150 caractères.
- Pile de badges à droite : **type** (`Vente` vert / `Location` indigo / `Recherche` orange), **statut**, **visibilité**.
- `StatGroup` : `IconCurrencyEuro` **prix formaté en euros** (`1 200,00 €`, ou `… / jour|semaine|mois` pour une location, ou **« Prix à négocier »** si aucun prix) · `IconCalendar` date de création · `IconMapPin` localisation textuelle (si renseignée).

### États
- Chargement : 6 `AdCardSkeleton`.
- Vide : `Paper withBorder p="xl"` centré, `Title order={3}` « Aucune annonce » + texte contextuel (« Aucun résultat » si filtre/recherche actif).

---

## 21. Détail d'une annonce

| | |
|---|---|
| **Route FR** | `/equipes/{teamSlug}/annonces/{adSlug}` |
| **Route EN** | `/teams/{teamSlug}/classifieds/{adSlug}` |
| **Fichier** | `src/pages/ad/AdDetailPage.tsx` |
| **Layout** | `Container size="md" py="xl"` — colonne étroite |

### Structure — un seul `Paper withBorder p="lg"`
1. `EntityLogo size="lg"` + `Stack` : `Title order={2}` (1 ligne) puis une paire de badges **type d'annonce** + **statut**.
2. À droite : `Button.Group` **Modifier + menu** — visible pour **tout membre de l'équipe** (l'autorisation fine est gérée côté serveur) : Publier / Dépublier / Restaurer / Supprimer.
3. **Bloc prix** mis en avant : `Box p="md"` sur fond `body` arrondi, `IconCurrencyEuro` 24 px + prix en `size="xl" fw={700}`.
4. **Description** : `MediaDisplay` (markdown + photos + pièces jointes).
5. **Méta** : `IconCalendar` date de création · `IconMapPin` localisation (si renseignée).

**Pas de section commentaires sur les annonces.** Introuvable → `Paper` centré « Annonce introuvable » + bouton vers la liste.

---

## 22. Profil & préférences

| | |
|---|---|
| **Route FR** | `/profil` |
| **Route EN** | `/profile` |
| **Fichier** | `src/pages/auth/UserProfilePage.tsx` |
| **Accès** | **authentifié** |
| **Layout** | `Box maw={672} mx="auto"` — colonne étroite, hors onglets |

### Structure verticale
- Lien retour `‹ Retour` (`Anchor` bouton, `dimmed`, appelle `navigate(-1)`).
- `Paper shadow="sm" radius="md"` avec un bandeau de titre (bordure basse) : `Title order={2} size="h4"` **« Paramètres du profil »**, puis un `Stack p="lg"` de sections séparées par des `Divider` :

1. **Identité** : `UserAvatar size="xl"` avec **deux `ActionIcon` ronds superposés en bas à droite** — `IconCamera` (primaire) pour téléverser une photo (accepte aussi HEIC/HEIF) et `IconX` (rouge) pour la supprimer (uniquement si une photo existe). Chacun affiche un `Loader` pendant l'opération. À droite : nom affiché (`fw={500} size="lg"`) et e-mail (`dimmed`).
2. **Informations** — en lecture : deux paires libellé `dimmed size="sm" fw={500}` / valeur (« Nom affiché », « Email ») + bouton **« Modifier le profil »**. En édition : `TextInput` « Nom affiché » validé en direct (Zod), boutons **« Enregistrer »** (désactivé tant que le formulaire est invalide) et **« Annuler »**.
3. **Préférences** : `Title order={3} size="h5"` « Préférences » + **`UnitSystemSwitcher`** — libellé + `SegmentedControl` **Métrique / Impérial**. Le changement est **immédiat**, persisté en local **et** synchronisé au serveur.
4. **`PasskeyManager`** — gestion des clés d'accès (WebAuthn).
5. **`GpsConnectionsManager`** — connexion/déconnexion des services GPS (ce sont eux qui alimentent le bouton « Envoyer vers l'appareil » des parcours).
6. **`SocialConnectionsManager`** — liaison de comptes sociaux (Strava).
7. **`DataExportManager`** — export des données personnelles.
8. **Compte** : `Title order={3} size="h5"` « Compte », bouton `variant="default"` **« Se déconnecter »**, puis une **zone de danger** séparée par une bordure : titre rouge, texte explicatif `dimmed`, bouton `variant="outline" color="danger"` de suppression du compte → `ConfirmDialog` variante `danger`.

**Chargement** : `Loader size="lg"` centré.

> Note : le sélecteur de **langue** et le sélecteur de **thème** ne sont **pas** sur cette page — ils vivent uniquement dans le header / la navbar mobile.

---

## 23. Page 404

`src/pages/NotFoundPage.tsx` — rendue **dans** la coquille applicative.
`Center mih="60vh"` → `Title order={1}` **« 404 »**, `Text size="lg" dimmed` **« Page non trouvée »**, lien **« Retour à l'accueil »** en `fw={500}`.

---

## 24. Composants partagés

### 24.1 Carte de publication — `components/card/PublicationCard.tsx`
Composant le plus visible du produit (accueil + fil d'équipe). Basé sur `Card` = `Paper withBorder radius="md"` **entièrement cliquable** (`component={Link}`), avec transition d'ombre et de bordure au survol.

Structure verticale d'une carte :
1. **`CardImage` (160 px)** — chaîne de repli : première photo → sinon **dégradé + icône blanche 48 px** dépendant du type : Sortie = bleu→cyan (`IconBike`), Article = violet→rose (`IconArticle`), Voyage = teal→vert (`IconRoute`), Équipe = violet→indigo (`IconUsersGroup`), Annonce = orange→jaune (`IconTag`).
2. **`CardTeamLink`** (optionnel) : `IconUsers` + nom d'équipe `dimmed fw={500}` + `IconChevronRight` — **clic isolé** qui navigue vers l'équipe sans déclencher le lien de la carte.
3. **Ligne principale** : `EntityLogo size="sm"` + (titre `Title order={4}` + extrait markdown 150 caractères `dimmed`), et à droite une **pile verticale de 3 badges** : type, statut, visibilité.
4. **Ligne participants** (sorties et voyages) : `UserAvatarGroup` (5 max + « +N »), **barre de progression des places** pour les sorties, et à droite la **vignette du parcours** (`RouteThumbnail`, 160 px, variante claire/sombre selon le thème, bordure + rayon).
5. **`StatGroup`** en bas (`mt="auto"`) :
   - Sortie : `IconCalendar` date/heure · `IconUsers` « N participants » · `IconStack2` « N groupes »
   - Voyage : `IconCalendar` date/heure · `IconUsers` participants · `IconStack2` « N étapes »
   - Article : `IconCalendar` date/heure uniquement

**Barre de progression** (`PublicationCardProgress`) : n'apparaît que si au moins un groupe a une capacité. `Progress size="sm"` + texte `size="xs" dimmed`. Couleur **verte** < 80 %, **jaune** ≥ 80 %, **rouge** quand c'est complet ; le texte passe alors à « Complet ».

### 24.2 Système de badges — `components/card/common/`
Tous les badges sont des `Badge` Mantine `variant="light" size="sm"`, avec une **palette sémantique unique** :

| Famille | Valeurs → couleur |
|---|---|
| Type | Sortie → **bleu**, Article → **violet (grape)**, Voyage → **teal**, Vente → **vert**, Location → **indigo**, Recherche → **orange** |
| Statut | Brouillon → **gris**, Publiée → **vert**, Annulée → **rouge** |
| Rôle | Admin → **violet**, Organisateur → **bleu**, Membre → **gris** |
| Revêtement | Route → **foncé**, Gravel → **orange**, VTT → **vert**, Mixte → **teal** |
| Visibilité | Publique → **bleu** (`IconEye`), Non listée → **orange** (`IconEyeOff`), Équipe → **gris** (`IconUsers`) |

### 24.3 Squelettes — `CardSkeleton`
Paramétrable (image + hauteur, logo, nombre de `Stat`, nombre de badges, action). Toujours **6 exemplaires** dans les grilles à 3 colonnes. C'est le pattern de chargement dominant des listes ; les pages de détail utilisent plutôt un `LoadingPage` (spinner + libellé centrés sur `60vh`) ou des `Skeleton` de bloc.

### 24.4 Pagination — `components/common/Pagination.tsx`
- **Desktop** : `Pagination` Mantine centrée, `withEdges`, 1 frère + 1 borne, taille adaptative.
- **Mobile (ou variante compacte)** : uniquement `‹` + **« Page 3 / 12 »** + `›`, taille `xs`.
- Masquée si ≤ 1 page. Indexation interne à partir de 0, affichée à partir de 1.
- **Préchargement** : la page suivante **et** la page précédente sont chargées en arrière-plan → la pagination est quasi instantanée.
- Changer de page fait **remonter jusqu'au haut de la liste** (pas au haut de la page), via `useScrollToListTop`.

### 24.5 Panneau de filtres parcours — `components/route/RouteFilterPanel.tsx`
Pattern de filtrage le plus riche du site, partagé par les 4 pages parcours.

- **Barre toujours visible** : bouton `variant="default"` **« Filtres »** (`IconFilter` + chevron haut/bas) qui déplie/replie, et — **seulement si au moins un filtre est actif** — un bouton `variant="subtle"` **« Effacer »** (`IconX`).
- **Panneau replié par défaut** (`Collapse`) → `Paper withBorder p="md"` contenant :
  - `SearchInput` pleine largeur « Rechercher un parcours » (débouncé).
  - `SimpleGrid cols={{ base: 1, sm: 2, lg: 3, xl: 4 }}` :
    1. **Distance** — `RangeInput` : libellé + unité entre parenthèses, deux `NumberInput` de 100 px séparés par un tiret, pas de 5, conversion automatique km/miles.
    2. **Dénivelé positif** — même pattern, pas de 50, m/ft.
    3. **Relief** — `Select` : *(aucun)* / Plat / Vallonné / Montagneux.
    4. **Revêtement** — `Select` : *(aucun)* / Route / Gravel / VTT / Mixte.
    5. **Orientation du vent** — `Select` : *(aucun)* + 8 directions (N, NE, E, SE, S, SO, O, NO).
    6. **Tri** (masqué sur les vues carte) — `Select` (Distance / Dénivelé / Relief / Date) + bouton carré `variant="default"` qui **inverse le sens** (`IconChevronUp`/`IconChevronDown`). Défaut : **Date, décroissant**.
- « Effacer » remet à zéro **uniquement** les filtres de ce panneau : la recherche, l'appartenance et (en vue carte) le tri sont préservés.

### 24.6 Section commentaires — `components/comment/`
Présente sur **sorties, articles, voyages, parcours**. **Visible uniquement pour les membres de l'équipe.**

- Conteneur `Paper p="xl" shadow="xs" withBorder`, `Title order={4}` **« Commentaires (N) »**.
- **Formulaire** en haut : `Textarea` autosize (2 lignes) + `ActionIcon` d'envoi `size="lg"` rempli (`IconSend`, remplacé par un `Loader` pendant l'envoi). Bouton « Annuler » en dessous pour les réponses.
- **Liste** : chaque commentaire = `UserAvatar size="sm"` + nom `fw={500}` + **date relative** (« il y a 2 h ») en `size="xs" dimmed` + contenu **texte brut** (retours à la ligne préservés, pas de markdown).
- Actions par commentaire : **« Répondre »** (`IconMessage`, `variant="subtle" size="xs"` gris — **uniquement au premier niveau**) et **« Supprimer »** (`IconTrash`, rouge) visible pour **l'auteur** ou un **organisateur/admin** → `ConfirmDialog`.
- **Réponses** : indentation `ml="xl" pl="md"` + **filet vertical gauche** de 2 px. **Un seul niveau d'imbrication.**
- Vide : « Aucun commentaire pour le moment. Soyez le premier à commenter ! » centré `dimmed`.
- Chargement : `Loader` centré dans le `Paper`.

### 24.7 Rendu markdown — `MarkdownDisplay` / `MediaDisplay`
- **Mode aperçu** (cartes) : markdown **aplati en texte brut** puis tronqué à 150 caractères + « … », en `size="sm" dimmed`.
- **Mode complet** : `Box fz="md" lh={1.75}` avec un mapping complet vers les composants Mantine — titres h1–h3 avec `letter-spacing: -0.025em`, paragraphes `lh 1.625`, listes, **liens externes en nouvel onglet**, code inline (fond gris, texte rose) et blocs de code scrollables, **citations `Blockquote` cyan en italique**, **tableaux `striped highlightOnHover withTableBorder`** dans un conteneur à défilement horizontal, en-têtes de tableau en majuscules `dimmed`, séparateurs, images arrondies pleine largeur.
- Support d'une **directive d'image d'asset** (`::asset{...}`) permettant d'insérer des images téléversées à taille contrôlée.
- **`MediaDisplay`** ajoute sous le markdown un bloc **« Pièces jointes »** : `Paper withBorder p="sm"`, `IconPaperclip` + titre, puis une liste de liens (vignette carrée arrondie si image, sinon icône trombone) avec le nom de fichier tronqué et une icône de téléchargement à droite.

### 24.8 Avatars et logos
- **`UserAvatar`** : photo, sinon **initiales** (2 lettres max) sur fond primaire ; toujours `radius="xl"` ; **`Tooltip` avec le nom affiché**.
- **`UserAvatarGroup`** : `Avatar.Group`, N visibles (5 par défaut) + pastille grise « +N ».
- **`TeamAvatar`** : logo, sinon initiales sur une **couleur déterministe** issue d'un hash du nom (palette de 12 couleurs) ; tooltip du nom.
- **`EntityLogo`** : logo sans repli (rend `null` s'il n'y en a pas) ; tailles 24/32/40/48/64 px, `fit="cover"`, `radius="md"`. Utilisé pour sorties, articles, voyages, parcours, annonces, étapes.
- Les URLs d'images contiennent un jeton `{size}` remplacé à l'affichage → **images servies à la bonne résolution** (×2 pour les écrans haute densité).

### 24.9 Boîte de confirmation — `ConfirmDialog`
`Modal centered` titré, texte `dimmed`, deux boutons alignés à droite : « Annuler » (`variant="default"`) et le bouton de confirmation coloré selon la variante (`danger` rouge / `warning` jaune / `info` primaire) avec état de chargement. **C'est le seul mécanisme de confirmation du site** (pas de `confirm()` natif).

### 24.10 Champ de recherche — `SearchInput`
`TextInput type="search"`, **`IconSearch` à gauche**, **`CloseButton` à droite dès qu'il y a du texte**. Largeur 100 % en mobile, 320 px à partir de `sm` (ou pleine largeur si demandé). La saisie est **débouncée** avant d'être écrite dans l'URL.

### 24.11 Cartes MapLibre — `components/map/`
- **`PedalonsMap`** : wrapper commun. Contient toujours un `NavigationControl` (haut-gauche par défaut), la couche **relief 3D / ombrage**, et le **`MapStyleSwitcher`** (bas-gauche par défaut, haut-droite sur les fiches parcours).
- **9 fonds de carte** répartis en 3 groupes : *Vecteur* (Light, Dark, IGN France), *Satellite* (VersaTiles, ESRI, IGN), *Raster* (OpenStreetMap, CyclOSM, IGN SCAN 25).
- Le fond, le relief 3D et l'ombrage sont **mémorisés dans le navigateur** et s'appliquent à toutes les cartes.
- **Hauteurs normalisées** (`useMapHeight`), toutes en `clamp(min, N·dvh, max)` pour ne jamais dépasser la hauteur réelle de l'écran mobile :
  - `compact` `clamp(200px, 34dvh, 360px)` · `standard` `clamp(260px, 44dvh, 460px)` · `full` `clamp(300px, 52dvh, 560px)` · `fullscreen` `clamp(400px, 72dvh, 820px)`.
- **Trois familles de cartes** : trace unique colorée par pente (`RouteTrackMap`), traces multiples colorées par groupe/étape avec overlay graphique (`RoutesMapView`), tuiles vectorielles de masse avec popup (`RoutesTileMap`).

### 24.12 Autres
- **`LoadingPage`** : `Loader size="lg"` + libellé `dimmed`, centré sur `min-height: 60vh` — utilisé au chargement initial des pages de détail.
- **`MembershipSelect`** : `Select` d'appartenance réutilisable, **ne rend rien pour un visiteur non connecté**.
- **`RangeInput`** : paire min/max avec conversion d'unité transparente.
- **`Stat` / `StatGroup`** : icône 16 px alignée + texte `size="sm" dimmed` ; `StatGroup` les aligne horizontalement avec une marge verticale `xs`.

---

## 25. État global, données, formats

### Filtres dans l'URL
**Règle structurante du produit** : recherche, filtres, tri et pagination **ne sont jamais en état local** — ils vivent dans la **query string** (`useUrlFilters`). Conséquences pour le design :
- Toute vue filtrée est **partageable par simple copie d'URL** ;
- Le **retour arrière** ramène à la liste **dans le même état** (mêmes filtres, même page, même position de scroll) ;
- Les clés sont abrégées (`type`, `role`, `dmin`, `dmax`, `emin`, `emax`, `hill`, `surf`, `wind`, `sort`, `dir`, `p`, `s`) pour garder des URLs lisibles ;
- Modifier un filtre produit un `REPLACE` d'historique → **la page ne remonte pas**.

### Pagination
Toutes les listes du périmètre utilisent **12 éléments par page** (publications, équipes, parcours, annonces). **Pas d'infinite scroll sur le web** (contrairement au mobile) : pagination numérotée avec préchargement des pages adjacentes.

### Unités
Préférence **Métrique / Impérial** (Zustand + localStorage + synchronisation serveur). Elle affecte **toutes** les distances, dénivelés, vitesses et les bornes des filtres (les champs de filtre convertissent à la volée).

### Dates
- **Date + heure** localisées pour les publications, groupes, étapes, annonces.
- **Date longue** (« 12 mars 2024 ») pour la création d'équipe.
- **Date relative** (« il y a 2 h ») pour les commentaires.

### Rôles et droits (vue membre non-admin)
- `role` absent → **visiteur / non-membre** : lecture des contenus publics, aucune action de participation, **pas de commentaires**, pas d'onglets Calendrier/Annonces.
- `MEMBER` → participation (rejoindre un groupe de sortie, participer à un voyage), commentaires, calendrier d'équipe, annonces (**y compris la création d'annonce**), quitter l'équipe.
- `ORGANIZER` / `ADMIN` → ajoutent création et édition (**hors périmètre**) ; un ADMIN ne peut pas quitter l'équipe.

### Modules activables par équipe
`enableRides`, `enableTrips`, `enablePosts`, `enableRoutes`, `enableAds` — ils conditionnent les onglets, les options des filtres de type et les menus de création. Certaines pages redirigent si le module est coupé (ex. voyage sans `enableTrips`).

### Mode mono-équipe
Un domaine peut être **épinglé à une seule équipe** : l'onglet « Équipes » disparaît, le maillon « Équipes » du fil d'Ariane est supprimé, les cartes n'affichent plus le nom d'équipe, et l'URL est servie sans le préfixe `/equipes/{slug}`.

---

## 26. Annexe — pages périphériques

Hors du périmètre demandé mais atteignables depuis la navigation :

- **`/connexion`** (`LoginPage`) — carte centrée : « Bienvenue sur {app} », `TextInput` e-mail + `PasswordInput`, lien « Mot de passe oublié ? » aligné à droite, bouton pleine largeur, `Divider` « ou », bouton **Strava**, bouton **clé d'accès (passkey)** si le navigateur le supporte, bascule vers un formulaire d'inscription (nom, e-mail, mot de passe + confirmation) avec mention des CGU et de la politique de confidentialité.
- **`/confidentialite`**, **`/cgu`** — pages légales en markdown, dans la coquille applicative.
- **`/completer-le-compte`** — cible du bandeau orange « e-mail manquant ».
- **`/outils-gpx…`** — outils GPX (hors périmètre).
- **`/equipes/{slug}/admin…`**, **`/plateforme…`** — administration (hors périmètre).
