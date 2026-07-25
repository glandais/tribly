# Pédalons mobile — Brief de design (prochaine version)

Direction artistique + product design. Périmètre strict : **consultation et participation d'un
membre**. Administration, création, édition et outils GPX sont hors scope et ne sont ni documentés
ni maquettés. Locale de référence : **français**. Utilisateur de référence : **Gaby Landais**,
membre de `gaby` (7 membres) et `n-peloton` (1999 membres, 2585 parcours, ~665 sorties).

Sources : `analyse/web-pages.md`, `analyse/mobile-screens.md`, `analyse/api-surface.md`,
`analyse/brand.md`, `web/` (27 captures), `mobile/` (38 captures).

---

## 1. Direction de design

L'identité Pédalons existe et fait autorité : indigo `#4c6ef5` / `#3b5bdb` en primaire, Inter en
famille unique, rayon 8 px (12 px côté Flutter), badges 10 px/700/capitales, code couleur
strictement sémantique (sortie bleu, publication raisin, voyage turquoise, annonce orange, cols
HC→CAT4, revêtements, rôles, visibilité), parité clair/sombre obligatoire. **On ne réinvente rien
de tout cela : on l'applique.** Ce qui change est l'ambition fonctionnelle et la densité
d'information — l'app doit cesser d'être une visionneuse de titres et redevenir un outil de club.

Quatre partis pris structurent la refonte.

**La donnée d'abord, la décoration ensuite.** Les en-têtes décoratifs actuels mangent 182 à 262 pt
(21 à 30 % de l'écran) pour afficher « Bonjour, Gaby! » ou un titre noir illisible posé sur une
carte. Ils deviennent des barres compactes rétractables ; l'espace récupéré finance ce que le site
affiche déjà et que le mobile perd : nom d'équipe, distance, dénivelé, heure de départ, statut
d'inscription, badges de type et de statut, vignette de parcours, barre de places.

**Chaque objet cycliste porte sa géométrie.** Une sortie, un voyage, une étape et un parcours
affichent tous une carte et un profil altimétrique. Le profil altimétrique — absent partout
aujourd'hui alors qu'il est l'information n°1 attendue — devient un composant de premier plan,
colorisé par pente selon le mapping HSL existant (vert 0 % → rouge 18 %).

**Une seule action pleine par écran.** La participation est le cœur du produit : « Rejoindre »,
« Inscrit », « Complet » sont les seuls éléments pleins et colorés de la zone où ils apparaissent.
L'inscription se fait **par groupe, depuis la carte du groupe**, jamais via une boîte de dialogue
étroite. Les erreurs sont explicites et l'état bascule optimistement.

**Tout nombre est une porte.** « 40 participants », « 1999 membres », « 10 groupes », « 5 cols »
deviennent cliquables et ouvrent une feuille. Le mobile n'a pas de place pour tout montrer : il doit
avoir des chemins vers tout.

Enfin, la navigation cesse d'être en silo : entrer dans une équipe ne doit plus supprimer l'accès à
l'accueil, au calendrier et aux parcours globaux, et les parcours redeviennent un territoire
transversal (liste + carte, toutes équipes).

---

## 2. Écarts majeurs site → app

1. **Le fil mobile est rétrospectif et anonyme.** Cartes à une ligne (titre + date + compteur), pas
   de nom d'équipe sur un feed pourtant multi-équipes, pas de distance/D+, pas de badges
   type/statut/visibilité, pas de vignette de parcours, pas de barre de places, pas de statut
   « je participe ». Le web affiche tout cela sur `PublicationCard`, plus recherche debouncée et
   filtre d'appartenance (`minRole`) — inexistants sur mobile. Et la prochaine sortie disparaît du
   feed après rafraîchissement.
2. **Le détail de sortie perd sa carte et ses groupes.** Le web superpose le parcours de chaque
   groupe en 10 couleurs, avec profil altimétrique flottant et survol croisé ; chaque `RideGroupCard`
   donne heure, vitesse moyenne, distance, D+, avatars, « Voir le parcours », GPX, FIT, « Envoyer
   vers l'appareil » et l'action Rejoindre/Quitter/Complet. Le mobile affiche un `ListTile` de deux
   lignes, un bouton global « Participer » qui échoue avec un snackbar « Erreur », et une
   `AlertDialog` de 232 pt qui casse le texte (`22 km/ h`).
3. **La fiche parcours est amputée de tout son contenu analytique.** Ni profil altimétrique, ni
   « Cols et montées » (catégorie, plage kilométrique, D+, pente moyenne/max), ni « Utilisée dans »,
   ni visibilité, ni auteur, ni date, ni conversion d'unités : la sheet déployée montre 3 chiffres,
   un type de surface, un bouton, puis ~800 pt de vide.
4. **Voyages et étapes sont vidés de leur substance.** Pas de carte d'ensemble des étapes (le web
   colorie chaque étape), pas de distance totale ni de D+ cumulé, pas de dates de fin. L'étape mobile
   est l'écran le plus pauvre de l'app (une carte date, 700 pt de blanc) alors que le web y embarque
   la fiche parcours complète plus un rail vertical de navigation entre étapes.
5. **Aucune cartographie de masse.** `/parcours`, `/parcours/carte`, `/equipes/{slug}/parcours/carte`
   et la carte plein écran n'ont aucun équivalent mobile. Sur 2585 parcours, l'app ne propose qu'une
   grille verticale à 2 items par écran, et jamais hors du contexte d'une équipe — alors que le
   filtre de proximité (`nearLat`/`nearLon`/`nearRadius`) existe déjà côté API.
6. **Le calendrier est illisible en volume.** ~152 pt par journée pour un événement (4 jours par
   écran), pas de vue mois, pas de marqueur « aujourd'hui », pas de distinction passé/à venir, pas de
   statut d'inscription, pas de lieu ni de distance, aucun voyage affiché, ouverture sur le 1er du
   mois, et pas de flux ICS (que le web propose avec copie de lien et régénération de jeton).
7. **Le social a totalement disparu.** Les commentaires existent côté web sur sortie, parcours,
   publication et voyage (réponses, modération) : zéro trace côté mobile. Pas de liste de membres,
   pas de participants nominatifs (5 initiales non cliquables), et la découverte d'équipe est un
   `// TODO` mort — on ne peut rejoindre aucune équipe depuis le mobile.
8. **Annonces et pages d'équipe sont inaccessibles.** Aucun onglet Annonces sur les équipes testées,
   le deeplink affiche 20 s de squelettes puis « Erreur » avec l'onglet « Fil » surligné ; pas de
   recherche, pas de filtre de type, pas de pagination. Les pages libres d'équipe n'ont pas d'écran
   propre : un lien web partagé n'a aucune cible mobile.
9. **Le profil est une coquille.** Pas d'édition de nom ni d'avatar, pas de système d'unités
   (mobile figé en km/m), pas de choix de thème, pas de gestion des appareils GPS ni de Strava, pas
   d'export RGPD, pas de suppression de compte, pas de notifications — alors que la politique de
   confidentialité affichée dans l'app déclare traiter photo de profil et préférence d'unités.
10. **Défauts de fabrication visibles.** Titres noirs sans voile posés sur les cartes (sortie #664,
    voyage), pastille de titre translucide illisible, 4ᵉ chip de filtre coupée à l'écran, rangée de
    filtres qui disparaît définitivement au scroll, « Trier par » rendu sur 1 pt de haut, feuilles
    modales qui ne recouvrent pas la barre d'onglets, liens markdown `[texte](url)` aplatis en texte
    brut, états vides réduits à une icône et une phrase.

---

## 3. Évolutions d'API proposées

Le mandat autorise l'augmentation de l'API. Les points ci-dessous sont ordonnés par ce qu'ils
débloquent en maquette.

1. **DTO de liste compacts** (`view=compact` ou `…SummaryDto`) : `excerpt` généré serveur au lieu du
   `media.markdown` intégral, `thumbnailUrl` au lieu de `assets`, suppression de `stages[]` dans
   `TripDto` en liste. Aujourd'hui une page de 20 publications transporte 20 corps d'articles.
2. **Champs « moi »** : `registered`, `registeredGroupId`, `waitlisted`, `full` sur `RideDto`,
   `RideGroupDto` et `TripDto`, plus `GET /api/users/me/participations?from&to&status`. Débloque le
   bloc « Ma prochaine sortie », le badge « Inscrit » dans le fil et le calendrier.
3. **Géométrie négociable** : `?simplify=<m>` / `?points=<max>` sur `getRoute`,
   `GET …/routes/{slug}/track?format=polyline|geojson`, et surtout
   `GET …/routes/{slug}/elevation-profile?samples=300` — indispensable pour dessiner un profil sans
   télécharger plusieurs Mo de `LineString<G3DM>`.
4. **Listes mono-type** `GET /api/rides`, `GET /api/teams/{t}/rides` (idem `trips`) avec
   `from`/`to`/`status`/`participating`/`page`/`size` : supprime la recopie manuelle du polymorphe
   `PublicationDto` et rend le filtre « à venir » natif.
5. **`CalendarEventDto` enrichi** : `startPlaceName`, `distance`, `elevationGain`, `thumbnailUrl`,
   `registered`, `groupName`, `status`, plus l'inclusion des voyages — supprime le N+1 `getRide` par
   événement et rend l'agenda lisible.
6. **`commentCount`** sur publications et parcours + pagination des commentaires (`page`, `size`,
   `sort`, `parentId`, `replyCount`) : aujourd'hui `GET …/comments` renvoie l'arbre entier sans
   aucun paramètre.
7. **Carte multi-entités** `GET /api/map/features?bbox&types=RIDE,ROUTE,PLACE,AD` en GeoJSON léger,
   exploitation des tuiles `routes/tiles/{z}/{x}/{y}.mvt` et de `routes/bounds` déjà générées côté
   client mobile mais jamais appelées, plus `GET /api/teams/{t}/places/bounds`.
8. **Filtres de proximité exposés au mobile** : `nearLat`/`nearLon`/`nearRadius`/`nearType` (déjà
   supportés par l'API) et `minRole` sur `/api/routes` — c'est le filtre le plus naturel du mobile.
9. **`RouteUsagesResponse` consommé** (`…/routes/{slug}/usages`) et exposition de
   `RouteDetailDto.tracks[].climbs`, `createdBy`, `waypoints`, aujourd'hui jamais lus.
10. **Membres et participants paginés** : appeler `teams/{t}/members?page&size&role&search` avec ses
    paramètres (le mobile charge silencieusement 20 membres sur 1999), plus des avatars groupés.
11. **Push et notifications** : `POST /api/users/me/devices` (FCM/APNs), `GET
    /api/users/me/notifications?page&size&unreadOnly`, `POST …/notifications/read`,
    `GET|PUT …/notification-preferences` — rappel J-1, annulation de sortie, réponse à un commentaire.
12. **`ConfigDto` étendu** : `mapStyles[{id,label,url,darkVariant}]`, `tileServerBaseUrl`,
    `defaultCenter`, `minSupportedAppVersion` / `forceUpdate` (les styles VersaTiles sont aujourd'hui
    codés en dur des deux côtés).
13. **Annonces** : `locationGeometry` sur `AdDto` (aujourd'hui réservé à `AdEditDto`), filtres
    `nearLat`/`nearLon`/`nearRadius`, `minPrice`/`maxPrice`, `sortBy`, `from`/`to`, plus la galerie
    d'images et l'auteur/contact.
14. **Pagination par curseur** (`cursor`/`nextCursor`) sur feed, parcours, membres, annonces, en
    conservant `total` : l'offset actuel duplique et saute des éléments pendant un scroll infini sur
    `n-peloton`.
15. **`GET …/count`** (ou `X-Total-Count`) sur parcours, publications et annonces — la feuille de
    filtres refait aujourd'hui un `fetchRoutes(size:1)` complet à chaque frappe.
16. **`ETag`/`If-None-Match`, `updatedAt` systématique, `?updatedSince=`**, et **URLs d'images
    signées** (`?sig=&exp=`) avec `blurHash` — cache natif, préchargement, hors-ligne.
17. **`GET /api/search?q&types=TEAM,RIDE,ROUTE,POST,TRIP,AD,MEMBER&limit`** pour une recherche
    unique, et **plafond serveur documenté sur `size`** (aucun `Math.min` aujourd'hui).

---

## 4. Écrans à maquetter

Douze écrans, tous en consultation/participation. Trois **hero** portent la promesse de la version.

### 4.1 `accueil-aujourdhui` — Accueil : aujourd'hui et à venir *(hero)*

**But.** Répondre en un écran à « qu'est-ce que je fais à vélo cette semaine, et avec qui ».
L'accueil cesse d'être un flux rétrospectif.

**Sections dans l'ordre.**
1. Barre supérieure compacte 56 px, rétractable au scroll : wordmark « Pédalons » indigo 700, à
   droite une cloche de notifications avec pastille de non-lus et l'avatar utilisateur (initiales
   « GL » sur indigo). Le bandeau dégradé de 182 pt disparaît.
2. **Bloc « Ma prochaine sortie »** (présent seulement si `registered` sur une sortie future) :
   carte pleine largeur à vignette de parcours 16:9, badge `SORTIE` bleu + badge `INSCRIT` indigo
   clair, nom de la sortie, compte à rebours relatif (« dans 3 jours »), date longue et heure de
   départ **du groupe** (« Chill Route Long — départ 19:30 »), lieu de départ, distance et D+ du
   parcours du groupe, rangée de 5 avatars du groupe + « +N ». Deux actions : « Voir la sortie »
   (plein indigo) et « Se désinscrire » (contour). Icônes Material outline 16 px.
3. **Rangée « À venir »** : carrousel horizontal de cartes 280 pt (sorties et voyages des 30 jours à
   venir, toutes équipes) — vignette, pastille d'équipe, titre, date courte, heure, distance,
   compteur de places (« 8/12 »), et un bouton pilule « Rejoindre » quand un seul groupe est
   disponible. Séparateurs de date implicites.
4. **Barre d'outils du fil** : champ de recherche debouncé (« Rechercher par titre ou
   description... ») + chip de portée (Toutes les équipes / Membre / Organisateur / Admin) + chips
   de type (Tout / Sorties / Publications / Voyages / Annonces). La barre est **épinglée**
   (`PinnedHeaderSliver`) et ne disparaît plus au scroll ; aucune chip n'est coupée (défilement
   horizontal avec fondu de bord).
5. **Fil enrichi.** Carte de publication à deux niveaux de lecture : bandeau visuel 160 px (photo,
   sinon dégradé sémantique du type avec icône blanche 48 px à 80 %) ; ligne équipe cliquable
   (avatar 16 px + nom + chevron) ; titre h4/700 sur une ligne ; extrait markdown aplati à 150
   caractères ; pile de badges à droite (type, statut, visibilité) ; ligne sociale (avatars des
   inscrits + barre de places verte/jaune/rouge + vignette carrée du parcours 80 px) ; barre de
   statistiques (date longue · N participants · N groupes ou N étapes). Séparation 8 px, scroll
   infini par curseur, compteur total en tête (« 1 248 publications »).

**États.** Squelettes `ShimmerCard` (5) ; erreur avec message et « Réessayer » ; vide absolu
(« Aucune publication pour le moment. ») distinct du vide filtré (« Aucune publication ne correspond
à votre recherche. » + bouton « Effacer la recherche ») ; fin de liste (« Vous avez tout vu »).

---

### 4.2 `sortie-detail` — Détail d'une sortie et inscription à un groupe *(hero)*

**But.** Réparer et amplifier l'action centrale du produit. C'est l'écran le plus déficitaire
aujourd'hui (participation en échec, aucune carte, groupes réduits à deux lignes).

**Sections dans l'ordre.**
1. **App bar transparente sur carte, avec voile.** Hauteur repliée 56 px. En fond, la carte statique
   multi-tracés ; un voile `rgba(36,36,36,0.9)` ou dégradé descendant garantit la lisibilité du
   titre en blanc (le titre noir sans voile est le pire défaut de lisibilité de l'app). Bouton retour
   circulaire semi-opaque, bouton partage, bouton « Ajouter à mon calendrier ».
2. **Ligne d'identité** : bandeau d'équipe (logo + nom + chevron), titre h2, badges statut
   (`PUBLIÉ` / `BROUILLON` / `ANNULÉ`) et visibilité. Une sortie annulée porte un bandeau rouge
   pleine largeur ; une sortie passée porte un badge gris `TERMINÉE` et masque toute action
   d'inscription (aujourd'hui « Participer » reste actif sur une sortie passée).
3. **Bloc méta compact** en deux colonnes : date longue + heure ; N participants (cliquable →
   feuille des participants) ; Départ (pastille verte, nom + adresse) ; Arrivée (pastille rouge,
   nom + adresse — jamais affichée aujourd'hui).
4. **Carte interactive multi-groupes**, hauteur `clamp(260, 44dvh, 460)` : un tracé par groupe dans
   la palette multi-parcours (`#566B13`, `#1d32a8`, `#732C7B`, …), marqueurs départ vert / arrivée
   rouge, bouton plein écran et sélecteur de fond. Sous la carte, **profil altimétrique** du groupe
   sélectionné, colorisé par pente, 110 pt. Sélection croisée : taper un tracé met en avant la carte
   de groupe correspondante et inversement (épaisseur 8 vs 5, opacité 0,9 vs 0,5).
5. **Section « Groupes »** — le composant clé, une carte par groupe :
   - Ligne 1 : nom du groupe (emoji inclus) + badge `INSCRIT` si concerné ; à droite l'action —
     **« Rejoindre »** plein indigo, **« Quitter »** contour, ou **« Complet »** badge gris désactivé.
   - Ligne 2 : 4 métriques à icône 16 px sur une ligne, jamais coupées — heure de départ, vitesse
     moyenne, distance, dénivelé positif.
   - Ligne 3 : avatars (5 + « +N ») + « X/Y participants » + « Voir tout » → feuille des participants
     nominatifs avec pastille organisateur.
   - Ligne 4 : actions texte à icône — « Voir le parcours », « GPX », « FIT », « Envoyer vers
     l'appareil » (menu des services GPS connectés, avec état de chargement).
   - Carte sélectionnée : contour indigo permanent.
   L'inscription se fait **ici**, jamais dans une boîte de dialogue. Bascule optimiste, retour
   d'erreur explicite en bandeau persistant (pas un snackbar de 4 s masqué derrière la barre
   d'action), avec le motif réel (« Groupe complet », « Vous êtes déjà inscrit dans un autre
   groupe », « Réservé aux membres de l'équipe »).
6. **Description** : markdown complet, liens `[texte](url)` cliquables (bug actuel), images inline
   ouvrables en plein écran, pièces jointes listées.
7. **Commentaires** (membres uniquement) : compteur, formulaire, liste avec avatar, nom, date
   relative, réponses à un seul niveau avec filet vertical, suppression pour l'auteur.
8. **Encart non-membre** : bandeau orange « Rejoignez cette équipe pour participer aux sorties. » +
   bouton « Voir l'équipe » (le web l'a, le mobile non).

**États.** Squelette structuré (carte + 3 blocs) ; sortie sans parcours → cadre neutre « Aucun
parcours disponible » et carte remplacée par le bloc lieux ; sortie sans groupe → « Aucun groupe
défini pour cette sortie. » ; erreurs d'inscription détaillées ; chargement par bouton.

---

### 4.3 `parcours-detail` — Fiche parcours complète *(hero)*

**But.** Rendre au parcours son contenu analytique. C'est l'objet le plus consulté d'une plateforme
cycliste, et le mobile en montre trois chiffres.

**Sections dans l'ordre.**
1. **Carte interactive plein écran** en fond (pattern existant conservé, c'est un point fort) :
   tracé colorisé par pente, marqueurs départ/arrivée, bornes kilométriques, points de passage
   nommés. Overlay haut : retour, titre **lisible** (pastille opaque ou voile, pas 60 % de
   transparence), bouton plein écran, sélecteur de fond de carte.
2. **`DraggableScrollableSheet`** à trois crans (0.18 / 0.5 / 0.92) — la fiche doit pouvoir être lue
   entièrement, ce qui n'est pas le cas aujourd'hui (max 0.7 puis 800 pt de vide) :
   - **Poignée + ligne de titre** : nom du parcours, badge de revêtement (Route foncé / Gravel
     orange / VTT vert / Mixte turquoise) et badge de visibilité.
   - **Trois statistiques** : Distance, Dénivelé positif (vert), Dénivelé négatif (rouge), valeurs
     600, unités selon la préférence utilisateur (métrique/impérial — le mobile est figé en km/m).
   - **Profil altimétrique** pleine largeur, 140 pt, colorisé par pente, avec réticule au toucher
     synchronisé avec un marqueur sur la carte (double sens).
   - **« Cols et montées (N) »** : une ligne par montée — badge de catégorie plein (HC raisin, Cat.1
     rouge, Cat.2 orange, Cat.3 jaune, Cat.4 vert), libellé « Montée N », plage kilométrique, puis à
     droite D+, pente moyenne et pente maximale en gras.
   - **« Utilisée dans »** : cartes cliquables des sorties et voyages référençant le parcours, avec
     badge de type, date, et mention « via les groupes : … » / « via l'étape … ».
   - **Informations** : auteur, date de création, équipe propriétaire.
   - **Description** markdown si présente.
   - **Barre d'actions collante en pied de feuille** : « Télécharger GPX », « FIT », « Envoyer vers
     l'appareil », « Partager ».

**États.** Parcours sans tracé → « Aucune donnée de tracé » centré, feuille réduite aux
métadonnées ; chargement progressif (métadonnées immédiates, géométrie simplifiée puis complète) ;
erreur avec « Réessayer » ; « Ce parcours n'existe pas ou a été supprimé ».

---

### 4.4 `parcours-exploration` — Parcours : liste, carte et proximité *(core)*

**But.** Rendre navigables 2585 parcours et sortir les parcours du silo d'équipe.

**Sections dans l'ordre.**
1. En-tête épinglé : recherche debouncée 350 ms + bouton filtres `filledTonal` avec `Badge.count`
   des filtres actifs. Sous la recherche, **sélecteur de portée** (Toutes les équipes / Membre /
   une équipe précise) — aujourd'hui absent, ce qui interdit toute recherche transversale.
2. **Bascule Liste | Carte** en segmenté, qui conserve les filtres (comme le web).
3. Barre de chips : chip de tri en 1ʳᵉ position (flèche + critère), puis chips de filtres actifs
   supprimables (croix), puis chips fantômes des filtres disponibles. Aucune chip coupée.
4. Compteur (« 2 585 parcours »).
5. **Vue liste** : deux densités possibles via un commutateur discret — carte à vignette 16:9
   (actuelle, 2 items/écran) et **ligne compacte** (vignette carrée 80 px + nom + distance + D+ +
   badge revêtement, ~5 items/écran) qui devient le défaut au-delà de 200 résultats.
6. **Vue carte** : tuiles vectorielles serveur (`routes/tiles`), cadrage initial calculé par
   `routes/bounds` et figé au montage, tracés en une couleur à opacité 0,75, épaisseur interpolée
   par zoom ; tap sur une trace → carte flottante en bas (vignette, nom, distance, D+, bouton
   « Ouvrir »). Bouton « Autour de moi » (géolocalisation → `nearLat`/`nearLon`/`nearRadius`, rayon
   réglable 10/25/50 km) et bouton « Rechercher dans cette zone » après déplacement.
7. **Feuille de filtres** : conserver l'existant (deux `RangeSlider` avec **bornes et valeurs
   affichées**, chips de revêtement et de relief, sous-feuille direction du vent) et corriger la
   ligne « Trier par » écrasée à 1 pt ; la feuille doit **recouvrir la barre d'onglets**. CTA
   « Voir N parcours » recalculé en continu via un endpoint `count`.
8. **État vide « cul-de-sac »** : conserver et généraliser le pattern existant (titre, description
   citant le terme recherché, bouton « Retirer le filtre X » le plus probablement fautif, « Tout
   réinitialiser », aperçu de 3 parcours que la levée ramènerait) — c'est le meilleur état vide de
   l'app.

**États.** Squelettes de grille et de carte ; erreur ; scroll infini par curseur ; conservation de
la position de scroll au retour.

---

### 4.5 `calendrier` — Agenda et mois *(core)*

**But.** Passer de 4 jours par écran à un mois lisible, et rattacher le calendrier à ma
participation.

**Sections dans l'ordre.**
1. Barre : titre « Calendrier », sélecteur de portée (Toutes mes équipes / une équipe), bouton
   « Aujourd'hui », chevrons ‹ › de mois.
2. **Vue mois en grille** (nouvelle) : semaine du lundi au dimanche, week-ends teintés, pastille
   indigo sur le jour courant, points colorés sous chaque jour selon le type (sortie bleu, étape de
   voyage vert) et **anneau indigo sur les jours où je suis inscrit**. Tap sur un jour → défilement
   vers ce jour dans l'agenda.
3. **Agenda du mois** sous la grille, densité doublée : en-tête de jour sur une seule ligne
   (« mer. 22 juillet » en gras, pas de doublon), cartes d'événement à 2 lignes — filet coloré 4 px,
   icône de type, titre, `équipe • heure • lieu de départ`, distance et D+ quand disponibles, badge
   `INSCRIT` et nom du groupe, chevron. Les événements passés passent en opacité réduite avec un
   séparateur « À venir » entre les deux.
4. **Filtres** : chips Type (Sorties / Voyages) et Équipe ; les voyages doivent apparaître (absents
   aujourd'hui).
5. **Bloc « Abonnement calendrier »** en pied : bandeau d'information, URL du flux ICS en champ
   lecture seule avec bouton copier, bouton « S'abonner » (`webcal://`) et « Régénérer le lien »
   avec confirmation. Le jeton personnel n'est jamais affiché en clair dans une capture partagée.

**États.** Ouverture sur le mois courant positionné sur aujourd'hui (jamais sur le 1er) ; mois vide
→ « Aucun événement ce mois-ci » + sous-texte + bouton « Aller au prochain mois avec une sortie » ;
squelettes en-tête + 3 cartes ; erreur avec « Réessayer ».

---

### 4.6 `equipe-accueil` — Équipe : en-tête, fil et accès *(core)*

**But.** Faire de la page d'équipe un vrai hub, et casser le silo de navigation.

**Sections dans l'ordre.**
1. **En-tête d'équipe compact** (120 pt max, rétractable à 56) : logo rond 56 px, nom en h2, badge
   de visibilité (`PUBLIC` / `NON RÉPERTORIÉ` / `ÉQUIPE`), badge de rôle si membre. À droite, action
   contextuelle : **« Rejoindre l'équipe »** (plein) si non membre et équipe joignable, **« Quitter
   l'équipe »** (contour, confirmation) si membre non admin. Ces actions n'existent pas sur mobile
   aujourd'hui.
2. **Rangée de statistiques cliquables** : `N membres` → trombinoscope, `N sorties à venir` →
   calendrier d'équipe, `N parcours` → parcours de l'équipe.
3. **Onglets d'équipe** en barre basse (Fil / Calendrier / Parcours / Annonces / À propos), 2 à 5
   selon `enableRides`, `enableTrips`, `enableRoutes`, `enableAds` et le rôle. **Un accès permanent
   à la racine** doit subsister : bouton « Toutes mes équipes » dans l'en-tête et geste retour
   cohérent (aujourd'hui, entrer dans une équipe supprime Accueil, Calendrier et Profil).
4. **Fil d'équipe** : mêmes cartes enrichies que l'accueil, moins la ligne d'équipe, avec recherche
   et filtre de type limité aux modules activés.
5. **Onglet À propos** : statistiques (membres, année de création), description markdown, contact,
   réseaux sociaux, puis **liste des pages libres d'équipe en entrées de navigation** (une ligne par
   page, titre + chevron) et non plus un empilement de markdown chargé en N requêtes ; chaque page
   ouvre un écran propre, deeplinkable, pour qu'un lien web partagé ait une cible.

**États.** Chargement de l'équipe (squelette d'en-tête, jamais de bascule « Rejoindre » → « Gérer »
après coup) ; fil vide ; erreur ; équipe introuvable → retour à la liste.

---

### 4.7 `voyage-detail` — Voyage et ses étapes *(core)*

**Sections dans l'ordre.**
1. App bar sur carte statique du tracé global, **avec voile** (titre illisible aujourd'hui) ;
   bandeau d'équipe ; titre ; badge de statut.
2. **Bloc de synthèse** : date de début → date de fin, durée en jours, `N étapes`, **distance totale
   et D+ cumulé** (absents aujourd'hui), `N participants` cliquable.
3. **Carte interactive** : une couleur par étape (dégradé nord → sud), marqueurs de début et de fin
   de chaque étape, profil altimétrique global en dessous. Sélection croisée avec la liste.
4. **Liste des étapes** : carte par étape — pastille numérotée indigo, nom, date, lieu de départ →
   lieu d'arrivée, distance et D+, vignette de parcours, chevron. Le décalage de dates observé
   (« Lundi » daté dimanche) doit être traité comme un cas de test de la maquette.
5. **Description** markdown, puis **participants** (grappe de noms, cliquable), puis
   **commentaires**.
6. **Barre d'action basse** : « Participer » / « Ne plus participer » au voyage entier, plein
   indigo, avec état de chargement.

**États.** Voyage sans étape → « Aucune étape » ; non-membre → bandeau orange ; chargement ; erreur.

---

### 4.8 `etape-detail` — Détail d'une étape *(secondary)*

**But.** Transformer l'écran le plus pauvre de l'app (une carte date puis 700 pt de blanc) en fiche
complète.

**Sections dans l'ordre.**
1. En-tête de contexte : nom du voyage en petit cliquable, nom de l'étape en h2, badge de statut.
2. **Rail d'étapes horizontal collant** sous l'en-tête (équivalent mobile du rail vertical web) :
   pastille « Aperçu » + une pastille numérotée par étape avec sa date, l'étape courante en indigo
   plein, défilement automatique sur la sélection.
3. Bloc étape : pastille numérotée, date et heure, Départ (vert, nom + adresse) et Arrivée (rouge,
   nom + adresse).
4. **Fiche parcours embarquée** : nom du parcours + lien « Voir les détails du parcours », carte,
   profil altimétrique, distance / D+ / D−, boutons GPX / FIT / Envoyer vers l'appareil, section
   « Cols et montées (N) ». C'est exactement ce que le web embarque et que le mobile remplace par
   un lien.
5. Description markdown ; commentaires.

**États.** Étape sans parcours → bloc lieux seul + message « Aucun parcours pour cette étape » ;
étape introuvable → « Étape introuvable » + « Retour au voyage ».

---

### 4.9 `publication-detail` — Publication *(secondary)*

**Sections dans l'ordre.**
1. App bar avec titre tronqué, bouton partage.
2. Bandeau d'équipe, titre h2, badges statut et visibilité, **auteur** et date longue.
3. Image de couverture si présente (ouvrable en plein écran, pincement pour zoomer).
4. Corps markdown complet : paragraphes, titres, listes, tableaux à défilement horizontal,
   citations, code, images d'assets, **liens `[texte](url)` cliquables** (aujourd'hui aplatis en
   texte brut, ce qui casse les inscriptions annoncées dans les publications), pièces jointes.
5. **Commentaires** (membres) avec compteur, formulaire, réponses à un niveau, suppression.
6. Navigation « Publication précédente / suivante » dans le fil de l'équipe.

**États.** Chargement ; erreur ; publication introuvable avec retour au fil.

---

### 4.10 `annonces` — Annonces d'équipe : liste et détail *(secondary)*

**But.** Rendre la rubrique réellement accessible (aujourd'hui : aucun onglet, deeplink en erreur
après 20 s de squelettes, onglet « Fil » surligné à tort).

**Sections — liste.**
1. En-tête d'équipe compact + titre « Annonces ».
2. Recherche debouncée + chips de type (Tous / Vente / Location / Recherche) + tri (Date / Prix).
3. Cartes d'annonce : bandeau image 120 px (photo, sinon dégradé orange→jaune avec icône), titre,
   extrait 150 caractères, badges type (Vente vert / Location indigo / Recherche orange) et statut,
   ligne de statistiques — **prix formaté** (`1 200,00 €`, `… / semaine`, ou « Prix à négocier »),
   date de création, localisation textuelle.
4. Scroll infini paginé (aujourd'hui la liste est tronquée silencieusement à la première page).

**Sections — détail.** Galerie d'images en carrousel ; titre + badges ; **bloc prix mis en avant** ;
description markdown ; localisation (texte, et carte si `locationGeometry` est exposé) ; date ;
**auteur et bouton de contact**.

**États.** Onglet visible dès que `enableAds` et membre ; état de navigation correct sur deeplink ;
vide absolu « Aucune annonce n'est disponible pour le moment. » vs vide filtré ; erreur avec cause et
« Réessayer » (jamais un écran « Erreur » nu).

---

### 4.11 `profil-preferences` — Profil et préférences *(core)*

**But.** Passer d'une coquille en lecture seule à un centre de réglages à parité avec le web.

**Sections dans l'ordre.**
1. **Identité** : avatar 100 px (photo ou initiales indigo) avec bouton appareil-photo en overlay et
   suppression ; nom affiché éditable en ligne ; e-mail.
2. **Mes participations** (nouveau) : « Mes sorties à venir » et « Historique » avec compteurs,
   accès à une liste dédiée alimentée par `GET /api/users/me/participations`.
3. **Préférences** : segmenté **Métrique / Impérial** (le mobile est figé en km/m alors que la
   politique de confidentialité affichée déclare cette préférence) ; sélecteur de **thème**
   (Système / Clair / Sombre) ; langue (Français / English) en feuille titrée avec bouton Annuler,
   couvrant la barre d'onglets.
4. **Notifications** : interrupteurs par catégorie — rappel de sortie J-1, annulation ou
   modification de sortie, nouvelle publication de mon équipe, réponse à mon commentaire.
5. **Sécurité** : passkeys en **liste** (appareil, dernière utilisation, suppression unitaire,
   ajout) — le mobile écrase aujourd'hui silencieusement les autres clés.
6. **Appareils GPS** : Garmin, Hammerhead/Karoo — connecter, déconnecter, état et date de connexion.
   Ce sont eux qui alimentent « Envoyer vers l'appareil ».
7. **Comptes liés** : Strava (logo, badge `LIÉ`, lier/délier).
8. **Vos données** : export RGPD avec état et date de validité.
9. **À propos** : version app, version serveur, confidentialité, CGU.
10. **Compte** : « Se déconnecter », « Déconnecter tous les appareils », puis **zone de danger**
    titrée en rouge avec « Supprimer le compte » en bouton contour rouge et confirmation.

**États.** Chargement par section ; erreurs d'enregistrement en ligne ; confirmations sous forme de
question fermée + conséquence.

---

### 4.12 `participants-et-membres` — Feuilles participants et trombinoscope *(secondary)*

**But.** Donner un chemin vers les personnes. Aujourd'hui, 5 initiales non cliquables et aucune
liste de membres nulle part.

**Contenu.**
1. **Feuille « Participants »** ouverte depuis une sortie, un groupe ou un voyage : titre = nom du
   groupe ou de la sortie, compteur en indigo, liste — avatar 40 px, nom affiché, pastille
   `IconShieldCheck` et mention « Organisateur du groupe » le cas échéant. Recherche si > 20.
   Scroll infini paginé.
2. **Trombinoscope d'équipe** ouvert depuis « N membres » : recherche, chips de rôle
   (Tous / Membres / Organisateurs / Admins), liste paginée réelle (l'app charge aujourd'hui 20
   membres sur 1999 sans le dire), avatar + nom + badge de rôle + date d'adhésion.
3. **Découverte d'équipes** (remplace les deux `// TODO` morts) : recherche parmi les équipes
   visibles du domaine, cartes d'équipe (bandeau dégradé violet→indigo, logo, nom, extrait,
   N membres, badge de visibilité) et bouton « Rejoindre l'équipe » quand `joinable`.

**États.** Vide (« Aucun participant pour le moment. ») ; vide filtré ; chargement en squelettes de
lignes ; erreur avec « Réessayer ».

---

## 5. Règles transverses de maquettage

- **Deux modes obligatoires** pour chaque écran : clair (défaut produit) et sombre. Les vignettes
  cartographiques existent en deux fichiers (`thumbnailLight` / `thumbnailDark`) : les utiliser.
- **Densité cible** : au moins 5 items de liste visibles sur un écran de 874 pt, 6 lignes d'agenda,
  3 groupes de sortie. Aucun en-tête décoratif au-delà de 120 pt.
- **Lisibilité sur carte** : jamais de texte posé directement sur une tuile — voile
  `rgba(255,255,255,0.9)` / `rgba(36,36,36,0.9)` ou pastille opaque.
- **Feuilles modales** : toujours au-dessus de la barre d'onglets, avec titre, poignée et action
  d'annulation.
- **Micro-copie** : vouvoiement, phrases courtes, lexique imposé (sortie, parcours, publication,
  voyage, étape, annonce, équipe, groupe, dénivelé positif, relief, revêtement, montée, membre),
  titres nominaux, boutons à l'infinitif, états vides = titre nominal + phrase explicative, vide
  absolu distinct du vide filtré, erreurs neutres suivies d'une action possible.
- **Unités** : métrique/impérial selon la préférence, espace insécable avant l'unité, séparateur de
  milliers en espace fine (`1 240 m`).
- **Accessibilité** : cibles tactiles 44 px minimum, libellés explicites sur toutes les icônes-actions
  (la loupe actuelle n'a pas d'`AXLabel`), respect du text scaling sur les en-têtes épinglés.
