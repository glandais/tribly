# Fuseau horaire des événements — une heure de départ est une heure du lieu

Écrit le 6 octobre 2026. Ledger `API-60`. Les défauts de l'audit qui ne dépendent pas de ce chantier
sont des entrées à part : `API-90`, `API-91`, `API-92`, `WEB-70`, `WEB-71`.

Rebasé le 6 octobre 2026 sur l'Agenda d'équipe (`API-85` : fin stockée des sorties et des
voyages) ; ce que ça change ici est signalé au fil du texte.

**Ce plan rouvre une décision.** Le 2 octobre 2026, `API-60` (« fuseau d'équipe ou dates zonées au
contrat ») avait été écarté avec le propriétaire au profit de la préférence `UserDto.timezone`
appliquée par les deux clients (`API-15`) : une équipe est presque toujours mono-fuseau, et un
fuseau d'équipe coûtait un réglage, une colonne et une seconde règle de rendu. La note d'alors disait
quand rouvrir : « si des voyages à l'étranger rendent l'heure locale de l'étape nécessaire — c'est
alors le fuseau du lieu de départ qu'il faudrait exposer, pas celui de l'équipe ». C'est exactement
le second scénario du §1 : la préférence de l'utilisateur ne suffit pas, parce qu'elle dit dans quel
fuseau *lire*, jamais dans quel fuseau l'heure a été *tapée*. Le plan suit la note : le fuseau
résolu est celui du lieu de départ (§4), celui de l'équipe n'est que le repli. Ce que `API-15` a
livré reste vrai pour tout ce qui n'est pas un rendez-vous (§7).

## 1. Ce qui ne va pas aujourd'hui

**Le stockage est sain.** Toutes les dates sont des `Instant` en `timestamptz`, sérialisées en ISO
avec `Z` ; aucun `LocalDateTime`, aucune colonne `timestamp` sans fuseau, aucune migration de type.
Deux exceptions seulement : `ride_groups.time` et `ride_template_groups.time` (`LocalTime`,
`time(0)`), et `weather_daily.date` (`LocalDate`, interne).

**C'est le fuseau qui manque.** Une heure de départ est une heure *murale* d'un lieu (« 9 h 30 au
parking »), or :

| Étape | Aujourd'hui | Où |
|---|---|---|
| Saisie | l'heure tapée est lue dans le fuseau **de celui qui saisit** (`user.timezone`, sinon le navigateur), convertie en instant UTC ; le fuseau est perdu | `frontend/src/components/common/InstantDateTimePicker.tsx:27` |
| Affichage | l'instant est converti dans le fuseau **de celui qui regarde**, sans jamais le nommer | `useEffectiveTimezone` (`frontend/src/utils/dateFormat.ts:37`), `AppFormatters` (`mobile/lib/core/utils/formatters.dart`) |
| Heure de groupe | `LocalTime` sans fuseau, affichée brute ; seuls la météo, les appareils et le calcul de la fin (`API-85`) la lisent, dans le fuseau du lieu de départ — **UTC** sans lieu | `RideGroupCard.tsx:168`, `ride_group_card.dart:170`, `RideWeatherCalculator.legStart`, `RideWeatherPlans.java:108`, `PublicationEndCalculator.departureZone` |
| Sortie, voyage, étape, équipe, domaine | aucun fuseau stocké | — |
| Replis | Europe/Paris en dur (notifications, webhooks, iCal, mail d'export, biketeam), UTC ailleurs (`TimezoneService`, SEO, crons) | voir `API-90`, `API-92`, `WEB-70` |

Les deux scénarios qui ont déclenché l'audit :

- **À Tokyo, je regarde la sortie de mon groupe parisien (9 h 30 à Paris).** Sans fuseau de profil,
  la sortie affiche 16:30 (heure de Tokyo, sans le dire) et son groupe 09:30 (brut) : deux fuseaux
  sur le même écran.
- **Depuis Paris, je prépare une étape au Japon à 8 h.** Il faut taper 01:00 (conversion de tête,
  rien ne le signale). Si je tape 08:00, l'étape est stockée à 08:00 Paris = 15:00 Tokyo, et c'est ce
  que mon téléphone affiche une fois sur place. La météo de l'étape est calculée pour le mauvais
  instant.

## 2. Les décisions

1. **Chaque équipe a un fuseau, obligatoire.** `Europe/Paris` pour les équipes existantes — juste
   pour toutes : au 6 octobre 2026, la production ne compte que des équipes françaises ;
   pré-rempli avec le fuseau du navigateur du créateur à la création ; celui de biketeam
   (`team.timezone`, déjà lu par `SnapshotBiketeamSource`) pour une équipe migrée.
2. **Toutes les dates restent des instants.** Tri, filtres, index, calendrier, rappels, météo : rien
   ne change de ce côté.
3. **Chaque entité d'équipe stocke son fuseau** (`team_entities.timezone`). Ce n'est **pas un
   cache** : c'est le *fuseau de saisie*, figé à l'enregistrement de l'entité et recalculé
   seulement par un nouvel enregistrement, jamais par effet de bord (un parcours partagé dont on
   remplace le GPX ne déplace pas les sorties qui l'utilisent — `RouteService.refreshUsersOf`
   recalcule leur **fin** (`API-85`), jamais leur fuseau ni leur départ). Avec l'instant, il permet de
   réafficher exactement l'heure murale saisie. **Ne pas en faire un cache recalculé** : l'instant
   ne bougeant pas, l'heure affichée changerait sans que personne n'y touche.
4. **Le backend est la seule autorité du fuseau.** Une requête envoie des heures murales sans
   offset, plus les lieux et le parcours ; le backend résout le fuseau (§4), en déduit les instants,
   stocke les deux. Le front ne calcule jamais de fuseau : il en *demande* un pendant l'édition, pour
   étiqueter le champ (§6). S'il se trompe, seule l'étiquette est fausse, jamais la donnée.
5. **Changer de lieu garde l'heure murale.** Puisque chaque enregistrement renvoie les heures
   murales, passer le départ de Paris à Tokyo garde « 08:00 » et déplace l'instant. C'est le
   comportement voulu.
6. **L'heure d'un groupe devient un instant** (`ride_groups.start_at`). Saisie en `LocalTime` dans le
   fuseau de la sortie, sur la date locale de la sortie ; recalculée à chaque enregistrement de la
   sortie (changer la date de la sortie déplace ses groupes, puis sa fin). `ride_template_groups.time` **reste un
   `LocalTime`** : un modèle n'a pas de date, l'heure n'y est pas ambiguë.
7. **À l'affichage, deux familles** (§7) : un *rendez-vous* (départ de sortie, de groupe, d'étape,
   dates d'un voyage, publication programmée) se lit dans le fuseau de l'entité, avec une mention
   quand il diffère de celui du lecteur ; un *horodatage* (création, commentaire, notification,
   « il y a 2 h ») se lit dans le fuseau du lecteur.

## 3. Le modèle de données

| Table | Colonne | Type | Remarque |
|---|---|---|---|
| `teams` | `timezone` | `varchar(64) NOT NULL DEFAULT 'Europe/Paris'` | validé par `ZoneId.of`, comme `users.timezone` |
| `team_entities` | `timezone` | `varchar(64)`, nullable le temps d'une version (§8) | une seule colonne : la table est en `SINGLE_TABLE` (sorties, voyages, étapes, articles, annonces, parcours, pages) |
| `ride_groups` | `start_at` | `timestamptz`, nullable le temps d'une version | remplace `time`, supprimée à la version suivante |

`team_entities.end_date_time` (`V63`, `API-85`) ne change pas : c'est un instant, écrit par
`PublicationEndCalculator` seul. Les migrations de ce plan prennent les numéros suivants (`V64`…).

Toutes les entités de `team_entities` reçoivent un fuseau, même celles dont la `dateTime` n'est pas
saisie (annonce, parcours, page : `dateTime` = maintenant). Pour elles, c'est le fuseau de l'équipe,
et leurs dates s'affichent comme des horodatages.

Au passage : `users.timezone` passe de `varchar(40)` à `varchar(64)`, la longueur de
`weather_cells.timezone` et de `ClientContextDto.timezone`.

## 4. La résolution du fuseau

Un service Java (`EventTimezoneResolver`, nom libre) au-dessus de `TimezoneService` :
`resolve(team, point?)` rend le fuseau du point, ou celui de l'équipe si le point est absent ou hors
de toute zone. **`TimezoneService` ne retombe plus sur UTC** : il rend un `Optional`, le repli est
l'équipe.

Le point de chaque entité, premier trouvé :

| Entité | Chaîne |
|---|---|
| Sortie | `start` → 1er point de `route` → *(équipe)* |
| Étape | `startPlace` → 1er point de `route` → **fuseau de l'étape précédente** → 1er point de `Trip.route` → *(équipe)* |
| Voyage | fuseau de la 1re étape → 1er point de `route` → *(équipe)* |
| Article, annonce, parcours, page | *(équipe)* |
| Groupe | fuseau de la sortie |

L'étape précédente plutôt que le voyage : une étape 2 au Japon, saisie avant d'avoir choisi son
lieu, ne doit pas hériter du fuseau du parcours du voyage (Paris). C'est aussi ce qui rend « ajouter
une étape = J+1 à la même heure » juste d'un pays à l'autre.

Une étape qui change de fuseau en route (frontière) : le départ est dans le fuseau du point de
départ ; les passages et l'arrivée calculés (météo) sont des instants, affichés dans le fuseau du
point concerné comme la météo le fait déjà par cellule.

**Une seule source de fuseau par lieu.** Le départ d'un groupe est aujourd'hui relu à trois
endroits, chacun avec sa résolution de fuseau : la météo (`RideWeatherPlans`, et
`weather_cells.timezone` en SQL pour la liste), les appareils (`DeviceRouteService.departureZone`)
et la fin stockée (`PublicationEndCalculator.departureZone`). Une fois `start_at` stocké, aucun
n'a plus à relire de fuseau ni à appeler `legStart` : tous partent du même instant, et
`legStart` disparaît.

## 5. Le contrat

- **Requêtes** (`RideRequest`, `TripRequest`, `StageRequest`, `PostRequest`) : `dateTime` et
  `publishAt` deviennent des heures murales sans offset (`LocalDateTime`, `2026-10-11T08:00:00`).
  `GroupRequest.time` reste un `LocalTime`.
  **Transition** : pendant une version, le backend accepte encore un instant avec `Z` ou un offset
  (ancien SPA encore chargé pendant le déploiement) et le ramène à l'heure murale dans le fuseau
  résolu. Ensuite il le refuse en 400.
- **Réponses** : `endDateTime` (`API-85`) ne change pas. `timezone` ajouté sur `RideDto`, `TripDto`, `TripStageDto`, `PostDto` et les résumés de
  liste qui portent une date de rendez-vous (`RideListSummary`, `TripListSummary`, `RouteUsageDto`, `CalendarEventDto`,
  `NotificationDto` si elle porte un `subjectDateTime`, `DeviceRideDto`). `RideGroupDto` et `RideGroupSummaryDto` gagnent
  `startAt` (instant) ; `time` reste servi, en heure murale du fuseau de la sortie, le temps que les
  clients passent à `startAt`.
- **Équipe** : `TeamDetailDto.timezone` (et dans les résumés d'équipe que le front lit pour
  éditer), modifiable par les administrateurs de l'équipe via `TeamRequest`.
- **Nouvel appel** : `GET /api/teams/{teamId}/timezone?lat=&lon=` (organisateur au minimum) → le
  fuseau du point, ou celui de l'équipe. `timeshape` répond en mémoire : pas de coût externe.
- **Version** : majeure (`11.0.0`) si l'ancien format de requête n'est pas toléré, mineure sinon.
  Le mobile ne saisit rien, il n'est touché qu'en lecture et republié en même temps (choix déjà fait
  pour `API-42`).

## 6. La saisie (web)

- `InstantDateTimePicker` est remplacé par un champ d'heure murale : la valeur est la chaîne
  `yyyy-MM-ddTHH:mm` que Mantine produit déjà, **sans conversion**. Le fuseau de l'entité sert
  seulement à l'étiquette.
- **Étiquette** : « heure de Tokyo » (nom de ville tiré de l'IANA, ou `Intl` `timeZoneName: 'long'`)
  quand le fuseau de l'entité diffère du fuseau d'affichage du lecteur ; rien sinon.
- **Pendant l'édition**, le front appelle `GET /api/teams/{teamId}/timezone` (avec un délai) quand le
  lieu de départ ou le parcours change, en suivant la chaîne du §4 pour choisir le point. À
  l'ouverture d'une entité existante, il part du `timezone` de son DTO.
- **Tous les champs d'une même fiche sont dans le même fuseau** : `dateTime`, `publishAt`, heures de
  groupe. Une étape a le sien.
- **Ajouter une étape** : J+n à la même heure *murale* que la précédente, en arithmétique de
  calendrier sur la chaîne (pas `setDate` sur un `Date` du navigateur — voir `WEB-71`).
- **Heures qui n'existent pas ou existent deux fois** (passage à l'heure d'été ou d'hiver) : le
  backend résout comme `ZonedDateTime.ofLocal` (décalage d'une heure dans le trou, offset le plus tôt
  dans le recouvrement). Sans enjeu pour des sorties à vélo ; documenté, pas signalé à l'utilisateur.

## 7. L'affichage

**Fuseau d'affichage du lecteur** : inchangé — `user.timezone`, sinon celui du système.

| Nature | Exemples | Fuseau |
|---|---|---|
| Rendez-vous | départ de sortie, de groupe, d'étape ; retour estimé (`endDateTime`) ; dates d'un voyage ; publication programmée | celui de l'entité, avec mention s'il diffère de celui du lecteur |
| Horodatage | création, commentaire, notification reçue, « il y a 2 h », dernière connexion, date d'une annonce | celui du lecteur, sans mention |

Par surface :

- **Détail** (web, mobile) : « samedi 11 oct. à 08:00 · heure de Tokyo (ven. 01:00 chez vous) ».
- **Cartes et listes** : l'heure de l'entité, un indicateur discret (icône), et la mention complète
  en infobulle (web) ou en seconde ligne (mobile).
- **Plages de l'Agenda** (`API-85`, `WEB-68`, `MOB-60`) : « 08:30 → retour vers 14:10 »
  (`PublicationTimeSpan`, `rideTimeSpan`) et « ven. 16 → dim. 18 oct. » (`tripDaySpan`) passent
  dans le fuseau de l'entité, une seule mention pour la plage entière. Un voyage multi-fuseaux
  prend les jours de sa première étape dans le fuseau de celle-ci, ceux de sa dernière dans le
  sien. « En cours » (`isUnderWay`) ne change pas : il compare des instants.

La mention (décidé le 6 octobre 2026) :

- **Le fuseau se nomme par la ville de son identifiant IANA** : « heure de Tokyo », « heure de New
  York » (dernier segment, `_` → espace), avec une petite table de traductions françaises pour les
  cas courants (Londres, Bruxelles…). Pas le nom long `Intl` (long, absent de Dart, variable d'un
  navigateur à l'autre), pas l'abréviation (`Intl` en français rend souvent « UTC+2 »), pas le
  décalage (il change avec l'heure d'été). Pas le nom du lieu de départ non plus : trop long, et
  une entité sans lieu n'en a pas.
- **Elle apparaît quand les décalages diffèrent à l'instant de l'événement**, pas quand les
  identifiants diffèrent : un lecteur à Bruxelles ne voit rien pour une sortie à Paris. Pour
  presque tout le monde, presque jamais.
- **L'équivalent « chez vous » porte le jour quand il change** (« ven. 01:00 chez vous »).

**12 h / 24 h** (décidé le 6 octobre 2026) : le web et le backend (notifications, e-mails) suivent la
langue, comme aujourd'hui ; le mobile suit le réglage du téléphone
(`MediaQuery.alwaysUse24HourFormat`) au lieu de `DateFormat.Hm` toujours en 24 h, comme Karoo et
Garmin. L'heure de groupe, devenue un instant, passe par les mêmes formateurs : fini l'affichage
brut « 08:30:00 ».
- **Grille du calendrier** : **dans le fuseau du lecteur**, forcément — une même grille ne peut pas
  porter deux fuseaux. L'indicateur reste sur l'événement.
- **« Aujourd'hui », « demain »**, regroupement par jour : relatifs au lecteur.
- **Notifications et rappels** (`NotificationTexts`) : heure de l'entité avec le nom de son fuseau,
  plus l'équivalent dans le fuseau de l'utilisateur quand il en a un et qu'il diffère. Webhooks
  d'équipe : fuseau de l'entité (au lieu de Paris en dur).
- **iCal** : `DTSTART` et `DTEND` restent en UTC avec `Z` (les agendas convertissent) ; les dates
  des événements « journée entière » (étapes : `DTSTART` et `allDayEnd`) se calculent dans le
  fuseau de l'étape ; `X-WR-TIMEZONE` = fuseau de
  l'équipe pour un flux d'équipe, retiré pour le flux personnel.
- **SEO et `og:`** (`frontend/src/config/routeMeta.ts`) : dates dans le fuseau de l'entité — c'est
  le seul fuseau qui a un sens pour un aperçu de lien.
- **Karoo et Garmin** : affichent dans le fuseau de l'appareil, qui est sur le vélo, donc en
  principe celui du lieu. `DeviceRideDto.timezone` est ajouté mais leur usage peut attendre.

## 8. Migration et déploiement progressif

Pendant une minute, l'ancien et le nouveau backend partagent la base : chaque étape doit rester
lisible et écrivable par la version précédente.

**Version N**

1. Flyway : `teams.timezone` (avec défaut, donc `NOT NULL` sans risque),
   `team_entities.timezone` nullable, `ride_groups.start_at` nullable, `users.timezone` élargie.
2. Remplissage SQL : `team_entities.timezone` = fuseau de l'équipe ; `ride_groups.start_at` =
   `(date locale de la sortie dans ce fuseau + time) AT TIME ZONE` ce fuseau.
3. Rattrapage Java, idempotent, pour les entités **à venir** dont la chaîne trouve un point dans un
   autre fuseau que l'équipe (`timeshape` n'existe pas en SQL) : recalculer `timezone` et
   `start_at` **à instant constant** pour la sortie elle-même (l'instant saisi aujourd'hui est la
   seule vérité qu'on ait), et recalculer `start_at` depuis `time` dans le fuseau corrigé. Toutes
   les équipes de production étant françaises, c'est attendu à zéro ligne : le mesurer par une
   requête, et n'écrire ce rattrapage que s'il en trouve.
   Un `start_at` déplacé repasse par `PublicationEndCalculator` (la fin en dépend).
4. Le code lit `timezone` et `start_at` avec un repli (équipe ; calcul depuis `time`) tant qu'ils
   peuvent être nuls — l'ancienne version, encore active une minute, écrit des lignes sans eux.
   Il écrit `time` **et** `start_at`. Le remplissage des lignes que l'ancienne version laisse
   nulles suit le modèle de `PublicationEndBackfill` (`API-85`) : au démarrage, par lots, par mise
   à jour conditionnelle (`where … is null`) sans toucher à la version. Le risque résiduel est
   celui qu'`API-89` a accepté pour la fin : une ligne *modifiée* par l'ancienne version pendant
   la minute de bascule garde un `start_at` périmé jusqu'au prochain enregistrement.

**Version N+1** : `team_entities.timezone` et `ride_groups.start_at` passent `NOT NULL` (après un
dernier rattrapage des lignes écrites par l'ancienne version), `ride_groups.time` est supprimée, le
backend refuse les instants avec offset en requête.
**Rectifié à l'exécution (7 octobre 2026)** : supprimer `ride_groups.time` dans la même version
casserait la version N, qui la mappe encore pendant la minute de bascule. N+1 l'écrit sans la lire ;
la colonne cesse d'être mappée en N+2 (lot 6) et n'est supprimée qu'en N+3 (lot 7) : une
version qui mappe une colonne casse si elle disparaît pendant qu'elle tourne encore.

## 9. Changer le fuseau d'une équipe

Les entités sans lieu dépendent du fuseau de l'équipe. **Décidé le 6 octobre 2026** : réécrire, dans
la même transaction, les instants des entités **à venir** dont le fuseau stocké est l'ancien fuseau
de l'équipe et dont la chaîne du §4 ne trouve aucun point, **à heure murale constante** — un
changement de fuseau d'équipe est presque toujours la correction d'un mauvais réglage, et l'heure a
été tapée en face de l'étiquette de l'ancien fuseau. Le passé garde ses instants mais change aussi
d'étiquette : une étape déjà roulée, un voyage en cours, une sortie passée stockés dans l'ancien
fuseau et sans point passent au nouveau **à instant constant** (les heures de groupe d'une sortie
passée réécrites dans le nouveau fuseau pour garder leurs départs). Sans cela, la modification d'un
voyage en cours — qui renvoie toutes ses étapes — relirait l'étape d'hier dans le nouveau fuseau et
la décalerait. L'invariant « fuseau stocké = fuseau résolu » tient donc pour toute entité sans
point de l'équipe. L'écran de réglage
montre un aperçu : « 3 événements à venir sans lieu : la sortie du samedi 11 restera à 09:30,
désormais heure de Montréal ». Chaque entité réécrite repasse par `PublicationEndCalculator`, seul
auteur de la fin stockée (`API-85`).

Deux alternatives écartées :

- **Ne rien réécrire.** Le fuseau stocké ne serait plus celui que le backend résout : au prochain
  enregistrement de l'entité, même pour corriger le titre, l'éditeur renverrait l'heure murale de
  l'ancien fuseau, relue dans le nouveau — l'événement se décalerait sans que personne l'ait
  demandé. Les deux autres options tiennent l'invariant « fuseau stocké = fuseau résolu ».
- **Garder l'instant et changer seulement le fuseau.** C'est la bonne règle pour une donnée saisie
  *avant* ce chantier (l'instant venait du navigateur de l'organisateur) : elle aurait servi au
  premier changement de fuseau d'une équipe migrée par défaut à Paris alors qu'elle est ailleurs.
  Toutes les équipes de production sont françaises : le cas ne se présente pas, ni la déduction du
  fuseau d'équipe à la migration ni un drapeau « fuseau confirmé » ne sont nécessaires.

## 10. Lots

| Lot | Contenu | Module |
|---|---|---|
| 0 | Les défauts indépendants : `API-90`, `API-91`, `API-92`, `WEB-70`, `WEB-71` | backend, web |
| 1 | Fuseau d'équipe, résolution, colonnes, remplissage, appel `timezone`, DTO, tolérance de l'ancien format | backend |
| 2 | Saisie en heure murale, étiquette, appel pendant l'édition, réglage du fuseau d'équipe | web |
| 3 | Affichage rendez-vous / horodatage, mention « chez vous », 12 h / 24 h du téléphone sur mobile | web, mobile |
| 4 | Notifications, webhooks, iCal, SEO ; météo, appareils et `PublicationEndCalculator` sur `start_at` (fin de `legStart`) ; `DeviceRideDto.timezone` | backend, web |
| 5 | Version N+1 (`NOT NULL`, refus de l'offset ; `time` écrite, plus lue) | backend |
| 6 | Version N+2 : `ride_groups.time` n'est plus mappée (ni lue ni écrite), la colonne reste | backend |
| 7 | Version N+3 : `DROP COLUMN ride_groups.time`, une fois le lot 6 en production | backend |

## 11. Tests

- **Backend** : la chaîne du §4 (chaque maillon, l'étape qui hérite de la précédente, le repli
  équipe) ; un départ déplacé de Paris à Tokyo garde son heure murale ; changer la date d'une sortie
  déplace ses groupes ; trou et recouvrement d'heure d'été ; ancien format toléré en version N ;
  changement de fuseau d'équipe (§9), dont l'entité avec lieu qui n'est pas réécrite ; iCal
  journée entière d'une étape à 00:30 et d'une étape finissant à 23:30 ; la fin stockée suit
  chaque instant déplacé (`PublicationEndStoredTest` étendu : changement de date, de lieu, de
  fuseau d'équipe) ; un remplacement de GPX recalcule la fin sans toucher au fuseau.
- **Web** : Vitest avec `TZ` fixé (aujourd'hui non fixé, seuls les tests qui passent le fuseau
  explicitement sont stables) ; un scénario Playwright avec `timezoneId: 'Asia/Tokyo'` sur une équipe
  à Paris (aujourd'hui `playwright.config.ts:27` fixe `Europe/Paris` partout).
- **Web et mobile, la mention** : absente pour Paris lu depuis Bruxelles (même décalage), présente
  pour Tokyo lu depuis Paris, avec le jour quand il change ; `display_timezone_test.dart` étendu.
- **Recette** : les deux scénarios du §1, sur le web et le mobile.

## 12. Décisions du 6 octobre 2026

Les trois questions ouvertes de la première version du plan sont tranchées :

1. **Changement de fuseau d'équipe** : heure murale constante, entités à venir sans lieu (§9).
2. **Forme de la mention** : ville de l'identifiant IANA, affichée quand les décalages diffèrent,
   jour précisé quand il change (§7).
3. **12 h / 24 h** : langue sur le web et le backend, réglage du téléphone sur le mobile (§7).
