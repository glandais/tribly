# La suite — ce qui reste à faire

Écrit le 27 juillet 2026 (sous le nom `NEXT.md`), au moment où la v2 mobile est terminée et le
portage web livré à trois tâches près, et tenu à jour depuis (dernière relecture d'ensemble :
29 septembre 2026). Ce fichier remplace les feuilles de route des plans archivés : ceux-ci gardent le
**pourquoi** des décisions, celui-ci porte le **reste à faire**, et
[`LEDGER_DONE.md`](LEDGER_DONE.md) ce qui est livré.

Rien ici ne bloque quoi que ce soit. C'est la propriété qui compte : la v2 est livrable en l'état,
et chaque ligne ci-dessous supprime une dégradation nommée plutôt que de réparer une panne.

**Contrat d'API au 30 septembre 2026 : `7.3.0`.** Toute évolution d'API listée ici demande un bump de
`pedalons.api.version` dans `backend/src/main/resources/application.properties`, puis la
régénération des deux clients (compétence `contract-first-api`).

## La nomenclature

**Chaque entrée porte un identifiant `PRÉFIXE-n`** (`API-21`, `MOB-7`…) : c'est lui qu'on cite,
jamais un titre ni une position dans le fichier.

- **L'identifiant ne change jamais.** Il suit l'entrée quand elle est reformulée, déplacée, ou
  livrée : une entrée livrée passe dans [`LEDGER_DONE.md`](LEDGER_DONE.md) **sous le même
  identifiant**, une entrée écartée passe dans « Délibérément dehors » sous le même identifiant.
- **Un numéro ne sert qu'une fois.** Nouvelle entrée : le plus grand numéro du préfixe dans les deux
  fichiers, plus un — `grep -ohE 'API-[0-9]+' docs/LEDGER_*.md | sort -t- -k2 -n | tail -1`. Une
  entrée scindée garde son numéro pour une part et en prend un neuf pour l'autre ; un numéro
  abandonné n'est pas recyclé.
- **Un renvoi cite l'identifiant seul** — « ledger `WEB-3` » dans une doc, `docs/LEDGER_*.md WEB-14`
  dans un commentaire de code — sans dire s'il est dans `LEDGER_NEXT.md` ou `LEDGER_DONE.md` :
  c'est ce qui permet de livrer une entrée sans toucher à ceux qui la citent. `grep -rn 'WEB-14'`
  retrouve l'entrée et tous ses renvois.
- **Le préfixe dit où ranger l'entrée**, dans les deux fichiers : chacun a une section par préfixe,
  dans le même ordre. Les sous-titres à l'intérieur d'une section sont libres et peuvent changer.

| Préfixe | Domaine |
|---|---|
| `MOB` | Application mobile (Flutter) — dont la recette sur appareil |
| `WEB` | Site web (React, SSR) — dont la suite e2e Playwright |
| `API` | Contrat d'API et backend |
| `OPS` | Exploitation, déploiement, recette du backend en conditions réelles |
| `NOTIF` | Notifications (in-app, e-mail, push, webhook d'équipe) |
| `MOD` | Modération et signalement de contenu |
| `ISSUE` | « Signaler un problème » et remontée des erreurs vers GitHub |
| `MIG` | Migration biketeam → Pédalons |
| `BRAND` | Charte, code couleur métier |
| `SEC` | Suivi de l'audit de sécurité ([`SECURITY_AUDIT.md`](SECURITY_AUDIT.md)) |
| `AUD` | Suivi de l'audit d'infrastructure de février ([`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md)) |
| `LEGAL` | Politique de confidentialité, CGU, déclarations des stores |

Un préfixe nouveau s'ajoute à cette table (et une section aux deux fichiers) ; un préfixe existant
ne se renomme pas.

Sources : [`plans/archive/`](plans/archive/) (les trois plans du 26 juillet, avec leur §4/§5, et
les plans exécutés depuis),
[`plans/archive/audit-ux/BRIEF.md`](plans/archive/audit-ux/BRIEF.md) (l'entrant de design),
[`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) (audit d'infrastructure,
encore ouvert), [`SECURITY_AUDIT.md`](SECURITY_AUDIT.md) (audit de sécurité de septembre 2026).

---

## MOB — Application mobile

### Recette sur une application qui tourne

Rien de ce qui suit n'est couvert par les tests automatisés — soit parce que c'est du rendu, soit
parce que ça dépend d'un vrai fournisseur (mail, tuiles, GPS), soit parce que c'est un comportement
de première ouverture. `flutter analyze` est propre, les tests mobiles (`mobile/check.sh`) passent :
ce qui suit est ce qu'ils ne peuvent pas dire.

À faire une fois **en clair** et une fois **en sombre** (le mode sombre est *dérivé*, aucune maquette
ne le fournit : c'est là que les erreurs de contraste apparaissent), sur un compte membre de
`n-peloton` (1 999 membres, 2 585 parcours, ~665 sorties) *et* sur `gaby` (7 membres — l'échantillon
d'états vides).

**Passe partielle du 27 juillet 2026** : audit de code complet sur les douze écrans, plus un passage
en direct sur simulateur (thème sombre, compte `n-peloton` — un seul compte suffit, il est admin des
deux équipes `n-peloton` et `gaby`). Un défaut trouvé et corrigé (`MOB-21`, le `GROUP_FULL` ne
nommait pas le groupe). Restent à faire en conditions réelles, non couvrables par la lecture de
code : bascule de fuseau horaire sur l'appareil, ouverture d'un deeplink app tuée, mesure de fps
(parcours et liste de 200), rendu du text scaling ×1,3/×2,0, et la capture d'écran de preuve pour le
jeton ICS. Thème clair et compte `gaby` pas repassés en revue depuis.

- [ ] `MOB-1` **Accueil (11)** — « Ma prochaine sortie » s'affiche quand on est inscrit, disparaît
      sinon ; badge `INSCRIT` sur les cartes du fil ; barre supérieure rétractable, barre d'outils
      épinglée ; 5 squelettes au chargement, pas 2.
- [ ] `MOB-2` **Sortie (12)** — les six états du bouton d'inscription. En particulier : un groupe
      complet affiche `Complet` **désactivé** (il n'existe aucune liste d'attente, « complet » est un
      état terminal) ; un `GROUP_FULL` en réponse restaure l'état optimiste **et nomme le groupe** ;
      aucune erreur nue « Erreur ». Carte à un tracé par groupe, sélection au tap.
- [ ] `MOB-3` **Pastille « Organisateur »** — présente uniquement si `RideGroupDto.leader` est
      présent. **Le cas courant est l'absence** (les 665 sorties existantes de `n-peloton` n'ont pas
      de meneur) : vérifier que ça ne rend rien, et surtout pas le créateur de la sortie.
- [ ] `MOB-4` **Parcours (13)** — profil altimétrique colorisé par pente, réticule fluide au
      glissement (60 fps ; regarder au `debugRepaintRainbowEnabled` que les barres ne sont pas
      repeintes), section « Cols et montées ».
- [ ] `MOB-5` **Exploration de parcours (21)** — vue liste et vue carte. Le mobile rend désormais les
      vraies tuiles `.mvt`, comme le web (jeton signé, API 2.3.0) : vérifier qu'au-delà de quelques
      centaines de tracés la carte les montre **tous**, sans plafond ni pilule de troncature, et
      qu'aucune requête ne part au tap sur un tracé. À faire **en build release au moins une fois** :
      `featuresAtPoint` a un antécédent de `ClassNotFoundException` Android corrigé en maplibre
      0.3.5, que le mode debug ne montre pas. Vérifier aussi le renouvellement du jeton : laisser la
      carte ouverte au-delà de la durée de vie, les tuiles doivent continuer d'arriver et les
      marqueurs rester **au-dessus** de la masse. Bascule automatique en compact au-delà de 200
      résultats.
- [ ] `MOB-6` **Calendrier (22)** — un mois s'affiche ; les étapes de voyage y sont (le voyage en
      tant qu'objet non, c'est voulu) ; anneau « inscrit » sur l'événement ; un jour à la fois
      « aujourd'hui » et « inscrit » porte les deux marqueurs.
- [ ] `MOB-7` **Jeton ICS (22)** — copier l'URL d'abonnement, puis **capturer l'écran** : le jeton ne
      doit apparaître nulle part à l'image, alors que le presse-papiers contient l'URL réelle.
- [ ] `MOB-8` **Fuseaux horaires (22, 24, 25)** — régler l'appareil sur `Pacific/Auckland` puis
      `America/Los_Angeles`. Une étape du lundi 17 août 2026 à 08:00 ne doit pas glisser d'un jour :
      on cherche une **double conversion**, pas une localisation d'équipe (le contrat n'a aucun fuseau
      d'équipe ; il porte une préférence de fuseau *utilisateur* que le web applique et que le
      mobile ignore : le mobile suit l'appareil).
- [ ] `MOB-9` **Voyage et étape (24, 25)** — tracé et profil ; au-delà de 12 étapes le tracé est
      volontairement partiel.
- [ ] `MOB-10` **Publication (31)** — liens markdown : interne → route interne, externe →
      navigateur, non lançable → bandeau. **Aucun lien inerte.** Tableau markdown à 4 colonnes :
      défilement horizontal sans déborder la page. Pas de bloc auteur (le contrat ne l'expose pas —
      ne pas s'étonner de son absence).
- [ ] `MOB-11` **Annonces (32)** — prix : `1200` → `1 200,00 €` ; `25` + `WEEK` → « 25,00 € /
      semaine » ; `null` → « Prix à négocier ». **La carte rend un secteur, jamais une punaise** : la
      position est floutée à ~1 km et un marqueur ponctuel prétendrait une précision qui n'existe
      pas.
- [ ] `MOB-12` **Contact du vendeur (32)** — les quatre issues rendent quatre écrans distincts :
      204, `AD_CONTACT_OPTED_OUT`, `AD_CONTACT_RATE_LIMITED` (429, `Retry-After` exploité),
      `AD_CONTACT_DELIVERY_FAILED` (500). **Aucun succès affiché sur un 500.** Le bouton est absent
      sur sa propre annonce. 9 et 2 001 caractères refusés côté client, sans appel réseau.
- [ ] `MOB-13` **Trombinoscope (34)** — sur `n-peloton`, le pied annonce le total exact (1 999) à
      chaque page. C'est le point où le mobile chargeait 20 membres sur 1 999 **sans le dire**.
- [ ] `MOB-14` **Découverte d'équipes (34)** — la loupe et le CTA d'état vide mènent quelque part
      (c'étaient les deux `// TODO` de `lib/`) ; chip `joinable=true` ; adhésion optimiste avec
      bandeau d'échec nommant la cause.
- [ ] `MOB-15` **Profil (33)** — les quatre réglages s'appliquent **immédiatement, sans bouton**
      (unités, thème, langue, « Être contacté par les membres ») ; un échec revient à la valeur
      précédente. Ajouter une seconde clé d'accès **n'écrase plus les autres**. `logout-all` est
      câblé. La cloche et la section Notifications existent depuis la phase 3 de
      [`plans/archive/2026-09-18-notifications.md`](plans/archive/2026-09-18-notifications.md) — mais
      la section reste **non rendue** tant que le serveur ne déclare aucun canal configurable — le
      défaut en dev, plus le cas en prod depuis que le push y est actif : à recetter dans les deux
      états.
- [ ] `MOB-16` **Deeplinks à froid** — application tuée, ouvrir un lien de sortie, de parcours et
      d'annonce. Le bon onglet est surligné et la pile de retour est cohérente. (Le test
      `deep_link_hierarchy_test.dart` couvre la table ; il ne couvre pas l'ouverture réelle.)
- [ ] `MOB-17` **Text scaling ×1,3 puis ×2,0** — badges, lignes de col à 3 colonnes, en-têtes
      épinglés : aucun débordement.
- [ ] `MOB-18` **Pièces jointes** — sur chacun des huit écrans à `MediaDto` (sortie, publication,
      annonce, parcours, voyage, étape, page d'équipe, « à propos ») : le bloc apparaît **avec** un
      fichier et disparaît sans, et un contenu qui ne porte qu'une pièce jointe sans texte affiche
      quand même le bloc (l'« à propos » ne doit plus se déclarer vide).
- [ ] `MOB-19` **Une image jointe se regarde dans l'app** — le tap ouvre la visionneuse zoomable, pas
      le navigateur ; le sous-titre porte « 1920 × 1080 » quand `imageDimensions` est là ; le bouton
      rapporte **l'originale** et ouvre la feuille de partage. À vérifier sur un contenu **visible
      des seuls membres** : c'est le cas où l'ancien `openLink` tombait sur un 403, l'autorisation
      de `/api/download/…` étant celle du contenu porteur. Un fichier volumineux (>10 Mo) : le
      bandeau « Téléchargement en cours… » reste visible et l'échec réseau donne un bandeau rouge,
      jamais une feuille de partage vide.
- [ ] `MOB-20` **Performance** — liste de 200 items : rester au-dessus de 55 fps. Si le
      `BackdropFilter` des barres épinglées coûte trop cher, le repli prévu (non implémenté à ce
      jour — `blurToolbar` est une constante fixe à 12, aucune branche conditionnelle) serait **un
      seul jeton** à faire tomber à 0 (`PdlMotion.blurToolbar`, surface opaque), aucun écran à
      rouvrir.

### Couverture e2e Patrol — ce que les tests ne couvrent pas encore

`mobile/patrol_test/` couvre les P0 de l'audit de couverture e2e (`WEB-26`) transposés à l'app, et
les scénarios `MOB-25` à `MOB-36` sauf `MOB-26`, écrits le 29 septembre 2026 (voir
`mobile/patrol_test/README.md`, « Coverage »). Ce que chacun laisse de côté est dit dans son entrée
livrée. Hors périmètre, parce que l'app ne les a pas : écrire une publication (le seul écrit est un
commentaire), la file de modération (une entrée `CONTENT_REPORTED` ouvre `/…/admin/reports` dans le
navigateur), la connexion par code e-mailé et une préférence de fuseau.

- [ ] `MOB-26` **Clés d'accès** — une clé ajoutée depuis le profil (`passkeys_section.dart`) connecte
      depuis `login_page.dart` ; une clé supprimée ne connecte plus (`flow-account.e2e.ts`). Demande
      un authentificateur sur le simulateur ou l'émulateur.
      **Bloqué sur la stack e2e** (évalué le 29 septembre 2026) : le RP ID y est `localhost` en HTTP,
      alors qu'iOS n'accepte une clé que pour un domaine associé (`webcredentials:`, fichier
      `apple-app-site-association` servi en HTTPS de confiance) et que `Runner.entitlements` ne
      déclare que `www.pedalons.fr` ; la correspondance Face ID du simulateur ne se pilote pas depuis
      Patrol ; Android bute de même sur `assetlinks.json`. Il faudrait un nom d'hôte HTTPS pour la
      stack (certificat mkcert ajouté au simulateur), `PEDALONS_WEBAUTHN_RP_ID` et
      `--dart-define=WEBAUTHN_RP_ID` réglés dessus, une variante d'entitlements de debug
      (`?mode=developer`) et un pas « visage reconnu » lancé par l'hôte.
- [ ] `MOB-37` **Les tests Patrol ne tournent qu'en local** — aucun workflow de `.github/` ne les
      lance : `ci.yml` ne passe que les tests unitaires. Il faudrait un runner macOS (simulateur) ou un
      émulateur Android, plus la stack e2e (`scripts/e2e.sh up`) dans le job. Pendant web : `AUD-3`.

---

## WEB — Site web

La recette du web est automatisée par une suite Playwright depuis le 25 septembre 2026
(`WEB-13` à `WEB-22`, livrés, avec ce que l'automatisation ne voit pas) : voir
`frontend/e2e/README.md`.

- [ ] `WEB-27` **Le SSR de `/calendrier` croît en carré des sorties d'une semaine, et gèle le
      serveur Node pendant ce temps (S–M)** — `@mantine/schedule` 9.6.2 place chaque événement du
      mois en le comparant à tous ceux déjà posés dans sa semaine (`findAvailableRow`, avec un
      `dayjs()` par comparaison) : 1 000 sorties sur une semaine, 2,7 s de rendu ; 2 400, 10 s. Le
      rendu est synchrone, et c'est le même processus qui sert `/assets` : un chunk demandé pendant ce
      temps attend 7,6 s au lieu de 17 ms (mesuré le 29 septembre 2026 sur la pile e2e, où l'admin
      bootstrap avait accumulé 2 520 équipes). Sans effet à l'échelle d'un vrai compte (quelques
      dizaines de sorties par semaine), mais rien ne borne le cas. La suite e2e ne l'exerce plus
      (routes-render promeut un admin plateforme neuf).
      **En cours côté Mantine** (depuis le 30 septembre 2026) : une issue et une PR sont en
      préparation sur `mantinedev/mantine`. Elles remplacent le rebalayage de la semaine dans
      `findAvailableRow` par un index d'occupation de 7 jours, soit O(7) par événement au lieu de
      O(n), pour le même placement. La tâche se ferme quand une version corrigée de
      `@mantine/schedule` est publiée, que `frontend/` y passe et qu'une mesure confirme le gain ;
      noter alors ici les liens de l'issue et de la PR. **Piste écartée** : ne passer à `Schedule`
      que les événements de la grille visible, qui est déjà le cas (`getVisibleRange` borne la
      requête) et ne change rien quand tout tombe dans une même semaine. **Repli** si l'amont
      tarde : un seuil au rendu serveur (au-delà de N événements, grille vide au premier rendu et
      remplie après montage), ou ne pas rendre la grille côté serveur.

### Couverture e2e — ce que l'audit du 27 septembre laisse ouvert

L'audit ([archivé](plans/archive/2026-09-27-e2e-coverage-audit.md), `WEB-26`) est exécuté : P0, P1
et P2 écrits, 54 défauts relevés, tous corrigés ou tranchés — le dernier, `WEB-6`, le 29 septembre. Le canal e-mail des
notifications n'a pas de test e2e : c'est `NOTIF-4`.

- [ ] `WEB-7` **Idées de la liste « P2 (à planifier) » qui n'ont pas été retenues** parmi les
      21 tests P2 écrits — quelques-unes peuvent être couvertes au passage par une autre spec, à
      vérifier avant de les écrire :
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

---

## API — Contrat d'API et backend

### Les chantiers d'infrastructure d'API

Quatre chantiers ont été chiffrés au §4 du
[document d'API archivé](plans/archive/2026-07-26-api-v2-livraison-et-suites.md) (contrat, modèle,
déclenchement, risques). Seul le push a été livré depuis (`NOTIF-9`) ; les trois autres n'ont pas été
commencés. Résumé et ordre recommandé :

#### `API-21` Pagination par curseur — **à faire avant d'étendre le scroll infini** (L)

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

#### Cache, fraîcheur et images — quatre briques indépendantes

- [ ] `API-22` **`ETag` / `If-None-Match` (M)** — le verrou est que les champs « moi »
      (`registered`, `registeredGroupId`, `full`, `commentCount`) rendent les réponses dépendantes
      de l'appelant. Un `ETag` calculé sur le contenu métier seul serait faux et un cache partagé
      servirait à Alice la réponse de Bob. Recommandation actée : intégrer `userId` au calcul et
      servir `Cache-Control: private` — le gain visé est le cache **du client**, pas d'un CDN.
      Commencer par les détails, pas les listes.
- [ ] `API-23` **`updatedAt` en liste + `?updatedSince=` (M)** — c'est ce qui rend une synchro
      incrémentale possible. **À décider avant de coder** : `updatedSince` ne dit rien des
      suppressions. Soit des tombstones (`deleted_at` interrogeable), soit une resynchronisation
      périodique complète.
- [ ] `API-24` **URLs d'images signées (M)** — HMAC sur `(chemin, expiration)`. Piège : une URL qui
      expire pendant qu'une image est en cache donne une image cassée **sans erreur lisible** ;
      prévoir la renégociation client. Même brique que l'imgproxy non signé (`AUD-9`).
- [ ] `API-25` **`blurHash` (S)** — 30 octets par image, calculés une fois à l'upload. Le seul point
      à trancher : faire de `thumbnailUrl` un objet `{url, blurHash, width, height}` est un
      **MAJOR** (changement de type sur un champ livré en 1.3.0) ; un champ frère
      `thumbnailBlurHash` ne l'est pas.

Ensemble, elles débloquent le chargement progressif du parcours, de vrais placeholders colorés à la
place des squelettes, et le **hors-ligne — qui n'est dans aucune maquette et devrait l'être avant
d'être promis**.

#### `API-26` Carte multi-entités `GET /api/map/features` (M)

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

#### `API-27` Coût du lot de géométries — ce qui reste après 2.0.0

Le paramètre `simplify` et `…/elevation-profile` ont été supprimés en 2.0.0 (`API-40`).

`MAX_BULK_SLUGS = 50` est désormais le *seul* garde-fou du
lot, sur un endpoint `@PermitAll` sans rate limiting en lecture. Un lot réaliste de 50 parcours pèse
3,2 Mo ; les 50 plus lourds de la base pèsent 64 Mo. Ce cas suppose un appelant qui sait lesquels
sont les plus lourds et les nomme tous : c'est un sujet de **rate limiting**, pas de contrat. Le
levier si ça devient sensible est de descendre `MAX_BULK_SLUGS` vers ~15 (les appelants réels
plafonnent à 12, `kTripTrackStageCap`), pas de réintroduire un paramètre de finesse.

### Petites évolutions d'API qui suppriment chacune une dégradation nommée

Extrait du §5.2 du plan mobile. Aucune ne bloque un écran ; chacune retire un repli visible.
Beaucoup sont des ajouts d'un champ — le rapport valeur/effort y est bon. Les numéros reprennent
ceux du plan (`API-1`, l'URL de tuile authentifiable, est livré).

| ID | Manque | Écrans | Dégradation actuelle |
|---|---|---|---|
| `API-4` | `groups[]` ou un `registeredGroup` compact sur les lignes de liste | 11 | Un `getRide` supplémentaire pour la seule prochaine sortie |
| `API-8` | Voisins de publication (`prev`/`next`) *(absence à reconfirmer — recherche ciblée seulement, pas de grep exhaustif sur toutes les resources de publication)* | 31 | Navigation rendue seulement depuis un fil déjà chargé |
| `API-10` | `ClimbDto.name` | 13, 25 | « Montée N » |
| `API-11` | Commentaires d'étape | 25 | Section absente, renvoi vers le voyage |
| `API-12` | Participants paginés et cherchables côté serveur | 24, 34 | Liste complète embarquée, recherche client, pas de pied « N sur M » |
| `API-14` | `logoUrl` de service GPS (`GpsServiceConnectionDto`) — `SocialIdentityDto.externalUsername` n'a plus d'objet : la connexion Strava a été retirée (API `5.0.0`) | 33 | Nom du service et « Connecté le *date* », sans logo |
| `API-15` | `Team.timezone` ou dates zonées au contrat | 22, 24, 25 | Fuseau de l'appareil ; le web applique en plus la préférence `UserDto.timezone`, que le mobile ignore |
| `API-17` | `?format=polyline` sur la géométrie de parcours | — | La géométrie stockée est déjà allégée à l'import (`API-40` : 681 points et 65 Ko pour le parcours médian) ; ~÷4 sur le poids, au prix d'un décodeur Dart. **À rouvrir seulement sur une mesure réelle** |
| `API-18` | Voyage comme événement multi-jour au calendrier (`CalendarEventType`) | 22 | Les étapes y sont, le voyage en tant qu'objet non |
| `API-19` | `GET /api/search?q&types=&limit` unifié | — | Plus aucune recherche transverse : `GET /api/users/search` a été **supprimé** en `3.0.0` (`API-39`). La seule recherche de personnes est celle du trombinoscope d'une équipe |
| `API-20` | Pagination du calendrier | 22 | Fenêtre fixe −30 j / +180 j, non paginée |

Le meneur de groupe (`API-41`, livré en 1.5.0) et l'URL de tuile (`API-1`) sont dans
[`LEDGER_DONE.md`](LEDGER_DONE.md). Les **gabarits de sortie n'ont volontairement pas de meneur** —
décision produit : `RideTemplateGroupRequest` reste sans champ.

### Vie privée : ce que la politique doit encore décrire faute de mieux

- [ ] `API-47` **Les documents joints gardent leurs métadonnées** — PDF (auteur, JPEG embarqués
      avec leur EXIF), fichiers bureautiques (`docProps`), PSD (EXIF/IPTC) sont stockés tels quels ;
      la politique (§1) le dit. À trancher : les nettoyer (dépendance nouvelle pour le PDF), ou
      garder la phrase. Taille : M à L. exiftool **n'aide pas** (mesuré le 29 septembre 2026,
      `API-54`) : il ajoute une mise à jour incrémentale réversible, le dictionnaire Info reste dans
      les octets, et l'EXIF des JPEG embarqués est intact. Seule voie : réécriture complète (PDFBox)
      en faisant réencoder les JPEG embarqués comme les images (`API-43`).
- [ ] `API-50` **Le rédacteur GPX de gpx2web écrit un `creator` fixe et une heure epoch** — la
      bibliothèque (gpx 1.5.x) écrit `creator="https://www.mapstogpx.com/strava"` (trompeur, pas
      personnel) et `<time>1970-01-01T00:00:00Z</time>` sur chaque point depuis `API-44`. Ne pas
      émettre `<time>` quand l'instant est `EPOCH` donnerait des fichiers plus propres. Changement de
      bibliothèque, pas de Pédalons ; `GpxSanitizationBackfill.isDirty` accepte déjà l'absence de
      `<time>`. Taille : S.
- [ ] `API-56` **Retirer les deux formes dépréciées de `API-45`** — `DELETE
      /api/push-devices/{token}` (`unregisterPushDeviceByPath`) et `GET
      /api/export/download/{token}` (`downloadDataExportByPath`) restent servies, jetons dans le
      chemin donc dans le journal d'accès, pour les builds mobiles installés et les liens d'export
      déjà envoyés. À retirer (contrat **majeur**) quand les liens envoyés avant le déploiement de
      6.5.0 ont expiré (7 jours) **et** que le mobile a été republié. Taille : XS.
- [ ] `API-57` **La colonne `auth_tokens.pending_password_hash` n'est plus écrite** — depuis
      `SEC-24` (API 7.0.0), l'inscription ne prend plus de mot de passe : le lien le demande. La
      colonne est gardée pour le déploiement progressif (l'ancienne version l'écrit encore pendant
      la minute de recouvrement) et pour les liens émis avant, dont `activateAccount` ignore le
      hachage. À retirer par une migration Flyway une fois 7.0.0 déployée **et** les liens
      d'avant expirés (24 h), avec le champ d'`AuthToken` et la mention dans `AccountExport`.
      Taille : S.

---

## OPS — Exploitation, déploiement, recette du backend

### Recette du backend en conditions réelles

Ce que les tests ne prouvent pas, parce qu'ils ne passent ni par Flyway ni par un vrai relais SMTP.

- [ ] `OPS-1` **Démarrage réel du backend** — les tests utilisent `drop-and-create` et ne passent pas
      par Flyway : un test vert ne prouve **pas** que les migrations s'appliquent sur une base
      existante. Attendre `Migrating schema … to version 45` au moins une fois, puis contrôler que
      `ad_contacts` existe, que `users.contactable_by_members` est nullable et que
      `ride_groups.leader_id` est nullable avec une FK en `ON DELETE SET NULL` (surtout pas
      `CASCADE`) et son index partiel. Les commandes exactes sont au §5.1 du
      [document d'API](plans/archive/2026-07-26-api-v2-livraison-et-suites.md).
      **Ajouté en `3.0.0`** : `teams.enable_member_directory` en `NOT NULL DEFAULT FALSE`, et la
      table `team_invitations` avec son index **partiel** `uk_team_invitations_pending on
      (team_id, email) where status = 'PENDING'` — les tests construisent le schéma depuis les
      mappings JPA, qui ne savent pas exprimer un index partiel, donc c'est précisément le genre
      d'objet qu'un test vert ne prouve pas.
- [ ] `OPS-2` **Les tests backend sont à lancer par le propriétaire du dépôt**, jamais par Claude
      (interdiction du projet). Le découpage par item est au §5.2 du même document. Deux gardes à
      ne jamais désactiver pour faire passer un build : les classes `…QueryCountTest` (elles
      échouent si quelqu'un réintroduit une requête par ligne) et le test
      `groupLeader_isNotTheRideCreator` de `RideGroupLeaderTest` (il échoue si quelqu'un réintroduit
      un repli sur `createdBy`).
- [ ] `OPS-3` **Le relais de contact en production** — les gabarits `ad-contact.{fr,en}` sont rendus
      par le backend (`templates/mail/`) et partent par le relais SMTP de Scaleway TEM comme tout le
      reste ; l'envoi a été validé par un message réel du temps de Brevo. À revérifier après la
      bascule TEM par un envoi réel : le `Reply-To` doit porter l'adresse de l'auteur et le corps ne
      doit jamais l'imprimer.
- [ ] `OPS-4` **Les quatre gabarits d'invitation en production** — `team-invitation.{fr,en}` et
      `team-invitation-signup.{fr,en}` vivent dans `templates/mail/` et partent par le relais SMTP
      de Scaleway TEM. Params des quatre : `appName`, `inviterName`, `teamName`, `invitationUrl`,
      `expiresInDays`. **Reste à faire : un envoi réel** après la bascule TEM, comme pour
      `ad-contact` (`OPS-3`). Si le relais refuse le message, `POST …/invitations` répond **500
      `TEAM_INVITE_DELIVERY_FAILED`** — délibérément, plutôt qu'un `INTERNAL_ERROR` opaque — et la
      transaction est annulée : aucune invitation fantôme ne subsiste.
- [ ] `OPS-5` **`AdDto` ne porte aucun champ de contact** — le `grep` et le script Python du §5.3 du
      document d'API. Le jour où ils remontent quelque chose, le relais a été contourné et une
      adresse personnelle est publiée à toute une équipe, irrévocablement.

### Exploitation

- [ ] `OPS-14` **Recette des deux rattrapages de métadonnées après le déploiement** —
      `AssetMetadataBackfillScheduler` (toutes les 5 min) vide `assets.metadata_pending` (V47) ;
      `GpxSanitizationBackfill` (4 h 15) écrit `maintenance/api-49-gpx-sanitized` après une passe
      sans échec. Vérifier `SELECT count(*) FROM assets WHERE metadata_pending` à zéro, le journal
      « GPX sanitization backfill: N file sets checked, M rewritten, 0 failed » et le marqueur dans
      le bucket ; les pièces jointes GPX/FIT illisibles (`API-49`, `API-55`) sont laissées telles
      quelles avec un WARN « Track attachment … left as is » : décider de les supprimer. Les TIFF/HEIF déjà stockés sont convertis en JPEG par le rattrapage ; restent les
      JPEG 2000 et les images qu'imgproxy ne sait pas lire (journal WARN « cannot be re-encoded »
      ou « imgproxy cannot decode », résultat `UNREADABLE`) : décider de les supprimer ou de les
      convertir à la main. Redémarrer varnish pour qu'il charge le `pass` des réencodages
      (`services/varnish/varnish.vcl`). Tant que le compte n'est pas à zéro, la phrase du §1 sur les photos antérieures n'est vraie
      qu'en devenir (`API-43`, `API-44`).
- [ ] `OPS-19` **Passer PostgreSQL 17 → 18** — dependabot l'a proposé (PR #338, fermée le
      29 septembre 2026 avec `ignore this major version`) en ne changeant que l'image de
      `docker-compose.yml`, ce qui empêcherait la prod de redémarrer : les fichiers d'un cluster 17
      ne s'ouvrent pas en 18, et l'image 18 attend ses données sous `/var/lib/postgresql/18/docker`,
      le volume monté sur `/var/lib/postgresql` (le montage actuel sur `…/data` la fait refuser de
      démarrer). À faire ensemble : dump avec `scripts/backup.sh`, nouveau volume au nouveau point
      de montage, restauration avec `scripts/restore.sh` ; l'image des devservices
      (`quarkus.datasource.devservices.image-name`, `application.properties`) pour que les tests
      tournent sur la même version ; la doc qui annonce « PostgreSQL 17 » (CLAUDE.md, README.md,
      backend/README.md). Rien ne presse tant que la 17 est maintenue. Taille : S.
- [ ] `OPS-21` **Ce que le monitoring ne voit pas encore** — PostgreSQL (connexions, taille, bloat,
      requêtes lentes : un `postgres_exporter` par environnement, sur `pedalons-shared`), le
      frontend SSR (ni métriques Node ni temps de rendu : seul Caddy le voit, par hôte), et les
      erreurs côté client web et mobile (un GlitchTip, compatible Sentry, pèserait une base et un
      Redis de plus : à ne faire que si le besoin se confirme). Taille : M.
- [ ] `OPS-24` **Masquer `next` et `Referer` dans le journal de Caddy** — le snippet
      `pedalons_access_log` d'[`OPERATIONS.md`](OPERATIONS.md#access-logs) a gagné
      `replace next REDACTED` et `request>headers>Referer delete` (`API-45`) : un lien d'export
      ouvert sans session renvoie vers `/login?next=/api/export/download?token=…`, et les
      ressources de cette page repartent avec l'URL entière en `Referer`. Reste à l'appliquer au
      Caddyfile de l'hôte, puis `caddy validate` et `chown caddy:caddy` des fichiers de journal
      (le piège décrit sous le snippet) ; vérifier par un `curl …/login?next=/x?token=abc -H
      'Referer: https://…?token=abc'` que la ligne ne porte ni l'un ni l'autre. Taille : XS.
- [ ] `OPS-23` **Les alertes partent par le relais de l'application** — `ALERT_SMTP_*` (`.env` de
      `~/shared`) pointe sur Scaleway TEM, comme `QUARKUS_MAILER_*` : une panne de TEM, ou un
      compte suspendu, tairait les alertes qui devraient la signaler. Le `Watchdog`, qui passe par
      Healthchecks, n'en dépend pas, mais aucune autre alerte n'arriverait. Prendre un relais
      indépendant (le SMTP d'une messagerie personnelle, avec un mot de passe d'application), le
      mettre dans le `.env`, `deploy.sh --monitoring`, puis un `amtool alert add` pour voir arriver
      le courriel ([`OPERATIONS.md`](OPERATIONS.md#alerts)). Taille : XS.
- [ ] `OPS-8` **Exercice de restauration** (audit de février, I20) — la procédure est écrite
      ([`OPERATIONS.md`](OPERATIONS.md), « Restore drill from another machine ») mais rien ne dit
      qu'elle a été menée de bout en bout sur une autre machine.
- [ ] `OPS-9` **Copie à froid des données Valhalla** — `~/shared/data/valhalla` (~17 Go, des heures
      à reconstruire) est hors des sauvegardes nocturnes : la copier **une fois** vers l'hôte de
      sauvegarde, puis à chaque changement d'extrait OSM. Rien ne dit que c'est fait, et les données
      tileserver, nommées comme « à reconstruire à la main », ne sont couvertes par aucune
      procédure. Source : [`OPERATIONS.md`](OPERATIONS.md#cold-backup-of-the-shared-stack).

---

## NOTIF — Notifications

Les phases 1 à 5 de [`plans/archive/2026-09-18-notifications.md`](plans/archive/2026-09-18-notifications.md)
sont en production, le Web Push aussi depuis le 29 septembre 2026 ; ce qui en est livré, et les
pièges à ne pas rejouer, sont sous `NOTIF-9` (le ledger du chantier y a été rapatrié le
29 septembre 2026). Restent deux points qui ne sont pas du code, et deux qui le sont :

- [ ] `NOTIF-1` **Recette du webhook d'équipe** contre un vrai Slack, un vrai Discord et un vrai
      Mattermost (bouton « Envoyer un test ») — vérifier au passage qu'un `@channel` dans un nom de
      sortie ne notifie personne sur Mattermost.
- [ ] `NOTIF-2` **Décision produit sur l'e-mail** : `PEDALONS_NOTIFICATIONS_EMAIL_ENABLED` reste à
      `false` en production (décision du 21 septembre 2026, « pas pour le moment »). L'activer,
      c'est écrire aux équipes entières, en connaissant les défauts (annulations, voyages publiés).
- [ ] `NOTIF-3` **Webhook d'équipe : le *DNS rebinding* n'est pas couvert** — `WebhookHttpClient`
      refuse toute résolution vers une adresse interne, mais `HttpClient` résout de nouveau à la
      connexion : un nom qui change de réponse entre les deux passe. Le fermer voudrait dire
      épingler l'adresse vérifiée pour la connexion (voir la javadoc de `WebhookHttpClient`).
- [ ] `NOTIF-4` **Le canal e-mail des notifications n'a pas de test e2e** : il est coupé en
      production comme sur la stack e2e (`NOTIF-2`). Laissé ouvert par l'audit de couverture e2e
      (`WEB-26`).

---

## MOD — Modération et signalement

Livrée le 24 septembre 2026 (`MOD-6`). Les quatre défauts mineurs notés à la livraison sont corrigés
(`MOD-1` à `MOD-4`).

---

## ISSUE — Signaler un problème → issues GitHub

En service en production depuis le 29 septembre 2026 (`ISSUE-6`, API 4.6.0). Suites possibles, non
faites :

- [ ] `ISSUE-1` **Piles web illisibles** : le bundle est minifié et l'empreinte ne garde que le nom
      du chunk. Pour symboliser, construire avec `VITE_BUILD_SOURCEMAP=true` et **ne pas** servir
      les `.map` (les archiver avec l'image), puis automatiser la symbolisation côté serveur.
- [ ] `ISSUE-2` **Côté mobile**, si le build release passe à `--obfuscate`, conserver les
      `--split-debug-info` par build, sans quoi les piles Dart deviennent inexploitables et
      l'empreinte change à chaque version.
- [ ] `ISSUE-3` **Plantages natifs** (Kotlin/Swift) : non couverts, seuls les handlers Dart
      remontent.
- [ ] `ISSUE-4` **Boucler avec le membre** : le prévenir quand l'issue de son signalement est fermée
      (webhook GitHub → notification), joindre une capture d'écran, corréler avec les logs serveur
      par request-id.
- [ ] `ISSUE-5` **Visiteurs non connectés** : ni signalement ni remontée automatique (l'endpoint
      exige une session). Une erreur sur la page de connexion n'arrive donc que par e-mail.

---

## MIG — Migration biketeam

En service en staging ; la mise en production attend biketeam
([plan](plans/2026-09-22-biketeam-live-migration.md) §10). Source :
[`MIGRATE_BIKETEAM.md`](MIGRATE_BIKETEAM.md).

### Mise en production

- [ ] `MIG-1` **Déroulé** : poser les secrets des deux côtés en production, puis un essai sur une
      petite équipe (`gaby`) contre staging, contre la production, et enfin le passage réel. Pour
      chaque équipe qui a un domaine personnalisé chez biketeam, vérifier que la redirection
      fonctionne : elle se règle au proxy, au cas par cas (plan §13, décision 11).
- [ ] `MIG-2` **Redirections 302 → 301** : une fois les bascules stabilisées, poser
      `PEDALONS_REDIRECT_STATUS=301` côté biketeam. Pas avant : un 301 est mis en cache par les
      navigateurs et ne se rattrape pas.
- [ ] `MIG-3` **Liens internes non réécrits** : les liens vers `prendslaroue.fr` dans les pages
      d'équipe (FAQ, descriptions) restent pointés vers biketeam et redirigent tant qu'il tourne. À
      corriger — à la main, par l'équipe, ou par une réécriture depuis la table d'URL — **avant**
      l'arrêt de biketeam.
- [ ] `MIG-6` **Données exportées mais non importées** : tags de parcours, ville et pays de l'équipe
      arrivent dans l'instantané (`BiketeamSnapshot`) sans être utilisés (plan §13, décision 14). À
      décider.
- [ ] `MIG-7` **Avant l'arrêt de biketeam** : outre les liens internes (`MIG-3`), les logos
      `/{t}/image` ne sont pas redirigés et restent servis par biketeam (plan, écarts biketeam,
      point 16).
- [ ] `MIG-8` **Après l'arrêt de biketeam** : retirer la migration en direct (endpoints internes,
      worker, les cinq variables, la page `biketeamMigration`, les codes `BIKETEAM_*`, peut-être
      `biketeam_migrations`), **sans** retirer `biketeam_migration_map`, dont dépendent encore
      `UserTeamRepository.findMigratedTeamsAdministeredAlone` (décision 22) et
      `BiketeamTargetResolver`. Archiver alors le plan, qui reste jusque-là le contrat en vigueur.

### Autour de la migration

- [ ] `MIG-9` **Purge physique des équipes à la corbeille** (reset de migration biketeam) — chantier
      séparé. Un `reset` de la migration en direct met l'équipe Pédalons à la corbeille et libère
      son slug (`TeamService.deleteTeam`), rien de plus : lignes `team_entities`, assets et fichiers
      S3 restent. Chaque reset en laisse un exemplaire de plus. **Quand** : si le volume le
      justifie, ou avec une politique de rétention générale de la corbeille. Décision du 2026-09-22
      ([plan](plans/2026-09-22-biketeam-live-migration.md) §13, décision 10).

---

## BRAND — Charte

Rien d'ouvert : le code couleur métier a une source unique depuis `BRAND-2`.

---

## SEC — Audit de sécurité

[`SECURITY_AUDIT.md`](SECURITY_AUDIT.md) fait foi pour les statuts et reste expurgé : cette liste ne
fait que les suivre ici, sans détail. Quand un constat est corrigé, mettre à jour **les deux**
fichiers (statut et commit dans l'audit, ligne déplacée dans `LEDGER_DONE.md` sous son
identifiant). Relevé le 29 septembre 2026 : aucun commit de code n'a touché ces points depuis la
mise à jour de l'audit. La colonne « Audit » garde l'identifiant du constat dans l'audit.

| ID | Priorité (audit) | Audit | Sévérité | Constat |
|---|---|---|---|---|
| `SEC-5` | 2 | V1 | **Critique si confirmé** | Clé JWT présente dans l'historique public : vérifier que prod et staging n'en sont pas des copies |
| `SEC-6` | 4 | M3 | Moyenne | Traitement GPX non borné en mémoire |
| `SEC-8` | — | M2 | Moyenne | Flou d'~1 km des annonces affinable par requêtes répétées (contredit la décision `API-31`) |
| `SEC-11` | — | M7 à M10 | Moyenne | Refresh token non renouvelé ; résolution d'identité sans filtre de domaine ; jeton d'appareil long et non révocable ; pas de limitation de débit HTTP globale — voir `API-27` (audit de février, S3 à S7) |
| `SEC-12` | — | L3, L10 | Faible | Voir la table des constats faibles de l'audit ; L1 et L5 à L9 sont livrés sous `SEC-20`, L12 et L13 sous `SEC-22`, L14 sous `SEC-23`, L4 sous `SEC-24` |
| `SEC-13` | — | L11 | Faible | Durcissement des workflows GitHub Actions — partiel, `ci.yml` seulement |
| `SEC-14` | — | Info | — | Images externes dans le markdown ; le parseur XML et le paramètre non encodé sont livrés sous `SEC-21` |
| `SEC-16` | — | V3–V8 | À valider | Configuration hors dépôt : proxy de l'hôte, hôte de sauvegarde, SMTP, imgproxy |

---

## AUD — Audit d'infrastructure de février

[`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) fait foi pour le détail et
les statuts ; cette table les suit, revérifiés dans le code le 29 septembre 2026. Quand une ligne
est corrigée : ✅ dans l'audit, ligne déplacée dans `LEDGER_DONE.md` sous son identifiant. La
colonne « Audit » garde l'identifiant de l'audit (section « Problèmes » ; `P…` = plan d'action).
Deux gestes d'exploitation de l'audit sont sous `OPS` : I13 (`OPS-7`) et I20 (`OPS-8`).

| ID | Thème | Audit | Gravité | Constat |
|---|---|---|---|---|
| `AUD-1` | CI/CD | I3 | Critique | Aucun pipeline de déploiement (`ci.yml`, `codeql.yml`, `karoo-release.yml` seulement) |
| `AUD-6` | Docker | I10 | Important | `forwardedHeaders.insecure=true` sur Traefik (atténué par les règles `DOCKER-USER` qui ne laissent que Caddy le joindre : sous Swarm il écoute sur toutes les interfaces, voir [`OPERATIONS.md`](OPERATIONS.md#only-caddy-may-reach-traefik)) — voir V3, `SEC-16` |
| `AUD-30` | Docker | I14 | Mineur | Aucune limite **CPU** dans les compose (la mémoire est bornée : `AUD-7`). À poser une fois les charges mesurées : une limite trop basse sur le backend rallonge le démarrage (Flyway, Magika) au-delà du `start_period`, et le JVM dimensionne ses threads de GC sur elle. valhalla n'a pas non plus de limite mémoire, à dessein (son rebuild) |
| `AUD-8` | Docker | I15 | Important | VCL Varnish réduite à un `backend default` : ni purge, ni grace, ni ban |
| `AUD-9` | Docker | I17 | Important | imgproxy sans signature d'URL (ni `IMGPROXY_KEY` ni `IMGPROXY_SALT`) — même brique que les URLs signées (`API-24`) |
| `AUD-10` | Docker | I19 | Important | Aucune procédure de rotation des secrets dans `OPERATIONS.md` |
| `AUD-15` | Backend | B9, B11 | Important | `FetchType.EAGER` sur neuf `@ManyToOne` (`Ride`, `RideGroup`, `Trip`, `TripStage`, `UserTeam`) |
| `AUD-16` | Backend | B12 | Important | Device flow sans test backend (couvert par `frontend/e2e/flow-device.e2e.ts`) |
| `AUD-20` | Mobile | M11 | Important | Lints Flutter par défaut seulement (`analysis_options.yaml`) |
| `AUD-21` | Mobile | M13 | Mineur | Pas de hors-ligne — voir `API-22` à `API-25` |
| `AUD-22` | Karoo | K1 | Critique | `MainActivity.kt` monolithique (1 625 lignes) |
| `AUD-23` | Karoo | K2, K3 | Critique | Aucun test ; aucun `ViewModel` alors que la dépendance est déclarée |
| `AUD-24` | Karoo | K7, K9, K10 | Important | Refresh du jeton en trois endroits ; pas de `slow_down` (RFC 8628 — le backend n'en émet aucun à ce jour, voir `AUD-27`) ; routes non paginées. K13 est livré sous `AUD-32` |
| `AUD-25` | Garmin | G1 | Critique | `BASE_URL` de production en dur (`ApiClient.mc`) : bloque le multi-tenant |
| `AUD-26` | Garmin | G4, G5, G7, G10 | Important | `loadResource()` dans `onUpdate()` ; AM/PM en dur ; état comparé à une chaîne localisée ; `_tokenCallback` partagé entre refresh et polling |
| `AUD-27` | Garmin | G8, G9 | Important | Pas de `slow_down` (le backend n'en émet aucun à ce jour — relevé le 30 septembre 2026 : `grep slow_down backend/` ne trouve rien ; `SEC-4` a limité `/complete` et `/verify`, pas `/token`, dont le `device_code` de 256 bits n'a rien à deviner : le point ne mordra que si `/token` est un jour freiné) ; offsets fixes dans les layouts. G6 est livré sous `AUD-31` |
| `AUD-29` | Appareils | §9.2 | — | Reprise d'un flow d'autorisation interrompu et résilience réseau pendant le polling, à revérifier |

Suivis ailleurs : les lignes de sécurité S2 à S12 sont versées dans `SECURITY_AUDIT.md`, donc sous
`SEC` (H5 livré sous `SEC-4`, M7 à M10 = `SEC-11`, L12 et L13 livrés sous `SEC-22`, L14 sous `SEC-23` ; S8 et S12 y sont rangées comme
conformes). K12 (= S13, jetons Karoo en clair) est L10, dans `SEC-12` ; P2-44 (en-têtes CSP/HSTS) est
V4, dans `SEC-16` ; F12 (sitemap) est livré sous `WEB-31`.

---

## LEGAL — Politique de confidentialité

Le plan des points ouverts est [archivé](plans/archive/2026-09-29-privacy-policy-open-points.md).
Toute modification de
`privacy/privacy-policy.{fr,en}.md` se fait **en parité FR/EN**, et le texte étant un asset de
l'app mobile, un changement n'y apparaît qu'avec la build suivante.

Aucun point ouvert : `LEGAL-13`, le dernier, a été tranché le 29 septembre 2026.

---

## Délibérément dehors

À relire avant de rouvrir l'un de ces points : chacun a été écarté avec un motif, et plusieurs
sont des invariants que le code garde. Chacun garde l'identifiant de son domaine : rouvert, il
redevient une entrée de sa section sous le même identifiant.

| ID | Sujet | Décision | Ce que ça implique |
|---|---|---|---|
| `API-29` | **Liste d'attente (`waitlisted`)** | N'existe pas en base ; ni colonne, ni statut, ni rang sur `RideParticipation` | « Complet » est un **état terminal**. Ne pas câbler un `waitlisted: false` en dur : un champ toujours faux rend la vraie fonctionnalité indétectable en revue |
| `API-30` | **Repli sur `createdBy` pour le meneur** | **Interdit partout** — base, DTO, client | `createdBy` vaut le créateur de la **sortie**, donc le même nom sur tous ses groupes : un repli serait faux presque partout, et faux de la façon qui ne se signale pas. C'est le défaut que `leader_id` corrige. Gardé par `groupLeader_isNotTheRideCreator` |
| `API-31` | **Position exacte d'une annonce** | Floutée à ~1 km, **et la sonde de proximité quantifiée sur la même grille** | Flouter la sortie ne suffit pas : répéter « cette annonce est-elle à moins de R de C ? » en déplaçant C multilatère la position réelle. D'où le rayon arrondi au multiple de cellule (3 km servis comme 3,33 km) : l'interface annonce un **ordre de grandeur**, pas une valeur exacte. Et **jamais de punaise**. **L'audit de sécurité garde pourtant M2 ouvert** (le flou peut encore être affiné par des requêtes répétées) : la quantification ne suffit pas, voir `SEC-8` |
| `API-32` | **Champ de contact libre sur une annonce** | Écarté au profit du relais e-mail | C'était la solution la moins chère, et elle publie une donnée personnelle **irrévocablement** à toute l'équipe (jusqu'à 1 999 personnes) : ce qui a été lu ne se dépublie pas. Retirer le champ plus tard ne répare rien |
| `API-33` | **`GET /api/rides` et listes mono-type** | Non créées ; `/api/publications?type=RIDE` est la surface canonique | Deux surfaces = deux jeux de filtres à garder cohérents. `RideListResponse` / `TripListResponse` existent encore comme records retournés par **aucun endpoint** — les supprimer serait un MAJOR gratuit |
| `API-34` | **`acceptTerms` obligatoire à l'inscription (contrat `4.1.0`)** | Laissé en mineure | Les builds mobiles qui n'envoient pas le champ reçoivent un 400 `VALIDATION` à l'inscription. La rupture est acceptée sans passer en `5.0.0` |
| `API-54` | **exiftool pour retirer les métadonnées des images** | Écarté le 29 septembre 2026, après mesure sur un corpus synthétique (métadonnées marquées, pixels comparés) | exiftool (micro-service ou WASM) retire ce qu'il connaît au lieu de ne garder que ce qui est autorisé : il a laissé passer un chunk PNG privé et les octets après le trailer GIF, et refusé un WebP valide. Il ne nettoie pas les PDF, il a des CVE répétées (dont CVE-2026-7580, qui touche la 13.50) et il ajoute un conteneur. En WASM (zeroperl sur Chicory), sa sortie est identique mais il prend 17 à 19 s par image. imgproxy, écarté le même jour parce qu'il n'a pas de mode sans perte, a finalement été retenu : la perte d'un réencodage a été acceptée pour un code plus simple, qui ne laisse rien passer par construction et lit aussi HEIC, AVIF, TIFF et JPEG XL (`API-43`) |
| `API-52` | **Durcir `ImageMetadataStripper`** | Sans objet depuis le 29 septembre 2026 | Le nettoyeur maison sans perte a été supprimé : le stockage fait réencoder chaque image par imgproxy (`API-43`), qui n'écrit que les pixels. Ne pas le réintroduire pour gagner la qualité perdue : c'est lui dont les branches gardaient par défaut ce qu'elles ne connaissaient pas |
| `WEB-8` | **Scroll infini côté web** | Non porté | Incompatible avec la règle structurante du frontend (filtres et pagination dans la query string, donc toute vue partageable). `usePaginatedQuery` précharge déjà la page suivante **et** la précédente |
| `WEB-9` | **Gabarits tactiles portés au web** | Non portés | Feuilles à crans, barre d'onglets basse, app bar interpolée, chips en remplacement des `Select` : ils résolvent une contrainte que le desktop n'a pas, et produiraient des composants hors Mantine |
| `WEB-10` | **Minimum de 44 px sur les boutons web** | Règle **tactile** uniquement | Le web descend à 36 px au-dessus de 768 px. Ne pas prendre `pedalons.css` pour une spécification web |
| `WEB-11` | **Jetons `--pdl-*` au web** | Non introduits | Le site a déjà la charte en thème Mantine ; une seconde couche de variables créerait deux sources de vérité |
| `WEB-12` | **Mode sombre dérivé au web** | Sans objet | Le tableau de parité est un livrable **pour Flutter**, parce qu'aucune maquette ne fournit le sombre. Mantine l'a déjà |
| `MOB-22` | **Jeu d'icônes Tabler côté mobile** | Material outline conservé | L'écart ne porte que sur la graisse du trait des icônes de badge de 11 px. `PdlIcons` devrait être le **seul** fichier à nommer `Icons.*` (c'est tenu dans `lib/core/pdl`, pas encore ailleurs : une vingtaine de fichiers le font encore) : une fois ce ménage fait, basculer ne touchera qu'un fichier |
| `MOB-23` | **Écran de profil public d'un membre** | Aucune maquette ne va au-delà de la liste | Les lignes du trombinoscope ne sont pas cliquables. Ne pas inventer l'écran |
| `MOB-24` | **Édition et création de contenu au mobile** | Hors brief : la v2 est une version de consultation et de participation | Le sélecteur de meneur dans l'éditeur de groupes existe **côté web** (livré hors plan) ; l'équivalent mobile n'est pas ouvert |
| `OPS-18` | **Rotation des secrets exposés par les sauvegardes en clair** (`ENCRYPTION_KEY`, clé JWT, compte de service FCM) | Non (décidé le 29 septembre 2026, avec `OPS-13`) | Les 30 copies antérieures à `OPS-13` les contenaient en clair sur l'hôte de sauvegarde. Rotation jugée disproportionnée : ces copies sont supprimées et le SSD trimé. Reste un résidu physique possible sur le SSD de l'hôte ; à rouvrir si le matériel est perdu ou volé |
| `NOTIF-5` | **Badge iOS du push** | Écarté, côté serveur comme côté app | Serveur : il faudrait recompter les non-lues à l'envoi, et `NotificationMessage` ne porte ni le domaine ni ce compteur. App : `flutter_local_notifications` ne pose un badge qu'en affichant une notification, et en arrière-plan c'est le système qui affiche celle de FCM — une dépendance de plus pour un compteur que la cloche montre déjà. D'où l'absence de `content-available` (voir `NOTIF-9`) |
| `NOTIF-6` | **Isolat de fond du push (`onBackgroundMessage`)** | Non écrit | Le serveur envoie `notification` **et** `data` : le système affiche la bannière sans l'app, un isolat n'aurait rien à faire de plus |
| `NOTIF-7` | **Temps réel des notifications (SSE)** | Non fait : sondage de `unread-count` | Un flux SSE « si le besoin se confirme » ([plan](plans/archive/2026-09-18-notifications.md) §9) ; la cloche sonde au plus une fois par minute |
| `NOTIF-8` | **Tests Vitest de la cloche et de la page Notifications** | Écartés le 29 septembre 2026 | La recette navigateur les a validées ; le mobile a son test de widget (`notifications_page_test.dart`) |
| `MOD-5` | **Contenu masqué d'un compte effacé** | Reste masqué | L'effacement supprime les signalements visant le membre, mais ne touche pas `moderationHiddenAt` sur ses sorties, parcours, posts et voyages. Ce contenu, masqué par 3 signalements, n'a plus d'entrée dans la file et reste invisible pour les membres. C'est voulu : le démasquer republierait un contenu signalé 3 fois |
| `LEGAL-14` | **Notification des changements de politique dans l'app** | Non : l'e-mail seul, comme le dit le §11 (décidé le 29 septembre 2026, scindé de `LEGAL-13`) | Aucun type de notification « politique mise à jour ». Un changement important se signale par e-mail à chaque membre ; ne pas ajouter de promesse au §11 sans créer le type d'abord |
| `LEGAL-16` | **Journal des accès des admins plateforme** | Non (décidé le 29 septembre 2026, avec `LEGAL-13`) | Il n'y a qu'un admin plateforme, le responsable du traitement lui-même : un journal ne surveillerait que lui. Le §4 dit qui a accès et pourquoi, sans promettre de traçabilité. À rouvrir dès qu'un second compte reçoit `PLATFORM_ADMIN` : écrire le journal (intercepteur sur `@Admin`) **avant** d'en parler au §4 |
| `LEGAL-17` | **Responsable du traitement par domaine** | Non : un seul responsable pour tous les sites (décidé le 29 septembre 2026, avec `LEGAL-13`) | Le §12 le dit, et que les clubs ne sont pas responsables du traitement. Un club qui voudrait l'être demanderait des champs sur `Domain`, une page légale générée par domaine et un accord de sous-traitance : à rouvrir si un club le demande, pas avant |
| `BRAND-3` | **Test de concordance des tables de couleurs web et mobile** | Écarté au profit du générateur (`BRAND-2`) | Un test qui parse les deux fichiers et vérifie qu'ils concordent détecte sans unifier, et repose sur des expressions régulières sur du TypeScript et du Dart |

---

## Les autres sources

Ce fichier ne couvre que les suites de la v2 et des chantiers qui l'ont suivie. D'autres sources
restent ouvertes :

- [`BACKLOG.md`](BACKLOG.md) — la roadmap produit (P0 → Icebox). Y figurent notamment le statut
  « Terminée » sur les sorties et voyages (livré sous `API-16`) et le système de notifications (qui
  recoupe `NOTIF`).
- [`plans/2026-02-14-project-audit.md`](plans/2026-02-14-project-audit.md) — audit d'infrastructure,
  CI/CD et qualité des modules, statuts rafraîchis le 29 septembre 2026. Ses lignes ouvertes sont
  suivies sous `AUD`.
- [`SECURITY_AUDIT.md`](SECURITY_AUDIT.md) — audit de sécurité de septembre 2026 ; il fait foi pour
  les vulnérabilités, l'audit de février pour l'infrastructure. Suivi sous `SEC`.
- [`plans/2026-07-25-privacy-improvement-opportunities.md`](plans/2026-07-25-privacy-improvement-opportunities.md) —
  les options d'amélioration de la vie privée et leur justification ; ce qui en reste ouvert, le
  chiffrement des jetons Karoo, est suivi sous `SEC-12`. L'audit de juillet et la mise à jour de
  septembre qui ont réécrit la politique sont archivés.
