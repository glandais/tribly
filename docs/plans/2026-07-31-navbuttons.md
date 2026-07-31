# `NavButtons` — instruction du point §3.4 de `NEXT.md`

Écrit le 31 juillet 2026, sur la branche `feat/navbuttons`. Mesures prises sur
**https://staging.pedalons.fr** (compte connecté, admin de `n-peloton`), pas déduites de la lecture
du code : chaque chiffre ci-dessous a été relevé dans le navigateur.

`NEXT.md` §3.4 pose le sujet ainsi : *« `NavButtons` n'a ni sémantique de `tablist` ni navigation
clavier fléchée. Le remplacer par `Tabs` Mantine a été écarté du portage : […] le réécrire est un
chantier de design, pas un portage. **M.** »*

**La prémisse ne tient pas, et c'est la conclusion principale de ce document.** Le composant ne doit
pas devenir un `tablist`, il ne doit pas gagner de navigation fléchée, et la mise en conformité ne
touche à aucun pixel. En revanche l'inspection a fait apparaître **trois défauts visuels mesurés** et
**trois écarts de contraste chiffrés** qui, eux, sont réels et qui n'étaient pas dans l'énoncé.

Le chantier se re-taille donc : **S** pour la sémantique (aucun changement visuel), **S** pour les
défauts, **M** seulement si l'on ouvre la question de contraste, qui déborde du composant.

---

## 1. Ce que le composant est aujourd'hui

`frontend/src/components/common/NavButtons.tsx` — une `ScrollArea` horizontale contenant un `Group`
de `UnstyledButton component={Link}`, chacun : un carré de 40 px (rempli en couleur primaire quand
actif, en `default-hover` sinon) surmontant un libellé de 12 px sur une ou deux lignes.

Quatre appelants, et la volumétrie réelle est **bornée** :

| Appelant | Items | Maximum réel |
|---|---|---|
| `HomeLayout` | Accueil, Équipes, Calendrier, Parcours | **4** (calendrier sous auth, Équipes tombe en mono-équipe) |
| `TeamLayout` | Publications, Calendrier, Parcours, Annonces, À propos + pages dynamiques | **8** — le back-end plafonne les pages d'équipe à 3 (« 0 page sur 3 maximum », vu sur staging) |
| `AdminLayout` | Tableau de bord, Domaines, Équipes, Utilisateurs | **4** |
| `TeamAdminLayout` | Modèles, Lieux, Pages, Membres, Paramètres | **5** (2 pour un organisateur non-admin) |

**Ce plafond de 8 est le fait décisif du dossier** : à 80 px par item, une rangée pleine fait 660 px.
Sur un écran de bureau (viewport de contenu mesuré à 1 140 px) **le débordement horizontal n'arrive
jamais**. Il n'existe que sous ~430 px de large. Toute proposition de menu « ⋯ / Plus » est donc du
travail pour un cas qui ne se produit pas — voir §5.

Deuxième fait structurant : `useNavItems.ts` est **déjà** partagé avec le menu déroulant du fil
d'Ariane (`useBreadcrumb.ts`). Les mêmes items sont donc rendus deux fois sur la même page, à trente
pixels l'un de l'autre, sous deux formes. Cela retire l'argument « il faut un repli quand ça
déborde » : le repli existe, il est au-dessus.

---

## 2. La sémantique — pourquoi `tablist` serait une erreur, et pas seulement du travail en trop

Relevé sur staging, page d'une équipe :

```
inNav: false        ← aucun <nav>, aucun repère de navigation
inList: false       ← aucun <ul>/<li>, donc aucun « 4 éléments » annoncé
aria-current: null  ← sur les 4 liens, y compris l'actif
```

Un lecteur d'écran entend quatre liens isolés au fil du texte, sans savoir qu'ils forment une
navigation, combien ils sont, ni sur lequel on se trouve. **C'est le vrai défaut, et il est entier.**

Mais le corriger avec `Tabs` produirait un composant **faux**. Le motif `tablist`/`tabpanel` de
l'APS WAI-ARIA décrit des panneaux échangés sur place ; il promet à l'utilisateur que rien ne
navigue. Or ici chaque item est **une URL distincte**, rendue par le serveur (SSR), partageable,
inscrite dans l'historique. L'APG tranche explicitement ce cas : quand l'activation charge une page,
ce sont des **liens dans un repère `nav`**, pas des onglets.

La même logique élimine la navigation fléchée. Le `roving tabindex` est la contrepartie d'un
`tablist` : il retire les items non actifs de l'ordre de tabulation. Sur une liste de liens, il
**casserait** le comportement attendu (parcourir les liens à `Tab`) sans rien apporter, et aucun
critère WCAG ne l'exige. La demande fléchée de §3.4 est donc à retirer, pas à planifier.

**Correctif proposé — aucun changement visuel, ~25 lignes :**

```
<nav aria-label={label}>            ← libellé passé par l'appelant : « Navigation de l'équipe », etc.
  <ScrollArea …>
    <ul>  (list-style:none, la Group actuelle devient le ul)
      <li><Link aria-current={isActive ? 'page' : undefined} …>
```

`aria-current="page"` est la pièce maîtresse : c'est le seul moyen non visuel de savoir dans quelle
section on se trouve. Il remplace à lui seul tout ce que `Tabs` aurait apporté, sans mentir sur la
nature du composant.

> À noter : `<Group>` accepte `component="ul"` et `UnstyledButton` accepte `aria-current` en
> passthrough. Aucune sortie de Mantine, contrairement à ce que craignait §3.4.

---

## 3. Les trois défauts visuels, mesurés

### 3.1 Sur écran large, un libellé de plusieurs mots perd tous ses mots sauf le premier

Sur un viewport de **1 722 px**, l'onglet actif de la page d'équipe affiche « **Fil…** ».

```
label « Fil d'actualités » : clientHeight 14 px, scrollHeight 29 px  → coupé
title (infobulle) : null                                             → aucune récupération
```

Mécanique : `maxWidth: 80` borne la boîte, le texte revient à la ligne **au mot**, et le
`-webkit-line-clamp: 1` appliqué à partir de 48 em ne garde que la première ligne. « Fil » tient seul
sur la ligne 1, « d'actualités » est jeté. Le comportement est **inversé** : sur mobile (2 lignes) le
libellé est complet, sur le bureau — où la place abonde — il est amputé.

Tout libellé dont le premier mot ne remplit pas 64 px est concerné : « Fil d'actualités », « Modèles
de sortie », et n'importe quel titre de page d'équipe, qui est du texte libre saisi par un admin.

**Trois issues, une recommandation.**

| | Quoi | Effet | Coût |
|---|---|---|---|
| (i) | Largeur libre sur le bureau | Rangée à colonnes inégales, la grille identitaire se perd | S |
| (ii) | Supprimer la règle `48em`, deux lignes partout | Grille intacte, libellés complets | +14 px de hauteur — **annule une partie du gain de T3.6** |
| **(iii)** | **`maxWidth` élargi au-dessus de 48 em, une ligne conservée** | **Grille intacte, hauteur intacte, tous les libellés actuels tiennent** | **S — un nombre** |

**Recommandation : (iii)**, plus `title={item.label}` sur l'élément dans tous les cas. (iii) préserve
le gain de hauteur que T3.6 venait d'obtenir ; l'infobulle couvre le résidu (titres de page longs,
qui sont du texte libre), que (ii) ne couvrirait pas non plus au-delà de deux lignes. **La valeur
retenue est 130 px** — mesurée, voir §7.

### 3.2 Quand la rangée déborde, l'item actif reste hors champ

Reproduit sur `/equipes/n-peloton/admin/membres`, conteneur ramené à 260 px :

```
overflows: true    scrollWidth 360   clientWidth 260
scrollLeft: 0                                     ← la rangée n'a pas bougé
actif « Membres » : bord droit 312 px    conteneur : 286 px    fullyVisible: false
```

Le composant n'a ni `ref` ni effet : rien ne recale jamais la rangée. Sur un téléphone, ouvrir un
lien profond vers la dernière section d'une équipe affiche une barre de navigation où **la section
courante n'est simplement pas là**, et rien n'indique qu'il faut faire défiler.

**Correctif :** au montage et à chaque changement d'item actif, si `scrollWidth > clientWidth`,
positionner `viewport.scrollLeft` pour amener l'actif dans le champ. Écrire le `scrollLeft`
directement plutôt que d'appeler `scrollIntoView`, qui remonterait aussi la page verticalement.
Sans animation au montage.

### 3.3 Le dégradé de bord annonce un débordement qui n'existe pas

`.nav-buttons-viewport` porte un `mask-image` **inconditionnel** qui éteint les 28 derniers pixels.
Relevé sur l'accueil : `scrollWidth 1140`, `clientWidth 1140` — aucun débordement, masque appliqué
quand même. C'est un signal qui ment dans le sens le plus coûteux : celui qui apprend à l'utilisateur
à ne pas y croire, si bien qu'au moment où la rangée déborde vraiment (§3.2) le signal ne porte plus.

Il ment aussi dans l'autre sens : une fois défilé jusqu'au bout, le fondu reste à droite (il n'y a
plus rien) et **rien** ne marque le bord gauche (où il y a désormais du contenu caché).

**Correctif :** un écouteur de défilement qui pose deux attributs (`data-fade-start` /
`data-fade-end`) selon `scrollLeft` et `scrollWidth − clientWidth`, le CSS composant le masque à
partir de ces deux attributs. ~15 lignes, aucune dépendance. La variante purement CSS
(`animation-timeline: scroll(self inline)`) est plus élégante mais Firefox ne la sert pas — à
écarter pour l'instant.

---

## 4. Les contrastes — chiffrés, et deux d'entre eux débordent du composant

Ratios WCAG mesurés sur les couleurs calculées, dans les deux thèmes :

| Élément | Sombre | Clair | Seuil | Verdict |
|---|---|---|---|---|
| Libellé inactif (`c="dimmed"`, 12 px) sur la page | **4,04** | **3,32** | 4,5 (AA, texte < 18 px) | **échoue des deux côtés** |
| Fond de l'item **actif** vs page | **1,39** | **1,05** | — | en clair, **invisible** |
| Anneau de focus vs page | **2,74** | 4,32 | 3,0 (SC 1.4.11 / 2.4.11) | **échoue en sombre** |
| Icône blanche sur le carré actif | 3,42 | 4,86 | 3,0 (objet graphique) | passe, juste en sombre |
| Libellé actif sur son fond | 6,76 | 19,92 | 4,5 | passe |

Trois lectures, de la plus locale à la plus large :

- **Les libellés inactifs échouent AA dans les deux thèmes** (3,32 en clair est loin du compte). Le
  `c="dimmed"` fait par ailleurs lire toute la barre comme désactivée. **Proposition : libellé
  inactif en couleur de texte normale, et l'état actif porté par `fw={600}` + couleur primaire.**
  C'est le correctif le moins cher et il règle la ligne suivante par la même occasion.
- **En thème clair, le fond « sélectionné » est à 1,05:1 — il n'existe pas.** Le seul repère de
  section courante est le carré bleu. Le critère 1.4.1 (usage de la couleur) est techniquement tenu
  par la graisse du libellé, mais l'état sélectionné ne se lit pas d'un coup d'œil. La proposition
  ci-dessus le corrige ; à défaut, donner à l'item actif un contour visible.
- **L'anneau de focus à 2,74 en sombre est un défaut du thème global**, pas de `NavButtons` : il vaut
  pour tous les composants focalisables du site. **À ne pas traiter ici** — le noter, ouvrir une
  ligne à part, et ne pas laisser ce point gonfler le chantier.

---

## 5. Ce qu'il ne faut pas faire

| Piste | Pourquoi l'écarter |
|---|---|
| **`Tabs` Mantine** | Promet des panneaux échangés sur place alors que chaque item est une URL rendue par le serveur. Sémantiquement faux, et perd le motif visuel identitaire pour rien. Voir §2 |
| **Navigation fléchée / roving tabindex** | Contrepartie d'un `tablist`. Sur des liens, elle casse la tabulation attendue et aucun critère ne la demande |
| **Menu de débordement « ⋯ / Plus »** | Le nombre d'items est plafonné à 8 (3 pages d'équipe maximum côté back-end) : sur le bureau la rangée ne déborde **jamais**. Et sur mobile, le repli existe déjà — c'est le menu déroulant du fil d'Ariane, alimenté par le **même** `useNavItems`. Ce serait une troisième copie de la même liste |
| **Une barre de scroll visible** | Le `type="never"` est un choix assumé ; le §3.3 rend le fondu honnête, ce qui est le vrai correctif |
| **Des jetons `--pdl-*`** | Interdit au web par §6 de `NEXT.md` — le site tient sa charte dans le thème Mantine |

---

## 6. Découpage proposé

| Lot | Contenu | Visuel | Taille |
|---|---|---|---|
| **A** | `<nav aria-label>` + `<ul>/<li>` + `aria-current="page"` + `title` sur le libellé | **aucun changement** | S |
| **B** | §3.1 `maxWidth` 110 px · §3.2 recalage de l'actif · §3.3 fondu piloté par le défilement | mineur, cadré | S |
| **C** | §4 libellés inactifs et lisibilité de l'état actif dans les deux thèmes | oui — à valider | S |
| **D** | Anneau de focus global à 2,74 en sombre (`lib/theme.ts`) | tout le site | **hors périmètre**, ligne à part |

A et B sont sans arbitrage à rendre : ils suppriment des défauts nommés et mesurés. C demande un
accord sur l'apparence. D ne doit pas entrer ici.

**Deux points d'hygiène ramassés au passage**, sans rapport avec la sémantique :

- `AdminLayout` et `TeamAdminLayout` posent `NavButtons` directement dans un `Container`, alors que
  `HomeLayout` et `TeamLayout` le posent dans un `Stack` (gap `md`). La barre est donc collée au
  titre sur les deux écrans d'administration et espacée ailleurs.
- **Hors sujet mais constaté sur staging** : `/equipes/{slug}/admin/parametres` tombe sur la frontière
  d'erreur (« Une erreur est survenue »), sur un compte admin. La page ne rend rien, `NavButtons`
  compris. À instruire séparément.

---

## 7. Arbitrage rendu et livraison — 31 juillet 2026

**Tranché : (iii) pour §3.1, et lot C ouvert.** Lots **A + B + C livrés** sur `feat/navbuttons`.
Lot D (anneau de focus) laissé dehors, comme prévu.

**La largeur est 130 px, pas 110.** Le 110 de §3.1 était une estimation ; mesuré dans le navigateur
sur les libellés réellement livrés, il fixe « Fil d'actualités » (107 px) mais **laisse « Modèles de
sortie » coupé**. Le plus large des libellés livrés en `fr` et `en` est « Modèles de sortie » à
126 px, puis « Tableau de bord » à 115. 130 px les couvre tous avec 4 px de marge, et ne coûte rien :
c'est un plafond, pas une largeur — « Lieux » reste à 64 px. Une rangée pleine de 8 items fait
1 096 px dans une colonne de 1 140.

Vérifié dans le navigateur (dev local branché sur staging par `VITE_API_TARGET`) :

| | Avant | Après |
|---|---|---|
| Repère, liste, `aria-current` | absents | `nav[aria-label]`, `ul`/`li`, `aria-current="page"` sur le seul actif |
| « Fil d'actualités » à 1 722 px | « Fil… » | complet (107 px), plus un `title` pour le reliquat |
| Rangée débordante, section courante | `scrollLeft` 0, item coupé | `scrollLeft` 62, item entièrement visible |
| Fondu sans débordement | 28 px à droite | 0 des deux côtés |
| Fondu en début / en fin de défilement | 28 px à droite dans les deux cas | 0/28 au début, 28/0 à la fin |
| Libellé inactif vs page (sombre / clair) | 4,04 / 3,32 — **échec AA** | **9,37 / 21** |
| Surface de l'item actif vs page (sombre / clair) | 1,39 / **1,05** | 1,13 / 1,27, **teintées** |

`pnpm typecheck`, `pnpm lint`, `pnpm i18n:lint` et `./format.sh frontend` passent. Quatre clés
`nav.landmark.*` ajoutées dans les deux locales. `AdminLayout` et `TeamAdminLayout` passent au
`Stack` pour retrouver le rythme vertical des deux autres appelants.

**Reste ouvert**, à instruire à part :

1. **Lot D** — anneau de focus à 2,74:1 en thème sombre, sous le seuil de 3,0 de SC 1.4.11. Il vient
   de `lib/theme.ts` et vaut pour tout le site. Ligne à ouvrir dans `NEXT.md` ou dans l'audit
   d'infrastructure.
2. **`/equipes/{slug}/admin/parametres`** tombe sur la frontière d'erreur sur staging, compte admin.
   Sans rapport avec ce chantier.
3. Une **préférence de langue enregistrée ne peut pas l'emporter** : `caches: ['localStorage']`
   réécrit la langue détectée avant que `src/i18n/index.ts` ne relise `i18nextLng` après init, si
   bien que la valeur relue est toujours celle qui vient d'être détectée. Constaté en essayant de
   forcer le français en local. Sans rapport, mais réel.
