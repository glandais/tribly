# Agenda et Publications — la page d'équipe découpée par temporalité

Écrit le 6 octobre 2026. Ledger `API-85`, `API-86`, `WEB-68`, `WEB-69`, `MOB-60`, `BRAND-6`.
**Exécuté** le 6 octobre 2026 et archivé ; les restes sont `API-87`, `API-88`, `MOB-61` et
`API-89` (écarté) au ledger.

## 1. Ce qui ne va pas aujourd'hui

La page d'une équipe a accumulé les vues d'une même liste — le fil (`?tab=publications`), les
onglets « Sorties » et « Voyages » (`WEB-64`), le calendrier, le tableau de bord — sans qu'aucune
ne réponde à la question que se pose celui qui la regarde. Les sorties et les publications n'ont
pas la même temporalité : une publication se lit comme un blog, de la plus récente à la plus
ancienne ; une sortie intéresse par sa proximité, la prochaine d'abord. Le fil mélange les deux et
leur impose les filtres de l'une.

| # | Défaut | Où |
|---|---|---|
| D1 | « À venir » trie du plus lointain au plus proche : il n'envoie que `from=now` et garde le tri par défaut, décroissant. La sortie de dimanche est en bas de page, celle de juin en haut. Le tableau de bord, lui, trie croissant (`TeamDashboardService`, `ascending(true)`) : deux écrans, deux ordres. | `publicationScopeToParams`, `frontend/src/hooks/filters/publicationFilters.ts` |
| D2 | « Je participe » n'a pas de borne de date : les participations passées noient la prochaine. | idem |
| D3 | « À venir » compare la **date de départ** à maintenant (`te.dateTime >= :from`) : une sortie partie il y a dix minutes en sort, un voyage commencé hier aussi, alors qu'il dure encore. | `TeamEntityRepository` (filtre `from`) |
| D4 | Aucune heure de fin n'existe nulle part : le calendrier et l'ICS envoient `end = null`, les étapes de voyage sont « journée entière ». Un voyage n'occupe dans le calendrier que son premier jour. | `CalendarService`, `PublicationIcsService` |
| D5 | « À venir / Je participe » sont proposés sur un fil qui contient des publications, pour qui ils n'ont pas de sens. | `PublicationListPage` |
| D6 | Le fil est la seule page d'accueil d'un visiteur (le tableau de bord est réservé aux membres, `ForbiddenException` si `role == null`) : c'est la seule raison qu'il a encore d'exister. | `TeamHomePage`, `TeamDashboardService` |
| D7 | Trois vues des mêmes objets datés : « Sorties », « Voyages », « Calendrier ». | `useTeamNavItems`, `team_sections.dart` |
| D8 | La liste des parcours a deux sélecteurs segmentés qui répondent à la même question (« comment je regarde cette liste ») à deux endroits : « Liste / Carte » change de page dans la ligne du titre, « Vignettes / Compact » change un paramètre au-dessus des résultats. | `RouteViewToggle`, `RouteDensityToggle`, `RouteListPage` |
| D9 | Les icônes des sections ne suivent pas la charte (web : `IconNews`, `IconMap2`, `IconTags` ; charte : `IconArticle`, `IconRoute`, `IconTag`) et le mobile en a d'autres encore (`dynamic_feed`, `sell`…). | `useNavItems.ts`, `team_sections.dart`, `docs/BRANDING.md` §6 |

## 2. La cible

```
 [Logo] Nom de l'équipe
 ┌────────────┬────────┬──────────────┬──────────┬──────────┬─────────┬──────────┐
 │ Tableau de │ Agenda │ Publications │ Parcours │ Annonces │ Membres │ À propos │  + pages perso
 │   bord     │        │              │          │          │         │          │
 └────────────┴────────┴──────────────┴──────────┴──────────┴─────────┴──────────┘
```

| Section | Contenu | Filtres, tri | Icône web (charte §6) |
|---|---|---|---|
| Tableau de bord | page d'accueil de l'équipe, **pour tout le monde** (§3.3) | — | `IconLayoutDashboard` |
| **Agenda** | sorties **et** voyages | **À venir** (défaut) · **Je participe** · **Passées** ; type Tout / Sorties / Voyages ; tags ; recherche. Vues Vignettes · Lignes · Calendrier (§4) | `IconCalendarEvent` |
| **Publications** | posts seuls | aucun filtre de date ; plus récente d'abord ; tags ; recherche | `IconArticle` |
| Parcours | inchangé, sauf le sélecteur de vue (§4) | Vignettes · Lignes · Carte | `IconRoute` |
| Annonces | inchangé | | `IconTag` |
| Membres | inchangé | | `IconUsers` |
| À propos | inchangé | | `IconInfoCircle` |
| Pages perso | inchangées, icône générique | | `IconFileText` |

Les icônes sont celles des **sections** : les pages perso gardent toutes la même (décision du
6 octobre 2026). Le mobile prend l'équivalent de chacune dans `PdlIcons` (à compléter s'il en
manque), pas une icône Material choisie à part.

### 2.1 Le vocabulaire

**Agenda** = sorties + voyages (décision du 6 octobre 2026). « Sorties » est exclu : c'est le
terme officiel du *Ride*, et un filtre « Sortie / Voyage » sous un onglet « Sorties » dirait le tout
et la partie avec le même mot. « Événements » (générique, pas cycliste), « Programme » (ne parle
que du futur, mal avec « Passées ») et « Activités » (une trace enregistrée sur Strava ou Garmin)
sont écartés. **Publications** = posts, ce que dit déjà le lexique (Post → « publication »).

À ajouter au lexique de `docs/BRANDING.md` §8 : « Agenda — sorties et voyages ; ne pas écrire
« événements », « programme » ». Titre d'état vide : « Rien à l'agenda pour le moment ».

### 2.2 Les filtres de l'Agenda

| Filtre | Ce qu'il retient | Tri |
|---|---|---|
| **À venir** (défaut) | non terminées : `end >= now` — une sortie en cours y reste, marquée « En cours » | départ croissant |
| **Je participe** (connecté seulement) | non terminées **et** où je suis inscrit | départ croissant |
| **Passées** | terminées : `end < now` | départ décroissant |

« Tout » disparaît (décision du 6 octobre 2026) : trié décroissant, il commençait par la sortie la
plus lointaine, traversait aujourd'hui et finissait dans l'archive — un ordre que personne ne
cherche. Le besoin derrière, c'est l'archive, que « Passées » sert avec un tri évident. Les
participations passées d'un membre restent dans son profil (« Mes sorties », `MyRidesPage`).

En vue Calendrier, on navigue par mois : « À venir » et « Passées » n'ont plus de sens, le filtre se
réduit à « Tout / Je participe ».

## 3. Backend et contrat (`API-85`, `API-86`)

### 3.1 La fin d'une sortie et d'un voyage, en base (`API-85`)

Colonne `end_date_time` (nullable) sur la table des `TeamEntity`, indexée avec l'équipe. Règle,
calculée par **un seul** service (`PublicationEndCalculator` ou équivalent) :

- **Sortie** : le plus tard des groupes, chacun valant son départ (`RideGroup.time`, à défaut celui
  de la sortie) + distance de son parcours (celui du groupe, à défaut celui de la sortie) ÷ sa
  `averageSpeed`. Groupe sans vitesse ou sans parcours : départ + durée par défaut. Sortie sans
  groupe : départ + durée par défaut.
- **Voyage** : la même règle appliquée à **la dernière étape** (son `dateTime`, son parcours, sa
  `averageSpeed`, `API-81`). Voyage sans étape : départ + durée par défaut.
- **Publication** : `null`.

La durée par défaut est une constante unique, documentée : 3 h (§7).

**Où la recalculer** — c'est le prix d'une valeur stockée, et chaque oubli rend une sortie
« passée » ou « à venir » à tort :

1. enregistrement d'une sortie et de ses groupes (création, modification, gabarit appliqué,
   publication programmée) ;
2. création, modification, suppression, réordonnancement d'une étape ;
3. remplacement du GPX d'un parcours : toutes les sorties, groupes et étapes qui le citent ;
4. import biketeam (`MIG`) : les sorties importées passent par le même calcul.

Un test par point d'entrée vérifie la valeur stockée (pas seulement le calcul).

**Déploiement à chaud** (ancien et nouveau backend partagent la base une minute) : la migration
Flyway ajoute la colonne nullable et l'index, sans contrainte ; le remplissage précis demande du
Java (distances, vitesses), il se fait par un recalcul au démarrage **idempotent** (ne traite que
les lignes `end_date_time is null` d'une sortie ou d'un voyage, par lots). Les requêtes lisent
`coalesce(te.endDateTime, te.dateTime + durée par défaut)` : les lignes que l'ancien backend écrit
pendant la minute de recouvrement restent correctement classées.

### 3.2 Le paramètre `when` et le champ `endDateTime`

- `GET /api/teams/{slug}/publications` (et la liste transverse `GET /api/publications`) gagnent
  `when=UPCOMING|PAST`. **Le serveur fixe le tri** : `UPCOMING` croissant, `PAST` décroissant ;
  `sortDir` reste prioritaire s'il est donné. `when` combiné à `participating=true` donne
  « Je participe ». Le client cesse d'envoyer `from=now` pour ces filtres — `hourAlignedNowIso`
  ne sert plus qu'aux appelants qui veulent une vraie borne.
- `RideDto` et `TripDto` gagnent `endDateTime`. Le calendrier (`CalendarEventDto.end`) et l'ICS
  (`DTEND`) le reprennent : un voyage occupe toutes ses journées.
- Le tableau de bord (« Mes prochaines », « Prochaines sorties ») passe sur la même règle
  (`end >= now`) au lieu de `from(now)` : une sortie en cours n'en disparaît plus.

Ajouts seulement : version **mineure** (`10.15.0` au moment de l'écriture), clients régénérés.

### 3.3 Le tableau de bord pour tous (`API-86`)

`GET /api/teams/{slug}/dashboard` cesse de répondre 403 à un non-membre. Il rend la partie
publique : prochaines sorties, dernières publications, nouveaux parcours — par les mêmes requêtes,
donc les mêmes règles de visibilité (équipe `TEAM` invisible, entités non `PUBLIC` exclues pour un
visiteur). `myUpcoming`, `latestAds` (l'onglet Annonces n'est offert qu'aux membres, web et mobile), `organizer` et
`admin` restent absents. **Relecture sécurité** (agent `security-reviewer`) avant livraison : c'est
un point d'entrée qui s'ouvre aux anonymes. Les tests de nombre de requêtes du tableau de bord
restent verts.

C'est ce qui permet de supprimer le fil : un visiteur veut savoir si l'équipe est active, quand
elle roule et ce qu'elle raconte, exactement ce que le tableau de bord montre.

## 4. Le sélecteur de vue (`WEB-69`)

Un seul composant, `ListViewSwitch`, à droite de la barre de filtres, qui remplace `RouteViewToggle`
et `RouteDensityToggle` :

| Page | Vues |
|---|---|
| Parcours | Vignettes · Lignes · Carte |
| Agenda | Vignettes · Lignes · Calendrier |

- Icônes seules, chacune avec son infobulle et son `aria-label` ; le groupe a un libellé
  (« Affichage »). Le nombre de résultats reste à gauche sur la même ligne.
- La vue vit dans l'URL (`?view=`), comme la densité aujourd'hui : un lien partagé garde la vue.
  Changer de vue garde les filtres. Les vues Carte et Calendrier gardent leur chemin
  (`routesMap`, `teamCalendar`) pour les liens existants ; le sélecteur y navigue au lieu de
  changer un paramètre, sans que l'utilisateur voie la différence.
- Le calendrier de l'équipe reste réservé aux membres (web et mobile) : pour un visiteur, l'Agenda
  n'a que les vues Vignettes et Lignes (§7).

## 5. Web (`WEB-68`)

- **Routes** (`contracts/routes.yaml`, puis `pnpm generate-routes`) :
  - `teamAgenda` : `/equipes/{teamSlug}/agenda`, `/teams/{teamSlug}/agenda` ; `web`, `mobile`,
    `deeplink`.
  - `teamPosts` : `/equipes/{teamSlug}/articles`, `/teams/{teamSlug}/posts` ; idem. `articles`
    et non `publications` : c'est le parent des adresses de détail existantes
    (`/equipes/{teamSlug}/articles/{postSlug}`), ce qui garde la hiérarchie des liens profonds ;
    le lexique ne régit que le texte affiché, pas les URL.
  - Le détail d'une sortie et d'un voyage garde son adresse (`…/sorties/{rideSlug}`,
    `…/voyages/{tripSlug}`) : `teamAgenda` en devient l'ancêtre dans `_deepLinkHierarchies` et le
    fil d'Ariane, à la place de `teamRides`/`teamTrips`.
  - `teamCalendar` reste : c'est l'Agenda en vue Calendrier.
  - `teamRides`, `teamTrips` (livrés par `WEB-64`) restent dans le contrat, en redirection.
- **Redirections** (remplacement de l'entrée d'historique, côté SSR aussi) :

  | Ancienne adresse | Nouvelle |
  |---|---|
  | `team?tab=publications&type=post` | `teamPosts` |
  | `team?tab=publications&type=ride\|trip` | `teamAgenda?type=…` |
  | `team?tab=publications` (sans type) | `team` (le tableau de bord) |
  | `w=me` / `w=upcoming` / `w=all` | « Je participe » / « À venir » / « À venir » |
  | `teamRides` / `teamTrips` | `teamAgenda` / `teamAgenda?type=trip` |

- **Pages** : `PublicationListPage` se scinde en `TeamAgendaPage` (scope `upcoming` par défaut,
  `PublicationScopeControl` réordonné « À venir / Je participe / Passées », sélecteur de type
  Tout/Sorties/Voyages, tags du type choisi — sans type, pas de tags, comme le fil aujourd'hui) et
  `TeamPostsPage` (sans scope). `TeamHomePage` rend toujours le tableau de bord ; `TEAM_FEED_TAB`,
  `teamFeedPath` et la branche « fil » disparaissent. Le changement de défaut du scope passe par
  `publicationApiParams`, page et prefetch ensemble — c'est l'avertissement de son commentaire.
- **Tableau de bord** : « Voir tout » de « Mes prochaines » → `teamAgenda?w=me`, de « Prochaines
  sorties » → `teamAgenda`, de « Dernières publications » → `teamPosts`.
- **Onglets** (`useTeamNavItems`) : tableau du §2, icônes de la charte.
- **Le fil d'accueil transverse** (`home`, toutes équipes) n'est pas touché ici (§7).

## 6. Mobile (`MOB-60`)

- `TeamSectionKind` perd `feed` et `calendar`, gagne `agenda` et `posts`. Ordre et visibilité
  comme au web ; icônes `PdlIcons` alignées (`BRAND-6`).
- `resolveTeamRootSection` rend le tableau de bord à tout le monde (`API-86`) ; `kTeamTabParam`
  reste lu pour les liens profonds d'avant (mêmes redirections qu'au §5).
- L'Agenda reprend `PublicationFeedView` avec les trois filtres de date (aujourd'hui le fil
  d'équipe mobile n'a pas « À venir / Je participe » du tout) et une bascule Liste / Calendrier
  qui rend `CalendarPage` filtré sur l'équipe.
- `router.dart` : `teamAgenda`, `teamPosts`, et `teamRides`/`teamTrips`/`teamCalendar` routés vers
  l'Agenda dans la bonne vue ; `internalRouteTemplates` et `_deepLinkHierarchies` suivent. Le type
  initial reste local à la vue, jamais recopié dans `publicationFeedTypeProvider` (leçon de
  `WEB-64`).

## 7. Décisions prises le 6 octobre 2026

1. **Durée par défaut** d'une sortie, d'un groupe ou d'une étape sans vitesse ou sans parcours :
   **3 h**.
2. **Pas de calendrier pour les visiteurs** : l'Agenda d'un non-membre n'a que les vues
   Vignettes et Lignes, et le sélecteur de vue n'y propose pas Calendrier.
3. **Le fil d'accueil transverse** (`home`, « Fil » de la navigation principale), qui mélange lui
   aussi sorties et publications de toutes les équipes, est **hors de ce plan**.

## 8. Ordre de livraison

1. `API-85` (colonne, calcul, `when`, `endDateTime`) puis `API-86` (tableau de bord public), un
   contrat chacun ou ensemble ; tests backend lancés par l'utilisateur.
2. `WEB-69` (sélecteur de vue, d'abord sur Parcours seul : il se recette sans le reste).
3. `WEB-68` + `BRAND-6` côté web : routes, redirections, Agenda, Publications, suppression du fil.
   e2e à adapter : `team-dashboard`, `list-filters`, `pagination`, `pinned-host`, `flow-posts`,
   `routes-render`.
4. `MOB-60` + `BRAND-6` côté mobile, publié en même temps que le web : les liens profonds des deux
   doivent atterrir au même endroit.
