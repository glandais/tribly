# La suite — ce qui reste après la v2

Écrit le 27 juillet 2026, au moment où la v2 mobile est terminée et le portage web livré à trois
tâches près. Ce fichier remplace les feuilles de route des plans archivés : ceux-ci gardent le
**pourquoi** des décisions, celui-ci porte le **reste à faire**.

Rien ici ne bloque quoi que ce soit. C'est la propriété qui compte : la v2 est livrable en l'état,
et chaque ligne ci-dessous supprime une dégradation nommée plutôt que de réparer une panne.

Sources : [`plans/archive/`](plans/archive/) (les trois plans du 26 juillet, avec leur §4/§5),
[`audit-ux/BRIEF.md`](audit-ux/BRIEF.md) (l'entrant de design),
[`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) (audit d'infrastructure,
encore ouvert).

**Contrat d'API au moment d'écrire : `3.0.0`.** Toute évolution d'API listée ici demande un bump de
`pedalons.api.version` dans `backend/src/main/resources/application.properties`, puis la
régénération des deux clients (compétence `contract-first-api`).

---

## A classifier

L'anneau de focus est à 2,74:1 en thème sombre, sous le seuil de 3,0 de SC 1.4.11. Il vient de lib/theme.ts et vaut pour tout le site — le corriger ici en ferait une PR d'un autre périmètre.

/equipes/{slug}/admin/parametres cassé en direct avec le SSR.

Une préférence de langue enregistrée ne peut jamais l'emporter : caches: ['localStorage'] réécrit la langue détectée avant que src/i18n/index.ts ne relise i18nextLng après init.

---

## 1. À recetter sur une application qui tourne

Rien de ce qui suit n'est couvert par les tests automatisés — soit parce que c'est du rendu, soit
parce que ça dépend d'un vrai fournisseur (mail, tuiles, GPS), soit parce que c'est un comportement
de première ouverture. `flutter analyze` est propre, les **480 tests mobiles** et le
`pnpm check` du web passent : ce qui suit est ce qu'ils ne peuvent pas dire.

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
      *(Corrigé le 27 juillet 2026 : le bandeau `GROUP_FULL` affichait « Groupe complet. » sans
      jamais dire lequel — `rides.failure.full` prend maintenant `{group}`, comme les bandeaux
      voisins.)*
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
      on cherche une **double conversion**, pas une localisation d'équipe (le contrat n'a aucun champ
      de fuseau, la parité est celle du navigateur).
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
      Aucune section Notifications, aucune cloche : il n'y a pas d'endpoint, et un emplacement mort
      est interdit.
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

- [ ] Les 5 écrans qui affichent le tracé complet d'un parcours : détail de parcours, carte plein
      écran de parcours et d'étape (les 4 via `useGetRoute`), carte de groupe d'une sortie (via
      `useRoutesBulk` sans `geometry:false`, `RoutesMapView.tsx`).
- [ ] « Ma prochaine sortie », badge `Inscrit` et « Mes participations » se rendent **après
      hydratation** : le SSR est anonyme. Vérifier qu'aucun de ces blocs n'apparaît dans le HTML
      initial (`curl` la page) et qu'ils ne clignotent pas à l'hydratation.
- [ ] Modale « Contacter le vendeur » : les quatre issues, brouillon **conservé** sur 429 et 500,
      et pas de double message (l'`Alert` de la modale **plus** le toast global — les quatre clés
      `errors.api.AD_CONTACT_*` doivent exister en `fr` et `en`). Le succès et
      `AD_CONTACT_OPTED_OUT` ferment la modale et se rendent en `Alert` **persistante** sur la page,
      le bouton disparaissant dans le second cas : c'est là que le toast global fait doublon, et
      `apiClient` n'a aucun moyen de le taire par appel.
- [ ] Annonces, détail : la galerie navigue aux flèches **et** aux vignettes, le plein écran sert la
      variante 1920 ; la carte de localisation rend un **disque sans punaise**, cadré sur son
      emprise, avec sa légende « à environ 1 km près » ; aucune section vide (pas de bloc
      Localisation sans lieu ni géométrie, pas de bloc Description sans corps).
- [ ] Annonces, liste : le tri et les bornes de prix survivent au retour arrière et au partage du
      lien (`?sort=`/`?dir=`/`?pmin=`/`?pmax=`), et « Effacer les filtres » **conserve** le tri.
- [ ] Page d'équipe en 1440×900 : le premier élément de contenu apparaît à moins de 220 px du haut.
- [ ] Une sortie à plus de 20 commentaires n'en charge que 20 au premier rendu.
- [ ] **Trombinoscope, la matrice rôle × réglage** — sur `gaby`, réglage désactivé : un membre
      ordinaire ne voit pas l'entrée « Membres » (mobile) et prend un 403 s'il force l'URL ; un
      organisateur voit la liste **sans les rôles ni les dates** ; le sélecteur de meneur de
      `RideEditor` propose toujours des candidats. Réglage activé : le membre voit tout. Puis
      `?search=` avec l'adresse **exacte** d'un coéquipier — **ne doit rien remonter** en membre et
      en organisateur, doit le remonter en admin. C'est le seul contrôle qui prouve que l'oracle
      d'énumération est fermé.
- [ ] **Invitation par e-mail** — inviter une adresse **avec** compte puis une **sans** : la réponse
      et l'écran doivent être **identiques**, seul le contenu du mail diffère (Mailhog en dev). La
      liste des invitations en attente affiche les deux ; « Renvoyer » remplace le jeton ; « Annuler »
      la retire. Accepter depuis un autre compte que l'adresse invitée : message dédié et bouton
      « se déconnecter ». Accepter deux fois : pas d'erreur, une seule adhésion.
- [ ] **Invitation d'une adresse sans compte, parcours complet** — inviter, s'inscrire par le lien,
      vérifier l'adresse, puis constater qu'on n'est **pas** encore membre et que l'invitation
      apparaît sur `/equipes` ; l'accepter. C'est le chemin que rien d'automatique ne couvre.

### 1.3 Backend et exploitation

- [ ] **Démarrage réel du backend** — les tests utilisent `drop-and-create` et ne passent pas par
      Flyway : un test vert ne prouve **pas** que les migrations s'appliquent sur une base existante.
      Attendre `Migrating schema … to version 34` au moins une fois, puis contrôler que
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
      (interdiction du projet). Le découpage par item est au §5.2 du même document. Deux classes à
      ne jamais désactiver pour faire passer un build : `…QueryCountTest` (elles échouent si
      quelqu'un réintroduit une requête par ligne) et `groupLeader_isNotTheRideCreator` (elle échoue
      si quelqu'un réintroduit un repli sur `createdBy`).
- [ ] **Le relais de contact en production** — les gabarits Brevo `ad-contact.fr` / `ad-contact.en`
      (identifiants 10 et 11, profil `%prod`) existent et l'envoi a été validé par un message réel.
      À revérifier après tout changement de compte Brevo : un identifiant manquant fait répondre
      **500 en nommant le template absent**, ce qui ressemble à un défaut de front et n'en est pas.
      L'API de prévisualisation de Brevo n'est pas exploitable — la seule recette est un envoi réel.
- [ ] **Les quatre gabarits d'invitation en production** — `team-invitation.{fr,en}` (12, 13) et
      `team-invitation-signup.{fr,en}` (14, 15) **existent et sont actifs** (créés le 1er août 2026,
      expéditeur `Pédalons ! <contact@pedalons.fr>`, id 1). Params des quatre : `appName`,
      `inviterName`, `teamName`, `invitationUrl`, `expiresInDays`. **Reste à faire : un envoi réel**,
      seule recette possible, comme pour `ad-contact`. Si un identifiant venait à manquer,
      `POST …/invitations` répond **500 `TEAM_INVITE_DELIVERY_FAILED`** — délibérément, plutôt qu'un
      `INTERNAL_ERROR` opaque — et la transaction est annulée : aucune invitation fantôme ne
      subsiste. En dev, Mailhog rend les branches `sendViaSMTP` et ne dépend d'aucun identifiant.
- [ ] **`AdDto` ne porte aucun champ de contact** — le `grep` et le script Python du §5.3 du document
      d'API. Le jour où ils remontent quelque chose, le relais a été contourné et une adresse
      personnelle est publiée à toute une équipe, irrévocablement.

---

## 2. Reprises immédiates, petites et sans décision à prendre

| # | Quoi | Où | Taille |
|---|---|---|---|
| ~~2.1~~ | ~~`GET /api/teams/{teamSlug}/classifieds/count`~~ — **fait (contrat `2.3.0`)** : `AdService.countAds` / `AdResource.countAds`, symétrique des quatre autres `…/count`, réutilise `AdQuery`/`countMatching` | backend | S |
| ~~2.2~~ | ~~Déclarer `Retry-After` dans le contrat sur les réponses **429** des autres endpoints à quota~~ — **fait (contrat `2.3.1`)** : seul `POST /api/users/me/export` manquait le header, ajouté sur `UserResource.requestExport`. Le mutator axios (`axiosInstance.ts`) le lisait déjà de façon générique sur l'objet `Response` brut, donc aucun changement frontend n'était nécessaire | contrat | S |
| ~~2.3~~ | ~~Mettre à jour le document d'API archivé, ou le laisser tel quel en assumant qu'il s'arrête à 1.5.0~~ — **tranché : laissé tel quel**, avec une note explicite ajoutée en tête renvoyant vers `NEXT.md`/git pour tout ce qui a suivi (1.5.1, 1.6.0, 2.0.0…). Le doc garde le *pourquoi* de la livraison qu'il décrit, pas l'état courant du contrat — le tenir à jour en ferait un second changelog à maintenir en double de `NEXT.md` | `docs/plans/archive/` | S |
| ~~2.4~~ | ~~`SlugService.RESERVED_SLUGS` (`bulk`, `count`, `bounds`, `tiles` pour `ROUTE` ; `reorder` pour `TEAM_PAGE`) n'empêche que les **nouvelles** écritures~~ — **vérifié le 31 juillet 2026, rien trouvé** : sur `pedalons-prod-postgres` (table unique `team_entities`, héritage single-table, `entity_type=2` pour `ROUTE` / `entity_type=4` pour `TEAM_PAGE`), 0 route sur 2 699 et 0 page d'équipe sur 2 porte un de ces slugs. Pas de renommage/backfill à faire | base de données (lecture seule) | S |

---

## 3. Le résidu du portage web

### 3.1 T5.4 — Trombinoscope : débloqué par un réglage d'équipe (contrat `3.0.0`)

**Livré, sauf la page web publique.** L'oracle d'énumération est traité, l'autorisation est graduée,
et l'ajout d'un membre par sélection d'utilisateur a été remplacé par une invitation par e-mail.

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

**Ce qui reste.** La **page web** du trombinoscope n'est pas écrite : la route `teamMembers` existe
dans `contracts/routes.yaml` en `web: false`, et le lien « N membres » de `TeamAboutPage` reste
inerte. Repartir de `TeamMembersPage` amputée des actions d'admin. **Taille : S.** Le mobile, lui, est
fonctionnel : l'écran existait déjà, seule l'entrée de navigation a été conditionnée au réglage.

### 3.2 T5.5 — Compléments d'annonces : livrée (juillet 2026)

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

### 3.3 T3.5 — Abandonnée, puis tranchée dans l'autre sens (2.0.0)

Alimenter `ElevationChart` par `…/elevation-profile` au lieu de la géométrie complète. **La prémisse
ne tenait pas** : il n'existe aucune vue web qui rende un profil *sans* rendre de carte, donc la
géométrie est de toute façon nécessaire et l'appel supplémentaire **ajoutait** une requête au lieu
d'en retirer une.

C'est finalement l'inverse qui a été fait, et pour la même raison : `…/elevation-profile` a été
**supprimé** de l'API en 2.0.0, et le mobile dérive désormais son profil de la géométrie comme le
web le faisait déjà. Voir §4.5. Ne pas rouvrir : le sujet est clos dans les deux clients.

### 3.4 ~~Refonte sémantique de `NavButtons`~~ — instruit **et livré** le 31 juillet 2026

**[`plans/2026-07-31-navbuttons.md`](plans/2026-07-31-navbuttons.md)** (mesures prises dans le
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
**9,37/21**). **Reste ouvert** : l'anneau de focus global à 2,74:1 en thème sombre, sous le seuil de
3,0 de SC 1.4.11 — il vient de `lib/theme.ts` et vaut pour tout le site, donc hors de ce chantier.

---

## 4. Les quatre chantiers d'infrastructure d'API

Aucun n'a été commencé. Le détail chiffrable — contrat, modèle, déclenchement, risques — est au §4 du
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

### 4.2 Notifications push (L, plus un délai de store)

Le seul mécanisme qui ramène un membre sans qu'il ouvre l'app. Trois déclencheurs : rappel J-1,
annulation de sortie, réponse à un commentaire. Six endpoints, deux ou trois entités, une migration.

Ce qui allonge le délai réel n'est pas le code : entitlement `aps-environment` et clé APNs `.p8` côté
Apple, `google-services.json` et permission runtime `POST_NOTIFICATIONS` côté Android 13+, mise à jour
des deux formulaires de confidentialité, **et une nouvelle soumission aux deux stores**. L'écran de
demande de permission Android n'est dans aucune maquette. Voir aussi
`mobile/store-metadata/data-safety.md`, qui est la source de vérité des déclarations.

Deux pièges qui cassent en production et pas en test : l'**idempotence** du rappel J-1 (sans marque
« rappel envoyé », un redémarrage renotifie tout le monde) et la purge des jetons périmés (retour
`UNREGISTERED` de FCM), sans quoi la table grossit indéfiniment.

Débloque la cloche de l'accueil, la section Notifications du profil (aujourd'hui **non rendue**, pas
un emplacement mort) et un écran « Notifications » qui n'est pas maquetté.

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

### 4.5 Coût par parcours de la géométrie — tranché en 2.0.0

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

**Ce qui reste ouvert, dit franchement.** `MAX_BULK_SLUGS = 50` est désormais le *seul* garde-fou du
lot, sur un endpoint `@PermitAll` sans rate limiting en lecture. Un lot réaliste de 50 parcours pèse
3,2 Mo ; les 50 plus lourds de la base pèsent 64 Mo. Ce cas suppose un appelant qui sait lesquels
sont les plus lourds et les nomme tous : c'est un sujet de **rate limiting**, pas de contrat. Le
levier si ça devient sensible est de descendre `MAX_BULK_SLUGS` vers ~15 (les appelants réels
plafonnent à 12, `kTripTrackStageCap`), pas de réintroduire un paramètre de finesse.

Côté mobile, l'écran qui paie est la **carte de masse** (`routes_mass_repository.dart`) : jusqu'à
~120 fiches à 65 Ko médian, soit quelques mégaoctets là où c'était quelques centaines de kilooctets.
Son levier est `PdlMassLayerController.limit`. À surveiller.

---

## 5. Petites évolutions d'API qui suppriment chacune une dégradation nommée

Extrait du §5.2 du plan mobile. Aucune ne bloque un écran ; chacune retire un repli visible.
Beaucoup sont des ajouts d'un champ — le rapport valeur/effort y est bon.

| # | Manque | Écrans | Dégradation actuelle |
|---|---|---|---|
| ~~1~~ | ~~URL de tuile authentifiable (jeton court en paramètre de requête)~~ — **livré en 2.3.0** : `POST /api/tiles/token` puis `?t=` sur les deux `.mvt`. Le repli GeoJSON et son plafond ont été supprimés, le tap lit les propriétés de la tuile comme le web | 21 | — |
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
| 14 | `SocialIdentityDto.externalUsername`, `logoUrl` de service GPS | 33 | Avatar-lettre et « Lié le *date* » |
| 15 | `Team.timezone` ou dates zonées au contrat | 22, 24, 25 | Fuseau de l'appareil, parité web |
| 16 | Statut `TERMINÉE` dans l'enum `Status` | 11, 12, 22 | Dérivé client de `dateTime < now`, centralisé dans `RideDto.isPast` |
| 17 | `?format=polyline` sur la géométrie de parcours | — | `?points=` couvre le besoin ; ~÷4 sur le poids, au prix d'un décodeur Dart. **À rouvrir seulement sur une mesure réelle** |
| 18 | Voyage comme événement multi-jour au calendrier (`CalendarEventType`) | 22 | Les étapes y sont, le voyage en tant qu'objet non |
| 19 | `GET /api/search?q&types=&limit` unifié | — | Plus aucune recherche transverse : `GET /api/users/search` a été **supprimé** en `3.0.0` (voir §3.1). La seule recherche de personnes est celle du trombinoscope d'une équipe |
| 20 | Pagination du calendrier | 22 | Fenêtre fixe −30 j / +180 j, non paginée |

**Le meneur de groupe n'est plus dans cette liste** : il est livré en 1.5.0. Et les **gabarits de
sortie n'ont volontairement pas de meneur** — ce n'est pas un manque à combler, c'est une décision
produit : `RideTemplateGroupRequest` reste sans champ, instancier une sortie depuis un gabarit ne
désigne personne.

---

## 6. Ce qui reste délibérément dehors

À relire avant de rouvrir l'un de ces points : chacun a été écarté avec un motif, et plusieurs
sont des invariants que le code garde.

| Sujet | Décision | Ce que ça implique |
|---|---|---|
| **Liste d'attente (`waitlisted`)** | N'existe pas en base ; ni colonne, ni statut, ni rang sur `RideParticipation` | « Complet » est un **état terminal**. Ne pas câbler un `waitlisted: false` en dur : un champ toujours faux rend la vraie fonctionnalité indétectable en revue |
| **Repli sur `createdBy` pour le meneur** | **Interdit partout** — base, DTO, client | `createdBy` vaut le créateur de la **sortie**, donc le même nom sur tous ses groupes : un repli serait faux presque partout, et faux de la façon qui ne se signale pas. C'est le défaut que `leader_id` corrige. Gardé par `groupLeader_isNotTheRideCreator` |
| **Position exacte d'une annonce** | Floutée à ~1 km, **et la sonde de proximité quantifiée sur la même grille** | Flouter la sortie ne suffit pas : répéter « cette annonce est-elle à moins de R de C ? » en déplaçant C multilatère la position réelle. D'où le rayon arrondi au multiple de cellule (3 km servis comme 3,33 km) : l'interface annonce un **ordre de grandeur**, pas une valeur exacte. Et **jamais de punaise** |
| **Champ de contact libre sur une annonce** | Écarté au profit du relais e-mail | C'était la solution la moins chère, et elle publie une donnée personnelle **irrévocablement** à toute l'équipe (jusqu'à 1 999 personnes) : ce qui a été lu ne se dépublie pas. Retirer le champ plus tard ne répare rien |
| **`GET /api/rides` et listes mono-type** | Non créées ; `/api/publications?type=RIDE` est la surface canonique | Deux surfaces = deux jeux de filtres à garder cohérents. `RideListResponse` / `TripListResponse` existent encore comme records retournés par **aucun endpoint** — les supprimer serait un MAJOR gratuit |
| **Scroll infini côté web** | Non porté | Incompatible avec la règle structurante du frontend (filtres et pagination dans la query string, donc toute vue partageable). `usePaginatedQuery` précharge déjà la page suivante **et** la précédente |
| **Gabarits tactiles portés au web** | Non portés | Feuilles à crans, barre d'onglets basse, app bar interpolée, chips en remplacement des `Select` : ils résolvent une contrainte que le desktop n'a pas, et produiraient des composants hors Mantine |
| **Minimum de 44 px sur les boutons web** | Règle **tactile** uniquement | Le web descend à 36 px au-dessus de 768 px. Ne pas prendre `pedalons.css` pour une spécification web |
| **Jetons `--pdl-*` au web** | Non introduits | Le site a déjà la charte en thème Mantine ; une seconde couche de variables créerait deux sources de vérité |
| **Mode sombre dérivé au web** | Sans objet | Le tableau de parité est un livrable **pour Flutter**, parce qu'aucune maquette ne fournit le sombre. Mantine l'a déjà |
| **Jeu d'icônes Tabler côté mobile** | Material outline conservé | L'écart ne porte que sur la graisse du trait des icônes de badge de 11 px. `PdlIcons` est le **seul** fichier autorisé à nommer `Icons.*` : basculer un jour ne touchera qu'un fichier |
| **Écran de profil public d'un membre** | Aucune maquette ne va au-delà de la liste | Les lignes du trombinoscope ne sont pas cliquables. Ne pas inventer l'écran |
| **Édition et création de contenu au mobile** | Hors brief : la v2 est une version de consultation et de participation | Le sélecteur de meneur dans l'éditeur de groupes existe **côté web** (livré hors plan) ; l'équivalent mobile n'est pas ouvert |

---

## 7. Le backlog produit et l'audit d'infrastructure

Ce fichier ne couvre que les suites de la v2. Deux autres sources restent ouvertes et n'ont pas été
relues dans cette passe :

- [`BACKLOG.md`](../BACKLOG.md) — la roadmap produit (P0 → Icebox). Y figurent notamment le statut
  « Terminée » sur les sorties et voyages (qui recoupe le point 16 du §5 ci-dessus) et le système de
  notifications versatile (qui recoupe le §4.2).
- [`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) — audit d'infrastructure,
  dernier contrôle le 1er avril 2026. Ses lignes critiques encore ouvertes : backups PostgreSQL et
  MinIO, rate limiting sur `/api/device/oauth/complete`, pipeline CD, `maximum-scale=1.0` du
  viewport, tests frontend.
