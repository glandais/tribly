# Le fait — ce qui a été livré depuis la v2

Pendant de [`LEDGER_NEXT.md`](LEDGER_NEXT.md) : quand une entrée du reste à faire est livrée, elle
vient ici avec les décisions qui la tiennent, **sous le même identifiant** (`WEB-14`, `NOTIF-9`…),
dans la section de son préfixe. La nomenclature — préfixes, attribution des numéros, forme des
renvois — est décrite en tête de `LEDGER_NEXT.md`, et vaut pour les deux fichiers. Ce qui a été
écarté délibérément n'est pas ici : c'est la section « Délibérément dehors » de `LEDGER_NEXT.md`.

Une entrée livrée s'écrit au passé, avec sa date, la version d'API si le contrat a changé, le test
qui la couvre, et les décisions qu'il ne faut pas défaire.

Les plans archivés ([`plans/archive/`](plans/archive/)) gardent le **pourquoi** détaillé ; ce
fichier garde **ce qui est fait**, et ce qu'il ne faut pas défaire.

---

## MOB — Application mobile

- `MOB-21` **Sortie (12), corrigé pendant la passe partielle du 27 juillet 2026** — le bandeau
  `GROUP_FULL` affichait « Groupe complet. » sans jamais dire lequel : `rides.failure.full` prend
  maintenant `{group}`, comme les bandeaux voisins. Le reste de la recette de l'écran est `MOB-2`.

### Couverture e2e Patrol

Écrits le 29 septembre 2026 par-dessus les P0 de l'audit de couverture e2e (`WEB-26`), un test
Patrol par scénario sous `mobile/patrol_test/` (tableau « Coverage » de son README). Ils ont trouvé
sept défauts de l'app, tous corrigés le même jour et nommés dans les entrées qui les ont trouvés,
plus un huitième hors scénario : un appui au-dessus d'une `PdlSheet` ne la refermait pas (la feuille
couvrait la barrière modale, dd207d81). Ce que chaque entrée laisse de côté est dit en « Non
couvert » ; les tests ne tournent qu'en local (`MOB-37`).

- [x] `MOB-25` **Annonces : carte de localisation et contact du vendeur** (29 septembre 2026) — la
  carte d'une annonce localisée montre son lieu, la légende « Localisation approximative », un
  secteur sur une carte qui ne dessine aucun point (ni repère, ni départ, ni arrivée, ni
  surcouche), centrée sur la cellule floutée à moins d'un kilomètre du point saisi et jamais
  dessus ; une annonce sans lieu n'a ni en-tête « Localisation », ni carte, ni secteur. Le contact :
  9, 2 001 et dix espaces laissent « Envoyer » désactivé, 10 et 2 000 l'activent ; un envoi réussi
  ferme la feuille et remplace le bouton par la confirmation, un seul mail relayé porte le
  brouillon, `Reply-To` l'acheteur, `From` jamais l'acheteur, aucune des deux adresses dans le
  corps, et l'annonce lue par l'API ne porte pas l'adresse du vendeur ; `AD_CONTACT_OPTED_OUT`
  (vendeur non contactable) ferme la feuille, pose le bandeau à la place du bouton et n'envoie
  rien ; le 11ᵉ message de l'heure (`RATE_LIMITED`, `Retry-After` 3 600) dit « Réessayez dans
  1 heure », garde le brouillon, passe le bouton à « Réessayer » et la page garde « Contacter le
  vendeur » — `ad_location_map_test.dart`, `ad_contact_test.dart`,
  `ad_contact_opted_out_test.dart`, `ad_contact_rate_limited_test.dart`. *Non couvert :
  `DELIVERY_FAILED` (500) — mailpit accepte tout, la stack réelle ne sait pas faire échouer le
  relais sans un crochet de test (le web le simule par `page.route`) ; le rendu en pixels du
  secteur (le web mesure la teinte du canvas, ici seul l'arbre de widgets est vérifié).*
- [x] `MOB-27` **Sorties passées et annulées** (29 septembre 2026) — une sortie de la veille, restée
  `PUBLISHED`, affiche « Terminée » et aucun de ses deux groupes ne propose « Rejoindre », « Quitter »
  ni « Complet » ; une sortie annulée derrière l'app (PUT du `RideRequest` complet, statut
  `CANCELLED`, comme l'éditeur web) affiche son bandeau, et la carte du groupe n'offre rien, pas même
  « Quitter » à un inscrit que l'API compte toujours comme tel — `ride_past_cancelled_test.dart`.
  *Non couvert : le refus `409 RIDE_PAST` de l'API (couvert côté web), la carte « Ma prochaine sortie »
  d'une sortie annulée.*
- [x] `MOB-28` **Écrans d'erreur sur un 5xx** (29 septembre 2026) — un 500 sur l'équipe puis sur la
  sortie est retenté trois fois par `providerRetry` (au moins 3 s, quatre requêtes pour la sortie),
  puis la page affiche son erreur — « Erreur de chargement » pour la sortie, pas l'« introuvable »
  d'un 404 ; une fois le serveur rétabli, « Réessayer » charge la page — `load_error_retry_test.dart`.
  Le 500 est fabriqué côté app par un intercepteur que le test ajoute au `Dio` authentifié
  (`patrol_test/server_errors.dart`), comme `page.route` côté web : la stack e2e ne sait pas échouer
  à la demande, et `lib/` ne porte aucun crochet de test. *Non couvert : un 5xx réellement émis par
  le serveur, l'erreur hors ligne (`errors.offline`), les autres écrans en erreur (voyage, article,
  parcours, annonce).*
- [x] `MOB-29` **Notifications des autres sujets** (29 septembre 2026) — `RIDE_CANCELLED` : une
  sortie annulée par son organisatrice (PUT du `RideRequest`, statut `CANCELLED`) n'atteint que
  l'inscrit, jamais le membre non inscrit ; l'entrée dit « Une sortie est annulée » avec le nom de la
  sortie, et ouvre la sortie sous son bandeau d'annulation — `notification_ride_cancelled_test.dart`.
  `RIDE_JOINED` : un cycliste s'inscrit depuis l'app ; la notification, qui nomme le cycliste et porte
  le groupe en `excerpt`, atteint le créateur de la sortie **et** le meneur du groupe
  (`RideGroupDto.leader`, distinct du créateur), jamais le cycliste lui-même ; l'entrée lue par le
  meneur nomme le cycliste et le groupe, et ouvre la sortie — `notification_ride_joined_test.dart`.
  *Non couvert : les autres types du sujet sortie (`RIDE_PUBLISHED`, `RIDE_UPDATED` et ses variantes
  de libellé, `RIDE_REMINDER`, `RIDE_GROUP_REMOVED`), `TRIP_*`, `CONTENT_REPORTED` (qui ouvre le web),
  et la réception push (FCM absent de la stack e2e).*
- [x] `MOB-30` **Parcours** (29 septembre 2026) — un cycliste d'aucune équipe cherche dans l'onglet Parcours : le parcours public d'une équipe publique sort, jamais son parcours réservé aux membres ni celui d'une équipe privée (aussi vérifié par `/api/routes`) ; la bascule liste/carte garde la recherche ; la fiche du parcours dessine son profil altimétrique (tiré du GPX téléversé) et « Utilisée dans » ne nomme que la sortie publique. Un membre trouve les deux parcours de l'équipe ; un filtre de revêtement réduit la liste, un filtre sans résultat mène à l'état vide filtré ; « Utilisée dans » ajoute la sortie réservée aux membres et le voyage (« via l'étape … »), jamais la sortie sans parcours — `routes_visibility_test.dart`. *Non couvert : le tracé sur la carte et le popup de la vue Carte (tuiles et valhalla absents de la stack e2e, seule la présence de la vue est vérifiée), la section Parcours d'une équipe, les autres filtres (distance, dénivelé, vent, « Autour de moi »), le tri et la densité.*
- [x] `MOB-31` **Calendrier** (29 septembre 2026) — l'onglet Calendrier d'un membre de deux équipes montre les sorties des deux, jamais la sortie publique d'une équipe publique dont il n'est pas ; chaque carte nomme son équipe, « Inscrit · Groupe A » sur la seule sortie rejointe ; un événement ouvre sa sortie. La section Calendrier de l'équipe ne montre que cette équipe ; « Copier le lien » met au presse-papiers l'URL du flux d'équipe (`teamFeedUrlTemplate`, slug inséré, chemin `/api/teams/{slug}/calendar/ics`), « Régénérer le lien » change le jeton (vérifié par l'API) et la copie suivante donne la nouvelle URL — `calendar_test.dart`. *Non couvert : « S'abonner » (`webcal://`, rien pour le recevoir sur le simulateur), le contenu du flux ICS lui-même (déjà couvert côté web), les étapes de voyage et les filtres de type du calendrier.*
- [x] `MOB-32` **Réglages du profil** (29 septembre 2026) — le nom affiché s'enregistre ; unités
  impériales (l'exemple chiffré passe en milles), thème sombre (l'app se redessine en sombre),
  « Être contacté par les membres » coupé et langue anglaise (la ligne dit « English »), puis
  retour au français : tout arrive sur `/api/users/me`, et l'interrupteur coupé fait refuser le
  relais d'annonce `AD_CONTACT_OPTED_OUT`. L'export de données : « Jamais demandé », « Demander
  un export » → « En préparation… », le mail du scheduler porte le lien de téléchargement, et le
  profil rouvert dit « Prêt » (l'API `READY`) — `profile_preferences_test.dart`, `profile_data_export_test.dart`.
  « Déconnecter tous les appareils » ramène à la connexion, refuse le refresh token de cet
  appareil et celui d'un autre, et le mot de passe connecte toujours —
  `profile_logout_all_test.dart`. Trois défauts trouvés en chemin et corrigés le même jour : un export `READY` restait « En préparation… » (le
  code attendait un statut `COMPLETED` absent du contrat, 8ef911d0) ; `logout-all` partait sans jeton et
  ne révoquait rien (1dab921c) ; les appels authentifiés sous `/api/auth/` (`getMe`,
  gestion des clés d'accès, `logout-all`) ne rafraîchissaient pas un jeton expiré (08aa46ef). *Non couvert : l'avatar (sélecteur de photos natif du
  système) ; les services GPS connectés (OAuth Strava/Garmin hors stack) ; le téléchargement de
  l'archive (lien du mail, pas de bouton dans l'app).*
- [x] `MOB-33` **Signalement au-delà des publications et commentaires, suppression par un modérateur** (29 septembre 2026) — un membre signale une sortie, un voyage, un parcours et une annonce depuis leur `⋯` (`showDetailModerationMenu`) : chaque page se ferme derrière le signalement et l'administrateur de l'équipe trouve chacun dans la file ouverte (`/api/teams/{slug}/reports`) — `report_details_test.dart` ; il signale un coéquipier depuis la section « Membres » (le menu propose aussi de le bloquer) et un participant depuis la feuille des participants d'une sortie, les deux signalements de membre arrivant dans la file — `report_members_test.dart` ; un organisateur (pas l'auteur de l'article) supprime le commentaire d'un membre depuis son `⋯`, qui le retire du fil et de l'API — `comment_moderator_delete_test.dart`. *Non couvert : le signalement depuis une étape de voyage et depuis la feuille des participants d'un voyage, ce que le modérateur voit de la file dans l'app (elle n'a pas d'écran de file : vérifié par l'API).*
- [x] `MOB-34` **Listes : pagination et filtres** (29 septembre 2026) — sous une équipe dédiée, le défilement atteint l'entrée que l'API place en page 2 (demandée à l'API, pas déduite du tri) : fil d'équipe et fil d'accueil (21 articles et une sortie), annonces (22), membres (22), commentaires d'un article (21) ; les puces de type du fil (« Sorties », « Articles ») et des annonces (« Recherche ») filtrent, une recherche sans résultat mène à l'état vide filtré (fil, annonces), la recherche des membres réduit la liste à un nom — `feed_pagination_test.dart`, `ads_members_pagination_test.dart`, `comments_pagination_test.dart`. Le test des commentaires a mis au jour un défaut, corrigé le même jour (231c2188) : le fil ne chargeait jamais sa deuxième page, les commentaires au-delà du 20ᵉ étaient inaccessibles. *Non couvert : la portée « rôle minimum » du fil d'accueil, les filtres de prix et de proximité des annonces, les puces de rôle des membres, le bouton « Voir plus de commentaires » (défiler suffit), les listes de découverte des équipes et de mes participations.*
- [x] `MOB-35` **Détail d'une sortie : participants et exports** (29 septembre 2026) — « Voir la
  liste » réunit les inscrits des deux groupes (total 2, une ligne chacun, ni le lecteur ni le
  créateur) ; la pastille de la carte des groupes nomme le premier groupe, puis celui dont on touche
  la carte ; la carte de groupe propose « GPX » (et « FIT » si le parcours en a un) d'après les assets
  du parcours, pas « Envoyer vers un appareil » sans service GPS connecté, et « GPX » ouvre la feuille
  de partage du système sur un fichier GPX (lue dans l'arbre natif) — `ride_participants_map_test.dart`.
  Deux défauts trouvés en chemin et corrigés : les exports n'étaient pas câblés sur la carte de groupe
  (05b20faf), et changer de groupe levait « pdl-track-src already exists » (poses de la carte non
  sérialisées, ec5df0df). *Non couvert : l'envoi vers un appareil (il faut un service GPS connecté
  par OAuth, hors de portée de la stack e2e), l'export FIT jusqu'à la feuille de partage, les tracés
  eux-mêmes (vue native MapLibre, illisible par Patrol) et le plein écran de la carte.*
- [x] `MOB-36` **Écrans périphériques** (29 septembre 2026) — la page Applications, ouverte depuis le
  profil, refuse une adresse mal formée (« Email invalide ») et enregistre l'inscription à la
  bêta (« Inscription enregistrée ») ; la même adresse en capitales répond pareil et la liste
  d'administration ne la compte qu'une fois. Déconnecté, depuis le formulaire d'inscription,
  « Confidentialité » et « Lire les conditions » ouvrent leur texte et le retour ramène au
  formulaire. « Signaler un problème » garde « Envoyer » désactivé sous 10 caractères, envoie
  une suggestion (`POST /api/feedback`), se ferme et remercie. Sans `PUSH` dans les canaux du
  serveur, la boîte de réception ne montre aucun bandeau d'activation —
  `apps_beta_signup_test.dart`, `legal_pages_test.dart`, `report_problem_test.dart`,
  `push_banner_absent_test.dart`. *Non couvert : le bandeau d'activation du push dans ses états
  `notDetermined` → dialogue système → `granted`, et `denied`. Il faudrait une stack e2e qui
  pousse (compte de service FCM, `PEDALONS_PUSH_ENABLED=true`) et des APNs sur le simulateur.
  Le signalement n'est vérifié que par le 204 et l'interface : aucune API ne relit un
  signalement (il part en issue GitHub). Le bouton « Télécharger l'APK » ouvre un lien externe
  et n'est pas suivi.*

---

## WEB — Site web

### La recette web, automatisée

**Le web est automatisé depuis le 25 septembre 2026** par une suite Playwright qui tourne contre
l'application entière (SSR, backend, postgres, MinIO, mailpit) sur une base vide :
`scripts/e2e.sh up` puis `scripts/e2e.sh test`, voir `frontend/e2e/README.md`. Elle a trouvé onze
défauts, tous corrigés sauf un arbitré (la page d'équipe, `WEB-18`).

Chaque ligne est couverte par un fichier de `frontend/e2e/`, sur desktop et sur mobile (Pixel 7).
Ce qui reste hors de portée de l'automatisation est dit sous la ligne concernée : c'est ce qu'une
recette à la main aurait encore à regarder.

- [x] `WEB-13` Les 5 écrans qui affichent le tracé complet d'un parcours : détail de parcours, carte
      plein écran de parcours et d'étape (les 4 via `useGetRoute`), carte de groupe d'une sortie (via
      `useRoutesBulk` sans `geometry:false`, `RoutesMapView.tsx`). — `route-maps.e2e.ts` : chaque
      écran lit la géométrie entière (aucun paramètre qui la réduise, chaque point du GPX à moins
      de 15 m) et la carte dessine le tracé. *Non couvert : une simplification qui serait ajoutée
      plus tard dans le chemin de dessin lui-même — le test compte des pixels, pas des sommets.*
- [x] `WEB-14` « Ma prochaine sortie », badge `Inscrit` et « Mes participations » se rendent **dans
      le HTML initial** pour une requête porteuse d'un cookie de session, en sont **absents** sans
      cookie, et survivent à l'hydratation sans erreur `[hydration]`. — `ssr-session.e2e.ts`, dont
      le contrôle `curl` fait à la lettre (avec `Cache-Control: no-store` et `Vary: Cookie`).
- [x] `WEB-15` Modale « Contacter le vendeur » : les quatre issues, brouillon conservé sur 429 et
      500, un seul message (le contact coupe le toast global, `skipErrorToast`), `Alert` persistante
      sur la page pour le succès et `AD_CONTACT_OPTED_OUT`, bouton absent sur sa propre annonce, 9 et
      2 001 caractères refusés sans appel réseau ; le mail relayé porte l'auteur en `Reply-To` et
      ne l'imprime jamais. — `ad-contact.e2e.ts`. *Le 500 est simulé : mailpit accepte tout, la
      pile ne sait pas faire échouer un envoi. Les clés `en` ont été vérifiées à la lecture, la
      suite tourne en `fr`.*
- [x] `WEB-16` Annonces, détail : galerie aux flèches et aux vignettes, **dans l'ordre d'ajout**,
      plein écran en 1920 ; disque sans punaise, cadré sur son emprise, légendé « à environ 1 km
      près » ; aucune section vide. — `ads-browse.e2e.ts`. *Le fond de carte (style tiers) est
      remplacé par un fond uni pendant le test.*
- [x] `WEB-17` Annonces, liste : tri et bornes de prix survivent au retour arrière et au partage du
      lien, « Effacer les filtres » conserve le tri, et des changements rapprochés arrivent tous
      dans l'URL. — `ads-browse.e2e.ts`.
- [x] `WEB-18` Page d'équipe en 1440×900 : **arbitré le 25 septembre 2026, l'objectif de 220 px
      n'est pas tenu** — le contenu commence à ~283 px (~300 avant le portage). Gagner les 60 px
      restants voudrait dire refaire la navigation d'équipe (le carré de 40 px et son libellé font
      ~78 px), un chantier de design et non de portage. — `team-misc.e2e.ts` fige **< 300 px** pour
      attraper une régression.
- [x] `WEB-19` Une sortie à plus de 20 commentaires n'en charge que 20 au premier rendu. —
      `rides.e2e.ts`, avec la pastille de meneur (absente sans meneur, jamais le créateur),
      l'inscription et le « Complet ».
- [x] `WEB-20` **Trombinoscope, la matrice rôle × réglage** : réglage désactivé, un membre ordinaire
      prend un 403 ; un organisateur voit la liste sans rôles ni dates ; le sélecteur de meneur
      propose toujours des candidats. Réglage activé, le membre voit tout. `?search=` avec l'adresse
      exacte d'un coéquipier ne remonte rien en membre ni en organisateur, et le remonte en admin
      d'équipe. — `member-directory.e2e.ts`, sur une équipe de test et non sur `gaby`. *L'entrée
      « Membres » est un écran mobile (`web: false`), hors de portée de Playwright ; sur le web, le
      403 est vérifié sur l'API, l'écran d'admin redirigeant vers la page d'équipe.*
- [x] `WEB-21` **Invitation par e-mail** : réponse et écran identiques avec et sans compte, seul le
      mail diffère ; « Renvoyer » remplace le jeton, « Annuler » retire l'invitation ; accepter
      depuis un autre compte donne un message dédié et un bouton « se déconnecter », qui **ramène à
      l'invitation** ; accepter deux fois ne crée qu'une adhésion, et le lien rejoué dit « Vous
      faites déjà partie de… ». — `invitations.e2e.ts`. *Non couvert : l'invitation expirée
      (14 jours d'horloge).*
- [x] `WEB-22` **Invitation d'une adresse sans compte, parcours complet** : inscription par le lien,
      vérification de l'adresse, pas encore membre, invitation visible sur `/equipes`, acceptation.
      — `invitations.e2e.ts`.

### `WEB-26` Audit de couverture e2e du 27 septembre — exécuté

[Archivé](plans/archive/2026-09-27-e2e-coverage-audit.md) : P0, P1 et P2 écrits, 54 défauts
relevés, tous corrigés ou tranchés sauf un (`WEB-6`). Les idées P2 non retenues sont `WEB-7`.

### Le résidu du portage web

#### `WEB-23` T5.5 — Compléments d'annonces : livrée (juillet 2026)

L'alignement du web sur le mobile est fait :

- **Liste** — tri (les six options à plat, comme la feuille de tri du mobile : personne ne pense
  « prix, ascendant », on pense « les moins chères d'abord ») et bornes de prix, tous dans la query
  string via `useUrlFilters` (alias `sort`/`dir`/`pmin`/`pmax`). Le tri **ne participe pas** à
  `isAdFiltered` et « Effacer les filtres » ne le remet pas à zéro : il ne peut pas vider une liste.
  La liste passe en `view=COMPACT` et la carte lit `excerpt` / `thumbnailUrl`.
- **Détail** — galerie sur `AdDto.images` (`Image` + `Group`, vignettes, plein écran en `Modal` —
  `@mantine/carousel` n'a pas été ajouté pour une galerie), sections Description / Localisation /
  Annonceur, auteur par `createdByDisplayName`, et `AdLocationMap`.
- **Contact** — la confirmation est un **état de la page** (`Alert` persistante) et non plus un
  toast qui disparaît avant d'être lu ; `AD_CONTACT_OPTED_OUT` ferme la modale et **retire** le
  bouton, puisque réessayer n'y changerait rien. Les échecs récupérables (429, 500) restent traités
  dans la modale, brouillon en main.

Les deux contraintes qui tenaient la tâche sont respectées et à ne pas perdre : `AdLocationMap` rend
un **polygone GeoJSON de 500 m** (remplissage translucide, contour 1 px), **aucun `Marker` ni couche
`symbol`**, cadrage sur l'emprise du cercle via `initialViewState.bounds`, carte non interactive, et
une légende qui dit « à environ 1 km près ».

#### `WEB-24` T3.5 — Abandonnée, puis tranchée dans l'autre sens (2.0.0)

Alimenter `ElevationChart` par `…/elevation-profile` au lieu de la géométrie complète. **La prémisse
ne tenait pas** : il n'existe aucune vue web qui rende un profil *sans* rendre de carte, donc la
géométrie est de toute façon nécessaire et l'appel supplémentaire **ajoutait** une requête au lieu
d'en retirer une.

C'est finalement l'inverse qui a été fait, et pour la même raison : `…/elevation-profile` a été
**supprimé** de l'API en 2.0.0, et le mobile dérive désormais son profil de la géométrie comme le
web le faisait déjà. Voir `API-40`. Ne pas rouvrir : le sujet est clos dans les deux clients.

#### `WEB-25` Refonte sémantique de `NavButtons` — instruite **et livrée** le 31 juillet 2026

**[`plans/archive/2026-07-31-navbuttons.md`](plans/archive/2026-07-31-navbuttons.md)** (mesures prises dans le
navigateur, pas déduites du code). La prémisse de cette ligne était fausse et le chantier s'est
re-taillé.

`NavButtons` doit **rester des liens** : chaque item est une URL distincte, rendue par le serveur et
inscrite dans l'historique. `Tabs` Mantine promettrait des panneaux échangés sur place — c'est
sémantiquement faux, et la navigation clavier fléchée qui va avec **casserait** la tabulation
attendue sur des liens. Ce qui manque vraiment est `<nav>` + `<ul>/<li>` + `aria-current="page"` :
**aucun changement visuel**, donc pas un chantier de design. **S.**

L'inspection a en revanche sorti ce qui n'était pas dans l'énoncé : sur écran large un libellé de
plusieurs mots **perd tous ses mots sauf le premier** (« Fil d'actualités » rend « Fil… » à 1 722 px,
`line-clamp: 1` sur une boîte de 80 px), l'item actif **n'est jamais ramené dans le champ** quand la
rangée déborde, le fondu de bord est **inconditionnel** donc il ment, et les libellés inactifs
échouent AA dans les deux thèmes (4,04 sombre / 3,32 clair). **S** de plus.

Le menu de débordement est écarté avec un motif : le nombre d'items est plafonné à 8 (3 pages
d'équipe maximum), donc la rangée **ne déborde jamais** sur écran de bureau, et le repli mobile
existe déjà — c'est le menu du fil d'Ariane, alimenté par le même `useNavItems`.

**Livré** : repère `nav` + `ul`/`li` + `aria-current="page"`, `maxWidth` à 130 px au-dessus de 48 em
(mesuré sur le plus large libellé livré, « Modèles de sortie » à 126 px), recalage de l'item actif,
fondu piloté par la position de défilement, et libellés inactifs sortis du `dimmed` (4,04/3,32 →
**9,37/21**). L'anneau de focus global, hors de ce chantier, reste ouvert : `WEB-2`.

---

## API — Contrat d'API et backend

### Reprises immédiates

Le détail de chacune est dans l'historique git de ce fichier et de `LEDGER_NEXT.md` (ex-`NEXT.md`).

- `API-35` Le compteur `GET /api/teams/{teamSlug}/classifieds/count` (contrat `2.3.0`).
- `API-36` `Retry-After` déclaré sur le 429 de `POST /api/users/me/export` (contrat `2.3.1`).
- `API-37` Le document d'API archivé, laissé tel quel à `1.5.0` avec une note de tête.
- `API-38` Les slugs réservés (`SlugService.RESERVED_SLUGS`) — vérifié le 31 juillet 2026 sur la
  base de production, aucune route ni page d'équipe n'en portait, pas de backfill.

### Petites évolutions d'API

| ID | Livré | Écrans |
|---|---|---|
| `API-1` | URL de tuile authentifiable — **API 2.3.0** : `POST /api/tiles/token` puis `?t=` sur les deux `.mvt`. Le repli GeoJSON et son plafond ont été supprimés, le tap lit les propriétés de la tuile comme le web | 21 |

- `API-41` **Le meneur de groupe** est livré en 1.5.0 (`RideGroupDto.leader`, nullable). Les
  **gabarits de sortie n'ont volontairement pas de meneur** — décision produit :
  `RideTemplateGroupRequest` reste sans champ, instancier une sortie depuis un gabarit ne désigne
  personne. Le repli sur `createdBy` est interdit (`API-30`).

### `API-39` T5.4 — Trombinoscope : débloqué par un réglage d'équipe (contrat `3.0.0`)

**Livré, sauf la page web publique** (reste à faire : `WEB-1`). L'oracle
d'énumération est traité, l'autorisation est graduée, et l'ajout d'un membre par sélection
d'utilisateur a été remplacé par une invitation par e-mail.

**Le réglage.** `Team.enableMemberDirectory` (`V33`), famille des `enable*`, éditable par l'admin
d'équipe — mais **`DEFAULT FALSE`**, le seul du lot : ouvrir le trombinoscope montre l'annuaire
complet à chaque membre, c'est un geste de l'équipe et pas un effet de bord de la migration.

**L'autorisation, graduée sur deux axes** (`UserTeamAccessChecker`, `TeamMembershipService`) :

| Rôle | Accès | `search` porte sur | `role` / `joinedAt` |
|---|---|---|---|
| admin (d'équipe ou plateforme) | toujours | nom **ou e-mail** | présents |
| organisateur | toujours | nom seul | présents si le réglage est activé |
| membre | si le réglage est activé | nom seul | présents |
| non-membre | 403 | — | — |

L'organisateur voit la liste quoi qu'il arrive : il lui faut des candidats pour désigner un meneur de
groupe, et `RideService` refuse un meneur non-membre. Ce que le réglage lui retire, ce sont les rôles
et les dates, pas les gens. `LIST` a dû être **extrait** de sa branche commune avec
`CREATE`/`UPDATE`/`DELETE` : les laisser fusionnés aurait ouvert l'ajout et le retrait de membres.

**L'oracle est fermé** : `UserTeamRepository.findByTeam` prend un `searchEmail` que seul un admin
reçoit. `MemberDto.role` et `joinedAt` deviennent nullables — c'est, avec la suppression ci-dessous,
la raison du **MAJOR**.

**`GET /api/users/search` est supprimé.** Son seul appelant produit était `UserAutocomplete`, sur deux
écrans qui n'existent plus sous cette forme. Le sélecteur de meneur passe par
`TeamMemberAutocomplete`, qui interroge `…/members`. Le garde-fou serveur reste
`RideService.resolveLeader` (`RIDE_GROUP_LEADER_NOT_MEMBER`).

**L'ajout d'un membre est devenu une invitation** (`V34`, table `team_invitations`) : `POST`/`GET`/
`DELETE /api/teams/{teamSlug}/invitations`, `POST /api/invitations/preview` (public) et `/accept`,
plus `GET /api/users/me/invitations`. Quatre décisions à ne pas défaire :

- **Personne ne rejoint une équipe sans un clic à soi**, compte préexistant ou non. `AuthService`
  n'est pas touché : s'inscrire n'est pas accepter, et avec deux invitations en attente il n'y aurait
  rien pour choisir. D'où `/me/invitations`, qui est le seul rattrapage de l'inscription spontanée.
- **La création répond à l'identique que le compte existe ou non** — seul le gabarit d'e-mail change.
  Sinon tout admin d'une équipe qu'il vient de créer dispose d'une sonde d'existence de compte, ce que
  `requestOtp` / `requestPasswordReset` / `requestEmailChange` refusent déjà. Prix assumé : une faute
  de frappe ne se signale pas — d'où la liste des invitations en attente, qui la rend visible.
- **`addMemberAllowed` n'est pas revérifié à l'acceptation.** Il garde l'acte de l'admin, pas le
  consentement de l'invité. Idem pour le rôle de l'inviteur : le recours est la révocation, explicite.
- **L'acceptation est idempotente et n'écrase jamais le rôle** : une invitation MEMBER ne rétrograde
  pas un ADMIN.

Trois plafonds (`pedalons.teams.invitations.*`) : 20/h par inviteur et 50/j par équipe → **429** ;
5/j **par adresse** → **échec silencieux** (l'invitation existe, le mail n'est pas envoyé), sinon on
révélerait à l'admin A que l'admin B, d'une autre équipe, vient d'inviter la même personne.

Le mobile est fonctionnel : l'écran existait déjà, seule l'entrée de navigation a été conditionnée au
réglage. La recette web est `WEB-20` à `WEB-22`.

### `API-40` Coût par parcours de la géométrie — tranché en 2.0.0

**Résolu autrement que prévu, et il vaut mieux savoir comment.** Ce point demandait de borner
`simplify` et les climbs, dont un Douglas-Peucker superlinéaire relancé en lecture sur la trace
stockée (9,9 s pour une trace adverse de 100 k points) et un `simplify=NaN` qui passait les gardes.

La mesure a montré que le paramètre lui-même ne servait à rien : les points stockés sont **déjà**
rééchantillonnés puis filtrés Douglas-Peucker à l'import (`GpxProcessingService.computeGpx`), ce qui
les laisse à **88,7 m de médiane entre deux sommets** (p05 59 m, p95 120 m) sur les 5 493 parcours de
staging — 681 points pour le parcours médian, 65 Ko de JSON. Le mobile demandait `simplify: 5 /
points: 3000` sur sa fiche : un no-op complet. Et cette passe en lecture est purement 2D, donc elle
rabotait l'altitude que les mêmes coordonnées transportent en Z et M — d'où l'existence même de
`…/elevation-profile`.

`simplify`, `points`, `GET …/elevation-profile` et `elevation`/`elevationSamples` ont donc été
**supprimés**, avec `TrackGeometry`, `GeometryOptions`, `ElevationProfileDto` et tout le budget
`DEFAULT_BULK_MAX_POINTS_PER_ROUTE` / `perRouteBudget`. D1, D2 et D4 disparaissent avec eux. Un
drapeau `geometry=false` sur `/routes/bulk` remplace l'astuce `points: 2` des trois écrans web qui ne
voulaient que les métadonnées.

Côté mobile, la carte de masse ne paie plus ce coût : depuis le passage aux tuiles `.mvt` (API
2.3.0, `API-1`), elle ne charge plus aucune fiche de géométrie. Seuls les écrans de détail (parcours,
voyage plafonné à 12 étapes par `kTripTrackStageCap`) lisent la géométrie complète.

Ce qui reste ouvert (`MAX_BULK_SLUGS` comme seul garde-fou) est `API-27`.

---

## OPS — Exploitation, déploiement, recette du backend

Rien de livré depuis l'ouverture du ledger.

---

## NOTIF — Notifications

### `NOTIF-9` Notifications — livrées, en production depuis le 21 septembre 2026

Repris le 18 septembre 2026 par
[`plans/archive/2026-09-18-notifications.md`](plans/archive/2026-09-18-notifications.md), où le push est devenu un
canal d'un pipeline commun (boîte de réception, e-mail, push). Les phases 1 à 5 (dont le rappel J-1)
sont en production depuis le 21 septembre 2026, le Web Push depuis le 29. Ce qui reste est
`NOTIF-1` à `NOTIF-4`.

Le ledger du chantier (`plans/2026-09-18-notifications-ledger.md`, phase par phase avec ses
recettes) a été rapatrié ici et supprimé le 29 septembre 2026 ; il reste dans l'historique git. Les
invariants qu'il portait sont déjà dans le plan de conception, dans `mobile/CLAUDE.md` et dans les
commentaires du code (`V39__push_devices.sql`, `PushNotificationSender`, `FcmClient`,
`NotificationPublisher.silently`) ; ce qui n'était écrit que là suit.

| Phase | Livré | Contrat, migration |
|---|---|---|
| 1 — socle backend | Pipeline évènement → notification → livraison, canaux in-app et e-mail, six endpoints | 3.5.0, V37, V38 |
| 2 — web | Cloche, page Notifications, matrice de préférences, ancre des e-mails | — |
| 3 — mobile | Cloche, écran, matrice, deeplink `/notifications` | — |
| 4 — push serveur | `push_devices`, `FcmClient` (FCM HTTP v1, sans dépendance nouvelle), purge des jetons morts | 3.6.0, V39 |
| 4 bis — push mobile | `firebase_messaging` derrière `PushGateway`, autorisation depuis la boîte, tap qui ouvre `data.path` | — |
| 5 | Rappel J-1, modification retardée, inscription, commentaire, invitation, équipes coupées, résumé quotidien, webhook d'équipe | 3.8.0, V40 |
| Web Push | Site installable, push navigateur par le même FCM (plateforme `WEB`) | 5.4.0 |

Deux types sont venus ensuite, hors de ce chantier : `RIDE_GROUP_REMOVED` et `CONTENT_REPORTED`
(`NotificationType` en compte 13).

**Pièges iOS, à ne pas rejouer** (recette sur iPhone du 21 septembre 2026) :

- **Tap perdu sur application tuée.** Un `content-available: 1` réveillait parfois l'app tuée dès
  l'arrivée du push ; `firebase_messaging` retenait alors le message comme « initial »,
  `getInitialMessage()` — appelé à ce réveil — répondait `null`, et au tap le plugin n'émettait pas
  `onMessageOpenedApp`. Correctif double : `FirebasePushGateway` refait un `getInitialMessage()` à
  chaque retour au premier plan sur iOS, et le serveur n'envoie plus `content-available`
  (`UIBackgroundModes: remote-notification` est parti avec lui). Le push s'affiche sans mode
  d'arrière-plan.
- **Ne pas poser `UNUserNotificationCenter.delegate = self` dans `AppDelegate`** : cela a coupé la
  réception des pushes sur l'iPhone. Agrandir le tampon du canal était l'autre fausse piste (le
  message n'atteignait pas Dart du tout).
- **Une build *debug* iOS ne se relance pas** depuis l'écran d'accueil sans débogueur : le cas
  « application tuée » se recette sur une build *release* signée développement.

**Autres décisions de mise en œuvre** :

- `register` est un upsert natif `ON CONFLICT (token) DO UPDATE` : deux `POST /api/push-devices`
  concurrents au lancement (ouverture de session et `onTokenRefresh`) faisaient échouer l'un des
  deux sur le verrou optimiste.
- `google-services.json` et `GoogleService-Info.plist` sont **commités** : identifiants d'app
  publics et clé API restreinte au bundle, les tenir hors dépôt aurait cassé toute build faite
  ailleurs. Le compte de service FCM et la clé APNs `.p8` vivent hors dépôt, dans
  `~/Documents/pedalons/firebase/`.
- L'export RGPD contient les appareils push **sans le jeton** (c'est l'adresse de l'appareil).

**Préalables hors dépôt** :

- Projet Firebase `pedalons-9e595`, **Analytics et Gemini désactivés** : le push n'en a pas besoin,
  et Analytics aurait ouvert une déclaration de collecte de plus dans les deux formulaires de
  confidentialité.
- Clé APNs créée en **Sandbox & Production** — la portée ne se change plus après coup, et une clé
  Sandbox seule ne livre rien en TestFlight — et téléversée dans Firebase sur les deux lignes
  (développement et production).
- Activer une capacité sur l'App ID `fr.pedalons.mobile` **invalide le profil de provisionnement**.
  Le profil de développement a été réémis le 21 septembre 2026 et **expire le 21 septembre 2027** ;
  le profil de distribution est généré par fastlane à l'archivage (`-allowProvisioningUpdates`).

**Web Push** (fusionné dans `develop` le 28 septembre 2026, en production et testé depuis le
29 septembre) : le site s'installe comme une application et reçoit le push par le même FCM
(plateforme `WEB`).

La cloche de l'accueil, la section Notifications du profil et l'écran « Notifications » (non maquetté)
sont livrés depuis la phase 3 du même plan.

L'analyse d'origine, pour mémoire : le seul mécanisme qui ramène un membre sans qu'il ouvre l'app.
Trois déclencheurs : rappel J-1, annulation de sortie, réponse à un commentaire. Ce qui allongeait le
délai réel n'était pas le code : entitlement `aps-environment` et clé APNs `.p8` côté Apple,
`google-services.json` et permission runtime `POST_NOTIFICATIONS` côté Android 13+, mise à jour des
deux formulaires de confidentialité, et une nouvelle soumission aux deux stores. Voir
`mobile/store-metadata/data-safety.md`, qui est la source de vérité des déclarations. Deux pièges qui
cassent en production et pas en test : l'**idempotence** du rappel J-1 (sans marque « rappel
envoyé », un redémarrage renotifie tout le monde) et la purge des jetons périmés (retour
`UNREGISTERED` de FCM), sans quoi la table grossit indéfiniment.

---

## MOD — Modération et signalement

- `MOD-6` **Modération** — livrée le 24 septembre 2026 (V41,
  [spécification archivée](plans/archive/2026-09-24-signalement.md)) : signalement, blocage et
  filtre de publication exigés par la directive App Store 1.2. Quatre défauts mineurs restent :
  `MOD-1` à `MOD-4`.

---

## ISSUE — Signaler un problème → issues GitHub

- `ISSUE-6` **Signaler un problème → issues GitHub (API 4.6.0)** — en service en production
  (constaté le 29 septembre 2026). Suites possibles : `ISSUE-1` à `ISSUE-5`.

---

## MIG — Migration biketeam

- `MIG-12` **L'ancien import biketeam** a été supprimé le 2026-09-28 : seule la migration en direct
  reste ([plan](plans/2026-09-22-biketeam-live-migration.md)), en service en staging et dont la mise
  en production attend biketeam (§10 du plan, `MIG-1`) ; `biketeam_migration_map` sert encore au
  direct, et ses lignes `USER`, `USER_TEAM`, `COMMENT`, `…_PARTICIPATION` écrites par l'import
  restent en base, inertes.

---

## BRAND — Charte

Rien de livré depuis l'ouverture du ledger.

---

## SEC — Audit de sécurité

Rien de livré depuis l'ouverture du ledger ; les constats corrigés avant sont dans
[`SECURITY_AUDIT.md`](SECURITY_AUDIT.md).

---

## AUD — Audit d'infrastructure de février

Rien de livré depuis l'ouverture du ledger ; les lignes corrigées avant sont cochées dans
[`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md).

---

## LEGAL — Politique de confidentialité

Rien de livré depuis l'ouverture du ledger.
