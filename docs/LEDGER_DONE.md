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
- [x] `MOB-34` **Listes : pagination et filtres** (29 septembre 2026) — sous une équipe dédiée, le défilement atteint l'entrée que l'API place en page 2 (demandée à l'API, pas déduite du tri) : fil d'équipe et fil d'accueil (21 articles et une sortie), annonces (22), membres (22), commentaires d'un article (21) ; les puces de type du fil (« Sorties », « Articles ») et des annonces (« Recherche ») filtrent, une recherche sans résultat mène à l'état vide filtré (fil, annonces), la recherche des membres réduit la liste à un nom — `feed_pagination_test.dart`, `ads_members_pagination_test.dart`, `comments_pagination_test.dart`. Le test des commentaires a mis au jour un défaut, corrigé le même jour (231c2188) : le fil ne chargeait jamais sa deuxième page, les commentaires au-delà du 20ᵉ étaient inaccessibles. Sur Android, ce correctif ne tenait pas : le pied du fil se mesurait dans l'écouteur de défilement, avant la mise en page de la frame, donc en retard d'un cran, et au dernier cran (sans rebond comme sur iOS) il se croyait encore loin. Il se mesure depuis le 29 septembre après la frame (`_NearViewportTrigger._onScroll`), testé sur une liste à slivers comme les pages (« un seul cran jusqu'en bas, sans rebond ») ; **ne pas remesurer dans l'écouteur**. *Non couvert : la portée « rôle minimum » du fil d'accueil, les filtres de prix et de proximité des annonces, les puces de rôle des membres, le bouton « Voir plus de commentaires » (défiler suffit), les listes de découverte des équipes et de mes participations.*
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
relevés, tous corrigés ou tranchés, le dernier (`WEB-6`) le 29 septembre. Les idées P2 non retenues sont `WEB-7`.

- [x] `WEB-6` **Erreur d'hydratation React #418 (texte), intermittente — corrigée le 29 septembre
      2026.** Vue d'abord sur `/equipes/{slug}` en membre (point 40 de l'audit), puis sous charge
      (`--workers=5`) sur `postEdit` et `gpxToolsEdit`, deux requêtes parties à la même
      milliseconde et servies en 2,5 s. Leur HTML serveur n'avait ni le nom du site (« © 2026 . »)
      ni la version, sans aucune erreur au journal : le `QueryClient` du SSR avait un `gcTime` de
      2 s, et la config et la version, lues avant le prefetch de la route et observées seulement au
      rendu, étaient ramassées entre les deux dès que le prefetch dépassait 2 s. Elles manquaient
      donc aussi à l'état déshydraté ; le client, qui lit la config avant d'hydrater
      (`entry-client.tsx`), rendait le nom, d'où #418 sur n'importe quelle page. Le serveur ne
      ramasse plus rien (`gcTime: Infinity`) : son client ne vit qu'une requête et `entry-server`
      le vide en fin de rendu. — `src/lib/queryClient.test.tsx` (« a server query nothing observes
      yet »). **À ne pas défaire** : aucune fenêtre finie n'est sûre côté serveur, elle ne fait que
      déplacer le seuil de charge. L'occurrence d'origine n'a pas été rejouée ; un #418 qui
      reviendrait sur une page dont le SSR contient bien nom et version est une autre cause, à
      ouvrir sous un nouvel identifiant.

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
**9,37/21**). L'anneau de focus global, hors de ce chantier, a été traité à part : `WEB-2`.

#### `WEB-1` Trombinoscope : la page web des membres (2026-09-30)

Le dernier reste de T5.4 (`API-39`). La route `teamMembers` (`/equipes/{slug}/membres`) passe à
`web: true` dans `contracts/routes.yaml` et perd son `webFallback` : elle ne redirige plus vers
l'équipe mais rend `TeamDirectoryPage`, route `team-directory` (`auth: 'authenticated'`, sous
« À propos »), que `scripts/routes-ssr.yml` crawle en `user1` sur `gaby`. Les deeplinks ne changent
pas (AASA et `AndroidManifest.xml` régénérés à l'identique), ni le mobile.

La page est `TeamMembersPage` sans invitations, changement de rôle ni retrait : `TeamMemberList` y
reçoit un rôle nul, donc aucune action. Elle affiche **ce que l'API renvoie** : un 403 est un état de
la page (« Liste des membres non partagée », retour vers « À propos »), jamais contourné ; un
`joinedAt` nul n'est plus rendu « A rejoint le inconnu » (`TeamMemberList`) ; le filtre de rôle
n'apparaît que si les rôles sont rendus au lecteur (admin d'équipe ou de plateforme, ou trombinoscope
ouvert), et la recherche ne promet l'e-mail qu'à un admin. Le prefetch (`prefetchTeamDirectory`,
`teamMembersData.ts`) ne demande pas les invitations, réservées aux admins.

Le lien « N membres » de `TeamAboutPage` est actif pour un admin, un organisateur, ou un membre d'une
équipe qui a ouvert son trombinoscope — la règle de `UserTeamAccessChecker` et de la puce du mobile
(`buildTeamSections`) : un lien qui mène toujours à un 403 est pire que pas de lien. Il n'y a pas
d'onglet « Membres » dans la navigation d'équipe, dont la largeur est mesurée pour huit items au
plus (`WEB-25`).

Couvert par trois scénarios de `frontend/e2e/member-directory.e2e.ts` (membre sur trombinoscope
fermé : pas de lien, page de refus sans aucun nom ; organisateur sur trombinoscope fermé : les noms,
sans rôle, date ni filtre de rôle, sans action d'admin ; membre sur trombinoscope ouvert : les
rôles), et par l'entrée `teamMembers` de `routes-render.e2e.ts` (anonyme renvoyé à la connexion ;
sur l'équipe du jeu de données, trombinoscope ouvert, rendu pour les membres, organisateur, admin et
admin plateforme ; refus sur place pour un extérieur), lancés le 2026-09-30, verts sur desktop et
mobile ; `appOnlyFallbacks.test.ts` et `smoke.e2e.ts` ne comptent plus `/membres` parmi les liens réservés à
l'app. Ne pas déduire les rôles ou l'accès côté client pour élargir ce que le serveur donne.

### Accessibilité

- `WEB-2` **L'anneau de focus passe 3:1 en thème sombre** (2026-09-30) — Mantine le dessine en
  `--mantine-primary-color-filled`, indigo-8 (`#3b5bdb`) en sombre : **2,74:1** sur la page
  (`#242424`), **2,40:1** sur les surfaces élevées et les champs (`#2e2e2e`), sous le 3:1 de SC 1.4.11.
  `index.css` repeint le contour des deux classes de Mantine (`.mantine-focus-auto:focus-visible`,
  `.mantine-focus-always:focus`) en `--mantine-color-primary-text` : la nuance que Mantine emploie
  déjà pour le texte primaire en sombre, indigo-4 (`#748ffc`, charte §3), soit **5,23:1** sur
  `#242424`, **4,58:1** sur `#2e2e2e`, **5,55:1** sur `#1f1f1f` et **3,78:1** sur le survol
  `#3b3b3b`. En clair la variable vaut la nuance pleine, indigo-6 (`#4c6ef5`) : l'anneau clair ne
  change pas (**4,32:1** sur blanc, **4,10:1** sur `#f8f9fa`). Indigo-5 (`#5c7cfa`) passait aussi,
  mais à 3,05:1 seulement sur `#3b3b3b`. Pas de nouvelle couleur : la ligne « Anneau de focus » de
  [`BRANDING.md`](BRANDING.md) §3.2 la consigne. Ne pas reposer l'anneau sur
  `--mantine-primary-color-filled`, ni le mettre dans `lib/theme.ts` (le thème Mantine n'a pas de
  réglage de couleur d'anneau). Pas de test automatique : ni vitest (jsdom) ni la suite e2e ne
  mesurent un contour ; la sortie de `pnpm build` a été vérifiée (la règle suit celle de Mantine),
  `pnpm typecheck`, `pnpm lint` et `npx vitest run` passent.

### Outillage

- `WEB-5` **`pnpm ssr-audit:verify` repasse** (2026-09-30) — `notifications`, `web: true` dans
  `contracts/routes.yaml`, manquait à `scripts/routes-ssr.yml` ; la vérification a sorti trois
  autres absentes, venues avec le signalement et le support : `support`, `teamAdminReports`,
  `adminReports`. Les quatre y sont, rangées comme leurs voisines : `notifications` pour les trois
  comptes connectés (la boîte est par utilisateur), `support` pour tous (page publique),
  `teamAdminReports` pour `user1` sur `gaby`, `adminReports` pour `admin`. Couvert par
  `pnpm ssr-audit:verify` lui-même (69 routes), qui tourne sans pile ; le crawl, lui, n'a pas été
  relancé.

### Défauts d'interface

- `WEB-3` **Le survol des cartes web a un effet** (2026-09-30) — l'ombre `md` des cartes était
  déclarée en `'&:hover'` dans la prop `styles`, que Mantine rend en style inline : le
  pseudo-sélecteur était ignoré. Elle passe par une classe de `Card.module.css` (mixin `hover` de
  `postcss-preset-mantine` : `:hover` sous `(hover: hover)`, `:active` sous `(hover: none)`), qui
  porte aussi le reste du style du lien (`display`, couleur, transition). Même motif corrigé pour le
  soulignement au survol de `CardTeamLink` (même module), `TeamContextBanner` et `TripStageCard`
  (un module chacun). [`BRANDING.md`](BRANDING.md) §5.2 et §7.1 décrivent désormais l'ombre au
  survol comme acquise. Ne pas remettre de pseudo-sélecteur dans `styles` ou `style` : il n'y a
  aucun effet et rien ne le signale. Pas de test : ni vitest (jsdom ne calcule pas `:hover`) ni la
  suite e2e ne mesurent un style au survol ; `pnpm typecheck`, `pnpm lint` et `npx vitest run`
  passent, et la sortie de `postcss-preset-mantine` sur le module a été vérifiée.

### Référencement

- `WEB-4` **`PUBLIC_UNLISTED` n'est plus indexé** (2026-09-30) — `frontend/index.html` servait un
  `<meta name="robots" content="index, follow">` statique et rien n'émettait de `noindex` : une page
  non listée, rendue en SSR, était indexable. La balise statique est retirée ; `buildMetaTags`
  (`lib/seo.ts`) émet maintenant **l'unique** balise robots de la page, dans le bloc injecté à
  `<!--ssr-head-->` (dans le `<head>`, donc lue sans JavaScript) : `noindex` si `RouteMeta.noindex`,
  sinon `index, follow`. Les `meta()` de `routeMeta.ts` le posent d'après la visibilité du contenu
  (équipe, page d'équipe, publication, sortie, voyage, étape — celle de son voyage —, parcours), et
  `withIndexing`, appliqué par `entry-server` après le `meta()` de la route, l'ajoute à **toute**
  page d'une équipe qui n'est pas `PUBLIC`, y compris celles sans `meta()` (listes, calendrier…) :
  l'équipe est déjà dans le cache de la requête, lue pour la décision 404/301. Seul `PUBLIC` est
  indexable : `TEAM` ne se rend que pour une session de membre, jamais pour un robot. Couvert par
  `frontend/src/config/routeMeta.test.ts` (une seule balise robots dans le bloc, `noindex` pour un
  contenu non listé, pour un contenu public d'une équipe non listée, et pour une page sans `meta()`
  de cette équipe). Ne pas remettre de balise robots dans `index.html` : elle côtoierait la balise
  par page et la contredirait. Le mode dev SPA sans SSR n'a pas de balise robots, ce qui vaut
  `index, follow`.

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
| `API-2` | **Logo d'équipe sur `TeamPublicationDto`** (2026-09-30) — **API 5.8.0** : `logoUrl`, nullable, même valeur que `TeamDetailDto.logoUrl` (le logo de la page « À propos », gabarit `{size}`). Il vient de deux `@Formula` de `Team` (`logoAssetId`, `aboutPageVisibility`), des sous-requêtes corrélées lues dans le `SELECT` même qui charge l'équipe : aucune requête ni entité de plus par ligne, ni par équipe, et `Team.aboutPage` reste LAZY. Ne pas le remplacer par une marche sur `aboutPage.assets` (c'est ce que le LAZY évite), ni par une table de correspondance à passer à la douzaine de fabriques qui construisent `TeamPublicationDto`. Le mobile passe le logo à `PdlTeamLine` (fiche et carte de sortie, publication, voyage, annonce via `TeamBanner`, page d'équipe, carrousel, prochaine sortie, carte et fiche de parcours) avec les initiales en repli. Le web n'avait pas ce repli : ses fiches lisent l'équipe entière (`TeamContextBanner`), ses cartes n'ont qu'une icône, rien à changer. Couvert par `PostResourceTest.getPost_carriesTheTeamLogo_sameAsTheTeamDetail` (fiche et liste compacte, identique à la fiche d'équipe), les `…QueryCountTest` verts, et `post_detail_page_test.dart` (logo, puis initiales sans logo) | 11, 12, 13, 24, 31, 32 |
| `API-3` | **Vignette du parcours du groupe** (2026-09-30) — **API 5.10.0** : `RideGroupDto.thumbnailLightUrl`, `thumbnailDarkUrl` et `thumbnailUrl` (la claire d'abord, sinon la sombre — la règle de `RideDto`), tous nullables, les vignettes du parcours du groupe. Les deux variantes thémées sont là en plus du champ réduit parce que le mobile rend la sombre en thème sombre. Nuls pour un groupe sans parcours : **jamais la vignette de la sortie en repli côté serveur**, le client l'a déjà. Résolues pour tous les groupes de la sortie par **une** requête `ThumbnailLookup.forTeamEntities` (`RideService.toDto`) ; ne pas remplacer par une marche sur `route.getAssets()` par groupe. Les groupes n'étant rendus que sur le détail, la liste n'est pas touchée. Le mobile (`next_ride_card.dart`, « Ma prochaine sortie ») rend la vignette du groupe, celle de la sortie en repli (`nextRideThumbnailUrl`). Le web ne rendait pas de vignette par groupe : rien à changer. Couvert par `RideResourceTest.getRide_groupsCarryTheirOwnRouteThumbnail` (deux variantes, sombre seule, groupe sans parcours), `PublicationQueryCountTest.rideDetail_groupRouteThumbnails_costDoesNotScaleWithGroupCount` (1 puis 6 groupes sur 6 parcours) et `next_ride_thumbnail_test.dart` | 11 |
| `API-5` | **Capacité de la sortie sur les lignes de liste** (2026-09-30) — **API 5.11.0** : `RideDto.maxParticipants`, nullable, la **somme des `maxParticipants` des groupes**, **nulle dès qu'un groupe est sans limite** (ou sans groupe) : la sortie n'a alors pas de plafond. C'est exactement l'ensemble des sorties pour lesquelles `full` peut être vrai — ne pas sommer les seuls groupes plafonnés, un « 12/20 » sur une sortie qui a un groupe illimité serait faux. `full` reste la vérité sur « complet » (il compare groupe par groupe). Même règle sur le détail (repli sur `ride.getGroups()`) et sur la liste, où elle est calculée dans `RideSummaryRepository.loadListSummaries` à partir des lignes par groupe déjà lues pour `full` : aucune requête de plus. Le mobile rend « N/M » dans le carrousel de l'accueil (`upcomingParticipants`), le nombre seul quand la capacité est nulle ; la carte de fil garde son compteur brut, sans barre. Le web (`PublicationCardProgress`) sommait `ride.groups`, **vide sur une ligne de liste** : sa barre « N/M places » ne s'affichait jamais ; elle lit désormais `maxParticipants` et `full`. Couvert par `RideMeFieldsTest.maxParticipants_sumsTheGroupCapacities_nullWhenOneGroupIsUncapped_listAndDetailAgree` (liste et détail), les `…QueryCountTest` verts, `upcoming_action_test.dart` et `PublicationCardProgress.test.tsx` | 11 |
| `API-9` | **Plage de dates d'un voyage dans « Utilisée dans »** (2026-09-30) — **API 5.9.0** : `RouteUsageDto.endDate`, nullable, la date de la dernière étape non supprimée — la règle de `TripDto.endDate` (le maximum des dates, pas la dernière par rang). Nul pour une sortie et pour un voyage sans étape, qui dure un jour. Aucune requête de plus : `fromTrip` parcourait déjà les étapes pour `viaChildNames`. Le mobile (`route_usages_section.dart`) rend « 1 août → 4 août » comme la carte de voyage, le web (`RouteUsages.tsx`) les deux dates au lieu de la date et l'heure de début ; une sortie garde sa date et son heure. Couvert par `RouteResourceTest.getRouteUsages_trip_carriesItsEndDate_rideDoesNot` (une étape supprimée plus tardive ignorée, voyage sans étape et sortie à nul) et `route_usages_section_test.dart` | 13 |
| `API-13` | **Tri de l'annuaire des équipes** (2026-09-30) — **API 5.12.0** : `GET /api/teams?sortBy=NAME\|MEMBER_COUNT&sortDir=ASC\|DESC`, sur le modèle des parcours et des annonces (`TeamSortBy`, `SortDirection`, DESC par défaut quand `sortBy` est donné). Sans `sortBy`, l'ordre historique (nom croissant). **La clé se termine toujours par l'id de l'équipe**, dans le même sens (`TeamRepository.orderClause`), y compris dans l'ordre par défaut qui ne l'avait pas : sans elle, deux équipes à égalité peuvent changer de place d'une page à l'autre, et la pagination par décalage en répète une et en saute une autre. `MEMBER_COUNT` trie sur la **même sous-requête corrélée** que le `memberCount` que portent les lignes — l'ordre correspond aux nombres affichés — dans l'unique requête de liste, sans compte par équipe. Le mobile (écran 34, découverte) demande `MEMBER_COUNT DESC` et rétablit la mention de la maquette, « 3 équipes · triées par nombre de membres » ; la requête et la mention lisent la même constante `kTeamDiscoverySort`. Le web n'annonçait aucun tri : rien à changer. Couvert par `TeamResourceTest.listTeams_sortByMemberCount_isTotalAcrossPages` (trois ex æquo départagés par l'id, page par page, puis ASC), `listTeams_withoutSort_keepsTheNameOrder`, `TeamMemberQueryCountTest.listTeams_sortedByMemberCount_costDoesNotScaleWithRowCount`, `team_repository_sort_test.dart` et `teams_discover_page_test.dart` | 34 |

- `API-41` **Le meneur de groupe** est livré en 1.5.0 (`RideGroupDto.leader`, nullable). Les
  **gabarits de sortie n'ont volontairement pas de meneur** — décision produit :
  `RideTemplateGroupRequest` reste sans champ, instancier une sortie depuis un gabarit ne désigne
  personne. Le repli sur `createdBy` est interdit (`API-30`).
- `API-42` **« Compléter le compte » retiré** (2026-09-29) — **API 6.0.0** : `UserDto.requiresEmail`
  (qui valait `!user.isEmailVerified()`) a disparu du contrat, avec la page web
  « Compléter le compte » (`/completer-le-compte`, `CompleteAccountPage`) et le bandeau qui y
  menait. C'était du code mort : plus aucun compte non vérifié ne peut ouvrir de session —
  l'inscription et le bootstrap vérifient l'e-mail, l'OTP et la réinitialisation du mot de passe
  refusent un compte non vérifié (`AuthService`), les comptes migrés non vérifiés n'ont pas de mot
  de passe, et les comptes factices `strava_<id>@…` ne peuvent plus se connecter depuis le retrait
  de Strava (`7cd2bf3a`), l'import par dump qui les créait ayant lui-même été retiré (`d93fd3af`).
  **Décision à ne pas défaire** : `POST /api/auth/email/change-request`,
  `AuthService.requestEmailChange` et la vérification du changement d'e-mail **restent** — le profil
  mobile s'en sert (`profile_identity_section.dart`) ; seul le parcours « compléter » est parti. Aucune
  migration Flyway ne supprime les comptes `strava_<id>@…`, mais staging et prod n'en ont plus
  (vérifié le 2026-09-29 : aucun compte factice ni non vérifié, bases reparties de zéro depuis
  l'import) ; seule une vieille base locale ou une restauration antérieure en porterait encore.

### Défauts relevés

- `API-28` **NPE 500 sur un `media` incomplet** (2026-09-30) — `POST /api/teams/{slug}/rides` avec
  `media.assets = {}` levait une `NullPointerException` dans `AssetService.updateAssets` : Jackson
  construit un record par son constructeur canonique, jamais par le builder qui posait les listes
  vides, et `@Schema(required = true)` ne valide rien sur `AssetsDto` ni `MediaDto` (pas de
  `@ValidateSchema`). Les deux records ont maintenant un constructeur compact qui **normalise à
  vide** : `images` et `attachments` nuls deviennent des listes vides, `assets` nul un inventaire
  vide, `markdown` nul la chaîne vide (la valeur par défaut du builder et de l'entité, dont la colonne
  est `NOT NULL`). Normaliser plutôt que répondre 400 : les clients envoient toujours les listes, et le
  contrat ne change pas. Couvert par `RideResourceTest.createRide_withEmptyAssetsObject_shouldSucceed`
  et `createRide_withEmptyMediaObject_shouldSucceed`. Un `media` nul, lui, reste un 400 là où la
  requête porte `@ValidateSchema` (`RideRequest`…).

### Vie privée : les métadonnées retirées à l'import

- `API-43` **Les images perdent leurs métadonnées au stockage** (2026-09-29, contrat inchangé) —
  `S3StorageService.store`, le seul chemin par lequel un fichier atteint le bucket (upload
  d'asset, pièce jointe, avatar et son original temporaire, vignettes, import biketeam via
  `addAsset`/`uploadAssetFile`, `uploadTempFileToS3`), passe les JPEG, PNG, WebP et GIF par
  `ImageMetadataStripper` : sans perte, sans décoder les pixels, **par liste blanche** de segments
  et de chunks par format (tout ce qui est inconnu part, y compris EXIF, GPS, XMP, IPTC, MPF,
  commentaires, miniature JFIF et tout octet après la fin de l'image — vidéos de « motion photo »,
  cartes de gain). L'orientation est gardée seule dans un EXIF minimal (26 octets, IFD0 = la seule
  balise Orientation), écrit seulement si elle n'est pas 1 ; les pixels ne sont pas tournés,
  largeur et hauteur ne changent pas. Sortie déterministe et idempotente : un fichier propre
  ressort octet pour octet. TIFF et ses dérivés (DNG, la plupart des RAW), HEIF/HEIC/AVIF/CR3,
  JPEG XL et JPEG 2000 sont **refusés** pour toutes les catégories, pièces jointes comprises, dans
  `FileTypeDetector.detectAndValidate` (`FILE_TYPE_REJECTED`) et à nouveau dans `store()` ; une
  image trop cassée pour être parcourue est refusée (`INVALID_FORMAT`). Rattrapage : V47 ajoute
  `assets.metadata_pending` (images et types `LOGO`/`IMAGE`/`ATTACHMENT`, index partiel) ;
  `AssetMetadataBackfillScheduler` (toutes les 5 min, lots de 100, curseur en mémoire) lit l'en-tête
  par une lecture partielle (`StorageService.retrieveHead`), ne télécharge que les images
  nettoyables, réécrit sous la même clé seulement si le contenu change, puis efface le drapeau ;
  une erreur de stockage transitoire le laisse posé. Tests : `ImageMetadataStripperTest` (dont
  `Jpeg.keepsAProgressiveJpegIntact`), `ImageFormatTest`, `FileTypeCategoryTest`,
  `StorageMetadataStrippingTest`, `AssetServiceTest.MetadataStripping`, `AssetMetadataBackfillTest`.
  **Décisions à ne pas défaire** : le nettoyage reste dans la couche de stockage, pas dans
  `AssetService` ni `UserAvatarService` (un futur chemin d'écriture le contournerait) ; le format se
  lit sur les octets (`ImageFormat.sniff`), jamais sur le type déclaré ni l'étiquette de Magika —
  un JPEG envoyé en `application/octet-stream` est nettoyé aussi ; liste blanche plutôt que liste
  noire ; refus d'une image cassée plutôt que stockage avec ses métadonnées. Limites connues : les
  vidéos (`API-46`), documents (`API-47`), SVG/ICO (`API-48`) restent tels quels ; les TIFF/HEIF
  déjà stockés, s'il y en a, et la restauration d'une vieille sauvegarde MinIO sont `OPS-14` et
  `OPS-15` ; un eXIf PNG placé après IDAT (hors norme) est réécrit à sa place ; le WebP est traité en
  mémoire. Les exports déjà générés (7 jours) et les sauvegardes antérieures (30 jours) gardent les
  originaux jusqu'à leur expiration.
- `API-44` **Les GPX importés ne gardent que la trace et les waypoints** (2026-09-29, contrat
  inchangé) — `GpxSanitizer`, appelé en tête de `GpxProcessingService.computeGpx` (dans son `try`,
  donc une erreur donne `GPX_FAILURE`), avant l'écriture d'`original.gpx` : chaque point garde
  latitude, longitude et altitude, son instant devient `Instant.EPOCH`, ses extensions (fréquence
  cardiaque, cadence, puissance, température) disparaissent ; les waypoints gardent position et
  nom. Tous les fichiers stockés d'un parcours et d'un aperçu de l'outil GPX (original, filtré,
  FIT) en sortent : upload, planificateur, promotion d'un aperçu en parcours, import biketeam. Les
  métadonnées du document (auteur, e-mail, lien, `creator`) ne survivaient déjà pas au lecteur.
  Le constat d'origine était inexact : `original.gpx` était déjà une resérialisation ; la fuite
  était le `<time>` de chaque point dans les deux GPX, les extensions de `filtered.gpx` (celui des
  téléchargements, des routes d'appareil et des envois aux services GPS) et l'heure et la puissance
  du FIT. Rattrapage : `GpxSanitizationBackfill` (`service/gpx`, 4 h 15, après la purge des aperçus,
  `SKIP`) réécrit en place, sous la même clé, tout parcours (supprimés et tous domaines compris) et
  tout aperçu dont un fichier porte encore `<extensions>` ou un `<time>` autre qu'epoch, en
  régénérant le FIT **avant** le GPX filtré (qui est le seul signal de saleté) ; le pipeline
  (SRTM, simplification, montées) n'est pas rejoué. Le marqueur `maintenance/api-44-gpx-sanitized`
  n'est écrit qu'après une passe sans échec ; une restauration du bucket le fait disparaître et la
  passe repart. Tests : `GpxSanitizerTest`, `GpxSanitizationBackfillTest`,
  `GpxProcessingServiceTest#createTracks_shouldStripTimestampsAndSensorsFromEveryStoredFile`,
  `GpxPreviewServiceTest#create_shouldStripTimestampsAndSensorsFromStoredFiles`.
  **Décisions à ne pas défaire** : un seul point d'entrée (`computeGpx`), pas chez un appelant ;
  nettoyage **en place** (les appelants écrivent le FIT depuis le même objet `GPX`) ; `EPOCH` et
  non `null` (le rédacteur FIT et `computeArrays` exigent un instant, et c'est déjà la valeur d'un
  point sans `<time>`) ; pas de rejeu du pipeline dans le rattrapage. Rien n'utilisait les
  horodatages : distances, dénivelés, montées, vent, simplification sont de la géométrie. Limites
  connues : un GPX/FIT déposé en pièce jointe (`API-49`) ; le rédacteur de la bibliothèque
  (`API-50`) ; la pose du marqueur n'a pas de test (`API-51`) ; les copies déjà envoyées à Garmin,
  Wahoo ou Hammerhead avant le correctif ne se réparent pas de notre côté.

### `API-39` T5.4 — Trombinoscope : débloqué par un réglage d'équipe (contrat `3.0.0`)

**Livré** — la page web des membres est venue ensuite, `WEB-1`. L'oracle
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

- `OPS-11` **`BACKUP_KEEP` retiré de `.env.example`** (2026-09-30) — le modèle de
  `/root/pedalons-backup.env` décrivait une variable que rien ne lit : la rétention est le second
  argument de `scripts/backup-prune.sh` (30 par défaut), sur l'hôte de sauvegarde, comme le dit
  [`OPERATIONS.md`](OPERATIONS.md). Pas de test : `grep -rn BACKUP_KEEP` ne trouve plus que ce
  ledger.
- `OPS-12` **Commentaire du cache gpx2web corrigé** (2026-09-30, scindé de `OPS-10`) —
  `.env.example` disait encore que gpx2web écrit les tuiles directement à leur chemin final ; depuis
  gpx2web 1.5.1 les tuiles de carte sont écrites puis renommées. Le commentaire le dit, et garde
  l'interdiction de partager `DATA_CACHE_PATH` entre environnements tant que les tuiles d'élévation
  ne sont pas revues (`OPS-10`, toujours ouvert) : ne pas la lever avant. Pas de test (commentaire).
- `OPS-6` **Journal d'accès de l'hôte masqué et borné à 14 jours** (2026-09-29) — le Caddy de
  l'hôte de production journalise `pedalons.fr` (redirection), `www.pedalons.fr` (l'application)
  et les deux domaines de staging, par le snippet `pedalons_access_log` d'
  [`OPERATIONS.md`](OPERATIONS.md#access-logs) : `t`, `token`, `code`, `state` → `REDACTED`,
  coordonnées supprimées, **et `resp_headers>Location` supprimé** — sans lui, la ligne d'une
  redirection gardait la query brute dans l'en-tête. Un fichier par pile
  (`pedalons-access.log`, `pedalons-staging-access.log`), rotation par
  `/etc/logrotate.d/caddy-access`. Vérifié par `curl …?lat=1&t=x` sur les trois domaines :
  `t=REDACTED`, pas de `lat`, pas de `Location`. Sauvegarde de l'ancienne config :
  `/etc/caddy/Caddyfile.bak-20260929`. Les jetons dans le **chemin** restent en clair : `API-45`.
  Ne pas retirer la ligne `Location`, et refaire le `chown caddy:caddy` des fichiers après tout
  `caddy validate` lancé en root. Pas de test (configuration hors dépôt).
- `OPS-7` **Journal d'accès Traefik coupé** (2026-09-29, audit de février I13) — il n'était pas
  persisté (pas de volume : il mourait avec le conteneur) et Traefik ne sait pas masquer un
  paramètre de requête. `docker-compose.yml` passe `--accesslog=false` : le journal du Caddy de
  l'hôte est le seul, et c'est lui que la politique décrit (`LEGAL-10`, application sur l'hôte :
  `OPS-6`). Ne pas le rallumer sans filtre ni rotation : il redoublerait, en clair et sans borne,
  ce que la politique promet de masquer. Pas de test (configuration).

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
  filtre de publication exigés par la directive App Store 1.2. Quatre défauts mineurs y ont été
  notés, `MOD-1` à `MOD-4`.
- `MOD-1` **La file plateforme regroupe par (type, id, équipe)** (2026-09-30, **API 5.7.0**) — un
  membre signalé dans deux équipes devenait une seule carte, étiquetée avec la première équipe, et
  une seule décision closait les signalements des deux. `ModerationService` regroupe maintenant par
  `TargetKey(type, id, teamId)` ; seul un membre peut tomber dans deux cartes, une publication ou un
  commentaire n'ayant qu'une équipe. Une carte s'adresse par ce qu'elle expose déjà : `targetType`,
  `targetId` et `teamSlug`. `ModerationDecisionRequest` gagne un `teamSlug` **facultatif** (ajout,
  d'où le mineur) : sur `POST /api/admin/reports/resolve` il limite la décision aux signalements de
  cette équipe (équipe inconnue : 404) ; absent, la décision vaut pour toutes les équipes comme
  avant ; la file d'une équipe l'ignore, son chemin nomme déjà l'équipe. Le web
  (`ModerationQueue.tsx`) envoie toujours `teamSlug` et met l'équipe dans la clé de la carte ; le
  mobile n'a pas de file de modération, son client est seulement régénéré. Couvert par
  `ModerationResourceTest.platformQueue_aMemberReportedInTwoTeams_isTwoItems_decidedOneByOne`
  (deux cartes, rejeter celle de team2 laisse celle de team1 ouverte) et
  `platformResolve_withAnUnknownTeam_is404`. Ne pas rendre `teamSlug` obligatoire sans bump majeur,
  et ne pas retirer l'équipe de la clé de regroupement.
- `MOD-2` **Plus de signalements orphelins après suppression** (2026-09-30) — `REMOVE_CONTENT` sur
  une publication laissait `OPEN` les signalements de ses commentaires, qui pointaient alors vers un
  contenu supprimé. `ModerationService.remove` les clôt maintenant en `REMOVED`, dans la même
  transaction, par une seule requête (`ContentReportRepository.resolveOpenCommentsOf`, un `update`
  sur les commentaires de la publication), comme il le faisait déjà pour les réponses d'un
  commentaire retiré. La clôture ne regarde pas l'auteur du commentaire : la décision porte sur la
  publication, un organisateur qui la retire clôt aussi le signalement d'un de ses propres
  commentaires dessous. Si un admin plateforme restaure la publication, ces signalements restent
  clos. Couvert par `ModerationResourceTest.removeContent_ofAPublication_closesTheReportsOfItsComments`
  (un commentaire d'une autre publication garde son signalement ouvert).
- `MOD-3` **Seuil de masquage tenu sous concurrence** (2026-09-30) — le nombre de signalants était
  compté dans la transaction de chaque signalement : deux signalements validés au même instant
  pouvaient chacun voir 2 signalants, et le contenu n'était pas masqué avant un 4e.
  `ReportService.hideIfReportedEnough` verrouille maintenant la ligne de la publication ou du
  commentaire signalé avant de compter (`ContentReportRepository.lockReported`,
  `refresh(…, PESSIMISTIC_WRITE)`, soit un `SELECT … FOR UPDATE`) : le second signalement attend le
  commit du premier, et son comptage, une nouvelle instruction en `READ COMMITTED`, voit alors le
  signalement de l'autre. Un `refresh` et non un `lock()` : la ligne a pu être masquée par la
  transaction qui tenait le verrou, et `lock()` échouerait sur la vérification de version là où le
  `refresh` relit cet état. Un signalement de membre ne verrouille rien (rien n'est masqué). Pas de
  test de concurrence, qui serait instable : le chemin séquentiel reste couvert par
  `ModerationResourceTest.thirdDistinctReporter_hidesTheContentFromMembers_butNotFromModerators` et
  `thirdDistinctReporter_hidesACommentFromMembers`, qui passent par le verrou ; l'exclusion
  elle-même repose sur la sémantique de PostgreSQL. Garder le verrou **avant** le comptage.
- `MOD-4` **Aucun signalant d'une rafale n'est notifié de son signalement** (2026-09-30) —
  `ContentReported` fusionne tant qu'il attend la diffusion (clé : équipe + cible), et le résolveur
  ne lisait que le premier signalement : seul son auteur était exclu, et un organisateur qui avait
  signalé en second recevait la notification de son propre signalement.
  `NotificationRecipientResolver.resolveReport` exclut maintenant tous les auteurs d'un signalement
  ouvert sur cette cible dans cette équipe (`ContentReportRepository.findOpenReporterIds`, une
  requête quelle que soit la rafale), en plus du premier. Un signalant d'une rafale précédente encore
  ouverte est exclu aussi : il connaît déjà le contenu. Couvert par
  `ModerationNotificationTest.aBurstOfReports_notifiesNoneOfItsReporters_notOnlyTheFirst` (un
  extérieur signale, puis l'organisateur : une notification, à l'admin et à l'admin plateforme,
  aucune aux deux signalants). Ne pas revenir à l'exclusion du seul `report.getReporter()`.

---

## ISSUE — Signaler un problème → issues GitHub

- `ISSUE-6` **Signaler un problème → issues GitHub (API 4.6.0)** — en service en production
  (constaté le 29 septembre 2026). Suites possibles : `ISSUE-1` à `ISSUE-5`.

---

## MIG — Migration biketeam

- `MIG-12` **L'ancien import biketeam** a été supprimé le 2026-09-28 : seule la migration en direct
  reste ([plan](plans/2026-09-22-biketeam-live-migration.md)), en service en staging et dont la mise
  en production attend biketeam (§10 du plan, `MIG-1`) ; `biketeam_migration_map` sert encore au
  direct, et les lignes `USER`, `USER_TEAM`, `COMMENT`, `…_PARTICIPATION` qu'écrivait l'import n'existent
  plus en staging ni en prod (vérifié le 2026-09-29, bases reparties de zéro).
- `MIG-10` **Javadoc de `contentVisibility` corrigée** (2026-09-30, `BiketeamMigrationService`) —
  elle disait que rabattre le `PUBLIC_UNLISTED` de l'équipe sur ses contenus ne changerait rien ;
  elle dit maintenant que le fil de l'équipe se viderait, les listes limitées à l'équipe exigeant
  `te.visibility = 'PUBLIC'`, comme [`MIGRATE_BIKETEAM.md`](MIGRATE_BIKETEAM.md). Le code n'a pas
  changé : la visibilité d'un contenu vient de son propre `listed_in_feed`, jamais de celle de
  l'équipe — ne pas la rabattre. Pas de test (commentaire).
- `MIG-11` **Javadoc de `isPlaceholderLogo` corrigée** (2026-09-30, `BiketeamMigrationService`) —
  elle citait encore « 70 of the 187 exported teams », un décompte du dump de 2026-07 que l'import
  live n'a plus ; elle dit seulement que beaucoup d'équipes n'ont jamais remplacé l'image par
  défaut, comme [`MIGRATE_BIKETEAM.md`](MIGRATE_BIKETEAM.md). Pas de test (commentaire).

- `MIG-4` **Vignettes redessinées seulement si leur source change** (2026-09-30) — `updateRide` et
  `updateTrip` redessinaient les vignettes claire et sombre à chaque écriture, donc à chaque rejeu de
  la migration : son dernier coût. `ThumbnailService.rideInput`/`tripInput` décrivent ce dont elles
  sont tirées — les parcours dans l'ordre de dessin, chacun avec les ids de ses traces — et sont lus
  avant la modification ; `refreshRideThumbnails`/`refreshTripThumbnails` ne redessinent que si
  cette entrée a changé, ou si l'une des deux vignettes manque (un rendu raté la dernière fois est
  retenté). Les ids de traces suffisent à dire que la géométrie a changé : une trace n'est jamais
  modifiée en place, un nouveau GPX remplace les lignes (`RouteService.updateRoute`). La création
  dessine toujours. Couvert par `ThumbnailRefreshTest` (sortie et voyage : création, deux rendus ;
  mise à jour au même parcours, aucun ; changement de parcours, deux), où le rendu est remplacé par
  un PNG de 1×1 compté par Mockito. Ne pas comparer la `version` du parcours : le rejeu la fait
  monter (nom, visibilité) sans toucher au tracé.

---

## BRAND — Charte

- `BRAND-1` **Types d'annonce réalignés sur la charte** (2026-09-30) — `AdDetailPage.tsx` gardait
  sa propre table `adTypeColors` (SALE `primary`, RENTAL `grape`, WANTED `yellow`) ; son badge lit
  maintenant `TYPE_COLORS` de `badgeColors.ts` (SALE green, RENTAL indigo, WANTED orange, comme le
  §3.6 de [`BRANDING.md`](BRANDING.md), qui ne signale plus la divergence). Ne pas remettre de table
  locale dans une page : la couleur d'une énumération se lit dans `badgeColors.ts`, en attendant la
  source unique de `BRAND-2`. Pas de test : rien ne compare encore les couleurs (c'est `BRAND-2`) ;
  `pnpm typecheck` et `pnpm lint` passent.

---

## SEC — Audit de sécurité

Les constats corrigés avant l'ouverture du ledger sont dans [`SECURITY_AUDIT.md`](SECURITY_AUDIT.md).

- `SEC-15` **Reliquat V2 (comptes rattachés par l'ancien import biketeam) : sans objet**
  (2026-09-29) — vérifié en staging et en prod : aucun compte venu de l'import (ni adresse factice,
  ni compte non vérifié, aucune ligne `USER` dans `biketeam_migration_map`) ; les bases sont
  reparties de zéro depuis l'import par dump, retiré par `d93fd3af`. Même constat que `LEGAL-1`.

- `SEC-18` **Le filtre de rôle du trombinoscope révélait des rôles masqués** (2026-09-30, **API
  5.7.1**, hors audit) — relevé en livrant `WEB-1`. Quand la réponse masque les rôles (un lecteur
  que `API-39` n'autorise pas à les voir), `GET /api/teams/{slug}/members` refuse désormais le
  paramètre `role` par un 403 `FORBIDDEN` (`TeamMembershipService.getTeamMembers`), comme tout ce
  que ce lecteur ne peut pas faire, au lieu de l'appliquer ; seuls la description du paramètre et
  celle du 403 changent au contrat, d'où le correctif. Le filtre reste ouvert à qui reçoit les
  rôles (admin d'équipe ou de plateforme, ou trombinoscope ouvert). Les deux clients ne le proposent
  plus dans ce cas : le web ne l'affichait déjà pas (`TeamDirectoryPage`), le mobile masque ses
  chips de rôle (`TeamMembersPage.rolesShown`). Couvert par
  `TeamMembershipServiceTest.getTeamMembers_roleFilter_isRefused_whenTheRolesAreHidden` (refus,
  et le même filtre accepté pour l'admin) et `getTeamMembers_roleFilter_works_onceTheDirectoryIsOpen`,
  et côté mobile par `team_members_page_test.dart` (« sans les rôles, aucune chip de rôle »). Ne pas
  remplacer le refus par un filtre ignoré en silence : un résultat filtré « pour rien » se lit comme
  une réponse.

---

## AUD — Audit d'infrastructure de février

Les lignes corrigées avant l'ouverture du ledger sont cochées dans
[`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md), qui porte aussi le ✅ de
celles d'ici. Même colonnes que la table de `LEDGER_NEXT.md`, le constat remplacé par ce qui a été
fait.

| ID | Thème | Audit | Gravité | Livré |
|---|---|---|---|---|
| `AUD-4` | CI/CD | I9 | Important | **Même Node en CI que dans l'image** (2026-09-30) — la CI installait Node 24, l'image du frontend tourne sur `node:26.9.0-alpine`. Le Dockerfile fait référence : `frontend/.nvmrc` porte `26.9.0`, et `ci.yml` le lit (`node-version-file`) au lieu d'une version en dur ; le Dockerfile rappelle de changer les deux ensemble (ses deux `FROM`). Pas de champ `engines` : il n'est lu nulle part en CI ni dans l'image, et ne ferait qu'avertir en poste de travail ; `packageManager` (`pnpm@11.20.0`) était déjà la seule source de pnpm, pour la CI (`pnpm/action-setup`) comme pour l'image (corepack). Pas de test : la CI elle-même, au prochain passage |
| `AUD-14` | Backend | B4 | Important | **`@Transactional` retiré des resources** (2026-09-30) — il était posé sur dix classes de `api/` : au niveau de la classe sur les quatre `*CommentResource`, sur la méthode de mise à jour de `RideResource`, `PostResource`, `TripResource`, `AdResource`, `TeamResource` et `TeamMemberResource`. Chacune de ces méthodes n'appelle **qu'une** méthode de service, elle-même `@Transactional` et qui construit son DTO dans la transaction (`CommentService.listComments`/`createComment`/`deleteComment`, `RideService.updateRide`, `PostService.updatePost`, `TripService.updateTrip`, `AdService.updateAd`, `TeamService.updateTeam`, `MembershipService.updateMemberRole`) : aucune ne compose plusieurs appels qui devraient être atomiques, donc aucune n'a été gardée. La frontière de transaction est la méthode de service ; une resource qui aurait besoin de plusieurs appels atomiques doit recevoir une méthode de service qui les fait, pas une transaction de resource. Deux règles d'`ArchitectureTest` le gardent (`resources_should_not_be_transactional`, `resource_methods_should_not_be_transactional`, vérifiées en réintroduisant l'annotation). Couvert par les tests de ressource des classes touchées, verts : `AdResourceTest`, les quatre `*CommentResourceTest`, `CommentPaginationTest`, `CommentCountTest`, `PostResourceTest`, `RideResourceTest`, `RideGroupLeaderTest`, `TeamMembershipResourceTest`, `TeamResourceTest`, `TripResourceTest` |
| `AUD-17` | Backend | B15 | Mineur | **Index en double retirés** (2026-09-30) — V5 déclarait `device_code_hash` et `user_code` `UNIQUE`, ce qui crée déjà un index pour chacune (`device_codes_device_code_hash_key`, `device_codes_user_code_key`), puis ajoutait `idx_device_codes_device_code_hash` et `idx_device_codes_user_code` par-dessus. `V46__drop_redundant_device_codes_indexes.sql` les supprime (`DROP INDEX IF EXISTS`) ; `idx_device_codes_expires_at`, qui sert au ménage, reste. Ne pas les recréer : l'unicité suffit aux recherches par hash et par code. Pas de test : les tests backend montent le schéma en `drop-and-create` depuis les entités, sans Flyway, donc la migration n'y passe pas ; elle joue au prochain démarrage en production (`validate`, qui ne regarde pas les index) |
| `AUD-18` | Web | F1 | Critique | **Zoom rendu** (2026-09-30) — `maximum-scale=1.0` retiré du viewport de `frontend/index.html`, seul gabarit HTML du frontend : le SSR (`server.js`) injecte son rendu dans ce même fichier, bâti en `dist/client/index.html`. Ne pas le remettre, ni `user-scalable=no` (WCAG 1.4.4). Conséquence connue et acceptée : iOS Safari zoome sur un champ dont la police fait moins de 16 px, ce qui est le cas des champs Mantine `sm` ; la parade est une taille de police, pas un viewport bloqué. Pas de test : aucune assertion ne lit le viewport ; `pnpm typecheck`, `pnpm lint` et `npx vitest run` passent |
| `AUD-19` | Web | F7 | Important | **Lien d'évitement** (2026-09-30) — premier élément focalisable de `Layout.tsx`, libellé `nav.skipToContent` (« Aller au contenu principal » / « Skip to main content »), hors écran jusqu'au focus (`Layout.module.css`, au-dessus de l'en-tête), il cible `<main id="main-content" tabIndex={-1}>`, sans anneau puisqu'il n'est pas un contrôle. Le clic appelle `focus()` sur la cible au lieu de suivre l'ancre : un changement de hash serait lu par React Router comme une navigation POP, et `useScrollRestoration` avec. Garder le lien **premier** dans le DOM, avant l'en-tête. Couvert par `frontend/e2e/smoke.e2e.ts` (« the first Tab reaches the skip link… » : premier Tab sur le lien, Entrée met le focus sur `main`), écrit sans avoir pu être lancé — la pile e2e tourne sur des images. L'anneau de focus lui-même est `WEB-2` |
| `AUD-28` | Garmin | G11, G12 | Mineur | **Code mort retiré de l'app Garmin** (2026-09-30) — les quinze `System.println` commentés (`ApiClient.mc`, `AuthManager.mc`, `PedalonsApp.mc`) sont supprimés, et la chaîne `Back` des deux `strings.xml`, qu'aucun `Rez.Strings` ni layout ne lit (grep). `AM` et `PM` sont **gardées** alors qu'elles ne servent pas encore : `FormatUtils.mc` code `"AM"`/`"PM"` en dur, et la correction de G5 (`AUD-26`) consiste justement à lire ces deux ressources ; les retirer l'obligerait à les recréer. Ne pas les supprimer tant que `AUD-26` est ouvert. Pas de test : l'app n'en a pas ; `make build` (edge1040) passe |

---

## LEGAL — Politique de confidentialité

- `LEGAL-1` **Import biketeam et « pas de données de tiers » : sans objet** (2026-09-29) — la
  question (responsable du traitement, base légale, information des membres importés) portait sur
  les comptes créés par l'import par dump. Il n'en reste aucun : staging et prod n'ont ni compte à
  adresse factice `strava_`/`facebook_`/`google_` ni compte non vérifié, et `biketeam_migration_map`
  n'y a aucune ligne `USER` (bases reparties de zéro depuis l'import, qui a été retiré par
  `d93fd3af`). La migration en direct n'importe aucune personne ; la politique (§1 « Équipes venues
  de Biketeam », §2) ne décrit plus que ce transfert d'équipe. Si une restauration antérieure ou
  une nouvelle forme d'import ramenait des comptes de membres, la question se rouvre sous un nouvel
  identifiant.
- `LEGAL-2` **Relais des messages d'annonce : finalité et durée annoncées** (2026-09-29) — la
  politique dit au §3 « Relay messages about classified ads | Performance of contract » et au §6
  que la trace (expéditeur, annonce, date — le message n'est pas stocké) vit tant que l'annonce est
  en base, donc jusqu'à son effacement définitif. Option retenue : annoncer, pas de purge ; une
  purge raccourcirait la ligne du §6 (voir `LEGAL-12`).
- `LEGAL-3` **Position précise de l'app Garmin décrite** (2026-09-29) — §1 de la politique : lue à
  l'affichage de l'écran principal et au chargement des parcours, envoyée pour les trier par
  distance, soumise à la permission « Positioning » ; base légale au §3 (consentement, connexion
  volontaire). La phrase « we do not track your real-time location » a disparu. Qu'elle finisse
  dans les logs d'accès est `LEGAL-10`.
- `LEGAL-4` **Contenu public et non listé lisible sans compte** (2026-09-29) — §4 de la politique :
  « unlisted » lisible par quiconque a le lien, sans compte ; « public » accessible à tous, rendu
  par le serveur et indexable.
- `LEGAL-8` **Jeton de calendrier exclu de la phrase sur les hachages** (2026-09-29) — option
  « exclure » : le §9 dit que deux secrets font exception et sont stockés lisibles, dont ce jeton,
  décrit au §1 et au §4 comme une clé qui ne s'expire pas et se régénère. Le hacher reste possible
  (l'URL ne se réafficherait plus) ; son expiration est `SEC-17`.
- `LEGAL-5` **Services push des navigateurs : destinataires, pas sous-traitants** (2026-09-29) —
  §4, nouvelle sous-section « Services push des navigateurs » : Google (Chrome), Mozilla (Firefox),
  Apple (Safari), Microsoft (Edge) remettent la notification web ; c'est le navigateur qui choisit
  le service, qui ne reçoit que le message chiffré (RFC 8291), l'adresse d'abonnement et l'IP de
  l'appareil, et agit en responsable de traitement indépendant. Le §5 y renvoie. Ils ne vont **pas**
  au tableau des sous-traitants : nous ne les choisissons pas et ils ne lisent pas le contenu. FCM,
  qui le lit, y reste.
- `LEGAL-6` **Wahoo ajouté à la FAQ** (2026-09-29) — actif sur pedalons.fr ; `privacy/support.{fr,en}.md`
  le cite avec Karoo et Garmin, comme les CGU (§2) et la politique (§4, §5).
- `LEGAL-7` **Message à l'auteur d'une annonce aussi déclaré en « Messages »** (2026-09-29) —
  `mobile/store-metadata/data-safety.md` : ligne Play *Messages → Other in-app messages*
  (facultatif, non partagé, *App functionality*) **en plus** de *Other user-generated content*.
  Déclarer en trop ne coûte rien, déclarer trop étroit expose à un rejet. À reporter dans la Play
  Console à la prochaine mise à jour de la fiche.
- `LEGAL-10` **Journaux d'accès : 14 jours, sans coordonnées ni jetons en paramètres** (2026-09-29)
  — la politique (§1 « Données de session » et « Données de localisation », §6) le dit ; le journal
  de Traefik est coupé (`OPS-7`), celui du Caddy de l'hôte est décrit dans
  [`OPERATIONS.md`](OPERATIONS.md#access-logs) : `t`, `token`, `code`, `state` remplacés par
  `REDACTED`, `lat`, `lon`, `nearLat`, `nearLon` supprimés, rotation par
  `scripts/caddy-access.logrotate` (quotidienne, 13 fichiers + le courant = 14 jours).
  Appliqué sur l'hôte le même jour (`OPS-6`).
  Les jetons dans le **chemin** (push FCM, export) restent visibles : `API-45`. Ne pas remonter la
  rotation au-delà de 14 jours sans changer le §6. Pas de test (configuration hors dépôt).
- `LEGAL-12` **Purges des conservations sans borne** (2026-09-29) — nettoyage nocturne : codes
  d'appairage et challenges WebAuthn expirés (`AuthCleanupScheduler`, 3 h), inscriptions bêta après
  un an (`BetaSignupScheduler`, 3 h 45, `pedalons.beta.signups.retention-days`), demandes de
  transfert biketeam un an après leur fin (`BiketeamMigrationRetentionScheduler`, 3 h 50,
  `pedalons.biketeam.retention-days`) — fin = `finishedAt` des jobs `SUCCEEDED`/`FAILED`,
  `grantExpiresAt` des `EXPIRED` ; jamais un `GRANTED`, `QUEUED` ou `RUNNING`, et
  `biketeam_migration_map` n'est pas touchée (le rejeu en dépend). La purge biketeam tourne même
  migration désactivée. §6 mis à jour, la phrase « pas encore couverts par un nettoyage » a disparu.
  Tests : `AuthCleanupSchedulerTest.CleanupAbandonedPairingsAndChallenges`,
  `BetaSignupSchedulerTest`, `BiketeamMigrationRetentionSchedulerTest`. Le chiffrement des
  sauvegardes, relevé en chemin (hôte au domicile, copies en clair), est `OPS-13`.
- `LEGAL-9` **Métadonnées des fichiers téléversés retirées à l'import** (2026-09-29) — les photos
  perdent EXIF, XMP, IPTC et commentaires, orientation gardée (`API-43`) ; les GPX ne gardent que
  la trace et les waypoints (`API-44`) ; les deux rattrapages nettoient ce qui était déjà stocké.
  Le §1 de la politique (« Fichiers GPX que vous importez », « Photos et images », fichiers
  téléchargés par l'app mobile) dit désormais ce qui est retiré et ce qui est gardé, à la place des
  avertissements sur l'EXIF, les photos d'annonce et les données de santé ; il dit aussi que les
  formats non nettoyables sont refusés et que les autres pièces jointes (vidéos, documents, GPX
  joints) sont gardées telles quelles (`API-46`, `API-47`, `API-49`). Opportunités #5 et #6.
