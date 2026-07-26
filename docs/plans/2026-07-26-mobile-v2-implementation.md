# Plan d'implémentation — Pédalons mobile v2

Ce document est le plan unique d'implémentation de la version 2 de l'application Flutter :
il fusionne et arbitre trois plans partiels (fondations, écrans hero, écrans core) rédigés en
parallèle, tranche leurs contradictions, dédoublonne la bibliothèque de composants et ordonne
le tout en lots livrables. Il est autoportant : le détail visuel se lit dans
[`docs/audit-ux/BRIEF.md`](../audit-ux/BRIEF.md), dans
[`docs/audit-ux/analyse/brand.md`](../audit-ux/analyse/brand.md) et dans les planches de
maquettes v2 (`00 Fondations`, écrans 11 à 34), mais aucune décision n'y est déléguée.

Les planches vivent dans le **projet Claude Design**
[« Pédalons — Mobile v2 »](https://claude.ai/design/p/587c89e2-46d9-4b7b-bad7-2485a1235632)
(`587c89e2-46d9-4b7b-bad7-2485a1235632`), consultable avec les outils
`mcp__claude-design__*` (`list_files`, `read_file`, `render_preview`). Deux réserves : le
projet est en partage restreint, un compte non invité n'y accède pas ; et ses
`contexte/*.md` sont des **copies** de `BRIEF.md`, `brand.md`, `mobile-screens.md` et
`web-pages.md` déjà divergentes — le dépôt fait foi. La feuille de style des planches, elle,
est versionnée ici : [`docs/audit-ux/pedalons.css`](../audit-ux/pedalons.css).

Le contrat d'API fait foi dans [`contracts/openapi.yaml`](../../contracts/openapi.yaml) (1.5.0) et
les conventions de code dans [`mobile/CLAUDE.md`](../../mobile/CLAUDE.md) et
[`mobile/rules.md`](../../mobile/rules.md).

## Suivi d'avancement

La colonne **État** de chaque tableau de tâches est la seule source de vérité sur ce qui est fait :
**☐** à faire · **▶** en cours · **☑** terminé.

Elle existe parce que ce plan est conçu pour être exécuté sur plusieurs sessions, chacune repartant
d'un contexte vierge. Une session ne sait rien de la précédente : sans marqueur, elle reconstitue
l'état à partir des commits, ce qui marche mal dès qu'une tâche est à moitié faite, et refait ou
saute du travail. Déduire l'avancement du code est la façon la plus fiable de se tromper.

**Cocher dans le même commit que le code.** Un état mis à jour plus tard est un état faux entre les
deux, et c'est précisément l'intervalle où la session suivante démarre. Une tâche n'est ☑ que
lorsque son critère de fin, en dernière colonne, est vérifié — pas quand le code est écrit.

Une session commence donc par : lire ce sommaire, prendre le premier lot dont les tâches ne sont pas
toutes ☑, et vérifier que ses dépendances le sont.

## Sommaire

- [§1 Fondations](#1-fondations) — conventions et arbitrages, thème et jetons, bibliothèque
  `core/pdl`, briques techniques, navigation, dette
- [§2 Les écrans, lot par lot](#2-les-écrans-lot-par-lot) — lot 2 hero (12, 13, 11), lot 3
  exploration (21, 22), lot 4 voyages (24, 25), lot 5 contenus et personnes (31, 32, 34),
  lot 6 équipe et profil (23, 33)
- [§3 Séquencement et chemin critique](#3-séquencement-et-chemin-critique)
- [§4 Risques](#4-risques)
- [§5 Hors périmètre](#5-hors-périmètre)

---

# 1. Fondations

## 1.0 Conventions, faits acquis, arbitrages tranchés

### 1.0.1 Lecture des tâches

Chaque tâche porte un identifiant, un intitulé à l'infinitif, ses fichiers réels
(`[C]` créé, `[M]` modifié, `[S]` supprimé), ses dépendances, une taille et un critère de fin
vérifiable. **Taille** : `S` une séance · `M` une demi-journée à une journée · `L` plusieurs
jours. Aucune date, aucune estimation en jours-homme.

Rappels de dépôt qui s'appliquent à toutes les tâches : `../format.sh mobile` avant chaque
commit, avec sa sortie dans le commit ; `flutter analyze` propre ; `dart run build_runner build`
après toute modification de modèle ; `mobile/lib/config/paths.generated.dart`,
`frontend/src/…/paths.generated.ts`, `apple-app-site-association` et la section deeplink de
`AndroidManifest.xml` ne s'éditent **jamais** à la main — ils sortent de `pnpm generate-routes`
lancé depuis `frontend/` après édition de `contracts/routes.yaml`.

### 1.0.2 Faits acquis, à ne pas re-discuter

- L'API v2 est livrée, le contrat régénéré en **1.5.0**, les clients Retrofit/Freezed mobiles
  sont à jour. `PedalonsApiClient.version` doit être vérifié à `'1.5.0'`.
- Ne sont **pas** livrés : notifications push, pagination par curseur, ETag / URLs d'images
  signées / blurHash, `GET /api/map/features`, le champ `waitlisted`. Aucune tâche de ce plan
  n'en dépend ; chaque dégradation est nommée.
- **Le meneur de groupe est livré** en 1.5.0 : la migration `V30__ride_group_leader.sql` ajoute
  `ride_groups.leader_id`, **nullable**, FK vers `users` en `ON DELETE SET NULL` (un compte
  supprimé n'emporte ni le groupe, ni son parcours, ni ses participants). En lecture,
  **`RideGroupDto.leader`** est un `PublicUserDto` **nullable** (`id`, `displayName`,
  `avatarUrl`) ; en écriture, **`GroupRequest.leaderId`** est un TSID en chaîne **optionnel**, et
  envoyer `null` **efface** la désignation — c'est une opération réelle, pas une omission. Le
  membre désigné **doit appartenir à l'équipe qui possède la sortie**, sinon **400
  `RIDE_GROUP_LEADER_NOT_MEMBER`** ; l'appartenance n'est **pas** revérifiée ensuite, un meneur
  qui quitte l'équipe reste affiché sur la sortie, qui a eu lieu — le mobile ne re-valide donc
  rien et affiche ce que l'API rend. La pastille « Organisateur » reste
  **conditionnelle**, non plus faute de donnée mais parce que **la plupart des groupes n'auront
  pas de meneur désigné** : `leader` nul ⇒ on n'affiche rien, et **jamais** de repli sur
  `createdBy`, qui vaut le créateur de la sortie pour **tous** ses groupes — c'est précisément le
  défaut que cette colonne corrige. Les **gabarits de sortie** n'ont volontairement pas de
  meneur : `RideTemplateGroupRequest` ne gagne aucun champ, instancier une sortie depuis un
  gabarit ne désigne personne.
- **Le contact d'un annonceur est livré** en 1.4.0 : `POST
  /api/teams/{teamSlug}/classifieds/{slug}/contact` relaie un message par e-mail, `Reply-To`
  posé sur l'expéditeur. **Aucune adresse n'apparaît dans l'API** ; `AdDto` ne porte aucun champ
  de contact, et c'est le point de la conception.
- Aucune maquette ne fournit le mode sombre, alors que le brief §5 l'exige. Il est **dérivé**
  par la règle du §1.1.2, qui produit des valeurs exactes, pas approchées.
- [`pedalons.css`](../audit-ux/pedalons.css) fait autorité contre la planche
  `00 Fondations` : `.media--16x9` = **208 px**,
  `.btn--sm` = **44 px** (cible tactile minimale du brief §5).

### 1.0.3 Arbitrages tranchés entre les trois plans sources

Les trois plans ont tranché différemment onze questions. Voici la décision unique et sa raison.

| # | Question | Décision | Raison |
|---|---|---|---|
| 1 | Emplacement de la bibliothèque : `core/pdl/` ou `core/design/` | **`mobile/lib/core/pdl/`**, barrel `pdl.dart` | Le répertoire porte le préfixe des widgets ; une seule arborescence à greper. `core/design/` est abandonné, y compris dans les tâches d'écran. |
| 2 | Frontière entre `core/pdl` et les widgets de feature | **`core/pdl` ne mentionne aucun DTO généré dans son API publique** ; tout widget qui prend un DTO vit dans son feature, sans préfixe `Pdl` | Règle vérifiable : `grep -rn "api/generated" mobile/lib/core/pdl` doit rester vide. Elle remplace les trois conventions divergentes (« vague D métier », « core/design », « features »). |
| 3 | Noms doublons (`PdlThumbnail`/`PdlThumb`, `PdlMediaHeader`/`PdlCardMedia`, `PdlSeatsBar`/`PdlProgressBar`, `PdlGroupTrack`/`PdlColorTrack`, `PdlStickyToolbar`/`PdlPinnedToolbar`, `PdlNavRow`/`PdlSettingRow`, `PdlTabBar`/`PdlBottomTabs`, `PdlTextAction`/`PdlTextButton`/`PdlLoadingButton`) | **`PdlThumb`, `PdlCardMedia`, `PdlProgressBar`, `PdlColorTrack`, `PdlPinnedToolbar`, `PdlSettingRow`, `PdlBottomTabs`, `PdlButton(variant:)`** | Le nom retenu est celui qui décrit le rôle, pas l'écran d'origine. Les alias sont interdits (revue). |
| 4 | Profil altimétrique : package de graphes ou peinture maison | **`CustomPainter` maison, deux `CustomPaint` superposés** ; source `GET …/elevation-profile?samples=` | Colorisation *par segment* (échelle HSL de pente), rendu histogramme jointif, et repeinte du seul réticule à 60 fps : aucun package n'offre les trois. Le calcul client sur `tracks[].line` disparaît. |
| 5 | Nombre d'échantillons du profil (200 ? 300 ? 2 × largeur ?) | **`samples = (2 × largeur logique).clamp(60, 300)`**, une seule règle, cache par `(routeSlug, samples)`, agrégation client vers ~76 barres | Trois valeurs différentes dans les sources auraient produit trois entrées de cache pour un même parcours. |
| 6 | Structure de routes : `StatefulShellRoute` à 5 onglets fixes, ou conservation du `TeamShell` à onglets d'équipe | **`StatefulShellRoute.indexedStack` à 5 branches fixes ; `TeamShell` supprimé ; les sections d'équipe deviennent du contenu** (rangée de chips collante sous l'en-tête d'équipe) | Les maquettes montrent 5 onglets racine sur **toutes** les planches, y compris d'équipe. La seconde `NavigationBar` est la cause directe de DE-8 (feuilles rendues sous la barre) et de la superposition d'états d'erreur de `_TeamTabPageWrapper`. Conséquence : `PdlBottomTabs` n'a plus de variante `team`, et `buildTeamDestinations` devient `buildTeamSections` alimentant la rangée de chips. |
| 7 | Carte multi-tracés : une couche par groupe, ou une source unique + expressions `case` | **Une `LineStyleLayer` par groupe** (`ride-track-{groupId}`), toutes adossées à une seule `GeoJsonSource` | Vérifié sur `maplibre 0.3.5` : `MapController.queryLayers(Offset)` renvoie `QueriedLayer{layerId, sourceId, sourceLayer}` **sans les propriétés de l'entité**. Identifier un groupe par `feature.properties.groupId` est donc impossible ; seul le `layerId` porte l'identité. À 10 groupes, c'est 10 couches (20 avec les halos) — tenable. La sélection change les propriétés des deux couches concernées (`setLayerProperties`), elle ne reconstruit rien. |
| 8 | Tuiles `.mvt` de masse : le contrat dit qu'elles s'authentifient **par cookie de session**, que le mobile n'a pas | **Repli GeoJSON de proximité retenu comme chemin de livraison**, bascule `VectorSource` prête derrière un drapeau | Le plan des écrans (21) supposait par endroits des tuiles disponibles : il est aligné sur le repli. Plafond assumé : quelques centaines de tracés dans la vue courante, contre 2 585 côté web. L'évolution qui lèverait le blocage (URL de tuile signée à durée courte) est listée en §5. |
| 9 | Mode sombre : table complète en fondations, ou « à trancher au moment où l'écran le rencontre » | **La table de jetons du §1.1 est complète et fait autorité** ; aucun écran ne tranche quoi que ce soit | Les trois points laissés ouverts par le plan des écrans (fonds doux de badge, voiles de carte, fond du profil) sont résolus en §1.1.3 à §1.1.5. |
| 10 | Jeu d'icônes : Tabler via `flutter_svg` (maquettes, `brand.md` §6) ou Material outline (brief §4.1.2, code existant) | **Material outline conservé pour la v2**, derrière une couche d'indirection `PdlIcons` | Tout le mobile est en `Icons.*` ; l'écart ne porte que sur la graisse du trait des icônes de 11 px de badge. Ajouter `flutter_svg` + un jeu d'assets pour cela est disproportionné en regard des ~65 composants à écrire. `PdlIcons` (un seul fichier de constantes) rend le basculement ultérieur mécanique. |
| 11 | Fuseau horaire des dates de sortie et d'étape | **Fuseau de l'appareil, parité avec le web** ; le « fuseau d'équipe » est abandonné et `package:timezone` retiré des dépendances | Vérifié : `TripStageDto.dateTime` et `CalendarEventDto.start` sont des `Instant` (UTC, sans zone) et **aucun champ de fuseau n'existe dans le contrat** ; `frontend/src/utils/dateFormat.ts` formate dans le fuseau du navigateur. Le cas de test « étape nommée *Lundi* datée dimanche » reste écrit, mais il vérifie l'**absence de double conversion**, pas une localisation d'équipe. L'évolution `Team.timezone` (ou des dates zonées au contrat) est listée en §5. |

### 1.0.4 Arbitrages maquette contre brief

| Écart | Décision | Raison |
|---|---|---|
| Mode sombre absent des maquettes, exigé par le brief §5 | **Brief.** Palette dérivée (§1.1.2), les écrans sont recettés en clair *et* en sombre | `themeMode` devient sélectionnable (`UserDto.theme`) : un écran non traité serait cassé, pas seulement laid |
| Barre supérieure de l'accueil : collante (maquette) contre rétractable (brief) | **Brief** : `SliverAppBar(pinned, floating, snap)` ; la barre **d'outils**, elle, reste épinglée | Rendre 56 px au défilement ramène la 5ᵉ carte à l'écran. C'est la barre d'outils qui corrigeait le défaut, pas l'app bar |
| Carte de la sortie figée à 300 px (maquette) | **Brief** : `clamp(260, 44 % de la hauteur, 460)` | La maquette est cadrée sur un seul appareil ; 300 px fixes mangent 40 % d'un iPhone SE |
| Barre système noire posée sur la tuile (maquette 13) | **Brief §5** : `SystemUiOverlayStyle.light` + `PdlScrim` haut | Défaut de maquette, contredit la règle « jamais de texte sur une tuile » |
| 2 squelettes (maquette) contre 5 (brief) | **Brief** : 5 | Coût nul ; 2 squelettes ressemblent à une fin de liste |
| Aucun rendu impérial dans les maquettes | **Brief** : formateur unique piloté par `UserDto.unitSystem` dès le premier écran | Le champ existe ; coder `km` en dur imposerait de rouvrir tous les écrans plus tard |
| Pas de badge « je participe » sur les cartes du fil (maquette) | **Brief** : badge dès `registered == true` | C'est l'apport principal de la v2 sur les listes |
| Barre d'onglets incohérente sur la maquette 12 | **Ignorée** : l'écran est hors coquille, il n'a pas de barre d'onglets | Défaut de maquettage |
| « Voir tout » omis sur les cartes de groupe non sélectionnées (maquette) | **Maquette** | 10 groupes × 4 lignes pleines dépassent l'écran ; les avatars restent cliquables partout |
| Ajouts de maquette non prévus au brief (« Choisir un groupe », « Voir les N autres groupes », légende de tracés, info-bulle à 3 valeurs, chevauchement feuille/carte de 20 px) | **Tous retenus** | Chacun répond à un cas réel et aucun ne coûte d'API |
| « Ne plus participer » en contour, plein indigo réservé à « Participer » (24) | **Maquette** | Règle « au plus une action pleine et colorée par écran » |
| Cloche de notifications sur les barres supérieures | **Retirée partout** | Aucun endpoint ; le brief §5 interdit une icône-action sans effet. `PdlAppBar` n'expose aucun emplacement de notification en v2 |

---

## 1.1 Thème et jetons

### 1.1.1 Diagnostic

`mobile/lib/core/theme/pedalons_theme.dart` (61 l.) construit tout depuis
`ColorScheme.fromSeed(seedColor: BrandColors.indigoLight)`. Material 3 dérive une palette tonale
du germe : **aucune** couleur de surface, de texte, de bordure, ni la primaire elle-même ne vaut
la valeur de marque. `#4c6ef5` devient un indigo tonal, `#ffffff` un blanc lavande, `onSurface`
un gris-violet. Toute la charte est invisible aujourd'hui. S'y ajoutent : aucun jeton de rôle
(paires doux / sur-doux, bordures nommées, voiles, dégradés, palette multi-parcours, échelle de
pente), aucune échelle typographique (les 14 rôles de la maquette n'existent pas), des rayons
faux (12 px imposé aux boutons et aux champs, là où la maquette met 8 px et réserve 12 px aux
cartes), et des cartes en relief (`elevation: 1`) alors que `.card` est plate. Enfin
`mobile/lib/app.dart:38,49` passe `theme` et `darkTheme` **sans `themeMode`** : l'app suit le
système sans recours possible.

### 1.1.2 Règle de dérivation du mode sombre

`brand.md` publie les paires clair/sombre des jetons de rôle mais **pas** les fonds doux de badge
(`#d0ebff`, `#f3d9fa`, `#c3fae8`, `#ffe8cc`…), qui sont des littéraux de `pedalons.css`. En
rapprochant les valeurs sombres publiées de la palette Mantine, la règle est mécanique :

```
soft(sombre)    = nuance 9 de la famille × 0,5   (arrondi entier par canal)
on-soft(sombre) = nuance 0 de la famille
```

Vérifiée sur les cinq paires publiées : `indigo-9 #364fc7 ×0,5 = #1b2864` ✓ ·
`green-9 #2b8a3e ×0,5 = #16451f` ✓ · `red-9 #c92a2a ×0,5 = #651515` ✓ ·
`gray-9 #212529 ×0,5 = #111315` ✓ · `yellow-9 #e67700 ×0,5 = #733c00` ✓ ; et les `on-soft`
publiés (`#edf2ff`, `#ebfbee`, `#fff5f5`, `#f8f9fa`, `#fff9db`) sont exactement les nuances 0.
**Cette règle produit les valeurs sombres manquantes** et doit être recopiée en commentaire de
tête du fichier de jetons : c'est la seule justification des hexadécimaux non maquettés.

### 1.1.3 Surfaces, bordures, texte, primaire et rôles

| Jeton `PdlColors` | Clair | Sombre | Jeton Flutter |
|---|---|---|---|
| `bg` / `surface` | `#ffffff` | `#242424` | `colorScheme.surface` |
| `surfaceAlt` | `#f8f9fa` | `#2e2e2e` | `surfaceContainerLow` |
| `surfaceRaised` | `#ffffff` | `#2e2e2e` | `surfaceContainerHigh` |
| `surfaceHover` | `#f8f9fa` | `#3b3b3b` | — (`WidgetStateProperty.hovered`) |
| `border` | `#ced4da` | `#424242` | `outline` |
| `borderSubtle` | `#dee2e6` | `#2e2e2e` | `outlineVariant` + `dividerTheme.color` |
| `text` | `#000000` | `#c9c9c9` | `onSurface` |
| `textBright` | `#000000` | `#ffffff` | — (titres d'écran et de section) |
| `textDimmed` | `#868e96` | `#828282` | `onSurfaceVariant` |
| `textPlaceholder` | `#adb5bd` | `#696969` | `inputDecorationTheme.hintStyle` |
| `link` | `#228be6` | `#4dabf7` | — |
| `overlay` | `rgba(255,255,255,.90)` | `rgba(36,36,36,.90)` | — (barre d'onglets) |
| `overlaySolid` | `rgba(255,255,255,.95)` | `rgba(36,36,36,.95)` | — (barres d'outils et d'action, boutons de carte) |
| `scrim` | `rgba(36,36,36,.90)` | identique | — (voile haut de carte) |
| `sheetBarrier` | `rgba(36,36,36,.45)` | identique | `barrierColor` des feuilles |
| `primary` | `#4c6ef5` | `#3b5bdb` | `primary` |
| `primaryHover` | `#4263eb` | `#364fc7` | — |
| `primarySoft` / `primaryOnSoft` | `#dbe4ff` / `#364fc7` | `#1b2864` / `#edf2ff` | `primaryContainer` / `onPrimaryContainer` |
| `onPrimary` | `#ffffff` | `#ffffff` | `onPrimary` |
| `success` / `successSoft` / `successOnSoft` | `#40c057` / `#d3f9d8` / `#2b8a3e` | `#2f9e44` / `#16451f` / `#ebfbee` | — |
| `warning` / `warningSoft` / `warningOnSoft` | `#fab005` / `#fff3bf` / `#e67700` | `#f08c00` / `#733c00` / `#fff9db` | — |
| `danger` / `dangerSoft` / `dangerOnSoft` | `#fa5252` / `#ffe3e3` / `#c92a2a` | `#e03131` / `#651515` / `#fff5f5` | `error` / `errorContainer` / `onErrorContainer` |
| `neutral` / `neutralSoft` / `neutralOnSoft` | `#868e96` / `#f1f3f5` / `#212529` | `#343a40` / `#111315` / `#f8f9fa` | — |
| `disabledBg` / `disabledText` / `disabledBorder` | `#e9ecef` / `#adb5bd` / `#ced4da` | `#2e2e2e` / `#696969` / `#424242` | états `disabled` des boutons |

Accents : `accentBlue #228be6` → `#1971c2` · `accentOrange #fd7e14` → `#e8590c` ·
`accentGrape #be4bdb` → `#9c36b5` · `accentTeal #12b886` → `#099268` ·
`accentViolet #7950f2` → `#6741d9` · `accentCyan #15aabf` → `#0c8599` ·
`accentLime #82c91e` → `#66a80f` (**absent de `BrandColors`, à ajouter**) ·
`accentPink #e64980` → `#c2255c` · `accentDark #2e2e2e` → `#828282` (revêtement route).

### 1.1.4 Paires douces de badge

| Famille (classes CSS) | Fond clair | Texte clair | Fond sombre | Texte sombre |
|---|---|---|---|---|
| bleu — `.b-ride` `.b-public` | `#d0ebff` | `#1864ab` | `#0c3255` | `#e7f5ff` |
| raisin — `.b-post` | `#f3d9fa` | `#862e9c` | `#43174e` | `#f8f0fc` |
| turquoise — `.b-trip` `.b-mixed` | `#c3fae8` | `#087f5b` | `#043f2d` | `#e6fcf5` |
| orange — `.b-ad` `.b-gravel` `.b-unlisted` `.b-wanted` | `#ffe8cc` | `#d9480f` | `#6c2407` | `#fff4e6` |
| vert — `.b-pub` `.b-mtb` `.b-sale` | `#d3f9d8` | `#2b8a3e` | `#16451f` | `#ebfbee` |
| gris — `.b-draft` `.b-team` `.b-road` | `#f1f3f5` | `#212529` | `#111315` | `#f8f9fa` |
| gris foncé — `.b-done` (« Terminée ») | `#e9ecef` | `#495057` | `#24282b` | `#f8f9fa` |
| rouge — `.b-cancel` | `#ffe3e3` | `#c92a2a` | `#651515` | `#fff5f5` |
| indigo — `.b-ins` `.b-rental` | `#dbe4ff` | `#364fc7` | `#1b2864` | `#edf2ff` |

Badges **pleins** (catégories de col uniquement) : `HC #be4bdb` · `CAT1 #fa5252` ·
`CAT2 #fd7e14` · `CAT3 #fab005` · `CAT4 #40c057`, texte blanc **sauf CAT3 en `#212529`**.
En sombre l'aplat passe en nuance 8 (`#9c36b5`, `#e03131`, `#e8590c`, `#f08c00`, `#2f9e44`).

Note de revue : `Vente` (`.b-sale`) et `Publié` (`.b-pub`) partagent exactement le même couple de
couleurs et s'empilent sur la même carte d'annonce — conforme à `brand.md`, mais la
différenciation ne tient qu'au libellé ; à surveiller en recette sombre.

### 1.1.5 Dégradés, palettes ordonnées, échelle de pente

Dégradés de repli des bandeaux média (135° CSS ≈ `topLeft → bottomRight`), **identiques dans les
deux modes** car ils portent une icône blanche à 80 % :
`gradRide #228be6 → #22b8cf` · `gradPost #be4bdb → #f06595` · `gradTrip #12b886 → #51cf66` ·
`gradTeam #7950f2 → #5c7cfa` · `gradAd #ff922b → #ffd43b`.

`kMultiTrackPalette` — ordre imposé, indexé par `RideGroupDto.sortOrder` puis
`TripStageDto.stageIndex`, **modulo 10**, identique dans les deux modes :
`#566B13, #1d32a8, #732C7B, #bdbd22, #c90808, #b81491, #628de3, #6dcc5c, #c694d4, #e3a209`.

```dart
Color slopeColor(double gradePercent) {
  final t = (gradePercent / 18).clamp(0.0, 1.0);
  final hue = 85 + (255 - 85) * t;                     // 85 vert → 255 violet
  return HSLColor.fromAHSL(1, hue, 0.86, 0.62).toColor();
}
const slopeNeutral = HSLColor.fromAHSL(1, 210, 0.86, 0.62);   // pente inconnue
```

Couleurs de carte, **identiques dans les deux modes sauf mention** : tracé `#228be6` clair /
`#4dabf7` sombre (déjà dans `BrandColors.mapRouteLineHex*`), marqueur de départ `#40c057`,
d'arrivée `#fa5252`, point de passage `#fab005`. Fond du profil altimétrique : `surfaceAlt`
(donc `#f8f9fa` → `#2e2e2e`), barres inchangées.

### 1.1.6 Échelle typographique (`PdlTypography`)

Famille `GoogleFonts.inter`, aucune graisse au-delà de 700, capitales réservées aux badges.

| Jeton | Taille / graisse | Divers | Couleur par défaut |
|---|---|---|---|
| `screenTitle` | 22 / 700, h 1.30 | `letterSpacing: -0.2` | `textBright` |
| `sectionTitle` | 18 / 700, h 1.40 | — | `textBright` |
| `barTitle` | 17 / 700 | 1 ligne + ellipsis | `text` |
| `cardTitle` | 16 / 700, h 1.35 | 1 ligne + ellipsis | `text` |
| `wordmark` | 20 / 700 | `letterSpacing: -0.2` | `primary` |
| `body` | 15 / 400, h 1.55 | — | `text` |
| `bodyStrong` | 15 / 600 | libellés actionnables, noms de personne | `text` |
| `sub` | 14 / 400, h 1.45 | — | `textDimmed` |
| `statValue` / `statBigValue` | 14 / 600 · 17 / 600 | — | `text` |
| `count` | 13 / 500 | compteurs de liste, ligne d'équipe | `textDimmed` |
| `xs` | 12 / 400, h 1.40 | — | `textDimmed` |
| `tab` | 10 / 500 | — | `textDimmed`, `primary` si actif |
| `badge` / `badgeLg` | 10 / 700 · 11 / 700 | CAPS, `letterSpacing: 0.25` | selon variante |
| `mono` | 11 / 400 | `letterSpacing: -0.2`, pile mono système | `textDimmed` |
| `monoAxis` | 10 / 400, h 1.40 | axe de profil, info-bulle | `textPlaceholder` |
| `button` / `buttonSm` | 15 / 600 · 14 / 600 | — | selon variante |
| `textButton` | 13 / 500 | — | `link` |
| `chip` | 14 / 500, **600 si actif** | — | `textDimmed` / `primaryOnSoft` |

La police mono passe par une `TextStyle(fontFamilyFallback: ['SFMono-Regular','Menlo','Roboto
Mono','monospace'])` et **non** par `GoogleFonts.jetBrainsMono` : quatre usages ne justifient pas
le téléchargement d'une police.

### 1.1.7 Espacements, rayons, métriques, mouvement

| Jeton | Valeur | Emploi |
|---|---|---|
| `PdlSpacing.section` | 16 | `.sec`, horizontal **et** vertical |
| `PdlSpacing.sectionTight` | 12 / 16 | `.sec--tight` |
| `PdlSpacing.card` / `.cardTight` | 16 · 12 | corps de carte |
| `PdlSpacing.feedGap` / `.teamCardGap` | 8 · 12 | écart entre cartes de fil · d'équipe |
| `PdlSpacing.statsWrap` / `.statsNowrap` | 4 / 16 · 12 | rangées de statistiques |
| `PdlSpacing.chipGap` / `.badgeGap` | 8 · 4 | rangées de chips · de badges |
| `PdlSpacing.avatarOverlap` | −8 | grappes d'avatars |
| `PdlSpacing.meta2` | 14 / 16 | grille méta 2 colonnes |
| `PdlSpacing.contentMaxWidth` | 600 | colonne bornée (écran 33, tablette) |
| `PdlRadii.sm` | 4 | squelettes, info-bulle |
| `PdlRadii.md` | **8** | boutons, champs, vignettes, bandeaux, segmenté, boutons de carte |
| `PdlRadii.segThumb` | 6 | pastille active du segmenté |
| `PdlRadii.card` | 12 | cartes **uniquement** |
| `PdlRadii.lg` | 16 | feuilles modales (coins hauts) |
| `PdlRadii.pill` | 1000 | badges, chips, avatars, poignée, barre de places |
| `PdlRadii.track` | 2 | traits de rappel de couleur |
| `PdlMotion.shimmer` | 1400 ms `easeInOut`, boucle | squelettes |
| `PdlMotion.stateChange` | 180 ms `easeOut` | sélection, bascule optimiste, ouverture de feuille — **non chiffré par les maquettes, valeur retenue** |
| `PdlMotion.blurToolbar` / `.blurOverlayButton` | σ 12 · σ 6 | barres épinglées · boutons sur carte |

Ombres : `md = [0 1 3 rgba(0,0,0,.05), 0 20 25 −5 rgba(0,0,0,.05), 0 10 10 −5 rgba(0,0,0,.04)]` ·
`sheet = [0 −2 16 rgba(0,0,0,.1)]` · `segThumb = [0 1 2 rgba(0,0,0,.12)]`. **En sombre l'ombre ne
porte pas : c'est la bordure `#424242` qui sépare les surfaces** (`brand.md` l. 278) — `md` et
`segThumb` deviennent des bordures, seule `sheet` est conservée.

Métriques (`PdlMetrics`) : `appBar 56` · `barAction 44` · `toolbarPadding 10` · `tabItem 52` ·
`tabBottomInset` = `MediaQuery.viewPadding.bottom` réel · `actionBar 44` · `chipVisual 34` ·
`tapTarget 44` · `media 160` · `media16x9 208` · `media120 120` · `thumb 80/64/56` ·
`elevation 72/110/140` · `seatsBar 6`.

### 1.1.8 Tâches

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| F-TH-1 | ☑ | Créer le fichier de jetons de couleur | `[C] core/theme/pdl_colors.dart` · `[M] core/theme/pedalons_colors.dart` (ajout `limeLight/limeDark`) · `[M] core/theme/theme.dart` | — | M | `ThemeExtension<PdlColors>` avec fabriques `light()`/`dark()`, `copyWith`, `lerp`, extension d'accès `context.pdl` ; règle §1.1.2 en commentaire de tête ; un test vérifie `primary == 0xFF4C6EF5` en clair et `0xFF3B5BDB` en sombre, et l'exposition des 9 paires douces dans les deux modes |
| F-TH-2 | ☑ | Créer l'échelle typographique | `[C] core/theme/pdl_typography.dart` | F-TH-1 | S | `ThemeExtension<PdlTypography>` des 20 rôles, paramétrée par `Brightness` ; test sur `screenTitle` (22/700/−0.2) et `badge` (10/`letterSpacing 0.25`) |
| F-TH-3 | ☑ | Créer les constantes de mise en page et de mouvement | `[C] core/theme/pdl_tokens.dart` (`PdlSpacing`, `PdlRadii`, `PdlMetrics`, `PdlMotion`, `PdlShadows`, `kMultiTrackPalette`, `slopeColor`) | F-TH-1 | S | `slopeColor(0) == hsl(85,86%,62%)` ; `slopeColor(18)` et `slopeColor(40)` valent `hsl(255,86%,62%)` — testé |
| F-TH-4 | ☑ | Créer la couche d'indirection d'icônes | `[C] core/theme/pdl_icons.dart` | — | S | Toutes les icônes de la bibliothèque et des écrans passent par `PdlIcons.*` ; `grep -rn "Icons\." mobile/lib/core/pdl` ne renvoie que `pdl_icons.dart` |
| F-TH-5 | ☑ | Réécrire `PedalonsTheme.build` | `[M] core/theme/pedalons_theme.dart` | F-TH-1..3 | M | `ColorScheme` **explicite** (plus de `fromSeed`), les deux extensions enregistrées ; cartes `elevation: 0` + bordure `borderSubtle` + `margin: zero` ; boutons et champs rayon 8, hauteur min 44 ; `appBarTheme` titre à gauche, 56 px, bordure basse ; `bottomSheetTheme` rayon 16 haut, `showDragHandle: false` ; **`chipTheme` non utilisé** (`PdlChip` est maison, un `Chip` Material fait 48 px). Capture d'accueil clair et sombre : la primaire relevée à la pipette vaut exactement `#4c6ef5` / `#3b5bdb`, aucune carte ne porte d'ombre |
| F-TH-6 | ☑ | Faire évoluer `enum_colors.dart` du monochrome vers le triplet | `[M] core/theme/enum_colors.dart` | F-TH-1 | S | `PdlTone{fill, soft, onSoft, filledStyle}` et une méthode `tone(PdlColors)` sur les 7 enums (`Status`, `TeamRole`, `SurfaceType`, `ClimbCategory`, `AdType`, `Visibility`, `PublicationType`) ; `ClimbCategory` seule porte `filledStyle: true` ; `SurfaceType.road` garde `fill #2e2e2e` mais reçoit la paire grise en doux ; le fichier cesse d'être du code mort (importé par `PdlBadge`) |
| F-TH-7 | ☑ | Ajouter le mode de thème persistant et les préférences utilisateur | `[C] core/preferences/user_preferences_provider.dart` · `[M] app.dart` · `[M] pubspec.yaml` (`shared_preferences`) · `[M] features/profile/presentation/pages/profile_page.dart` (bloc Préférences minimal) | F-TH-5 | M | Chaîne unique : `UserDto.theme`/`.language`/`.unitSystem` font autorité → miroir `shared_preferences` pour que le **premier cadre** après un démarrage à froid soit dans le bon mode → écriture par `PATCH /api/users/me/preferences`. `UserDto.contactableByMembers` emprunte le même provider et le même `PATCH`, **sans miroir local** : il n'a aucun effet sur le premier cadre. `app.dart` passe enfin `themeMode`. Le segmenté greffé sur le profil existant change le thème sans redémarrage ; tuer et relancer l'app restitue le mode avant tout appel réseau. **C'est le seul point d'entrée des préférences** : l'écran 33 (lot 6) le consomme, ne le duplique pas |
| F-TH-8 | ☑ | Aligner toute lecture de luminosité sur le thème de l'app | `[M] features/routes/presentation/widgets/route_map.dart:36,51` et tout autre site | F-TH-7 | S | `grep -rn "platformBrightnessOf" mobile/lib` ne renvoie plus rien — sans quoi la carte reste claire dans une app forcée en sombre |

---

## 1.2 La bibliothèque de composants `core/pdl`

Emplacement : `mobile/lib/core/pdl/`, barrel `pdl.dart`. **Pas** dans `core/widgets/` : cette
bibliothèque a un contrat propre — jetons obligatoires, cible tactile 44 px, deux modes, aucun
littéral de couleur, **aucune importation de `api/generated`**.

Colonne **Origine** : `créer` = nouveau · `étendre` = un widget existant de `core/widgets`,
`core/animations` ou `core/pagination` sert de base. Les alias abandonnés du §1.0.3 ne doivent
apparaître nulle part.

### 1.2.1 Vague A — primitives (20, aucune dépendance hors thème)

| # | Widget | Rôle et spécification | Origine |
|---|---|---|---|
| A1 | `PdlSkeleton` | Rectangle ou cercle scintillant ; dégradé `neutralSoft → disabledBg → neutralSoft`, 400 % de large, 1400 ms | **étendre** `core/animations/shimmer_placeholder.dart` (animation maison déjà écrite) |
| A2 | `PdlBadge` | Étiquette type / statut / visibilité / rôle / revêtement / col. `tone` (`PdlTone`), `size` (`sm` h 18 · `lg` h 22), doux ou plein, padding `0 8`/`0 10`, rayon pilule, icône interne 11 px | créer |
| A3 | `PdlButton` | **Bouton unique de l'app.** `variant` (`fill`/`outline`/`text`/`danger`), `size` (`md`/`sm`), `fullWidth`, `pill`, `icon`, `loading`, `enabled`. **Hauteur min 44 partout**, rayon 8 ou pilule. `loading` → fond `disabledBg` + libellé substantivé (« Inscription... »). `danger` = **contour**, jamais d'aplat rouge | créer (absorbe `PdlTextButton`, `PdlTextAction`, `PdlLoadingButton`) |
| A4 | `PdlAvatar` | Rond image ou initiales, tailles 16/20/26/32/40/56/100, polices 8/10/10/12/14/19/34 en 700 ; teinte par hachage sur 12 accents ; **l'utilisateur courant est toujours `primary`** | **étendre** `core/widgets/authenticated_image.dart` (en-tête `Authorization` et repli initiales déjà gérés) |
| A5 | `PdlCard` | Rayon 12, bordure 1 px `borderSubtle`, **aucune ombre** ; `selected` → bordure 2 px `primary` ; `flat` → fond `surfaceAlt` sans bordure ; `padding` 16 / 12 / aucun | **étendre** `core/animations/animated_card.dart` (échelle 0.98 au press, respect de `disableAnimations` — à conserver) |
| A6 | `PdlStat` | Icône + valeur + libellé ; `big` → colonne, valeur 17 px ; `up`/`down` en `successOnSoft`/`dangerOnSoft` | créer |
| A7 | `PdlStatRow` | Rangée de `PdlStat`, `wrap` (gap 4/16) ou `nowrap` (gap 12) ; **repli en deux lignes** au-delà de `textScaler.scale(14) > 17` | créer |
| A8 | `PdlProgressBar` | Barre de places : h 6, rayon pilule, fond `neutralSoft`, `success` / `warning` ≥ 80 % / `danger` à 100 % | créer |
| A9 | `PdlColorTrack` | Trait de rappel de couleur de tracé : 4 × 20 (carte de groupe) ou 14 × 3 (légende), rayon 2 | créer |
| A10 | `PdlNumberPill` | Pastille ronde 26 px, fond `primary`, chiffre 12/700 blanc | créer |
| A11 | `PdlChip` | Filtre / portée / tri. **Visuel 34 px dans une cible de 44 px** (`SizedBox(height: 44) + Center`) ; actif `primarySoft`/`primaryOnSoft`/600 ; variantes `removable` (croix) et `sortStyle` | créer |
| A12 | `PdlSearchField` | Champ de recherche et de saisie : h 44, rayon 8, fond `surfaceAlt`, debounce 350 ms, `error`, `readOnly` | **étendre puis déplacer** `features/routes/.../route_search_bar.dart` → `core/pdl/pdl_search_field.dart` |
| A13 | `PdlSectionHeader` | Titre 18/700 + compteur 13/500 + action optionnelle, marge basse 8-10 | créer |
| A14 | `PdlSegmented` | 2 ou 3 positions : conteneur `neutralSoft` rayon 8 padding 3, segment **min 44 px** rayon 6 en 14/600, actif `surface` + ombre (bordure en sombre). **Ne pas** utiliser `SegmentedButton` M3 (formes et coche imposées) | créer |
| A15 | `PdlSwitch` | 46 × 28, curseur 24 marge 2, `border` → `primary`. `Switch` M3 fait 52 × 32 | créer |
| A16 | `PdlInfoLine` | Ligne libellé / valeur, padding `8 0`, filet bas | créer |
| A17 | `PdlSettingRow` | Ligne de réglage ou de navigation : **min 52 px, ligne entière tappable**, `trailing` = badge, valeur, `PdlSwitch` ou chevron | créer |
| A18 | `PdlScrim` | Voile de lisibilité : haut 150 px `rgba(36,36,36,.9) → .55 à 45 % → 0`, bas 90 px `.75 → 0`, `IgnorePointer` | créer |
| A19 | `PdlBlurSurface` | `ClipRect + BackdropFilter` ; **coûteux, réservé aux barres épinglées, jamais dans une liste défilante** | créer |
| A20 | `PdlFilterButton` | Bouton 44 × 44 « filtres » portant un compteur de filtres actifs (`Badge.count`) ; seul témoin de filtrage en vue carte | créer |

### 1.2.2 Vague B — composés (26, dépendent de A)

| # | Widget | Spécification | Origine |
|---|---|---|---|
| B1 | `PdlChipRow` | `ListView` horizontal + `ShaderMask(dstIn)` sur les 28 derniers pixels, padding latéral 16, gap 8, **hauteur mesurée jamais figée** | créer |
| B2 | `PdlAvatarStack` | Chevauchement −8, anneau 2 px `surface`, pastille `+N`, `max: 5` | créer |
| B3 | `PdlTeamLine` | Ligne d'équipe cliquable, min 24 px, 13/500 `textDimmed`, chevron 14 px ; variante « voyage » à icône | **étendre** `core/widgets/team_banner.dart` |
| B4 | `PdlPlaceRow` | Lieu départ (vert) / arrivée (rouge) : pastille 10 px, nom `bodyStrong`, adresse `xs` | créer |
| B5 | `PdlThumb` | Vignette carrée 56/64/80, rayon 8, bordure, repli icône route ; **choix clair/sombre sur `Theme.of(context).brightness`** | **étendre** `AuthenticatedImage` |
| B6 | `PdlCardMedia` | Bandeau média 120/140/160/208 : photo, sinon dégradé §1.1.5 + icône de type 48 px blanche à 80 % | créer |
| B7 | `PdlEmptyState` | 4 variantes `empty` / `filtered` / `error` / `notFound` : icône 48 px, **titre nominal**, phrase, actions | **étendre** `core/animations/animated_empty_state.dart` |
| B8 | `PdlBanner` | Bandeau **persistant** `info` / `warn` / `danger`, titre gras dans le flux, action optionnelle, `fullBleed`. **Jamais un snackbar de 4 s** | créer |
| B9 | `PdlPagedListFooter` | « N *sur* M · chargement de la suite... », erreur → « Réessayer », fin → « Vous avez tout vu ». **Le total est indispensable** | **étendre** `core/pagination/paged_list_footer.dart` |
| B10 | `PdlPersonRow` | Min 56 px, avatar 40, nom `bodyStrong`, sous-titre, badge de rôle, filet bas. `organizerFlag` est **alimenté** par `RideGroupDto.leader` (1.5.0) comparé à l'identifiant de la personne rendue ; la pastille « Organisateur » reste **conditionnelle** parce que la plupart des groupes n'ont pas de meneur désigné — `false` ⇒ rien n'est rendu, et **jamais** de repli sur `createdBy` | créer |
| B11 | `PdlAttachmentRow` | Icône ou `PdlThumb 56` + nom + `mono` « TYPE », bouton 44 × 44 dont le libellé d'accessibilité nomme le fichier | créer |
| B12 | `PdlLegendRow` | Légende de tracés défilante, trait 14 × 3, entrée active en `text`/600 | créer |
| B13 | `PdlClimbRow` | Badge **plein** de catégorie + nom + plage + 3 colonnes chiffrées ; passage en `Wrap` au text scaling élevé | créer |
| B14 | `PdlStatCellRow` | `PdlCard(flat)`, cellules `Expanded` séparées d'un filet, valeur 17/600 + libellé `xs` + chevron si cliquable | créer |
| B15 | `PdlAvatarEditor` | Avatar 100 + bouton 44 × 44 en débord (`Stack(clipBehavior: none)`) + « Supprimer la photo » en `dangerOnSoft` | créer (dépend de `image_picker`) |
| B16 | `PdlDangerZone` | Filet, titre 16 px `dangerOnSoft`, conséquence, `PdlButton(danger, fullWidth)` ; **hors carte** | créer |
| B17 | `PdlRangeFilter` | Double poignée : rail 4 px, plage `primary`, poignées 20 px, bornes en `monoAxis` | créer |
| B18 | `PdlGalleryDots` | Points 6 px, actif pilule 18 × 6 blanche | créer |
| B19 | `PdlPriceBlock` | `PdlCard(flat)` + icône 24 px `accentOrange` + prix 20/700 | créer |
| B20 | `PdlBadgeStack` | Pile de `PdlBadge` alignée à droite, gap 4, ordre type → statut → visibilité | créer |
| B21 | `PdlSkeletonCard` | Composition `PdlCard` + `PdlSkeleton` : gabarits média 208 / ligne compacte 100 / personne 56 | créer (A1, A5) |
| B22 | `PdlDeadEndEmpty` | État vide « cul-de-sac » : terme et filtres cités littéralement, « Retirer le filtre X », « Tout réinitialiser », filet, puis 3 lignes compactes d'aperçu | **extraire** `features/routes/.../routes_page.dart:278-357` (`RoutesEmptyState` — le meilleur état vide de l'app) et généraliser |
| B23 | `PdlScopeSelector` | Sélecteur de portée (toutes mes équipes / une équipe / rôle minimum) rendu en chip ouvrant une `PdlSheet` | créer |
| B24 | `PdlMarkdownBody` | Rendu complet, feuille de style unique du §1.3.5 : p, h1-h3, listes, citation, code, **table à défilement horizontal**, image 160 px, **liens soulignés et actifs** | **étendre puis remplacer** `core/widgets/markdown_content.dart` |
| B25 | `PdlImageViewer` | Visionneuse plein écran : pincement, balayage vertical pour fermer, transition `Hero` | créer (dépend de `photo_view`) |
| B26 | `PdlMonthGrid` + `PdlDayHeader` | Grille 7 colonnes, gap 2, cellules 52 px rayon 8, week-ends teintés, 0 à 2 points de 5 px ; semaine lundi→dimanche **calculée**, jamais issue du premier jour de semaine système ; « aujourd'hui » et « inscrit » **cumulables** (fond plein + anneau intérieur blanc 2 px) | créer |

### 1.2.3 Vague C — coquilles d'écran (10, dépendent de A et B)

| # | Widget | Spécification |
|---|---|---|
| C1 | `PdlAppBar` | 56 px, padding `0 8 0 16`, boutons 44 × 44, bordure basse ; variante `overlay` → fond transparent, texte blanc, boutons `rgba(36,36,36,.55)` + flou σ 6, `SystemUiOverlayStyle.light`. **Aucun emplacement de notification** |
| C2 | `PdlBottomTabs` | 52 px + inset système, fond `overlay` + flou σ 12, bordure haute, libellé 10/500, icône 20. **Cinq entrées racine fixes, variante d'équipe supprimée** (§1.0.3-6) |
| C3 | `PdlPinnedToolbar` | `SliverPersistentHeader(pinned: true)` sous la `SliverAppBar(pinned: true)`, fond `overlaySolid` + flou σ 12, bordure basse ; **hauteur mesurée, jamais figée** |
| C4 | `PdlActionBar` | Barre d'action basse collante, `padding: 10 16 22`, fond `overlaySolid` + flou σ 12, bordure haute, 1 à 4 enfants |
| C5 | `PdlSheet` | Rayon 16 haut, ombre `sheet`, poignée 36 × 4, gabarit `Column[en-tête fixe, Expanded(ListView), pied fixe]`. **`PdlSheet.show()` force `useRootNavigator: true`, `isScrollControlled: true`, `useSafeArea: true`, `barrierColor: sheetBarrier`** ; l'appel direct à `showModalBottomSheet` est interdit |
| C6 | `PdlFullSheet` | Feuille plein rideau : `top: 128`, `initialChildSize: .85`, `maxChildSize: .94` ; **recouvre la barre d'onglets** |
| C7 | `PdlDetentSheet` | Feuille à crans `[.18, .5, .92]`, `snap: true`, avec pied fixe (`Positioned(bottom: 0)` du `Stack` interne) ; double composition d'en-tête pilotée par un `ValueListenableBuilder` **qui n'englobe que l'en-tête**. Remplace le `DraggableScrollableSheet` bridé à `maxChildSize: 0.7` de `route_detail_page.dart:137` |
| C8 | `PdlScreenScaffold` | Assemble C1-C4, applique la contrainte de largeur 600, garantit l'ordre de superposition contenu < barre d'outils < barre d'action < app bar / onglets < feuille |
| C9 | `PdlStageRail` | Rail horizontal collant de navigation frère-à-frère, h 64, pastilles bi-ligne, active en `primary` plein, **auto-scroll centré** (`scroll_to_index`, `AutoScrollPosition.middle`) ; troncature des noms par `characters`, jamais `substring` |
| C10 | `PdlPrevNextNav` | Deux blocs de 64 px séparés par des filets, le bloc « suivant » inversé |

### 1.2.4 Ce qu'il ne faut **pas** porter

Appareillage de planche : `PdlMapPlaceholder` (`.ph`), `.ph-cap`, `.blk`, `.demo-t`, `.states`,
`.sb`, `.spec`, `.detent`, `.sw`. À **supprimer** : `core/widgets/safe_network_image.dart`
(`SafeCircleAvatar` / `SafeDecorationImage` sont des doublons non authentifiés) et le
`SliverContentWidthConstraint` factice de `core/adaptive/content_width_constraint.dart:42-82`
(il ne contraint rien et piège).

### 1.2.5 Widgets métier — spécifiés ici, livrés par leur lot d'écran

Ils prennent un DTO, vivent donc dans leur feature et **n'introduisent aucune primitive
nouvelle** : `PublicationCard` (4 corps, réécriture de
`features/teams/.../publication_card.dart` en gardant le `switch` l. 21-26) · `RideGroupCard` ·
`RouteCard` / `CompactRouteRow` · `AgendaCard` · `StageCard` · `TeamDiscoveryCard` · `AdCard` ·
`CommentThread` · `EmbeddedRouteSheet` · `TeamHeader` · `NextRideCard` · `UpcomingCarousel`.
Chacun est décrit dans son lot au §2.

### 1.2.6 Tâches

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| F-CO-1 | ☑ | Créer le module et son contrat | `[C] core/pdl/pdl.dart` · `[C] core/pdl/README.md` | F-TH-5 | S | Le barrel compile ; le README énonce le contrat (jetons obligatoires, 44 px, deux modes, aucun littéral de couleur, aucune importation de `api/generated`) et la règle de revue associée |
| F-CO-2 | ☑ | Construire la vague A | `[C] core/pdl/*.dart` (un fichier par widget) · `[M] core/animations/shimmer_placeholder.dart`, `animated_card.dart` · `[M] core/widgets/authenticated_image.dart` · `[M→C] route_search_bar.dart` | F-CO-1, F-TH-6 | L | Une **galerie de composants** (`[C] mobile/lib/dev/pdl_gallery_page.dart`, route de debug non routée en production) rend les 20 primitives dans leurs variantes, en clair et en sombre ; un test de widget vérifie que `PdlChip`, `PdlButton(size: sm)`, `PdlSettingRow`, `PdlSegmented` et les actions de `PdlAppBar` mesurent **au moins 44 px** |
| F-CO-3 | ☑ | Construire la vague B | `[C] core/pdl/*.dart` · `[M] core/widgets/team_banner.dart` · `[M] core/animations/animated_empty_state.dart` · `[M] core/pagination/paged_list_footer.dart` · `[M→C] routes_page.dart` (extraction) | F-CO-2 | L | La galerie couvre les 26 ; `PdlEmptyState` rend ses 4 variantes ; `PdlPagedListFooter` affiche « 8 participants sur 40 · chargement de la suite... » et « Vous avez tout vu » en fin de liste |
| F-CO-4 | ☑ | Construire la vague C | `[C] core/pdl/*.dart` · `[M] core/adaptive/adaptive_scaffold.dart` (bascule `NavigationBar` ↔ `NavigationRail` à conserver) | F-CO-3, F-NA-2 | L | Un écran de démonstration empile `PdlAppBar` + `PdlPinnedToolbar` + liste longue + `PdlActionBar` + `PdlBottomTabs` : la barre d'outils **reste visible au défilement**, une `PdlSheet` ouverte depuis une page d'équipe recouvre la barre d'onglets, et le tout tient à `textScaleFactor = 1,3` sans débordement |
| F-CO-5 | ☑ | Supprimer les doublons | `[S] core/widgets/safe_network_image.dart` · `[M] core/widgets/widgets.dart` · `[M] core/adaptive/content_width_constraint.dart` | F-CO-3 | S | `flutter analyze` propre, aucune référence résiduelle |

---

## 1.3 Les briques techniques

### 1.3.1 Le profil altimétrique — `PdlElevationProfile`

**Décision (§1.0.3-4) : peinture maison.** Aucune bibliothèque de graphe n'est ajoutée, pour
trois raisons : la colorisation est **par segment** (chaque barre porte la couleur de sa propre
pente, ce qu'aucun `LineChart` ne fait) ; le rendu est un **histogramme jointif** de 64 à 76
barres à gap 1 px et sommet arrondi, soit une dizaine de lignes de `drawRRect` ; et la
réactivité doit être **découplée** — le réticule se repeint à chaque frame de glissement, les
barres ne doivent pas.

Architecture : **deux `CustomPaint` superposés dans un `Stack`**. Le painter du bas
(`_ProfileBarsPainter`) ne dépend que des données, son `shouldRepaint` est faux tant que le
profil ne change pas. Le painter du haut (`_CursorPainter`) reçoit
`repaint: ValueListenable<double?>` — la distance survolée — et ne dessine que le trait et
l'info-bulle.

Source : `GET /api/teams/{teamSlug}/routes/{routeSlug}/elevation-profile?samples=` →
`ElevationProfileDto{routeId, slug, distance, minElevation, maxElevation, samples, points}`,
`points: ElevationPointDto[]{distance, elevation, grade}`, distances **cumulées en mètres**,
`grade` en pourcent du segment se terminant sur le point (0 sur le premier). `samples` est borné
serveur à `2..1000` et **réduit au nombre de points réellement stockés** : ne jamais supposer
l'égalité avec la valeur demandée. Règle unique de demande (§1.0.3-5) :
`samples = (2 × largeur logique).clamp(60, 300)`, cache par `(routeSlug, samples)`, agrégation
client vers ~76 barres, la pente d'une barre étant la **moyenne pondérée par la distance** des
pentes qu'elle couvre. **Ce n'est plus un calcul depuis `tracks[].line`** : la géométrie complète
(plusieurs Mo) n'est plus téléchargée pour dessiner un profil.

| Propriété | Valeur |
|---|---|
| Hauteurs | **72** compact · **110** (écrans 12 et 24) · **140** (écrans 13 et 25) |
| Fond | `surfaceAlt`, rayon 8, clip |
| Barres | largeur `(w − (n−1)) / n`, gap 1 px, alignées en bas, sommet arrondi 1 px |
| Hauteur d'une barre | `lerp(0.10, 0.94, (elevation − min) / (max − min))` × hauteur |
| Couleur | `slopeColor(grade)`, `slopeNeutral` si la pente est absente |
| Axe | `monoAxis`, `space-between`, **5 graduations** aux quarts de `distance`, la dernière suffixée de l'unité |
| Réticule | trait 1 px `text` à 70 %, de haut en bas, **persistant après le relâchement** |
| Info-bulle | `top: 6`, centrée sur le réticule et **bornée aux bords**, fond `overlaySolid`, bordure 1 px, rayon 4, `monoAxis` |
| Contenu | **3 valeurs** : abscisse · altitude · pente — « 28,7 km · 74 m · 4,1 % », **dans l'unité choisie** |
| Interaction | `onTapDown` + `onHorizontalDragStart/Update/End` ; effacement au tap hors profil |
| Chargement | `PdlSkeleton` de la hauteur cible + « Chargement du profil altimétrique... » ; **le titre et les 3 statistiques s'affichent avant** |
| Échec du seul profil | ligne discrète « Profil indisponible » + « Réessayer ». **Pas d'écran d'erreur global pour un enrichissement** |

**Synchronisation avec la carte.** Le lien est une **distance cumulée**, jamais un index.
Profil → carte : la distance est convertie en position par `PolylineIndex` (cumul précalculé au
chargement, dichotomie + interpolation) et pousse les données d'une `GeoJsonSource` de point
**déjà installée** (`style.updateGeoJsonSource`) — jamais un ajout/retrait de couche, qui
provoque scintillement et fuite de couches. Carte → profil : `MapEventClick.point` →
`PolylineIndex.nearest` → même distance. Le `ValueNotifier<double?> cursorDistance` est le
**seul** état partagé ; il vit dans le `State` de l'écran, **pas** dans un provider Riverpod (on
ne reconstruit pas l'arbre à 60 fps).

### 1.3.2 La carte — `PdlMap`

`features/routes/.../route_map.dart` (276 l.) rend un tracé, une couleur, aucune interaction,
avec des styles VersaTiles **codés en dur** (l. 35-39) et un cadrage par `Future.delayed(100 ms)`.
Il devient un mince adaptateur puis disparaît.

Deux contraintes du SDK, **vérifiées sur `maplibre 0.3.5`**, déterminent l'architecture :

1. `MapController.queryLayers(Offset)` renvoie `QueriedLayer{layerId, sourceId, sourceLayer}`
   **sans les propriétés de l'entité** → l'identité passe par le `layerId`, donc **une
   `LineStyleLayer` par groupe ou par étape** (§1.0.3-7), toutes sur une même `GeoJsonSource`,
   chacune filtrée sur son `groupId`. Le tracé sélectionné est en `line-width: 8` /
   `line-opacity: 0.9`, les autres en `5` / `0.5` ; MapLibre n'ayant pas de `zIndex` sur les
   lignes, l'ordre de dessin place le sélectionné en dernier.
2. `VectorSource.maxZoom` vaut **2** par défaut : ne pas le régler produit une carte vide au-delà
   du zoom 2. Et `sourceLayer` se précise sur la **couche**, pas sur la source.

Cadrage : `fitBounds` déclenché sur `MapEventStyleLoaded` (plus de `Future.delayed`), bornes
issues de `GET …/routes/bounds` pour une liste et du calcul local pour un tracé unique ;
ouverture avant toute donnée sur `ConfigDto.defaultCenter`.

Styles de fond servis par `ConfigDto.mapStyles[]{id, label, url, darkVariant}` et
`tileServerBaseUrl` : le sélecteur liste les styles dans l'ordre servi, charge `darkVariant`
en sombre **s'il est non nul**, et mémorise le choix localement.

Marqueurs : départ `#40c057`, arrivée `#fa5252` (deux `CircleStyleLayer`, comme aujourd'hui),
plus une couche `symbol` pour `RouteDetailDto.waypoints[]` — **jamais lus à ce jour** —, avec
`symbol-sort-key` pour que les bornes kilométriques cèdent le pas aux points de passage nommés.
`_computeKmMarkers` et `fitBounds` de l'implémentation actuelle sont conservés.

**Tuiles `.mvt` : blocage.** Le contrat est explicite sur `/api/routes/tiles/{z}/{x}/{y}.mvt` —
« *fetched directly by the map renderer, so it authenticates with the session cookie rather than
a bearer token* ». Le mobile n'a pas de cookie : il porte un JWT injecté par `AuthInterceptor`
sur Dio, et MapLibre récupère ses tuiles par sa propre pile HTTP native. **La carte de masse
n'est donc pas réalisable en l'état.** Décision (§1.0.3-8) : livrer le **repli GeoJSON de
proximité** (liste paginée filtrée par `nearLat`/`nearLon`/`nearRadius`), plafonné à quelques
centaines de tracés, et laisser derrière un drapeau la bascule
`VectorSource(tiles: […], maxZoom: 14)` prête à être activée le jour où une URL de tuile
authentifiable existe. Les endpoints `routes/bounds`, eux, passent par Dio et fonctionnent.

**Voiles de lisibilité (brief §5).** Règle absolue : **jamais de texte posé directement sur une
tuile**. Sont livrés à cet effet `PdlScrim`, `PdlAppBar(overlay: true)`, `PdlMapButton` 44 × 44
sur `overlaySolid`, `PdlMapPill` (pilule opaque 14/600), `PdlMapButtonColumn` et
`PdlMapFloatingCard`. `SystemUiOverlayStyle.light` **dès qu'une carte est en fond**.

### 1.3.3 Unités, formatage, dates

Un seul point d'entrée public : **`AppFormatters`** (`core/utils/formatters.dart`), adossé à une
table de conversion `core/units/unit_system.dart`. Toute autre implémentation disparaît :
`features/routes/.../route_filter_labels.dart` y est fusionné, et les `km`/`m` codés en dur de
`route_detail_page.dart:186-197` tombent. Règles : espace insécable avant l'unité, séparateur de
milliers en espace fine (U+202F, que `NumberFormat.decimalPattern('fr')` produit déjà — `intl`
est une **dépendance directe** ajoutée par F-TE-1, jamais la transitive d'`easy_localization`), source
`UserDto.unitSystem` (`METRIC`/`IMPERIAL`) via `userPreferencesProvider`. Concerne distances,
D+, D−, `averageSpeed`, altitudes et pentes de l'info-bulle, **et les bornes des filtres**.

Dates : fuseau de l'appareil (§1.0.3-11), formats longs et relatifs (« il y a 3 jours »)
centralisés dans `AppFormatters`. Prix : `NumberFormat.currency(locale: 'fr_FR', symbol: '€',
decimalDigits: 2)` → `1 200,00 €`, la période étant un `TextSpan` séparé en 400 `textDimmed`
(« 25,00 € » en 600 puis « / semaine »), et `price == null` → « **Prix à négocier** » en 600,
jamais un tiret.

### 1.3.4 Erreurs, hors-ligne, bandeaux

`core/utils/api_error_handler.dart` est étendu : `DioExceptionType.connectionError`,
`connectionTimeout`, `sendTimeout` et `receiveTimeout` produisent un motif `offline` distinct de
`common.error` ; une exception **non-`DioException`** est journalisée avec son type réel plutôt
que repliée silencieusement (c'est le suspect résiduel de l'erreur nue « Erreur » à
l'inscription : un échec de désérialisation Freezed de `RideParticipationDto`,
`ride_repository.dart:69-79`). Règle transverse : toute erreur d'action se rend en **`PdlBanner`
persistant nommant l'objet fautif**, jamais en `SnackBar` de 4 s — le snackbar était masqué par
la barre d'onglets. Les pages déjà chargées restent affichées : `PagedListNotifier` ne vide pas
la liste sur échec de rafraîchissement.

### 1.3.5 Markdown et liens

`PdlMarkdownBody` remplace `core/widgets/markdown_content.dart`. Feuille de style unique :
racine 15 px / interligne 1.6 · `p` marge basse 14 · `h1/h2/h3` 20/700, 18/700, 16/700 ·
`ul` retrait 20, `li` marge 6 · citation filet gauche 3 px + `textDimmed` · `pre` sur
`surfaceAlt`, bordure, rayon 8, **défilement horizontal**, appui long = copier · code inline mono
12 sur `surfaceAlt` rayon 4 · **lien `link` souligné et actif** · image d'asset h 160 rayon 8,
plein écran au tap (`PdlImageViewer`) · **table** dans un conteneur bordé rayon 8 à
`overflow-x` **indépendant du défilement vertical**, largeur minimale 430 px, en-têtes 600 sur
`surfaceAlt`, cellules `nowrap`.

**Le défaut est fonctionnel, pas cosmétique** : `markdown_content.dart:101-107` instancie
`LinkConfig(style: …)` **sans `onTap`** ; `markdown_widget 2.3.2` n'attache alors aucun
`TapGestureRecognizer` — le lien est stylé mais inerte. Conjugué à l'absence d'`url_launcher`,
cela casse toutes les inscriptions annoncées dans les publications. Résolution des liens, dans
`core/utils/link_launcher.dart` : (1) URL du domaine courant reconnue par un motif de
`paths.generated.dart` → `context.push`, **pas** le navigateur ; (2) sinon `launchUrl(mode:
externalApplication)` ; (3) schéma inconnu ou lancement refusé → `PdlBanner(warn)` avec l'URL en
clair et une action « Copier le lien ». **Jamais d'échec silencieux.**

### 1.3.6 Dépendances à ajouter

Rappel impératif du dépôt : **toute API touchant à une donnée personnelle impose de mettre à jour
`mobile/ios/Runner/PrivacyInfo.xcprivacy` ET `mobile/store-metadata/data-safety.md` dans le même
commit**. Une sous-déclaration est un motif de rejet en revue App Store.

| Paquet | Version | Motif | Impact déclaratif |
|---|---|---|---|
| `url_launcher` | ^6.3 | Liens markdown, `webcal://` du calendrier, OAuth Strava et GPS, `mailto:` | Aucune donnée collectée. iOS : `LSApplicationQueriesSchemes` (`webcal`, `mailto`, `tel`) ; Android : `<queries>`. **Pas** de `PrivacyInfo` |
| `shared_preferences` | ^2.3 | Miroir local du thème, de la langue, des unités, du fond de carte et de la densité de liste, pour que le premier cadre soit correct | Aucune donnée ne quitte l'appareil. **Ne jamais y écrire de jeton** (le refresh token reste en `flutter_secure_storage`) |
| `image_picker` | ^1.1 | Avatar (`POST /api/users/me/avatar`) | `NSPhotoLibraryUsageDescription` ; `PrivacyInfo` → `NSPrivacyCollectedDataTypePhotosorVideos`, `Linked: true`, `AppFunctionality` ; `data-safety.md`. **Vérifier l'existant avant d'ajouter un doublon** |
| `geolocator` | ^13 | « Autour de moi » → `nearLat`/`nearLon`/`nearRadius`, déjà supportés par l'API. Préféré à `location` : permission intégrée et `LocationAccuracy.low` réellement honorée | **Donnée personnelle.** `NSLocationWhenInUseUsageDescription` (fr + en) ; Android `ACCESS_COARSE_LOCATION` **seule** ; `PrivacyInfo` → `NSPrivacyCollectedDataTypeCoarseLocation`, `Linked: false`, `Tracking: false` ; `data-safety.md` « localisation approximative · filtre de proximité · non conservée » |
| `photo_view` | ^0.15 | Visionneuse plein écran (couverture, galerie, images markdown) | Aucun |
| `scrollable_positioned_list` | ^0.3 | Défilement de l'agenda jusqu'à un jour donné | Aucun |
| `scroll_to_index` | ^3.0 | Centrage automatique du rail d'étapes | Aucun |
| `intl` | ^0.20 | `NumberFormat.decimalPattern`/`.currency` et les formats de date d'`AppFormatters` (§1.3.3). Aujourd'hui seulement **transitive** via `easy_localization` : dépendre d'une transitive est une panne qui attend une montée de version | Aucun |

**Écartés** : `fl_chart` (§1.3.1) · `shimmer` (implémentation maison déjà là) ·
`permission_handler` (redondant avec `geolocator`, qui expose aussi `openAppSettings`) ·
`flutter_svg` + jeu Tabler (§1.0.3-10) · `package:timezone` (§1.0.3-11) ·
`firebase_messaging` / `flutter_local_notifications` (le push n'est pas livré côté serveur).
**À retirer** : `hooks_riverpod`, `riverpod_annotation`, `riverpod_generator` — vérifiés sans
aucun usage dans `lib/`.

### 1.3.7 Tâches

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| F-TE-1 | ☑ | Ajouter les dépendances et leur déclaratif | `[M] pubspec.yaml` · `[M] ios/Runner/Info.plist` · `[M] ios/Runner/PrivacyInfo.xcprivacy` · `[M] android/app/src/main/AndroidManifest.xml` · `[M] store-metadata/data-safety.md` | — | M | `flutter pub get`, `flutter build ios` et `flutter build appbundle` passent ; les trois déclarations (Info.plist, PrivacyInfo, data-safety) sont cohérentes et modifiées **dans le même commit** ; les trois dépendances mortes ont disparu |
| F-TE-2 | ☑ | Créer le système d'unités et le formateur unique | `[C] core/units/unit_system.dart` · `[M] core/utils/formatters.dart` · `[M] features/routes/.../route_filter_labels.dart` · `[M] assets/l10n/{fr,en}.json` | F-TH-7 | M | `formatDistance(46300, IMPERIAL)` rend `28.8 mi`, `(46300, METRIC)` rend `46,3 km` avec espace insécable ; `formatElevation(9840)` rend `9 840 m` avec espace fine ; tests sur les deux systèmes et les deux locales ; `grep -rn "'km'" mobile/lib` ne renvoie plus que `AppFormatters` |
| F-TE-3 | ☑ | Étendre la gestion d'erreur au hors-ligne | `[M] core/utils/api_error_handler.dart` · `[M] assets/l10n/{fr,en}.json` | — | S | Les quatre types de timeout/connexion renvoient un motif `offline` distinct ; une exception non-`DioException` est journalisée avec son type |
| F-TE-4 | ☑ | Créer l'index de distance de polyligne | `[C] core/geo/polyline_index.dart` | — | M | `fromLineStrings` expose `positionAt(mètres)` et `nearest(lngLat) → mètres` ; la 4ᵉ composante `G3DM` est utilisée quand elle porte le cumul, sinon haversine ; tests : `positionAt(0)` = départ, `positionAt(distance)` = arrivée, `nearest` à 5 m du tracé rend la bonne distance à ±10 m, total à ±0,5 % de `RouteDetailDto.distance` |
| F-TE-5 | ☑ | Créer le client et le provider du profil | `[C] features/routes/data/elevation_profile_repository.dart` · `[M] api/pedalons_api_client.dart` | — | S | `FutureProvider.family` **autoDispose** clé `(teamSlug, routeSlug, samples)` ; aucun écran ne télécharge `tracks[].line` pour dessiner un profil |
| F-TE-6 | ☑ | Créer `PdlElevationProfile` | `[C] core/pdl/elevation/pdl_elevation_profile.dart`, `elevation_bars_painter.dart`, `elevation_cursor_painter.dart`, `elevation_samples.dart` | F-CO-2, F-TH-3, F-TE-5 | L | Test de painter sur 300 points synthétiques (pente 0 → `hsl(85,…)`, pente ≥ 18 % → `hsl(255,…)`) ; le glissement **ne repeint pas** `_ProfileBarsPainter` (compteur d'appels à `paint` ou `debugRepaintRainbowEnabled`) ; info-bulle bornée aux bords |
| F-TE-7 | ☑ | Consommer `ConfigDto` | `[C] core/config/config_provider.dart` · `[M] app.dart` (préchargement) | F-CO-3 | M | `mapStyleProvider` (style × luminosité → URL) et `defaultCenterProvider` exposés ; `minSupportedAppVersion` comparé à `package_info_plus` alimente un `PdlBanner(warn)` ; changer `mapStyles` côté serveur change le sélecteur **sans livrer une version** |
| F-TE-8 | ☑ | Réécrire `RouteMap` en `PdlMap` | `[C] core/pdl/map/pdl_map.dart`, `pdl_map_controller.dart`, `pdl_map_buttons.dart`, `pdl_map_hero.dart` · `[M] features/routes/.../route_map.dart` (adaptateur puis suppression) | F-CO-2, F-TE-7 | L | Un écran de démonstration rend 10 tracés colorisés ; taper un tracé sélectionne l'entrée correspondante et réciproquement ; le cadrage se fait sur `MapEventStyleLoaded` ; plein écran et sélecteur de fond fonctionnent ; **aucune URL de style codée en dur** |
| F-TE-9 | ☑ | Livrer la couche de masse et son repli | `[C] core/pdl/map/pdl_mass_layer.dart` | F-TE-8 | M | Le repli GeoJSON de proximité rend les parcours de la zone visible ; la bascule `VectorSource` existe derrière un drapeau ; un commentaire de tête documente le blocage d'authentification et l'évolution attendue |
| F-TE-10 | ☑ | Livrer `PdlMarkdownBody` et le routeur de liens | `[C] core/utils/link_launcher.dart` · `[M→C] core/widgets/markdown_content.dart → core/pdl/pdl_markdown_body.dart` · `[C] core/pdl/pdl_image_viewer.dart` | F-TE-1, F-TE-7, F-CO-3 | L | Lien interne → route interne, lien externe → navigateur, lien non lançable → bandeau ; **aucun lien inerte** ; une table de 4 colonnes défile horizontalement sans faire déborder la page, emoji préservés ; bloc de code copiable ; image plein écran zoomable avec transition `Hero` |
| F-TE-11 | ☑ | Créer le service de localisation | `[C] core/geo/location_service.dart` | F-TE-1, F-CO-3 | M | `coarsePosition()` rend `null` sans lever ; un refus de permission produit un `PdlBanner` explicatif avec « Ouvrir les réglages » (`Geolocator.openAppSettings`), **jamais un dialogue système répété** ; l'écran reste utilisable |

---

## 1.4 La navigation

### 1.4.1 Le silo, et pourquoi il ne peut pas rester

`mobile/lib/config/router.dart` déclare **deux `ShellRoute` mutuellement exclusives** :
`MainShell` (l. 473, 4 destinations globales — Accueil, Équipes, Calendrier, Profil) et
`TeamShell` (l. 502, 2 à 5 destinations d'équipe construites par `buildTeamDestinations`).
Entrer dans une équipe **remplace** la barre globale. Les maquettes montrent 5 onglets fixes sur
toutes les planches, y compris d'équipe. Deux contraintes interdisent de simplement supprimer
`TeamShell` :

1. **Les hiérarchies de retour.** `_deepLinkHierarchies` (l. 64-135) et `ancestorsForDeepLink()`
   (l. 156) reconstruisent une pile d'ancêtres pour qu'un lien froid ait un retour cohérent ;
   `test/deep_link_hierarchy_test.dart` les protège.
2. **Le routeur est construit une seule fois.** `routerProvider` (l. 408) ne doit **jamais**
   `ref.watch` l'authentification : les changements passent par `refreshListenable` et ne
   relancent que `redirect`. Reconstruire le `GoRouter` détruirait la pile arrière que le
   gestionnaire de deep link de `main.dart:52-167` vient d'assembler.

### 1.4.2 Structure cible

Une seule coquille, cinq onglets fixes, et l'équipe cesse d'être une coquille pour redevenir
**du contenu**.

```
GoRouter (root navigator)
├── auth, vérification d'appareil, pages légales                      (hors coquille)
├── StatefulShellRoute.indexedStack        ← remplace les DEUX ShellRoute
│   │   builder: PdlBottomTabs à 5 entrées fixes
│   ├── branche 0 · Accueil     /
│   ├── branche 1 · Équipes     /equipes
│   │                           /equipes/decouvrir                    [nouveau]
│   │                           /equipes/:teamSlug                    → TeamHomePage
│   │                           /equipes/:teamSlug/{calendrier,parcours,annonces,a-propos}
│   │                           /equipes/:teamSlug/membres            [nouveau]
│   │                           /equipes/:teamSlug/pages/:pageSlug    [nouveau]
│   ├── branche 2 · Calendrier  /calendrier
│   ├── branche 3 · Parcours    /parcours, /parcours/carte            [nouveau : mobile:false aujourd'hui]
│   └── branche 4 · Profil      /profil
└── détails plein écran, parentNavigatorKey: _rootNavigatorKey        (INCHANGÉ)
    sorties, parcours, publications, voyages, étapes, annonces
```

Cinq points de conception, par ordre de risque :

1. **`indexedStack` et non `ShellRoute`** : chaque onglet garde **sa propre pile**. Entrer dans
   une équipe, basculer sur Calendrier, revenir sur Équipes restitue l'équipe — ce qu'aucune des
   deux coquilles actuelles ne sait faire.
2. **L'arbre d'équipe migre sous la branche Équipes sans changer une seule URL.**
   `_teamShellTree(locale)` et `_underTeam()` (l. 219-390) dérivent déjà les segments depuis
   `PathVariants` ; seul leur point de greffe change. **`contracts/routes.yaml` n'est pas touché
   pour les routes existantes** — ni les deeplinks, ni l'`apple-app-site-association`, ni la
   section deeplink d'`AndroidManifest.xml` ne bougent.
3. **`_deepLinkHierarchies` reste la source de vérité du retour et gagne des entrées** : les
   onglets d'équipe, portés implicitement par `TeamShell`, deviennent des routes de branche et
   doivent déclarer leur ancêtre ; les nouvelles routes aussi. Le test existant est le garde-fou —
   **l'étendre avant de migrer**, pas après.
4. **La navigation interne à une équipe devient du contenu** : les sections (Fil, Calendrier,
   Parcours, Annonces, À propos) passent dans une `PdlChipRow` collante sous l'en-tête d'équipe,
   alimentée par `buildTeamSections` (ex-`buildTeamDestinations`, qui doit enfin intégrer
   Annonces) selon `enableRides/Trips/Routes/Ads/Posts` et le rôle. Cela supprime d'un coup la
   seconde `NavigationBar`, la cause de F-DE-8 et la superposition d'états d'erreur de
   `_TeamTabPageWrapper` / `TeamShell`.
5. **`getDestinationIndex` teste du plus spécifique au plus général** : `/equipes/:slug/parcours`
   doit rester sur **Équipes** et non basculer sur **Parcours**, avec Accueil (`/`) en repli.
   L'icône d'Équipes est tranchée une fois pour toutes (la planche Fondations utilise `i-group`,
   les écrans 33/34 `i-users` — **une seule** est retenue).

### 1.4.3 Tâches

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| F-NA-1 | ☑ | Étendre le test de hiérarchie de deep link **avant** toute migration | `[M] test/deep_link_hierarchy_test.dart` | — | S | Le test couvre 100 % des entrées de `_deepLinkHierarchies`, onglets d'équipe compris, et passe sur le code actuel |
| F-NA-2 | ☑ | Migrer vers `StatefulShellRoute.indexedStack` à 5 branches | `[M] config/router.dart` · `[M] core/adaptive/navigation_destination.dart` · `[M] features/navigation/.../main_shell.dart` (`navigationShell.goBranch`) · `[S] features/teams/.../team_shell.dart`, `team_navigation_destination.dart` | F-NA-1 | L | F-NA-1 passe toujours ; les 5 onglets sont présents depuis n'importe quel écran d'équipe ; basculer d'onglet et revenir restitue la pile de chaque branche ; `test/list_pages_primary_scroll_test.dart` et `status_bar_scroll_to_top_test.dart` passent |
| F-NA-3 | ☑ | Déclarer les routes manquantes au contrat | `[M] contracts/routes.yaml` (`teamsDiscover` **et `teamMembersPublic`** nouveaux — l'entrée `teamMembers` existante est la route d'administration `/equipes/{teamSlug}/admin/membres`, elle ne doit **pas** passer à `mobile: true` ; `allRoutes`, `allRoutesMap`, `teamPage` passent à `mobile: true`) puis **`pnpm generate-routes` depuis `frontend/`** · `[M] config/router.dart` | F-NA-2 | M | `git diff` montre les 4 fichiers générés modifiés **par le script seul** ; F-NA-1 étendu aux 5 nouvelles routes passe ; un lien froid vers `/equipes/n-peloton/membres` a « équipe » puis « mes équipes » comme ancêtres |
| F-NA-4 | ☑ | Transformer la navigation d'équipe en contenu | `[C] features/teams/.../team_home_page.dart` (rebâti depuis `team_detail_page.dart`, **514 lignes déjà écrites et routées nulle part**) · `[M] team_feed_page.dart` · `[M] config/router.dart:547-565` (suppression de `_TeamTabPageWrapper` et de son `SizedBox.shrink()` d'erreur) | F-NA-2, F-CO-4 | L | Un deeplink vers une section d'équipe n'affiche plus deux états d'erreur superposés ni d'écran blanc ; **un seul propriétaire** de l'état de chargement et d'erreur : la page ; l'onglet global Équipes est surligné pour toute URL sous `/equipes/` |

---

## 1.5 La dette du brief §2.10

Huit défauts nommés, plus les deux seuls `// TODO` de `lib/`. Chacun a un fichier et une
correction ; tous sont structurellement résolus par les composants ou les champs de la 1.3.0.

| # | État | Tâche | Défaut et correction | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|---|
| F-DE-1 | ☑ | Poser un voile sous les titres d'app bar à vignette | Titre `onSurface` posé directement sur une tuile ou une photo → image surmontée de `PdlScrim(top)` 150 px et `PdlScrim(bottom)` 90 px, titre blanc via `PdlAppBar(overlay)` + `SystemUiOverlayStyle.light` | `[M] features/rides/.../ride_detail_page.dart:233-253` · `[M] features/trips/.../trip_detail_page.dart:165-177` · `[M] features/teams/.../team_sliver_app_bar.dart` (`expandedHeight: 160 → ≤ 120`) | F-CO-2, F-CO-4 | S | Capture des deux écrans en clair et en sombre, titre lisible sur vignette claire ; `expandedHeight ≤ 120` partout |
| F-DE-2 | ☑ | Rendre opaques les pastilles posées sur la carte | `surface.withValues(alpha: 0.8)` ne suffit pas sur une tuile chargée → `PdlMapPill` (95 %) et `PdlMapButton` | `[M] features/routes/.../route_detail_page.dart:97-126` | F-TE-8 | S | Opacité relevée ≥ 0,95 ; titre lisible sur une tuile satellite |
| F-DE-3 | ☑ | Réparer la rangée de chips coupée | La 4ᵉ chip est tronquée par le bord droit sans signal ; `route_filter_chips_bar.dart` fige la hauteur à 40 px, ce qui casse au text scaling → `PdlChipRow` partout (fondu 28 px, hauteur mesurée) | `[M] features/feed/.../publication_feed_view.dart:203-233` · `[M] features/routes/.../route_filter_chips_bar.dart:36-40` | F-CO-3 | S | À 4 chips un fondu apparaît à droite ; à `textScaleFactor = 1,3` aucune chip n'est rognée |
| F-DE-4 | ☑ | Épingler la rangée de filtres du fil | Les chips vivent dans un `SliverToBoxAdapter` et sortent définitivement de l'écran → `PdlPinnedToolbar`, motif déjà employé par `routes_page.dart:68-74` ; y monter aussi le champ de recherche, absent du fil | `[M] features/feed/.../publication_feed_view.dart:72-85` | F-CO-4 | S | Défiler le fil jusqu'en bas laisse la rangée visible ; `test/list_pages_primary_scroll_test.dart` passe toujours (le scrollable primaire ne reçoit **pas** de `controller:`, sinon le tap sur la barre d'état iOS cesse de remonter la liste) |
| F-DE-5 | ☑ | Réparer « Trier par » écrasé à 1 pt | Dernier enfant d'un `Column` dans `Flexible > SingleChildScrollView`, sous un parent `MainAxisSize.min`, avec un CTA collant hors du scroll → gabarit `PdlSheet` : `Column[en-tête fixe, Expanded(ListView), CTA fixe]` | `[M] features/routes/.../route_filter_sheet.dart:81,100-189,333-366` | F-CO-4 | M | Avec tous les filtres actifs et `textScaleFactor = 1,3`, « Direction du vent » et « Trier par » gardent leurs 52 px et restent atteignables |
| F-DE-6 | ☑ | Rendre les liens markdown actifs | Voir §1.3.5 | couvert par F-TE-10 | F-TE-10 | — | Critère de F-TE-10 |
| F-DE-7 | ☑ | Réparer la participation à une sortie | Quatre défauts : `AlertDialog` de choix de groupe aveugle (l. 85-170), détection d'inscription par parcours de `participants[]` (l. 482-488), désinscription par force brute en boucle (l. 172-209), erreur nue en snackbar masqué (l. 122-127). **Tous résolus par les champs 1.3.0** : l'action passe dans la carte de groupe, `registered`/`registeredGroupId`/`full` remplacent les heuristiques, `leaveGroup(registeredGroupId)` est un appel unique, l'erreur devient un `PdlBanner` nommant le groupe. Ajouter le `ref.invalidate` absent de la branche `catch` | `[M] features/rides/.../ride_detail_page.dart` · `[M] features/rides/data/ride_repository.dart` · `[M] core/utils/api_error_handler.dart` | F-CO-3, lot 2 | M | **Trois défauts sur quatre corrigés hors lot 2** : détection par `registered`, désinscription par `leaveGroup(registeredGroupId)` en un appel, échec en `PdlBanner` nommant le groupe, `ref.invalidate` ajouté aux deux branches `catch`. **Soldé** : S12-3 et S12-8 ont supprimé l'`AlertDialog` avec la page de la v1 ; le choix de groupe est la carte elle-même |
| F-DE-8 | ☑ | Faire passer les feuilles au-dessus de la barre d'onglets | Cause unique : `showModalBottomSheet` appelé **sans `useRootNavigator: true`** → `PdlSheet.show()` | `[M] features/routes/.../route_filter_sheet.dart:20,36,202` · `[M] features/profile/.../profile_page.dart:258` · `[M] features/routes/.../route_detail_page.dart:297` | F-CO-4 | S | `grep -rn "showModalBottomSheet" mobile/lib` ne renvoie plus que `pdl_sheet.dart` |
| F-DE-9 | ☑ | Enrichir les états vides | Icône + une phrase, sans titre nominal, sans action, sans distinction vide absolu / vide filtré → `PdlEmptyState` avec la micro-copie du brief §5 | `[M] features/feed/.../publication_feed_view.dart:139-161` · `[M] features/ads/.../ads_page.dart:40-62` · `[M] features/calendar/.../calendar_page.dart:136-156` · `[M] features/teams/.../teams_page.dart:41-71` | F-CO-3 | M | Les cinq écrans distinguent vide absolu et vide filtré ; le vide filtré propose « Effacer la recherche » |
| F-DE-10 | ☑ | Tuer les deux `// TODO` | `teams_page.dart:34` (loupe visible en permanence, sans effet **et sans `tooltip`** — violation directe de l'accessibilité) et `:63` (CTA d'état vide sans effet). La plomberie existe déjà (`getPublicTeams`, `joinTeam`, `leaveTeam`) ; il manque l'écran (lot 5), la route et l'entrée de hiérarchie | `[M] features/teams/.../teams_page.dart` | F-NA-3, lot 5 (S34-3) | S | `grep -rn "TODO" mobile/lib --include=*.dart \| grep -v generated` ne renvoie rien |
| F-DE-11 | ☑ | Solder les dettes annexes | Annonces non paginées (`ads_page.dart:19`) → lot 5 · N+1 sur les pages d'équipe (`team_about_page.dart:89-103`) → lot 6 · 404 non traduite (`router.dart:519-536`, seul endroit non i18n) · `baseDioProvider` force `Accept-Language: fr` (`api/pedalons_api_client.dart:23`) → lire la locale courante · `logoutAll()` jamais câblé → lot 6 | `[M] config/router.dart` · `[M] api/pedalons_api_client.dart` · `[M] assets/l10n/{fr,en}.json` | F-CO-3 | M | Les trois chaînes françaises en dur du routeur passent par `errors.notFound.*` ; l'en-tête `Accept-Language` suit la locale ; les autres points sont couverts par leurs lots |

**État livrable en fin de lot 1** — l'application est à la charte en clair et en sombre, navigue
sur cinq onglets fixes avec ses deeplinks intacts, ses feuilles passent au-dessus de la barre
d'onglets, ses liens markdown fonctionnent, ses états vides sont nominaux, et une galerie de
composants documente la bibliothèque. Aucun écran n'a encore sa structure v2.

---

# 2. Les écrans, lot par lot

Chaque lot a un objectif, des prérequis satisfaits par les lots précédents, et laisse
l'application dans un état cohérent et livrable. Tout lot suppose le lot 1 (§1) livré ; la
dépendance n'est réécrite que lorsqu'elle porte sur un composant précis et bloquant.

## Lot 2 — Les trois écrans hero : sortie, parcours, accueil

**Objectif** : porter la promesse de la version — réparer l'inscription à une sortie, donner
enfin une carte et un profil altimétrique aux parcours, et transformer l'accueil rétrospectif en
« qu'est-ce que je fais à vélo cette semaine ».

**Prérequis** : §1 en entier, en particulier `PdlElevationProfile` (F-TE-6), `PdlMap` (F-TE-8),
`PolylineIndex` (F-TE-4), `PdlMarkdownBody` (F-TE-10), les vagues A à C.

**Ordre interne : 12 → 13 → 11.** L'écran 12 porte le défaut le plus grave du produit
(participation en échec) ; l'écran 13 réutilise son profil et sa carte et produit
`EmbeddedRouteSheet`, réutilisé plus tard par l'écran 25 ; l'accueil vient en dernier parce qu'il
consomme les cartes et les providers des deux autres.

### 2.1 Écran 12 — Détail de sortie et inscription à un groupe

`features/rides/presentation/pages/ride_detail_page.dart` (522 l.) est **réécrit
intégralement** : rien n'est conservé — ni le `SliverAppBar` sans voile (l. 233-253), ni les
groupes en `ListTile`, ni l'`AlertDialog` de choix de groupe (l. 133-170), ni le `_joinRide`
aveugle (l. 85-131), ni le `_leaveRide` en force brute (l. 172-209), ni
`_isCurrentUserParticipant` (l. 482-488). `ride_repository.dart` perd `getTeamRides`
(reconstruction manuelle d'un `RideDto` depuis un `PublicationDtoRide`, l. 20-60, dont le seul
appelant est une page morte) et gagne `getRide`, `joinGroup`, `leaveGroup` et les commentaires.

**Structure verticale.** (1) Hero cartographique 210 px — aperçu statique multi-tracés,
`PdlScrim` haut et bas, `PdlAppBar(overlay)` avec retour, titre, partage et « Ajouter à mon
calendrier ». (2) Identité : `PdlTeamLine`, titre 22/700, `PdlBadgeStack` (statut, visibilité,
et « ✓ Inscrit · *nom du groupe* » si `registered`). (3) Bandeaux conditionnels. (4) Bloc méta :
`PdlCard(flat)` en grille 2 × 2 — date/heure, participants + « Voir la liste », départ (vert),
arrivée (rouge). (5) Carte interactive `clamp(260, 44 % de la hauteur, 460)` + `PdlMapPill` du
groupe sélectionné + `PdlLegendRow` + `PdlElevationProfile(110)`. (6) **Groupes** : compteur,
bandeau d'erreur d'inscription, `RideGroupCard` × 4 puis « Voir les N autres groupes ».
(7) Description (`PdlMarkdownBody`). (8) Commentaires.

**`RideGroupCard`** — quatre lignes : trait de couleur + nom + badge + **une seule action
pleine** ; `PdlStatRow(nowrap)` heure · vitesse · **distance** · **D+** (jamais coupée, repli en
deux lignes au text scaling) ; avatars + « X/Y participants » + « Voir tout », remplacés par
`PdlProgressBar(full)` si `full` ; actions texte « Voir le parcours », « GPX », « FIT »,
« Envoyer vers l'appareil ». **Meneur** : quand `RideGroupDto.leader` est non nul, la carte rend
une ligne « Meneur » avec l'avatar 24 px et le `displayName` du `PublicUserDto` (repli initiales
si `avatarUrl` est nul) ; quand `leader` est nul, **rien n'est rendu** — ni ligne, ni libellé, ni
emplacement réservé — et c'est le **cas courant**, pas un cas dégradé : la plupart des groupes
n'auront pas de meneur désigné. Le bouton se dérive **sans heuristique** grâce à la v2 :

```
group.registered           → PdlButton(outline, sm) « Quitter »
appel en cours             → PdlButton(outline, sm, loading) « Adhésion... »
group.full                 → PdlButton(disabled, sm) « Complet »       (non focalisable)
sortie passée ou CANCELLED → aucun bouton
non-membre                 → aucun bouton (bandeau à la place)
sinon                      → PdlButton(fill, sm) « Rejoindre »
```

**Données.** `ridesClient.getRide` rend un `RideDto` aux `groups[]` peuplées avec `registered`,
`registeredGroupId`, `full`, `commentCount`, et chaque `RideGroupDto` porte `registered`, `full`,
`distance`, `elevationGain` — **tous nouveaux en 1.3.0**, et tous décisifs : ils remplacent le
parcours de `participants[]` (vide sans droit d'accès), l'inférence de saturation, la
désinscription par boucle et N+1 `getRoute` pour la ligne 2. Les tracés viennent de
`routesClient.getRoute(simplify: 15, points: 1500)` **dédoublonné par `routeSlug`** (10 groupes
partageant 3 parcours = 3 appels). Le profil du groupe sélectionné vient d'`elevation-profile`.
Les commentaires sont paginés (`page`, `size`, `sort`, `parentId`, `itemTotal`, `replyCount`).

**Le point critique du lot : l'inscription.** `RideRegistrationController`, un `Notifier` par
`(teamSlug, rideSlug)`, détient le `RideDto` courant, `pendingGroupId` et `failure`. Séquence :
(1) si un autre groupe a `registered == true`, **on n'appelle pas** — on pose directement le
bandeau d'exclusivité (l'API renverrait 400) ; (2) mutation optimiste locale ; (3) `joinGroup` ;
(4) succès → invalidation du détail **et** du provider « prochaine sortie » de l'accueil ;
(5) échec → **rollback complet** puis bandeau. Le `PdlBanner(danger)` s'insère **en tête de la
section Groupes**, reste affiché jusqu'à une nouvelle tentative ou un tap sur sa croix, et
**nomme le groupe fautif** : « **Inscription impossible à {groupe}.** Vous êtes déjà inscrit dans
un autre groupe. » (avec action inline « Quitter {groupe actuel} » qui enchaîne les deux appels),
« **Groupe complet.** », « **Réservé aux membres de l'équipe.** », ou le motif générique avec
« Réessayer ». **Aucun `showSnackBar` et aucune `AlertDialog` de choix de groupe** : le choix
est la carte elle-même.

**Sélection croisée.** Un unique `selectedGroupId` (initialisé à
`registeredGroupId ?? groups.first.id`) pilote simultanément `RideGroupCard(selected:)`,
`PdlMapPill`, `PdlLegendRow`, l'épaisseur et l'opacité des tracés, et la source du profil. Tap
sur un tracé → sélection ; tap sur une carte → sélection + `Scrollable.ensureVisible` sur la
carte si elle est hors écran. Transition 180 ms.

**États** : chargement (hero + 4 blocs de squelettes) · erreur · introuvable (404) · hors-ligne ·
**non-membre** (bandeau « Rejoignez cette équipe pour participer aux sorties. » + « Voir
l'équipe » ; détection par 403 `FORBIDDEN` sur `joinGroup` **ou**, en préventif, `RideDto.team`
absent de `myTeamsProvider`) · **passée** (`dateTime < now` → badge « Terminée », aucune
inscription, actions texte conservées) · **annulée** (bandeau rouge pleine largeur, aucune
action **y compris pour un inscrit** : on n'offre pas « Quitter » sur une sortie annulée) ·
sans parcours · sans groupe.

**Limites assumées** : le meneur de groupe est **livré** (§1.0.2, `RideGroupDto.leader`), mais son
rendu reste **conditionnel** — la plupart des groupes n'en désignent pas, et un `leader` nul
n'affiche rien plutôt que de se replier sur `createdBy` ; le logo d'équipe reste absent
de `PdlTeamLine` car la sortie porte un `TeamPublicationDto`, qui n'a pas `logoUrl` (seul
`TeamDetailDto` l'a) — repli sur les initiales teintées ; le statut « Terminée » est dérivé
client (`RideDto.isPast`, **une seule extension** partagée par les trois écrans, sensible à
l'horloge de l'appareil) ; aucune liste d'attente, « Complet » est terminal.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S12-1 | ☑ | Reprendre le repository et créer les providers de détail | `[M] features/rides/data/ride_repository.dart` (suppression de `getTeamRides`) · `[C] features/rides/providers/ride_detail_provider.dart` | F-TE-3 | S | `rideDetailProvider((teamSlug, rideSlug))` rend un `RideDto` aux `groups[]` peuplées ; `getTeamRides` n'a plus d'appelant |
| S12-2 | ☑ | Construire `RideGroupCard` et ses six états de bouton | `[C] features/rides/presentation/widgets/ride_group_card.dart` | F-TE-2, F-CO-3 | M | Les 6 états sont dérivés de `registered` et `full` **sans parcourir `participants[]`** ; la ligne de statistiques ne se coupe jamais et bascule en deux lignes à `textScaleFactor ≥ 1,3` ; golden test des 6 états en clair et en sombre |
| S12-3 | ☑ | Écrire le contrôleur d'inscription : bascule optimiste et bandeau persistant | `[C] features/rides/providers/ride_registration_controller.dart`, `.../widgets/ride_groups_section.dart` · `[M] assets/l10n/{fr,en}.json` | S12-1, S12-2 | L | Rejoindre bascule le bouton en < 16 ms sans attendre le réseau ; un `GROUP_FULL` produit le bandeau nommant le groupe **et restaure** l'état ; un `ALREADY_REGISTERED` propose « Quitter {groupe} » en action inline ; `grep -rn "showSnackBar" mobile/lib/features/rides` ne renvoie rien ; test de widget sur les trois motifs. Le grep est vert depuis la réécriture de la page en S12-8 |
| S12-4 | ☑ | Construire la carte multi-tracés et la sélection croisée | `[C] features/rides/presentation/widgets/ride_groups_map.dart`, `.../providers/ride_group_selection_provider.dart` | F-TE-4, F-TE-8 | L | Une `GeoJsonSource` et **une couche par groupe** (`ride-track-{groupId}`) ; les `routeSlug` sont dédoublonnés avant appel ; tap sur un tracé sélectionne la carte de groupe et réciproquement ; hauteur `clamp(260, 44 %, 460)` ; la sélection change les propriétés de deux couches, elle ne reconstruit pas la carte |
| S12-5 | ☑ | Brancher le profil du groupe sélectionné | `[C] features/routes/providers/route_elevation_provider.dart` | S12-4, F-TE-6 | S | Le profil est rechargé au changement de sélection et mis en cache par `routeSlug` ; le squelette 110 px n'empêche pas le reste de l'écran d'être interactif |
| S12-6 | ☑ | Livrer le fil de commentaires paginé | `[C] features/comments/presentation/widgets/comment_thread.dart`, `.../providers/comment_thread_provider.dart`, `.../data/comment_repository.dart` | F-CO-3 | L | Page 0 de 20 commentaires de premier niveau, `itemTotal` alimente le compteur, `replyCount > replies.length` déclenche un appel `parentId` à la demande ; **un seul niveau de réponse** (indentation 14 px, filet gauche 2 px) ; composeur **unique** en zone multiligne 72 px + bouton (les deux formes maquettées sont unifiées sur celle-ci) ; « Supprimer » visible pour l'auteur seul ; dates relatives longues |
| S12-7 | ☑ | Livrer la feuille Participants | `[C] features/participants/presentation/widgets/participants_sheet.dart` | F-CO-4 | S | Ouverte par `PdlSheet.show` / `PdlFullSheet` — visible **au-dessus** de la barre d'onglets ; recherche **côté client** (les participants sont embarqués, non paginés) ; le titre porte le total simple, pas de pied « N sur M » ; la pastille « Organisateur du groupe » est **conditionnelle** — `organizerFlag` vaut `leader?.id == participant.id`, donc **rien n'est rendu** quand `RideGroupDto.leader` est nul (cas courant) ou quand le meneur ne participe pas au groupe, et **jamais** de repli sur `createdBy` ; un test couvre les trois cas (pas de meneur, meneur participant, meneur non participant) |
| S12-8 | ☑ | Assembler l'écran, le hero et les bandeaux d'état | `[M→réécrit] features/rides/presentation/pages/ride_detail_page.dart` | S12-1…S12-7, F-TE-10 | L | Les 8 sections sont en place ; annulée, passée, non-membre, sans parcours et sans groupe rendent les bandeaux et états prévus ; aucun texte n'est posé sans voile sur une tuile ; recette en clair **et** en sombre |

### 2.2 Écran 13 — Fiche parcours

`route_detail_page.dart` (502 l.) est **restructuré, pas réécrit** : le motif `Stack` = carte
plein écran + feuille glissante est le meilleur socle de l'app. Sont remplacés les crans
(`0.15/0.1/0.7` → `PdlDetentSheet` `[0.18, 0.5, 0.92]`), la pastille de titre à 80 % d'opacité
(l. 108-126), le bouton retour (l. 97-106), les `_StatItem` (→ `PdlStat(big)`) et la feuille de
téléchargement (l. 297). Sont ajoutés le profil, les cols, les usages, les informations et la
barre d'actions collante.

**Structure** : carte plein écran en fond (tracé colorisé par pente, bornes kilométriques,
départ/arrivée, **points de passage nommés** et marqueur de réticule) ; overlay haut opaque ;
feuille à 3 crans chevauchant la carte de 20 px, contenant titre + badges (**au cran 0.18 :
titre 16 px + badge de revêtement seul + 3 statistiques compactes** — seconde composition isolée
dans son propre widget), 3 `PdlStat(big)` (Distance, D+ vert, D− rouge affiché négatif),
`PdlElevationProfile(140, réticule)`, « Cols et montées (N) », « Utilisée dans », informations,
description ; barre d'actions collante en pied **de la feuille** (GPX plein, FIT contour, icône
appareil, partage). Pas de barre d'onglets : l'écran est hors coquille.

**Données.** `getRoute(simplify: 5, points: 3000)` — les paramètres `simplify`/`points` bornent
enfin la géométrie ; `elevation-profile` remplace le calcul client ; `getRouteUsages`,
`tracks[].climbs[]`, `waypoints[]` et `createdBy` sont **des endpoints et champs existants que
cet écran active pour la première fois** ; `ConfigDto.mapStyles` remplace les URL VersaTiles
codées en dur et donne le sélecteur de fond.

**Réticule bidirectionnel** : voir §1.3.1. Le curseur est un `ValueNotifier<double?>` de distance
en mètres tenu par l'écran, **pas un provider**.

**Limites** : `RouteUsageDto` n'a pas de date de fin → un usage de type voyage affiche la date de
début seule ; les montées ne sont pas nommées (`ClimbDto` n'a pas de `name`) → « Montée N »
dérivé de l'index, à ne pas inventer ; le serveur concatène les pistes pour le profil, **le
client doit concaténer dans le même ordre** pour que `PolylineIndex` et
`ElevationProfileDto.distance` coïncident ; pas de chargement « simplifié puis complet » — un
seul appel.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S13-1 | ☑ | Étendre le repository et les providers du parcours | `[M] features/routes/data/route_repository.dart` · `[C] features/routes/providers/route_usages_provider.dart` | F-TE-3 | S | `getRouteDetail(simplify: 5, points: 3000)`, `getElevationProfile` et `getUsages` exposés ; le poids de la réponse est mesuré avant/après sur un parcours de 150 km |
| S13-2 | ☑ | Coloriser le tracé, rendre les points de passage et les bornes | `[M] features/routes/.../route_map.dart` (adaptateur de `PdlMap`) | S13-1, F-TE-8 | M | Tracé colorisé par `grade` selon `slopeColor` ; `waypoints[]` rendus en `symbol` avec `symbol-sort-key` ; `_computeKmMarkers` conservé ; plus aucune URL de style en dur |
| S13-3 | ☑ | Synchroniser le réticule profil ↔ carte | `[C] features/routes/presentation/widgets/route_cursor_controller.dart` · `[M] route_map.dart` | F-TE-4, F-TE-6, S13-2 | L | Le glissement sur le profil déplace le marqueur de carte et l'inverse ; **les barres ne sont pas repeintes** ; l'info-bulle affiche km · altitude · pente dans l'unité choisie ; la distance coïncide avec `ElevationProfileDto.distance` à ±0,5 % |
| S13-4 | ☑ | Livrer la feuille à crans et sa double composition d'en-tête | `[C] features/routes/.../route_sheet_header.dart` · `[M] route_detail_page.dart` | F-CO-4 | M | `[0.18, 0.5, 0.92]`, `snap: true` ; au cran 0.18 le titre passe à 16 px et les statistiques à 14 px ; le `ValueListenableBuilder` n'englobe **que** l'en-tête (vérifié au `Timeline`) ; la feuille se lit entièrement au cran 0.92 |
| S13-5 | ☑ | Livrer les sections Cols, Utilisée dans et Informations | `[C] features/routes/.../route_climbs_section.dart`, `route_usages_section.dart` | S13-1, F-CO-3 | M | Badges de catégorie **pleins** (HC raisin → Cat.4 vert, Cat.3 à texte sombre) ; « via les groupes … » et « via l'étape … » dérivés de `RouteUsageDto.type` ; un usage voyage affiche la date de début seule, avec la limite commentée |
| S13-6 | ☑ | Extraire la fiche parcours embarquée | `[C] features/routes/presentation/widgets/embedded_route_sheet.dart` | S13-3, S13-5 | L | Bloc complet (en-tête, carte 240 px, profil 140 px, 3 statistiques, 3 exports, cols) **réutilisable tel quel par l'écran 25** ; toute divergence de rendu entre 13 et 25 est un défaut |
| S13-7 | ☑ | Poser la barre d'actions collante et l'overlay opaque | `[M] route_detail_page.dart` | S13-4 | M | La barre reste visible à tous les crans ; la pastille de titre est **opaque** ; `SystemUiOverlayStyle.light` + voile haut ; la feuille de téléchargement passe par `PdlSheet.show` |

### 2.3 Écran 11 — Accueil : aujourd'hui et à venir

`home_page.dart` devient un `CustomScrollView` à six slivers : (1) `SliverAppBar(pinned,
floating, snap)` 56 px — wordmark, avatar 32 px, **sans cloche** ; le dégradé de 120 px et la
carte passkey conditionnelle disparaissent. (2) « Ma prochaine sortie » : `NextRideCard` à média
208 px, badges, `PdlTeamLine`, titre, date longue, groupe + heure, lieu de départ, statistiques,
avatars + barre de places, « Voir la sortie » (plein) et « Se désinscrire » (contour) — **sliver
absent si aucune participation à venir**, remplacé par une carte compacte. (3) Rangée « À
venir » : carrousel de cartes 280 px, masque de bord 24 px. (4) `PdlPinnedToolbar` : recherche
debouncée + `PdlChipRow` (portée puis 5 types). (5) En-tête de fil + `SliverList.separated`
(gap 8) de `PublicationCard` polymorphes. (6) `PdlPagedListFooter`.

**Données** : `listMyParticipations(from: now, status: PUBLISHED, size: 1, view: COMPACT)` — c'est
l'endpoint qui rend le bloc possible — puis **un unique** `getRide` sur ce seul item pour obtenir
`groups[]` et en extraire celui dont l'`id` vaut `registeredGroupId` (`name`, `time`,
`distance`, `elevationGain`, avatars, places). Rangée « À venir » :
`listAllPublications(from, to: +30 j, size: 10, view: COMPACT)` filtré client sur sorties et
voyages. Fil : `listAllPublications` paginé en vue compacte + `countAllPublications`.

**Règle du bouton de carrousel**, entièrement calculable côté client :

```
ride.registered            → PdlBadge « ✓ Inscrit », pas de bouton
ride.full                  → PdlButton(disabled) « Complet »
ride.groupCount == 1       → PdlButton(fill, pill) « Rejoindre »
sinon                      → PdlButton(outline, pill) « Choisir un groupe »
```

« Rejoindre » **ne peut pas** s'inscrire directement : la ligne de liste ne porte pas `groups[]`
(le backend passe `List.of()` dans `RideDto.fromListItem`), l'`id` du groupe unique est donc
inconnu. Le bouton navigue vers l'écran 12 avec `autoJoin: true`, qui déclenche l'inscription dès
le détail chargé **si et seulement si** il y a bien un seul groupe non plein ; l'erreur retombe
dans le bandeau de la section Groupes. C'est un aller simple, pas une boîte de dialogue.

**Limites** : la vignette 16:9 est celle de la sortie, pas du parcours du groupe
(`RideGroupDto` n'a pas de `thumbnailUrl`) ; le compteur « 2/48 » du carrousel devient
« N participants » faute de somme des `maxParticipants` en liste ; l'erreur du bloc « prochaine
sortie » **masque le bloc** sans rien afficher — c'est un enrichissement, il ne doit pas casser
l'accueil.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S11-1 | ☑ | Créer les providers « Ma prochaine sortie » et « À venir » | `[C] features/home/providers/next_ride_provider.dart`, `upcoming_provider.dart` | S12-1 | M | Un `listMyParticipations` suivi d'un **unique** `getRide` qui **réutilise `rideDetailProvider`** — le tap « Voir la sortie » n'entraîne donc aucun rechargement ; un échec masque le bloc sans propager d'erreur |
| S11-2 | ☑ | Réécrire `PublicationCard` (4 corps) | `[M→réécrit] features/teams/.../publication_card.dart` · `[S] features/rides/.../ride_card.dart` | F-TE-2, F-CO-3 | L | Les 4 types sont rendus depuis le `switch` conservé ; l'extrait vient d'**`excerpt`** et non de `media.markdown` ; la vignette de `thumbnailUrl` ; `commentCount` affiché ; badge « ✓ Inscrit » dès `registered` ; hauteurs de média 208/160/140/120 figées en constante partagée ; golden tests clair et sombre |
| S11-3 | ☑ | Construire `NextRideCard` | `[C] features/home/presentation/widgets/next_ride_card.dart` | S11-1, S11-2 | M | Nom du groupe, heure, `distance`, `elevationGain`, avatars, barre de places ; « Se désinscrire » utilise `registeredGroupId` (**jamais une boucle**) et pose un `PdlBanner` en cas d'échec ; l'absence de participation rend la carte compacte « Aucune sortie à venir » avec « Explorer » |
| S11-4 | ☑ | Construire `UpcomingCarousel` | `[C] features/home/presentation/widgets/upcoming_carousel.dart` | S11-1, S11-2 | M | Cartes 280 px, masque de bord 24 px (`ShaderMask(dstIn)`) ; la règle de bouton est appliquée ; « Rejoindre » navigue avec `autoJoin: true` ; le voyage affiche `endDate`, `totalDistance`, `totalElevationGain` — impossible en 1.2.0 |
| S11-5 | ☑ | Épingler la barre d'outils du fil | `[C] features/home/presentation/widgets/feed_toolbar.dart` · `[M] features/feed/providers/publication_feed_provider.dart` | F-CO-4, F-DE-3 | M | La barre **ne sort jamais de l'écran** ; aucune chip coupée sans fondu ; la clé de famille intègre `search`, `minRole`, `status`, `participating` — un changement crée un notifier neuf, il ne mute pas l'ancien ; le `BackdropFilter` est mesuré sur une liste de 200 items (pas de chute sous 55 fps) |
| S11-6 | ☑ | Rendre le fil paginé en vue compacte | `[M→réécrit] features/feed/.../publication_feed_view.dart` en `SliverPublicationFeed` · `[M] features/teams/.../team_feed_page.dart` | S11-2, S11-5 | M | `view: COMPACT` envoyé ; `countAllPublications` alimente « N publications » ; **5** squelettes ; vide absolu et vide filtré distincts ; le contrat `PagedListNotifier` (test existant) est intact ; **`team_feed_page.dart`, second appelant, fonctionne toujours** |
| S11-7 | ☑ | Assembler l'accueil | `[M→réécrit] features/home/presentation/pages/home_page.dart` | S11-1…S11-6 | M | Les 6 slivers dans l'ordre ; app bar 56 px sans dégradé et sans cloche ; pull-to-refresh invalidant les 3 providers ; `test/list_pages_primary_scroll_test.dart` et `status_bar_scroll_to_top_test.dart` passent toujours ; recette clair et sombre |
| S11-8 | ☑ | Passer l'accessibilité et la micro-copie des trois écrans | tous les fichiers du lot · `assets/l10n/{fr,en}.json` | S11-7, S12-8, S13-7 | M | Toute icône-action porte un `Semantics(label:)` ; aucune cible < 44 px ; les 3 écrans tiennent à `textScaleFactor = 2,0` sans débordement ; le lexique du brief §5 est respecté (sortie, parcours, groupe, dénivelé positif, revêtement, montée) ; `fr.json` et `en.json` ont le même jeu de clés ; les 3 écrans sont capturés dans les 4 combinaisons clair/sombre × métrique/impérial |

**État livrable en fin de lot 2** — l'inscription à une sortie fonctionne et s'explique quand elle
échoue, les parcours ont carte, profil et cols, l'accueil répond à la question du brief. Le reste
de l'app est à la charte mais garde ses structures d'écran actuelles.

---

## Lot 3 — Exploration : parcours et calendrier

**Objectif** : ouvrir les deux surfaces racine que la barre à cinq onglets vient de créer —
l'exploration de parcours (aujourd'hui atteignable seulement depuis une équipe) et un calendrier
qui montre enfin un mois.

**Prérequis** : lot 1 (routes déclarées par F-NA-3, `PdlMap` et son repli de masse, `PdlMonthGrid`,
`PdlDeadEndEmpty`, `PdlScopeSelector`, service de localisation) ; du lot 2, rien de bloquant —
**ce lot peut avancer en parallèle du lot 2** dès que F-CO-4 et F-TE-9 sont livrés.

### 3.1 Écran 21 — Parcours : liste, carte et proximité

`routes_page.dart` (547 l.) devient une coquille ; la liste et la carte deviennent deux vues d'un
même jeu de filtres. `route_filters.dart` (209 l.) s'étend (portée, proximité, tri, vue) ;
`route_list_provider.dart` conserve son `PagedListNotifier` et sa clé de famille, étendue.

**Structure** : `PdlAppBar` « Parcours » ; `PdlPinnedToolbar` — en vue Liste, recherche +
`PdlFilterButton` | `PdlScopeSelector` | `PdlSegmented(Liste|Carte)` | `PdlChipRow` ; en vue
Carte, recherche + `PdlFilterButton` | `PdlSegmented` seulement. Vue Liste : ligne de compteur +
`PdlSegmented` de densité, puis `SliverList` de `CompactRouteRow` (100 px) ou de `RouteCard`
(média 208 px). Vue Carte : `PdlMapHero` occupant le reste de l'écran, `PdlMapPill` « Rechercher
dans cette zone », `PdlMapButtonColumn` (plein écran, fond, « Autour de moi »), groupe de
proximité (pastille + chips 10/25/50 km), `PdlMapFloatingCard` à la sélection.

Un seul `routeFiltersProvider` alimente les deux vues ; la vue est un état distinct, persisté. La
vue Liste est enveloppée d'un `AutomaticKeepAliveClientMixin` + `PageStorageKey` pour conserver
son offset au retour de la carte **et au retour d'un détail**. Densités : vignettes par défaut
sous 200 résultats, compact au-delà ; le choix manuel écrase la règle et est persisté, sauf
franchissement du seuil qui rebascule une fois avec un `PdlBanner` d'information.

**Écart maquette assumé** : en vue Carte, la maquette supprime le sélecteur de portée et la barre
de chips. On conserve la suppression (budget vertical) mais le compteur du `PdlFilterButton`
devient **obligatoire** dès qu'un filtre est actif, portée comprise — c'est le seul témoin
restant.

**Données** : `GET /api/routes` et `/api/teams/{teamSlug}/routes` avec l'intégralité des filtres
(`search`, `surfaceType`, `hilliness`, bornes de distance et de dénivelé, `windDirection`,
`minRole`, `nearLat`/`nearLon`/`nearRadius`/`nearType`, `view`, `page`, `size`) ;
`GET …/routes/count` alimente « 412 parcours sur 2 585 » (**deux** appels : filtres courants et
filtres vides) et le CTA « Voir N parcours » de la feuille, debouncé 350 ms avec annulation de la
requête précédente ; `GET …/routes/bounds` cadre la carte **avant le premier build** et n'est
jamais rejoué sur changement de filtre.

**Règle de choix du filtre à retirer** dans l'état « cul-de-sac » (la maquette n'en propose qu'un
alors que deux sont actifs) : pour chaque filtre actif, un appel `count` avec ce seul filtre
levé ; on propose celui qui **maximise** le total, à égalité le plus récemment ajouté. **Au plus
un** bouton, en plus de « Tout réinitialiser », et ces appels ne partent que lorsque la liste est
vide.

**Limites** : tuiles `.mvt` non authentifiables → repli GeoJSON (§1.3.2) ; pagination par offset →
deux pages peuvent dupliquer un élément si un parcours est créé entre-temps,
`PagedListNotifier` déduplique déjà par `itemKey` ; `RouteDto` n'expose pas `hilliness`, la chip
« Vallonné » est un filtre serveur sans restitution possible sur la carte de résultat — ne pas
l'afficher.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S21-1 | ☑ | Étendre le modèle de filtres et sa clé de famille | `[M] features/routes/domain/route_filters.dart`, `providers/route_list_provider.dart` | F-NA-3 | M | `RouteFilters` porte portée (`minRole`, `teamSlug`), proximité et tri ; changer un filtre crée un notifier neuf, l'ancien est éliminé |
| S21-2 | ☑ | Brancher le compteur serveur | `[C] features/routes/providers/route_count_provider.dart` | S21-1 | S | « N parcours sur M » et le CTA de la feuille viennent de `routes/count`, debounce 350 ms, requête précédente annulée |
| S21-3 | ☑ | Construire la barre d'outils épinglée | `[C] features/routes/presentation/widgets/routes_toolbar.dart` · `[M] routes_page.dart` | S21-1, F-CO-4 | M | La barre ne disparaît jamais au défilement ; la 4ᵉ chip n'est plus coupée ; `textScaleFactor = 1,3` sans débordement |
| S21-4 | ☑ | Livrer les deux densités de liste | `[C] features/routes/presentation/widgets/route_card.dart`, `compact_route_row.dart` · `[M] routes_page.dart` | F-CO-3 | M | Bascule instantanée, choix persisté, compact forcé au-delà de 200 résultats, **position de défilement conservée au retour d'un détail** |
| S21-5 | ☑ | Livrer la vue carte multi-tracés | `[C] features/routes/presentation/widgets/routes_map_view.dart` | S21-1, F-TE-9 | L | Cadrage initial issu de `routes/bounds`, figé au montage ; tap sur un tracé ouvre la carte flottante ; « Rechercher dans cette zone » n'apparaît qu'après déplacement ; le plafond du repli GeoJSON est signalé à l'utilisateur, pas silencieux |
| S21-6 | ☑ | Livrer la proximité « Autour de moi » | `[C] features/routes/presentation/widgets/routes_near_me_controls.dart` · `[M] route_filters.dart` | F-TE-11, S21-5 | M | Permission refusée définitivement → bandeau + « Ouvrir les réglages » ; les 3 rayons rechargent **la liste et la carte** |
| S21-7 | ☑ | Reprendre la feuille de filtres | `[M] features/routes/.../route_filter_sheet.dart` | F-DE-5, S21-2 | M | « Trier par » et « Direction du vent » ne sont plus écrasés à 1 pt ; la feuille recouvre la barre d'onglets ; le CTA affiche le compte serveur |
| S21-8 | ☑ | Généraliser l'état « cul-de-sac » | `[M] core/pdl/pdl_dead_end_empty.dart` · `[M] routes_page.dart` | F-CO-3, S21-2 | M | Composant paramétré (requête, filtres actifs, filtre suggéré, aperçu de 3 lignes) **réutilisé tel quel par l'écran 32** ; la règle de suggestion ci-dessus est implémentée |

### 3.2 Écran 22 — Calendrier : mois et agenda

`calendar_page.dart` (459 l.) ne rend qu'une liste par jour. `_findFirstMonthWithEvents()`
(l. 58) sonde jusqu'à 6 mois consécutifs — comportement **conservé et réutilisé** pour l'action
« Aller au prochain mois avec une sortie ».

**Structure** : `PdlAppBar` + bouton pilule « Aujourd'hui » ; `PdlPinnedToolbar` (‹ « Juillet
2026 » › · `PdlScopeSelector` · `PdlChipRow`) ; `SliverPersistentHeader` rétractable portant
`PdlMonthGrid` (334 px max, 0 min, opacité interpolée) + légende ; `SliverList` d'agenda
(`PdlDayHeader` + `AgendaCard` ≈ 92 px) ; bloc « Abonnement calendrier ». Sans la compression au
défilement, deux cartes d'agenda seulement sont visibles au premier paint.

**Correctifs de maquette actés** : « aujourd'hui » et « inscrit » sont **cumulables** (fond plein
+ anneau intérieur blanc 2 px) là où la maquette les rend exclusifs ; **une seule couleur
d'étape**, celle de `PublicationType.trip` (`#12b886` / `#099268`), le `#40c057` restant réservé
au marqueur de départ cartographique, légende alignée ; le jour sélectionné (interaction non
matérialisée par la maquette) prend `primarySoft` ; le badge « Annulé », oublié par la maquette,
est ajouté depuis `CalendarEventDto.status`.

**Données** : `GET /api/calendar/events?from&to` et sa variante d'équipe. Les champs
**nouveaux** `distance`, `elevationGain`, `groupName`, `registered`, `startPlaceName`, `status`,
`thumbnailUrl` **suppriment le `getRide` par événement** — c'est le gain principal de l'écran.
Les étapes de voyage étaient déjà renvoyées (`TRIP_STAGE`) : le manque était le rendu.
Sémantique des chips (non dite par la maquette) : **type exclusif** (Tout | Sorties | Voyages)
et **équipe exclusive** (Toutes mes équipes | une équipe), les deux groupes se combinant,
séparés par un filet vertical ; la chip d'équipe **change d'endpoint**, elle ne filtre pas côté
client.

**Jeton ICS — règles non négociables.** `GET /api/calendar/token` rend
`CalendarTokenDto{token, globalFeedUrl, teamFeedUrlTemplate}`. L'URL est affichée **masquée**
(`https://…/api/calendar/••••••••.ics`) : le jeton n'apparaît ni à l'écran, ni dans une capture,
ni dans un log. `SelectableText` interdit ; la copie passe par `Clipboard.setData` avec l'URL
**réelle**. « S'abonner » lance `webcal://` en application externe, avec repli sur la copie +
bandeau si aucun gestionnaire. « Régénérer » passe par une confirmation en question fermée
(« Régénérer le lien ? » / « L'ancien lien cessera immédiatement de fonctionner sur tous vos
appareils. »). Le jeton n'est **jamais** stocké hors mémoire ; il est relu à chaque ouverture du
bloc.

**Limite** : pas de pagination sur `/api/calendar/events` — on charge le mois entier, ce qui est
acceptable.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S22-1 | ☑ | Livrer la grille de mois | `[M] core/pdl/pdl_month_grid.dart` | F-CO-3 | M | 35 cellules pour juillet 2026, week-ends teintés, jours hors mois sans point, « aujourd'hui » **et** « inscrit » cumulés ; test unitaire sur la composition de 4 mois dont un février bissextile |
| S22-2 | ☑ | Reprendre le modèle et le repository du mois | `[M] features/calendar/data/calendar_repository.dart` · `[C] features/calendar/providers/calendar_month_provider.dart` | — | M | Un seul appel par mois et par portée ; **aucun appel de détail par événement**, vérifiable au journal réseau |
| S22-3 | ☑ | Construire la carte d'agenda | `[C] features/calendar/presentation/widgets/agenda_card.dart` | S22-2, F-TE-2 | M | Filet à la couleur du type, ligne « équipe · heure · lieu », distance et D+, badge « Inscrit · *groupe* », badge « Annulé », opacité 0,55 pour le passé |
| S22-4 | ☑ | Assembler grille et agenda | `[M] features/calendar/presentation/pages/calendar_page.dart` | S22-1, S22-3 | L | Tap sur un jour défile l'agenda jusqu'à ce jour (`scrollable_positioned_list`) et le marque sélectionné ; « Aujourd'hui » ramène au mois et au jour courants ; grille rétractable par chevron **et** compressée au défilement ; ouverture sur le mois courant, agenda positionné sur aujourd'hui, ou sur le prochain jour renseigné avec une carte informative |
| S22-5 | ☑ | Construire la barre d'outils | `[C] features/calendar/presentation/widgets/calendar_toolbar.dart` | S22-2, F-CO-4 | M | Chips de type et d'équipe combinables selon la règle ci-dessus, barre épinglée, fondu de bord ; vide filtré distinct avec « Effacer les filtres » |
| S22-6 | ☑ | Livrer le bloc d'abonnement ICS | `[C] features/calendar/presentation/widgets/calendar_subscription_card.dart` | F-TE-1 | M | Jeton **jamais rendu en clair** (revue de code + capture d'écran) ; le presse-papiers contient l'URL réelle ; `webcal://` avec repli ; régénération confirmée |
| S22-7 | ☑ | Aligner la section calendrier d'équipe | `[M] config/router.dart`, page de section d'équipe | S22-4, F-NA-4 | S | La section d'équipe rend le même écran avec la portée figée sur l'équipe et un en-tête cohérent avec les autres sections |

**État livrable en fin de lot 3** — les cinq onglets ont tous un contenu de niveau v2 sauf Profil
et Équipes ; l'exploration de parcours existe enfin hors d'une équipe, avec carte et proximité.

---

## Lot 4 — Voyages : vue d'ensemble et étape

**Objectif** : donner au voyage la même densité qu'à la sortie, et sortir l'écran d'étape de son
état d'écran le plus pauvre de l'app.

**Prérequis** : lot 2 (l'étape consomme `EmbeddedRouteSheet` de S13-6 et le voyage consomme
`CommentThread` de S12-6) et lot 1 (`PdlStageRail`, `PdlElevationProfile`, `PdlMap`).

### 4.1 Écran 24 — Voyage

`trip_detail_page.dart` (414 l.) a un `SliverAppBar` de 200 px **sans voile**, un bandeau
d'équipe, des cartes date / participants / étapes / parcours / description et une barre basse.
Ni carte, ni distance totale, ni D+ cumulé, ni date de fin, ni profil, ni commentaires.

**Structure** : `PdlMapHero` 210 px (carte statique du tracé global) + voiles + `PdlAppBar(overlay)` ;
identité ; **carte plate de synthèse** en grille 2 colonnes (Dates / Étapes / Distance totale /
Dénivelé cumulé) + filet + rangée participants cliquable ; bloc « Tracé du voyage » —
`PdlMap` 280 px interactif, `PdlMapPill` « J1 · Clermont-Ferrand → Issoire »,
`PdlMapButtonColumn`, `PdlLegendRow` J1…J7 ; `PdlElevationProfile(110)` **sans réticule** ;
« Étapes » + `StageCard` × N ; description ; participants en pastilles `PdlBadge(lg)` ;
`CommentThread` ; `PdlActionBar` collante — « Ne plus participer » en **contour**, « Participer »
en plein (une seule action pleine par écran).

**Données** : `TripDto.endDate`, `totalDistance`, `totalElevationGain`, `registered`,
`commentCount` et `TripStageDto.stageIndex`/`stageCount` sont **tous nouveaux** et rendent le
bloc de synthèse possible ; `null` se rend « — », **jamais « 0 »**. Palette d'étape :
`kMultiTrackPalette[stageIndex % 10]`, indexée sur le rang, pas sur la géographie ; au-delà de
10 étapes, cyclage — la maquette ne dit rien de ce cas.

**Limites** : pas d'endpoint carte multi-entités → le tracé global impose **N appels
`getRoute(simplify, points)`**, un par étape ; dégradation retenue : chargement parallèle
plafonné à 4 requêtes simultanées, `simplify` agressif, **carte statique en hero pendant que la
carte interactive se remplit étape par étape dans l'ordre**, et au-delà de **12 étapes** on ne
charge que les 12 premières en signalant « tracé partiel » sous la carte. Même contrainte pour le
profil global : N appels `elevation-profile` dont le budget d'échantillons est **réparti
proportionnellement à la distance de chaque étape** (total ≈ 240 points) puis concaténé, chaque
segment décalé de la distance cumulée ; si une étape échoue, le profil est rendu **avec un trou
signalé**, pas masqué. Participants embarqués et non paginés → feuille complète, recherche
client. « Ajouter à mon calendrier » : aucun endpoint ICS par publication → l'action ouvre le
bloc d'abonnement de l'écran 22, et le bouton disparaît pour un non-membre.

**États** non couverts par la maquette et **ajoutés** : voyage annulé (bandeau rouge pleine
largeur) et voyage passé. Voyage sans étape ou sans tracé → état vide, carte et profil
**masqués** plutôt que rendus vides.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S24-1 | ☑ | Poser le hero avec voile et app bar transparente | `[M] features/trips/.../trip_detail_page.dart` | F-DE-1 | M | Aucun texte posé sur la tuile ; contraste vérifié en clair et en sombre |
| S24-2 | ☑ | Construire le bloc de synthèse | `[C] features/trips/presentation/widgets/trip_summary_card.dart` | F-TE-2 | S | Les 4 cellules viennent d'`endDate`, `stageCount`, `totalDistance`, `totalElevationGain` ; `null` rendu « — » |
| S24-3 | ☑ | Construire la carte multi-étapes colorisée | `[C] features/trips/presentation/widgets/trip_map.dart` | F-TE-8, S24-1 | L | Une couleur par rang d'étape (modulo 10) ; **une couche par étape** ; sélection croisée carte ↔ liste ↔ légende **sans reconstruire la carte** ; plafond de 12 étapes signalé ; concurrence plafonnée à 4 requêtes |
| S24-4 | ☑ | Assembler le profil altimétrique global | `[C] features/trips/presentation/widgets/trip_elevation.dart` | F-TE-6, S24-3 | M | Concaténation de N profils avec décalage de distance, axe en quarts de `totalDistance`, trou signalé si une étape échoue |
| S24-5 | ☑ | Construire la carte d'étape | `[C] features/trips/presentation/widgets/stage_card.dart` | F-TE-2 | M | `PdlNumberPill` + nom + trait de couleur + date + « départ → arrivée » + statistiques + `PdlThumb 80` + chevron ; état sélectionné |
| S24-6 | ☑ | Brancher la barre d'action et la participation | `[M] trip_detail_page.dart` | S24-2, S12-6 | M | Bascule optimiste, état « Inscription... », non-membre → bandeau, **`registered` fait foi** (plus de déduction sur `participants[]`) ; annulé et passé rendus |

### 4.2 Écran 25 — Étape

`stage_detail_page.dart` (251 l.) charge le voyage puis sélectionne l'étape par `slug`. **Il n'y
a pas d'endpoint d'étape dédié, et ce n'est pas un problème** : le rail d'étapes a de toute façon
besoin de la liste complète.

**Structure** : `PdlAppBar` opaque (« ← Retour au voyage | J1 | Partager ») ; **`PdlStageRail`**
collant sous la barre (pastille « Aperçu » puis une pastille bi-ligne par étape) ; contexte
(`PdlTeamLine` à icône remontant **au voyage**, titre = nom de l'étape, badges « Étape 1 sur 7 »
et statut) ; carte plate (pastille numérotée, date longue, « Départ groupé à 09:00 »,
`PdlPlaceRow` départ et arrivée **avec adresse** — `PlaceDetailDto.address` n'est jamais lue
aujourd'hui) ; **`EmbeddedRouteSheet`** (en-tête + « Voir les détails du parcours › », carte
240 px, profil 140 px **avec réticule**, 3 statistiques, exports GPX/FIT/appareil, cols et
montées) ; description.

**Limites** : **pas de commentaires d'étape** — le contrat n'expose de commentaires que sur les
publications, sorties, parcours et voyages ; la section n'est pas rendue et un bouton texte
« Commenter ce voyage » renvoie à l'écran 24. Pas de nom de col → « Montée N ». La maquette
n'affiche ni badge de revêtement ni badge de visibilité sur le parcours embarqué : écart mineur
accepté. Ligne de col non tapable (aucune interaction maquettée).

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S25-1 | ☑ | Livrer le rail d'étapes collant | `[M] core/pdl/pdl_stage_rail.dart` · `[M] features/trips/.../stage_detail_page.dart` | F-CO-4, F-TE-1 | L | Rail épinglé sous l'app bar, pastille active **centrée automatiquement**, tap = remplacement d'écran **sans empilement** ; troncature sûre des noms longs par `characters` (test avec emoji et accents) |
| S25-2 | ☑ | Rendre le bloc lieux avec adresse | `[M] stage_detail_page.dart` | F-CO-3 | S | `address` affichée sous le nom ; absente → la ligne se réduit sans espace vide |
| S25-3 | ☑ | Intégrer la fiche parcours embarquée | `[M] stage_detail_page.dart` | S13-6 | M | Le bloc est celui de l'écran 13, **sans divergence de rendu** ; exports fonctionnels ; « Voir les détails du parcours » ouvre l'écran 13 |
| S25-4 | ☑ | Assembler l'écran étape | `[M] stage_detail_page.dart` | S25-1…S25-3 | M | Retour = voyage ; badge « Étape N sur M » ; états sans parcours et introuvable rendus ; squelettes (4 pastilles de rail + carte + deux blocs) |

**État livrable en fin de lot 4** — le voyage et l'étape sont au niveau des écrans hero ;
`EmbeddedRouteSheet` est prouvé partagé.

---

## Lot 5 — Contenus et personnes : publication, annonces, membres et découverte

**Objectif** : rendre accessible ce qui ne l'est pas du tout aujourd'hui (les annonces, la
découverte d'équipes) et solder les deux `// TODO` de `lib/`.

**Prérequis** : lot 1 (routes déclarées, `PdlMarkdownBody`, `PdlImageViewer`, `PdlPersonRow`,
`PdlDeadEndEmpty` de la vague B ; `TeamDiscoveryCard` est un widget métier livré par S34-2, sans
préfixe `Pdl`, cf. §1.2.5) ; du lot 2, `CommentThread` (S12-6).
**Les tâches d'accessibilité S32-1 et S34-3 peuvent partir très tôt** : ce sont les deux
fonctionnalités totalement absentes de l'application.

### 5.1 Écran 31 — Publication

`post_detail_page.dart` (129 l.) est le plus court de l'app : app bar, bandeau d'équipe, date,
markdown. Le défaut central est **fonctionnel** et déjà corrigé par F-TE-10 (liens inertes).

**Structure** : `PdlAppBar` opaque (« ← Retour au fil | titre tronqué | Partager ») ; identité
(`PdlTeamLine`, titre multi-ligne, badges) ; couverture 16:9 208 px avec bouton plein écran ;
`PdlMarkdownBody` complet ; « Pièces jointes » (`PdlAttachmentRow`) ; `CommentThread` réservé aux
membres (bandeau sinon) ; `PdlPrevNextNav`.

**Limites** : **pas d'auteur** — `PostDto` n'expose ni `createdBy` ni `createdById` ; le bloc
auteur de la maquette (avatar + nom + date) n'est pas alimentable, **seule la date longue est
conservée** ; pas de taille de pièce jointe (`AssetDto` porte `contentType`, pas d'octets) → « PDF »
et non « PDF · 240 Ko » ; pas de voisins de publication → `PdlPrevNextNav` n'est rendu que
lorsque l'écran est ouvert **depuis un fil déjà chargé** (voisins passés en `extra` de la route),
et **absent en ouverture par deeplink**, sans espace vide résiduel. Micro-copie : « Soyez le
premier à commenter. » **sans point d'exclamation** (`brand.md` §8.1).

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S31-1 | ☐ | Livrer les pièces jointes | `[M] core/pdl/pdl_attachment_row.dart` · `[M] features/posts/...` | F-CO-3 | S | Icône ou vignette 56 px (dérivée d'`imageUrl` via le gabarit `{size}` d'imgproxy), nom tronqué, type MIME, bouton 44 px dont le libellé d'accessibilité **nomme le fichier** |
| S31-2 | ☐ | Assembler l'écran publication | `[M] features/posts/presentation/pages/post_detail_page.dart` | F-TE-10, S31-1, S12-6 | M | Couverture plein écran zoomable, corps complet (tables et code compris), pièces jointes, commentaires réservés aux membres, partage ; aucun bloc auteur inventé |
| S31-3 | ☐ | Livrer la navigation précédent / suivant | `[M] core/pdl/pdl_prev_next_nav.dart` · `[M] config/router.dart` (voisins en `extra`) | S31-2 | M | Blocs de 64 px rendus depuis un fil chargé, **absents en ouverture froide**, sans espace vide |

### 5.2 Écran 32 — Annonces

**La rubrique est aujourd'hui inaccessible** : aucune section dans `buildTeamDestinations`, et un
deeplink tombe en erreur après ~20 s. C'est le premier point à corriger. `ads_page.dart:19`
appelle `listAds` **sans `page` ni `size`** → liste tronquée silencieusement.

**Liste** : en-tête d'équipe compact, titre « Annonces » + compteur, `PdlPinnedToolbar`
(recherche + `PdlChipRow` avec la chip de tri en première position, puis Tous | Vente | Location
| Recherche), `SliverList` d'`AdCard` (bandeau 120 px photo ou dégradé `gradAd`, titre,
`PdlBadgeStack`, extrait 2 lignes, prix / date / lieu), `PdlPagedListFooter` alimenté par
`AdListResponse.total` (**pas besoin d'un endpoint count**).

**Détail** : galerie `PageView` 260 px + `PdlGalleryDots` + `PdlAppBar(overlay)` ; identité ;
`PdlPriceBlock` ; description ; « Localisation » ; « Annonceur ».

**La carte de localisation doit dire ce qu'elle montre** : `AdDto.locationGeometry` est
délibérément flouté au centre d'une cellule d'environ **1 km — valeur confirmée, plus une
hypothèse**. Rendu imposé partout où la carte d'une annonce apparaît : un **secteur** (cercle de
500 m de rayon, pastille floutée, zone), **jamais une punaise**. Poser une punaise sur un centre
de cellule prétend une précision que la donnée n'a pas. Légende « Localisation approximative —
le point exact est communiqué par l'annonceur. » Carte absente si `locationGeometry` est nul.

**« Contacter le vendeur » est livré** par un relais e-mail (contrat 1.4.0) : `POST
/api/teams/{teamSlug}/classifieds/{slug}/contact`, corps `AdContactRequest { message }` de 10 à
2000 caractères, réponse **204 sans corps**. Le serveur envoie le message à l'auteur et pose
`Reply-To` sur l'expéditeur, qui reçoit donc la réponse directement. **Aucune adresse n'apparaît
dans l'API** : `AdDto` ne gagne aucun champ de contact, et c'est le point de la conception. Accès :
même règle que la lecture de l'annonce (membre de l'équipe). Conséquences d'interface, toutes
portées par S32-7 : le bouton n'apparaît **pas sur sa propre annonce** (400 `AD_CONTACT_SELF`) ;
il est masqué ou désactivé quand l'auteur s'est rendu injoignable (400 `AD_CONTACT_OPTED_OUT`,
préférence `contactableByMembers`, cf. écran 33) ; le quota est de **10 messages par heure et par
expéditeur, toutes annonces confondues** (429 `AD_CONTACT_RATE_LIMITED` + en-tête `Retry-After`) ;
un échec d'envoi est un **500 `AD_CONTACT_DELIVERY_FAILED` franc**, jamais un succès optimiste —
un relais qui avale le message est pire qu'un relais qui échoue, parce que l'expéditeur attend
une réponse qui n'arrivera pas. Les templates Brevo sont créés et l'envoi a été validé par un
message réel : le chemin est opérationnel de bout en bout. Le mobile n'a donc rien à contourner —
il rend le 500 si le relais échoue, et rien d'autre.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S32-1 | ☑ | **Rendre la rubrique accessible** | `[M] config/router.dart` (`buildTeamSections`, `_deepLinkHierarchies`) · `[M] contracts/routes.yaml` + `pnpm generate-routes` | F-NA-3 | M | La section Annonces apparaît dès `enableAds` ; un deeplink d'annonce ouvre l'écran avec la bonne section active en moins de 2 s ; cas ajouté à `test/deep_link_hierarchy_test.dart` |
| S32-2 | ☑ | Paginer et filtrer la liste | `[M→réécrit] features/ads/presentation/pages/ads_page.dart` · `[C] features/ads/providers/ad_list_provider.dart` | S32-1 | L | `page`/`size` transmis ; recherche debouncée ; chips de type exclusives ; pied « N sur M » ; **plus aucune troncature silencieuse** ; erreur nommée (« La rubrique Annonces n'est pas activée sur cette équipe, ou le réseau est indisponible. ») ; vide filtré → `PdlDeadEndEmpty` réutilisé tel quel, **suggestion de filtre désactivée** (`suggestedFilter: null`) : la règle de S21-8 exige un `count` par filtre levé et `GET …/classifieds/count` **n'existe pas** ; la seule alternative, N appels `…/classifieds?size=1&view=compact`, coûterait jusqu'à 5 requêtes à chaque frappe pour une suggestion secondaire, alors que « Tout réinitialiser » suffit à sortir du cul-de-sac |
| S32-3 | ☑ | Livrer la feuille de tri | `[C] features/ads/presentation/widgets/ad_sort_sheet.dart` | S32-2 | S | Date, prix et nom, ascendant et descendant, via `sortBy` + `sortDir` ; la chip de tri reflète la sélection (non dessinée par la maquette, spécifiée ici) |
| S32-4 | ☑ | Construire la carte d'annonce | `[C] features/ads/presentation/widgets/ad_card.dart` | F-CO-3, F-TE-2 | M | Bandeau photo ou dégradé ; extrait 2 lignes depuis `excerpt` ; les 3 formes de prix (ferme, périodique, à négocier) au format exact `1 200,00 €` |
| S32-5 | ☑ | Livrer la galerie du détail | `[M] features/ads/presentation/pages/ad_detail_page.dart` | F-TE-10, S32-4 | M | Balayage horizontal sur `images[]`, indicateur pilule 18 × 6, appui → plein écran zoomable |
| S32-6 | ☑ | Livrer le bloc prix et la localisation approximative | `[M] ad_detail_page.dart` | S32-4 | M | Carte statique 140 px rayon 8 rendant un **secteur** — cercle de 500 m, floutage confirmé à ~1 km — et **aucune punaise** (une punaise prétendrait une précision que la donnée n'a pas) ; légende d'imprécision ; carte absente si `locationGeometry` est nul |
| S32-7 | ☑ | Livrer « Contacter le vendeur » | `[M] ad_detail_page.dart` · `[C] features/ads/presentation/widgets/ad_contact_sheet.dart` · `[M] features/ads/data/ad_repository.dart` | S32-6 | M | Bouton plein sur la section annonceur, **absent sur sa propre annonce** (comparaison `AdDto.createdById` / utilisateur courant) ; feuille de saisie multiligne 10–2000 caractères avec compteur vivant et envoi désactivé hors bornes ; avant envoi, la feuille dit que **la réponse du vendeur partira vers l'adresse de l'expéditeur, donc que celle-ci lui sera visible** ; `POST /api/teams/{teamSlug}/classifieds/{slug}/contact` avec `AdContactRequest{message}` ; les quatre issues traitées : **204** → feuille fermée + confirmation « Message envoyé, le vendeur vous répondra directement », **400 `AD_CONTACT_OPTED_OUT`** → bouton masqué ou désactivé avec « Ce vendeur ne souhaite pas être contacté », **429 `AD_CONTACT_RATE_LIMITED`** → délai lisible calculé depuis l'en-tête `Retry-After`, **500 `AD_CONTACT_DELIVERY_FAILED`** → échec franc et « Réessayer », **jamais un message de succès** ; le brouillon survit à l'échec |

**Note de livraison — `S32-1` n'a demandé aucun code.** La migration de navigation du
lot 1 l'avait déjà soldée : `buildTeamSections` rend la section Annonces dès
`enableAds` (et l'appartenance, que la lecture exige), `_deepLinkHierarchies` porte
`teamAds` puis `ad`, et `test/deep_link_hierarchy_test.dart` couvre les deux. La tâche a
donc été **vérifiée** contre son critère de fin, pas réécrite.

### 5.3 Écran 34 — Membres et découverte d'équipes

La feuille Participants (34-A) est livrée au lot 2 (S12-7). Sa pastille « Organisateur du
groupe » est **alimentée** par `RideGroupDto.leader` (1.5.0) : la feuille reçoit le `leader` du
groupe ouvert et pose `organizerFlag` sur la seule ligne dont l'identifiant lui est égal — elle
ne cherche pas de meneur dans la liste et ne se replie **jamais** sur `createdBy`. Deux cas
rendent zéro pastille et sont l'un comme l'autre normaux : `leader` nul, qui restera le cas
courant, et un meneur qui **ne participe pas** au groupe — il figure alors sur la carte de groupe
de l'écran 12 mais pas dans la liste, et **aucune ligne fantôme n'est insérée** pour l'y faire
apparaître ; la feuille liste les participants, pas les rôles. Restent deux surfaces.

**34-B · Trombinoscope** — écran plein, atteint depuis la cellule « N membres » de l'écran 23 :
barre « Membres de N-Peloton », compteur, `PdlPinnedToolbar` (recherche + chips de rôle
exclusives Tous | Membres | Organisateurs | Admins), liste de `PdlPersonRow` avec badge de rôle
et « Membre depuis mars 2019 ». **Tout est déjà disponible côté API** (`GET
/api/teams/{teamSlug}/members?role&search&page&size` → `MemberListResponse{members, total}`) : le
défaut est purement client — `TeamRepository.getTeamMembers` existe et **aucun écran ne
l'appelle**, si bien que le mobile charge 20 membres sur 1 999 **sans le dire**. C'est ici que le
pied « 60 membres sur 1 999 · chargement de la suite... » est pleinement alimenté et devient
l'exemple de référence. **Aucune ligne n'est cliquable** : ne pas inventer d'écran de profil
public.

**34-C · Découverte** — barre « Découvrir des équipes », chips exclusives (Toutes | Ouvertes à
l'adhésion | Mes équipes), compteur, cartes à gap 12. `TeamDiscoveryCard` : bandeau 120 px
(`gradTeam` + icône blanche), **logo 56 px débordant de 20 px sous le bandeau**
(`Stack(clipBehavior: none)` + corps en `padding-top: 26`), titre, badge de visibilité, extrait
2 lignes, « N membres » et action. Quatre états d'action : `joinable && role == null` →
« Rejoindre l'équipe » (plein) ; appel en cours → contour grisé « Adhésion... » ; `role != null`
→ badge « Membre » + « Voir l'équipe » (contour) ; `!joinable && role == null` → « Sur
invitation » (désactivé). Les chips sont désormais alimentables grâce à **`joinable`** (nouveau)
et `minRole=MEMBER`, et `excerpt` + `logoUrl` évitent de transporter tout l'article de
présentation dans chaque carte.

**Limite** : `GET /api/teams` n'a **aucun paramètre de tri** — la mention « triées par nombre de
membres » de la maquette est **retirée** ; annoncer un tri qu'on ne contrôle pas serait faux.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S34-1 | ☑ | Livrer le trombinoscope paginé | `[C] features/teams/presentation/pages/team_members_page.dart`, `providers/team_members_provider.dart` · `[M] contracts/routes.yaml` + génération | F-CO-3, F-NA-3 | L | `role`, `search`, `page`, `size` transmis ; pied « N membres sur M » **exact** à chaque page ; chips de rôle exclusives ; plus jamais 20 membres silencieux sur 1 999 |
| S34-2 | ☑ | Construire la carte d'équipe de découverte | `[C] features/teams/presentation/widgets/team_discovery_card.dart` | F-CO-3 | M | Logo débordant **sans clip parasite** ; les 4 états d'action ; extrait 2 lignes depuis `excerpt` |
| S34-3 | ☑ | Livrer l'écran de découverte et tuer les deux `// TODO` | `[C] features/teams/presentation/pages/team_discovery_page.dart` · `[M] features/teams/presentation/pages/teams_page.dart` (l. 34 et 63) · `[M] contracts/routes.yaml`, `config/router.dart` | S34-2, F-NA-3 | L | La loupe (qui reçoit enfin un `tooltip`) et le CTA d'état vide mènent quelque part ; chip `joinable=true` fonctionnelle ; adhésion optimiste avec bandeau d'échec nommant la cause ; **aucune mention de tri** ; « Mes équipes » vide propose « Découvrir des équipes » comme **seul CTA plein de tous les états vides** ; `grep TODO` ne renvoie rien |

**Note de livraison — l'écran de découverte s'appelle `teams_discover_page.dart`.** Le
plan le nommait `team_discovery_page.dart` ; la coquille livrée au lot 1 portait déjà
l'autre nom **et sa route**, et la réécrire sur place évitait de toucher au routeur pour
un renommage. Les deux `// TODO` de `teams_page.dart`, eux, étaient déjà soldés par
F-DE-10 : la loupe a son `tooltip` et le CTA d'état vide mène ici.

**État livrable en fin de lot 5** — plus aucune fonctionnalité n'est inaccessible ; les listes
disent toujours combien d'éléments elles cachent ; il ne reste que l'équipe et le profil.

---

## Lot 6 — Équipe et profil

**Objectif** : refermer la boucle — la page d'équipe devient un hub qui pointe vers les surfaces
racine, et le profil devient un vrai écran de réglages.

**Prérequis** : tous les lots précédents. L'écran 23 consomme les cartes de fil (S11-2), la
navigation de sections (F-NA-4), le trombinoscope (S34-1) et l'exploration de parcours (lot 3) ;
l'écran 33 consomme le provider de préférences (F-TH-7) qu'il **ne duplique pas**.

### 6.1 Écran 23 — Équipe

Le hub complet existe déjà, codé et **routé nulle part** : `team_detail_page.dart` (514 l.,
statistiques, à propos, prochaines sorties, carrousel de parcours). Il sert de base à
`TeamHomePage` (F-NA-4) puis disparaît en tant que page.

**Structure** : `PdlAppBar` (retour · nom · partager) ; en-tête d'équipe interpolé (déployé
≈ 104 px → rétracté 56 px : avatar 56 → 26, titre 20 → 17, badges en fondu, **action toujours
atteignable** car elle vit dans `actions` et non dans le `flexibleSpace`) avec le lien « Toutes
mes équipes » porté à **44 px de cible** (la maquette est à 28) ; `PdlStatCellRow` à 3 cellules
cliquables ; `PdlPinnedToolbar` (recherche + chips de type) ; « Fil » + compteur ; liste de
`PublicationCard` ; rangée de sections d'équipe (Fil | Calendrier | Parcours | Annonces | À
propos) rendue **en contenu**, pas en coquille (§1.4.2-4).

**C'est ici que le silo se casse** — les trois cellules de statistiques pointent vers des
**surfaces racine pré-filtrées**, pas vers des écrans dupliqués par équipe :
« 1 999 membres › » (`memberCount`) → trombinoscope ; « 2 sorties à venir › »
(**`upcomingRideCount`**) → calendrier de portée équipe ; « 2 585 parcours › »
(**`routeCount`**) → **écran Parcours global** avec la portée pré-réglée. Sur la section À
propos, deux cellules seulement : membres (cliquable) et année de création (`createdAt` — il n'y
a pas de `foundedYear`).

**L'état d'adhésion est résolu avant le premier paint** : le bouton n'existe pas tant que
`TeamDetailDto` n'est pas arrivé ; **pas** de bascule « Rejoindre » → « Quitter » après coup.

**Pages libres** : `TeamDetailDto.pages[]` donne titre et slug ; le contenu n'est chargé qu'à
l'ouverture de la page, ce qui supprime le N+1 de `team_about_page.dart:89-103` (N pages = N
requêtes simultanées aujourd'hui).

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S23-1 | ☐ | Construire l'en-tête d'équipe interpolé | `[C] features/teams/presentation/widgets/team_header.dart` · `[S à terme] team_sliver_app_bar.dart` | F-NA-4 | L | Avatar 56 → 26, titre 20 → 17, badges en fondu, action toujours visible ; **déployé ≤ 120 px** ; `logoUrl` (nouveau) enfin affiché |
| S23-2 | ☐ | Brancher l'adhésion et le départ | `[M] features/teams/data/team_repository.dart` · `[C] features/teams/providers/team_membership_provider.dart` | S23-1 | M | État résolu **avant le premier paint** ; « Adhésion... » pendant l'appel ; échec → `PdlBanner(danger)` nommant la cause ; départ confirmé par une feuille en question fermée |
| S23-3 | ☐ | Livrer la rangée de statistiques cliquables | `[M] core/pdl/pdl_stat_cell_row.dart` · `[M] TeamHomePage` | S23-1, S21-1, S34-1 | M | Les 3 cellules naviguent vers le trombinoscope, le calendrier de portée équipe et `/parcours` **pré-filtré** ; version à 2 cellules sur À propos |
| S23-4 | ☐ | Livrer la rangée de sections d'équipe | `[M] config/router.dart` (`buildTeamSections`), `TeamHomePage` | F-NA-4, S32-1 | M | 2 à 5 sections selon `enableRides/Trips/Routes/Ads/Posts` et le rôle, **Annonces comprise** ; la section active suit l'URL ; une seule `NavigationBar` dans l'arbre |
| S23-5 | ☐ | Livrer À propos et les pages libres | `[M] features/teams/.../team_about_page.dart` · `[C] .../team_page_screen.dart` · `[M] contracts/routes.yaml` + génération | S23-1, F-NA-3 | M | **Une seule requête** à l'ouverture de l'onglet ; chaque page libre ouvre un écran deeplinkable avec son entrée de hiérarchie |
| S23-6 | ☐ | Retirer la page morte | `[S] features/teams/presentation/pages/team_detail_page.dart` · providers déplacés vers `features/teams/providers/` | S23-1, S23-3 | S | Aucune référence résiduelle ; `flutter analyze` propre |

### 6.2 Écran 33 — Profil et préférences

`profile_page.dart` (324 l.) est en lecture seule, n'affiche **qu'une** passkey (les autres sont
écrasées, l. 291+), et n'a ni unités, ni thème, ni GPS, ni Strava, ni export, ni suppression de
compte.

**Structure** : colonne unique bornée à 600 px (`ConstrainedBox + Center`, **pas** une grille à
deux colonnes), motif de section constant — `PdlSectionHeader` puis `PdlCard` de
`PdlSettingRow`. Dix sections : identité, mes participations, préférences, **notifications (non
livrée)**, sécurité, appareils GPS, comptes liés, vos données, à propos, compte et zone de
danger.

**Réalisable maintenant** : nom affiché (`PUT /api/users/me`) ; avatar (`POST`/`DELETE
/api/users/me/avatar`) ; **unités, thème, langue et « Être contacté par les membres »**
(`PATCH /api/users/me/preferences`, champs omissibles) — via le provider **déjà livré** en
F-TH-7 ; participations
(`GET /api/users/me/participations`, le `total` avec `from = maintenant` donne « à venir », avec
`to = maintenant` l'historique) ; clés d'accès (4 endpoints) ; appareils GPS (liste **pilotée par
`GET /api/gps/available`**, jamais codée en dur) ; Strava ; export RGPD (4 endpoints) ;
`logout-all` **qui existe et n'a jamais été câblé** ; suppression de compte ; versions
(`package_info_plus` + `GET /api/version`).

**Non réalisable, et donc non livré sans emplacement mort** : la section Notifications (ni
endpoint de préférences, ni push) ; le nom de compte Strava (`SocialIdentityDto` n'expose pas
d'identifiant distant → « Lié le *date* » seulement) ; les logos officiels Garmin / Karoo /
Strava (pas de `logoUrl` → avatar-lettre teinté, comme la maquette).

**États** : chargement **par section** (l'identité s'affiche pendant que les réglages arrivent),
jamais un squelette d'écran entier. Erreur d'enregistrement : bordure `danger` sur le champ +
bandeau **sous le champ**, contextuel, jamais global. Succès : carte plate + coche verte.
Feuille « Langue » : sélection marquée d'une **coche indigo**, pas d'un radio. La règle
« question fermée + conséquence » de la maquette, qui ne couvre que la suppression de compte, est
**étendue** à délier Strava, supprimer une clé d'accès et déconnecter un appareil GPS.
Suppression de compte : feuille rouge, bouton **contour rouge**, jamais un aplat.

| # | État | Tâche | Fichiers | Dépend de | Taille | Fin |
|---|---|---|---|---|---|---|
| S33-1 | ☐ | Rendre l'identité éditable | `[C] features/profile/presentation/widgets/profile_identity_section.dart` · `[M] profile_page.dart` | F-CO-3, F-TE-1 | M | Avatar 100 px + bouton 44 px + « Supprimer la photo » en rouge ; champ nom avec Annuler/Enregistrer toujours visibles ; e-mail en lecture seule avec badge ; erreur en ligne |
| S33-2 | ☐ | Assembler l'écran de préférences | `[M] profile_page.dart` · `[C] features/profile/presentation/widgets/preferences_section.dart` | F-TH-7, F-TE-2 | M | Les **quatre** réglages s'appliquent **immédiatement, sans bouton** — unités, thème, langue, et l'interrupteur « Être contacté par les membres » (`contactableByMembers`, lu sur `UserDto`, écrit par le même `PATCH /api/users/me/preferences`, `null` valant **joignable**), sous-titré « Les membres de vos équipes peuvent vous écrire au sujet de vos annonces ; votre adresse ne leur est jamais montrée » ; l'exemple chiffré sous le segmenté d'unités reflète le réglage ; échec → bandeau contextuel **et retour à la valeur précédente** ; **aucune duplication du provider de F-TH-7** |
| S33-3 | ☐ | Livrer « Mes participations » | `[C] features/profile/presentation/pages/my_participations_page.dart` · `[M] contracts/routes.yaml` + génération | F-NA-3 | M | Deux compteurs issus du `total` de l'endpoint, deux listes paginées, badge indigo pour « à venir » et neutre pour l'historique |
| S33-4 | ☐ | Réparer les clés d'accès multiples | `[M] features/auth/services/passkey_service.dart`, `profile_page.dart` | S33-1 | M | Ajouter une clé **n'écrase plus les autres** ; nom d'appareil et dernière utilisation affichés (`null` traité) ; suppression confirmée, libellé d'accessibilité nommant la clé |
| S33-5 | ☐ | Livrer les appareils GPS et Strava | `[C] features/profile/presentation/widgets/connected_services_section.dart` | S33-1, F-TE-10 | L | Liste pilotée par `GET /api/gps/available` ; connexion par OAuth externe et retour dans l'app ; déconnexion et déliaison confirmées en question fermée |
| S33-6 | ☐ | Livrer export RGPD, compte et zone de danger | `[C] features/profile/presentation/widgets/data_and_account_section.dart` | S33-1 | M | Les 3 états d'export (absent / en préparation / prêt avec dates) et le téléchargement ; `logout-all` enfin câblé ; suppression de compte en feuille rouge à bouton contour |
| S33-7 | ☐ | Livrer « À propos » | `[M] profile_page.dart` | — | S | Version applicative et version serveur en mono ; liens légaux vers les écrans existants |

**État livrable en fin de lot 6** — la version 2 est complète : douze écrans refondus, cinq
onglets, deux thèmes, deux systèmes d'unités, et aucune fonctionnalité inaccessible.

---

# 3. Séquencement et chemin critique

## 3.1 Graphe du lot 1

```
F-TH-1 ─┬─ F-TH-2 ─┐
        ├─ F-TH-3 ─┼─ F-TH-5 ─┬─ F-TH-7 ── F-TH-8
        └─ F-TH-6 ─┘          └─ F-CO-1 ── F-CO-2 ─┬─ F-CO-3 ─┬─ F-CO-4 ─┬─ F-CO-5
F-TH-4 ───────────────────────────────────────────┘           │          │
                                                               │          │
F-TE-1, F-TE-3, F-TE-4  (indépendants, à lancer immédiatement) │          │
F-TE-5 ── F-TE-6 ──────────────────────────────────────────────┘          │
F-TE-7 ─┬─ F-TE-8 ── F-TE-9                                               │
        └─ F-TE-10 ── (F-DE-6)                                            │
F-TE-2 (après F-TH-7) · F-TE-11 (après F-TE-1 et F-CO-3)                  │
F-NA-1 ── F-NA-2 ─┬─ F-NA-3                                               │
                  └─ F-NA-4 ───────────────────────────────────────────────┘
F-DE-1..F-DE-5, F-DE-8..F-DE-11 : chacune sur le composant qui la porte
```

## 3.2 Chemin critique

**`F-TH-1 → F-TH-5 → F-CO-1 → F-CO-2 → F-CO-3 → F-CO-4 → S12-8 → S13-6 → S25-3`.**

Autrement dit : les jetons et le thème explicite conditionnent tout ; la vague C conditionne tout
écran ; l'écran 12 conditionne l'accueil ; l'écran 13 produit `EmbeddedRouteSheet` que l'écran 25
consomme. Sur ce chemin, **`F-CO-2` et `F-CO-3` sont les deux plus gros blocs** et méritent d'être
attaqués en parallèle par plusieurs mains (un fichier par composant, aucune dépendance croisée à
l'intérieur d'une vague).

Deux dépendances non évidentes à surveiller :

- **`F-CO-4` dépend de `F-NA-2`** : `PdlBottomTabs` et `PdlScreenScaffold` ne peuvent être écrits
  et recettés qu'une fois la coquille à cinq branches en place, sinon ils sont conçus pour une
  structure qui disparaît.
- **`F-NA-1` doit précéder `F-NA-2`** : le test de hiérarchie de deep link est le filet de la
  migration. L'écrire après revient à écrire un test qui décrit le bogue.

## 3.3 Ce qui peut avancer en parallèle

| Piste | Contenu | Se détache après |
|---|---|---|
| Thème et bibliothèque | `F-TH-*`, `F-CO-*` | immédiatement |
| Navigation | `F-NA-1` → `F-NA-2` → `F-NA-3` / `F-NA-4` | immédiatement (`F-NA-1` n'a aucune dépendance) |
| Briques techniques hors interface | `F-TE-1`, `F-TE-3`, `F-TE-4`, `F-TE-5` | immédiatement — aucune ne touche à l'interface |
| Carte | `F-TE-7` → `F-TE-8` → `F-TE-9` | après `F-CO-2` (`PdlScrim`, `PdlBlurSurface`) |
| Markdown et liens | `F-TE-10` → `F-DE-6` | après `F-TE-1` et `F-CO-3` |
| Lot 3 (parcours, calendrier) | `S21-*`, `S22-*` | après `F-CO-4` et `F-TE-9` — **en parallèle du lot 2** |
| Lot 5 (annonces, découverte) | `S32-1`, `S34-2`, `S34-3` | après `F-NA-3` — **en parallèle des lots 2 à 4** |

Trois précédences croisées entre lots, et trois seulement : `S11-1` attend `S12-1` ; `S25-3`
attend `S13-6` ; `S24-6` et `S31-2` attendent `S12-6` (`CommentThread`). Tout le reste des lots 3
à 5 est indépendant du lot 2.

## 3.4 Ordre d'attaque recommandé

1. `F-NA-1`, `F-TE-1`, `F-TE-3`, `F-TE-4` en ouverture — sans dépendance, ils débloquent le reste.
2. `F-TH-1` → `F-TH-5`, puis `F-CO-1` → `F-CO-2` à plusieurs mains.
3. `F-NA-2` en parallèle de `F-CO-3`, pour que `F-CO-4` trouve la coquille prête.
4. `F-TE-5` → `F-TE-6` et `F-TE-7` → `F-TE-8` dès `F-CO-2` livré : ce sont les deux briques
   longues, elles ne doivent pas être découvertes au moment d'écrire l'écran 12.
5. Lot 2 dans l'ordre 12 → 13 → 11, lot 3 en parallèle, `S32-1` et `S34-3` dès que possible.
6. Lots 4, 5, 6.

---

# 4. Risques

| # | Risque | Signal qu'il se réalise | Repli |
|---|---|---|---|
| 1 | **La migration `StatefulShellRoute` casse les piles de retour ou les deeplinks** | `test/deep_link_hierarchy_test.dart` rouge, ou un lien froid ouvre l'écran avec une pile vide, ou le mauvais onglet est surligné sous `/equipes/…` | Conserver `MainShell` en `ShellRoute` et greffer les routes d'équipe comme pages ordinaires sous le navigateur racine : cinq onglets quand même, au prix de la préservation de pile par branche. Décision réversible tant que F-CO-4 n'a pas figé `PdlScreenScaffold` |
| 2 | **Les tuiles `.mvt` restent inaccessibles au mobile** (authentification par cookie de session) | 401/403 sur les requêtes de tuiles, ou carte vide au-delà du zoom 2 | Déjà le chemin retenu : GeoJSON de proximité (F-TE-9), plafonné à quelques centaines de tracés, **avec un message explicite quand le plafond est atteint** — jamais une carte silencieusement incomplète |
| 3 | **`maplibre 0.3.5` ne permet pas la sélection de tracé** (`queryLayers` sans propriétés, `VectorSource.maxZoom = 2`, coût des mises à jour de couches) | Le tap ne sélectionne rien, ou scintillement / fuite de couches à chaque frame | Hit-test **géométrique côté client** : `PolylineIndex.nearest` sur chaque tracé chargé, indépendant du SDK. Coûte une boucle sur ≤ 12 tracés, ce qui est négligeable |
| 4 | **Le profil altimétrique ne tient pas 60 fps au glissement** | Repeinte des barres visible au `debugRepaintRainbowEnabled`, ou frames > 16 ms au `Timeline` sur un appareil de milieu de gamme | Pré-agréger à 76 barres au chargement, isoler le réticule dans un `RepaintBoundary`, et limiter la fréquence de mise à jour du curseur à 30 Hz |
| 5 | **Le `BackdropFilter` des barres épinglées coûte trop cher** | Chute sous 55 fps sur une liste de 200 items (mesure imposée par le critère de S11-5) | Basculer `PdlBlurSurface` en surface **opaque** `overlaySolid` sans flou : un seul jeton (`PdlMotion.blurToolbar = 0`) à changer, aucun écran à rouvrir |
| 6 | **Le mode sombre dérive** puisqu'aucune maquette ne le fournit | Contraste relevé < 4,5:1 en recette, notamment sur les badges doux et sur `Vente`/`Publié` qui partagent le même couple | La règle §1.1.2 reste la source : remonter la luminosité du fond doux de la seule famille fautive et passer son texte en nuance 0 ; **ne jamais corriger un écran par un littéral** |
| 7 | **Les dates s'affichent au mauvais jour** faute de fuseau d'équipe dans le contrat | Le cas de test « étape du lundi 17 août 08:00 » rend la veille ou le lendemain sur un appareil réglé en `Pacific/Auckland` ou `America/Los_Angeles` | Parité web assumée (fuseau de l'appareil) et **une seule** fonction de formatage, pour qu'une éventuelle évolution `Team.timezone` ne touche qu'un fichier |
| 8 | **La refonte du fil casse son second appelant** (`publication_feed_view` sert l'accueil *et* la page d'équipe) | `team_feed_page.dart` ne compile plus, ou le fil d'équipe perd ses filtres | Le critère de S11-6 l'interdit explicitement ; en dernier recours, dupliquer temporairement le widget et refermer la dette au lot 6 |
| 9 | **Le lot 1 est long avant qu'un écran ne bouge** — risque de démarrer les écrans sur une bibliothèque incomplète | Un lot d'écran commence alors que la vague C n'est pas livrée, et réinvente des barres ou des feuilles localement | Prioriser `C1`, `C3` et `C5` dans `F-CO-4` : l'écran 12 n'a besoin ni de `C6`, ni de `C9`, ni de `C10`. Toute barre ou feuille écrite hors `core/pdl` est un motif de refus en revue |
| 10 | **L'erreur nue « Erreur » à l'inscription n'est pas causée par l'API** mais par une désérialisation Freezed de `RideParticipationDto` | Après un 201, une exception **non-`DioException`** apparaît dans les journaux (F-TE-3 la rend visible) | Vérifier la réponse réelle du serveur **avant** de conclure ; rendre le mapping tolérant aux champs absents. Ne pas masquer la cause derrière le bandeau |
| 11 | **Une maquette recettée réclame une donnée qui n'existe pas** (liste d'attente, auteur de publication) — ou, pour le meneur, réclame de le rendre **toujours** | Une revue demande d'« afficher quand même » en dérivant un champ approchant, ou s'étonne qu'un groupe n'affiche pas de meneur | Refuser la simulation : `createdBy` n'est **pas** le meneur (c'est le créateur de la sortie, identique sur tous ses groupes), l'absence de file d'attente n'est pas un « complet temporaire ». `RideGroupDto.leader` est livré, mais la pastille « Organisateur » reste **conditionnelle** : `leader` nul est le cas courant et ne rend rien ; les dégradations restantes sont nommées dans le code et listées en §5 |
| 12 | **Un fichier généré est édité à la main** (`paths.generated.dart`, `apple-app-site-association`, section deeplink d'`AndroidManifest.xml`, DTO Freezed) | Un `git diff` montre l'un de ces fichiers modifié sans exécution du générateur | Revert et régénération : `pnpm generate-routes` depuis `frontend/` pour les routes, `dart run build_runner build` pour les modèles |

---

# 5. Hors périmètre

## 5.1 Chantiers exclus de la v2, et pourquoi

| Chantier | Raison | Conséquence assumée |
|---|---|---|
| **Notifications et push** | Aucun endpoint livré (`GET /api/users/me/notifications`, `POST /api/users/me/devices`, préférences de notification) et aucune dépendance de push ajoutée | **Aucune cloche nulle part**, et la section Notifications de l'écran 33 n'est pas rendue — pas d'emplacement mort, conformément au brief §5 |
| **Pagination par curseur** | Non livrée, et **incompatible avec le besoin** : le pied de liste maquetté (« 60 membres sur 1 999 ») exige un `total` que le curseur ne fournit pas. Les deux propositions du brief se contredisent ; le plan retient l'offset | `PagedListNotifier` reste en `page`/`size`, avec déduplication par `itemKey` |
| **ETag / `If-None-Match`, URLs d'images signées, blurHash** | Non livrés | `AuthenticatedImage` reste sur `cached_network_image`, sans préchargement ni placeholder flouté |
| **`GET /api/map/features`** | Non livré | Aucune carte multi-entités : `PdlMap` ne rend que des parcours et des lieux ; le voyage charge N parcours, plafonné à 12 étapes |
| **Tuiles `.mvt` de masse** | Bloquées par l'authentification par cookie de session, que le mobile n'a pas | Repli GeoJSON de proximité ; parité web non atteinte sur `/parcours/carte` |
| **Liste d'attente (`waitlisted`)** | N'existe pas en base | « Complet » est un **état terminal** : ni file d'attente, ni notification de place libérée |
| **Écran de profil public d'un membre** | Aucune maquette ne va au-delà de la liste | Les lignes du trombinoscope ne sont pas cliquables — ne pas inventer l'écran |
| **Jeu d'icônes Tabler** | Coût disproportionné (§1.0.3-10) | Material outline derrière `PdlIcons`, bascule ultérieure en un fichier |
| **Édition et création de contenu** (créer une sortie, un parcours, une annonce) | Hors du brief : la v2 est une version de consultation et de participation | — |

## 5.2 Évolutions d'API qui lèveraient une dégradation

Aucune ne bloque un lot ; chacune supprimerait une dégradation nommée dans ce plan. Le meneur de
groupe n'y figure plus : il est **livré** en 1.5.0 (`RideGroupDto.leader`, `GroupRequest.leaderId`
— §1.0.2). Les **gabarits de sortie** n'en ont volontairement pas, et ce n'est pas un manque à
combler : `RideTemplateGroupRequest` reste sans champ de meneur, instancier une sortie depuis un
gabarit n'en désigne aucun.

| # | Manque | Écrans | Dégradation actuelle |
|---|---|---|---|
| 1 | URL de tuile authentifiable (jeton court en paramètre de requête) | 21 | Repli GeoJSON, plafond de quelques centaines de tracés |
| 2 | `logoUrl` sur `TeamPublicationDto` (le `TeamDetailDto` l'a déjà) | 11, 12, 13, 24, 31, 32 | Avatar d'initiales à teinte hachée |
| 3 | `RideGroupDto.thumbnailUrl` | 11 | Vignette de la sortie au lieu de celle du parcours du groupe |
| 4 | `groups[]` ou un `registeredGroup` compact sur les lignes de liste | 11 | Un `getRide` supplémentaire pour la seule prochaine sortie |
| 5 | Capacité agrégée sur `RideDto` de liste | 11 | « N participants » au lieu de « N/M » |
| 6 | `PostDto.createdByDisplayName` / `createdById` | 31 | Bloc auteur supprimé, seule la date reste |
| 7 | `AssetDto.size` | 31, 32 | « PDF » au lieu de « PDF · 240 Ko » |
| 8 | Voisins de publication (`prev`/`next`) | 31 | Navigation rendue seulement depuis un fil déjà chargé |
| 9 | `RouteUsageDto.endDate` | 13 | Date de début seule pour un usage de type voyage |
| 10 | `ClimbDto.name` | 13, 25 | « Montée N » |
| 11 | Commentaires d'étape | 25 | Section absente, renvoi vers le voyage |
| 12 | Participants paginés et cherchables côté serveur | 24, 34-A | Liste complète embarquée, recherche client, pas de pied « N sur M » |
| 13 | Tri sur `GET /api/teams` | 34-C | Mention « triées par nombre de membres » retirée |
| 14 | `SocialIdentityDto.externalUsername`, `logoUrl` de service GPS | 33 | Avatar-lettre et « Lié le *date* » |
| 15 | Préférences de notification et push | 33, 11 | Section entière non livrée, aucune cloche |
| 16 | `Team.timezone` ou dates zonées au contrat | 22, 24, 25 | Fuseau de l'appareil, parité web |
| 17 | `GET /api/map/features` | 24, 21 | N appels de parcours plafonnés, tracé partiel au-delà de 12 étapes |
| 18 | Statut `TERMINÉE` dans l'enum `Status` | 11, 12, 22 | Dérivé client de `dateTime < now`, centralisé dans `RideDto.isPast` |

## 5.3 Cas de test à écrire avec les écrans

1. **Inscription** (12) : les six états de bouton ; un `GROUP_FULL` restaure l'état optimiste et
   nomme le groupe ; aucun `showSnackBar` dans `features/rides`.
2. **Réticule** (13, 25) : les barres ne sont pas repeintes au glissement ; la distance affichée
   coïncide avec `ElevationProfileDto.distance` à ±0,5 %.
3. **Dates** (22, 24, 25) : appareil en `Pacific/Auckland` puis `America/Los_Angeles`, étape du
   lundi 17 août 2026 à 08:00 → **aucune double conversion**.
4. **Jeton ICS** (22) : capture de l'écran d'abonnement → aucune occurrence du jeton ; le
   presse-papiers contient l'URL réelle.
5. **Cellule de jour cumulée** (22) : un jour à la fois « aujourd'hui » et « inscrit » porte les
   deux marqueurs.
6. **Liens markdown** (31) : interne → route interne, externe → navigateur, non lançable →
   bandeau. **Aucun lien inerte.**
7. **Tableau markdown** (31) : 4 colonnes, emoji dans les libellés, défilement horizontal sans
   débordement de la page.
8. **Prix** (32) : `1200` → `1 200,00 €` ; `25` + `WEEK` → « 25,00 € » en 600 puis « / semaine »
   en 400 ; `null` → « Prix à négocier ».
9. **Trombinoscope** (34-B) : équipe à 1 999 membres → le pied annonce le total exact à chaque
   page.
10. **Densité de liste** (21) : 201 résultats → bascule automatique en compact ; choix manuel
    persistant entre deux lancements.
11. **Deeplink d'annonce** (32) : ouverture à froid → section Annonces active, pile de retour
    cohérente.
12. **Text scaling ×1,3 puis ×2,0** : badges, lignes de col à 3 colonnes, en-têtes épinglés —
    aucun débordement.
13. **Sombre** : chacun des douze écrans capturé en clair et en sombre ;
    `grep -rnE '0xFF[0-9A-Fa-f]{6}' mobile/lib/features` ne remonte rien.
14. **Frontière de la bibliothèque** : `grep -rn "api/generated" mobile/lib/core/pdl` ne remonte
    rien ; `grep -rn "showModalBottomSheet" mobile/lib` ne remonte que `pdl_sheet.dart`.
15. **Contact d'une annonce** (32) : les quatre issues du relais — 204, 403
    `AD_CONTACT_OPTED_OUT`, 429 `AD_CONTACT_RATE_LIMITED` (`Retry-After` exploité), 500
    `AD_CONTACT_DELIVERY_FAILED` — rendent quatre écrans distincts ; **aucun succès affiché sur
    un 500** ; le bouton est absent sur sa propre annonce ; 9 et 2001 caractères sont refusés
    côté client sans appel réseau.








