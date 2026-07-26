# Audit UX — écart site web / application mobile

Entrant de design produit le 25 juillet 2026 pour cadrer la prochaine version de l'app mobile.
L'objectif est de rapprocher l'application de ce que le site propose déjà.

**Périmètre** : consultation et participation d'un membre. Administration, création, édition et
outils GPX sont hors scope — ni documentés, ni maquettés.

**Conditions de l'audit** : données de production, session « Gaby Landais », locale française.
Site exploré dans Chrome sur `https://www.pedalons.fr` ; application explorée sur simulateur
iPhone 17 Pro / iOS 27.0 (402 × 874 pt) piloté par AXe. Équipes de référence : `n-peloton`
(1999 membres, 2585 parcours) et `gaby` (7 membres, sert d'échantillon d'états vides).

## Contenu

| Chemin | Quoi |
|---|---|
| [`BRIEF.md`](BRIEF.md) | Direction de design, écarts majeurs, évolutions d'API, 12 écrans priorisés |
| [`analyse/web-pages.md`](analyse/web-pages.md) | Structure de chaque page du site, champ par champ |
| [`analyse/mobile-screens.md`](analyse/mobile-screens.md) | Structure de chaque écran de l'app, et ce qui manque |
| [`analyse/api-surface.md`](analyse/api-surface.md) | Endpoints de lecture, ce que le mobile n'utilise pas, évolutions proposées |
| [`analyse/brand.md`](analyse/brand.md) | Palette clair/sombre, typographie, rayons, composants signature, tokens CSS |
| [`pedalons.css`](pedalons.css) | Feuille de style des maquettes, copiée du projet Claude Design. Fait autorité sur les métriques contre la planche `00 Fondations` |
| [`web/`](web/) | 27 captures du site + descriptif ([`web/README.md`](web/README.md)) |
| [`mobile/`](mobile/) | 38 captures de l'app + descriptif ([`mobile/README.md`](mobile/README.md)) |

## Maquettes

Les maquettes de la v2 vivent dans un projet Claude Design séparé (13 écrans plus une planche de
fondations) : <https://claude.ai/design/p/587c89e2-46d9-4b7b-bad7-2485a1235632>

Le projet embarque une copie de `BRIEF.md`, `analyse/brand.md` et des deux descriptifs
d'exploration sous `contexte/`.

## Constat en une phrase

Sur les 63 routes du contrat, 24 sont exposées au mobile, mais celles qui existent affichent une
fraction de ce que le site montre : le fil est rétrospectif et anonyme, le détail de sortie perd sa
carte et ses groupes, la fiche parcours perd son profil altimétrique et ses cols, la cartographie de
masse et les commentaires sont entièrement absents, et la découverte d'équipe n'est pas implémentée.
Le détail est dans [`BRIEF.md`](BRIEF.md).
