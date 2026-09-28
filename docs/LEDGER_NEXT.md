# La suite — ce qui reste après la v2

Écrit le 27 juillet 2026 (sous le nom `NEXT.md`), au moment où la v2 mobile est terminée et le
portage web livré à trois tâches près, et tenu à jour depuis (dernière relecture d'ensemble :
29 septembre 2026). Ce fichier remplace les feuilles de route des plans archivés : ceux-ci gardent le
**pourquoi** des décisions, celui-ci porte le **reste à faire**, et
[`LEDGER_DONE.md`](LEDGER_DONE.md) ce qui est livré. Quand une ligne est faite, elle part dans
`LEDGER_DONE.md` sous le même numéro de section.

Rien ici ne bloque quoi que ce soit. C'est la propriété qui compte : la v2 est livrable en l'état,
et chaque ligne ci-dessous supprime une dégradation nommée plutôt que de réparer une panne.

Sources : [`plans/archive/`](plans/archive/) (les trois plans du 26 juillet, avec leur §4/§5, et
les plans exécutés depuis),
[`plans/archive/audit-ux/BRIEF.md`](plans/archive/audit-ux/BRIEF.md) (l'entrant de design),
[`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) (audit d'infrastructure,
encore ouvert), [`SECURITY_AUDIT.md`](SECURITY_AUDIT.md) (audit de sécurité de septembre 2026).

**Contrat d'API au 29 septembre 2026 : `5.6.0`.** Toute évolution d'API listée ici demande un bump de
`pedalons.api.version` dans `backend/src/main/resources/application.properties`, puis la
régénération des deux clients (compétence `contract-first-api`).

---

## 1. À recetter sur une application qui tourne

Rien de ce qui suit n'est couvert par les tests automatisés — soit parce que c'est du rendu, soit
parce que ça dépend d'un vrai fournisseur (mail, tuiles, GPS), soit parce que c'est un comportement
de première ouverture. `flutter analyze` est propre, les tests mobiles (`mobile/check.sh`) et le
`pnpm check` du web passent : ce qui suit est ce qu'ils ne peuvent pas dire.

**Le web (§1.2) est automatisé** par une suite Playwright depuis le 25 septembre 2026 : voir
[`LEDGER_DONE.md`](LEDGER_DONE.md) §1.2 et `frontend/e2e/README.md`.

### 1.1 Mobile — les douze écrans

À faire une fois **en clair** et une fois **en sombre** (le mode sombre est *dérivé*, aucune maquette
ne le fournit : c'est là que les erreurs de contraste apparaissent), sur un compte membre de
`n-peloton` (1 999 membres, 2 585 parcours, ~665 sorties) *et* sur `gaby` (7 membres — l'échantillon
d'états vides).

**Passe partielle du 27 juillet 2026** : audit de code complet sur les douze écrans, plus un passage
en direct sur simulateur (thème sombre, compte `n-peloton` — un seul compte suffit, il est admin des
deux équipes `n-peloton` et `gaby`). Un défaut trouvé et corrigé (le `GROUP_FULL` ne nommait pas le
groupe, voir ci-dessous). Restent à faire en conditions réelles, non couvrables par la lecture de
code : bascule de fuseau horaire sur l'appareil, ouverture d'un deeplink app tuée, mesure de fps
(parcours et liste de 200), rendu du text scaling ×1,3/×2,0, et la capture d'écran de preuve pour le
jeton ICS. Thème clair et compte `gaby` pas repassés en revue depuis.

- [ ] **Accueil (11)** — « Ma prochaine sortie » s'affiche quand on est inscrit, disparaît sinon ;
      badge `INSCRIT` sur les cartes du fil ; barre supérieure rétractable, barre d'outils épinglée ;
      5 squelettes au chargement, pas 2.
- [ ] **Sortie (12)** — les six états du bouton d'inscription. En particulier : un groupe complet
      affiche `Complet` **désactivé** (il n'existe aucune liste d'attente, « complet » est un état
      terminal) ; un `GROUP_FULL` en réponse restaure l'état optimiste **et nomme le groupe** ;
      aucune erreur nue « Erreur ». Carte à un tracé par groupe, sélection au tap.
- [ ] **Pastille « Organisateur »** — présente uniquement si `RideGroupDto.leader` est présent.
      **Le cas courant est l'absence** (les 665 sorties existantes de `n-peloton` n'ont pas de
      meneur) : vérifier que ça ne rend rien, et surtout pas le créateur de la sortie.
- [ ] **Parcours (13)** — profil altimétrique colorisé par pente, réticule fluide au glissement
      (60 fps ; regarder au `debugRepaintRainbowEnabled` que les barres ne sont pas repeintes),
      section « Cols et montées ».
- [ ] **Exploration de parcours (21)** — vue liste et vue carte. Le mobile rend désormais les vraies
      tuiles `.mvt`, comme le web (jeton signé, API 2.3.0) : vérifier qu'au-delà de quelques
      centaines de tracés la carte les montre **tous**, sans plafond ni pilule de troncature, et
      qu'aucune requête ne part au tap sur un tracé. À faire **en build release au moins une fois** :
      `featuresAtPoint` a un antécédent de `ClassNotFoundException` Android corrigé en maplibre
      0.3.5, que le mode debug ne montre pas. Vérifier aussi le renouvellement du jeton : laisser la
      carte ouverte au-delà de la durée de vie, les tuiles doivent continuer d'arriver et les
      marqueurs rester **au-dessus** de la masse. Bascule automatique en compact au-delà de 200
      résultats.
- [ ] **Calendrier (22)** — un mois s'affiche ; les étapes de voyage y sont (le voyage en tant
      qu'objet non, c'est voulu) ; anneau « inscrit » sur l'événement ; un jour à la fois
      « aujourd'hui » et « inscrit » porte les deux marqueurs.
- [ ] **Jeton ICS (22)** — copier l'URL d'abonnement, puis **capturer l'écran** : le jeton ne doit
      apparaître nulle part à l'image, alors que le presse-papiers contient l'URL réelle.
- [ ] **Fuseaux horaires (22, 24, 25)** — régler l'appareil sur `Pacific/Auckland` puis
      `America/Los_Angeles`. Une étape du lundi 17 août 2026 à 08:00 ne doit pas glisser d'un jour :
      on cherche une **double conversion**, pas une localisation d'équipe (le contrat n'a aucun fuseau
      d'équipe ; il porte une préférence de fuseau *utilisateur* que le web applique et que le
      mobile ignore : le mobile suit l'appareil).
- [ ] **Voyage et étape (24, 25)** — tracé et profil ; au-delà de 12 étapes le tracé est
      volontairement partiel.
- [ ] **Publication (31)** — liens markdown : interne → route interne, externe → navigateur, non
      lançable → bandeau. **Aucun lien inerte.** Tableau markdown à 4 colonnes : défilement
      horizontal sans déborder la page. Pas de bloc auteur (le contrat ne l'expose pas — ne pas
      s'étonner de son absence).
- [ ] **Annonces (32)** — prix : `1200` → `1 200,00 €` ; `25` + `WEEK` → « 25,00 € / semaine » ;
      `null` → « Prix à négocier ». **La carte rend un secteur, jamais une punaise** : la position
      est floutée à ~1 km et un marqueur ponctuel prétendrait une précision qui n'existe pas.
- [ ] **Contact du vendeur (32)** — les quatre issues rendent quatre écrans distincts : 204,
      `AD_CONTACT_OPTED_OUT`, `AD_CONTACT_RATE_LIMITED` (429, `Retry-After` exploité),
      `AD_CONTACT_DELIVERY_FAILED` (500). **Aucun succès affiché sur un 500.** Le bouton est absent
      sur sa propre annonce. 9 et 2 001 caractères refusés côté client, sans appel réseau.
- [ ] **Trombinoscope (34)** — sur `n-peloton`, le pied annonce le total exact (1 999) à chaque page.
      C'est le point où le mobile chargeait 20 membres sur 1 999 **sans le dire**.
- [ ] **Découverte d'équipes (34)** — la loupe et le CTA d'état vide mènent quelque part (c'étaient
      les deux `// TODO` de `lib/`) ; chip `joinable=true` ; adhésion optimiste avec bandeau
      d'échec nommant la cause.
- [ ] **Profil (33)** — les quatre réglages s'appliquent **immédiatement, sans bouton** (unités,
      thème, langue, « Être contacté par les membres ») ; un échec revient à la valeur précédente.
      Ajouter une seconde clé d'accès **n'écrase plus les autres**. `logout-all` est câblé.
      La cloche et la section Notifications existent depuis la phase 3 de
      [`plans/2026-09-18-notifications.md`](plans/2026-09-18-notifications.md) — mais la section
      reste **non rendue** tant que le serveur ne déclare aucun canal configurable — le défaut en
      dev, plus le cas en prod depuis que le push y est actif : à recetter dans les deux états.
- [ ] **Deeplinks à froid** — application tuée, ouvrir un lien de sortie, de parcours et d'annonce.
      Le bon onglet est surligné et la pile de retour est cohérente. (Le test
      `deep_link_hierarchy_test.dart` couvre la table ; il ne couvre pas l'ouverture réelle.)
- [ ] **Text scaling ×1,3 puis ×2,0** — badges, lignes de col à 3 colonnes, en-têtes épinglés :
      aucun débordement.
- [ ] **Pièces jointes** — sur chacun des huit écrans à `MediaDto` (sortie, publication, annonce,
      parcours, voyage, étape, page d'équipe, « à propos ») : le bloc apparaît **avec** un fichier
      et disparaît sans, et un contenu qui ne porte qu'une pièce jointe sans texte affiche quand
      même le bloc (l'« à propos » ne doit plus se déclarer vide).
- [ ] **Une image jointe se regarde dans l'app** — le tap ouvre la visionneuse zoomable, pas le
      navigateur ; le sous-titre porte « 1920 × 1080 » quand `imageDimensions` est là ; le bouton
      rapporte **l'originale** et ouvre la feuille de partage. À vérifier sur un contenu **visible
      des seuls membres** : c'est le cas où l'ancien `openLink` tombait sur un 403, l'autorisation
      de `/api/download/…` étant celle du contenu porteur. Un fichier volumineux (>10 Mo) : le
      bandeau « Téléchargement en cours… » reste visible et l'échec réseau donne un bandeau rouge,
      jamais une feuille de partage vide.
- [ ] **Performance** — liste de 200 items : rester au-dessus de 55 fps. Si le `BackdropFilter` des
      barres épinglées coûte trop cher, le repli prévu (non implémenté à ce jour — `blurToolbar` est
      une constante fixe à 12, aucune branche conditionnelle) serait **un seul jeton** à faire tomber
      à 0 (`PdlMotion.blurToolbar`, surface opaque), aucun écran à rouvrir.

### 1.2 Web

Couvert par la suite e2e : tout est livré, voir [`LEDGER_DONE.md`](LEDGER_DONE.md) §1.2 (y compris
ce que l'automatisation ne voit pas, dit sous chaque ligne).

### 1.3 Backend et exploitation

- [ ] **Démarrage réel du backend** — les tests utilisent `drop-and-create` et ne passent pas par
      Flyway : un test vert ne prouve **pas** que les migrations s'appliquent sur une base existante.
      Attendre `Migrating schema … to version 45` au moins une fois, puis contrôler que
      `ad_contacts` existe, que `users.contactable_by_members` est nullable et que
      `ride_groups.leader_id` est nullable avec une FK en `ON DELETE SET NULL` (surtout pas
      `CASCADE`) et son index partiel. Les commandes exactes sont au §5.1 du
      [document d'API](plans/archive/2026-07-26-api-v2-livraison-et-suites.md).
      **Ajouté en `3.0.0`** : `teams.enable_member_directory` en `NOT NULL DEFAULT FALSE`, et la
      table `team_invitations` avec son index **partiel** `uk_team_invitations_pending on
      (team_id, email) where status = 'PENDING'` — les tests construisent le schéma depuis les
      mappings JPA, qui ne savent pas exprimer un index partiel, donc c'est précisément le genre
      d'objet qu'un test vert ne prouve pas.
- [ ] **Les tests backend sont à lancer par le propriétaire du dépôt**, jamais par Claude
      (interdiction du projet). Le découpage par item est au §5.2 du même document. Deux gardes à
      ne jamais désactiver pour faire passer un build : les classes `…QueryCountTest` (elles échouent si
      quelqu'un réintroduit une requête par ligne) et le test `groupLeader_isNotTheRideCreator` de
      `RideGroupLeaderTest` (il échoue
      si quelqu'un réintroduit un repli sur `createdBy`).
- [ ] **Le relais de contact en production** — les gabarits `ad-contact.{fr,en}` sont rendus par le
      backend (`templates/mail/`) et partent par le relais SMTP de Scaleway TEM comme tout le reste ;
      l'envoi a été validé par un message réel du temps de Brevo. À revérifier après la bascule TEM
      par un envoi réel : le `Reply-To` doit porter l'adresse de l'auteur et le corps ne doit jamais
      l'imprimer.
- [ ] **Les quatre gabarits d'invitation en production** — `team-invitation.{fr,en}` et
      `team-invitation-signup.{fr,en}` vivent dans `templates/mail/` et partent par le relais SMTP de
      Scaleway TEM. Params des quatre : `appName`, `inviterName`, `teamName`, `invitationUrl`,
      `expiresInDays`. **Reste à faire : un envoi réel** après la bascule TEM, comme pour
      `ad-contact`. Si le relais refuse le message, `POST …/invitations` répond **500
      `TEAM_INVITE_DELIVERY_FAILED`** — délibérément, plutôt qu'un `INTERNAL_ERROR` opaque — et la
      transaction est annulée : aucune invitation fantôme ne subsiste.
- [ ] **`AdDto` ne porte aucun champ de contact** — le `grep` et le script Python du §5.3 du document
      d'API. Le jour où ils remontent quelque chose, le relais a été contourné et une adresse
      personnelle est publiée à toute une équipe, irrévocablement.
- [ ] **Secrets dans les journaux d'accès** — `?t=` (jeton de tuile, ~15 min) et `?token=` (flux
      ICS, **sans expiration**) sont écrits en clair par Traefik et par le Caddy de l'hôte. Le
      masquage n'est prescrit que pour Caddy, et rien dans le dépôt ne dit qu'il est en place ;
      Traefik (`docker-compose.yml`, `--accesslog=true`) n'a aucun filtre de champs. Configurer le
      masquage des deux paramètres dans les deux, ou retirer le champ de la requête du journal
      Traefik. Source : [`OPERATIONS.md`](OPERATIONS.md#redacting-credentials-from-access-logs).
      Voir aussi §7.1 (le jeton ICS qui n'expire jamais).
- [ ] **Copie à froid des données Valhalla** — `~/shared/data/valhalla` (~17 Go, des heures à
      reconstruire) est hors des sauvegardes nocturnes : la copier **une fois** vers l'hôte de
      sauvegarde, puis à chaque changement d'extrait OSM. Rien ne dit que c'est fait, et les données
      tileserver, nommées comme « à reconstruire à la main », ne sont couvertes par aucune procédure.
      Source : [`OPERATIONS.md`](OPERATIONS.md#cold-backup-of-the-shared-stack).

---

## 2. Reprises immédiates, petites et sans décision à prendre

Les reprises faites (2.1 à 2.4, suppression de l'ancien import biketeam) sont dans
[`LEDGER_DONE.md`](LEDGER_DONE.md) §2.

- **Purge physique des équipes à la corbeille** (reset de migration biketeam) — chantier séparé. Un
  `reset` de la migration en direct met l'équipe Pédalons à la corbeille et libère son slug
  (`TeamService.deleteTeam`), rien de plus : lignes `team_entities`, assets et fichiers S3 restent.
  Chaque reset en laisse un exemplaire de plus. **Quand** : si le volume le justifie, ou avec une
  politique de rétention générale de la corbeille. Décision du 2026-09-22
  ([plan](plans/2026-09-22-biketeam-live-migration.md) §13, décision 10).

Relevés le 29 septembre 2026, en vérifiant `docs/*.md` contre le code :

- **Survol des cartes web sans effet** — l'ombre `md` des cartes est déclarée en `'&:hover'` dans
  la prop `styles`, que Mantine rend en style inline : le pseudo-sélecteur est ignoré. À passer par
  une classe CSS (module ou `classNames`) ; [`BRANDING.md`](BRANDING.md) §7.1 le note comme absent
  d'ici là. Le même motif `'&:hover'` dans `styles` est ignoré aussi dans `CardTeamLink.tsx`,
  `TeamContextBanner.tsx` et `TripStageCard.tsx` (soulignement au survol). Une fois corrigé, aligner
  [`BRANDING.md`](BRANDING.md) §5.2, qui décrit l'ombre au survol comme acquise et contredit §7.1.
- **Cache gpx2web : commentaire périmé et tuiles d'élévation non revues** — `.env.example:51-52`
  dit encore que gpx2web écrit les tuiles directement à leur chemin final ; depuis gpx2web 1.5.1
  (le dépôt est en 1.5.2) les tuiles de carte sont écrites puis renommées. Corriger le commentaire.
  Les tuiles d'élévation, elles, n'ont pas été revues et ne sont gardées que par un verrou interne
  à la JVM : tant que ce n'est pas fait, `DATA_CACHE_PATH` ne se partage pas entre backends.
  Source : [`OPERATIONS.md`](OPERATIONS.md) (services « per-environment on purpose »).
- **`PUBLIC_UNLISTED` indexable** — la moitié « non indexé » de la visibilité manque :
  `frontend/index.html` sert un `<meta name="robots" content="index, follow">` statique et rien
  n'émet de `noindex` par page ; les pages non listées étant rendues en SSR, un robot les indexe.
  Le correctif va dans les `meta()` de `routes.config.ts`. Source : [`BACKLOG.md`](BACKLOG.md)
  (« Visibility Controls »).
- **NPE 500 sur un `media` incomplet** — relevée le 20 septembre 2026 pendant la recette des
  notifications : `POST /api/teams/{slug}/rides` avec `media.assets = {}` lève une
  `NullPointerException` dans `AssetService.updateAssets`, qui déréférence `assets.images()` sans
  garde ; même risque sur `attachments()`, sur `assets` nul et sur `markdown` nul. `AssetsDto` ne
  pose ses listes vides que dans son builder, que Jackson n'emprunte pas pour un record, et
  `@Schema(required = true)` ne valide rien. Invisible depuis les clients (ils envoient toujours des
  listes) ; répondre 400 ou normaliser à vide.
- **`.env.example:143` décrit un `BACKUP_KEEP` que rien ne lit** — la rétention est le second
  argument de `scripts/backup-prune.sh`, sur l'hôte de sauvegarde. Retirer la ligne, comme dans
  [`OPERATIONS.md`](OPERATIONS.md).
- **Javadoc périmée de `contentVisibility`** (`BiketeamMigrationService`) — elle dit que rabattre le
  `PUBLIC_UNLISTED` de l'équipe sur ses contenus ne changerait rien ; en réalité le fil de l'équipe
  se viderait, les listes limitées à l'équipe exigeant `te.visibility = 'PUBLIC'`
  ([`MIGRATE_BIKETEAM.md`](MIGRATE_BIKETEAM.md) a été corrigé).

---

## 3. Le résidu du portage web

### 3.1 T5.4 — Trombinoscope : la page web publique (S)

Le reste est livré (contrat `3.0.0`, réglage `Team.enableMemberDirectory`, autorisation graduée,
invitations par e-mail) : [`LEDGER_DONE.md`](LEDGER_DONE.md) §3.1, avec les décisions à ne pas
défaire.

La **page web** du trombinoscope n'est pas écrite : la route `teamMembers` existe
dans `contracts/routes.yaml` en `web: false`, et le lien « N membres » de `TeamAboutPage` reste
inerte. Repartir de `TeamMembersPage` amputée des actions d'admin. **Taille : S.** Le mobile, lui, est
fonctionnel.

T5.5 (compléments d'annonces), T3.5 (profil altimétrique, tranchée en 2.0.0) et la refonte
sémantique de `NavButtons` sont livrées : [`LEDGER_DONE.md`](LEDGER_DONE.md) §3.2 à §3.4.

### 3.4 Anneau de focus global sous le seuil de contraste

Resté ouvert après `NavButtons` : l'anneau de focus global est à 2,74:1 en thème sombre, sous le
seuil de 3,0 de SC 1.4.11 — il vient de `lib/theme.ts` et vaut pour tout le site.

---

## 4. Les quatre chantiers d'infrastructure d'API

Seul le push (§4.2) a été livré depuis ([`LEDGER_DONE.md`](LEDGER_DONE.md) §4.2 ; ce qui en reste
est au §8.3) ; les trois autres n'ont pas été commencés. Le détail chiffrable — contrat, modèle, déclenchement, risques — est au §4 du
[document d'API archivé](plans/archive/2026-07-26-api-v2-livraison-et-suites.md). Résumé et ordre
recommandé :

### 4.1 Pagination par curseur — **à faire avant d'étendre le scroll infini** (L)

C'est le seul des quatre qui a une **contrainte d'ordre** : il touche `BaseRepository.getPage`,
`PedalonsQuery` et tous les `…ListResponse`. L'offset actuel duplique et saute des éléments dès que
la liste bouge sous le curseur — sur `n-peloton` ce n'est pas un cas limite.

Trois règles à tenir : `cursor` et `page` mutuellement exclusifs (les deux ⇒ **400**, pas un
comportement silencieux) ; `nextCursor` renvoyé **toujours**, y compris en mode offset (c'est le
chemin de migration des clients) ; `total` reste calculé — les compteurs de tête de liste en
dépendent, et le pied de liste mobile (« 60 membres sur 1 999 ») l'exige. La clé de tri doit
**toujours** se terminer par `id`, sinon elle n'est pas totale. Le tri variable des parcours
(`sortBy` × `sortDir` × un `price` nullable) est le point délicat : un test par critère.

Note : le plan mobile a explicitement retenu l'offset **et** l'a justifié (le curseur ne fournit pas
le `total` que le pied de liste maquetté exige). Livrer le curseur ne suffit donc pas à faire basculer
le mobile — la cohabitation ci-dessus est ce qui rend la bascule possible.

### 4.2 Notifications push — livré

En production depuis le 21 septembre 2026, le Web Push depuis le 29 : voir
[`LEDGER_DONE.md`](LEDGER_DONE.md) §4.2. Ce qui reste est au §8.3.

### 4.3 Cache, fraîcheur et images (C.1 M · C.2 M · C.3 M · C.4 S)

Quatre briques indépendantes :

- **`ETag` / `If-None-Match`** — le verrou est que les champs « moi » (`registered`,
  `registeredGroupId`, `full`, `commentCount`) rendent les réponses dépendantes de l'appelant. Un
  `ETag` calculé sur le contenu métier seul serait faux et un cache partagé servirait à Alice la
  réponse de Bob. Recommandation actée : intégrer `userId` au calcul et servir
  `Cache-Control: private` — le gain visé est le cache **du client**, pas d'un CDN. Commencer par les
  détails, pas les listes.
- **`updatedAt` en liste + `?updatedSince=`** — c'est ce qui rend une synchro incrémentale possible.
  **À décider avant de coder** : `updatedSince` ne dit rien des suppressions. Soit des tombstones
  (`deleted_at` interrogeable), soit une resynchronisation périodique complète.
- **URLs d'images signées** — HMAC sur `(chemin, expiration)`. Piège : une URL qui expire pendant
  qu'une image est en cache donne une image cassée **sans erreur lisible** ; prévoir la
  renégociation client.
- **`blurHash`** — 30 octets par image, calculés une fois à l'upload. Le seul point à trancher :
  faire de `thumbnailUrl` un objet `{url, blurHash, width, height}` est un **MAJOR** (changement de
  type sur un champ livré en 1.3.0) ; un champ frère `thumbnailBlurHash` ne l'est pas.

Débloque le chargement progressif du parcours, de vrais placeholders colorés à la place des
squelettes, et le **hors-ligne — qui n'est dans aucune maquette et devrait l'être avant d'être
promis**.

### 4.4 Carte multi-entités `GET /api/map/features` (M)

Une `FeatureCollection` légère par bbox et par types (`RIDE`, `ROUTE`, `PLACE`, `AD`), plus
`…/places/bounds`. Le point dur est de **ne pas contourner les règles de visibilité** : une union de
requêtes projetées, une par type, jamais du HQL brut sur `team_entities`. Plafond dur de features et
refus explicite des bbox trop grandes, sinon `types=ROUTE` sur le monde renvoie 2 585 features. Cet
endpoint doit rester **non paginé et borné**.

Les features `type=AD` sortent **floutées** et doivent se rendre en **secteur, jamais en punaise** —
une carte qui mélange les types est précisément l'endroit où l'on dessinerait tout le monde avec le
même marqueur.

À noter : ce n'est **pas** un prérequis de la vue carte des parcours, qui fonctionne déjà par les
tuiles MVT. C'est ce qui permet d'y **ajouter** les autres entités.

### 4.5 Coût du lot de géométries — ce qui reste après 2.0.0

Le paramètre `simplify` et `…/elevation-profile` ont été supprimés en 2.0.0 (voir
[`LEDGER_DONE.md`](LEDGER_DONE.md) §4.5).

`MAX_BULK_SLUGS = 50` est désormais le *seul* garde-fou du
lot, sur un endpoint `@PermitAll` sans rate limiting en lecture. Un lot réaliste de 50 parcours pèse
3,2 Mo ; les 50 plus lourds de la base pèsent 64 Mo. Ce cas suppose un appelant qui sait lesquels
sont les plus lourds et les nomme tous : c'est un sujet de **rate limiting**, pas de contrat. Le
levier si ça devient sensible est de descendre `MAX_BULK_SLUGS` vers ~15 (les appelants réels
plafonnent à 12, `kTripTrackStageCap`), pas de réintroduire un paramètre de finesse.

---

## 5. Petites évolutions d'API qui suppriment chacune une dégradation nommée

Extrait du §5.2 du plan mobile. Aucune ne bloque un écran ; chacune retire un repli visible.
Beaucoup sont des ajouts d'un champ — le rapport valeur/effort y est bon.

| # | Manque | Écrans | Dégradation actuelle |
|---|---|---|---|
| 2 | `logoUrl` sur `TeamPublicationDto` (`TeamDetailDto` l'a déjà) | 11, 12, 13, 24, 31, 32 | Avatar d'initiales à teinte hachée |
| 3 | `RideGroupDto.thumbnailUrl` | 11 | Vignette de la sortie au lieu de celle du parcours du groupe |
| 4 | `groups[]` ou un `registeredGroup` compact sur les lignes de liste | 11 | Un `getRide` supplémentaire pour la seule prochaine sortie |
| 5 | Capacité agrégée sur `RideDto` de liste | 11 | « N participants » au lieu de « N/M » |
| 6 | `PostDto.createdByDisplayName` / `createdById` | 31 | Bloc auteur supprimé, seule la date reste |
| 7 | `AssetDto.size` | 31, 32 | « PDF » au lieu de « PDF · 240 Ko » |
| 8 | Voisins de publication (`prev`/`next`) *(absence à reconfirmer — recherche ciblée seulement, pas de grep exhaustif sur toutes les resources de publication)* | 31 | Navigation rendue seulement depuis un fil déjà chargé |
| 9 | `RouteUsageDto.endDate` | 13 | Date de début seule pour un usage de type voyage |
| 10 | `ClimbDto.name` | 13, 25 | « Montée N » |
| 11 | Commentaires d'étape | 25 | Section absente, renvoi vers le voyage |
| 12 | Participants paginés et cherchables côté serveur | 24, 34 | Liste complète embarquée, recherche client, pas de pied « N sur M » |
| 13 | Tri sur `GET /api/teams` | 34 | Mention « triées par nombre de membres » retirée |
| 14 | `logoUrl` de service GPS (`GpsServiceConnectionDto`) — `SocialIdentityDto.externalUsername` n'a plus d'objet : la connexion Strava a été retirée (API `5.0.0`) | 33 | Nom du service et « Connecté le *date* », sans logo |
| 15 | `Team.timezone` ou dates zonées au contrat | 22, 24, 25 | Fuseau de l'appareil ; le web applique en plus la préférence `UserDto.timezone`, que le mobile ignore |
| 16 | Statut `TERMINÉE` dans l'enum `Status` | 11, 12, 22 | Dérivé client de `dateTime < now`, centralisé dans `RideDto.isPast` |
| 17 | `?format=polyline` sur la géométrie de parcours | — | La géométrie stockée est déjà allégée à l'import (§4.5 : 681 points et 65 Ko pour le parcours médian) ; ~÷4 sur le poids, au prix d'un décodeur Dart. **À rouvrir seulement sur une mesure réelle** |
| 18 | Voyage comme événement multi-jour au calendrier (`CalendarEventType`) | 22 | Les étapes y sont, le voyage en tant qu'objet non |
| 19 | `GET /api/search?q&types=&limit` unifié | — | Plus aucune recherche transverse : `GET /api/users/search` a été **supprimé** en `3.0.0` (voir §3.1). La seule recherche de personnes est celle du trombinoscope d'une équipe |
| 20 | Pagination du calendrier | 22 | Fenêtre fixe −30 j / +180 j, non paginée |

Le meneur de groupe (livré en 1.5.0) et le tableau des évolutions livrées sont dans
[`LEDGER_DONE.md`](LEDGER_DONE.md) §5. Les **gabarits de sortie n'ont volontairement pas de
meneur** — décision produit : `RideTemplateGroupRequest` reste sans champ.

---

## 6. Ce qui reste délibérément dehors

À relire avant de rouvrir l'un de ces points : chacun a été écarté avec un motif, et plusieurs
sont des invariants que le code garde.

| Sujet | Décision | Ce que ça implique |
|---|---|---|
| **Liste d'attente (`waitlisted`)** | N'existe pas en base ; ni colonne, ni statut, ni rang sur `RideParticipation` | « Complet » est un **état terminal**. Ne pas câbler un `waitlisted: false` en dur : un champ toujours faux rend la vraie fonctionnalité indétectable en revue |
| **Repli sur `createdBy` pour le meneur** | **Interdit partout** — base, DTO, client | `createdBy` vaut le créateur de la **sortie**, donc le même nom sur tous ses groupes : un repli serait faux presque partout, et faux de la façon qui ne se signale pas. C'est le défaut que `leader_id` corrige. Gardé par `groupLeader_isNotTheRideCreator` |
| **Position exacte d'une annonce** | Floutée à ~1 km, **et la sonde de proximité quantifiée sur la même grille** | Flouter la sortie ne suffit pas : répéter « cette annonce est-elle à moins de R de C ? » en déplaçant C multilatère la position réelle. D'où le rayon arrondi au multiple de cellule (3 km servis comme 3,33 km) : l'interface annonce un **ordre de grandeur**, pas une valeur exacte. Et **jamais de punaise**. **L'audit de sécurité garde pourtant M2 ouvert** (le flou peut encore être affiné par des requêtes répétées) : la quantification ne suffit pas, voir §7.1 |
| **Champ de contact libre sur une annonce** | Écarté au profit du relais e-mail | C'était la solution la moins chère, et elle publie une donnée personnelle **irrévocablement** à toute l'équipe (jusqu'à 1 999 personnes) : ce qui a été lu ne se dépublie pas. Retirer le champ plus tard ne répare rien |
| **`GET /api/rides` et listes mono-type** | Non créées ; `/api/publications?type=RIDE` est la surface canonique | Deux surfaces = deux jeux de filtres à garder cohérents. `RideListResponse` / `TripListResponse` existent encore comme records retournés par **aucun endpoint** — les supprimer serait un MAJOR gratuit |
| **Scroll infini côté web** | Non porté | Incompatible avec la règle structurante du frontend (filtres et pagination dans la query string, donc toute vue partageable). `usePaginatedQuery` précharge déjà la page suivante **et** la précédente |
| **Gabarits tactiles portés au web** | Non portés | Feuilles à crans, barre d'onglets basse, app bar interpolée, chips en remplacement des `Select` : ils résolvent une contrainte que le desktop n'a pas, et produiraient des composants hors Mantine |
| **Minimum de 44 px sur les boutons web** | Règle **tactile** uniquement | Le web descend à 36 px au-dessus de 768 px. Ne pas prendre `pedalons.css` pour une spécification web |
| **Jetons `--pdl-*` au web** | Non introduits | Le site a déjà la charte en thème Mantine ; une seconde couche de variables créerait deux sources de vérité |
| **Mode sombre dérivé au web** | Sans objet | Le tableau de parité est un livrable **pour Flutter**, parce qu'aucune maquette ne fournit le sombre. Mantine l'a déjà |
| **Jeu d'icônes Tabler côté mobile** | Material outline conservé | L'écart ne porte que sur la graisse du trait des icônes de badge de 11 px. `PdlIcons` devrait être le **seul** fichier à nommer `Icons.*` (c'est tenu dans `lib/core/pdl`, pas encore ailleurs : une vingtaine de fichiers le font encore) : une fois ce ménage fait, basculer ne touchera qu'un fichier |
| **Écran de profil public d'un membre** | Aucune maquette ne va au-delà de la liste | Les lignes du trombinoscope ne sont pas cliquables. Ne pas inventer l'écran |
| **Édition et création de contenu au mobile** | Hors brief : la v2 est une version de consultation et de participation | Le sélecteur de meneur dans l'éditeur de groupes existe **côté web** (livré hors plan) ; l'équivalent mobile n'est pas ouvert |
| **Badge iOS du push** | Écarté, côté serveur comme côté app | Serveur : il faudrait recompter les non-lues à l'envoi, et `NotificationMessage` ne porte ni le domaine ni ce compteur. App : `flutter_local_notifications` ne pose un badge qu'en affichant une notification, et en arrière-plan c'est le système qui affiche celle de FCM — une dépendance de plus pour un compteur que la cloche montre déjà. D'où l'absence de `content-available` (voir `LEDGER_DONE.md` §4.2) |
| **Isolat de fond du push (`onBackgroundMessage`)** | Non écrit | Le serveur envoie `notification` **et** `data` : le système affiche la bannière sans l'app, un isolat n'aurait rien à faire de plus |
| **Tests Vitest de la cloche et de la page Notifications** | Écartés le 29 septembre 2026 | La recette navigateur les a validées ; le mobile a son test de widget (`notifications_page_test.dart`) |
| **Contenu masqué d'un compte effacé** | Reste masqué | L'effacement supprime les signalements visant le membre, mais ne touche pas `moderationHiddenAt` sur ses sorties, parcours, posts et voyages. Ce contenu, masqué par 3 signalements, n'a plus d'entrée dans la file et reste invisible pour les membres. C'est voulu : le démasquer republierait un contenu signalé 3 fois |
| **`acceptTerms` obligatoire à l'inscription (contrat `4.1.0`)** | Laissé en mineure | Les builds mobiles qui n'envoient pas le champ reçoivent un 400 `VALIDATION` à l'inscription. La rupture est acceptée sans passer en `5.0.0` |

---

## 7. Le backlog produit et les audits

Ce fichier ne couvre que les suites de la v2 et des chantiers qui l'ont suivie. Quatre autres sources
restent ouvertes :

- [`BACKLOG.md`](BACKLOG.md) — la roadmap produit (P0 → Icebox). Y figurent notamment le statut
  « Terminée » sur les sorties et voyages (qui recoupe le point 16 du §5 ci-dessus) et le système de
  notifications (qui recoupe le §4.2 et le §8.3).
- [`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) — audit d'infrastructure,
  CI/CD et qualité des modules, statuts rafraîchis le 29 septembre 2026. Ses lignes critiques encore
  ouvertes : rate limiting sur `/api/device/oauth/complete`, pipeline CD, `maximum-scale=1.0` du
  viewport, healthchecks Docker partiels, `forwardedHeaders.insecure` sur Traefik, URL de
  production en dur dans l'app Garmin, `MainActivity.kt` monolithique côté Karoo. Les backups
  PostgreSQL et MinIO existent (`scripts/backup.sh`, `scripts/restore.sh`). Côté tests frontend,
  une suite e2e existe depuis le 25 septembre 2026 (`frontend/e2e/`, §1.2), mais elle ne tourne
  qu'en local : aucune CI ne la lance.
- [`SECURITY_AUDIT.md`](SECURITY_AUDIT.md) — audit de sécurité de septembre 2026 ; il fait foi pour
  les vulnérabilités, l'audit de février pour l'infrastructure.
- [`plans/2026-09-29-privacy-policy-open-points.md`](plans/2026-09-29-privacy-policy-open-points.md) — ce que la politique de
  confidentialité ne dit pas encore, ou mal, et qui demande une décision juridique : import
  biketeam, conservation des messages d'annonce, position précise envoyée par Garmin et Karoo,
  contenu lisible sans compte, sous-traitants du Web Push.

### 7.1 Audit de sécurité — constats ouverts

[`SECURITY_AUDIT.md`](SECURITY_AUDIT.md) fait foi pour les statuts et reste expurgé : cette liste ne
fait que les suivre ici, sans détail. Quand un constat est corrigé, mettre à jour **les deux**
fichiers (statut et commit dans l'audit, ligne déplacée dans `LEDGER_DONE.md` §7.1). Relevé le
29 septembre 2026 : aucun commit de code n'a touché ces points depuis la mise à jour de l'audit.

| Priorité (audit) | # | Sévérité | Constat |
|---|---|---|---|
| 1 | H2 | Élevée | Des fichiers téléversés peuvent être servis de façon à exécuter du contenu actif |
| 1 | H3 | Élevée | L'autorisation d'un appareil peut aboutir sans confirmation explicite |
| 1 | H4 | Élevée | L'app mobile peut transmettre ses identifiants à d'autres hôtes que l'API |
| 2 | V1 | **Critique si confirmé** | Clé JWT présente dans l'historique public : vérifier que prod et staging n'en sont pas des copies |
| 4 | M3 | Moyenne | Traitement GPX non borné en mémoire |
| 4 | M4 | Moyenne | Connexion par mot de passe sans limitation de débit ni verrouillage |
| — | M2 | Moyenne | Flou d'~1 km des annonces affinable par requêtes répétées (contredit la décision du §6) |
| — | M5 | Moyenne | Login CSRF via le lien de vérification d'e-mail |
| — | M6 | Moyenne | ReDoS sur une expression régulière appliquée au markdown |
| — | L1, L3–L10 | Faible | Voir la table des constats faibles de l'audit |
| — | L11 | Faible | Durcissement des workflows GitHub Actions — partiel, `ci.yml` seulement |
| — | Info | — | Images externes dans le markdown, parseur XML non durci, paramètre de requête non encodé |
| — | V2 | — | Reliquat : les comptes déjà rattachés par l'ancien import biketeam |
| — | V3–V8 | À valider | Configuration hors dépôt : proxy de l'hôte, hôte de sauvegarde, SMTP, imgproxy |

Hors audit, relevé dans [`OPERATIONS.md`](OPERATIONS.md#redacting-credentials-from-access-logs) :
le **jeton du flux ICS n'expire jamais** — seule la régénération manuelle
(`CalendarService.regenerateToken`) le révoque. Une expiration, ou au moins le masquage du §1.3, est
ce qui borne sa fuite par un journal.

---

## 8. Suites des chantiers de septembre 2026

### 8.1 Signaler un problème → issues GitHub (API 4.6.0) — suites possibles, non faites

En service en production depuis le 29 septembre 2026 ([`LEDGER_DONE.md`](LEDGER_DONE.md) §8).

- **Piles web illisibles** : le bundle est minifié et l'empreinte ne garde que le nom du chunk. Pour
  symboliser, construire avec `VITE_BUILD_SOURCEMAP=true` et **ne pas** servir les `.map` (les
  archiver avec l'image), puis automatiser la symbolisation côté serveur.
- **Côté mobile**, si le build release passe à `--obfuscate`, conserver les `--split-debug-info` par
  build, sans quoi les piles Dart deviennent inexploitables et l'empreinte change à chaque version.
- **Plantages natifs** (Kotlin/Swift) : non couverts, seuls les handlers Dart remontent.
- **Boucler avec le membre** : le prévenir quand l'issue de son signalement est fermée (webhook
  GitHub → notification), joindre une capture d'écran, corréler avec les logs serveur par request-id.
- **Visiteurs non connectés** : ni signalement ni remontée automatique (l'endpoint exige une
  session). Une erreur sur la page de connexion n'arrive donc que par e-mail.

### 8.2 Modération — quatre défauts mineurs, notés sans être corrigés

Livrée le 24 septembre 2026 ([`LEDGER_DONE.md`](LEDGER_DONE.md) §8).

- **File plateforme regroupée par (type, id) seulement** (`ModerationService`) : un membre signalé
  dans deux équipes devient une seule carte, étiquetée avec la première équipe, et une seule décision
  clôt les signalements des deux. Regrouper par (type, id, équipe).
- **Signalements orphelins après suppression** : `REMOVE_CONTENT` sur une publication laisse
  `OPEN` les signalements de ses commentaires, qui pointent alors vers un contenu supprimé. Les clore
  en même temps.
- **Seuil de masquage sous concurrence** (`ReportService`) : le nombre de signalants est compté
  dans la transaction de chaque signalement. Deux signalements validés au même instant peuvent
  chacun voir 2 signalants, et le contenu n'est pas masqué avant un 4e. Verrouiller la ligne du
  contenu (`SELECT … FOR UPDATE`) avant de compter.
- **Notification de signalements fusionnés** (`NotificationRecipientResolver`) : une rafale de
  signalements donne une seule notification qui ne connaît que le premier. Seul ce premier signalant
  est exclu des destinataires, et un organisateur qui vient de signaler est donc notifié de son propre
  signalement.

### 8.3 Notifications — ce qui reste

Les phases 1 à 5 de [`plans/2026-09-18-notifications.md`](plans/2026-09-18-notifications.md) sont en
production, le Web Push aussi depuis le 29 septembre 2026 ; ce qui en est livré, et les pièges à ne
pas rejouer, sont au §4.2 de [`LEDGER_DONE.md`](LEDGER_DONE.md) (le ledger du chantier y a été
rapatrié le 29 septembre 2026). Restent deux points qui ne sont pas du code, et un qui l'est :

- **Recette du webhook d'équipe** contre un vrai Slack, un vrai Discord et un vrai Mattermost
  (bouton « Envoyer un test ») — vérifier au passage qu'un `@channel` dans un nom de sortie ne
  notifie personne sur Mattermost.
- **Décision produit sur l'e-mail** : `PEDALONS_NOTIFICATIONS_EMAIL_ENABLED` reste à `false` en
  production (décision du 21 septembre 2026, « pas pour le moment »). L'activer, c'est écrire aux
  équipes entières, en connaissant les défauts (annulations, voyages publiés).
- **Webhook d'équipe : le *DNS rebinding* n'est pas couvert** — `WebhookHttpClient` refuse toute
  résolution vers une adresse interne, mais `HttpClient` résout de nouveau à la connexion : un nom
  qui change de réponse entre les deux passe. Le fermer voudrait dire épingler l'adresse vérifiée
  pour la connexion (voir la javadoc de `WebhookHttpClient`).

### 8.4 Couverture e2e — ce que l'audit du 27 septembre laisse ouvert

L'audit ([archivé](plans/archive/2026-09-27-e2e-coverage-audit.md)) est exécuté : P0, P1 et P2
écrits, 54 défauts relevés, tous corrigés ou tranchés sauf un.

- **Point 40 — erreur d'hydratation React #418 (texte), intermittente**, sur `/equipes/{slug}` en
  membre, sur mobile. Vue une fois pendant la validation des P0 (`team-misc.e2e.ts`), non reproduite
  en 4 répétitions ; cause inconnue. À surveiller.
- **Le canal e-mail des notifications** n'a pas de test e2e : il est coupé en production comme sur
  la stack e2e (§8.3).
- **Idées de la liste « P2 (à planifier) » qui n'ont pas été retenues** parmi les 21 tests P2
  écrits — quelques-unes peuvent être couvertes au passage par une autre spec, à vérifier avant de
  les écrire :
  - Sorties : modale des participants avec le meneur marqué ; publication programmée dans le passé
    refusée ; calendrier au-delà de la fenêtre préchargée ; recherche dans les modèles.
  - Articles et voyages : commentaire d'un compte supprimé ; modale des participants d'un voyage ;
    étape d'une équipe dont le module est coupé.
  - Parcours : « cul-de-sac » (`RouteDeadEnd`) ; unités impériales dans les filtres ; bouton plein
    écran et retour ; « Enregistrer comme parcours » limité aux équipes éligibles ; carte d'équipe
    sans fuite des tuiles TEAM ; hub GPX face à un fichier illisible.
  - Équipe : promotion en ADMIN et filtre par rôle ; page publique, suppression et restauration
    d'une page, plafond de 3 (les pages supprimées comptent-elles ?).
  - Annonces : brouillon d'autrui introuvable par son URL ; défauts du formulaire (DRAFT par
    défaut, Annuler) ; signalement puis redirection.
  - Transverse : navigation mobile (tiroir, entrée Admin selon le rôle, fil d'Ariane « Plus ») ;
    restauration du défilement au retour.

### 8.5 Charte — le code couleur métier n'est synchronisé par rien

Constaté le 29 septembre 2026, en fusionnant `brand.md` dans [`BRANDING.md`](BRANDING.md).
L'association énumération → famille de couleur (`RIDE` → blue, `CANCELLED` → red, `HC` → grape…)
est écrite à la main côté web dans `frontend/src/components/card/common/badgeColors.ts` (types,
statuts, rôles, surfaces, visibilités ; nom Mantine), `RouteDetailView.tsx` (`getClimbCategoryColor`,
catégories de col) et `CardImage.tsx` (dégradés de repli), et côté mobile dans
`mobile/lib/core/theme/enum_colors.dart` (paire `PdlTone`). Aucun test ne les compare ;
`mobile/test/core/theme/pdl_tokens_test.dart` ne fige que les hexadécimaux du mobile. Une
divergence ne casse rien de visible, c'est justement le risque.

- **Divergence déjà là** : `frontend/src/pages/ad/AdDetailPage.tsx` garde sa propre table
  `adTypeColors` (SALE `primary`, RENTAL `grape`, WANTED `yellow`) alors que `badgeColors.ts` et
  le §3.6 de la charte disent SALE green, RENTAL indigo, WANTED orange. À réaligner tout de suite
  en passant par `badgeColors.ts`, sans attendre le générateur.
- **À faire** : une source unique `contracts/brand-colors.yaml` (énumération → famille, plus les
  dégradés de repli), et un générateur sur le modèle de `pnpm generate-routes` qui produit
  `badgeColors.generated.ts` (qui remplace les trois sources web) et `enum_colors.generated.dart`. Seul le **choix de la famille** est
  partagé ; chaque client garde sa façon de la rendre (nuances Mantine d'un côté, `c.softXxx` de
  l'autre). `BRANDING.md` §3.6 renverra alors au YAML au lieu de recopier les tableaux.
- **Écarté** : un test qui parse les deux fichiers et vérifie qu'ils concordent — il détecte sans
  unifier, et repose sur des expressions régulières sur du TypeScript et du Dart.
- **Hors sujet** : les échelles de nuances, figées par Mantine.

### 8.6 Migration biketeam en direct — mise en production

En service en staging ; la mise en production attend biketeam
([plan](plans/2026-09-22-biketeam-live-migration.md) §10). Source :
[`MIGRATE_BIKETEAM.md`](MIGRATE_BIKETEAM.md).

- **Déroulé** : poser les secrets des deux côtés en production, puis un essai sur une petite équipe
  (`gaby`) contre staging, contre la production, et enfin le passage réel.
- **Redirections 302 → 301** : une fois les bascules stabilisées, poser
  `PEDALONS_REDIRECT_STATUS=301` côté biketeam. Pas avant : un 301 est mis en cache par les
  navigateurs et ne se rattrape pas.
- **Liens internes non réécrits** : les liens vers `prendslaroue.fr` dans les pages d'équipe (FAQ,
  descriptions) restent pointés vers biketeam et redirigent tant qu'il tourne. À corriger — à la
  main, par l'équipe, ou par une réécriture depuis la table d'URL — **avant** l'arrêt de biketeam.
- **Vignettes régénérées à chaque rejeu** : `updateRide`/`updateTrip` régénèrent les vignettes de
  sortie et de voyage sans condition (`RideService`, `TripService`) ; c'est le dernier coût d'un
  rejeu. Ne régénérer que si le parcours ou l'image en entrée a changé.
