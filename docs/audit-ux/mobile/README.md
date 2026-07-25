# Audit UX — Application mobile Pédalons (iOS)

Exploration réelle de l'app Flutter sur simulateur **iPhone 17 Pro / iOS 27.0** (402 × 874 pt),
session connectée en tant que **Gaby Landais**, données de **production**, locale **français**.

Périmètre : consultation et participation d'un membre. Administration, création et édition
volontairement exclues.

Équipes explorées : **n-peloton** (1999 membres, 2585 parcours, ~665 sorties) et **gaby** (7 membres).

---

## Table des captures

| # | Fichier | Écran |
|---|---------|-------|
| 01 | `01-accueil-feed-tout.png` | Accueil — feed agrégé, filtre « Tout » |
| 02 | `02-accueil-filtre-sorties.png` | Accueil — filtre « Sorties » |
| 03 | `03-accueil-filtre-publications.png` | Accueil — filtre « Publications » |
| 04 | `04-accueil-filtre-voyages.png` | Accueil — filtre « Voyages » |
| 05 | `05-accueil-scroll-infini.png` | Accueil — après ~8 swipes (scroll infini) |
| 06 | `06-equipes-liste.png` | Équipes — « Mes équipes » |
| 07 | `07-equipes-recherche-sans-effet.png` | Équipes — après tap sur la loupe (aucun effet) |
| 08 | `08-equipe-npeloton-fil.png` | Équipe N-Peloton — onglet Fil |
| 09 | `09-equipe-calendrier.png` | Équipe N-Peloton — onglet Calendrier |
| 10 | `10-equipe-parcours.png` | Équipe N-Peloton — onglet Parcours |
| 11 | `11-equipe-parcours-filtres.png` | Parcours — bottom sheet « Filtres » |
| 12 | `12-equipe-a-propos.png` | Équipe N-Peloton — onglet À propos |
| 13 | `13-sortie-detail-haut.png` | Sortie à venir (N-Peloton #665) — haut |
| 14 | `14-sortie-detail-bas.png` | Sortie #665 — groupes + description |
| 15 | `15-sortie-detail-fin.png` | Sortie #665 — bas de page |
| 16 | `16-sortie-modale-groupes.png` | Sortie — modale « Choisir un groupe » |
| 17 | `17-sortie-passee-haut.png` | Sortie passée (#664, 40 participants) — haut avec carte |
| 18 | `18-sortie-passee-groupes.png` | Sortie #664 — bloc Groupes complet |
| 19 | `19-parcours-detail-haut.png` | Parcours (NP664 - CHILL GRAVEL) — carte plein écran |
| 20 | `20-parcours-detail-fiche.png` | Parcours — sheet déployée (stats + Télécharger) |
| 21 | `21-publication-detail.png` | Publication (CountryCat) — haut |
| 22 | `22-publication-detail-bas.png` | Publication — bas (image jointe) |
| 23 | `23-voyage-detail.png` | Voyage NP 550 — détail + liste d'étapes |
| 24 | `24-etape-detail-vide.png` | Étape « Mardi » — écran quasi vide |
| 25 | `25-etape-detail-avec-parcours.png` | Étape « Lundi » — avec lien « Voir le parcours » |
| 26 | `26-equipe-gaby-fil.png` | Équipe Gaby (petite équipe) — onglet Fil |
| 27 | `27-etat-vide-publications.png` | État vide — « Aucune publication » |
| 28 | `28-etat-vide-calendrier-equipe.png` | État vide — « Aucun événement ce mois-ci » |
| 29 | `29-equipe-gaby-a-propos.png` | Équipe Gaby — À propos (contenu minimal) |
| 30 | `30-calendrier-global.png` | Calendrier global (onglet 3) — haut du mois |
| 31 | `31-calendrier-global-bas.png` | Calendrier global — fin du mois |
| 32 | `32-profil.png` | Profil |
| 33 | `33-profil-bas.png` | Profil — bas (versions, mentions légales) |
| 34 | `34-profil-langue.png` | Profil — sélecteur de langue |
| 35 | `35-profil-confidentialite.png` | Politique de confidentialité (in-app) |
| 36 | `36-annonces-chargement.png` | Petites annonces (deeplink) — squelettes de chargement |
| 37 | `37-annonces-erreur.png` | Petites annonces — « Erreur » après ~20 s |
| 38 | `38-sortie-participation-erreur.png` | Sortie — échec silencieux de la participation |

---

## Descriptifs par écran

### 01–05 · Accueil (onglet 1)

**Structure.** En-tête gradient de **182 pt** (21 % de la hauteur d'écran) ne contenant que
« Bonjour, Gaby! ». En dessous, une rangée de 4 chips de filtre (Tout / Sorties / Publications /
Voyages) scrollable horizontalement, puis une liste de cartes.

**Densité.** ~114 pt par carte, soit **4,5 items visibles** par écran. Chaque carte porte : vignette
carte (image statique du tracé) ou icône par défaut, une étiquette de type redondante
(« Sorties » écrit sur chaque carte d'une liste déjà filtrée), le titre, et une ligne
`date • N participants` ou `date • N Étapes`. Chevron à droite.

**Données affichées.** Titre, date longue en français, compteur de participants / d'étapes.
Rien d'autre : ni équipe d'origine (alors que le feed est multi-équipes), ni distance, ni
dénivelé, ni statut de participation de l'utilisateur, ni lieu de départ.

**Filtres.** Les 4 chips fonctionnent. Mais le 4ᵉ (« Voyages ») est **coupé à l'écran** (largeur
utile 25 pt sur 402) : il faut deviner qu'il faut scroller horizontalement. Aucune notion de
filtre « Petites annonces » ni « Parcours ».

**Scroll infini.** Fonctionne (chargement transparent, pas de spinner gênant), l'en-tête se
rétracte de 182 → 118 pt. La rangée de filtres, elle, **disparaît complètement** au scroll : pour
rechanger de filtre il faut remonter toute la liste.

**Anomalie constatée.** Au premier chargement le feed « Tout » commençait par **N-Peloton #665
(29 juillet, à venir)**. Après navigation puis pull-to-refresh, cette sortie **a disparu du feed
Accueil** alors qu'elle est toujours présente dans le Calendrier global et dans le fil d'équipe.
Le feed devient donc purement rétrospectif — c'est exactement l'information la plus utile
(la prochaine sortie) qui manque.

**Ce qui manque visiblement.** Aucun bloc « Prochaine sortie » / « Mes participations », aucun
badge « je participe », pas de séparateurs temporels (Cette semaine / Ce mois-ci), pas de
recherche, pas de notifications, pas de raccourci vers les parcours.

### 06–07 · Équipes (onglet 2)

Liste très simple : logo (Hero animation vers la page équipe), nom, `N membres`, badge de rôle
(« Admin »), chevron. Deux items ici, donc **~1700 pt de vide** sous la liste.

Une **loupe** est présente en haut à droite (48 × 48 pt, sans AXLabel → invisible pour VoiceOver).
Elle est **inopérante** : `teams_page.dart:33` contient `// TODO: Navigate to discover teams`.
Même TODO sur le bouton « Découvrir des équipes » de l'état vide (`:63`). Il n'existe donc
**aucun moyen de découvrir ou rejoindre une équipe depuis le mobile**.

### 08 · Équipe — onglet Fil

En-tête équipe de **222 pt** (logo + nom sur gradient), puis exactement le même composant de feed
que l'Accueil (mêmes 4 chips, mêmes cartes). L'en-tête ne remonte pas au scroll dans cette vue,
ce qui laisse ~4 cartes visibles.

Le shell d'équipe **remplace la barre d'onglets racine** par 4 onglets : Fil / Calendrier /
Parcours / À propos. Conséquence : depuis l'intérieur d'une équipe on ne peut plus atteindre
Accueil, Calendrier global ou Profil sans repasser par la flèche retour.

Un 5ᵉ onglet « Annonces » est prévu dans le code (`team_navigation_destination.dart:38`) mais
conditionné à `isMember && team.enableAds` — il n'apparaît sur aucune des deux équipes testées.

### 09 · Équipe — onglet Calendrier

Agenda mensuel en liste : titre `Juillet 2026` avec chevrons ‹ › de navigation, puis, par jour,
une pastille date (`mer. / 1`) + un libellé `1 juillet` redondant, puis une carte d'événement
(`titre` + `équipe • heure`).

**Densité catastrophique : ~152 pt par journée pour un seul événement**, soit 4 jours visibles.
Un mois de N-Peloton (8 sorties) demande 3 écrans de scroll.

**Ce qui manque.** Pas de vue grille/mois, pas de mise en évidence d'« aujourd'hui » (le 25 juillet
n'est marqué nulle part), pas de distinction passé / à venir, pas de statut de participation,
pas de distance ni de lieu, pas de saut automatique à la date du jour, aucun voyage affiché
(seules les sorties apparaissent).

### 10–11 · Équipe — onglet Parcours

Le meilleur écran de l'app. Barre de recherche + bouton Filtres, rangée de chips de tri
(Date ↓ / Distance / Dénivelé positif / Relief / Revêtement / Direction du vent, scrollable),
compteur `2585 parcours`, puis des cartes carte-vignette + nom + `distance` + `D+`.

**Densité faible** : ~275 pt par carte, **2 parcours visibles** à l'écran. Sur 2585 parcours c'est
un frein réel ; il n'existe ni vue liste compacte ni vue carte.

**Bottom sheet Filtres (11).** Deux RangeSliders (Distance, Dénivelé positif), deux groupes de
chips (Revêtement : Tous/Route/Gravel/VTT/Mixte ; Relief : Tous/Plat/Vallonné/Montagneux), une
ligne « Direction du vent », et un CTA `Voir 2585 parcours`.
**Bug de mise en page** : la ligne « Trier par — Date ↓ » est rendue avec une **hauteur de 1 pt**
(`@(20,675) 362x1`) — elle est écrasée et invisible entre le séparateur et le bouton.
Autre défaut : les sliders n'affichent que « Tous », jamais les bornes ni les valeurs
sélectionnées ; et la sheet **ne recouvre pas la barre d'onglets**, qui reste visible et tappable
sous la modale.

### 12 / 29 · Équipe — onglet À propos

Deux statistiques centrées (`1999 Membres`, `2021 Création`) puis une carte rendant le markdown de
la page « À propos » de l'équipe (description, adresse, liens réseaux sociaux cliquables).

**Ce qui manque.** `1999 Membres` n'est **pas cliquable** : aucune liste de membres, aucun
trombinoscope, aucun moyen de voir qui est dans l'équipe. Pas de bouton « Quitter l'équipe »,
pas de bouton partage, pas d'accès aux **pages personnalisées d'équipe** (`/equipes/{slug}/pages/{page}`
existe sur le web). Sur la petite équipe (29) l'écran se réduit à `7 / 2022 / « Maps persos »` :
énormément de vide.

### 13–16 · Détail d'une sortie (à venir)

De haut en bas : titre dans l'AppBar, chip équipe (`N › N-Peloton ›`, cliquable), carte date,
carte lieu (nom + adresse), ligne `0 participants`, section **Groupes** (9 cartes :
nom + `Départ: hh:mm • NN km/h` + compteur d'inscrits), carte **Description** (markdown avec liens
cliquables), et un bouton flottant plein largeur **« Participer »**.

**Modale « Choisir un groupe » (16).** C'est un `AlertDialog` étroit (232 pt de large sur 402) :
le texte casse en plein milieu (`22 km/ h`), 7 groupes sur 9 sont visibles, le scroll interne
n'est pas signalé, et le seul bouton est « Annuler » en bas à droite. Rien pour participer
« sans groupe ».

**Défaut majeur — la participation ne fonctionne pas (38).** Tap « Participer » → choix d'un
groupe → l'application affiche un snackbar noir affichant simplement **« Erreur »** (message
générique de `getErrorMessage`, aucun code d'erreur traduit) et l'état reste inchangé
(`0 participants`, bouton toujours « Participer »). Reproduit 3 fois sur 2 groupes différents,
sur une sortie **future**. **L'action centrale de l'app — s'inscrire à une sortie — est cassée.**
De plus, le snackbar s'affiche *derrière/au-dessus* du bandeau « Participer » et disparaît en 4 s :
il est très facile de le manquer et de croire que rien ne s'est passé.

**Ce qui manque.** Pas de carte quand la sortie n'a pas encore de parcours (13), pas de lien vers
le parcours du groupe choisi, pas de liste de participants, pas de partage, pas d'« ajouter à mon
calendrier », pas de météo, pas de bouton « Se désinscrire » visible, pas de distinction visuelle
sortie passée / à venir.

### 17–18 · Détail d'une sortie (passée, avec parcours)

L'en-tête devient une **image de carte statique de 262 pt** superposant tous les tracés de groupes.
Le titre `N-Peloton #664` est écrit **en noir directement sur la carte, sans voile ni ombre** →
illisible (voir 17). La flèche retour souffre du même problème de contraste.

La ligne participants montre `40 participants` + **5 pastilles d'initiales** (N, C, B, J, Y) —
sans photo, sans nom, **non cliquables** : impossible de savoir qui participe. Les cartes de
groupe gagnent une icône « parcours » et deviennent cliquables (→ détail du parcours).

**Anomalie.** Le bouton **« Participer » reste actif sur une sortie passée** (22 juillet, alors
qu'on est le 25). Aucun état « Terminée ».

### 19–20 · Détail d'un parcours

Carte **interactive** plein écran (zoom, boussole, marqueurs de bornes kilométriques tous les
5 km, point de départ rouge), pastille de titre flottante en haut, et une `DraggableScrollableSheet`
en bas.

Sheet repliée : `46.3 km` / `187 m` D+ / `-188 m` D-.
Sheet déployée (20) : les mêmes stats, une carte « Type de surface — Gravel », un bouton
**Télécharger**, puis **~800 pt de vide**.

**Ce qui manque, et c'est beaucoup.** Aucun **profil altimétrique** (le graphe est pourtant
l'information n°1 attendue sur une fiche parcours), pas de description, pas de relief calculé,
pas de direction de vent, pas de date de création / auteur, pas de liste des sorties qui utilisent
ce parcours, pas de partage, pas d'envoi vers Garmin/Karoo alors que le projet embarque des apps
pour ces appareils. Le titre en pastille translucide sur la carte est peu lisible.

### 21–22 · Détail d'une publication

AppBar avec titre tronqué, chip équipe, date, puis le corps markdown et les images jointes.
Rendu du markdown correct (paragraphes, sauts de ligne, images pleine largeur).

**Défaut.** Le lien de la phrase finale « Pour vous inscrire, c'est juste ici ! » est rendu
**en texte brut** : l'ancre markdown est perdue, l'utilisateur ne peut pas s'inscrire.
(Les liens *nus* type `https://…` sont bien cliquables — cf. la description de la sortie #665 —
donc ce sont les liens markdown `[texte](url)` qui sautent.)

**Ce qui manque.** Pas d'auteur, pas de partage, pas de réactions ni commentaires, pas de
« publication suivante / précédente », les images ne s'ouvrent pas en plein écran.

### 23–25 · Voyage et étape

**Voyage (23).** En-tête carte statique du tracé global + titre illisible dessus (même défaut que
17), chip équipe, carte date de départ, `0 participants`, liste des **Étapes** (pastille numérotée,
libellé, date), bouton **« Rejoindre »**.
Manquent : distance totale, D+ total, date de fin / durée, description du voyage, distance par
étape, statut de participation.
À noter aussi une **incohérence de données visible** : l'étape intitulée « Lundi » est datée
`dimanche 12 mai`, « Mardi » → `lundi 13 mai`, etc. (décalage systématique d'un jour).

**Étape (24).** Écran **quasiment vide** : chip équipe + une carte `2 — lundi 13 mai — 22:00`,
puis 700 pt de blanc. Rien d'autre.
**Étape avec parcours (25).** Ajoute une seule ligne « Voir le parcours ». Le code
(`stage_detail_page.dart`) prévoit aussi lieu de départ, lieu d'arrivée et description, mais
**jamais de carte, ni distance, ni D+, ni participants** — même quand un parcours est rattaché.
C'est l'écran le plus pauvre de l'app.

### 26–29 · Petite équipe et états vides

Le fil de l'équipe « gaby » mélange Voyages et Sorties et se termine par « Vous avez tout vu ».

Les états vides (27, 28) sont **minimalistes à l'excès** : une icône grise + une phrase
(« Aucune publication », « Aucun événement ce mois-ci »). Pas de sous-texte explicatif,
pas d'action proposée, pas d'illustration. Sur le calendrier vide, aucune invitation à changer
de mois alors que c'est la seule action utile.

### 30–31 · Calendrier global (onglet 3)

Rigoureusement le même composant que le calendrier d'équipe, en agrégeant les équipes (la ligne
`équipe • heure` prend enfin son sens). Mêmes défauts : ~152 pt/jour, pas de vue mois, pas de
marqueur « aujourd'hui », pas de filtre par équipe, pas de statut de participation, pas de
voyages, ouverture systématique sur le 1er du mois plutôt que sur la date du jour.

### 32–35 · Profil (onglet 4)

Avatar circulaire (initiale seule, jamais de photo), nom, e-mail. Trois sections :
**Sécurité** (Passkeys — « Connexion biométrique activée » + bouton *Remplacer*),
**Préférences** (Langue → Français), **À propos** (Version 1.0.0 (23), Version du serveur
1.2.0 (afbf613), Politique de confidentialité, Conditions d'utilisation). Icône de déconnexion
en haut à droite.

**Sélecteur de langue (34).** Bottom sheet sans titre ni bouton Annuler, affichée **sous la barre
d'onglets** qui reste visible ; seulement Français / English.

**Pages légales (35).** Rendues correctement in-app avec markdown et liens mailto.
Ironie utile : la politique de confidentialité déclare traiter « Photo de profil (facultatif) » et
« Préférences : système d'unités (métrique/impérial), langue » — **ni la photo ni les unités ne
sont modifiables depuis le profil mobile**.

**Ce qui manque.** Aucune édition du profil (nom, avatar), aucun réglage de notifications,
aucun choix de thème clair/sombre, aucun choix d'unités, aucune gestion des appareils
GPS appairés (Garmin / Karoo) alors que le projet livre ces extensions, aucun historique
« mes sorties » / statistiques, aucun moyen de quitter une équipe ni de supprimer son compte.

### 36–37 · Petites annonces — inaccessibles

Aucun onglet « Annonces » n'apparaît dans les deux équipes testées. En forçant le deeplink
`https://www.pedalons.fr/equipes/n-peloton/annonces`, l'app **ouvre bien la page** mais :
1. affiche des squelettes de chargement pendant **~20 secondes** (36) ;
2. termine sur un écran **« Erreur » + « Réessayer »** sans aucune explication (37) ;
3. la barre d'onglets d'équipe surligne « **Fil** » alors qu'on est sur les annonces — l'état de
   navigation est incohérent (`getTeamDestinationIndex` retombe sur l'index 0 quand la destination
   Annonces n'existe pas dans la liste).

Les petites annonces sont donc **de facto absentes de l'app mobile**, et le seul chemin qui y mène
conduit à une erreur muette.

---

## Écrans du site web sans aucun équivalent dans l'app

Extraits de `contracts/routes.yaml` (`mobile: false`), périmètre consultation/participation
uniquement :

| Route web (fr) | Écran | Statut mobile |
|----------------|-------|---------------|
| `/parcours` | **Parcours globaux** (toutes équipes confondues) | **Aucun équivalent.** Les parcours ne sont accessibles que via l'onglet d'une équipe. |
| `/parcours/carte` | **Carte globale des parcours** | **Aucun équivalent.** |
| `/equipes/{slug}/parcours/carte` | **Vue carte de la parcothèque d'équipe** | **Aucun équivalent** — sur 2585 parcours, seule la liste verticale existe. |
| `/equipes/{slug}/parcours/{route}/carte` | **Carte plein écran d'un parcours** | Partiellement couvert : la fiche mobile *est* déjà une carte, mais sans profil altimétrique ni bascule. |
| `/equipes/{slug}/pages/{page}` | **Pages personnalisées d'équipe** (règlement, FAQ, tarifs…) | **Aucun équivalent.** Contenu totalement invisible sur mobile — seule la page « À propos » remonte. |
| `/equipes/{slug}/voyages/{trip}/etapes/{stage}/carte` | **Carte d'une étape** | **Aucun équivalent.** L'étape mobile n'a même pas de vignette. |
| `/equipes/{slug}/annonces` et `/annonces/{ad}` | **Petites annonces** | Routes déclarées `mobile: true` mais **aucun onglet ne les expose** et l'accès direct échoue (36–37). |

Autres absences fonctionnelles (sans route dédiée sur le web, mais présentes dans l'UI web) :
liste des membres d'une équipe, liste des participants d'une sortie, découverte / adhésion à une
équipe, notifications, profil éditable, préférences d'unités et de thème, appairage Garmin/Karoo.

Hors périmètre et donc légitimement absents : `/plateforme/*`, `/equipes/*/admin/*`, toutes les
routes `…/nouveau|nouvelle|modifier`, et `/outils-gpx/*`.

---

## Synthèse des faiblesses

1. **Participer à une sortie ne fonctionne pas** — snackbar « Erreur » générique, aucun changement
   d'état (38). C'est la fonction centrale du produit.
2. **Petites annonces inaccessibles** — pas d'onglet, deeplink en erreur après 20 s de squelettes
   (36–37).
3. **Fiche parcours sans profil altimétrique** et fiche étape quasi vide (20, 24) : sur les deux
   objets les plus « cyclistes » de l'app, l'information attendue n'est pas là.
4. **Densité d'information très faible partout** — 4 items par écran sur les feeds, 2 sur les
   parcours, 4 jours sur les calendriers ; en-têtes décoratifs de 182–262 pt.
5. **Impasses et boutons morts** — loupe « Découvrir des équipes » (TODO en dur), compteur de
   membres non cliquable, participants non cliquables, « Trier par » écrasé à 1 pt de haut.
6. **Contraste et lisibilité** — titres noirs posés directement sur les cartes (17, 23), pastille
   de titre translucide (19), modale de choix de groupe à 232 pt de large qui casse le texte (16).
7. **Rendu de contenu incomplet** — les liens markdown `[texte](url)` sont aplatis en texte brut
   dans les publications (22).
8. **Le feed Accueil perd les sorties à venir** après rafraîchissement (05) alors qu'elles restent
   dans le calendrier : l'écran d'entrée n'annonce pas la prochaine sortie.
9. **Profil sans réglages** — pas d'édition, pas de notifications, pas de thème, pas d'unités, pas
   d'appareils GPS, alors que la politique de confidentialité les mentionne.
10. **Navigation en silo** — entrer dans une équipe remplace la barre d'onglets racine ; aucun
    accès transversal aux parcours ou à une recherche globale.
